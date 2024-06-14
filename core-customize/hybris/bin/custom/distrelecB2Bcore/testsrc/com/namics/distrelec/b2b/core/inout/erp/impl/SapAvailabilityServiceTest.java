/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import com.distrelec.webservice.sap.v1.AvailabilityRequest;
import com.distrelec.webservice.sap.v1.AvailabilityResponse;
import com.distrelec.webservice.sap.v1.ErpArticleAvailability;
import com.distrelec.webservice.sap.v1.ObjectFactory;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.distrelec.webservice.sap.v1.StockLevels;
import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.model.PickupStockLevelExtModel;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.product.model.StockLevelData;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.TestCache;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.internal.converter.impl.DefaultLocaleProvider;
import de.hybris.platform.servicelayer.internal.model.impl.LocaleProvider;
import de.hybris.platform.servicelayer.model.ItemModelContextImpl;
import de.hybris.platform.stock.StockService;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link SapAvailabilityService} class using mocks for the SAP PI system.
 *
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class SapAvailabilityServiceTest extends ServicelayerTransactionalTest {

    @Mock
    private SIHybrisV1Out webServiceClient;
    @Mock
    private DistSalesOrgService distSalesOrgService;
    @Mock
    private CMSSiteService cmsSiteService;
    @Mock
    private I18NService i18nService;
    @Mock
    private LocaleProvider localeProvider;
    @Mock
    private DistProductService productService;
    @Mock
    private StockService stockService;
    @Mock
    private ObjectFactory sapObjectFactory;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private ConfigurationService configurationService;
    @InjectMocks
    private final SapAvailabilityService sapAvailabilityService = new SapAvailabilityService();
    @Mock
    private AvailabilityService movexAvailabilityService;

    private Cache testCache = new TestCache();

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);

        when(cacheManager.getCache(anyString()))
                .thenReturn(testCache);

        localeProvider = new DefaultLocaleProvider(i18nService);
        when(localeProvider.getCurrentDataLocale()).thenReturn(new Locale("en"));
        when(i18nService.getBestMatchingLocale(new Locale("en"))).thenReturn(new Locale("en"));

        sapAvailabilityService.setMovexAvailabilityService(movexAvailabilityService);

        sapAvailabilityService.setProductService(productService);
        when(productService.getProductForCode(any(String.class))).thenReturn(new ProductModel());

        final StockLevelModel backorderStock = new StockLevelModel();
        sapAvailabilityService.setStockService(stockService);
        when(stockService.getStockLevels(any(ProductModel.class), any(Collection.class))).thenReturn(Collections.singleton(backorderStock));

        sapAvailabilityService.setSapObjectFactory(sapObjectFactory);

        // init mock response for getCurrentSalesOrg
        final DistSalesOrgModel mockedDistSalesOrgModel = new DistSalesOrgModel();
        mockedDistSalesOrgModel.setCode("7310");
        mockedDistSalesOrgModel.setErpSystem(DistErpSystem.SAP);
        // mockedDistSalesOrgModel.setLocaleProvider(localeProvider);
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(mockedDistSalesOrgModel);

        final CMSSiteModel mockedCurrentSite = new CMSSiteModel();

        ItemModelContextImpl itemModelContext = (ItemModelContextImpl)mockedCurrentSite.getItemModelContext();
        itemModelContext.setLocaleProvider(localeProvider);

        // mockedCurrentSite.setLocaleProvider(localeProvider);
        mockedCurrentSite.setUid("distrelec_CH");
        mockedCurrentSite.setActive(Boolean.TRUE);
        mockedCurrentSite.setFastDeliveryTime("24h");
        mockedCurrentSite.setSlowDeliveryTime("72h");
        mockedCurrentSite.setBackorderDeliveryTime("> 72h");
        mockedCurrentSite.setExclusiveFastDeliveryTime("24h");
        mockedCurrentSite.setExclusiveSlowDeliveryTime("48h");
        mockedCurrentSite.setExternalFastDeliveryTime("24h");
        mockedCurrentSite.setExternalSlowDeliveryTime("48h");
        mockedCurrentSite.setSalesOrg(mockedDistSalesOrgModel);

        when(cmsSiteService.getCurrentSite()).thenReturn(mockedCurrentSite);

        when(sapObjectFactory.createAvailabilityRequest())
                .thenReturn(new AvailabilityRequest());

        Configuration mockConfiguration = mock(Configuration.class);

        when(mockConfiguration.getString(anyString(), anyString()))
                .thenReturn("");

        when(configurationService.getConfiguration())
                .thenReturn(mockConfiguration);
    }

    @After
    public void afterTest(){
        testCache.clear();
    }

    /*
     * three "normal" articles in two default warehouses, two alternative warehouses and two pickup warehouses
     */
    @Test
    public void testNormalArticlesAllWarehouses() throws Exception {
        // init mocked webServiceClient
        final AvailabilityResponse mockedAvailabilityResponse = initializeMockedWebServiceClientForNormalArticlesAllWarehouses();
        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // init call arguments
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("1001");
        productCodes.add("1002");
        productCodes.add("1003");

        DistSalesStatusModel salesStatus = createSalesStatus(true);
        when(productService.getProductSalesStatusModel(any(ProductModel.class))).thenReturn(salesStatus);

        // action
        final List<ProductAvailabilityExtModel> availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation
        assertEquals("size of returned availabilities is wrong", 3, availabilities.size());
        evaluateResultForNormalArticlesAllWarehouses(availabilities);
    }

    private AvailabilityResponse initializeMockedWebServiceClientForNormalArticlesAllWarehouses() {
        final AvailabilityResponse mockedAvailabilityResponse = new AvailabilityResponse();
        final List<ErpArticleAvailability> erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();

        ErpArticleAvailability availability = buildNewArticleFoundAvailability("1001");
        List<StockLevels> stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", 200));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("102", 20));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("111", 300));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("112", 30));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("121", 400));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("122", 40));
        erpArticleAvailability.add(availability);

        availability = buildNewArticleFoundAvailability("1002");
        stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", 20000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("102", 2000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("111", 30000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("112", 3000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("121", 40000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("122", 4000));
        erpArticleAvailability.add(availability);

        availability = buildNewArticleFoundAvailability("1003");
        stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", 2000000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("102", 200000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("111", 3000000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("112", 300000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("121", 4000000));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("122", 400000));
        erpArticleAvailability.add(availability);

        return mockedAvailabilityResponse;
    }

    private void evaluateResultForNormalArticlesAllWarehouses(final List<ProductAvailabilityExtModel> availabilities) {
        ProductAvailabilityExtModel availability = availabilities.get(0);
        assertEquals("first product code is wrong", "1001", availability.getProductCode());
        assertEquals("first detail info is wrong", Boolean.TRUE, availability.getDetailInfo());
        assertEquals("first stock level total is wrong", Integer.valueOf(990), availability.getStockLevelTotal());
        // assertNull("first backorder quantity is not null", availability.getBackorderQuantity());
        assertEquals("first backorder quantity is not zero", Integer.valueOf(0), availability.getBackorderQuantity());
        assertNull("first backorder delivery date is not null", availability.getBackorderDeliveryDate());
        List<PickupStockLevelExtModel> pickupStockLevels = availability.getStockLevelPickup();
        assertEquals("size of returned pickup stock levels is wrong", 2, pickupStockLevels.size());
        PickupStockLevelExtModel pickupStockLevel = pickupStockLevels.get(0);
        assertEquals("first pickup warehouse of first product is wrong", "121", pickupStockLevel.getWarehouse().getCode());
        assertEquals("first pickup stock level of first product is wrong", Integer.valueOf(400), pickupStockLevel.getStockLevel());
        pickupStockLevel = pickupStockLevels.get(1);
        assertEquals("second pickup warehouse of first product is wrong", "122", pickupStockLevel.getWarehouse().getCode());
        assertEquals("second pickup stock level of first product is wrong", Integer.valueOf(40), pickupStockLevel.getStockLevel());

        availability = availabilities.get(1);
        assertEquals("second product code is wrong", "1002", availability.getProductCode());
        assertEquals("second detail info is wrong", Boolean.TRUE, availability.getDetailInfo());
        assertEquals("second stock level total is wrong", Integer.valueOf(99000), availability.getStockLevelTotal());
        // assertNull("second backorder quantity is not null", availability.getBackorderQuantity());
        assertEquals("second backorder quantity is not zero", Integer.valueOf(0), availability.getBackorderQuantity());
        assertNull("second backorder delivery date is not null", availability.getBackorderDeliveryDate());
        pickupStockLevels = availability.getStockLevelPickup();
        assertEquals("size of returned pickup stock levels is wrong", 2, pickupStockLevels.size());
        pickupStockLevel = pickupStockLevels.get(0);
        assertEquals("first pickup warehouse of second product is wrong", "121", pickupStockLevel.getWarehouse().getCode());
        assertEquals("first pickup stock level of second product is wrong", Integer.valueOf(40000), pickupStockLevel.getStockLevel());
        pickupStockLevel = pickupStockLevels.get(1);
        assertEquals("second pickup warehouse of second product is wrong", "122", pickupStockLevel.getWarehouse().getCode());
        assertEquals("second pickup stock level of second product is wrong", Integer.valueOf(4000), pickupStockLevel.getStockLevel());

        availability = availabilities.get(2);
        assertEquals("third product code is wrong", "1003", availability.getProductCode());
        assertEquals("third detail info is wrong", Boolean.TRUE, availability.getDetailInfo());
        assertEquals("third stock level total is wrong", Integer.valueOf(9900000), availability.getStockLevelTotal());
        // assertNull("third backorder quantity is not null", availability.getBackorderQuantity());
        assertEquals("third backorder quantity is not zero", Integer.valueOf(0), availability.getBackorderQuantity());
        assertNull("third backorder delivery date is not null", availability.getBackorderDeliveryDate());
        pickupStockLevels = availability.getStockLevelPickup();
        assertEquals("size of returned pickup stock levels is wrong", 2, pickupStockLevels.size());
        pickupStockLevel = pickupStockLevels.get(0);
        assertEquals("first pickup warehouse of third product is wrong", "121", pickupStockLevel.getWarehouse().getCode());
        assertEquals("first pickup stock level of third product is wrong", Integer.valueOf(4000000), pickupStockLevel.getStockLevel());
        pickupStockLevel = pickupStockLevels.get(1);
        assertEquals("second pickup warehouse of third product is wrong", "122", pickupStockLevel.getWarehouse().getCode());
        assertEquals("second pickup stock level of third product is wrong", Integer.valueOf(400000), pickupStockLevel.getStockLevel());
    }

    @Test
    public void test24hExceedsIntegerMax() throws Exception {
        // init mocked webServiceClient
        final AvailabilityResponse mockedAvailabilityResponse = new AvailabilityResponse();

        final List<ErpArticleAvailability> erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();

        final ErpArticleAvailability availability = buildNewArticleFoundAvailability("1001");
        final List<StockLevels> stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", Integer.MAX_VALUE - 10));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("102", 20));
        erpArticleAvailability.add(availability);

        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // init call arguments
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("1001");
    }

    @Test
    public void testTotalExceedsIntegerMax() throws Exception {
        // init mocked webServiceClient
        final AvailabilityResponse mockedAvailabilityResponse = new AvailabilityResponse();

        final List<ErpArticleAvailability> erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();

        final ErpArticleAvailability availability = buildNewArticleFoundAvailability("1001");
        final List<StockLevels> stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", Integer.MAX_VALUE - 10));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("111", 10));
        erpArticleAvailability.add(availability);

        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // init call arguments
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("1001");

        DistSalesStatusModel salesStatus = createSalesStatus(true);
        when(productService.getProductSalesStatusModel(any(ProductModel.class))).thenReturn(salesStatus);

        // action
        final List<ProductAvailabilityExtModel> availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation
        final ProductAvailabilityExtModel productAvailability = availabilities.get(0);
        assertEquals("total stock level is not Integer.MAX_VALUE", Integer.valueOf(Integer.MAX_VALUE), productAvailability.getStockLevelTotal());
    }

    @Test
    public void testPickupExceedsIntegerMax() throws Exception {
        // init mocked webServiceClient
        final AvailabilityResponse mockedAvailabilityResponse = new AvailabilityResponse();

        final List<ErpArticleAvailability> erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();

        final ErpArticleAvailability availability = buildNewArticleFoundAvailability("1001");
        final List<StockLevels> stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("121", Integer.MAX_VALUE));
        erpArticleAvailability.add(availability);

        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // init call arguments
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("1001");

        DistSalesStatusModel salesStatus = createSalesStatus(true);
        when(productService.getProductSalesStatusModel(any(ProductModel.class))).thenReturn(salesStatus);

        // action
        final List<ProductAvailabilityExtModel> availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation
        final ProductAvailabilityExtModel productAvailability = availabilities.get(0);
        final List<PickupStockLevelExtModel> pickupStockLevels = productAvailability.getStockLevelPickup();
        final PickupStockLevelExtModel pickupStockLevel = pickupStockLevels.get(0);
        assertEquals("pickup stock level is not Integer.MAX_VALUE", Integer.valueOf(Integer.MAX_VALUE), pickupStockLevel.getStockLevel());
    }

    @Test
    public void testArticleNotFound() throws Exception {
        // init mocked webServiceClient
        final AvailabilityResponse mockedAvailabilityResponse = new AvailabilityResponse();

        final List<ErpArticleAvailability> erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();

        final ErpArticleAvailability availability = buildNewArticleNotFoundAvailability("1001");
        erpArticleAvailability.add(availability);

        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // init call arguments
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("1001");

        // action
        final List<ProductAvailabilityExtModel> availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation
        assertEquals("size of returned availabilities is wrong", 0, availabilities.size());
    }

    @Test
    public void testArticleNotFoundInWarehouse() throws Exception {
        // init mocked webServiceClient
        final AvailabilityResponse mockedAvailabilityResponse = new AvailabilityResponse();

        final List<ErpArticleAvailability> erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();

        final ErpArticleAvailability availability = buildNewArticleFoundAvailability("1001");
        final List<StockLevels> stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleNotFoundInWarehouseStockLevel("101"));
        stockLevels.add(buildNewArticleNotFoundInWarehouseStockLevel("102"));
        stockLevels.add(buildNewArticleNotFoundInWarehouseStockLevel("111"));
        stockLevels.add(buildNewArticleNotFoundInWarehouseStockLevel("112"));
        stockLevels.add(buildNewArticleNotFoundInWarehouseStockLevel("121"));
        stockLevels.add(buildNewArticleNotFoundInWarehouseStockLevel("122"));
        erpArticleAvailability.add(availability);

        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // init call arguments
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("1001");

        // action
        final List<ProductAvailabilityExtModel> availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation
        assertEquals("size of returned availabilities is wrong", 0, availabilities.size());
    }

    @Test
    public void testFoundAndNotFound() throws Exception {
        // three articles, three warehouses, second of each not found

        // init mocked webServiceClient
        final AvailabilityResponse mockedAvailabilityResponse = new AvailabilityResponse();

        final List<ErpArticleAvailability> erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();

        ErpArticleAvailability availability = buildNewArticleFoundAvailability("1001");
        List<StockLevels> stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", 10));
        stockLevels.add(buildNewArticleNotFoundInWarehouseStockLevel("102"));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("103", 100));
        erpArticleAvailability.add(availability);

        availability = buildNewArticleNotFoundAvailability("1002");
        erpArticleAvailability.add(availability);

        availability = buildNewArticleFoundAvailability("1003");
        stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", 20));
        stockLevels.add(buildNewArticleNotFoundInWarehouseStockLevel("102"));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("103", 200));
        erpArticleAvailability.add(availability);

        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // init call arguments
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("1001");
        productCodes.add("1002");
        productCodes.add("1003");

        DistSalesStatusModel salesStatus = createSalesStatus(true);
        when(productService.getProductSalesStatusModel(any(ProductModel.class))).thenReturn(salesStatus);

        // action
        final List<ProductAvailabilityExtModel> availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation
        assertEquals("size of returned availabilities is wrong", 2, availabilities.size());

        ProductAvailabilityExtModel productAvailability = availabilities.get(0);
        assertEquals("first product code is wrong", "1001", productAvailability.getProductCode());
        assertEquals("first detail info is wrong", Boolean.TRUE, productAvailability.getDetailInfo());
        assertEquals("second stock level total is wrong", Integer.valueOf(110), productAvailability.getStockLevelTotal());
        List<PickupStockLevelExtModel> pickupStockLevels = productAvailability.getStockLevelPickup();
        assertEquals("size of returned pickup stock levels is wrong", 0, pickupStockLevels.size());

        productAvailability = availabilities.get(1);
        assertEquals("second product code is wrong", "1003", productAvailability.getProductCode());
        assertEquals("second detail info is wrong", Boolean.TRUE, productAvailability.getDetailInfo());
        assertEquals("second stock level total is wrong", Integer.valueOf(220), productAvailability.getStockLevelTotal());
        pickupStockLevels = productAvailability.getStockLevelPickup();
        assertEquals("size of returned pickup stock levels is wrong", 0, pickupStockLevels.size());
    }

    @Test
    public void testNotFoundInOnePickupWarehouse() throws Exception {
        // one articles, one default warehouse, three pickup warehouses, second pickup warehouse not found

        // init mocked webServiceClient
        final AvailabilityResponse mockedAvailabilityResponse = new AvailabilityResponse();

        final List<ErpArticleAvailability> erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();

        final ErpArticleAvailability availability = buildNewArticleFoundAvailability("1001");
        final List<StockLevels> stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", 10));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("121", 20));
        stockLevels.add(buildNewArticleNotFoundInWarehouseStockLevel("122"));
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("123", 200));
        erpArticleAvailability.add(availability);

        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // init call arguments
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("1001");

        DistSalesStatusModel salesStatus = createSalesStatus(true);
        when(productService.getProductSalesStatusModel(any(ProductModel.class))).thenReturn(salesStatus);

        // action
        final List<ProductAvailabilityExtModel> availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation
        assertEquals("size of returned availabilities is wrong", 1, availabilities.size());
        final ProductAvailabilityExtModel productAvailability = availabilities.get(0);
        assertEquals("first product code is wrong", "1001", productAvailability.getProductCode());
        assertEquals("first detail info is wrong", Boolean.TRUE, productAvailability.getDetailInfo());
        assertEquals("first stock level total is wrong", Integer.valueOf(230), productAvailability.getStockLevelTotal());
        final List<PickupStockLevelExtModel> pickupStockLevels = productAvailability.getStockLevelPickup();
        assertEquals("size of returned pickup stock levels is wrong", 2, pickupStockLevels.size());
        PickupStockLevelExtModel pickupStockLevel = pickupStockLevels.get(0);
        assertEquals("first pickup warehouse of first product is wrong", "121", pickupStockLevel.getWarehouse().getCode());
        assertEquals("first pickup stock level of first product is wrong", Integer.valueOf(20), pickupStockLevel.getStockLevel());
        pickupStockLevel = pickupStockLevels.get(1);
        assertEquals("second pickup warehouse of first product is wrong", "123", pickupStockLevel.getWarehouse().getCode());
        assertEquals("second pickup stock level of first product is wrong", Integer.valueOf(200), pickupStockLevel.getStockLevel());
    }

    /*
     * Test merging of results: first request: one product at one warehouse, second request: same request with one additional product. value
     * for first product must come from cache then.
     */
    @Test
    public void testMergingOfCachedAndUncachedResults() throws Exception {
        // init mocked webServiceClient
        AvailabilityResponse mockedAvailabilityResponse = new AvailabilityResponse();
        List<ErpArticleAvailability> erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();
        ErpArticleAvailability availability = buildNewArticleFoundAvailability("1001");
        List<StockLevels> stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", 200));
        erpArticleAvailability.add(availability);
        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // init call arguments
        final List<String> productCodes = new ArrayList<String>();
        productCodes.add("1001");

        DistSalesStatusModel salesStatus = createSalesStatus(true);
        when(productService.getProductSalesStatusModel(any(ProductModel.class))).thenReturn(salesStatus);

        // action
        List<ProductAvailabilityExtModel> availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation
        assertEquals("size of returned availabilities for first invocation is wrong", 1, availabilities.size());
        ProductAvailabilityExtModel productAvailability = availabilities.get(0);
        assertEquals("first product code for first invocation is wrong", "1001", productAvailability.getProductCode());

        // changed mocked webServiceClient
        mockedAvailabilityResponse = new AvailabilityResponse();
        erpArticleAvailability = mockedAvailabilityResponse.getErpArticleAvailability();
        availability = buildNewArticleFoundAvailability("1002");
        stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel("101", 220));
        erpArticleAvailability.add(availability);
        when(webServiceClient.if06Availability(any(AvailabilityRequest.class))).thenReturn(mockedAvailabilityResponse);

        // change call arguments
        productCodes.add("1002");

        // repeat action
        availabilities = sapAvailabilityService.getAvailability(productCodes, true);

        // evaluation of second invocation -> should have two products
        assertEquals("size of returned availabilities for second invocation is wrong", 2, availabilities.size());
        productAvailability = availabilities.get(0);
        assertEquals("first product code for second invocation is wrong", "1001", productAvailability.getProductCode());
        productAvailability = availabilities.get(1);
        assertEquals("second product code for second invocation is wrong", "1002", productAvailability.getProductCode());
    }

    @Test
    public void testNullLeadTimeErpAreFilteredOut(){
        final String code = "1001";

        final ProductModel productModel = new ProductModel();
        productModel.setCode(code);
        productModel.setProditecArticle(false);


        final ProductAvailabilityExtModel model = new ProductAvailabilityExtModel();
        model.setStockLevels(new ArrayList<>());

        final StockLevelModel stockLevelModel = new StockLevelModel();
        stockLevelModel.setLeadTimeErp(null);

        when(stockService.getStockLevel(any(ProductModel.class), any(WarehouseModel.class))).thenReturn(stockLevelModel);

        final ErpArticleAvailability availability = buildNewArticleFoundAvailability(code);
        final List<StockLevels> stockLevels = availability.getStockLevels();
        stockLevels.add(buildNewArticleFoundInWarehouseStockLevel(code, 10));

        final boolean ignoreProditcecCDC = true;

        sapAvailabilityService.calculateStockLevels(model, availability, productModel, ignoreProditcecCDC);


        final StockLevelData stockLevelData = model.getStockLevels().get(2);
        assertEquals(Integer.valueOf(0), stockLevelData.getLeadTime());
    }

    private DistSalesStatusModel createSalesStatus(boolean isVisible) {
        DistSalesStatusModel distSalesStatusModel = new DistSalesStatusModel();
        distSalesStatusModel.setVisibleInShop(isVisible);
        return distSalesStatusModel;
    }

    private ErpArticleAvailability buildNewArticleFoundAvailability(final String articleNr) {
        final ErpArticleAvailability availability = new ErpArticleAvailability();
        availability.setArticleNumber(articleNr);
        availability.setArticleFound(true);
        return availability;
    }

    private ErpArticleAvailability buildNewArticleNotFoundAvailability(final String articleNr) {
        final ErpArticleAvailability availability = new ErpArticleAvailability();
        availability.setArticleNumber(articleNr);
        availability.setArticleFound(false);
        return availability;
    }

    private StockLevels buildNewArticleFoundInWarehouseStockLevel(final String warehouseId, final long available) {
        final StockLevels stockLevel = new StockLevels();
        stockLevel.setWarehouseId(warehouseId);
        stockLevel.setAvailable(BigInteger.valueOf(available));
        stockLevel.setArticleFoundInWarehouse(true);
        return stockLevel;
    }

    private StockLevels buildNewArticleNotFoundInWarehouseStockLevel(final String warehouseId) {
        final StockLevels stockLevel = new StockLevels();
        stockLevel.setWarehouseId(warehouseId);
        stockLevel.setArticleFoundInWarehouse(false);
        return stockLevel;
    }

}
