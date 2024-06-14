package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.FORWARD_SLASH;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants.SeoRobots.META_ROBOTS;
import static de.hybris.platform.acceleratorstorefrontcommons.controllers.ThirdPartyConstants.SeoRobots.NOINDEX_NOFOLLOW;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.b2b.budget.DistB2BBudgetFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;
import com.namics.distrelec.b2b.facades.product.DistPriceDataFactory;
import com.namics.distrelec.b2b.storefront.annotations.RequireHardLogin;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.forms.CodiceVatForm;
import com.namics.distrelec.b2b.storefront.response.GlobalMessageResponse;

import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;

@Controller
@RequestMapping("/checkout/review-and-pay")
@RequireHardLogin
public class DistCheckoutReviewAndPayController extends AbstractDistCheckoutController {

    private static final Logger LOG = LoggerFactory.getLogger(DistCheckoutReviewAndPayController.class);

    private static final String NEW_CARD_ID = "NEW_CARD";

    private static final String PURCHASE_BLOCKED_PRODUCT_CODES = "purchaseBlockedProductCodes";

    @Autowired
    private DistB2BBudgetFacade distB2BBudgetFacade;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private DistPriceDataFactory priceDataFactory;

    @GetMapping
    public String checkoutReviewAndPay(HttpServletRequest request, Model model, RedirectAttributes redirectModel) throws CMSItemNotFoundException,
                                                                                                                  CalculationException {
        CustomerData customer = getDistCheckoutFacade().getCurrentCheckoutCustomer();
        getDistCheckoutFacade().modifyPaymentModesIfBudgetApplied();
        CartData cartData = getDistCheckoutFacade().getCheckoutCart();

        if (getDistCheckoutFacade().hasUnallowedBackorders()) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_BACK_ORDER_DETAILS);
        }

        String validationMessage = validateDeliveryPage(cartData);
        if (StringUtils.isNotEmpty(validationMessage)) {
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList(validationMessage));
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_DELIVERY);
        }

        getOrderFacade().setCustomerClientID(request, cartData.getCode());
        final String redirectString = validateCheckout(model, redirectModel);
        if (isNotBlank(redirectString)) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + redirectString);
        }

        addPaymentOptions(model, cartData, customer);
        model.addAttribute("showBillingEdit", isBillingEditButtonShown(customer));
        model.addAttribute("pickupWarehouses", getPickupWarehouse(cartData));
        model.addAttribute("showOrderReference", isB2BCustomer(customer));
        addCodiceDestinatario(model, customer, cartData);
        addPaymentModeModelAttributes(model);
        addOrderApproval(model, customer, cartData);
        getSessionService().setAttribute(DistConstants.Session.CHECKOUT_USER, customer.getUid());

        addGlobalModelAttributes(model, request);
        storeCmsPageInModel(model, getContentPageForLabelOrId(PAGE_CHECKOUT_REVIEW_AND_PAY));
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(PAGE_CHECKOUT_REVIEW_AND_PAY));
        model.addAttribute(META_ROBOTS, NOINDEX_NOFOLLOW);
        DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageCategoryAndPageType(digitalDatalayer,
                                                                        DigitalDatalayerConstants.PageName.Checkout.CHECKOUT,
                                                                        DigitalDatalayerConstants.PageName.Checkout.REVIEW,
                                                                        DigitalDatalayerConstants.PageType.CHECKOUTPAGE);
        addPageEventOrLoginRegisterEvents(request, digitalDatalayer, DigitalDatalayer.EventName.CHECKOUT_PAYMENT);
        getDistDigitalDatalayerFacade().storeCheckoutStep(digitalDatalayer, PAGE_CHECKOUT_REVIEW_AND_PAY);
        model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);

        return ControllerConstants.Views.Pages.Checkout.CheckoutReviewAndPayPage;
    }

    private boolean isBillingEditButtonShown(CustomerData customer) {
        return getDistCheckoutFacade().shouldAllowEditBilling(customer.getCustomerType())
                || isMultipleAddresses(getDistCheckoutFacade().getBillingAddresses());
    }

    private void addCodiceDestinatario(Model model, CustomerData customer, CartData cartData) {
        if (getDistCheckoutFacade().isCodiceDestinatarioShown(customer)) {
            model.addAttribute("showCodiceDestinatario", Boolean.TRUE);
            model.addAttribute("codiceVatForm", createAndPopulateCodiceForm(customer, cartData));
        }
    }

    private CodiceVatForm createAndPopulateCodiceForm(CustomerData customer, CartData cartData) {
        CodiceVatForm form = new CodiceVatForm();
        form.setVat4(customer.getVat4());
        form.setLegalEmail(customer.getLegalEmail());
        form.setCodiceCIG(cartData.getCodiceCIG());
        form.setCodiceCUP(cartData.getCodiceCUP());
        return form;
    }

    private void addOrderApproval(Model model, CustomerData customer, CartData cartData) {
        if (customer.getBudget() != null) {
            final B2BBudgetData budget = customer.getBudget();
            distB2BBudgetFacade.calculateExceededBudget(budget, cartData.getTotalPrice());
            model.addAttribute("isOrderApprovalLimitExceeded", distB2BBudgetFacade.doesOrderRequireApproval(budget));
        }
    }

    private List<WarehouseData> getPickupWarehouse(CartData cartData) {
        return getDistCheckoutFacade().getPickupDeliveryModeCode().equals(cartData.getDeliveryMode().getCode())
                                                                                                                ? warehouseFacade.getPickupWarehousesAndCalculatePickupDate(cartData)
                                                                                                                : Collections.emptyList();
    }

    @PutMapping(value = "/projectNumber")
    public ResponseEntity<Void> setProjectNumber(@RequestBody(required = false) String projectNumber) {
        getDistCheckoutFacade().setProjectNumber(projectNumber);
        return ok().build();
    }

    private void addPaymentOptions(final Model model, CartData cartData, CustomerData customer) throws CalculationException {
        final List<DistPaymentModeData> validPaymentModes = cartData.getValidPaymentModes();
        if (isSinglePaymentOptionAvailable(validPaymentModes)) {
            getDistCheckoutFacade().setPaymentMode(validPaymentModes.iterator().next());
            getDistCheckoutFacade().calculateOrder(true);
        }

        if (getDistCheckoutFacade().shouldDisplayVatWarningForExportShop(customer)) {
            model.addAttribute("displayVatWarningExportShop", Boolean.TRUE);
        }

        model.addAttribute("paymentOptions", validPaymentModes);
        model.addAttribute("selectedPaymentOption", cartData.getPaymentMode());
        if (getDistCheckoutFacade().isCreditCardPaymentAllowed(cartData)) {
            addCCPaymentInfo(model, cartData);
        }
    }

    private boolean isSinglePaymentOptionAvailable(List<DistPaymentModeData> validPaymentModes) {
        return CollectionUtils.size(validPaymentModes) == 1;
    }

    private void addCCPaymentInfo(final Model model, final CartData cartData) {
        final CustomerData customerData = cartData.getB2bCustomerData();

        if (CollectionUtils.isNotEmpty(customerData.getCcPaymentInfos())) {
            if (isCreditCardPayment(cartData.getPaymentMode()) && cartData.getPaymentInfo() != null) {
                model.addAttribute("selectedCcPaymentInfo", cartData.getPaymentInfo());
            }
            model.addAttribute("ccPaymentInfos", customerData.getCcPaymentInfos());
        }
    }

    private boolean isCreditCardPayment(final DistPaymentModeData paymentMode) {
        return paymentMode != null && BooleanUtils.isTrue(paymentMode.getCreditCardPayment());
    }

    @PutMapping(value = "/select/payment")
    public ResponseEntity<?> changePaymentOption(@RequestBody String paymentModeCode) throws CalculationException {
        if (isNotBlank(paymentModeCode)) {
            final DistPaymentModeData paymentMode = getDistCheckoutFacade().getPaymentModeForCode(paymentModeCode);
            if (paymentMode != null) {
                if (!isCreditCardPayment(paymentMode)) {
                    getDistCheckoutFacade().removePaymentInfo();
                }
                getDistCheckoutFacade().setPaymentMode(paymentMode);
                getDistCheckoutFacade().calculateOrder(true);
                return ok().body(paymentMode);
            }
        }
        return badRequest().build();
    }

    @PutMapping(value = "/select/paymentInfo")
    public ResponseEntity<?> selectCreditCardPaymentInfo(@RequestBody(required = false) String paymentInfoId) throws CalculationException {
        final DistPaymentModeData creditCardPaymentMode = getDistCheckoutFacade().getCreditCardPaymentMode();
        getDistCheckoutFacade().setPaymentMode(creditCardPaymentMode);
        if (isNewCard(paymentInfoId)) {
            getDistCheckoutFacade().removePaymentInfo();
        } else {
            getDistCheckoutFacade().setPaymentDetails(paymentInfoId);
        }
        getDistCheckoutFacade().calculateOrder(true);
        return isNewCard(paymentInfoId) ? ok().build() : ok(getDistCheckoutFacade().getCheckoutCart().getPaymentInfo());
    }

    @PutMapping(value = "/payment/set-default-paymentInfo")
    public ResponseEntity<Void> setDefaultPaymentInfo(@RequestBody String paymentInfoId) {
        if (isNotBlank(paymentInfoId)) {
            getDistUserFacade().setDefaultPaymentInfo(paymentInfoId);
            return ok().build();
        }
        return badRequest().build();
    }

    @DeleteMapping(value = "/paymentInfo")
    public ResponseEntity<Void> removePaymentInfo(@RequestBody String paymentInfoId) {
        if (isNotBlank(paymentInfoId)) {
            getDistUserFacade().removeCCPaymentInfo(paymentInfoId);
            LOG.info("Payment Info : {} has been removed.", paymentInfoId);
            return ok().build();
        }
        return badRequest().build();
    }

    @PostMapping(value = "/vat")
    public ResponseEntity<?> setVatCheckoutDetails(@Valid CodiceVatForm form, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getFieldErrorResponse(bindingResult);
        }
        getDistCheckoutFacade().saveOrderCodiceDetails(form.getCodiceCUP(), form.getCodiceCIG());
        getDistCheckoutFacade().saveCustomerVatDetails(form.getVat4(), form.getLegalEmail());

        return ok().build();
    }

    @PostMapping(value = "/request/invoice")
    public ResponseEntity<Void> requestInvoicePaymentMode() {
        if (getDistUserFacade().canRequestInvoicePaymentMode()) {
            sendInvoicePaymentModeRequest();
            return ok().build();
        }
        return badRequest().build();
    }

    @PostMapping(value = "/order/invoice")
    public String invoiceOrder(final RedirectAttributes redirectAttributes) throws InvalidCartException {
        final CartData cartData = getDistCheckoutFacade().getCheckoutCart();
        if (cartData.getPaymentMode() == null || BooleanUtils.isFalse(cartData.getPaymentMode().getInvoicePayment())) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_REVIEW_AND_PAY);
        }

        List<String> purchaseBlockedProductCodes = availabilityService.getPurchaseBlockedProductCodes();
        if (CollectionUtils.isNotEmpty(purchaseBlockedProductCodes)) {
            redirectAttributes.addFlashAttribute(PURCHASE_BLOCKED_PRODUCT_CODES, purchaseBlockedProductCodes);
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_BACK_ORDER_DETAILS);
        }

        OrderData orderData = getDistCheckoutFacade().placeOrder();
        setCustomerDefaultPaymentDetail(orderData);
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_PAYMENT_SUCCESS + FORWARD_SLASH + orderData.getCode());
    }

    @GetMapping(value = "/approve/invoice")
    public String submitForApproval() throws InvalidCartException {
        final CartData cart = getDistCheckoutFacade().getCheckoutCart();
        final CustomerData customer = cart.getB2bCustomerData();
        if (cart.getPaymentMode() == null || BooleanUtils.isFalse(cart.getPaymentMode().getInvoicePayment())) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_REVIEW_AND_PAY);
        }

        final B2BBudgetData budget = customer.getBudget();
        if (budget != null) {
            distB2BBudgetFacade.calculateExceededBudget(budget, cart.getTotalPrice());
            if (distB2BBudgetFacade.doesOrderRequireApproval(budget)) {
                final PriceData budgetPrice = priceDataFactory.create(PriceDataType.BUY, distB2BBudgetFacade.getExceededBudgetValue(budget),
                                                                      cart.getTotalPrice().getCurrencyIso());
                final OrderData orderData = getDistCheckoutFacade().placeOrder();
                setCustomerDefaultPaymentDetail(orderData);
                getSessionService().setAttribute(WebConstants.EXCEEDED_BUDGET, budgetPrice);
                getSessionService().setAttribute(WebConstants.SUBMIT_APPROVAL, Boolean.TRUE);
                return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_PAYMENT_SUCCESS + FORWARD_SLASH + orderData.getCode());
            }
        }
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CHECKOUT_REVIEW_AND_PAY);
    }

    private void setCustomerDefaultPaymentDetail(OrderData orderData) {
        final CustomerData currentCustomer = orderData.getB2bCustomerData();
        if (StringUtils.isEmpty(currentCustomer.getDefaultPaymentMode())) {
            getDistUserFacade().setDefaultPaymentMode(orderData.getPaymentMode().getCode());
            if (DistConstants.PaymentMethods.CREDIT_CARD.equals(orderData.getPaymentMode().getCode())) {
                getDistUserFacade().setDefaultPaymentInfo(orderData.getPaymentInfo());
            }
        } else if (orderData.getPaymentInfo() != null && DistConstants.PaymentMethods.CREDIT_CARD.equals(currentCustomer.getDefaultPaymentMode())
                && null == currentCustomer.getDefaultPaymentInfo()) {
            getDistUserFacade().setDefaultPaymentInfo(orderData.getPaymentInfo());
        }
    }

    private boolean isNewCard(String paymentInfoId) {
        return NEW_CARD_ID.equals(paymentInfoId);
    }

    @ExceptionHandler(CalculationException.class)
    public ResponseEntity<List<GlobalMessageResponse>> handleModelNotFoundException() {
        return getGlobalErrorResponse("cart.error.recalculate");
    }

    @Override
    protected boolean recalculateCartBeforePage() {
        return false;
    }
}
