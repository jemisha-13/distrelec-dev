/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistFeedbackProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

/**
 * Velocity context for a feedback email.
 */
public class DistFeedbackEmailContext extends AbstractDistEmailContext {

    private String name;
    private String emailSubject;
    private String phone;
    private String feedback;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof DistFeedbackProcessModel) {
            final DistFeedbackProcessModel feedbackProcess = (DistFeedbackProcessModel) businessProcessModel;
            put(DISPLAY_NAME, feedbackProcess.getEmailDisplayName());
            put(FROM_DISPLAY_NAME, feedbackProcess.getFromDisplayName());
            put(EMAIL, BooleanUtils.isTrue(feedbackProcess.getSearchFeedback()) ? getSearchFeedbackEmail() : getFeedbackEmail());
            setName(feedbackProcess.getName());
            put(FROM_EMAIL, feedbackProcess.getEmail());
            setPhone(feedbackProcess.getPhone());
            setFeedback(feedbackProcess.getFeedback());
            setEmailSubject(feedbackProcess.getEmailSubjectMsg());
        }
    }

    private String getFeedbackEmail() {
        return getEmail(DistConstants.PropKey.Email.FEEDBACK_EMAIL_PREFIX, DistConstants.PropKey.Email.FEEDBACK_EMAIL_DEFAULT);
    }

    private String getSearchFeedbackEmail() {
        return getEmail(DistConstants.PropKey.Email.FEEDBACK_SEARCH_EMAIL_PREFIX, DistConstants.PropKey.Email.FEEDBACK_SEARCH_EMAIL_DEFAULT);
    }

    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof DistFeedbackProcessModel) {
            return ((DistFeedbackProcessModel) businessProcessModel).getSite();
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

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(final String feedback) {
        this.feedback = feedback;
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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

}
