/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.ProductElementConverter;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimAttributeDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.ClassAttributeAssignmentTypeMap;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.PimImportInitializer;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Sets basic properties of a {@link ClassAttributeAssignmentModel}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ClassAttributeAssignmentInitializer implements PimImportInitializer<ClassAttributeAssignmentModel> {

    private static final Logger LOG = LogManager.getLogger(ClassAttributeAssignmentInitializer.class);

    @Autowired
    private ClassificationSystemService classificationSystemService;

    @Override
    public void initialize(final ClassAttributeAssignmentModel assignment, final ImportContext importContext) {
        final String externalId = assignment.getClassificationClass().getCode() + "-" + assignment.getClassificationAttribute().getCode();
        assignment.setExternalID(externalId);

        final PimAttributeDto pimAttributeDto = importContext.getAttributeDtos().get(assignment.getClassificationAttribute().getCode());
        if (pimAttributeDto == null) {
            throw new ElementConverterException(
                    "Reference to non-existing attribute with code [" + assignment.getClassificationAttribute().getCode() + "] found.");
        }

        // set the unit to the ClassificationAttributeAssignment
        if (StringUtils.isNotBlank(pimAttributeDto.getUnitCode())) {
            try {
                if (isAttributeTypeText(pimAttributeDto)) {
                    // DISTRELEC-6067: text attributes doesn't need the unit.
                    assignment.setUnit(null);
                } else {
                    assignment.setUnit(
                            classificationSystemService.getAttributeUnitForCode(importContext.getClassificationSystemVersion(), pimAttributeDto.getUnitCode()));
                }
            } catch (final UnknownIdentifierException e) {
                LOG.error("Classification attribute unit for code [{}] not found: {}", pimAttributeDto.getUnitCode(), e.getMessage());
            }
        } else {
            // DISTRELEC-6067: the unit is reset when not needed.
            assignment.setUnit(null);
        }

        final ClassificationAttributeTypeEnum classificationAttributeType = ClassAttributeAssignmentTypeMap.TYPE_TRANSLATIONS
                .get(pimAttributeDto.getAttributeTypeCode());
        if (classificationAttributeType == null) {
            throw new ElementConverterException("Could not determine attribute type for code [" + pimAttributeDto.getAttributeTypeCode()
                    + "] within classification class [" + assignment.getClassificationClass().getCode() + "]");
        }
        assignment.setAttributeType(classificationAttributeType);
        setMultiValued(assignment, pimAttributeDto);
        setCalculatedValued(assignment, pimAttributeDto);
        setEmbeddedValued(assignment, pimAttributeDto);
        // the assignment object is localized in case of it's Classification attribute is null or does not appear in the file
        assignment.setLocalized(assignment.getClassificationAttribute() == null
                || !importContext.getNonLocalizedProductFeatures().contains(assignment.getClassificationAttribute().getCode()));
    }

    private boolean isAttributeTypeText(final PimAttributeDto pimAttributeDto) {
        final String typeCode = pimAttributeDto.getAttributeTypeCode();
        return StringUtils.isBlank(typeCode) ? true : typeCode.equals(ClassAttributeAssignmentTypeMap.TEXT_PIM_TYPE);
    }

    private void setCalculatedValued(ClassAttributeAssignmentModel assignment, PimAttributeDto pimAttributeDto) {
        assignment.setPimCalculated((pimAttributeDto.getCalculated() == null ? Boolean.FALSE : pimAttributeDto.getCalculated()));
    }

    private void setEmbeddedValued(ClassAttributeAssignmentModel assignment, PimAttributeDto pimAttributeDto) {
        assignment.setPimEmbedded(pimAttributeDto.getEmbedded() == null ? Boolean.FALSE : pimAttributeDto.getEmbedded());
    }

    private void setMultiValued(final ClassAttributeAssignmentModel assignment, final PimAttributeDto pimAttributeDto) {
        if (ProductElementConverter.MULTI_LINE_ATTRIBUTE_CODES.contains(pimAttributeDto.getCode())) {
            assignment.setMultiValued(Boolean.TRUE);
        } else {
            assignment.setMultiValued(pimAttributeDto.getMultiValued());
        }
    }

    public ClassificationSystemService getClassificationSystemService() {
        return classificationSystemService;
    }

    public void setClassificationSystemService(final ClassificationSystemService classificationSystemService) {
        this.classificationSystemService = classificationSystemService;
    }

}
