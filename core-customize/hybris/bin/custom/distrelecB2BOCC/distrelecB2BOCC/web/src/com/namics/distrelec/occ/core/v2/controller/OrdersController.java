/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.CDN_TTL;
import static com.namics.distrelec.b2b.core.constants.DistCDNConstants.LOCAL_TTL;
import static de.hybris.platform.webservicescommons.cache.CacheControlDirective.PUBLIC;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.order.exceptions.PurchaseBlockedException;
import com.namics.distrelec.b2b.facades.order.DistB2BOrderFacade;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;
import com.namics.distrelec.mapping.converters.ErpOrderCodeWsDTOConverter;
import com.namics.distrelec.occ.core.order.data.ErpOrderCodeWsDTO;
import com.namics.distrelec.occ.core.order.ws.dto.OrderReferenceWsDTO;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import com.namics.distrelec.occ.core.v2.annotations.B2ERestricted;
import com.namics.distrelec.occ.core.v2.controller.exceptions.OrderException;
import com.namics.distrelec.occ.core.v2.forms.OrderHistoryForm;
import com.namics.distrelec.occ.core.v2.helper.CartsHelper;
import com.namics.distrelec.occ.core.v2.helper.OrdersHelper;

import de.hybris.platform.basecommerce.enums.CancelReason;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.CancellationRequestEntryInputWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderHistoryListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.order.OrderWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.strategies.CartLoaderStrategy;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelEntryData;
import de.hybris.platform.ordermanagementfacades.cancellation.data.OrderCancelRequestData;
import de.hybris.platform.ordermanagementfacades.order.OmsOrderFacade;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.errors.exceptions.NotFoundException;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Web Service Controller for the ORDERS resource. Most methods check orders of the user. Methods require authentication and are restricted
 * to https channel.
 */

@Controller
@RequestMapping(value = "/{baseSiteId}")
@Tag(name = "Orders")
public class OrdersController extends BaseCommerceController {
    public static final String PURCHASE_BLOCKED_PRODUCT_CODES = "PurchaseBlockedProducts";

    private static final Logger LOG = LoggerFactory.getLogger(OrdersController.class);

    private static final String MESSAGE_PLACING_ORDER_EXCEPTION = "Error by placing order with cartId: {'%s'}";

    @Resource(name = "orderFacade")
    private OrderFacade orderFacade;

    @Resource(name = "cartLoaderStrategy")
    private CartLoaderStrategy cartLoaderStrategy;

    @Resource(name = "ordersHelper")
    private OrdersHelper ordersHelper;

    @Resource(name = "omsOrderFacade")
    private OmsOrderFacade omsOrderFacade;

    @Resource(name = "cancellationRequestEntryInputListDTOValidator")
    private Validator cancellationRequestEntryInputListDTOValidator;

    @Resource(name = "wsCustomerFacade")
    private CustomerFacade customerFacade;

    @Resource(name = "erp.orderHistoryFacade")
    private OrderHistoryFacade orderHistoryFacade;

    @Resource(name = "orderReferenceDTOValidator")
    private Validator orderReferenceDTOValidator;

    @Autowired
    private ErpOrderCodeWsDTOConverter erpOrderCodeWsDTOConverter;

    @Autowired
    private CartsHelper cartsHelper;

    @Autowired
    private DistUserFacade userFacade;

    @Autowired
    private DistB2BOrderFacade distB2BOrderFacade;

