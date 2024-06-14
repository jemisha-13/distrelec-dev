/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.strategies.impl.dibs;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.service.order.data.PaymentTransactionData;
import com.namics.distrelec.b2b.core.service.order.strategies.impl.AbstractDistPaymentParamStrategy;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import com.namics.distrelec.b2b.core.util.DistUtils;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

/**
 * implementation of payment strategy for DIBS payment provider
 * <p>
 * DIBS (http://www.dibspayment.com/)
 * </p>
 * 
 * @author rhaemmerli, Namics AG
 * 
 */

public class DibsPaymentParamStrategy extends AbstractDistPaymentParamStrategy {

    private static final Logger LOG = LogManager.getLogger(DibsPaymentParamStrategy.class);
    private static final String CREDITCARD = "CreditCard";

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Autowired
    private BaseSiteService baseSiteService;

    protected static final String PAYMENT_PROVIDER = "DIBS";

    // BIBS configuration keys
    protected static final String CFG_PAGE_SET = "payment.dibs.pageset";
    protected static final String CFG_METHOD = "payment.dibs.method.";
    protected static final String CFG_SHOPNAME = "payment.dibs.shopname";
    protected static final String CFG_MAC_FIELD = "payment.dibs.mac.key.field";

    // DIBS status codes
    public static final String DIBS_ACCEPTED = "A";
    protected static final String DIBS_DENIED = "D";
    protected static final String DIBS_UNKNOWN = "FF";

    // form files keys
    public static final String PARAM_STATUS = "status";
    protected static final String PARAM_RETURNURL = "returnUrl";
    protected static final String PARAM_DATA_KEY = "data";
    protected static final String PARAM_AUTH_CODE = "auhtCode";
    protected static final String PARAM_ORDER_CODE = "orderCode";
    protected static final String PARAM_DESCRIPTION = "replyText";
    protected static final String PARAM_CURRENCY = "currency";
    protected static final String PARAM_LANGUAGE = "lang";
    protected static final String PARAM_ORDERNO = "orderNo";
    protected static final String PARAM_CUSTOMERNO = "customerNo";
    protected static final String PARAM_PAGESET = "pageSet";
    protected static final String PARAM_AUTH_ONLY = "authOnly";
    protected static final String PARAM_METHOD = "method";
    protected static final String PARAM_MAC = "MAC";
    protected static final String PARAM_TOTAL = "totalsum";
    protected static final String PARAM_SHOP_NAME = "shopname";

    //
    protected static final String URL_PLACEHOLDER = "SHOP_NAME";

    @Autowired
    private CartService cartService;

    @Autowired
    private SiteConfigService siteConfigService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void handlePaymentSuccessFailureParams(final Map<String, String> paymentParams, final String encryptedString) {
        AbstractOrderModel cart = null;
        final String orderCode = paymentParams.get(PARAM_ORDER_CODE);
        if (StringUtils.isNotBlank(orderCode)) {
            cart = getAbstractOrderForCode(orderCode);
            LOG.debug("get cart from code [" + orderCode + "], cart [" + cart + "]");
        } else if (getCartService().hasSessionCart()) {
            cart = getCartService().getSessionCart();
            LOG.debug("get cart from session [" + orderCode + "]");
        }

        if (cart != null) {
            final PaymentTransactionData paymentTransactionData = createPaymentTransactionData(paymentParams, encryptedString);

            PaymentTransactionType type = StringUtils.isNotBlank(paymentParams.get(PARAM_AUTH_CODE)) && DIBS_ACCEPTED.equals(paymentParams.get(PARAM_STATUS))
                    ? PaymentTransactionType.SUCCESS_RESPONSE
                    : PaymentTransactionType.FAILED_RESPONSE;

            paymentTransactionData.setType(type);

            final PaymentTransactionModel paymentTransaction = getLatestPaymentTransaction(cart);
            createPaymentTransactionEntry(paymentTransaction, paymentTransactionData);
        } else {
            LOG.error("{} Could not handle payment response because cart is null [{}] ",ErrorLogCode.CART_CALCULATION_ERROR.getCode(),cart);
        }
    }

    @Override
    protected PaymentTransactionData createPaymentTransactionData(final Map<String, String> paymentParams, final String encryptedTransaction) {

        final PaymentTransactionData paymentTransaction = new PaymentTransactionData();

        paymentTransaction.setPaymentProvider(getPaymentProvider());

        paymentTransaction.setRefNr(paymentParams.get(PARAM_ORDER_CODE));
        paymentTransaction.setTransId(paymentParams.get(PARAM_AUTH_CODE));
        paymentTransaction.setStatus(paymentParams.get(PARAM_STATUS));
        paymentTransaction.setDescription(paymentParams.get(PARAM_DESCRIPTION));
        paymentTransaction.setEncryptedTransaction(paymentParams.toString());

        return paymentTransaction;
    }

