/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.impl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.TestUtils;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimAttributeDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.classification.ClassificationSystemService;

/**
 * Tests the {@link ClassAttributeAssignmentInitializer} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class ClassAttributeAssignmentInitializerTest {

    private ClassAttributeAssignmentInitializer classAttributeAssignmentInitializer = new ClassAttributeAssignmentInitializer();

    private ClassAttributeAssignmentModel assignment;
    private ImportContext importContext;
    private ClassificationClassModel classificationClass;
    private ClassificationAttributeModel classificationAttribute;
    private ClassificationAttributeUnitModel classAttributeUnit;
    private PimAttributeDto attributeDto;

    @Before
    public void setUp() {
        classAttributeAssignmentInitializer = new ClassAttributeAssignmentInitializer();

        assignment = new ClassAttributeAssignmentModel();

        classificationClass = new ClassificationClassModel();
        classificationClass.setCode("ClassificationClassCode");
        assignment.setClassificationClass(classificationClass);

        classificationAttribute = new ClassificationAttributeModel();
        classificationAttribute.setCode("ClassificationAttributeCode");
        assignment.setClassificationAttribute(classificationAttribute);

        importContext = new ImportContext();

        attributeDto = new PimAttributeDto();
        attributeDto.setCode(classificationAttribute.getCode());
        attributeDto.setUnitCode("UnitCode");
        attributeDto.setAttributeTypeCode("text");
        attributeDto.setMultiValued(Boolean.FALSE);
        importContext.getAttributeDtos().put(attributeDto.getCode(), attributeDto);

        final ClassificationSystemService mockedClassificationSystemService = Mockito.mock(ClassificationSystemService.class);
        TestUtils.mockUnitForClassificationAttribute(mockedClassificationSystemService, "UnitCode", "Unit Name");
        // Mockito.when(mockedClassificationSystemService.getAttributeUnitForCode(null,
        // attributeDto.getUnitCode())).thenReturn(classAttributeUnit);
        classAttributeAssignmentInitializer.setClassificationSystemService(mockedClassificationSystemService);
    }

    @Test
    public void testInitialize() {
        // Init

        // Action
        classAttributeAssignmentInitializer.initialize(assignment, importContext);

        // Evaluation
        Assert.assertEquals(classificationClass.getCode() + "-" + classificationAttribute.getCode(), assignment.getExternalID());
        Assert.assertEquals(classAttributeUnit, assignment.getUnit());
        Assert.assertEquals(ClassificationAttributeTypeEnum.STRING, assignment.getAttributeType());
        Assert.assertEquals(Boolean.FALSE, assignment.getMultiValued());
    }

    @Test
    public void testInitializeWithoutAttributeDto() {
        // Init
        importContext.getAttributeDtos().clear();

        // Action
        try {
            classAttributeAssignmentInitializer.initialize(assignment, importContext);
            Assert.fail();
        } catch (final ElementConverterException e) {
            // Evaluation
            Assert.assertTrue(e.getMessage().startsWith("Reference to non-existing attribute with code"));
        }
    }

    @Test
    public void testInitializeUnknownType() {
        // Init
        attributeDto.setAttributeTypeCode("unknown_code");

        // Action
        try {
            classAttributeAssignmentInitializer.initialize(assignment, importContext);
            Assert.fail();
        } catch (final ElementConverterException e) {
            // Evaluation
            Assert.assertTrue(e.getMessage().startsWith("Could not determine attribute type for code"));
        }
    }

}