    @Secured({ SecuredAccessConstants.ROLE_ANONYMOUS, SecuredAccessConstants.ROLE_CLIENT })
    @GetMapping(value = "/users/anonymous/orders/{guid}")
    @ResponseBody
    @Operation(operationId = "getGuestOrder", summary = "Get a guest order.", description = "Returns specific order details based on a specific guid. The response contains detailed order information.")
    @ApiBaseSiteIdParam
    public OrderWsDTO getGuestOrders(@Parameter(description = "Order GUID (Globally Unique Identifier)", required = true) @PathVariable final String guid,
                                     @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final OrderData orderData = orderFacade.getOrderDetailsForGUID(guid);
        if (!CustomerType.GUEST.equals(orderData.getB2bCustomerData().getCustomerType())) {
            throw new UnknownIdentifierException("No order with code: " + guid);
        }
        return getDataMapper().map(orderData, OrderWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CLIENT })
    @RequestMapping(value = "/users/{userId}/orders/{code}", method = RequestMethod.GET)
    @CacheControl(directive = PUBLIC, maxAge = LOCAL_TTL, sMaxAge = CDN_TTL)
    @Cacheable(value = "orderCache", key = "T(de.hybris.platform.commercewebservicescommons.cache.CommerceCacheKeyGenerator).generateKey(true,true,'getOrderForUserByCode',#code,#fields)")
    @ResponseBody
    @Operation(operationId = "getUserOrders", summary = "Get a order.", description = "Returns specific order details based on a specific order code. The response contains detailed order information.")
    @ApiBaseSiteIdAndUserIdParam
    public OrderWsDTO getUserOrders(@Parameter(description = "Order GUID (Globally Unique Identifier) or order CODE", required = true) @PathVariable final String code,
                                    @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        final OrderData orderData = distB2BOrderFacade.getOrderDetailsForCode(code);
        final OrderWsDTO orderWsDTO = getDataMapper().map(orderData, OrderWsDTO.class, getFields(fields, orderData));
        setInvoicePaymentModeAttributes(orderWsDTO);
        return orderWsDTO;
    }

    private String getFields(final String fields, final OrderData orderData) {
        if (CustomerType.B2E.equals(orderData.getB2bCustomerData().getCustomerType())) {
            return "code,guid,b2bCustomerData(BASIC),billingAddress";
        }
        return fields;
    }

