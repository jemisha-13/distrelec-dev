/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;

/**
 * Common capabilities of search result populators.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public abstract class AbstractSearchResultPopulator implements Populator<SearchResultValueData, ProductData> {

    protected <T> T getValue(final SearchResultValueData source, final String propertyName) {
        if (source.getValues() == null) {
            return null;
        }
        return (T) source.getValues().get(propertyName);
    }

    protected String getStringValue(final SearchResultValueData source, final String propertyName) {
        final String value = getValue(source, propertyName);
        if (StringUtils.isNotBlank(value)) {
            return value;
        } else {
            return null;
        }
    }

}
