/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Fusion.ALLOWED_CHARACTERS_FIELDNAME;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.EFFICENCY_FEATURE;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.ENERGY_CLASSES_LUMINAR_BUILT_IN_LED;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.ENERGY_CLASSES_LUMINAR_FITTING;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.ENERGY_CLASSES_LUMINAR_INCLUDED_BULB;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.POWER_FEATURE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.distrelec.b2b.core.search.data.PimWebUseField;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.CProductFeatureKey;
import com.namics.distrelec.b2b.core.service.classification.DistClassificationService;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.comparator.CProductFeatureComparator;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeature;

import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.springframework.beans.factory.annotation.Required;

public class FFAdditionalDataGenerator {

    private static final Logger LOG = LogManager.getLogger(FFAdditionalDataGenerator.class);

    public static final Set<String> EEL_CODES = new HashSet<>(Arrays.asList(EFFICENCY_FEATURE, POWER_FEATURE, ENERGY_CLASSES_LUMINAR_FITTING, //
            ENERGY_CLASSES_LUMINAR_INCLUDED_BULB, ENERGY_CLASSES_LUMINAR_BUILT_IN_LED));

    private DistClassificationService distClassificationService;

    private static final Gson GSON = new Gson();

    /**
     * This method is doing the following:
     * <ul>
     * <li>Generate Fact-Finder PIM WebUse as string containing the PIM WebUse (visibility= A, B or C) separated by '|'</li>
     * <li>Generate Fact-Finder PIM WebUse D as string containing the PIM WebUse (visibility= D) separated by '|'</li>
     * <li>Calculate the EELs</li>
     * </ul>
     * The update of this method is to optimize the previous implementation which loops several time on the list of {@link CProductFeature}
     * and do all the calculation in one single loop.
     *
     * @param features
     * @param target
     * @param currentLocale
     */
    public void generateFFAdditionalData(final List<CProductFeature> features, final ProductModel target, final Locale currentLocale,
                                         final Map<CProductFeatureKey, CProductFeature> englishFeatureMap) {

        final Map<String, StringBuilder> energyEfficiencyLabels = new HashMap<>();
        // TRUE => WebUseD, FALSE => FF-WebUse
        final Map<Boolean, List<String>> webUseStringMap = new HashMap<>() {
            /*
             * (non-Javadoc)
             *
             * @see java.util.HashMap#get(java.lang.Object)
             */
            @Override
            public List<String> get(final Object key) {
                if (!containsKey(key)) {
                    put((Boolean) key, new ArrayList<>(features.size()));
                }

                return super.get(key);
            }
        };

        // Sort the product features
        Collections.sort(features, new CProductFeatureComparator());

        List<PimWebUseField> pimWebUseFields = new ArrayList<>();
        for (final CProductFeature feature : features) {
            // DISTRELEC-9077
           if (isEelFeature(feature)) {
               String eel_code = extractProductFeatureCode(feature);
               final StringBuilder eelValue = energyEfficiencyLabels.containsKey(eel_code) ? energyEfficiencyLabels.get(eel_code) : new StringBuilder();
               eelValue.append(eelValue.length() > 0 ? ";" : StringUtils.EMPTY).append(feature.getValue());
               energyEfficiencyLabels.put(eel_code, eelValue);
            }

            if (isPimWebUseVisible(feature.getClassificationAttributeAssignment()) //
                    && feature.getClassificationAttributeAssignment().getClassificationAttribute() != null //
                    && feature.getClassificationAttributeAssignment().getClassificationAttribute().getName(currentLocale) != null) {

                final List<String> featureValueList;
                List<String> featureValueEnList = null;
                CProductFeature englishFeature = !feature.isLangIndependent() && !Locale.ENGLISH.equals(currentLocale) ? englishFeatureMap.get(new CProductFeatureKey(Locale.ENGLISH, feature)) : null;
                if (splitFeatureValues(feature, englishFeature)) {
                    featureValueList = Arrays.asList(feature.getValue().toString().split(";"));
                    if (englishFeature != null) {
                        featureValueEnList = Arrays.asList(englishFeature.getValue().split(";"));
                    }
                    // } else if (feature.getValue() != null && feature.getValue().toString().contains(", ") &&
                    // StringUtils.countMatches(feature.getValue().toString(), ", ") >= 2) {
                    // featureValueList = Arrays.asList(feature.getValue().toString().split(", "));
                } else {
                    featureValueList = Arrays.asList(feature.getValue().toString());
                    if (englishFeature != null) {
                        featureValueEnList = Arrays.asList(englishFeature.getValue());
                    }
                }
                Boolean isPimWebUseDfeature = Boolean.valueOf(isPimWebUseDfeature(feature.getClassificationAttributeAssignment()));
                final List<String> webUseString = webUseStringMap.get(isPimWebUseDfeature);
                Iterator<String> featureValueIt = featureValueList.iterator();
                Iterator<String> featureValueEnIt = featureValueEnList != null ? featureValueEnList.iterator() : null;

                while (featureValueIt.hasNext()) {

                    String value = featureValueIt.next().trim();
                    if(!isPimWebUseDfeature)
                    {
                        PimWebUseField pimWebUseField = createPimWebUseField(feature, value, currentLocale);
                        pimWebUseFields.add(pimWebUseField);

                    }
                    if (featureValueEnIt != null) {
                        String enValue = featureValueEnIt.next().trim();
                        webUseString.add(createFeatureString(feature, value, currentLocale, enValue).toString());
                    } else {
                        webUseString.add(createFeatureString(feature, value, currentLocale).toString());
                    }
                }
            }
        }
        LOG.debug("Setting pim-webuse for product:{}", target.getCode());
        if(CollectionUtils.isNotEmpty(pimWebUseFields))
        {
            target.setPimWebUseJson(GSON.toJson(pimWebUseFields),currentLocale);
        }

        // FALSE => FF-WebUse
        target.setPimWebUse(webUseStringMap.get(Boolean.FALSE).isEmpty() ? null : ("|" + StringUtils.join(webUseStringMap.get(Boolean.FALSE), "|") + "|"),
                currentLocale);
        // DISTRELEC-9077: TRUE => WebUseD
        target.setPimWebUseDfeatures(
                webUseStringMap.get(Boolean.TRUE).isEmpty() ? null : ("|" + StringUtils.join(webUseStringMap.get(Boolean.TRUE), "|") + "|"), currentLocale);

        try {
            target.setEnergyEffiencyLabels(new ObjectMapper().writeValueAsString(energyEfficiencyLabels), currentLocale);
        } catch (final IOException e) {
            LOG.error("Error occur while converting EELs", e);
        }
    }

