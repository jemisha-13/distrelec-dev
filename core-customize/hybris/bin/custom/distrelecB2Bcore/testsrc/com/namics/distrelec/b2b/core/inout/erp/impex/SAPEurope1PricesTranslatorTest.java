/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impex;

import java.io.StringReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.jalo.DistPriceRow;
import com.namics.distrelec.b2b.core.jalo.DistSalesOrg;
import com.namics.distrelec.b2b.core.jalo.Namb2bacceleratorCoreManager;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.europe1.jalo.Europe1PriceFactory;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.impex.jalo.imp.ImpExImportReader;
import de.hybris.platform.jalo.ConsistencyCheckException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.c2l.C2LManager;
import de.hybris.platform.jalo.c2l.Country;
import de.hybris.platform.jalo.c2l.Currency;
import de.hybris.platform.jalo.enumeration.EnumerationManager;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.jalo.product.Product;
import de.hybris.platform.jalo.product.ProductManager;
import de.hybris.platform.jalo.product.Unit;
import de.hybris.platform.testframework.HybrisJUnit4TransactionalTest;
import de.hybris.platform.util.CSVReader;
import de.hybris.platform.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test class for {@link SAPEurope1PricesTranslator}.
 * 
 * @author daehusir, Distrelec
 * 
 */
@SuppressWarnings("deprecation")
public class SAPEurope1PricesTranslatorTest extends HybrisJUnit4TransactionalTest {

    private static final Logger LOG = LoggerFactory.getLogger(SAPEurope1PricesTranslatorTest.class);

    private Product testProduct1;
    private Product testProduct2;
    private Product testProduct3;
    private Product testProduct4;
    private Product testProduct5;
    private Unit unit;
    private EnumerationValue userPriceGroup1;
    private EnumerationValue userPriceGroup2;
    private Currency currency;
    private static final String CHF = "CHF";
    private SessionContext ctx;
    private Europe1PriceFactory europe1PriceFactory;
    private final Locale loc = new Locale("de", "CH");
    private final SimpleDateFormat dateFormatWithoutTime = Utilities.getSimpleDateFormat("yyyyMMdd");
    private final NumberFormat numberFormat = Utilities.getDecimalFormat("###0.##", loc);

    @Before
    public void setUp() throws Exception {
        ctx = jaloSession.getSessionContext();
        ctx.setLanguage(getOrCreateLanguage("de"));
        europe1PriceFactory = Europe1PriceFactory.getInstance();

        try {
            currency = C2LManager.getInstance().getCurrencyByIsoCode(CHF);
        } catch (final JaloItemNotFoundException e) {
            currency = C2LManager.getInstance().createCurrency(CHF);
        }

        Assert.assertNotNull(currency);

        testProduct1 = ProductManager.getInstance().createProduct("testProduct1");
        testProduct2 = ProductManager.getInstance().createProduct("testProduct2");
        testProduct3 = ProductManager.getInstance().createProduct("testProduct3");
        testProduct4 = ProductManager.getInstance().createProduct("testProduct4");
        testProduct5 = ProductManager.getInstance().createProduct("testProduct5");
        Assert.assertNotNull(testProduct1);
        Assert.assertNotNull(testProduct2);
        Assert.assertNotNull(testProduct3);
        Assert.assertNotNull(testProduct4);
        Assert.assertNotNull(testProduct5);

        unit = ProductManager.getInstance().createUnit("pieces", "ST");
        Assert.assertNotNull(unit);

        testProduct1.setUnit(unit);
        testProduct2.setUnit(unit);
        testProduct3.setUnit(unit);
        testProduct4.setUnit(unit);
        testProduct5.setUnit(unit);

        userPriceGroup1 = europe1PriceFactory.getUserPriceGroup("SalesOrg_UPG_7310_M01");
        if (userPriceGroup1 == null) {
            userPriceGroup1 = europe1PriceFactory.createUserPriceGroup("SalesOrg_UPG_7310_M01");
        }

        userPriceGroup2 = europe1PriceFactory.getUserPriceGroup("SalesOrg_UPG_7320_M01");
        if (userPriceGroup2 == null) {
            userPriceGroup2 = europe1PriceFactory.createUserPriceGroup("SalesOrg_UPG_7320_M01");
        }

        Assert.assertNotNull(userPriceGroup1);
        Assert.assertNotNull(userPriceGroup2);

        final Map<String, Object> salesOrg7310Attributes = new HashMap<String, Object>();
        salesOrg7310Attributes.put(DistSalesOrgModel.CODE, "7310");
        salesOrg7310Attributes.put(DistSalesOrgModel.NAME, "Sales Org 7310");
        salesOrg7310Attributes.put(DistSalesOrgModel.COUNTRY, getOrCreateCountry("CH"));
        salesOrg7310Attributes.put(DistSalesOrgModel.ERPSYSTEM, EnumerationManager.getInstance()
                .getEnumerationValue(EnumerationManager.getInstance().getEnumerationType(DistErpSystem._TYPECODE), DistErpSystem.SAP.getCode()));
        final DistSalesOrg distSalesOrg7310 = Namb2bacceleratorCoreManager.getInstance().createDistSalesOrg(salesOrg7310Attributes);

        final Map<String, Object> salesOrg7320Attributes = new HashMap<String, Object>();
        salesOrg7320Attributes.put("code", "7320");
        salesOrg7320Attributes.put("name", "Sales Org 7320");
        salesOrg7320Attributes.put(DistSalesOrgModel.COUNTRY, getOrCreateCountry("AT"));
        salesOrg7320Attributes.put(DistSalesOrgModel.ERPSYSTEM, EnumerationManager.getInstance()
                .getEnumerationValue(EnumerationManager.getInstance().getEnumerationType(DistErpSystem._TYPECODE), DistErpSystem.SAP.getCode()));
        final DistSalesOrg distSalesOrg7320 = Namb2bacceleratorCoreManager.getInstance().createDistSalesOrg(salesOrg7320Attributes);

        Assert.assertNotNull(distSalesOrg7310);
        Assert.assertNotNull(distSalesOrg7320);
    }

