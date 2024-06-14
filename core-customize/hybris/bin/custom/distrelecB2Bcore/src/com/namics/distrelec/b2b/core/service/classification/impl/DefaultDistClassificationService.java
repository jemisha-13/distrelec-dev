/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.classification.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.service.classification.dao.DistClassificationDao;
import com.namics.distrelec.b2b.core.service.classification.dao.DistProductFeaturesDao;
import com.namics.distrelec.b2b.core.service.classification.features.DistLocalizedFeature;
import com.namics.distrelec.b2b.core.service.classification.features.DistUnlocalizedFeature;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeature;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.classification.daos.ProductFeaturesDao;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.service.classification.DistClassificationService;
import com.namics.distrelec.b2b.core.service.classification.strategy.DistLoadStoreFeaturesStrategy;

import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.classification.ClassificationClassesResolverStrategy;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.impl.DefaultClassificationService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * {@code DefaultDistClassificationService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.12
 */
public class DefaultDistClassificationService extends DefaultClassificationService implements DistClassificationService {

    private static final String FIND_CLASS_ATTR_QUERY = "SELECT {pk} FROM {" + ClassificationAttributeModel._TYPECODE + "} WHERE {"
            + ClassificationAttributeModel.CODE + "}=?" + ClassificationAttributeModel.CODE;

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistClassificationService.class);

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private DistLoadStoreFeaturesStrategy distLoadStoreFeaturesStrategy;

    private ClassificationClassesResolverStrategy classResolverStrategy;

    @Autowired
    private DistClassificationDao distClassificationDao;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private ClassificationSystemService classificationSystemService;

    @Autowired
    private CommonI18NService commonI18nService;

    @Autowired
    private ProductFeaturesDao productFeaturesDao;

    @Autowired
    private DistProductFeaturesDao distProductFeaturesDao;

    @Autowired
    private CMSSiteService cmsSiteService;

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.classification.impl.DefaultClassificationService#getFeatures(de.hybris.platform.core.model.product.ProductModel)
     */
    @Override
    public FeatureList getFeatures(final ProductModel product) {
        CMSSiteModel cmsSiteModel = cmsSiteService.getCurrentSite();
        if (cmsSiteModel != null && cmsSiteModel.getUseProductFeatures() != null && !cmsSiteModel.getUseProductFeatures()) {
            return new FeatureList();
        } else {
            return getSessionService().executeInLocalView(new SessionExecutionBody() {
                @Override
                public Object execute() {
                    getI18NService().setCurrentLocale(new Locale(getI18NService().getCurrentLocale().getLanguage()));
                    return getFeatureList(product);
                }
            });
        }
    }

    private List<ClassAttributeAssignmentModel> getAssignmentsForProduct(final ProductModel product) {
        final Set<ClassificationClassModel> classificationClasses = classResolverStrategy.resolve(product);
        return classResolverStrategy.getAllClassAttributeAssignments(classificationClasses);
    }

    private FeatureList getFeatureList(ProductModel product) {
        return super.getFeatures(product);
    }

    @Override
    public void saveLangIndependentProductFeatures(ProductModel product, List<CProductFeature> productFeatures) {
        saveProductFeatures(product, productFeatures, null);
    }

    @Override
    public void saveLangDependentProductFeatures(ProductModel product, List<CProductFeature> productFeatures, Locale currentLocale) {
        i18NService.setCurrentLocale(currentLocale);
        saveProductFeatures(product, productFeatures, currentLocale);
    }

    protected void saveProductFeatures(ProductModel product, List<CProductFeature> productFeatures, Locale currentLocale) {
        List<ClassAttributeAssignmentModel> assignments = productFeatures.stream()
                .map(CProductFeature::getClassificationAttributeAssignment)
                .filter(assign -> assign != null)
                .collect(Collectors.toList());

        if (assignments.isEmpty()) {
            assignments = getAssignmentsForProduct(product);
        }

        LanguageModel currentLanguage = currentLocale != null ? getCommonI18nService().getLanguage(currentLocale.getLanguage()) : null;

        List<List<ItemModel>> persistedProductFeatures =
                getDistProductFeaturesDao().findProductFeaturesByProductAndLanguage(product, currentLanguage, null);

        Map<PK, List<ProductFeatureModel>> prodFeatureModelsPerAssign = mapProductFeatureModelsPerAssignments(persistedProductFeatures);
        Set<ProductFeatureModel> productFeaturesToRemove = persistedProductFeatures.stream()
            .map(list -> (ProductFeatureModel) list.get(0))
            .collect(Collectors.toSet());

        Map<ClassAttributeAssignmentModel, List<CProductFeature>> prodFeaturesPerAssign =
                mapProductFeaturesPerAssignments(productFeatures, assignments);

        List<Feature> features = new ArrayList<>();
        for (Entry<ClassAttributeAssignmentModel, List<CProductFeature>> prodFeaturePerAssignEntry : prodFeaturesPerAssign.entrySet()) {
            ClassAttributeAssignmentModel assignment = prodFeaturePerAssignEntry.getKey();
            List<CProductFeature> prodFeaturesList = prodFeaturePerAssignEntry.getValue();
            List<ProductFeatureModel> persistedProductFeatureModels = prodFeatureModelsPerAssign.get(assignment.getPk());
            Feature feature = convert(prodFeaturesList, assignment, currentLocale, persistedProductFeatureModels, productFeaturesToRemove);
            if (feature != null) {
                features.add(feature);
            }
        }

        getDistLoadStoreFeaturesStrategy().replaceProductFeatures(product, features, currentLanguage, productFeaturesToRemove);
    }

    private Map<PK, List<ProductFeatureModel>> mapProductFeatureModelsPerAssignments(List<List<ItemModel>> persistedProductFeatures) {
        Map<PK, List<ProductFeatureModel>> prodFeaturesPerAssign = new HashMap<>();

        for (List<ItemModel> persistedFeatures : persistedProductFeatures) {
            ProductFeatureModel productFeature = (ProductFeatureModel)persistedFeatures.get(0);

            ClassAttributeAssignmentModel assignment = null;
            try {
                assignment = (ClassAttributeAssignmentModel) persistedFeatures.get(1);
            } catch (IllegalArgumentException e) {
                // ignore exception as it may be thrown if referenced assignment does not exist
                LOG.warn("Referenced class attribute assignment was removed for product feature: " + productFeature.getPk());
            }

            if (assignment != null) {
                List<ProductFeatureModel> productFeatures = prodFeaturesPerAssign.get(assignment.getPk());
                if (productFeatures == null) {
                    productFeatures = new ArrayList<>();
                    prodFeaturesPerAssign.put(assignment.getPk(), productFeatures);
                }
                productFeatures.add(productFeature);
            }
        }

        return prodFeaturesPerAssign;
    }

    private Map<ClassAttributeAssignmentModel, List<CProductFeature>> mapProductFeaturesPerAssignments(
            List<CProductFeature> productFeatures, List<ClassAttributeAssignmentModel> assignments) {
        Map<String, ClassAttributeAssignmentModel> mappedAssignments = null;
        Map<ClassAttributeAssignmentModel, List<CProductFeature>> prodFeaturesPerAssign = new HashMap<>();
        for (CProductFeature productFeature : productFeatures) {
            ClassAttributeAssignmentModel classAttrAssign = productFeature.getClassificationAttributeAssignment();

            if (classAttrAssign == null) {
                if (mappedAssignments == null) {
                    mappedAssignments = mapClassAttrAssignments(assignments);
                }
                String classAttrAssExternalID = productFeature.getClassAttrAssExternalID();
                classAttrAssign = mappedAssignments.get(classAttrAssExternalID);
            }

            if (classAttrAssign != null) {
                List<CProductFeature> prodFeaturesList;
                if (prodFeaturesPerAssign.containsKey(classAttrAssign)) {
                    prodFeaturesList = prodFeaturesPerAssign.get(classAttrAssign);
                } else {
                    prodFeaturesList = new ArrayList<>();
                    prodFeaturesPerAssign.put(classAttrAssign, prodFeaturesList);
                }

                prodFeaturesList.add(productFeature);
            }
        }
        return prodFeaturesPerAssign;
    }

    private Map<String, ClassAttributeAssignmentModel> mapClassAttrAssignments(List<ClassAttributeAssignmentModel> assignments) {
        Map<String, ClassAttributeAssignmentModel> mappedAssignments = assignments.stream()
                .filter(assignment -> isNotBlank(assignment.getExternalID()))
                .collect(Collectors.toMap(ClassAttributeAssignmentModel::getExternalID, assignment -> assignment));
        return mappedAssignments;
    }

    private Feature convert(List<CProductFeature> productFeatures, ClassAttributeAssignmentModel assignment, Locale currentLocale,
            List<ProductFeatureModel> persistedProductFeatures, Set<ProductFeatureModel> productFeaturesToRemove) {
        List<FeatureValue> featureValues = new ArrayList<>();
        int featurePosition = 0;

        for (CProductFeature productFeature : productFeatures) {
            Object value = convertFeatureValue(assignment, productFeature.getValue());
            if (value == null) {
                return null;
            }
            featurePosition = productFeature.getFeaturePosition();
            ClassificationAttributeUnitModel unit = getClassificationAttributeUnit(assignment.getSystemVersion(), productFeature.getUnit());

            PK productFeaturePK = null;
            if (persistedProductFeatures != null && !persistedProductFeatures.isEmpty()) {
                int minValuePosition = persistedProductFeatures.stream()
                        .map(ProductFeatureModel::getValuePosition)
                        .min(Integer::compare)
                        .get();
                for (ProductFeatureModel persistedProductFeature : persistedProductFeatures) {
                    int valuePosition = minValuePosition + productFeature.getValuePosition();
                    if (valuePosition == persistedProductFeature.getValuePosition()) {
                        productFeaturePK = persistedProductFeature.getPk();
                        productFeaturesToRemove.remove(persistedProductFeature);
                    }
                }
            }

            FeatureValue featureValue = new FeatureValue(value, null, unit);
            featureValue.setProductFeaturePk(productFeaturePK);
            featureValues.add(featureValue);
        }

        if (currentLocale != null) {
            Map<Locale, List<FeatureValue>> featureMap = new HashMap<>();
            featureMap.put(currentLocale, featureValues);
            return new DistLocalizedFeature(assignment, featureMap, currentLocale, featurePosition);
        } else {
            return new DistUnlocalizedFeature(assignment, featureValues, featurePosition);
        }
    }

    private int getMinValuePosition(final ProductModel product, final ClassAttributeAssignmentModel assignment) {
        final List<Integer> minValuePosition = getDistProductFeaturesDao().getProductFeatureMinValuePosition(product, assignment);
        return minValuePosition.get(0) == null ? 0 : minValuePosition.get(0).intValue();
    }

    private boolean matchesLocale(ProductFeatureModel productFeature, Locale currentLocale) {
        LanguageModel language = productFeature.getLanguage();
        if (currentLocale == null && language == null) {
            return true;
        } else if (currentLocale != null && language != null) {
            Locale locale = getCommonI18nService().getLocaleForLanguage(language);
            return currentLocale.equals(locale);
        }
        return false;
    }

    private Object convertFeatureValue(final ClassAttributeAssignmentModel assignment, final String featureValue) {
        Object returnObj = featureValue;
        if (featureValue != null) {
            final ClassificationAttributeTypeEnum type = assignment.getAttributeType();
            switch (type) {
                case BOOLEAN:
                    returnObj = Boolean.valueOf(featureValue);
                    break;
                case NUMBER:
                    returnObj = Double.valueOf(featureValue);
                    break;
                default:
                    break;
            }
        }
        return returnObj;
    }

    private ClassificationAttributeUnitModel getClassificationAttributeUnit(ClassificationSystemVersionModel systemVersion, String code) {
        if (systemVersion != null && code != null) {
            return getClassificationSystemService().getAttributeUnitForCode(systemVersion, code);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.classification.dao.DistClassificationDao#findClassificationAttribute(java.lang.String)
     */
    @Override
    public ClassificationAttributeModel findClassificationAttribute(final String code) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_CLASS_ATTR_QUERY);
        searchQuery.addQueryParameter(ClassificationAttributeModel.CODE, code);
        return getFlexibleSearchService().<ClassificationAttributeModel> searchUnique(searchQuery);
    }

    @Override
    public Map<String, String> getClassificationValueTranslation(String classAttrName, String classAttrValueName) {
        String facetGroupName = classAttrName;
        String facetValueName = classAttrValueName;

        Locale currentLocale = getI18NService().getCurrentLocale();
        List<ClassificationAttributeModel> classAttrs = getDistClassificationDao().findClassificationAttribute(classAttrName, currentLocale);
        if (classAttrs != null && !classAttrs.isEmpty()) {
            Optional<ClassificationAttributeModel> classAttrOptional = classAttrs.stream()
                .filter(classAttr -> isNotBlank(classAttr.getName(Locale.ENGLISH)))
                .findFirst();
            if (classAttrOptional.isPresent()) {
                facetGroupName = classAttrOptional.get().getName(Locale.ENGLISH);
            }

            List<ClassificationAttributeValueModel> classAttrValue = getDistClassificationDao().findClassificationAttributeValue(classAttrs, classAttrValueName, currentLocale);
            if (classAttrValue != null) {
                Optional<ClassificationAttributeValueModel> classAttrValueOptional = classAttrValue.stream()
                    .filter(attrValue -> isNotBlank(attrValue.getName(Locale.ENGLISH)))
                    .findFirst();
                if (classAttrValueOptional.isPresent()) {
                    facetValueName = classAttrValueOptional.get().getName(Locale.ENGLISH);
                }
            }
        }

        Map<String, String> translations =new HashMap<>();
        translations.put(FACET_GROUP_NAME, facetGroupName);
        translations.put(FACET_VALUE_NAME, facetValueName);
        return translations;
    }

    @Override
    public void updateClassificationAttributeValue(ClassificationAttributeModel classificationAttributeModel, String value, Locale currentLocale, String enValue) {
        ClassificationAttributeValueModel classAttrValue = getDistClassificationDao().getClassificationAttributeValue(classificationAttributeModel, enValue);

        if (classAttrValue == null) {
            createClassificationAttributeValue(classificationAttributeModel, value, currentLocale, enValue);
        } else {
            if (!value.equals(classAttrValue.getName(currentLocale))) {
                updateClassificationAttributeValueName(classAttrValue, value, currentLocale);
            }
        }
    }

    private void createClassificationAttributeValue(ClassificationAttributeModel classificationAttributeModel, String value, Locale currentLocale, String enValue) {
        List<ClassificationAttributeValueModel> attrValues = getDistClassificationDao().findAttributeValuesByCode(enValue);
        ClassificationAttributeValueModel attrValue;
        if (!attrValues.isEmpty()) {
            attrValue = attrValues.get(0);

            if (!value.equals(attrValue.getName(currentLocale))) {
                updateClassificationAttributeValueName(attrValue, value, currentLocale);
            }
        } else {
            attrValue = new ClassificationAttributeValueModel();
            attrValue.setCode(enValue);
            attrValue.setName(value, currentLocale);
            attrValue.setName(enValue, Locale.ENGLISH);
            attrValue.setSystemVersion(classificationAttributeModel.getSystemVersion());
            getModelService().save(attrValue);
        }

        // add attribute value to attribute
        List<ClassificationAttributeValueModel> defaultAttrValues = classificationAttributeModel.getDefaultAttributeValues();
        List<ClassificationAttributeValueModel> newAttrValues = new ArrayList<>(defaultAttrValues.size() + 1);
        newAttrValues.addAll(defaultAttrValues);
        newAttrValues.add(attrValue);
        classificationAttributeModel.setDefaultAttributeValues(newAttrValues);
        getModelService().save(classificationAttributeModel);
    }

    private void updateClassificationAttributeValueName(ClassificationAttributeValueModel classificationAttributeValueModel, String value, Locale currentLocale) {
        classificationAttributeValueModel.setName(value, currentLocale);
        getModelService().save(classificationAttributeValueModel);
    }

    // Getters & Setters

    public DistLoadStoreFeaturesStrategy getDistLoadStoreFeaturesStrategy() {
        return distLoadStoreFeaturesStrategy;
    }

    public void setDistLoadStoreFeaturesStrategy(final DistLoadStoreFeaturesStrategy distLoadStoreFeaturesStrategy) {
        this.distLoadStoreFeaturesStrategy = distLoadStoreFeaturesStrategy;
    }

    public ClassificationClassesResolverStrategy getClassResolverStrategy() {
        return classResolverStrategy;
    }

    @Required
    @Override
    public void setClassResolverStrategy(ClassificationClassesResolverStrategy classResolverStrategy) {
        super.setClassResolverStrategy(classResolverStrategy);
        this.classResolverStrategy = classResolverStrategy;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public DistClassificationDao getDistClassificationDao() {
        return distClassificationDao;
    }

    public void setDistClassificationDao(DistClassificationDao distClassificationDao) {
        this.distClassificationDao = distClassificationDao;
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(I18NService i18NService) {
        this.i18NService = i18NService;
    }

    public ClassificationSystemService getClassificationSystemService() {
        return classificationSystemService;
    }

    public void setClassificationSystemService(final ClassificationSystemService classificationSystemService) {
        this.classificationSystemService = classificationSystemService;
    }

    public CommonI18NService getCommonI18nService() {
        return commonI18nService;
    }

    public void setCommonI18nService(final CommonI18NService commonI18nService) {
        this.commonI18nService = commonI18nService;
    }

    public ProductFeaturesDao getProductFeaturesDao() {
        return productFeaturesDao;
    }

    public void setProductFeaturesDao(ProductFeaturesDao productFeaturesDao) {
        this.productFeaturesDao = productFeaturesDao;
    }

    public DistProductFeaturesDao getDistProductFeaturesDao() {
        return distProductFeaturesDao;
    }

    public void setDistProductFeaturesDao(DistProductFeaturesDao distProductFeaturesDao) {
        this.distProductFeaturesDao = distProductFeaturesDao;
    }
}
