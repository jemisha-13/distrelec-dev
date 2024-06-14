/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.converters;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.namics.distrelec.b2b.facades.cms.data.DistCarouselItemData;
import com.namics.distrelec.b2b.facades.util.WebpMediaUtil;

import de.hybris.platform.commercefacades.product.data.ProductData;

public class DistCarouselProductDataConverter extends AbstractDistCarouselItemDataConverter<ProductData> {

    @Override
    public void populate(final ProductData source, final DistCarouselItemData target) {
        target.setProductCode(source.getCode());
        target.setName(source.getName());
        target.setPrice(source.getPrice());
        target.setPromotionText(source.getPromotionText());
        target.setPicture(WebpMediaUtil.getImageDataFromListByType(LANDSCAPE_LARGE_WEBP, LANDSCAPE_LARGE, source.getProductImages()));
        target.setThumbnail(WebpMediaUtil.getImageDataFromListByType(LANDSCAPE_SMALL_WEBP, LANDSCAPE_SMALL, source.getProductImages()));
        target.setUrl(source.getUrl());
        if (CollectionUtils.isNotEmpty(source.getActivePromotionLabels())) {
            target.setActivePromotion(source.getActivePromotionLabels().iterator().next());
        }
        if (source.getDistManufacturer() != null && MapUtils.isNotEmpty(source.getDistManufacturer().getImage())) {
            target.setManufacturer(source.getDistManufacturer().getName());
            target.setManufacturerImage(WebpMediaUtil.getImageDataFromMapByType(BRAND_LOGO_WEBP, BRAND_LOGO, source.getDistManufacturer().getImage()));
        }
        target.setCategories(source.getCategories());
        target.setCodeErpRelevant(source.getCodeErpRelevant());
        target.setTypeName(source.getTypeName());

        super.populate(source, target);
    }

}
