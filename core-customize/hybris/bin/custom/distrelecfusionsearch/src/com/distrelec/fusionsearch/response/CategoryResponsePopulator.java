package com.distrelec.fusionsearch.response;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.CATEGORY_CODES_FACET_CODE;
import static com.distrelec.fusionsearch.response.QueryFilterUtil.buildQueryFilter;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_ROOT_PATH_PATTERN;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_ROOT_PATH_PREFIX;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.fusion.search.dto.FacetDTO;
import com.distrelec.fusion.search.dto.FacetValueDTO;
import com.distrelec.fusion.search.dto.SearchResponseDTO;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetValueData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.populator.response.helper.SearchQueryTransformer;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class CategoryResponsePopulator implements Populator<SearchResponseTuple, FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData>> {

    @Autowired
    private EnhanceSearchQueryStrategy enhanceSearchQueryStrategy;

    @Autowired
    private SearchQueryTransformer queryTransformer;

    @Override
    public void populate(SearchResponseTuple searchResponseTuple,
                         FactFinderProductSearchPageData<SearchQueryData, SearchResultValueData> searchPageData) throws ConversionException {
        SearchResponseDTO searchResponseDTO = searchResponseTuple.getSearchResponseDTO();
        List<FacetDTO> facets = searchResponseDTO.getFacets();
        if (facets != null) {
            SearchQueryData searchQuery = enhanceSearchQueryStrategy.enhanceSearchQuery(searchResponseTuple);
            List<FactFinderFacetData<SearchQueryData>> convertedFacets = facets.stream()
                                                                               .filter(facetDTO -> CATEGORY_CODES_FACET_CODE.equals(facetDTO.getCode()))
                                                                               .map(facetDTO -> convertFacet(searchQuery, facetDTO))
                                                                               .collect(toList());

            if (!convertedFacets.isEmpty()) {
                searchPageData.setCategories(convertedFacets.get(0));
            }
        }
    }

    private FactFinderFacetData<SearchQueryData> convertFacet(SearchQueryData searchQuery, FacetDTO facetDTO) {
        FactFinderFacetData<SearchQueryData> facetData = new FactFinderFacetData<>();

        List<FacetValueDTO> facetValues = facetDTO.getValues();
        if (facetValues != null) {
            Set<String> selectedCategoryCodes = getSelectedCategoryCodes(searchQuery);
            facetData.setHasSelectedElements(!selectedCategoryCodes.isEmpty());

            int categoryLevel = getCategoryLevel(facetDTO) + 2;
            List<FactFinderFacetValueData<SearchQueryData>> facetValueDataList = facetValues.stream()
                                                                                            .filter(facetValueDTO -> facetValueDTO.getLevel() <= categoryLevel
                                                                                                    || selectedCategoryCodes.contains(facetValueDTO.getValue()))
                                                                                            .map(facetValueDTO -> convertFacetValue(searchQuery, facetValueDTO,
                                                                                                                                    selectedCategoryCodes))
                                                                                            .sorted(Comparator.comparing(FactFinderFacetValueData::getCount,
                                                                                                                         Comparator.reverseOrder()))
                                                                                            .toList();
            facetData.setValues(facetValueDataList);
        }

        return facetData;
    }

    private FactFinderFacetValueData<SearchQueryData> convertFacetValue(SearchQueryData searchQuery, FacetValueDTO facetValueDTO,
                                                                        Set<String> selectedCategoryCodes) {
        FactFinderFacetValueData<SearchQueryData> facetValueData = new FactFinderFacetValueData<>();

        String facetKey = buildFacetKey(facetValueDTO);
        String facetValue = facetValueDTO.getValue();
        boolean isSelected = selectedCategoryCodes.contains(facetValue);
        SearchQueryData refinedSearchQuery = buildQuery(searchQuery, facetKey, facetValue, isSelected);
        String queryFilter = buildQueryFilter(refinedSearchQuery, facetKey, facetValue, isSelected);

        facetValueData.setCode(facetKey);
        facetValueData.setName(facetValue);
        facetValueData.setCount(facetValueDTO.getCount());
        facetValueData.setQuery(refinedSearchQuery);
        facetValueData.setQueryFilter(queryFilter);
        facetValueData.setSelected(isSelected);

        return facetValueData;
    }

    private String buildFacetKey(FacetValueDTO facetValueDTO) {
        StringBuilder facetCodeStringBuilder = new StringBuilder(CATEGORY_CODE_ROOT_PATH_PREFIX);

        String path = facetValueDTO.getPath();
        String pathSegments[] = path.split("\\|\\|");
        for (int pathSegmentId = 1; pathSegmentId < pathSegments.length - 1; pathSegmentId++) {
            String pathSegment = pathSegments[pathSegmentId];
            String categoryCode = pathSegment.split("\\|")[0];

            facetCodeStringBuilder.append("/").append(categoryCode);
        }

        String facetCode = facetCodeStringBuilder.toString();
        return facetCode;
    }

    private SearchQueryData buildQuery(SearchQueryData searchQuery, String facetKey, String facetValue,
                                       boolean isSelected) {
        SearchQueryData refinedQueryData;
        if (isSelected) {
            refinedQueryData = queryTransformer.refineQueryRemoveFacet(searchQuery, facetKey, facetValue);
        } else {
            refinedQueryData = queryTransformer.refineQueryAddFacet(searchQuery, facetKey, facetValue);
        }
        return refinedQueryData;
    }

    private Set<String> getSelectedCategoryCodes(SearchQueryData searchQuery) {
        Set<String> categoryCodes = new HashSet<>();

        List<SearchQueryTermData> filterTerms = searchQuery.getFilterTerms();
        if (filterTerms != null) {
            filterTerms.stream()
                       .filter(this::isCategoryFilter)
                       .map(SearchQueryTermData::getValue)
                       .forEach(categoryCodes::add);
        }

        return categoryCodes;
    }

    private boolean isCategoryFilter(SearchQueryTermData filterTerm) {
        String facetKey = filterTerm.getKey();
        boolean categoryFilter = CATEGORY_CODE_ROOT_PATH_PATTERN.matcher(facetKey).matches();
        return categoryFilter;
    }

    private int getCategoryLevel(FacetDTO facetDTO) {
        int categoryLevel = 0;
        Map<Integer, List<FacetValueDTO>> facetValuesPerLevel = facetDTO.getValues().stream()
                                                                        .collect(groupingBy(FacetValueDTO::getLevel, toList()));

        // mark single category facet values as selected
        for (Integer level : facetValuesPerLevel.keySet()) {
            List<FacetValueDTO> values = facetValuesPerLevel.get(level);
            if (values.size() == 1) {
                FacetValueDTO facetValueDTO = values.get(0);
                categoryLevel = Integer.max(categoryLevel, facetValueDTO.getLevel());
            } else {
                break;
            }
        }

        return categoryLevel;
    }
}
