/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.caching.impl;

import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.facades.caching.DistCachingFacade;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.Config;
import org.springframework.beans.factory.annotation.Required;

public class DefaultDistCachingFacade implements DistCachingFacade {
    private static final String SEPARATOR = "_";

    private BaseSiteService baseSiteService;
    private CMSSiteService cmsSiteService;
    private DistrelecBaseStoreService baseStoreService;
    private CMSPageService cmsPageService;
    private DistSalesOrgService distSalesOrgService;
    private CommonI18NService commonI18NService;
    private ModelService modelService;
    private UserService userService;

    @Override
    public String getCachingKeyMainnav() {
        String userid = "";
        String country = "";
        String language = "";

        // username / id
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            userid = getUserService().getCurrentUser().getUid();
        } else {
            final B2BCustomerModel customer = (B2BCustomerModel) getModelService().get(getUserService().getCurrentUser().getPk());
            userid = customer.getDefaultB2BUnit().getUid();
        }

        country = getCmsSiteService().getCurrentSite().getCountry().getIsocode();
        language = getCommonI18NService().getCurrentLanguage().getIsocode();

        return new StringBuilder().append(country).append(SEPARATOR).append(language).append(SEPARATOR).append(userid).toString();
    }

    @Override
    public String getCachingKeyFooter() {
        String country = "";
        String language = "";

        country = getCmsSiteService().getCurrentSite().getCountry().getIsocode();
        language = getCommonI18NService().getCurrentLanguage().getIsocode();

        return new StringBuilder().append(country).append(SEPARATOR).append(language).toString();
    }

    @Override
    public String getCachingKeyHomepage() {
        String userid = "";
        String country = "";
        String language = "";
        String channel = "";
        String lastModified = "";

        // username / id
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            userid = getUserService().getCurrentUser().getUid();
        } else {
            final B2BCustomerModel customer = (B2BCustomerModel) getModelService().get(getUserService().getCurrentUser().getPk());
            userid = customer.getDefaultB2BUnit().getUid();
        }

        country = getCmsSiteService().getCurrentSite().getCountry().getIsocode();
        language = getCommonI18NService().getCurrentLanguage().getIsocode();
        channel = getBaseStoreService().getCurrentChannel(getBaseSiteService().getCurrentBaseSite()).getCode();

        // last modified time of homepage
        final ContentPageModel homePage = getCmsPageService().getHomepage();
        if (homePage != null) {
            lastModified = String.valueOf(homePage.getModifiedtime().getTime());
        }

        return new StringBuilder().append(country).append(SEPARATOR).append(language).append(SEPARATOR).append(channel).append(SEPARATOR).append(userid)
                .append(SEPARATOR).append(lastModified).toString();
    }

    @Override
    public int getCachingTimeFooter() {
        return Config.getInt("cache.footer.time", 0);
    }

    @Override
    public int getCachingTimeHomepage() {
        return Config.getInt("cache.homepage.time", 0);
    }

    @Override
    public int getCachingTimeMainnav() {
        return Config.getInt("cache.mainnav.time", 0);
    }

    @Override
    public int getCachingTimeCategoryLink() {
        return Config.getInt("cache.categorylink.time", 0);
    }

    // START GENERATED CODE

    public final BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    @Required
    public final void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public final DistrelecBaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    @Required
    public final void setBaseStoreService(final DistrelecBaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public final CMSPageService getCmsPageService() {
        return cmsPageService;
    }

    @Required
    public final void setCmsPageService(final CMSPageService cmsPageService) {
        this.cmsPageService = cmsPageService;
    }

    public final DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    @Required
    public final void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public final CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public final void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    public final ModelService getModelService() {
        return modelService;
    }

    @Required
    public final void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public final UserService getUserService() {
        return userService;
    }

    @Required
    public final void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    // END GENERATED CODE
}