    @Test
    public void testImport() throws Exception {
        final SAPEurope1PriceRowTranslator rowTrans = new SAPEurope1PriceRowTranslator(dateFormatWithoutTime, numberFormat, loc);
        rowTrans.init(rowTrans.getColumnDescriptor());
        final SAPEurope1PricesTranslator trans = new SAPEurope1PricesTranslator(rowTrans);
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DATE, 28);
        cal.set(Calendar.HOUR_OF_DAY, 22);
        cal.set(Calendar.MINUTE, 33);
        cal.set(Calendar.SECOND, 44);
        cal.set(Calendar.MILLISECOND, 0);

        // Normal list price import for SalesOrg 7310
        Collection rows = (Collection) trans
                .importValue("SalesOrg_UPG_7310_M01 1/1 = 53.5 CHF N [20120102,99991231] false 1000001_20130528223344_20130531142800000 false", testProduct1);
        Assert.assertNotNull(rows);
        Assert.assertEquals(1L, rows.size());
        DistPriceRow row = (DistPriceRow) rows.iterator().next();
        Assert.assertEquals(currency, row.getCurrency());
        Assert.assertNotNull(row.getDateRange());
        checkPrice(numberFormat, "53.5", row);
        Assert.assertEquals(1L, row.getMinQuantity());
        Assert.assertEquals(unit, row.getUnit());
        Assert.assertEquals(1, row.getUnitFactor().intValue());
        Assert.assertNull(row.getUser());
        Assert.assertEquals(userPriceGroup1, row.getUg());
        Assert.assertTrue(row.isNetAsPrimitive());
        Assert.assertFalse(row.isSpecialPrice());
        Assert.assertEquals("1000001", row.getPriceConditionIdErp());
        Assert.assertEquals(cal.getTime(), row.getLastModifiedErp());
        Assert.assertEquals(Long.valueOf(20130531142800000L), row.getAttribute(PriceRowModel.SEQUENCEID));

        // Normal list price import for SalesOrg 7310 with special scale
        rows = (Collection) trans
                .importValue("SalesOrg_UPG_7310_M01 1/10 = 50.5 CHF N [20120102,99991231] false 1000002_20130528223344_20130531142800000 false", testProduct1);
        Assert.assertNotNull(rows);
        Assert.assertEquals(1L, rows.size());
        row = (DistPriceRow) rows.iterator().next();
        Assert.assertEquals(currency, row.getCurrency());
        Assert.assertNotNull(row.getDateRange());
        checkPrice(numberFormat, "50.5", row);
        Assert.assertEquals(10L, row.getMinQuantity());
        Assert.assertEquals(unit, row.getUnit());
        Assert.assertEquals(1, row.getUnitFactor().intValue());
        Assert.assertNull(row.getUser());
        Assert.assertEquals(userPriceGroup1, row.getUg());
        Assert.assertTrue(row.isNetAsPrimitive());
        Assert.assertFalse(row.isSpecialPrice());
        Assert.assertEquals("1000002", row.getPriceConditionIdErp());
        Assert.assertEquals(cal.getTime(), row.getLastModifiedErp());
        Assert.assertEquals(Long.valueOf(20130531142800000L), row.getAttribute(PriceRowModel.SEQUENCEID));

