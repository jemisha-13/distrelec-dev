/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.commercefacades.product.converters.populator.ProductFeatureListPopulator;
import de.hybris.platform.commercefacades.product.data.ClassificationData;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.FeatureValueData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.media.MediaService;

import static com.namics.distrelec.b2b.facades.product.Constants.DANGEROUS_SUBSTANCE_DIRECTIVE_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.DIS_HAZARDSTATE_TXT_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.DIS_SUPPHAZARDINFO_TXT_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.EFFICENCY_FEATURE_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.ENERGY_CLASSES_LUMINAR_BUILT_IN_LED_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.ENERGY_CLASSES_LUMINAR_FITTING_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.ENERGY_CLASSES_LUMINAR_INCLUDED_BULB_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.POWER_FEATURE_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.ROHS_URL_TXT_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.SVHC_URL_LOWER;
import static com.namics.distrelec.b2b.facades.product.Constants.UNSPSC_5_LOWER;

public class DistProductFeatureListPopulator<SOURCE extends FeatureList, TARGET extends ProductData> extends ProductFeatureListPopulator<SOURCE, TARGET> {

    private static final Logger LOG = LoggerFactory.getLogger(DistProductFeatureListPopulator.class);

    private static final String[] allowedTypes = {ClassificationAttributeVisibilityEnum.A_VISIBILITY.getCode(),
                                                  ClassificationAttributeVisibilityEnum.B_VISIBILITY.getCode(),
                                                  ClassificationAttributeVisibilityEnum.C_VISIBILITY.getCode(),
                                                  ClassificationAttributeVisibilityEnum.D_VISIBILITY.getCode()};

    private static final String H_CODE = "H";

    private static final String EUH_CODE = "EUH";

    private static final String GHS_CODE = "GHS";

    @Autowired
    private MediaService mediaService;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Override
    public void populate(final SOURCE source, final TARGET target) throws ConversionException {
        super.populate(source, target);

        // sort features
        if (CollectionUtils.isNotEmpty(target.getClassifications())) {
            final List<ClassificationData> sortedList = new ArrayList<>();
            for (final ClassificationData classification : target.getClassifications()) {
                filter(target, classification);
                sortedList.add(classification);
            }
            target.setClassifications(sortedList);
        }
    }

