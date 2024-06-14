package com.namics.distrelec.occ.core.validator;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;
import org.springframework.validation.Errors;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;

import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.webservicescommons.validators.AbstractFieldValidator;

public class BizPostalCodeValidator extends AbstractFieldValidator {

    private Logger LOG = LoggerFactory.getLogger(BizPostalCodeValidator.class);

    @Autowired
    private DistrelecStoreSessionFacade distrelecStoreSessionFacade;

    @Autowired
    private DistCheckoutFacade checkoutFacade;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18NService;

    private String errorMessageID;

    private String validationPattern;

    @Override
    public boolean supports(Class<?> clazz) {
        return AddressWsDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AddressWsDTO addressWsDTO = (AddressWsDTO) target;
        Assert.notNull(errors, "Errors object must not be null");
        LOG.info("BizPostalCodeValidator target data: {}", addressWsDTO);

        String postalCode = addressWsDTO.getPostalCode();
        String countryIsoCode = addressWsDTO.getCountry() != null ? addressWsDTO.getCountry().getIsocode() : null;

        if (distrelecStoreSessionFacade.isCurrentShopExport() && checkoutFacade.isSupportedExportCountry(countryIsoCode)) {
            if (StringUtils.isNotBlank(postalCode) && StringUtils.isNotBlank(countryIsoCode)) {
                String countrySpecificPostalCodeRegex = getMessage(appendCountryIso(getValidationPattern(), countryIsoCode));
                if (StringUtils.isNotBlank(countrySpecificPostalCodeRegex) && Pattern.matches(countrySpecificPostalCodeRegex, postalCode)) {
                    return;
                }
            }
            errors.rejectValue("postalCode", appendCountryIso(getErrorMessageID(), countryIsoCode), "Please enter a valid postalCode for your country");
        }
    }

    private String appendCountryIso(String value, String countryIso) {
        LOG.info("BizPostalCodeValidator countrySpecificRegex: {}", value + DistConstants.Punctuation.UNDERSCORE + countryIso);
        return value + DistConstants.Punctuation.UNDERSCORE + countryIso;
    }

    private String getMessage(String value) {
        LOG.info("BizPostalCodeValidator currentLocale: {}", i18NService.getCurrentLocale());
        return messageSource.getMessage(value, null, i18NService.getCurrentLocale());
    }

    public String getErrorMessageID() {
        return errorMessageID;
    }

    public void setErrorMessageID(String errorMessageID) {
        this.errorMessageID = errorMessageID;
    }

    public String getValidationPattern() {
        LOG.info("BizPostalCodeValidator validationPattern: {}", validationPattern);
        return validationPattern;
    }

    public void setValidationPattern(String validationPattern) {
        this.validationPattern = validationPattern;
    }
}