    /**
     * check hash mac and paymnt data
     */
    @Override
    public void checkPaymentSuccessFailure(final Map<String, String> paymentParams) {

        LOG.debug("checkPaymentSuccessFailure check payment response params");

        if (StringUtils.isEmpty(paymentParams.get(PARAM_AUTH_CODE))) {
            throw new IllegalStateException("check success/failure failed PARAM_STATUS [" + paymentParams.get(PARAM_STATUS) + "], PARAM_AUTH_CODE ["
                    + paymentParams.get(PARAM_AUTH_CODE) + "] ");

        }

        final String authCode = paymentParams.get(DibsPaymentParamStrategy.PARAM_AUTH_CODE);
        final String orderCode = paymentParams.get(DibsPaymentParamStrategy.PARAM_ORDER_CODE);

        final String mac = paymentParams.get(PARAM_MAC);
        final String status = paymentParams.get(PARAM_STATUS);
        final String totalsum = paymentParams.get(PARAM_TOTAL);

        final CartModel cart = getCartService().getSessionCart();
        final String currency = cart.getCurrency().getIsocode();
        final String secretKey = DistCryptography.getDibsSecretKey();

        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(totalsum);
        sbuilder.append('&');
        sbuilder.append(currency);
        sbuilder.append('&');
        sbuilder.append(status);
        sbuilder.append('&');
        sbuilder.append(authCode);
        sbuilder.append('&');
        sbuilder.append(orderCode);
        sbuilder.append('&');
        sbuilder.append(secretKey);
        sbuilder.append('&');

        final String generatedMac = DistCryptography.toSHA1(sbuilder.toString());
        if (!generatedMac.equalsIgnoreCase(mac)) {

            throw new IllegalStateException("MAC in response is not correct. Check if you probably have wrong secret key configured.");
        }

        if (!validPaymentData(cart, totalsum)) {
            throw new IllegalStateException("Payment details not valid please verify data in cart and data confirmed by payment provider");
        }

        LOG.debug("checkPaymentSuccessFailure succesesfully checked");

    }

    /**
     * check if payment details confirmed by the payment provider are the same as the details on the cart
     * <p>
     * assure that the cart has not been changed during the payment process
     * </p>
     * 
     * @param cart
     *            hybris cart
     * @param totalsum
     *            payment total confirmed by the payment provider
     * @return true if the details are correct
     */

    protected boolean validPaymentData(CartModel cart, String totalsum) {

        try {

            // decimal format from DIBS is in the form "123456,12"
            DecimalFormat decimalFormat = new DecimalFormat("#0.#", DecimalFormatSymbols.getInstance(Locale.GERMANY));
            decimalFormat.setParseBigDecimal(true);

            BigDecimal paymentTotalDec = (BigDecimal) decimalFormat.parse(totalsum);

            String cartTotal = DistUtils.getSmallestUnitOfCurrency(cart.getCurrency(), cart.getTotalPrice());

            String paymentTotal = DistUtils.getSmallestUnitOfCurrency(cart.getCurrency(), paymentTotalDec.doubleValue());

            return cartTotal.equals(paymentTotal);

        } catch (ParseException e) {
            throw new IllegalStateException("Payment details not valid please verify data in cart and data confirmed by payment provider cart.getTotalPrice: ["
                    + cart.getTotalPrice() + "], totalsum [" + totalsum + "] ", e);
        }

    }