    private boolean splitFeatureValues(CProductFeature feature, CProductFeature englishFeature) {
        int featureSegments = StringUtils.countMatches(feature.getValue(), ";");

        if (featureSegments >= 2) {
            if (englishFeature != null) {
                int enFeatureSegments = StringUtils.countMatches(englishFeature.getValue(), ";");
                boolean featureSegmentsAreEquals = featureSegments == enFeatureSegments;
                return featureSegmentsAreEquals;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private StringBuilder createFeatureString(final CProductFeature feature, final String value, final Locale currentLocale) {
        return createFeatureString(feature, value, currentLocale, null);
    }

    private StringBuilder createFeatureString(final CProductFeature feature, final String value, final Locale currentLocale, final String enValue) {
        final StringBuilder sb = new StringBuilder();
        ClassificationAttributeModel classAttr = feature.getClassificationAttributeAssignment().getClassificationAttribute();
        String featureName = classAttr.getName(currentLocale);
        sb.append(encodeFactFinderSpecialChars(featureName));
        if (feature.getUnitSymbol() != null) {
            String unitSymbol = feature.getUnitSymbol();
            if (unitSymbol != null)
            {
                replaceSpecialUnitCharacters(unitSymbol);
                sb.append(FACTFINDER_UNIT_PREFIX).append(encodeFactFinderSpecialChars(replaceSpecialUnitCharacters(unitSymbol)));
            }
        }
        sb.append("=");
        sb.append(encodeFactFinderSpecialChars(value));

        if (isNotBlank(enValue))
        {
            getDistClassificationService().updateClassificationAttributeValue(classAttr, value, currentLocale, enValue);
        }
        return sb;
    }

    private PimWebUseField createPimWebUseField(final CProductFeature feature, final String value, final Locale currentLocale)
    {
        ClassAttributeAssignmentModel attrAssignment = feature.getClassificationAttributeAssignment();
        ClassificationAttributeModel classAttr = attrAssignment.getClassificationAttribute();
        String code = classAttr.getCode();
        String unitSymbol = StringUtils.isNotEmpty(feature.getUnitSymbol()) ? feature.getUnitSymbol() : "";

        PimWebUseField field = new PimWebUseField();

        String name = classAttr.getName(currentLocale);
        field.setAttributeName(name);
        field.setCode(code.replaceAll(ALLOWED_CHARACTERS_FIELDNAME,"").toLowerCase());
        field.setUnit(unitSymbol);
        field.setValue(value);

        String fieldType = ClassificationAttributeTypeEnum.NUMBER.equals(attrAssignment.getAttributeType()) ?
                SolrPropertiesTypes.DOUBLE.getCode() : SolrPropertiesTypes.STRING.getCode();
        field.setFieldType(fieldType);

        return field;
    }

    private String replaceSpecialUnitCharacters(String unitSymbol)
    {
        if ("Î©".equals(unitSymbol))
        {
            return "Ohm";
        }
        return unitSymbol;
    }

    private String encodeFactFinderSpecialChars(final String input)
    {
        return input.replace("\u00BE", "3/4")
                .replace("\u00BD", "1/2")
                .replace("\u00BC", "1/4")
                .replace("\u0085", "...")
                .replace("|", "%7C")
                .replace("#", "%23")
                .replace("=", "%3D")
                .replace("\u2264", "&le;")
                .replace("≤", "&le;")
                .replace("\u2265", "&ge;")
                .replace("≥", "&ge;")
                //DISTRELEC-17974: Removing encoding as FactFinder doesn't like it.
                //.replace("ø", "&oslash;")
                .replace("&oslash;","ø")
                .replace("&Oslash;","Ø")
                .replace("\u00F8", "ø")
                //.replace("Ø", "&Oslash;")
                .replace("\u00D8", "Ø")
                .replace("⅓", "1/3")
                .replace("¾", "3/4")
                .replace("½", "1/2")
                .replace("¼", "1/4")
                .replace("±", "&plusmn;");
    }

    /**
     * Checks whether the {@code ClassAttributeAssignmentModel} is visible on the shop or not
     *
     * @param assignment
     *            the {@code ClassAttributeAssignmentModel} to check
     * @return {@code true} if the specified {@code ClassAttributeAssignmentModel} has visibility A, B, C or D, {@code false} otherwise.
     */
    protected boolean isVisible(final ClassAttributeAssignmentModel assignment) {
        return isPimWebUseVisible(assignment);
    }

    protected boolean isFFVisible(final ClassAttributeAssignmentModel assignment) {
        return assignment != null && (assignment.getVisibility() == ClassificationAttributeVisibilityEnum.A_VISIBILITY //
                || assignment.getVisibility() == ClassificationAttributeVisibilityEnum.B_VISIBILITY //
                || assignment.getVisibility() == ClassificationAttributeVisibilityEnum.C_VISIBILITY);
    }

    protected boolean isPimWebUseDfeature(final ClassAttributeAssignmentModel assignment) {
        return assignment != null && (assignment.getVisibility() == ClassificationAttributeVisibilityEnum.D_VISIBILITY);
    }

    public static boolean isPimWebUseVisible(final ClassAttributeAssignmentModel assignment) {
        return assignment != null && (assignment.getVisibility() == ClassificationAttributeVisibilityEnum.A_VISIBILITY //
                || assignment.getVisibility() == ClassificationAttributeVisibilityEnum.B_VISIBILITY //
                || assignment.getVisibility() == ClassificationAttributeVisibilityEnum.C_VISIBILITY //
                || assignment.getVisibility() == ClassificationAttributeVisibilityEnum.D_VISIBILITY);
    }

    public static boolean isEelFeature(CProductFeature feature) {
        String productFeatureCode = extractProductFeatureCode(feature);
        boolean isEelFeature = EEL_CODES.contains(productFeatureCode);
        return isEelFeature;
    }

    private static String extractProductFeatureCode(CProductFeature feature) {
        String classAttrAssExternalID = feature.getClassAttrAssExternalID();
        int dotIndex = classAttrAssExternalID.lastIndexOf("-");
        if (dotIndex > 0) {
            return classAttrAssExternalID.substring(dotIndex + 1);
        } else {
            return classAttrAssExternalID;
        }
    }

    public DistClassificationService getDistClassificationService() {
        return distClassificationService;
    }

    @Required
    public void setDistClassificationService(DistClassificationService distClassificationService) {
        this.distClassificationService = distClassificationService;
    }
}
