/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.strategies.impl.evopayment;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Headless.HEADLESS_CART;
import static com.namics.distrelec.b2b.core.service.order.util.PostCodeToStateMapUtil.getStateAbv;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Base64Utils;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.inout.erp.PaymentOptionService;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.model.payment.HopSupportingPaymentTransactionEntryModel;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.order.data.PaymentTransactionData;
import com.namics.distrelec.b2b.core.service.order.strategies.DistPaymentParamsStrategy;
import com.namics.distrelec.b2b.core.service.order.strategies.impl.AbstractDistPaymentParamStrategy;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.util.CountryCode;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import com.namics.distrelec.b2b.core.util.DistUtils;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

/**
 * Implementation of {@link DistPaymentParamsStrategy} for evopayment
 * <p>
 * http://evopayments.com/
 * </p>
 *
 * @author pbueschi, Namics AG
 */
public class EvoDistPaymentParamsStrategy extends AbstractDistPaymentParamStrategy {

    private static final String CURRENCY_CODE = "{currency}";

    private static final Logger LOG = LoggerFactory.getLogger(EvoDistPaymentParamsStrategy.class);

    protected static final String ENCRYPTED_RESPONSE = "encrypt";

    protected static final String PAYMENT_PROVIDER = "DeuCS";

    private static final String CAPTURE_CONFIG_KEY = ".payment.capture";

    private static final String NOTIFY_URL = "/checkout/payment/notify/" + CURRENCY_CODE;

    private static final String HEADLESS_NOTIFY_URL = "/%s/checkout/payment/notify?currency=%s";

    public static final String JUMPOUT_SUCCESS_PAGE_URL = "/checkout/payment/jumpout/success";

    public static final String JUMPOUT_FAILURE_PAGE_URL = "/checkout/payment/jumpout/failure";

    public static final String SUCCESS_PAGE_URL = "/checkout/payment/success";

    public static final String FAILURE_PAGE_URL = "/checkout/payment/failure";

    public static final String ORDER_CODE = "orderCode";

    public static final String ORDER_DESC_SANDBOX = "Test:0000";

    private String captureMethod;

    @Autowired
    private BaseStoreService baseStoreService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    @Qualifier("erp.paymentOptionService")
    private PaymentOptionService paymentOptionService;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    /**
     * @param merchantId
     *            unused
     * @param cartModel
     *            unused
     */
    public String getPaymentParams(final String merchantId, final CartModel cartModel) {
        return StringUtils.EMPTY;
    }

    /**
     * @param merchantId
     *            unused
     * @param cartModel
     * @param refId
     */
    protected StringBuilder getDefaultPaymentParams(final String merchantId, final CartModel cartModel, final String refId) {
        // define payment parameters
        final StringBuilder paymentParams = new StringBuilder();
        paymentParams.append("MerchantID=").append(merchantId);
        paymentParams.append("&TransID=").append(cartModel.getUser().getPk().toString());
        paymentParams.append("&RefNr=").append(refId);
        paymentParams.append("&Amount=").append(DistUtils.getSmallestUnitOfCurrency(cartModel.getCurrency(), cartModel.getTotalPrice()));
        paymentParams.append("&Currency=").append(cartModel.getCurrency().getIsocode());
        paymentParams.append("&UserData=").append(getUserDataForPaymentRequest(cartModel));

        return paymentParams;
    }

    // DISTRELEC-11237 : true for ideal implementation only
    protected boolean appendRefIdIntoOrderDesc() {
        return false;
    }

    /**
     * Override this method in the specific payment implementation so that you can test it without money flow for example in case of paypal
     * payment
     *
     * @return {@code false}
     */
    protected boolean isTestOrder() {
        return false;
    }

    @Override
    public void handlePaymentNotifyParams(final Map<String, String> paymentParams, final Map<String, String> encryptedPaymentParamsMap) {
        throw new UnsupportedOperationException("Must be implemented in dedicated strategy");
    }

