/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.REPLACEMENT;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.namics.distrelec.b2b.facades.search.data.DistFactFinderReplacementData;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

/**
 * Populator for {@link DistFactFinderReplacementData} on {@link ProductData}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class SearchResultReplacementPopulator extends AbstractSearchResultPopulator {

    private static final Logger LOG = LoggerFactory.getLogger(SearchResultReplacementPopulator.class);

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create(); // thread-safe

    @Override
    public void populate(final SearchResultValueData source, final ProductData target) {
        try {

            final String replacementValue = this.<String> getValue(source, REPLACEMENT.getValue());
            if (StringUtils.isBlank(replacementValue)) {
                return;
            }
            final DistFactFinderReplacementData replacement = GSON.fromJson(replacementValue, DistFactFinderReplacementData.class);
            target.setBuyableReplacementProduct(replacement.isBuyable());
            target.setReplacementReason(replacement.getReason());
            target.setEndOfLifeDate(replacement.getEolDate());
            // BEGIN SUPPRESS WARNING
        } catch (final Exception e) {
            // END SUPPRESS WARNING
            LOG.error("Failed parsing replacement information from values [{}].", ReflectionToStringBuilder.toString(source), e);
        }
    }

}
