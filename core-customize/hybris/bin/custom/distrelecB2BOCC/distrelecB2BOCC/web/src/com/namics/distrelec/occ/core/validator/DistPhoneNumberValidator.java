package com.namics.distrelec.occ.core.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.facades.phone.DistPhoneNumberFacade;

import de.hybris.platform.webservicescommons.validators.AbstractFieldValidator;

public class DistPhoneNumberValidator extends AbstractFieldValidator {

    private String errorMessageID;

    @Autowired
    private DistPhoneNumberFacade distPhoneNumberFacade;

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object o, Errors errors) {
        Assert.notNull(errors, "Errors object must not be null");
        String fieldValue = (String) errors.getFieldValue(getFieldPath());

        if (StringUtils.isEmpty(fieldValue)) {
            return;
        }
        String countryIsoCode = namicsCommonI18NService.getCurrentCountry().getIsocode();
        if (countryIsoCode.equalsIgnoreCase(DistConstants.CountryIsoCode.EXPORT)) {
            return;
        }

        if (!distPhoneNumberFacade.isValidPhoneNumberForRegion(fieldValue, countryIsoCode)) {
            markFieldAsInvalid(errors, countryIsoCode);
        }
    }

    private void markFieldAsInvalid(Errors errors, String countryIsoCode) {
        errors.rejectValue(getFieldPath(), getErrorMessageID(), distPhoneNumberFacade.getExample(countryIsoCode),
                           "Please enter a valid phone number for your country");
    }

    public String getErrorMessageID() {
        return errorMessageID;
    }

    public void setErrorMessageID(String errorMessageID) {
        this.errorMessageID = errorMessageID;
    }
}
