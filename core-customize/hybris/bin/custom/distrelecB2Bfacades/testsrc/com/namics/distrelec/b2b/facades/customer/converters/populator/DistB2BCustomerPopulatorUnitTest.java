package com.namics.distrelec.b2b.facades.customer.converters.populator;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.core.model.ObsolescenceCategoryModel;
import com.namics.distrelec.b2b.core.security.DistCryptographyService;
import com.namics.distrelec.b2b.core.service.customer.b2b.budget.DistB2BCommerceBudgetService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import com.namics.distrelec.b2b.facades.user.data.ObsolescenceCategoryData;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BPermissionModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bapprovalprocessfacades.company.data.B2BPermissionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.hmc.model.UserProfileModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DistB2BCustomerPopulatorUnitTest {

    @Mock
    private Populator<CustomerModel, CustomerData> customerPopulator;

    @Mock
    private DistUserService userService;

    @Mock
    private Converter<LanguageModel, LanguageData> languageConverter;

    @Mock
    private CMSSiteService cmsSiteService;

    @Mock
    private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;

    @Mock
    private Converter<AddressModel, AddressData> addressConverter;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private Converter<CurrencyModel, CurrencyData> currencyConverter;

    @Mock
    private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

    @Mock
    private DistB2BCommerceBudgetService distB2BCommerceBudgetService;

    @Mock
    private DistWebtrekkFacade distWebtrekkFacade;

    @Mock
    private Converter<CountryModel, CountryData> countryConverter;

    @Mock
    private DistCryptographyService distCryptographyService;

    @Mock
    private Converter<ObsolescenceCategoryModel, ObsolescenceCategoryData> obsolescenceCategoryConverter;

    @Mock
    private Converter<B2BPermissionModel, B2BPermissionData> b2bPermissionConverter;

    @Mock
    private Converter<DistB2BBudgetModel, B2BBudgetData> b2bBudgetConverter;

    @InjectMocks
    private DistB2BCustomerPopulator populator;

    @Test
    public void testPopulateIsNotB2BCustomer() {
        // given
        CustomerModel source = mock(CustomerModel.class);
        CustomerData target = new CustomerData();

        // when
        populator.populate(source, target);

        // then
        verify(customerPopulator).populate(source, target);
    }

    @Test
    public void populateSetsCustomerType() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(userService.getCustomerType(source)).thenReturn(CustomerType.B2B);

        populator.populate(source, target);

        // then
        assertThat(target.getCustomerType(), equalTo(CustomerType.B2B));
    }

    @Test
    public void populateSetsContactId() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getErpContactID()).thenReturn("12345");

        populator.populate(source, target);

        // then
        assertThat(target.getContactId(), equalTo("12345"));
    }

    @Test
    public void populateSetsEmail() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getEmail()).thenReturn("distrelec@distrelec.com");

        populator.populate(source, target);

        // then
        assertThat(target.getEmail(), equalTo("distrelec@distrelec.com"));
    }

    @Test
    public void populateSetsNewsletter() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.isNewsletter()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.isNewsletter(), is(true));
    }

    @Test
    public void populateSetsSubscribePhoneMarketing() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.isPhoneMarketingConsent()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.isSubscribePhoneMarketing(), is(true));
    }

    @Test
    public void populateSetsRegisteredAsGuest() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.isRegisteredAsGuest()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.getRegisteredAsGuest(), equalTo(true));
    }

    @Test
    public void populateSetsRsCustomer() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.isRsCustomer()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.getRsCustomer(), equalTo(true));
    }

    @Test
    public void populateSetsErpSelectedCustomer() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.isErpSelectedCustomer()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.getErpSelectedCustomer(), equalTo(true));
    }

    @Test
    public void populateSetsAllowedToPlaceOpenOrders() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel defaultB2BUnit = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(defaultB2BUnit);
        when(defaultB2BUnit.isAllowedToPlaceOpenOrder()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.isAllowedToPlaceOpenOrders(), equalTo(true));
    }

    @Test
    public void populateSetsLanguage() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        LanguageModel sessionLanguage = mock(LanguageModel.class);
        LanguageData languageData = mock(LanguageData.class);
        B2BUnitModel defaultB2BUnit = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(defaultB2BUnit);
        when(userService.isAnonymousUser(source)).thenReturn(false);
        when(source.getSessionLanguage()).thenReturn(sessionLanguage);
        when(languageConverter.convert(sessionLanguage)).thenReturn(languageData);

        populator.populate(source, target);

        // then
        assertThat(target.getLanguage(), equalTo(languageData));
    }

    @Test
    public void populateSetsDoubleOptinActivated() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel defaultB2BUnit = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(defaultB2BUnit);
        when(source.isDoubleOptInActivated()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.isDoubleOptinActivated(), is(true));
    }

    @Test
    public void populateSetsLoginDisabled() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel defaultB2BUnit = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(defaultB2BUnit);
        when(source.getActive()).thenReturn(false);

        populator.populate(source, target);

        // then
        assertThat(target.isLoginDisabled(), equalTo(true));
    }

    @Test
    public void populateSetsAdminUser() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel defaultB2BUnit = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(defaultB2BUnit);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(mock(UserGroupModel.class));
        when(userService.isMemberOfGroup(any(B2BCustomerModel.class), any(UserGroupModel.class))).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.isAdminUser(), is(true));
    }

    @Test
    public void populateSetsCreatedDate() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        Date creationTime = new Date();
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getCreationtime()).thenReturn(creationTime);

        populator.populate(source, target);

        // then
        assertThat(target.getCreatedDate(), equalTo(creationTime));
    }

    @Test
    public void populateSetsConsentConditionRequired() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getConsentConditionRequired()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.getConsentConditionRequired(), equalTo(true));
    }

    @Test
    public void populateSetsOptedForObsolescence() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getOptedForObsolescence()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.isOptedForObsolescence(), is(true));
    }

    @Test
    public void populateSetsAllObsolCatSelected() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getAllObsolCatSelected()).thenReturn(true);

        populator.populate(source, target);

        // then
        assertThat(target.isAllObsolCatSelected(), is(true));
    }

    @Test
    public void populateSetsCategories() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        List<ObsolescenceCategoryModel> obsolescenceCategories = List.of(mock(ObsolescenceCategoryModel.class));
        List<ObsolescenceCategoryData> convertedCategories = List.of(mock(ObsolescenceCategoryData.class));
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getObsolescenceCategories()).thenReturn(obsolescenceCategories);
        when(obsolescenceCategoryConverter.convertAll(obsolescenceCategories)).thenReturn(convertedCategories);

        populator.populate(source, target);

        // then
        assertThat(target.getCategories(), equalTo(convertedCategories));
    }

    @Test
    public void populateSetsVat4() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getVat4()).thenReturn("123VAT4");

        populator.populate(source, target);

        // then
        assertThat(target.getVat4(), equalTo("123VAT4"));
    }

    @Test
    public void populateSetsLegalEmail() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getLegalEmail()).thenReturn("legal.email@distrelec.com");
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);

        populator.populate(source, target);

        // then
        assertThat(target.getLegalEmail(), equalTo("legal.email@distrelec.com"));
    }

    @Test
    public void populateSetsChannelTypeWhenCurrentSiteAndChannelAreNotNull() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CMSSiteModel currentSite = mock(CMSSiteModel.class);
        SiteChannel siteChannel = mock(SiteChannel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(cmsSiteService.getCurrentSite()).thenReturn(currentSite);
        when(currentSite.getChannel()).thenReturn(siteChannel);
        when(siteChannel.getCode()).thenReturn("channelCode");

        populator.populate(source, target);

        // then
        assertThat(target.getChannelType(), equalTo("channelCode"));
    }

    @Test
    public void populateSetsCurrencyWhenSessionCurrencyIsNotNull() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CurrencyModel sessionCurrency = mock(CurrencyModel.class);
        CurrencyData currencyData = mock(CurrencyData.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getSessionCurrency()).thenReturn(sessionCurrency);
        when(currencyConverter.convert(sessionCurrency)).thenReturn(currencyData);

        populator.populate(source, target);

        // then
        assertThat(target.getCurrency(), equalTo(currencyData));
    }

    @Test
    public void populateSetsContactAddressAndNamesWhenContactAddressIsNotNull() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        AddressModel contactAddress = mock(AddressModel.class);
        AddressData convertedAddress = mock(AddressData.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getContactAddress()).thenReturn(contactAddress);
        when(addressConverter.convert(contactAddress)).thenReturn(convertedAddress);
        when(contactAddress.getFirstname()).thenReturn("John");
        when(contactAddress.getLastname()).thenReturn("Paul");

        populator.populate(source, target);

        // then
        assertThat(target.getContactAddress(), equalTo(convertedAddress));
        assertThat(target.getFirstName(), equalTo("John"));
        assertThat(target.getLastName(), equalTo("Paul"));
    }

    @Test
    public void populateSetsDefaultPaymentAddressAndNames() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        AddressModel defaultPaymentAddress = mock(AddressModel.class);
        AddressData convertedAddress = mock(AddressData.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getDefaultPaymentAddress()).thenReturn(defaultPaymentAddress);
        when(addressConverter.convert(defaultPaymentAddress)).thenReturn(convertedAddress);
        when(defaultPaymentAddress.getFirstname()).thenReturn("John");
        when(defaultPaymentAddress.getLastname()).thenReturn("Paul");

        populator.populate(source, target);

        // then
        assertThat(target.getBillingAddress(), equalTo(convertedAddress));
        assertThat(target.getDefaultBillingAddress(), equalTo(convertedAddress));
        assertThat(target.getFirstName(), equalTo("John"));
        assertThat(target.getLastName(), equalTo("Paul"));
    }

    @Test
    public void populateSetsPermissions() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        B2BPermissionModel permissionModel = mock(B2BPermissionModel.class);
        B2BPermissionData permissionData = mock(B2BPermissionData.class);
        Set<B2BPermissionModel> permissions = Set.of(permissionModel);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getPermissions()).thenReturn(permissions);
        when(b2bPermissionConverter.convert(permissionModel)).thenReturn(permissionData);

        populator.populate(source, target);

        // then
        assertThat(target.getPermissions().get(0), equalTo(permissionData));
    }

    @Test
    public void populateSetsUserProfileData() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        UserProfileModel userProfile = mock(UserProfileModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getUserprofile()).thenReturn(userProfile);
        when(userProfile.getUserProfileName()).thenReturn("john.paul");

        populator.populate(source, target);

        // then
        assertThat(target.getUserProfileData().getUserProfileName(), equalTo("john.paul"));
    }

    @Test
    public void populateSetsFunctionCodeAndName() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        DistFunctionModel distFunction = mock(DistFunctionModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getDistFunction()).thenReturn(distFunction);
        when(distFunction.getCode()).thenReturn("manager");
        when(distFunction.getName()).thenReturn("Manager");
        when(distFunction.getName(Locale.ENGLISH)).thenReturn("MANAGER");

        populator.populate(source, target);

        // then
        assertThat(target.getFunctionCode(), equalTo("manager"));
        assertThat(target.getFunctionName(), equalTo("Manager"));
        assertThat(target.getFunctionNameEN(), equalTo("MANAGER"));
    }

    @Test
    public void populateSetsCcPaymentInfosAndDefaultPaymentInfo() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CreditCardPaymentInfoModel ccPaymentInfoModel = mock(CreditCardPaymentInfoModel.class);
        List<PaymentInfoModel> paymentInfos = List.of(ccPaymentInfoModel);
        CCPaymentInfoData ccPaymentInfoData = mock(CCPaymentInfoData.class);
        PK pk = PK.fromLong(123L);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(ccPaymentInfoData.getId()).thenReturn("123");
        when(source.getPaymentInfos()).thenReturn(paymentInfos);
        when(creditCardPaymentInfoConverter.convert(ccPaymentInfoModel)).thenReturn(ccPaymentInfoData);
        when(source.getDefaultPaymentInfo()).thenReturn(ccPaymentInfoModel);
        when(ccPaymentInfoModel.getPk()).thenReturn(pk);

        populator.populate(source, target);

        // then
        verify(ccPaymentInfoData).setDefaultPaymentInfo(true);
        assertThat(target.getCcPaymentInfos(), equalTo(List.of(ccPaymentInfoData)));
    }

    @Test
    public void populateSetsDefaultDeliveryMethod() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(source.getDefaultDeliveryMethod()).thenReturn(DistConstants.Shipping.METHOD_STANDARD);

        populator.populate(source, target);

        // then
        assertThat(target.getDefaultDeliveryMode(), equalTo(DistConstants.Shipping.METHOD_STANDARD));
    }

    @Test
    public void populateSetsBudget() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        DistB2BBudgetModel budgetModel = mock(DistB2BBudgetModel.class);
        B2BBudgetData budgetData = mock(B2BBudgetData.class);
        B2BUnitModel b2BUnitModel = mock(B2BUnitModel.class);
        CustomerData target = new CustomerData();

        // when
        when(distB2BCommerceBudgetService.getActiveBudget(source)).thenReturn(budgetModel);
        when(b2bBudgetConverter.convert(budgetModel)).thenReturn(budgetData);
        when(source.getDefaultB2BUnit()).thenReturn(b2BUnitModel);

        populator.populate(source, target);

        // then
        assertThat(target.getBudget(), equalTo(budgetData));
    }

    @Test
    public void populateSetsCompanyName() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        AddressModel billingAddress = mock(AddressModel.class);
        CustomerData target = new CustomerData();
        B2BUnitModel defaultB2BUnit = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(defaultB2BUnit);
        when(defaultB2BUnit.getBillingAddress()).thenReturn(billingAddress);
        when(billingAddress.getCompany()).thenReturn("Company Name");

        populator.populate(source, target);

        // then
        assertThat(target.getCompanyName(), equalTo("Company Name"));
    }

    @Test
    public void populateSetsEncryptedUserID() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        String customerID = "customer123";
        String encryptedString = "encryptedString";
        String encodedString = "encodedString";
        B2BUnitModel defaultB2BUnit = mock(B2BUnitModel.class);

        // when
        when(source.getDefaultB2BUnit()).thenReturn(defaultB2BUnit);
        when(source.getCustomerID()).thenReturn(customerID);
        when(distCryptographyService.encryptString(customerID, DistCryptography.WEBTREKK_KEY)).thenReturn(encryptedString);
        when(distWebtrekkFacade.encodeToUTF8(encryptedString)).thenReturn(encodedString);

        populator.populate(source, target);

        // then
        assertThat(target.getEncryptedUserID(), equalTo(encodedString));
    }


    @Test
    public void populateContactAddressWhenAddressNotNull() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        AddressModel sourceAddress = mock(AddressModel.class);
        AddressData convertedAddress = new AddressData();
        CustomerData target = new CustomerData();

        // when
        when(source.getContactAddress()).thenReturn(sourceAddress);
        when(addressConverter.convert(sourceAddress)).thenReturn(convertedAddress);

        populator.populateContactAddress(source, target);

        // then
        assertThat(target.getContactAddress(), equalTo(convertedAddress));
    }

    @Test
    public void populateContactAddressWhenAddressIsNull() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();

        // when
        when(source.getContactAddress()).thenReturn(null);

        populator.populateContactAddress(source, target);

        // then
        assertThat(target.getContactAddress(), is(nullValue()));
    }

    @Test
    public void testPopulateUnit() {
        // given
        B2BCustomerModel source = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        CountryModel countryModel = mock(CountryModel.class);
        CountryData countryData = new CountryData();
        B2BUnitModel parentUnit = mock(B2BUnitModel.class);

        // when
        when(b2bUnitService.getParent(source)).thenReturn(parentUnit);
        when(parentUnit.getName()).thenReturn("unitName");
        when(parentUnit.getVatID()).thenReturn("1111VatId");
        when(parentUnit.getErpCustomerID()).thenReturn("300123209");
        when(parentUnit.getCountry()).thenReturn(countryModel);
        when(countryConverter.convert(countryModel)).thenReturn(countryData);

        populator.populateUnit(source, target);

        // then
        assertThat(target.getUnit().getName(), is("unitName"));
        assertThat(target.getUnit().getVatID(), is("1111VatId"));
        assertThat(target.getUnit().getErpCustomerId(), is("300123209"));
        assertThat(target.getUnit().getCountry(), is(countryData));
    }

    @Test
    public void testPopulateApprover() {
        // given
        B2BCustomerModel approver = mock(B2BCustomerModel.class);
        when(approver.getName()).thenReturn("Approver");
        when(approver.getEmail()).thenReturn("approver@distrelec.com");
        when(approver.getUid()).thenReturn("approver");

        // when
        CustomerData result = populator.populateApprover(approver, new CustomerData());

        // then
        assertThat(result.getName(), is("Approver"));
        assertThat(result.getEmail(), is("approver@distrelec.com"));
        assertThat(result.getUid(), is("approver"));
    }

    @Test
    public void testPopulateApprovers() {
        // given
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        CustomerData target = new CustomerData();
        B2BCustomerModel approver1 = mock(B2BCustomerModel.class);
        B2BCustomerModel approver2 = mock(B2BCustomerModel.class);

        // when
        when(approver1.getName()).thenReturn("Approver One");
        when(approver1.getEmail()).thenReturn("approver.one@distrelec.com");
        when(approver1.getUid()).thenReturn("approver1");
        when(approver2.getName()).thenReturn("Approver Two");
        when(approver2.getEmail()).thenReturn("approver.two@distrelec.com");
        when(approver2.getUid()).thenReturn("approver2");
        when(customer.getApprovers()).thenReturn(Set.of(approver1, approver2));

        populator.populateApprovers(customer, target);

        // then
        List<CustomerData> approversData = target.getApprovers();
        assertThat(approversData, containsInAnyOrder(
                allOf(
                        hasProperty("name", is("Approver One")),
                        hasProperty("email", is("approver.one@distrelec.com")),
                        hasProperty("uid", is("approver1"))
                ),
                allOf(
                        hasProperty("name", is("Approver Two")),
                        hasProperty("email", is("approver.two@distrelec.com")),
                        hasProperty("uid", is("approver2"))
                )
        ));
    }
}
