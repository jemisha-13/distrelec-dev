/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Test;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test class for <code>DistManufacturerUrlIdPrepareInterceptor</code>.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistManufacturerUrlIdPrepareInterceptorTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Test
    public void testWithGoodManufacturerCode() {
        final DistManufacturerModel distManufacturer = modelService.create(DistManufacturerModel.class);
        distManufacturer.setCode("man_test1");
        distManufacturer.setName("manufacturer test 1");
        modelService.save(distManufacturer);

        Assert.assertEquals("test1", distManufacturer.getUrlId());
    }

    @Test
    public void testWithBadManufacturerCode() {
        final DistManufacturerModel distManufacturer = modelService.create(DistManufacturerModel.class);
        distManufacturer.setCode("asdf_test1");
        distManufacturer.setName("asdf test 1");
        modelService.save(distManufacturer);

        Assert.assertEquals("asdf_test1", distManufacturer.getUrlId());
    }
}
