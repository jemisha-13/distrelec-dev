/*
* Copyright 2000-2013 Namics AG. All rights reserved.
*/

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.Product;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementIdNotFoundException;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.CProductFeatureKey;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.PimImportInitializer;
import com.namics.distrelec.b2b.core.service.classification.impl.DefaultDistClassificationService;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeature;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeatures;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.FFAdditionalDataGenerator.isEelFeature;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.FFAdditionalDataGenerator.isPimWebUseVisible;

/**
 * {@code ProductFeatureElementConverter}
 * <p>
 * Converts an XML element to a {@link ProductModel}.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.9
 */
public class ProductFeatureElementConverter extends AbstractElementConverter implements PimImportElementConverter<ProductModel> {

    private static final Logger LOG = LogManager.getLogger(ProductFeatureElementConverter.class);

    private static final String XP_ERP_ID6 = "Values/Value[@AttributeID='ERP_ArtNr']";
    private static final String XP_ERP_ID8 = "Values/Value[@AttributeID='articlenumber_num']";

    private static final String XP_FEATURES = "Values/*|MultiValue/*|ValueGroup/*";
    private static final String XP_FEATURES_VALUE = "Value";
    private static final String XP_FEATURES_MULTIVALUE = "MultiValue";
    private static final String XP_FEATURES_CALCULATED = "Derived";

    private static final String XP_FEATURES_ATTRIBUTEID = "AttributeID";
    private static final String XP_FEATURES_UNIT = "UnitID";
    private static final String XP_FEATURES_MULTILANG = "ValueGroup";

    private static final String EN = "en";
    public static final String ID = "ID";
    public static final String MULTI_VALUE_DELIMITER = "|";
    public static final String QUALIFIER_ID = "QualifierID";
    public static final String LOV_QUALIFIER_ID = "LOVQualifierID";

    public static final List<String> MULTI_LINE_ATTRIBUTE_CODES = Arrays.asList( //
            Product.ATTRIBUTE_CODE_FAMILY_DESCRIPTION, //
            Product.ATTRIBUTE_CODE_FAMILY_DESCRIPTION_BULLET, //
            Product.ATTRIBUTE_CODE_SERIES_DESCRIPTION, //
            Product.ATTRIBUTE_CODE_SERIES_DESCRIPTION_BULLET, //
            Product.ATTRIBUTE_CODE_ARTICLE_DESCRIPTION, //
            Product.ATTRIBUTE_CODE_ARTICLE_DESCRIPTION_BULLET);

    public static final Pattern MULTI_LINE_VALUE_SPLIT_PATTERN = Pattern.compile("#NEWLINE#|<br\\/>");

    @Autowired
    private ModelService modelService;

    @Autowired
    private ClassificationSystemService classificationSystemService;

    @Autowired
    private PimImportInitializer<ClassAttributeAssignmentModel> classAttributeAssignmentInitializer;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private FFAdditionalDataGenerator ffAdditionalDataGenerator;

    @Autowired
    private ConverterLanguageUtil converterLanguageUtil;

    @Autowired
    private DefaultDistClassificationService defaultDistClassificationService;

    private static Set<Locale> activePimLocales;

    @Override
    public String getId(final Element element) {
        final String erpId6 = element.valueOf(XP_ERP_ID6);
        final String erpId8 = element.valueOf(XP_ERP_ID8);
        String productCode = null;

        if (StringUtils.isNotEmpty(erpId8)) {
            productCode = erpId8;
        } else {
            productCode = erpId6;
            if (StringUtils.isBlank(productCode)) {
                throw new ElementIdNotFoundException("No product code found for product with PIM-ID [" + element.attributeValue(ATTRIBUTE_ID) + "]");
            } else {
                LOG.debug("No 8-digit article number found for product with PIM-ID [" + element.attributeValue(ATTRIBUTE_ID) + "] found [" + productCode + "]");
            }
        }

        return productCode;
    }

