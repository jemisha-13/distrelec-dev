package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistRegisterDataForExistingCustomerPopulatorUnitTest {

    private DistExistingCustomerRegisterData target;

    @Mock
    private RegisterData source;

    @InjectMocks
    private DistRegisterDataForExistingCustomerPopulator populator;

    @Before
    public void setUp() {
        target = new DistExistingCustomerRegisterData();
    }

    @Test
    public void testPopulateEmail() {
        // given
        String expectedEmail = "distrelec.customer@distrelec.com";

        // when
        when(source.getLogin()).thenReturn(expectedEmail);

        populator.populate(source, target);

        // then
        assertThat(target.getEmail(), equalTo(expectedEmail));
    }

    @Test
    public void testPopulateFirstName() {
        // given
        String expectedFirstName = "John";

        // when
        when(source.getFirstName()).thenReturn(expectedFirstName);

        populator.populate(source, target);

        // then
        assertThat(target.getFirstName(), equalTo(expectedFirstName));
    }

    @Test
    public void testPopulateLastName() {
        // given
        String expectedLastName = "Paul";

        // when
        when(source.getLastName()).thenReturn(expectedLastName);

        populator.populate(source, target);

        // then
        assertThat(target.getLastName(), equalTo(expectedLastName));
    }

    @Test
    public void testPopulateLogin() {
        // given
        String expectedLogin = "john.paul";

        // when
        when(source.getLogin()).thenReturn(expectedLogin);

        populator.populate(source, target);

        // then
        assertThat(target.getLogin(), equalTo(expectedLogin));
    }

    @Test
    public void testPopulatePassword() {
        // given
        String expectedPassword = "qwertz";

        // when
        when(source.getPassword()).thenReturn(expectedPassword);

        populator.populate(source, target);

        // then
        assertThat(target.getPassword(), equalTo(expectedPassword));
    }

    @Test
    public void testPopulateTitleCode() {
        // given
        String expectedTitleCode = "Mr";

        // when
        when(source.getTitleCode()).thenReturn(expectedTitleCode);

        populator.populate(source, target);

        // then
        assertThat(target.getTitleCode(), equalTo(expectedTitleCode));
    }

    @Test
    public void testPopulateCustomerId() {
        // given
        String expectedCustomerId = "12345";

        // when
        when(source.getCustomerId()).thenReturn(expectedCustomerId);

        populator.populate(source, target);

        // then
        assertThat(target.getCustomerId(), equalTo(expectedCustomerId));
    }

    @Test
    public void testPopulatePhoneNumber() {
        // given
        String expectedPhoneNumber = "0123456789";

        // when
        when(source.getPhoneNumber()).thenReturn(expectedPhoneNumber);

        populator.populate(source, target);

        // then
        assertThat(target.getPhoneNumber(), equalTo(expectedPhoneNumber));
    }

    @Test
    public void testPopulateMobileNumber() {
        // given
        String expectedMobileNumber = "0987654321";

        // when
        when(source.getMobileNumber()).thenReturn(expectedMobileNumber);

        populator.populate(source, target);

        // then
        assertThat(target.getMobileNumber(), equalTo(expectedMobileNumber));
    }

    @Test
    public void testPopulateFaxNumber() {
        // given
        String expectedFaxNumber = "1234567890";

        // when
        when(source.getFaxNumber()).thenReturn(expectedFaxNumber);

        populator.populate(source, target);

        // then
        assertThat(target.getFaxNumber(), equalTo(expectedFaxNumber));
    }

    @Test
    public void testPopulateNewsletterOption() {
        // given
        boolean expectedNewsletterOption = true;

        // when
        when(source.getMarketingConsent()).thenReturn(expectedNewsletterOption);

        populator.populate(source, target);

        // then
        assertThat(target.isNewsletterOption(), is(expectedNewsletterOption));
    }

    @Test
    public void testPopulateNpsConsent() {
        // given
        boolean expectedNpsConsent = true;

        // when
        when(source.getMarketingConsent()).thenReturn(expectedNpsConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isNpsConsent(), is(expectedNpsConsent));
    }

    @Test
    public void testPopulatePhoneMarketingOption() {
        // given
        boolean expectedPhoneMarketingOption = true;

        // when
        when(source.getMarketingConsent()).thenReturn(expectedPhoneMarketingOption);

        populator.populate(source, target);

        // then
        assertThat(target.isPhoneMarketingOption(), is(expectedPhoneMarketingOption));
    }

    @Test
    public void testPopulateMarketingCookieEnabled() {
        // given
        boolean expectedMarketingCookieEnabled = true;

        // when
        when(source.getIsMarketingCookieEnabled()).thenReturn(expectedMarketingCookieEnabled);

        populator.populate(source, target);

        // then
        assertThat(target.isMarketingCookieEnabled(), is(expectedMarketingCookieEnabled));
    }

    @Test
    public void testPopulatePhoneConsent() {
        // given
        boolean expectedPhoneConsent = true;

        // when
        when(source.getPhoneConsent()).thenReturn(expectedPhoneConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isPhoneConsent(), is(expectedPhoneConsent));
    }

    @Test
    public void testPopulateSmsConsent() {
        // given
        boolean expectedSmsConsent = true;

        // when
        when(source.getSmsConsent()).thenReturn(expectedSmsConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isSmsConsent(), is(expectedSmsConsent));
    }

    @Test
    public void testPopulatePostConsent() {
        // given
        boolean expectedPostConsent = true;

        // when
        when(source.getPostConsent()).thenReturn(expectedPostConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isPostConsent(), is(expectedPostConsent));
    }

    @Test
    public void testPopulatePersonalisationConsent() {
        // given
        boolean expectedPersonalisationConsent = true;

        // when
        when(source.getPersonalisationConsent()).thenReturn(expectedPersonalisationConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isPersonalisationConsent(), is(expectedPersonalisationConsent));
    }

    @Test
    public void testPopulateProfilingConsent() {
        // given
        boolean expectedProfilingConsent = true;

        // when
        when(source.getProfilingConsent()).thenReturn(expectedProfilingConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isProfilingConsent(), is(expectedProfilingConsent));
    }

    @Test
    public void testPopulateCountryCode() {
        // given
        String expectedCountryCode = "CH";

        // when
        when(source.getCountryCode()).thenReturn(expectedCountryCode);

        populator.populate(source, target);

        // then
        assertThat(target.getCountryCode(), equalTo(expectedCountryCode));
    }
}
