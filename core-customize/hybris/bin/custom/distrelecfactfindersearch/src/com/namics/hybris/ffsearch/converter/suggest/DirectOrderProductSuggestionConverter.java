/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import com.namics.hybris.ffsearch.data.suggest.ProductSuggestion;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.factfinder.webservice.ws71.FFsearch.ResultSuggestion;

/**
 * Converter for Direct-Order product suggestions. Compared to a "regular" search suggestion, some values (product name and product number)
 * are transmitted differently.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DirectOrderProductSuggestionConverter extends ProductSuggestionConverter {

    @Override
    public ProductSuggestion convert(final ResultSuggestion source, final ProductSuggestion target) {
        final ProductSuggestion productSuggestion = super.convert(source, target);
        productSuggestion.setName(getName(source));
        return productSuggestion;
    }

    @Override
    protected String getId(final ResultSuggestion ffResult) {
        return ffResult.getName();
    }

    protected String getName(final ResultSuggestion ffResult) {
        return getValue(DistFactFinderExportColumns.TITLE.getValue(), ffResult);
    }

}
