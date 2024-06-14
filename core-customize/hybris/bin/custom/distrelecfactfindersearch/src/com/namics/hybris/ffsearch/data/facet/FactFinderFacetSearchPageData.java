package com.namics.hybris.ffsearch.data.facet;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.namics.hybris.ffsearch.data.campaign.advisor.AdvisorCampaignData;
import com.namics.hybris.ffsearch.data.campaign.feedback.FeedbackCampaignData;
import com.namics.hybris.ffsearch.data.paging.FactFinderSortData;

import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;

public class FactFinderFacetSearchPageData<STATE, RESULT> extends SearchPageData<RESULT> {

    private STATE currentQuery;

    // possible search facets classified as categories
    private FactFinderFacetData<STATE> categories;

    // current active filters
    private List<FilterBadgeData<STATE>> filters;

    // possible search facets which are NOT classified as categories (e.g. battery size, price, etc).
    private List<FactFinderFacetData<STATE>> otherFacets;

    private List<FactFinderLazyFacetData<STATE>> lazyFacets;

    private List<FactFinderSortData> sorting;

    private List<FeedbackCampaignData<RESULT>> feedbackCampaigns;

    private List<AdvisorCampaignData<STATE>> advisorCampaigns;

    private String removeFiltersURL;

    private List<SingleWordSearchItem<RESULT>> singleWordSearchItems;

    private boolean mpnMatch;

    private boolean filtersRemovedGeneralSearch;

    private String productFamilyCategoryCode;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    public STATE getCurrentQuery() {
        return currentQuery;
    }

    public String getProductFamilyCategoryCode() {
        return productFamilyCategoryCode;
    }

    public boolean isFiltersRemovedGeneralSearch() {
        return filtersRemovedGeneralSearch;
    }

    public void setCurrentQuery(final STATE currentQuery) {
        this.currentQuery = currentQuery;
    }

    public List<FilterBadgeData<STATE>> getFilters() {
        return filters;
    }

    public void setFilters(final List<FilterBadgeData<STATE>> filters) {
        this.filters = filters;
    }

    public FactFinderFacetData<STATE> getCategories() {
        return categories;
    }

    public void setCategories(final FactFinderFacetData<STATE> categories) {
        this.categories = categories;
    }

    public List<FactFinderFacetData<STATE>> getOtherFacets() {
        return otherFacets;
    }

    public void setOtherFacets(final List<FactFinderFacetData<STATE>> otherFacets) {
        this.otherFacets = otherFacets;
    }

    public List<FactFinderSortData> getSorting() {
        return sorting;
    }

    public void setSorting(final List<FactFinderSortData> sorting) {
        this.sorting = sorting;
    }

    public List<FactFinderLazyFacetData<STATE>> getLazyFacets() {
        return lazyFacets;
    }

    public void setLazyFacets(final List<FactFinderLazyFacetData<STATE>> lazyFacets) {
        this.lazyFacets = lazyFacets;
    }

    /**
     * @deprecated use {@link #getSorting()} ()}
     */
    @Override
    @Deprecated
    public List<SortData> getSorts() {
        //throw new UnsupportedOperationException();
        return null; // returns null because of ws mapper
    }

    /**
     * @deprecated use {@link #setSorting(List)}
     */
    @Override
    @Deprecated
    public void setSorts(final List<SortData> sorts) {
        throw new UnsupportedOperationException();
    }

    public List<AdvisorCampaignData<STATE>> getAdvisorCampaigns() {
        return advisorCampaigns;
    }

    public void setAdvisorCampaigns(final List<AdvisorCampaignData<STATE>> advisorCampaigns) {
        this.advisorCampaigns = advisorCampaigns;
    }

    public List<FeedbackCampaignData<RESULT>> getFeedbackCampaigns() {
        return feedbackCampaigns;
    }

    public void setFeedbackCampaigns(final List<FeedbackCampaignData<RESULT>> feedbackCampaigns) {
        this.feedbackCampaigns = feedbackCampaigns;
    }

    public String getRemoveFiltersURL() {
        return removeFiltersURL;
    }

    public void setRemoveFiltersURL(final String removeFiltersURL) {
        this.removeFiltersURL = removeFiltersURL;
    }

    public List<SingleWordSearchItem<RESULT>> getSingleWordSearchItems() {
        return singleWordSearchItems;
    }

    public void setSingleWordSearchItems(final List<SingleWordSearchItem<RESULT>> singleWordSearchItems) {
        this.singleWordSearchItems = singleWordSearchItems;
    }

    public boolean isMpnMatch() {
        return mpnMatch;
    }

    public void setMpnMatch(final boolean mpnMatch) {
        this.mpnMatch = mpnMatch;
    }

    public void setProductFamilyCategoryCode(String productFamilyCategoryCode) {
        this.productFamilyCategoryCode = productFamilyCategoryCode;
    }

    public void setFiltersRemovedGeneralSearch(boolean filtersRemovedGeneralSearch) {
        this.filtersRemovedGeneralSearch = filtersRemovedGeneralSearch;
    }
}
