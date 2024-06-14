/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_PATTERN;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.PRODUCT_FAMILY_CODE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.BUYABLE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PROMOTIONLABELS;
import static com.namics.hybris.ffsearch.util.DistFactFinderUtils.getCategoryCodeFFAttribute;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSource;

import com.google.common.base.Optional;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetType;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;
import com.namics.hybris.ffsearch.populator.common.PriceFilterTranslator;
import com.namics.hybris.ffsearch.populator.response.helper.ResponseFilterBadgesSorter;
import com.namics.hybris.ffsearch.populator.response.helper.SearchQueryTransformer;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfGroupElement;
import de.factfinder.webservice.ws71.FFsearch.CustomParameter;
import de.factfinder.webservice.ws71.FFsearch.Group;
import de.factfinder.webservice.ws71.FFsearch.GroupElement;
import de.factfinder.webservice.ws71.FFsearch.Result;
import de.hybris.platform.converters.Populator;

/**
 * Populator for badge filter items.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 * @param <ITEM>
 */
public class ResponseFilterBadgesPopulator<ITEM> implements Populator<SearchResponse, FactFinderFacetSearchPageData<SearchQueryData, ITEM>> {

    private MessageSource messageSource;
    private ResponseFilterBadgesSorter<SearchQueryData> filterBadgeSorter;
    private SearchQueryTransformer queryTransformer;

    @Override
    public void populate(final SearchResponse source, final FactFinderFacetSearchPageData<SearchQueryData, ITEM> target) {
        target.setFilters(buildFilterBadges(source, target));
    }

    /**
     * Populates the filter badges by fetching the corresponding values from the current filterTerms.
     * 
     * @param source
     */
    protected List<FilterBadgeData<SearchQueryData>> buildFilterBadges(final SearchResponse source,
            final FactFinderFacetSearchPageData<SearchQueryData, ITEM> target) {
        final List<FilterBadgeData<SearchQueryData>> filterBadges = new ArrayList<FilterBadgeData<SearchQueryData>>();

        if (!isFamilyView(source.getSearchResult())) {
            SearchQueryData truncateQuery = getQueryTransformer().cloneSearchQueryDataText(target.getCurrentQuery());
            final Iterator<SearchQueryTermData> filterTerms = getFilterTerms(target.getCurrentQuery());

            while (filterTerms.hasNext()) {
                final SearchQueryTermData filterTerm = filterTerms.next();
                if (isFilterVisible(filterTerm)) {
                    final FilterBadgeData<SearchQueryData> filterBadgeData = createFilterBadgeData();
                    final String facetKey = filterTerm.getKey();
                    final String facetValue = filterTerm.getValue();

                    filterBadgeData.setType(getType(filterTerm, source));
                    filterBadgeData.setFacetCode(facetKey);
                    filterBadgeData.setFacetValueCode(facetValue);
                    filterBadgeData.setFacetName(buildFacetName(facetKey, source));
                    filterBadgeData.setFacetValueName(buildFacetValue(facetKey, facetValue));
                    filterBadgeData.setFacetValuePropertyName(buildFacetPropertyValue(facetKey, facetValue));
                    filterBadgeData.setCategoryFilter(isCategoryFilter(filterTerm));

                    // If this is the last filterTerm then we don't need a truncate query, as that is the same as the current query
                    if (filterTerms.hasNext()) {
                        truncateQuery = getQueryTransformer().refineQueryAddFacet(truncateQuery, facetKey, facetValue);
                        filterBadgeData.setTruncateQuery(truncateQuery);
                    }
                    filterBadgeData.setRemoveQuery(getQueryTransformer()
                            .refineQueryRemoveFacetWithSubcategories(target.getCurrentQuery(), facetKey, facetValue));
                    filterBadges.add(filterBadgeData);
                }
            }
            filterBadgeSorter.sortBreadcrumbs(filterBadges);
        }
        return filterBadges;
    }

    private boolean isFilterVisible(final SearchQueryTermData filterTerm) {
        // Do not show buyable and productFamilyCode filter badge
        if (BUYABLE.getValue().equals(filterTerm.getKey()) || filterTerm.getKey().equals(PRODUCT_FAMILY_CODE)
                || filterTerm.getKey().equals(PROMOTIONLABELS.getValue())
                || filterTerm.getKey().equals(getCategoryCodeFFAttribute(1))
                || filterTerm.getKey().equals(getCategoryCodeFFAttribute(2))) {
            return false;
        }

        return true;
    }

    private boolean isCategoryFilter(final SearchQueryTermData filterTerm) {
        return CATEGORY_CODE_PATTERN.matcher(filterTerm.getKey()).matches();
    }

    /**
     * Selects all filter terms to be displayed in the frontend.
     * 
     * Filters which are strict supercategories of an implicit navigation category filter, are not taken into account.
     */
    private Iterator<SearchQueryTermData> getFilterTerms(final SearchQueryData query) {
        final List<SearchQueryTermData> filterTerms = new ArrayList<SearchQueryTermData>();
        for (final SearchQueryTermData term : query.getFilterTerms()) {
            // only add terms...
            if (doAddFilterTerm(term, query)) {
                filterTerms.add(term);
            }
        }
        return filterTerms.iterator();
    }