    @Override
    public void handlePaymentSuccessFailureParams(final Map<String, String> paymentParams, final String encryptedString) {
        // default implementation if payment provider doesn't deliver RefNr which is mandatory to get the current payment method!
        if (getCartService().getSessionCart() != null) {
            final PaymentTransactionData paymentTransactionData = createPaymentTransactionData(paymentParams, encryptedString);
            paymentTransactionData
                                  .setType(paymentParams.get("Status").equals("FAILED") ? PaymentTransactionType.FAILED_RESPONSE
                                                                                        : PaymentTransactionType.SUCCESS_RESPONSE);
            final PaymentTransactionModel paymentTransaction = getLatestPaymentTransaction(getCartService().getSessionCart());
            createPaymentTransactionEntry(paymentTransaction, paymentTransactionData);
        }
    }

    @Override
    public void checkPaymentNotify(final Map<String, String> paymentParams) {
        final boolean paymentParamsCorrect = checkPaymentParamsForAbstractOrder(paymentParams, "Check of URLNotify failed");
        if (!paymentParamsCorrect) {
            throw new IllegalStateException("check notify failed");
        }
    }

    @Override
    public void checkPaymentSuccessFailure(final Map<String, String> paymentParams) {
        final boolean paymentParamsCorrect = checkPaymentParamsForAbstractOrder(paymentParams, "Check of URLSuccess/URLFailure failed");
        if (!paymentParamsCorrect) {
            throw new IllegalStateException("check success/failure failed");
        }
    }

    @Override
    public Map<String, String> getAllPaymentParams(final String merchantId, final String paymentModeCode, final CartModel cartModel,
                                                   final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        final Map<String, String> paymentParameters = new HashMap<>();
        // encrypt payment params
        final Map<Integer, String> encryptedPaymentParams = getEncryptedPaymentParams(merchantId, cartModel, userAgent);
        paymentParameters.put("Len", String.valueOf(encryptedPaymentParams.keySet().iterator().next()));
        paymentParameters.put("Data", encryptedPaymentParams.values().iterator().next());

        // additional values for predefined credit card
        if (cartModel.getPaymentInfo() != null && cartModel.getPaymentInfo().isSaved()) {
            final CreditCardPaymentInfoModel ccPaymentInfo = (CreditCardPaymentInfoModel) cartModel.getPaymentInfo();
            final String deuCSPaymentModeCode = getPaymentOptionService()
                                                                         .getDeuCSCodeForCreditCardTypeCode((AbstractDistPaymentModeModel) cartModel.getPaymentMode(),
                                                                                                            ccPaymentInfo.getType().getCode());
            paymentParameters.put("CCSelect", deuCSPaymentModeCode);
            paymentParameters.put("PCNr", ccPaymentInfo.getNumber());
            paymentParameters.put("PCNRBrand", deuCSPaymentModeCode);
            paymentParameters.put("PCNRYear", ccPaymentInfo.getValidToYear());
            paymentParameters.put("PCNRMonth", ccPaymentInfo.getValidToMonth());
            paymentParameters.put("Creditcardholder", ccPaymentInfo.getCcOwner());
        }
        final String paymentRequestString = "Len=" + paymentParameters.get("Len") + "&Data=" + paymentParameters.get("Data");
        LOG.info("Starting Payment Request For Cart ID: {}", cartModel.getCode());
        LOG.info("User ID: {}", (cartModel.getUser() != null ? cartModel.getUser().getUid() : "Anonymous"));
        LOG.info("Payment-Request: {}", paymentRequestString);
        return paymentParameters;
    }

    private Map<Integer, String> getEncryptedPaymentParams(final String merchantId, final CartModel cartModel,
                                                           final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        final Map<Integer, String> encryptedPaymentParams = new HashMap<>();
        final String paymentParamsForCart = getPaymentParams(merchantId, cartModel);
        final String extendedPaymentParams = getExtendedPaymentParams(paymentParamsForCart, merchantId, cartModel, userAgent);
        encryptedPaymentParams.put(DistUtils.countUTF8Length(extendedPaymentParams),
                                   DistCryptography.encryptString(extendedPaymentParams, getCurrentCartCurrencyCode() + DistCryptography.DATA_CRYPTION_KEY));
        return encryptedPaymentParams;
    }

