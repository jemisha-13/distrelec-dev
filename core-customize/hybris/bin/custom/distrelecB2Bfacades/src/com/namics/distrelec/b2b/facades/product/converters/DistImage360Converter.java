/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.facades.product.data.DistImage360Data;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.Set;

public class DistImage360Converter extends AbstractPopulatingConverter<DistImage360Model, DistImage360Data> {

    @Autowired
    @Qualifier("mediaContainerToImageMapConverter")
    private Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter;

    @Override
    protected DistImage360Data createTarget() {
        return new DistImage360Data();
    }

    @Override
    public void populate(final DistImage360Model source, final DistImage360Data target) {
        target.setCols(source.getColumns());
        target.setRows(source.getRows());
        target.setPattern(source.getPattern());

        final Set<MediaContainerModel> mediaContainers = source.getMediaContainers();
        if (CollectionUtils.isNotEmpty(mediaContainers)) {
            final MediaContainerModel firstMediaContainer = mediaContainers.iterator().next();
            final Map<String, ImageData> imageMap = mediaContainerToImageMapConverter.convert(firstMediaContainer);

            target.setFirstImageSmallPath(getImageUrl(imageMap, DistConstants.MediaFormat.IMAGE360_SMALL));
            target.setFirstImageMediumPath(getImageUrl(imageMap, DistConstants.MediaFormat.IMAGE360_MEDIUM));
            target.setFirstImageLargePath(getImageUrl(imageMap, DistConstants.MediaFormat.IMAGE360_LARGE));
        }

    }

    private String getImageUrl(final Map<String, ImageData> imageMap, final String mediaFormat) {
        final ImageData imageData = imageMap.get(mediaFormat);
        if (imageData == null) {
            return null;
        } else {
            return imageData.getUrl();
        }
    }
}
