package com.namics.distrelec.b2b.core.service.order.strategies.impl.evopayment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.model.payment.HopSupportingPaymentTransactionEntryModel;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class CreditCardPaymentParamsStrategyUnitTest {

    private static final String CUSTOMER_EMAIL = "customer@distrelec.com";

    private static final String COMPANY_NAME = "CompanyName";

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private BaseSiteService baseSiteService;

    @Mock
    private BaseStoreService baseStoreService;

    @Mock
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Mock
    private CartService cartService;

    @Mock
    private ModelService modelService;

    @Mock
    private CommonI18NService commonI18NService;

    @Mock
    private B2BCustomerModel customer;

    @Mock
    private CartModel cart;

    @Mock
    private CurrencyModel currency;

    @Mock
    private AbstractDistPaymentModeModel paymentModeModel;

    @Mock
    private Configuration configuration;

    @Mock
    private BaseSiteModel baseSite;

    @Mock
    private PaymentTransactionModel paymentTransactionModel;

    @Mock
    private HopSupportingPaymentTransactionEntryModel hopSupportingPaymentTransactionEntryModel;

    @Mock
    private AddressModel contactAddress;

    @Mock
    private AddressModel paymentAddress;

    @Mock
    private CountryModel countryModel;

    @Mock
    private B2BUnitModel b2BUnitModel;

    @Mock
    private TitleModel titleModel;

    @InjectMocks
    private CreditCardPaymentParamsStrategy creditCardPaymentParamsStrategy;

    @Before
    public void setUp() {
        when(cart.getUser()).thenReturn(customer);
        PK pk1 = PK.createFixedPK(1, 2L);
        PK pk2 = PK.createFixedPK(2, 3L);
        when(customer.getPk()).thenReturn(pk1, pk2);
        when(cart.getCurrency()).thenReturn(currency);
        when(currency.getIsocode()).thenReturn("CHF");
        when(customer.getEmail()).thenReturn(CUSTOMER_EMAIL);
        when(cart.getPaymentMode()).thenReturn(paymentModeModel);
        when(configurationService.getConfiguration()).thenReturn(configuration);
        when(configuration.getBoolean(anyString(), anyBoolean())).thenReturn(false);
        when(baseSiteService.getCurrentBaseSite()).thenReturn(baseSite);
        when(baseSite.getUid()).thenReturn("basesite-uid");
        when(cartService.getSessionCart()).thenReturn(cart);
        when(modelService.create(any(Class.class))).thenReturn(paymentTransactionModel, hopSupportingPaymentTransactionEntryModel);

        when(customer.getDefaultB2BUnit()).thenReturn(b2BUnitModel);
        when(b2BUnitModel.getName()).thenReturn("UnitName");
        when(contactAddress.getFirstname()).thenReturn("Firstname");
        when(contactAddress.getLastname()).thenReturn("Lastname");
        when(paymentAddress.getCountry()).thenReturn(countryModel);
        when(countryModel.getIsocode()).thenReturn("CH");
        when(titleModel.getEvoCode()).thenReturn("Mr");
    }

    @Test
    public void testGetB2CPaymentParamsFallbackAddressIsNullFirstAndLastNameNotExistOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2C);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals("", billToCustomerData.getConsumer().getFirstName());
        assertEquals("", billToCustomerData.getConsumer().getLastName());
    }

    @Test
    public void testGetB2CPaymentParamsFirstAndLastNameExistOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2C);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(paymentAddress.getFirstname()).thenReturn("Name");
        when(paymentAddress.getLastname()).thenReturn("Surname");
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals("Name", billToCustomerData.getConsumer().getFirstName());
        assertEquals("Surname", billToCustomerData.getConsumer().getLastName());
    }

    @Test
    public void testGetB2CPaymentParamsFallbackFirstAndLastNameExistOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2C);
        when(customer.getContactAddress()).thenReturn(contactAddress);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals("Firstname", billToCustomerData.getConsumer().getFirstName());
        assertEquals("Lastname", billToCustomerData.getConsumer().getLastName());
    }

    @Test
    public void testGetB2CPaymentParamsSalutationExistsOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2C);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(paymentAddress.getTitle()).thenReturn(titleModel);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals("Mr", billToCustomerData.getConsumer().getSalutation());
    }

    @Test
    public void testGetB2CPaymentParamsSalutationIsNullOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2C);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertNull(billToCustomerData.getConsumer().getSalutation());
    }

    @Test
    public void testGetB2CPaymentParamsEmailAddressExistsOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2C);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals(CUSTOMER_EMAIL, billToCustomerData.getEmail());
    }

    @Test
    public void testGetB2BKeyAccountPaymentParamsLegalNameExistsOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B_KEY_ACCOUNT);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(paymentAddress.getCompany()).thenReturn(COMPANY_NAME);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals(COMPANY_NAME, billToCustomerData.getBusiness().getLegalName());
    }

    @Test
    public void testGetB2BKeyAccountPaymentParamsFallbackLegalNameExistsOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B_KEY_ACCOUNT);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals("UnitName", billToCustomerData.getBusiness().getLegalName());
    }

    @Test
    public void testGetB2BKeyAccountPaymentParamsEmailAddressExistsOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B_KEY_ACCOUNT);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(paymentAddress.getCompany()).thenReturn(COMPANY_NAME);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals(CUSTOMER_EMAIL, billToCustomerData.getEmail());
    }

    @Test
    public void testGetB2BPaymentParamsLegalNameExistsOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(paymentAddress.getCompany()).thenReturn(COMPANY_NAME);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals(COMPANY_NAME, billToCustomerData.getBusiness().getLegalName());
    }

    @Test
    public void testGetB2BPaymentParamsFallbackLegalNameExistsOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals("UnitName", billToCustomerData.getBusiness().getLegalName());
    }

    @Test
    public void testGetB2BPaymentParamsEmailAddressExistsOnBillToCustomerParam() throws IOException {
        // when
        when(customer.getCustomerType()).thenReturn(CustomerType.B2B);
        when(cart.getPaymentAddress()).thenReturn(paymentAddress);
        when(paymentAddress.getCompany()).thenReturn(COMPANY_NAME);
        String result = creditCardPaymentParamsStrategy.getPaymentParams("merchantId", cart);

        // then
        String billToCustomerValue = extractBillToCustomerValue(result);
        String decodedValue = decodeValue(billToCustomerValue);
        BillToCustomerData billToCustomerData = extractCustomerData(decodedValue);
        assertNotNull(billToCustomerData);
        assertEquals(CUSTOMER_EMAIL, billToCustomerData.getEmail());
    }

    private String extractBillToCustomerValue(String input) {
        String key = "billToCustomer=";
        return Arrays.stream(input.split("&"))
                     .filter(pair -> pair.startsWith(key))
                     .findFirst()
                     .map(pair -> pair.substring(key.length()))
                     .orElse("");
    }

    private String decodeValue(String encodedValue) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedValue.trim());
        return new String(decodedBytes);
    }

    private BillToCustomerData extractCustomerData(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, BillToCustomerData.class);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class BillToCustomerData {
        @JsonProperty("consumer")
        private Consumer consumer;

        @JsonProperty("business")
        private Business business;

        @JsonProperty("email")
        private String email;

        public Consumer getConsumer() {
            return consumer;
        }

        public Business getBusiness() {
            return business;
        }

        public String getEmail() {
            return email;
        }
    }

    private static class Consumer {
        @JsonProperty("firstName")
        private String firstName;

        @JsonProperty("lastName")
        private String lastName;

        @JsonProperty("salutation")
        private String salutation;

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getSalutation() {
            return salutation;
        }
    }

    private static class Business {
        @JsonProperty("legalName")
        private String legalName;

        public String getLegalName() {
            return legalName;
        }
    }
}
