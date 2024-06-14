package com.namics.hybris.ffsearch.jalo.export;

import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;

import de.hybris.platform.cronjob.constants.GeneratedCronJobConstants;
import de.hybris.platform.cronjob.jalo.JobLog;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;

public class DistFactFinderExportCronjob extends GeneratedDistFactFinderExportCronjob {

    @SuppressWarnings("unused")
    private final static Logger LOG = Logger.getLogger(DistFactFinderExportCronjob.class.getName());

    @Override
    protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException {
        // business code placed here will be executed before the item is created
        // then create the item
        final Item item = super.createItem(ctx, type, allAttributes);
        // business code placed here will be executed after the item was created
        // and return the item
        return item;
    }

    @Override
    public void sendEmail(final CronJobResult cronJobResult) throws EmailException {
        if (doSendMail(cronJobResult)) {
            super.sendEmail(cronJobResult);
        } else {
            logCronJobResult(cronJobResult);
        }
    }

    /**
     * Mail text gets only built, if an error occurred.
     */
    @Override
    protected String getFinishedEmailBody(final CronJobResult cronJobResult) {
        final StringBuilder body = new StringBuilder(super.getFinishedEmailBody(cronJobResult));
        for (final JobLog jobLog : getLogs()) {
            body.append("\n").append("Exception message: [").append(jobLog.getMessage()).append("] ").append("\n");
        }
        return body.toString();
    }

    protected void logCronJobResult(final CronJobResult cronJobResult) {
        if (GeneratedCronJobConstants.Enumerations.CronJobResult.ERROR.equals(cronJobResult.getResult().getCode())) {
            LOG.error(getFinishedEmailBody(cronJobResult));
        } else {
            LOG.info(getFinishedEmailBody(cronJobResult));
        }
    }

    private boolean doSendMail(final CronJobResult cronJobResult) {
        if (cronJobResult == null || cronJobResult.getResult() == null) {
            return false;
        }
        return (isSendEmailAsPrimitive() && !GeneratedCronJobConstants.Enumerations.CronJobResult.SUCCESS.equals(cronJobResult.getResult().getName()));
    }

}