    @Override
    public void convert(final Element source, final ProductModel target, final ImportContext importContext, final String hash) {
        convertLocalized(source, target, importContext);
    }

    private void convertLocalized(final Element source, final ProductModel target, final ImportContext importContext) {
        if (importProductFeatures()) {
            convertProductFeatures(source, target, importContext);
        }
    }

    private boolean importProductFeatures() {
        return getConfigurationService().getConfiguration().getBoolean(PimConstants.IMPORT_PRODUCTFEATURES_PARAM, true);
    }

    private void convertProductFeatures(final Element source, final ProductModel target, final ImportContext importContext) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Updating database with product features for product " + target.getCode());
        }

        if (activePimLocales == null) {
            activePimLocales = getConverterLanguageUtil().getActivePimLocales();
        }

        // create map with all languages, because even if a product has not language dependent attribute,
        // or no attributes for a certain language,
        // the language independent attributes have to be written to the no-sql object for this language
        Map<Locale, CProductFeatures> languageFeatures = new HashMap<>();
        activePimLocales.forEach(key -> {
            CProductFeatures lProductFeatures = new CProductFeatures();
            lProductFeatures.setLanguage(key.getLanguage());
            lProductFeatures.setProduct(target.getCode());
            lProductFeatures.setFeatures(new ArrayList<>());
            languageFeatures.put(key,lProductFeatures);

        });

        final Map<CProductFeatureKey, CProductFeature> indexedFeatures = new HashMap<>();

        // Create the Product features, to temporary hold language independent features
        final CProductFeatures cProductFeatures = new CProductFeatures();
        cProductFeatures.setProduct(target.getCode());
        cProductFeatures.setFeatures(new ArrayList<>());
        cProductFeatures.setLanguage(Locale.ENGLISH.getLanguage());

        int featurePosition = 0;
        final List<Element> featureElements = source.selectNodes(XP_FEATURES);
        for (final Element element : featureElements) {
            if (importContext.getBlacklistedProductFeatures().contains(element.attributeValue(XP_FEATURES_ATTRIBUTEID))) {
                // Skip blacklisted feature
                continue;
            }
            if (XP_FEATURES_VALUE.equals(element.getName())) {
                addSingleValueFeatures(target, element, cProductFeatures, indexedFeatures, featurePosition++, importContext);
            } else if (XP_FEATURES_MULTIVALUE.equals(element.getName())) {

                final List<Element> languageDependent = element.selectNodes(XP_FEATURES_MULTILANG);
                if (CollectionUtils.isNotEmpty(languageDependent)) {
                    addMvLanguageDependentFeatures(target, element, languageFeatures, indexedFeatures, featurePosition++, importContext);
                } else {
                    addMultiValueFeatures(target, element, cProductFeatures, indexedFeatures, featurePosition++, importContext);
                }
            } else if (XP_FEATURES_MULTILANG.equals(element.getName())) {
                addLanguageDependentFeatures(target, element, languageFeatures, indexedFeatures, featurePosition++, importContext);
            } else {
                LOG.warn("Got unexpected element [{}]", element.getName());
            }
        }
        mergeAndPersist(cProductFeatures, languageFeatures, target);
    }

    private void addMvLanguageDependentFeatures(final ProductModel target, final Element element, final Map<Locale, CProductFeatures> languageFeatures,
                                                final Map<CProductFeatureKey, CProductFeature> indexedFeatures, final int featurePosition, final ImportContext importContext) {
        final String attributeCode = element.attributeValue(XP_FEATURES_ATTRIBUTEID);

        Map<Locale, Map<String, Element>> values = getConverterLanguageUtil().getLocalizedMvElements(element, "ValueGroup/Value | Value");

        populateMissingLanguages(languageFeatures, values);

        values.entrySet().stream().filter((value) -> value.getKey() != null).forEach((value) -> {
            int valuePosition = 0;
            for (final Element mvElement : value.getValue().values()) {
                addSingleValueFeatures(target, mvElement, languageFeatures.get(value.getKey()), indexedFeatures,
                                       importContext, attributeCode, featurePosition, valuePosition++);
            }
        });
    }

    private void populateMissingLanguages(final Map<Locale, CProductFeatures> languageFeatures, final Map<Locale, Map<String, Element>> values) {
        final Locale englishLocale = getEnglishLocale(values);
        final Map<String, Element> englishValue = values.getOrDefault(englishLocale, null);

        if (englishLocale != null && MapUtils.isNotEmpty(englishValue)) {
            for (Locale l : languageFeatures.keySet()) {
                if (!values.containsKey(l)) {
                    values.put(l, englishValue);
                } else {
                    if (!"en".equals(l.getLanguage())) {
                        Map<String, Element> elements = values.get(l);
                        englishValue.forEach(elements::putIfAbsent);
                    }
                }
            }
        }
    }

    private void mergeAndPersist(final CProductFeatures langIndependentFeatures, final Map<Locale, CProductFeatures> languageFeatures, final ProductModel target) {
        // mark all lang independent features
        langIndependentFeatures.getFeatures().forEach(f -> f.setLangIndependent(true));
        getDefaultDistClassificationService().saveLangIndependentProductFeatures(target, langIndependentFeatures.getFeatures());

        CProductFeatures englishFeatures = languageFeatures.get(Locale.ENGLISH);
        Map<CProductFeatureKey, CProductFeature> englishFeatureMap = new HashMap<>();
        mergeVisibleFeaturesToMap(langIndependentFeatures.getFeatures(), englishFeatureMap);
        mergeVisibleFeaturesToMap(englishFeatures.getFeatures(), englishFeatureMap);

        for (CProductFeatures languageFeature : languageFeatures.values()) {
            try {
                // Calculate WebUse string used later on during Fact-Finder export + EELs + WebUseD
                Locale languageLocal = new Locale(languageFeature.getLanguage());

                getDefaultDistClassificationService().saveLangDependentProductFeatures(target, languageFeature.getFeatures(), languageLocal);

                Map<CProductFeatureKey, CProductFeature> allFeaturesMap = new HashMap<>(englishFeatureMap);
                mergeVisibleFeaturesToMap(languageFeature.getFeatures(), allFeaturesMap);

                getFfAdditionalDataGenerator().generateFFAdditionalData(new ArrayList<>(allFeaturesMap.values()), target, languageLocal, englishFeatureMap);

                getModelService().save(target);
            } catch (final Exception exp) {
                LOG.error("mergeAndPersist", exp);
            }
        }
    }

    private void mergeVisibleFeaturesToMap(List<CProductFeature> features, Map<CProductFeatureKey, CProductFeature> mappedVisibleProductFeatures) {
        for (CProductFeature feature : features) {
            if (isPimWebUseVisible(feature.getClassificationAttributeAssignment()) || isEelFeature(feature)) {
                CProductFeatureKey featureKey = new CProductFeatureKey(Locale.ENGLISH, feature);
                mappedVisibleProductFeatures.put(featureKey, feature);
            }
        }
    }

    private void addLanguageDependentFeatures(final ProductModel target, final Element element, final Map<Locale, CProductFeatures> languageFeatures,
                                              final Map<CProductFeatureKey, CProductFeature> indexedFeatures, final int featurePosition, final ImportContext importContext) {

        final String attributeCode = element.attributeValue(XP_FEATURES_ATTRIBUTEID);

        Map<Locale, Element> values = getConverterLanguageUtil().getLocalizedElements(element, XP_FEATURES_VALUE);

        logNullLocaleWarning(values, target, attributeCode);

        replaceMissingLanguageWithEnglish(languageFeatures, values);

        values.entrySet().stream().filter ((value) -> value.getKey() != null).forEach((value) -> {
            addSingleValueFeatures(target, value.getValue(), languageFeatures.get(value.getKey()), indexedFeatures, featurePosition, importContext, attributeCode);
        });
    }

    private void logNullLocaleWarning(Map<Locale, Element> values, ProductModel product, String attributeCode) {
        boolean containsNullLocale = values.entrySet().stream()
                                           .map(Map.Entry::getKey)
                                           .filter(Objects::isNull)
                                           .findAny()
                                           .isPresent();
        if (containsNullLocale) {
            LOG.warn("Locale is not resolved for product: {}, and attribute: {}", product.getCode(), attributeCode);
        }
    }

    private void replaceMissingLanguageWithEnglish(final Map<Locale, CProductFeatures> languageFeatures, final Map<Locale, Element> values) {
        final Locale englishLocale = getEnglishLocale(values);
        final Element englishValue = values.getOrDefault(englishLocale, null);

        if (englishLocale != null && englishValue != null) {
            for (Locale locale : languageFeatures.keySet()) {
                if (!values.containsKey(locale)) {
                    values.put(locale, englishValue);
                }
            }
        }
    }

    private Locale getEnglishLocale(final Map<Locale, ?> values) {
        return values.keySet().stream()
                     .filter(Objects::nonNull)
                     .filter(elements -> EN.equalsIgnoreCase(elements.getLanguage()))
                     .findFirst()
                     .orElse(null);
    }

    private void addSingleValueFeatures(final ProductModel target, final Element element, final CProductFeatures cProductFeatures,
            final Map<CProductFeatureKey, CProductFeature> indexedFeatures, final int featurePosition,
            final ImportContext importContext, String attributeCode) {
        addSingleValueFeatures(target, element, cProductFeatures, indexedFeatures, importContext, attributeCode, featurePosition, Integer.valueOf(0));

    }

        /**
		 *
		 * @param target
		 * @param element
		 * @param cProductFeatures
		 * @param importContext
		 */
    private void addSingleValueFeatures(final ProductModel target, final Element element, final CProductFeatures cProductFeatures,
            final Map<CProductFeatureKey, CProductFeature> indexedFeatures, final ImportContext importContext, String attributeCode,
            int featurePosition, Integer valuePosition) {

        final ClassAttributeAssignmentModel assignment = getOrCreateClassAttributeAssignment(attributeCode, importContext);
        if (assignment == null) {
            return;
        }

        final Boolean calculated = getCalculatedValued(element);
        final String unitCode = element.attributeValue(XP_FEATURES_UNIT);
        final String value = getProductFeatureValue(target, attributeCode, element, importContext, unitCode, calculated);

        if (MULTI_LINE_ATTRIBUTE_CODES.contains(attributeCode)) {
            addMultiLineFeatures(target, cProductFeatures, indexedFeatures, attributeCode, unitCode, value, featurePosition, importContext, assignment);
        } else {
            // Adding the product feature to the database.
            addProductFeature(target, cProductFeatures, indexedFeatures, attributeCode, unitCode, value, featurePosition, valuePosition, importContext, assignment);
        }
    }

	private void addSingleValueFeatures(final ProductModel target, final Element element, final CProductFeatures cProductFeatures,
            final Map<CProductFeatureKey, CProductFeature> indexedFeatures, final int featurePosition, final ImportContext importContext) {
		final String attributeCode = element.attributeValue(XP_FEATURES_ATTRIBUTEID);
		addSingleValueFeatures(target, element, cProductFeatures, indexedFeatures, featurePosition, importContext, attributeCode);
	}

    private Boolean getCalculatedValued(final Element element) {
        final String value = element.attributeValue(XP_FEATURES_CALCULATED);
        return StringUtils.isBlank(value) ? Boolean.FALSE : Boolean.valueOf(value);
    }

    private String getProductFeatureValue(final ProductModel target, final String attributeCode, final Element productFeatureValueElement,
            final ImportContext importContext, final String unitCode, final Boolean calculated) {
        final StringBuilder result = new StringBuilder();

        final String unitValue = getUnitValueFromUnitCode(target, attributeCode, importContext, unitCode);

        if (productFeatureValueElement.isTextOnly()) {
            result.append(productFeatureValueElement.getTextTrim());
            if (calculated) {
                result.append(" ");
                result.append(unitValue);
            }
            return result.toString().trim();
        }

        for (final Object childElement : productFeatureValueElement.elements()) {
            if (childElement instanceof Element) {
                final Element element = (Element) childElement;
                if (element.getName().equalsIgnoreCase("prefix")) {
                    result.append(element.getTextTrim());
                    result.append(productFeatureValueElement.getTextTrim());
                    result.append(StringUtils.isNotBlank(unitValue) ? " " : "");
                    result.append(unitValue == null ? "" : unitValue);
                } else if (element.getName().equalsIgnoreCase("suffix")) {
                    result.append(element.getTextTrim());
                }
            }
        }

        return result.toString().trim();
    }

    private String getUnitValueFromUnitCode(final ProductModel target, final String attributeCode, final ImportContext importContext, final String unitCode) {
        String unitValue = DistConstants.Punctuation.EMPTY;
        if (StringUtils.isNotBlank(unitCode)) {
            try {
                final ClassificationAttributeUnitModel unit = getClassificationSystemService()
                        .getAttributeUnitForCode(importContext.getClassificationSystemVersion(), unitCode);
                unitValue = unit.getName();
            } catch (final UnknownIdentifierException e) {
                LOG.error("Unit with code [{}] can not be found for product with PIM-ID [{}] for attribute [{}].",
                          unitCode, target.getPimId(), attributeCode);
            } catch (final AmbiguousIdentifierException e) {
                LOG.error("More than one unit found with code [{}] for product with PIM-ID [{}] for attribute [{}].",
                          unitCode, target.getPimId(), attributeCode);
            }
        }
        return unitValue;
    }

    /**
     * Adding multi-line product feature. A Multi-lined product feature is considered as Multi-Value one
     * 
     * @param target the target {@code ProductModel}
     * @param cProductFeatures the target object holder of the product features
     * @param attributeCode the attribute code
     * @param unitCode the unit code
     * @param multiLineValue the XML content of the multi-line feature
     * @param importContext the import context
     */
    private void addMultiLineFeatures(final ProductModel target, final CProductFeatures cProductFeatures,
            final Map<CProductFeatureKey, CProductFeature> indexedFeatures, final String attributeCode, final String unitCode,
            final String multiLineValue, final int featurePosition, final ImportContext importContext,
            final ClassAttributeAssignmentModel assignment) {

        final String[] lines = MULTI_LINE_VALUE_SPLIT_PATTERN.split(multiLineValue);

        int nonEmptyLines = 0;

        for (final String line : lines) {
            if (!StringUtils.isBlank(line.trim())) {
                // Adding the product feature to the database.
                addProductFeature(target, cProductFeatures, indexedFeatures, attributeCode, unitCode, line,
                        featurePosition, Integer.valueOf(nonEmptyLines++), importContext, assignment);
            }
        }
    }

    /**
     * Adding Multi-valued product feature
     * 
     * @param target
     *            the target {@code ProductModel}
     * @param element
     *            the XML element from file
     * @param cProductFeatures
     *            the target object holder of the localized product features.
     * @param importContext
     *            the import context
     */
    private void addMultiValueFeatures(final ProductModel target, final Element element, final CProductFeatures cProductFeatures,
            final Map<CProductFeatureKey, CProductFeature> indexedFeatures, final int featurePosition, final ImportContext importContext) {
        final String attributeCode = element.attributeValue(XP_FEATURES_ATTRIBUTEID);

        final ClassAttributeAssignmentModel assignment = getOrCreateClassAttributeAssignment(attributeCode, importContext);
        if (assignment == null) {
            return;
        }

        int valuePosition = 0;
        final List<Element> valueElements = element.selectNodes(XP_FEATURES_VALUE);
        for (final Element valueElement : valueElements) {
            final String unitCode = valueElement.attributeValue(XP_FEATURES_UNIT);
            final String value = getProductFeatureValue(target, attributeCode, valueElement, importContext, unitCode, Boolean.FALSE);
            // Adding the product feature to the database.
            addProductFeature(target, cProductFeatures, indexedFeatures, attributeCode, unitCode, value, featurePosition,
                    valuePosition++, importContext, assignment);
        }
    }

    /**
     * Add a product feature to the list of features.
     * 
     * @param target
     * @param cProductFeatures
     * @param attributeCode
     * @param unitCode
     * @param value
     * @param valuePosition
     * @param importContext
     */
    protected void addProductFeature(final ProductModel target, final CProductFeatures cProductFeatures,
            final Map<CProductFeatureKey, CProductFeature> indexedFeatures, final String attributeCode, final String unitCode,
            final String value, final int featurePosition, final Integer valuePosition, final ImportContext importContext,
            final ClassAttributeAssignmentModel assignment) {

        // https://jira.distrelec.com/browse/DISTRELEC-14609
        if (StringUtils.isBlank(value)) {
            LOG.debug("Product: {} has attribute: {} with blank value", target.getCode(), attributeCode);
            return;
        }
        if (getConverterLanguageUtil().ignoreValue(value)) {
            return;
        }

        final CProductFeature feature = new CProductFeature();

        /**
         * @see {@link de.hybris.platform.catalog.impl.ProductFeaturePrepareInterceptor}
         */
        feature.setQualifier(createFeatureQualifier(assignment));
        feature.setFeaturePosition(featurePosition);
        feature.setValuePosition(valuePosition);
        feature.setValue(value);
        feature.setVisibility(assignment.getVisibility());
        feature.setClassAttrAssExternalID(assignment.getExternalID());

        // This will not be serialized because of the annotation @JsonIgnore
        feature.setClassificationAttributeAssignment(assignment);

        // if (unitCode != null && !(attributeCode.startsWith("Calc_") || attributeCode.startsWith("calc_"))) {
        // DISTRELEC-6837 changing the logic on how to identify the calculated and embedded attributes.
        if (!(BooleanUtils.isTrue(assignment.getPimCalculated()) || BooleanUtils.isTrue(assignment.getPimEmbedded()))) {
            final ClassificationAttributeUnitModel unit = getClassificationAttributeUnit(importContext.getClassificationSystemVersion(), unitCode,
                    attributeCode, target.getCode());

            feature.setUnit(unit != null ? unit.getCode() : null);
            feature.setUnitSymbol(unit != null ? unit.getSymbol() : null);
        }

        if (CollectionUtils.isEmpty(cProductFeatures.getFeatures())) {
            cProductFeatures.setFeatures(new ArrayList<>());
        }
        CProductFeatureKey featureKey = new CProductFeatureKey(new Locale(cProductFeatures.getLanguage(), ""), feature);
        if (indexedFeatures.containsKey(featureKey)) {
            LOG.warn("Product feature is already imported for product: {} ; feature qualifier: {} ; feature valuePosition: {} ; language: {}",
                    target.getCode(), feature.getQualifier(), feature.getValuePosition(), cProductFeatures.getLanguage());
        } else {
            indexedFeatures.put(featureKey, feature);
            cProductFeatures.getFeatures().add(feature);
        }
    }

    private ClassAttributeAssignmentModel getOrCreateClassAttributeAssignment(final String attributeCode, final ImportContext importContext) {
        ClassAttributeAssignmentModel assignment = importContext.getCurrentClassificationClassWrapper().getClassAttributeAssignment(attributeCode);

        if (assignment == null && importContext.getWhitelistedRootProductFeatures().contains(attributeCode) && !importContext.isIgnoreRootAttributes()) {
            assignment = importContext.getRootClassificationClassWrapper().getClassAttributeAssignment(attributeCode);

            if (assignment == null) {
                assignment = createRootClassAttributeAssignment(attributeCode, importContext);
            }
        }

        if (assignment != null && assignment.getExternalID() == null) {
            // regenerate external id if it is not defined
            String externalId = assignment.getClassificationClass().getCode() + "-" + assignment.getClassificationAttribute().getCode();
            assignment.setExternalID(externalId);
            getModelService().save(assignment);
        }

        return assignment;
    }

    public static String createFeatureQualifier(final ClassAttributeAssignmentModel classificationAttrAssignment) {
        return new StringBuilder(classificationAttrAssignment.getClassificationClass().getCode()) //
                .append('.') //
                .append(classificationAttrAssignment.getClassificationAttribute().getCode().toLowerCase().intern()) //
                .toString();
    }

    private ClassificationAttributeUnitModel getClassificationAttributeUnit(final ClassificationSystemVersionModel systemVersion, final String unitCode,
            final String attributeCode, final String productCode) {
        if (StringUtils.isNotBlank(unitCode)) {
            try {
                return getClassificationSystemService().getAttributeUnitForCode(systemVersion, unitCode);
            } catch (final UnknownIdentifierException e) {
                LOG.error("Could not get ClassificationAttributeUnitModel for unit code [" + unitCode + "] used to create attribute code [" + attributeCode
                        + "] for product [" + productCode + "]");
                return null;
            }
        } else {
            return null;
        }
    }

    private ClassAttributeAssignmentModel createRootClassAttributeAssignment(final String attributeCode, final ImportContext importContext) {
        ClassAttributeAssignmentModel assignment = null;

        try {
            final ClassificationAttributeModel classificationAttribute = getClassificationSystemService()
                    .getAttributeForCode(importContext.getClassificationSystemVersion(), attributeCode);

            assignment = getModelService().create(ClassAttributeAssignmentModel.class);
            assignment.setClassificationClass(importContext.getRootClassificationClassWrapper().getClassificationClass());
            assignment.setClassificationAttribute(classificationAttribute);

            try {
                classAttributeAssignmentInitializer.initialize(assignment, importContext);
                getModelService().save(assignment);
                importContext.getRootClassificationClassWrapper().addClassAttributeAssignment(assignment);
            } catch (final ElementConverterException e) {
                LOG.error("Could not initialize root ClassAttributeAssignment with basic properties: " + e.getMessage());
                assignment = null;
            }
        } catch (final UnknownIdentifierException e) {
            LOG.warn("Could not find classification attribute with code [{}]", attributeCode);
        }

        return assignment;
    }

    // BEGIN GENERATED CODE

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public ClassificationSystemService getClassificationSystemService() {
        return classificationSystemService;
    }

    public void setClassificationSystemService(final ClassificationSystemService classificationSystemService) {
        this.classificationSystemService = classificationSystemService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public FFAdditionalDataGenerator getFfAdditionalDataGenerator() {
        return ffAdditionalDataGenerator;
    }

    public void setFfAdditionalDataGenerator(final FFAdditionalDataGenerator ffAdditionalDataGenerator) {
        this.ffAdditionalDataGenerator = ffAdditionalDataGenerator;
    }

    public ConverterLanguageUtil getConverterLanguageUtil() {
        return converterLanguageUtil;
    }

    public void setConverterLanguageUtil(ConverterLanguageUtil converterLanguageUtil) {
        this.converterLanguageUtil = converterLanguageUtil;
    }

    public DefaultDistClassificationService getDefaultDistClassificationService() {
        return defaultDistClassificationService;
    }

    public void setDefaultDistClassificationService(final DefaultDistClassificationService defaultDistClassificationService) {
        this.defaultDistClassificationService = defaultDistClassificationService;
    }

    // END GENERATED CODE

}
