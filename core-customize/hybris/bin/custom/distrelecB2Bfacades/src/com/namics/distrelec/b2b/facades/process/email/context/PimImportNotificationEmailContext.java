/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hybris.platform.core.model.c2l.LanguageModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DurationFormatUtils;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.PimImportNotificationProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.time.DurationFormatUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * PimImportNotificationEmailContext.
 *
 * @author datkuppuras, Distrelec
 * @since Distrelec 1.1
 */
public class PimImportNotificationEmailContext extends AbstractDistEmailContext {

    private String importStatus;
    private String logMessage;
    private String startTime;
    private String endTime;
    private String duration;
    private String pimLogStatistics;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        LanguageModel language = getEmailLanguage(businessProcessModel);

        put(THEME, null);
        put(FROM_EMAIL, emailPageModel.getFromEmail());
        put(FROM_DISPLAY_NAME, emailPageModel.getFromName());
        put(EMAIL, getConfigurationService().getConfiguration().getProperty(DistConstants.PropKey.Import.IMPORT_PIM_NOTIFICATION_EMAIL));
        put(DISPLAY_NAME, getConfigurationService().getConfiguration().getProperty(DistConstants.PropKey.Import.IMPORT_PIM_NOTIFICATION_EMAIL));
        put(EMAIL_LANGUAGE, language);
        put("StringEscapeUtils", StringEscapeUtils.class);

        if (businessProcessModel instanceof PimImportNotificationProcessModel) {
            final PimImportNotificationProcessModel pimImportNotificationProcess = (PimImportNotificationProcessModel) businessProcessModel;
            setImportStatus(pimImportNotificationProcess.isImportSuccessful() ? "Successful" : "Had errors");
            setLogMessage(pimImportNotificationProcess.getLogMessage());
            setPimLogStatistics(pimImportNotificationProcess.getPimLogStatistics());
            final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", getCommonI18NService().getLocaleForLanguage(language));
            final Date startTime = pimImportNotificationProcess.getStartTime();
            final Date endTime = pimImportNotificationProcess.getEndTime();
            setStartTime(sdf.format(startTime));
            setEndTime(sdf.format(endTime));
            if (startTime != null && endTime != null) {
                setDuration(DurationFormatUtils.formatPeriod(startTime.getTime(), endTime.getTime(), "HH'h' mm'm' ss's'"));
            }
        }
    }

    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        return null;
    }

    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
        return null;
    }

    public String getImportStatus() {
        return importStatus;
    }

    public void setImportStatus(final String importStatus) {
        this.importStatus = importStatus;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public void setLogMessage(final String logMessage) {
        this.logMessage = logMessage;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(final String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(final String endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    public String getPimLogStatistics() {
        return pimLogStatistics;
    }

    public void setPimLogStatistics(final String pimLogStatistics) {
        this.pimLogStatistics = pimLogStatistics;
    }
}