    @Override
    public Map<String, String> getAllPaymentParams(final String merchantId, final String paymentModeCode, final CartModel cartModel,
            final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        final Map<String, String> paymentParamsMap = new HashMap<String, String>();
        final CurrencyModel currency = cartModel.getCurrency();
        final String data = getOrderData(cartModel);

        final String paymentCode = cartModel.getPaymentMode().getPaymentInfoType().getCode().equals(CreditCardPaymentInfoModel._TYPECODE) ? CREDITCARD
                : cartModel.getPaymentMode().getCode();

        // get DIBS specific method from configuration
        paymentParamsMap.put(PARAM_METHOD, getSiteConfigService().getProperty(CFG_METHOD + paymentCode));

        final String macField = configurationService.getConfiguration().getString(CFG_MAC_FIELD);

        // only do the authorization, the settlement will be done by the SAP-ERP
        paymentParamsMap.put(PARAM_AUTH_ONLY, "true");

        final String secretKey = DistCryptography.getDibsSecretKey();

        paymentParamsMap.put(PARAM_DATA_KEY, data);
        paymentParamsMap.put(PARAM_CURRENCY, currency.getIsocode());

        paymentParamsMap.put(PARAM_LANGUAGE, getCurrentLanguageIsoCode());

        paymentParamsMap.put(PARAM_ORDERNO, cartModel.getCode());
        paymentParamsMap.put(PARAM_CUSTOMERNO, cartModel.getUser().getUid());

        String shopName = siteConfigService.getProperty(CFG_SHOPNAME);
        paymentParamsMap.put(PARAM_SHOP_NAME, shopName);

        String httpsSite = distSiteBaseUrlResolutionService.getWebsiteUrlForSite(baseSiteService.getCurrentBaseSite(), true, null);

        if (!DistSiteBaseUrlResolutionService.MobileUserAgent.NONE.equals(userAgent)) {
            // DISTRELEC-2505: add different redirect URLs for mobile
            httpsSite = distSiteBaseUrlResolutionService.getMobileUrlForSiteAndUseragent(baseSiteService.getCurrentBaseSite(), true, null, userAgent);
        }

        paymentParamsMap.put(PARAM_RETURNURL, httpsSite);

        // shipping cost will be included in total
        //
        // if (cartModel.getDeliveryCost() != null && cartModel.getDeliveryCost().doubleValue() > 0) {
        // paymentParamsMap.put("shipment", DistUtils.getSmallestUnitOfCurrency(currency, cartModel.getDeliveryCost()));
        // }

        final AddressModel billingAddress = cartModel.getPaymentAddress();
        paymentParamsMap.put("billingFirstName", billingAddress.getFirstname());
        paymentParamsMap.put("billingLastName", billingAddress.getLastname());
        paymentParamsMap.put("billingCompany", billingAddress.getCompany());
        paymentParamsMap.put("billingAddress", billingAddress.getLine1() + " " + billingAddress.getLine2());
        paymentParamsMap.put("billingCity", billingAddress.getTown());
        paymentParamsMap.put("billingZipCode", billingAddress.getPostalcode());
        paymentParamsMap.put("billingCountry", billingAddress.getCountry().getIsocode());
        paymentParamsMap.put("eMail", ((B2BCustomerModel) cartModel.getUser()).getEmail());

        paymentParamsMap.put(PARAM_PAGESET, getSiteConfigService().getProperty(CFG_PAGE_SET));

        final PaymentTransactionData transactionData = createPaymentTransactionData(merchantId, cartModel, generaterefId(cartModel),
                paymentParamsMap.toString());
        final PaymentTransactionModel paymentTransaction = createPaymentTransaction(transactionData);
        createPaymentTransactionEntry(paymentTransaction, transactionData);

        // build hash mac
        StringBuilder sbuilder = new StringBuilder();
        sbuilder.append(paymentParamsMap.get(PARAM_DATA_KEY));
        sbuilder.append('&');
        sbuilder.append(paymentParamsMap.get(PARAM_CURRENCY));
        sbuilder.append('&');
        sbuilder.append(paymentParamsMap.get(macField));
        sbuilder.append('&');
        sbuilder.append(secretKey);
        sbuilder.append('&');

        paymentParamsMap.put(PARAM_MAC, DistCryptography.toSHA1(sbuilder.toString()));

        return paymentParamsMap;
    }

    protected String getOrderData(final CartModel cart) {
        return getOrderData(cart, ':');
    }

    protected String getOrderData(final CartModel cart, final char separator) {
        // only send the cart total not the single entries
        // with cost for payment and shipping ,discounts and so on this is way easier

        StringBuilder orderData = new StringBuilder();

        orderData.append('0'); // position
        orderData.append(separator);
        orderData.append("order total"); // product name
        orderData.append(separator);
        orderData.append('1'); // quantity
        orderData.append(separator);
        orderData.append(DistUtils.getSmallestUnitOfCurrency(cart.getCurrency(), cart.getTotalPrice())); // total price
        orderData.append(separator);

        return orderData.toString();
    }

    @Override
    public String getPaymentProvider() {
        return PAYMENT_PROVIDER;
    }

    @Override
    public void checkPaymentNotify(final Map<String, String> paymentParams) {
        LOG.debug("No notify for payment provider " + PAYMENT_PROVIDER);
    }

    @Override
    public void handlePaymentNotifyParams(final Map<String, String> paymentParams, final Map<String, String> encryptedPaymentParamsMap) {
        LOG.debug("Nothing to do for payment provider DIBS on notify.");
    }

    @Override
    public CartService getCartService() {
        return cartService;
    }

    @Override
    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public SiteConfigService getSiteConfigService() {
        return siteConfigService;
    }

    public void setSiteConfigService(final SiteConfigService siteConfigService) {
        this.siteConfigService = siteConfigService;
    }

    /**
     * get language isoCode from the session
     * 
     * @return iso code from the current language
     */
    protected String getCurrentLanguageIsoCode() {
        return ((LanguageModel) getSessionService().getAttribute("language")).getIsocode();
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public DistSiteBaseUrlResolutionService getDistSiteBaseUrlResolutionService() {
        return distSiteBaseUrlResolutionService;
    }

    public void setDistSiteBaseUrlResolutionService(DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService) {
        this.distSiteBaseUrlResolutionService = distSiteBaseUrlResolutionService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    @Override
    public String adjustURL(String paymentMethodUrl) {

        // replace placeholder in url with shop name from configuration

        String shopName = siteConfigService.getProperty(CFG_SHOPNAME);
        String adjustedURL = paymentMethodUrl.replaceAll(URL_PLACEHOLDER, shopName);

        return adjustedURL;
    }

}
