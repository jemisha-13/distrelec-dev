package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.MODE_PARAM;
import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.QUERY_PARAM;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.factfinder.webservice.ws71.FFsearch.Filter;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class SearchQueryParamsPopulatorTest extends AbstractParamsPopulatorTest {

    @InjectMocks
    SearchQueryParamsPopulator searchQueryParamsPopulator;

    @Mock
    SearchQueryData searchQuery;

    @Before
    public void setUp() {
        when(searchRequestTuple.getSearchQueryData()).thenReturn(searchQuery);
    }

    @Test
    public void testPopulateFreeTextSearch() {
        String freeTextSearch = "query";
        mockSearchRequest(false);
        when(searchQuery.getFreeTextSearch()).thenReturn(freeTextSearch);
        when(searchQuery.getSearchType()).thenReturn(DistSearchType.CATEGORY_AND_TEXT);
        searchQueryParamsPopulator.populate(searchRequestTuple, params);
        assertContainsParam(QUERY_PARAM, freeTextSearch);
    }

    @Test
    public void testPopulateCategoryMode() {
        mockSearchRequest(false);
        when(searchQuery.getSearchType()).thenReturn(DistSearchType.CATEGORY);
        searchQueryParamsPopulator.populate(searchRequestTuple, params);
        assertContainsParam(MODE_PARAM, "category");
    }

    private void assertContainsParam(String paramName, String paramValue) {
        Collection<String> values = params.get(paramName);
        assertEquals(1, values.size());
        assertEquals(paramValue, values.iterator().next());
    }

    private SearchRequest mockSearchRequest(boolean containsProductFamilyCodeFilter) {
        SearchRequest searchRequest = mock(SearchRequest.class);
        Params searchParams = mock(Params.class);
        ArrayOfFilter arrayOfFilter = mock(ArrayOfFilter.class);

        when(searchRequestTuple.getSearchRequest()).thenReturn(searchRequest);
        when(searchRequest.getSearchParams()).thenReturn(searchParams);
        when(searchParams.getFilters()).thenReturn(arrayOfFilter);

        if (containsProductFamilyCodeFilter) {
            Filter filter = mock(Filter.class);
            when(filter.getName()).thenReturn("productFamilyCode");
            when(arrayOfFilter.getFilter()).thenReturn(List.of(filter));
        } else {
            when(arrayOfFilter.getFilter()).thenReturn(List.of());
        }

        return searchRequest;
    }
}
