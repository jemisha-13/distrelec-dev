package com.distrelec.fusionsearch.response;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.distrelec.fusion.search.dto.FacetDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;

@UnitTest
public class FilterBadgesResponsePopulatorTest extends AbstractSearchResponsePopulatorTest {

    @InjectMocks
    @Spy
    FilterBadgesResponsePopulator filterBadgesResponsePopulator;

    @Mock
    Populator<SearchResponse, FactFinderFacetSearchPageData<SearchQueryData, SearchResultValueData>> responseFilterBadgesPopulator;

    @Test
    public void testConvertFilterTerms() {
        String facetCode = "facetCode";
        String facetName = "facetName";

        FacetDTO facetDTO = mock(FacetDTO.class);
        FilterBadgeData<SearchQueryData> filterBadgeData = mock(FilterBadgeData.class);
        SearchQueryTermData filterTerm = mock(SearchQueryTermData.class);
        SearchQueryData searchQuery = mock(SearchQueryData.class);
        SearchResponseDTO searchResponseDTO = mock(SearchResponseDTO.class);
        FactFinderFacetSearchPageData<SearchQueryData, SearchResultValueData> tempSearchPageData = mock(FactFinderFacetSearchPageData.class);

        when(searchResponseTuple.getSearchQueryData()).thenReturn(searchQuery);
        when(searchResponseTuple.getSearchResponseDTO()).thenReturn(searchResponseDTO);
        when(searchResponseDTO.getFacets()).thenReturn(List.of(facetDTO));
        when(searchQuery.getFilterTerms()).thenReturn(List.of(filterTerm));
        doReturn(tempSearchPageData).when(filterBadgesResponsePopulator).createTempSearchPageData();
        when(tempSearchPageData.getFilters()).thenReturn(List.of(filterBadgeData));

        when(facetDTO.getCode()).thenReturn(facetCode);
        when(facetDTO.getName()).thenReturn(facetName);
        when(filterBadgeData.getFacetCode()).thenReturn(facetCode);

        filterBadgesResponsePopulator.populate(searchResponseTuple, searchPageData);

        verify(responseFilterBadgesPopulator).populate(any(SearchResponse.class), eq(tempSearchPageData));
        verify(filterBadgeData).setFacetName(facetName);

        List<FilterBadgeData<SearchQueryData>> actualFilters = searchPageData.getFilters();
        assertEquals(1, actualFilters.size());
        assertEquals(filterBadgeData, actualFilters.get(0));
    }
}
