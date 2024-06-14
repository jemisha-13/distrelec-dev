/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.checkout.impl;

import com.namics.distrelec.b2b.core.inout.erp.impl.SapOrderCalculationService;
import com.namics.distrelec.b2b.core.inout.erp.mock.MockSIHybrisIF11V1Out;
import com.namics.distrelec.b2b.core.inout.erp.mock.MockSIHybrisV1Out;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.order.checkout.DistCheckoutFacade;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.storesession.StoreSessionService;
import de.hybris.platform.core.Constants;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.AddressService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static com.namics.distrelec.b2b.core.service.order.strategies.impl.evopayment.CreditCardPaymentParamsStrategy.IS_3DS_VER2_ENABLED;
import static java.util.Map.entry;

/**
 * Integration test suite for {@link DefaultDistCheckoutFacade}
 */
@IntegrationTest
public class DefaultDistCheckoutFacadeTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private CartService cartService;

    @Resource
    private UserService userService;

    @Resource
    private ProductService productService;

    @Resource
    private PaymentModeService paymentModeService;

    @Resource
    private DeliveryModeService deliveryModeService;

    @Resource
    private CMSSiteService cmsSiteService;

    @Resource
    private SessionService sessionService;

    @Resource
    private CommonI18NService commonI18NService;

    @Resource
    private AddressService addressService;

    @Resource
    private CurrencyDao currencyDao;

    @Resource
    private DistCheckoutFacade b2bCheckoutFacade;

    @Resource
    private B2BOrderService b2bOrderService;

    @Resource
    private EnumerationService enumerationService;

    @Resource
    private ConfigurationService configurationService;

    @Resource
    private StoreSessionService storeSessionService;

    @Resource(name = "sap.orderCalculationService")
    private SapOrderCalculationService sapOrderCalculationService;

    private MockSIHybrisV1Out mockSIHybrisV1Out = new MockSIHybrisV1Out();

    private MockSIHybrisIF11V1Out mockSIHybrisIF11V1Out = new MockSIHybrisIF11V1Out();

    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bfacades/test/testDistCheckoutFacade.impex", "utf-8");
        sessionService.setAttribute("Europe1PriceFactory_UPG", enumerationService.getEnumerationValue(UserPriceGroup.class, "SalesOrg_UPG_7310_M01"));
        sapOrderCalculationService.setWebServiceClient(mockSIHybrisV1Out);
        sapOrderCalculationService.setWebServiceClientIF11(mockSIHybrisIF11V1Out);

        final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_CH", false);
        commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("CHF"));
        storeSessionService.setCurrentLanguage("en");
        sessionService.setAttribute(Europe1Constants.PARAMS.UTG, currentCMSSite.getUserTaxGroup());
        cmsSiteService.setCurrentSite(cmsSiteService.getSites().iterator().next());

        final B2BCustomerModel b2bCustomer = (B2BCustomerModel) userService.getUserForUID("b2buser");
        final AddressModel address = addressService.getAddressesForOwner(b2bCustomer).iterator().next();
        final ProductModel product = productService.getProductForCode("5849020");

        final CartModel sampleCart = modelService.create(CartModel.class);
        sampleCart.setUser(b2bCustomer);
        sampleCart.setCurrency(currencyDao.findCurrenciesByCode("CHF").iterator().next());
        sampleCart.setDate(new Date());
        sampleCart.setTotalPrice(42.0);
        sampleCart.setCode("0000001");
        sampleCart.setErpOrderCode("0000002");
        sampleCart.setDeliveryMode(deliveryModeService.getDeliveryModeForCode("Express"));
        sampleCart.setPaymentMode(paymentModeService.getPaymentModeForCode("CreditCard"));
        sampleCart.setDeliveryAddress(address);
        sampleCart.setPaymentAddress(address);
        sampleCart.setSite(currentCMSSite);
        sampleCart.setOrderReference("DefaultDistCheckoutFacadeTest");
        sampleCart.setLanguage(commonI18NService.getCurrentLanguage());
        modelService.save(sampleCart);

        final CartEntryModel cartEntry = modelService.create(CartEntryModel.class);
        cartEntry.setProduct(product);
        cartEntry.setQuantity(1L);
        cartEntry.setUnit(product.getUnit());
        cartEntry.setBasePrice(42.0);
        cartEntry.setTotalPrice(42.0);
        cartEntry.setOrder(sampleCart);
        modelService.save(cartEntry);

        final PaymentTransactionModel paymentTransaction = modelService.create(PaymentTransactionModel.class);
        paymentTransaction.setCode(b2bCustomer.getUid() + "." + sampleCart.getCode() + "." + UUID.randomUUID().toString().replaceAll("-", ""));
        paymentTransaction.setInfo(sampleCart.getPaymentInfo());
        paymentTransaction.setCurrency(sampleCart.getCurrency());
        paymentTransaction.setOrder(sampleCart);
        modelService.save(paymentTransaction);

        cartService.setSessionCart(sampleCart);
        sessionService.setAttribute(UserConstants.USER_SESSION_ATTR_KEY, b2bCustomer);

        JaloSession jaloSession = JaloSession.getCurrentSession();
        HttpSession httpSession = new MockHttpSession();
        httpSession.setAttribute(Constants.WEB.JALOSESSION, JaloSession.getCurrentSession());
        jaloSession.setHttpSessionId(httpSession.getId());
    }

    @Test
    public void testGetParameterizedPaymentUrl() {
        final String parameterizedPaymentUrl = b2bCheckoutFacade.getParameterizedPaymentUrl(DistSiteBaseUrlResolutionService.MobileUserAgent.NONE);
        Assert.assertNotNull(parameterizedPaymentUrl);
    }

    @Test
    public void testGetPaymentParameters() {
        final Map<String, String> paymentParameters = b2bCheckoutFacade.getPaymentParameters(DistSiteBaseUrlResolutionService.MobileUserAgent.NONE);
        Assert.assertTrue(MapUtils.isNotEmpty(paymentParameters));
    }

    @Test
    public void testHandleNotifySuccess() throws CalculationException {
        //given
        final String plainText = "PayID=a2487c19a50f489ca3f0de3edbe52442&TransID=8796158623748&MID=pg_50702t&XID=7bfecb81f6d64ef0a2afcbc1cb294676&Code=00000000&Status=AUTHORIZED&Description=success&Type=SSL&RefNr=0000002&PCNr=0890767903239543&CCBrand=MasterCard&CCExpiry=201412&UserData=UmVmTnI6MDAwMDAwMjtBbW91bnQ6NjQ0O0N1cnJlbmN5OkNIRjtQYXltZW50TW9kZUNvZGU6Q3JlZGl0Q2FyZDtvcmRlckNvZGU6MDAwMDAwMg==&MAC=9845b3d429dd38c2147c1aa509ae2c6ebd1f8e1a85d34a24c8322dda1dbb496c";
        final String encodedString = DistCryptography.encryptString(plainText, getCurrentCartCurrencyCode() + DistCryptography.DATA_CRYPTION_KEY);
        b2bCheckoutFacade.calculateOrder(true);

        //when
        b2bCheckoutFacade.handlePaymentNotify("Len=" + DistUtils.countUTF8Length(plainText) + "&Data=" + encodedString, getCurrentCartCurrencyCode());

        //then
        assertThatItHasCorrectPaymentTransactions(cartService.getSessionCart(), PaymentTransactionType.NOTIFY);
    }

    @Test
    public void testHandlePaymentSuccess() throws InvalidCartException, CalculationException {
        //given
        final String plainText = "PayID=a2487c19a50f489ca3f0de3edbe52442&TransID=8796158623748&MID=pg_50702t&XID=7bfecb81f6d64ef0a2afcbc1cb294676&Code=00000000&Status=AUTHORIZED&Description=success&Type=SSL&RefNr=0000002&PCNr=0890767903239543&CCBrand=MasterCard&CCExpiry=201412&UserData=UmVmTnI6MDAwMDAwMjtBbW91bnQ6NjQ0O0N1cnJlbmN5OkNIRjtQYXltZW50TW9kZUNvZGU6Q3JlZGl0Q2FyZA==&MAC=9845b3d429dd38c2147c1aa509ae2c6ebd1f8e1a85d34a24c8322dda1dbb496c";
        final String encodedString = DistCryptography.encryptString(plainText, getCurrentCartCurrencyCode() + DistCryptography.DATA_CRYPTION_KEY);
        configurationService.getConfiguration().setProperty(IS_3DS_VER2_ENABLED, "false");
        b2bCheckoutFacade.calculateOrder(true);
        final Map<String, String> paramMap = Map.ofEntries(entry("Len", String.valueOf(plainText.length())), entry("Data", encodedString));

        //when
        final String returnCode = b2bCheckoutFacade.handlePaymentSuccessFailure(paramMap);

        //then
        Assert.assertEquals("0000002", returnCode);
        final OrderModel order = b2bOrderService.getOrderForCode("0000002");
        assertThatItHasCorrectPaymentTransactions(order, PaymentTransactionType.SUCCESS_RESPONSE);
    }

    @Test
    public void testHandlePaymentSuccess_3DS_2() throws InvalidCartException, CalculationException {
        //given
        final String plainText = "PayID=a2487c19a50f489ca3f0de3edbe52442&TransID=8796158623748&MID=pg_50702t&XID=7bfecb81f6d64ef0a2afcbc1cb294676&Code=00000000&Status=AUTHORIZED&Description=success&Type=SSL&RefNr=0000002&PCNr=0890767903239543&CCBrand=MasterCard&CCExpiry=201412&UserData=UmVmTnI6MDAwMDAwMjtBbW91bnQ6NjQ0O0N1cnJlbmN5OkNIRjtQYXltZW50TW9kZUNvZGU6Q3JlZGl0Q2FyZA==&MAC=9845b3d429dd38c2147c1aa509ae2c6ebd1f8e1a85d34a24c8322dda1dbb496c&Card=eyJjYXJkaG9sZGVyTmFtZSI6IlBlcm8iLCJudW1iZXIiOiIwMDIyNTg5NTAwMTYzMTExIiwiZXhwaXJ5RGF0ZSI6IjIwMjIxMCIsImJpbiI6eyJhY2NvdW50QklOIjoiNDExMTExIn0sImJyYW5kIjoiVklTQSIsInByb2R1Y3QiOiIiLCJzb3VyY2UiOiJDUkVESVQiLCJ0eXBlIjoiIiwiY291bnRyeSI6eyJjb3VudHJ5TmFtZSI6IlVuaXRlZCBTdGF0ZXMgb2YgQW1lcmljYSIsImNvdW50cnlBMiI6IlVTIiwiY291bnRyeUEzIjoiVVNBIiwiY291bnRyeU51bWJlciI6Ijg0MCJ9LCJpc3N1ZXIiOiJKUE1PUkdBTiBDSEFTRSBCQU5LLCBOLkEuIn0=";
        final String encodedString = DistCryptography.encryptString(plainText, getCurrentCartCurrencyCode() + DistCryptography.DATA_CRYPTION_KEY);
        b2bCheckoutFacade.calculateOrder(true);
        final Map<String, String> paramMap = Map.ofEntries(entry("Len", String.valueOf(plainText.length())), entry("Data", encodedString));

        //when
        final String returnCode = b2bCheckoutFacade.handlePaymentSuccessFailure(paramMap);

        //then
        Assert.assertEquals("0000002", returnCode);
        final OrderModel order = b2bOrderService.getOrderForCode("0000002");
        assertThatItHasCorrectPaymentTransactions(order, PaymentTransactionType.SUCCESS_RESPONSE);
    }

    @Test
    public void testHandleNotifyFailure() throws CalculationException {
        //given
        final String plainText = "PayID=a2487c19a50f489ca3f0de3edbe52442&TransID=8796158623748&MID=pg_50702t&XID=7bfecb81f6d64ef0a2afcbc1cb294676&Code=40301003&Status=FAILED&Description=Strom verloren bei Transaktion&Type=SSL&RefNr=0000002&PCNr=0890767903239543&CCBrand=MasterCard&CCExpiry=201412&UserData=UmVmTnI6MDAwMDAwMjtBbW91bnQ6NjQ0O0N1cnJlbmN5OkNIRjtQYXltZW50TW9kZUNvZGU6Q3JlZGl0Q2FyZA==&MAC=9845b3d429dd38c2147c1aa509ae2c6ebd1f8e1a85d34a24c8322dda1dbb496c";
        final String encodedString = DistCryptography.encryptString(plainText, getCurrentCartCurrencyCode() + DistCryptography.DATA_CRYPTION_KEY);
        b2bCheckoutFacade.calculateOrder(true);

        //when
        b2bCheckoutFacade.handlePaymentNotify("Len=" + DistUtils.countUTF8Length(plainText) + "&Data=" + encodedString, getCurrentCartCurrencyCode());

        //then
        assertThatItHasCorrectPaymentTransactions(cartService.getSessionCart(), PaymentTransactionType.NOTIFY);
    }

    @Test
    public void testHandlePaymentFailure() throws CalculationException, InvalidCartException {
        //given
        final String plainText = "PayID=a2487c19a50f489ca3f0de3edbe52442&TransID=8796158623748&MID=pg_50702t&XID=7bfecb81f6d64ef0a2afcbc1cb294676&Code=40301003&Status=FAILED&Description=Strom verloren bei Transaktion&Type=SSL&RefNr=0000002&PCNr=0890767903239543&CCBrand=MasterCard&CCExpiry=201412&UserData=UmVmTnI6MDAwMDAwMjtBbW91bnQ6NjQ0O0N1cnJlbmN5OkNIRjtQYXltZW50TW9kZUNvZGU6Q3JlZGl0Q2FyZA==&MAC=9845b3d429dd38c2147c1aa509ae2c6ebd1f8e1a85d34a24c8322dda1dbb496c";
        final String encodedString = DistCryptography.encryptString(plainText, getCurrentCartCurrencyCode() + DistCryptography.DATA_CRYPTION_KEY);
        b2bCheckoutFacade.calculateOrder(true);
        final Map<String, String> paramMap = Map.ofEntries(entry("Len", String.valueOf(plainText.length())), entry("Data", encodedString));

        //when
        final String returnCode = b2bCheckoutFacade.handlePaymentSuccessFailure(paramMap);


        //then
        Assert.assertEquals("40301003", returnCode);
        assertThatItHasCorrectPaymentTransactions(cartService.getSessionCart(), PaymentTransactionType.FAILED_RESPONSE);
    }

    @Test
    public void testHandleInvoicePayment() throws CalculationException, InvalidCartException {
        //given
        final CartModel cart = cartService.getSessionCart();
        cart.setPaymentMode(paymentModeService.getPaymentModeForCode("Z001_Invoice"));
        cart.setErpOrderCode("0000003");
        cart.setPaymentTransactions(null);
        modelService.save(cart);
        b2bCheckoutFacade.calculateOrder(true);

        //when
        final OrderData orderData = b2bCheckoutFacade.placeOrder();

        //then
        Assert.assertNotNull(orderData);
        final OrderModel order = b2bOrderService.getOrderForCode(orderData.getCode());
        Assert.assertTrue(CollectionUtils.isEmpty(order.getPaymentTransactions()));
    }

    private void assertThatItHasCorrectPaymentTransactions(final AbstractOrderModel abstractOrderModel, final PaymentTransactionType type) {
        Assert.assertNotNull(abstractOrderModel);
        Assert.assertTrue(abstractOrderModel.getPaymentTransactions()
                .stream()
                .flatMap(paymentTransactionModel -> paymentTransactionModel.getEntries().stream())
                .anyMatch(entry -> type.equals(entry.getType())));
    }

    private String getCurrentCartCurrencyCode() {
        return cartService.getSessionCart().getCurrency().getIsocode().toUpperCase();
    }
}
