package com.namics.distrelec.b2b.core.mail.internal;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.feedback.service.DistFeedbackService;
import com.namics.distrelec.b2b.core.model.feedback.DistFeedbackModel;
import com.namics.distrelec.b2b.core.model.jobs.DistZeroResultFeedbackNotificationCronjobModel;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.opencsv.CSVWriter;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.Feedback.ZERO_RESULT_FEEDBACK_MONTHS_AGO;


/**
 * @author <a href="abhinay.jadhav@datwyler.com">Abhinay Jadhav</a>, DIT
 * @since Distrelec 6.7
 */
public class DistZeroResultFeedbackNotificationJob extends AbstractDistInternalMailJob<DistZeroResultFeedbackNotificationCronjobModel, String, String> {

    private static final Logger LOG = LogManager.getLogger(DistZeroResultFeedbackNotificationJob.class);
    private static final String[] HEADERS = { "Search term", "Manufacturer", "Other Manufacturer Name", "Manufacturer type", "Please tell us more", "Email address", "Country", "Sales org" };

    @Autowired
    private DistFeedbackService distFeedbackService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    protected boolean send(DistInternalMailContext<String> context) {
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

            String template = getTemplate() + "_nofile";
            File report = createZeroResultFeedbackReport();
            if (report != null && report.exists()) {
                addAttachment(message, report);
                template = getTemplate();
            }

            message.setValidateAddresses(true);
            final RendererTemplateModel bodyTemplate = getRendererService().getRendererTemplateForCode(template);
            LOG.debug("Template = {}", template);
            final StringWriter renderedText = new StringWriter();
            getRendererService().render(bodyTemplate, context, renderedText);
            message.setText(renderedText.getBuffer().toString(), true);
            if (LOG.isDebugEnabled()) {
                LOG.debug(renderedText.getBuffer().toString());
            }
        };
        try {
            getMailSender().send(preparator);
            LOG.info("Report email sent at : {}", new Date());
            return true;
        } catch (final MailException e) {
            DistLogUtils.logError(LOG, "{} {} Email NOT sent! Please check logs.", e, DistConstants.ErrorLogCode.EMAIL_ERROR, DistConstants.ErrorSource.HYBRIS);
            return false;
        }
    }


    private void addAttachment(MimeMessageHelper message, File report) {
        try {
            LocalDate startMonth = LocalDate.now().minusMonths(getMonthsToSubtract());
            message.addAttachment(String.valueOf(startMonth.getYear()) + startMonth.getMonth().getValue() + ".csv", report);
            report.deleteOnExit();
        } catch (Exception ex) {
            LOG.error("Error while attaching zero result notification file", ex);
        }
    }

    private File createZeroResultFeedbackReport() {
        LocalDate startMonth = LocalDate.now().minusMonths(getMonthsToSubtract());
        LocalDate endMonth = startMonth.plusMonths(1);
        Date startDate = convertLocalDateToDate(startMonth.with(TemporalAdjusters.firstDayOfMonth()));
        Date endDate = convertLocalDateToDate(endMonth.with(TemporalAdjusters.firstDayOfMonth()));
        List<DistFeedbackModel> feedbacks = getDistFeedbackService().findFeedbacks(startDate, endDate);
        if (CollectionUtils.isEmpty(feedbacks)) {
            LOG.info("No feedbacks for month: {}, year: {}", startMonth.getMonth().getValue(), startMonth.getYear());
            return null;
        }
        try {
            File tempFile = File.createTempFile("ZeroResultsFeedback", ".csv");
            tempFile.deleteOnExit();
            try (FileOutputStream outputStream = new FileOutputStream(tempFile);
                 OutputStreamWriter streamWriter = new OutputStreamWriter(outputStream);
                 CSVWriter csvWriter = new CSVWriter(streamWriter)) {
                csvWriter.writeNext(HEADERS);
                for (DistFeedbackModel feedback : feedbacks) {
                    csvWriter.writeNext(exportFeedback(feedback));
                    csvWriter.flush();
                }
            }
            return tempFile;
        } catch (IOException ex) {
            LOG.error("Could not create zero result feedback feedback report", ex);
            return null;
        }
    }

    private long getMonthsToSubtract() {
        return getConfigurationService().getConfiguration().getLong(ZERO_RESULT_FEEDBACK_MONTHS_AGO, 1);
    }

    private Date convertLocalDateToDate(LocalDate localDate){
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private String[] exportFeedback(DistFeedbackModel feedback) {
        return new String[] {
          prepareContent(feedback.getSearchTerm()),
          prepareContent(feedback.getManufacturer()),
          prepareContent(feedback.getOtherManufacturerName()),
          prepareContent(feedback.getManufacturerType()),
          prepareContent(feedback.getTellUsMore()),
          prepareContent(feedback.getEmail()),
          prepareContent(feedback.getCountry()),
          prepareContent(feedback.getSalesOrg())
        };
    }

    private String prepareContent(String value) {
        return StringUtils.isEmpty(value) ? StringUtils.EMPTY : value;
    }

    @Override
    protected FlexibleSearchQuery getQuery() {
        // YTODO Auto-generated method stub
        return null;
    }

    @Override
    protected String convert(String source) {
        // YTODO Auto-generated method stub
        return null;
    }

    public DistFeedbackService getDistFeedbackService() {
        return distFeedbackService;
    }

    public void setDistFeedbackService(final DistFeedbackService distFeedbackService) {
        this.distFeedbackService = distFeedbackService;
    }

    @Override
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Override
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
