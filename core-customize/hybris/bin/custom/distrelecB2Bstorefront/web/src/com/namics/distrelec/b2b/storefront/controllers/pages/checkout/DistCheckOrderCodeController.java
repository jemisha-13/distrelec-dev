/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageController;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

@Controller
@RequestMapping(DistCheckOrderCodeController.CHECK_ORDER_CODE_PATH_VARIABLE_PATTERN)
public class DistCheckOrderCodeController extends AbstractPageController {

    public static final String CHECK_ORDER_CODE_PATH_VARIABLE_PATTERN = "/checkOrder";

    public static final String ORDER_ERP_CODE_CHECK_PATH_VARIABLE_PATTERN = "/erpCode";

    @Autowired
    private DistB2BOrderFacade distB2BOrderFacade;

    @Autowired
    private DistUserService userService;

    @GetMapping(value = ORDER_ERP_CODE_CHECK_PATH_VARIABLE_PATTERN, produces = MediaType.APPLICATION_JSON_VALUE)
    public String checkErpOrderCode(@RequestParam final String code, final Model model) {
        OrderData orderData = getOrderData(code);
        populateModel(orderData, model);
        return ControllerConstants.Views.Fragments.Checkout.CheckOrderCode;
    }

    private OrderData getOrderData(String code) {
        // this had to be done this way, because anonymous checkout was finished when landing on the orderConfirmation screen
        if (userService.isAnonymousUser(userService.getCurrentUser())) {
            return distB2BOrderFacade.getOrderDetailsForGUID(code);
        }
        return distB2BOrderFacade.getOrderDetailsForCode(code);
    }

    /**
     * Populate the model with the required data which will be rendered to the client.
     *
     * @param order
     *            the order data which will be the base for populating the model.
     * @param model
     *            the model to populate.
     */
    private void populateModel(final OrderData order, final Model model) {
        final boolean success = StringUtils.isNotEmpty(order.getErpOrderCode());
        model.addAttribute("status", success ? "ok" : "waiting");
        model.addAttribute("erpCode", success ? order.getErpOrderCode() : StringUtils.EMPTY);
        if (success && checkQuotation(order)) {
            getSessionService().removeAttribute(OFFERED_QUOTES_SESSION_ATTR);
        }
        if (order.getGeneratedVoucher() != null) {
            distB2BOrderFacade.sendVoucherEmail(order.getCode(), getCurrentDatePatternFormat());
            model.addAttribute("erpVoucher", order.getGeneratedVoucher());
        }
    }

    /**
     * Check whether an order containing a quote or not.
     *
     * @param order
     *            the target order
     * @return {@code true} if the order contains a quote, otherwise {@code false}
     */
    private boolean checkQuotation(final OrderData order) {
        return order.getEntries().stream().anyMatch(OrderEntryData::isIsQuotation);
    }

    @ExceptionHandler({ UnknownIdentifierException.class })
    public String handleUnknownIdentifierException(final SystemException exception, final HttpServletRequest request) {
        final String uuidString = java.util.UUID.randomUUID().toString();
        ERROR_PAGE_LOG.error("a technical error occurred [uuid: " + uuidString + "], IP Address: " + request.getRemoteAddr() + ". " + exception.getMessage());
        request.setAttribute("uuid", uuidString);
        request.setAttribute("exception", exception);

        return FORWARD_PREFIX + "/notFound";
    }
}
