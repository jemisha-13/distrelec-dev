package com.namics.distrelec.b2b.core.mail.internal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.mail.internal.data.SuccessfulOrder;
import com.namics.distrelec.b2b.core.model.jobs.DistSuccessfulOrdersCronjobModel;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.hybris.toolbox.DateTimeUtils;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * @author <a href="abhinay.jadhav@datwyler.com">Abhinay Jadhav</a>, DIT
 * @since Distrelec 5.18
 */
public class DistSuccessfulOrdersJob extends AbstractJobPerformable<DistSuccessfulOrdersCronjobModel> {

    private static final Logger LOG = LogManager.getLogger(DistSuccessfulOrdersJob.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RendererService rendererService;

    // Localization support
    @Autowired
    private L10NService l10NService;

    @Override
    public PerformResult perform(final DistSuccessfulOrdersCronjobModel successCronJob) {
        LOG.info("Sending successful orders mails");
        boolean result = false;
        List<OrderModel> successfulOrders = flexibleSearchService.<OrderModel> search(getQuery(successCronJob)).getResult();
        // final List<SuccessfulOrder> data = convertAll(successfulOrders);
        final List<SuccessfulOrder> data = Collections.<SuccessfulOrder> emptyList();
        try {
            File file = File.createTempFile("successfulOrders", ".csv");
            file.deleteOnExit();
            PrintWriter pw;
            pw = new PrintWriter(file);

            if (null != successfulOrders) {
                pw.write(
                        "Order No,ERP Order Code,Sales Org,Country, User ID, Payment Type,Transaction Ref Token,Date,Total Price,Currency,Transaction Type, Transaction Status,Success/Notify Count\n");

                int successful = 0;
                int totalTransactions = 0;
                for (OrderModel it : successfulOrders) {
                    List<PaymentTransactionModel> paymentTransactions = it.getPaymentTransactions();
                    if (CollectionUtils.isNotEmpty(paymentTransactions)) {
                        final CMSSiteModel cmsSite = ((CMSSiteModel) it.getSite());
                        Date trialDate = null;
                        int authCount = 0;
                        for (PaymentTransactionModel paymentTransaction : paymentTransactions) {
                            totalTransactions++;
                            List<PaymentTransactionEntryModel> entries = paymentTransaction.getEntries();
                            boolean success = false;
                            boolean notify = false;
                            if (CollectionUtils.isNotEmpty(entries)) {

                                for (PaymentTransactionEntryModel ccEntry : entries) {
                                    if (PaymentTransactionType.SUCCESS_RESPONSE.getCode().equals(ccEntry.getType().getCode())) {
                                        success = ("AUTHORIZED".equalsIgnoreCase(ccEntry.getTransactionStatus()))
                                                || ("AUTHORIZED_REQUEST".equalsIgnoreCase(ccEntry.getTransactionStatus()))
                                                || ("OK".equalsIgnoreCase(ccEntry.getTransactionStatus()));
                                    }
                                    if (PaymentTransactionType.NOTIFY.equals(ccEntry.getType())) {
                                        notify = ("AUTHORIZED".equalsIgnoreCase(ccEntry.getTransactionStatus()))
                                                || ("AUTHORIZED_REQUEST".equalsIgnoreCase(ccEntry.getTransactionStatus()))
                                                || "OK".equals(ccEntry.getTransactionStatus());
                                    }
                                    if (success || notify) {
                                        trialDate = ccEntry.getModifiedtime();
                                        successful++;
                                        authCount++;
                                        final StringBuilder sb = new StringBuilder(it.getCode()) //
                                                .append(",") //
                                                .append(it.getErpOrderCode()) //
                                                .append(",") //
                                                .append(cmsSite.getSalesOrg().getNameErp()) //
                                                .append(",") //
                                                .append(cmsSite.getSalesOrg().getCountry().getName()) //
                                                .append(",") //
                                                .append(it.getUser().getUid()) //
                                                .append(",") //
                                                .append(it.getPaymentMode().getName()) //
                                                .append(",") //
                                                .append(paymentTransaction.getRequestToken()) //
                                                .append(",") //
                                                .append(trialDate) //
                                                .append(",") //
                                                .append(it.getTotalPrice()) //
                                                .append(",") //
                                                .append(it.getCurrency().getIsocode()) //
                                                .append(",") //
                                                .append(ccEntry.getType()) //
                                                .append(",") //
                                                .append(ccEntry.getTransactionStatus()) //
                                                .append(",") //
                                                .append(authCount) //
                                                .append("\n");
                                        pw.write(sb.toString());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                pw.write("\n\n");
                pw.write("Total  payment transactions : " + totalTransactions);
                pw.write("Total successful payment transaction entries : " + successful);
            } else {

            }
            pw.close();

            result = send(createContext(data, successCronJob), file, successCronJob);

        } catch (final IOException e) {
            DistLogUtils.logError(LOG, "{} {} Error in DistSuccessfulOrdersCronjob!", e, ErrorLogCode.ORDER_RELATED_ERROR, ErrorSource.HYBRIS);
        }

        return new PerformResult(result ? CronJobResult.SUCCESS : CronJobResult.ERROR, CronJobStatus.FINISHED);
    }

    // Setup the email content data in email context
    protected DistInternalMailContext<SuccessfulOrder> createContext(final List<SuccessfulOrder> data, final DistSuccessfulOrdersCronjobModel successCronJob) {
        final DistInternalMailContext<SuccessfulOrder> context = new DistInternalMailContext<SuccessfulOrder>(data);
        final String emailSubject = successCronJob.getEmailSubject().replace("{date}", getYesterdayDateString());

        context.setEmailSubject(emailSubject);
        return context;
    }

    // Send an email, with successful orders csv
    protected boolean send(final DistInternalMailContext<SuccessfulOrder> context, final File fileData, final DistSuccessfulOrdersCronjobModel successCronJob) {
        if (context == null || context.isEmpty()) {
            LOG.info("No data found");
        }

        final MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setTo(successCronJob.getToMailAddress());
                message.setFrom(successCronJob.getFromMailAddress());
                message.setReplyTo(getReplyToEmail());
                message.setSentDate(new Date());
                message.setSubject(context.getEmailSubject().replace("{date}", getYesterdayDateString()));
                // Set Cc list
                final String[] ccs = successCronJob.getCcMailAddress() == null ? null : successCronJob.getCcMailAddress().split(",");
                if (ccs != null && ccs.length > 0) {
                    message.setCc(ccs);
                }

                // Set Bcc list
                final String[] bccs = successCronJob.getBccMailAddress() == null ? null : successCronJob.getBccMailAddress().split(",");
                if (bccs != null && bccs.length > 0) {
                    message.setBcc(bccs);
                }

                // Add attachment
                message.addAttachment("successfulOrders.csv", fileData);
                fileData.deleteOnExit();
                message.setValidateAddresses(true);
                final RendererTemplateModel bodyTemplate = rendererService.getRendererTemplateForCode(successCronJob.getEmailTemplate());
                LOG.debug("Template = {}", successCronJob.getEmailTemplate());
                final StringWriter renderedText = new StringWriter();
                rendererService.render(bodyTemplate, context, renderedText);
                message.setText(renderedText.getBuffer().toString(), true);
                LOG.debug(renderedText.getBuffer().toString());
            }
        };
        try {
            mailSender.send(preparator);
            LOG.info("Successful Order Report Email Sent At : " + new Date().toString());
            return true;
        } catch (final MailException e) {
            DistLogUtils.logError(LOG, "{} {} Order Report Email NOT sent! Please check logs", e, ErrorLogCode.EMAIL_ERROR, ErrorSource.HYBRIS);
            return false;
        }
    }

    // Forms Flex query to fetch the data from DB
    public FlexibleSearchQuery getQuery(final DistSuccessfulOrdersCronjobModel successCronJob) {
        DateTime today = new DateTime(new Date());
        Calendar endCalenderDate = new GregorianCalendar(today.getYear(), today.getMonthOfYear() - 1, today.getDayOfMonth());
        Date startDate = DateUtils.addDays(endCalenderDate.getTime(), -1);
        Date endDate = DateUtils.addDays(endCalenderDate.getTime(), 0);

        final FlexibleSearchQuery query = new FlexibleSearchQuery(successCronJob.getSqlQuery());
        query.addQueryParameter("startDate", startDate);
        query.addQueryParameter("endDate", endDate);
        return query;
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

    /*
     * protected List<SuccessfulOrder> convertAll(final List<OrderModel> sources) { if (sources == null || sources.isEmpty()) { return
     * Collections.<SuccessfulOrder> emptyList(); }
     * 
     * final List<SuccessfulOrder> targets = new ArrayList<SuccessfulOrder>();
     * 
     * for (final OrderModel source : sources) { targets.add(convert(source)); }
     * 
     * return targets; } public SuccessfulOrder convert(final OrderModel source) { final List<PaymentTransactionModel> paymentTransactions =
     * source.getPaymentTransactions();
     * 
     * if (CollectionUtils.isNotEmpty(paymentTransactions)) { final CMSSiteModel cmsSite = ((CMSSiteModel) source.getSite()); final
     * Iterator<PaymentTransactionModel> paymentTransactionModelIterator = paymentTransactions.iterator(); int authCount = 0; while
     * (paymentTransactionModelIterator.hasNext()) { PaymentTransactionModel paymentTransaction = paymentTransactionModelIterator.next();
     * final List<PaymentTransactionEntryModel> entries = paymentTransaction.getEntries(); if (CollectionUtils.isNotEmpty(entries)) {
     * 
     * final Iterator<PaymentTransactionEntryModel> entriesiterator = entries.iterator(); boolean success = false; boolean notify = false;
     * 
     * while (entriesiterator.hasNext()) {
     * 
     * final PaymentTransactionEntryModel ccEntry = entriesiterator.next(); if
     * (PaymentTransactionType.SUCCESS_RESPONSE.equals(ccEntry.getType())) { success = isCorrectPaymentTransaction(ccEntry); } if
     * (PaymentTransactionType.NOTIFY.equals(ccEntry.getType())) { notify = isCorrectPaymentTransaction(ccEntry); }
     * 
     * if (notify || success) { final SuccessfulOrder p = new SuccessfulOrder(); p.setCode(source.getCode());
     * p.setErpCode(source.getErpOrderCode()); p.setSalesOrg(cmsSite.getSalesOrg().getNameErp());
     * p.setCountry(cmsSite.getSalesOrg().getCountry().getName()); p.setUserId(source.getUser().getUid());
     * p.setPaymentType(source.getPaymentMode().getCode()); p.setTransactionRefToken(paymentTransaction.getRequestToken());
     * p.setModifiedTime(source.getModifiedtime().toString()); p.setTotalPrice(source.getTotalPrice().toString());
     * p.setCurrency(source.getCurrency().getName()); p.setTransactionType(ccEntry.getType().getCode());
     * p.setTransactionStatus(ccEntry.getTransactionStatus()); authCount++; p.setPaymentCount(authCount); return p; } }
     * 
     * 
     * } }
     * 
     * }
     * 
     * return null; }
     * 
     * private static boolean isCorrectPaymentTransaction(final PaymentTransactionEntryModel ccEntry) { return
     * "OK".equalsIgnoreCase(ccEntry.getTransactionStatus()) || "AUTHORIZED".equalsIgnoreCase(ccEntry.getTransactionStatus()); }
     */

    /**
     * @return the reply to email address.
     */
    protected String getReplyToEmail() {
        return getConfigurationService().getConfiguration().getString("mail.replyto", "do-not-reply@distrelec.com");
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public JavaMailSender getMailSender() {
        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public RendererService getRendererService() {
        return rendererService;
    }

    public void setRendererService(RendererService rendererService) {
        this.rendererService = rendererService;
    }

    public L10NService getL10NService() {
        return l10NService;
    }

    public void setL10NService(L10NService l10nService) {
        l10NService = l10nService;
    }

}
