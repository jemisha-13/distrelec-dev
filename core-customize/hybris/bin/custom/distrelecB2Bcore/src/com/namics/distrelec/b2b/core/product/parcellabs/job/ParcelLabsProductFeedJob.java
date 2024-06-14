package com.namics.distrelec.b2b.core.product.parcellabs.job;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.distrelec.b2b.core.model.jobs.ParcelLabsProductFeedCronjobModel;
import com.namics.distrelec.b2b.core.product.parcellabs.dao.ParcelLabsProductFeedDao;
import com.namics.distrelec.b2b.core.service.export.DistExportService;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.commons.lang.time.FastDateFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import static com.namics.distrelec.b2b.core.service.export.impl.DefaultDistExportService.DATE_PATTERN;
import static java.util.Date.from;

public class ParcelLabsProductFeedJob extends AbstractJobPerformable<ParcelLabsProductFeedCronjobModel> {

    private static final Logger LOG = LogManager.getLogger(ParcelLabsProductFeedJob.class);

    @Autowired
    protected ParcelLabsProductFeedDao parcelLabsProductFeedDao;

    @Autowired
    private DistExportService distExportService;

    @Autowired
    private DistCsvTransformationService distCsvTransformationService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ModelService modelService;

    @Override
    public PerformResult perform(ParcelLabsProductFeedCronjobModel cronJob) {
        try {
            String fileType = DistConstants.Punctuation.DOT + DistConstants.Export.FORMAT_CSV;
            String salesOrg = cronJob.getSalesOrgCode();

            InputStream exportData = distCsvTransformationService.transform(getResults(salesOrg, cronJob),';');

            String exportMediaName = getMediaCode(
                    configurationService.getConfiguration().getString(DistConstants.PropKey.Sap.PARCEL_LAB_PRODUCT_EXPORT_MEDIA_PREFIX),
                    salesOrg);

            if (exportData != null) {
                distExportService.saveExportData(
                        exportData,
                        exportMediaName,
                        configurationService.getConfiguration().getString(DistConstants.PropKey.Sap.PARCEL_LAB_EXPORT_UPLOAD_SCP_FOLDER),
                        fileType,
                        false);
            }

            cronJob.setLastSuccessfulEndDate(getCurrentDate());
            modelService.save(cronJob);
            LOG.info("CronJob {} finished successfully!", cronJob.getCode());
            return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
        } catch (final Exception ex) {
            LOG.error("CronJob {} failed!", cronJob.getCode(), ex);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        }
    }

    private String getMediaCode(final String prefix, String salesOrg) {
        return prefix + FastDateFormat.getInstance(DATE_PATTERN).format(new Date()) + DistConstants.Punctuation.DASH + salesOrg;
    }

    protected ResultSet getResults(String salesOrg, ParcelLabsProductFeedCronjobModel cronJob) {
        return parcelLabsProductFeedDao.findChangedProductsForSalesOrg(salesOrg, cronJob.getLastSuccessfulEndDate());
    }

    private Date getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        return from(calendar.toInstant());
    }

}
