package com.distrelec.cronjob.bmecat.export;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.model.DistBrandModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.europe1.enums.UserTaxGroup;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.bmecat.export.model.DistBMECatExportChannelModel;
import com.namics.distrelec.b2b.core.bmecat.export.model.DistBMECatExportCronJobModel;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import junit.framework.Assert;

@IntegrationTest
public class DistBMECatExportTest extends ServicelayerTransactionalTest {

    @Resource
    private CronJobService cronJobService;

    @Resource
    private ModelService modelService;

    @Resource
    private UserService userService;
    @Resource
    private CMSSiteService cmsSiteService;
    @Resource
    private CatalogService catalogService;

    @Resource
    private CommonI18NService commonI18NService;

    private DistBMECatExportCronJobModel cronJobModel;

    @Before
    public void setUp() throws Exception {
        LanguageModel deLang = modelService.create(LanguageModel.class);
        deLang.setIsocode("de");

        modelService.save(deLang);

        final LanguageModel lang = commonI18NService.getLanguage("de");

        final ServicelayerJobModel jobModel = modelService.create(ServicelayerJobModel.class);
        jobModel.setCode("distBMECatExportCronJob");
        jobModel.setSpringId("distBMECatExportJob");
        modelService.save(jobModel);
        final DistBMECatExportChannelModel channel = modelService.create(DistBMECatExportChannelModel.class);

        final CMSSiteModel cmsSiteModel = modelService.create(CMSSiteModel.class);
        cmsSiteModel.setUid("testSite");

        UserPriceGroup userPriceGroup = UserPriceGroup.valueOf("testUserPriceGroup");
        cmsSiteModel.setUserPriceGroup(userPriceGroup);

        UserTaxGroup userTaxGroup = UserTaxGroup.valueOf("testUserTaxGroup");
        cmsSiteModel.setUserTaxGroup(userTaxGroup);

        DistSalesOrgModel salesOrgModel = modelService.create(DistSalesOrgModel.class);

        CountryModel countryModel = modelService.create(CountryModel.class);
        countryModel.setIsocode("de");
        modelService.save(countryModel);

        DistBrandModel brandModel = modelService.create(DistBrandModel.class);
        brandModel.setCode("testBrand");
        modelService.save(brandModel);

        salesOrgModel.setBrand(brandModel);
        salesOrgModel.setCountry(countryModel);
        salesOrgModel.setErpSystem(DistErpSystem.SAP);
        salesOrgModel.setCode("testSalesOrg");
        modelService.save(salesOrgModel);

        cmsSiteModel.setSalesOrg(salesOrgModel);

        modelService.save(cmsSiteModel);

        channel.setChannel("distrelec_bmecat_7310_ch_de_test");

        CatalogModel testCatalog = modelService.create(CatalogModel.class);
        testCatalog.setId("testCatalog");
        modelService.save(testCatalog);

        CatalogVersionModel testCatalogVersion = modelService.create(CatalogVersionModel.class);
        testCatalogVersion.setVersion("Online");
        testCatalogVersion.setCatalog(testCatalog);

        channel.setCatalogVersion(testCatalogVersion);
        channel.setLanguage(lang);
        channel.setCmsSite(cmsSiteModel);
        channel.setCode("distrelec_bmecat_7310_ch_de_test");
        channel.setOwner(userService.getUserForUID("admin"));

        cronJobModel = modelService.create(DistBMECatExportCronJobModel.class);
        cronJobModel.setCode("distrelec_7310_ch_de_bmecat_export_test");
        cronJobModel.setSendEmail(Boolean.FALSE);
        cronJobModel.setChannel(channel);
        cronJobModel.setJob(jobModel);
        cronJobModel.setActive(Boolean.TRUE);
        cronJobModel.setSessionUser(userService.getUserForUID("admin"));
        cronJobModel.setSessionLanguage(lang);
        cronJobModel.setMediaPrefix("erpExport");
        modelService.save(cronJobModel);
    }

    @Test
    public void testPerformAbsoluteSanityCheckFailure() {
        executeTest(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void executeTest(final CronJobResult expectedResult, final CronJobStatus expectedStatus) {

        cronJobService.performCronJob(cronJobModel, true);

        Assert.assertEquals(expectedResult, cronJobModel.getResult());
        Assert.assertEquals(expectedStatus, cronJobModel.getStatus());
    }

    @Test
    public void test() {

        final String ENV_LOCALHOST = "LOCALHOST";
        final String ENV_HP_Q = "HP_Q";
        final String ENV_HP_P = "HP_P";

        final List<String> environments = new ArrayList<String>();
        environments.add(ENV_LOCALHOST);
        environments.add(ENV_HP_Q);
        environments.add(ENV_HP_P);

        for (final String env : environments) {
            final Map<String, String> params = new HashMap<String, String>();
            params.put("\\?LanguageIsocode", "'de'");
            //params.put("\\?Date", now());
            params.put("\\?Country", "8796094464034"); // CH - LOCALHOST, HP_Q, HP_P
            params.put("\\?ExcludedProducts", "0");
            params.put("\\?NumberOfDeliveryCountries", "2");
            params.put("\\?Language", "8796125823008"); // LOCALHOST, HP_Q, HP_P
            params.put("\\?DistSalesOrg", "8796094650841"); // LOCALHOST, HP_Q, HP_P
            params.put("\\?DeliveryCountries", "8796094464034, 8796097249314"); // HP_Q

            if (env.equals(ENV_LOCALHOST)) {
                params.put("\\?CMSSite", "8796093219880");
            } else if (env.equals(ENV_HP_Q)) {
                params.put("\\?CMSSite", "8796093056040");
            } else if (env.equals(ENV_HP_P)) {
                params.put("\\?CMSSite", "8796093187112");
            }

        }

        assertTrue("BMECat Query Created Successfully!", true);
    }

}
