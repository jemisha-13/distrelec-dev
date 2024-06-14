/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.service.classification.DistFeatureValueConversionService;
import com.namics.distrelec.b2b.core.service.classification.features.DistLocalizedFeature;
import com.namics.distrelec.b2b.core.service.classification.features.DistUnlocalizedFeature;
import com.namics.distrelec.b2b.facades.product.data.DistFeatureValueData;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.commercefacades.product.converters.populator.FeaturePopulator;
import de.hybris.platform.commercefacades.product.data.FeatureData;
import de.hybris.platform.commercefacades.product.data.FeatureUnitData;
import de.hybris.platform.commercefacades.product.data.FeatureValueData;

/**
 * Converter for ClassificationFeatures.
 *
 * @author sivakumaran, Namics AG
 * @since Namics Extensions 1.0
 */
public class DistFeaturePopulator extends FeaturePopulator {

    private static final int BIG_DECIMAL_SCALE = 12;

    @Autowired
    private DistFeatureValueConversionService featureConversionService;

    @Override
    public void populate(final Feature source, final FeatureData target) {
        // PRE-Populate - replace super.populate(source, target)
        prePopulate(source, target);

        final ClassificationAttributeUnitModel unit = source.getValue().getUnit();
        if (unit != null) {
            final FeatureUnitData featureUnitData = new FeatureUnitData();
            featureUnitData.setName(unit.getName());
            featureUnitData.setSymbol(unit.getSymbol());
            featureUnitData.setUnitType(unit.getUnitType());
            target.setFeatureUnit(featureUnitData);
        } else {
            target.setFeatureUnit(null);
        }

        // DISTRELEC-6323 taking care of the multiple values and multiple units
        if (CollectionUtils.isNotEmpty(target.getFeatureValues())) {
            final Map<FeatureValueData, FeatureUnitData> featureValuesWithUnit = populateValuesWithUnit(source, target);
            target.setFeatureValuesWithUnit(featureValuesWithUnit);
        }

        final ClassAttributeAssignmentModel classAttributeAssignment = source.getClassAttributeAssignment();
        target.setPosition(classAttributeAssignment.getPosition());
        if (classAttributeAssignment.getVisibility() != null) {
            target.setVisibility(classAttributeAssignment.getVisibility().getCode());
            if (ClassificationAttributeVisibilityEnum.A_VISIBILITY.equals(classAttributeAssignment.getVisibility())) {
                target.setSearchable(Boolean.TRUE);
            } else {
                target.setSearchable(Boolean.FALSE);
            }
        }
        if (source instanceof DistLocalizedFeature) {
            DistLocalizedFeature sourceLocalizedFeature = (DistLocalizedFeature) source;
            target.setFeaturePosition(sourceLocalizedFeature.getFeaturePosition());
        } else if (source instanceof DistUnlocalizedFeature) {
            DistUnlocalizedFeature sourceUnlocalizedFeature = (DistUnlocalizedFeature) source;
            target.setFeaturePosition(sourceUnlocalizedFeature.getFeaturePosition());
        }
    }

