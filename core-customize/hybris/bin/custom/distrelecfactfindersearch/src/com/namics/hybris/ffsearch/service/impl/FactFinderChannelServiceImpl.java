/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.impl;

import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.model.DistFactFinderExportChannelModel;
import com.namics.hybris.ffsearch.service.DistFactFinderChannelDao;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * The FactFinder channel service implementation of {@link FactFinderChannelService}. Remember: The link to the search channel in the
 * FactFinder instance is the field {@link DistFactFinderExportChannelModel#getChannel()}
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class FactFinderChannelServiceImpl implements FactFinderChannelService {

    private static final Logger LOG = LoggerFactory.getLogger(FactFinderChannelServiceImpl.class);

    private DistFactFinderChannelDao channelDao;
    private CMSSiteService cmsSiteService;
    private FlexibleSearchService flexibleSearchService;
    private DistCommerceCommonI18NService distCommerceCommonI18NService;

    @Override
    public String getCurrentFactFinderChannel() {
        final DistFactFinderExportChannelModel channel = getChannelDao().getChannel(getCmsSite(), getLanguage());
        return channel == null ? StringUtils.EMPTY : channel.getChannel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.hybris.ffsearch.service.FactFinderChannelService#getCurrentFactFinderChannelCode()
     */
    @Override
    public String getCurrentFactFinderChannelCode() {
        final DistFactFinderExportChannelModel channel = getChannelDao().getChannel(getCmsSite(), getLanguage());
        return channel == null ? StringUtils.EMPTY : channel.getCode();
    }

    @Override
    public DistFactFinderExportChannelModel getChannelForCode(final String code) {
        final DistFactFinderExportChannelModel example = new DistFactFinderExportChannelModel();
        example.setCode(code);
        return flexibleSearchService.getModelByExample(example);
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getSwissGermanChannel() {
        final DistFactFinderExportChannelModel channel = new DistFactFinderExportChannelModel();
        channel.setCode("distrelec_7310_ch_de");
        final DistFactFinderExportChannelModel swissChannel = flexibleSearchService.getModelByExample(channel);
        if (swissChannel != null) {
            return swissChannel.getChannel();
        } else {
            return "";
        }
    }

    private CMSSiteModel getCmsSite() {
        final CMSSiteModel site = getCmsSiteService().getCurrentSite();
        if (site == null) {
            LOG.error("There is currently no CMS site defined");
        }
        return site;
    }

    private LanguageModel getLanguage() {
        LanguageModel language = getDistCommerceCommonI18NService().getCurrentLanguage();

        language = getDistCommerceCommonI18NService().getBaseLanguage(language);

        if (language == null) {
            LOG.error("There is currently no language defined");
        }
        return language;
    }

    // // BEGIN GENERATED CODE

    protected CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    @Required
    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    protected DistFactFinderChannelDao getChannelDao() {
        return channelDao;
    }

    @Required
    public void setChannelDao(final DistFactFinderChannelDao channelDao) {
        this.channelDao = channelDao;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DistCommerceCommonI18NService getDistCommerceCommonI18NService() {
        return distCommerceCommonI18NService;
    }

    public void setDistCommerceCommonI18NService(final DistCommerceCommonI18NService distCommerceCommonI18NService) {
        this.distCommerceCommonI18NService = distCommerceCommonI18NService;
    }
}
