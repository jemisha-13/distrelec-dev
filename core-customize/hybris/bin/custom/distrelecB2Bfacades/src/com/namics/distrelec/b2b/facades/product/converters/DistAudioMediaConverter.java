package com.namics.distrelec.b2b.facades.product.converters;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistAudioMediaModel;
import com.namics.distrelec.b2b.facades.product.data.DistAudioData;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.site.BaseSiteService;

public class DistAudioMediaConverter extends AbstractPopulatingConverter<DistAudioMediaModel, DistAudioData> {

    @Autowired
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Override
    public void populate(DistAudioMediaModel source, DistAudioData target) {
        String baseUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSiteService.getCurrentBaseSite(), true, "");

        target.setAudioUrl(baseUrl + source.getURL());
        target.setMimeType(source.getMime());
    }
}
