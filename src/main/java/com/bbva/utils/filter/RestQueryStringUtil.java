package com.bbva.utils.filter;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.search.FiqlParseException;
import org.apache.cxf.jaxrs.ext.search.FiqlParser;
import org.apache.cxf.jaxrs.ext.search.SearchCondition;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alejandro on 19/11/2014.
 */
public class RestQueryStringUtil {

    public static Map<String, String> getQueryMap(String query) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isNotBlank(query)) {
            String[] params = query.split("&");
            for (String param : params) {
                String name = param.split("=")[0];
                String value = param.split("=")[1];
                map.put(name, value);
            }
        }
        return map;
    }

    public static Object toDtoExt(String queryString, Class dtoExtClass) throws IllegalArgumentException, IllegalAccessException, InstantiationException {

        FiqlParser parser = new FiqlParser(dtoExtClass);
        try {
            SearchCondition condition1 = parser.parse(queryString);
            return condition1.getCondition();
        } catch (FiqlParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    public static Object toDtoExt(String queryString, Object dtoExtObject) throws IllegalArgumentException {
        Map<String, String> map = RestQueryStringUtil.getQueryMap(queryString);
        try {
            BeanUtils.populate(dtoExtObject, map);
            return dtoExtObject;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    public static Object toDtoExt(HttpServletRequest request, Class dtoExtClass) throws IllegalArgumentException, InstantiationException, IllegalAccessException {
        return RestQueryStringUtil.toDtoExt(request.getQueryString(), dtoExtClass);
    }
}
