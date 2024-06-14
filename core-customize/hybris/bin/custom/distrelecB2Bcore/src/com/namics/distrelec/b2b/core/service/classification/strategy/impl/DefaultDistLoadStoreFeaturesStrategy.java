/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.classification.strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.namics.distrelec.b2b.core.service.classification.dao.DistProductFeaturesDao;
import com.namics.distrelec.b2b.core.service.classification.features.DistLocalizedFeature;
import com.namics.distrelec.b2b.core.service.classification.features.DistUnlocalizedFeature;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.classification.daos.ProductFeaturesDao;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.classification.strategy.DistLoadStoreFeaturesStrategy;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.strategy.impl.DefaultLoadStoreFeaturesStrategy;
import de.hybris.platform.core.model.product.ProductModel;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

/**
 * {@code DefaultDistLoadStoreFeaturesStrategy}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.9
 */
public class DefaultDistLoadStoreFeaturesStrategy extends DefaultLoadStoreFeaturesStrategy implements DistLoadStoreFeaturesStrategy {

    private static final Logger LOG = Logger.getLogger(DefaultDistLoadStoreFeaturesStrategy.class);

    private CommonI18NService commonI18nService;

    private I18NService i18nService;

    private ModelService modelService;

    private ProductFeaturesDao productFeaturesDao;

    private TransactionTemplate txTemplate;

    @Autowired
    private DistProductFeaturesDao distProductFeaturesDao;

    /**
     * Create a new instance of {@code DefaultDistLoadStoreFeaturesStrategy}
     */
    public DefaultDistLoadStoreFeaturesStrategy() {
        super();
    }

