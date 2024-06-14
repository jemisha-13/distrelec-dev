package com.distrelec.job;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@IntegrationTest
public class PimWebUseMigrationJobIntegrationTest extends ServicelayerTransactionalTest {

    public static final String DISTRELECFUSIONINTEGRATION_PIMWEBUSEMIGRATION_LANGUAGES = "distrelecfusionintegration.pimwebusemigration.languages";

    public static final String DISTRELECFUSIONINTEGRATION_PIMWEBUSEMIGRATION_FULLMIGRATION = "distrelecfusionintegration.pimwebusemigration.fullmigration";

    private static final String PRODUCT_ONE_CODE = "test1";

    @Resource
    private PimWebUseMigrationJob pimWebUseMigrationJob;

    @Resource
    private ModelService modelService;

    @Resource
    private ProductService productService;

    private CronJobModel cronJob;

    private String expectedPimWebUseJson;

    /**
     * Using only one product and language because job is using parallel streams
     * and I get de.hybris.platform.util.jeeapi.YNoSuchEntityException for the second product.
     */
    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bcore/test/testErpCodelist.impex", "utf-8");
        importCsv("/distrelecfusionintegration/test/pimWebUseMigrationJob.impex", "utf-8");

        cronJob = modelService.create(CronJobModel.class);

        Config.setParameter(DISTRELECFUSIONINTEGRATION_PIMWEBUSEMIGRATION_LANGUAGES, "en");
        Config.setParameter(DISTRELECFUSIONINTEGRATION_PIMWEBUSEMIGRATION_FULLMIGRATION, "true");

        setExpectedPimWebUseJson();
    }

    private void setExpectedPimWebUseJson() {
        StringBuilder jsonString = new StringBuilder();
        jsonString.append("[")
                  .append("{")
                  .append("\"code\":\"attr1\",")
                  .append("\"attributeName\":\"name1_en\",")
                  .append("\"value\":\"value1_en\",")
                  .append("\"unit\":\"\",")
                  .append("\"fieldType\":\"string\"")
                  .append("},")
                  .append("{")
                  .append("\"code\":\"attr2\",")
                  .append("\"attributeName\":\"name2_en\",")
                  .append("\"value\":\"1\",")
                  .append("\"unit\":\"vlt\",")
                  .append("\"fieldType\":\"double\"")
                  .append("},")
                  .append("{")
                  .append("\"code\":\"attr5\",")
                  .append("\"attributeName\":\"name5_en\",")
                  .append("\"value\":\"5\",")
                  .append("\"unit\":\"MMT\",")
                  .append("\"fieldType\":\"string\"")
                  .append("},")
                  .append("{")
                  .append("\"code\":\"attr3\",")
                  .append("\"attributeName\":\"name3_en\",")
                  .append("\"value\":\"value3_en\",")
                  .append("\"unit\":\"\",")
                  .append("\"fieldType\":\"string\"")
                  .append("}")
                  .append("]");
        expectedPimWebUseJson = jsonString.toString();
    }

    @Test
    public void testPerform() {
        PerformResult performResult = pimWebUseMigrationJob.perform(cronJob);

        assertThat(performResult.getStatus(), is(CronJobStatus.FINISHED));
        assertThat(performResult.getResult(), is(CronJobResult.SUCCESS));

        ProductModel product = productService.getProductForCode(PRODUCT_ONE_CODE);
        assertThat(product, notNullValue());
        assertThat(product.getPimWebUseJson(), notNullValue());
        assertThat(product.getPimWebUseJson(), is(expectedPimWebUseJson));
    }

    @Test
    public void testPerformDelta() {
        Config.setParameter(DISTRELECFUSIONINTEGRATION_PIMWEBUSEMIGRATION_FULLMIGRATION, "false");
        ProductModel product = productService.getProductForCode(PRODUCT_ONE_CODE);
        product.setPimWebUse("value");
        modelService.save(product);

        PerformResult performResult = pimWebUseMigrationJob.perform(cronJob);

        assertThat(performResult.getStatus(), is(CronJobStatus.FINISHED));
        assertThat(performResult.getResult(), is(CronJobResult.SUCCESS));

        product = productService.getProductForCode(PRODUCT_ONE_CODE);
        assertThat(product, notNullValue());
        assertThat(product.getPimWebUseJson(), notNullValue());
        assertThat(product.getPimWebUseJson(), is(expectedPimWebUseJson));
    }

    @Test
    public void testPerformDeltaNoProducts() {
        Config.setParameter(DISTRELECFUSIONINTEGRATION_PIMWEBUSEMIGRATION_FULLMIGRATION, "false");
        ProductModel product = productService.getProductForCode(PRODUCT_ONE_CODE);
        String existingPimWebUseJson = "[{\"field\":\"value\"}]";
        product.setPimWebUseJson(existingPimWebUseJson);
        modelService.save(product);

        PerformResult performResult = pimWebUseMigrationJob.perform(cronJob);

        assertThat(performResult.getStatus(), is(CronJobStatus.FINISHED));
        assertThat(performResult.getResult(), is(CronJobResult.SUCCESS));

        product = productService.getProductForCode(PRODUCT_ONE_CODE);
        assertThat(product, notNullValue());
        assertThat(product.getPimWebUseJson(), notNullValue());
        assertThat(product.getPimWebUseJson(), is(existingPimWebUseJson));
    }

}
