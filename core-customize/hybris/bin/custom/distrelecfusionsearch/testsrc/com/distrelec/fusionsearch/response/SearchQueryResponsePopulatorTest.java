package com.distrelec.fusionsearch.response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class SearchQueryResponsePopulatorTest extends AbstractSearchResponsePopulatorTest {

    @InjectMocks
    SearchQueryResponsePopulator responsePopulator;

    @Test
    public void testPopulateCurrentSearchQuery() {
        DistSearchType searchType = DistSearchType.NEW;

        SearchQueryData searchQuery = mock(SearchQueryData.class);

        when(searchResponseTuple.getSearchQueryData()).thenReturn(searchQuery);
        when(searchQuery.getSearchType()).thenReturn(searchType);

        responsePopulator.populate(searchResponseTuple, searchPageData);

        assertEquals(searchQuery, searchPageData.getCurrentQuery());
        assertEquals(searchType, searchPageData.getSearchType());
    }
}
