/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.product.dao.DistProductDao;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Test product punchout filters.
 * 
 * @author dsivakumaran, Namics AG
 * @since Distrelc 1.0
 */
@IntegrationTest
public class DefaultDistProductDaoTest extends ServicelayerTransactionalTest {

    @Resource
    private DistProductDao productDao;

    @Resource
    private ProductService productService;

    @Resource
    private DistrelecCodelistService distCodelistService;

    @Resource
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Resource
    private CommonI18NService commonI18NService;

    private B2BUnitModel b2cUnit;
    private B2BUnitModel b2bUnit;

    private DistSalesOrgModel soCH;
    private DistSalesOrgModel soAT;
    private CountryModel countryCH;
    private CountryModel countryAT;
    private final Date today = new Date();

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        importStream(DistrelecCodelistService.class.getResourceAsStream("/distrelecB2Bcore/test/testErpCodelist.impex"), "UTF-8", null);
        importStream(DistrelecCodelistService.class.getResourceAsStream("/distrelecB2Bcore/test/punchOut/testPunchOutFilters.impex"), "UTF-8", null);

        b2cUnit = b2bUnitService.getUnitForUid("b2c_unit");
        b2bUnit = b2bUnitService.getUnitForUid("b2b_unit");
        soCH = distCodelistService.getDistrelecSalesOrg("7310");
        soAT = distCodelistService.getDistrelecSalesOrg("7320");
        countryCH = commonI18NService.getCountry("CH");
        countryAT = commonI18NService.getCountry("AT");
    }

    @Test
    public void testNoPunchOutFilters() {
        final ProductModel product = productService.getProductForCode("9301491_sample");
        List<PunchoutFilterResult> punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2cUnit, SiteChannel.B2C, countryCH, product, today);
        assertEquals("Punchout filter list should be empty!", ListUtils.EMPTY_LIST, punchOutFilterResult);
        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2bUnit, SiteChannel.B2B, countryCH, product, today);
        assertEquals("Punchout filter list should be empty!", ListUtils.EMPTY_LIST, punchOutFilterResult);
    }

    @Test
    public void testFindPunchOutFiltersForSingleProductB2C() {
        final ProductModel product = productService.getProductForCode("2910243_sample");
        List<PunchoutFilterResult> punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2cUnit, SiteChannel.B2C, countryCH, product, today);
        assertEquals("Punchout filter list should contain 1 entry!", 1, punchOutFilterResult.size());
        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2bUnit, SiteChannel.B2B, countryCH, product, today);
        assertEquals("Punchout filter list should be empty!", ListUtils.EMPTY_LIST, punchOutFilterResult);
    }

    @Test
    public void testFindPunchOutFiltersForSingleProductB2B() {
        final ProductModel product = productService.getProductForCode("8706616_sample");
        List<PunchoutFilterResult> punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2cUnit, SiteChannel.B2C, countryAT, product, today);
        assertEquals("Punchout filter list should be empty!", ListUtils.EMPTY_LIST, punchOutFilterResult);
        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2bUnit, SiteChannel.B2B, countryCH, product, today);
        assertEquals("Punchout filter list should contain 2 entries!", 2, punchOutFilterResult.size());
    }

    @Test
    public void testInactiveFilter() {
        ProductModel product = productService.getProductForCode("8500027_sample");
        List<PunchoutFilterResult> punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2cUnit, SiteChannel.B2C, countryCH, product, today);
        assertEquals("Punchout filter list should be empty!", ListUtils.EMPTY_LIST, punchOutFilterResult);

        product = productService.getProductForCode("8500035_sample");
        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2bUnit, SiteChannel.B2B, countryCH, product, today);
        assertEquals("Punchout filter list should be empty!", ListUtils.EMPTY_LIST, punchOutFilterResult);
    }

    @Test
    public void testFindPunchOutFiltersForListOfProducts() {
        final List<ProductModel> products = new ArrayList<ProductModel>();
        products.add(productService.getProductForCode("8500050_sample"));
        products.add(productService.getProductForCode("9301490_sample"));
        List<PunchoutFilterResult> punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2cUnit, SiteChannel.B2C, countryCH, products, today);
        assertEquals("Punchout filter list should contain 1 entry!", 1, punchOutFilterResult.size());

        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2bUnit, SiteChannel.B2B, countryCH, products, today);
        assertEquals("Punchout filter list should contain 2 entries!", 2, punchOutFilterResult.size());
    }

    @Test
    public void testCustomerTypePunchouts() {
        ProductModel product = productService.getProductForCode("9301490_sample");
        List<PunchoutFilterResult> punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2bUnit, SiteChannel.B2B, countryCH, product, today);
        assertEquals("Punchout filter list should contain 1 entry!", 1, punchOutFilterResult.size());

        product = productService.getProductForCode("9301491_sample");
        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2cUnit, SiteChannel.B2C, countryCH, product, today);
        assertEquals("Punchout filter list should contain 0 entries!", 0, punchOutFilterResult.size());

        product = productService.getProductForCode("8500027_sample");
        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2bUnit, SiteChannel.B2B, countryCH, product, today);
        assertEquals("Punchout filter list should contain 0 entries!", 0, punchOutFilterResult.size());

        product = productService.getProductForCode("4289013_sample");
        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2cUnit, SiteChannel.B2C, countryCH, product, today);
        assertEquals("Punchout filter list should contain 0 entries!", 0, punchOutFilterResult.size());
    }

    @Test
    public void testCountryPunchouts() {
        ProductModel product = productService.getProductForCode("8500027_sample");
        List<PunchoutFilterResult> punchOutFilterResult = productDao.findPunchOutFilters(soAT, b2bUnit, SiteChannel.B2B, countryCH, product, today);
        assertEquals("Punchout filter list should contain 0 entries!", 0, punchOutFilterResult.size());

        product = productService.getProductForCode("8706616_sample");
        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2cUnit, SiteChannel.B2C, countryCH, product, today);
        assertEquals("Punchout filter list should contain 1 entry!", 1, punchOutFilterResult.size());

        product = productService.getProductForCode("1351902_sample");
        punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2cUnit, SiteChannel.B2C, countryCH, product, today);
        assertEquals("Punchout filter list should contain 0 entries!", 0, punchOutFilterResult.size());
    }

    @Test
    public void testPunchoutsWithMultipleProducts() {
        final List<ProductModel> products = Arrays.asList( //
                productService.getProductForCode("1235423_sample"), //
                productService.getProductForCode("3248927_sample"), //
                productService.getProductForCode("1342351_sample"), //
                productService.getProductForCode("2322312_sample"), //
                productService.getProductForCode("2356342_sample"), //
                productService.getProductForCode("2323819_sample")  //
                );
        final List<PunchoutFilterResult> punchOutFilterResult = productDao.findPunchOutFilters(soCH, b2bUnit, SiteChannel.B2B, countryCH, products, today);
        assertEquals("Punchout filter list should contain 6 entries!", 6, punchOutFilterResult.size());
    }

}
