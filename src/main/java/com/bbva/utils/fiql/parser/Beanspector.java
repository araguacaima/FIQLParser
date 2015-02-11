package com.bbva.utils.fiql.parser;

/**
 * Created by Alejandro on 20/11/2014.
 */

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Bean introspection utility.
 */
class Beanspector<T> {

	private Class<T> tclass;
	private T tobj;
	private final Map<String, Method> getters = new HashMap<String, Method>();
	private final Map<String, Method> setters = new HashMap<String, Method>();

	public Beanspector(final Class<T> tclass) {
		if (tclass == null) {
			throw new IllegalArgumentException("tclass is null");
		}
		this.tclass = tclass;
		init();
	}

	public Beanspector(final T tobj) {
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

	private void fill(Class tclass, final Object tobj, final Map<String, Method> getters, final Map<String, Method> setters) {
		if (tclass == null) {
			if (tobj != null) {
				tclass = tobj.getClass();
			}
		}
		if (tclass != null) {
			for (final Method m : tclass.getMethods()) {
				if (isGetter(m)) {
					getters.put(getterName(m), m);
				} else if (isSetter(m)) {
					setters.put(setterName(m), m);
				}
			}
			// check type equality for getter-setter pairs
			final Set<String> pairs = new HashSet<String>(getters.keySet());
			pairs.retainAll(setters.keySet());
			for (final String accessor : pairs) {
				final Class<?> getterClass = getters.get(accessor).getReturnType();
				final Class<?> setterClass = setters.get(accessor).getParameterTypes()[0];
				if (!getterClass.equals(setterClass)) {
					throw new IllegalArgumentException(String.format(
							"Accessor '%s' type mismatch, getter type is %s while setter type is %s", accessor, getterClass.getName(),
							setterClass.getName()));
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

	public Class<?> getAccessorType(final String getterOrSetterName) throws Exception {
		final String[] tokens = getterOrSetterName.split("\\.");
		if (tokens.length > 1) {
			final String token = tokens[0];
			final Class clazz = getAccessorType(token);
			return getAccessorType(clazz, getterOrSetterName.replaceFirst(token + "\\.", StringUtils.EMPTY));
		} else {
			return getTopLevelAccesorType(getterOrSetterName, getters, setters);
		}
	}

	private Class<?> getAccessorType(final Class clazz, final String getterOrSetterName) throws Exception {
		final Map<String, Method> getters = new HashMap<String, Method>();
		final Map<String, Method> setters = new HashMap<String, Method>();
		fill(clazz, null, getters, setters);
		final String[] tokens = getterOrSetterName.split("\\.");
		if (tokens.length > 1) {
			final String token = tokens[0];
			return getAccessorType(clazz, getterOrSetterName.replaceFirst(token + "\\.", StringUtils.EMPTY));
		} else {
			return getTopLevelAccesorType(getterOrSetterName, getters, setters);
		}
	}

	private Class<?> getTopLevelAccesorType(final String getterOrSetterName, final Map<String, Method> getters,
			final Map<String, Method> setters) throws IntrospectionException {
		Method m = getters.get(getterOrSetterName);
		if (m == null) {
			m = setters.get(getterOrSetterName);
		}
		if (m == null) {
			final String msg = String.format("Accessor '%s' not found, " + "known setters are: %s, known getters are: %s",
					getterOrSetterName, setters.keySet(), getters.keySet());
			throw new IntrospectionException(msg);
		}
		return m.getReturnType();
	}

	public Beanspector<T> swap(final T newobject) throws Exception {
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

	public Beanspector<T> setValue(final String setterName, final Object value) throws Throwable {
		instantiateNestedProperties(getBean(), setterName);
		PropertyUtils.setProperty(getBean(), setterName, value);
		return this;
	}

	public Object getValue(final String getterName) throws Throwable {
		return getValue(getters.get(getterName));
	}

	public Object getValue(final Method getter) throws Throwable {
		try {
			return getter.invoke(tobj);
		} catch (final InvocationTargetException e) {
			throw e.getCause();
		} catch (final Exception e) {
			throw e;
		}
	}

	private boolean isGetter(final Method m) {
		return m.getParameterTypes().length == 0 && (m.getName().startsWith("get") || m.getName().startsWith("is"));
	}

	private String getterName(final Method m) {
		return StringUtils.uncapitalize(m.getName().startsWith("is") ? m.getName().substring(2) : m.getName().startsWith("get") ? m
				.getName().substring(3) : m.getName());
	}

	private boolean isSetter(final Method m) {
		return m.getReturnType().equals(void.class) && m.getParameterTypes().length == 1
				&& (m.getName().startsWith("set") || m.getName().startsWith("is"));
	}

	private String setterName(final Method m) {
		return StringUtils.uncapitalize(m.getName().replace("is", "").replace("set", ""));
	}

	private void instantiateNestedProperties(final Object obj, final String fieldName) {
		try {
			final String[] fieldNames = fieldName.split("\\.");
			if (fieldNames.length > 1) {
				final StringBuilder nestedProperty = new StringBuilder();
				for (int i = 0; i < fieldNames.length - 1; i++) {
					final String fn = fieldNames[i];
					if (i != 0) {
						nestedProperty.append(".");
					}
					nestedProperty.append(fn);

					final Object value = PropertyUtils.getProperty(obj, nestedProperty.toString());

					if (value == null) {
						final PropertyDescriptor propertyDescriptor = PropertyUtils.getPropertyDescriptor(obj, nestedProperty.toString());
						final Class<?> propertyType = propertyDescriptor.getPropertyType();
						final Object newInstance = propertyType.newInstance();
						PropertyUtils.setProperty(obj, nestedProperty.toString(), newInstance);
					}
				}
			}
		} catch (final IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (final InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (final NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (final InstantiationException e) {
			throw new RuntimeException(e);
		}
	}

}
