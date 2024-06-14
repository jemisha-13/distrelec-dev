package com.distrelec.fusionsearch.response.result;

import java.util.HashMap;
import java.util.Map;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * Extends populating converter to create a values map in the SearchResultValueData.
 */
public class DocToSearchResultConverter extends AbstractPopulatingConverter<Map<String, Object>, SearchResultValueData> {

    @Override
    protected SearchResultValueData createTarget() {
        SearchResultValueData searchResult = new SearchResultValueData();
        searchResult.setValues(new HashMap<>()); // injects map as it is null
        return searchResult;
    }
}
