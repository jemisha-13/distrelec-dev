/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.checkout.impl;

import com.namics.distrelec.b2b.core.inout.erp.impl.SapOrderCalculationService;
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
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.DeliveryModeService;
import de.hybris.platform.order.PaymentModeService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.daos.CurrencyDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.AddressService;
import de.hybris.platform.servicelayer.user.UserConstants;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.annotation.Resource;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Integration test suite for checkout payment process.
 */
@IntegrationTest
public class DistCheckoutPaymentIntegrationTest extends ServicelayerTransactionalTest {

    @Resource
    private ModelService modelService;

    @Resource
    private CartService cartService;

    @Resource
    private UserService userService;

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
    private ProductService productService;

    @Resource
    private CurrencyDao currencyDao;

    @Resource
    private DistCheckoutFacade b2bCheckoutFacade;

    @Resource
    private B2BOrderService b2bOrderService;

    @Resource
    private EnumerationService enumerationService;

    @Resource(name="sap.orderCalculationService" )
    private SapOrderCalculationService sapOrderCalculationService;

    private MockSIHybrisV1Out mockSIHybrisV1Out = new   MockSIHybrisV1Out();;

    @Before
    public void setUp() throws Exception {
        importCsv("/distrelecB2Bfacades/test/testDistCheckoutPayment.impex", "utf-8");
        sessionService.setAttribute("Europe1PriceFactory_UPG", enumerationService.getEnumerationValue(UserPriceGroup.class, "SalesOrg_UPG_7310_M01"));
        sapOrderCalculationService.setWebServiceClient(mockSIHybrisV1Out);

        final B2BCustomerModel b2bCustomer = (B2BCustomerModel) userService.getUserForUID("b2buser");
        final AddressModel address = addressService.getAddressesForOwner(b2bCustomer).iterator().next();
        final ProductModel product = productService.getProductForCode("5849020");

        // set current cms site
        final CMSSiteModel currentCMSSite = cmsSiteService.setCurrentSiteAndCatalogVersions("distrelec_CH", false);
        commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("CHF"));
        sessionService.setAttribute(Europe1Constants.PARAMS.UTG, currentCMSSite.getUserTaxGroup());
        cmsSiteService.setCurrentSite(cmsSiteService.getSites().iterator().next());

        final CartModel sampleCart = modelService.create(CartModel.class);
        sampleCart.setUser(b2bCustomer);
        sampleCart.setCurrency(currencyDao.findCurrenciesByCode("CHF").iterator().next());
        sampleCart.setDate(new Date());
        sampleCart.setTotalPrice(Double.valueOf(42));
        sampleCart.setCode("0000001");
        sampleCart.setErpOrderCode("0000002");
        sampleCart.setDeliveryMode(deliveryModeService.getDeliveryModeForCode("Express"));
        sampleCart.setPaymentMode(paymentModeService.getPaymentModeForCode("CreditCard"));
        sampleCart.setDeliveryAddress(address);
        sampleCart.setPaymentAddress(address);
        sampleCart.setSite(currentCMSSite);
        modelService.save(sampleCart);

