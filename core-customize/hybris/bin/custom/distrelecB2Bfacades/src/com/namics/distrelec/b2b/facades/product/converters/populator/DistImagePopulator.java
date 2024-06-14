/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import org.apache.commons.lang.StringUtils;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.converters.populator.ImagePopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.site.BaseSiteService;

/**
 * DistImagePopulator extends ImageConverter.
 *
 * @author lzamofing, Distrelec
 * @since Distrelec 1.0
 *
 */
public class DistImagePopulator extends ImagePopulator {

    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    private BaseSiteService baseSiteService;

    @Override
    public void populate(final MediaModel source, final ImageData target) {
        super.populate(source, target);

        // Add the image name
        target.setName(source.getCode());

        // Add the domain prefic to the URL
        if (StringUtils.isNotEmpty(target.getUrl()) && target.getUrl().startsWith("/")) {
            BaseSiteModel site = getBaseSiteService().getCurrentBaseSite();
            if (site != null) {
                String mediaDomainUrl = getSiteBaseUrlResolutionService().getMediaUrlForSite(site, true);
                target.setUrl(mediaDomainUrl + source.getURL());
            }
        }
    }

    public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService() {
        return siteBaseUrlResolutionService;
    }

    public void setSiteBaseUrlResolutionService(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
        this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }
}
