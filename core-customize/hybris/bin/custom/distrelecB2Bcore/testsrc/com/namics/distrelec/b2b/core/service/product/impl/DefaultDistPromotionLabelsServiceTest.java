/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.impl;

import static junit.framework.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.lang.BooleanUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.core.model.DistErpPriceConditionTypeModel;
import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.product.ProductCountryService;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.util.PriceValue;

/**
 * Test class for {@link DefaultDistPromotionLabelsService}.
 * 
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 */
@IntegrationTest
public class DefaultDistPromotionLabelsServiceTest extends ServicelayerTransactionalTest {

    @InjectMocks
    private final DefaultDistPromotionLabelsService distPromotionLabelsService = new DefaultDistPromotionLabelsService();

    @Mock
    private final DefaultDistCommercePriceService commercePriceService = new DefaultDistCommercePriceService();

    @Resource
    private FlexibleSearchService flexibleSearchService;

    @Resource
    private ProductService productService;

    @Resource
    private UnitService unitService;

    @Resource
    private DistrelecCodelistService distCodelistService;

    @Resource
    private DistSalesOrgProductService distSalesOrgProductService;

    @Resource
    private ProductCountryService productCountryService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private EnumerationService enumerationService;

    @Resource
    private SessionService sessionService;

    private DistSalesOrgModel salesOrgCH;
    private DistSalesOrgModel salesOrgAT;
    private CountryModel countryCH;
    private CountryModel countryAT;

    @Before
    public void setUp() throws Exception {
        super.createCoreData();
        super.createDefaultCatalog();

        importStream(DefaultDistPromotionLabelsServiceTest.class.getResourceAsStream("/distrelecB2Bcore/test/testErpCodelist.impex"), "UTF-8", null);
        importStream(DefaultDistPromotionLabelsServiceTest.class.getResourceAsStream("/distrelecB2Bcore/test/testDistPromotionLabels.impex"), "UTF-8", null);

        salesOrgCH = distCodelistService.getDistrelecSalesOrg("7310");
        salesOrgAT = distCodelistService.getDistrelecSalesOrg("7320");
        countryCH = commonI18NService.getCountry("CH");
        countryAT = commonI18NService.getCountry("AT");

        distPromotionLabelsService.setSessionService(sessionService);
        distPromotionLabelsService.setFlexibleSearchService(flexibleSearchService);
        distPromotionLabelsService.setModelService(getApplicationContext().getBean("modelService", ModelService.class));

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testWrongSalesOrg() {
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCodeAndSalesOrg("8500035_sample", salesOrgAT, countryCH);
        assertEquals("Should be empty", 0, promotions.size());
    }

    @Test
    public void testUsedLabel() {
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCode("2352138_sample");
        assertEquals("Should contain 1 item", 1, promotions.size());
        assertEquals("Label should be \"used\"", distCodelistService.getDistrelecPromotionLabel("used"), promotions.iterator().next());
    }

    @Test
    public void testNoPromotions() {
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCode("8500050_sample");
        assertEquals("Should be empty", 0, promotions.size());
    }

    @Test
    public void testSalesOrgPromotion() {
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCode("9301491_sample");
        assertEquals("Should contain 1 item", 1, promotions.size());
        assertEquals("Label should be \"bestseller\"", distCodelistService.getDistrelecPromotionLabel("bestseller"), promotions.iterator().next());
    }

    @Test
    public void testNewLabel() {
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCode("8500035_sample");
        assertEquals("Should contain 1 item", 1, promotions.size());
        assertEquals("Label should be \"new\"", distCodelistService.getDistrelecPromotionLabel("new"), promotions.iterator().next());
    }

    @Test
    public void testNewAndBestseller() {
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCode("8500027_sample");
        assertEquals("Should contain 2 items", 2, promotions.size());
    }

    @Test
    public void testWithWrongCountry() {
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCodeAndSalesOrg("8500043_sample", salesOrgAT, countryAT);
        assertEquals("Should be empty", 0, promotions.size());
    }

    @Test
    public void testProductCountryPromotions() {
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCode("8500043_sample", Boolean.FALSE);
        System.out.println(promotions);
        assertEquals("Should contain 2 items", 2, promotions.size());
    }

    @Test
    public void testSalesOrgAndCountryPromotions() {
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCode("8706616_sample", Boolean.TRUE);
        assertEquals("Should contain 6 items", 6, promotions.size());
    }

    @Test
    public void testOfferLabel() {
        // wrong country, therefore should just return offer
        final List<DistPromotionLabelModel> promotions = getPromotionsForProductCodeAndSalesOrg("8500043_sample", salesOrgAT, countryAT, Boolean.TRUE);
        assertEquals("Should contain 1 item", 1, promotions.size());
    }

    protected void mockWebPrice(final Boolean useSpecialPrice, final ProductModel product) {
        final ModelService modelService = SpringUtil.getBean("modelService", ModelService.class);
        DistPriceRowModel priceRow = null;
        if (BooleanUtils.isTrue(useSpecialPrice)) {
            priceRow = modelService.create(DistPriceRowModel.class);
            final DistErpPriceConditionTypeModel specialPriceConditionType = modelService.create(DistErpPriceConditionTypeModel.class);
            specialPriceConditionType.setCode("ZN00");
            specialPriceConditionType.setPriority(Integer.valueOf(1));

            priceRow.setErpPriceConditionType(specialPriceConditionType);
            priceRow.setMinqtd(Long.valueOf(1));
            priceRow.setPrice(Double.valueOf(5.0));
            priceRow.setUnitFactor(Integer.valueOf(1));
            priceRow.setUnit(unitService.getUnitForCode("PC"));
            priceRow.setCurrency(commonI18NService.getCurrency("CHF"));
            priceRow.setUg(enumerationService.getEnumerationValue(UserPriceGroup.class, "SalesOrg_UPG_7310_M01"));
            modelService.save(priceRow);
        }
        final Map qualifiers = new HashMap();
        qualifiers.put("pricerow", priceRow != null ? modelService.getSource(priceRow) : null);
        final PriceInformation priceInfo = new PriceInformation(qualifiers, new PriceValue("CHF", 5.0, true));
        Mockito.when(commercePriceService.getWebPriceForProduct(product, false)).thenReturn(priceInfo);
    }

    protected List<DistPromotionLabelModel> getPromotionsForProductCode(final String productCode) {
        return getPromotionsForProductCodeAndSalesOrg(productCode, salesOrgCH, countryCH);
    }

    protected List<DistPromotionLabelModel> getPromotionsForProductCode(final String productCode, final Boolean isSpecialPrice) {
        return getPromotionsForProductCodeAndSalesOrg(productCode, salesOrgCH, countryCH, isSpecialPrice);
    }

    protected List<DistPromotionLabelModel> getPromotionsForProductCodeAndSalesOrg(final String productCode, final DistSalesOrgModel salesOrg,
            final CountryModel country) {
        return getPromotionsForProductCodeAndSalesOrg(productCode, salesOrg, country, null);
    }

    protected List<DistPromotionLabelModel> getPromotionsForProductCodeAndSalesOrg(final String productCode, final DistSalesOrgModel salesOrg,
            final CountryModel country, final Boolean ispecialPrice) {
        final ProductModel product = productService.getProductForCode(productCode);
        mockWebPrice(ispecialPrice, product);
        final DistSalesOrgProductModel salesOrgProduct = distSalesOrgProductService.getSalesOrgProduct(product, salesOrg);
        final ProductCountryModel productCountry = productCountryService.getProductCountry(country, product);
        return distPromotionLabelsService.getActivePromotionLabels(product, salesOrgProduct, productCountry);
    }
}
