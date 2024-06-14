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
 * MultiCountryMultiLanguageSingleValueConverter.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class MultiCountryMultiLanguageSingleValueConverter {
    private static final Logger LOG = LogManager.getLogger(MultiCountryMultiLanguageSingleValueConverter.class);

    private static final String PATTERN_STRING = "^([a-zA-Z]{2})[_]([a-zA-Z]{3})[_](.*)";

    public Map<String, Map<String, String>> convert(final Element multiValue) {
        final Pattern pattern = Pattern.compile(PATTERN_STRING);
        final List<Element> elements = multiValue.elements();
        if (CollectionUtils.isNotEmpty(elements)) {
            final Map<String, Map<String, String>> countryMap = new HashMap<String, Map<String, String>>();
            for (final Element element : elements) {
                final String elementValue = element.getTextTrim();
                final Matcher matcher = pattern.matcher(elementValue);
                if (matcher.find()) {
                    final String country = StringUtils.lowerCase(matcher.group(1));
                    final String language = StringUtils.lowerCase(matcher.group(2));
                    final String value = matcher.group(3);
                    if (!countryMap.containsKey(country)) {
                        countryMap.put(country, new HashMap<String, String>());
                    }

                    final Map<String, String> languageMap = countryMap.get(country);
                    languageMap.put(language, value);
                } else {
                    LOG.debug("Could not parse value: {}", elementValue);
                }
            }

            return countryMap;
        }

        return null;
    }
}