        // Normal list price import for SalesOrg 7310 with special unit factor
        rows = (Collection) trans
                .importValue("SalesOrg_UPG_7310_M01 100/1 = 53.5 CHF N [20120102,99991231] false 1000003_20130528223344_20130531142800000 false", testProduct2);
        Assert.assertNotNull(rows);
        Assert.assertEquals(1L, rows.size());
        row = (DistPriceRow) rows.iterator().next();
        Assert.assertEquals(currency, row.getCurrency());
        Assert.assertNotNull(row.getDateRange());
        checkPrice(numberFormat, "53.5", row);
        Assert.assertEquals(1L, row.getMinQuantity());
        Assert.assertEquals(unit, row.getUnit());
        Assert.assertEquals(100, row.getUnitFactor().intValue());
        Assert.assertNull(row.getUser());
        Assert.assertEquals(userPriceGroup1, row.getUg());
        Assert.assertTrue(row.isNetAsPrimitive());
        Assert.assertFalse(row.isSpecialPrice());
        Assert.assertEquals("1000003", row.getPriceConditionIdErp());
        Assert.assertEquals(cal.getTime(), row.getLastModifiedErp());
        Assert.assertEquals(Long.valueOf(20130531142800000L), row.getAttribute(PriceRowModel.SEQUENCEID));

        // Import of a special price for SalesOrg 7310
        rows = (Collection) trans.importValue("SalesOrg_UPG_7310_M01 1/1 = 53.5 CHF N [20120102,99991231] true 1000004_20130528223344_20130531142800000 false",
                testProduct3);
        Assert.assertNotNull(rows);
        Assert.assertEquals(1L, rows.size());
        row = (DistPriceRow) rows.iterator().next();
        Assert.assertEquals(currency, row.getCurrency());
        Assert.assertNotNull(row.getDateRange());
        checkPrice(numberFormat, "53.5", row);
        Assert.assertEquals(1L, row.getMinQuantity());
        Assert.assertEquals(unit, row.getUnit());
        Assert.assertEquals(1, row.getUnitFactor().intValue());
        Assert.assertNull(row.getUser());
        Assert.assertEquals(userPriceGroup1, row.getUg());
        Assert.assertTrue(row.isNetAsPrimitive());
        // Assert.assertTrue(row.isSpecialPrice());
        Assert.assertEquals("1000004", row.getPriceConditionIdErp());
        Assert.assertEquals(cal.getTime(), row.getLastModifiedErp());
        Assert.assertEquals(Long.valueOf(20130531142800000L), row.getAttribute(PriceRowModel.SEQUENCEID));

        // Normal list price import for SalesOrg 7320
        rows = (Collection) trans.importValue("SalesOrg_UPG_7320_M01 1/1 = 53.5 CHF N [20120102,99991231] true 1000005_20130528223344_20130531142800000 false",
                testProduct1);
        Assert.assertNotNull(rows);
        Assert.assertEquals(1L, rows.size());
        row = (DistPriceRow) rows.iterator().next();
        Assert.assertEquals(currency, row.getCurrency());
        Assert.assertNotNull(row.getDateRange());
        checkPrice(numberFormat, "53.5", row);
        Assert.assertEquals(1L, row.getMinQuantity());
        Assert.assertEquals(unit, row.getUnit());
        Assert.assertEquals(1, row.getUnitFactor().intValue());
        Assert.assertNull(row.getUser());
        Assert.assertEquals(userPriceGroup2, row.getUg());
        Assert.assertTrue(row.isNetAsPrimitive());
        // Assert.assertTrue(row.isSpecialPrice());
        Assert.assertEquals("1000005", row.getPriceConditionIdErp());
        Assert.assertEquals(cal.getTime(), row.getLastModifiedErp());
        Assert.assertEquals(Long.valueOf(20130531142800000L), row.getAttribute(PriceRowModel.SEQUENCEID));

