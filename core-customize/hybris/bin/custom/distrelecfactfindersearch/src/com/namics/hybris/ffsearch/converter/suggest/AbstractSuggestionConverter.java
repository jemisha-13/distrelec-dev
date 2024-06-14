/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import org.apache.commons.lang.StringUtils;

import de.factfinder.webservice.ws71.FFsearch.ResultSuggestion;
import de.factfinder.webservice.ws71.FFsearch.String2StringMap.Entry;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Common Converter methods.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 * @param <SOURCE>
 * @param <TARGET>
 */
abstract public class AbstractSuggestionConverter<SOURCE, TARGET> implements Converter<SOURCE, TARGET> {

    private static final String ID_ATTRIBUTE_NAME = "id";
    private static final String DEEPLINK_ATTRIBUTE_NAME = "deeplink";

    @Override
    public TARGET convert(final SOURCE source) {
        return convert(source, createTarget());
    }

    protected String getId(final ResultSuggestion ffResult) {
        return getValue(ID_ATTRIBUTE_NAME, ffResult);
    }

    protected String getUrl(final ResultSuggestion ffResult) {
        return getValue(DEEPLINK_ATTRIBUTE_NAME, ffResult);
    }

    protected String getValue(final String key, final ResultSuggestion ffResult) {
        for (final Entry entry : ffResult.getAttributes().getEntry()) {
            if (StringUtils.equalsIgnoreCase(entry.getKey(), key)) {
                return entry.getValue();
            }
        }
        return StringUtils.EMPTY;
    }

    abstract protected TARGET createTarget();

}
