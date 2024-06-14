/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.model.jobs.DistInternalCronjobModel;
import com.namics.hybris.toolbox.DateTimeUtils;

import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.commons.renderer.RendererService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;

public abstract class AbstractReportJob<T extends DistInternalCronjobModel> extends AbstractJobPerformable<T> {

    private static final Logger LOG = LogManager.getLogger(AbstractReportJob.class);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RendererService rendererService;

    protected Set<Pair<String, String>> getShopCodesAndNames(final List<List<String>> productsPerShop) {
        return productsPerShop.stream().map(l -> Pair.of(l.get(0), l.get(1))).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    protected void applyBorderStyle(final XSSFSheet sheet, final CellRangeAddress totalRegion, final BorderStyle borderStyle) {
        RegionUtil.setBorderBottom(borderStyle, totalRegion, sheet);
        RegionUtil.setBorderTop(borderStyle, totalRegion, sheet);
        RegionUtil.setBorderLeft(borderStyle, totalRegion, sheet);
        RegionUtil.setBorderRight(borderStyle, totalRegion, sheet);
    }

    protected void writeFileToFilesystem(final XSSFWorkbook workbook, final File file) {
        try {
            workbook.write(new FileOutputStream(file));
        } catch (final IOException e) {
            LOG.error(new ParameterizedMessage("{} for file {}", e.getMessage(), file.getAbsolutePath()), e);
        }
        LOG.info("{} written successfully:", file.getAbsolutePath());
    }

    protected boolean sendEmail(final T cronJob, final File file) {
        return send(createContext(cronJob), file, cronJob);
    }

    // Setup the email content data in email context
    protected DistInternalMailContext<T> createContext(final T cronJob) {
        Assert.notNull(cronJob);
        final DistInternalMailContext<T> context = new DistInternalMailContext<>(Collections.emptyList());
        final String emailSubject = cronJob.getEmailSubject().replace("{date}", getTodaysDateString());
        context.setEmailSubject(emailSubject);
        return context;
    }

    // Send an email, with products per webshop csv
    protected boolean send(final DistInternalMailContext<?> context, final File fileData, final T cronJob) {
        if (context == null) {
            LOG.info("No data found");
        }

        final MimeMessagePreparator preparator = mimeMessage -> {
            final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            message.setTo(cronJob.getToMailAddress());
            message.setFrom(cronJob.getFromMailAddress());
            message.setReplyTo(getReplyToEmail());
            message.setSentDate(new Date());
            message.setSubject(context.getEmailSubject().replace("{date}", getTodaysDateString()));
            // Set Cc list
            final String[] ccs = cronJob.getCcMailAddress() == null ? null : cronJob.getCcMailAddress().split(",");
            if (ccs != null && ccs.length > 0) {
                message.setCc(ccs);
            }

            // Set Bcc list
            final String[] bccs = cronJob.getBccMailAddress() == null ? null : cronJob.getBccMailAddress().split(",");
            if (bccs != null && bccs.length > 0) {
                message.setBcc(bccs);
            }

            // Add attachment
            message.addAttachment(FilenameUtils.removeExtension(getReportFileName()) + "-" + getTodaysDateString() + ".xlsx", fileData);
            message.setValidateAddresses(true);
            final RendererTemplateModel bodyTemplate = rendererService.getRendererTemplateForCode(cronJob.getEmailTemplate());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Template = " + cronJob.getEmailTemplate());
            }
            final StringWriter renderedText = new StringWriter();
            rendererService.render(bodyTemplate, context, renderedText);
            message.setText(renderedText.getBuffer().toString(), true);
            if (LOG.isDebugEnabled())
                LOG.debug(renderedText.getBuffer().toString());
        };
        try {
            mailSender.send(preparator);
            LOG.info("{} Report Email Sent At : {}", cronJob.getCode(), new Date().toString());
            return true;
        } catch (final MailException e) {
            LOG.error("Email NOT sent! Please check logs", e);
            return false;
        }
    }

    protected XSSFSheet getSheet(final XSSFWorkbook workbook) {
        XSSFSheet sheet;
        try {
            sheet = workbook.getSheetAt(0);
        } catch (final IllegalArgumentException e) {
            sheet = workbook.createSheet();
        }
        return sheet;
    }

    protected abstract String getReportFileName();

    protected static String getTodaysDateString() {
        final DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY");
        final Calendar cal = DateTimeUtils.getTodaysMidnightPlus1MinuteAsCalender();
        cal.add(Calendar.DATE, 0);
        return dateFormat.format(cal.getTime());
    }

    /**
     * @return the reply to email address.
     */
    protected String getReplyToEmail() {
        return getConfigurationService().getConfiguration().getString("mail.replyto", "do-not-reply@distrelec.com");
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
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

}
