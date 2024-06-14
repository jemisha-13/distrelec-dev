/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response.helper;

import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.CATEGORY_CODE_PATTERN;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.BUYABLE;
import static com.namics.hybris.ffsearch.util.DistFactFinderUtils.getCategoryCodeFFAttribute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import com.namics.distrelec.b2b.core.util.DistUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Optional;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;
import com.namics.hybris.ffsearch.data.search.AdvisorStatusData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchQueryTermData;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.CommerceCategoryService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.i18n.I18NService;

/**
 * Helper class to work with {@link SearchQueryData} objects and their transformations.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class SearchQueryTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(SearchQueryTransformer.class);

    private CommerceCategoryService categoryService;
    private I18NService i18nService;
    private DistManufacturerService distManufacturerService;
    private CategoryNavigationHelper categoryNavigationHelper;
    private DistCategoryService distCategoryService;

    /**
     * Get an optional {@link DistManufacturerModel} from a {@link SearchQueryTermData} and a manufacturer code.
     *
     * @param term
     *            search term to get the DistManufacturerModel for.
     * @param manufacturerCode
     *            code of the DistManufacturerModel
     * @return Optional DistManufacturerModel
     */
    public Optional<DistManufacturerModel> transformManufacturer(final SearchQueryTermData term, final String manufacturerCode) {
        // check if there is a manufacturer code and the term is a manufacturer filter
        if (term == null || StringUtils.isEmpty(manufacturerCode)) {
            return Optional.absent();
        }
        try {
            final DistManufacturerModel distManufacturer = getDistManufacturerService().getManufacturerByCode(manufacturerCode);
            return Optional.fromNullable(distManufacturer);
        } catch (final SystemException e) {
            LOG.warn("Could not get manufacturer for code [{}]. Ignoring manufacturer code as navigation category filter!", manufacturerCode, e);
        }
        return Optional.absent();
    }

    /**
     * Get an optional {@link SearchQueryTermData} for a category.
     *
     * @param category
     *            CategoryModel
     * @return Optional SearchQueryTermData
     */
    public Optional<SearchQueryTermData> transform(final CategoryModel category) {
        if (category == null || category.getLevel() == null) {
            return Optional.absent();
        }
        final SearchQueryTermData term = buildQueryTerm(category);
        if (StringUtils.isEmpty(term.getKey()) || StringUtils.isEmpty(term.getValue())) {
            return Optional.absent();
        }
        return Optional.fromNullable(term);
    }

    /**
     * Get an optional {@link SearchQueryTermData} for a manufacturer.
     *
     * @param manufacturer
     *            DistManufacturerModel
     * @return Optional SearchQueryTermData
     */
    public Optional<SearchQueryTermData> transform(final DistManufacturerModel manufacturer) {
        if (manufacturer == null) {
            return Optional.absent();
        }
        final SearchQueryTermData term = buildQueryTerm(manufacturer);
        if (StringUtils.isEmpty(term.getKey()) || StringUtils.isEmpty(term.getValue())) {
            return Optional.absent();
        }
        return Optional.fromNullable(term);
    }

    public Optional<SearchQueryTermData> transformPromotionLabel(final int promotionLabelRanking) {

        final SearchQueryTermData term = buildQueryTermPromotion(promotionLabelRanking);
        if (StringUtils.isEmpty(term.getKey()) || StringUtils.isEmpty(term.getValue())) {
            return Optional.absent();
        }
        return Optional.fromNullable(term);
    }

    private SearchQueryTermData buildQueryTerm(final CategoryModel category) {
        final String categoryFilterName = getCategoryCodeFFAttribute(category.getLevel());
        final String categoryFilterValue = category.getCode();
        final SearchQueryTermData term = new SearchQueryTermData();
        term.setKey(categoryFilterName);
        term.setValue(encodeFactFinderSpecialChars(categoryFilterValue));
        return term;
    }

    private String encodeFactFinderSpecialChars(final String input) {
        if (input != null) {
            return  DistUtils.encodeFfSpecialChars(input)
                    .replace("—", "&mdash;")
                    .replace("–", "%2013");
        } else {
            return StringUtils.EMPTY;
        }
    }

    private SearchQueryTermData buildQueryTerm(final DistManufacturerModel manufacturer) {
        final String manufacturerFilterName = DistFactFinderExportColumns.MANUFACTURER.getValue();
        final String manufacturerFilterValue = manufacturer.getName();
        final SearchQueryTermData term = new SearchQueryTermData();
        term.setKey(manufacturerFilterName);
        term.setValue(manufacturerFilterValue);
        return term;
    }

    private SearchQueryTermData buildQueryTermPromotion(final int promotionLabelRanking) {

        return getPromotionLabelQueryTerm(promotionLabelRanking);
    }

    private SearchQueryTermData getPromotionLabelQueryTerm(final int ranking) {
        final String outletFilterName = DistFactFinderExportColumns.PROMOTIONLABELS.getValue();
        final String outletFilterValue = "rank:" + ranking + ",active:true";
        final SearchQueryTermData term = new SearchQueryTermData();
        term.setKey(outletFilterName);
        term.setValue(outletFilterValue);
        term.setSubstring(Boolean.TRUE);
        return term;
    }

    /**
     * Adds the given facet to the query object. This method is useful to reflect the new active search filters, after a user has added a
     * facet filter.
     *
     * @param queryData
     *            Query object to which to add the facet.
     * @param facet
     *            Key of the facet to add
     * @param facetValue
     *            Value of the facet to add
     * @return a new Query object with the current filters.
     */
    public SearchQueryData refineQueryAddFacet(final SearchQueryData queryData, final String facet, final String facetValue) {
        final SearchQueryTermData newTerm = createSearchQueryTermData();
        newTerm.setKey(facet);
        newTerm.setValue(facetValue);

        final List<SearchQueryTermData> newTerms = new ArrayList<SearchQueryTermData>(queryData.getFilterTerms());

        final Iterator<SearchQueryTermData> iterator = newTerms.iterator();
        while (iterator.hasNext()) {
            final SearchQueryTermData term = iterator.next();
            // remove the buyable facet, as it get set newly for the next request
            if (BUYABLE.getValue().equals(term.getKey())) {
                iterator.remove();
            }
        }

        newTerms.add(newTerm);

        // Build the new query data
        final SearchQueryData result = cloneSearchQueryData(queryData);
        result.setFilterTerms(newTerms);
        updateMoreSpecificCategoryCode(result, facet);
        return result;
    }

    protected void updateMoreSpecificCategoryCode(final SearchQueryData queryData, final String facet) {
        // Update only if searched by category and added facet is of type category
        if ((DistSearchType.CATEGORY.equals(queryData.getSearchType()) || DistSearchType.CATEGORY_AND_TEXT.equals(queryData.getSearchType()))
                && DistrelecfactfindersearchConstants.CATEGORY_CODE_PATTERN.matcher(facet).matches()) {
            // Get most specific category
            final String mostSpecificCategoryName = getMostSpecificCategoryName(queryData);
            if (mostSpecificCategoryName != null) {
                // Get category for name
                final CategoryModel category = categoryNavigationHelper.getCategoryForNameDownwards(queryData.getCode(), mostSpecificCategoryName);
                if (category != null) {
                    // Update code
                    queryData.setCode(category.getCode());
                }
            }
        }
    }

    protected String getMostSpecificCategoryName(final SearchQueryData searchQuery) {
        String mostSpecificCategoryName = null;
        int mostSpecificCategoryLevel = 0;
        for (final SearchQueryTermData filterTerm : searchQuery.getFilterTerms()) {
            final Matcher matcher = DistrelecfactfindersearchConstants.CATEGORY_CODE_PATTERN.matcher(filterTerm.getKey());
            if (matcher.matches()) {
                final int currentLevel = Integer.parseInt(matcher.group(1));
                if (currentLevel > mostSpecificCategoryLevel) {
                    mostSpecificCategoryLevel = currentLevel;
                    mostSpecificCategoryName = filterTerm.getValue();
                }
            }
        }

        return mostSpecificCategoryName;
    }

    /**
     * Removes the given facet from the query object. This method is useful to reflect the new active search filters, after a user has
     * removed a facet filter.
     *
     * @param queryData
     *            Query object from where to remove to search filter
     * @param facet
     *            Key of the facet to remove
     * @param facetValue
     *            Value of the facet to remove
     * @return a new Query object with the current filters.
     */
    public SearchQueryData refineQueryRemoveFacet(final SearchQueryData queryData, final String facet, final String facetValue) {
        final List<SearchQueryTermData> newTerms = new ArrayList<SearchQueryTermData>(queryData.getFilterTerms());
        // Remove the term for the specified facet
        final Iterator<SearchQueryTermData> iterator = newTerms.iterator();
        while (iterator.hasNext()) {
            final SearchQueryTermData term = iterator.next();
            // remove the deselected badge
            if (facet.equals(term.getKey()) && facetValue.equals(term.getValue())) {
                iterator.remove();
            }

            // remove the buyable facet, as it get set newly for the next request
            if (BUYABLE.getValue().equals(term.getKey())) {
                iterator.remove();
            }


        }
        // Build the new query data
        final SearchQueryData result = cloneSearchQueryData(queryData);
        result.setFilterTerms(newTerms);
        return result;
    }

    /**
     * Removes the given facet from the query object <b>AND</b> if the removed query was a category facet, also remove facets which are
     * subcategories of the given removed category. This method is useful to reflect the new active search filters, after a user has removed
     * a facet filter.
     *
     * @param queryData
     *            Query object from where to remove to search filter
     * @param facet
     *            Key of the facet to remove
     * @param facetValue
     *            Value of the facet to remove
     * @return a new Query object with the current filters.
     */
    public SearchQueryData refineQueryRemoveFacetWithSubcategories(final SearchQueryData queryData, final String facet, final String facetValue) {
        final List<SearchQueryTermData> newTerms = new ArrayList<SearchQueryTermData>(queryData.getFilterTerms());

        // Remove the term for the specified facet
        final Iterator<SearchQueryTermData> iterator = newTerms.iterator();
        while (iterator.hasNext()) {
            final SearchQueryTermData term = iterator.next();
            if (StringUtils.equals(facet, term.getKey()) && StringUtils.equals(facetValue, term.getValue())) {
                iterator.remove();
            }
            // remove the buyable facet, as it get set newly for the next request
            else if (BUYABLE.getValue().equals(term.getKey())) {
                iterator.remove();
            }
            // remove badges which are a subcategory of the facet badge to be removed
            else if (isSubCategoryOfFacet(term, facet)) {
                iterator.remove();
            }

        }

        // Build the new query data
        final SearchQueryData result = cloneSearchQueryData(queryData);
        result.setFilterTerms(newTerms);
        updateLessSpecificCategoryCode(result, facet);
        return result;
    }

    private void updateLessSpecificCategoryCode(final SearchQueryData queryData, final String facet) {
        // Update only if searched by category...
        if (DistSearchType.CATEGORY.equals(queryData.getSearchType()) || DistSearchType.CATEGORY_AND_TEXT.equals(queryData.getSearchType())) {
            // ...and removed facet is of type category
            final Matcher matcher = CATEGORY_CODE_PATTERN.matcher(facet);
            if (matcher.matches()) {
                final Integer levelOfRemovedCategory = Integer.valueOf(matcher.group(1));
                CategoryModel category = getCategoryService().getCategoryForCode(queryData.getCode());

                while (true) {
                    if (category.getLevel() == null || category.getLevel().compareTo(levelOfRemovedCategory) < 0) {
                        break;
                    }

                    final List<CategoryModel> superCategories = getDistCategoryService().getProductSuperCategories(category);
                    if (CollectionUtils.isEmpty(superCategories)) {
                        break;
                    } else {
                        category = superCategories.get(0);
                    }
                }

                queryData.setCode(category.getCode());
            }
        } else {
            // We have to reset the category code for the family search
            if (!DistSearchType.MANUFACTURER.equals(queryData.getSearchType())) {
                queryData.setCode(null);
            }
        }
    }

    /**
     * If the category number checked in the key() value of parameter <code>term</code> is smaller than the category number of the parameter
     * <code>facet</code>, the <code>term</code> is a subcategory of <code>facet</code>. e.g. when: <br/>
     * <br/>
     *
     * <code>term.getKey() == "Category3"</code> <br/>
     * <code>facet == "Category2"</code> <br/>
     * <br/>
     *
     * ==> term is a subcategory of facet
     *
     * @param term
     *            Term to be checked, e.g. {"Category3", "electornics"} or {"manufacturer", "sony"}
     * @param facet
     *            Facet to be checked against, e.g. Category3
     * @return <b>true</b>, if term and facet are a category filter, and term is a subcategory of facet. <b>false</b> otherwise.
     */
    protected boolean isSubCategoryOfFacet(final SearchQueryTermData term, final String facet) {
        final Matcher termKeyMatcher = CATEGORY_CODE_PATTERN.matcher(term.getKey());
        if (!termKeyMatcher.matches()) {
            return false;
        }

        final Matcher facetMatcher = CATEGORY_CODE_PATTERN.matcher(facet);
        if (!facetMatcher.matches()) {
            return false;
        }

        // the term is a subcategory of the facet, if the category number of the term is bigger than the category number of the facet
        return Integer.parseInt(termKeyMatcher.group(1)) > Integer.parseInt(facetMatcher.group(1));
    }

    /**
     * Shallow clone of the source SearchQueryData
     *
     * @param source
     *            the instance to clone
     * @return the shallow clone
     */
    public SearchQueryData cloneSearchQueryDataText(final SearchQueryData source) {
        final SearchQueryData result = cloneSearchQueryData(source);
        result.setFilterTerms(Collections.<SearchQueryTermData> emptyList());
        return result;
    }

    /**
     * Shallow clone of the source SearchQueryData
     *
     * @param source
     *            the instance to clone
     * @return the shallow clone
     */
    public SearchQueryData cloneSearchQueryData(final SearchQueryData source) {
        final SearchQueryData target = createSearchQueryData();
        target.setFreeTextSearch(source.getFreeTextSearch());
        target.setSearchType(source.getSearchType());
        target.setCode(source.getCode());
        target.setSort(source.getSort());
        target.setFilterTerms(new ArrayList(source.getFilterTerms()));
        target.setAdvisorStatus(cloneAdvisorStatusData(source.getAdvisorStatus()));
        target.setAdditionalControlParams(source.getAdditionalControlParams());
        target.setAdditionalSearchParams(source.getAdditionalSearchParams());
        return target;
    }

    private AdvisorStatusData cloneAdvisorStatusData(final AdvisorStatusData advisorStatus) {
        if (advisorStatus == null) {
            return null;
        } else {
            final AdvisorStatusData clone = new AdvisorStatusData();
            clone.setAnswerPath(advisorStatus.getAnswerPath());
            clone.setCampaignId(advisorStatus.getCampaignId());
            return clone;
        }
    }

    private SearchQueryData createSearchQueryData() {
        return new SearchQueryData();
    }

    private SearchQueryTermData createSearchQueryTermData() {
        return new SearchQueryTermData();
    }

    // BEGIN GENERATED CODE

    protected CommerceCategoryService getCategoryService() {
        return categoryService;
    }

    @Required
    public void setCategoryService(final CommerceCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    protected I18NService getI18nService() {
        return i18nService;
    }

    @Required
    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    public DistManufacturerService getDistManufacturerService() {
        return distManufacturerService;
    }

    @Required
    public void setDistManufacturerService(final DistManufacturerService distManufacturerService) {
        this.distManufacturerService = distManufacturerService;
    }

    public CategoryNavigationHelper getCategoryNavigationHelper() {
        return categoryNavigationHelper;
    }

    @Required
    public void setCategoryNavigationHelper(final CategoryNavigationHelper categoryNavigationHelper) {
        this.categoryNavigationHelper = categoryNavigationHelper;
    }

    public DistCategoryService getDistCategoryService() {
        return distCategoryService;
    }

    @Required
    public void setDistCategoryService(final DistCategoryService distCategoryService) {
        this.distCategoryService = distCategoryService;
    }
}
