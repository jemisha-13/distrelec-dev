package com.namics.distrelec.b2b.core.missingorders.job;

import com.namics.distrelec.b2b.core.missingorders.service.CreateMissingOrdersResult;
import com.namics.distrelec.b2b.core.missingorders.service.DistMissingOrdersService;
import com.namics.distrelec.b2b.core.missingorders.service.MissingOrdersMatchResult;
import com.namics.distrelec.b2b.core.model.jobs.DistSapMissingOrdersCronJobModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.*;

@UnitTest
public class DistSapMissingOrdersJobTest {

    @InjectMocks
    private DistSapMissingOrdersJob distSapMissingOrdersJob;

    @Mock
    private DistMissingOrdersService distMissingOrdersService;

    @Mock
    private DistSapMissingOrdersCronJobModel cronJob;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(cronJob.getNumberOfDays())
                .thenReturn(1);
        Date fromDate = new Date();
        when(cronJob.getOrdersFromDate())
                .thenReturn(fromDate);
        Date toDate = new Date();
        when(cronJob.getOrdersToDate())
                .thenReturn(toDate);
        when(cronJob.isFetchOrdersByDays())
                .thenReturn(true);
    }

    @Test
    public void testPerformOk() {
        DistSapMissingOrdersCronJobModel cronJob = mock(DistSapMissingOrdersCronJobModel.class);
        when(cronJob.getNumberOfDays())
                .thenReturn(1);
        Date fromDate = new Date();
        when(cronJob.getOrdersFromDate())
                .thenReturn(fromDate);
        Date toDate = new Date();
        when(cronJob.getOrdersToDate())
                .thenReturn(toDate);
        when(cronJob.isFetchOrdersByDays())
                .thenReturn(true);

        MissingOrdersMatchResult matchResult = mock(MissingOrdersMatchResult.class);
        OrderModel matchedOrder = mock(OrderModel.class);
        OrderModel missingOrderSuccess = mock(OrderModel.class);
        OrderModel missingOrderFail = mock(OrderModel.class);
        when(matchResult.getOrdersFoundInErp())
                .thenReturn(Collections.singletonList(matchedOrder));
        when(matchResult.getOrdersMissingInErp())
                .thenReturn(Arrays.asList(missingOrderSuccess, missingOrderFail));

        when(distMissingOrdersService.matchMissingOrders(eq(1), eq(true), eq(fromDate), eq(toDate)))
                .thenReturn(matchResult);

        CreateMissingOrdersResult createResult = mock(CreateMissingOrdersResult.class);
        when(createResult.getSuccessfullyCreatedOrders())
                .thenReturn(Collections.singletonList(missingOrderSuccess));
        when(createResult.getFailedOrders())
                .thenReturn(Collections.singletonList(missingOrderFail));

        when(distMissingOrdersService.sendReportEmail(eq(Collections.singletonList(matchedOrder)),
                eq(Collections.singletonList(missingOrderSuccess)),
                eq(Collections.singletonList(missingOrderFail))))
                .thenReturn(true);

        when(distMissingOrdersService.createSapOrders(eq(Arrays.asList(missingOrderSuccess, missingOrderFail))))
                .thenReturn(createResult);

        PerformResult result = distSapMissingOrdersJob.perform(cronJob);
        verify(distMissingOrdersService, times(1)).matchMissingOrders(eq(1), eq(true), eq(fromDate), eq(toDate));
        assertThat(result.getResult())
                .isEqualTo(CronJobResult.SUCCESS);
        assertThat(result.getStatus())
                .isEqualTo(CronJobStatus.FINISHED);
    }

    @Test
    public void testPerformFailure() {
        when(distMissingOrdersService.matchMissingOrders(anyInt(), anyBoolean(), any(), any()))
                .thenThrow(new RuntimeException("Something happened :("));

        PerformResult result = distSapMissingOrdersJob.perform(cronJob);

        assertThat(result.getResult())
                .isEqualTo(CronJobResult.FAILURE);
        assertThat(result.getStatus())
                .isEqualTo(CronJobStatus.FINISHED);
    }

    @Test
    public void testPerformEmailSendingFailure() {
        DistSapMissingOrdersCronJobModel cronJob = mock(DistSapMissingOrdersCronJobModel.class);
        when(cronJob.getNumberOfDays())
                .thenReturn(1);
        Date fromDate = new Date();
        when(cronJob.getOrdersFromDate())
                .thenReturn(fromDate);
        Date toDate = new Date();
        when(cronJob.getOrdersToDate())
                .thenReturn(toDate);
        when(cronJob.isFetchOrdersByDays())
                .thenReturn(true);

        MissingOrdersMatchResult matchResult = mock(MissingOrdersMatchResult.class);
        OrderModel matchedOrder = mock(OrderModel.class);
        OrderModel missingOrderSuccess = mock(OrderModel.class);
        OrderModel missingOrderFail = mock(OrderModel.class);
        when(matchResult.getOrdersFoundInErp())
                .thenReturn(Collections.singletonList(matchedOrder));
        when(matchResult.getOrdersMissingInErp())
                .thenReturn(Arrays.asList(missingOrderSuccess, missingOrderFail));

        when(distMissingOrdersService.matchMissingOrders(eq(1), eq(true), eq(fromDate), eq(toDate)))
                .thenReturn(matchResult);

        CreateMissingOrdersResult createResult = mock(CreateMissingOrdersResult.class);
        when(createResult.getSuccessfullyCreatedOrders())
                .thenReturn(Collections.singletonList(missingOrderSuccess));
        when(createResult.getFailedOrders())
                .thenReturn(Collections.singletonList(missingOrderFail));

        when(distMissingOrdersService.sendReportEmail(eq(Collections.singletonList(matchedOrder)),
                eq(Collections.singletonList(missingOrderSuccess)),
                eq(Collections.singletonList(missingOrderFail))))
                .thenReturn(false);

        when(distMissingOrdersService.createSapOrders(eq(Arrays.asList(missingOrderSuccess, missingOrderFail))))
                .thenReturn(createResult);

        PerformResult result = distSapMissingOrdersJob.perform(cronJob);
        verify(distMissingOrdersService, times(1)).matchMissingOrders(eq(1), eq(true), eq(fromDate), eq(toDate));
        assertThat(result.getResult())
                .isEqualTo(CronJobResult.FAILURE);
        assertThat(result.getStatus())
                .isEqualTo(CronJobStatus.FINISHED);
    }

}
