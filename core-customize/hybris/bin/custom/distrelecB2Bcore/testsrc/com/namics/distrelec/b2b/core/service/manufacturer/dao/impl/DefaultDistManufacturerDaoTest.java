/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.manufacturer.dao.impl;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.manufacturer.dao.DistManufacturerDao;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link DefaultDistManufacturerDao}.
 *
 * @author pbueschi, Namics AG
 */
@IntegrationTest
public class DefaultDistManufacturerDaoTest extends ServicelayerTransactionalTest {

    @Resource
    private DistManufacturerDao distManufacturerDao;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private DistSalesOrgService distSalesOrgService;

    @Before
    public void setUp() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/testDistManufacturers.impex", "utf-8");
    }

    @Test
    public void testFindManufacturers() {
        final List<DistManufacturerModel> distManufacturers = distManufacturerDao.findManufacturers();
        Assert.assertTrue(CollectionUtils.isNotEmpty(distManufacturers));
        Assert.assertTrue(distManufacturers.size() == 6);
    }

    @Test
    public void testFindManufactuereByCode() {
        final DistManufacturerModel distManufacturer = distManufacturerDao.findManufacturerByCode("testManufacturer1");
        Assert.assertNotNull(distManufacturer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindManufacturerByCodeNull() {
        distManufacturerDao.findManufacturerByCode(null);
    }

    @Test
    public void testFindManufacturerByCodeInvalid() {
        final DistManufacturerModel distManufacturer = distManufacturerDao.findManufacturerByCode("asdf");
        Assert.assertNull(distManufacturer);
    }

    @Test
    public void testFindManufacturerByUrlId() {
        final DistManufacturerModel distManufacturer = distManufacturerDao.findManufacturerByUrlId("test1");
        Assert.assertNotNull(distManufacturer);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindManufacturerByUrlIdNull() {
        distManufacturerDao.findManufacturerByUrlId(null);
    }

    @Test
    public void testFindManufacturerByUrlIdInvalid() {
        final DistManufacturerModel distManufacturer = distManufacturerDao.findManufacturerByUrlId("asdf");
        Assert.assertNull(distManufacturer);
    }

    @Test
    public void testFindManufacturerByCode() {
        final DistManufacturerModel manufacturer = distManufacturerDao.findManufacturerByCode("man_test1");
        final CountryModel country = commonI18NService.getCountry("CH");
        final DistManufacturerCountryModel manufacturerCountry = distManufacturerDao.findCountrySpecificManufacturerInformation(manufacturer, country);
        Assert.assertNotNull(manufacturerCountry);
    }

    @Test
    public void testFindManufacturerByCodeInvalidManufacturer() {
        final DistManufacturerModel manufacturer = distManufacturerDao.findManufacturerByCode("man_test2");
        final CountryModel country = commonI18NService.getCountry("CH");
        final DistManufacturerCountryModel manufacturerCountry = distManufacturerDao.findCountrySpecificManufacturerInformation(manufacturer, country);
        Assert.assertNull(manufacturerCountry);
    }

    @Test
    public void testFindManufacturerByCodeInvalidCountry() {
        final DistManufacturerModel manufacturer = distManufacturerDao.findManufacturerByCode("man_test1");
        final CountryModel country = commonI18NService.getCountry("DE");
        final DistManufacturerCountryModel manufacturerCountry = distManufacturerDao.findCountrySpecificManufacturerInformation(manufacturer, country);
        Assert.assertNull(manufacturerCountry);
    }

    @Test
    public void testFindManufacturersForRemovalFromCountry() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/testDistCountryManufacturer.impex", "utf-8");
        CountryModel testCountry = commonI18NService.getCountry("test");
        DistSalesOrgModel testSalesOrg = distSalesOrgService.getSalesOrgForCode("test");
        List<DistManufacturerModel> manufacturersForRemoval = distManufacturerDao.findManufacturersForRemovalFromCountry(testSalesOrg, testCountry);

        assertThat(manufacturersForRemoval.size())
                .withFailMessage("Incorrect number of manufacturers were removed")
                .isEqualTo(2);
        assertThat(manufacturersForRemoval)
                .withFailMessage("Manufacturer with punched out products was not removed")
                .filteredOn(manufacturer -> manufacturer.getName().equals("assignedAndHasPuchedOutActiveProducts"))
                .isEmpty();
        assertThat(manufacturersForRemoval)
                .withFailMessage("Manufacturer with no active products wasn't removed")
                .filteredOn(manufacturer -> manufacturer.getName().equals("assignedAndDoesntHaveActiveProducts"))
                .isEmpty();
    }

    @Test
    public void testFindManufacturersForAssignToCountry() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/testDistCountryManufacturer.impex", "utf-8");
        CountryModel testCountry = commonI18NService.getCountry("test");
        DistSalesOrgModel testSalesOrg = distSalesOrgService.getSalesOrgForCode("test");
        List<DistManufacturerModel> manufacturersForRemoval = distManufacturerDao.findManufacturersForAssignToCountry(testSalesOrg, testCountry);

        assertThat(manufacturersForRemoval.size())
                .withFailMessage("Incorrect number of manufacturers were assigned")
                .isEqualTo(2);
        assertThat(manufacturersForRemoval)
                .withFailMessage("Incorrect manufacturer was assigned")
                .filteredOn(manufacturer -> manufacturer.getName().equals("notAssignedAndHasActiveProducts"))
                .isNotNull();
        assertThat(manufacturersForRemoval)
                .withFailMessage("Manufacturer that is assigned to other country wasn't assigned")
                .filteredOn(manufacturer -> manufacturer.getName().equals("assignedToOtherCountry"))
                .isNotNull();
    }

}
