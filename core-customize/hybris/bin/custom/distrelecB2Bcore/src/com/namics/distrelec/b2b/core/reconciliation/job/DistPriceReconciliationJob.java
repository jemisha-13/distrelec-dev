package com.namics.distrelec.b2b.core.reconciliation.job;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.jobs.DistPriceReconciliationCronJobModel;
import com.namics.distrelec.b2b.core.reconciliation.service.DistPriceReconciliationService;
import com.namics.distrelec.b2b.core.service.export.DistExportService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Date;

import static com.namics.distrelec.b2b.core.service.export.impl.DefaultDistExportService.DATE_PATTERN;

public class DistPriceReconciliationJob extends AbstractJobPerformable<DistPriceReconciliationCronJobModel> {
    private static final Logger LOG = LogManager.getLogger(DistPriceReconciliationJob.class);

    @Autowired
    private DistPriceReconciliationService distPriceReconciliationService;

    @Autowired
    private DistExportService distExportService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public PerformResult perform(final DistPriceReconciliationCronJobModel cronJob) {
        try {
            InputStream exportData = null;
            String fileType = DistConstants.Punctuation.DOT + DistConstants.Export.FORMAT_CSV;
            String salesOrg = cronJob.getSalesOrgCode();
            ResultSet resultSet = distPriceReconciliationService.getAllPriceRows(salesOrg);

            if (resultSet != null && resultSet.next()) {
                exportData = distPriceReconciliationService.transform(resultSet);
            }

            String exportMediaName = getMediaCode(configurationService.getConfiguration().getString(DistConstants.PropKey.Sap.PRICE_EXPORT_MEDIA_PREFIX));

            if (exportData != null) {
                distExportService.saveExportData(
                        exportData,
                        exportMediaName,
                        configurationService.getConfiguration().getString(DistConstants.PropKey.Sap.EXPORT_UPLOAD_SCP_FOLDER),
                        fileType,
                        true);
            }

            LOG.info("CronJob {} finished successfully!", cronJob.getCode());
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        } catch (final Exception ex) {
            LOG.error("CronJob {} failed!", cronJob.getCode(), ex);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        }
    }

    private String getMediaCode(final String prefix) {
        return prefix + DistConstants.Punctuation.UNDERSCORE + FastDateFormat.getInstance(DATE_PATTERN).format(new Date());
    }
}
