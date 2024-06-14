/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.converters;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

public class DistBaseSiteConverter extends AbstractPopulatingConverter<BaseSiteModel, BaseSiteData> {

    @Override
    public void populate(final BaseSiteModel source, final BaseSiteData target) {
        target.setName(source.getName());
    }

}
