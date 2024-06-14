package com.namics.distrelec.b2b.storefront.security.wrappers;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

import java.util.*;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
public class DistXSSRequestWrapperTest {

    private DistXSSRequestWrapper distXSSRequestWrapper;
    private HttpServletRequest request;
    @Before
    public void setUp() {
        request = mock(HttpServletRequest.class);
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
    }

    @Test
    public void testGetParameterNamesEmpty() {
        when(request.getParameterMap()).thenReturn(null);
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
        Enumeration<String> outputParameterNames = distXSSRequestWrapper.getParameterNames();
        assertFalse(outputParameterNames.hasMoreElements());
    }

    @Test
    public void testGetParameterNamesClean() {
        List<String> inputParameterNames = asList("p1","p2", "p3");

        String[] inputValues1 = {"v1"};
        String[] inputValues2 = {"v2"};
        String[] inputValues3 = {"v3"};
        Map<String, String[]> inputParameterMap = new HashMap<String, String[]>(){{
            put("p1",inputValues1);
            put("p2",inputValues2);
            put("p3",inputValues3);
        }};

        when(request.getParameterMap()).thenReturn(inputParameterMap);
        when(request.getParameterValues("p1")).thenReturn(inputValues1);
        when(request.getParameterValues("p2")).thenReturn(inputValues2);
        when(request.getParameterValues("p3")).thenReturn(inputValues3);
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
        Enumeration<String> outputParameterNames = distXSSRequestWrapper.getParameterNames();
        assertTrue(new ArrayList<>(Collections.list(outputParameterNames)).containsAll(inputParameterNames));
    }

    @Test
    public void testGetParameterNamesMalicious() {
        List<String> cleanOutputParameterNames = asList("<p1>","</p2>", "p3");
        String[] inputValues1 = {"v1"};
        String[] inputValues2 = {"v2"};
        String[] inputValues3 = {"v3"};
        Map<String, String[]> inputParameterMap = new HashMap<String, String[]>(){{
            put("<p1>",inputValues1);
            put("</p2>",inputValues2);
            put("p3",inputValues3);
        }};

        when(request.getParameterMap()).thenReturn(inputParameterMap);
        when(request.getParameterValues("<p1>")).thenReturn(inputValues1);
        when(request.getParameterValues("</p2>")).thenReturn(inputValues2);
        when(request.getParameterValues("p3")).thenReturn(inputValues3);
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
        Enumeration<String> outputParameterNames = distXSSRequestWrapper.getParameterNames();
        assertTrue(new ArrayList<>(Collections.list(outputParameterNames)).containsAll(cleanOutputParameterNames));
    }

    @Test
    public void testGetQueryString() {
        String testQueryString = "filter_CuratedProducts=1_Robotics&q%22%3E%3C/script%3E%3CDyEwZOt%3Eabc%3C/DyEwZOt%3Edef=*";
        when(request.getQueryString()).thenReturn(testQueryString);
        Map<String, String[]> paramMap = new HashMap<>();
        paramMap.put("filter_CuratedProducts", new String[]{"1_Robotics&q%22%3E%3C/script%3E%3CDyEwZOt%3Eabc%3C/DyEwZOt%3Edef=*"});
        when(request.getParameterMap()).thenReturn(paramMap);
        when(request.getParameterValues("filter_CuratedProducts"))
                .thenReturn(new String[]{"1_Robotics&q%22%3E%3C/script%3E%3CDyEwZOt%3Eabc%3C/DyEwZOt%3Edef=*"});
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
        String cleanValue = distXSSRequestWrapper.getQueryString();
        assertEquals("filter_CuratedProducts=1_Robotics%26q%2522%253E%253C%2Fscript%253E%253CDyEwZOt%253Eabc%253C%2FDyEwZOt%253Edef%3D%2A", cleanValue);
    }

    @Test
    public void testGetNullQueryString() {
        when(request.getQueryString()).thenReturn(null);
        String cleanValue = distXSSRequestWrapper.getQueryString();
        assertNull(cleanValue);
    }

    @Test
    public void testGetParameterCleanNameAndValue() {

        String[] inputValues1 = {"v1"};

        Map<String, String[]> inputParameterMap = new HashMap<String, String[]>(){{
            put("p1",inputValues1);

        }};

        when(request.getParameterMap()).thenReturn(inputParameterMap);
        when(request.getParameterValues("p1")).thenReturn(inputValues1);
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
        String outputParameterValue = distXSSRequestWrapper.getParameter("p1");
        assertEquals(inputValues1[0], outputParameterValue);
    }

    @Test
    public void testGetParameterMaliciousValue() {

        String[] inputValues1 = {"<v1>"};

        Map<String, String[]> inputParameterMap = new HashMap<String, String[]>(){{
            put("p1",inputValues1);

        }};

        when(request.getParameterMap()).thenReturn(inputParameterMap);
        when(request.getParameterValues("p1")).thenReturn(inputValues1);
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
        String outputParameterValue = distXSSRequestWrapper.getParameter("p1");
        assertEquals("<v1>", outputParameterValue);
    }

    @Test
    public void testGetParameterNoValue() {

        String[] inputValues1 = {"v1"};

        Map<String, String[]> inputParameterMap = new HashMap<String, String[]>(){{
            put("p1",inputValues1);

        }};

        when(request.getParameterMap()).thenReturn(inputParameterMap);
        when(request.getParameterValues("p1")).thenReturn(inputValues1);
        when(request.getParameterValues("p2")).thenReturn(null);
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
        String outputParameterValue = distXSSRequestWrapper.getParameter("p2");
        assertNull(outputParameterValue);
    }

    @Test
    public void testGetParameterMaliciousName() {

        String[] inputValues1 = {"v1"};

        Map<String, String[]> inputParameterMap = new HashMap<String, String[]>(){{
            put("<p1>",inputValues1);

        }};

        when(request.getParameterMap()).thenReturn(inputParameterMap);
        when(request.getParameterValues("<p1>")).thenReturn(inputValues1);
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
        String outputParameterValue = distXSSRequestWrapper.getParameter("<p1>");
        assertEquals(inputValues1[0], outputParameterValue);
    }

    @Test
    public void testGetParameters() {

        String[] inputValues1 = {"v1", "v2"};

        Map<String, String[]> inputParameterMap = new HashMap<String, String[]>(){{
            put("p1",inputValues1);

        }};

        when(request.getParameterMap()).thenReturn(inputParameterMap);
        when(request.getParameterValues("p1")).thenReturn(inputValues1);
        distXSSRequestWrapper = new DistXSSRequestWrapper(request);
        String[] outputParameterValues = distXSSRequestWrapper.getParameterValues("p1");
        assertEquals(2, outputParameterValues.length);
        assertTrue(Arrays.asList(inputValues1).containsAll(Arrays.asList(outputParameterValues)));
    }
}