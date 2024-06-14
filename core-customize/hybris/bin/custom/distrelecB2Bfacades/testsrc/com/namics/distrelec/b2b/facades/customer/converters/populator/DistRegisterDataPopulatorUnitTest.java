package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistRegisterDataPopulatorUnitTest {

    private DistRegisterData target;

    @Mock
    private RegisterData source;

    @InjectMocks
    private DistRegisterDataPopulator distRegisterDataPopulator;

    @Before
    public void setUp() {
        target = new DistRegisterData();
    }

    @Test
    public void testPopulateMarketingConsentIsTrueNewsletterOptionSetToTrue() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistRegisterData target = new DistRegisterData();

        // when
        when(source.getMarketingConsent()).thenReturn(Boolean.TRUE);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isNewsletterOption(), is(true));
    }

    @Test
    public void testPopulateFirstName() {
        // given
        String firstName = "John";

        // when
        when(source.getFirstName()).thenReturn(firstName);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getFirstName(), equalTo(firstName));
    }

    @Test
    public void testPopulateLastName() {
        // given
        String lastName = "Paul";

        // when
        when(source.getLastName()).thenReturn(lastName);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getLastName(), equalTo(lastName));
    }

    @Test
    public void testPopulateLogin() {
        // given
        String login = "john.paul";

        // when
        when(source.getLogin()).thenReturn(login);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getLogin(), equalTo(login));
    }

    @Test
    public void testPopulateEmail() {
        // given
        String email = "distrelec.customer@distrelec.com";

        // when
        when(source.getLogin()).thenReturn(email);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getEmail(), equalTo(email));
    }

    @Test
    public void testPopulatePassword() {
        // given
        String password = "securePassword123";

        // when
        when(source.getPassword()).thenReturn(password);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getPassword(), equalTo(password));
    }

    @Test
    public void testPopulateTitleCode() {
        // given
        String titleCode = "Mr";

        // when
        when(source.getTitleCode()).thenReturn(titleCode);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getTitleCode(), equalTo(titleCode));
    }

    @Test
    public void testPopulateCustomerId() {
        // given
        String customerId = "12345";

        // when
        when(source.getCustomerId()).thenReturn(customerId);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getCustomerId(), equalTo(customerId));
    }

    @Test
    public void testPopulateAdditionalAddressCompany() {
        // given
        String additionalAddress = "Not main street";

        // when
        when(source.getAdditionalAddress()).thenReturn(additionalAddress);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getAdditionalAddressCompany(), equalTo(additionalAddress));
    }

    @Test
    public void testPopulateCountryCode() {
        // given
        String countryCode = "CH";

        // when
        when(source.getCountryCode()).thenReturn(countryCode);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getCountryCode(), equalTo(countryCode));
    }

    @Test
    public void testPopulateCurrencyCode() {
        // given
        String currencyCode = "CHF";

        // when
        when(source.getCurrencyCode()).thenReturn(currencyCode);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getCurrencyCode(), equalTo(currencyCode));
    }

    @Test
    public void testPopulatePhoneNumber() {
        // given
        String phoneNumber = "1234567890";

        // when
        when(source.getPhoneNumber()).thenReturn(phoneNumber);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getPhoneNumber(), equalTo(phoneNumber));
    }

    @Test
    public void testPopulateMobileNumber() {
        // given
        String mobileNumber = "0987654321";

        // when
        when(source.getMobileNumber()).thenReturn(mobileNumber);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getMobileNumber(), equalTo(mobileNumber));
    }

    @Test
    public void testPopulateFaxNumber() {
        // given
        String faxNumber = "1112223333";

        // when
        when(source.getFaxNumber()).thenReturn(faxNumber);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getFaxNumber(), equalTo(faxNumber));
    }

    @Test
    public void testPopulateStreetName() {
        // given
        String streetName = "Main Street 111";

        // when
        when(source.getStreetName()).thenReturn(streetName);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getStreetName(), equalTo(streetName));
    }

    @Test
    public void testPopulatePostalCode() {
        // given
        String postalCode = "12345";

        // when
        when(source.getPostalCode()).thenReturn(postalCode);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getPostalCode(), equalTo(postalCode));
    }

    @Test
    public void testPopulateTown() {
        // given
        String town = "Zurich";

        // when
        when(source.getTown()).thenReturn(town);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getTown(), equalTo(town));
    }

    @Test
    public void testPopulatePhoneMarketingOption() {
        // given
        boolean marketingConsent = true;

        // when
        when(source.getMarketingConsent()).thenReturn(marketingConsent);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isPhoneMarketingOption(), is(marketingConsent));
    }

    @Test
    public void testPopulateNpsConsent() {
        // given
        boolean marketingConsent = true;

        // when
        when(source.getMarketingConsent()).thenReturn(marketingConsent);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isNpsConsent(), is(marketingConsent));
    }

    @Test
    public void testPopulateInvoiceEmail() {
        // given
        String invoiceEmail = "invoice@distrelec.com";

        // when
        when(source.getInvoiceEmail()).thenReturn(invoiceEmail);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getInvoiceEmail(), equalTo(invoiceEmail));
    }

    @Test
    public void testPopulateVat4() {
        // given
        String vat4 = "VAT1234";

        // when
        when(source.getVat4()).thenReturn(vat4);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getVat4(), equalTo(vat4));
    }

    @Test
    public void testPopulateLegalEmail() {
        // given
        String legalEmail = "legal@distrelec.com";

        // when
        when(source.getLegalEmail()).thenReturn(legalEmail);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.getLegalEmail(), equalTo(legalEmail));
    }

    @Test
    public void testPopulatePhoneConsent() {
        // given
        boolean phoneConsent = true;

        // when
        when(source.getPhoneConsent()).thenReturn(phoneConsent);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isPhoneConsent(), is(phoneConsent));
    }

    @Test
    public void testPopulatePostConsent() {
        // given
        boolean postConsent = true;

        // when
        when(source.getPostConsent()).thenReturn(postConsent);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isPostConsent(), is(postConsent));
    }

    @Test
    public void testPopulateSmsConsent() {
        // given
        boolean smsConsent = true;

        // when
        when(source.getSmsConsent()).thenReturn(smsConsent);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isSmsConsent(), is(smsConsent));
    }

    @Test
    public void testPopulatePersonalisationConsent() {
        // given
        boolean personalisationConsent = true;

        // when
        when(source.getPersonalisationConsent()).thenReturn(personalisationConsent);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isPersonalisationConsent(), is(personalisationConsent));
    }

    @Test
    public void testPopulateProfilingConsent() {
        // given
        boolean profilingConsent = true;

        // when
        when(source.getProfilingConsent()).thenReturn(profilingConsent);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isProfilingConsent(), is(profilingConsent));
    }

    @Test
    public void testPopulatePersonalisedRecommendationConsent() {
        // given
        boolean personalisedRecommendationConsent = true;

        // when
        when(source.getPersonalisedRecommendationConsent()).thenReturn(personalisedRecommendationConsent);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isPersonalisedRecommendationConsent(), is(personalisedRecommendationConsent));
    }

    @Test
    public void testPopulateCustomerSurveysConsent() {
        // given
        boolean customerSurveysConsent = true;

        // when
        when(source.getCustomerSurveysConsent()).thenReturn(customerSurveysConsent);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isCustomerSurveysConsent(), is(customerSurveysConsent));
    }

    @Test
    public void testPopulateMarketingCookieEnabled() {
        // given
        boolean isMarketingCookieEnabled = true;

        // when
        when(source.getIsMarketingCookieEnabled()).thenReturn(isMarketingCookieEnabled);

        distRegisterDataPopulator.populate(source, target);

        // then
        assertThat(target.isMarketingCookieEnabled(), is(isMarketingCookieEnabled));
    }
}
