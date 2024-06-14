package com.namics.distrelec.b2b.core.service.manufacturer.impl;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.manufacturer.dao.DistManufacturerDao;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.annotation.Resource;

@IntegrationTest
public class DefaultDistManufacturerServiceIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private DefaultDistManufacturerService distManufacturerService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private DistSalesOrgService distSalesOrgService;

    @Resource
    private DistManufacturerDao distManufacturerDao;

    @Test
    public void testUpdateManufacturerIndexList() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/testDistCountryManufacturer.impex", "utf-8");

        CountryModel testCountry = commonI18NService.getCountry("test");
        DistSalesOrgModel testSalesOrg = distSalesOrgService.getSalesOrgForCode("test");

        boolean success = distManufacturerService.updateManufacturerIndexList(testCountry, testSalesOrg);

        assertThat(success)
                .withFailMessage("Expected to successfully update manufacturer per country relations")
                .isTrue();

        DistManufacturerModel assignedWithActiveProduct = distManufacturerDao.findManufacturerByCode("assignedAndHasActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(assignedWithActiveProduct, testCountry))
                .withFailMessage("Assigned manufacturer with assigned active products was removed")
                .isNotNull();

        DistManufacturerModel assignedWithInactiveProduct = distManufacturerDao.findManufacturerByCode("assignedAndDoesntHaveActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(assignedWithInactiveProduct, testCountry))
                .withFailMessage("Assigned manufacturer with assigned inactive products was not removed")
                .isNull();

        DistManufacturerModel assignedWithPunchedOutProduct = distManufacturerDao.findManufacturerByCode("assignedAndHasPuchedOutActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(assignedWithPunchedOutProduct, testCountry))
                .withFailMessage("Assigned manufacturer with assigned punched out products was not removed")
                .isNull();

        DistManufacturerModel notAssignedWithActiveProduct = distManufacturerDao.findManufacturerByCode("notAssignedAndHasActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(notAssignedWithActiveProduct, testCountry))
                .withFailMessage("Not Assigned manufacturer with active products was not assigned")
                .isNotNull();

        DistManufacturerModel notAssignedWithInactiveProduct = distManufacturerDao.findManufacturerByCode("notAssignedAndDoesntHaveActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(notAssignedWithInactiveProduct, testCountry))
                .withFailMessage("Not Assigned manufacturer with inactive products was assigned")
                .isNull();

        DistManufacturerModel notAssignedWithPunchedOutProduct = distManufacturerDao.findManufacturerByCode("notAssignedAndHasPuchedOutActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(notAssignedWithPunchedOutProduct, testCountry))
                .withFailMessage("Not Assigned manufacturer with punched out products was assigned")
                .isNull();

        DistManufacturerModel notAssignedAndAssignedOnOtherCountry = distManufacturerDao.findManufacturerByCode("assignedToOtherCountry");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(notAssignedAndAssignedOnOtherCountry, testCountry))
                .withFailMessage("Not Assigned manufacturer with with active produts assigned to other country was not assigned")
                .isNotNull();
    }

    @Test
    public void testUpdateManufacturerIndexListPartialFailure() throws ImpExException {
        importCsv("/distrelecB2Bcore/test/testDistCountryManufacturer.impex", "utf-8");

        CountryModel testCountry = commonI18NService.getCountry("test");
        DistSalesOrgModel testSalesOrg = distSalesOrgService.getSalesOrgForCode("test");


        DistManufacturerDao partialMockDistManufacturerDao = spy(distManufacturerDao);

        DistManufacturerModel assignedWithInactiveProduct = distManufacturerDao.findManufacturerByCode("assignedAndDoesntHaveActiveProducts");
        doThrow(new RuntimeException("Some exception")).when(partialMockDistManufacturerDao).findCountrySpecificManufacturerInformation(assignedWithInactiveProduct, testCountry);

        ReflectionTestUtils.setField(distManufacturerService, "distManufacturerDao", partialMockDistManufacturerDao);

        boolean success = distManufacturerService.updateManufacturerIndexList(testCountry, testSalesOrg);

        assertThat(success)
                .withFailMessage("Expected to fail update manufacturer per country relations")
                .isFalse();

        DistManufacturerModel assignedWithActiveProduct = distManufacturerDao.findManufacturerByCode("assignedAndHasActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(assignedWithActiveProduct, testCountry))
                .withFailMessage("Assigned manufacturer with assigned active products was removed")
                .isNotNull();

        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(assignedWithInactiveProduct, testCountry))
                .withFailMessage("Assigned manufacturer with assigned inactive products was not removed")
                .isNotNull();

        DistManufacturerModel assignedWithPunchedOutProduct = distManufacturerDao.findManufacturerByCode("assignedAndHasPuchedOutActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(assignedWithPunchedOutProduct, testCountry))
                .withFailMessage("Assigned manufacturer with assigned punched out products was not removed")
                .isNull();

        DistManufacturerModel notAssignedWithActiveProduct = distManufacturerDao.findManufacturerByCode("notAssignedAndHasActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(notAssignedWithActiveProduct, testCountry))
                .withFailMessage("Not Assigned manufacturer with active products was not assigned")
                .isNotNull();

        DistManufacturerModel notAssignedWithInactiveProduct = distManufacturerDao.findManufacturerByCode("notAssignedAndDoesntHaveActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(notAssignedWithInactiveProduct, testCountry))
                .withFailMessage("Not Assigned manufacturer with inactive products was assigned")
                .isNull();

        DistManufacturerModel notAssignedWithPunchedOutProduct = distManufacturerDao.findManufacturerByCode("notAssignedAndHasPuchedOutActiveProducts");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(notAssignedWithPunchedOutProduct, testCountry))
                .withFailMessage("Not Assigned manufacturer with punched out products was assigned")
                .isNull();

        DistManufacturerModel notAssignedAndAssignedOnOtherCountry = distManufacturerDao.findManufacturerByCode("assignedToOtherCountry");
        assertThat(distManufacturerDao.findCountrySpecificManufacturerInformation(notAssignedAndAssignedOnOtherCountry, testCountry))
                .withFailMessage("Not Assigned manufacturer with with active produts assigned to other country was not assigned")
                .isNotNull();
    }

    public DefaultDistManufacturerService getDistManufacturerService() {
        return distManufacturerService;
    }

    public void setDistManufacturerService(DefaultDistManufacturerService distManufacturerService) {
        this.distManufacturerService = distManufacturerService;
    }
}
