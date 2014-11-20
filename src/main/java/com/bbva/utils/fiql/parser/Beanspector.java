package com.bbva.utils.fiql.parser;

/**
 * Created by Alejandro on 20/11/2014.
 */


import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bean introspection utility.
 */
class Beanspector<T> {

    private Class<T> tclass;
    private T tobj;
    private Map<String, Method> getters = new HashMap<String, Method>();
    private Map<String, Method> setters = new HashMap<String, Method>();

    public Beanspector(Class<T> tclass) {
        if (tclass == null) {
            throw new IllegalArgumentException("tclass is null");
        }
        this.tclass = tclass;
        init();
    }

    public Beanspector(T tobj) {
        if (tobj == null) {
            throw new IllegalArgumentException("tobj is null");
        }
        this.tobj = tobj;
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        fill(tclass, tobj, getters, setters);
    }

    private void fill(Class tclass, Object tobj, Map<String, Method> getters, Map<String, Method> setters) {
        if (tclass == null) {
            if (tobj != null) {
                tclass = (Class<T>) tobj.getClass();
            }
        }
        if (tclass != null) {
            for (Method m : tclass.getMethods()) {
                if (isGetter(m)) {
                    getters.put(getterName(m), m);
                } else if (isSetter(m)) {
                    setters.put(setterName(m), m);
                }
            }
            // check type equality for getter-setter pairs
            Set<String> pairs = new HashSet<String>(getters.keySet());
            pairs.retainAll(setters.keySet());
            for (String accessor : pairs) {
                Class<?> getterClass = getters.get(accessor).getReturnType();
                Class<?> setterClass = setters.get(accessor).getParameterTypes()[0];
                if (!getterClass.equals(setterClass)) {
                    throw new IllegalArgumentException(String
                            .format("Accessor '%s' type mismatch, getter type is %s while setter type is %s",
                                    accessor, getterClass.getName(), setterClass.getName()));
                }
            }
        } else {
            throw new IllegalArgumentException("Class and Object can not both be null");
        }
    }

    public T getBean() {
        return tobj;
    }

    public Set<String> getGettersNames() {
        return Collections.unmodifiableSet(getters.keySet());
    }

    public Set<String> getSettersNames() {
        return Collections.unmodifiableSet(setters.keySet());
    }

    public Class<?> getAccessorType(String getterOrSetterName) throws Exception {
        String[] tokens = getterOrSetterName.split("\\.");
        if (tokens.length > 1) {
            String token = tokens[0];
            Class clazz = getAccessorType(token);
            return getAccessorType(clazz, getterOrSetterName.replaceFirst(token + "\\.", StringUtils.EMPTY));
        } else {
            return getTopLevelAccesorType(getterOrSetterName, getters, setters);
        }
    }

    private Class<?> getAccessorType(Class clazz, String getterOrSetterName) throws Exception {
        Map<String, Method> getters = new HashMap<String, Method>();
        Map<String, Method> setters = new HashMap<String, Method>();
        fill(clazz, null, getters, setters);
        String[] tokens = getterOrSetterName.split("\\.");
        if (tokens.length > 1) {
            String token = tokens[0];
            return getAccessorType(clazz, getterOrSetterName.replaceFirst(token + "\\.", StringUtils.EMPTY));
        } else {
            return getTopLevelAccesorType(getterOrSetterName, getters, setters);
        }
    }

    private Class<?> getTopLevelAccesorType(String getterOrSetterName, Map<String, Method> getters, Map<String, Method> setters) throws IntrospectionException {
        Method m = getters.get(getterOrSetterName);
        if (m == null) {
            m = setters.get(getterOrSetterName);
        }
        if (m == null) {
            String msg = String.format("Accessor '%s' not found, "
                            + "known setters are: %s, known getters are: %s", getterOrSetterName,
                    setters.keySet(), getters.keySet());
            throw new IntrospectionException(msg);
        }
        return m.getReturnType();
    }

    public Beanspector<T> swap(T newobject) throws Exception {
        if (newobject == null) {
            throw new IllegalArgumentException("newobject is null");
        }
        tobj = newobject;
        return this;
    }

    public Beanspector<T> instantiate() throws Exception {
        if (tobj == null) {
            tobj = tclass.newInstance();
        }
        return this;
    }

    public Beanspector<T> setValue(String setterName, Object value) throws Throwable {
        instantiateNestedProperties(getBean(), setterName);
        PropertyUtils.setProperty(getBean(), setterName, value);
        return this;
    }

    public Object getValue(String getterName) throws Throwable {
        return getValue(getters.get(getterName));
    }

    public Object getValue(Method getter) throws Throwable {
        try {
            return getter.invoke(tobj);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        } catch (Exception e) {
            throw e;
        }
    }

    private boolean isGetter(Method m) {
        return m.getParameterTypes().length == 0
                && (m.getName().startsWith("get") || m.getName().startsWith("is"));
    }

    private String getterName(Method m) {
        return StringUtils.uncapitalize(m.getName().replace("is", "").replace("get", ""));
    }

    private boolean isSetter(Method m) {
        return m.getReturnType().equals(void.class) && m.getParameterTypes().length == 1
                && (m.getName().startsWith("set") || m.getName().startsWith("is"));
    }

    private String setterName(Method m) {
        return StringUtils.uncapitalize(m.getName().replace("is", "").replace("set", ""));
    }

    private void instantiateNestedProperties(Object obj, String fieldName) {
        try {
            String[] fieldNames = fieldName.split("\\.");
            if (fieldNames.length > 1) {
                StringBuilder nestedProperty = new StringBuilder();
                for (int i = 0; i < fieldNames.length - 1; i++) {
                    String fn = fieldNames[i];
                    if (i != 0) {
                        nestedProperty.append(".");
                    }
                    nestedProperty.append(fn);

                    Object value = PropertyUtils.getProperty(obj, nestedProperty.toString());

                    if (value == null) {
                        PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(obj, nestedProperty.toString());
                        Class<?> propertyType = propertyDescriptor.getPropertyType();
                        Object newInstance = propertyType.newInstance();
                        PropertyUtils.setProperty(obj, nestedProperty.toString(), newInstance);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

}
