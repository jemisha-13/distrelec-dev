/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import com.namics.distrelec.b2b.core.model.jobs.DistInternalCronjobModel;

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

@ManualTest
public class DistMOQOverStockReportJobTest {

    @Mock
    private DistMOQOverStockReportDao DistMOQOverStockReportJobDao;

    @Mock
    private DistInternalCronjobModel distInternalCronjobModel;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private DistMOQOverStockReportJob distMOQOverStockReportJob;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void test() {
        final List<List<String>> mockedueryResult = Arrays.asList( //
                Arrays.asList("7320", "Distrelec AT", "18081785", "1", "0"), //
                Arrays.asList("7320", "Distrelec AT", "18090623", "2", "0"), //
                Arrays.asList("7320", "Distrelec AT", "16971607", "3", "1"), //
                Arrays.asList("7310", "Distrelec CH", "18081785", "4", "0"), //
                Arrays.asList("7310", "Distrelec CH", "30001808", "5", "1"), //
                Arrays.asList("7330", "Distrelec IT", "18081785", "1", "0"), //
                Arrays.asList("7330", "Distrelec IT", "18090623", "2", "0"), //
                Arrays.asList("7330", "Distrelec IT", "16971607", "3", "1"), //
                Arrays.asList("7330", "Distrelec IT", "30001808", "5", "1") //
        );
        Mockito.doReturn(mockedueryResult).when(DistMOQOverStockReportJobDao).getQueryResult();
        Mockito.doReturn("MOQ over Stock on {date}").when(distInternalCronjobModel).getEmailSubject();

        final PerformResult result = distMOQOverStockReportJob.perform(distInternalCronjobModel);

        assertEquals(CronJobResult.SUCCESS, result.getResult());
    }

}
