/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.b2b.storefront.annotations.RequireHardLogin;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.ThirdPartyConstants;
import com.namics.distrelec.b2b.storefront.forms.CheckoutGuestRegisterForm;
import com.namics.distrelec.b2b.storefront.forms.FeedbackNPSForm;
import com.namics.distrelec.b2b.storefront.security.GUIDCookieStrategy;
import com.namics.hybris.ffsearch.breadcrumb.Breadcrumb;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.OrderModel;

@Controller
@RequestMapping(DistCheckoutOrderConfirmationController.ORDER_CONFIRMATION_PATH_VARIABLE_PATTERN)
@RequireHardLogin
public class DistCheckoutOrderConfirmationController extends AbstractDistCheckoutController {
    private static final Logger LOG = LogManager.getLogger(DistCheckoutOrderConfirmationController.class);

    public static final String ORDER_CONFIRMATION_PATH_VARIABLE_PATTERN = "/checkout/orderConfirmation";

    public static final String ORDER_CODE_PATH_VARIABLE_PATTERN = "{orderCode:.*}";

    private static final String ORDER_CONFIRMATION_PAGE = "orderConfirmationPage";

    private static final String ORDER_CONFIRMATION_PICKUP_PAGE = "orderConfirmationPickup";

    private static final String ORDER_CONFIRMATION_APPROVAL_PAGE = "orderConfirmationApprovalPage";

    public static final String ORDER_ATTRIBUTE_NAME = "order";

    private static final String BLANK_STRING = "";

    @Autowired
    private DistB2BOrderFacade distB2BOrderFacade;

    @Autowired
    private GUIDCookieStrategy guidCookieStrategy;

    @Autowired
    private DistUserFacade userFacade;

    @Autowired
    private DistCustomerFacade customerFacade;

    @Override
    @ModelAttribute(ThirdPartyConstants.Webtrekk.WT_ORDER_CONFIRMATION_CONTENT_ID)
    public String getWtContentId(final HttpServletRequest request) {
        final StringBuilder builder = new StringBuilder();
        builder.append(getCmsSiteService().getCurrentSite().getWtDomain());
        builder.append(request.getRequestURI().substring(0, request.getRequestURI().lastIndexOf("/")));
        return builder.toString();
    }

    @RequestMapping(ORDER_CODE_PATH_VARIABLE_PATTERN)
    public String orderConfirmation(final Model model, @PathVariable final String orderCode, final HttpServletRequest request,
                                    final HttpServletResponse response) throws CMSItemNotFoundException {
        OrderData order = distB2BOrderFacade.getOrderDetailsForCode(orderCode);

        final String checkoutUser = getSessionService().getAttribute(DistConstants.Session.CHECKOUT_USER);
        if (!StringUtils.equals(checkoutUser, order.getB2bCustomerData().getUid())) {
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + "/");
        }

        addGlobalModelAttributes(model, request);
        addPaymentModeModelAttributes(model);
        model.addAttribute("pageType", PageType.OrderConfirmation);
        model.addAttribute(ORDER_ATTRIBUTE_NAME, order);

        if (isEShopGroup() && order.getB2bCustomerData() != null) {
            if (getSessionService().getAttribute("deliveryAddress") != null) {
                order.getB2bCustomerData().setEmail(((AddressData) getSessionService().getAttribute("deliveryAddress")).getEmail());
            } else if (order.getDeliveryAddress() != null && order.getDeliveryAddress().getEmail() != null) {
                order.getB2bCustomerData().setEmail(order.getDeliveryAddress().getEmail());
            }
        }
        model.addAttribute("user", order.getB2bCustomerData());

        // Webtrekk
        getDistWebtrekkFacade().prepareWebtrekkOrderParams(model, order);
        populateDigitalDatalayer(model, orderCode, request, order);
        getSessionService().removeAttribute(ThirdPartyConstants.IntelliAd.CART_TRACKING);

