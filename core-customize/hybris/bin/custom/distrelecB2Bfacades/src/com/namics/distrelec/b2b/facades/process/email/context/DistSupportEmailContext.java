/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistSupportProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Velocity context for a feedback email.
 */
public class DistSupportEmailContext extends AbstractDistEmailContext {

    private String title;
    private String firstname;
    private String lastname;
    private String email;
    private String emailSubject;
    private String phone;
    private String comment;
    private String contactBy;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof DistSupportProcessModel) {
            final DistSupportProcessModel supportProcess = (DistSupportProcessModel) businessProcessModel;
            put(DISPLAY_NAME, supportProcess.getEmailDisplayName());
            put(FROM_DISPLAY_NAME, supportProcess.getFromDisplayName());
            setContactBy(supportProcess.getContactBy());
            put(EMAIL, getSupportEmail());
            setFirstname(supportProcess.getFirstname());
            setLastname(supportProcess.getLastname());
            put(FROM_EMAIL, supportProcess.getEmail());
            setPhone(supportProcess.getPhone());
            setEmail(supportProcess.getEmail());
            setComment(supportProcess.getComment());
            setTitle(supportProcess.getTitle());
            setEmailSubject(supportProcess.getEmailSubjectMsg());
        }
    }

    private String getSupportEmail() {
        return getEmail(DistConstants.PropKey.Email.SUPPORT_EMAIL_PREFIX, DistConstants.PropKey.Email.SUPPORT_EMAIL_DEFAULT);
    }

    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof DistSupportProcessModel) {
            return ((DistSupportProcessModel) businessProcessModel).getSite();
        }
        return null;
    }

    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
        return null;
    }

    @Override
    protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof StoreFrontCustomerProcessModel) {
            return ((StoreFrontCustomerProcessModel) businessProcessModel).getCustomer().getSessionLanguage();
        }
        return null;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(final String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(final I18NService i18NService) {
        this.i18NService = i18NService;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    @Override
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Override
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getContactBy() {
        return contactBy;
    }

    public void setContactBy(final String contactBy) {
        this.contactBy = contactBy;
    }

}