    /**
     * This method is doing exactly the same as super.populate(source, target); except that it uses {@code DistFeatureValueData} instead of
     * {@code FeatureValueData}
     *
     * @param source
     * @param target
     */
    private void prePopulate(final Feature source, final FeatureData target) {
        final ClassAttributeAssignmentModel classAttributeAssignment = source.getClassAttributeAssignment();

        // Create the feature
        target.setCode(source.getCode());
        target.setComparable(Boolean.TRUE.equals(classAttributeAssignment.getComparable()));
        target.setDescription(classAttributeAssignment.getDescription());
        target.setName(source.getName());
        target.setRange(Boolean.TRUE.equals(classAttributeAssignment.getRange()));
        target.setType(classAttributeAssignment.getAttributeType() != null ? classAttributeAssignment.getAttributeType().getCode() : null);

        final ClassificationAttributeUnitModel unit = classAttributeAssignment.getUnit();
        if (unit != null) {
            final FeatureUnitData featureUnitData = new FeatureUnitData();
            featureUnitData.setName(unit.getName());
            featureUnitData.setSymbol(unit.getSymbol());
            featureUnitData.setUnitType(unit.getUnitType());
            target.setFeatureUnit(featureUnitData);
        }

        // Create the feature data items
        final List<FeatureValueData> featureValueDataList = new ArrayList<>();
        for (final FeatureValue featureValue : source.getValues()) {
            final FeatureValueData featureValueData = new DistFeatureValueData();
            final Object value = featureValue.getValue();
            if (value instanceof ClassificationAttributeValueModel) {
                featureValueData.setValue(((ClassificationAttributeValueModel) value).getName());
            } else {
                String stringValue = featureConversionService.toString(featureValue);
                featureValueData.setValue(stringValue);
            }

            if (featureValue.getUnit() != null
                && ClassificationAttributeTypeEnum.NUMBER.equals(classAttributeAssignment.getAttributeType())) {
                featureValueData.setBaseUnitValue(createBaseUnitValue(featureValue.getUnit(), featureValueData.getValue()));
            }

            featureValueDataList.add(featureValueData);
        }
        target.setFeatureValues(featureValueDataList);
    }

    /**
     * use jackson serialization on a number to use scientific notation if needed, same as on indexing export.
     */
    private String createBaseUnitValue(ClassificationAttributeUnitModel unit, String quantity) {
        BigDecimal baseUnitValue = convertToBaseUnit(unit, quantity);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(baseUnitValue).toLowerCase();
        } catch (JsonProcessingException e) {
            return baseUnitValue.toPlainString();
        }
    }

    private BigDecimal convertToBaseUnit(ClassificationAttributeUnitModel unit, String quantity) {
        if (isInvalidInputForConversion(unit, quantity)) {
            return BigDecimal.ZERO;
        }
        BigDecimal quantityNumber = new BigDecimal(quantity)
                                      .multiply(BigDecimal.valueOf(unit.getConversionFactor())).setScale(BIG_DECIMAL_SCALE, RoundingMode.HALF_UP)
                                      .stripTrailingZeros();
        return quantityNumber;
    }

    private boolean isInvalidInputForConversion(ClassificationAttributeUnitModel unit, String quantity) {
        return !(isValidUnitForConversion(unit) && isValidQuantityForConversion(quantity));
    }

    private boolean isValidUnitForConversion(ClassificationAttributeUnitModel unit) {
        return unit != null && unit.getConversionFactor() != null;
    }

    private boolean isValidQuantityForConversion(String quantity) {
        return NumberUtils.isCreatable(quantity);
    }

    /**
     * This methods returns a map of value,unit to the frontend. <br>
     * In case of multiple value feature, each value will keep the right unit
     *
     * @param source
     * @param target
     * @return Map<FeatureValueData, FeatureUnitData>
     */
    private Map<FeatureValueData, FeatureUnitData> populateValuesWithUnit(final Feature source, final FeatureData target) {
        final Map<FeatureValueData, FeatureUnitData> featureValuesWithUnit = new TreeMap<>();

        // looping on all the already converted values
        for (final FeatureValueData fv : target.getFeatureValues()) {
            for (final FeatureValue value : source.getValues()) {
                featureValuesWithUnit.entrySet();
                // identifying the right unit for each value.
                String stringValue = featureConversionService.toString(value);
                if (stringValue.equals(fv.getValue())) {
                    if (value.getUnit() != null) {
                        final FeatureUnitData u = new FeatureUnitData();
                        u.setName(value.getUnit().getName());
                        u.setSymbol(value.getUnit().getSymbol());
                        u.setUnitType(value.getUnit().getUnitType());
                        featureValuesWithUnit.put(fv, u);
                    } else {

                        // in this case the value has no unit.
                        featureValuesWithUnit.put(fv, null);
                    }
                }
            }

        }
        return featureValuesWithUnit;
    }
}
