/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashMap;
import java.util.Map;

public class MediaContainerToImageMapConverter extends AbstractPopulatingConverter<MediaContainerModel, Map<String, ImageData>> {

    @Autowired
    @Qualifier("imageConverter")
    private Converter<MediaModel, ImageData> imageConverter;

    @Autowired
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
    @Autowired
    private BaseSiteService baseSiteService;


    @Override
    protected Map<String, ImageData> createTarget() {
        return new HashMap<String, ImageData>();
    }

    @Override
    public void populate(final MediaContainerModel source, final Map<String, ImageData> target) {

        for (final MediaModel media : source.getMedias()) {
            final ImageData image = getImageConverter().convert(media);
            //populateUrlWithDomain(image);
            if (StringUtils.isNotEmpty(image.getFormat())) {
                target.put(image.getFormat(), image);
            }
        }
        super.populate(source, target);
    }

    private void populateUrlWithDomain(ImageData image) {
        BaseSiteModel site = getBaseSiteService().getCurrentBaseSite();
        String mediaDomainUrl = getSiteBaseUrlResolutionService().getMediaUrlForSite(site, true);
        image.setUrl(mediaDomainUrl + image.getUrl());
    }

    public Converter<MediaModel, ImageData> getImageConverter() {
        return imageConverter;
    }

    public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter) {
        this.imageConverter = imageConverter;
    }

    protected SiteBaseUrlResolutionService getSiteBaseUrlResolutionService() {
        return siteBaseUrlResolutionService;
    }

    public void setSiteBaseUrlResolutionService(SiteBaseUrlResolutionService siteBaseUrlResolutionService) {
        this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
    }

    protected BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }
}
