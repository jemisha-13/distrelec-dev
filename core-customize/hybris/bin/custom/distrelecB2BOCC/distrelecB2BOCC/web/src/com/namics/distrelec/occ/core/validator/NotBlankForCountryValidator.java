package com.namics.distrelec.occ.core.validator;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;

import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;

import de.hybris.platform.webservicescommons.validators.AbstractFieldValidator;

public class NotBlankForCountryValidator extends AbstractFieldValidator {

    private List<String> countryCodes;

    private String errorMessageID;

    private DistrelecStoreSessionFacade storeSessionFacade;

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public void validate(Object target, Errors errors) {
        String currentCountryIsoCode = storeSessionFacade.getCurrentCountry().getIsocode();
        String fieldValue = (String) errors.getFieldValue(this.getFieldPath());
        if (countryCodes.contains(currentCountryIsoCode) && StringUtils.isBlank(fieldValue)) {
            errors.rejectValue(this.getFieldPath(), "field.required", new String[]{this.getFieldPath()}, "This field is required.");
        }

    }

    protected DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    public List<String> getCountryCodes() {
        return countryCodes;
    }

    public void setCountryCodes(List<String> countryCodes) {
        this.countryCodes = countryCodes;
    }

    public String getErrorMessageID() {
        return errorMessageID;
    }

    public void setErrorMessageID(String errorMessageID) {
        this.errorMessageID = errorMessageID;
    }
}
