/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import static java.util.Comparator.comparing;

import java.util.List;

import com.namics.distrelec.b2b.facades.product.DistPromotionLabelsFacade;
import com.namics.distrelec.b2b.facades.product.data.DistPromotionLabelData;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

public class ProductPromotionLabelsPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {

    private DistPromotionLabelsFacade promotionLabelsFacade;

    @Override
    public void populate(final SOURCE source, final TARGET target) {
        final List<DistPromotionLabelData> promoLabels = getPromotionLabelsFacade().getActivePromotionLabelsForProductCode(source.getCode());
        promoLabels.sort(comparing(DistPromotionLabelData::getPriority));
        target.setActivePromotionLabels(promoLabels);
    }

    public DistPromotionLabelsFacade getPromotionLabelsFacade() {
        return promotionLabelsFacade;
    }

    public void setPromotionLabelsFacade(final DistPromotionLabelsFacade promotionLabelsFacade) {
        this.promotionLabelsFacade = promotionLabelsFacade;
    }

}
