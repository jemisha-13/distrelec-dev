package com.distrelec.fusionsearch.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;

@UnitTest
public class SearchResponseTupleImplTest {

    @InjectMocks
    SearchResponseTupleImpl searchResponseTuple;

    @Mock
    SearchQueryPageableData<SearchQueryData> searchQueryPageableData;

    @Mock
    SearchRequest searchRequest;

    @Mock
    SearchResponseDTO searchResponseDTO;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testParameters() {
        assertEquals(searchQueryPageableData, searchResponseTuple.getSearchQueryPageableData());
        assertEquals(searchRequest, searchResponseTuple.getSearchRequest());
        assertEquals(searchResponseDTO, searchResponseTuple.getSearchResponseDTO());
    }

    @Test
    public void testGetPageableData() {
        PageableData pageableData = mock(PageableData.class);
        when(searchQueryPageableData.getPageableData()).thenReturn(pageableData);
        PageableData actualPageableData = searchResponseTuple.getPageableData();
        assertEquals(pageableData, actualPageableData);
    }

    @Test
    public void testGetSeachQuery() {
        SearchQueryData searchQuery = mock(SearchQueryData.class);
        when(searchQueryPageableData.getSearchQueryData()).thenReturn(searchQuery);
        SearchQueryData actualSearchQuery = searchResponseTuple.getSearchQueryData();
        assertEquals(searchQuery, actualSearchQuery);
    }
}