    protected String getExtendedPaymentParams(final String paymentParams, final String merchantId, final CartModel cartModel,
                                              final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        BaseSiteModel baseSite = baseSiteService.getCurrentBaseSite();
        final StringBuilder extendedPaymentParams = new StringBuilder(paymentParams);

        if (BooleanUtils.isTrue(getSessionService().getAttribute(HEADLESS_CART))) {
            String httpsSite = distSiteBaseUrlResolutionService.getHeadlessWebsiteUrlForSite(baseSite, true, null);
            extendedPaymentParams.append("&URLSuccess=").append(httpsSite).append(SUCCESS_PAGE_URL);
            extendedPaymentParams.append("&URLFailure=").append(httpsSite).append(FAILURE_PAGE_URL);
            extendedPaymentParams.append("&URLNotify=").append(distSiteBaseUrlResolutionService.getApiWebsiteUrlForSite(baseSite, true, "/rest/v2"))
                                 .append(getNotifyUrl(baseSite));
        } else {
            String httpsSite = getDistSiteBaseUrlResolutionService().getStorefrontWebsiteUrlForSite(baseSite, true, null);
            // Commented to fix DISTRELEC-16011
            if (!DistSiteBaseUrlResolutionService.MobileUserAgent.NONE.equals(userAgent)) {
                // DISTRELEC-2505: add different redirect URLs for mobile
                httpsSite = distSiteBaseUrlResolutionService.getMobileUrlForSiteAndUseragent(baseSite, true, null, userAgent);
            }
            extendedPaymentParams.append("&URLSuccess=").append(httpsSite).append(JUMPOUT_SUCCESS_PAGE_URL);
            extendedPaymentParams.append("&URLFailure=").append(httpsSite).append(JUMPOUT_FAILURE_PAGE_URL);
            extendedPaymentParams.append("&URLNotify=").append(httpsSite).append(getNotifyUrl(baseSite));
        }
        extendedPaymentParams.append("&MAC=").append(getEncodedHmacValue(merchantId, cartModel));
        extendedPaymentParams.append("&Custom=").append(DistConstants.Session.SID).append("=").append(getEncryptedSessionId())
                             .append("|").append(DistConstants.Session.LN).append("=").append(getEncryptedSessionId().length());
        return extendedPaymentParams.toString();
    }

    private String getEncryptedSessionId() {
        // HDLS-1025 - Empty check added / HttpSessionId is empty for headless
        if (StringUtils.isNotEmpty(JaloSession.getCurrentSession().getHttpSessionId())) {
            return DistCryptography.encryptString(JaloSession.getCurrentSession().getHttpSessionId(), DistCryptography.WEB_SESSION_KEY);
        } else {
            return DistCryptography.encryptString(JaloSession.getCurrentSession().getSessionID(), DistCryptography.WEB_SESSION_KEY);
        }
    }

    private String getEncodedHmacValue(final String merchantId, final CartModel cartModel) {
        final StringBuilder hmacValue = new StringBuilder();
        hmacValue.append("*").append(cartModel.getUser().getPk().toString()).append("*").append(merchantId).append("*")
                 .append(DistUtils.getSmallestUnitOfCurrency(cartModel.getCurrency(), cartModel.getTotalPrice())).append("*")
                 .append(cartModel.getCurrency().getIsocode());
        return DistCryptography.encodeString(hmacValue.toString(), getCurrentCartCurrencyCode() + DistCryptography.HMAC_KEY);
    }

