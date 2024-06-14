/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.unix;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.namics.hybris.ffsearch.model.unix.UnixScriptExecutionCronJobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

public class UnixScriptExecutionJob extends AbstractJobPerformable<UnixScriptExecutionCronJobModel> {

    private static final Logger LOG = Logger.getLogger(UnixScriptExecutionJob.class);

    /**
     * 
     * @param command
     * @throws Exception
     */
    static void printLinuxCommand(String command) throws Exception {
        LOG.info("Linux command: " + command);
        String line;
        final Process process = Runtime.getRuntime().exec(command);
        final BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = in.readLine()) != null) {
            LOG.info(line);
        }
        in.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(UnixScriptExecutionCronJobModel cronJob) {
        final String scriptCommand = cronJob.getScript();
        try {
            printLinuxCommand(scriptCommand);
        } catch (final Exception e) {
            LOG.error("Exception occurred during execution of printLinuxCommand", e);
            return new PerformResult(CronJobResult.ERROR, CronJobStatus.FINISHED);
        }

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }
}
