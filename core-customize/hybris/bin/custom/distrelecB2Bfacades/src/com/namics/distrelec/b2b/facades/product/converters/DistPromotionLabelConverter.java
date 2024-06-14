/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.facades.product.data.DistPromotionLabelData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

import java.util.Locale;

public class DistPromotionLabelConverter extends AbstractPopulatingConverter<DistPromotionLabelModel, DistPromotionLabelData> {

    @Override
    protected DistPromotionLabelData createTarget() {
        return new DistPromotionLabelData();
    }

    @Override
    public void populate(final DistPromotionLabelModel source, final DistPromotionLabelData target) {

        target.setCode(source.getCode());
        target.setLabel(source.getName());
        target.setNameEN(source.getName(Locale.ENGLISH));
        target.setRank(source.getRank());
        target.setPriority(source.getPriority());
        super.populate(source, target);
    }

}