    /**
     * @param target
     * @param classification
     */
    private void filter(final ProductData target, final ClassificationData classification) {
        final List<FeatureData> features = new ArrayList<>();
        for (final FeatureData feature : classification.getFeatures()) {
            if (feature.getCode().endsWith(SVHC_URL_LOWER) && CollectionUtils.isNotEmpty(feature.getFeatureValues())) {
                target.setSvhcURL(((List<FeatureValueData>) feature.getFeatureValues()).get(0).getValue());
            } else if (feature.getCode().endsWith(ROHS_URL_TXT_LOWER) && CollectionUtils.isNotEmpty(feature.getFeatureValues())) {
                target.setRohsURL(((List<FeatureValueData>) feature.getFeatureValues()).get(0).getValue());
            } else if (feature.getCode().endsWith(UNSPSC_5_LOWER)) {
                target.setUnspsc5(((List<FeatureValueData>) feature.getFeatureValues()).get(0).getValue());
            } else if (feature.getCode().endsWith(EFFICENCY_FEATURE_LOWER)) {
                target.setEnergyEfficiency(((List<FeatureValueData>) feature.getFeatureValues()).get(0).getValue());
            } else if (feature.getCode().endsWith(POWER_FEATURE_LOWER)) {
                target.setEnergyPower(((List<FeatureValueData>) feature.getFeatureValues()).get(0).getValue());
            } else if (feature.getCode().endsWith(ENERGY_CLASSES_LUMINAR_FITTING_LOWER)) {
                target.setEnergyClassesFitting(featureValuesAsString(feature.getFeatureValues(), ";"));
            } else if (feature.getCode().endsWith(ENERGY_CLASSES_LUMINAR_INCLUDED_BULB_LOWER)) {
                target.setEnergyClassesIncludedBulb(featureValuesAsString(feature.getFeatureValues(), ";"));
            } else if (feature.getCode().endsWith(ENERGY_CLASSES_LUMINAR_BUILT_IN_LED_LOWER)) {
                target.setEnergyClassesBuiltInLed(featureValuesAsString(feature.getFeatureValues(), ";"));
            } else if (feature.getCode().endsWith(DANGEROUS_SUBSTANCE_DIRECTIVE_LOWER)) {
                final StringBuilder sb = new StringBuilder();
                final List<FeatureValueData> filteredValues = feature.getFeatureValues().stream()
                                                                     .filter(featureData -> !featureData.getValue().startsWith(GHS_CODE))
                                                                     .collect(Collectors.toList());
                sb.append(target.getDangerousSubstancesDirective() != null
                          ? target.getDangerousSubstancesDirective() + ","
                          : StringUtils.EMPTY)
                  .append(featureValuesAsString(filteredValues, ","));
                target.setDangerousSubstancesDirective(sb.toString());

                CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(DistConstants.Catalog.DEFAULT_CATALOG_ID,
                                                                                             DistConstants.CatalogVersion.ONLINE);
                List<String> ghsImageList = feature.getFeatureValues()
                                                   .stream()
                                                   .filter(featureData -> featureData.getValue().contains(GHS_CODE))
                                                   .map(featureData -> getMediaURL(catalogVersion, featureData.getValue().toLowerCase()))
                                                   .filter(Objects::nonNull)
                                                   .collect(Collectors.toList());
                target.setGhsImages(ghsImageList);
            } else if (feature.getCode().endsWith(DIS_SUPPHAZARDINFO_TXT_LOWER)) {
                final List<String> suppHazardInfoList = new ArrayList<>();
                for (final FeatureValueData featureData : feature.getFeatureValues()) {
                    if (featureData.getValue().contains("/")) {
                        final String[] parts = featureData.getValue().split("/");
                        final String part1 = parts[0].replaceAll("\\s+", "");
                        final String part2 = EUH_CODE + parts[1].replaceAll("\\s+", "");
                        suppHazardInfoList.add(part1);
                        suppHazardInfoList.add(part2);
                    } else {
                        suppHazardInfoList.add(featureData.getValue().replaceAll("\\s+", ""));
                    }
                }
                target.setSupplementalHazardInfos(suppHazardInfoList);
            } else if (feature.getCode().endsWith(DIS_HAZARDSTATE_TXT_LOWER)) {
                final List<String> hazardStatements = new ArrayList<>();
                for (final FeatureValueData featureData : feature.getFeatureValues()) {
                    if (featureData.getValue().contains("/")) {
                        final String[] parts = featureData.getValue().split("/");
                        final String part1 = parts[0].replaceAll("\\s+", "");
                        final String part2 = H_CODE + parts[1].replaceAll("\\s+", "");
                        hazardStatements.add(part1);
                        hazardStatements.add(part2);
                    } else {
                        hazardStatements.add(featureData.getValue());
                    }
                }
                target.setHazardStatements(hazardStatements);
            }

            if (ArrayUtils.contains(allowedTypes, feature.getVisibility())) {
                features.add(feature);
            }
        }

        sortFeatures(features);
        classification.setFeatures(features);
    }

    protected String getMediaURL(CatalogVersionModel catalogVersion, String code) {
        try {
            MediaModel media = mediaService.getMedia(catalogVersion, code);
            return media.getURL();
        } catch (Exception ex) {
            LOG.error("Error while loading media!", ex);
            return null;
        }
    }

    /**
     * If the collection of {@link FeatureValueData} is not empty, then build a string from the element's values separated by ','
     *
     * @param featureValues the collection of {@link FeatureValueData}.
     */
    protected String featureValuesAsString(final Collection<FeatureValueData> featureValues, final String separator) {
        if (featureValues == null || featureValues.isEmpty()) {
            return StringUtils.EMPTY;
        }
        final StringBuilder sb = new StringBuilder();
        for (final FeatureValueData value : featureValues) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append(value.getValue());
        }
        return sb.toString();
    }

    /**
     * Sort the product features.
     *
     * @param features
     */
    protected void sortFeatures(final List<FeatureData> features) {
        Collections.sort(features, new Comparator<>() {
            @Override
            public int compare(final FeatureData feature1, final FeatureData feature2) {
                int returnValue = 0;
                final String f1 = feature1.getVisibility();
                final String f2 = feature2.getVisibility();
                if (StringUtils.equals(f1, f2)) {
                    final Integer n1 = feature1.getPosition();
                    final Integer n2 = feature2.getPosition();
                    returnValue = ObjectUtils.compare(n1, n2, true);
                } else {
                    returnValue = ObjectUtils.compare(f1, f2, true);
                }

                if (returnValue == 0) {
                    int fp1 = feature1.getFeaturePosition();
                    int fp2 = feature2.getFeaturePosition();
                    returnValue = ObjectUtils.compare(fp1, fp2);
                }
                return returnValue;
            }
        });
    }
}
