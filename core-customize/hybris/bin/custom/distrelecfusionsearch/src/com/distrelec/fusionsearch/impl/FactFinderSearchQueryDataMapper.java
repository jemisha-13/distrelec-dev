package com.distrelec.fusionsearch.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class FactFinderSearchQueryDataMapper {

    @Value("#{'${distrelecfusionsearch.factfinder.fusion.filter.ignored}'.split(',')}")
    private List<String> factFinderFusionFiltersIgnored;

    @Value("#{${distrelecfusionsearch.factfinder.fusion.filter.mapping}}")
    private Map<String, String> factFinderFusionFilterMapping;

    private Predicate<SearchQueryTermData> filterOutIgnoredParamsRule = termData -> !factFinderFusionFiltersIgnored.contains(termData.getKey());

    public void updateFFParamsToFusionFormat(SearchQueryData searchQuery) {
        removeUnnecessaryFFParams(searchQuery);
        mapFFParamsToFusionFormat(searchQuery);
    }

    private void removeUnnecessaryFFParams(SearchQueryData searchQuery) {
        removeFilters(searchQuery, filterOutIgnoredParamsRule);
    }

    private void removeFilters(SearchQueryData searchQuery, Predicate<SearchQueryTermData> filterOutRule) {
        List<SearchQueryTermData> updatedFilterTerms = searchQuery.getFilterTerms()
                                                                  .stream()
                                                                  .filter(filterOutRule)
                                                                  .collect(toList());
        searchQuery.setFilterTerms(updatedFilterTerms);
    }

    private void mapFFParamsToFusionFormat(SearchQueryData searchQuery) {
        searchQuery.getFilterTerms()
                   .stream()
                   .forEach(this::updateSearchQueryTermData);
    }

    private void updateSearchQueryTermData(SearchQueryTermData termData) {
        String fusionParam = factFinderFusionFilterMapping.get(termData.getKey());
        if (isNotBlank(fusionParam)) {
            termData.setKey(fusionParam);
        }
    }
}
