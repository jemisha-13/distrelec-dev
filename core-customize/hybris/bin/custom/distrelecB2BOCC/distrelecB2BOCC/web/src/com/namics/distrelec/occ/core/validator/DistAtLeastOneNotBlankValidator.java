package com.namics.distrelec.occ.core.validator;

import java.util.Arrays;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.hybris.platform.servicelayer.i18n.I18NService;

public class DistAtLeastOneNotBlankValidator implements Validator {

    private static final Logger LOG = LogManager.getLogger(DistAtLeastOneNotBlankValidator.class);

    private String[] fieldnames;

    private String messageKey;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {

        for (final String number : fieldnames) {
            try {
                final String entry = (String) PropertyUtils.getProperty(target, number);
                if (StringUtils.isNotBlank(entry)) {
                    return;
                }
            } catch (final Exception e) {
                LOG.error("Could not validate", e);
                throw new IllegalArgumentException(e);
            }

        }
        String messageText = getMessageSource().getMessage(getMessageKey(), null, getI18nService().getCurrentLocale());
        Arrays.stream(fieldnames).forEach(fieldName -> errors.rejectValue(fieldName, messageText));
    }

    public String[] getFieldnames() {
        return fieldnames;
    }

    public void setFieldnames(String[] fieldnames) {
        this.fieldnames = fieldnames;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    protected I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(I18NService i18nService) {
        this.i18nService = i18nService;
    }
}
