package com.namics.distrelec.occ.core.validator;

import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;

import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.validators.AbstractFieldValidator;

public class DistDataFormInputValidator extends AbstractFieldValidator {

    private static final Logger LOG = LogManager.getLogger(DistDataFormInputValidator.class);

    private String messageKey;

    private String patternKey;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        Assert.notNull(errors, "Errors object must not be null");
        String fieldValue = (String) errors.getFieldValue(getFieldPath());

        if (StringUtils.isBlank(fieldValue)) {
            return;
        }

        try {
            final String countryIsoCode = namicsCommonI18NService.getCurrentCountry().getIsocode();
            final Locale locale = new Locale(i18nService.getCurrentLocale().getLanguage(), countryIsoCode);
            final String pattern = getMessageSource().getMessage(getPatternKey(), null, locale);
            if (Pattern.matches(pattern, fieldValue)) {
                return;
            }

            MessageSourceResolvable messageSourceResolvable = new MessageSourceResolvable() {
                @Override
                public String[] getCodes() {
                    return new String[] {
                                          getMessageKey() + "." + countryIsoCode,
                                          getMessageKey()
                    };
                }

                @Override
                public Object[] getArguments() {
                    return new Object[0];
                }

                @Override
                public String getDefaultMessage() {
                    return null;
                }
            };

            String messageText = getMessageSource().getMessage(messageSourceResolvable, locale);
            errors.reject(messageText);
        } catch (final Exception e) {
            LOG.warn("No pattern found for the specified key");
        }
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getPatternKey() {
        return patternKey;
    }

    public void setPatternKey(String patternKey) {
        this.patternKey = patternKey;
    }

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
