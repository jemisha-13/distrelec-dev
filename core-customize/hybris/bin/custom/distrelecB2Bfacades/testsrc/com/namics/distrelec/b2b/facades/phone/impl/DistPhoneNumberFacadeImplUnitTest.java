package com.namics.distrelec.b2b.facades.phone.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.CountryIsoCode.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CountryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistPhoneNumberFacadeImplUnitTest {

    private static final String SWITZERLAND_PHONE_NUMBER_PREFIX = "+41";

    private static final String LIECHTENSTEIN_PHONE_NUMBER_PREFIX = "+423";

    private static final String NORTHERN_IRELAND_PHONE_NUMBER_PREFIX = "+44";

    private static final String ITALY_PHONE_NUMBER_PREFIX = "+39";

    private static final String EXAMPLE_SWITZERLAND_PHONE_NUMBER = "+41782345678";

    private static final String EXAMPLE_ITALY_PHONE_NUMBER = "+390811234567";

    private static final String EXAMPLE_SAN_MARINO_PHONE_NUMBER = "+378991007";

    private static final String EXAMPLE_VATICAN_PHONE_NUMBER = "+390669812345";

    private static final String EXAMPLE_SWITZERLAND_FIXED_PHONE_NUMBER = "+41212345671";

    private static final String EXAMPLE_LIECHTENSTEIN_PHONE_NUMBER = "+4233993311";

    private static final String EXAMPLE_NORTHERN_IRELAND_PHONE_NUMBER = "+442890311600";

    private static final String INVALID_PHONE_NUMBER = "123";

    @Mock
    private NamicsCommonI18NService namicsCommonI18NService;

    @Mock
    private CountryModel country;

    @InjectMocks
    private DistPhoneNumberFacadeImpl distPhoneNumberFacade;

    @Test
    public void testParsePhoneNumberForSwitzerlandWithPrefix() {
        Phonenumber.PhoneNumber phoneNumber = distPhoneNumberFacade.parsePhoneNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER,
                                                                                     SWITZERLAND);

        assertNotNull(phoneNumber);
    }

    @Test
    public void testParsePhoneNumberForSwitzerlandWithoutPrefix() {
        String number = removePrefixFromFullNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER, SWITZERLAND_PHONE_NUMBER_PREFIX);
        Phonenumber.PhoneNumber phoneNumber = distPhoneNumberFacade.parsePhoneNumber(number, SWITZERLAND);

        assertNotNull(phoneNumber);
    }

    @Test
    public void testParsePhoneNumberForNorthernIreland() {
        Phonenumber.PhoneNumber phoneNumber = distPhoneNumberFacade.parsePhoneNumber(EXAMPLE_NORTHERN_IRELAND_PHONE_NUMBER,
                                                                                     NORTHERN_IRELAND);

        assertNotNull(phoneNumber);
    }

    @Test
    public void testParsePhoneNumberWithInvalidRegionAndWithoutPrefix() {
        String phone = removePrefixFromFullNumber(EXAMPLE_NORTHERN_IRELAND_PHONE_NUMBER, NORTHERN_IRELAND_PHONE_NUMBER_PREFIX);
        Phonenumber.PhoneNumber phoneNumber = distPhoneNumberFacade.parsePhoneNumber(phone, EXPORT);

        assertNull(phoneNumber);
    }

    @Test
    public void testParsePhoneNumberWithInvalidRegionAndWithPrefix() {
        Phonenumber.PhoneNumber phoneNumber = distPhoneNumberFacade.parsePhoneNumber(EXAMPLE_NORTHERN_IRELAND_PHONE_NUMBER, EXPORT);

        assertNotNull(phoneNumber);
    }

    @Test
    public void testParsePhoneNumberForCurrentCountry() {
        when(country.getIsocode()).thenReturn(SWITZERLAND);
        when(namicsCommonI18NService.getCurrentCountry()).thenReturn(country);

        Phonenumber.PhoneNumber phoneNumber = distPhoneNumberFacade.parsePhoneNumberForCurrentCountry(EXAMPLE_NORTHERN_IRELAND_PHONE_NUMBER);
        assertNotNull(phoneNumber);
    }

    @Test
    public void testIsValidPhoneNumberForSwitzerlandRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_SWITZERLAND_PHONE_NUMBER, SWITZERLAND);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidLiechtensteinPhoneNumberForSwitzerlandRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_LIECHTENSTEIN_PHONE_NUMBER, SWITZERLAND);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidLiechtensteinPhoneNumberWithoutPrefixForSwitzerlandRegion() {
        String number = removePrefixFromFullNumber(EXAMPLE_LIECHTENSTEIN_PHONE_NUMBER, LIECHTENSTEIN_PHONE_NUMBER_PREFIX);
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(number, SWITZERLAND);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidSwitzerlandPhoneNumberForLiechtensteinRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_SWITZERLAND_PHONE_NUMBER, LIECHTENSTEIN);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidPhoneNumberForNorthernIrelandRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_NORTHERN_IRELAND_PHONE_NUMBER,
                                                                            NORTHERN_IRELAND);

        assertTrue(isValid);
    }

    @Test
    public void testEmptyPhoneNumberForRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(StringUtils.EMPTY, NORTHERN_IRELAND);

        assertFalse(isValid);
    }

    @Test
    public void testIsValidPhoneNumberForItalyRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_ITALY_PHONE_NUMBER, ITALY);

        assertTrue(isValid);
    }

    @Test
    public void testIsVaticanNumberInvalidPhoneNumberForItalyRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_VATICAN_PHONE_NUMBER, ITALY);

        assertFalse(isValid);
    }

    @Test
    public void testIsValidPhoneNumberForVaticanRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_VATICAN_PHONE_NUMBER, VATICAN);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidPhoneNumberForSanMarinoRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_SAN_MARINO_PHONE_NUMBER, SAN_MARINO);

        assertTrue(isValid);
    }

    @Test
    public void testIsItalyPhoneNumberValidForVaticanRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_ITALY_PHONE_NUMBER, VATICAN);

        assertTrue(isValid);
    }

    @Test
    public void testIsItalyPhoneNumberValidForSanMarinoRegion() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(EXAMPLE_ITALY_PHONE_NUMBER, SAN_MARINO);

        assertTrue(isValid);
    }

    @Test
    public void testIsItalyPhoneNumberWithoutPrefixValidForSanMarinoRegion() {
        String number = removePrefixFromFullNumber(EXAMPLE_ITALY_PHONE_NUMBER, ITALY_PHONE_NUMBER_PREFIX);
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumberForRegion(number, SAN_MARINO);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidPhoneNumberForSwitzerland() {
        String number = removePrefixFromFullNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER, SWITZERLAND_PHONE_NUMBER_PREFIX);
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumber(number, SWITZERLAND);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidPhoneNumberForSwitzerlandWithPrefix() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER, SWITZERLAND);

        assertTrue(isValid);
    }

    @Test
    public void testIsValidPhoneNumberForNorthernIreland() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumber(EXAMPLE_NORTHERN_IRELAND_PHONE_NUMBER, NORTHERN_IRELAND);

        assertTrue(isValid);
    }

    @Test
    public void testInvalidPhoneNumberForNorthernIreland() {
        String number = removePrefixFromFullNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER, SWITZERLAND_PHONE_NUMBER_PREFIX);
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumber(number, NORTHERN_IRELAND);

        assertFalse(isValid);
    }

    @Test
    public void testIsEmptyValidPhoneNumber() {
        boolean isValid = distPhoneNumberFacade.isValidPhoneNumber(StringUtils.EMPTY, NORTHERN_IRELAND);

        assertFalse(isValid);
    }

    @Test
    public void testIsPossiblePhoneNumberWithoutPrefix() {
        String number = removePrefixFromFullNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER, SWITZERLAND_PHONE_NUMBER_PREFIX);
        boolean isValid = distPhoneNumberFacade.isPossiblePhoneNumber(number, SWITZERLAND);

        assertTrue(isValid);
    }

    @Test
    public void testIsPossiblePhoneNumberWithPrefix() {
        boolean isValid = distPhoneNumberFacade.isPossiblePhoneNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER, SWITZERLAND);

        assertTrue(isValid);
    }

    @Test
    public void testInvalidIsPossiblePhoneForNorthernIreland() {
        boolean isValid = distPhoneNumberFacade.isPossiblePhoneNumber(INVALID_PHONE_NUMBER, NORTHERN_IRELAND);

        assertFalse(isValid);
    }

    @Test
    public void testIsEmptyPossiblePhoneNumber() {
        boolean isValid = distPhoneNumberFacade.isPossiblePhoneNumber(StringUtils.EMPTY, SWITZERLAND);

        assertFalse(isValid);
    }

    @Test
    public void testGetExampleForSwitzerland() {
        String[] examples = distPhoneNumberFacade.getExample(SWITZERLAND);

        assertNotNull(examples);
        assertTrue(Arrays.stream(examples).anyMatch(example -> example.startsWith(SWITZERLAND_PHONE_NUMBER_PREFIX)));
    }

    @Test
    public void testGetExampleForExport() {
        String[] examples = distPhoneNumberFacade.getExample(EXPORT);

        assertNotNull(examples);
        assertTrue(Arrays.stream(examples).anyMatch(example -> example.startsWith(SWITZERLAND_PHONE_NUMBER_PREFIX)));
    }

    @Test
    public void testGetExampleForInvalidCountry() {
        String[] examples = distPhoneNumberFacade.getExample("XX");

        assertNotNull(examples);
        assertArrayEquals(examples, ArrayUtils.EMPTY_STRING_ARRAY);
    }

    @Test
    public void testMobilePhoneNumberType() {
        Phonenumber.PhoneNumber parsedPhoneNumber = distPhoneNumberFacade.parsePhoneNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER,
                                                                                           SWITZERLAND);
        PhoneNumberUtil.PhoneNumberType phoneNumberType = distPhoneNumberFacade.getPhoneNumberType(parsedPhoneNumber);

        assertEquals(PhoneNumberUtil.PhoneNumberType.MOBILE, phoneNumberType);
    }

    @Test
    public void testFixedLinePhoneNumberType() {
        Phonenumber.PhoneNumber parsedPhoneNumber = distPhoneNumberFacade.parsePhoneNumber(EXAMPLE_SWITZERLAND_FIXED_PHONE_NUMBER, SWITZERLAND);

        PhoneNumberUtil.PhoneNumberType phoneNumberType = distPhoneNumberFacade.getPhoneNumberType(parsedPhoneNumber);

        assertEquals(PhoneNumberUtil.PhoneNumberType.FIXED_LINE, phoneNumberType);
    }

    @Test
    public void testFormatPhoneNumber() {
        Phonenumber.PhoneNumber parsedPhoneNumber = distPhoneNumberFacade.parsePhoneNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER, SWITZERLAND);

        String formattedPhoneNumber = distPhoneNumberFacade.formatPhoneNumber(parsedPhoneNumber);

        assertTrue(formattedPhoneNumber.contains(StringUtils.SPACE));
        assertTrue(formattedPhoneNumber.contains(SWITZERLAND_PHONE_NUMBER_PREFIX));
    }

    @Test
    public void testFormatPhoneNumberForCurrentCountry() {
        when(country.getIsocode()).thenReturn(SWITZERLAND);
        when(namicsCommonI18NService.getCurrentCountry()).thenReturn(country);

        String formattedPhoneNumber = distPhoneNumberFacade.formatPhoneNumberForCurrentCountry(EXAMPLE_SWITZERLAND_PHONE_NUMBER);

        assertTrue(formattedPhoneNumber.contains(StringUtils.SPACE));
        assertTrue(formattedPhoneNumber.contains(SWITZERLAND_PHONE_NUMBER_PREFIX));
    }

    @Test
    public void testFormatPhoneNumberForCurrentCountryWithoutPrefix() {
        when(country.getIsocode()).thenReturn(SWITZERLAND);
        when(namicsCommonI18NService.getCurrentCountry()).thenReturn(country);
        String number = removePrefixFromFullNumber(EXAMPLE_SWITZERLAND_PHONE_NUMBER, SWITZERLAND_PHONE_NUMBER_PREFIX);

        String formattedPhoneNumber = distPhoneNumberFacade.formatPhoneNumberForCurrentCountry(number);

        assertTrue(formattedPhoneNumber.contains(StringUtils.SPACE));
        assertTrue(formattedPhoneNumber.contains(SWITZERLAND_PHONE_NUMBER_PREFIX));
    }

    @Test
    public void testEmptyFormatPhoneNumberForCurrentCountry() {
        when(country.getIsocode()).thenReturn(SWITZERLAND);
        when(namicsCommonI18NService.getCurrentCountry()).thenReturn(country);

        String formattedPhoneNumber = distPhoneNumberFacade.formatPhoneNumberForCurrentCountry(StringUtils.EMPTY);

        assertEquals(formattedPhoneNumber, StringUtils.EMPTY);
    }

    private String removePrefixFromFullNumber(String phoneNumber, String prefix) {
        return phoneNumber.replace(prefix, StringUtils.EMPTY);
    }

}
