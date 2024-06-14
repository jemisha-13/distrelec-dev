package com.namics.distrelec.b2b.facades.customer.impl;

import com.distrelec.webservice.sap.v1.AddressResponse;
import com.distrelec.webservice.sap.v1.ReadCustomerResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.namics.distrelec.b2b.core.bloomreach.exception.DistBloomreachBatchException;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.RegistrationType;
import com.namics.distrelec.b2b.core.inout.erp.CustomerService;
import com.namics.distrelec.b2b.core.inout.erp.data.FindContactRequestData;
import com.namics.distrelec.b2b.core.inout.erp.data.FindContactResponseData;
import com.namics.distrelec.b2b.core.model.DistB2BBudgetModel;
import com.namics.distrelec.b2b.core.model.DistB2BRequestQuotationPermissionModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.security.IpAddressService;
import com.namics.distrelec.b2b.core.service.company.DistB2BCommerceUnitService;
import com.namics.distrelec.b2b.core.service.customer.DistCustomerAccountService;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.facades.bloomreach.DistBloomreachFacade;
import com.namics.distrelec.b2b.facades.category.DistCategoryFacade;
import com.namics.distrelec.b2b.facades.customer.exceptions.ExistingCustomerRegistrationException;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.distrelec.b2b.facades.storesession.data.ChannelData;
import com.namics.distrelec.b2b.facades.user.data.DistConsentData;
import com.namics.distrelec.b2b.facades.user.data.DistExistingCustomerRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistRegisterData;
import com.namics.distrelec.b2b.facades.user.data.DistSubUserData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.company.B2BCommercePermissionService;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BBudgetExceededPermissionModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bapprovalprocessfacades.company.B2BApproverFacade;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.model.process.ForgottenPasswordProcessModel;
import de.hybris.platform.commerceservices.strategies.CustomerNameStrategy;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.interceptor.impl.UniqueAttributesInterceptor;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultDistCustomerFacadeUnitTest {

    private static final String TEST_EMAIL = "testemailselected@testdistrelec123.com";

    private static final String APPROVER_EMAIL = "approveremailselected@testdistrelec123.com";

    private static final String PHONE_NUMBER = "+41212345678";

    private static final String COUNTRY_CODE = "CH";

    private static final String TITLE_CODE = "mr";

    private static final String FIRST_NAME = "Firstname";

    private static final String LAST_NAME = "Lastname";

    private static final String FULL_NAME = "Firstname Lastname";

    private static final String COMPANY_NAME = "Companyname";

    private static final String SALES_ORG_CODE = "7371";

    @InjectMocks
    @Spy
    private DefaultDistCustomerFacade defaultDistCustomerFacade;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private DistCategoryFacade distCategoryFacade;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private CustomerAccountService customerAccountService;

    @Mock
    private DistSalesOrgService distSalesOrgService;

    @Mock
    private CustomerService customerService;

    @Mock
    private UserService userService;

    @Mock
    private ModelService modelService;

    @Mock
    private DistBloomreachFacade distBloomreachFacade;

    @Mock
    private IpAddressService ipAddressService;

    @Mock
    private DistrelecStoreSessionFacade distrelecStoreSessionFacade;

    @Mock
    private Converter<B2BCustomerModel, CustomerData> b2BCustomerConverter;

    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Mock
    private DistCustomerAccountService distCustomerAccountService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

    @Mock
    private CustomerNameStrategy customerNameStrategy;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private DistB2BCommerceUnitService b2bCommerceUnitService;

    @Mock
    private B2BCommercePermissionService b2bCommercePermissionService;

    @Mock
    private CartService cartService;

    @Mock
    private B2BApproverFacade b2bApproverFacade;

    @Test
    public void testShort() {
        List<String> companyNames = defaultDistCustomerFacade.formatCompanyName("exceed Gmbh");
        assertEquals("exceed Gmbh", companyNames.get(0));
    }

    @Test
    public void testMedium() {
        List<String> companyNames = defaultDistCustomerFacade.formatCompanyName("ZAKŁADY MIĘSNE ZAKRZEWSCY DARIUSZ ZAKRZEWSKI JANUSZ ZAKRZEWSKI SPÓŁKA JAWNA");
        assertEquals("ZAKŁADY MIĘSNE ZAKRZEWSCY DARIUSZ", companyNames.get(0));
        assertEquals("ZAKRZEWSKI JANUSZ ZAKRZEWSKI SPÓŁKA", companyNames.get(1));
        assertEquals("JAWNA", companyNames.get(2));
    }

    @Test
    public void testLongCutLetters() {
        List<String> companyNames = defaultDistCustomerFacade.formatCompanyName(
                "PRZEDSIĘBIORSTWO HANDLOWO USŁUGOWO PRODUKCYJNE MERKURY ROBERT ZGRZYWA MAŁGORZATA STRUG ZGRZYWA SPÓŁKA JAWNA");
        assertEquals("PRZEDSIĘBIORSTWO HANDLOWO USŁUGOWO", companyNames.get(0));
        assertEquals("PRODUKCYJNE MERKURY ROBERT ZGRZYWA", companyNames.get(1));
        assertEquals("MAŁGORZATA STRUG ZGRZYWA SPÓŁKA JAW", companyNames.get(2));
    }

    @Test
    public void testLongNoCut() {
        List<String> companyNames = defaultDistCustomerFacade.formatCompanyName("Gemeinnützige Haftpflichtversicherungsanstalt Kassel");
        assertEquals("Gemeinnützige", companyNames.get(0));
        assertEquals("Haftpflichtversicherungsanstalt", companyNames.get(1));
        assertEquals("Kassel", companyNames.get(2));
    }

    @Test
    public void testLongCutWords() {
        List<String> companyNames = defaultDistCustomerFacade.formatCompanyName(
                "ZAKŁADY PRODUKCYJNO HANDLOWO USŁUGOWE BONDFLEX S C JAN ERMANOWICZ ALICJA ERMANOWICZ BIURO HANDLOWE W JAROCINIE");
        assertEquals("ZAKŁADY PRODUKCYJNO HANDLOWO", companyNames.get(0));
        assertEquals("USŁUGOWE BONDFLEX S C JAN", companyNames.get(1));
        assertEquals("ERMANOWICZ ALICJA ERMANOWICZ BIURO", companyNames.get(2));
    }

    @Test
    public void testVeryLongWord() {
        List<String> companyNames = defaultDistCustomerFacade.formatCompanyName(
                "ZAKŁADY PRODUKCYJNOASJNKDHJSJSBHJBHJSBHJSBHJSBHJSBHJSA HANDLOWO USŁUGOWE BONDFLEX S C JAN ERMANOWICZ");
        assertEquals("ZAKŁADY", companyNames.get(0));
        assertEquals("PRODUKCYJNOASJNKDHJSJSBHJBHJSBHJSBH", companyNames.get(1));
        assertEquals("JSBHJSBHJSA HANDLOWO USŁUGOWE BONDF", companyNames.get(2));
    }

    @Test
    public void testStoreIPAddressWithOneIpAddress() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserModel currentUser = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(ipAddressService.getRequest()).thenReturn(request);
        when(ipAddressService.getClientIpAddress()).thenReturn("X-Forwarded-By : 93.94.71.94");

        defaultDistCustomerFacade.storeIPAddress();

        // then
        verify(currentUser).setLastIPAddress("X-Forwarded-By : 93.94.71.94");
    }

    @Test
    public void testStoreIPAddressWithMultipleIpAddress() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserModel currentUser = mock(UserModel.class);

        // when
        when(userService.getCurrentUser()).thenReturn(currentUser);
        when(ipAddressService.getClientIpAddress()).thenReturn( "X-Forwarded-By : 93.94.71.94 | HTTP_CLIENT_IP : 93.94.71.95 | HTTP_FORWARDED : 93.94.71.96");
        when(ipAddressService.getRequest()).thenReturn(request);

        defaultDistCustomerFacade.storeIPAddress();

        // then
        verify(currentUser).setLastIPAddress("X-Forwarded-By : 93.94.71.94 | HTTP_CLIENT_IP : 93.94.71.95 | HTTP_FORWARDED : 93.94.71.96");
    }

    @Test
    public void testUpdateSessionCurrencyPreferredCurrencyAvailable() {
        // given
        CurrencyData preferredCurrency = new CurrencyData();
        preferredCurrency.setIsocode("USD");
        CurrencyData defaultCurrency = new CurrencyData();
        defaultCurrency.setIsocode("EUR");
        List<CurrencyModel> currencies = List.of(mock(CurrencyModel.class));

        // when
        when(commonI18NService.getAllCurrencies()).thenReturn(currencies);
        when(currencies.iterator().next().getIsocode()).thenReturn("USD");

        boolean result = defaultDistCustomerFacade.updateSessionCurrency(preferredCurrency, defaultCurrency);

        // then
        verify(distrelecStoreSessionFacade).setCurrentCurrency("USD");
        assertThat(result, is(true));
    }

    @Test
    public void testUpdateSessionCurrencyPreferredCurrencyNotAvailable() {
        // given
        CurrencyData preferredCurrency = new CurrencyData();
        preferredCurrency.setIsocode("USD");
        CurrencyData defaultCurrency = new CurrencyData();
        defaultCurrency.setIsocode("EUR");

        List<CurrencyModel> currencies = List.of(mock(CurrencyModel.class));
        when(commonI18NService.getAllCurrencies()).thenReturn(currencies);
        when(currencies.iterator().next().getIsocode()).thenReturn("JPY");

        // when
        boolean result = defaultDistCustomerFacade.updateSessionCurrency(preferredCurrency, defaultCurrency);

        // then
        verify(distrelecStoreSessionFacade).setCurrentCurrency("EUR");
        assertThat(result, is(false));
    }

    @Test
    public void testUpdateSessionCurrencyNullPreferredCurrency() {
        // given
        CurrencyData defaultCurrency = new CurrencyData();
        defaultCurrency.setIsocode("EUR");

        // when
        boolean result = defaultDistCustomerFacade.updateSessionCurrency(null, defaultCurrency);

        // then
        verify(distrelecStoreSessionFacade).setCurrentCurrency("EUR");
        assertThat(result, is(false));
    }

    @Test(expected = ExistingCustomerRegistrationException.class)
    public void testValidateFindContactResultCustomerNotFound() throws ExistingCustomerRegistrationException {
        FindContactResponseData findResult = mock(FindContactResponseData.class);
        DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);

        when(findResult.isCustomerFound()).thenReturn(false);

        defaultDistCustomerFacade.validateFindContactResult(findResult, currentSalesOrg);
    }

    @Test(expected = ExistingCustomerRegistrationException.class)
    public void testValidateFindContactResultContactNotFoundSelfRegistrationDisabled() throws ExistingCustomerRegistrationException {
        FindContactResponseData findResult = mock(FindContactResponseData.class);
        DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        when(findResult.isCustomerFound()).thenReturn(true);

        defaultDistCustomerFacade.validateFindContactResult(findResult, currentSalesOrg);
    }

    @Test(expected = ExistingCustomerRegistrationException.class)
    public void testValidateFindContactResultContactNotUnique() throws ExistingCustomerRegistrationException {
        FindContactResponseData findResult = mock(FindContactResponseData.class);
        DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        when(findResult.isCustomerFound()).thenReturn(true);
        when(findResult.isContactFound()).thenReturn(true);

        defaultDistCustomerFacade.validateFindContactResult(findResult, currentSalesOrg);
    }

    @Test
    public void testFindExistingCustomerAndContactInERPCreatesCorrectFindContactRequestData() {
        // given
        DistExistingCustomerRegisterData registerData = new DistExistingCustomerRegisterData();
        registerData.setCustomerId("123456");
        registerData.setFirstName("John");
        registerData.setLastName("Paul");
        registerData.setEmail("john.paul@distrelec.com");
        registerData.setVatId("123Vat");
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        FindContactResponseData findContactResponseData = new FindContactResponseData();

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn(SALES_ORG_CODE);
        when(customerService.findContact(any(FindContactRequestData.class))).thenReturn(findContactResponseData);
        ArgumentCaptor<FindContactRequestData> captor = ArgumentCaptor.forClass(FindContactRequestData.class);

        defaultDistCustomerFacade.findExistingCustomerAndContactInERP(registerData);

        verify(customerService).findContact(captor.capture());
        FindContactRequestData capturedRequest = captor.getValue();

        // then
        assertThat(capturedRequest.getErpCustomerId(), equalTo("123456"));
        assertThat(capturedRequest.getFirstName(), equalTo("John"));
        assertThat(capturedRequest.getLastName(), equalTo("Paul"));
        assertThat(capturedRequest.getEmail(), equalTo("john.paul@distrelec.com"));
        assertThat(capturedRequest.getVatNumber(), equalTo("123Vat"));
        assertThat(capturedRequest.getSalesOrganization(), equalTo(SALES_ORG_CODE));
    }

    @Test
    public void testUpdateSessionChannelSetsB2BChannel() {
        // given
        CustomerType customerType = CustomerType.B2B;
        ChannelData defaultChannel = mock(ChannelData.class);
        ChannelData b2bChannel = new ChannelData();
        b2bChannel.setType("B2B");
        Collection<ChannelData> channels = Arrays.asList(b2bChannel);

        // when
        when(distrelecStoreSessionFacade.getAllChannels()).thenReturn(channels);

        boolean result = defaultDistCustomerFacade.updateSessionChannel(customerType, defaultChannel);

        // then
        verify(distrelecStoreSessionFacade).setCurrentChannel("B2B");
        assertThat(result, is(true));
    }

    @Test
    public void testUpdateSessionChannelSetsB2CChannel() {
        // given
        CustomerType customerType = CustomerType.B2C;
        ChannelData defaultChannel = mock(ChannelData.class);
        ChannelData b2cChannel = new ChannelData();
        b2cChannel.setType("B2C");
        Collection<ChannelData> channels = Arrays.asList(b2cChannel);

        // when
        when(distrelecStoreSessionFacade.getAllChannels()).thenReturn(channels);

        boolean result = defaultDistCustomerFacade.updateSessionChannel(customerType, defaultChannel);

        // then
        verify(distrelecStoreSessionFacade).setCurrentChannel("B2C");
        assertThat(result, is(true));
    }

    @Test
    public void testUpdateSessionChannelNoMatchingChannel() {
        // given
        CustomerType customerType = CustomerType.B2B;
        ChannelData defaultChannel = new ChannelData();
        defaultChannel.setType("DefaultChannel");
        Collection<ChannelData> channels = Collections.emptyList();

        // when
        when(distrelecStoreSessionFacade.getAllChannels()).thenReturn(channels);

        boolean result = defaultDistCustomerFacade.updateSessionChannel(customerType, defaultChannel);

        // then
        verify(distrelecStoreSessionFacade).setCurrentChannel("DefaultChannel");
        assertThat(result, is(false));
    }

    @Test
    public void testCreateRequestQuotationPermissionSetsCode() {
        // given
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);

        // when
        when(customer.getCustomerID()).thenReturn("112233");
        when(modelService.create(DistB2BRequestQuotationPermissionModel.class)).thenReturn(requestQuotationPermission);

        DistB2BRequestQuotationPermissionModel result = defaultDistCustomerFacade.createRequestQuotationPermission(customer);

        // then
        verify(requestQuotationPermission).setCode("RequestQuotationPermission_112233");
        assertThat(result, equalTo(requestQuotationPermission));
    }

    @Test
    public void testValidatePasswordLinkReturnsFalseIfTokenIsNull() {
        // when
        boolean result = defaultDistCustomerFacade.validateResetPasswordToken(null);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testValidatePasswordLinkReturnsFalseIfTokenDoesNotExist() {
        // given
        String token = "invalidToken";

        // when
        when(distCustomerAccountService.getForgottenPasswordProcessForToken(token)).thenReturn(null);

        boolean result = defaultDistCustomerFacade.validateResetPasswordToken(token);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testValidatePasswordLinkReturnsFalseIfTokenIsExpired() {
        // given
        ForgottenPasswordProcessModel passwordModel = mock(ForgottenPasswordProcessModel.class);
        CustomerModel customerModel = mock(CustomerModel.class);
        String token = "expiredToken";

        // when
        when(distCustomerAccountService.getForgottenPasswordProcessForToken(token)).thenReturn(passwordModel);
        when(passwordModel.getCustomer()).thenReturn(customerModel);
        when(distCustomerAccountService.isResetPasswordTokenValid(token)).thenReturn(false);

        boolean result = defaultDistCustomerFacade.validateResetPasswordToken(token);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testValidatePasswordLinkReturnsTrueIfTokenIsValid() {
        // given
        ForgottenPasswordProcessModel passwordModel = mock(ForgottenPasswordProcessModel.class);
        CustomerModel customerModel = mock(CustomerModel.class);
        String token = "validToken";

        // when
        when(distCustomerAccountService.getForgottenPasswordProcessForToken(token)).thenReturn(passwordModel);
        when(passwordModel.getCustomer()).thenReturn(customerModel);
        when(distCustomerAccountService.isResetPasswordTokenValid(token)).thenReturn(true);

        boolean result = defaultDistCustomerFacade.validateResetPasswordToken(token);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testGetTopCategoriesWithNoIgnoredCategories() {
        // given
        Configuration configuration = mock(Configuration.class);
        DistCategoryIndexData distCategoryIndexData1 = new DistCategoryIndexData();
        distCategoryIndexData1.setCode("Cat1");
        DistCategoryIndexData distCategoryIndexData2 = new DistCategoryIndexData();
        distCategoryIndexData2.setCode("Cat2");
        List<DistCategoryIndexData> allCategories = Arrays.asList(distCategoryIndexData1, distCategoryIndexData2);

        // when
        when(distCategoryFacade.getCategoryIndex()).thenReturn(allCategories);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString("searchBar.ignoredCategoryCodes")).thenReturn("");

        List<DistCategoryIndexData> result = defaultDistCustomerFacade.getTopCategories();

        // then
        assertThat(result, equalTo(allCategories));
    }

    @Test
    public void testGetTopCategoriesWithSomeIgnoredCategories() {
        // given
        Configuration configuration = mock(Configuration.class);
        DistCategoryIndexData distCategoryIndexData1 = new DistCategoryIndexData();
        distCategoryIndexData1.setCode("Cat1");
        DistCategoryIndexData distCategoryIndexData2 = new DistCategoryIndexData();
        distCategoryIndexData2.setCode("Cat2");
        DistCategoryIndexData distCategoryIndexData3 = new DistCategoryIndexData();
        distCategoryIndexData3.setCode("Cat3");
        List<DistCategoryIndexData> allCategories = Arrays.asList(distCategoryIndexData1, distCategoryIndexData2, distCategoryIndexData3);

        // when
        when(distCategoryFacade.getCategoryIndex()).thenReturn(allCategories);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString("searchBar.ignoredCategoryCodes")).thenReturn("Cat2, Cat3");

        List<DistCategoryIndexData> result = defaultDistCustomerFacade.getTopCategories();

        // then
        assertThat(result.get(0).getCode(), equalTo("Cat1"));
    }

    @Test
    public void testGetTopCategoriesIgnoreObsoleteWithNoIgnoredCategories() {
        // given
        Configuration configuration = mock(Configuration.class);
        List<DistCategoryIndexData> allCategories = createCategoryList("Cat1", "Cat2");

        // when
        doReturn(allCategories).when(defaultDistCustomerFacade).getTopCategories();
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString("obsolescence.ignoredCategoryCodes")).thenReturn("");

        List<DistCategoryIndexData> result = defaultDistCustomerFacade.getTopCategoriesIgnoreObsolete();

        // then
        assertThat(result, equalTo(allCategories));
    }

    @Test
    public void testGetTopCategoriesIgnoreObsoleteWithSomeIgnoredCategories() {
        // given
        Configuration configuration = mock(Configuration.class);
        List<DistCategoryIndexData> allCategories = createCategoryList("Cat1", "Cat2", "Cat3");

        // when
        doReturn(allCategories).when(defaultDistCustomerFacade).getTopCategories();
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getString("obsolescence.ignoredCategoryCodes")).thenReturn("Cat2, Cat3");

        List<DistCategoryIndexData> result = defaultDistCustomerFacade.getTopCategoriesIgnoreObsolete();

        // then
        assertThat(result.get(0).getCode(), equalTo("Cat1"));
    }

    @Test
    public void testGetCodiceFiscaleForGuestWithValidB2BCustomerAndB2BUnit() {
        // given
        String expectedVatId = "123VatId";
        B2BCustomerModel customer = mock(B2BCustomerModel.class);
        B2BUnitModel unit = mock(B2BUnitModel.class);

        // when
        when(checkoutCustomerStrategy.getCurrentUserForCheckout()).thenReturn(customer);
        when(b2bUnitService.getParent(customer)).thenReturn(unit);
        when(unit.getVatID()).thenReturn(expectedVatId);

        String result = defaultDistCustomerFacade.getCodiceFiscaleForGuest();

        // then
        assertThat(result, equalTo(expectedVatId));
    }

    @Test
    public void testIsJobRoleShownSupportedCountryAndTypes() {
        // given
        CountryData countryData = mock(CountryData.class);

        // when
        when(distrelecStoreSessionFacade.getCurrentCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn(DistConstants.CountryIsoCode.FRANCE);

        boolean result = defaultDistCustomerFacade.isJobRoleShown(RegistrationType.STANDALONE, CustomerType.B2B);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testCreateUserInBloomreach() throws IOException, DistBloomreachBatchException {
        final DistConsentData consentData = createRegistrationConsentData();
        final String registrationRequest = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonAllCheckboxSelected.json");
        when(distBloomreachFacade.createBloomreachRegistrationRequest(consentData)).thenReturn(registrationRequest);

        boolean result = defaultDistCustomerFacade.createUserInBloomreach(consentData);

        verify(distBloomreachFacade, times(1)).sendBatchRequestToBloomreach(registrationRequest);
        assertThat(result, is(true));
    }

    @Test
    public void testCreateUserInBloomreachThrowsIOException() throws IOException, DistBloomreachBatchException {
        final DistConsentData consentData = createRegistrationConsentData();
        when(distBloomreachFacade.createBloomreachRegistrationRequest(consentData)).thenThrow(IOException.class);

        boolean result = defaultDistCustomerFacade.createUserInBloomreach(consentData);

        verify(distBloomreachFacade, times(0)).sendBatchRequestToBloomreach(anyString());
        assertThat(result, is(false));
    }

    @Test
    public void testConvertGuestToB2CAndRegisterInBloomreach() throws DuplicateUidException, IOException, DistBloomreachBatchException {
        final B2BCustomerModel guestCustomer = mock(B2BCustomerModel.class);
        final List<DistCategoryIndexData> topCategories = createCategoryList("Cat1");
        final CategoryModel categoryOne = mock(CategoryModel.class);
        final AddressModel paymentAddress = mock(AddressModel.class);
        final TitleModel customerTitle = mock(TitleModel.class);
        final CountryModel customerCountry = mock(CountryModel.class);
        final String registrationRequest = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonAllCheckboxUnchecked.json");

        when(userService.getCurrentUser()).thenReturn(guestCustomer);
        when(guestCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(guestCustomer.getErpContactID()).thenReturn("0000000");
        when(guestCustomer.getDefaultPaymentAddress()).thenReturn(paymentAddress);
        when(paymentAddress.getFirstname()).thenReturn(FIRST_NAME);
        when(paymentAddress.getLastname()).thenReturn(LAST_NAME);
        when(paymentAddress.getPhone1()).thenReturn(PHONE_NUMBER);
        when(paymentAddress.getCellphone()).thenReturn(StringUtils.EMPTY);
        when(paymentAddress.getTitle()).thenReturn(customerTitle);
        when(customerTitle.getCode()).thenReturn(TITLE_CODE);
        when(paymentAddress.getCountry()).thenReturn(customerCountry);
        when(customerCountry.getIsocode()).thenReturn(COUNTRY_CODE);
        doReturn(topCategories).when(defaultDistCustomerFacade).getTopCategoriesIgnoreObsolete();
        when(categoryService.getCategoryForCode("Cat1")).thenReturn(categoryOne);
        when(distBloomreachFacade.createBloomreachRegistrationRequest(any(DistConsentData.class))).thenReturn(registrationRequest);

        defaultDistCustomerFacade.convertGuestToB2CAndRegisterInBloomreach("pass", "1234567890");

        verify(customerAccountService, times(1)).convertGuestToCustomer("pass", "1234567890");
        verify(modelService, times(1)).save(guestCustomer);
        verify(distBloomreachFacade, times(1)).createBloomreachRegistrationRequest(any(DistConsentData.class));
        verify(distBloomreachFacade, times(1)).sendBatchRequestToBloomreach(registrationRequest);
    }

    @Test
    public void testConvertGuestToB2CAndRegisterInBloomreachThrowsDuplicateUidException() throws DuplicateUidException, IOException,
                                                                                                 DistBloomreachBatchException {
        final B2BCustomerModel guestCustomer = mock(B2BCustomerModel.class);

        doThrow(DuplicateUidException.class).when(customerAccountService).convertGuestToCustomer("pass", "1234567890");
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.convertGuestToB2CAndRegisterInBloomreach("pass", "1234567890");

        verify(customerAccountService, times(1)).convertGuestToCustomer("pass", "1234567890");
        verify(modelService, times(0)).save(guestCustomer);
        verify(distBloomreachFacade, times(0)).createBloomreachRegistrationRequest(any(DistConsentData.class));
        verify(distBloomreachFacade, times(0)).sendBatchRequestToBloomreach(anyString());
    }

    private DistConsentData createRegistrationConsentData() {
        final DistConsentData consentData = new DistConsentData();
        consentData.setUid(TEST_EMAIL);
        consentData.setErpContactId("0000000");
        consentData.setTitleCode(TITLE_CODE);
        consentData.setFirstName(FIRST_NAME);
        consentData.setLastName(LAST_NAME);
        consentData.setPhoneNumber(PHONE_NUMBER);
        consentData.setMobileNumber(StringUtils.EMPTY);
        consentData.setCountryCode(COUNTRY_CODE);
        consentData.setActiveSubscription(true);
        consentData.setPhonePermission(true);
        consentData.setSmsPermissions(true);
        consentData.setPaperPermission(true);
        consentData.setPersonalisationSubscription(true);
        consentData.setProfilingSubscription(true);
        return consentData;
    }

    private String getExpectedData(final String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        return new String(resource.getInputStream().readAllBytes());
    }

    @Test
    public void testSearchCustomerWhenCustomerExists() {
        // given
        String customerId = "000444555";
        String customerName = "Max-Planck-Institut für Radioastronomie";
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        String code = "7350";
        ReadCustomerResponse readCustomerResponse = mock(ReadCustomerResponse.class);
        AddressResponse addressResponse = mock(AddressResponse.class);
        List<AddressResponse> addressResponses = List.of(addressResponse);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn(code);
        when(customerService.readCustomer(code, customerId)).thenReturn(readCustomerResponse);
        when(readCustomerResponse.getAddresses()).thenReturn(addressResponses);
        when(addressResponse.getCompanyName1()).thenReturn("Max-Planck-Institut für");
        when(addressResponse.getCompanyName2()).thenReturn("Radioastronomie");
        when(addressResponse.getCompanyName3()).thenReturn(null);

        boolean result = defaultDistCustomerFacade.searchCustomer(customerId, customerName);

        // then
        assertThat(result, is(true));
    }

    @Test
    public void testIsJobRoleShownUnsupportedCountry() {
        // given
        CountryData countryData = mock(CountryData.class);

        // when
        when(distrelecStoreSessionFacade.getCurrentCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn(DistConstants.CountryIsoCode.GERMANY);

        boolean result = defaultDistCustomerFacade.isJobRoleShown(RegistrationType.STANDALONE, CustomerType.B2B);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsJobRoleShownMismatchedRegistrationType() {
        // given
        CountryData countryData = mock(CountryData.class);

        // when
        when(distrelecStoreSessionFacade.getCurrentCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn(DistConstants.CountryIsoCode.FRANCE);

        boolean result = defaultDistCustomerFacade.isJobRoleShown(RegistrationType.CHECKOUT_EXISTING, CustomerType.B2B);

        // then
        assertThat(result, is(false));
    }

    @Test
    public void testIsJobRoleShownMismatchedCustomerType() {
        // given
        CountryData countryData = mock(CountryData.class);

        // when
        when(distrelecStoreSessionFacade.getCurrentCountry()).thenReturn(countryData);
        when(countryData.getIsocode()).thenReturn(DistConstants.CountryIsoCode.FRANCE);

        boolean result = defaultDistCustomerFacade.isJobRoleShown(RegistrationType.STANDALONE, CustomerType.B2C);

        assertThat(result, is(false));
    }

    @Test
    public void testSearchCustomerWhenCustomerNotExists() {
        // given
        String customerId = "000444555";
        String customerName = "Max-Planck-Institut für Radioastronomie";
        DistSalesOrgModel distSalesOrgModel = mock(DistSalesOrgModel.class);
        String code = "7350";
        ReadCustomerResponse readCustomerResponse = mock(ReadCustomerResponse.class);
        AddressResponse addressResponse = mock(AddressResponse.class);
        List<AddressResponse> addressResponses = List.of(addressResponse);

        // when
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(distSalesOrgModel);
        when(distSalesOrgModel.getCode()).thenReturn(code);
        when(customerService.readCustomer(code, customerId)).thenReturn(readCustomerResponse);
        when(readCustomerResponse.getAddresses()).thenReturn(addressResponses);
        when(addressResponse.getCompanyName1()).thenReturn("Max-Planck-Institut für");
        when(addressResponse.getCompanyName2()).thenReturn("Radioastronomie GMBH");
        when(addressResponse.getCompanyName3()).thenReturn(null);

        boolean result = defaultDistCustomerFacade.searchCustomer(customerId, customerName);

        // then
        assertThat(result, is(false));
    }

    private List<DistCategoryIndexData> createCategoryList(String... codes) {
        return Arrays.stream(codes)
                     .map(this::createCategory)
                     .collect(Collectors.toList());
    }

    private DistCategoryIndexData createCategory(String code) {
        DistCategoryIndexData category = new DistCategoryIndexData();
        category.setCode(code);
        return category;
    }

    @Test
    public void testRegisterNewB2CCustomer() throws DuplicateUidException {
        final DistRegisterData registerData = createRegisterData(false);
        final B2BCustomerModel newCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2cCustomerGroup = mock(UserGroupModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        setupNewCustomerRegistration();

        when(modelService.create(B2BCustomerModel.class)).thenReturn(newCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID)).thenReturn(b2cCustomerGroup);
        when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
        when(modelService.create(AddressModel.class)).thenReturn(mock(AddressModel.class));
        when(newCustomer.getErpContactID()).thenReturn("0001122333");

        defaultDistCustomerFacade.registerNewCustomer(registerData, CustomerType.B2C);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, newCustomer);
        verify(modelService, times(2)).save(newCustomer);
        verify(modelService, times(2)).save(any(AddressModel.class));
        verify(modelService, times(1)).save(b2bUnit);
        verify(distCustomerAccountService, times(1)).register(newCustomer, "pass");
        verify(distCustomerAccountService, times(1)).raiseRegistrationEvent(newCustomer, RegistrationType.STANDALONE);
    }

    @Test
    public void testRegisterNewB2CCustomerThrowsAmbiguousUniqueKeysException() throws DuplicateUidException {
        final DistRegisterData registerData = createRegisterData(false);
        final B2BCustomerModel duplicatedCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2cCustomerGroup = mock(UserGroupModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final ModelSavingException modelSavingException = mock(ModelSavingException.class);
        final UniqueAttributesInterceptor.AmbiguousUniqueKeysException ambiguousUniqueKeysException = mock(
                UniqueAttributesInterceptor.AmbiguousUniqueKeysException.class);
        setupNewCustomerRegistration();

        when(modelService.create(B2BCustomerModel.class)).thenReturn(duplicatedCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID)).thenReturn(b2cCustomerGroup);
        when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
        doThrow(modelSavingException).when(modelService).save(duplicatedCustomer);
        when(modelSavingException.getCause()).thenReturn(ambiguousUniqueKeysException);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerNewCustomer(registerData, CustomerType.B2C);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, duplicatedCustomer);
        verify(modelService, times(1)).save(duplicatedCustomer);
    }

    @Test
    public void testRegisterNewB2CCustomerThrowsDuplicateUidException() throws DuplicateUidException {
        final DistRegisterData registerData = createRegisterData(false);
        final B2BCustomerModel duplicatedCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2cCustomerGroup = mock(UserGroupModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        setupNewCustomerRegistration();

        when(modelService.create(B2BCustomerModel.class)).thenReturn(duplicatedCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID)).thenReturn(b2cCustomerGroup);
        when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
        when(modelService.create(AddressModel.class)).thenReturn(mock(AddressModel.class));
        doThrow(DuplicateUidException.class).when(distCustomerAccountService).register(duplicatedCustomer, "pass");
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerNewCustomer(registerData, CustomerType.B2C);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, duplicatedCustomer);
        verify(modelService, times(2)).save(duplicatedCustomer);
        verify(modelService, times(1)).save(b2bUnit);
        verify(modelService, times(2)).save(any(AddressModel.class));
        verify(distCustomerAccountService, times(1)).register(duplicatedCustomer, "pass");
    }

    @Test
    public void testRegisterNewB2BCustomer() throws DuplicateUidException {
        final DistRegisterData registerData = createRegisterData(true);
        final B2BCustomerModel newCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2bCustomerGroup = mock(UserGroupModel.class);
        final UserGroupModel b2bAdminGroup = mock(UserGroupModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final BaseSiteModel currentSite = mock(BaseSiteModel.class);
        final Configuration configuration = mock(Configuration.class);
        final B2BBudgetExceededPermissionModel b2BBudgetExceededPermission = mock(B2BBudgetExceededPermissionModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        setupNewCustomerRegistration();

        when(modelService.create(B2BCustomerModel.class)).thenReturn(newCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2BCUSTOMERGROUP_UID)).thenReturn(b2bCustomerGroup);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(b2bAdminGroup);
        when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);
        when(currentSite.getUid()).thenReturn("distrelec_CH");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(newCustomer.getCustomerID()).thenReturn("112233");
        when(newCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(modelService.create(AddressModel.class)).thenReturn(mock(AddressModel.class));
        when(modelService.create(B2BBudgetExceededPermissionModel.class)).thenReturn(b2BBudgetExceededPermission);
        when(b2BBudgetExceededPermission.getCode()).thenReturn("B2BBudgetExceededPermissionFor112233");
        when(modelService.create(DistB2BRequestQuotationPermissionModel.class)).thenReturn(requestQuotationPermission);
        when(requestQuotationPermission.getCode()).thenReturn("RequestQuotationPermission_112233");
        when(newCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(newCustomer.getErpContactID()).thenReturn("0001122333");

        defaultDistCustomerFacade.registerNewCustomer(registerData, CustomerType.B2B);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, newCustomer);
        verify(modelService, times(2)).save(newCustomer);
        verify(modelService, times(1)).save(b2bUnit);
        verify(modelService, times(2)).save(any(AddressModel.class));
        verify(distCustomerAccountService, times(1)).register(newCustomer, "pass");
        verify(b2bCommerceUnitService, times(1)).addApproverToUnit(b2bUnit, newCustomer);
        verify(b2bCommercePermissionService, times(2)).addPermissionToCustomer(anyString(), anyString());
        verify(distCustomerAccountService, times(1)).raiseRegistrationEvent(newCustomer, RegistrationType.STANDALONE);
    }

    @Test
    public void testRegisterNewB2BCustomerThrowsAmbiguousUniqueKeysException() throws DuplicateUidException {
        final DistRegisterData registerData = createRegisterData(false);
        final B2BCustomerModel duplicatedCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2bCustomerGroup = mock(UserGroupModel.class);
        final UserGroupModel b2bAdminGroup = mock(UserGroupModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final BaseSiteModel currentSite = mock(BaseSiteModel.class);
        final Configuration configuration = mock(Configuration.class);
        final ModelSavingException modelSavingException = mock(ModelSavingException.class);
        final UniqueAttributesInterceptor.AmbiguousUniqueKeysException ambiguousUniqueKeysException = mock(
                UniqueAttributesInterceptor.AmbiguousUniqueKeysException.class);
        setupNewCustomerRegistration();

        when(modelService.create(B2BCustomerModel.class)).thenReturn(duplicatedCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2BCUSTOMERGROUP_UID)).thenReturn(b2bCustomerGroup);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(b2bAdminGroup);
        when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);
        when(currentSite.getUid()).thenReturn("distrelec_CH");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        doThrow(modelSavingException).when(modelService).save(duplicatedCustomer);
        when(modelSavingException.getCause()).thenReturn(ambiguousUniqueKeysException);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerNewCustomer(registerData, CustomerType.B2B);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, duplicatedCustomer);
        verify(modelService, times(1)).save(duplicatedCustomer);
    }

    @Test
    public void testRegisterNewB2BCustomerThrowsDuplicateUidException() throws DuplicateUidException {
        final DistRegisterData registerData = createRegisterData(false);
        final B2BCustomerModel duplicatedCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2bCustomerGroup = mock(UserGroupModel.class);
        final UserGroupModel b2bAdminGroup = mock(UserGroupModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final BaseSiteModel currentSite = mock(BaseSiteModel.class);
        final Configuration configuration = mock(Configuration.class);
        setupNewCustomerRegistration();

        when(modelService.create(B2BCustomerModel.class)).thenReturn(duplicatedCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2BCUSTOMERGROUP_UID)).thenReturn(b2bCustomerGroup);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(b2bAdminGroup);
        when(modelService.create(B2BUnitModel.class)).thenReturn(b2bUnit);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);
        when(currentSite.getUid()).thenReturn("distrelec_CH");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(modelService.create(AddressModel.class)).thenReturn(mock(AddressModel.class));
        doThrow(DuplicateUidException.class).when(distCustomerAccountService).register(duplicatedCustomer, "pass");
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerNewCustomer(registerData, CustomerType.B2B);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, duplicatedCustomer);
        verify(modelService, times(2)).save(duplicatedCustomer);
        verify(modelService, times(1)).save(b2bUnit);
        verify(modelService, times(2)).save(any(AddressModel.class));
        verify(distCustomerAccountService, times(1)).register(duplicatedCustomer, "pass");
    }


    private DistRegisterData createRegisterData(boolean isB2BCustomer) {
        final DistRegisterData registerData = new DistRegisterData();
        registerData.setTitleCode(TITLE_CODE);
        registerData.setFirstName(FIRST_NAME);
        registerData.setLastName(LAST_NAME);
        registerData.setLogin(TEST_EMAIL);
        registerData.setEmail(TEST_EMAIL);
        registerData.setLegalEmail(TEST_EMAIL);
        registerData.setCountryCode(COUNTRY_CODE);
        registerData.setPassword("pass");
        registerData.setMarketingCookieEnabled(true);
        registerData.setRegistrationType(RegistrationType.STANDALONE);
        if (isB2BCustomer) {
            registerData.setPhoneNumber(PHONE_NUMBER);
            registerData.setCompanyName(COMPANY_NAME);
            registerData.setVatId("123456789");
        }
        return registerData;
    }

    private void setupNewCustomerRegistration() {
        final TitleModel customerTitle = mock(TitleModel.class);
        final LanguageModel currentLanguage = mock(LanguageModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        final List<DistCategoryIndexData> topCategories = createCategoryList("Cat1");
        final CategoryModel categoryOne = mock(CategoryModel.class);

        when(customerNameStrategy.getName(FIRST_NAME, LAST_NAME)).thenReturn(FULL_NAME);
        when(userService.getTitleForCode(TITLE_CODE)).thenReturn(customerTitle);
        when(commonI18NService.getCurrentLanguage()).thenReturn(currentLanguage);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(commonI18NService.getCountry(COUNTRY_CODE)).thenReturn(currentCountry);
        doReturn(topCategories).when(defaultDistCustomerFacade).getTopCategoriesIgnoreObsolete();
        when(categoryService.getCategoryForCode("Cat1")).thenReturn(categoryOne);
    }

    @Test
    public void testRegisterExistingB2BCustomer() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(true);
        registerData.setRegistrationType(RegistrationType.STANDALONE);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final ReadCustomerResponse readCustomerResponse = mock(ReadCustomerResponse.class);
        final AddressResponse addressResponse = mock(AddressResponse.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2bCustomerGroup = mock(UserGroupModel.class);
        final UserGroupModel b2bAdminGroup = mock(UserGroupModel.class);
        final BaseSiteModel currentSite = mock(BaseSiteModel.class);
        final B2BBudgetExceededPermissionModel b2BBudgetExceededPermission = mock(B2BBudgetExceededPermissionModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final Configuration configuration = mock(Configuration.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        setupExistingCustomerRegistration();

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(true);
        when(customerService.readCustomer(SALES_ORG_CODE, "0001234567")).thenReturn(readCustomerResponse);
        when(readCustomerResponse.getAddresses()).thenReturn(List.of(addressResponse));
        when(addressResponse.getCompanyName1()).thenReturn(COMPANY_NAME);
        when(addressResponse.getCompanyName2()).thenReturn(null);
        when(addressResponse.getCompanyName3()).thenReturn(null);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        doReturn(1).when(defaultDistCustomerFacade).removeOrphanB2BUnit2Approvers(Optional.of(b2bUnit));
        when(modelService.create(B2BCustomerModel.class)).thenReturn(existingCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2BCUSTOMERGROUP_UID)).thenReturn(b2bCustomerGroup);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(b2bAdminGroup);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);
        when(currentSite.getUid()).thenReturn("distrelec_CH");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        when(existingCustomer.getCustomerID()).thenReturn("112233");
        when(existingCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(existingCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(modelService.create(B2BBudgetExceededPermissionModel.class)).thenReturn(b2BBudgetExceededPermission);
        when(b2BBudgetExceededPermission.getCode()).thenReturn("B2BBudgetExceededPermissionFor112233");
        when(modelService.create(DistB2BRequestQuotationPermissionModel.class)).thenReturn(requestQuotationPermission);
        when(requestQuotationPermission.getCode()).thenReturn("RequestQuotationPermission_112233");
        when(existingCustomer.getErpContactID()).thenReturn("0001122333");

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2B);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
        verify(b2bUnitService, times(1)).addMember(b2bUnit, existingCustomer);
        verify(modelService, times(2)).save(existingCustomer);
        verify(distCustomerAccountService, times(1)).registerExisting(existingCustomer,false, "pass", true);
        verify(b2bCommerceUnitService, times(1)).addApproverToUnit(b2bUnit, existingCustomer);
        verify(b2bCommercePermissionService, times(2)).addPermissionToCustomer(anyString(), anyString());
        verify(userService, times(1)).setPassword(existingCustomer, "pass");
        verify(distCustomerAccountService, times(1)).raiseCheckNewCustomerEvent(existingCustomer, RegistrationType.STANDALONE);
        verify(distCustomerAccountService, times(1)).raiseRegistrationEvent(existingCustomer, RegistrationType.STANDALONE);
    }

    @Test
    public void testRegisterExistingB2BCustomerWithInvalidFindContactResult() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(true);
        registerData.setRegistrationType(RegistrationType.STANDALONE);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final FindContactResponseData findContactResponseData = createFindContactResponseData(false);

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(customerService.findContact(any(FindContactRequestData.class))).thenReturn(findContactResponseData);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(false);
        expectedException.expect(ExistingCustomerRegistrationException.class);

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2B);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
    }

    @Test
    public void testRegisterExistingB2BCustomerWithInvalidCompanyName() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(true);
        registerData.setRegistrationType(RegistrationType.STANDALONE);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final FindContactResponseData findContactResponseData = createFindContactResponseData(false);
        final ReadCustomerResponse readCustomerResponse = mock(ReadCustomerResponse.class);
        final AddressResponse addressResponse = mock(AddressResponse.class);

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(customerService.findContact(any(FindContactRequestData.class))).thenReturn(findContactResponseData);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(true);
        when(customerService.readCustomer(SALES_ORG_CODE, "0001234567")).thenReturn(readCustomerResponse);
        when(readCustomerResponse.getAddresses()).thenReturn(List.of(addressResponse));
        when(addressResponse.getCompanyName1()).thenReturn("Name Company");
        when(addressResponse.getCompanyName2()).thenReturn(null);
        when(addressResponse.getCompanyName3()).thenReturn(null);
        expectedException.expect(ExistingCustomerRegistrationException.class);

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2B);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
    }

    @Test
    public void testRegisterExistingB2BCustomerWithExistingContactId() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(true);
        registerData.setRegistrationType(RegistrationType.STANDALONE);
        registerData.setContactId("0001122333");
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final FindContactResponseData findContactResponseData = createFindContactResponseData(true);
        final ReadCustomerResponse readCustomerResponse = mock(ReadCustomerResponse.class);
        final AddressResponse addressResponse = mock(AddressResponse.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(customerService.findContact(any(FindContactRequestData.class))).thenReturn(findContactResponseData);
        when(customerService.readCustomer(SALES_ORG_CODE, "0001234567")).thenReturn(readCustomerResponse);
        when(readCustomerResponse.getAddresses()).thenReturn(List.of(addressResponse));
        when(addressResponse.getCompanyName1()).thenReturn(COMPANY_NAME);
        when(addressResponse.getCompanyName2()).thenReturn(null);
        when(addressResponse.getCompanyName3()).thenReturn(null);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        doReturn(1).when(defaultDistCustomerFacade).removeOrphanB2BUnit2Approvers(Optional.of(b2bUnit));
        when(b2bCommerceUnitService.getB2BCustomerByErpContactId(b2bUnit, "0001122333")).thenReturn(existingCustomer);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2B);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
        verify(b2bCommerceUnitService, times(1)).getB2BCustomerByErpContactId(b2bUnit, "0001122333");
    }

    @Test
    public void testRegisterExistingB2BCustomerThrowsDuplicateUidException() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(true);
        registerData.setRegistrationType(RegistrationType.STANDALONE);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final ReadCustomerResponse readCustomerResponse = mock(ReadCustomerResponse.class);
        final AddressResponse addressResponse = mock(AddressResponse.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2bCustomerGroup = mock(UserGroupModel.class);
        final UserGroupModel b2bAdminGroup = mock(UserGroupModel.class);
        final BaseSiteModel currentSite = mock(BaseSiteModel.class);
        final Configuration configuration = mock(Configuration.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        setupExistingCustomerRegistration();

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(true);
        when(customerService.readCustomer(SALES_ORG_CODE, "0001234567")).thenReturn(readCustomerResponse);
        when(readCustomerResponse.getAddresses()).thenReturn(List.of(addressResponse));
        when(addressResponse.getCompanyName1()).thenReturn(COMPANY_NAME);
        when(addressResponse.getCompanyName2()).thenReturn(null);
        when(addressResponse.getCompanyName3()).thenReturn(null);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        doReturn(1).when(defaultDistCustomerFacade).removeOrphanB2BUnit2Approvers(Optional.of(b2bUnit));
        when(modelService.create(B2BCustomerModel.class)).thenReturn(existingCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2BCUSTOMERGROUP_UID)).thenReturn(b2bCustomerGroup);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(b2bAdminGroup);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);
        when(currentSite.getUid()).thenReturn("distrelec_CH");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        doThrow(DuplicateUidException.class).when(distCustomerAccountService).registerExisting(existingCustomer, false, "pass", true);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2B);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
        verify(b2bUnitService, times(1)).addMember(b2bUnit, existingCustomer);
        verify(modelService, times(2)).save(existingCustomer);
        verify(distCustomerAccountService, times(1)).registerExisting(existingCustomer,false, "pass", true);
    }

    @Test
    public void testRegisterExistingB2BCustomerThrowsAmbiguousUniqueKeysException() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(true);
        registerData.setRegistrationType(RegistrationType.STANDALONE);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final ReadCustomerResponse readCustomerResponse = mock(ReadCustomerResponse.class);
        final AddressResponse addressResponse = mock(AddressResponse.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2bCustomerGroup = mock(UserGroupModel.class);
        final UserGroupModel b2bAdminGroup = mock(UserGroupModel.class);
        final BaseSiteModel currentSite = mock(BaseSiteModel.class);
        final Configuration configuration = mock(Configuration.class);
        final ModelSavingException modelSavingException = mock(ModelSavingException.class);
        final UniqueAttributesInterceptor.AmbiguousUniqueKeysException ambiguousUniqueKeysException = mock(
                UniqueAttributesInterceptor.AmbiguousUniqueKeysException.class);
        setupExistingCustomerRegistration();

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(true);
        when(customerService.readCustomer(SALES_ORG_CODE, "0001234567")).thenReturn(readCustomerResponse);
        when(readCustomerResponse.getAddresses()).thenReturn(List.of(addressResponse));
        when(addressResponse.getCompanyName1()).thenReturn(COMPANY_NAME);
        when(addressResponse.getCompanyName2()).thenReturn(null);
        when(addressResponse.getCompanyName3()).thenReturn(null);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        doReturn(1).when(defaultDistCustomerFacade).removeOrphanB2BUnit2Approvers(Optional.of(b2bUnit));
        when(modelService.create(B2BCustomerModel.class)).thenReturn(existingCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2BCUSTOMERGROUP_UID)).thenReturn(b2bCustomerGroup);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(b2bAdminGroup);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);
        when(currentSite.getUid()).thenReturn("distrelec_CH");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        doThrow(modelSavingException).when(modelService).save(existingCustomer);
        when(modelSavingException.getCause()).thenReturn(ambiguousUniqueKeysException);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2B);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
        verify(b2bUnitService, times(1)).addMember(b2bUnit, existingCustomer);
        verify(modelService, times(1)).save(existingCustomer);
    }

    @Test
    public void testRegisterExistingB2CCustomer() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(false);
        registerData.setRegistrationType(RegistrationType.STANDALONE_EXISTING);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2cCustomerGroup = mock(UserGroupModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        setupExistingCustomerRegistration();

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(true);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(existingCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID)).thenReturn(b2cCustomerGroup);
        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        when(cartService.hasSessionCart()).thenReturn(false);
        when(existingCustomer.getErpContactID()).thenReturn("0001122333");

        defaultDistCustomerFacade.registerExistingCustomer(registerData, false, CustomerType.B2C);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, existingCustomer);
        verify(modelService, times(2)).save(existingCustomer);
        verify(distCustomerAccountService, times(1)).registerExisting(existingCustomer,false, "pass", false);
        verify(distCustomerAccountService, times(1)).raiseCheckNewCustomerEvent(existingCustomer, RegistrationType.STANDALONE_EXISTING);
    }

    @Test
    public void testRegisterExistingB2CCustomerWithInvalidFindContactResult() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(false);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final FindContactResponseData findContactResponseData = createFindContactResponseData(false);

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(customerService.findContact(any(FindContactRequestData.class))).thenReturn(findContactResponseData);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(false);
        expectedException.expect(ExistingCustomerRegistrationException.class);

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2C);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
    }

    @Test
    public void testRegisterExistingB2CCustomerWithExistingContactId() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(false);
        registerData.setContactId("0001122333");
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final FindContactResponseData findContactResponseData = createFindContactResponseData(true);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(customerService.findContact(any(FindContactRequestData.class))).thenReturn(findContactResponseData);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        when(b2bCommerceUnitService.getB2BCustomerByErpContactId(b2bUnit, "0001122333")).thenReturn(existingCustomer);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2C);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
        verify(b2bCommerceUnitService, times(1)).getB2BCustomerByErpContactId(b2bUnit, "0001122333");
    }

    @Test
    public void testRegisterExistingB2CCustomerThrowsDuplicateUidException() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(false);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2cCustomerGroup = mock(UserGroupModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        setupExistingCustomerRegistration();

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(true);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(existingCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID)).thenReturn(b2cCustomerGroup);
        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        doThrow(DuplicateUidException.class).when(distCustomerAccountService).registerExisting(existingCustomer, false, "pass", true);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2C);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
        verify(b2bUnitService, times(1)).addMember(b2bUnit, existingCustomer);
        verify(modelService, times(2)).save(existingCustomer);
        verify(distCustomerAccountService, times(1)).registerExisting(existingCustomer,false, "pass", true);
    }

    @Test
    public void testRegisterExistingB2CCustomerThrowsAmbiguousUniqueKeysException() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(false);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2cCustomerGroup = mock(UserGroupModel.class);
        final ModelSavingException modelSavingException = mock(ModelSavingException.class);
        final UniqueAttributesInterceptor.AmbiguousUniqueKeysException ambiguousUniqueKeysException = mock(
                UniqueAttributesInterceptor.AmbiguousUniqueKeysException.class);
        setupExistingCustomerRegistration();

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(true);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(existingCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID)).thenReturn(b2cCustomerGroup);
        doThrow(modelSavingException).when(modelService).save(existingCustomer);
        when(modelSavingException.getCause()).thenReturn(ambiguousUniqueKeysException);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.registerExistingCustomer(registerData, true, CustomerType.B2C);

        verify(customerService, times(1)).readCustomer(SALES_ORG_CODE, "0001234567");
        verify(b2bUnitService, times(1)).addMember(b2bUnit, existingCustomer);
        verify(modelService, times(1)).save(existingCustomer);
    }

    private DistExistingCustomerRegisterData createExistingCustomerRegisterData(boolean isB2BCustomer) {
        final DistExistingCustomerRegisterData registerData = new DistExistingCustomerRegisterData();
        registerData.setCustomerId("0001234567");
        registerData.setLogin(TEST_EMAIL);
        registerData.setPassword("pass");
        registerData.setTitleCode(TITLE_CODE);
        registerData.setFirstName(FIRST_NAME);
        registerData.setLastName(LAST_NAME);
        registerData.setEmail(TEST_EMAIL);
        registerData.setPhoneNumber(PHONE_NUMBER);
        registerData.setCountryCode(COUNTRY_CODE);
        if (isB2BCustomer) {
            registerData.setCompanyName(COMPANY_NAME);
            registerData.setInvoiceEmail(TEST_EMAIL);
        }
        return registerData;
    }

    private void setupExistingCustomerRegistration() {
        final FindContactResponseData findContactResponseData = createFindContactResponseData(false);
        final TitleModel customerTitle = mock(TitleModel.class);
        final LanguageModel currentLanguage = mock(LanguageModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        final List<DistCategoryIndexData> topCategories = createCategoryList("Cat1");
        final CategoryModel categoryOne = mock(CategoryModel.class);

        when(customerService.findContact(any(FindContactRequestData.class))).thenReturn(findContactResponseData);
        when(customerNameStrategy.getName(FIRST_NAME, LAST_NAME)).thenReturn(FULL_NAME);
        when(userService.getTitleForCode(TITLE_CODE)).thenReturn(customerTitle);
        when(commonI18NService.getCurrentLanguage()).thenReturn(currentLanguage);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
        when(commonI18NService.getCountry(COUNTRY_CODE)).thenReturn(currentCountry);
        doReturn(topCategories).when(defaultDistCustomerFacade).getTopCategoriesIgnoreObsolete();
        when(categoryService.getCategoryForCode("Cat1")).thenReturn(categoryOne);
    }

    private FindContactResponseData createFindContactResponseData(boolean isContactFound) {
        final FindContactResponseData findContactResponseData = new FindContactResponseData();
        findContactResponseData.setCustomerFound(true);
        findContactResponseData.setContactFound(isContactFound);
        findContactResponseData.setContactUnique(true);
        findContactResponseData.setErpCustomerId("0001234567");
        if (isContactFound) {
            findContactResponseData.setErpContactId("0001122333");
        }
        return findContactResponseData;
    }

    @Test
    public void testActivateB2BCustomer() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(true);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final ReadCustomerResponse readCustomerResponse = mock(ReadCustomerResponse.class);
        final AddressResponse addressResponse = mock(AddressResponse.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2bCustomerGroup = mock(UserGroupModel.class);
        final UserGroupModel b2bAdminGroup = mock(UserGroupModel.class);
        final BaseSiteModel currentSite = mock(BaseSiteModel.class);
        final List<DistCategoryIndexData> topCategories = createCategoryList("Cat1");
        final CategoryModel categoryOne = mock(CategoryModel.class);
        final B2BBudgetExceededPermissionModel b2BBudgetExceededPermission = mock(B2BBudgetExceededPermissionModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final Configuration configuration = mock(Configuration.class);
        setupCustomerActivation(true);

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(customerService.readCustomer(SALES_ORG_CODE, "0001234567")).thenReturn(readCustomerResponse);
        when(readCustomerResponse.getAddresses()).thenReturn(List.of(addressResponse));
        when(addressResponse.getCompanyName1()).thenReturn(COMPANY_NAME);
        when(addressResponse.getCompanyName2()).thenReturn(null);
        when(addressResponse.getCompanyName3()).thenReturn(null);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        doReturn(1).when(defaultDistCustomerFacade).removeOrphanB2BUnit2Approvers(Optional.of(b2bUnit));
        when(b2bCommerceUnitService.getB2BCustomerByErpContactId(b2bUnit, "0001122333")).thenReturn(null);
        when(customerNameStrategy.getName(FIRST_NAME, LAST_NAME)).thenReturn(FULL_NAME);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(existingCustomer);
        when(existingCustomer.isRsCustomer()).thenReturn(false);
        when(userService.getUserGroupForUID(DistConstants.User.B2BCUSTOMERGROUP_UID)).thenReturn(b2bCustomerGroup);
        when(userService.getUserGroupForUID(B2BConstants.B2BADMINGROUP)).thenReturn(b2bAdminGroup);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(currentSite);
        when(currentSite.getUid()).thenReturn("distrelec_CH");
        when(configurationService.getConfiguration()).thenReturn(configuration);
        doReturn(topCategories).when(defaultDistCustomerFacade).getTopCategoriesIgnoreObsolete();
        when(categoryService.getCategoryForCode("Cat1")).thenReturn(categoryOne);
        when(existingCustomer.getCustomerID()).thenReturn("112233");
        when(existingCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(existingCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(modelService.create(B2BBudgetExceededPermissionModel.class)).thenReturn(b2BBudgetExceededPermission);
        when(b2BBudgetExceededPermission.getCode()).thenReturn("B2BBudgetExceededPermissionFor112233");
        when(modelService.create(DistB2BRequestQuotationPermissionModel.class)).thenReturn(requestQuotationPermission);
        when(requestQuotationPermission.getCode()).thenReturn("RequestQuotationPermission_112233");

        defaultDistCustomerFacade.activateCustomer(registerData, CustomerType.B2B);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, existingCustomer);
        verify(modelService, times(1)).save(existingCustomer);
        verify(userService, times(1)).setCurrentUser(existingCustomer);
        verify(distCustomerAccountService, times(1)).registerExisting(existingCustomer,true, "pass", true);
        verify(distCustomerAccountService, times(1)).updateConsentConditionRequired(existingCustomer, false);
        verify(b2bCommerceUnitService, times(1)).addApproverToUnit(b2bUnit, existingCustomer);
        verify(b2bCommercePermissionService, times(2)).addPermissionToCustomer(anyString(), anyString());
        verify(userService, times(1)).setPassword(existingCustomer, "pass");
    }

    @Test
    public void testActivateB2CCustomer() throws DuplicateUidException, ExistingCustomerRegistrationException {
        final DistExistingCustomerRegisterData registerData = createExistingCustomerRegisterData(false);
        final DistSalesOrgModel currentSalesOrg = mock(DistSalesOrgModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel existingCustomer = mock(B2BCustomerModel.class);
        final UserGroupModel b2cCustomerGroup = mock(UserGroupModel.class);
        setupCustomerActivation(false);

        when(distSalesOrgService.getCurrentSalesOrg()).thenReturn(currentSalesOrg);
        when(currentSalesOrg.getCode()).thenReturn(SALES_ORG_CODE);
        when(currentSalesOrg.isRegisteringNewContactToExistingCustomerAllowed()).thenReturn(true);
        when(b2bCommerceUnitService.getB2BUnitByErpCustomerId(currentSalesOrg, "0001234567")).thenReturn(b2bUnit);
        when(customerNameStrategy.getName(FIRST_NAME, LAST_NAME)).thenReturn(FULL_NAME);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(existingCustomer);
        when(userService.getUserGroupForUID(DistConstants.User.B2CCUSTOMERGROUP_UID)).thenReturn(b2cCustomerGroup);
        when(cartService.hasSessionCart()).thenReturn(false);
        when(existingCustomer.getErpContactID()).thenReturn("0001122333");

        defaultDistCustomerFacade.activateCustomer(registerData, CustomerType.B2C);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, existingCustomer);
        verify(modelService, times(1)).save(existingCustomer);
        verify(distCustomerAccountService, times(1)).registerExisting(existingCustomer,false, "pass");
        verify(distCustomerAccountService, times(1)).raiseNewCustomerActivationEvent(existingCustomer);
    }

    private void setupCustomerActivation(boolean isContactFound) {
        final FindContactResponseData findContactResponseData = createFindContactResponseData(isContactFound);
        final TitleModel customerTitle = mock(TitleModel.class);
        final LanguageModel currentLanguage = mock(LanguageModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);

        when(customerService.findContact(any(FindContactRequestData.class))).thenReturn(findContactResponseData);
        when(userService.getTitleForCode(TITLE_CODE)).thenReturn(customerTitle);
        when(commonI18NService.getCurrentLanguage()).thenReturn(currentLanguage);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);

        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        when(commonI18NService.getCountry(COUNTRY_CODE)).thenReturn(currentCountry);
    }

    @Test
    public void testCreateB2BEmployeeThrowsAmbiguousUniqueKeysException() throws DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(true, false);
        final B2BCustomerModel approverCustomer = mock(B2BCustomerModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel newCustomer = mock(B2BCustomerModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        final ModelSavingException modelSavingException = mock(ModelSavingException.class);
        final UniqueAttributesInterceptor.AmbiguousUniqueKeysException ambiguousUniqueKeysException = mock(
                UniqueAttributesInterceptor.AmbiguousUniqueKeysException.class);
        setupB2BEmployeeRegistration();

        when(approverCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bUnit.getCustomerType()).thenReturn(CustomerType.REGISTERED);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(newCustomer);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
        doThrow(modelSavingException).when(modelService).save(newCustomer);
        when(modelSavingException.getCause()).thenReturn(ambiguousUniqueKeysException);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.createB2BEmployee(registerData, approverCustomer);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, newCustomer);
        verify(modelService, times(1)).save(newCustomer);
    }

    @Test
    public void testCreateB2BEmployeeThrowsDuplicateUidException() throws DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(true, false);
        final B2BCustomerModel approverCustomer = mock(B2BCustomerModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel newCustomer = mock(B2BCustomerModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        setupB2BEmployeeRegistration();

        when(approverCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bUnit.getCustomerType()).thenReturn(CustomerType.REGISTERED);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(newCustomer);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        doThrow(DuplicateUidException.class).when(distCustomerAccountService).registerB2BEmployee(newCustomer);
        expectedException.expect(DuplicateUidException.class);

        defaultDistCustomerFacade.createB2BEmployee(registerData, approverCustomer);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, newCustomer);
        verify(modelService, times(2)).save(newCustomer);
        verify(distCustomerAccountService, times(1)).registerB2BEmployee(newCustomer);
    }


    @Test
    public void testCreateB2BEmployeeWithUnlimitedBudgetAndWithoutQuotationPermission() throws DuplicateUidException, IOException {
        final DistSubUserData registerData = createSubUserData(true, false);
        final B2BCustomerModel approverCustomer = mock(B2BCustomerModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel newCustomer = mock(B2BCustomerModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final String registrationRequest = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonAllCheckboxUnchecked.json");
        setupB2BEmployeeRegistration();

        when(approverCustomer.getUid()).thenReturn(APPROVER_EMAIL);
        when(approverCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(b2bUnit.getCustomerType()).thenReturn(CustomerType.REGISTERED);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(newCustomer);
        when(newCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        when(distBloomreachFacade.createBloomreachRegistrationRequest(any(DistConsentData.class))).thenReturn(registrationRequest);

        defaultDistCustomerFacade.createB2BEmployee(registerData, approverCustomer);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, newCustomer);
        verify(modelService, times(2)).save(newCustomer);
        verify(distCustomerAccountService, times(1)).registerB2BEmployee(newCustomer);
        verify(b2bApproverFacade, times(1)).addApproverForCustomer(TEST_EMAIL, APPROVER_EMAIL);
        verify(distCustomerAccountService, times(1)).updateConsentConditionRequired(newCustomer, true);
    }

    @Test
    public void testCreateB2BEmployeeWithUnlimitedBudgetAndWithQuotationPermission() throws DuplicateUidException, IOException {
        final DistSubUserData registerData = createSubUserData(true, true);
        final B2BCustomerModel approverCustomer = mock(B2BCustomerModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel newCustomer = mock(B2BCustomerModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final String registrationRequest = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonAllCheckboxUnchecked.json");
        setupB2BEmployeeRegistration();

        when(approverCustomer.getUid()).thenReturn(APPROVER_EMAIL);
        when(approverCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(b2bUnit.getCustomerType()).thenReturn(CustomerType.REGISTERED);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(newCustomer);
        when(newCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(newCustomer.getPermissions()).thenReturn(Collections.emptySet());
        when(newCustomer.getCustomerID()).thenReturn("112233");
        when(newCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        when(modelService.create(DistB2BRequestQuotationPermissionModel.class)).thenReturn(requestQuotationPermission);
        when(requestQuotationPermission.getCode()).thenReturn("RequestQuotationPermission_112233");
        when(distBloomreachFacade.createBloomreachRegistrationRequest(any(DistConsentData.class))).thenReturn(registrationRequest);

        defaultDistCustomerFacade.createB2BEmployee(registerData, approverCustomer);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, newCustomer);
        verify(modelService, times(2)).save(newCustomer);
        verify(distCustomerAccountService, times(1)).registerB2BEmployee(newCustomer);
        verify(b2bApproverFacade, times(1)).addApproverForCustomer(TEST_EMAIL, APPROVER_EMAIL);
        verify(distCustomerAccountService, times(1)).updateConsentConditionRequired(newCustomer, true);
        verify(b2bCommercePermissionService, times(1)).addPermissionToCustomer(anyString(), anyString());
    }

    @Test
    public void testCreateB2BEmployeeWithoutUnlimitedBudgetAndWithQuotationPermission() throws DuplicateUidException, IOException {
        final DistSubUserData registerData = createSubUserData(false, true);
        final B2BCustomerModel approverCustomer = mock(B2BCustomerModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel newCustomer = mock(B2BCustomerModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final String registrationRequest = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonAllCheckboxUnchecked.json");
        setupB2BEmployeeRegistration();

        when(approverCustomer.getUid()).thenReturn(APPROVER_EMAIL);
        when(approverCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(b2bUnit.getCustomerType()).thenReturn(CustomerType.REGISTERED);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(newCustomer);
        when(newCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(newCustomer.getPermissions()).thenReturn(Collections.emptySet());
        when(newCustomer.getCustomerID()).thenReturn("112233");
        when(newCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(newCustomer.getSessionCurrency()).thenReturn(currentCurrency);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        when(modelService.create(DistB2BRequestQuotationPermissionModel.class)).thenReturn(requestQuotationPermission);
        when(requestQuotationPermission.getCode()).thenReturn("RequestQuotationPermission_112233");
        when(modelService.create(DistB2BBudgetModel.class)).thenReturn(budget);
        when(distBloomreachFacade.createBloomreachRegistrationRequest(any(DistConsentData.class))).thenReturn(registrationRequest);

        defaultDistCustomerFacade.createB2BEmployee(registerData, approverCustomer);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, newCustomer);
        verify(modelService, times(3)).save(newCustomer);
        verify(distCustomerAccountService, times(1)).registerB2BEmployee(newCustomer);
        verify(b2bApproverFacade, times(1)).addApproverForCustomer(TEST_EMAIL, APPROVER_EMAIL);
        verify(distCustomerAccountService, times(1)).updateConsentConditionRequired(newCustomer, true);
        verify(b2bCommercePermissionService, times(1)).addPermissionToCustomer(anyString(), anyString());
    }

    @Test
    public void testCreateB2BEmployeeWithoutUnlimitedBudgetAndWithoutQuotationPermission() throws DuplicateUidException, IOException {
        final DistSubUserData registerData = createSubUserData(false, false);
        final B2BCustomerModel approverCustomer = mock(B2BCustomerModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final B2BCustomerModel newCustomer = mock(B2BCustomerModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final String registrationRequest = getExpectedData("distrelecB2Bfacades/test/Registration/ExpectedJsonAllCheckboxUnchecked.json");
        setupB2BEmployeeRegistration();

        when(approverCustomer.getUid()).thenReturn(APPROVER_EMAIL);
        when(approverCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(b2bUnit.getCustomerType()).thenReturn(CustomerType.REGISTERED);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);
        when(modelService.create(B2BCustomerModel.class)).thenReturn(newCustomer);
        when(newCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(newCustomer.getCustomerID()).thenReturn("112233");
        when(newCustomer.getSessionCurrency()).thenReturn(currentCurrency);
        when(commonI18NService.getCurrentCurrency()).thenReturn(currentCurrency);
        when(modelService.create(AddressModel.class)).thenReturn(customerAddress);
        when(modelService.create(DistB2BBudgetModel.class)).thenReturn(budget);
        when(distBloomreachFacade.createBloomreachRegistrationRequest(any(DistConsentData.class))).thenReturn(registrationRequest);

        defaultDistCustomerFacade.createB2BEmployee(registerData, approverCustomer);

        verify(b2bUnitService, times(1)).addMember(b2bUnit, newCustomer);
        verify(modelService, times(3)).save(newCustomer);
        verify(distCustomerAccountService, times(1)).registerB2BEmployee(newCustomer);
        verify(b2bApproverFacade, times(1)).addApproverForCustomer(TEST_EMAIL, APPROVER_EMAIL);
        verify(distCustomerAccountService, times(1)).updateConsentConditionRequired(newCustomer, true);
    }

    private void setupB2BEmployeeRegistration() {
        final UserGroupModel b2bCustomerGroup = mock(UserGroupModel.class);
        final TitleModel customerTitle = mock(TitleModel.class);
        final LanguageModel currentLanguage = mock(LanguageModel.class);
        final List<DistCategoryIndexData> topCategories = createCategoryList("Cat1");
        final CategoryModel categoryOne = mock(CategoryModel.class);

        when(customerNameStrategy.getName(FIRST_NAME, LAST_NAME)).thenReturn(FULL_NAME);
        when(userService.getUserGroupForUID(DistConstants.User.B2BCUSTOMERGROUP_UID)).thenReturn(b2bCustomerGroup);
        when(userService.getTitleForCode(TITLE_CODE)).thenReturn(customerTitle);
        when(commonI18NService.getCurrentLanguage()).thenReturn(currentLanguage);
        doReturn(topCategories).when(defaultDistCustomerFacade).getTopCategoriesIgnoreObsolete();
        when(categoryService.getCategoryForCode("Cat1")).thenReturn(categoryOne);
    }

    @Test
    public void testUpdateB2BEmployeeWithUnlimitedBudgetAndQuotationPermissionDisabled()
            throws DistBloomreachBatchException, JsonProcessingException, DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(true, false);
        final B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        setupB2BEmployeeUpdate();

        when(b2bCustomer.getContactAddress()).thenReturn(customerAddress);
        when(b2bCustomer.getBudget()).thenReturn(budget);
        when(b2bCustomer.getPermissions()).thenReturn(Set.of(requestQuotationPermission));
        when(b2bCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bCustomer.getErpContactID()).thenReturn("0001122333");
        when(requestQuotationPermission.getActive()).thenReturn(true);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);

        defaultDistCustomerFacade.updateB2BEmployee(registerData, b2bCustomer);

        verify(modelService, times(2)).save(b2bCustomer);
        verify(modelService, times(1)).remove(budget);
        verify(modelService, times(1)).save(requestQuotationPermission);
        verify(customerService, times(1)).updateContact(b2bCustomer);
        verify(distBloomreachFacade, times(1)).updateCustomerInBloomreach(any(DistConsentData.class));
    }

    @Test
    public void testUpdateB2BEmployeeWithUnlimitedBudgetAndQuotationPermissionEnabled()
            throws DistBloomreachBatchException, JsonProcessingException, DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(true, true);
        final B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        setupB2BEmployeeUpdate();

        when(b2bCustomer.getContactAddress()).thenReturn(customerAddress);
        when(b2bCustomer.getBudget()).thenReturn(budget);
        when(b2bCustomer.getPermissions()).thenReturn(Set.of(requestQuotationPermission));
        when(b2bCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bCustomer.getErpContactID()).thenReturn("0001122333");
        when(requestQuotationPermission.getActive()).thenReturn(false);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);

        defaultDistCustomerFacade.updateB2BEmployee(registerData, b2bCustomer);

        verify(modelService, times(2)).save(b2bCustomer);
        verify(modelService, times(1)).remove(budget);
        verify(modelService, times(1)).save(requestQuotationPermission);
        verify(customerService, times(1)).updateContact(b2bCustomer);
        verify(distBloomreachFacade, times(1)).updateCustomerInBloomreach(any(DistConsentData.class));
    }

    @Test
    public void testUpdateB2BEmployeeWithUnlimitedBudgetAndQuotationPermissionCreated()
            throws DistBloomreachBatchException, JsonProcessingException, DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(true, true);
        final B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        setupB2BEmployeeUpdate();

        when(b2bCustomer.getContactAddress()).thenReturn(customerAddress);
        when(b2bCustomer.getBudget()).thenReturn(budget);
        when(b2bCustomer.getPermissions()).thenReturn(Collections.emptySet());
        when(b2bCustomer.getCustomerID()).thenReturn("112233");
        when(b2bCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(b2bCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bCustomer.getErpContactID()).thenReturn("0001122333");
        when(modelService.create(DistB2BRequestQuotationPermissionModel.class)).thenReturn(requestQuotationPermission);
        when(requestQuotationPermission.getCode()).thenReturn("RequestQuotationPermission_112233");
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);

        defaultDistCustomerFacade.updateB2BEmployee(registerData, b2bCustomer);

        verify(modelService, times(2)).save(b2bCustomer);
        verify(modelService, times(1)).remove(budget);
        verify(modelService, times(1)).save(requestQuotationPermission);
        verify(b2bCommercePermissionService, times(1)).addPermissionToCustomer(anyString(), anyString());
        verify(customerService, times(1)).updateContact(b2bCustomer);
        verify(distBloomreachFacade, times(1)).updateCustomerInBloomreach(any(DistConsentData.class));
    }

    @Test
    public void testUpdateB2BEmployeeWithBudgetCreatedAndQuotationPermissionDisabled()
            throws DistBloomreachBatchException, JsonProcessingException, DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(false, false);
        final B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        setupB2BEmployeeUpdate();

        when(b2bCustomer.getContactAddress()).thenReturn(customerAddress);
        when(b2bCustomer.getBudget()).thenReturn(null);
        when(b2bCustomer.getPermissions()).thenReturn(Set.of(requestQuotationPermission));
        when(b2bCustomer.getCustomerID()).thenReturn("112233");
        when(b2bCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bCustomer.getErpContactID()).thenReturn("0001122333");
        when(b2bCustomer.getSessionCurrency()).thenReturn(currentCurrency);
        when(modelService.create(DistB2BBudgetModel.class)).thenReturn(budget);
        when(requestQuotationPermission.getActive()).thenReturn(true);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);
        setupB2BEmployeeUpdate();

        defaultDistCustomerFacade.updateB2BEmployee(registerData, b2bCustomer);

        verify(modelService, times(2)).save(b2bCustomer);
        verify(modelService, times(1)).save(budget);
        verify(modelService, times(1)).save(requestQuotationPermission);
        verify(customerService, times(1)).updateContact(b2bCustomer);
        verify(distBloomreachFacade, times(1)).updateCustomerInBloomreach(any(DistConsentData.class));
    }

    @Test
    public void testUpdateB2BEmployeeWithBudgetCreatedAndQuotationPermissionEnabled()
            throws DistBloomreachBatchException, JsonProcessingException, DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(false, true);
        final B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        setupB2BEmployeeUpdate();

        when(b2bCustomer.getContactAddress()).thenReturn(customerAddress);
        when(b2bCustomer.getBudget()).thenReturn(null);
        when(b2bCustomer.getPermissions()).thenReturn(Set.of(requestQuotationPermission));
        when(b2bCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bCustomer.getErpContactID()).thenReturn("0001122333");
        when(b2bCustomer.getSessionCurrency()).thenReturn(currentCurrency);
        when(modelService.create(DistB2BBudgetModel.class)).thenReturn(budget);
        when(requestQuotationPermission.getActive()).thenReturn(false);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);

        defaultDistCustomerFacade.updateB2BEmployee(registerData, b2bCustomer);

        verify(modelService, times(2)).save(b2bCustomer);
        verify(modelService, times(1)).save(budget);
        verify(modelService, times(1)).save(requestQuotationPermission);
        verify(customerService, times(1)).updateContact(b2bCustomer);
        verify(distBloomreachFacade, times(1)).updateCustomerInBloomreach(any(DistConsentData.class));
    }

    @Test
    public void testUpdateB2BEmployeeWithBudgetCreatedAndQuotationPermissionCreated()
            throws DistBloomreachBatchException, JsonProcessingException, DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(false, true);
        final B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        final CurrencyModel currentCurrency = mock(CurrencyModel.class);
        setupB2BEmployeeUpdate();

        when(b2bCustomer.getContactAddress()).thenReturn(customerAddress);
        when(b2bCustomer.getBudget()).thenReturn(null);
        when(b2bCustomer.getPermissions()).thenReturn(Collections.emptySet());
        when(b2bCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(b2bCustomer.getCustomerID()).thenReturn("112233");
        when(b2bCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bCustomer.getErpContactID()).thenReturn("0001122333");
        when(b2bCustomer.getSessionCurrency()).thenReturn(currentCurrency);
        when(modelService.create(DistB2BBudgetModel.class)).thenReturn(budget);
        when(modelService.create(DistB2BRequestQuotationPermissionModel.class)).thenReturn(requestQuotationPermission);
        when(requestQuotationPermission.getCode()).thenReturn("RequestQuotationPermission_112233");
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);

        defaultDistCustomerFacade.updateB2BEmployee(registerData, b2bCustomer);

        verify(modelService, times(2)).save(b2bCustomer);
        verify(modelService, times(1)).save(budget);
        verify(modelService, times(1)).save(requestQuotationPermission);
        verify(b2bCommercePermissionService, times(1)).addPermissionToCustomer(anyString(), anyString());
        verify(customerService, times(1)).updateContact(b2bCustomer);
        verify(distBloomreachFacade, times(1)).updateCustomerInBloomreach(any(DistConsentData.class));
    }

    @Test
    public void testUpdateB2BEmployeeWithBudgetUpdatedAndQuotationPermissionDisabled()
            throws DistBloomreachBatchException, JsonProcessingException, DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(false, false);
        final B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        setupB2BEmployeeUpdate();

        when(b2bCustomer.getContactAddress()).thenReturn(customerAddress);
        when(b2bCustomer.getBudget()).thenReturn(budget);
        when(b2bCustomer.getPermissions()).thenReturn(Set.of(requestQuotationPermission));
        when(b2bCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bCustomer.getErpContactID()).thenReturn("0001122333");
        when(budget.getOriginalYearlyBudget()).thenReturn(BigDecimal.valueOf(75));
        when(requestQuotationPermission.getActive()).thenReturn(true);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);

        defaultDistCustomerFacade.updateB2BEmployee(registerData, b2bCustomer);

        verify(modelService, times(2)).save(b2bCustomer);
        verify(modelService, times(1)).save(budget);
        verify(modelService, times(1)).save(requestQuotationPermission);
        verify(customerService, times(1)).updateContact(b2bCustomer);
        verify(distBloomreachFacade, times(1)).updateCustomerInBloomreach(any(DistConsentData.class));
    }

    @Test
    public void testUpdateB2BEmployeeWithBudgetUpdatedAndQuotationPermissionEnabled()
            throws DistBloomreachBatchException, JsonProcessingException, DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(false, true);
        final B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        setupB2BEmployeeUpdate();

        when(b2bCustomer.getContactAddress()).thenReturn(customerAddress);
        when(b2bCustomer.getBudget()).thenReturn(budget);
        when(b2bCustomer.getPermissions()).thenReturn(Set.of(requestQuotationPermission));
        when(b2bCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bCustomer.getErpContactID()).thenReturn("0001122333");
        when(budget.getOriginalYearlyBudget()).thenReturn(BigDecimal.valueOf(75));
        when(requestQuotationPermission.getActive()).thenReturn(false);
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);

        defaultDistCustomerFacade.updateB2BEmployee(registerData, b2bCustomer);

        verify(modelService, times(2)).save(b2bCustomer);
        verify(modelService, times(1)).save(budget);
        verify(modelService, times(1)).save(requestQuotationPermission);
        verify(customerService, times(1)).updateContact(b2bCustomer);
        verify(distBloomreachFacade, times(1)).updateCustomerInBloomreach(any(DistConsentData.class));
    }

    @Test
    public void testUpdateB2BEmployeeWithBudgetUpdatedAndQuotationPermissionCreated()
            throws DistBloomreachBatchException, JsonProcessingException, DuplicateUidException {
        final DistSubUserData registerData = createSubUserData(false, true);
        final B2BCustomerModel b2bCustomer = mock(B2BCustomerModel.class);
        final AddressModel customerAddress = mock(AddressModel.class);
        final DistB2BBudgetModel budget = mock(DistB2BBudgetModel.class);
        final DistB2BRequestQuotationPermissionModel requestQuotationPermission = mock(DistB2BRequestQuotationPermissionModel.class);
        final B2BUnitModel b2bUnit = mock(B2BUnitModel.class);
        final CountryModel currentCountry = mock(CountryModel.class);
        setupB2BEmployeeUpdate();

        when(b2bCustomer.getContactAddress()).thenReturn(customerAddress);
        when(b2bCustomer.getBudget()).thenReturn(budget);
        when(b2bCustomer.getPermissions()).thenReturn(Collections.emptySet());
        when(b2bCustomer.getDefaultB2BUnit()).thenReturn(b2bUnit);
        when(b2bCustomer.getUid()).thenReturn(TEST_EMAIL);
        when(b2bCustomer.getCustomerID()).thenReturn("112233");
        when(b2bCustomer.getErpContactID()).thenReturn("0001122333");
        when(budget.getOriginalYearlyBudget()).thenReturn(BigDecimal.valueOf(75));
        when(modelService.create(DistB2BRequestQuotationPermissionModel.class)).thenReturn(requestQuotationPermission);
        when(requestQuotationPermission.getCode()).thenReturn("RequestQuotationPermission_112233");
        when(b2bUnit.getCountry()).thenReturn(currentCountry);
        when(currentCountry.getIsocode()).thenReturn(COUNTRY_CODE);

        defaultDistCustomerFacade.updateB2BEmployee(registerData, b2bCustomer);

        verify(modelService, times(2)).save(b2bCustomer);
        verify(modelService, times(1)).save(budget);
        verify(modelService, times(1)).save(requestQuotationPermission);
        verify(b2bCommercePermissionService, times(1)).addPermissionToCustomer(anyString(), anyString());
        verify(customerService, times(1)).updateContact(b2bCustomer);
        verify(distBloomreachFacade, times(1)).updateCustomerInBloomreach(any(DistConsentData.class));
    }

    private void setupB2BEmployeeUpdate() {
        final TitleModel customerTitle = mock(TitleModel.class);
        final List<DistCategoryIndexData> topCategories = createCategoryList("Cat1");
        final CategoryModel categoryOne = mock(CategoryModel.class);

        when(userService.getTitleForCode(TITLE_CODE)).thenReturn(customerTitle);
        when(customerNameStrategy.getName(FIRST_NAME, LAST_NAME)).thenReturn(FULL_NAME);
        doReturn(topCategories).when(defaultDistCustomerFacade).getTopCategoriesIgnoreObsolete();
        when(categoryService.getCategoryForCode("Cat1")).thenReturn(categoryOne);
    }

    private DistSubUserData createSubUserData(boolean isBudgetWithoutLimit, boolean isRequestQuotationPermission) {
        final DistSubUserData registerData = new DistSubUserData();
        registerData.setLogin(TEST_EMAIL);
        registerData.setTitleCode(TITLE_CODE);
        registerData.setFirstName(FIRST_NAME);
        registerData.setLastName(LAST_NAME);
        registerData.setEmail(TEST_EMAIL);
        registerData.setPhoneNumber(PHONE_NUMBER);
        registerData.setBudgetWithoutLimit(isBudgetWithoutLimit);
        if (!isBudgetWithoutLimit) {
            registerData.setBudgetPerOrder(BigDecimal.valueOf(50));
            registerData.setYearlyBudget(BigDecimal.valueOf(100));
        }
        registerData.setRequestQuotationPermission(isRequestQuotationPermission);
        return registerData;
    }

}
