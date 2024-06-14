/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.service.order.exceptions.PurchaseBlockedException;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.core.util.DistCryptography;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.b2b.budget.DistB2BBudgetFacade;
import com.namics.distrelec.b2b.facades.newsletter.DistNewsletterFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.DistCancelledOrderPrepayment;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.storefront.annotations.RequireHardLogin;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;

import de.hybris.platform.acceleratorservices.hostedorderpage.data.HostedOrderPageData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.order.InvalidCartException;

/**
 * This Controller is used for reviewing the entire checkout steps, checkout data and proceed towards payment. Customer can modify the
 * checkout data from this page.
 *
 * @author <a href="sudarshan.tembhurnikar@datwyler.com">Sudarshan Tembhurnikar@datwyler.com</a>, Distrelec
 */
@Controller
@RequestMapping(value = "/checkout/payment")
public class DistCheckoutPaymentController extends AbstractDistCheckoutController {

    private static final Logger LOG = LoggerFactory.getLogger(DistCheckoutPaymentController.class);

    private static final String JUMPOUT_FAILURE = "/jumpout/failure";

    private static final String JUMPOUT_SUCCESS = "/jumpout/success";

    private static final String NOTIFY = "/notify";

    private static final String HEADER_USER_AGENT = "User-Agent";

    private static final String DL_PAYMENT_ERROR_PAGE = "payment error page";

    private static final String CHECKOUT_PAYMENT_UNKNOWN_ERROR_PAGE = "checkoutPaymentUnknownErrorPage";

    private static final String PAGE_CHECKOUT_ORDERCONFIRMATION = "/checkout/orderConfirmation";

    private static final String NOTIFY_PATH_VARIABLE_PATTERN = NOTIFY + "/{currency:.*}";

    private static final String SIMULATED_PAYMENT_PATH_VARIABLE_PATTERN = "/{simulatedPaymentForOrder:.*}";

    private static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "/{orderCode:.*}";

    private static final String PAYMENT_ERROR_MESSAGE_BASIC_KEY = "checkout.error.payment.";

    private static final String PAYMENT_ERROR_MESSAGE_KEY = "checkout.error.payment";

    private static final String PAGE_CHECKOUT_PAYMENT_SUCCESS = "/checkout/payment/success";

    private static final String PAGE_CHECKOUT_PAYMENT_FAILURE = "/checkout/payment/failure";

    private static final String CONTINUE_URL = "continueUrl";

    private static final String PURCHASE_BLOCKED_PRODUCT_CODES = "purchaseBlockedProductCodes";

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private DistB2BBudgetFacade distB2BBudgetFacade;

    @Autowired
    private DistPriceDataFactory priceDataFactory;

    @Autowired
    private DistNewsletterFacade distNewsletterFacade;

    @Autowired
    private DistUserFacade userFacade;

    @Autowired
    private DistB2BOrderFacade distB2BOrderFacade;

    @Autowired
    private AvailabilityService availabilityService;

    @Override
    protected boolean recalculateCartBeforePage() {
        return false;
    }

    /**
     * This method is used to hold the payment details and perform payment transaction.
     *
     * @param model
     * @param request
     * @return String
     */
    @RequireHardLogin
    @RequestMapping(value = "/hiddenPaymentForm", method = RequestMethod.GET)
    public String checkoutHiddenPaymentForm(final Model model, final HttpServletRequest request) {
        final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent = DistSiteBaseUrlResolutionService.MobileUserAgent
                                                                                                                           .getByAgentString(request.getHeader(HEADER_USER_AGENT));

        final Map<String, String> paymentParameters = getDistCheckoutFacade().getPaymentParameters(userAgent);

        // extend payment parameters with Template= and Language= for payment with credit cards
        final CartData cartData = getDistCheckoutFacade().getCheckoutCart();
        if (BooleanUtils.isTrue(cartData.getPaymentMode().getCreditCardPayment())) {
            paymentParameters.put("Language", getCurrentLanguage().getIsocode());
            paymentParameters.put("Template", paymentParameters.get("TemplateID"));
        }

        // extend payment parameters with Target= for target of the form request (_self = iframe, _top = non-iframe)
        if (BooleanUtils.isTrue(cartData.getPaymentMode().getIframe())) {
            paymentParameters.put("Target", "_self");
        } else {
            paymentParameters.put("Target", "_top");
        }

        final HostedOrderPageData hostedOrderPageData = new HostedOrderPageData();
        hostedOrderPageData.setParameters(paymentParameters);
        hostedOrderPageData.setPostUrl(paymentParameters.get("URL"));
        hostedOrderPageData.setDebugMode(Boolean.TRUE);
        model.addAttribute("hostedOrderPageData", hostedOrderPageData);

        LOG.debug("{} hiddenPaymentForm paymentparameters: {}", getPaymentuserUID(), paymentParameters);
        return ControllerConstants.Views.Fragments.Checkout.HiddenPaymentForm;
    }

