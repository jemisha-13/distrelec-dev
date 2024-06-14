package com.distrelec.fusionsearch.response;

import java.util.List;

import com.distrelec.fusion.search.dto.FacetDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class FilterBadgesResponsePopulator implements Populator<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    private Populator<SearchResponse, FactFinderFacetSearchPageData<SearchQueryData, SearchResultValueData>> responseFilterBadgesPopulator;

    @Override
    public void populate(SearchResponseTuple searchResponseTuple,
                         FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) throws ConversionException {
        SearchQueryData searchQuery = searchResponseTuple.getSearchQueryData();
        SearchResponseDTO searchResponseDTO = searchResponseTuple.getSearchResponseDTO();

        List<FacetDTO> facets = searchResponseDTO.getFacets();
        List<SearchQueryTermData> filterTerms = searchQuery.getFilterTerms();
        if (filterTerms != null && facets != null) {
            FactFinderFacetSearchPageData<SearchQueryData, SearchResultValueData> tempSearchPageData =
                    createTempSearchPageData();
            tempSearchPageData.setCurrentQuery(searchQuery);
            SearchResponse tempSearchResponse = new SearchResponse();

            responseFilterBadgesPopulator.populate(tempSearchResponse, tempSearchPageData);

            List<FilterBadgeData<SearchQueryData>> filters = tempSearchPageData.getFilters();
            searchPageData.setFilters(filters);

            // translate filter badges
            for (FilterBadgeData<SearchQueryData> filter : filters) {
                facets.stream().filter(facetDTO -> filter.getFacetCode().equals(facetDTO.getCode()))
                      .forEach(facetDTO -> filter.setFacetName(facetDTO.getName()));
            }
        }
    }

    /**
     * To be used by unit tests.
     */
    FactFinderFacetSearchPageData<SearchQueryData, SearchResultValueData> createTempSearchPageData() {
        return new FactFinderFacetSearchPageData<>();
    }

    public void setResponseFilterBadgesPopulator(Populator<SearchResponse, FactFinderFacetSearchPageData<SearchQueryData, SearchResultValueData>> responseFilterBadgesPopulator) {
        this.responseFilterBadgesPopulator = responseFilterBadgesPopulator;
    }
}
