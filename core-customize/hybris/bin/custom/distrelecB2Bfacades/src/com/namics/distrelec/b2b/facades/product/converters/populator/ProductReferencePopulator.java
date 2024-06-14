/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.product.DistProductService;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Populates a product reference to its data object representation.
 *
 * @author rmeier, Namics AG
 * @since Distrelec 1.0
 *
 * @param <SOURCE>
 *            extends ProductModel
 * @param <TARGET>
 *            extends ProductData
 */
public class ProductReferencePopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {

    private ProductReferenceTypeEnum productReferenceType;

    private Boolean activeOnly;

    @Autowired
    private ProductReferenceService productReferenceService;

    @Autowired
    private DistProductService distProductService;

    private Converter<ProductReferenceModel, ProductReferenceData> productReferenceConverter;

    @Override
    public void populate(final SOURCE source, final TARGET target) {
        Map<String, List<ProductReferenceData>> allProductReferences = target.getProductReferencesMap();
        if (allProductReferences == null) {
            allProductReferences = new HashMap<>();
        }

        String key = "all";
        if (getProductReferenceType() != null) {
            key = StringUtils.lowerCase(getProductReferenceType().getCode());
        } else if (Boolean.TRUE.equals(activeOnly)) {
            key = "active";
        }

        final List<ProductReferenceModel> productReferences = new ArrayList(
                                                                            getProductReferenceService().getProductReferencesForSourceProduct(source,
                                                                                                                                              getProductReferenceType(),
                                                                                                                                              getActiveOnly().booleanValue()));

        CollectionUtils.filter(productReferences, new Predicate() {

            @Override
            public boolean evaluate(final Object paramObject) {
                return getDistProductService().isProductBuyable(((ProductReferenceModel) paramObject).getTarget());
            }
        });

        allProductReferences.put(key, Converters.convertAll(productReferences, getProductReferenceConverter()));
        target.setProductReferencesMap(allProductReferences);
    }

    public ProductReferenceTypeEnum getProductReferenceType() {
        return productReferenceType;
    }

    public void setProductReferenceType(final ProductReferenceTypeEnum productReferenceType) {
        this.productReferenceType = productReferenceType;
    }

    public Boolean getActiveOnly() {
        return activeOnly;
    }

    public void setActiveOnly(final Boolean activeOnly) {
        this.activeOnly = activeOnly;
    }

    public ProductReferenceService getProductReferenceService() {
        return productReferenceService;
    }

    public void setProductReferenceService(final ProductReferenceService productReferenceService) {
        this.productReferenceService = productReferenceService;
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    public void setDistProductService(final DistProductService distProductService) {
        this.distProductService = distProductService;
    }

    public Converter<ProductReferenceModel, ProductReferenceData> getProductReferenceConverter() {
        return productReferenceConverter;
    }

    public void setProductReferenceConverter(final Converter<ProductReferenceModel, ProductReferenceData> productReferenceConverter) {
        this.productReferenceConverter = productReferenceConverter;
    }

}