    /* Checks if payment parameters conforms with current cart/order data */
    protected boolean checkPaymentParamsForAbstractOrder(final Map<String, String> paymentParams, final String message) {
        final Map<String, String> userDataMap = DistUtils.getMapFromString(new String(Base64.getDecoder().decode(paymentParams.get("UserData"))), ";", ":");

        final AbstractOrderModel abstractOrderModel = getAbstractOrderForCode(userDataMap.get(ORDER_CODE));

        if (abstractOrderModel == null) {
            LOG.error("{} {} No abstractOrder found for code: {}", ErrorLogCode.ORDER_RELATED_ERROR, ErrorSource.HYBRIS, userDataMap.get(ORDER_CODE));
            return false;
        }

        if (!abstractOrderModel.getCurrency().getIsocode().equals(userDataMap.get("Currency"))
                || !DistUtils.getSmallestUnitOfCurrency(abstractOrderModel.getCurrency(), abstractOrderModel.getTotalPrice()).equals(userDataMap.get("Amount"))
                || !abstractOrderModel.getPaymentMode().getCode().equals(userDataMap.get("PaymentModeCode"))) {

            if (!abstractOrderModel.getCurrency().getIsocode().equals(userDataMap.get("Currency"))) {
                LOG.error("{} Currency does not match for order : {}, userDataMap: {}", ErrorLogCode.PAYMENT_ERROR.getCode(), abstractOrderModel.getCode(),
                          userDataMap);
            }
            if (!DistUtils.getSmallestUnitOfCurrency(abstractOrderModel.getCurrency(), abstractOrderModel.getTotalPrice()).equals(userDataMap.get("Amount"))) {
                LOG.error(ErrorLogCode.PAYMENT_ERROR.getCode() + "{} Amount does not match for order : {}, userDataMap: {}",
                          ErrorLogCode.PAYMENT_ERROR.getCode(), abstractOrderModel.getCode(), userDataMap);
            }
            if (!abstractOrderModel.getPaymentMode().getCode().equals(userDataMap.get("PaymentModeCode"))) {
                LOG.error(ErrorLogCode.PAYMENT_ERROR.getCode() + "{} Payment mode does not match for order : {}, userDataMap: {}",
                          ErrorLogCode.PAYMENT_ERROR.getCode(), abstractOrderModel.getCode(), userDataMap);
            }

            final PaymentTransactionModel paymentTransaction = getLatestPaymentTransaction(abstractOrderModel);
            paymentTransaction.setCheckFailedMessage(message);
            getModelService().save(abstractOrderModel);
            return false;
        }
        return true;
    }

    /* Checks if payment parameters conforms with received payment parameters from notify */
    protected boolean checkPaymentParamsAgainstNotify(final Map<String, String> paymentParams, final String message) {
        final Map<String, String> userDataMap = DistUtils.getMapFromString(paymentParams.get("UserData"), ";", ":");

        final String orderCode = userDataMap.get(ORDER_CODE);
        final AbstractOrderModel abstractOrderModel = getAbstractOrderForCode(orderCode);

        if (abstractOrderModel == null) {
            return false;
        }
        final PaymentTransactionModel paymentTransaction = getLatestPaymentTransaction(abstractOrderModel);
        if (paymentTransaction == null) {
            return false;
        }
        final List<PaymentTransactionEntryModel> entries = paymentTransaction.getEntries();
        if (CollectionUtils.isEmpty(entries)) {
            return false;
        }
        for (final PaymentTransactionEntryModel entry : entries) {
            if (PaymentTransactionType.NOTIFY.equals(entry.getType())) {
                final HopSupportingPaymentTransactionEntryModel distEntry = (HopSupportingPaymentTransactionEntryModel) entry;
                if (distEntry.getAmount().equals(DistUtils.getBiggestUnitOfCurrency(distEntry.getCurrency(), new BigDecimal(userDataMap.get("Amount"))))
                        && distEntry.getCurrency().getIsocode().equals(userDataMap.get("Currency"))
                        && distEntry.getErrorCode().equals(paymentParams.get("Code"))) {
                    return true;
                }
            }
        }
        paymentTransaction.setCheckFailedMessage(message);
        getModelService().save(paymentTransaction);
        return false;
    }

    @Override
    protected PaymentTransactionData createPaymentTransactionData(final String merchantId, final CartModel cartModel, final String refId,
                                                                  String paymentParams) {
        final PaymentTransactionData paymentTransactionData = super.createPaymentTransactionData(merchantId, cartModel, refId, paymentParams);
        paymentTransactionData.setUserData(getUserDataForPaymentRequest(cartModel));
        return paymentTransactionData;
    }

