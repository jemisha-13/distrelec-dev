package com.namics.distrelec.occ.core.util.ws.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.MultiSet;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.namics.distrelec.occ.core.util.ws.SearchQueryCodec;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class DefaultSearchQueryCodec implements SearchQueryCodec<SearchQueryData> {

    public static final String MAIN_CATEGORY_PARAM = "mainCategory";

    @Override
    public SearchQueryData decodeQuery(final String queryString) {
        SearchQueryData searchQuery = new SearchQueryData();
        if (isBlank(queryString)) {
            return searchQuery;
        }
        String[] splittedFilterTerms = queryString.split(":");
        MultiValuedMap<String, String> filtersMap = getFiltersMap(splittedFilterTerms);

        searchQuery.setFreeTextSearch(splittedFilterTerms.length > 0 ? splittedFilterTerms[0] : EMPTY);
        searchQuery.setSort(splittedFilterTerms.length > 1 ? splittedFilterTerms[1] : EMPTY);
        searchQuery.setSearchType(getSearchType(filtersMap.keys(), searchQuery.getFreeTextSearch()));
        searchQuery.setCode(getCode(searchQuery.getSearchType(), filtersMap));
        searchQuery.setFilterTerms(getFilterTerms(filtersMap, searchQuery.getSearchType()));
        searchQuery.setTechnicalView(Boolean.valueOf(getFirstValue(filtersMap, "useTechnicalView")));
        return searchQuery;
    }

    private List<SearchQueryTermData> getFilterTerms(MultiValuedMap<String, String> filtersMap, DistSearchType searchType) {
        return filtersMap.keys().stream()
                         .distinct()
                         .filter(mapKey -> isValidFilterTerm(mapKey, searchType))
                         .map(filterMapKey -> createFilterTerms(filterMapKey, filtersMap.get(filterMapKey)))
                         .flatMap(List::stream)
                         .collect(Collectors.toList());
    }

    private boolean isValidFilterTerm(String mapKey, DistSearchType searchType) {
        if (mapKey.startsWith(DistrelecfactfindersearchConstants.FILTER)) {
            if (DistSearchType.MANUFACTURER.equals(searchType)) {
                return !mapKey.equals(DistrelecfactfindersearchConstants.MANUFACTURER_CODE_PATH_ROOT);
            }
            return true;
        }
        return false;
    }

    private MultiValuedMap<String, String> getFiltersMap(String[] splittedFilterTerms) {
        MultiValuedMap<String, String> filtersMap = new ArrayListValuedHashMap<>();
        for (int i = 2; (i + 1) < splittedFilterTerms.length; i += 2) {
            filtersMap.put(splittedFilterTerms[i], splittedFilterTerms[i + 1]);
        }
        return filtersMap;
    }

    private List<SearchQueryTermData> createFilterTerms(String filterMapKey, Collection<String> filterMapValues) {
        return filterMapValues.stream()
                              .map(value -> createFilterTerm(filterMapKey, value))
                              .collect(Collectors.toList());
    }

    private SearchQueryTermData createFilterTerm(String key, String value) {
        SearchQueryTermData term = new SearchQueryTermData();
        term.setKey(key.substring(DistrelecfactfindersearchConstants.FILTER.length()));
        term.setValue(value);
        return term;
    }

    private String getCode(DistSearchType searchType, MultiValuedMap<String, String> filtersMap) {
        if (DistSearchType.CATEGORY.equals(searchType)) {
            return getFirstValue(filtersMap, MAIN_CATEGORY_PARAM);
        }
        if (DistSearchType.MANUFACTURER.equals(searchType)) {
            return getFirstValue(filtersMap, DistrelecfactfindersearchConstants.MANUFACTURER_CODE_PATH_ROOT);
        }
        return EMPTY;
    }

    private DistSearchType getSearchType(MultiSet<String> filterKeys, String freeTextSearch) {
        if (notFreeTextSearch(freeTextSearch)) {
            if (filterKeys.contains(DistrelecfactfindersearchConstants.OUTLET_SEARCH_PARAMETER_NAME)) {
                return DistSearchType.OUTLET;
            }
            if (filterKeys.contains(DistrelecfactfindersearchConstants.NEW_SEARCH_PARAMETER_NAME)) {
                return DistSearchType.NEW;
            }
            if (filterKeys.contains(DistrelecfactfindersearchConstants.MANUFACTURER_CODE_PATH_ROOT)) {
                return DistSearchType.MANUFACTURER;
            }
            if (filterKeys.contains(MAIN_CATEGORY_PARAM)) {
                return DistSearchType.CATEGORY;
            }
        }
        return DistSearchType.TEXT;
    }

    private boolean notFreeTextSearch(String freeTextSearch) {
        return isBlank(freeTextSearch) || "*".equals(freeTextSearch);
    }

    @Override
    public String encodeQuery(SearchQueryData searchQueryData) {
        if (searchQueryData == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(Optional.ofNullable(searchQueryData.getFreeTextSearch()).orElse(EMPTY));
        if (searchQueryData.getSort() != null || isNotEmpty(searchQueryData.getFilterTerms())) {
            builder.append(":");
            builder.append(Optional.ofNullable(searchQueryData.getSort()).orElse(EMPTY));
        }
        emptyIfNull(searchQueryData.getFilterTerms()).forEach(term -> appendToQuery(builder, term));
        return builder.toString();
    }

    private void appendToQuery(StringBuilder builder, SearchQueryTermData term) {
        builder.append(":");
        builder.append(term.getKey());
        builder.append(":");
        builder.append(term.getValue());
    }

    private String getFirstValue(MultiValuedMap<String, String> filtersMap, String key) {
        return filtersMap.get(key)
                         .stream()
                         .findFirst()
                         .orElse(EMPTY);
    }
}
