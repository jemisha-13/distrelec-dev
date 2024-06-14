/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.impl;

import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.core.service.product.DistPromotionLabelsService;
import com.namics.distrelec.b2b.facades.product.DistPromotionLabelsFacade;
import com.namics.distrelec.b2b.facades.product.data.DistPromotionLabelData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Default implementation of {@link DistPromotionLabelsFacade}
 *
 * @author sivakumaran, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public class DefaultDistPromotionLabelsFacade implements DistPromotionLabelsFacade {

    @Autowired
    private ProductService productService;

    @Autowired
    private DistPromotionLabelsService distPromotionLabelsService;

    @Autowired
    @Qualifier("promotionLabelConverter")
    private Converter<DistPromotionLabelModel, DistPromotionLabelData> promotionLabelConverter;

    @Override
    public List<DistPromotionLabelData> getActivePromotionLabelsForProductCode(final String code) {
        final List<DistPromotionLabelModel> promotionLabels = getDistPromotionLabelsService()
                .getActivePromotionLabelsForProduct(getProductService().getProductForCode(code));
        return Converters.convertAll(promotionLabels, getPromotionLabelConverter());
    }

    public DistPromotionLabelsService getDistPromotionLabelsService() {
        return distPromotionLabelsService;
    }

    public void setDistPromotionLabelsService(final DistPromotionLabelsService distProductPromotionLabelsService) {
        this.distPromotionLabelsService = distProductPromotionLabelsService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public Converter<DistPromotionLabelModel, DistPromotionLabelData> getPromotionLabelConverter() {
        return promotionLabelConverter;
    }

    public void setPromotionLabelConverter(final Converter<DistPromotionLabelModel, DistPromotionLabelData> promotionLabelConverter) {
        this.promotionLabelConverter = promotionLabelConverter;
    }

}
