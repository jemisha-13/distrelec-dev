/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.export.impl;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.export.DistExportFacade;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.StockData;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.site.BaseSiteService;

/**
 * Test class for {@link DefaultDistExportFacade}.
 *
 * @author pbueschi, Namics AG
 *
 */
@IntegrationTest
public class DefaultDistExportFacadeIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private BaseSiteService baseSiteService;

    @Resource
    private DistExportFacade distExportFacade;

    private ProductData b2bProductData;

    private CartData cartData;

    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bfacades/test/testDistExportFacade.impex", "utf-8");

        final DistManufacturerData distManufacturerData = new DistManufacturerData();
        distManufacturerData.setName("test manufacturer");

        final Map<Long, Map<String, PriceData>> volumePriceDataMap = new HashMap<>();
        final Map<String, PriceData> priceDataMap = new HashMap<>();
        PriceData priceData = new PriceData();
        priceData.setFormattedValue("CHF 42.-");
        priceData.setMinQuantity(1L);
        priceDataMap.put("price", priceData);
        volumePriceDataMap.put(Long.valueOf(1), priceDataMap);
        priceData = new PriceData();
        priceData.setFormattedValue("CHF 32.-");
        priceData.setMinQuantity(5L);
        priceDataMap.put("price", priceData);
        volumePriceDataMap.put(Long.valueOf(1), priceDataMap);
        priceData = new PriceData();
        priceData.setFormattedValue("CHF 22.-");
        priceData.setMinQuantity(10L);
        priceDataMap.put("price", priceData);
        volumePriceDataMap.put(Long.valueOf(1), priceDataMap);

        b2bProductData = new ProductData();
        b2bProductData.setCode("5849020");
        b2bProductData.setName("super product");
        b2bProductData.setDistManufacturer(distManufacturerData);
        final StockData stockData = new StockData();
        stockData.setStockLevel(42L);
        b2bProductData.setStock(stockData);
        b2bProductData.setTypeName("test typename");
        b2bProductData.setVolumePricesMap(volumePriceDataMap);

        final OrderEntryData orderEntryData = new OrderEntryData();
        orderEntryData.setQuantity(Long.valueOf(2));
        orderEntryData.setBasePrice(null);
        orderEntryData.setTotalPrice(null);
        orderEntryData.setProduct(b2bProductData);
        final List<OrderEntryData> cartEntries = new ArrayList<>();
        cartEntries.add(orderEntryData);

        cartData = new CartData();
        cartData.setEntries(cartEntries);

        final BaseSiteModel baseSite = baseSiteService.getBaseSiteForUID("distrelec_CH");
        baseSiteService.setCurrentBaseSite(baseSite, false);
    }

    @Test
    public void testExportProductsToCsv() {
        final List<NamicsWishlistEntryData> whishlistEntries = new ArrayList<>();
        final NamicsWishlistEntryData entry = new NamicsWishlistEntryData();
        entry.setProduct(b2bProductData);
        entry.setComment("Test comment");
        entry.setDesired(Integer.valueOf(10));
        entry.setAddedDate(new Date());
        whishlistEntries.add(entry);
        final File downloadFile = distExportFacade.exportProducts(whishlistEntries, DistConstants.Export.FORMAT_CSV, "facadeProductTest");
        Assert.assertNotNull(downloadFile);
    }

    @Test
    public void testExportCartToCsv() {
        final File downloadFile = distExportFacade.exportCart(cartData, DistConstants.Export.FORMAT_CSV, "facadeCartTest");
        Assert.assertNotNull(downloadFile);
    }
}