    @Override
    protected PaymentTransactionData createPaymentTransactionData(final Map<String, String> paymentParams, final String encryptedTransaction) {
        final PaymentTransactionData paymentTransactionData = new PaymentTransactionData();
        paymentTransactionData.setMerchantId(paymentParams.get("MID"));
        paymentTransactionData.setRefNr(paymentParams.get("RefNr"));
        paymentTransactionData.setPayId(paymentParams.get("PayID"));
        paymentTransactionData.setxId(paymentParams.get("XID"));
        paymentTransactionData.setTransId(paymentParams.get("TransID"));
        paymentTransactionData.setStatus(paymentParams.get("Status"));
        paymentTransactionData.setDescription(paymentParams.get("Description"));
        paymentTransactionData.setErrorCode(paymentParams.get("Code"));
        paymentTransactionData.setUserData(new String(Base64Utils.decodeFromString(paymentParams.get("UserData"))));
        paymentTransactionData.setPaymentProvider(getPaymentProvider());

        setUserDataToPaymentTransaction(paymentTransactionData, paymentParams.get("UserData"));

        paymentTransactionData.setEncryptedTransaction(encryptedTransaction);

        return paymentTransactionData;
    }

    @Override
    protected String getPaymentProvider() {
        return PAYMENT_PROVIDER;
    }

    /* Gets values for payment provider attribute "UserData" */
    protected String getUserDataForPaymentRequest(final CartModel cartModel) {
        final StringBuilder userData = new StringBuilder();
        String orderCode;
        if (checkoutCustomerStrategy.isAnonymousCheckout()) {
            orderCode = cartModel.getGuid();
        } else {
            orderCode = StringUtils.isNotBlank(cartModel.getErpOrderCode()) ? cartModel.getErpOrderCode() : cartModel.getCode();
        }
        userData.append(ORDER_CODE + ":").append(orderCode);
        userData.append(";Amount:").append(DistUtils.getSmallestUnitOfCurrency(cartModel.getCurrency(), cartModel.getTotalPrice()));
        userData.append(";Currency:").append(cartModel.getCurrency().getIsocode());
        userData.append(";PaymentModeCode:").append(cartModel.getPaymentMode().getCode());
        return Base64Utils.encodeToString(userData.toString().getBytes());
    }

    /* Sets values from payment provider attribute "UserData" to correct attribute in paymentTransactionEntryModel */
    protected void setUserDataToPaymentTransaction(final PaymentTransactionData paymentTransactionData, final String userData) {
        if (StringUtils.isNotBlank(userData)) {
            final Map<String, String> userDataMap = DistUtils.getMapFromString(new String(Base64.getDecoder().decode(userData)), ";", ":");
            paymentTransactionData.setAmount(new BigDecimal(userDataMap.get("Amount")));
            paymentTransactionData.setCurrency(userDataMap.get("Currency"));
            paymentTransactionData.setReqId(userDataMap.get(ORDER_CODE));
        }
    }

    /* DISTRELEC-7570: Gets values for payment provider attribute "OrderDesc" */
    protected String getOrderDescForPaymentRequest(final CartModel cartModel) {
        final StringBuilder orderDesc = new StringBuilder();
        if (CollectionUtils.isNotEmpty(cartModel.getEntries())) {
            ProductModel product = null;
            if (cartModel.getEntries().size() == 1) {
                product = cartModel.getEntries().get(0).getProduct();
                orderDesc.append(product.getCode()).append(",").append(cartModel.getEntries().get(0).getBasePrice()).append(";");
            } else {
                for (final AbstractOrderEntryModel entry : cartModel.getEntries()) {
                    orderDesc.append(entry.getProduct().getCode()).append(";");
                }
            }
        }
        correctOrderDescStringLength(orderDesc);
        return orderDesc.toString();
    }

    // DISTRELEC-7570:The maximum number of characters allowed by EvoPayment is 127.
    protected void correctOrderDescStringLength(final StringBuilder orderDesc) {
        if (orderDesc.length() > 127) {
            orderDesc.delete(127, orderDesc.length());
        }
    }

    /* Gets list of numeric country codes for available delivery countries of current baseStore */
    protected String getNumericCountryCodesForCurrentSalesOrg() {
        final StringBuilder numericCountryCodes = new StringBuilder();
        final BaseStoreModel baseStore = getBaseStoreService().getCurrentBaseStore();
        if (baseStore != null) {
            final Collection<CountryModel> countries = baseStore.getDeliveryCountries();
            if (CollectionUtils.isNotEmpty(countries)) {
                for (final CountryModel country : countries) {
                    if (StringUtils.isNotEmpty(country.getIsocode()) && CountryCode.getByCode(country.getIsocode()) != null) {
                        numericCountryCodes.append(StringUtils.leftPad(String.valueOf(CountryCode.getByCode(country.getIsocode()).getNumeric()), 3, "0"));
                        numericCountryCodes.append(",");
                    }
                }
                return numericCountryCodes.toString().substring(0, numericCountryCodes.length() - 1);
            }
        }
        return "";
    }

