package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistB2BRegisterDataForExistingCustomerPopulatorUnitTest {

    private DistExistingCustomerRegisterData target;

    @Mock
    private RegisterData source;

    @Mock
    private DistSalesOrgModel distSalesOrgModel;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @InjectMocks
    private DistB2BRegisterDataForExistingCustomerPopulator populator;

    @Before
    public void setUp() {
        target = new DistExistingCustomerRegisterData();
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7371");
    }
    @Test
    public void testPopulateCompanyName() {
        // given
        String company = "Test Company";

        // when
        when(source.getCompany()).thenReturn(company);

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName(), equalTo(company));
    }

    @Test
    public void testPopulateFunctionCode() {
        // given
        String functionCode = "functionCode";

        // when
        when(source.getFunctionCode()).thenReturn(functionCode);

        populator.populate(source, target);

        // then
        assertThat(target.getFunctionCode(), equalTo(functionCode));
    }

    @Test
    public void testPopulateVatId() {
        // given
        String vatId = "1122VatId";

        // when
        when(source.getVatId()).thenReturn(vatId);

        populator.populate(source, target);

        // then
        assertThat(target.getVatId(), equalTo(vatId));
    }

    @Test
    public void testPopulateInvoiceEmail() {
        // given
        String invoiceEmail = "distrelec.customer@distrelec.com";

        // when
        when(source.getInvoiceEmail()).thenReturn(invoiceEmail);

        populator.populate(source, target);

        // then
        assertThat(target.getInvoiceEmail(), equalTo(invoiceEmail));
    }

    @Test
    public void testPopulateCountryCode() {
        // given
        String countryCode = "CH";

        // when
        when(source.getCountryCode()).thenReturn(countryCode);

        populator.populate(source, target);

        // then
        assertThat(target.getCountryCode(), equalTo(countryCode));
    }

    @Test
    public void testPopulatePhoneNumber() {
        // given
        String phoneNumber = "0123456789";

        // when
        when(source.getPhoneNumber()).thenReturn(phoneNumber);

        populator.populate(source, target);

        // then
        assertThat(target.getPhoneNumber(), equalTo(phoneNumber));
    }

    @Test
    public void testPopulatePhoneConsent() {
        // given
        boolean phoneConsent = true;

        // when
        when(source.getPhoneConsent()).thenReturn(phoneConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isPhoneConsent(), is(phoneConsent));
    }

    @Test
    public void testPopulatePostConsent() {
        // given
        boolean postConsent = true;

        // when
        when(source.getPostConsent()).thenReturn(postConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isPostConsent(), is(postConsent));
    }

    @Test
    public void testPopulateSmsConsent() {
        // given
        boolean smsConsent = true;

        // when
        when(source.getSmsConsent()).thenReturn(smsConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isSmsConsent(), is(smsConsent));
    }

    @Test
    public void testPopulatePersonalisationConsent() {
        // given
        boolean personalisationConsent = true;

        // when
        when(source.getPersonalisationConsent()).thenReturn(personalisationConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isPersonalisationConsent(), is(personalisationConsent));
    }

    @Test
    public void testPopulateProfilingConsent() {
        // given
        boolean profilingConsent = true;

        // when
        when(source.getProfilingConsent()).thenReturn(profilingConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isProfilingConsent(), is(profilingConsent));
    }

    @Test
    public void testPopulatePersonalisedRecommendationConsent() {
        // given
        boolean personalisedRecommendationConsent = true;

        // when
        when(source.getPersonalisedRecommendationConsent()).thenReturn(personalisedRecommendationConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isPersonalisedRecommendationConsent(), is(personalisedRecommendationConsent));
    }

    @Test
    public void testPopulateCustomerSurveysConsent() {
        // given
        boolean customerSurveysConsent = true;

        // when
        when(source.getCustomerSurveysConsent()).thenReturn(customerSurveysConsent);

        populator.populate(source, target);

        // then
        assertThat(target.isCustomerSurveysConsent(), is(customerSurveysConsent));
    }

    @Test
    public void testPopulateMarketingCookieEnabled() {
        // given
        boolean marketingCookieEnabled = true;

        // when
        when(source.getIsMarketingCookieEnabled()).thenReturn(marketingCookieEnabled);

        populator.populate(source, target);

        // then
        assertThat(target.isMarketingCookieEnabled(), is(marketingCookieEnabled));
    }

    @Test
    public void testPopulateDuns() {
        // given
        String duns = "123456789";

        // when
        when(source.getDuns()).thenReturn(duns);

        populator.populate(source, target);

        // then
        assertThat(target.getDuns(), equalTo(duns));
    }

    @Test
    public void testPopulateRemoveWhitespaceFromOrgNumber() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(source.getLogin()).thenReturn("distrelec.customer@distrelec.com");
        when(source.getPhoneNumber()).thenReturn("1234567890");
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn(DistConstants.SalesOrg.SALES_ORG_7650);
        when(source.getOrganizationalNumber()).thenReturn("123 456 789");

        populator.populate(source, target);

        // then
        assertThat(target.getOrganizationalNumber(), equalTo("123456789"));
    }

    @Test
    public void testPopulateWhenSalesOrgIsNot7650() {
        // given
        RegisterData source = mock(RegisterData.class);
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn("7350");
        when(source.getOrganizationalNumber()).thenReturn("123 456 789");

        populator.populate(source, target);

        // then
        assertThat(target.getOrganizationalNumber(), equalTo("123 456 789"));
    }
}
