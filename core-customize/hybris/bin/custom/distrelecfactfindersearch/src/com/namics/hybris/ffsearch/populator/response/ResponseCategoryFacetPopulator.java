/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_ROOT_PATH_PATTERN;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_PATTERN;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import com.namics.hybris.ffsearch.populator.response.helper.SearchQueryTransformer;

import de.factfinder.webservice.ws71.FFsearch.Group;
import de.factfinder.webservice.ws71.FFsearch.Result;
import de.hybris.platform.converters.Populator;
/**
 * Populates category facets.
 * 
 * @author ceberle, Namics AG
 * @since Namics Extensions 1.0
 * 
 * @param <ITEM>
 */
public class ResponseCategoryFacetPopulator<ITEM> implements Populator<SearchResponse, FactFinderFacetSearchPageData<SearchQueryData, ITEM>> {

    private ResponseFacetPopulator responseFacetPopulator;
    private SearchQueryTransformer queryTransformer;

    @Override
    public void populate(final SearchResponse source, final FactFinderFacetSearchPageData<SearchQueryData, ITEM> target) {
        final Result searchResult = source.getSearchResult();
        final FactFinderFacetData<SearchQueryData> categoryFacets = buildCategoryFacets(searchResult, target.getCurrentQuery());
        target.setCategories(categoryFacets);

        // set search type
        final SearchRequest sourceSearchRequest = source.getSearchRequest();
        if (sourceSearchRequest != null) {
            target.getCurrentQuery().setCode(sourceSearchRequest.getCode());
            target.getCurrentQuery().setSearchType(sourceSearchRequest.getSearchType());
        }

    }

    private FactFinderFacetData<SearchQueryData> buildCategoryFacets(final Result searchResult, final SearchQueryData queryData) {
        final List<FactFinderFacetData<SearchQueryData>> target = new ArrayList<FactFinderFacetData<SearchQueryData>>();
        if (searchResult.getFilters() == null) {
            return null;
        }
        final List<Group> source = getCategoryFacets(searchResult);
        getResponseFacetPopulator().populate(searchResult.getResultCount().longValue(), source, queryData, target);
        return !target.isEmpty() ? target.get(0) : null;
    }

    private List<Group> getCategoryFacets(final Result searchResult) {
        final List<Group> categoryFacets = new ArrayList<Group>();

        for (final Group group : searchResult.getGroups().getGroup()) {
            // Check if associatedFieldName of first GroupElement matches the category pattern
            if (group != null && group.getElements() != null && CollectionUtils.isNotEmpty(group.getElements().getGroupElement())
                    && group.getElements().getGroupElement().get(0) != null) {
                final String associatedFieldName = group.getElements().getGroupElement().get(0).getAssociatedFieldName();
                if (StringUtils.isNotBlank(associatedFieldName) && (CATEGORY_CODE_PATTERN.matcher(associatedFieldName).matches()
                        || CATEGORY_CODE_ROOT_PATH_PATTERN.matcher(associatedFieldName).matches())) {
                    categoryFacets.add(group);
                }
            }
        }

        return categoryFacets;
    }

    // BEGIN GENERATED CODE

    protected SearchQueryTransformer getQueryTransformer() {
        return queryTransformer;
    }

    @Required
    public void setQueryTransformer(final SearchQueryTransformer queryTransformer) {
        this.queryTransformer = queryTransformer;
    }

    public ResponseFacetPopulator getResponseFacetPopulator() {
        return responseFacetPopulator;
    }

    public void setResponseFacetPopulator(final ResponseFacetPopulator responseFacetPopulator) {
        this.responseFacetPopulator = responseFacetPopulator;
    }

}
