package com.namics.distrelec.occ.core.validator;

import static java.util.Objects.nonNull;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.phone.DistPhoneNumberFacade;
import com.namics.distrelec.b2b.facades.vat.eu.DistVatEUFacade;

import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commercewebservicescommons.dto.user.UserSignUpWsDTO;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;

public class DistRegistrationValidator implements Validator {
    public static final String EMAIL_REGEX = "email.regex";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistVatEUFacade distVatEUFacade;

    @Autowired
    private DistCustomerFacade customerFacade;

    @Autowired
    private DistPhoneNumberFacade distPhoneNumberFacade;

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Override
    public boolean supports(Class<?> aClass) {
        return UserSignUpWsDTO.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final UserSignUpWsDTO form = (UserSignUpWsDTO) target;
        if (isNotGuest(form)) {
            validateTitleCode(errors, form.getTitleCode());

            validateName(errors, form.getFirstName(), "firstName", "register.firstName.invalid");
            validateName(errors, form.getLastName(), "lastName", "register.lastName.invalid");
            validateLengthOfLastNameAndFirstName(errors, form.getFirstName(), form.getLastName());

            validateEmail(errors, form.getUid());
            if (StringUtils.isNotEmpty(form.getLegalEmail())) {
                validateEmail(errors, form.getLegalEmail());
            }

            validatePassword(errors, form.getPassword());
            comparePasswords(errors, form.getPassword(), form.getCheckPwd());

            validateTermsAndConditions(errors, form.getTermsCheck());
            validateVatId(errors, form.getVatId());

            validateAndParsePhoneNumber(form, errors);

            validateCodiceFiscale(errors, form);
            validateJobRole(errors, form);
        } else {
            validatePassword(errors, form.getPassword());
            comparePasswords(errors, form.getPassword(), form.getCheckPwd());
        }
    }

    private boolean isNotGuest(UserSignUpWsDTO form) {
        return StringUtils.isEmpty(form.getGuid());
    }

    private void validateJobRole(Errors errors, UserSignUpWsDTO form) {
        if (customerFacade.isJobRoleShown(form.getRegistrationType(), form.getCustomerType()) && !isFunctionCodeValid(form.getFunctionCode())) {
            errors.rejectValue("functionCode", "register.function.invalid");
        }
    }

    private boolean isFunctionCodeValid(String functionCode) {
        return StringUtils.isNotBlank(functionCode) && functionCode.length() < 3;
    }

    public List<CountryData> getCountriesB2C() {
        return customerFacade.getCountriesForRegistration(SiteChannel.B2C);
    }

    private void validateLengthOfLastNameAndFirstName(Errors errors, String firstName, String lastName) {
        if (StringUtils.length(firstName) + StringUtils.length(lastName) > 255) {
            errors.rejectValue("lastName", "register.name.invalid");
            errors.rejectValue("firstName", "register.name.invalid");
        }
    }

    private void validateName(final Errors errors, final String name, final String propertyName, final String property) {
        if (StringUtils.isBlank(name)) {
            errors.rejectValue(propertyName, property);
        } else if (StringUtils.length(name) > 35) {
            errors.rejectValue(propertyName, property);
        }
    }

    private void validateEmail(final Errors errors, final String email) {
        if (StringUtils.isEmpty(email)) {
            errors.rejectValue("email", "register.email.invalid");
        } else if (StringUtils.length(email) > 255 || !validateEmailAddress(email)) {
            errors.rejectValue("email", "register.email.invalid");
        }
    }

    private boolean validateEmailAddress(final String email) {
        final Matcher matcher = Pattern.compile(configurationService.getConfiguration().getString(EMAIL_REGEX))
                                       .matcher(email);
        return matcher.matches();
    }

    private void validatePassword(final Errors errors, final String pwd) {
        if (StringUtils.isEmpty(pwd)) {
            errors.rejectValue("pwd", "register.pwd.invalid");
        } else if (StringUtils.length(pwd) < 6 || StringUtils.length(pwd) > 255) {
            errors.rejectValue("pwd", "register.pwd.invalid");
        }
    }

    private void comparePasswords(final Errors errors, final String pwd, final String checkPwd) {
        if (StringUtils.isNotEmpty(pwd) && StringUtils.isNotEmpty(checkPwd) && !StringUtils.equals(pwd, checkPwd)) {
            errors.rejectValue("checkPwd", "validation.checkPwd.equals");
        } else {
            if (StringUtils.isEmpty(checkPwd)) {
                errors.rejectValue("checkPwd", "register.checkPwd.invalid");
            }
        }
    }

    private void validateTermsAndConditions(final Errors errors, final boolean termsCheck) {
        if (!termsCheck) {
            errors.rejectValue("termsCheck", "register.terms.not.accepted");
        }
    }

    private void validateTitleCode(final Errors errors, final String titleCode) {
        if (StringUtils.isNotEmpty(titleCode) && StringUtils.length(titleCode) > 255) {
            errors.rejectValue("titleCode", "register.title.invalid");
        }
    }

    private void validateVatId(final Errors errors, final String vatId) {
        CountryModel currentCountry = namicsCommonI18NService.getCurrentCountry();
        if (StringUtils.isNotEmpty(vatId) && nonNull(currentCountry) && distVatEUFacade.isVatValidatedExternally(currentCountry.getIsocode())) {
            String countryIsoCode = currentCountry.getIsocode();
            boolean success = distVatEUFacade.validateVatNumber(distVatEUFacade.getVatWithoutPrefix(vatId, countryIsoCode),
                                                                distVatEUFacade.getVatPrefixForCountry(countryIsoCode));
            if (!success) {
                errors.rejectValue("vatId", "vat.validation.error." + countryIsoCode);
            }
        }
    }

    private void validateAndParsePhoneNumber(final UserSignUpWsDTO userSignUpWsDTO, final Errors errors) {
        if (StringUtils.isEmpty(userSignUpWsDTO.getPhoneNumber())
                || !distPhoneNumberFacade.isValidPhoneNumberForRegion(userSignUpWsDTO.getPhoneNumber(), userSignUpWsDTO.getCountryCode())) {
            markPhoneNumberAsInvalid(userSignUpWsDTO, errors);
        }
    }

    private void markPhoneNumberAsInvalid(UserSignUpWsDTO userSignUpWsDTO, Errors errors) {
        errors.rejectValue("phoneNumber", "register.phoneNumber.invalid", new String[] { userSignUpWsDTO.getPhoneNumber() },
                           "Please enter a valid landline number.");
    }

    private void validateCodiceFiscale(Errors errors, UserSignUpWsDTO form) {
        if (CustomerType.B2C.equals(form.getCustomerType()) && StringUtils.isEmpty(form.getLegalEmail())
                && nonNull(namicsCommonI18NService.getCurrentCountry())) {
            if (DistConstants.CountryIsoCode.ITALY.equals(namicsCommonI18NService.getCurrentCountry().getIsocode())
                    && StringUtils.isBlank(form.getCodiceFiscale())) {
                errors.rejectValue("codiceFiscale", "register.codiceFiscale.invalid");
            }
            if (getCountriesB2C().stream().noneMatch(countryData -> countryData.getIsocode().equals(form.getCountryCode()))) {
                errors.rejectValue("registerForm", "register.country.invalid");
            }
        }
    }
}
