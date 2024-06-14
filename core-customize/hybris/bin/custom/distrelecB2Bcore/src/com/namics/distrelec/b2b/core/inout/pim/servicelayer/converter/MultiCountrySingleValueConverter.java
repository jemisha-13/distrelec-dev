/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;

/**
 * MultiCountrySingleValueConverter.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class MultiCountrySingleValueConverter {
    private static final Logger LOG = LogManager.getLogger(MultiCountrySingleValueConverter.class);

    private static final String PATTERN_STRING = "^([a-zA-Z]{2})[_](.*)";

    public Map<String, String> convert(final Element multiValue) {
        final Pattern pattern = Pattern.compile(PATTERN_STRING);
        final List<Element> elements = multiValue.elements();
        if (CollectionUtils.isNotEmpty(elements)) {
            final Map<String, String> countryMap = new HashMap<String, String>();
            for (final Element element : elements) {
                final String elementValue = element.getTextTrim();
                final Matcher matcher = pattern.matcher(elementValue);
                if (matcher.find()) {
                    final String country = StringUtils.lowerCase(matcher.group(1));
                    final String value = matcher.group(2);
                    if (!countryMap.containsKey(country)) {
                        countryMap.put(country, value);
                    }
                } else {
                    LOG.debug("Could not parse value: {}", elementValue);
                }
            }

            return countryMap;
        }

        return null;
    }
}
