package com.namics.distrelec.b2b.core.jalo.jobs;

import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.sanity.DefaultDistSanityCheckNotificationContext;
import com.namics.distrelec.b2b.core.sanity.DistSanityCheckNotificationContext;

import de.hybris.platform.commons.jalo.renderer.RendererTemplate;
import de.hybris.platform.cronjob.jalo.CronJobNotificationTemplateContext;

/**
 * CronJob class for sanity check cronjobs. This is required to send notification emails using an own mail template.
 * 
 * @author pforster, Namics AG
 * @version 3.0.0
 */
public class DistGenericSanityCheckCronJob extends GeneratedDistGenericSanityCheckCronJob {
    private final static Logger LOG = Logger.getLogger(DistGenericSanityCheckCronJob.class.getName());

    @Override
    protected CronJobNotificationTemplateContext getRendererNotificationContext() {
        RendererTemplate rt = getEmailNotificationTemplate();
        String interfaceName = rt.getContextClass();
        if (DistSanityCheckNotificationContext.class.getCanonicalName().equals(interfaceName)) {
            return new DefaultDistSanityCheckNotificationContext(this);
        } else {
            // the default implementation of CronJob.getRendererNotificationContext()
            LOG.warn("Unable to load sanity check context for notification template. Trying to use default cron job context.");
            return super.getRendererNotificationContext();
        }
    }

}
