/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.product.DistProductService;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Populator for similar products.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public class SimilarProductPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends AbstractProductPopulator<SOURCE, TARGET> {

    private static final Logger LOG = LogManager.getLogger(SimilarProductPopulator.class);

    private Converter<ProductModel, ProductData> productReferenceTargetConverter;

    private DistProductService distProductService;

    private int similarProductsLimit;

    @Override
    public void populate(final SOURCE source, final TARGET target) {
        Map<String, List<ProductReferenceData>> productReferences = target.getProductReferencesMap();
        if (productReferences == null) {
            productReferences = new HashMap<>();
            target.setProductReferencesMap(productReferences);
        }

        final List<ProductModel> similarProducts = getDistProductService().getSimilarProducts(source);
        getDistProductService().removeNonBuyableProducts(similarProducts);
        if (CollectionUtils.isNotEmpty(similarProducts)) {
            productReferences.put(ProductReferenceTypeEnum.DIST_SIMILAR.getCode().toLowerCase(), convertSimilarProducts(similarProducts));
        }
    }

    private List<ProductReferenceData> convertSimilarProducts(final Collection<ProductModel> similarProducts) {
        final List<ProductReferenceData> productReferencesSimilarProducts = new ArrayList<>();

        int count = 0;
        for (final ProductModel similarProduct : similarProducts) {
            final ProductReferenceData productReferenceData = new ProductReferenceData();
            productReferenceData.setTarget(getProductReferenceTargetConverter().convert(similarProduct));
            productReferencesSimilarProducts.add(productReferenceData);

            count++;
            if (count >= similarProductsLimit) {
                LOG.info("Limit similar products to {}", Integer.valueOf(similarProductsLimit));
                break;
            }
        }

        return productReferencesSimilarProducts;
    }

    public Converter<ProductModel, ProductData> getProductReferenceTargetConverter() {
        return productReferenceTargetConverter;
    }

    @Required
    public void setProductReferenceTargetConverter(final Converter<ProductModel, ProductData> productReferenceTargetConverter) {
        this.productReferenceTargetConverter = productReferenceTargetConverter;
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    @Required
    public void setDistProductService(final DistProductService distProductService) {
        this.distProductService = distProductService;
    }

    public int getSimilarProductsLimit() {
        return similarProductsLimit;
    }

    public void setSimilarProductsLimit(final int similarProductsLimit) {
        this.similarProductsLimit = similarProductsLimit;
    }

}