        final CartEntryModel cartEntry = modelService.create(CartEntryModel.class);
        cartEntry.setProduct(product);
        cartEntry.setQuantity(Long.valueOf(1));
        cartEntry.setUnit(product.getUnit());
        cartEntry.setBasePrice(Double.valueOf(42));
        cartEntry.setTotalPrice(Double.valueOf(42));
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

    }

    @Ignore
    @Test
    public void testSuccessCreditCardPayment() throws Exception {
        // calculate cart
        b2bCheckoutFacade.calculateOrder(true);

        // simulate first request to DeuCS
        final Map<String, String> paymentParams = b2bCheckoutFacade.getPaymentParameters(DistSiteBaseUrlResolutionService.MobileUserAgent.NONE);
        Assert.assertTrue(MapUtils.isNotEmpty(paymentParams));
        Assert.assertTrue(hasCorrectPaymentTransactions(cartService.getSessionCart(), PaymentTransactionType.PAYMENT_REQUEST));

        final String orderCode = cartService.getSessionCart().getCode();
        // simulate notify request from DeuCS
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("{payId}", UUID.randomUUID().toString().replaceAll("-", ""));
        parameters.put("{transId}", cartService.getSessionCart().getUser().getPk().toString());
        parameters.put("{xId}", UUID.randomUUID().toString().replaceAll("-", ""));
        parameters.put("{code}", "00000000");
        parameters.put("{status}", "AUTHORIZED");
        parameters.put("{description}", "success");
        parameters.put("{refNr}", orderCode);
        parameters.put("{orderCode}", orderCode);
        parameters.put("{encodedMac}", getEncodedHashMac());

        final String encryptedDeuCsParams = getEncryptedDeuCsParams(parameters);
        final String requestBody = "Len=" + paymentParams.get("Len") + "&Data=" + encryptedDeuCsParams;
        // b2bCheckoutFacade.placeOrder();
        b2bCheckoutFacade.handlePaymentNotify(requestBody, getCurrentCartCurrencyCode());
        Assert.assertTrue(hasCorrectPaymentTransactions(cartService.getSessionCart(), PaymentTransactionType.NOTIFY));

        final Map<String, String> params = new HashMap<String, String>();
        params.put("Len", paymentParams.get("Len"));
        params.put("Data", encryptedDeuCsParams);
        b2bCheckoutFacade.handlePaymentSuccessFailure(params);
        // b2bCheckoutFacade.placeOrder();
        final OrderModel order = b2bOrderService.getOrderForCode("0000002");
        Assert.assertNotNull(order);
        Assert.assertTrue(hasCorrectPaymentTransactions(order, PaymentTransactionType.SUCCESS_RESPONSE));
    }

    @Ignore
    @Test
    public void testFailureCreditCardPayment() throws Exception {
        // calculate cart
        b2bCheckoutFacade.calculateOrder(true);

        // simulate first request to DeuCS
        final Map<String, String> paymentParams = b2bCheckoutFacade.getPaymentParameters(DistSiteBaseUrlResolutionService.MobileUserAgent.NONE);
        Assert.assertTrue(MapUtils.isNotEmpty(paymentParams));
        Assert.assertTrue(hasCorrectPaymentTransactions(cartService.getSessionCart(), PaymentTransactionType.PAYMENT_REQUEST));

        final String orderCode = cartService.getSessionCart().getCode();

        // simulate notify request from DeuCS
        final Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("{payId}", UUID.randomUUID().toString().replaceAll("-", ""));
        parameters.put("{transId}", cartService.getSessionCart().getUser().getPk().toString());
        parameters.put("{xId}", UUID.randomUUID().toString().replaceAll("-", ""));
        parameters.put("{code}", "40301003");
        parameters.put("{status}", "FAILED");
        parameters.put("{description}", "Strom bei Transaktion verloren");
        parameters.put("{refNr}", orderCode);
        parameters.put("{orderCode}", orderCode);
        parameters.put("{encodedMac}", getEncodedHashMac());

        final String encryptedDeuCsParams = getEncryptedDeuCsParams(parameters);
        final String requestBody = "Len=" + paymentParams.get("Len") + "&Data=" + encryptedDeuCsParams;
        b2bCheckoutFacade.handlePaymentNotify(requestBody, getCurrentCartCurrencyCode());
        Assert.assertTrue(hasCorrectPaymentTransactions(cartService.getSessionCart(), PaymentTransactionType.NOTIFY));

        final Map<String, String> params = new HashMap<String, String>();
        params.put("Len", paymentParams.get("Len"));
        params.put("Data", encryptedDeuCsParams);
        b2bCheckoutFacade.handlePaymentSuccessFailure(params);
        // In case of a Failure transaction a real order is never placed so a cart exists
        Assert.assertTrue(hasCorrectPaymentTransactions(cartService.getSessionCart(), PaymentTransactionType.FAILED_RESPONSE));
    }

    /* Helper method to create encrypt DeuCS parameters */
    private String getEncryptedDeuCsParams(final Map<String, String> parameters) {
        String data = "PayID={payId}&TransID={transId}&MID=pg_50702t&XID={xId}&Code={code}&Status={status}&Description={description}&Type=SSL&RefNr={refNr}&PCNr=0890767903239543&CCBrand=MasterCard&CCExpiry=201412&UserData=orderCode:{orderCode};RefNr:{refNr};Amount:644;Currency:CHF;PaymentModeCode:CreditCard&MAC={encodedMac}";
        for (final String paramKey : parameters.keySet()) {
            data = StringUtils.replace(data, paramKey, parameters.get(paramKey));
        }
        return DistCryptography.encryptString(data, getCurrentCartCurrencyCode() + DistCryptography.DATA_CRYPTION_KEY);
    }

    /**
     * @return current card currency
     */
    private String getCurrentCartCurrencyCode() {
        return getCartService().getSessionCart().getCurrency().getIsocode().toUpperCase();
    }

    /* Helper method to create encoded Hash-MAC */
    private String getEncodedHashMac() throws NoSuchAlgorithmException, InvalidKeyException {
        final CartModel cartModel = cartService.getSessionCart();
        final StringBuilder hmacValue = new StringBuilder();
        hmacValue.append("*").append(cartModel.getUser().getPk().toString()).append("*").append("pg_50702t").append("*")
                .append(DistUtils.getSmallestUnitOfCurrency(cartModel.getCurrency(), cartModel.getTotalPrice())).append("*")
                .append(cartModel.getCurrency().getIsocode());
        return DistCryptography.encodeString(hmacValue.toString(), getCurrentCartCurrencyCode() + DistCryptography.HMAC_KEY);
    }

    /* Helper method to check if correct payment transaction entry was created */
    private boolean hasCorrectPaymentTransactions(final AbstractOrderModel abstractOrderModel, final PaymentTransactionType type) {
        if (abstractOrderModel != null) {
            final List<PaymentTransactionModel> paymentTransactions = abstractOrderModel.getPaymentTransactions();
            for (final PaymentTransactionModel paymentTransaction : paymentTransactions) {
                final List<PaymentTransactionEntryModel> paymentTransactionEntries = paymentTransaction.getEntries();
                for (final PaymentTransactionEntryModel paymentTransactionEntry : paymentTransactionEntries) {
                    if (paymentTransactionEntry.getType().equals(type)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public EnumerationService getEnumerationService() {
        return enumerationService;
    }

    public void setEnumerationService(final EnumerationService enumerationService) {
        this.enumerationService = enumerationService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }
}
