package com.bbva.utils.filter;


import com.bbva.utils.fiql.parser.ExtendableFiqlParser;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.search.ConditionType;
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

    static {
        ExtendableFiqlParser.addOperator("=in=", ConditionType.CUSTOM);
        ExtendableFiqlParser.addOperator("=out=", ConditionType.CUSTOM);
    }

    public static Object toDtoExt(String queryString, Class dtoExtClass) throws IllegalArgumentException, IllegalAccessException, InstantiationException {

        ExtendableFiqlParser parser = new ExtendableFiqlParser(dtoExtClass);
        try {
            if (StringUtils.isNotBlank(queryString)) {
//                SearchCondition condition1 = parser.parse(queryString);
                parser.parse(queryString);
                return parser.getBean();
            }
        } catch (FiqlParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
        return null;
    }

    public static Object toDtoExt(HttpServletRequest request, Class dtoExtClass) throws IllegalArgumentException, InstantiationException, IllegalAccessException {
        return RestQueryStringUtil.toDtoExt(request.getQueryString(), dtoExtClass);
    }
}
