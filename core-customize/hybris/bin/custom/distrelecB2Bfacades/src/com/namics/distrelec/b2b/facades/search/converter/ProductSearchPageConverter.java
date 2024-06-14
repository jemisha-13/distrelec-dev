package com.namics.distrelec.b2b.facades.search.converter;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.reevoo.service.DistReevooService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.hybris.ffsearch.data.campaign.advisor.AdvisorCampaignData;
import com.namics.hybris.ffsearch.data.campaign.feedback.FeedbackCampaignData;
import com.namics.hybris.ffsearch.data.facet.FactFinderFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderLazyFacetData;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import com.namics.hybris.ffsearch.data.facet.SingleWordSearchItem;
import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class ProductSearchPageConverter<QUERY, STATE, RESULT, ITEM extends ProductData>
        extends
        AbstractPopulatingConverter<FactFinderProductSearchPageData<QUERY, RESULT>, FactFinderProductSearchPageData<STATE, ITEM>> {

    private static final Logger LOG = LogManager.getLogger(ProductSearchPageConverter.class);

    @Autowired
    private Converter<QUERY, STATE> searchStateConverter;

    @Autowired
    private Converter<FilterBadgeData<QUERY>, FilterBadgeData<STATE>> filterBadgeConverter;

    @Autowired
    private Converter<FactFinderFacetData<QUERY>, FactFinderFacetData<STATE>> facetConverter;

    @Autowired
    private Converter<FactFinderLazyFacetData<QUERY>, FactFinderLazyFacetData<STATE>> lazyFacetConverter;

    @Autowired
    private Converter<RESULT, ITEM> searchResultProductConverter;

    @Autowired
    private Converter<RESULT, ITEM> outletSearchResultProductConverter;

    @Autowired
    private Converter<RESULT, ITEM> newSearchResultProductConverter;

    @Autowired
    private Populator<FactFinderProductSearchPageData<STATE, ITEM>, List<RESULT>> stateResultPopulator;

    @Autowired
    private Converter<FeedbackCampaignData<RESULT>, FeedbackCampaignData<ITEM>> feedbackCampaignConverter;

    @Autowired
    private Converter<AdvisorCampaignData<QUERY>, AdvisorCampaignData<STATE>> advisorCampaignConverter;

    @Autowired
    private Converter<SingleWordSearchItem<RESULT>, SingleWordSearchItem<ITEM>> singleWordSearchProductConverter;

    @Autowired
    private DistProductService productService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private DistReevooService distReevooService;

    @Override
    public void populate(final FactFinderProductSearchPageData<QUERY, RESULT> source, final FactFinderProductSearchPageData<STATE, ITEM> target) {
        target.setFreeTextSearch(source.getFreeTextSearch());
        target.setCode(source.getCode());
        target.setResultStatus(source.getResultStatus());
        target.setResultArticleNumberStatus(source.getResultArticleNumberStatus());
        target.setTechnicalView(source.isTechnicalView());
        target.setSearchType(source.getSearchType());
        target.setMpnMatch(source.isMpnMatch());
        target.setSessionId(source.getSessionId());
        target.setFiltersRemovedGeneralSearch(source.isFiltersRemovedGeneralSearch());

        final FactFinderPaginationData<STATE> paginationData = (FactFinderPaginationData) source.getPagination();
        paginationData.setNextPageNr(paginationData.getCurrentPage() + (paginationData.getCurrentPage() < paginationData.getNumberOfPages() ? 1 : 0));
        paginationData.setPrevPageNr(paginationData.getCurrentPage() - (paginationData.getCurrentPage() > 1 ? 1 : 0));
        target.setPagination(paginationData);

        target.setSorting(source.getSorting());

        if (CollectionUtils.isNotEmpty(source.getFilters())) {
            target.setFilters(Converters.convertAll(source.getFilters(), filterBadgeConverter));
        }

        target.setCurrentQuery(searchStateConverter.convert(source.getCurrentQuery()));

        if (source.getCategories() != null) {
            target.setCategories(facetConverter.convert(source.getCategories()));
        }

        if (CollectionUtils.isNotEmpty(source.getOtherFacets())) {
            target.setOtherFacets(Converters.convertAll(source.getOtherFacets(), facetConverter));
        }

        if (CollectionUtils.isNotEmpty(source.getLazyFacets())) {
            target.setLazyFacets(Converters.convertAll(source.getLazyFacets(), lazyFacetConverter));
        }

        if (CollectionUtils.isNotEmpty(source.getResults())) {
            stateResultPopulator.populate(target, source.getResults());
            target.setResults(Converters.convertAll(source.getResults(), getSearchResultProductConverter(source.getSearchType())));
            target.setCurrencyIso(cmsSiteService.getCurrentSite() != null
                                  && cmsSiteService.getCurrentSite().getDefaultCurrency() != null ? cmsSiteService.getCurrentSite().getDefaultCurrency()
                                                                                                                  .getIsocode() : null);
        }

        if (CollectionUtils.isNotEmpty(source.getSingleWordSearchItems())) {
            target.setSingleWordSearchItems(Converters.convertAll(source.getSingleWordSearchItems(), singleWordSearchProductConverter));
        }

        if (CollectionUtils.isNotEmpty(source.getAdvisorCampaigns())) {
            target.setAdvisorCampaigns(Converters.convertAll(source.getAdvisorCampaigns(), advisorCampaignConverter));
        }

        target.setKeywordRedirectUrl(source.getKeywordRedirectUrl());

        if (CollectionUtils.isNotEmpty(source.getFeedbackCampaigns())) {
            target.setFeedbackCampaigns(Converters.convertAll(source.getFeedbackCampaigns(), feedbackCampaignConverter));
        }

        CMSSiteModel site = cmsSiteService.getCurrentSite();
        if (site.isReevooActivated()) {
            final List<ProductData> results = (List<ProductData>) target.getResults();
            if (CollectionUtils.isNotEmpty(results)) {
                for (final ProductData productData : results) {
                    try {
                        productData.setEligibleForReevoo(distReevooService.isProductEligibleForReevoo(productService.getProductForCode(productData.getCode())));
                    } catch (UnknownIdentifierException ex) {
                        LOG.warn("No Product Found ::");
                    }
                }
            }
        }

        super.populate(source, target);
    }

    @Override
    protected FactFinderProductSearchPageData<STATE, ITEM> createTarget() {
        return new FactFinderProductSearchPageData<>();
    }

    protected Converter<RESULT, ITEM> getSearchResultProductConverter(final DistSearchType searchType) {
        if (DistSearchType.OUTLET.equals(searchType)) {
            // the product url has to be extended for outlet searches
            return outletSearchResultProductConverter;
        }
        if (DistSearchType.NEW.equals(searchType)) {
            return newSearchResultProductConverter;
        }
        return searchResultProductConverter;
    }
}