    @Override
    public void replaceProductFeatures(final ProductModel product, final List<Feature> features,
            final LanguageModel language, final Set<ProductFeatureModel> productFeaturesToRemove) {
        Preconditions.checkArgument(!modelService.isNew(product), "ProductModel is not persisted");

        txTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(final TransactionStatus status) {
                modelService.removeAll(productFeaturesToRemove);
                storeFeaturesInTx(product, features);
            }
        });
    }

    @Override
    protected List<ProductFeatureModel> storeFeaturesInTx(final ProductModel product, final List<Feature> features) {
        final List<ProductFeatureModel> correctlyStored = new ArrayList<>();
        for (final Feature feature : features) {
            if (feature instanceof DistUnlocalizedFeature) {
                DistUnlocalizedFeature unlocalizedFeature = (DistUnlocalizedFeature) feature;
                final List<FeatureValue> featureValues = unlocalizedFeature.getValues();
                int featurePosition = unlocalizedFeature.getFeaturePosition();
                if (!featureValues.isEmpty()) {
                    correctlyStored.addAll(writeFeatureValues(product, feature.getClassAttributeAssignment(), featurePosition,
                            feature, featureValues, null));
                }
            } else if (feature instanceof DistLocalizedFeature) {
                DistLocalizedFeature localizedFeature = (DistLocalizedFeature) feature;
                final Map<Locale, List<FeatureValue>> values = localizedFeature.getValuesForAllLocales();
                int featurePosition = localizedFeature.getFeaturePosition();
                for (final Map.Entry<Locale, List<FeatureValue>> entry : values.entrySet()) {
                    correctlyStored.addAll(writeFeatureValues(product, feature.getClassAttributeAssignment(), featurePosition,
                            feature, entry.getValue(), entry.getKey()));
                }
            }
        }

        modelService.saveAll(correctlyStored);

        return correctlyStored;
    }

    private Set<ProductFeatureModel> writeFeatureValues(final ProductModel product,
        final ClassAttributeAssignmentModel assignment, final int featurePosition, final Feature feature,
        final List<FeatureValue> values, final Locale locale) {

        final Set<ProductFeatureModel> toSave = new HashSet<>();

        int valuePosition = getMinValuePosition(product, assignment);
        boolean updateProductFeatures = false;
        for (final FeatureValue featureValue : values) {
            PK productFeaturePk = featureValue.getProductFeaturePk();

            if (productFeaturePk != null) {
                ProductFeatureModel _productFeature = modelService.get(featureValue.getProductFeaturePk());
                if (_productFeature != null && isNotChanged(_productFeature, product, featureValue, featurePosition, valuePosition)) {
                    valuePosition++;
                    continue;
                }
            }
            updateProductFeatures = true;
            break;
        }

        if (updateProductFeatures) {
            valuePosition = getMaxValuePosition(product, assignment);
            for (final FeatureValue featureValue : values) {
                ProductFeatureModel productFeature;
                final PK productFeaturePk = featureValue.getProductFeaturePk();

                if (productFeaturePk == null) {
                    productFeature = createNewProductFeature(product, assignment, feature, locale);
                } else {
                    final ProductFeatureModel _productFeature = modelService.get(featureValue.getProductFeaturePk());
                    if (_productFeature != null && _productFeature.getProduct().getPk().equals(product.getPk())) {
                        // product has not been changed
                        productFeature = _productFeature;
                    } else {
                        // request for changing product, existing product feature must stay and new one must to be created
                        productFeature = createNewProductFeature(product, assignment, feature, locale);
                    }
                }

                productFeature.setValue(featureValue.getValue());
                productFeature.setDescription(featureValue.getDescription());
                productFeature.setUnit(featureValue.getUnit());
                productFeature.setValuePosition(Integer.valueOf(++valuePosition));
                productFeature.setFeaturePosition(Integer.valueOf(featurePosition));

                toSave.add(productFeature);
            }
        }
        return toSave;
    }

    private int getMinValuePosition(final ProductModel product, final ClassAttributeAssignmentModel assignment) {
        final List<Integer> minValuePosition = getDistProductFeaturesDao().getProductFeatureMinValuePosition(product, assignment);
        return minValuePosition.get(0) == null ? 0 : minValuePosition.get(0).intValue();
    }

    private int getMaxValuePosition(final ProductModel product, final ClassAttributeAssignmentModel assignment) {
        final List<Integer> maxValuePosition = productFeaturesDao.getProductFeatureMaxValuePosition(product, assignment);
        return maxValuePosition.get(0) == null ? 0 : maxValuePosition.get(0).intValue();
    }

    private boolean isNotChanged(ProductFeatureModel productFeature, ProductModel product, FeatureValue featureValue,
            int featurePosition, int valuePosition) {
        if (!Objects.equals(productFeature.getProduct().getPk(), product.getPk())) {
            return false;
        }
        if (!Objects.equals(productFeature.getValue(), featureValue.getValue())) {
            return false;
        }
        if (productFeature.getUnit() == null ^ featureValue.getUnit() == null) {
            return false;
        }
        if (productFeature.getUnit() != null && featureValue.getUnit() != null
                && !Objects.equals(productFeature.getUnit().getPk(), featureValue.getUnit().getPk())) {
            return false;
        }
        if (!Objects.equals(productFeature.getFeaturePosition(), featurePosition)) {
            return false;
        }
        if (!Objects.equals(productFeature.getValuePosition(), valuePosition)) {
            return false;
        }
        return true;
    }

    private ProductFeatureModel createNewProductFeature(final ProductModel product,
            final ClassAttributeAssignmentModel assignment, final Feature feature, final Locale locale) {
        ProductFeatureModel productFeature;
        productFeature = modelService.create(ProductFeatureModel.class);
        productFeature.setProduct(product);
        productFeature.setClassificationAttributeAssignment(assignment);
        productFeature.setQualifier(feature.getCode());
        if (locale != null) {
            try {
                final Locale dataLocale = i18nService.getBestMatchingLocale(locale);
                final LanguageModel language = commonI18nService.getLanguage(dataLocale.toString());
                productFeature.setLanguage(language);
            } catch (final UnknownIdentifierException e) {
                LOG.error("Cannot set language for iso code: " + locale.getLanguage());
            }
        }
        return productFeature;
    }

    private Map<ClassAttributeAssignmentModel, List<ProductFeatureModel>> convertFeaturesResult(final List<List<ItemModel>> features) {
        final Map<ClassAttributeAssignmentModel, List<ProductFeatureModel>> result = new LinkedHashMap<>();
        for (final List<ItemModel> row : features) {
            final ProductFeatureModel productFeature = (ProductFeatureModel) row.get(0);
            final ClassAttributeAssignmentModel assignment = (ClassAttributeAssignmentModel) row.get(1);
            List<ProductFeatureModel> productFeatures = result.get(assignment);
            if (productFeatures == null) {
                productFeatures = new ArrayList<>();
                result.put(assignment, productFeatures);
            }
            productFeatures.add(productFeature);
        }
        return result;
    }

    @Override
    protected List<Feature> loadTypedFeatures(final List<ClassAttributeAssignmentModel> assignments, final ProductModel product) {
        final List<Feature> result = new ArrayList<>();

        final Map<ClassAttributeAssignmentModel, List<ProductFeatureModel>> featuresMap = convertFeaturesResult(productFeaturesDao
                .findProductFeaturesByProductAndAssignment(
                        product,
                        assignments));

        for (final ClassAttributeAssignmentModel assignment : assignments) {
            final List<ProductFeatureModel> productFeatures = featuresMap.get(assignment);
            // get feature position of first product features as it must be the same at all product features values
            int featurePosition = isNotEmpty(productFeatures) ? productFeatures.get(0).getFeaturePosition() : 0;
            if (assignment.getLocalized().booleanValue()) {
                Locale currentLocale = i18nService.getCurrentLocale();
                DistLocalizedFeature localizedFeature = new DistLocalizedFeature(assignment,
                        getLocalizedFeaturesValues(productFeatures, currentLocale), currentLocale, featurePosition);
                result.add(localizedFeature);
            } else {
                DistUnlocalizedFeature unlocalizedFeature = new DistUnlocalizedFeature(assignment,
                        getFeaturesValues(productFeatures), featurePosition);
                result.add(unlocalizedFeature);
            }
        }

        return result;
    }

    private List<FeatureValue> getFeaturesValues(final List<ProductFeatureModel> productFeatures) {
        List<FeatureValue> result = null;
        if (productFeatures != null) {
            Collections.sort(productFeatures, new ProductFeatureComparator());
            result = new ArrayList<>();
            for (final ProductFeatureModel productFeature : productFeatures) {
                if (productFeature.getValue() != null) {
                    final FeatureValue featureValue = new FeatureValue(productFeature.getValue(), productFeature.getDescription(),
                            productFeature.getUnit(), productFeature.getPk());
                    result.add(featureValue);
                }
            }
        }
        return result;
    }

    private Map<Locale, List<FeatureValue>> getLocalizedFeaturesValues(final List<ProductFeatureModel> productFeatures,
        final Locale currentLocale) {
        if (productFeatures == null) {
            return null;
        }

        Collections.sort(productFeatures, new ProductFeatureComparator());
        final Map<Locale, List<FeatureValue>> result = new HashMap<>();

        for (final ProductFeatureModel productFeature : productFeatures) {
            final LanguageModel language = productFeature.getLanguage();
            Locale locale;
            if (language == null) {
                locale = currentLocale;
            } else {
                locale = commonI18nService.getLocaleForLanguage(productFeature.getLanguage());
            }

            final Object value = productFeature.getValue();
            if (value == null) {
                continue;
            }
            final FeatureValue featureValue = new FeatureValue(value, productFeature.getDescription(),
                    productFeature.getUnit(), productFeature.getPk());
            if (result.containsKey(locale)) {
                    result.get(locale).add(featureValue);
            } else {
                result.put(locale, Lists.newArrayList(featureValue));
            }
        }

        return result;
    }

    // Getters and Setters

    @Required
    public void setCommonI18nService(final CommonI18NService commonI18nService) {
        super.setCommonI18nService(commonI18nService);
        this.commonI18nService = commonI18nService;
    }

    @Required
    public void setI18nService(final I18NService i18nService) {
        super.setI18nService(i18nService);
        this.i18nService = i18nService;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        super.setModelService(modelService);
        this.modelService = modelService;
    }

    @Override
    public void setProductFeaturesDao(ProductFeaturesDao productFeaturesDao) {
        super.setProductFeaturesDao(productFeaturesDao);
        this.productFeaturesDao = productFeaturesDao;
    }

    public TransactionTemplate getTxTemplate() {
        return txTemplate;
    }

    @Override
    public void setTxTemplate(final TransactionTemplate txTemplate) {
        super.setTxTemplate(txTemplate);
        this.txTemplate = txTemplate;
    }

    public DistProductFeaturesDao getDistProductFeaturesDao() {
        return distProductFeaturesDao;
    }

    public void setDistProductFeaturesDao(final DistProductFeaturesDao distProductFeaturesDao) {
        this.distProductFeaturesDao = distProductFeaturesDao;
    }

    private class ProductFeatureComparator implements Comparator<ProductFeatureModel> {

        @Override
        public int compare(final ProductFeatureModel first, final ProductFeatureModel other) {
            final Integer firstPosition = first.getValuePosition() == null ? Integer.valueOf(0) : first.getValuePosition();
            final Integer otherPosition = other.getValuePosition() == null ? Integer.valueOf(0) : other.getValuePosition();

            return firstPosition.intValue() - otherPosition.intValue();
        }
    }
}
