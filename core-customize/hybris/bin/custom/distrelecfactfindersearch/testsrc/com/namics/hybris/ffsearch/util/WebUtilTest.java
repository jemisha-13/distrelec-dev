package com.namics.hybris.ffsearch.util;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@UnitTest
public class WebUtilTest {

    @Test
    public void testFilterParametersHaveTags(){
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final Map<String, Object> parameterMap = createParameterMap();
        request.setParameters(parameterMap);

        final String result = WebUtil.getQueryParamsStartingWith(request, "*",  "filter_");
        final String expectedSubstring = "<sup>-1</sup>";
        assertTrue(result.contains(expectedSubstring));
    }

    private Map<String, Object> createParameterMap(){
        final Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put("q", "*");
        parameterMap.put("filter_Buyable", "1");
        parameterMap.put("filter_Category4", "Iron-Core DC Motors with Gear Drive");
        parameterMap.put("filter_Category3", "Direct Current Motors");
        parameterMap.put("filter_Rotational speed", "10.5...12 min-1");
        return parameterMap;
    }
}
