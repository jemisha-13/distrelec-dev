/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.converters;

import com.namics.distrelec.b2b.facades.cms.data.DistCarouselItemData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.List;
import java.util.Map;

public abstract class AbstractDistCarouselItemDataConverter<SOURCE> extends AbstractPopulatingConverter<SOURCE, DistCarouselItemData> {

    @Override
    protected DistCarouselItemData createTarget() {
        return new DistCarouselItemData();
    }

}
