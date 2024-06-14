package com.namics.distrelec.b2b.storefront.security.wrappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.namics.hybris.ffsearch.util.XSSFilterUtil;

/**
 * Extended Wrapper that will clean parameter names and the query string
 * It used by the DistXSSWrapperFilter.
 * In addition, existing XSSFilter also uses the XSSRequestWrapper that cleans parameter values out of the box (configurable as per Hybris doc.)
 */
public class DistXSSRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> cleanParameterMap = new LinkedHashMap<>();

    private String queryString;

    public DistXSSRequestWrapper(HttpServletRequest request) {
        super(request);
        // Sanitise Parameter Names and Values

        if (null != request.getParameterMap()) {

            for (final String originalParameterName : request.getParameterMap().keySet()) {
                String cleanParameterName = XSSFilterUtil.filterParameter(originalParameterName);

                String[] cleanParameterValues = null;
                String[] originalParameterValues = super.getParameterValues(originalParameterName);
                if (null != originalParameterValues && originalParameterValues.length > 0) {
                    Stream<String> stream1 = Arrays.stream(originalParameterValues);
                    cleanParameterValues = stream1.map(XSSFilterUtil::filterParameter).toArray(String[]::new);
                }

                // merge cleaned with old parameter values
                if (cleanParameterMap.containsKey(cleanParameterName)) {
                    String[] currentValues = cleanParameterMap.get(cleanParameterName);
                    if (null != currentValues && currentValues.length > 0) {
                        if (cleanParameterValues != null && cleanParameterValues.length > 0) {

                            String[] mergedValues = new String[currentValues.length + cleanParameterValues.length];
                            for (int i = 0; i < currentValues.length; i++) {
                                mergedValues[i] = currentValues[i];
                            }
                            for (int i = 0; i < cleanParameterValues.length; i++) {
                                mergedValues[i + currentValues.length] = cleanParameterValues[i];
                            }
                            cleanParameterValues = mergedValues;
                        } else {
                            cleanParameterValues = currentValues;
                        }
                    }
                }

                cleanParameterMap.put(cleanParameterName, cleanParameterValues);
            }
        }
    }

    @Override
    public String getQueryString() {
        if (super.getQueryString() != null && queryString == null) {
            queryString = XSSFilterUtil.filterQueryString(this);
        }
        return queryString;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(cleanParameterMap.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return cleanParameterMap.get(name);
    }

    @Override
    public String getParameter(String name) {
        String parameterValue = null;
        String[] parameterValues = cleanParameterMap.get(name);
        if (null != parameterValues && parameterValues.length > 0) {
            parameterValue = parameterValues[0];
        }
        return parameterValue;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return cleanParameterMap;
    }
}
