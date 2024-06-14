package com.namics.distrelec.cms.populators;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cmsfacades.data.MediaData;
import de.hybris.platform.cmsfacades.media.populator.BasicMediaPopulator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;

public class DistrelecMediaPopulator extends BasicMediaPopulator {

    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
    private BaseSiteService baseSiteService;

    @Override
    public void populate(final MediaModel source, final MediaData target) throws ConversionException {
        super.populate(source, target);
        BaseSiteModel site = getBaseSiteService().getCurrentBaseSite();
        if(site != null) {
            String mediaDomainUrl = getSiteBaseUrlResolutionService().getMediaUrlForSite(site, true);
            target.setUrl(mediaDomainUrl + source.getURL());
            target.setDownloadUrl(mediaDomainUrl + source.getDownloadURL());
        }

        target.setHeight(source.getHeight());
        target.setWidth(source.getWidth());
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
