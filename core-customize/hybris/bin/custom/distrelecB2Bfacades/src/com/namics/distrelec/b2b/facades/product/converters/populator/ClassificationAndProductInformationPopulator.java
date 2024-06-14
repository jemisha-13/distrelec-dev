/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import org.springframework.beans.factory.annotation.Required;

import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * {@code ClassificationAndProductInformationPopulator}
 * <p>
 * This populator class aims to combine the tow populators
 * {@link de.hybris.platform.commercefacades.product.converters.populator.ProductClassificationPopulator} and
 * {@link com.namics.distrelec.b2b.facades.product.converters.populator.ProductInformationPopulator}. The idea to avoid fetching the product
 * features twice.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.7
 */
public class ClassificationAndProductInformationPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
                                                         extends ProductInformationPopulator<SOURCE, TARGET> {

    private Populator<FeatureList, ProductData> productFeatureListPopulator;

    /*
     * (non-Javadoc)
     * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        final FeatureList featureList = getClassificationService().getFeatures(source);

        if (featureList != null && !featureList.getFeatures().isEmpty()) {
            // Populate first the classifications
            getProductFeatureListPopulator().populate(featureList, target);

            // Populate the product informations
            setProductInformation(source, target, featureList);
        }
    }

    public Populator<FeatureList, ProductData> getProductFeatureListPopulator() {
        return productFeatureListPopulator;
    }

    @Required
    public void setProductFeatureListPopulator(final Populator<FeatureList, ProductData> productFeatureListPopulator) {
        this.productFeatureListPopulator = productFeatureListPopulator;
    }
}
