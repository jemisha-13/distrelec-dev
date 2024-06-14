/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import static org.springframework.http.ResponseEntity.badRequest;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.service.i18n.impl.DistCommerceCommonI18NService;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Price;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Transaction;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.warehouse.WarehouseFacade;
import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageController;
import com.namics.distrelec.b2b.storefront.controllers.pages.CartPageController;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;
import com.namics.distrelec.b2b.storefront.response.FieldErrorResponse;
import com.namics.distrelec.b2b.storefront.response.GlobalMessageResponse;
import com.namics.distrelec.b2b.storefront.security.B2BUserGroupProvider;

import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.order.exceptions.CalculationException;

/**
 * Abstract Distrelec Checkout Controller.
 */
public abstract class AbstractDistCheckoutController extends AbstractPageController {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDistCheckoutController.class);

    protected static final String PAGE_CHECKOUT = "/checkout";

    protected static final String PAGE_CHECKOUT_DELIVERY = "checkoutDeliveryPage";

    protected static final String PAGE_CHECKOUT_REVIEW_AND_PAY = "checkoutReviewAndPayPage";

    protected static final String PAGE_BACKORDER_DETAILS = "checkoutBackOrderDetailsPage";

    protected static final String CHECKOUT_DELIVERY = "/checkout/delivery";

    protected static final String CHECKOUT_REVIEW_AND_PAY = "/checkout/review-and-pay";

    protected static final String CHECKOUT_BACK_ORDER_DETAILS = "/checkout/backorderDetails";

    protected static final String CHECKOUT_PAYMENT_SUCCESS = "/checkout/payment/success";

    protected static final String CHECKOUT_PAYMENT_FAILURE = "/checkout/payment/failure";

    @Autowired
    private B2BUserGroupProvider b2bUserGroupProvider;

    @Autowired
    private DistCommerceCommonI18NService distCommerceCommonI18NService;

    @Autowired
    protected WarehouseFacade warehouseFacade;

    @Override
    public void addGlobalModelAttributes(Model model, HttpServletRequest request) {
        addCommonModelAttributes(model, request);
        model.addAttribute("cartData", getDistCheckoutFacade().getCheckoutCart());
        model.addAttribute("metaRobotContent", setUpMetaRobotContent());
        model.addAttribute("forceDesktopCookie", getForceDesktopCookie(request));
        model.addAttribute("ymktTrackingEnabled", Boolean.FALSE);
        addRequestSettings(model, request);
        addCachingModelAttributes(model, request);

        if (isDatalayerEnabled()) {
            populateDatalayer(model, request);
        }

        model.addAttribute("isGuestCheckout", getDistCheckoutFacade().isAnonymousCheckout());

        CMSSiteModel site = getCmsSiteService().getCurrentSite();
        if (site != null) {
            addMicrosoftUetTag(model, site);
        }
    }

    protected void addPageEventOrLoginRegisterEvents(HttpServletRequest request, DigitalDatalayer digitalDatalayer, DigitalDatalayer.EventName eventName) {
        if (Boolean.TRUE.equals(request.getSession().getAttribute(WebConstants.CHECKOUT_LOGIN_SUCCESS))) {
            digitalDatalayer.setEventName(DigitalDatalayer.EventName.LOGIN_CHECKOUT_BILLING);
            request.getSession().removeAttribute(WebConstants.CHECKOUT_LOGIN_SUCCESS);
        } else if (Boolean.TRUE.equals(request.getSession().getAttribute(WebConstants.CHECKOUT_REGISTER_SUCCESS))) {
            digitalDatalayer.setEventName(DigitalDatalayer.EventName.REGISTER_CHECKOUT_BILLING);
            request.getSession().removeAttribute(WebConstants.CHECKOUT_REGISTER_SUCCESS);
        } else {
            digitalDatalayer.setEventName(eventName);
        }
    }

    /**
     * Check whether the current customer is a B2E customer
     *
     * @return {@code true} if the current customer is B2E customer. Else {@code false}
     */
    @ModelAttribute("isEShopGroup")
    public boolean isEShopGroup() {

        // Instead of calculation this attribute for each request, calculate it once and put the value in the session.
        return getSessionService().getOrLoadAttribute("isEShopGroup",
                                                      () -> getUserService().isMemberOfGroup(getUserService().getCurrentUser(),
                                                                                             getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID)));
    }

    protected boolean isB2BCustomer(CustomerData customer) {
        return CustomerType.B2B.equals(customer.getCustomerType()) || CustomerType.B2B_KEY_ACCOUNT.equals(customer.getCustomerType());
    }

    protected boolean isB2CCustomer(CustomerData customer) {
        return CustomerType.B2C.equals(customer.getCustomerType());
    }

    protected String validateCheckout(final Model model, final RedirectAttributes redirectModel) {
        final boolean anonymousCheckout = getDistCheckoutFacade().isAnonymousCheckout();

        if ((!b2bUserGroupProvider.isCurrentUserAuthorizedToCheckOut() || getDistCheckoutFacade().isCurrentCustomerBlocked()) && !anonymousCheckout) {
            GlobalMessages.addErrorMessage(model, "checkout.error.invalid.accountType");
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("checkout.error.invalid.accountType"));
            return CartPageController.PAGE_CART;
        }

        if (!getDistCheckoutFacade().hasCheckoutCart() || !hasItemsInCart()) {
            return CartPageController.PAGE_CART;
        }

        final Collection<PunchoutFilterResult> punchoutFilterResult = getB2bCartFacade().removeProductsWithPunchout();
        if (!punchoutFilterResult.isEmpty()) {
            redirectModel.addFlashAttribute(WebConstants.HAS_PUNCHED_OUT_PRODUCTS, Boolean.TRUE);
            model.addAttribute(WebConstants.HAS_PUNCHED_OUT_PRODUCTS, Boolean.TRUE);

            final List<String> punchedOutProducts = new ArrayList<>(punchoutFilterResult.size());
            for (final PunchoutFilterResult result : punchoutFilterResult) {
                if (result.getPunchedOutProduct() != null) {
                    punchedOutProducts.add(result.getPunchedOutProduct().getCode());
                } else {
                    LOG.warn("Can not add punch out result, because the product is empty! PunchoutResult: {}", result);
                }
            }

            final String punchedOutProductsString = StringUtils.join(punchedOutProducts, ", ");
            model.addAttribute(WebConstants.PRODUCTS_PUNCHED_OUT, punchedOutProductsString);
            redirectModel.addFlashAttribute(WebConstants.PRODUCTS_PUNCHED_OUT, punchedOutProductsString);
            return CartPageController.PAGE_CART;
        }

        // Before sending request to SAP, we validate the length of the voucher code
        final boolean voucherValid = checkVoucher(model);
        if (!voucherValid) {
            return CartPageController.PAGE_CART;
        }

        // Simulate cart calculation to check if the cart can be properly calculated
        try {
            getDistCheckoutFacade().calculateOrder(true);
            if (!hasReachedMovAmount()) {
                redirectModel.addFlashAttribute(WebConstants.MOV_DISPLAY_MESSAGE_ON_PAGE_LOAD, true);
                return CartPageController.PAGE_CART;
            }
        } catch (final CalculationException e) {
            LOG.error("{} {} Can not calculate cart. {} ", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, e.getMessage());
            GlobalMessages.addErrorMessage(model, "cart.error.recalculate");
            redirectModel.addFlashAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, Collections.singletonList("cart.error.recalculate"));
            return CartPageController.PAGE_CART;
        }

        return null;
    }

    /**
     * Check the validity of a voucher.
     * 
     * @param model
     */
    protected boolean checkVoucher(final Model model) {
        final CartData cartData = getDistCheckoutFacade().getCheckoutCart();
        final DistErpVoucherInfoData voucher = cartData.getErpVoucherInfoData();

        if (null != voucher && BooleanUtils.isTrue(voucher.getCalculatedInERP()) && BooleanUtils.isFalse(voucher.getValid())) {
            LOG.error("Voucher Code Entered is Invalid");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * Check whether the current customer is a B2E customer
     *
     * @return {@code true} if the current customer is B2E customer. Else {@code false}
     */
    @ModelAttribute("isRequestedDeliveryDateEnabled")
    public boolean isRequestedDeliveryDateEnabled() {
        return getDistCheckoutFacade().isRequestedDeliveryDateEnabledForCurrentCart();
    }

    /**
     * Get the minimum requestable delivery date
     *
     * @return {@code true} if the current customer is B2E customer. Else {@code false}
     */
    @ModelAttribute("minRequestedDeliveryDate")
    public Date getMinRequestedDeliveryDate() {
        return getDistCheckoutFacade().getMinimumRequestedDeliveryDateForCurrentCart();
    }

    /**
     * Get the minimum requestable delivery date
     *
     * @return {@code true} if the current customer is B2E customer. Else {@code false}
     */
    @ModelAttribute("maxRequestedDeliveryDate")
    public Date getMaxRequestedDeliveryDate() {
        return getDistCheckoutFacade().getMaximumRequestedDeliveryDateForCurrentCart();
    }

    @ModelAttribute("isGuestCheckout")
    public boolean isGuestCheckout() {
        return getDistCheckoutFacade().isAnonymousCheckout();
    }

    protected abstract boolean recalculateCartBeforePage();

    protected String validateDeliveryPage(CartData cartData) {
        if (null == cartData.getDeliveryMode()) {
            return "checkout.deliveryMethod.notSelected";
        }

        if (null == cartData.getBillingAddress()) {
            return "checkout.billingAddress.notSelected";
        }

        if (null == cartData.getDeliveryAddress()) {
            return "checkout.deliveryAddress.notSelected ";
        }

        if (getDistCheckoutFacade().shouldAllowEditBilling(cartData.getB2bCustomerData().getCustomerType())
                && isAddressNotValid(cartData.getBillingAddress())) {
            return "order.checkout.address.billing";
        }

        if (shouldAllowEditShipping(cartData) && isAddressNotValid(cartData.getDeliveryAddress())) {
            return "order.checkout.address.shipping";
        }

        return null;
    }

    private boolean shouldAllowEditShipping(CartData cartData) {
        if (areBillingAndShippingTheSame(cartData)) {
            return getDistCheckoutFacade().shouldAllowEditBilling(cartData.getB2bCustomerData().getCustomerType());
        }
        return true;
    }

    private boolean areBillingAndShippingTheSame(CartData cartData) {
        return Objects.nonNull(cartData.getDeliveryAddress())
                && Objects.equals(cartData.getBillingAddress().getId(), cartData.getDeliveryAddress().getId());
    }

    private boolean isAddressNotValid(AddressData addressData) {
        return !getDistCheckoutFacade().isAddressValid(addressData)
                || isUserAddressBlocked(addressData);
    }

    private boolean customerHasValidDeliveryCountry(final CustomerData b2bCustomerData) {
        final Collection<CountryModel> countries = distCommerceCommonI18NService.getAllCountries();
        final B2BUnitData unitData = b2bCustomerData.getUnit();

        for (final CountryModel country : countries) {
            if (unitData == null || unitData.getCountry() == null) {
                return false;
            }

            if (country.getIsocode().equals(unitData.getCountry().getIsocode())) {
                return true;
            }
        }
        return false;
    }

    protected String validateDetailPage() {
        final CartData cartData = getDistCheckoutFacade().getCheckoutCart();

        if (null == cartData.getDeliveryMode()) {
            return "checkout.deliveryMethod.notSelected";
        }

        if (null == cartData.getPaymentMode()) {
            return "checkout.paymentMethod.notSelected";
        }

        final DeliveryModeData deliveryMode = cartData.getDeliveryMode();
        final String deliveryCode = getDistCheckoutFacade().getPickupDeliveryModeCode();
        if (null == cartData.getDeliveryAddress() && (null == deliveryMode || !StringUtils.equals(deliveryMode.getCode(), deliveryCode))) {
            // check if the customer is in a valid delivery country
            if (!customerHasValidDeliveryCountry(cartData.getB2bCustomerData())) {
                return "checkout.deliveryAddress.delivyCountryInvalid";
            } else {
                return "checkout.deliveryAddress.notSelected";
            }
        }

        if (null != cartData.getDeliveryAddress() && !cartData.getDeliveryAddress().isShippingAddress()) {
            return "checkout.deliveryAddress.notSelected";
        }

        if (null != cartData.getDeliveryAddress() && isUserAddressBlocked(cartData.getDeliveryAddress())) {
            return "order.checkout.address.shipping";
        }
        return null;
    }

    protected boolean isUserAddressBlocked(AddressData deliveryAddress) {
        String countryIso = deliveryAddress.getCountry() != null ? deliveryAddress.getCountry().getIsocode() : null;
        return getDistCheckoutFacade().isUserAddressBlocked(deliveryAddress.getPostalCode(), deliveryAddress.getLine1(), deliveryAddress.getLine2(),
                                                            deliveryAddress.getTown(), countryIso);
    }

    protected boolean hasItemsInCart() {
        final CartData cartData = getDistCheckoutFacade().getCheckoutCart();
        if (cartData != null) {
            return CollectionUtils.isNotEmpty(cartData.getEntries());
        } else {
            return false;
        }
    }

    protected void setPaymentError(final String error, final Model model) {
        try {
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);

            if (null == digitalDatalayer.getTransaction()) {
                digitalDatalayer.setTransaction(new Transaction());
            }
            if (null == digitalDatalayer.getTransaction().getTotal()) {
                digitalDatalayer.getTransaction().setTotal(new Price());
            }
            digitalDatalayer.getTransaction().getTotal().setPaymentError(error);
            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        } catch (final Exception ex) {
            LOG.error("{} Error while adding payment error in DTM", ErrorLogCode.DATALAYER_ERROR.getCode(), ex);
        }
    }

    protected ResponseEntity<List<FieldErrorResponse>> getFieldErrorResponse(BindingResult bindingResult) {
        List<FieldErrorResponse> errorResponses = bindingResult.getFieldErrors().stream()
                                                               .map(error -> new FieldErrorResponse.Builder()
                                                                                                             .withField(error.getField())
                                                                                                             .withMessage(error.getDefaultMessage())
                                                                                                             .build())
                                                               .collect(Collectors.toList());
        return badRequest().body(errorResponses);
    }

    protected ResponseEntity<List<GlobalMessageResponse>> getGlobalErrorResponse(String message, String... params) {
        return badRequest().body(Collections.singletonList(new GlobalMessageResponse.Builder().withMessage(getLocalizedMessage(message, params))
                                                                                              .withType(GlobalMessageResponse.MessageType.ERROR).build()));
    }

    protected boolean isMultipleAddresses(List<? extends AddressData> addresses) {
        return CollectionUtils.size(addresses) > 1;
    }
}
