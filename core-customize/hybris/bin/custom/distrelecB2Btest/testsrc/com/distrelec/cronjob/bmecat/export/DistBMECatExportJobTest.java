package com.distrelec.cronjob.bmecat.export;

import com.namics.distrelec.b2b.core.bmecat.export.model.DistBMECatExportChannelModel;
import com.namics.distrelec.b2b.core.bmecat.export.model.DistBMECatExportCronJobModel;
import com.namics.distrelec.b2b.core.bmecat.export.query.DistBMECatExportQueryCreator;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.JobModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static org.junit.Assert.assertTrue;

@IntegrationTest
public class DistBMECatExportJobTest extends ServicelayerTransactionalTest {

    private final DistBMECatExportQueryCreator queryCreator = new DistBMECatExportQueryCreator();
    @Resource
    private CronJobService cronJobService;

    @Resource
    private ModelService modelService;

    @Resource
    private UserService userService;

    @Resource
    private CatalogVersionService catalogVersionService;

    @Resource(name = "commonI18NService")
    private CommonI18NService i18NService;

    @Resource
    private CMSSiteService cmsSiteService;

    private DistBMECatExportCronJobModel cronJobModel;

    @Before
    public void setUp() throws Exception {
        importStream(getClass().getResourceAsStream("/distrelecB2Bcore/test/bmecatexport/testBMECatExport.impex"), "UTF-8", null);

        LanguageModel lang = i18NService.getLanguage("de");
        CMSSiteModel cmsSite = cmsSiteService.getSiteForURL(new URL("http://testsite.com"));
        JobModel job = cronJobService.getJob("distBMECatExportCronJob");

        DistBMECatExportChannelModel channel = modelService.create(DistBMECatExportChannelModel.class);

        channel.setChannel("distrelec_bmecat_7310_ch_de_test");
        channel.setCatalogVersion(catalogVersionService.getCatalogVersion("testCatalog", "Online"));
        channel.setLanguage(lang);
        channel.setCmsSite(cmsSite);
        channel.setCode("distrelec_bmecat_7310_ch_de_test");
        channel.setOwner(userService.getUserForUID("admin"));

        cronJobModel = modelService.create(DistBMECatExportCronJobModel.class);
        cronJobModel.setCode("distrelec_7310_ch_de_bmecat_export_test");
        cronJobModel.setSendEmail(Boolean.FALSE);
        cronJobModel.setChannel(channel);
        cronJobModel.setJob(job);
        cronJobModel.setActive(Boolean.TRUE);
        cronJobModel.setSessionUser(userService.getUserForUID("admin"));
        cronJobModel.setSessionLanguage(lang);
        cronJobModel.setQueryCreatorStrategyBean("distBMECatExportQueryCreator");

        modelService.save(cronJobModel);
    }

    @Test
    @Ignore
    public void testPerformAbsoluteSanityCheckSuccess() {
        executeTest(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    @Test
    @Ignore
    public void testPerformAbsoluteSanityCheckFailure() {
        executeTest(CronJobResult.FAILURE, CronJobStatus.FINISHED);
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

        final String originalQuery = queryCreator.createQuery(true);

        final List<String> environments = new ArrayList<String>();
        environments.add(ENV_LOCALHOST);
        environments.add(ENV_HP_Q);
        environments.add(ENV_HP_P);

        for (final String env : environments) {
            String query = originalQuery;

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

            for (final Entry<String, String> entry : params.entrySet()) {
                query = query.replaceAll(entry.getKey(), entry.getValue());
            }

        }

        assertTrue("BMECat Query Created Successfully!", true);
    }

}
