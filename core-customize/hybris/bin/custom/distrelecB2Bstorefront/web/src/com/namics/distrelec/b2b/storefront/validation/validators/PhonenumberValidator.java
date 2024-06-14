/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.validation.validators;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.storefront.validation.annotations.Phonenumber;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for valid phone number.
 */
public class PhonenumberValidator implements ConstraintValidator<Phonenumber, Object> {

    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    private String message;
    private String countryIsoCode;
    private String[] example;
    private boolean checkForCurrentCountry;
    private boolean required;
    public static final String SWISS_COUNTRY_CODE="CH";
    public static final String BIZ_COUNTRY_CODE="EX";

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    protected MessageSource getMessageSource() {
        return messageSource;
    }

    protected I18NService getI18nService() {
        return i18nService;
    }

    protected DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    @Override
    public void initialize(final Phonenumber constraintAnnotation) {

        required = constraintAnnotation.required();
        message = constraintAnnotation.message();
        countryIsoCode = getStoreSessionFacade().getCurrentCountry().getIsocode();

        checkForCurrentCountry = constraintAnnotation.checkForCurrentCountry();
        final PhoneNumber nr = phoneUtil.getExampleNumber(countryIsoCode);

        example = nr == null ? new String[] {} : new String[] { phoneUtil.format(nr, PhoneNumberFormat.NATIONAL),
                phoneUtil.format(nr, PhoneNumberFormat.INTERNATIONAL), phoneUtil.format(nr, PhoneNumberFormat.E164) };
    }

    @Override
    public boolean isValid(final Object object, final ConstraintValidatorContext constraintContext) {
        if (StringUtils.isEmpty((String) object)) {
            return true;
        }
    
        initializeData();
        
        if(countryIsoCode!=null && countryIsoCode.equalsIgnoreCase(BIZ_COUNTRY_CODE) && !required) {
            return true;
        }
            
        boolean result = true;

        try {
            final PhoneNumber numberProto = phoneUtil.parse(object.toString(), countryIsoCode);
            if(checkForCurrentCountry){
                result = phoneUtil.isValidNumberForRegion(numberProto, countryIsoCode);
            }else{
                result = phoneUtil.isValidNumber(numberProto);
            }
        } catch (final NumberParseException e) {
            result = false;
        }

        if (!result) {
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate(
                    getMessageSource().getMessage(message.substring(1, message.length() - 1), example, message, getI18nService().getCurrentLocale()))
                    .addConstraintViolation();
        }

        return result;
    }
    
    private void initializeData() {
        countryIsoCode = getStoreSessionFacade().getCurrentCountry().getIsocode();

        if(countryIsoCode.equalsIgnoreCase("XI")){
            countryIsoCode = "GB";
        }

        if(countryIsoCode!=null && countryIsoCode.equalsIgnoreCase(BIZ_COUNTRY_CODE)) {
            final PhoneNumber nr = phoneUtil.getExampleNumber(SWISS_COUNTRY_CODE);
            example = nr == null ? new String[] {} : new String[] { phoneUtil.format(nr, PhoneNumberFormat.NATIONAL),
                    phoneUtil.format(nr, PhoneNumberFormat.INTERNATIONAL), phoneUtil.format(nr, PhoneNumberFormat.E164) };
        }else {
            final PhoneNumber nr = phoneUtil.getExampleNumber(countryIsoCode);
            example = nr == null ? new String[] {} : new String[] { phoneUtil.format(nr, PhoneNumberFormat.NATIONAL),
                    phoneUtil.format(nr, PhoneNumberFormat.INTERNATIONAL), phoneUtil.format(nr, PhoneNumberFormat.E164) };
        }
    }
}
