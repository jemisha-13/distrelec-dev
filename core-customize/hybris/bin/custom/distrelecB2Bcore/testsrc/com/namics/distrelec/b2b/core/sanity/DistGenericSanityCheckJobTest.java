/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.sanity;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.enums.GenericSanityCheckType;
import com.namics.distrelec.b2b.core.model.jobs.DistGenericSanityCheckCronJobModel;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Tests the {@link DistGenericSanityCheckJob} class.
 * 
 * @author pforster, Namics AG
 * @since Distrelec 3.0.0
 * 
 */
public class DistGenericSanityCheckJobTest extends ServicelayerTest {

    @Resource
    private CronJobService cronJobService;

    @Resource
    private ModelService modelService;

    @Resource
    private UserService userService;

    @Resource
    private CommonI18NService commonI18NService;

    private DistGenericSanityCheckCronJobModel cronJobModel;

    @Before
    public void setUp() throws Exception {
        final LanguageModel lang = commonI18NService.getLanguage("en");

        final ServicelayerJobModel jobModel = modelService.create(ServicelayerJobModel.class);
        jobModel.setCode("distGenericSanityCheckTestJob");
        jobModel.setSpringId("distGenericSanityCheckJob");
        modelService.save(jobModel);

        cronJobModel = modelService.create(DistGenericSanityCheckCronJobModel.class);
        cronJobModel.setCode("distGenericSanityCheckJobUnitTest");
        cronJobModel.setQueryLanguage(lang);
        cronJobModel.setSendEmail(Boolean.FALSE);
        cronJobModel.setBaseQuery(getQueryReturningScalar(Double.valueOf(10000)));
        cronJobModel.setJob(jobModel);
        cronJobModel.setQueryLanguage(lang);
        cronJobModel.setActive(Boolean.TRUE);
        cronJobModel.setSessionUser(userService.getUserForUID("admin"));
        cronJobModel.setSessionLanguage(lang);
    }

    @Test
    public void testPerformAbsoluteSanityCheckSuccess() {
        executeTest(GenericSanityCheckType.ABSOLUTSANITYCHECK, Double.valueOf(200), getQueryReturningScalar(Double.valueOf(199)), CronJobResult.SUCCESS,
                CronJobStatus.FINISHED);
    }

    @Test
    public void testPerformAbsoluteSanityCheckSuccessEqualsThreshold() {
        executeTest(GenericSanityCheckType.ABSOLUTSANITYCHECK, Double.valueOf(200), getQueryReturningScalar(Double.valueOf(200)), CronJobResult.SUCCESS,
                CronJobStatus.FINISHED);
    }

    @Test
    public void testPerformAbsoluteSanityCheckFail() {
        executeTest(GenericSanityCheckType.ABSOLUTSANITYCHECK, Double.valueOf(200), getQueryReturningScalar(Double.valueOf(201)), CronJobResult.FAILURE,
                CronJobStatus.FINISHED);
    }

    @Test
    public void testPerformPercentageSanityCheckSuccess() {
        executeTest(GenericSanityCheckType.PERCENTAGESANITYCHECK, Double.valueOf(10), getQueryReturningScalar(Double.valueOf(999)), CronJobResult.SUCCESS,
                CronJobStatus.FINISHED);
    }

    @Test
    public void testPerformPercentageSanityCheckSuccessEqualsThreshold() {
        executeTest(GenericSanityCheckType.PERCENTAGESANITYCHECK, Double.valueOf(10), getQueryReturningScalar(Double.valueOf(1000)), CronJobResult.SUCCESS,
                CronJobStatus.FINISHED);
    }

    @Test
    public void testPerformPercentageSanityCheckFail() {
        executeTest(GenericSanityCheckType.PERCENTAGESANITYCHECK, Double.valueOf(10), getQueryReturningScalar(Double.valueOf(1001)), CronJobResult.FAILURE,
                CronJobStatus.FINISHED);
    }

    @Test
    public void testPerformPercentageSanityCheckDivByZero() {

        setValuesToModel(GenericSanityCheckType.PERCENTAGESANITYCHECK, Double.valueOf(10), getQueryReturningScalar(Double.valueOf(1001)));
        cronJobModel.setBaseQuery(getQueryReturningScalar(Double.valueOf(0)));
        modelService.save(cronJobModel);

        cronJobService.performCronJob(cronJobModel, true);

        Assert.assertEquals(CronJobResult.ERROR, cronJobModel.getResult());
        Assert.assertEquals(CronJobStatus.ABORTED, cronJobModel.getStatus());
    }

    private void executeTest(final GenericSanityCheckType type, final Double threshold, final String checkQuery, final CronJobResult expectedResult,
            final CronJobStatus expectedStatus) {
        setValuesToModel(type, threshold, checkQuery);

        cronJobService.performCronJob(cronJobModel, true);

        Assert.assertEquals(expectedResult, cronJobModel.getResult());
        Assert.assertEquals(expectedStatus, cronJobModel.getStatus());
    }

    private void setValuesToModel(final GenericSanityCheckType type, final Double threshold, final String checkQuery) {
        cronJobModel.setType(type);
        cronJobModel.setThreshold(threshold);
        cronJobModel.setCheckQuery(checkQuery);
        cronJobModel.setBaseQuery(getQueryReturningScalar(Double.valueOf(10000)));
        modelService.save(cronJobModel);
    }

    /**
     * This function uses a dummy query to return one single scalar. The user information is only used to get one single row returned.
     * 
     * @param scalar
     *            the scalar to select
     * @return the query which returns the param scalar
     */
    private String getQueryReturningScalar(final Double scalar) {
        return "SELECT " + scalar + " AS returnValue FROM {" + UserModel._TYPECODE + "} WHERE {" + UserModel.UID + "} = 'admin'";
    }

}
