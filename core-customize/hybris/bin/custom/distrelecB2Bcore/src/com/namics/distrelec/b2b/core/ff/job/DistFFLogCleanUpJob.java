package com.namics.distrelec.b2b.core.ff.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AgeFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.namics.distrelec.b2b.core.model.jobs.DistFFLogCleanUpCronJobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class DistFFLogCleanUpJob extends AbstractJobPerformable<DistFFLogCleanUpCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DistFFLogCleanUpJob.class);

    @Override
    public PerformResult perform(final DistFFLogCleanUpCronJobModel cronJob) {
        LOG.info("Inside Perform :- " + DistFFLogCleanUpJob.class);
        File directory = new File(cronJob.getSourcePath());
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + cronJob.getSourcePath());
        }

        byte[] buffer = new byte[4096]; // Create a buffer for copying
        int bytesRead;

        ZipOutputStream out;
        try {
            out = new ZipOutputStream(new FileOutputStream(cronJob.getTargetPath()));
            Date currentDate = new Date();
            if (cronJob.getCutoffDate() == null) {
                DateTime dt = new DateTime(currentDate);
                cronJob.setCutoffDate(dt.minusDays(90).toDate());
            }
            Iterator<File> filesToClean = FileUtils.iterateFiles(directory, new AgeFileFilter(cronJob.getCutoffDate()), TrueFileFilter.TRUE);
            while (filesToClean.hasNext()) {
                File file = filesToClean.next();
                FileInputStream in = new FileInputStream(file);
                ZipEntry zEntry = new ZipEntry(file.getName());
                out.putNextEntry(zEntry); // Store entry
                while ((bytesRead = in.read(buffer)) != -1)
                    out.write(buffer, 0, bytesRead);
                in.close();
            }
            out.close();
        } catch (FileNotFoundException e) {
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        } catch (IOException e) {
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
}
