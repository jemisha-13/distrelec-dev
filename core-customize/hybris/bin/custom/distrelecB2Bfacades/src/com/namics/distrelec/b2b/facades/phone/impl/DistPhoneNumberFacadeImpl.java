package com.namics.distrelec.b2b.facades.phone.impl;

import static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import static com.namics.distrelec.b2b.core.constants.DistConstants.CountryIsoCode.*;
import static java.util.Objects.nonNull;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.facades.phone.DistPhoneNumberFacade;

@Component
public class DistPhoneNumberFacadeImpl implements DistPhoneNumberFacade {
    private static final PhoneNumberUtil PHONENUMBERUTIL = PhoneNumberUtil.getInstance();

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Override
    public PhoneNumber parsePhoneNumber(String phoneNumber, String countryIso) {
        try {
            String mappedCountryIsoCode = getMappedCountryIsoCode(countryIso);
            return PHONENUMBERUTIL.parse(phoneNumber, mappedCountryIsoCode);
        } catch (NumberParseException e) {
            return null;
        }
    }

    @Override
    public PhoneNumber parsePhoneNumberForCurrentCountry(String phoneNumber) {
        String mappedCountryIsoCode = getMappedCountryIsoCode(namicsCommonI18NService.getCurrentCountry().getIsocode());
        return parsePhoneNumber(phoneNumber, mappedCountryIsoCode);
    }

    @Override
    public boolean isValidPhoneNumberForRegion(String phoneNumber, String countryIso) {
        String mappedCountryIsoCode = getMappedCountryIsoCode(countryIso);
        return isValidForRegion(phoneNumber, mappedCountryIsoCode);
    }

    @Override
    public boolean isValidPhoneNumber(String phoneNumber, String countryIso) {
        String mappedCountryIsoCode = getMappedCountryIsoCode(countryIso);
        PhoneNumber parsedPhoneNumber = parsePhoneNumber(phoneNumber, mappedCountryIsoCode);
        if (nonNull(parsedPhoneNumber)) {
            return PHONENUMBERUTIL.isValidNumber(parsedPhoneNumber);
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean isPossiblePhoneNumber(String phoneNumber, String countryIso) {
        String mappedCountryIsoCode = getMappedCountryIsoCode(countryIso);
        PhoneNumber parsedPhoneNumber = parsePhoneNumber(phoneNumber, mappedCountryIsoCode);
        if (nonNull(parsedPhoneNumber)) {
            return PHONENUMBERUTIL.isPossibleNumber(parsedPhoneNumber);
        }
        return Boolean.FALSE;
    }

    @Override
    public String[] getExample(String countryIso) {
        if (StringUtils.isBlank(countryIso) || countryIso.equalsIgnoreCase(DistConstants.CountryIsoCode.EXPORT)) {
            PhoneNumber exampleNumber = PHONENUMBERUTIL.getExampleNumber(SWITZERLAND);
            return formatExample(exampleNumber);
        }
        String mappedCountryIsoCode = getMappedCountryIsoCode(countryIso);
        PhoneNumber exampleNumber = PHONENUMBERUTIL.getExampleNumber(mappedCountryIsoCode);
        return formatExample(exampleNumber);
    }

    @Override
    public PhoneNumberUtil.PhoneNumberType getPhoneNumberType(PhoneNumber phoneNumber) {
        return PHONENUMBERUTIL.getNumberType(phoneNumber);
    }

    @Override
    public String formatPhoneNumber(PhoneNumber phoneNumber) {
        return PHONENUMBERUTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    }

    @Override
    public String formatPhoneNumberForCurrentCountry(String phoneNumber) {
        PhoneNumber parsedPhoneNumber = parsePhoneNumberForCurrentCountry(phoneNumber);
        if (nonNull(parsedPhoneNumber)) {
            return formatPhoneNumber(parsedPhoneNumber);
        }
        return StringUtils.EMPTY;
    }

    private String[] formatExample(PhoneNumber phoneNumber) {
        return phoneNumber == null ? new String[] {}
                                   : new String[] { PHONENUMBERUTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL),
                                                    PHONENUMBERUTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL),
                                                    PHONENUMBERUTIL.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164) };
    }

    private String getMappedCountryIsoCode(String countryIso) {
        if (StringUtils.equalsIgnoreCase(countryIso, DistConstants.CountryIsoCode.NORTHERN_IRELAND)) {
            return DistConstants.CountryIsoCode.GREAT_BRITAIN;
        }
        return countryIso;
    }

    private boolean isValidForRegion(String phoneNumber, String countryIso) {
        PhoneNumber parsedPhoneNumber = parsePhoneNumber(phoneNumber, countryIso);
        if (LIECHTENSTEIN.equals(countryIso)) {
            PhoneNumber parsedPhoneNumberForSwitzerland = parsePhoneNumber(phoneNumber, SWITZERLAND);
            return isValidForRegion(parsedPhoneNumberForSwitzerland, SWITZERLAND) || isValidForRegion(parsedPhoneNumber, LIECHTENSTEIN);
        }

        if (SWITZERLAND.equals(countryIso)) {
            PhoneNumber parsedPhoneNumberForLiechtenstein = parsePhoneNumber(phoneNumber, LIECHTENSTEIN);
            return isValidForRegion(parsedPhoneNumber, SWITZERLAND) || isValidForRegion(parsedPhoneNumberForLiechtenstein, LIECHTENSTEIN);
        }

        if (VATICAN.equals(countryIso) || SAN_MARINO.equals(countryIso)) {
            PhoneNumber parsedPhoneNumberForItaly = parsePhoneNumber(phoneNumber, ITALY);
            return isValidForRegion(parsedPhoneNumber, countryIso) || isValidForRegion(parsedPhoneNumberForItaly, ITALY);
        }
        return isValidForRegion(parsedPhoneNumber, countryIso);
    }

    private boolean isValidForRegion(PhoneNumber phoneNumber, String countryCode) {
        if (nonNull(phoneNumber)) {
            return PHONENUMBERUTIL.isValidNumberForRegion(phoneNumber, countryCode) && PHONENUMBERUTIL.isPossibleNumber(phoneNumber);

        }
        return Boolean.FALSE;
    }
}
