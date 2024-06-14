package com.distrelec.fusionsearch.response.result;

import java.util.HashMap;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

abstract class AbstractSearchResultPopulatorTest {

    protected SearchResultValueData searchResult;

    @Before
    public void setUpSearchResult() {
        MockitoAnnotations.openMocks(this);

        searchResult = new SearchResultValueData();
        searchResult.setValues(new HashMap<>());
    }
}
