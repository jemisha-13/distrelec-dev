/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.converters;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.*;

import com.namics.distrelec.b2b.facades.cms.data.DistCarouselItemData;
import com.namics.distrelec.b2b.facades.util.WebpMediaUtil;

import de.hybris.platform.commercefacades.product.data.CategoryData;

public class DistCarouselCategoryDataConverter extends AbstractDistCarouselItemDataConverter<CategoryData> {

    @Override
    public void populate(final CategoryData source, final DistCarouselItemData target) {
        target.setPicture(WebpMediaUtil.getImageDataFromMapByType(LANDSCAPE_LARGE_WEBP, LANDSCAPE_LARGE, source.getImages()));
        target.setName(source.getName());
        target.setPrice(null);
        target.setPromotionText(source.getPromotionText());
        target.setThumbnail(WebpMediaUtil.getImageDataFromMapByType(LANDSCAPE_SMALL_WEBP, LANDSCAPE_SMALL, source.getImages()));
        target.setUrl(source.getUrl());

        super.populate(source, target);
    }

}
