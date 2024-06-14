/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.converters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.hybris.platform.commercefacades.order.converters.populator.CardTypePopulator;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DistCardTypePopulator extends CardTypePopulator {

    @Autowired
    @Qualifier("imageConverter")
    private Converter<MediaModel, ImageData> imageConverter;

    protected CardTypeData createTarget() {
        return new CardTypeData();
    }

    @Override
    public void populate(final CreditCardType source, final CardTypeData target) {
        super.populate(source, target);
        if (getTypeService().getEnumerationValue(source).getIcon() != null) {
            target.setIcon(imageConverter.convert(getTypeService().getEnumerationValue(source).getIcon()));
        }
    }

}
