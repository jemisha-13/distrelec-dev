/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2011 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.facades.i18n.converter;

import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.RegionModel;

/**
 * Accelerator specific region data converter implementation.
 *
 * @author dathusir, Distrelec
 * @since Distrelec 3.0
 *
 */
public class DistRegionConverter extends AbstractPopulatingConverter<RegionModel, RegionData> {

    @Override
    protected RegionData createTarget() {
        return new RegionData();
    }

    @Override
    public void populate(final RegionModel source, final RegionData target) {
        super.populate(source, target);
        target.setIsocode(source.getIsocode());
        target.setName(source.getName());
        target.setCountryIso(source.getCountry() != null ? source.getCountry().getIsocode() : "");
    }
}
