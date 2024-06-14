/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.event.PaymentNotifyEvent;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.model.payment.AbstractDistPaymentModeModel;
import com.namics.distrelec.b2b.core.model.payment.DistPaymentModeModel;
import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;
import com.namics.distrelec.b2b.core.service.order.DistCommercePaymentService;
import com.namics.distrelec.b2b.core.service.order.exceptions.PurchaseBlockedException;
import com.namics.distrelec.b2b.core.service.order.strategies.DistPaymentParamsStrategy;
import com.namics.distrelec.b2b.core.service.order.util.FindOrderForCodeUtil;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Base64Utils;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Default implementation of {@link DistCommercePaymentService}
 *
 * @author pbueschi, Namics AG
 */
public class DefaultDistCommercePaymentService extends AbstractBusinessService implements DistCommercePaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistCommercePaymentService.class);

    private static final String MERCHANT_ID_MESSAGE_KEY = ".payment.merchantId";

    private static final String TEMPLATE_ID_MESSAGE_KEY = ".payment.templateId";

    private static final String DEFAULT_TEMPLATE_ID_MESSAGE_KEY = "distrelec.payment.templateId";

    private Map<String, DistPaymentParamsStrategy> paymentParamsStrategies;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private CartService cartService;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private EventService eventService;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private UserService userService;

    @Autowired
    private DistCommerceCommonI18NService distCommerceCommonI18NService;

    @Override
    public String getPaymentUrlForCart(final CartModel cartModel, final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        final String paymentUrl = ((AbstractDistPaymentModeModel) cartModel.getPaymentMode()).getUrl();
        return paymentUrl + buildPaymentParamsList(cartModel, userAgent);
    }

    protected String buildPaymentParamsList(final CartModel cartModel, final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        final String merchantId = getMerchantId();
        final String paymentModeCode = cartModel.getPaymentMode().getCode();

        // encrypt payment params
        final DistPaymentParamsStrategy strategy = paymentParamsStrategies.get(paymentModeCode);
        final Map<String, String> paymentParamsMap = strategy.getAllPaymentParams(merchantId, paymentModeCode, cartModel, userAgent);

        // build complete payment params
        final StringBuilder paymentParams = new StringBuilder("?");
        for (final Entry<String, String> entry : paymentParamsMap.entrySet()) {
            paymentParams.append(entry.getKey()).append("=").append(entry.getValue());
        }

        final String paymentRequestString = paymentParams.toString();
        LOG.info("Payment-Request: {}", paymentRequestString);

        return paymentRequestString;
    }

    @Override
    public Map<String, String> getPaymentParameters(final CartModel cartModel, final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent) {
        final Map<String, String> paymentParameters = new HashMap<>();
        final AbstractDistPaymentModeModel paymentMethod = (AbstractDistPaymentModeModel) cartModel.getPaymentMode();
        final String merchantId = getMerchantId();

        final String paymentModeCode = paymentMethod.getCode();
        final DistPaymentParamsStrategy strategy = paymentParamsStrategies.get(paymentModeCode);

        String paymentMethodUrl = paymentMethod.getUrl();
        // needed because dips URL is environment specific
        String adjustedURL = strategy.adjustURL(paymentMethodUrl);

        paymentParameters.put("URL", adjustedURL);
        paymentParameters.put("MerchantID", merchantId);
        paymentParameters.put("TemplateID", getTemplateId());

        paymentParameters.putAll(strategy.getAllPaymentParams(merchantId, paymentModeCode, cartModel, userAgent));
        if (CreditCardPaymentInfoModel._TYPECODE.equals(cartModel.getPaymentMode().getPaymentInfoType().getCode())) {
            paymentParameters.put("Language", getBaseLanguageIsocode());
            paymentParameters.put("Template", paymentParameters.get("TemplateID"));
        }
        if (Boolean.TRUE.equals(((AbstractDistPaymentModeModel)cartModel.getPaymentMode()).getIframe())) {
            paymentParameters.put("Target", "_self");
        } else {
            paymentParameters.put("Target", "_top");
        }

        return paymentParameters;
    }

    private String getBaseLanguageIsocode() {
        LanguageModel language = distCommerceCommonI18NService.getCurrentLanguage();
        LanguageModel baseLanguage = distCommerceCommonI18NService.getBaseLanguage(language);
        return baseLanguage.getIsocode();
    }

    @Override
    public Map<String, String> handlePaymentNotify(final String requestBody, final String currencycode) {
        final Map<String, String> dibsMap = new HashMap<>();
        if (StringUtils.isNotBlank(requestBody)) {
            final Map<String, String> encryptedPaymentParamsMap = DistUtils.getMapFromString(requestBody, "&", "=");
            if (StringUtils.isNotBlank(encryptedPaymentParamsMap.get("Len")) && StringUtils.isNotBlank(encryptedPaymentParamsMap.get("Data"))) {
                final String length = encryptedPaymentParamsMap.get("Len");
                final String encryptedPaymentParams = encryptedPaymentParamsMap.get("Data");
                final String decryptedPaymentRequest = DistCryptography.decryptString(encryptedPaymentParams, currencycode + DistCryptography.DATA_CRYPTION_KEY,
                                                                                      length);
                final Map<String, String> decryptedPaymentRequestMap = DistUtils.getMapFromString(decryptedPaymentRequest, "&", "=");

                try {

                    String userData = new String(Base64Utils.decodeFromString(decryptedPaymentRequestMap.get("UserData")));
                    final Map<String, String> userDataPaymentParamMap = DistUtils.getMapFromString(userData, ";", ":");
                    final String paymentModeCode = userDataPaymentParamMap.get("PaymentModeCode");
                    final DistPaymentParamsStrategy distPaymentParamsStrategy = getPaymentStrategy(paymentModeCode);
                    distPaymentParamsStrategy.handlePaymentNotifyParams(decryptedPaymentRequestMap, encryptedPaymentParamsMap);
                    distPaymentParamsStrategy.checkPaymentNotify(decryptedPaymentRequestMap);
                    // If the status is OK then we trigger the payment notify process event.

                    if (statusIsOk(decryptedPaymentRequestMap) && createOrderOnNotifyEnabled() && noBlockedProductsExists()) {
                        // Get the right user and the right cart.
                        final CartModel cartModel = FindOrderForCodeUtil.getCartByCode(userDataPaymentParamMap.get("orderCode"), flexibleSearchService);

                        if (cartModel != null && cartModel.getUser() != null) {
                            LOG.debug("Publishing Payment Notify Event [Cart Code: {}, User UID: {}]", cartModel.getCode(), cartModel.getUser().getUid());
                            DistPaymentModeModel paymentMode = (DistPaymentModeModel) cartModel.getValidPaymentModes().stream()
                                                                                               .filter(pModes -> pModes.getCode()
                                                                                                                       .equalsIgnoreCase(paymentModeCode))
                                                                                               .findFirst().get();

                            eventService.publishEvent(new PaymentNotifyEvent(cartModel.getCode(), cartModel.getUser(), paymentMode));
                        }
                    }
                    return decryptedPaymentRequestMap;
                } catch (final NullPointerException npe) {
                    LOG.error("{} Exception in handlePaymentNotify [requestBody: {}, currencycode: {}, decryptedPaymentRequestMap: {}]  ",
                              ErrorLogCode.PAYMENT_ERROR.getCode(), requestBody, currencycode, decryptedPaymentRequestMap, npe);
                    throw npe;
                }
            }
        }

        // in case of dibs notify is not used
        return dibsMap;
    }

    private boolean noBlockedProductsExists() {
        return CollectionUtils.isEmpty(availabilityService.getPurchaseBlockedProductCodes());
    }

    /**
     * Check if the payment request status is OK.
     *
     * @param decryptedPaymentRequestMap
     * @return @return {@code true} if the payment request status is OK, {@code false} otherwise.
     */
    private boolean statusIsOk(final Map<String, String> decryptedPaymentRequestMap) {
        return "OK".equalsIgnoreCase(decryptedPaymentRequestMap.get("Status")) || "AUTHORIZED".equalsIgnoreCase(decryptedPaymentRequestMap.get("Status"));
    }

    /**
     * @return {@code true} if the create order on notify process is enable, {@code false} otherwise.
     */
    private boolean createOrderOnNotifyEnabled() {
        return configurationService.getConfiguration().getBoolean("distrelec.create.order.on.notify.enabled", false);
    }

    /**
     * @return current card currency
     */
    private String getCurrentCartCurrencyCode() {
        return cartService.getSessionCart().getCurrency().getIsocode().toUpperCase();
    }

    @Override
    public String handlePaymentSuccessFailure(final Map<String, String> paymentParamsMap) {
        if (StringUtils.isNotBlank(paymentParamsMap.get("Len")) && StringUtils.isNotBlank(paymentParamsMap.get("Data"))) {
            final String length = paymentParamsMap.get("Len");
            final String encryptedPaymentParams = paymentParamsMap.get("Data");
            LOG.info("Payment-Success/Failure: Len={}&Data={}", length, encryptedPaymentParams);

            final String decryptedPaymentParams = DistCryptography.decryptString(encryptedPaymentParams,
                                                                                 getCurrentCartCurrencyCode() + DistCryptography.DATA_CRYPTION_KEY, length);
            final Map<String, String> decryptedPaymentParamsMap = DistUtils.getMapFromString(decryptedPaymentParams, "&", "=");
            final Map<String, String> userDataPaymentParamMap = DistUtils.getMapFromString(
                                                                                           new String(Base64.getDecoder()
                                                                                                            .decode(decryptedPaymentParamsMap.get("UserData"))),
                                                                                           ";", ":");
            final DistPaymentParamsStrategy distPaymentParamsStrategy = getPaymentStrategy(userDataPaymentParamMap.get("PaymentModeCode"));
            distPaymentParamsStrategy.handlePaymentSuccessFailureParams(decryptedPaymentParamsMap, "Len=" + length + "\nData=" + encryptedPaymentParams);
            distPaymentParamsStrategy.checkPaymentSuccessFailure(decryptedPaymentParamsMap);

            List<String> purchaseBlockedProductCodes = availabilityService.getPurchaseBlockedProductCodes();
            if (CollectionUtils.isNotEmpty(purchaseBlockedProductCodes)) {
                List<String> productNames = getProductNames(purchaseBlockedProductCodes);
                throw new PurchaseBlockedException(purchaseBlockedProductCodes, productNames, userDataPaymentParamMap.get("orderCode"));
            }
            getSessionService().setAttribute(DistConstants.Session.ORDER_CODE_CONFIRMATION, userDataPaymentParamMap.get("orderCode"));

            final String desc = decryptedPaymentParamsMap.get("Description");
            if (StringUtils.isNotBlank(desc)) {
                getSessionService().setAttribute(DistConstants.Session.PAYMENT_DESCRIPTION, desc);
            }
            return decryptedPaymentParamsMap.get("Code");
        } else {
            throw new IllegalStateException(
                                            "{} HandlePaymentSuccessFailure failed because of missing Data= in Request Body!"
                                            + ErrorLogCode.PAYMENT_ERROR.getCode());
        }
    }

    private List<String> getProductNames(List<String> purchaseBlockedProductCodes) {
        List<ProductModel> products = distProductService.getProductListForCodes(purchaseBlockedProductCodes);
        Locale locale = LocaleUtils.toLocale(userService.getCurrentUser().getSessionLanguage().getIsocode());
        return products.stream()
                       .map(product -> product.getName(locale))
                       .filter(Objects::nonNull)
                       .collect(Collectors.toList());
    }

    private DistPaymentParamsStrategy getPaymentStrategy(final String paymentCode) {
        try {
            return paymentParamsStrategies.get(paymentCode);
        } catch (final NullPointerException npe) {
            String message = "";
            LOG.error("{} Null Pointer Exception for order payment code: {}", ErrorLogCode.PAYMENT_ERROR.getCode(), paymentCode);
            LOG.error(message, npe);
            throw npe;
        }
    }

    private String getMerchantId() {
        return configurationService.getConfiguration()
                                   .getString(baseSiteService.getCurrentBaseSite().getUid() + "." + getCurrentCartCurrencyCode() + MERCHANT_ID_MESSAGE_KEY);
    }

    private String getTemplateId() {
        String TemplateId = configurationService.getConfiguration().getString(baseSiteService.getCurrentBaseSite().getUid() + TEMPLATE_ID_MESSAGE_KEY);
        return StringUtils.isNotEmpty(TemplateId) ? TemplateId : configurationService.getConfiguration().getString(DEFAULT_TEMPLATE_ID_MESSAGE_KEY);
    }

    @Required
    public void setPaymentParamsStrategies(final Map<String, DistPaymentParamsStrategy> paymentParamsStrategies) {
        this.paymentParamsStrategies = paymentParamsStrategies;
    }
}
