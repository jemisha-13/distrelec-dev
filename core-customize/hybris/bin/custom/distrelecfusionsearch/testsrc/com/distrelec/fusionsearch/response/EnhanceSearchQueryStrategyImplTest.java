package com.distrelec.fusionsearch.response;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.converters.Populator;

@UnitTest
public class EnhanceSearchQueryStrategyImplTest {

    @InjectMocks
    EnhanceSearchQueryStrategyImpl enhanceSearchQueryStrategy;

    @Mock
    Populator<Params, SearchQueryData> queryDataPopulator;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testEnhanceSearchQuery() {
        String searchQueryCode = "searchQueryCode";
        DistSearchType searchType = DistSearchType.CATEGORY;

        SearchResponseTuple searchResponseTuple = mock(SearchResponseTuple.class);
        SearchQueryPageableData<SearchQueryData> searchQueryPageableData = mock(SearchQueryPageableData.class);
        SearchQueryData searchQuery = mock(SearchQueryData.class);
        SearchRequest searchRequest = mock(SearchRequest.class);
        Params params = mock(Params.class);

        when(searchResponseTuple.getSearchQueryData()).thenReturn(searchQuery);
        when(searchQuery.getCode()).thenReturn(searchQueryCode);
        when(searchQuery.getSearchType()).thenReturn(searchType);
        when(searchResponseTuple.getSearchRequest()).thenReturn(searchRequest);
        when(searchRequest.getSearchParams()).thenReturn(params);

        SearchQueryData enhancedSearchQuery = enhanceSearchQueryStrategy.enhanceSearchQuery(searchResponseTuple);

        assertEquals(searchQueryCode, enhancedSearchQuery.getCode());
        assertEquals(searchType, enhancedSearchQuery.getSearchType());
        verify(queryDataPopulator).populate(eq(params), eq(enhancedSearchQuery));
    }
}
