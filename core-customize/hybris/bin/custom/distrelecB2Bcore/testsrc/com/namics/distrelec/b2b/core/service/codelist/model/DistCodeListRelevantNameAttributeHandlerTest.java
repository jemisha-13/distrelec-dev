/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.model;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.DistBrandModel;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;

import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for extends {@link DistCodeListRelevantNameAttributeHandler}.
 * 
 * @author daehusir, Distrelec
 * 
 */
public class DistCodeListRelevantNameAttributeHandlerTest extends ServicelayerTransactionalTest {

    // Non ERP Model
    private DistBrandModel brandModel;

    // ERP Model
    private DistSalesUnitModel salesUnitModelWithName;
    private DistSalesUnitModel salesUnitModelWithoutName;

    @Resource
    private ModelService modelService;

    @Resource
    private UnitService unitService;

    @Before
    public void setUp() {
        brandModel = modelService.create(DistBrandModel.class);
        brandModel.setCode("testBrand");
        brandModel.setName("Test Brand");
        modelService.save(brandModel);

        UnitModel unit = null;
        try {
            unit = unitService.getUnitForCode("pieces");
        } catch (final UnknownIdentifierException e) {
            unit = modelService.create(UnitModel.class);
            unit.setCode("pieces");
            unit.setName("Pieces");
            unit.setUnitType("pieces");
            modelService.save(unit);
        } catch (final AmbiguousIdentifierException e) {
            Assert.fail("More than one unit found with code pieces!");
        }

        salesUnitModelWithName = modelService.create(DistSalesUnitModel.class);
        salesUnitModelWithName.setCode("testUnit1");
        salesUnitModelWithName.setName("Test Unit 1");
        salesUnitModelWithName.setNameErp("Test Unit Erp 1");
        salesUnitModelWithName.setAmount(Double.valueOf(0));
        salesUnitModelWithName.setUnit(unit);
        modelService.save(salesUnitModelWithName);

        salesUnitModelWithoutName = modelService.create(DistSalesUnitModel.class);
        salesUnitModelWithoutName.setCode("testUnit2");
        salesUnitModelWithoutName.setNameErp("Test Unit Erp 2");
        salesUnitModelWithoutName.setAmount(Double.valueOf(0));
        salesUnitModelWithoutName.setUnit(unit);
        modelService.save(salesUnitModelWithoutName);
    }

    @Test
    public void getRelevantNameOfDistCodeList() {
        Assert.assertEquals("Test Brand", brandModel.getRelevantName());
    }

    @Test
    public void getRelevantNameOfDistCodeListErpWithFilledNameAttribute() {
        Assert.assertEquals("Test Unit 1", salesUnitModelWithName.getRelevantName());
    }

    @Test
    public void getRelevantNameOfDistCodeListErpWithEmptyNameAttribute() {
        Assert.assertEquals("Test Unit Erp 2", salesUnitModelWithoutName.getRelevantName());
    }
}
