package com.namics.distrelec.b2b.storefront.validation.validators;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.storefront.forms.AbstractDistAddressForm;
import com.namics.distrelec.b2b.storefront.validation.annotations.BizPostalCode;

import de.hybris.platform.servicelayer.i18n.I18NService;

public class BizPostalCodeValidator implements ConstraintValidator<BizPostalCode, AbstractDistAddressForm> {

    @Autowired
    private DistrelecStoreSessionFacade distrelecStoreSessionFacade;

    @Autowired
    private DistCheckoutFacade distCheckoutFacade;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18NService;

    private String message;

    private String validationPattern;

    @Override
    public void initialize(BizPostalCode constraintAnnotation) {
        message = constraintAnnotation.message();
        validationPattern = constraintAnnotation.pattern();
    }

    @Override
    public boolean isValid(AbstractDistAddressForm value, ConstraintValidatorContext constraintValidatorContext) {
        if (distrelecStoreSessionFacade.isCurrentShopExport() && distCheckoutFacade.isSupportedExportCountry(value.getCountryIso())) {
            if (StringUtils.isNotBlank(value.getPostalCode()) && StringUtils.isNotBlank(value.getCountryIso())) {
                String countrySpecificPostalCodeRegex = getMessage(appendCountryIso(validationPattern, value.getCountryIso()));

                if (StringUtils.isNotBlank(countrySpecificPostalCodeRegex) && Pattern.matches(countrySpecificPostalCodeRegex, value.getPostalCode())) {
                    return Boolean.TRUE;
                }
            }

            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(getMessage(appendCountryIso(message, value.getCountryIso())))
                                      .addPropertyNode("postalCode")
                                      .addConstraintViolation();
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private String appendCountryIso(String value, String countryIso) {
        return value + DistConstants.Punctuation.UNDERSCORE + countryIso;
    }

    private String getMessage(String value) {
        return messageSource.getMessage(value, null, i18NService.getCurrentLocale());
    }
}
