/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import com.namics.hybris.ffsearch.data.suggest.ManufacturerSuggestion;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.factfinder.webservice.ws71.FFsearch.ResultSuggestion;

/**
 * Converter for a {@link ResultSuggestion} to a {@link ManufacturerSuggestion}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ManufacturerSuggestionConverter extends AbstractSuggestionConverter<ResultSuggestion, ManufacturerSuggestion> {

    @Override
    public ManufacturerSuggestion convert(final ResultSuggestion source, final ManufacturerSuggestion target) {
        target.setId(getId(source));
        target.setName(source.getName());
        target.setCount(source.getHitCount().intValue());
        target.setUrl(getUrl(source));
        target.setImg_url(getImageUrl(source));
        return target;
    }

    @Override
    protected String getUrl(final ResultSuggestion ffResult) {
        return getValue(DistFactFinderExportColumns.MANUFACTURER_URL.getValue(), ffResult);
    }

    @Override
    protected ManufacturerSuggestion createTarget() {
        return new ManufacturerSuggestion();
    }

    private String getImageUrl(final ResultSuggestion source) {
        return getValue(DistFactFinderExportColumns.MANUFACTURER_IMAGE_URL.getValue(), source);
    }

}
