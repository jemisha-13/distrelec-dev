package com.namics.distrelec.occ.core.v2.controller;

import static com.namics.distrelec.b2b.core.service.order.util.AddToCartParamsBuilder.aAddToCartParamsBuilder;
import static com.namics.distrelec.occ.core.v2.controller.AddressController.ADDRESS_DOES_NOT_EXIST;
import static com.namics.distrelec.occ.core.v2.controller.AddressController.OBJECT_NAME_ADDRESS_ID;
import static com.namics.distrelec.occ.core.v2.helper.AddressHelper.CHECKOUT_GUEST_ADDRESS_MAPPING;
import static de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.google.i18n.phonenumbers.NumberParseException;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.exception.ErpCommunicationException;
import com.namics.distrelec.b2b.core.security.SanitizationService;
import com.namics.distrelec.b2b.core.service.order.exceptions.AddToCartDisabledException;
import com.namics.distrelec.b2b.core.service.order.exceptions.PunchoutException;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.facades.backorder.BackOrderFacade;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.customer.b2b.budget.DistB2BBudgetFacade;
import com.namics.distrelec.b2b.facades.message.queue.data.AddToCartBulkRequestData;
import com.namics.distrelec.b2b.facades.message.queue.data.AddToCartBulkResponseData;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.order.data.DistPaymentModeData;
import com.namics.distrelec.b2b.facades.order.data.PossibleDeliveryDatesData;
import com.namics.distrelec.b2b.facades.payment.ws.dto.DistPaymentOptionsWsDTO;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;
import com.namics.distrelec.occ.core.cart.ws.dto.*;
import com.namics.distrelec.occ.core.exceptions.InvalidPaymentInfoException;
import com.namics.distrelec.occ.core.order.data.CartDataList;
import com.namics.distrelec.occ.core.order.data.OrderEntryDataList;
import com.namics.distrelec.occ.core.order.ws.dto.AddQuotationRequestWsDTO;
import com.namics.distrelec.occ.core.product.data.PromotionResultDataList;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;
import com.namics.distrelec.occ.core.v2.annotations.B2ERestricted;
import com.namics.distrelec.occ.core.v2.annotations.CheckoutRestriction;
import com.namics.distrelec.occ.core.v2.annotations.QuotationRestriction;
import com.namics.distrelec.occ.core.v2.helper.AddressHelper;
import com.namics.distrelec.occ.core.v2.helper.CartsHelper;
import com.namics.distrelec.occ.core.v2.helper.OrdersHelper;
import com.namics.distrelec.occ.core.v2.helper.UsersHelper;
import com.namics.distrelec.occ.core.voucher.data.VoucherDataList;

import de.hybris.platform.acceleratorservices.hostedorderpage.data.HostedOrderPageData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.*;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.commercefacades.promotion.CommercePromotionRestrictionFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.voucher.VoucherFacade;
import de.hybris.platform.commercefacades.voucher.exceptions.VoucherOperationException;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commerceservices.promotion.CommercePromotionRestrictionException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commercewebservicescommons.dto.order.*;
import de.hybris.platform.commercewebservicescommons.dto.product.PromotionResultListWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.user.AddressWsDTO;
import de.hybris.platform.commercewebservicescommons.dto.voucher.VoucherListWsDTO;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartEntryException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.RequestParameterException;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdAndUserIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdUserIdAndCartIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/{baseSiteId}/users/{userId}/carts")
@CacheControl(directive = CacheControlDirective.NO_CACHE)
@Tag(name = "Carts")
@Validated
public class CartsController extends BaseCommerceController {
    public static final String CART_PRICES_ENTRIES_FIELDS = "code,guid,calculated,totalPrice(DEFAULT),totalTax(DEFAULT),totalPriceWithTax,entries(DEFAULT),deliveryCost(DEFAULT),subTotal(DEFAULT),user(DEFAULT)";

    private static final Logger LOG = LoggerFactory.getLogger(CartsController.class);

    private static final String ENTRY = "entry";

    private static final String COUPON_STATUS_CODE = "couponNotValid";

    private static final String VOUCHER_STATUS_CODE = "voucherNotValid";

    private static final String HEADER_USER_AGENT = "User-Agent";

    private static final String ADDRESS = "address";

    private static final String CART_VOUCHER_FIELDS = "code,calculated,erpVoucherInfoData(DEFAULT),totalPrice(DEFAULT),totalTax(DEFAULT),totalPriceWithTax,deliveryCost(DEFAULT),subTotal(DEFAULT),user(DEFAULT)";

    private static final String BASIC_CART_FIELDS = "code,guid,entries(BASIC),totalItems,calculated";

    private static final String REEVOO_CART_FIELDS = "code,guid,calculated,reevooEligible";

    @Autowired
    private CommercePromotionRestrictionFacade commercePromotionRestrictionFacade;

    @Autowired
    private DistB2BCartFacade b2bCartFacade;

    @Autowired
    private DistProductPriceQuotationFacade distProductPriceQuotationFacade;

    @Autowired
    private DistCustomerFacade distCustomerFacade;

    @Autowired
    private BackOrderFacade backOrderFacade;

    @Autowired
    private DistCartFacade cartFacade;

    @Autowired
    private DistB2BBudgetFacade distB2BBudgetFacade;

    @Autowired
    private SaveCartFacade saveCartFacade;

    @Autowired
    private VoucherFacade voucherFacade;

    @Autowired
    private UsersHelper usersHelper;

    @Autowired
    private AddressHelper addressHelper;

    @Autowired
    private CartsHelper cartsHelper;

    @Autowired
    private OrdersHelper ordersHelper;

    @Resource(name = "orderEntryCreateValidator")
    private Validator orderEntryCreateValidator;

    @Resource(name = "addQuotationRequestValidator")
    private Validator addQuotationRequestValidator;

    @Resource(name = "checkoutB2bUserAddressValidator")
    private Validator b2bUserAddressValidator;

    @Resource(name = "checkoutB2cUserAddressValidator")
    private Validator b2cUserAddressValidator;

    @Resource(name = "b2eUserAddressValidator")
    private Validator b2eUserAddressValidator;

    @Resource(name = "guestUserAddressValidator")
    private Validator guestUserAddressValidator;

    @Resource(name = "codiceVatFormWsDTOValidator")
    private Validator codiceVatFormWsDTOValidator;

    @Resource(name = "guestUserValidator")
    private Validator guestUserValidator;

    @Resource(name = "customerReferenceValidator")
    private Validator customerReferenceValidator;

