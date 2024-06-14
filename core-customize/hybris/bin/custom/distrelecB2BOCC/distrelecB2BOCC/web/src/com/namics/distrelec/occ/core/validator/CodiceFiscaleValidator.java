package com.namics.distrelec.occ.core.validator;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;

import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.validators.AbstractFieldValidator;

public class CodiceFiscaleValidator extends AbstractFieldValidator {

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private MessageSource messageSource;

    private String errorMessageID;

    @Override
    public boolean supports(Class<?> aClass) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        String codiceFiscale = (String) target;
        String currentCountryIsoCode = namicsCommonI18NService.getCurrentCountry().getIsocode();
        String countrySpecificPostalCodeRegex = getMessage("register.codiceFiscale.validationPattern");

        if (StringUtils.equals(currentCountryIsoCode, DistConstants.CountryIsoCode.ITALY)
                && !Pattern.matches(countrySpecificPostalCodeRegex, codiceFiscale)) {
            errors.rejectValue(getFieldPath(), getErrorMessageID(), "Codice fiscale is invalid. (RegExp)");
        }
    }

    private String getMessage(String value) {
        return messageSource.getMessage(value, null, i18NService.getCurrentLocale());
    }

    public String getErrorMessageID() {
        return errorMessageID;
    }

    public void setErrorMessageID(String errorMessageID) {
        this.errorMessageID = errorMessageID;
    }
}
