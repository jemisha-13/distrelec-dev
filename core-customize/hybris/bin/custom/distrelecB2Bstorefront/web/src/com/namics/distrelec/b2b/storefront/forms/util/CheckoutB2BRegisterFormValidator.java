package com.namics.distrelec.b2b.storefront.forms.util;

import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.storefront.forms.CheckoutB2BRegisterForm;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Arrays;
import java.util.List;
@Component
public class CheckoutB2BRegisterFormValidator implements Validator {
    
    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;
    
    public static final String GERMAN_COUNTRY_CODE = "DE";
    public static final String NORWAY_COUNTRY_CODE = "NO";
    public static final String SWEDEN_COUNTRY_CODE = "SE";
    public static final String DENMARK_COUNTRY_CODE = "DK";
    public static final String NETHERLAND_COUNTRY_CODE = "NL";
    public static final String ESTONIA_COUNTRY_CODE = "EE";
    public static final String LATVIA_COUNTRY_CODE = "LV";
    
    private static final List<String> VAT_MANDATORY_COUNTRIES = Arrays.asList(
            //
            "BE","CZ","FI","HU","IT","LV","LT","NL","PL","RO","SK","EX");
    
    @Override
    public boolean supports(Class<?> clazz) {
        return CheckoutB2BRegisterForm.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CheckoutB2BRegisterForm b2bForm = (CheckoutB2BRegisterForm) target;
        if (b2bForm != null) {
            
            String formCountryCode=b2bForm.getCountryCode();
            
            if(null==formCountryCode) {
                formCountryCode=getStoreSessionFacade().getCurrentCountry().getIsocode();
            }
            
            if (b2bForm.getExistingCustomer() != null && !b2bForm.getExistingCustomer() && formCountryCode == null) {
                errors.rejectValue("countryCode", "register.country.invalid");
            }
            if (b2bForm.getExistingCustomer() != null && !b2bForm.getExistingCustomer() && b2bForm.getCurrencyCode() == null) {
                errors.rejectValue("currencyCode", "register.currency.invalid");
            }
            if (formCountryCode != null && formCountryCode.equalsIgnoreCase(GERMAN_COUNTRY_CODE)) {
                if (!b2bForm.isTermsOfUseOption()) {
                    errors.rejectValue("termsOfUseOption", "register.tOfUse.invalid");
                }
            }
           
            if ( b2bForm.getExistingCustomer() != null && !b2bForm.getExistingCustomer() && formCountryCode != null && (formCountryCode.equalsIgnoreCase(NORWAY_COUNTRY_CODE)
                    || formCountryCode.equalsIgnoreCase(SWEDEN_COUNTRY_CODE) || formCountryCode.equalsIgnoreCase(DENMARK_COUNTRY_CODE) || formCountryCode.equalsIgnoreCase(NETHERLAND_COUNTRY_CODE) || formCountryCode.equalsIgnoreCase(ESTONIA_COUNTRY_CODE) || formCountryCode.equalsIgnoreCase(LATVIA_COUNTRY_CODE))) {
                if (StringUtils.isBlank(b2bForm.getOrganizationalNumber())) { 
                    errors.rejectValue("organizationalNumber", "validate.error.required");
                }
            }
            
            if(b2bForm.getExistingCustomer() != null && !b2bForm.getExistingCustomer() && formCountryCode!=null && VAT_MANDATORY_COUNTRIES.contains(formCountryCode.toUpperCase())) {
                if (StringUtils.isBlank(b2bForm.getVatId())) {
                    errors.rejectValue("vatId", "validate.error.required");
                }
            }
            
        }
    }

    protected DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    
    

}
