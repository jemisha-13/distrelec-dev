/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.DistBrandModel;
import com.namics.distrelec.b2b.core.service.codelist.dao.DistrelecCodelistDao;
import com.namics.distrelec.b2b.core.service.exception.NotFoundException;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for {@link DefaultDistrelecCodelistDao}.
 * 
 * @author daehusir, Distrelec
 * 
 */
public class DefaultDistrelecCodelistDaoTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private DistrelecCodelistDao distrelecCodelistDao;

    @Test(expected = NotFoundException.class)
    public void testGetCodelistEntryForCodeNotExists() throws Exception {
        distrelecCodelistDao.getDistrelecCodelistEntry("codeNotExists", DistBrandModel._TYPECODE);
    }

    @Test
    public void testGetCodelistEntryForCodeExists() throws Exception {
        final DistBrandModel distBrand = modelService.create(DistBrandModel.class);
        distBrand.setCode("test");
        distBrand.setName("Test Brand");
        modelService.save(distBrand);

        Assert.assertNotNull(distrelecCodelistDao.getDistrelecCodelistEntry("test", DistBrandModel._TYPECODE));
    }

    @Test
    public void testGetAllCodelistBrandEntries() throws Exception {
        final DistBrandModel distBrand1 = modelService.create(DistBrandModel.class);
        distBrand1.setCode("test1");
        distBrand1.setName("Test Brand 1");
        modelService.save(distBrand1);

        final DistBrandModel distBrand2 = modelService.create(DistBrandModel.class);
        distBrand2.setCode("test2");
        distBrand2.setName("Test Brand 2");
        modelService.save(distBrand2);

        Assert.assertNotNull(distrelecCodelistDao.getAllCodelistEntries(DistBrandModel._TYPECODE));
        Assert.assertEquals(2, distrelecCodelistDao.getAllCodelistEntries(DistBrandModel._TYPECODE).size());
    }

    @Test
    public void testInsertOrUpdateCodelist() throws Exception {
        final DistBrandModel distBrand1 = modelService.create(DistBrandModel.class);
        distBrand1.setCode("test1");
        distBrand1.setName("Test Brand 1");
        modelService.save(distBrand1);

        final DistBrandModel distBrand2 = modelService.create(DistBrandModel.class);
        distBrand2.setCode("test2");
        distBrand2.setName("Test Brand 2");
        modelService.save(distBrand2);

        final List<DistBrandModel> codeListEntryList = new ArrayList<DistBrandModel>();
        codeListEntryList.add(distBrand1);
        codeListEntryList.add(distBrand2);

        distrelecCodelistDao.insertOrUpdateDistrelecCodelistEntry(codeListEntryList);
    }
}