    /* Adds address information as payment parameters for payment provider */
    protected String addAddressInformation(final AddressModel address, final String prefix, final String streetHouseNr) {
        final StringBuilder addressParams = new StringBuilder();
        if (address != null) {
            if (StringUtils.isNotBlank(address.getFirstname())) {
                addressParams.append("&").append(prefix).append("FirstName=").append(address.getFirstname());
            }
            if (StringUtils.isNotBlank(address.getLastname())) {
                addressParams.append("&").append(prefix).append("LastName=").append(address.getLastname());
            }
            if (StringUtils.isNotBlank(address.getStreetname())) {
                addressParams.append("&").append(prefix).append("AddrStreet=").append(address.getStreetname());
            }
            if (StringUtils.isNotBlank(address.getStreetnumber())) {
                addressParams.append("&").append(prefix).append(streetHouseNr).append("=").append(address.getStreetnumber());
            }
            if (StringUtils.isNotBlank(address.getPostalcode())) {
                addressParams.append("&").append(prefix).append("AddrZip=").append(address.getPostalcode());
            }
            if (StringUtils.isNotBlank(address.getTown())) {
                addressParams.append("&").append(prefix).append("AddrCity=").append(address.getTown());
            }

            if (address.getCountry() != null) {
                if (StringUtils.equalsIgnoreCase(address.getCountry().getIsocode(), "XI")) {
                    addressParams.append("&").append(prefix).append("AddrCountryCode=").append("gb");
                } else {
                    addressParams.append("&").append(prefix).append("AddrCountryCode=").append(address.getCountry().getIsocode().toLowerCase());
                }
            }

            if (includeAddrStateParam(address)) {
                setAddrStateforPaypal(address, prefix, addressParams);
            }
        }
        return addressParams.toString();
    }

    /**
     * @param address
     * @param prefix
     * @param addressParams
     */
    private void setAddrStateforPaypal(final AddressModel address, final String prefix, final StringBuilder addressParams) {
        final String stateAbv = getStateAbv(address.getCountry().getIsocode(), address.getPostalcode());
        if (StringUtils.isNotBlank(stateAbv)) {
            addressParams.append("&").append(prefix).append("AddrState=").append(stateAbv);
        }
    }

    /**
     * In case of paypal payment only
     *
     * @param address
     * @return true only when country is us or ca
     */
    private boolean includeAddrStateParam(final AddressModel address) {
        return (this instanceof PayPalPaymentParamsStrategy) && (address.getCountry() != null) && (address.getCountry().getIsocode() != null)
                && (address.getCountry().getIsocode().equalsIgnoreCase("us") || address.getCountry().getIsocode().equalsIgnoreCase("ca"));
    }

    /* Adds shipping address information as payment parameters for payment provider */
    protected String addShippingAddressInformation(final AddressModel address, final String prefix) {
        final StringBuilder shippingAddressParams = new StringBuilder();
        if (address != null) {
            if (StringUtils.isNotBlank(address.getFirstname())) {
                shippingAddressParams.append("&").append(prefix).append("FirstName=").append(address.getFirstname());
            }
            if (StringUtils.isNotBlank(address.getLastname())) {
                shippingAddressParams.append("&").append(prefix).append("LastName=").append(address.getLastname());
            }
            if (StringUtils.isNotBlank(address.getStreetname())) {
                shippingAddressParams.append("&").append(prefix).append("Street=").append(address.getStreetname());
            }
            if (StringUtils.isNotBlank(address.getStreetnumber())) {
                shippingAddressParams.append("&").append(prefix).append("HouseNumber=").append(address.getStreetnumber());
            }
            if (StringUtils.isNotBlank(address.getPostalcode())) {
                shippingAddressParams.append("&").append(prefix).append("ZipCode=").append(address.getPostalcode());
            }
            if (StringUtils.isNotBlank(address.getTown())) {
                shippingAddressParams.append("&").append(prefix).append("City=").append(address.getTown());
            }
            if (address.getCountry() != null) {
                final String country = StringUtils.leftPad(String.valueOf(CountryCode.getByCode(address.getCountry().getIsocode()).getNumeric()), 3, "0");
                shippingAddressParams.append("&").append(prefix).append("CountryCode=").append(country);
            }
        }
        return shippingAddressParams.toString();
    }

