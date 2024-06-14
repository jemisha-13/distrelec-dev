/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.codelist.job;

import static junit.framework.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.model.jobs.UpdateCodelistsCronJobModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;

import de.hybris.platform.core.Registry;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * Base class for testing of update codelist cron jobs.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 * 
 */
@Ignore
public abstract class AbstractCronJobTest extends ServicelayerTest {

    private static final Logger LOG = Logger.getLogger(AbstractCronJobTest.class);

    static {
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2014, 1 - 1, 23, 18, 30, 00);
        EARLIER_DATE = cal.getTime();
        cal.set(2014, 1 - 1, 23, 18, 45, 00);
        LATER_DATE = cal.getTime();
    }

    protected static final Date EARLIER_DATE;

    protected static final Date LATER_DATE;

    protected static final long EARLIER_SAP_DATE = 20140123183000l;

    protected static final long LATER_SAP_DATE = 20140123184500l;

    @Mock
    protected SIHybrisV1Out webServiceClient;

    @Resource
    private CronJobService cronJobService;

    @Resource
    protected ModelService modelService;

    @Resource
    protected FlexibleSearchService flexibleSearchService;

    @Resource(name = "distCodelistService")
    protected DistrelecCodelistService distrelecCodelistService;

    private UpdateCodelistsCronJobModel updateCodelistCronJob;

    @Before
    public void setUp() throws Exception {
        createCoreData();
        final String beanID = "updateCodelistsJob";
        createCronJob(beanID);
        useMockedWebServiceClientInCronJob(beanID);
        // Clean data
        modelService.removeAll(distrelecCodelistService.getAllDistrelecSalesStatus());
        modelService.removeAll(distrelecCodelistService.getAllDistrelecTransportGroup());
        modelService.removeAll(distrelecCodelistService.getAllDistFunctions());
        modelService.removeAll(distrelecCodelistService.getAllDistrelecShippingMethod());
    }

    private void createCronJob(final String beanID) {
        final ServicelayerJobModel sjm = new ServicelayerJobModel();
        sjm.setSpringId(beanID);
        ServicelayerJobModel servicelayerJobModel;
        try {
            servicelayerJobModel = flexibleSearchService.getModelByExample(sjm);
        } catch (ModelNotFoundException e) {
            servicelayerJobModel = modelService.create(ServicelayerJobModel.class);
            servicelayerJobModel.setSpringId(beanID);
            servicelayerJobModel.setCode(beanID);
            modelService.save(servicelayerJobModel);
        }
        updateCodelistCronJob = modelService.create(UpdateCodelistsCronJobModel.class);
        updateCodelistCronJob.setActive(Boolean.TRUE);
        updateCodelistCronJob.setJob(servicelayerJobModel);
        modelService.save(updateCodelistCronJob);
    }

    private void useMockedWebServiceClientInCronJob(final String beanID) {
        MockitoAnnotations.initMocks(this);
        ((UpdateCodelistsJob) Registry.getApplicationContext().getBean(beanID)).setWebServiceClient(webServiceClient);
    }

    protected void executeCronJobAndWait(final int waitSeconds) {
        cronJobService.performCronJob(updateCodelistCronJob);
        try {
            Thread.sleep(1000 * waitSeconds);
        } catch (final InterruptedException e) {
            LOG.error("interrupted: ", e);
        }
    }

    protected void assertCronJobSuccess(final int waitSeconds) {
        // test if the job was executed successfully, if it fails here try to extend the wait time
        assertEquals("the cron job has not finished successfully within " + waitSeconds + "seconds", CronJobResult.SUCCESS, updateCodelistCronJob.getResult());
    }

}