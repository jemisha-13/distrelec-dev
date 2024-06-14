/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.site.dao.impl;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.DistBrandModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.site.dao.DistrelecBaseSiteDao;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Test for {@link DistrelecBaseSiteDao}.
 * 
 * @author rmeier, Namics AG
 * @since Namics Extensions 1.0
 * 
 */

public class DefaultDistrelecBaseSiteDaoTest extends ServicelayerTransactionalTest {

    @Resource
    private DistrelecBaseSiteDao distrelecBaseSiteDao;

    @Resource
    private ModelService modelService;

    private CMSSiteModel cmsSite;
    private DistBrandModel brand_distrelec;
    private DistBrandModel brand_proditec;
    private CountryModel countryCh;
    private DistSalesOrgModel salesOrg_7620;
    private DistSalesOrgModel salesOrg_7310;

    private EnumerationMetaTypeModel upg;

    @Before
    public void setUp() {
        brand_distrelec = modelService.create(DistBrandModel.class);
        brand_distrelec.setCode("distrelec");
        brand_distrelec.setName("Distrelec");

        brand_proditec = modelService.create(DistBrandModel.class);
        brand_proditec.setCode("proditec");
        brand_proditec.setName("Proditec");

        countryCh = modelService.create(CountryModel.class);
        countryCh.setIsocode("CH");
        countryCh.setName("Schweiz");

        salesOrg_7620 = modelService.create(DistSalesOrgModel.class);
        salesOrg_7620.setCode("7620");
        salesOrg_7620.setBrand(brand_distrelec);
        salesOrg_7620.setName("Sweden");
        salesOrg_7620.setCountry(countryCh);

        // defaultWarehouse, userDiscountGroup, userPriceGroup]

        modelService.saveAll();

        salesOrg_7310 = modelService.create(DistSalesOrgModel.class);
        salesOrg_7310.setBrand(brand_distrelec);
        salesOrg_7310.setName("Switzerland");

        cmsSite = modelService.create(CMSSiteModel.class);
        cmsSite.setUid("test_base_site_uid");
        cmsSite.setSalesOrg(salesOrg_7310);
        modelService.saveAll();

    }

    @Ignore
    @Test
    public void testFindDistrelecBaseSiteByCountry() {
        final BaseSiteModel baseSite = this.distrelecBaseSiteDao.findBaseSiteByCountryAndBrand("CH", "distrelec");
        Assert.assertNotNull("baseSite is null", baseSite);
        Assert.assertEquals("baseSites differ", this.brand_distrelec, baseSite);
    }
}
