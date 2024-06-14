/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.request;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Required;

import com.google.common.base.Optional;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchRequest;
import com.namics.hybris.ffsearch.populator.common.CustomParameterHelper;
import com.namics.hybris.ffsearch.populator.common.PriceFilterTranslator;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;

import de.factfinder.webservice.ws71.FFsearch.AdvisorCampaignStatusHolder;
import de.factfinder.webservice.ws71.FFsearch.CustomParameter;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SearchQueryPageableData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Populator which builds the search request query and channel for the data object representing a search request.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class RequestQueryPopulator implements Populator<SearchQueryPageableData<SearchQueryData>, SearchRequest> {

    private static final String NAVIGATION_PARAMETER_NAME = "navigation";
    private static final String NAVIGATION_PARAMETER_VALUE = "true";
    private static final String CURRENT_EMPLOYEE_FF_CHANNEL = "currentEmployeeFFChannel";

    private CMSSiteService cmsSiteService;
    private BaseStoreService baseStoreService;
    private CommonI18NService commonI18NService;
    private FactFinderChannelService channelService;
    private RequestQueryTypePopulator queryTypePopulator;
    private UserService userService;
    private FlexibleSearchService flexibleSearchService;
    private SessionService sessionService;

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void populate(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        initializeTarget(target);
        populateChannel(target);
        populateQuery(source.getSearchQueryData(), target);
        populatePriceFilter(target);
        populateNavigationParameter(source, target);
        populateAdvisorStatus(source, target);

    }

    private void populateAdvisorStatus(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        if (source.getSearchQueryData().getAdvisorStatus() != null) {
            final AdvisorCampaignStatusHolder advisorStatus = new AdvisorCampaignStatusHolder();
            advisorStatus.setAnswerPath(source.getSearchQueryData().getAdvisorStatus().getAnswerPath());
            advisorStatus.setCampaignId(source.getSearchQueryData().getAdvisorStatus().getCampaignId());
            target.getSearchParams().setAdvisorStatus(advisorStatus);
        }
    }

    private void initializeTarget(final SearchRequest target) {
        if (target.getSearchParams() == null) {
            target.setSearchParams(new Params());
        }
    }

    private void populateChannel(final SearchRequest target) {
        if (userService.getCurrentUser() instanceof EmployeeModel) {
            target.getSearchParams().setChannel((String) getSessionService().getAttribute(CURRENT_EMPLOYEE_FF_CHANNEL));
        } else {
            target.getSearchParams().setChannel(getChannelService().getCurrentFactFinderChannel());
        }
    }

    private void populateQuery(final SearchQueryData query, final SearchRequest target) {
        // Query parameter not necessary for navigation requests
        target.getSearchParams().setQuery(query.getFreeTextSearch());
        getQueryTypePopulator().populate(query, target);
    }

    /**
     * Prices should only be returned for current currency and current Net/Gross value
     */
    private void populatePriceFilter(final SearchRequest target) {
        BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
        CurrencyModel currency = getCmsSiteService().getCurrentSite().getDefaultCurrency();
        if (currency == null) {
            currency = baseStore.getDefaultCurrency() != null ? baseStore.getDefaultCurrency() : getCommonI18NService().getCurrentCurrency();
        }
        if (userService.getCurrentUser() instanceof EmployeeModel) {
            BaseStoreModel baseEx = new BaseStoreModel();
            baseEx.setUid("distrelec_CH_b2b");
            baseStore = flexibleSearchService.getModelByExample(baseEx);

            CurrencyModel currencyEx = new CurrencyModel();
            currencyEx.setIsocode("CHF");
            currency = flexibleSearchService.getModelByExample(currencyEx);
        }
        final Optional<CustomParameter> priceFilter = PriceFilterTranslator.buildPriceFilterValue(currency, baseStore);
        if (priceFilter.isPresent()) {
            CustomParameterHelper.addCustomParameter(target, priceFilter.get());
        }
    }

    private void populateNavigationParameter(final SearchQueryPageableData<SearchQueryData> source, final SearchRequest target) {
        // If category search, use navigation instead of default search ASN configuration
        if (DistSearchType.CATEGORY.equals(source.getSearchQueryData().getSearchType())
                || DistSearchType.CATEGORY_AND_TEXT.equals(source.getSearchQueryData().getSearchType())) {
            CustomParameterHelper.addCustomParameter(target,
                    CustomParameterHelper.createCustomParameter(NAVIGATION_PARAMETER_NAME, Collections.singletonList(NAVIGATION_PARAMETER_VALUE)));
        }
    }

    // BEGIN GENERATED CODE

    protected FactFinderChannelService getChannelService() {
        return channelService;
    }

    @Required
    public void setChannelService(final FactFinderChannelService channelService) {
        this.channelService = channelService;
    }

    protected BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Required
    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public RequestQueryTypePopulator getQueryTypePopulator() {
        return queryTypePopulator;
    }

    @Required
    public void setQueryTypePopulator(final RequestQueryTypePopulator queryTypePopulator) {
        this.queryTypePopulator = queryTypePopulator;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    // END GENERATED CODE

}