        final Boolean approval = getSessionService().getAttribute(WebConstants.SUBMIT_APPROVAL);
        if (Boolean.TRUE.equals(approval)) {
            final PriceData exceededBudget = getSessionService().getAttribute(WebConstants.EXCEEDED_BUDGET);
            model.addAttribute(WebConstants.EXCEEDED_BUDGET, exceededBudget);
            getSessionService().removeAttribute(WebConstants.SUBMIT_APPROVAL);
            getSessionService().removeAttribute(WebConstants.EXCEEDED_BUDGET);
            return getOrderConfirmationApprovalPage(model);
        } else if (approval != null) {
            getSessionService().removeAttribute(WebConstants.SUBMIT_APPROVAL);
        }

        setUpNPS(order, model);

        final String pickupCode = getDistCheckoutFacade().getPickupDeliveryModeCode();
        if (order.getDeliveryMode() != null && pickupCode.equals(order.getDeliveryMode().getCode())) {
            model.addAttribute("pickupLocation", order.getPickupLocation());
            return getOrderConfirmationPickupPage(model);
        }

        // Order Appoval Confirmation
        final Boolean orderApprovalConfirmation = getSessionService().getAttribute(DistConstants.Session.ORDER_APROVAL_CONFIRMATION);
        if (Boolean.TRUE.equals(orderApprovalConfirmation)) {
            getSessionService().removeAttribute(DistConstants.Session.ORDER_APROVAL_CONFIRMATION);
            model.addAttribute("orderApprovalConfirmation", Boolean.TRUE);
        }
        if (isEShopGroup() && getSessionService().getAttribute("guestForm") != null) {
            getSessionService().removeAttribute("guestForm");
        }
        if (isEShopGroup() && getSessionService().getAttribute(DistConstants.Session.B2E_ADDRESS_FORM) != null) {
            getSessionService().removeAttribute(DistConstants.Session.B2E_ADDRESS_FORM);
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePageCategoryAndPageType(digitalDatalayer,
                                                                        DigitalDatalayerConstants.PageName.Checkout.CHECKOUT,
                                                                        DigitalDatalayerConstants.PageName.Checkout.CONFIRMATION,
                                                                        DigitalDatalayerConstants.PageType.CHECKOUTPAGE);
        model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        handleAnonymousCheckout(model, request, response);

        return getOrderConfirmationPage(model);
    }

    private void handleAnonymousCheckout(Model model, HttpServletRequest request, HttpServletResponse response) {
        if (getDistCheckoutFacade().isAnonymousCheckout()) {
            model.addAttribute("checkoutGuestRegistrationForm", new CheckoutGuestRegisterForm());
            getDistCheckoutFacade().finishAnonymousCheckout();
            guidCookieStrategy.deleteCookie(request, response);
        }
    }

