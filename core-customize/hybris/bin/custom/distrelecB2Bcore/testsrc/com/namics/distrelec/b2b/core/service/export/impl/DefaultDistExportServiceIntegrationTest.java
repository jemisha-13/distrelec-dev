/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.export.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.export.DistExportService;
import com.namics.distrelec.b2b.core.service.export.data.AbstractDistExportData;
import com.namics.distrelec.b2b.core.service.export.data.DistCartExportData;
import com.namics.distrelec.b2b.core.service.export.data.DistProductExportData;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

/**
 * Test class for {@link DefaultDistExportService}.
 * 
 * @author pbueschi, Namics AG
 * 
 */
@IntegrationTest
public class DefaultDistExportServiceIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private DistExportService distExportService;

    private AbstractDistExportData distProductExportData;
    private AbstractDistExportData distProductExportData2;
    private AbstractDistExportData distProductExportData3;

    private DistCartExportData distCartExportData;

    @Before
    public void setUp() throws Exception {
        distProductExportData = new DistProductExportData();
        distProductExportData.setCode("2078456");
        distProductExportData.setManufacturerName("dell");
        distProductExportData.setName("dell product");
        distProductExportData.setStockLevel(Integer.valueOf(42));
        distProductExportData.setTypeName("dell type");
        final Map<Long, String> volumePrices = new HashMap<Long, String>();
        volumePrices.put(Long.valueOf(1), "CHF 42.-");
        volumePrices.put(Long.valueOf(5), "CHF 32.-");
        volumePrices.put(Long.valueOf(10), "CHF 22.-");
        distProductExportData.setVolumePrices(volumePrices);

        distProductExportData2 = new DistProductExportData();
        distProductExportData2.setCode("5710895");
        distProductExportData2.setManufacturerName("hp");
        distProductExportData2.setName("hp product");
        distProductExportData2.setStockLevel(Integer.valueOf(42));
        distProductExportData2.setTypeName("hp type");
        final Map<Long, String> volumePrices2 = new HashMap<Long, String>();
        volumePrices2.put(Long.valueOf(3), "CHF 42.-");
        volumePrices2.put(Long.valueOf(7), "CHF 32.-");
        distProductExportData2.setVolumePrices(volumePrices2);

        distProductExportData3 = new DistProductExportData();
        distProductExportData3.setCode("8908640");
        distProductExportData3.setManufacturerName("acer");
        distProductExportData3.setName("acer product");
        distProductExportData3.setStockLevel(Integer.valueOf(42));
        distProductExportData3.setTypeName("acer type");

        distCartExportData = new DistCartExportData();
        BeanUtils.copyProperties(distCartExportData, distProductExportData3);
        distCartExportData.setQuantity(Long.valueOf(2));
        distCartExportData.setMySinglePrice("CHF 42.-");
        distCartExportData.setMySubtotal("CHF 84.-");
    }

    @Test
    public void testGetDownloadUrlForProductExportFileXls() {
        final List<AbstractDistExportData> distProductExportDataList = new ArrayList<AbstractDistExportData>();
        distProductExportDataList.add(distProductExportData);
        distProductExportDataList.add(distProductExportData2);
        distProductExportDataList.add(distProductExportData3);
        final File downloadFile = distExportService.getDownloadExportFile(distProductExportDataList, DistConstants.Export.FORMAT_XLS, "productXlsTest");
        Assert.assertNotNull(downloadFile);
    }

    @Test
    public void testGetDownloadUrlForProductExportFileCsv() {
        final List<AbstractDistExportData> distProductExportDataList = new ArrayList<AbstractDistExportData>();
        distProductExportDataList.add(distProductExportData);
        distProductExportDataList.add(distProductExportData2);
        distProductExportDataList.add(distProductExportData3);
        final File downloadFile = distExportService.getDownloadExportFile(distProductExportDataList, DistConstants.Export.FORMAT_CSV, "productCsvTest");
        Assert.assertNotNull(downloadFile);
    }

    @Test
    public void testGetDownloadUrlForCartExportFileXls() {
        final List<DistCartExportData> distCartExportDataList = new ArrayList<DistCartExportData>();
        distCartExportDataList.add(distCartExportData);
        final File downloadFile = distExportService.getDownloadExportFile(distCartExportDataList, DistConstants.Export.FORMAT_XLS, "cartXlsTest");
        Assert.assertNotNull(downloadFile);
    }

    @Test
    public void testGetDownloadUrlForCartExportFileCsv() {
        final List<DistCartExportData> distCartExportDataList = new ArrayList<DistCartExportData>();
        distCartExportDataList.add(distCartExportData);
        final File downloadFile = distExportService.getDownloadExportFile(distCartExportDataList, DistConstants.Export.FORMAT_CSV, "cartCsvTest");
        Assert.assertNotNull(downloadFile);
    }
}
