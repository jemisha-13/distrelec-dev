package com.distrelec.job;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.distrelec.enums.FusionMigrationAuditStatus;
import com.distrelec.enums.FusionMigrationAuditType;
import com.distrelec.fusion.integration.dto.MigrationStatusRequestDTO;
import com.distrelec.solrfacetsearch.model.jobs.FusionMigrationAuditSendCronJobModel;
import com.distrelec.solrfacetsearch.service.FusionExportService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CompositeCronJobModel;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class FusionMigrationAuditSendJobUnitTest {

    private static final String CRON_JOB_CODE = "full-ProdCatMan-Fusion-migration-audit";

    private static final String PARENT_COMPOSITE_CRON_JOB_CODE = "full-ProdCatMan-Fusion";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @InjectMocks
    private FusionMigrationAuditSendJob fusionMigrationAuditSendJob;

    @Mock
    private CronJobService cronJobService;

    @Mock
    private FusionExportService fusionExportService;

    @Mock
    private FusionMigrationAuditSendCronJobModel fusionMigrationAuditSendCronJob;

    @Mock
    private CompositeCronJobModel parentCompositeJob;

    private Date parentCompositeJobStart;

    private Date defaultStartTime;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);

        when(fusionMigrationAuditSendCronJob.getCode()).thenReturn(CRON_JOB_CODE);
        when(fusionMigrationAuditSendCronJob.getMigrationAuditType()).thenReturn(FusionMigrationAuditType.BULK);
        when(fusionMigrationAuditSendCronJob.getMigrationAuditStatus()).thenReturn(FusionMigrationAuditStatus.COMPLETE);

        defaultStartTime = Date.from(LocalDateTime.now()
                                                  .minusHours(1)
                                                  .atZone(ZoneId.systemDefault())
                                                  .toInstant());

        parentCompositeJobStart = Date.from(LocalDateTime.now()
                                                         .minusMinutes(40)
                                                         .atZone(ZoneId.systemDefault())
                                                         .toInstant());
        when(parentCompositeJob.getStartTime()).thenReturn(parentCompositeJobStart);
        when(parentCompositeJob.getStatus()).thenReturn(CronJobStatus.RUNNING);
        when(parentCompositeJob.getCode()).thenReturn(PARENT_COMPOSITE_CRON_JOB_CODE);
        when(cronJobService.getCronJob(PARENT_COMPOSITE_CRON_JOB_CODE)).thenReturn(parentCompositeJob);
    }

    @Test
    public void testSendCompleteMigrationAuditForCompositeRun() {
        PerformResult performResult = fusionMigrationAuditSendJob.perform(fusionMigrationAuditSendCronJob);

        verify(fusionExportService, times(1))
                                             .sendMigrationStatus(refEq(createRequest(fusionMigrationAuditSendCronJob, parentCompositeJobStart),
                                                                        "id", "end"));
        assertThat(performResult.getStatus(), is(CronJobStatus.FINISHED));
        assertThat(performResult.getResult(), is(CronJobResult.SUCCESS));
    }

    @Test
    public void testSendCompleteMigrationAuditForManualRun() {
        when(parentCompositeJob.getStatus()).thenReturn(CronJobStatus.FINISHED);

        PerformResult performResult = fusionMigrationAuditSendJob.perform(fusionMigrationAuditSendCronJob);

        verify(fusionExportService, times(1))
                                             .sendMigrationStatus(refEq(createRequest(fusionMigrationAuditSendCronJob, defaultStartTime),
                                                                        "id", "start", "end"));
        assertThat(performResult.getStatus(), is(CronJobStatus.FINISHED));
        assertThat(performResult.getResult(), is(CronJobResult.SUCCESS));
    }

    @Test
    public void testSendCompleteMigrationAuditForCompositeRunException() {
        when(cronJobService.getCronJob(any())).thenThrow(UnknownIdentifierException.class);

        PerformResult performResult = fusionMigrationAuditSendJob.perform(fusionMigrationAuditSendCronJob);

        assertThat(performResult.getStatus(), is(CronJobStatus.FINISHED));
        assertThat(performResult.getResult(), is(CronJobResult.ERROR));
    }

    private MigrationStatusRequestDTO createRequest(FusionMigrationAuditSendCronJobModel cronJob,
                                                    Date startDate) {
        MigrationStatusRequestDTO migrationStatusRequestDTO = new MigrationStatusRequestDTO();
        migrationStatusRequestDTO.setName(cronJob.getCode());
        migrationStatusRequestDTO.setStart(formatDate(startDate.toInstant()
                                                               .atZone(ZoneId.systemDefault())
                                                               .toLocalDateTime()));
        migrationStatusRequestDTO.setStatus(cronJob.getMigrationAuditStatus().getCode());
        migrationStatusRequestDTO.setType(cronJob.getMigrationAuditType().getCode());

        return migrationStatusRequestDTO;
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return DateTimeFormatter.ISO_DATE_TIME.format(date);
    }

}
