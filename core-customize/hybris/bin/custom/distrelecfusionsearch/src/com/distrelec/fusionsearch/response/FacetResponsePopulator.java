package com.distrelec.fusionsearch.response;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.CATEGORY_CODES_FACET_CODE;
import static com.distrelec.fusionsearch.response.QueryFilterUtil.urlEncode;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.fusion.search.dto.FacetDTO;
import com.distrelec.fusion.search.dto.FacetValueDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetType;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.populator.response.helper.SearchQueryTransformer;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class FacetResponsePopulator implements Populator<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    @Autowired
    private SearchQueryTransformer queryTransformer;

    @Override
    public void populate(SearchResponseTuple searchResponseTuple,
                         FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) throws ConversionException {
        SearchResponseDTO searchResponseDTO = searchResponseTuple.getSearchResponseDTO();
        SearchQueryData searchQuery = searchResponseTuple.getSearchQueryData();
        List<FacetDTO> facets = searchResponseDTO.getFacets();
        if (facets != null) {
            Set<FeatureValueEntry> selectedFeatureValues = calculateSelectedFeatureValues(searchQuery);
            List<FactFinderFacetData<SearchQueryData>> otherFacets = facets.stream()
                                                                           .map(facetDTO -> convertFacet(facetDTO, selectedFeatureValues, searchQuery))
                                                                           .collect(Collectors.toList());
            searchPageData.setOtherFacets(otherFacets);
        }
    }

    private Set<FeatureValueEntry> calculateSelectedFeatureValues(SearchQueryData searchQuery) {
        Set<FeatureValueEntry> featureValues = new HashSet<>();

        List<SearchQueryTermData> filterTerms = searchQuery.getFilterTerms();
        if (filterTerms != null) {
            for (SearchQueryTermData filterTerm : filterTerms) {
                FeatureValueEntry featureValue = new FeatureValueEntry(filterTerm.getKey(), filterTerm.getValue());
                featureValues.add(featureValue);
            }
        }

        return featureValues;
    }

    private FactFinderFacetData<SearchQueryData> convertFacet(FacetDTO facetDTO, Set<FeatureValueEntry> selectedFeatureValues,
                                                              SearchQueryData searchQuery) {
        FactFinderFacetData<SearchQueryData> facetData = new FactFinderFacetData<>();

        facetData.setCode(facetDTO.getCode());
        facetData.setName(facetDTO.getName());
        facetData.setIsViable(isViable(facetDTO));
        facetData.setType(FactFinderFacetType.CHECKBOX);

        List<FacetValueDTO> facetValues = facetDTO.getValues();
        if (facetValues != null) {
            List<FactFinderFacetValueData<SearchQueryData>> facetValueDataList = facetValues.stream()
                                                                                            .sorted(Comparator.comparing(FacetValueDTO::getValue))
                                                                                            .map(facetValueDTO -> convertFacetValue(facetDTO, facetValueDTO,
                                                                                                                                    selectedFeatureValues,
                                                                                                                                    searchQuery))
                                                                                            .toList();
            facetData.setValues(facetValueDataList);
        }

        return facetData;
    }

    private boolean isViable(FacetDTO facetDTO) {
        String facetCode = facetDTO.getCode();
        return !CATEGORY_CODES_FACET_CODE.equals(facetCode);
    }

    private FactFinderFacetValueData<SearchQueryData> convertFacetValue(FacetDTO facetDTO, FacetValueDTO facetValueDTO,
                                                                        Set<FeatureValueEntry> selectedFeatureValues, SearchQueryData searchQuery) {
        FactFinderFacetValueData<SearchQueryData> facetValueData = new FactFinderFacetValueData<>();
        facetValueData.setName(facetValueDTO.getName());
        facetValueData.setCount(facetValueDTO.getCount());

        String facetKey = facetDTO.getCode();
        String facetValue = facetValueDTO.getValue();
        boolean isSelected = isSelected(facetDTO, facetValueDTO, selectedFeatureValues); // for now
        SearchQueryData refinedSearchQuery = buildQuery(searchQuery, facetKey, facetValue, isSelected);
        StringBuilder queryFilterBuilder = new StringBuilder();
        queryFilterBuilder.append("filter_")
                          .append(urlEncode(facetKey))
                          .append("=")
                          .append(urlEncode(facetValue));

        facetValueData.setQuery(refinedSearchQuery);
        facetValueData.setQueryFilter(queryFilterBuilder.toString());
        facetValueData.setSelected(isSelected);

        return facetValueData;
    }

    private boolean isSelected(FacetDTO facetDTO, FacetValueDTO facetValueDTO, Set<FeatureValueEntry> selectedFeatureValues) {
        String facetCode = facetDTO.getCode();
        String facetValue = facetValueDTO.getValue();
        FeatureValueEntry currentFeatureValue = new FeatureValueEntry(facetCode, facetValue);
        boolean selected = selectedFeatureValues.contains(currentFeatureValue);
        return selected;
    }

    private SearchQueryData buildQuery(SearchQueryData searchQuery, String facetKey, String facetValue, boolean isSelected) {
        SearchQueryData refinedQueryData;
        if (isSelected) {
            refinedQueryData = queryTransformer.refineQueryRemoveFacet(searchQuery, facetKey, facetValue);
        } else {
            refinedQueryData = queryTransformer.refineQueryAddFacet(searchQuery, facetKey, facetValue);
        }
        return refinedQueryData;
    }

    record FeatureValueEntry(String key, String value) {
    }
}
