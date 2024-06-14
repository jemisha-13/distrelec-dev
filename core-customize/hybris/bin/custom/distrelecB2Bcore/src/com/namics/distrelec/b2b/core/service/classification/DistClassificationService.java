/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.classification;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeature;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * {@code DistClassificationService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.12
 */
public interface DistClassificationService extends ClassificationService {

    String FACET_GROUP_NAME = "facetGroupName";
    String FACET_VALUE_NAME = "facetValueName";

    public ClassificationAttributeModel findClassificationAttribute(final String code);

    public Map<String, String> getClassificationValueTranslation(String classAttrName, String classAttrValueName);

    public void updateClassificationAttributeValue(ClassificationAttributeModel classificationAttributeModel, String value,
        Locale currentLocale, String enValue);

    void saveLangIndependentProductFeatures(ProductModel product, List<CProductFeature> productFeatures);

    void saveLangDependentProductFeatures(ProductModel product, List<CProductFeature> productFeatures, Locale currentLocale);
}
