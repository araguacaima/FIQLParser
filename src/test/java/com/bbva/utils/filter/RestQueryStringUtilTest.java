package com.bbva.utils.filter;

import com.bbva.utils.filter.model.Movie;
import org.apache.commons.lang3.StringUtils;
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

    private static final java.lang.String QUERY_STRING_1 = "name==\"Kill Bill\";year=gt=2003";
    private static final java.lang.String QUERY_STRING_2 = "genres.name=in=(sci-fi,action);(director.name=='Christopher Nolan',actor.name==*Bale);year=ge=2000";
    private static final java.lang.String QUERY_STRING_3 = "director.lastName==Nolan;year=ge=2000;year=lt=2010";
    private static final java.lang.String QUERY_STRING_4 = "genres.name=in=(sci-fi,action);genres.name=out=(romance,animated,horror),director.name==Que*Tarantino";
    private static final java.lang.String QUERY_STRING_5 = "genres.name==sci-fi";
    private static final java.lang.String QUERY_STRING_6 = "director.awards.year=ge=2013";
    private static final java.lang.String QUERY_STRING_7 = "genres=in=(sci-fi,action) and genres=out=(romance,animated,horror) or director==Que*Tarantino";

    private HttpServletRequest httpServletRequest;

    @Before
    public void setUp() throws Exception {
        httpServletRequest = mock(HttpServletRequest.class);
    }

    @Test
    public void testToDtoExtWithNullQueryStringOnHttpServletRequest() throws Exception {
        Mockito.when(httpServletRequest.getQueryString()).thenReturn(null);
        Movie movie;
        movie = (Movie) RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
        assertNull("Movie can not be null", movie);
        System.out.println("movie: " + movie);
    }

    @Test
    public void testToDtoExtWithEmptyQueryStringOnHttpServletRequest() throws Exception {
        Mockito.when(httpServletRequest.getQueryString()).thenReturn(StringUtils.EMPTY);
        Movie movie;
        movie = (Movie) RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
        assertNull("Movie can not be null", movie);
        System.out.println("movie: " + movie);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToDtoExtInvalidExpressionOnHttpServletRequest() throws Exception {
        Mockito.when(httpServletRequest.getQueryString()).thenReturn(QUERY_STRING_7);
        RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
    }

    @Test
    public void testToDtoExtOnHttpServletRequest() throws Exception {
        Movie movie;


        Mockito.when(httpServletRequest.getQueryString()).thenReturn(QUERY_STRING_1);
        movie = (Movie) RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
        assertNotNull("Movie can not be null", movie);
        assertEquals("\"Kill Bill\"", movie.getName());
        assertEquals(2003, movie.getYear());
        System.out.println("movie: " + ReflectionToStringBuilder.toString(movie));

        Mockito.when(httpServletRequest.getQueryString()).thenReturn(QUERY_STRING_2);
        movie = (Movie) RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
        assertNotNull("Movie can not be null", movie);
        assertNotNull("Director can not be null", movie.getDirector());
        assertNotNull("Actor can not be null", movie.getActor());
        assertNotNull("Genres can not be null", movie.getGenres());
        assertEquals("(sci-fi,action)", movie.getGenres().getName());
        assertEquals("'Christopher Nolan'", movie.getDirector().getName());
        assertEquals("*Bale", movie.getActor().getName());
        assertEquals(2000, movie.getYear());
        System.out.println("movie: " + ReflectionToStringBuilder.toString(movie));

        Mockito.when(httpServletRequest.getQueryString()).thenReturn(QUERY_STRING_3);
        movie = (Movie) RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
        assertNotNull("Movie can not be null", movie);
        assertNotNull("Director can not be null", movie.getDirector());
        assertEquals("Nolan", movie.getDirector().getLastName());
        assertEquals(2010, movie.getYear());
        System.out.println("movie: " + ReflectionToStringBuilder.toString(movie));

        Mockito.when(httpServletRequest.getQueryString()).thenReturn(QUERY_STRING_4);
        movie = (Movie) RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
        assertNotNull("Movie can not be null", movie);
        assertNotNull("Genres can not be null", movie.getGenres());
        assertNotNull("Director can not be null", movie.getDirector());
        assertEquals("Que*Tarantino", movie.getDirector().getName());
        assertEquals("(romance,animated,horror)", movie.getGenres().getName());
        System.out.println("movie: " + ReflectionToStringBuilder.toString(movie));

        Mockito.when(httpServletRequest.getQueryString()).thenReturn(QUERY_STRING_5);
        movie = (Movie) RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
        assertNotNull("Movie can not be null", movie);
        assertEquals("sci-fi", movie.getGenres().getName());
        System.out.println("movie: " + ReflectionToStringBuilder.toString(movie));


        //TODO AMM: Falte culminar esta prueba. Los campos indexados mas allá del nivel 2 no se ven bien
        Mockito.when(httpServletRequest.getQueryString()).thenReturn(QUERY_STRING_6);
        movie = (Movie) RestQueryStringUtil.toDtoExt(httpServletRequest, Movie.class);
        assertNotNull("Movie can not be null", movie);
        assertNotNull("Director can not be null", movie.getDirector());
        assertNotNull("Awards can not be null", movie.getDirector().getAwards());
        assertEquals(2013, movie.getDirector().getAwards().getYear());
        System.out.println("movie: " + ReflectionToStringBuilder.toString(movie));
    }
}