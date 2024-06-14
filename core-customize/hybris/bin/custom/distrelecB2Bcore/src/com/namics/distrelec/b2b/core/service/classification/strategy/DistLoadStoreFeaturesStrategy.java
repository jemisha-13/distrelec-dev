/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.classification.strategy;

import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.strategy.LoadStoreFeaturesStrategy;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.util.List;
import java.util.Set;

/**
 * {@code DistLoadStoreFeaturesStrategy}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.6
 */
public interface DistLoadStoreFeaturesStrategy extends LoadStoreFeaturesStrategy {

    void replaceProductFeatures(ProductModel product, List<Feature> features, LanguageModel language,
        Set<ProductFeatureModel> productFeaturesToRemove);
}
