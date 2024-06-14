/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.email;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.DistEmailMessageModel;

import de.hybris.platform.acceleratorservices.email.impl.DefaultEmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAttachmentModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;

/**
 * {@code DefaultDistEmailService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class DefaultDistEmailService extends DefaultEmailService {

    private static final Logger LOG = Logger.getLogger(DefaultDistEmailService.class);

    public static final String BLACKLIST_EMAIL_PREFIX = "mail.blacklist.email.prefix";

    private static final String CFG_OVERWRITE_FROM = "mail.overwrite.from";

    /*
     * (non-Javadoc)
     * @see de.hybris.platform.acceleratorservices.email.impl.DefaultEmailService#createEmailMessage(java.util.List, java.util.List,
     * java.util.List, de.hybris.platform.acceleratorservices.model.email.EmailAddressModel, java.lang.String, java.lang.String,
     * java.lang.String, java.util.List)
     */
    @Override
    public EmailMessageModel createEmailMessage(final List<EmailAddressModel> toAddresses, final List<EmailAddressModel> ccAddresses,
                                                final List<EmailAddressModel> bccAddresses, final EmailAddressModel fromAddress, final String replyToAddress,
                                                final String subject,
                                                final String body, final List<EmailAttachmentModel> attachments) {
        // Do all validation now before creating the message
        if (toAddresses == null || toAddresses.isEmpty()) {
            throw new IllegalArgumentException("toAddresses must not be empty");
        }
        if (fromAddress == null) {
            throw new IllegalArgumentException("fromAddress must not be null");
        }
        if (subject == null || subject.isEmpty()) {
            throw new IllegalArgumentException("subject must not be empty");
        }
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("body must not be empty");
        }
        validateEmailAddress(replyToAddress, "replyToAddress");

        final DistEmailMessageModel emailMessageModel = getModelService().create(DistEmailMessageModel.class);
        emailMessageModel.setToAddresses(toAddresses);
        emailMessageModel.setCcAddresses(ccAddresses);
        emailMessageModel.setBccAddresses(bccAddresses);
        emailMessageModel.setFromAddress(fromAddress);
        emailMessageModel.setReplyToAddress((replyToAddress != null && !replyToAddress.isEmpty()) ? replyToAddress : fromAddress.getEmailAddress());
        emailMessageModel.setSubject(subject);
        emailMessageModel.setAttachments(attachments);
        // Save the email message without the body first
        getModelService().save(emailMessageModel);
        // Setting the body and saving the email message.
        emailMessageModel.setBody(body);

        // overwrite from address. for local development environment.
        String overwriteFrom = getConfigurationService().getConfiguration().getString(CFG_OVERWRITE_FROM);
        if (StringUtils.isNotEmpty(overwriteFrom)) {
            emailMessageModel.setFromAddress(new EmailAddressModel("local overwrite " + overwriteFrom, overwriteFrom));
        }

        getModelService().save(emailMessageModel);

        return emailMessageModel;
    }

    @Override
    public boolean send(EmailMessageModel message) {
        boolean isBlacklisted = isEmailRecipientBlacklisted(message);
        if (!isBlacklisted) {
            return super.send(message);
        } else {
            LOG.warn("Email recipient is blacklisted");
            return true;
        }
    }

    protected boolean isEmailRecipientBlacklisted(EmailMessageModel message) {
        Configuration config = getConfigurationService().getConfiguration();
        if (config.containsKey(BLACKLIST_EMAIL_PREFIX)) {
            String blacklistEmailPrefix = config.getString(BLACKLIST_EMAIL_PREFIX);
            List<String> prefixes = Arrays.asList(blacklistEmailPrefix.split(","));

            if (!prefixes.isEmpty()) {
                return message.getToAddresses().stream()
                              .filter(emailAddress -> isBlacklisted(emailAddress, prefixes))
                              .findAny()
                              .isPresent();
            }
        }
        return false;
    }

    protected boolean isBlacklisted(EmailAddressModel emailAddress, List<String> prefixes) {
        String email = emailAddress.getEmailAddress();
        return prefixes.stream()
                       .filter(prefix -> email.startsWith(prefix))
                       .findAny()
                       .isPresent();
    }
}
