package com.namics.distrelec.b2b.facades.phone;

import static com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import static com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public interface DistPhoneNumberFacade {

    PhoneNumber parsePhoneNumber(String phoneNumber, String countryIsoCode);

    PhoneNumber parsePhoneNumberForCurrentCountry(String phoneNumber);

    boolean isValidPhoneNumberForRegion(String phoneNumber, String countryIso);

    boolean isValidPhoneNumber(String phoneNumber, String countryIso);

    boolean isPossiblePhoneNumber(String phoneNumber, String countryIso);

    String[] getExample(String countryIso);

    PhoneNumberType getPhoneNumberType(PhoneNumber phoneNumber);

    String formatPhoneNumber(PhoneNumber phoneNumber);

    String formatPhoneNumberForCurrentCountry(String phoneNumber);

}
