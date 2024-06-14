/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.sanity;

import org.apache.commons.lang.StringEscapeUtils;

import com.namics.distrelec.b2b.core.jalo.jobs.DistGenericSanityCheckCronJob;

import de.hybris.platform.cronjob.jalo.CronJob;
import de.hybris.platform.cronjob.jalo.DefaultCronJobNotificationTemplateContext;

/**
 * Implementation for the context class for MailNotification.
 * 
 * @author pforster, Namics AG
 * @version 3.0.0
 */
public class DefaultDistSanityCheckNotificationContext extends DefaultCronJobNotificationTemplateContext implements DistSanityCheckNotificationContext {

    private DistGenericSanityCheckCronJob cronJob;

    public DefaultDistSanityCheckNotificationContext(CronJob cronJob) {
        super(cronJob);
        this.cronJob = (DistGenericSanityCheckCronJob) cronJob;

    }

    @Override
    public String getType() {
        return cronJob.getType().getCode();
    }

    @Override
    public String getBaseQuery() {
        return cronJob.getBaseQuery();
    }

    @Override
    public String getCheckQuery() {
        return cronJob.getCheckQuery();
    }

    @Override
    public String getThreshold() {
        return String.valueOf(cronJob.getThreshold());
    }

    @Override
    public String getErrorMessage() {
        return cronJob.getErrorMessage();
    }

    @Override
    public String getLastResultBaseQuery() {
        return cronJob.getLastResultBaseQuery().toString();
    }

    @Override
    public String getLastResultCheckQuery() {
        return cronJob.getLastResultCheckQuery().toString();
    }

    public Class<StringEscapeUtils> getStringEscapeUtils() {
        return StringEscapeUtils.class;
    }
}