    /* Adds additional address information as payment parameters for payment provider */
    protected String addAdditionalAddressInformation(final AddressModel address, final String prefix, final boolean salutation, final boolean title) {
        final StringBuilder additionalAddressParams = new StringBuilder();
        if (address != null) {
            if (salutation) {
                additionalAddressParams.append("&").append(prefix).append("Salutation=");
                String titletext = (address.getTitle() != null) ? address.getTitle().getName() : StringUtils.EMPTY;
                additionalAddressParams.append(titletext);
            }
            if (title) {
                additionalAddressParams.append("&").append(prefix).append("Title=");
                String titletext = (address.getTitle() != null) ? address.getTitle().getName() : StringUtils.EMPTY;
                additionalAddressParams.append(titletext);
            }
            if (address.getDateOfBirth() != null) {
                final String language = ((LanguageModel) getSessionService().getAttribute("language")).getIsocode();
                final String country = ((CMSSiteModel) getSessionService().getAttribute("currentSite")).getSalesOrg().getCountry().getIsocode();
                final DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", new Locale(language, country));
                additionalAddressParams.append("&").append(prefix).append("DateOfBirth=").append(dateFormat.format(address.getDateOfBirth()));
            }
        }
        return additionalAddressParams.toString();
    }

    /* Gets language isoCode from session */
    protected String getCurrentLanguageIsoCode() {
        return ((LanguageModel) getSessionService().getAttribute("language")).getIsocode();
    }

    /* Gets country isoCode from session */
    protected String getCurrentCountryIsoCode() {
        return ((CMSSiteModel) getSessionService().getAttribute("currentSite")).getSalesOrg().getCountry().getIsocode();
    }

    public BaseStoreService getBaseStoreService() {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService) {
        this.baseStoreService = baseStoreService;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public String getCaptureMethod(final CartModel cartModel) {
        final AbstractDistPaymentModeModel paymentMethod = (AbstractDistPaymentModeModel) cartModel.getPaymentMode();
        String paymentMethodCode = paymentMethod.getCode();
        // get country dependent capture method or return the default
        final String cfgKey = baseSiteService.getCurrentBaseSite().getUid() + "." + paymentMethodCode + CAPTURE_CONFIG_KEY;
        return configurationService.getConfiguration().getString(cfgKey, captureMethod);
    }

    public void setCaptureMethod(final String captureMethod) {
        this.captureMethod = captureMethod;
    }

    public PaymentOptionService getPaymentOptionService() {
        return paymentOptionService;
    }

    public void setPaymentOptionService(final PaymentOptionService paymentOptionService) {
        this.paymentOptionService = paymentOptionService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistSiteBaseUrlResolutionService getDistSiteBaseUrlResolutionService() {
        return distSiteBaseUrlResolutionService;
    }

    public void setDistSiteBaseUrlResolutionService(final DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService) {
        this.distSiteBaseUrlResolutionService = distSiteBaseUrlResolutionService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    @Override
    public String adjustURL(String paymentMethodUrl) {
        // nothing to do for evo-payment
        return paymentMethodUrl;
    }

    public String getNotifyUrl(BaseSiteModel baseSite) {
        return BooleanUtils.isTrue(sessionService.getAttribute(HEADLESS_CART)) ? getHeadlessNotifyURL(baseSite)
                                                                               : NOTIFY_URL.replace(CURRENCY_CODE, getCurrentCartCurrencyCode());
    }

    private String getHeadlessNotifyURL(BaseSiteModel baseSite) {
        return String.format(HEADLESS_NOTIFY_URL, baseSite.getUid(), getCurrentCartCurrencyCode());
    }
}
