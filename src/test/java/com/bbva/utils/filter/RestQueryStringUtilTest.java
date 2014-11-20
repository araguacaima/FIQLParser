package com.bbva.utils.filter;

import com.bbva.utils.filter.model.Movie;
import junit.framework.Assert;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class RestQueryStringUtilTest {

    private static final java.lang.String QUERY_STRING_1 = "genres=in=(sci-fi,action) and genres=out=(romance,animated,horror) or director==Que*Tarantino";
    private static final java.lang.String QUERY_STRING_2 = "name==\"Kill Bill\";year=gt=2003";
    private static final java.lang.String QUERY_STRING_3 = "name==\"Kill Bill\" and year>2003";
    private static final java.lang.String QUERY_STRING_4 = "genres=in=(sci-fi,action);(director=='Christopher Nolan',actor==*Bale);year=ge=2000";
    private static final java.lang.String QUERY_STRING_5 = "genres=in=(sci-fi,action) and (director=='Christopher Nolan' or actor==*Bale) and year>=2000";
    private static final java.lang.String QUERY_STRING_6 = "director.lastName==Nolan;year=ge=2000;year=lt=2010";
    private static final java.lang.String QUERY_STRING_7 = "director.lastName==Nolan and year>=2000 and year<2010";
    private static final java.lang.String QUERY_STRING_8 = "genres=in=(sci-fi,action);genres=out=(romance,animated,horror),director==Que*Tarantino";

    private HttpServletRequest httpServletRequest;

    @Before
    public void setUp() throws Exception {
        httpServletRequest = mock(HttpServletRequest.class);
    }

    @Test
    public void testGetQueryMap() throws Exception {

    }

    @Test
    public void testGetQueryMapWithNull() throws Exception {

    }

    @Test
    public void testGetQueryMapWithEmpty() throws Exception {

    }

    @Test
    public void testToDtoExt() throws Exception {

    }

    @Test
    public void testToDtoExtWithNullQueryStringOnHttpServletRequest() throws Exception {

    }

    @Test
    public void testToDtoExtWithEmptyQueryStringOnHttpServletRequest() throws Exception {

    }

    @Test
    public void testToDtoExtOnHttpServletRequest() throws Exception {
        Mockito.when(httpServletRequest.getQueryString()).thenReturn(QUERY_STRING_2);
        Movie movie;
        movie = (Movie) RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
        assertNotNull("Movie can not be null", movie);
        System.out.println("movie: " + ReflectionToStringBuilder.toString(movie));
    }
}