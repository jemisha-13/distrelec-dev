/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.manufacturer.converters;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;

/**
 * Custom Converter for DistManufacturerModel.
 *
 * @author pbueschi, Namics AG
 *
 */
public class DistManufacturerConverter extends AbstractPopulatingConverter<DistManufacturerModel, DistManufacturerData> {

    @Autowired
    @Qualifier("mediaContainerToImageMapConverter")
    private Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter;

    @Autowired
    @Qualifier("distManufacturerModelUrlResolver")
    private DistUrlResolver<DistManufacturerModel> distManufacturerModelUrlResolver;

    @Override
    protected DistManufacturerData createTarget() {
        return new DistManufacturerData();
    }

    @Override
    public void populate(final DistManufacturerModel source, final DistManufacturerData target) {

        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setNameSeo(source.getNameSeo());
        // target.setWebshopUrl(DistUtils.getManufacturerCanonicalUrl(target.getCode(), target.getName()));

        if (source.getPrimaryImage() != null) {
            target.setImage(mediaContainerToImageMapConverter.convert(source.getPrimaryImage()));
        }

        target.setProductGroups(source.getProductCategories());
        target.setPromotionText(source.getPromotionText());
        target.setUrlId(getDistManufacturerModelUrlResolver().resolve(source));

        target.setEmailAddresses(source.getRelevantSupportEmails());
        target.setPhoneNumbers(source.getRelevantSupportPhoneNumbers());
        target.setRepairsText(source.getRelevantSupportRepairInfo());
        target.setUrl(source.getRelevantManufacturerUrl());
        target.setWebsites(source.getRelevantSupportUrls());
        target.setWebDescription(source.getWebDescription());
        target.setSeoMetaDescription(source.getSeoMetaDescription());
        target.setSeoMetaTitle(source.getSeoMetaTitle());

        super.populate(source, target);
    }

    public Converter<MediaContainerModel, Map<String, ImageData>> getMediaContainerToImageMapConverter() {
        return mediaContainerToImageMapConverter;
    }

    public void setMediaContainerToImageMapConverter(final Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter) {
        this.mediaContainerToImageMapConverter = mediaContainerToImageMapConverter;
    }

    public DistUrlResolver<DistManufacturerModel> getDistManufacturerModelUrlResolver() {
        return distManufacturerModelUrlResolver;
    }

    public void setDistManufacturerModelUrlResolver(final DistUrlResolver<DistManufacturerModel> distManufacturerModelUrlResolver) {
        this.distManufacturerModelUrlResolver = distManufacturerModelUrlResolver;
    }

}
