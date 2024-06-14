package com.distrelec.job;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.fusion.integration.dto.MigrationStatusRequestDTO;
import com.distrelec.solrfacetsearch.indexer.cron.DistSolrIndexerJob;
import com.distrelec.solrfacetsearch.model.jobs.FusionMigrationAuditSendCronJobModel;
import com.distrelec.solrfacetsearch.service.FusionExportService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CompositeCronJobModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class FusionMigrationAuditSendJob extends AbstractJobPerformable<FusionMigrationAuditSendCronJobModel> {

    private static final Logger LOG = LogManager.getLogger(DistSolrIndexerJob.class);

    private static final String MIGRATION_AUDIT_JOB_SUFFIX = "-migration-audit";

    private static final List<CronJobStatus> CRON_JOB_RUNNING_STATUSES = List.of(CronJobStatus.RUNNING, CronJobStatus.RUNNINGRESTART);

    @Autowired
    private CronJobService cronJobService;

    @Autowired
    private FusionExportService fusionExportService;

    /**
     * After all index jobs are finished in scope of composite job. Last job is sending migration audit log with a COMPLETE status.
     * That status is triggering update logic for typeahead and other post-processing in Fusion.
     */
    @Override
    public PerformResult perform(FusionMigrationAuditSendCronJobModel cronJob) {
        CronJobResult cronJobResult = CronJobResult.SUCCESS;

        try {
            fusionExportService.sendMigrationStatus(createRequest(cronJob));
        } catch (Exception ex) {
            LOG.error("Error sending migration audit log to Fusion for type {} with {} status",
                      cronJob.getMigrationAuditType().getCode(),
                      cronJob.getMigrationAuditStatus().getCode(),
                      ex);
            cronJobResult = CronJobResult.ERROR;
        }

        return new PerformResult(cronJobResult, CronJobStatus.FINISHED);
    }

    private MigrationStatusRequestDTO createRequest(FusionMigrationAuditSendCronJobModel cronJob) {
        MigrationStatusRequestDTO migrationStatusRequestDTO = new MigrationStatusRequestDTO();

        migrationStatusRequestDTO.setId(System.currentTimeMillis());
        migrationStatusRequestDTO.setName(cronJob.getCode());

        migrationStatusRequestDTO.setStart(getStartTime(cronJob));
        migrationStatusRequestDTO.setEnd(getEndTime());

        migrationStatusRequestDTO.setStatus(cronJob.getMigrationAuditStatus().getCode());
        migrationStatusRequestDTO.setType(cronJob.getMigrationAuditType().getCode());

        return migrationStatusRequestDTO;
    }

    /**
     * Start date is used to find delta of products which needs to be updated to typeahead collection.
     * Will be used only for incremental and atomic types.
     *
     * If job is not started via composite job, but it's started manually for any reason, 1 hour in past
     * will be used since every indexing job should take less than 1 hour. Scenario, somebody runs update index
     * job for CH, that job will take around 40 minutes. Then it can run this job (update-ProdCatMan-Fusion-migration-audit)
     * to send a complete log which triggers typeahead jobs. And start date will make sure that only products modified
     * in last hour in main collection to be sent to typeahead collection.
     */
    private String getStartTime(FusionMigrationAuditSendCronJobModel cronJob) {
        return getRunningParentCompositeJob(cronJob).map(job -> formatDate(job.getStartTime()))
                                                    .orElse(formatDate(LocalDateTime.now().minusHours(1)));
    }

    private Optional<CronJobModel> getRunningParentCompositeJob(FusionMigrationAuditSendCronJobModel cronJob) {
        CronJobModel parentCompositeJob = cronJobService.getCronJob(getParentCompositeJobUid(cronJob));
        if (isCompositeAndRunning(parentCompositeJob)) {
            LOG.info("Fusion migration audit send job is triggered in scope of composite job {} for type {} with {} status.",
                     parentCompositeJob.getCode(),
                     cronJob.getMigrationAuditType().getCode(),
                     cronJob.getMigrationAuditStatus().getCode());
            return Optional.of(parentCompositeJob);
        } else {
            LOG.info("Fusion migration audit send job is triggered outside of composite job for type {} with {} status.",
                     cronJob.getMigrationAuditType().getCode(),
                     cronJob.getMigrationAuditStatus().getCode());
            return Optional.empty();
        }
    }

    private String getParentCompositeJobUid(FusionMigrationAuditSendCronJobModel cronJob) {
        return cronJob.getCode().replace(MIGRATION_AUDIT_JOB_SUFFIX, "");
    }

    private boolean isCompositeAndRunning(CronJobModel parentCompositeJob) {
        return parentCompositeJob instanceof CompositeCronJobModel
                && CRON_JOB_RUNNING_STATUSES.contains(parentCompositeJob.getStatus());
    }

    private String getEndTime() {
        return formatDate(LocalDateTime.now());
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return formatDate(date.toInstant()
                              .atZone(ZoneId.systemDefault())
                              .toLocalDateTime());
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        return DateTimeFormatter.ISO_DATE_TIME.format(date);
    }

}