    private void populateDigitalDatalayer(Model model, String orderCode, HttpServletRequest request, OrderData order) {
        final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        model.addAttribute("cartDataLayer", gson.toJson(getCartDataLayerFromOrder(order)));
        try {
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            final OrderModel orderModel = getOrderFacade().getOrderModel(orderCode);
            if (isDatalayerEnabled()) {
                // getTransactionForDTM(digitalDatalayer, orderModel);
                getCartForDTM(digitalDatalayer, orderModel, request);
                digitalDatalayer.setTransaction(getDistDigitalDatalayerFacade().getTransactionData(digitalDatalayer, orderModel,
                                                                                                   customerFacade.getCurrentCustomerUid()));
                digitalDatalayer.setEventName(DigitalDatalayer.EventName.TRANSACTION);
                model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
            }
            getDistDigitalDatalayerFacade().populatePrimaryPageCategoryStepAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.TRANSACTION,
                                                                                       DigitalDatalayerConstants.PageName.Checkout.CONFIRMATION,
                                                                                       DigitalDatalayerConstants.PageType.TRANSACTIONPAGE);
        } catch (Exception ex) {
            LOG.error("{} Error while populating DTM cart datalayer on order confirmation page:", ErrorLogCode.DATALAYER_ERROR.getCode(), ex);
        }
    }

    @Override
    protected boolean recalculateCartBeforePage() {
        return false;
    }

    protected String getOrderConfirmationPage(final Model model) throws CMSItemNotFoundException {
        setupPage(model, ORDER_CONFIRMATION_PAGE);
        if (updateProfile()) {
            model.addAttribute("updateProfile", Boolean.TRUE);
            if (!customerHasDepartment()) {
                model.addAttribute("departments", userFacade.getDepartments());
            }
            if (!customerHasFunction()) {
                model.addAttribute("functions", userFacade.getFunctions());
            }
        }
        final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        getDistDigitalDatalayerFacade().populatePrimaryPageCategoryStepAndPageType(digitalDatalayer, DigitalDatalayerConstants.PageCategory.TRANSACTION,
                                                                                   DigitalDatalayerConstants.PageName.Checkout.CONFIRMATION,
                                                                                   DigitalDatalayerConstants.PageType.TRANSACTIONPAGE);
        return ControllerConstants.Views.Pages.Checkout.CheckoutConfirmationPage;
    }

    private boolean updateProfile() {
        return !isGuestUser() && needToUpdateDepartmentFunctionInformationOnCurrentCustomerProfile();
    }

    public boolean needToUpdateDepartmentFunctionInformationOnCurrentCustomerProfile() {
        return !(customerHasFunction() && customerHasDepartment());
    }

    private boolean customerHasFunction() {
        return getCurrentSessionCustomer().getDistFunction() != null && isNotBlank(getCurrentSessionCustomer().getDistFunction().getCode());
    }

    private boolean customerHasDepartment() {
        return getCurrentSessionCustomer().getContactAddress() != null && getCurrentSessionCustomer().getContactAddress().getDistDepartment() != null
                && isNotBlank(getCurrentSessionCustomer().getContactAddress().getDistDepartment().getCode());
    }

    private B2BCustomerModel getCurrentSessionCustomer() {
        return (B2BCustomerModel) getUserService().getCurrentUser();
    }

    protected String getOrderConfirmationApprovalPage(final Model model) throws CMSItemNotFoundException {
        setupPage(model, ORDER_CONFIRMATION_APPROVAL_PAGE);
        return ControllerConstants.Views.Pages.Checkout.CheckoutConfirmationApprovalPage;
    }

    protected String getOrderConfirmationPickupPage(final Model model) throws CMSItemNotFoundException {
        setupPage(model, ORDER_CONFIRMATION_PICKUP_PAGE);
        return ControllerConstants.Views.Pages.Checkout.CheckoutConfirmationPickupPage;
    }

    protected void setupPage(final Model model, final String labelId) throws CMSItemNotFoundException {
        final ContentPageModel contentPage = getContentPageForLabelOrId(labelId);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        final Breadcrumb loginBreadcrumbEntry = new Breadcrumb("#",
                                                               getMessageSource().getMessage("header.link.checkout.confirmation", null,
                                                                                             getI18nService().getCurrentLocale()),
                                                               null);
        model.addAttribute("breadcrumbs", Collections.singletonList(loginBreadcrumbEntry));
    }

    private void setUpNPS(final OrderData order, final Model model) {
        final FeedbackNPSForm npsForm = new FeedbackNPSForm();
        npsForm.setType(NPSType.ORDERCONFIRMATION.getCode());
        npsForm.setOrder(order.getCode());
        npsForm.setEmail(order.getB2bCustomerData().getEmail());
        npsForm.setCompany(order.getB2bCustomerData().getUnit().getName());
        npsForm.setCnumber(order.getB2bCustomerData().getUnit().getErpCustomerId());
        model.addAttribute("npsForm", npsForm);
    }
}