    private void setInvoicePaymentModeAttributes(final OrderWsDTO orderWsDTO) {
        orderWsDTO.setCanRequestInvoicePaymentMode(userFacade.canRequestInvoicePaymentMode());
        orderWsDTO.setInvoicePaymentModeRequested(userFacade.isInvoicePaymentModeRequested());
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/users/{userId}/order-details/{orderCode}", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getUserOrderDetails", summary = "Get order details.", description = "Returns specific order details based on a specific order code. The response contains detailed order information.")
    @ApiBaseSiteIdAndUserIdParam
    public OrderWsDTO getUserOrderDetails(@Parameter(description = "Order code", required = true) @PathVariable final String orderCode,
                                          @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws Exception {
        try {
            final OrderData orderData = orderHistoryFacade.getOrderForCode(orderCode);
            if (orderData == null) {
                throw new UnknownIdentifierException("No Order found with code " + orderCode);
            }
            final OrderWsDTO orderDetails = getDataMapper().map(orderData, OrderWsDTO.class, fields);
            orderDetails.setIsRMARaised(isRMARaised(orderData));
            return orderDetails;
        } catch (final UnknownIdentifierException e) {
            LOG.error("Attempted to load a order that does not exist or is not visible", e);
            throw new NotFoundException(getErrorMessage("text.account.order.notfound"), "text.account.order.notfound");
        } catch (final Exception e) {
            LOG.error("Attempt to load a order failed", e);
            throw new OrderException(getErrorMessage("text.account.order.notfound"), "text.account.order.notfound", e);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/users/{userId}/update/order-reference", method = RequestMethod.POST)
    @ResponseBody
    @Operation(operationId = "updateOrderReference", summary = "Update order reference.", description = "Updates project number.")
    @ApiBaseSiteIdAndUserIdParam
    public ResponseEntity<?> updateOrderReference(@Parameter(description = "orderReferenceDTO", required = true) @RequestBody final OrderReferenceWsDTO orderReferenceDTO) {
        validate(orderReferenceDTO, "orderReferenceDTO", orderReferenceDTOValidator);
        final boolean isUpdated = orderHistoryFacade.updateProjectNumber(orderReferenceDTO.getOrderCode(), orderReferenceDTO.getWorkflowCode(),
                                                                         orderReferenceDTO.getOrderReference());
        return isUpdated ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CLIENT,
               SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/users/{userId}/fetchorders", method = RequestMethod.POST)
    @ResponseBody
    @Operation(operationId = "fetchUserOrderHistory", summary = "Fetch order history based on search criteria", description = "Returns order history data for all orders placed by a specified user for a specified base store. The response can display the results across multiple pages, if required.")
    @ApiBaseSiteIdAndUserIdParam
    public OrderHistoryListWsDTO fetchUserOrderHistory(@Parameter(description = "Order history form.") @RequestBody final OrderHistoryForm orderHistoryForm,
                                                       @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                                       final HttpServletResponse response) {

        final List<String> orgContactList = userFacade.getMemberCustomersForB2BUnit(customerFacade.getCurrentCustomer().getUnit().getUid());
        if (null != orderHistoryForm.getContactId() && !orderHistoryForm.getContactId().isEmpty()
                && !orgContactList.contains(orderHistoryForm.getContactId())) {
            throw new ForbiddenException("User is not allowed to access orders");
        }

        final OrderHistoryListWsDTO orderHistoryList = ordersHelper.searchOrderHistory(addPaginationField(fields), orderHistoryForm);

        setTotalCountHeader(response, orderHistoryList.getPagination());

        return orderHistoryList;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @B2ERestricted
    @RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.GET)
    @ResponseBody
    @Operation(operationId = "getUserOrderHistory", summary = "Get order history for user on page load.", description = "Returns order history data for all orders placed by a specified user for a specified base store. The response can display the results across multiple pages, if required.")
    @ApiBaseSiteIdAndUserIdParam
    public OrderHistoryListWsDTO getUserOrderHistory(@Parameter(description = "The number of results returned per page.") @RequestParam(defaultValue = "0") final int pageSize,
                                                     @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                                     final HttpServletResponse response) {

        final OrderHistoryListWsDTO orderHistoryList = ordersHelper.searchOrderHistory(getPageSizeOrDefault(pageSize), addPaginationField(fields));

        setTotalCountHeader(response, orderHistoryList.getPagination());

        return orderHistoryList;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/users/{userId}/orders", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    @Operation(operationId = "placeOrder", summary = "Place a order.", description = "Authorizes the cart and places the order. The response contains the new order data.")
    @ApiBaseSiteIdAndUserIdParam
    public OrderWsDTO placeOrder(@Parameter(description = "Cart code for logged in user, cart GUID for guest checkout", required = true) @RequestParam final String cartId,
                                 @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        cartLoaderStrategy.loadCart(cartId, Boolean.FALSE);
        cartsHelper.validateCartForPlaceOrder();

        try {
            getDistCheckoutFacade().checkForPurchaseBlockedProducts();
            final OrderData orderData = getDistCheckoutFacade().placeOrder();
            setCustomerDefaultPaymentDetail(orderData);
            return getDataMapper().map(orderData, OrderWsDTO.class, fields);
        } catch (final PurchaseBlockedException e) {
            throw new CartException(String.join(DistConstants.Punctuation.COMMA, e.getProductCodes()), PURCHASE_BLOCKED_PRODUCT_CODES);
        } catch (final Exception e) {
            throw new OrderException(String.format(MESSAGE_PLACING_ORDER_EXCEPTION, cartId), "order.place.error", e.getCause());
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/users/{userId}/orders/{code}/cancellation", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "doCancelOrder", summary = "Cancel an order.", description = "Cancels an order partially or completely")
    @ApiBaseSiteIdAndUserIdParam
    public void doCancelOrder(@Parameter(description = "Order code", required = true) @PathVariable final String code,
                              @Parameter(description = "Cancellation request input list for the current order.", required = true) @RequestBody final CancellationRequestEntryInputListWsDTO cancellationRequestEntryInputList) {
        validate(cancellationRequestEntryInputList, "cancellationRequestEntryInputList", cancellationRequestEntryInputListDTOValidator);
        validateUserForOrder(code);

        omsOrderFacade.createRequestOrderCancel(prepareCancellationRequestData(code, cancellationRequestEntryInputList));
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP,
               SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/users/{userId}/erpCode/{orderCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @ApiBaseSiteIdAndUserIdParam
    @Operation(operationId = "erpCode", summary = "Get order ERP code", description = "Returns order ERP code and generated voucher data if available")
    public ErpOrderCodeWsDTO checkErpOrderCode(@Parameter(required = true) @PathVariable(name = "orderCode") final String orderCode) {
        final OrderData orderData = getOrderData(orderCode);
        return erpOrderCodeWsDTOConverter.convert(orderData, null);
    }

    private OrderData getOrderData(final String code) {
        // this had to be done this way, because anonymous checkout was finished when landing on the orderConfirmation screen
        if (getUserFacade().isAnonymousUser()) {
            return orderFacade.getOrderDetailsForGUID(code);
        }
        return distB2BOrderFacade.getOrderDetailsForCode(code);
    }

    /**
     * Validates if the current user has access to the order
     *
     * @param code
     *            the order code
     * @throws NotFoundException
     *             if current user has no access to the order
     */
    protected void validateUserForOrder(final String code) {
        try {
            distB2BOrderFacade.getOrderDetailsForCode(code);
        } catch (final UnknownIdentifierException ex) {
            LOG.warn("Order not found for the current user in current BaseStore", ex);
            throw new NotFoundException("Resource not found");
        }
    }

    /**
     * It prepares the {@link OrderCancelRequestData} object by taking the order code and a map of order entry and cancel quantity and sets the
     * user
     *
     * @param code
     *            which we want to request to cancel
     * @param cancellationRequestEntryInputList
     *            map of order entry and cancel quantity
     * @return Populated {@link OrderCancelRequestData}
     */
    protected OrderCancelRequestData prepareCancellationRequestData(final String code,
                                                                    final CancellationRequestEntryInputListWsDTO cancellationRequestEntryInputList) {
        final OrderCancelRequestData cancellationRequest = new OrderCancelRequestData();

        final List<OrderCancelEntryData> cancellationEntries = cancellationRequestEntryInputList.getCancellationRequestEntryInputs().stream()
                                                                                                .map(this::mapToOrderCancelEntryData).collect(toList());

        cancellationRequest.setUserId(customerFacade.getCurrentCustomerUid());
        cancellationRequest.setOrderCode(code);
        cancellationRequest.setEntries(cancellationEntries);

        return cancellationRequest;
    }

    protected OrderCancelEntryData mapToOrderCancelEntryData(final CancellationRequestEntryInputWsDTO entryInput) {
        final OrderCancelEntryData cancelEntry = new OrderCancelEntryData();

        cancelEntry.setOrderEntryNumber(entryInput.getOrderEntryNumber());
        cancelEntry.setCancelQuantity(entryInput.getQuantity());
        cancelEntry.setCancelReason(CancelReason.CUSTOMERREQUEST);

        return cancelEntry;
    }

    private void setCustomerDefaultPaymentDetail(final OrderData orderData) {
        final CustomerData currentCustomer = getDistCheckoutFacade().getCurrentCheckoutCustomer();
        if (StringUtils.isEmpty(currentCustomer.getDefaultPaymentMode())) {
            getUserFacade().setDefaultPaymentMode(orderData.getPaymentMode().getCode());
            if (DistConstants.PaymentMethods.CREDIT_CARD.equals(orderData.getPaymentMode().getCode())) {
                getUserFacade().setDefaultPaymentInfo(orderData.getPaymentInfo());
            }
        } else {
            if (orderData.getPaymentInfo() != null && currentCustomer.getDefaultPaymentMode().equals("CreditCard")
                    && null == currentCustomer.getDefaultPaymentInfo()) {
                getUserFacade().setDefaultPaymentInfo(orderData.getPaymentInfo());
            }
        }
    }

    private boolean isRMARaised(final OrderData orderData) {
        return !orderData.getEntries().stream()
                         .filter(entries -> (null != entries && null != entries.getRmaData() && null != entries.getRmaData().getRmas()
                                 && !entries.getRmaData().getRmas().isEmpty()))
                         .collect(Collectors.toList())
                         .isEmpty();
    }
}
