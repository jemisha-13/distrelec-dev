package com.namics.distrelec.b2b.storefront.validation.validators;

import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.storefront.validation.annotations.NotBlankForCountry;

import de.hybris.platform.servicelayer.i18n.I18NService;

public class NotBlankForCountryValidator implements ConstraintValidator<NotBlankForCountry, String> {

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18NService;

    private String[] countries;

    private String message;

    @Override
    public void initialize(NotBlankForCountry constraintAnnotation) {
        message = constraintAnnotation.message();
        countries = constraintAnnotation.countries();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        String currentCountryIsoCode = storeSessionFacade.getCurrentCountry().getIsocode();
        if (Arrays.asList(countries).contains(currentCountryIsoCode) && StringUtils.isBlank(value)) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(messageSource.getMessage(message, null, i18NService.getCurrentLocale()))
                                      .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