    @Autowired
    private SanitizationService sanitizationService;

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(method = RequestMethod.GET)
    @Operation(operationId = "getCarts", summary = "Get all customer carts.", description = "Lists all customer carts.")
    @ApiBaseSiteIdAndUserIdParam
    public CartListWsDTO getCarts(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                  @Parameter(description = "Optional parameter. If the parameter is provided and its value is true, only saved carts are returned.") @RequestParam(defaultValue = "false") final boolean savedCartsOnly,
                                  @Parameter(description = "Optional pagination parameter in case of savedCartsOnly == true. Default value 0.") @RequestParam(defaultValue = DEFAULT_CURRENT_PAGE) final int currentPage,
                                  @Parameter(description = "Optional {@link PaginationData} parameter in case of savedCartsOnly == true.") @RequestParam(defaultValue = "0") final int pageSize,
                                  @Parameter(description = "Optional sort criterion in case of savedCartsOnly == true. No default value.") @RequestParam(required = false) final String sort) {
        if (getUserFacade().isAnonymousUser()) {
            throw new AccessDeniedException("Access is denied");
        }

        final CartDataList cartDataList = new CartDataList();

        final PageableData pageableData = new PageableData();
        pageableData.setCurrentPage(currentPage);
        pageableData.setPageSize(getPageSizeOrDefault(pageSize));
        pageableData.setSort(sort);
        List<CartData> allCarts = new ArrayList<>(saveCartFacade.getSavedCartsForCurrentUser(pageableData, null).getResults());
        if (!savedCartsOnly) {
            allCarts.addAll(b2bCartFacade.getCartsForCurrentUser());
        }
        // DISTRELEC-31988 Remove ghostOrder carts so they are not accidentaly picked up as an active cart
        allCarts = allCarts.stream().filter(cart -> !cart.isGhostOrder())
                           .collect(Collectors.toList());
        cartDataList.setCarts(allCarts);

        return getDataMapper().map(cartDataList, CartListWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}", method = RequestMethod.GET)
    @Operation(operationId = "getCart", summary = "Get a cart with a given identifier.", description = "Returns the cart with a given identifier.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO getCart(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                             @ApiFieldsParam @RequestParam(defaultValue = "true") final boolean removeUnavailable) throws CalculationException {
        Collection<PunchoutFilterResult> punchoutFilterResult = Collections.emptyList();
        List<CartModificationData> removedEntries = Collections.emptyList();
        List<CartModificationData> updatedEntries = Collections.emptyList();
        List<CartModificationData> updatedEntriesMOQ = Collections.emptyList();
        List<CartModificationData> blockedEntries = Collections.emptyList();

        if (removeUnavailable) {
            blockedEntries = cartFacade.removeBlockedProductsFromCart();
            punchoutFilterResult = cartFacade.removeProductsWithPunchout();
            removedEntries = cartFacade.removeEndOfLifeProductsFromCart();
            updatedEntries = cartFacade.updatePhasedOutProducts();
            updatedEntriesMOQ = cartFacade.updateEntriesWithMOQ();
        }

        getDistCheckoutFacade().prepareCartForCheckout();
        cartsHelper.resetInvalidVoucher();

        CartWsDTO cartWsDTO = getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
        cartWsDTO.setPunchedOutProducts(cartsHelper.getJoinedPunchedOutProducts(punchoutFilterResult));
        cartWsDTO.setEndOfLifeProducts(cartsHelper.getProductCodesFromCartModification(removedEntries));
        cartWsDTO.setPhasedOutProducts(cartsHelper.getProductCodesFromCartModification(updatedEntries));
        cartWsDTO.setUpdatedMOQProducts(cartsHelper.getProductCodesFromCartModification(updatedEntriesMOQ));
        cartWsDTO.setBlockedProducts(cartsHelper.getJoinedBlockedProducts(blockedEntries));
        return cartWsDTO;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/mini", method = RequestMethod.GET)
    @Operation(operationId = "getMiniCart", summary = "Get a mini cart with a given identifier.", description = "Returns the mini cart with a given identifier.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO getMiniCart() {
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, BASIC_CART_FIELDS);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(operationId = "createCart", summary = "Creates or restore a cart for a user.", description = "Creates a new cart or restores an anonymous cart as a user's cart (if an old Cart Id is given in the request).")
    @ApiBaseSiteIdAndUserIdParam
    public CartWsDTO createCart(@Parameter(description = "Anonymous cart GUID.") @RequestParam(required = false) final String oldCartId,
                                @Parameter(description = "The GUID of the user's cart that will be merged with the anonymous cart.") @RequestParam(required = false) final String toMergeCartGuid,
                                @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (StringUtils.isNotEmpty(oldCartId)) {
            validateUserIsNotAnonymous();
            validateOldCartIsAnonymous(oldCartId);
            if (StringUtils.isNotEmpty(toMergeCartGuid)) {
                validateNewCartBelongsToUser(toMergeCartGuid);
            }
            return restoreAnonymousCartAndMerge(oldCartId, getToMergeCartGuid(toMergeCartGuid), fields);
        } else if (StringUtils.isNotEmpty(toMergeCartGuid)) {
            validateNewCartBelongsToUser(toMergeCartGuid);
            return restoreSavedCart(oldCartId, toMergeCartGuid, fields);
        }
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
    }

    private CartWsDTO restoreSavedCart(String oldCartId, String toMergeCartGuid, String fields) {
        try {
            b2bCartFacade.restoreSavedCart(toMergeCartGuid);
            return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
        } catch (final CommerceCartRestorationException e) {
            throw new CartException("Couldn't restore cart", CANNOT_RESTORE, oldCartId, e);
        }
    }

    private CartWsDTO restoreAnonymousCartAndMerge(String oldCartId, String toMergeCartGuid, String fields) {
        try {
            b2bCartFacade.restoreAnonymousCartAndMerge(oldCartId, toMergeCartGuid);
            return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
        } catch (final CommerceCartMergingException e) {
            throw new CartException("Couldn't merge carts", CANNOT_MERGE, e);
        } catch (final CommerceCartRestorationException e) {
            throw new CartException("Couldn't restore cart", CANNOT_RESTORE, e);
        }
    }

    private String getToMergeCartGuid(String toMergeCartGuid) {
        return StringUtils.isNotBlank(toMergeCartGuid) ? toMergeCartGuid : cartsHelper.getSessionCart().getGuid();
    }

    private void validateNewCartBelongsToUser(String toMergeCartGuid) {
        if (!getDistCommerceWebServicesCartFacade().isCurrentUserCart(toMergeCartGuid)) {
            throw new CartException("Cart is not current user's cart", CANNOT_RESTORE, toMergeCartGuid);
        }
    }

    private void validateOldCartIsAnonymous(String oldCartId) {
        if (!getDistCommerceWebServicesCartFacade().isAnonymousUserCart(oldCartId)) {
            throw new CartException("Cart is not anonymous", CANNOT_RESTORE, oldCartId);
        }
    }

    private void validateUserIsNotAnonymous() {
        if (getUserFacade().isAnonymousUser()) {
            throw new CartException("Anonymous user is not allowed to copy cart!", CANNOT_RESTORE);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "removeCart", summary = "Deletes a cart with a given cart id.", description = "Deletes a cart with a given cart id.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void removeCart() {
        b2bCartFacade.removeSessionCart();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{cartId}/email", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "replaceCartGuestUser", summary = "Assigns an email to the cart.", description = "Assigns an email to the cart. This step is required to make a guest checkout.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void replaceCartGuestUser(@Parameter(description = "Email of the guest user. It will be used during the checkout process.", required = true) @RequestParam final String email)
                                                                                                                                                                                          throws DuplicateUidException {
        validate(email, "email", guestUserValidator);
        distCustomerFacade.createGuestUserForAnonymousCheckout(DistConstants.User.GUEST, cartsHelper.createGuestRegisterData(email));
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/entries", method = RequestMethod.GET)
    @Operation(operationId = "getCartEntries", summary = "Get cart entries.", description = "Returns cart entries.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public OrderEntryListWsDTO getCartEntries(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        LOG.debug("getCartEntries");
        final OrderEntryDataList dataList = new OrderEntryDataList();
        dataList.setOrderEntries(getSessionCart().getEntries());
        return getDataMapper().map(dataList, OrderEntryListWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/entries", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE })
    @Operation(operationId = "addToCart", summary = "Add a product to a cart.", description = "Adds a product to a cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartModificationWsDTO addToCart(@PathVariable String cartId,
                                           @Parameter(description = "Request body parameter that contains details such as the product code (product.code), the quantity of product (quantity), and the pickup store name (deliveryPointOfService.name).The DTO is in XML or .json format.", required = true) @RequestBody final OrderEntryWsDTO entry,
                                           @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                           @Parameter(description = "q") @RequestParam(required = false) final String searchQuery)
                                                                                                                                   throws CommerceCartModificationException {
        entry.setQuantity(cartsHelper.getOrderQuantity(entry));
        validate(entry, ENTRY, orderEntryCreateValidator);
        final String code = entry.getProduct().getCode();
        try {
            b2bCartFacade.checkIsProductBuyable(code, entry.getQuantity());
            CartModificationData cartModificationData = b2bCartFacade.addToCart(buildAddToCartParams(entry, searchQuery));
            return getDataMapper().map(cartModificationData, CartModificationWsDTO.class, fields);
        } catch (UnknownIdentifierException ex) {
            throw new CartEntryException("Add to cart failed, product not found", "cart.product.not_found", ex);
        }
    }

    private AddToCartParams buildAddToCartParams(OrderEntryWsDTO entry, String searchQuery) {
        return aAddToCartParamsBuilder().withProductCode(entry.getProduct().getCode())
                                        .withQuantity(entry.getQuantity())
                                        .withSearchQuery(searchQuery)
                                        .withAddedFrom(entry.getAddedFrom())
                                        .build();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.GET)
    @Operation(operationId = "getCartEntry", summary = "Get the details of the cart entries.", description = "Returns the details of the cart entries.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public OrderEntryWsDTO getCartEntry(
                                        @Parameter(description = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable final int entryNumber,
                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        return getDataMapper().map(cartFacade.getCartEntryForNumber(entryNumber), OrderEntryWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PUT)
    @Operation(operationId = "replaceCartEntry", summary = "Selector API for changing the calibration of product", description = "Calibrated and non-calibrated are different products")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO replace(
                             @Parameter(description = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable final int entryNumber,
                             @Parameter(description = "source product code", required = true) @RequestBody final CalibrationFormWsDTO form) throws Exception {
        cartsHelper.replace(form, entryNumber);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, CART_PRICES_ENTRIES_FIELDS);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.PATCH, consumes = { MediaType.APPLICATION_JSON_VALUE,
                                                                                                          MediaType.APPLICATION_XML_VALUE })
    @Operation(operationId = "updateCartEntry", summary = "Update quantity and store details of a cart entry.", description = "Updates the quantity of a single cart entry and the details of the store where the cart entry will be picked up.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartModificationWsDTO updateCartEntry(@PathVariable String cartId,
                                                 @Parameter(description = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable final long entryNumber,
                                                 @Parameter(description = "Represents quantity to which entry will be updated", required = true) @RequestParam final long quantity,
                                                 @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        try {
            cartsHelper.validateEntryForUpdate(entryNumber, quantity);
            CartModificationData cartModificationData = b2bCartFacade.updateCartEntry(entryNumber, quantity, Boolean.FALSE);
            return getDataMapper().map(cartModificationData, CartModificationWsDTO.class, fields);
        } catch (final Exception ex) {
            throw new CartEntryException("Adding entry to cart has failed", ex.getMessage(), ex);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/entries/{entryNumber}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "removeCartEntry", summary = "Deletes cart entry.", description = "Deletes cart entry.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO removeCartEntry(
                                     @Parameter(description = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @PathVariable final long entryNumber)
                                                                                                                                                                                                                                                          throws CommerceCartModificationException {
        b2bCartFacade.updateCartEntry(entryNumber, 0, Boolean.FALSE);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, CART_PRICES_ENTRIES_FIELDS);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT })
    @RequestMapping(value = "/{cartId}/quotation/{quotationId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "removeCartQuotationEntry", summary = "Deletes cart quotation entry.", description = "Deletes cart quotation entry.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO removeQuotation(@Parameter(description = "Quotation ID", required = true) @PathVariable String quotationId) throws CommerceCartModificationException {
        b2bCartFacade.removeQuotationFromCart(quotationId);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, CART_PRICES_ENTRIES_FIELDS);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CLIENT,
               SecuredAccessConstants.ROLE_ANONYMOUS })
    @ApiBaseSiteIdUserIdAndCartIdParam
    @RequestMapping(value = "/{cartId}/update/reference", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "updateEntryReference", summary = "Update cart entry reference", description = "Update cart entry reference")
    public CartModificationWsDTO setReferenceText(
                                                  @Parameter(description = "customer reference to cart entry", required = true) @RequestParam final String customerReference,
                                                  @Parameter(description = "The entry number. Each entry in a cart has an entry number. Cart entries are numbered in ascending order, starting with zero (0).", required = true) @RequestParam final long entryNumber,
                                                  @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        validate(customerReference, "customerReference", customerReferenceValidator);
        try {
            CartModificationData cartModificationData = b2bCartFacade.updateCartEntry(entryNumber, customerReference);
            return getDataMapper().map(cartModificationData, CartModificationWsDTO.class, fields);
        } catch (final CommerceCartModificationException ex) {
            String errorMessage = String.format("{%s} {%s} Could not update customer reference text on order entry with entryNumber {%s}", ex,
                                                DistConstants.ErrorLogCode.CART_CALCULATION_ERROR,
                                                entryNumber);
            LOG.error("{} {} Could not update customer reference text on order entry with entryNumber {}", ex,
                      DistConstants.ErrorLogCode.CART_CALCULATION_ERROR,
                      entryNumber);
            throw new CartEntryException(errorMessage);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(path = "/{cartId}/possibleDeliveryDates", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "getPossibleDeliveryDates", summary = "Get possible delivery dates in checkout", description = "Returns min and max date for current cart")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PossibleDeliveryDatesWsDTO getPossibleDeliveryDates(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        PossibleDeliveryDatesData response = new PossibleDeliveryDatesData();
        response.setMinRequestedDeliveryDateForCurrentCart(getDistCheckoutFacade().getMinimumRequestedDeliveryDateForCurrentCart());
        response.setMaxRequestedDeliveryDateForCurrentCart(getDistCheckoutFacade().getMaximumRequestedDeliveryDateForCurrentCart());
        return getDataMapper().map(response, PossibleDeliveryDatesWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT })
    @CheckoutRestriction
    @Operation(operationId = "schedule", summary = "Change the a shipping date for the cart.", description = "Change the a shipping date for the cart as per customer request.")
    @PostMapping(value = "/{cartId}/schedule")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO scheduleRequestDeliveryDate(@RequestBody(required = false) final String date,
                                                 @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws ParseException,
                                                                                                                                      CalculationException {
        if (StringUtils.isNotEmpty(date)) {
            if (!getDistCheckoutFacade().isScheduleDeliveryAllowedForCurrentCart()) {
                throw new CartException("Delivery mode not supported", INVALID);
            }
            String dateFormatForLanguage = getDataFormatForCurrentCmsSite();
            Date deliveryDate = getDistCheckoutFacade().calculateScheduledDeliveryDate(date, dateFormatForLanguage);
            getDistCheckoutFacade().requestDeliveryDate(deliveryDate);
        } else {
            getDistCheckoutFacade().requestDeliveryDate(null);
        }
        b2bCartFacade.recalculateCart();
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "replaceCartDeliveryAddress", summary = "Sets a delivery address for the cart.", description = "Sets a delivery address for the cart. The address country must be placed among the delivery countries of the current base store.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void replaceCartDeliveryAddress(@Parameter(description = "Address identifier", required = true) @RequestParam final String addressId) {
        final AddressData address = getUserFacade().getAddressForCode(addressId);
        if (isNull(address) || !address.isShippingAddress()) {
            throw new CartException("Address is not a shipping address", INVALID);
        }
        getDistCheckoutFacade().setDeliveryAddress(address, Boolean.TRUE);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/addresses/delivery", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "removeCartDeliveryAddress", summary = "Deletes the delivery address from the cart.", description = "Deletes the delivery address from the cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void removeCartDeliveryAddress() {
        if (!getDistCheckoutFacade().removeDeliveryAddress()) {
            throw new CartException("Cannot reset address!", CANNOT_RESET_ADDRESS);
        }
    }

    @RequestMapping(path = "/{cartId}/addresses/billing", method = RequestMethod.PUT)
    @CheckoutRestriction
    @Operation(operationId = "setBillingAddress", summary = "Set billing address for checkout", description = "Updates the billing address with given")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseStatus(HttpStatus.OK)
    public void setBillingAddress(@Parameter(required = true) @RequestParam final String addressId) {
        final AddressData address = getUserFacade().getAddressForCode(addressId);
        if (isNull(address) || !address.isBillingAddress()) {
            throw new CartException("Address is not a billing address", INVALID);
        }
        getDistCheckoutFacade().setPaymentAddress(address);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/deliverymodes", method = RequestMethod.GET)
    @Operation(operationId = "getCartDeliveryModes", summary = "Get the delivery mode selected for the cart.", description = "Returns the delivery mode selected for the cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public DeliveryModeListWsDTO getCartDeliveryModes(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        DeliveryModeListWsDTO response = new DeliveryModeListWsDTO();
        boolean isPickupAllowed = getDistCheckoutFacade().isPickupAllowedForCurrentCart();
        response.setDeliveryModes(getDataMapper().mapAsList(getDistCheckoutFacade().getValidDeliveryModesForCurrentCart(isPickupAllowed),
                                                            DeliveryModeWsDTO.class, fields));
        response.setWarehouses(getDataMapper().mapAsList(cartsHelper.getValidWarehouse(isPickupAllowed), WarehouseWsDTO.class, fields));
        return response;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/deliverymode", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "replaceCartDeliveryMode", summary = "Sets the delivery mode for a cart.", description = "Sets the delivery mode with a given identifier for the cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO replaceCartDeliveryMode(
                                             @Parameter(description = "Delivery mode identifier (code)", required = true) @RequestParam final String deliveryModeId,
                                             @Parameter(description = "Warehouse identifier (code)", required = false) @RequestParam(required = false) final String wareHouseId,
                                             @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws Exception {
        cartsHelper.setDeliveryMode(deliveryModeId, wareHouseId);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/deliverymode", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "removeCartDeliveryMode", summary = "Deletes the delivery mode from the cart.", description = "Deletes the delivery mode from the cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void removeCartDeliveryMode() {
        LOG.debug("removeCartDeliveryMode");
        if (!getDistCheckoutFacade().removeDeliveryMode()) {
            throw new CartException("Cannot reset delivery mode!", CANNOT_RESET_DELIVERYMODE);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/payments", method = RequestMethod.GET)
    @Operation(operationId = "getPaymentMethods", summary = "Fetch all payment methods from the cart", description = "returns payment informations")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public DistPaymentOptionsWsDTO getPayments(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<DistPaymentModeData> paymentModes = getUserFacade().getSupportedPaymentModesForUser();
        DistPaymentOptionsWsDTO response = usersHelper.getDistPaymentOptionsWsDTO(fields, paymentModes);
        final PaymentModeListWsDTO paymentModeList = new PaymentModeListWsDTO();
        paymentModeList.setPaymentModes(getDataMapper().mapAsList(paymentModes, PaymentModeWsDTO.class, fields));
        usersHelper.setInvoicePaymentModeAttributes(response);
        return response;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/payment/paymentMode", method = RequestMethod.PUT)
    @Operation(operationId = "paymentMode", summary = "Sets the current payment mode on the cart and removes existing payment info", description = "Sets the current payment on the cart, identified by code, and removes any existing payment information.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PaymentModeWsDTO paymentMode(@Parameter(description = "The payment mode", required = true) @RequestParam final String paymentModeCode,
                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws CalculationException {
        final DistPaymentModeData paymentMode = getDistCheckoutFacade().getPaymentModeForCode(paymentModeCode);
        if (nonNull(paymentMode)) {
            CCPaymentInfoData paymentInfoData = cartsHelper.applyOrRemovePaymentInfo(paymentMode);
            getDistCheckoutFacade().setPaymentMode(paymentMode);
            b2bCartFacade.recalculateCart();
            PaymentModeWsDTO paymentModeWsDTO = getDataMapper().map(paymentMode, PaymentModeWsDTO.class, fields);
            if (nonNull(paymentInfoData)) {
                PaymentDetailsWsDTO paymentDetailsWsDTO = getDataMapper().map(paymentInfoData, PaymentDetailsWsDTO.class, DEFAULT_FIELD_SET);
                paymentModeWsDTO.setPaymentDetails(paymentDetailsWsDTO);
            }
            return paymentModeWsDTO;
        }
        throw new CartException("Payment mode not supported", INVALID);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/promotions", method = RequestMethod.GET)
    @Operation(operationId = "getCartPromotions", summary = "Get information about promotions applied on cart.", description = "Returns information about the promotions applied on the cart. "
                                                                                                                               + "Requests pertaining to promotions have been developed for the previous version of promotions and vouchers, and as a result, some of them "
                                                                                                                               + "are currently not compatible with the new promotions engine.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PromotionResultListWsDTO getCartPromotions(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        LOG.debug("getCartPromotions");
        final List<PromotionResultData> appliedPromotions = new ArrayList<>();
        final List<PromotionResultData> orderPromotions = getSessionCart().getAppliedOrderPromotions();
        final List<PromotionResultData> productPromotions = getSessionCart().getAppliedProductPromotions();
        appliedPromotions.addAll(orderPromotions);
        appliedPromotions.addAll(productPromotions);

        final PromotionResultDataList dataList = new PromotionResultDataList();
        dataList.setPromotions(appliedPromotions);
        return getDataMapper().map(dataList, PromotionResultListWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/promotions/{promotionId}", method = RequestMethod.GET)
    @Operation(operationId = "getCartPromotion", summary = "Get information about promotion applied on cart.", description = "Returns information about a promotion (with a specific promotionId), that has "
                                                                                                                             + "been applied on the cart. Requests pertaining to promotions have been developed for the previous version of promotions and vouchers, and as a result, some "
                                                                                                                             + "of them are currently not compatible with the new promotions engine.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PromotionResultListWsDTO getCartPromotion(@Parameter(description = "Promotion identifier (code)", required = true) @PathVariable final String promotionId,
                                                     @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        LOG.debug("getCartPromotion: promotionId = {}", sanitize(promotionId));
        final List<PromotionResultData> appliedPromotions = new ArrayList<>();
        final List<PromotionResultData> orderPromotions = getSessionCart().getAppliedOrderPromotions();
        final List<PromotionResultData> productPromotions = getSessionCart().getAppliedProductPromotions();
        for (final PromotionResultData prd : orderPromotions) {
            if (prd.getPromotionData().getCode().equals(promotionId)) {
                appliedPromotions.add(prd);
            }
        }
        for (final PromotionResultData prd : productPromotions) {
            if (prd.getPromotionData().getCode().equals(promotionId)) {
                appliedPromotions.add(prd);
            }
        }

        final PromotionResultDataList dataList = new PromotionResultDataList();
        dataList.setPromotions(appliedPromotions);
        return getDataMapper().map(dataList, PromotionResultListWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/promotions", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "doApplyCartPromotion", summary = "Enables promotions based on the promotionsId of the cart.", description = "Enables a promotion for the order based on the promotionId defined for the cart. "
                                                                                                                                          + "Requests pertaining to promotions have been developed for the previous version of promotions and vouchers, and as a result, some of them are currently not compatible "
                                                                                                                                          + "with the new promotions engine.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void doApplyCartPromotion(@Parameter(description = "Promotion identifier (code)", required = true) @RequestParam final String promotionId)
                                                                                                                                                      throws CommercePromotionRestrictionException {
        LOG.debug("doApplyCartPromotion: promotionId = {}", sanitize(promotionId));
        commercePromotionRestrictionFacade.enablePromotionForCurrentCart(promotionId);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/promotions/{promotionId}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "removeCartPromotion", summary = "Disables the promotion based on the promotionsId of the cart.", description = "Disables the promotion for the order based on the promotionId defined for the cart. "
                                                                                                                                             + "Requests pertaining to promotions have been developed for the previous version of promotions and vouchers, and as a result, some of them are currently not compatible with "
                                                                                                                                             + "the new promotions engine.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void removeCartPromotion(@Parameter(description = "Promotion identifier (code)", required = true) @PathVariable final String promotionId)
                                                                                                                                                     throws CommercePromotionRestrictionException {
        LOG.debug("removeCartPromotion: promotionId = {}", sanitize(promotionId));
        commercePromotionRestrictionFacade.disablePromotionForCurrentCart(promotionId);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/vouchers", method = RequestMethod.GET)
    @Operation(operationId = "getCartVouchers", summary = "Get a list of vouchers applied to the cart.", description = "Returns a list of vouchers applied to the cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public VoucherListWsDTO getCartVouchers(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        LOG.debug("getVouchers");
        final VoucherDataList dataList = new VoucherDataList();
        dataList.setVouchers(voucherFacade.getVouchersForCart());
        return getDataMapper().map(dataList, VoucherListWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CLIENT,
               SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/vouchers", method = RequestMethod.POST)
    @Operation(operationId = "redeemVoucher", summary = "Applies a voucher based on the voucherId defined for the cart.", description = "Applies a voucher based on the voucherId defined for the cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO redeemVoucher(@Parameter(description = "Voucher identifier (code)", required = true) @RequestParam final String voucherId)
                                                                                                                                                throws VoucherOperationException,
                                                                                                                                                CalculationException {
        cartsHelper.redeemOrResetVoucher(voucherId);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, CART_VOUCHER_FIELDS);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CLIENT,
               SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/vouchers/{voucherId}", method = RequestMethod.DELETE)
    @Operation(operationId = "removeCartVoucher", summary = "Deletes a voucher defined for the current cart.", description = "Deletes a voucher based on the voucherId defined for the current cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO removeCartVoucher(@Parameter(description = "Voucher identifier (code)") @PathVariable final String voucherId) throws CalculationException {
        LOG.debug("release voucher : voucherCode = {}", sanitize(voucherId));
        b2bCartFacade.resetVoucherOnCurrentCart();
        b2bCartFacade.recalculateCart();
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, CART_VOUCHER_FIELDS);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/emptyCart", method = RequestMethod.POST)
    @Operation(operationId = "emptyCart", summary = "Delete the cart entries but retain the session cart as is", description = "Delete the cart entries but retain the session cart as is")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO emptyCart(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws CalculationException {
        CartData cartData = b2bCartFacade.emptyCart();
        return getDataMapper().map(cartData, CartWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(path = "/{cartId}/bulk", method = RequestMethod.POST)
    @Operation(operationId = "addToCartBulk", summary = "Add products to cart (quick order)", description = "Add products to cart with quick order")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public AddToCartBulkResponseWsDTO addToCartBulk(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields,
                                                    @RequestBody final AddToCartBulkRequestData addToCartBulkRequestData) throws AddToCartDisabledException {
        AddToCartBulkResponseData response = b2bCartFacade.addToCartBulk(addToCartBulkRequestData);
        return getDataMapper().map(response, AddToCartBulkResponseWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @B2ERestricted
    @QuotationRestriction
    @RequestMapping(value = "/{cartId}/add-quotation", method = RequestMethod.POST)
    @Operation(operationId = "addQuotationToCart", summary = "Add quotation items to the cart", description = "Add quotation items to the cart")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO addQuotationToCart(
                                        @Parameter(description = "Add quotation request", required = true) @RequestBody final AddQuotationRequestWsDTO addQuotationRequest,
                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws CommerceCartModificationException {
        validate(addQuotationRequest, "addQuotationRequest", addQuotationRequestValidator);
        b2bCartFacade.addQuotationInToCart(addQuotationRequest.getQuotationId(), cartsHelper.getProductQuantities(addQuotationRequest, fields), Boolean.FALSE);
        return getDataMapper().map(b2bCartFacade.getSessionCart(), CartWsDTO.class, CART_PRICES_ENTRIES_FIELDS);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @RequestMapping(path = "/{cartId}/requestQuote", method = RequestMethod.POST)
    @QuotationRestriction
    @Operation(operationId = "submitQuote", summary = "Creates a quote for price in cart")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public QuoteStatusWsDTO submitQuote(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (b2bCartFacade.hasSessionCart()) {
            return getDataMapper().map(distProductPriceQuotationFacade.createCartQuotation(), QuoteStatusWsDTO.class, fields);
        } else {
            LOG.error("Current user has no cart!");
            throw new CartException("Current user has no cart!", "cart.missing");
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/recalculate", method = RequestMethod.PATCH)
    @Operation(operationId = "recalculateCart", summary = "Recalculate a cart with a given identifier.", description = "Recalculate a cart with a given identifier.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO recalculateCart(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws CalculationException {
        return getCart(fields, true);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(path = "/{cartId}/sendToFriend", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "sendToFriend", summary = "Sends the current cart to friend via email")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public void sendCompareListToFriend(@Parameter(description = "User's object for registration.", required = true) @RequestBody final SendToFriendFormWsDTO form) {
        cartsHelper.shareCartPdfWithFriends(form);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/setCompleteDelivery", method = RequestMethod.POST)
    @Operation(operationId = "setCompleteDelivery", summary = "Set the complete delivery date ", description = "When delivery is completed this API Call sets the completion of the given cart")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseStatus(HttpStatus.OK)
    public CartWsDTO setCompleteDelivery(@Parameter(description = "User's object for registration.", required = true) @RequestParam final boolean completeDelivery)
                                                                                                                                                                    throws CalculationException {
        if (getDistCheckoutFacade().isCompleteDeliveryPossible()) {
            getDistCheckoutFacade().setCompleteDeliveryOnCart(BooleanUtils.toBoolean(completeDelivery));
            b2bCartFacade.recalculateCart();
            return getDataMapper().map(getSessionCart(), CartWsDTO.class);
        }
        throw new CartException("Cart doesn't allow completing delivery", INVALID);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @CheckoutRestriction
    @RequestMapping(path = "/{cartId}/payment/requestInvoicePaymentMode", method = RequestMethod.PUT)
    @Operation(operationId = "requestInvoicePaymentMode", summary = "Request for Invoice Payment mode", description = "Creates an event for sending invoice payment method")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseStatus(HttpStatus.OK)
    public void requestInvoicePaymentMode() {
        cartsHelper.requestInvoicePaymentMode();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(path = "/{cartId}/payment/hiddenPaymentForm", method = RequestMethod.GET)
    @CheckoutRestriction
    @Operation(operationId = "hiddenPaymentForm", summary = "Checkout hidden payment form", description = "Returns the form for submitting the payment")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public HostedOrderPageWsDTO hiddenPaymentForm(final HttpServletRequest request,
                                                  @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        cartsHelper.validateCartForPlaceOrder();
        final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent = DistSiteBaseUrlResolutionService.MobileUserAgent.getByAgentString(
                                                                                                                                             request.getHeader(HEADER_USER_AGENT));
        final Map<String, String> paymentParameters = getDistCheckoutFacade().getPaymentParameters(userAgent);
        final HostedOrderPageData hostedOrderPageData = new HostedOrderPageData();
        hostedOrderPageData.setParameters(paymentParameters);
        hostedOrderPageData.setPostUrl(paymentParameters.get("URL"));
        hostedOrderPageData.setDebugMode(Boolean.TRUE);
        LOG.debug("Uid: {} HiddenPaymentForm payment parameters:{}", getUserService().getCurrentUser().getUid(), paymentParameters);

        return getDataMapper().map(hostedOrderPageData, HostedOrderPageWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/setProjectNumber", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @B2ERestricted
    @ApiBaseSiteIdUserIdAndCartIdParam
    @Operation(operationId = "setProjectNumber", summary = "Set project number", description = "Set project number")
    public void setProjectNumber(@RequestParam(required = false) final String projectNumber) {
        validate(projectNumber, "projectNumber", customerReferenceValidator);
        getDistCheckoutFacade().setProjectNumber(projectNumber);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @RequestMapping(value = "/{cartId}/setCostCenter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdUserIdAndCartIdParam
    @Operation(operationId = "setCostCenter", summary = "Set cost center", description = "Set cost center")
    public void setCostCenter(@Parameter(required = true) @RequestParam final String costCenter) {
        getDistCheckoutFacade().setCostCenter(costCenter);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_ANONYMOUS })
    @RequestMapping(value = "/{cartId}/setOrderNote", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ApiBaseSiteIdUserIdAndCartIdParam
    @Operation(operationId = "setOrderNote", summary = "Set order note", description = "Set order note")
    public void setOrderNote(@Parameter(required = true) @RequestParam String note) {
        getDistCheckoutFacade().setOrderNote(note);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP,
               SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/payment/details", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    @Operation(operationId = "replaceCcPaymentDetails", summary = "Sets credit card payment details for the cart.", description = "Sets credit card payment details for the cart.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public PaymentDetailsWsDTO replaceCcPaymentDetails(
                                                       @Parameter(description = "Payment details identifier.", required = true) @RequestParam final String paymentDetailsId)
                                                                                                                                                                             throws InvalidPaymentInfoException {
        if (StringUtils.equals(paymentDetailsId, DistConstants.PaymentMethods.NEW_CREDIT_CARD)) {
            getDistCheckoutFacade().removePaymentInfo();
        } else {
            boolean isSuccessful = getDistCheckoutFacade().setPaymentDetails(paymentDetailsId);
            if (!isSuccessful) {
                throw new InvalidPaymentInfoException(paymentDetailsId);
            }
        }
        return getDataMapper().map(getDistCheckoutFacade().getPaymentInfo(), PaymentDetailsWsDTO.class, DEFAULT_FIELD_SET);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @ApiBaseSiteIdUserIdAndCartIdParam
    @RequestMapping(value = "/{cartId}/payment/success", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "checkoutPaymentSuccess", summary = "returns the redirects to the payment confirmation page when the checkout payment is successful", description = "This methods returns the redirects of the customer to the payment confirmation page when the checkout payment is successful.")
    public CheckoutPaymentResponseWsDTO checkoutPaymentSuccess(@Parameter(required = true) @RequestBody final Map<String, String> allParameters)
                                                                                                                                                 throws Exception {
        return ordersHelper.checkoutPaymentSuccess(allParameters);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @ApiBaseSiteIdUserIdAndCartIdParam
    @RequestMapping(value = "/{cartId}/payment/failure", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "checkoutPaymentFailed", summary = "returns the redirects to the payment confirmation when the checkout payment is failure", description = "This methods returns the redirects of the customer to the payment confirmation page when the checkout payment is failure.")
    public CheckoutPaymentResponseWsDTO checkoutPaymentFailed(@Parameter(required = true) @RequestBody final Map<String, String> allParameters)
                                                                                                                                                throws Exception {
        return ordersHelper.checkoutPaymentFailed(allParameters);
    }

    @Secured(SecuredAccessConstants.ROLE_ANONYMOUS)
    @RequestMapping(value = "/{cartId}/addresses/guest", method = RequestMethod.POST)
    @Operation(operationId = "addGuestAddress", summary = "This methods adds address to guest user.", description = "Returns address data")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO createGuestAddress(@Parameter(description = "Address object.", required = true) @RequestBody final AddressWsDTO address,
                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) String fields) throws NumberParseException {
        validate(address, ADDRESS, guestUserAddressValidator);
        AddressData addressData = getDataMapper().map(address, AddressData.class, CHECKOUT_GUEST_ADDRESS_MAPPING);
        addressData.setShippingAddress(Boolean.TRUE);
        addressData.setVisibleInAddressBook(Boolean.TRUE);

        getDistCheckoutFacade().createGuestAddress(addressData);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
    }

    @Secured(SecuredAccessConstants.ROLE_ANONYMOUS)
    @RequestMapping(value = "/{cartId}/addresses/guest/{addressId}", method = RequestMethod.PUT)
    @Operation(operationId = "editGuestAddress", summary = "This methods replaces address of guest user.", description = "Returns address data")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO updateGuestAddress(@Parameter(description = "Address identifier.", required = true) @PathVariable final String addressId,
                                        @Parameter(description = "Address object.", required = true) @RequestBody final AddressWsDTO address,
                                        @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) String fields) throws NumberParseException,
                                                                                                                       CalculationException {
        validate(address, ADDRESS, guestUserAddressValidator);
        AddressData existingAddressData = getUserFacade().getAddressForCode(addressId);
        existingAddressData.setFormattedAddress(null);
        getDataMapper().map(address, existingAddressData, CHECKOUT_GUEST_ADDRESS_MAPPING, Boolean.TRUE);
        existingAddressData.setShippingAddress(Boolean.TRUE);
        existingAddressData.setVisibleInAddressBook(Boolean.TRUE);

        getDistCheckoutFacade().updateGuestAddress(existingAddressData);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @GetMapping(path = "/{cartId}/unallowed-backorder")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public boolean hasBackorderUnallowedProducts() {
        return getDistCheckoutFacade().hasUnallowedBackorders();
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @GetMapping(path = "/{cartId}/unallowed-backorder-entries")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public OrderEntryListWsDTO getBackorderUnallowedOrderEntries(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        List<OrderEntryData> unallowedBackorderEntries = backOrderFacade.getBackOrderNotProfitableItems(getSessionCart().getEntries());
        final OrderEntryDataList dataList = new OrderEntryDataList();
        dataList.setOrderEntries(unallowedBackorderEntries);
        return getDataMapper().map(dataList, OrderEntryListWsDTO.class, fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT })
    @PostMapping(path = "/{cartId}/zeroStock")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseStatus(HttpStatus.OK)
    public boolean saveStockNotificationDataBackOrder(@Parameter(description = "Zero Stock Notify Form.", required = true) @RequestBody final ZeroStockWsDTO form) {
        return b2bCartFacade.updateCustomerEmailForOOS(form.getCustomerEmail(), form.getArticleNumbers());
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @PostMapping(path = "/{cartId}/update-backorder")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseStatus(HttpStatus.OK)
    public CartWsDTO updateBackorder(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws CalculationException {
        backOrderFacade.updateBackOrderItemsForCurrentCart();
        backOrderFacade.updateBlockedProductsForCurrentCart();
        return prepareForCheckout(fields);
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @CheckoutRestriction
    @PostMapping(path = "/{cartId}/vat")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseStatus(HttpStatus.OK)
    public void setVatCheckoutDetails(@Parameter(description = "Codice Vat Form.", required = true) @RequestBody final CodiceVatFormWsDTO form) {
        validate(form, "codiceVatForm", codiceVatFormWsDTOValidator);
        getDistCheckoutFacade().saveOrderCodiceDetails(form.getCodiceCUP(), form.getCodiceCIG());
        getDistCheckoutFacade().saveCustomerVatDetails(form.getVat4(), form.getLegalEmail());
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @PostMapping(path = "/{cartId}/checkout")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO prepareForCheckout(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        Collection<PunchoutFilterResult> punchoutFilterResult = b2bCartFacade.removeProductsWithPunchout();
        boolean isValidForFastCheckout = getDistCheckoutFacade().isValidFastCheckout();
        if (BooleanUtils.isFalse(isValidForFastCheckout)) {
            getDistCheckoutFacade().setPaymentAddressIfAvailable();
            getDistCheckoutFacade().setDeliveryAddressIfAvailable();
        }
        getDistCheckoutFacade().prepareCartForCheckout();

        CartWsDTO cartWsDTO = getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
        cartWsDTO.setEligibleForFastCheckout(isValidForFastCheckout);
        cartWsDTO.setHasUnallowedBackorder(getDistCheckoutFacade().hasUnallowedBackorders());
        cartWsDTO.setPunchedOutProducts(cartsHelper.getJoinedPunchedOutProducts(punchoutFilterResult));
        return cartWsDTO;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @GetMapping(path = "/{cartId}/validate-delivery-page")
    @Operation(summary = "Check if cart is valid on delivery page")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<CartWsDTO> isValidDeliveryPage(@ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) String fields) {
        try {
            cartsHelper.validateDeliveryPage();
            return ok().build();
        } catch (PunchoutException ex) {
            String punchoutProducts = cartsHelper.getJoinedPunchedOutProducts(ex.getPunchoutFilterResults());
            CartWsDTO cartWsDTO = getDataMapper().map(getSessionCart(), CartWsDTO.class, fields);
            cartWsDTO.setPunchedOutProducts(punchoutProducts);
            return badRequest().body(cartWsDTO);
        }
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_B2BAPPROVERGROUP,
               SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_ANONYMOUS })
    @CheckoutRestriction
    @RequestMapping(path = "/{cartId}/punchout", method = RequestMethod.DELETE)
    @ApiBaseSiteIdUserIdAndCartIdParam
    @Operation(operationId = "punchout", summary = "This methods for punchout products in cart", description = "Removes the products which is in punchout filter")
    public PunchoutWsDTO punchout() {
        final Collection<PunchoutFilterResult> punchoutFilterResult = b2bCartFacade.removeProductsWithPunchout();
        PunchoutWsDTO response = new PunchoutWsDTO();
        if (CollectionUtils.isNotEmpty(punchoutFilterResult)) {
            response.setPunchout(true);
            final List<String> punchedOutProducts = new ArrayList<>(punchoutFilterResult.size());
            for (final PunchoutFilterResult result : punchoutFilterResult) {
                if (result.getPunchedOutProduct() != null) {
                    punchedOutProducts.add(result.getPunchedOutProduct().getCode());
                } else {
                    LOG.warn("Can not add punch out result, because the product is empty! PunchoutResult: {}", result);
                    throw new CartException(String.format("Can not add punch out result, because the product is empty! PunchoutResult: {%s}", result),
                                            CartException.CANNOT_MERGE);
                }
            }
            final String punchedOutProductsString = StringUtils.join(punchedOutProducts, ", ");
            response.setPunchedOutProducts(punchedOutProductsString);
        } else {
            response.setPunchout(false);
        }
        return response;
    }

    @Secured({ SecuredAccessConstants.ROLE_B2BCUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT })
    @CheckoutRestriction
    @RequestMapping(path = "/{cartId}/approve/invoice", method = RequestMethod.GET)
    @ApiBaseSiteIdUserIdAndCartIdParam
    @Operation(operationId = "sendOrderForApproval", summary = "This methods submit for approval invoice", description = "Returns the response of the submit for invoice")
    public OrderWsDTO submitForApprovalInvoice(@PathVariable String cartId,
                                               @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) String fields)
                                                                                                                              throws CartException,
                                                                                                                              InvalidCartException {
        cartsHelper.validateCartForInvoiceApproval();
        B2BBudgetData budget = distB2BBudgetFacade.getExceededBudgetForCart();
        if (distB2BBudgetFacade.doesOrderRequireApproval(budget)) {
            OrderData orderData = getDistCheckoutFacade().placeOrder();
            orderData.setStatus(OrderStatus.PENDING_APPROVAL);
            return getDataMapper().map(orderData, OrderWsDTO.class, fields);
        } else {
            throw new CartException("Order require approval is false!", INVALID, cartId);
        }
    }

    @RequestMapping(path = "/{cartId}/reevoo", method = RequestMethod.POST)
    @Operation(operationId = "reevooeligible", summary = "Set Reevoo Eligible Flag", description = "Set Reevoo Eligible Flag for Order to be send to Reevoo")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public CartWsDTO setReevooEligibleFlag(@Parameter(description = "Address identifier.", required = true) @RequestParam final Boolean reevooEligible) {
        getDistCheckoutFacade().saveReevooEligibleFlag(reevooEligible);
        return getDataMapper().map(getSessionCart(), CartWsDTO.class, REEVOO_CART_FIELDS);
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/addresses/{addressId}", method = RequestMethod.PATCH, consumes = { MediaType.APPLICATION_JSON_VALUE,
                                                                                                          MediaType.APPLICATION_XML_VALUE })
    @Operation(operationId = "updateAddress", summary = "Updates the address", description = "Updates the address. Only attributes provided in the request body will be changed.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    @ResponseStatus(HttpStatus.OK)
    public AddressWsDTO updateAddress(@Parameter(description = "Address identifier.", required = true) @PathVariable final String addressId,
                                      @Parameter(description = "Address object", required = true) @RequestBody final AddressWsDTO address,
                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws Exception {
        sanitizeInputFields(address);
        final AddressData addressData = getAddressData(addressId);
        CustomerType customerType = getUserService().getCurrentCustomerType();
        addressData.setFormattedAddress(null);
        validate(address, ADDRESS, Objects.requireNonNull(getAddressValidatorByCustomerType(customerType)));
        getDataMapper().map(address, addressData, addressHelper.getAddressMappingByCustomerType(customerType, Boolean.TRUE), false);

        try {
            AddressData updatedData = getUserFacade().editAddress(addressData, shouldSaveInERP(customerType));
            return getDataMapper().map(updatedData, AddressWsDTO.class, fields);
        } catch (final ErpCommunicationException e) {
            LOG.error("Can not edit address for the customer with Email: {}", addressData.getEmail(), e);
            throw new ErpCommunicationException(getErrorMessage("form.global.error.erpcommunication"));
        } catch (final Exception e) {
            LOG.error("Can not edit address for the customer with Email: {}", addressData.getEmail(), e);
            throw new Exception(getErrorMessage("text.account.addresses.editAddressError"));
        }
    }

    private AddressData getAddressData(final String addressId) {
        final AddressData addressData = getUserFacade().getAddressForCode(addressId);
        if (addressData == null) {
            throw new RequestParameterException(String.format(ADDRESS_DOES_NOT_EXIST, sanitize(addressId)), RequestParameterException.INVALID,
                                                OBJECT_NAME_ADDRESS_ID);
        }
        return addressData;
    }

    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT, SecuredAccessConstants.ROLE_CUSTOMERMANAGERGROUP })
    @CheckoutRestriction
    @RequestMapping(value = "/{cartId}/addresses", method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE,
                                                                                             MediaType.APPLICATION_XML_VALUE })
    @ResponseStatus(value = HttpStatus.CREATED)
    @Operation(operationId = "createAddress", summary = "Creates a new address.", description = "Creates a new address.")
    @ApiBaseSiteIdUserIdAndCartIdParam
    public AddressWsDTO createAddress(@Parameter(description = "Address object.", required = true) @RequestBody final AddressWsDTO address,
                                      @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) throws Exception {
        sanitizeInputFields(address);
        CustomerType customerType = getUserService().getCurrentCustomerType();
        validate(address, ADDRESS, Objects.requireNonNull(getAddressValidatorByCustomerType(customerType)));
        AddressData addressData = getDataMapper().map(address, AddressData.class, addressHelper.getAddressMappingByCustomerType(customerType, Boolean.TRUE));
        addressData.setShippingAddress(true);
        addressData.setVisibleInAddressBook(true);

        AddressData createdAddress = addAddress(addressData, shouldSaveInERP(customerType));
        return getDataMapper().map(createdAddress, AddressWsDTO.class, fields);
    }

    private void sanitizeInputFields(AddressWsDTO address) {
        address.setFirstName(sanitizationService.removePeriods(address.getFirstName()));
        address.setLastName(sanitizationService.removePeriods(address.getLastName()));
    }

    private boolean shouldSaveInERP(CustomerType customerType) {
        return customerType != CustomerType.B2E;
    }

    private AddressData addAddress(AddressData addressData, boolean saveInErp) {
        try {
            return getUserFacade().addAddress(addressData, saveInErp);
        } catch (final ErpCommunicationException e) {
            LOG.error("Can not add address for the customer with Email: {} ", addressData.getEmail(), e);
            throw new ErpCommunicationException(getErrorMessage("form.global.error.erpcommunication"));
        } catch (final Exception e) {
            LOG.error("Can not add address for the customer with Email: {} ", addressData.getEmail(), e);
            throw new RuntimeException(getErrorMessage("text.account.addresses.addAddressError"));
        }
    }

    private Validator getAddressValidatorByCustomerType(CustomerType type) {
        switch (type) {
            case B2C:
                return b2cUserAddressValidator;
            case B2B:
            case B2B_KEY_ACCOUNT:
                return b2bUserAddressValidator;
            case B2E:
                return b2eUserAddressValidator;
            default:
                return null;
        }
    }
}
