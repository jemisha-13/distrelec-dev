package com.distrelec.fusionsearch.response;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

@RunWith(MockitoJUnitRunner.class)
abstract class AbstractSearchResponsePopulatorTest {

    protected FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData;

    @Mock
    protected SearchResponseTuple searchResponseTuple;

    @Before
    public void setUpSearchResponse() {
        searchPageData = new FactFinderProductSearchPageData<>();
    }
}
