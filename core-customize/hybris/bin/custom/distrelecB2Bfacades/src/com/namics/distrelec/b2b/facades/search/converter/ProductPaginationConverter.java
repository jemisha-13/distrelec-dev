/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Required;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.*;

/**
 * Populating Converter for {@link FactFinderPaginationData}.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public class ProductPaginationConverter
        extends AbstractPopulatingConverter<FactFinderPaginationData<SearchQueryData>, FactFinderPaginationData<SearchStateData>> {

    // define constants for url parameter names
    private static final String FIRST_PARAM_QUERY = "?" + SEARCH_QUERY_PARAMETER_NAME + "=";
    private static final String PARAM_PAGE = "&" + PAGE_PARAMETER_NAME + "=";
    private static final String PARAM_MANUFACTURER_CODE = "&" + MANUFACTURER_CODE_PARAMETER_NAME + "=";
    private static final String PARAM_OUTLET_CODE = "&" + OUTLET_SEARCH_PARAMETER_NAME + "=";
    private static final String PARAM_NEW_CODE = "&" + NEW_SEARCH_PARAMETER_NAME + "=";

    private Converter<SearchQueryData, SearchStateData> searchStateConverter;

    @Override
    public void populate(final FactFinderPaginationData<SearchQueryData> source, final FactFinderPaginationData<SearchStateData> target) {
        if (source.getNextQuery() != null) {
            target.setNextUrl(buildQueryUrl(source.getNextQuery(), source.getNextPageNr()));
        }
        if (source.getPrevQuery() != null) {
            target.setPrevUrl(buildQueryUrl(source.getPrevQuery(), source.getPrevPageNr()));
        }
        target.setSort(source.getSort());
        target.setCurrentProductCode(source.getCurrentProductCode());
    }

    private String buildQueryUrl(final SearchQueryData queryData, final int pageNr) {
        final StringBuilder url = new StringBuilder();
        final SearchStateData state = getSearchStateConverter().convert(queryData);
        url.append(FIRST_PARAM_QUERY).append(state.getQuery().getValue()).append(PARAM_PAGE).append(pageNr);

        // add manufacturerCode to the next-/back-Link
        if (DistSearchType.MANUFACTURER.equals(queryData.getSearchType())) {
            url.append(PARAM_MANUFACTURER_CODE).append(queryData.getCode());
        } else if (DistSearchType.OUTLET.equals(queryData.getSearchType())) {
            url.append(PARAM_OUTLET_CODE).append(Boolean.TRUE);
        } else if (DistSearchType.NEW.equals(queryData.getSearchType())) {
            url.append(PARAM_NEW_CODE).append(Boolean.TRUE);
        }

        return url.toString();
    }

    @Override
    protected FactFinderPaginationData createTarget() {
        return new FactFinderPaginationData();
    }

    // BEGIN GENERATED CODE

    protected Converter<SearchQueryData, SearchStateData> getSearchStateConverter() {
        return searchStateConverter;
    }

    @Required
    public void setSearchStateConverter(final Converter<SearchQueryData, SearchStateData> searchStateConverter) {
        this.searchStateConverter = searchStateConverter;
    }

    // END GENERATED CODE
}
