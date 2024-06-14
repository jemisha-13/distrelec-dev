/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.customer.DistPunchoutService;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;
import com.namics.hybris.ffsearch.populator.common.PriceFilterTranslator;
import com.namics.hybris.ffsearch.populator.response.helper.SearchQueryTransformer;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilterValue;
import de.factfinder.webservice.ws71.FFsearch.Filter;
import de.factfinder.webservice.ws71.FFsearch.FilterValue;
import de.factfinder.webservice.ws71.FFsearch.FilterValueType;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.session.SessionService;

/**
 * Populator which builds the search request filters for the data object representing a search request.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class RequestFiltersPopulator implements Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(RequestFiltersPopulator.class);

    private SessionService sessionService;
    private CommerceCategoryService categoryService;
    private DistManufacturerService distManufacturerService;
    private SearchQueryTransformer queryTransformer;
    private DistPunchoutService distPunchoutService;

    private final Map<DistSearchType, List<Integer>> searchTypeToPromotionMapping =
            ImmutableMap.<DistSearchType, List<Integer>> builder()
                    .put(DistSearchType.OUTLET,
                            Arrays.asList(Integer.valueOf(DistConstants.PromotionLabelRanking.OUTLET),
                                    Integer.valueOf(DistConstants.PromotionLabelRanking.HOT_OFFER), Integer.valueOf(DistConstants.PromotionLabelRanking.OFFER)))
                    .put(DistSearchType.NEW, Arrays.asList(Integer.valueOf(DistConstants.PromotionLabelRanking.NEW))).build();

    @Override
    public void populate(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        final List<SearchQueryTermData> terms = buildQueryTerms(source, target);
        populateFacets(terms, target);
    }

    private List<SearchQueryTermData> buildQueryTerms(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        // add "ordinary" filters, if any
        final List<SearchQueryTermData> terms = Lists.newArrayList();
        CollectionUtils.addAll(terms, source.getSearchQueryData().getFilterTerms().iterator());

        // only one is possible.. either there's a category, manufacturer or an outlet term

        // check for filters (implicitly) set by the navigation category. if there is a navigation category (set as a filter), the field
        // categoryCode is set.
        final List<Optional<SearchQueryTermData>> categoryTerms = buildCategoryTerm(source.getSearchQueryData(), target);
        for (final Optional<SearchQueryTermData> categoryTerm : categoryTerms) {
            if (categoryTerm.isPresent()) {
                terms.add(categoryTerm.get());
            }
        }

        // check for filters (implicitly) set by the navigation manufacturer. if there is a navigation manufacturer (set as a filter), the
        // field manufacturerCode is set.
        final Optional<SearchQueryTermData> manufacturerTerm = buildManufacturerTerm(source.getSearchQueryData(), target);
        if (manufacturerTerm.isPresent()) {
            terms.add(manufacturerTerm.get());
        }

        // set filters for Promotionlabels, active:true
        terms.addAll(buildPromotionTerm(source.getSearchQueryData()));

        // set punchout filters
        final List<String> productHierarchyCodes = distPunchoutService.getPunchoutList();
        for (final String productHierarchyCode : productHierarchyCodes) {
            final SearchQueryTermData term = new SearchQueryTermData();
            term.setKey(DistFactFinderExportColumns.PRODUCT_HIERARCHY.getValue());
            term.setValue(productHierarchyCode);
            term.setExclude(Boolean.TRUE);
            terms.add(term);
        }
        return terms;
    }

    protected void populateFacets(final List<SearchQueryTermData> terms, final SearchRequest target) {
        if (CollectionUtils.isNotEmpty(terms)) {
            target.getSearchParams().setFilters(new ArrayOfFilter());

            // map Filters to KeyValues-Map (aggregated facets)
            final List<AggregatedFilterData> facets = aggregateFilters(terms);
            // map KeyValues-Map (aggregated facets) to the SearchRequest
            convertFacets(facets, target);
        }
    }

    private List<AggregatedFilterData> aggregateFilters(final List<SearchQueryTermData> filterTerms) {

        final ArrayList<AggregatedFilterData> filters = new ArrayList<>();

        for (final SearchQueryTermData term : filterTerms) {
            final String filterName = term.getKey();
            final String filterValue = term.getValue();

            AggregatedFilterData currentFilter = getFilterDataByName(filters, filterName);
            if (currentFilter == null) {
                currentFilter = createFilterData(filterName, term.getSubstring(), term.getExclude());
                filters.add(currentFilter);
            }

            final List<String> filterValues = currentFilter.getValues();
            // age values
            if (!filterValues.contains(filterValue)) {
                final String filterValueWithoutUnit = StringUtils.contains(filterValue, '=') ? StringUtils.substringAfterLast(filterValue, "=") : filterValue;
                filterValues.add(filterValueWithoutUnit);
            }
        }

        return filters;
    }

    private AggregatedFilterData getFilterDataByName(final List<AggregatedFilterData> list, final String name) {
        if (StringUtils.isNotEmpty(name)) {
            for (final AggregatedFilterData filter : list) {
                if (name.equals(filter.getName())) {
                    return filter;
                }
            }
        }
        return null;
    }

    private AggregatedFilterData createFilterData(final String name, final Boolean substring, final Boolean exclude) {
        final AggregatedFilterData filter = new AggregatedFilterData();
        filter.setName(name);
        filter.setSubstring(substring);
        filter.setExclude(exclude);
        filter.setValues(new ArrayList<String>());
        return filter;
    }

    private void convertFacets(final List<AggregatedFilterData> facets, final SearchRequest target) {
        for (final AggregatedFilterData facet : facets) {
            // filter name
            final Filter filter = new Filter();
            filter.setName(PriceFilterTranslator.getPriceSensitiveFacetCode(facet.getName(), target));
            filter.setSubstring(facet.getSubstring());
            filter.setValueList(new ArrayOfFilterValue());
            for (final String facetValue : facet.getValues()) {
                // filter value
                final FilterValue filterValue = new FilterValue();
                filterValue.setType(FilterValueType.OR);
                filterValue.setValue(facetValue);
                filterValue.setExclude(facet.getExclude());
                filter.getValueList().getFilterValue().add(filterValue);
            }
            // add filter to search request
            target.getSearchParams().getFilters().getFilter().add(filter);
        }
    }

    protected List<Optional<SearchQueryTermData>> buildCategoryTerm(final SearchQueryData queryData, final SearchRequest target) {
        final List<Optional<SearchQueryTermData>> categoryTerms = new ArrayList<>();

        if (!DistSearchType.CATEGORY.equals(queryData.getSearchType()) && !DistSearchType.CATEGORY_AND_TEXT.equals(queryData.getSearchType())) {
            // add the category itself as a search query term
            // final List<CategoryModel> categories = distCategoryService.getCategoriesForName(queryData.getFreeTextSearch());
            // if (categories != null && CollectionUtils.isNotEmpty(categories)) {
            // for (CategoryModel category : categories) {
            // if (!(category instanceof ClassificationClassModel) && category.getLevel().intValue() > 2) {
            // categoryTerms.add(getQueryTransformer().transform(category));
            //
            // // add all parent categories as filters as well to have more precision
            // for (final CategoryModel superCategory : category.getAllSupercategories()) {
            // if (!(superCategory instanceof ClassificationClassModel) && superCategory.getLevel().intValue() > 2) {
            // categoryTerms.add(getQueryTransformer().transform(superCategory));
            // }
            // }
            // }
            // }
            // }

            categoryTerms.add(Optional.<SearchQueryTermData> absent());
            return categoryTerms;
        }

        // add category code to request to be able to fetch it later

        target.setCode(queryData.getCode());
        try {
            // add the category itself as a search query term
            final CategoryModel category = getCategoryService().getCategoryForCode(queryData.getCode());
            categoryTerms.add(getQueryTransformer().transform(category));

            // add all parent categories as filters as well to have more precision
            for (final CategoryModel superCategory : category.getAllSupercategories()) {
                if (superCategory != null && superCategory.getLevel() != null && !(superCategory instanceof ClassificationClassModel)
                        && superCategory.getLevel().intValue() > 2) {
                    categoryTerms.add(getQueryTransformer().transform(superCategory));
                }
            }
        } catch (final Exception e) {
            LOG.warn("Could not get category for code [{}]. Ignoring category code as navigation category filter!", queryData.getCode(), e);
        }
        categoryTerms.add(Optional.<SearchQueryTermData> absent());
        return categoryTerms;
    }

    protected Optional<SearchQueryTermData> buildManufacturerTerm(final SearchQueryData queryData, final SearchRequest target) {
        if (!DistSearchType.MANUFACTURER.equals(queryData.getSearchType())) {
            return Optional.absent();
        }
        // add manufacturer code to request to be able to fetch it later
        target.setCode(queryData.getCode());
        try {
            // add manufacturer as a search query term
            final DistManufacturerModel manufacturer = distManufacturerService.getManufacturerByCode(queryData.getCode());
            return getQueryTransformer().transform(manufacturer);
        } catch (final SystemException e) {
            LOG.warn("Could not get manufacturer for code [{}]. Ignoring manufacturer code as navigation manufacturer filter!", queryData.getCode(), e);
        }
        return Optional.absent();
    }

    protected List<SearchQueryTermData> buildPromotionTerm(final SearchQueryData queryData) {
        final List<Integer> promotionCodes = Optional.fromNullable(searchTypeToPromotionMapping.get(queryData.getSearchType())).or(Collections.emptyList());

        try {
            return promotionCodes.stream().map(i -> getQueryTransformer().transformPromotionLabel(i.intValue())).filter(Optional::isPresent).map(Optional::get)
                    .collect(Collectors.toList());
        } catch (final SystemException e) {
          LOG.warn("Could not transform the outlet filter", e);
        }
        return Collections.emptyList();
    }

    // BEGIN GENERATED CODE

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public CommerceCategoryService getCategoryService() {
        return categoryService;
    }

    @Required
    public void setCategoryService(final CommerceCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public SearchQueryTransformer getQueryTransformer() {
        return queryTransformer;
    }

    @Required
    public void setQueryTransformer(final SearchQueryTransformer queryTransformer) {
        this.queryTransformer = queryTransformer;
    }

    public DistManufacturerService getDistManufacturerService() {
        return distManufacturerService;
    }

    @Required
    public void setDistManufacturerService(final DistManufacturerService distManufacturerService) {
        this.distManufacturerService = distManufacturerService;
    }

    public DistPunchoutService getDistPunchoutService() {
        return distPunchoutService;
    }

    @Required
    public void setDistPunchoutService(final DistPunchoutService distPunchoutService) {
        this.distPunchoutService = distPunchoutService;
    }
}