    private boolean doAddFilterTerm(final SearchQueryTermData term, final SearchQueryData query) {
        // Do not add a manufacturer filter term which is the implicit navigation manufacturer
        if (DistSearchType.MANUFACTURER.equals(query.getSearchType())) {
            final Optional<DistManufacturerModel> distManufacturer = getQueryTransformer().transformManufacturer(term, query.getCode());
            if (distManufacturer.isPresent() && StringUtils.equals(term.getValue(), distManufacturer.get().getName())) {
                return false;
            }
        }

        return true;
    }

    private FactFinderFacetType getType(final SearchQueryTermData searchTerm, final SearchResponse searchResponse) {
        final Group group = getFacetGroupForFacetKey(searchTerm.getKey(), searchResponse);
        if (group != null) {
            return FactFinderFacetType.fromFacet(group);
        }

        return FactFinderFacetType.CHECKBOX; // default
    }

    private String buildFacetName(final String facetKey, final SearchResponse searchResponse) {
        final Group group = getFacetGroupForFacetKey(facetKey, searchResponse);
        if (group != null) {
            return group.getName();
        }

        return facetKey;
    }

    private Group getFacetGroupForFacetKey(final String facetKey, final SearchResponse searchResponse) {
        if (StringUtils.isNotBlank(facetKey) && searchResponse.getSearchResult() != null && searchResponse.getSearchResult().getGroups() != null
                && searchResponse.getSearchResult().getGroups().getGroup() != null) {

            final String priceSensitiveFacetCode = PriceFilterTranslator.getPriceSensitiveFacetCode(facetKey, searchResponse.getSearchRequest());

            for (final Group group : searchResponse.getSearchResult().getGroups().getGroup()) {
                if (checkGroupContainsFacetKey(group, priceSensitiveFacetCode)) {
                    return group;
                }
            }
        }

        return null;
    }

    private boolean checkGroupContainsFacetKey(final Group group, final String priceSensitiveFacetCode) {
        if (group == null) {
            return false;
        }

        // Return true if any element or any selectedElement has the same associatedFieldName
        return checkArrayOfGroupElementContaintsFacetKey(group.getElements(), priceSensitiveFacetCode)
                || checkArrayOfGroupElementContaintsFacetKey(group.getSelectedElements(), priceSensitiveFacetCode);
    }

    private boolean checkArrayOfGroupElementContaintsFacetKey(final ArrayOfGroupElement arrayOfGroupElement, final String priceSensitiveFacetCode) {
        if (arrayOfGroupElement == null) {
            return false;
        }

        if (arrayOfGroupElement.getGroupElement() != null) {
            for (final GroupElement groupElement : arrayOfGroupElement.getGroupElement()) {
                if (groupElement != null && priceSensitiveFacetCode.equals(groupElement.getAssociatedFieldName())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * e.g.<br />
     * facetKey = "Impedans @ 25 MHz~~Ohm"<br />
     * facetValue = "133"<br />
     * 
     * @return 133 Ohm
     */
    private String buildFacetValue(final String facetKey, final String facetValue) {
        final StringBuilder value = new StringBuilder();
        value.append(facetValue).append(" ").append(StringUtils.substringAfter(facetKey, FACTFINDER_UNIT_PREFIX));
        return StringUtils.trim(value.toString());
    }

    /**
     * Returns the property name if there is one needed for the given facet value.
     */
    private String buildFacetPropertyValue(final String facetKey, final String facetValue) {
        if (DistrelecfactfindersearchConstants.FILTER_INSTOCK.equalsIgnoreCase(facetKey)) {
            if ("1".equalsIgnoreCase(facetValue)) {
                return DistrelecfactfindersearchConstants.FILTER_INSTOCK_SLOW;
            } else if ("2".equalsIgnoreCase(facetValue)) {
                return DistrelecfactfindersearchConstants.FILTER_INSTOCK_FAST;
            }
        }
        if (DistrelecfactfindersearchConstants.FILTER_PICKUP.equalsIgnoreCase(facetKey)) {
            if ("1".equalsIgnoreCase(facetValue)) {
                return DistrelecfactfindersearchConstants.FILTER_BOOLEAN_TRUE;
            }
        }

        return StringUtils.EMPTY;
    }

    private boolean isFamilyView(final Result searchResult) {
        if (searchResult != null && searchResult.getSearchParams() != null && searchResult.getSearchParams().getDetailCustomParameters() != null) {
            final List<CustomParameter> customParameters = searchResult.getSearchParams().getDetailCustomParameters().getCustomParameter();
            for (final CustomParameter customParameter : customParameters) {
                if (DistrelecfactfindersearchConstants.PRODUCT_FAMILY_CODE.equals(customParameter.getName())) {
                    return true;
                }
            }
        }

        return false;
    }

    protected FilterBadgeData<SearchQueryData> createFilterBadgeData() {
        return new FilterBadgeData<SearchQueryData>();
    }

    // BEGIN GENERATED CODE

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    @Required
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected ResponseFilterBadgesSorter<SearchQueryData> getFilterBadgeSorter() {
        return filterBadgeSorter;
    }

    @Required
    public void setFilterBadgeSorter(final ResponseFilterBadgesSorter<SearchQueryData> filterBadgeSorter) {
        this.filterBadgeSorter = filterBadgeSorter;
    }

    protected SearchQueryTransformer getQueryTransformer() {
        return queryTransformer;
    }

    @Required
    public void setQueryTransformer(final SearchQueryTransformer queryTransformer) {
        this.queryTransformer = queryTransformer;
    }

}
