package com.namics.distrelec.cms.populators;

import com.namics.distrelec.b2b.core.config.DistrelecSiteConfigService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cmsfacades.data.SiteData;
import de.hybris.platform.cmsfacades.sites.populator.model.SiteModelPopulator;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class DistrelecSiteModelPopulator extends SiteModelPopulator {

    private static final Logger LOG = LoggerFactory.getLogger(DistrelecSiteModelPopulator.class);

    private final SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    public DistrelecSiteModelPopulator(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
        this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
    }

    @Override
    public void populate(CMSSiteModel source, SiteData target) throws ConversionException {
        super.populate(source, target);

        try {
            URI uri = URI.create(target.getPreviewUrl());
            if(!uri.isAbsolute()){
                String siteBaseUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(source, true, "/");
                String previewUrl = siteBaseUrl + (target.getPreviewUrl().startsWith("/") ? target.getPreviewUrl().substring(1) : target.getPreviewUrl());
                target.setPreviewUrl(previewUrl);
            }else{
                LOG.debug("Skipping modification of preview URL {} for site {} because it is absolute", target.getPreviewUrl(), target.getUid());
            }
        } catch (IllegalArgumentException e) {
            LOG.error("Malformed preview URL {} found for site {}", target.getPreviewUrl(), target.getUid());
        }
    }
}