    @RequestMapping(value = "/invoice/order", method = RequestMethod.POST)
    public String invoiceOrder(final RedirectAttributes redirectAttributes) throws InvalidCartException {
        final CartData cartData = getDistCheckoutFacade().getCheckoutCart();
        List<String> purchaseBlockedProductCodes = availabilityService.getPurchaseBlockedProductCodes();
        if (CollectionUtils.isNotEmpty(purchaseBlockedProductCodes)) {
            redirectAttributes.addFlashAttribute(PURCHASE_BLOCKED_PRODUCT_CODES, purchaseBlockedProductCodes);
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_BACK_ORDER_DETAILS);
        }
        // * Simulate payment step for payment by invoice
        if (cartData.getPaymentMode() != null && BooleanUtils.isFalse(cartData.getPaymentMode().getHop())) {
            final OrderData orderData = getDistCheckoutFacade().placeOrder();
            // Default Payment Details
            setCustomerDefaultPaymentDetail(orderData);

            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + PAGE_CHECKOUT_PAYMENT_SUCCESS + "/" + orderData.getCode());
        }

        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + PAGE_CHECKOUT_REVIEW_AND_PAY);
    }

    private void setCustomerDefaultPaymentDetail(OrderData orderData) {
        // Set the default Payment data for customer
        final CustomerData currentCustomer = getDistCheckoutFacade().getCurrentCheckoutCustomer();
        if (StringUtils.isEmpty(currentCustomer.getDefaultPaymentMode())) {

            // Default Payment Details Not exists
            userFacade.setDefaultPaymentMode(orderData.getPaymentMode().getCode());
            if (orderData.getPaymentMode().getCode().equals("CreditCard")) {
                userFacade.setDefaultPaymentInfo(orderData.getPaymentInfo());
            }
        } else {
            // Default Payment Details exists
            if (orderData.getPaymentInfo() != null && currentCustomer.getDefaultPaymentMode().equals("CreditCard")) {

                // Credit card Details
                if (null == currentCustomer.getDefaultPaymentInfo()) {
                    userFacade.setDefaultPaymentInfo(orderData.getPaymentInfo());
                }
            }
        }

    }

    /**
     * This method verify if the payment transaction is success and redirect accordingly to respective pages.
     *
     * @param allParameters
     * @param model
     */
    @RequestMapping(value = JUMPOUT_SUCCESS, method = { RequestMethod.GET, RequestMethod.POST })
    public String jumpOutPaymentSuccess(@RequestParam final Map<String, String> allParameters, HttpServletResponse response, final Model model) {
        model.addAttribute("allParameters", DistUtils.getStringFromMap(allParameters, "&", "="));
        model.addAttribute(CONTINUE_URL, PAGE_CHECKOUT_PAYMENT_SUCCESS);
        final String jsessionId = getDecryptedSessionId(allParameters.get(DistConstants.Session.SID), allParameters.get(DistConstants.Session.LN));
        response.setHeader("Set-Cookie", DistConstants.Session.JSESSIONID + "=" + jsessionId);
        return ControllerConstants.Views.Pages.Checkout.CheckoutPaymentFinalizePage;
    }

    private String getDecryptedSessionId(final String sid, final String length) {
        String jsessionId = StringUtils.EMPTY;
        try {
            jsessionId = DistCryptography.decryptString(sid, DistCryptography.WEB_SESSION_KEY, length);
        } catch (Exception ex) {
            LOG.error("Error while decrypting session id in success handler", ex);
        }
        return jsessionId;
    }

    /**
     * This method verify if the payment transaction is a failure and redirect accordingly to respective pages.
     *
     * @param allParameters
     * @param model
     */
    @RequestMapping(value = JUMPOUT_FAILURE, method = { RequestMethod.GET, RequestMethod.POST })
    public String jumpOutPaymentFailure(@RequestParam final Map<String, String> allParameters, HttpServletResponse response, final Model model) {
        model.addAttribute("allParameters", DistUtils.getStringFromMap(allParameters, "&", "="));
        model.addAttribute(CONTINUE_URL, PAGE_CHECKOUT_PAYMENT_FAILURE);
        final String jsessionId = getDecryptedSessionId(allParameters.get(DistConstants.Session.SID), allParameters.get(DistConstants.Session.LN));
        response.setHeader("Set-Cookie", DistConstants.Session.JSESSIONID + "=" + jsessionId);
        return ControllerConstants.Views.Pages.Checkout.CheckoutPaymentFinalizePage;
    }

    /**
     * This method notify the checkout payment transaction status.
     *
     * @param request
     * @param currency
     * @return ResponseEntity
     * @throws IOException
     */
    @ResponseBody
    @PostMapping(value = NOTIFY_PATH_VARIABLE_PATTERN)
    public ResponseEntity<String> checkoutPaymentNotify(final HttpServletRequest request, @PathVariable final String currency) throws IOException {
        final StringBuilder notifyRequestBody = new StringBuilder();
        final String requestBody = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        if (StringUtils.isNotBlank(requestBody)) {
            notifyRequestBody.append(requestBody);
            LOG.debug("Notify with data from request body: {}", requestBody);
        } else {
            notifyRequestBody.append("Len=").append(request.getParameter("Len")).append("&Data=").append(request.getParameter("Data"));
            LOG.debug("Notify with data from request parameters: {}", notifyRequestBody);
        }

        getDistCheckoutFacade().handlePaymentNotify(notifyRequestBody.toString(), currency);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This methods redirects the customer to the payment confirmation page when the checkout payment is successful.
     *
     * @param allParameters
     * @param model
     * @param request
     * @return String
     * @throws InvalidCartException
     * @throws IOException
     */
    @RequireHardLogin
    @GetMapping(value = "/success")
    public String checkoutPaymentSuccess(@RequestParam final Map<String, String> allParameters, final Model model,
                                         final HttpServletRequest request, final RedirectAttributes redirectAttributes) {
        // Add Global Attributes
        addGlobalModelAttributes(model, request);

        try {
            // get data from request body
            simulatePaymentNotify(allParameters);
            LOG.debug("{} Successful payment with data from request parameters: {}", getPaymentuserUID(), allParameters);

            getDistCheckoutFacade().handlePaymentSuccessFailure(allParameters);
            final Object orderCode = getSessionService().getAttribute(DistConstants.Session.ORDER_CODE_CONFIRMATION);

            model.addAttribute("orderCode", orderCode);
            getSessionService().removeAttribute(DistConstants.Session.ORDER_CODE_CONFIRMATION);
            getSessionService().removeAttribute(DistConstants.Session.PAYMENT_DESCRIPTION);
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + PAGE_CHECKOUT_ORDERCONFIRMATION + "/" + orderCode);

        } catch (final PurchaseBlockedException pbe) {
            DistCancelledOrderPrepayment cancelledOrderPrepayment = createDistCancelledOrderPrepayment(pbe);
            distB2BOrderFacade.sendOrderCancellationPrepaymentMail(cancelledOrderPrepayment);
            redirectAttributes.addFlashAttribute(PURCHASE_BLOCKED_PRODUCT_CODES, pbe.getProductCodes());
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_BACK_ORDER_DETAILS);

        } catch (final Exception ise) {
            LOG.error("{} For UID: {}. Successful payment could not be finished", ErrorLogCode.PAYMENT_ERROR.getCode(), getPaymentuserUID(), ise);
            model.addAttribute("orderCode", getSessionService().getAttribute(DistConstants.Session.ORDER_CODE_CONFIRMATION));
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_REVIEW_AND_PAY);
        }
    }

    private DistCancelledOrderPrepayment createDistCancelledOrderPrepayment(PurchaseBlockedException pbe) {
        DistCancelledOrderPrepayment distCancelledOrderPrepayment = new DistCancelledOrderPrepayment();
        distCancelledOrderPrepayment.setUid(getUserService().getCurrentUser().getUid());
        distCancelledOrderPrepayment.setOrderNumber(pbe.getOrderNumber());
        distCancelledOrderPrepayment.setArticleNumbers(pbe.getProductCodes());
        distCancelledOrderPrepayment.setProductNames(pbe.getProductNames());
        return distCancelledOrderPrepayment;
    }

    /**
     * This methods redirects the customer to the payment confirmation page when the checkout payment is successful for Invoice.
     *
     * @param simulatedPaymentForOrder
     * @return String
     */
    @RequireHardLogin
    @RequestMapping(value = "/success" + SIMULATED_PAYMENT_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String checkoutPaymentSuccess(@PathVariable("simulatedPaymentForOrder") final String simulatedPaymentForOrder) {
        // simulated DeuCS URLSuccess for invoice payment
        LOG.debug("Simulate successful payment for order paid by invoice");
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + PAGE_CHECKOUT_ORDERCONFIRMATION + "/" + simulatedPaymentForOrder);
    }

    /**
     * This method redirects the customer to final payment page if the payment transaction is not successful(Failure).
     *
     * @param allParameters
     * @param model
     * @param request
     * @return String
     */
    @RequireHardLogin
    @RequestMapping(value = "/failure", method = RequestMethod.GET)
    public String checkoutPaymentFailed(@RequestParam final Map<String, String> allParameters, final Model model, final HttpServletRequest request,
                                        RedirectAttributes redirectAttr) {

        // Add Global Attributes
        addGlobalModelAttributes(model, request);

        try {
            simulatePaymentNotify(allParameters);
            LOG.debug("{} Failure payment with data from request parameters: {}", getPaymentuserUID(), allParameters);
            final String failureCode = getDistCheckoutFacade().handlePaymentSuccessFailure(allParameters);
            final String failureMessageKey = getFailureMessageKeyByErrorCode(failureCode);

            model.addAttribute(CONTINUE_URL, CHECKOUT_REVIEW_AND_PAY);
            model.addAttribute("errorCode", failureMessageKey);
            GlobalMessages.addErrorMessage(model, failureMessageKey);

            // Data layer details
            setPaymentError(failureCode, model);
            redirectAttr.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, failureMessageKey);
            // Add the data layer details
            DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            getDistDigitalDatalayerFacade().storePaymentErrorData(digitalDatalayer, failureCode, DL_PAYMENT_ERROR_PAGE);
            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
            redirectAttr.addFlashAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_REVIEW_AND_PAY);

        } catch (final Exception ise) {
            LOG.error("{} For UID: {}. Failure payment could not be finished", ErrorLogCode.PAYMENT_ERROR.getCode(), getPaymentuserUID(), ise);
            model.addAttribute("orderCode", getSessionService().getAttribute(DistConstants.Session.ORDER_CODE_CONFIRMATION));

            model.addAttribute(CONTINUE_URL, CHECKOUT_REVIEW_AND_PAY);
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_REVIEW_AND_PAY);
        }
    }

    /**
     * This methods confirms if the budget is exceeding from order budget.
     *
     * @return String
     */
    @RequestMapping(value = "/approval/submit", method = RequestMethod.GET)
    public String submitForApproval() throws InvalidCartException {
        final CartData cart = getDistCheckoutFacade().getCheckoutCart();
        final CustomerData customer = cart.getB2bCustomerData();
        boolean submitForApproval = false;
        BigDecimal exceededBudget = BigDecimal.ZERO;
        if (customer.getBudget() != null) {
            final B2BBudgetData budget = customer.getBudget();
            distB2BBudgetFacade.calculateExceededBudget(budget, cart.getTotalPrice());
            if (budget.getExceededOrderBudget() != null && budget.getExceededOrderBudget().compareTo(BigDecimal.ZERO) != 0) {
                submitForApproval = true;
                exceededBudget = budget.getExceededOrderBudget();
            } else if (budget.getExceededYearlyBudget() != null && budget.getExceededYearlyBudget().compareTo(BigDecimal.ZERO) != 0) {
                submitForApproval = true;
                exceededBudget = budget.getExceededYearlyBudget();
            }
        }
        if (cart.getPaymentMode() == null || BooleanUtils.isFalse(cart.getPaymentMode().getInvoicePayment())) {
            submitForApproval = false;
        }
        if (submitForApproval) {
            getSessionService().setAttribute(WebConstants.SUBMIT_APPROVAL, Boolean.TRUE);
            // negate to make -19.4 to 19.4 so the user can see how much he exceeded.
            final PriceData budgetPrice = priceDataFactory.create(PriceDataType.BUY, exceededBudget, cart.getTotalPrice().getCurrencyIso());
            getSessionService().setAttribute(WebConstants.EXCEEDED_BUDGET, budgetPrice);
            final OrderData orderData;
            // * Simulate payment step for payment by invoice
            if (cart.getPaymentMode() != null && BooleanUtils.isFalse(cart.getPaymentMode().getHop())) {
                orderData = getDistCheckoutFacade().placeOrder();
                // Default Payment Details
                setCustomerDefaultPaymentDetail(orderData);

                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + PAGE_CHECKOUT_PAYMENT_SUCCESS + "/" + orderData.getCode());
            }
        }
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_REVIEW_AND_PAY);
    }

    @RequireHardLogin
    @RequestMapping(value = "/unknownerror" + ORDER_CODE_PATH_VARIABLE_PATTERN, method = RequestMethod.GET)
    public String checkoutPaymentUnknownError(@PathVariable("orderCode") final String orderCode, final Model model,
                                              final HttpServletRequest request) throws CMSItemNotFoundException {
        LOG.debug("Unkonwn error occured for order: {}", orderCode);
        // Add Global Attributes
        addGlobalModelAttributes(model, request);

        setPaymentError("Unknown", model);
        setUpUnknownErrorPageModel(model);
        model.addAttribute("orderCode", orderCode);
        return ControllerConstants.Views.Pages.Checkout.CheckoutPaymentUnknownErrorPage;
    }

    @RequireHardLogin
    @RequestMapping(value = "/unknownerror", method = RequestMethod.GET)
    public String checkoutPaymentUnknownErrorWithoutOrderCode(final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        LOG.debug("Unknown error occured without valid order code");
        // Add Global Attributes
        addGlobalModelAttributes(model, request);

        setUpUnknownErrorPageModel(model);
        setPaymentError("Unknown Error", model);
        return ControllerConstants.Views.Pages.Checkout.CheckoutPaymentUnknownErrorPage;
    }

    /**
     * Helper method to set up model for unknown error page
     *
     * @param model
     * @throws CMSItemNotFoundException
     */
    protected void setUpUnknownErrorPageModel(final Model model) throws CMSItemNotFoundException {
        final ContentPageModel contentPage = getContentPageForLabelOrId(CHECKOUT_PAYMENT_UNKNOWN_ERROR_PAGE);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                           simpleBreadcrumbBuilder.getBreadcrumbs(contentPage.getTitle(), contentPage.getTitle(Locale.ENGLISH)));
    }

    /**
     * Helper method to simulate the payment notify if the configuration is set to true
     *
     * @param parameters
     */
    protected void simulatePaymentNotify(final Map<String, String> parameters) {
        if (getConfigurationService().getConfiguration().getBoolean("distrelec.payment.simulate.notify", false)) {
            getDistCheckoutFacade().handlePaymentNotify(DistUtils.getStringFromMap(parameters, "&", "="), getCurrentCartCurrencyCode());
        }
    }

    @Override
    protected String validateCheckout(final Model model, final RedirectAttributes redirectModel) {
        final String code = super.validateCheckout(model, redirectModel);
        if (StringUtils.isBlank(code)) {
            final String detailPageErrorCode = validateDetailPage();
            if (StringUtils.isNotBlank(detailPageErrorCode)) {
                redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList(detailPageErrorCode));
                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_DELIVERY);
            }
        }

        return code;
    }

    /**
     * Get the current cart currency details.
     *
     * @return String
     */
    private String getCurrentCartCurrencyCode() {
        return getB2bCartFacade().getSessionCart().getCurrency().getIsocode().toUpperCase();
    }

    /**
     * Helper method to provide dedicated error message key
     *
     * @param errorCode
     * @return String
     */
    protected String getFailureMessageKeyByErrorCode(final String errorCode) {
        try {
            getMessageSource().getMessage(PAYMENT_ERROR_MESSAGE_BASIC_KEY + errorCode, null, getI18nService().getCurrentLocale());
            return PAYMENT_ERROR_MESSAGE_BASIC_KEY + errorCode;
        } catch (final NoSuchMessageException e) {
            if (getSessionService().getAttribute(DistConstants.Session.PAYMENT_DESCRIPTION) != null) {
                // Customize error messages only for certain codes that are handled and localized, return others untouched.
                String paymentErrorDescription = getSessionService().getAttribute(DistConstants.Session.PAYMENT_DESCRIPTION).toString();
                if (getDistCheckoutFacade().isErrorHandled(paymentErrorDescription)) {
                    return PAYMENT_ERROR_MESSAGE_KEY + "." + paymentErrorDescription.replaceAll("[^a-zA-Z]+", "").toLowerCase();
                } else {
                    return paymentErrorDescription;
                }
            } else {
                // use default error message if no dedicated message is available
                return PAYMENT_ERROR_MESSAGE_BASIC_KEY + "not.accepted";
            }
        }
    }

    public String getPaymentuserUID() {
        return "Uid: " + getUserService().getCurrentUser().getUid() + " ";
    }

}
