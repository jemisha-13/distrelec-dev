/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.converters;

import com.namics.distrelec.b2b.facades.cms.data.DistCarouselItemData;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.util.WebpMediaUtil;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_LARGE;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_LARGE_WEBP;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_SMALL;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_SMALL_WEBP;

public class DistCarouselManufacturerDataConverter extends AbstractDistCarouselItemDataConverter<DistManufacturerData> {

    @Override
    public void populate(final DistManufacturerData source, final DistCarouselItemData target) {
        target.setPicture(WebpMediaUtil.getImageDataFromMapByType(LANDSCAPE_LARGE_WEBP, LANDSCAPE_LARGE, source.getImage()));
        target.setName(source.getName());
        target.setPrice(null);
        target.setPromotionText(source.getPromotionText());
        target.setThumbnail(WebpMediaUtil.getImageDataFromMapByType(LANDSCAPE_SMALL_WEBP, LANDSCAPE_SMALL, source.getImage()));
        target.setUrl(source.getUrl());

        super.populate(source, target);
    }

}
