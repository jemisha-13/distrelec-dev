/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants.Product;
import com.namics.distrelec.b2b.core.service.classification.DistFeatureValueConversionService;
import com.namics.distrelec.b2b.facades.product.data.ProductInformationData;

import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * Populates additional product information on {@link ProductData}.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 *
 * @param <SOURCE>
 * @param <TARGET>
 */
public class ProductInformationPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
                                        extends AbstractProductPopulator<SOURCE, TARGET> {

    @Autowired
    private ClassificationService classificationService;

    @Autowired
    private DistFeatureValueConversionService featureConversionService;

    @Override
    public void populate(final SOURCE source, final TARGET target) {
        final FeatureList featureList = classificationService.getFeatures(source);
        if (featureList != null && !featureList.isEmpty()) {
            // Set the product information
            setProductInformation(source, target, featureList);
        }
    }

    protected void setProductInformation(final SOURCE source, final TARGET target, final FeatureList featureList) {
        final ProductInformationData productInformationData = new ProductInformationData();

        productInformationData.setOrderNote(getValue(featureList, Product.ATTRIBUTE_CODE_ORDER_NOTE));
        productInformationData.setOrderNoteArticle(getValue(featureList, Product.ATTRIBUTE_CODE_ORDER_NOTE_ARTICLE));
        productInformationData.setDeliveryNote(getValue(featureList, Product.ATTRIBUTE_CODE_DELIVERY_NOTE));
        productInformationData
                              .setDeliveryNoteArticle(getValue(featureList, Product.ATTRIBUTE_CODE_DELIVERY_NOTE_ARTICLE));
        productInformationData.setAssemblyNote(getValue(featureList, Product.ATTRIBUTE_CODE_ASSEMBLY_NOTE));
        productInformationData.setUsageNote(getValues(featureList, Product.ATTRIBUTE_CODE_USAGE_NOTE));

        productInformationData.setFamilyDescription(getValues(featureList, Product.ATTRIBUTE_CODE_FAMILY_DESCRIPTION));
        productInformationData
                              .setFamilyDescriptionBullets(getValues(featureList, Product.ATTRIBUTE_CODE_FAMILY_DESCRIPTION_BULLET));

        productInformationData.setSeriesDescription(getValues(featureList, Product.ATTRIBUTE_CODE_SERIES_DESCRIPTION));
        productInformationData
                              .setSeriesDescriptionBullets(getValues(featureList, Product.ATTRIBUTE_CODE_SERIES_DESCRIPTION_BULLET));

        productInformationData
                              .setArticleDescription(getValues(featureList, Product.ATTRIBUTE_CODE_ARTICLE_DESCRIPTION));
        productInformationData.setArticleDescriptionBullets(
                                                            getValues(featureList, Product.ATTRIBUTE_CODE_ARTICLE_DESCRIPTION_BULLET));

        productInformationData
                              .setPaperCatalogPageNumber((String) getProductAttribute(source, ProductModel.PAPERCATALOGPAGENUMBER));
        productInformationData.setPaperCatalogPageNumber_16_17(
                                                               (String) getProductAttribute(source, ProductModel.PAPERCATALOGPAGENUMBER_16_17));

        target.setProductInformation(productInformationData);
    }

    protected String getValue(final FeatureList featureList, final String attributeCode) {
        final Feature feature = getFeatureByAttributeCode(featureList, attributeCode);
        if (feature != null && feature.getValue() != null && feature.getValue().getValue() != null) {
            return featureConversionService.toString(feature.getValue());
        } else {
            return null;
        }
    }

    protected List<String> getValues(final FeatureList featureList, final String attributeCode) {
        final List<String> values = new ArrayList<>();

        final Feature feature = getFeatureByAttributeCode(featureList, attributeCode);
        if (feature != null && CollectionUtils.isNotEmpty(feature.getValues())) {
            for (final FeatureValue featureValue : feature.getValues()) {
                if (featureValue != null && featureValue.getValue() != null) {
                    values.add(featureConversionService.toString(featureValue));
                }
            }
        }

        return values;
    }

    protected Feature getFeatureByAttributeCode(final FeatureList featureList, final String attributeCode) {
        for (final Feature feature : featureList.getFeatures()) {
            if (feature.getClassAttributeAssignment() != null
                    && feature.getClassAttributeAssignment().getClassificationAttribute() != null && attributeCode
                                                                                                                  .equals(feature.getClassAttributeAssignment()
                                                                                                                                 .getClassificationAttribute()
                                                                                                                                 .getCode())) {
                return feature;
            }
        }
        return null;
    }

    public ClassificationService getClassificationService() {
        return classificationService;
    }

    public void setClassificationService(final ClassificationService classificationService) {
        this.classificationService = classificationService;
    }
}
