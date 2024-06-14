package com.namics.distrelec.b2b.facades.vat.eu.impl;

import com.namics.distrelec.b2b.core.service.vatEU.DistEUVatService;
import com.namics.distrelec.b2b.core.service.vatEU.impl.VatEUServiceException;
import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.xml.ws.WebServiceException;
import java.net.SocketTimeoutException;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistVatEUFacadeUnitTest {

    @Mock
    private DistEUVatService distEUVatService;

    @InjectMocks
    private DefaultDistVatEUFacade facade;

    @Test
    public void testValidateVatNumberValidVat() throws SocketTimeoutException, VatEUServiceException {
        // when
        when(distEUVatService.validateVat("DE217410393", "DE")).thenReturn(true);

        boolean result = facade.validateVatNumber("DE217410393", "DE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testValidateVatNumberNullVat() {
        // when
        boolean result = facade.validateVatNumber(null, "DE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testValidateVatNumberVatEUServiceException() throws SocketTimeoutException, VatEUServiceException {
        // when
        when(distEUVatService.validateVat("invalidVat", "DE")).thenThrow(VatEUServiceException.class);

        boolean result = facade.validateVatNumber("invalidVat", "DE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testValidateVatNumberSocketTimeoutException() throws SocketTimeoutException, VatEUServiceException {
        // when
        when(distEUVatService.validateVat("timeoutVat", "DE")).thenThrow(SocketTimeoutException.class);

        boolean result = facade.validateVatNumber("timeoutVat", "DE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testValidateVatNumberWebServiceException() throws SocketTimeoutException, VatEUServiceException {
        // when
        when(distEUVatService.validateVat("webServiceErrorVat", "DE")).thenThrow(WebServiceException.class);

        boolean result = facade.validateVatNumber("webServiceErrorVat", "DE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testValidateVatNumberException() throws SocketTimeoutException, VatEUServiceException {
        // when
        when(distEUVatService.validateVat("otherErrorVat", "DE")).thenThrow(RuntimeException.class);

        boolean result = facade.validateVatNumber("otherErrorVat", "DE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyBE() {
        // when
        boolean result = facade.isVatValidatedExternally("BE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyCZ() {
        // when
        boolean result = facade.isVatValidatedExternally("CZ");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyDK() {
        // when
        boolean result = facade.isVatValidatedExternally("DK");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyEE() {
        // when
        boolean result = facade.isVatValidatedExternally("EE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyFI() {
        // when
        boolean result = facade.isVatValidatedExternally("FI");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyFR() {
        // when
        boolean result = facade.isVatValidatedExternally("FR");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyDE() {
        // when
        boolean result = facade.isVatValidatedExternally("DE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyXI() {
        // when
        boolean result = facade.isVatValidatedExternally("XI");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyHU() {
        // when
        boolean result = facade.isVatValidatedExternally("HU");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyIT() {
        // when
        boolean result = facade.isVatValidatedExternally("IT");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyLV() {
        // when
        boolean result = facade.isVatValidatedExternally("LV");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyLT() {
        // when
        boolean result = facade.isVatValidatedExternally("LT");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyNL() {
        // when
        boolean result = facade.isVatValidatedExternally("NL");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyPL() {
        // when
        boolean result = facade.isVatValidatedExternally("PL");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyRO() {
        // when
        boolean result = facade.isVatValidatedExternally("RO");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallySK() {
        // when
        boolean result = facade.isVatValidatedExternally("SK");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyBG() {
        // when
        boolean result = facade.isVatValidatedExternally("BG");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyHR() {
        // when
        boolean result = facade.isVatValidatedExternally("HR");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyCY() {
        // when
        boolean result = facade.isVatValidatedExternally("CY");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyGR() {
        // when
        boolean result = facade.isVatValidatedExternally("GR");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyIE() {
        // when
        boolean result = facade.isVatValidatedExternally("IE");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyLU() {
        // when
        boolean result = facade.isVatValidatedExternally("LU");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyMT() {
        // when
        boolean result = facade.isVatValidatedExternally("MT");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyPT() {
        // when
        boolean result = facade.isVatValidatedExternally("PT");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallySI() {
        // when
        boolean result = facade.isVatValidatedExternally("SI");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyES() {
        // when
        boolean result = facade.isVatValidatedExternally("ES");

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsVatValidatedExternallyInvalidCountryCode() {
        // given
        String invalidCountryCode = "XX";

        // when
        boolean result = facade.isVatValidatedExternally(invalidCountryCode);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testGetVatPrefixForCountryGreece() {
        // given
        String countryCode = "GR";
        String expectedPrefix = "EL";

        // when
        String result = facade.getVatPrefixForCountry(countryCode);

        // then
        assertThat(result, equalTo(expectedPrefix));
    }

    @Test
    public void testGetVatPrefixForCountryOtherCountry() {
        // given
        String countryCode = "DE";
        String expectedPrefix = "DE";

        // when
        String result = facade.getVatPrefixForCountry(countryCode);

        // then
        assertThat(result, equalTo(expectedPrefix));
    }

    @Test
    public void testGetVatWithoutPrefixWithPrefix() {
        // given
        String vat = "DE123456789";
        String country = "DE";
        String expectedVat = "123456789";

        // when
        String result = facade.getVatWithoutPrefix(vat, country);

        // then
        assertThat(result, equalTo(expectedVat));
    }

    @Test
    public void testGetVatWithoutPrefixWithGreekPrefix() {
        // given
        String vat = "EL123456789";
        String country = "GR";
        String expectedVat = "123456789";

        // when
        String result = facade.getVatWithoutPrefix(vat, country);

        // then
        assertThat(result, equalTo(expectedVat));
    }

    @Test
    public void testGetVatWithoutPrefixWithoutPrefix() {
        // given
        String vat = "123456789";
        String country = "DE";
        String expectedVat = "123456789";

        // when
        String result = facade.getVatWithoutPrefix(vat, country);

        // then
        assertThat(result, equalTo(expectedVat));
    }

    @Test
    public void testGetVatWithoutPrefixEmptyVat() {
        // given
        String vat = "";
        String country = "DE";
        String expectedVat = "";

        // when
        String result = facade.getVatWithoutPrefix(vat, country);

        // then
        assertThat(result, equalTo(expectedVat));
    }

    @Test
    public void testGetVatWithoutPrefixNullVat() {
        // given
        String country = "DE";

        // when
        String result = facade.getVatWithoutPrefix(null, country);

        // then
        assertThat(result, is(nullValue()));
    }
}
