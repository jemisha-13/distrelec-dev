/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.mail.internal;

import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Email;
import com.namics.distrelec.b2b.core.model.jobs.DistInternalCronjobModel;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.hybris.toolbox.DateTimeUtils;

import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * {@code AbstractDistInternalMailJob}
 * 
 * @param <T>
 *            the target CronJob
 * @param <S>
 *            the fetched item type.
 * @param <E>
 *            the target data type
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.10
 */
public abstract class AbstractDistInternalMailJob<T extends DistInternalCronjobModel, S, E> extends AbstractJobPerformable<T> {

    private static final Logger LOG = LogManager.getLogger(AbstractDistInternalMailJob.class);

    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private RendererService rendererService;
    // Localization support
    @Autowired
    private L10NService l10NService;

    protected T cronjob;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(final T cronJob) {
        setCronjob(cronJob);
        // Fetch the data and send it directly
        final List<S> models = fetch();
        // Convert the fetched data
        final List<E> data = convertAll(models);
        LOG.debug("size of data to send email = {}", data.size());
        // Create the context and send the data.
        final boolean result = send(createContext(data));
        return new PerformResult(result ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    /**
     * @return The list of data which will be sent to the internal mail.
     */
    protected List<S> fetch() {
        if (null != getQuery() && null != getQuery().getQuery()) {
            return processQueryResults(flexibleSearchService.<S> search(getQuery()).getResult());
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * 
     * Allows you to apply any process results (i.e. Filter or Shorting or Enhancing data ) before returning them
     * 
     * @param results
     * @return Processed Results
     */
    protected List<S> processQueryResults(List<S> results) {
        return results;
    }

    /**
     * Create the email context
     * 
     * @param data
     *            the list of data to be sent
     * @return a mail context.
     */
    protected DistInternalMailContext<E> createContext(final List<E> data) {
        final DistInternalMailContext<E> context = new DistInternalMailContext<E>(data);
        final String emailSubject = getEmailSubject().replace("{date}", getYesterdayDateString());

        context.setEmailSubject(emailSubject);
        return context;
    }

    /**
     * Convert the list of {@code SOURCE} data to a list of {@code TARGET}
     * 
     * @param sources
     *            the source list
     * @return a list of {@code TARGET}
     */
    protected List<E> convertAll(final List<S> sources) {
        if (sources == null || sources.isEmpty()) {
            return Collections.<E> emptyList();
        }

        return sources.stream().map(source -> convert(source)).filter(result -> result != null).collect(Collectors.toList());
    }

    /**
     * Convert the source object model to the target data object.
     * 
     * @param source
     *            the object model to convert
     * @return an object of type {@code E}
     */
    protected abstract E convert(final S source);

    /**
     * @return the query to be used for fetching the data from the database.
     */
    protected abstract FlexibleSearchQuery getQuery();

    /**
     * @return the template name to be used to send the email
     */
    protected String getTemplate() {
        return getCronjob().getEmailTemplate();
    }

    /**
     * @return {@code true} if the mail is sent successfully, {@code false} otherwise.
     */
    protected boolean send(final DistInternalMailContext<E> context) {
        if (context == null || context.isEmpty()) {
            LOG.info("No data found");
        }

        final MimeMessagePreparator preparator = (mimeMessage) -> {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setTo(getToEmails());
            message.setFrom(getFromEmail());
            message.setReplyTo(getReplyToEmail());
            message.setSentDate(new Date());
            message.setSubject(context.getEmailSubject());
            // Set Cc list
            final String[] ccs = getEmailCc();
            if (ccs != null && ccs.length > 0) {
                message.setCc(ccs);
            }

            // Set Bcc list
            final String[] bccs = getEmailBcc();
            if (bccs != null && bccs.length > 0) {
                message.setBcc(bccs);
            }

            // Add attachment
            addAttachment(message, context);

            message.setValidateAddresses(true);
            final RendererTemplateModel bodyTemplate = rendererService.getRendererTemplateForCode(getTemplate());
            LOG.debug("Template = {}", getTemplate());
            final StringWriter renderedText = new StringWriter();
            rendererService.render(bodyTemplate, context, renderedText);
            message.setText(renderedText.getBuffer().toString(), true);
            LOG.debug(renderedText.getBuffer().toString());
        };
        try {
            mailSender.send(preparator);
            LOG.info("Report email sent at : " + new Date().toString());
            return true;
        } catch (final MailException e) {
            DistLogUtils.logError(LOG, "{} {} Email NOT sent! Please check logs.", e, ErrorLogCode.EMAIL_ERROR, ErrorSource.HYBRIS);
            return false;
        }
    }

    protected static String getYesterdayDateString() {
        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        final Calendar cal = DateTimeUtils.getTodaysMidnightPlus1MinuteAsCalender();
        cal.add(Calendar.DATE, -1);
        return dateFormat.format(cal.getTime());
    }

    protected static Date getYesterdayDate() {
        final Calendar cal = DateTimeUtils.getTodaysMidnightPlus1MinuteAsCalender();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    /**
     * Add an attachment to the message to be sent. The default implementation is doing nothing.
     * 
     * @param message
     *            the target message
     */
    protected void addAttachment(final MimeMessageHelper message, final DistInternalMailContext<E> context) {
        // NOP
    }

    /**
     * @return the sender email address.
     */
    protected String getFromEmail() {
        return getCronjob().getFromMailAddress() == null ? null : getCronjob().getFromMailAddress();
    }

    /**
     * @return the reply to email address.
     */
    protected String getReplyToEmail() {
        return getConfigurationService().getConfiguration().getString("mail.replyto", "do-not-reply@distrelec.com");
    }

    /**
     * @return the destination email address.
     */
    protected String[] getToEmails() {
        String toEmail = getOverriddenToEmailAddress();
        if (isBlank(toEmail)) {
            toEmail = getCronjob().getToMailAddress();
        }
        return toEmail == null ? null : toEmail.split(";");
    }

    /**
     * @return the email subject
     */
    protected String getEmailSubject() {
        return getCronjob().getEmailSubject();
    }

    /**
     * @return the list of email addresses to be in Cc. The default value is null string array.
     */
    protected String[] getEmailCc() {
        if (!isToEmailAddressOverriden()) {
            return getCronjob().getCcMailAddress() == null ? null : getCronjob().getCcMailAddress().split(",");
        } else {
            return null;
        }
    }

    /**
     * @return the list of email addresses to be in Bcc. The default value is null string array.
     */
    protected String[] getEmailBcc() {
        if (!isToEmailAddressOverriden()) {
            return getCronjob().getBccMailAddress() == null ? null : getCronjob().getBccMailAddress().split(",");
        } else {
            return null;
        }
    }

    protected boolean isToEmailAddressOverriden() {
        return isNotBlank(getOverriddenToEmailAddress());
    }

    protected String getOverriddenToEmailAddress() {
        return getConfigurationService().getConfiguration().getString(Email.DIST_INTERNAL_EMAIL_TO);
    }

    // Getters & Setters

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configService) {
        this.configurationService = configService;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public RendererService getRendererService() {
        return rendererService;
    }

    public void setRendererService(final RendererService rendererService) {
        this.rendererService = rendererService;
    }

    public L10NService getL10NService() {
        return l10NService;
    }

    public void setL10NService(final L10NService l10nService) {
        l10NService = l10nService;
    }

    public T getCronjob() {
        return cronjob;
    }

    public void setCronjob(final T cronjob) {
        this.cronjob = cronjob;
    }
}
