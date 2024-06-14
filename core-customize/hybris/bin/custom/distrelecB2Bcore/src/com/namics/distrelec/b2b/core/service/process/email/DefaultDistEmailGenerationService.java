/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.process.email;

import com.namics.distrelec.b2b.core.model.DistOrderProcessModel;
import com.namics.distrelec.b2b.core.model.process.DistQuoteEmailProcessModel;
import com.namics.distrelec.b2b.core.model.process.DistSendToFriendProcessModel;
import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageTemplateModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.commons.model.renderer.RendererTemplateModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reads file from email context and add it as attachment.
 */
public class DefaultDistEmailGenerationService extends DefaultEmailGenerationService {

    private static final Logger LOG = Logger.getLogger(DefaultDistEmailGenerationService.class);

    @Override
    public EmailMessageModel generate(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        ServicesUtil.validateParameterNotNull(emailPageModel, "EmailPageModel cannot be null");
        Assert.isInstanceOf(EmailPageTemplateModel.class, emailPageModel.getMasterTemplate(),
                "MasterTemplate associated with EmailPageModel should be EmailPageTemplate");

        final EmailPageTemplateModel emailPageTemplateModel = (EmailPageTemplateModel) emailPageModel.getMasterTemplate();
        final RendererTemplateModel bodyRenderTemplate = emailPageTemplateModel.getHtmlTemplate();
        Assert.notNull(bodyRenderTemplate, "HtmlTemplate associated with MasterTemplate of EmailPageModel cannot be null");
        final RendererTemplateModel subjectRenderTemplate = emailPageTemplateModel.getSubject();
        Assert.notNull(subjectRenderTemplate, "Subject associated with MasterTemplate of EmailPageModel cannot be null");

        final EmailMessageModel emailMessageModel;
        final AbstractEmailContext emailContext = getEmailContextFactory().create(businessProcessModel, emailPageModel, bodyRenderTemplate);

        if (emailContext == null) {
            LOG.error("Failed to create email context");
            throw new RuntimeException("Failed to create email context");
        } else {
            final StringWriter subject = new StringWriter();
            getRendererService().render(subjectRenderTemplate, emailContext, subject);

            final StringWriter body = new StringWriter();
            getRendererService().render(bodyRenderTemplate, emailContext, body);

            final List<EmailAttachmentModel> attachments = new ArrayList<EmailAttachmentModel>();
            if (businessProcessModel instanceof DistSendToFriendProcessModel) {
                final EmailAttachmentModel attachment = ((DistSendToFriendProcessModel) businessProcessModel).getAttachment();
                if (attachment != null) {
                    attachments.add(attachment);
                }
            }
            if (businessProcessModel instanceof DistOrderProcessModel) {
                final EmailAttachmentModel attachment = ((DistOrderProcessModel) businessProcessModel).getAttachment();
                if (attachment != null) {
                    attachments.add(attachment);
                }
            }
            if (businessProcessModel instanceof DistQuoteEmailProcessModel) {
                final EmailAttachmentModel attachment = ((DistQuoteEmailProcessModel) businessProcessModel).getAttachment();
                if (attachment != null) {
                    attachments.add(attachment);
                }
            }

            emailMessageModel = createEmailMessage(subject.toString(), body.toString(), emailContext, attachments);
        }

        return emailMessageModel;
    }

    protected EmailMessageModel createEmailMessage(final String emailSubject, final String emailBody, final AbstractEmailContext emailContext,
                                                   final List<EmailAttachmentModel> attachments) {
        final List<EmailAddressModel> toEmails = new ArrayList<EmailAddressModel>();

        final List<String> emails = Arrays.asList(emailContext.getToEmail().split(";"));
        final List<String> displayNames = Arrays.asList(emailContext.getDisplayName().split(";"));

        List<EmailWithDisplayName> emailsWithDisplayNames = mergeEmailsAndDisplayNames(emails, displayNames);

        for (final EmailWithDisplayName emailWithDisplayName : emailsWithDisplayNames) {
            if (StringUtils.isNotBlank(emailWithDisplayName.getEmail()) && StringUtils.isNotBlank(emailWithDisplayName.getDisplayName())) {
                final EmailAddressModel toAddress = getEmailService().getOrCreateEmailAddressForEmail(emailWithDisplayName.getEmail(), emailWithDisplayName.getDisplayName());
                toEmails.add(toAddress);
            }
        }

        final EmailAddressModel fromAddress = getEmailService().getOrCreateEmailAddressForEmail(emailContext.getFromEmail(), emailContext.getFromDisplayName());
        return getEmailService().createEmailMessage(toEmails, new ArrayList<EmailAddressModel>(), new ArrayList<EmailAddressModel>(), fromAddress,
                emailContext.getFromEmail(), emailSubject, emailBody, attachments);
    }

    private List<EmailWithDisplayName> mergeEmailsAndDisplayNames(List<String> emails, List<String> displayNames) {
        List<EmailWithDisplayName> emailsWithDisplayNames = new ArrayList<>();
        for (int i = 0; i < emails.size(); i++) {
            String email = emails.get(i);
            String displayName;
            if (i >= displayNames.size()) {
                displayName = displayNames.get(displayNames.size() - 1);
            } else {
                displayName = displayNames.get(i);
            }
            emailsWithDisplayNames.add(new EmailWithDisplayName(email, displayName));
        }
        return emailsWithDisplayNames;
    }

    private class EmailWithDisplayName {
        private final String email;
        private final String displayName;

        public EmailWithDisplayName(String email, String displayName) {
            this.email = email;
            this.displayName = displayName;
        }

        public String getEmail() {
            return email;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

}
