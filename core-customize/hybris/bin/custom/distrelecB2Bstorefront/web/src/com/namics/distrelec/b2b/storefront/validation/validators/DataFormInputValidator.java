/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.validation.validators;

import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.storefront.validation.annotations.DataFormInput;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * {@code DataFormInputValidator}
 * <p>
 * Validation class for the annotation {@link DataFormInput}
 * </p>
 *
 * @author <a href="laurent.zamofing@distrelec.com">Laurent Zamofing</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DataFormInputValidator implements ConstraintValidator<DataFormInput, String> {

    private String message;
    private String patternKey;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    protected CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    protected I18NService getI18nService() {
        return i18nService;
    }

    protected DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    @Override
    public void initialize(final DataFormInput constraintAnnotation) {
        message = constraintAnnotation.message();
        patternKey = constraintAnnotation.patternKey();
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext constraintContext) {
        if (StringUtils.isBlank(value)) {
            return true;
        }

        try {
            // Search the pattern
            final Locale locale = new Locale(getI18nService().getCurrentLocale().getLanguage(), getStoreSessionFacade().getCurrentCountry().getIsocode());
            final String pattern = getMessageSource().getMessage(patternKey, null, locale);
            if (pattern == null || Pattern.matches(pattern, value)) {
                return true;
            }

            String messageKey = message.replace("{", "")
                    .replace("}", "");

            MessageSourceResolvable messageSourceResolvable = new MessageSourceResolvable() {
                @Override
                public String[] getCodes() {
                    return new String[]{
                            messageKey + "." + getStoreSessionFacade().getCurrentCountry().getIsocode(),
                            messageKey
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

            constraintContext.disableDefaultConstraintViolation();
            String messageText = getMessageSource().getMessage(messageSourceResolvable, locale);
            constraintContext.buildConstraintViolationWithTemplate(messageText).addConstraintViolation();
            return false;
        } catch (final Exception e) {
            // No pattern found for the specified key
            return true;
        }
    }
}