        // Import of a price row flagged as deleted. The price row from the import before will be deleted.
        rows = (Collection) trans.importValue("SalesOrg_UPG_7310_M01 1/1 = 53.5 CHF N [20120102,99991231] false 1000006_20130528223344_20130531142800000 true",
                testProduct4);
        Assert.assertNull(rows);

        // Import of a price row with a scale of 0. Price row will be skipped and not imported.
        rows = (Collection) trans
                .importValue("SalesOrg_UPG_7310_M01 1/0 ST = 0.0 CHF N [20120102,99991231] false 1000007_20130528223344_20130531142800000 false", testProduct5);
        Assert.assertNotNull(rows);
        Assert.assertTrue(rows.isEmpty());
    }

    @Test
    public void testOverallImport() {
        final StringBuffer buf = new StringBuffer(1800);
        buf.append("#% impex.setLocale(new Locale(\"de\",\"CH\"));");
        buf.append('\n');
        buf.append(
                "INSERT_UPDATE Product;code[unique=true];catalogVersion[unique=true, allownull=true];europe1prices[translator=com.namics.distrelec.b2b.core.inout.erp.impex.SAPEurope1PricesTranslator][mode=append][dateformat=yyyyMMdd]");
        buf.append('\n');
        buf.append(';');
        buf.append(testProduct1.getCode(ctx));
        buf.append(";;SalesOrg_UPG_7310_M01 1/1 = 53.5 CHF N [20120102,99991231] false 1000001_20130528223344_20130531142800000 false");
        buf.append('\n');
        buf.append(';');
        buf.append(testProduct1.getCode(ctx));
        buf.append(";;SalesOrg_UPG_7310_M01 1/10 = 50.5 CHF N [20120102,99991231] false 1000002_20130528223344_20130531142800000 false");
        buf.append('\n');
        buf.append(';');
        buf.append(testProduct1.getCode(ctx));
        buf.append(";;SalesOrg_UPG_7320_M01 1/1 = 53.5 CHF N [20120102,99991231] false 1000003_20130528223344_20130531142800000 false");
        buf.append('\n');
        buf.append(';');
        buf.append(testProduct1.getCode(ctx));
        buf.append(";;SalesOrg_UPG_7320_M01 1/10 = 50.5 CHF N [20120102,99991231] false 1000004_20130528223344_20130531142800000 false");
        buf.append('\n');
        buf.append(';');
        buf.append(testProduct2.getCode(ctx));
        buf.append(
                ";;SalesOrg_UPG_7310_M01 1/1 = 53.5 CHF N [20120102,99991231] false 1000005_20130528223344_20130531142800000 false, SalesOrg_UPG_7310_M01 1/10 ST = 50.5 CHF N [20120102,99991231] false 1000005_20130528223344_20130531142800000 false, SalesOrg_UPG_7310_M01 1/15 ST = 47.5 CHF N [20120102,99991231] false 1000005_20130528223344_20130531142800000 false");
        buf.append('\n');
        buf.append('\n');
        buf.append(';');
        buf.append(testProduct3.getCode(ctx));
        buf.append(
                ";;SalesOrg_UPG_7310_M01 1/1 = 50.5 CHF N [20120102,99991231] true 1000006_20130528223344_20130531142800000 false, SalesOrg_UPG_7310_M01 1/10 ST = 47.5 CHF N [20120102,99991231] true 1000006_20130528223344_20130531142800000 false, SalesOrg_UPG_7310_M01 1/15 ST = 44.5 CHF N [20120102,99991231] true 1000006_20130528223344_20130531142800000 false");
        buf.append('\n');
        buf.append(';');
        buf.append(testProduct4.getCode(ctx));
        buf.append(
                ";;SalesOrg_UPG_7310_M01 1/1 = 50.5 CHF N [20120102,99991231] false 1000007_20130528223344_20130531142800000 false, SalesOrg_UPG_7310_M01 1/10 ST = 47.5 CHF N [20120102,99991231] false 1000007_20130528223344_20130531142800000 false, SalesOrg_UPG_7310_M01 1/15 ST = 44.5 CHF N [20120102,99991231] false 1000007_20130528223344_20130531142800000 false");
        final String data = buf.toString();

        final ImpExImportReader impExImportReader = new ImpExImportReader(new CSVReader(new StringReader(data)), null);
        impExImportReader.enableCodeExecution(true);

        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 4);
        cal.set(Calendar.DATE, 28);
        cal.set(Calendar.HOUR_OF_DAY, 22);
        cal.set(Calendar.MINUTE, 33);
        cal.set(Calendar.SECOND, 44);
        cal.set(Calendar.MILLISECOND, 0);

        try {
            final Product product1 = (Product) impExImportReader.readLine();
            Assert.assertNotNull(product1);
            final Collection<DistPriceRow> priceRows = europe1PriceFactory.getProductPriceRows(ctx, testProduct1,
                    europe1PriceFactory.getPPG(ctx, testProduct1));
            Assert.assertEquals(1, priceRows.size());

            final DistPriceRow priceRow = priceRows.iterator().next();
            Assert.assertEquals(currency, priceRow.getCurrency());
            Assert.assertNotNull(priceRow.getDateRange());
            checkPrice(numberFormat, "53.5", priceRow);
            Assert.assertEquals(1L, priceRow.getMinQuantity());
            Assert.assertEquals(unit, priceRow.getUnit());
            Assert.assertEquals(1, priceRow.getUnitFactor().intValue());
            Assert.assertNull(priceRow.getUser());
            Assert.assertEquals(userPriceGroup1, priceRow.getUg());
            Assert.assertTrue(priceRow.isNetAsPrimitive());
            Assert.assertFalse(priceRow.isSpecialPrice());
            Assert.assertEquals("1000001", priceRow.getPriceConditionIdErp());
            Assert.assertEquals(cal.getTime(), priceRow.getLastModifiedErp());
            Assert.assertEquals(Long.valueOf(20130531142800000L), priceRow.getAttribute(PriceRowModel.SEQUENCEID));
        } catch (final Exception e) {
            LOG.error("Exception occurred for overall import", e);
            Assert.fail(e.getMessage());
        }

        try {
            final Product product1 = (Product) impExImportReader.readLine();
            Assert.assertNotNull(product1);
            final Collection<DistPriceRow> priceRows = europe1PriceFactory.getProductPriceRows(ctx, testProduct1,
                    europe1PriceFactory.getPPG(ctx, testProduct1));
            Assert.assertEquals(2, priceRows.size());

            DistPriceRow priceRow = null;
            for (final DistPriceRow row : priceRows) {
                if (row.getPriceConditionIdErp().equals("1000002")) {
                    priceRow = row;
                    break;
                }
            }
            Assert.assertNotNull(priceRow);

            Assert.assertEquals(currency, priceRow.getCurrency());
            Assert.assertNotNull(priceRow.getDateRange());
            checkPrice(numberFormat, "50.5", priceRow);
            Assert.assertEquals(10L, priceRow.getMinQuantity());
            Assert.assertEquals(unit, priceRow.getUnit());
            Assert.assertEquals(1, priceRow.getUnitFactor().intValue());
            Assert.assertNull(priceRow.getUser());
            Assert.assertEquals(userPriceGroup1, priceRow.getUg());
            Assert.assertTrue(priceRow.isNetAsPrimitive());
            Assert.assertFalse(priceRow.isSpecialPrice());
            Assert.assertEquals("1000002", priceRow.getPriceConditionIdErp());
            Assert.assertEquals(cal.getTime(), priceRow.getLastModifiedErp());
            Assert.assertEquals(Long.valueOf(20130531142800000L), priceRow.getAttribute(PriceRowModel.SEQUENCEID));
        } catch (final Exception e) {
            LOG.error("Exception occurred for overall import", e);
            Assert.fail(e.getMessage());
        }

        try {
            final Product product1 = (Product) impExImportReader.readLine();
            Assert.assertNotNull(product1);
            final Collection<DistPriceRow> priceRows = europe1PriceFactory.getProductPriceRows(ctx, testProduct1,
                    europe1PriceFactory.getPPG(ctx, testProduct1));
            Assert.assertEquals(3, priceRows.size());

            DistPriceRow priceRow = null;
            for (final DistPriceRow row : priceRows) {
                if (row.getPriceConditionIdErp().equals("1000003")) {
                    priceRow = row;
                    break;
                }
            }
            Assert.assertNotNull(priceRow);

            Assert.assertEquals(currency, priceRow.getCurrency());
            Assert.assertNotNull(priceRow.getDateRange());
            checkPrice(numberFormat, "53.5", priceRow);
            Assert.assertEquals(1L, priceRow.getMinQuantity());
            Assert.assertEquals(unit, priceRow.getUnit());
            Assert.assertEquals(1, priceRow.getUnitFactor().intValue());
            Assert.assertNull(priceRow.getUser());
            Assert.assertEquals(userPriceGroup2, priceRow.getUg());
            Assert.assertTrue(priceRow.isNetAsPrimitive());
            Assert.assertFalse(priceRow.isSpecialPrice());
            Assert.assertEquals("1000003", priceRow.getPriceConditionIdErp());
            Assert.assertEquals(cal.getTime(), priceRow.getLastModifiedErp());
            Assert.assertEquals(Long.valueOf(20130531142800000L), priceRow.getAttribute(PriceRowModel.SEQUENCEID));
        } catch (final Exception e) {
            LOG.error("Exception occurred for overall import", e);
            Assert.fail(e.getMessage());
        }

        try {
            final Product product1 = (Product) impExImportReader.readLine();
            Assert.assertNotNull(product1);
            final Collection<DistPriceRow> priceRows = europe1PriceFactory.getProductPriceRows(ctx, testProduct1,
                    europe1PriceFactory.getPPG(ctx, testProduct1));
            Assert.assertEquals(4, priceRows.size());

            DistPriceRow priceRow = null;
            for (final DistPriceRow row : priceRows) {
                if (row.getPriceConditionIdErp().equals("1000004")) {
                    priceRow = row;
                    break;
                }
            }
            Assert.assertNotNull(priceRow);

            Assert.assertEquals(currency, priceRow.getCurrency());
            Assert.assertNotNull(priceRow.getDateRange());
            checkPrice(numberFormat, "50.5", priceRow);
            Assert.assertEquals(10L, priceRow.getMinQuantity());
            Assert.assertEquals(unit, priceRow.getUnit());
            Assert.assertEquals(1, priceRow.getUnitFactor().intValue());
            Assert.assertNull(priceRow.getUser());
            Assert.assertEquals(userPriceGroup2, priceRow.getUg());
            Assert.assertTrue(priceRow.isNetAsPrimitive());
            Assert.assertFalse(priceRow.isSpecialPrice());
            Assert.assertEquals("1000004", priceRow.getPriceConditionIdErp());
            Assert.assertEquals(cal.getTime(), priceRow.getLastModifiedErp());
            Assert.assertEquals(Long.valueOf(20130531142800000L), priceRow.getAttribute(PriceRowModel.SEQUENCEID));
        } catch (final Exception e) {
            LOG.error("Exception occurred for overall import", e);
            Assert.fail(e.getMessage());
        }

        try {
            final Product product = (Product) impExImportReader.readLine();
            Assert.assertNotNull(product);
            final Collection<DistPriceRow> priceRows = europe1PriceFactory.getProductPriceRows(ctx, testProduct2,
                    europe1PriceFactory.getPPG(ctx, testProduct2));
            Assert.assertEquals(3, priceRows.size());

            long quantity = 1;
            String price = "53.5";
            for (final DistPriceRow priceRow : priceRows) {
                Assert.assertEquals(currency, priceRow.getCurrency());
                Assert.assertNotNull(priceRow.getDateRange());
                checkPrice(numberFormat, price, priceRow);
                Assert.assertEquals(quantity, priceRow.getMinQuantity());
                Assert.assertEquals(unit, priceRow.getUnit());
                Assert.assertEquals(1, priceRow.getUnitFactor().intValue());
                Assert.assertNull(priceRow.getUser());
                Assert.assertEquals(userPriceGroup1, priceRow.getUg());
                Assert.assertTrue(priceRow.isNetAsPrimitive());
                Assert.assertFalse(priceRow.isSpecialPrice());
                Assert.assertEquals("1000005", priceRow.getPriceConditionIdErp());
                Assert.assertEquals(cal.getTime(), priceRow.getLastModifiedErp());
                Assert.assertEquals(Long.valueOf(20130531142800000L), priceRow.getAttribute(PriceRowModel.SEQUENCEID));

                if (quantity == 1) {
                    quantity = 10;
                } else {
                    quantity += 5;
                }

                price = numberFormat.format(numberFormat.parse(price).doubleValue() - 3);
            }
        } catch (final Exception e) {
            LOG.error("Exception occurred for overall import", e);
            Assert.fail(e.getMessage());
        }

        try {
            final Product product = (Product) impExImportReader.readLine();
            Assert.assertNotNull(product);
            final Collection<DistPriceRow> priceRows = europe1PriceFactory.getProductPriceRows(ctx, testProduct3,
                    europe1PriceFactory.getPPG(ctx, testProduct3));
            Assert.assertEquals(3, priceRows.size());

            long quantity = 1;
            String price = "50.5";
            for (final DistPriceRow priceRow : priceRows) {
                Assert.assertEquals(currency, priceRow.getCurrency());
                Assert.assertNotNull(priceRow.getDateRange());
                checkPrice(numberFormat, price, priceRow);
                Assert.assertEquals(quantity, priceRow.getMinQuantity());
                Assert.assertEquals(unit, priceRow.getUnit());
                Assert.assertEquals(1, priceRow.getUnitFactor().intValue());
                Assert.assertNull(priceRow.getUser());
                Assert.assertEquals(userPriceGroup1, priceRow.getUg());
                Assert.assertTrue(priceRow.isNetAsPrimitive());
                // Assert.assertTrue(priceRow.isSpecialPrice());
                Assert.assertEquals("1000006", priceRow.getPriceConditionIdErp());
                Assert.assertEquals(cal.getTime(), priceRow.getLastModifiedErp());
                Assert.assertEquals(Long.valueOf(20130531142800000L), priceRow.getAttribute(PriceRowModel.SEQUENCEID));

                if (quantity == 1) {
                    quantity = 10;
                } else {
                    quantity += 5;
                }

                price = numberFormat.format(numberFormat.parse(price).doubleValue() - 3);
            }
        } catch (final Exception e) {
            LOG.error("Exception occurred for overall import", e);
            Assert.fail(e.getMessage());
        }

        try {
            final Product product = (Product) impExImportReader.readLine();
            Assert.assertNotNull(product);
            final Collection<DistPriceRow> priceRows = europe1PriceFactory.getProductPriceRows(ctx, testProduct4,
                    europe1PriceFactory.getPPG(ctx, testProduct4));
            Assert.assertEquals(priceRows.size(), 3);

            long quantity = 1;
            String price = "50.5";
            for (final DistPriceRow priceRow : priceRows) {
                Assert.assertEquals(currency, priceRow.getCurrency());
                Assert.assertNotNull(priceRow.getDateRange());
                checkPrice(numberFormat, price, priceRow);
                Assert.assertEquals(quantity, priceRow.getMinQuantity());
                Assert.assertEquals(unit, priceRow.getUnit());
                Assert.assertEquals(1, priceRow.getUnitFactor().intValue());
                Assert.assertNull(priceRow.getUser());
                Assert.assertEquals(userPriceGroup1, priceRow.getUg());
                Assert.assertTrue(priceRow.isNetAsPrimitive());
                Assert.assertFalse(priceRow.isSpecialPrice());
                Assert.assertEquals("1000007", priceRow.getPriceConditionIdErp());
                Assert.assertEquals(cal.getTime(), priceRow.getLastModifiedErp());
                Assert.assertEquals(Long.valueOf(20130531142800000L), priceRow.getAttribute(PriceRowModel.SEQUENCEID));

                if (quantity == 1) {
                    quantity = 10;
                } else {
                    quantity += 5;
                }

                price = numberFormat.format(numberFormat.parse(price).doubleValue() - 3);
            }
        } catch (final Exception e) {
            LOG.error("Exception occurred for overall import", e);
            Assert.fail(e.getMessage());
        }
    }

    private void checkPrice(final NumberFormat numberFormat, final String priceAsString, final PriceRow priceRow) {
        try {
            Assert.assertEquals(numberFormat.parse(priceAsString).doubleValue(), priceRow.getPriceAsPrimitive(), 0.0D);
        } catch (final ParseException e) {
            throw new JaloSystemException(e);
        }
    }

    private Country getOrCreateCountry(String isoCode) throws JaloSystemException {
        Country ret = null;
        try {
            ret = C2LManager.getInstance().getCountryByIsoCode(isoCode);
        } catch (JaloItemNotFoundException localJaloItemNotFoundException) {
            try {
                ret = C2LManager.getInstance().createCountry(isoCode);
            } catch (ConsistencyCheckException e1) {
                throw new JaloSystemException(e1);
            }
        }
        return ret;
    }
}
