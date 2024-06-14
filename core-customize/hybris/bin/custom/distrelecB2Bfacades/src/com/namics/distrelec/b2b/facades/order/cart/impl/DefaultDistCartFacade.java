package com.namics.distrelec.b2b.facades.order.cart.impl;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorLogCode;
import com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource;
import com.namics.distrelec.b2b.core.enums.QuoteModificationType;
import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.service.cart.dao.DistCartDao;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.order.DistCommerceCartService;
import com.namics.distrelec.b2b.core.service.order.exceptions.BlockedProductException;
import com.namics.distrelec.b2b.core.service.order.exceptions.InvalidQuantityForSapCatalogException;
import com.namics.distrelec.b2b.core.service.order.exceptions.PunchoutException;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.util.DistLogUtils;
import com.namics.distrelec.b2b.facades.order.Constants;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationEntry;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationProductData;
import com.namics.distrelec.b2b.facades.product.DistProductPriceQuotationFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CartRestorationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.impl.DefaultCartFacade;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commerceservices.order.*;
import de.hybris.platform.commerceservices.service.data.CommerceCartParameter;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartEntryException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.session.SessionService;

public class DefaultDistCartFacade extends DefaultCartFacade implements DistCartFacade {

    private static final Logger LOG = LogManager.getLogger(DefaultDistCartFacade.class);

    private static final long MAX_QUANTITY_SAP_CATALOG_ARTICLES = 1;

    @Autowired
    private DistEProcurementService distEProcurementService;

    @Autowired
    private DistCartDao distCartDao;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DistProductPriceQuotationFacade distProductPriceQuotationFacade;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    private DistProductService productService;

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistrelecProductFacade distrelecProductFacade;

    @Override
    public CartModificationData addToCart(final String code, final long quantity, final String searchQuery) throws CommerceCartModificationException {
        return addToCart(code, quantity, searchQuery, true);
    }

    @Override
    public CartModificationData addToCart(final String code, final long quantity, final String searchQuery, final boolean calculate)
                                                                                                                                     throws CommerceCartModificationException {
        final ProductModel product = getProductService().getProductForCode(code);
        final CartModel cart = getCartService().getSessionCart();
        return getCartModificationConverter()
                                             .convert(getCommerceCartService().addToCart(cart, product, quantity, product.getUnit(), false, searchQuery,
                                                                                         calculate));
    }

    @Override
    public CartModificationData addToCartWithoutCalcCart(final String code, final long quantity, final String ref) throws CommerceCartModificationException {
        final ProductModel product = getProductService().getProductForCode(code);
        final CartModel cartModel = getCartService().getSessionCart();
        final CommerceCartModification modification = getCommerceCartService().addToCart(cartModel, product, quantity, product.getUnit(), false, false, false,
                                                                                         ref, true);
        return getCartModificationConverter().convert(modification);
    }

    @Override
    public void persistCart() {
        final boolean isAnonymousUser = getUserService().isAnonymousUser(getUserService().getCurrentUser());
        if (!isAnonymousUser && !distEProcurementService.isEProcurementCustomer()) {
            final B2BCustomerModel customer = (B2BCustomerModel) getUserService().getCurrentUser();
            getCommerceCartService().persistCart(customer);
        } else if (!isAnonymousUser && hasSessionCart()) {
            final CartModel sessionCart = getCartService().getSessionCart();
            sessionCart.setNet(Boolean.TRUE);
        }
    }

    @Override
    public CartModificationData updateCartEntry(final long entryNumber, final String customerReference) throws CommerceCartModificationException {
        final CartModel cartModel = getCartService().getSessionCart();

        final CommerceCartModification modification = getCommerceCartService().updateCartEntry(cartModel, entryNumber, customerReference);

        return getCartModificationConverter().convert(modification);
    }

    @Override
    public void recalculateCart() throws CalculationException {
        if (getCartService().hasSessionCart()) {
            final CommerceCartParameter parameter = new CommerceCartParameter();
            parameter.setEnableHooks(true);
            parameter.setCart(getCartService().getSessionCart());
            getCommerceCartService().recalculateCart(parameter);
        }
    }

    @Override
    public Collection<PunchoutFilterResult> removeProductsWithPunchout() {
        if (getCartService().hasSessionCart()) {
            final CartModel cartModel = getCartService().getSessionCart();
            final Collection<PunchoutFilterResult> removedProducts = new ArrayList();
            final Collection<PunchoutFilterResult> result = ((DistProductService) getProductService()).isProductPunchedOutForCart(cartModel);
            if (CollectionUtils.isNotEmpty(cartModel.getEntries()) && CollectionUtils.isNotEmpty(result)) {
                for (final AbstractOrderEntryModel orderEntry : cartModel.getEntries()) {
                    for (final PunchoutFilterResult punchoutFilterResult : result) {
                        if ((null != punchoutFilterResult.getPunchedOutProduct()
                                && StringUtils.equals(punchoutFilterResult.getPunchedOutProduct().getCode(), orderEntry.getProduct().getCode()))
                                || (null != punchoutFilterResult.getPunchedOutProductHierarchy() && StringUtils
                                                                                                               .equals(punchoutFilterResult.getPunchedOutProductHierarchy(),
                                                                                                                       orderEntry.getProduct()
                                                                                                                                 .getProductHierarchy()))) {
                            try {
                                punchoutFilterResult.setPunchedOutProduct(orderEntry.getProduct());
                                updateCartEntry(orderEntry.getEntryNumber().longValue(), 0, Boolean.FALSE);
                                removedProducts.add(punchoutFilterResult);
                                break;
                            } catch (final CommerceCartModificationException e) {
                                DistLogUtils.logError(LOG,
                                                      "{} {} Could not remove entry with number {} from cart. Trying to remove because PunchoutFilter is active.",
                                                      e,
                                                      ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, orderEntry.getEntryNumber().longValue());
                            }
                        }
                    }
                }
                return removedProducts;
            }
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public void loadCart(final String code) {
        final List<CartModel> carts = distCartDao.find(Collections.singletonMap(CartModel.CODE, code));
        if (CollectionUtils.size(carts) == 1) {
            final CartModel cartToLoad = carts.iterator().next();
            getCartService().setSessionCart(cartToLoad);
        } else {
            throw new UnknownIdentifierException("Cart with code '" + code + "' not found");
        }
    }

    @Override
    public CartModificationData addToCartExt(final String code, final long quantity) throws CommerceCartModificationException {
        final ProductModel product = ((DistProductService) getProductService()).getProductForTypeOrCode(code);
        final CartModel cartModel = getCartService().getSessionCart();

        final CommerceCartModification modification = getCommerceCartService().addToCart(cartModel, product, quantity, product.getUnit(), false);

        return getCartModificationConverter().convert(modification);
    }

    @Override
    public void emptySessionCart() throws SystemException {
        if (getCartService().hasSessionCart()) {
            final CartModel cartModel = getCartService().getSessionCart();

            // Check first if there is any other backup Cart in the session
            getCartService().removeBackupCart();

            // DISTRELEC-10364
            sessionService.setAttribute(Constants.DIST_BACKUP_CART, cartModel);
            getCartService().removeSessionCart(false);
            getCartService().getSessionCart();
        }
    }

    @Override
    public void restoreSessionCart() throws SystemException {
        final CartModel cartModel = sessionService.getAttribute(Constants.DIST_BACKUP_CART);
        if (cartModel == null) { // NOP
            return;
        }

        getCartService().setSessionCart(cartModel);
        sessionService.removeAttribute(Constants.DIST_BACKUP_CART);
    }

    @Override
    public CartModificationData updateCartEntry(final String code, final long quantity) throws CommerceCartModificationException {
        if (hasSessionCart()) {
            try {
                final ProductModel product = getProductService().getProductForCode(code);
                final CartModel cartModel = getCartService().getSessionCart();
                if (cartModel != null && CollectionUtils.isNotEmpty(cartModel.getEntries())) {
                    for (final AbstractOrderEntryModel entry : cartModel.getEntries()) {
                        if (entry.getProduct().getCode().equals(product.getCode())) {
                            return updateCartEntry(entry.getEntryNumber().intValue(), quantity);
                        }
                    }
                }
            } catch (final RuntimeException re) {
                LOG.error("{} {} No product found with code {}", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, code);
            }
        }

        return null;
    }

    @Override
    public CartModificationData removeFromCart(final String code) throws CommerceCartModificationException {
        return updateCartEntry(code, 0);
    }

    @Override
    public CartData getSessionCartIfPresent() {
        return hasSessionCart() ? getSessionCart() : null;
    }

    @Override
    public boolean isProductInCart(final String code) {
        return !productsInCart(code).isEmpty();
    }

    @Override
    public boolean allowedToAccessCartWithCode(final String cartCode) {
        final Optional<CartModel> cart = distCartDao.getCartForCode(cartCode);
        final UserModel currentUser = getUserService().getCurrentUser();
        // Only cart owner can access the cart
        return cart.isPresent() && !getUserService().isAnonymousUser(currentUser) && Objects.equals(currentUser, cart.get().getUser());
    }

    @Override
    public void replace(final String target, final String replacement) {
        // YTODO Auto-generated method stub

    }

    @Override
    public List<String> productsInCart(final String... codes) {
        return hasSessionCart() ? distCartDao.productsInCart(getCartService().getSessionCart(), codes) : Collections.<String> emptyList();
    }

    @Override
    protected DistCommerceCartService getCommerceCartService() {
        return (DistCommerceCartService) super.getCommerceCartService();
    }

    @Override
    public long checkCartLevel(final String productCode) {
        final ProductModel product = getProductService().getProductForCode(productCode);
        final CartModel cartModel = getCartService().getSessionCart();
        return getCommerceCartService().checkCartLevel(product, cartModel);
    }

    @Override
    public AbstractOrderEntryModel getOrderEntry(final long entryNumber) {
        final CartModel cartModel = getCartService().getSessionCart();
        return getCommerceCartService().getOrderEntry(entryNumber, cartModel);
    }

    /**
     * Overrides the origin method just to avoid calculation of anonymous cart before merging.
     */
    @Override
    public CartRestorationData restoreAnonymousCartAndMerge(final String fromAnonymousCartGuid, final String toUserCartGuid)
                                                                                                                             throws CommerceCartRestorationException,
                                                                                                                             CommerceCartMergingException {
        final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
        final CartModel fromCart = getCommerceCartService().getCartForGuidAndSiteAndUser(fromAnonymousCartGuid, currentBaseSite,
                                                                                         getUserService().getAnonymousUser());

        final CartModel toCart = getCommerceCartService().getCartForGuidAndSiteAndUser(toUserCartGuid, currentBaseSite,
                                                                                       getUserService().getCurrentUser());

        if (toCart == null) {
            throw new CommerceCartRestorationException("Cart cannot be null");
        }

        if (fromCart == null) {
            return restoreSavedCart(toUserCartGuid);
        }

        final CommerceCartParameter parameter = new CommerceCartParameter();
        parameter.setEnableHooks(true);
        parameter.setCart(toCart);
        parameter.setIgnoreRecalculation(true);

        final CommerceCartRestoration restoration = getCommerceCartService().restoreCart(parameter);
        parameter.setCart(getCartService().getSessionCart());

        getCommerceCartService().mergeCarts(fromCart, parameter.getCart(), restoration.getModifications());

        parameter.setIgnoreRecalculation(false);
        final CommerceCartRestoration commerceCartRestoration = getCommerceCartService().restoreCart(parameter);

        commerceCartRestoration.setModifications(restoration.getModifications());

        getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
        return getCartRestorationConverter().convert(commerceCartRestoration);
    }

    @Override
    public CartRestorationData restoreCartAndMerge(final String fromUserCartGuid, final String toUserCartGuid)
                                                                                                               throws CommerceCartRestorationException,
                                                                                                               CommerceCartMergingException {
        final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
        final CartModel fromCart = getCommerceCartService().getCartForGuidAndSiteAndUser(fromUserCartGuid, currentBaseSite, getUserService().getCurrentUser());

        final CartModel toCart = getCommerceCartService().getCartForGuidAndSiteAndUser(toUserCartGuid, currentBaseSite, getUserService().getCurrentUser());

        LOG.info("Merging cart:{} with ghost:{} and cart:{} with ghost:{}", fromUserCartGuid, fromCart == null ? null : fromCart.isGhostOrder(), toUserCartGuid,
                 toCart == null ? null : toCart.isGhostOrder());

        if (fromCart == null && toCart == null) { // Case both null
            return null;
        }
        if (fromCart == null ^ toCart == null) { // case one is null and the other is not.
            final CartModel cart = (fromCart == null ? toCart : fromCart);
            return cart.isGhostOrder() ? null : restoreSavedCart(cart.getGuid());
        }
        if (fromCart.isGhostOrder() ^ toCart.isGhostOrder()) { // case both are not null but one is Ghost
            return restoreSavedCart((fromCart.isGhostOrder() ? toCart : fromCart).getGuid());
        }
        if (fromCart.isGhostOrder() && toCart.isGhostOrder()) {// case both are not null and both are ghosts
            return null;
        }

        // case both are not null and both are not Ghost

        final CommerceCartParameter parameter = new CommerceCartParameter();
        parameter.setEnableHooks(true);
        parameter.setCart(toCart);

        final CommerceCartRestoration restoration = getCommerceCartService().restoreCart(parameter);
        parameter.setCart(getCartService().getSessionCart());

        getCommerceCartService().mergeCarts(fromCart, parameter.getCart(), restoration.getModifications());

        final CommerceCartRestoration commerceCartRestoration = getCommerceCartService().restoreCart(parameter);

        commerceCartRestoration.setModifications(restoration.getModifications());

        getCartService().changeCurrentCartUser(getUserService().getCurrentUser());
        return getCartRestorationConverter().convert(commerceCartRestoration);
    }

    @Override
    public Map<String, Object> addQuotationInToCart(final String quotationId, final Map<MultiKey, Long> productQuantities,
                                                    boolean recalculate) {

        final Map<String, Object> result = new HashMap<>();
        result.put("status", Boolean.FALSE);
        result.put("message", "lightboxreturnrequest.process.error");

        // check if user is logged in
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            LOG.error("{} {} Add quotation is not possible for anonymous users", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS);
            return result;
        }

        final CartModel cartModel = getCartService().getSessionCart();

        // Check if quotation is already added into cart
        if (cartModel != null && CollectionUtils.isNotEmpty(cartModel.getEntries())) {
            for (final AbstractOrderEntryModel entry : cartModel.getEntries()) {
                if (BooleanUtils.isTrue(entry.getQuotation()) && StringUtils.equals(entry.getQuotationId(), quotationId)) {
                    LOG.error("{} {} Quotation {} is already added into the cart", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, quotationId);
                    result.put("message", "basket.add.quotation.existing");
                    return result;
                }
            }
        }

        final QuotationData quotationData = distProductPriceQuotationFacade.getQuotationDetails(quotationId);
        final B2BUnitModel company = ((B2BCustomerModel) getUserService().getCurrentUser()).getDefaultB2BUnit();

        if (quotationData == null) {
            LOG.error("{} {} No quotation data found with Id '{}' that belongs to current customer {}", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                      quotationId, company.getErpCustomerID());
            return result;
        }

        if (!quotationData.isPurchasable()) {
            if (quotationData.getStatus() != null) {
                if ("01".equals(quotationData.getStatus().getCode())) {
                    LOG.error("{} {} The quotation with Id '{}' is not ready yet for ordering!", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                              quotationId);
                    result.put("message", "basket.add.quotation.notready");
                } else {
                    LOG.error("{} {} The quotation [Id:{}, status: {}] cannot be ordered! ", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                              quotationId, quotationData.getStatus().getName());
                    result.put("message", "basket.add.quotation.error." + quotationData.getStatus().getCode());
                }
            } else {
                LOG.error("{} {} The quotation [Id: {}, status: N/A] cannot be ordered! ", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                          quotationId);
            }

            return result;
        }

        // check if quotation was created by current customer
        if (!StringUtils.equals(formatErpID(quotationData.getCustomerId(), 10), formatErpID(company.getErpCustomerID(), 10))) {
            LOG.error("{} {} QuotationId = {} doesn't belongs to current customer {}", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, quotationId,
                      company.getErpCustomerID());
            return result;
        }

        // make sure quotation is not Empty
        if (CollectionUtils.isEmpty(quotationData.getQuotationEntries())) {
            LOG.error("{}  {} Can't add QuotationId {} into the cart because it doesn't have any item for customerID {}", ErrorLogCode.CART_CALCULATION_ERROR,
                      ErrorSource.HYBRIS, quotationId, company.getErpCustomerID());
            return result;
        }

        boolean ok = true;
        // Finally adding Quotations Entries into Cart
        for (final QuotationEntry quoteEntry : quotationData.getQuotationEntries()) {
            try {
                final String pcode = quoteEntry.getProduct().getCode();
                final MultiKey key = new MultiKey(pcode, quoteEntry.getItemNumber());
                // We skip the quote entry if it is not mandatory the map is not empty and does contain the related item.
                if (!quoteEntry.isMandatory() && MapUtils.isNotEmpty(productQuantities) && !productQuantities.containsKey(key)) {
                    continue;
                }

                final ProductModel productModel = getProductService().getProductForCode(pcode);
                final long qty = productQuantities.containsKey(key) ? getQuoteEntryQuantity(quoteEntry, productQuantities.get(key))
                                                                    : quoteEntry.getQuantity().longValue();

                final CommerceCartModification cartModificationData = getCommerceCartService().addToCart(cartModel, productModel, qty, productModel.getUnit(),
                                                                                                         true, false, recalculate,
                                                                                                         quoteEntry.getCustomerReference(),
                                                                                                         true);
                updateQuotationDataOnEntry(quotationId, quoteEntry, cartModificationData, quotationData);
            } catch (final UnknownIdentifierException uie) {
                ok = false;
                DistLogUtils.logError(LOG, "{} {} The product with code {} does not exist!{}", uie, ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                                      quoteEntry.getProduct().getCode());
            } catch (final Exception e) {
                ok = false;
                DistLogUtils.logError(LOG, "{} {} An Exception Occurred! {}", e, ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, e.getMessage());
            }
        }
        if (ok) {
            result.put("status", Boolean.TRUE);
            result.remove("message");
        } else {
            // Not all products have been added to the cart.
            result.put("message", "basket.error.unknown");
        }
        return result;
    }

    @Override
    public void addQuotationInToCart(final String quotationId, List<QuotationProductData> quotationProducts,
                                     boolean recalculate) throws CommerceCartModificationException {
        if (getUserService().isAnonymousUser(getUserService().getCurrentUser())) {
            LOG.error("{} {} Add quotation is not possible for anonymous users", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS);
            throw new CommerceCartModificationException("Add quotation is not possible for anonymous users");
        }

        if (getCartService().doesQuoteAlreadyExistInCart(quotationId)) {
            throw new CommerceCartModificationException("Quote already exists in cart");
        }

        QuotationData quotationData = distProductPriceQuotationFacade.getQuotationDetails(quotationId);
        validateQuotation(quotationId, quotationData);

        for (final QuotationEntry quoteEntry : quotationData.getQuotationEntries()) {
            QuotationProductData quotationProductData = quotationProducts.stream()
                                                                         .filter(product -> findQuotationProduct(quoteEntry, product))
                                                                         .findFirst().orElse(null);

            if (!quoteEntry.isMandatory() && CollectionUtils.isNotEmpty(quotationProducts) && isNull(quotationProductData)) {
                continue;
            }
            ProductModel productModel = getProductService().getProductForCode(quoteEntry.getProduct().getCode());
            long qty = getQuantityForQuoteEntry(quoteEntry, quotationProductData);

            CommerceCartModification cartModificationData = getCommerceCartService().addToCart(getCartService().getSessionCart(), productModel, qty,
                                                                                               productModel.getUnit(),
                                                                                               true, false, recalculate,
                                                                                               quoteEntry.getCustomerReference(),
                                                                                               true);
            updateQuotationDataOnEntry(quotationId, quoteEntry, cartModificationData, quotationData);
        }
    }

    private void validateQuotation(String quotationId, QuotationData quotationData) throws CommerceCartModificationException {
        B2BUnitModel company = ((B2BCustomerModel) getUserService().getCurrentUser()).getDefaultB2BUnit();

        if (quotationData == null) {
            LOG.error("{} {} No quotation data found with Id '{}' that belongs to current customer {}", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                      quotationId, company.getErpCustomerID());
            throw new CommerceCartModificationException("Quotation not found");
        }

        if (!quotationData.isPurchasable()) {
            if (quotationData.getStatus() != null) {
                LOG.error("{} {} The quotation [Id:{}, status: {}] cannot be ordered! ", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, quotationId,
                          quotationData.getStatus().getName());
                throw new CartException("basket.add.quotation.error." + quotationData.getStatus().getCode());
            }
            LOG.error("{} {} The quotation [Id: {}, status: N/A] cannot be ordered! ", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS,
                      quotationId);
            throw new CommerceCartModificationException("Quotation is not purchasable");
        }

        if (!StringUtils.equals(formatErpID(quotationData.getCustomerId(), 10), formatErpID(company.getErpCustomerID(), 10))) {
            LOG.error("{} {} QuotationId = {} doesn't belongs to current customer {}", ErrorLogCode.CART_CALCULATION_ERROR, ErrorSource.HYBRIS, quotationId,
                      company.getErpCustomerID());
            throw new CommerceQuoteAssignmentException("Quote not assigned to current customer", quotationData.getCustomerId());
        }

        if (CollectionUtils.isEmpty(quotationData.getQuotationEntries())) {
            LOG.error("{}  {} Can't add QuotationId {} into the cart because it doesn't have any item for customerID {}", ErrorLogCode.CART_CALCULATION_ERROR,
                      ErrorSource.HYBRIS, quotationId, company.getErpCustomerID());
            throw new CommerceCartModificationException("Quote is empty");
        }
    }

    private long getQuantityForQuoteEntry(QuotationEntry quoteEntry, QuotationProductData quotationProductData) {
        return nonNull(quotationProductData) ? getQuoteEntryQuantity(quoteEntry, quotationProductData.getQuantity())
                                             : quoteEntry.getQuantity().longValue();
    }

    private boolean findQuotationProduct(QuotationEntry quoteEntry, QuotationProductData product) {
        return nonNull(quoteEntry.getProduct()) && StringUtils.equals(product.getProductCode(), quoteEntry.getProduct().getCode())
                && StringUtils.equals(product.getItemNumber(), quoteEntry.getItemNumber());
    }

    private void updateQuotationDataOnEntry(String quotationId, QuotationEntry quoteEntry, CommerceCartModification cartModificationData,
                                            QuotationData quotationData) {
        final AbstractOrderEntryModel abstractOrderEntryModel = cartModificationData.getEntry();
        abstractOrderEntryModel.setLineNumber(quoteEntry.getItemNumber());
        abstractOrderEntryModel.setQuotationReference(quotationData.getPoNumber());
        abstractOrderEntryModel.setQuotationId(quotationId);
        abstractOrderEntryModel.setQuotation(Boolean.TRUE);
        abstractOrderEntryModel.setDummyItem(quoteEntry.isDummyItem());
        abstractOrderEntryModel.setArticleDescription(quoteEntry.getArticleDescription());
        abstractOrderEntryModel.setQuotationExpiryDate(quotationData.getQuotationExpiryDate());
        abstractOrderEntryModel.setMandatoryItem(quoteEntry.isMandatory());
        abstractOrderEntryModel.setQuoteModificationType(QuoteModificationType.valueOf(quoteEntry.getQuantityModificationType().getCode()));

        getModelService().save(abstractOrderEntryModel);
    }

    @Override
    public PriceData getFreeShippingValue(final BigDecimal value, final String isoCode) {
        return priceDataFactory.create(PriceDataType.BUY, value, isoCode);
    }

    @Override
    public CartData emptyCart() throws CalculationException {
        final CartModel cartModel = getCartService().getSessionCart();
        if (cartModel != null && CollectionUtils.isNotEmpty(cartModel.getEntries())) {
            getModelService().removeAll(cartModel.getEntries());
            getModelService().save(cartModel);
            getModelService().refresh(cartModel);
            recalculateCart();
        }
        return getSessionCartIfPresent();
    }

    @Override
    public CartData getSessionMiniCart() {
        final CartModel cart = getCartService().getSessionCart();
        return getMiniCartConverter().convert(cart);
    }

    @Override
    public List<OrderEntryData> getEntriesFromCart() {
        if (hasSessionCart()) {
            return getSessionCart().getEntries();
        }
        return Collections.emptyList();
    }

    /**
     * Calculate the quantity for the quote entry based on the new submitted value and the modification type.
     *
     * @param quotationEntry
     *            the quote entry
     * @param newQty
     *            the new submitted quantity
     * @return either the new submitted quantity if the quote entry can be updated and the new value fulfill the modification type. The
     *         default value is the original quantity value.
     */
    protected long getQuoteEntryQuantity(final QuotationEntry quotationEntry, final long newQty) {
        final long quoteEntryQty = quotationEntry.getQuantity().longValue();
        if (newQty != quoteEntryQty && //
                (quotationEntry.getQuantityModificationType() == QuoteModificationType.ALL //
                        || (quotationEntry.getQuantityModificationType() == QuoteModificationType.DECREASE && newQty <= quoteEntryQty) //
                        || (quotationEntry.getQuantityModificationType() == QuoteModificationType.INCREASE && newQty >= quoteEntryQty))) {
            return newQty;
        }

        return quoteEntryQty;
    }

    @Override
    public void removeQuotationFromCart(final String quotationId) throws CommerceCartModificationException {
        final CartModel cartModel = getCartService().getSessionCart();
        for (final AbstractOrderEntryModel orderEntryModel : cartModel.getEntries()) {
            if (BooleanUtils.isTrue(orderEntryModel.getQuotation()) && orderEntryModel.getQuotationId().equals(quotationId)) {
                updateCartEntry(orderEntryModel.getEntryNumber(), 0L, Boolean.FALSE);
            }
        }
    }

    /**
     * The customer ERP contact ID is 10 digit format. The value is composed from the login ID plus few zeros in front. E.g.,
     * {@code loginID = "198745"} gives {@code ErpContactID = "0000198745"}
     *
     * @param loginID
     *            the source login ID
     * @return the formatted ERP contact ID
     */
    @Override
    public String formatErpID(final String loginID, final int maxLength) {
        final StringBuilder sb = new StringBuilder(loginID);
        final int length = maxLength - loginID.length();
        for (int i = 0; i < length; i++) {
            sb.insert(0, '0');
        }

        return sb.toString();
    }

    @Override
    public List<CartModificationData> removeEndOfLifeProductsFromCart() {
        if (getCartService().hasSessionCart()) {
            CartModel cart = getCartService().getSessionCart();
            List<AbstractOrderEntryModel> endOfLifeEntries = cart.getEntries()
                                                                 .stream()
                                                                 .filter(entry -> productService.isEndOfLife(entry.getProduct()))
                                                                 .collect(Collectors.toList());

            return endOfLifeEntries
                                   .stream()
                                   .map(entry -> {
                                       try {
                                           return updateCartEntry(entry.getEntryNumber().longValue(), 0);
                                       } catch (CommerceCartModificationException e) {
                                           LOG.error("Could not remove EOL entry with number {} from cart.",
                                                     entry.getEntryNumber());
                                           return null;
                                       }
                                   })
                                   .filter(Objects::nonNull)
                                   .collect(Collectors.toList());

        }
        return Collections.emptyList();
    }

    @Override
    public List<CartModificationData> updatePhasedOutProducts() {
        if (getCartService().hasSessionCart()) {
            CartModel cart = getCartService().getSessionCart();
            List<AbstractOrderEntryModel> orderEntriesNotForSale = cart.getEntries()
                                                                       .stream()
                                                                       .filter(entry -> productService.isProductNotForSale(entry.getProduct()))
                                                                       .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(orderEntriesNotForSale)) {
                return Collections.emptyList();
            }

            final List<ProductAvailabilityData> availabilities = ((DistrelecProductFacade) getProductFacade()).getAvailabilityForEntries(orderEntriesNotForSale);

            return orderEntriesNotForSale
                                         .stream()
                                         .map(entry -> {
                                             int stockAvailable = getAvailableStock(availabilities, entry);
                                             if (isRequestedQuantityAvailable(entry, stockAvailable)) {
                                                 return null;
                                             }
                                             try {
                                                 return updateCartEntry(entry.getEntryNumber().longValue(), stockAvailable);
                                             } catch (CommerceCartModificationException e) {
                                                 LOG.error("Could not update PhasedOut entry with number {} from cart.",
                                                           entry.getEntryNumber());
                                                 return null;
                                             }
                                         })
                                         .filter(Objects::nonNull)
                                         .collect(Collectors.toList());

        }
        return Collections.emptyList();
    }

    @Override
    public List<CartModificationData> updateEntriesWithMOQ() {
        if (getCartService().hasSessionCart()) {
            CartModel cart = getCartService().getSessionCart();
            return cart.getEntries()
                       .stream()
                       .map(entry -> {
                           DistSalesOrgProductModel salesOrgProduct = distSalesOrgProductService.getCurrentSalesOrgProduct(entry.getProduct());
                           if (isMoqUpdateNotApplicable(entry.getQuantity(), salesOrgProduct.getOrderQuantityMinimum(),
                                                        salesOrgProduct.getOrderQuantityStep())) {
                               return null;
                           }
                           long newQty = calculateNewMoqQuantity(entry.getQuantity(), salesOrgProduct.getOrderQuantityMinimum(),
                                                                 salesOrgProduct.getOrderQuantityStep());
                           try {
                               return updateCartEntry(entry.getEntryNumber().longValue(), newQty);
                           } catch (CommerceCartModificationException e) {
                               LOG.error("Could not update MOQ for entry with number {} from cart.",
                                         entry.getEntryNumber());
                               return null;
                           }
                       }).filter(Objects::nonNull)
                       .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public OrderEntryData getCartEntryForNumber(int requestedEntryNumber) {
        if (getCartService().hasSessionCart()) {
            CartModel cart = getCartService().getSessionCart();
            final List<AbstractOrderEntryModel> entries = cart.getEntries();
            if (CollectionUtils.isNotEmpty(entries)) {
                AbstractOrderEntryModel orderEntry = entries.stream()
                                                            .filter(Objects::nonNull)
                                                            .filter(entry -> entry.getEntryNumber().equals(requestedEntryNumber))
                                                            .findFirst().orElse(null);

                if (nonNull(orderEntry)) {
                    return getOrderEntryConverter().convert(orderEntry);
                }
                throw new CartEntryException("Entry not found", CartEntryException.NOT_FOUND, String.valueOf(requestedEntryNumber));
            }
        }
        throw new CartException("Cart not found", CartException.NOT_FOUND);
    }

    @Override
    public void checkIsProductBuyable(String productCode, long quantity) {
        ProductModel product = productService.getProductForCode(productCode);
        if (isInvalidQuantityForSAPCatalogProduct(product, quantity)) {
            throw new InvalidQuantityForSapCatalogException("sap.catalog.order.articles.error", "Quantity requested is larger than 1 for SAP Catalog Article");
        }

        if (distrelecProductFacade.isBlockedProduct(product)) {
            throw new BlockedProductException("product.message.status.90", "Product is in blocked status!");
        }

        if (!productService.isProductBuyable(product)) {
            final Collection<PunchoutFilterResult> punchoutFilterResult = productService.getPunchOutFilters(product);
            if (CollectionUtils.isNotEmpty(punchoutFilterResult)) {
                throw new PunchoutException("cart.punchout_error", punchoutFilterResult);
            }
            throw new ProductLowStockException("cart.nonstock.phaseout.product", "Entry is not buyable");
        }
        if (productService.isEndOfLife(product)) {
            throw new ProductLowStockException("cart.product.clearedOut.message.checkout.eol", "Entry is considered End Of Life");
        }
    }

    @Override
    public boolean isInvalidQuantityForSAPCatalogProduct(ProductModel product, long quantity) {
        if (productService.isSAPCatalogProduct(product)) {
            return quantity != MAX_QUANTITY_SAP_CATALOG_ARTICLES || quantity + checkCartLevel(product.getCode()) != MAX_QUANTITY_SAP_CATALOG_ARTICLES;
        }
        return Boolean.FALSE;
    }

    @Override
    public List<CartModificationData> removeBlockedProductsFromCart() {
        List<String> productCodes = distrelecProductFacade.getProductCodesForBlockedSalesStatus();
        return productCodes.stream()
                           .map(code -> {
                               try {
                                   Integer entryNumber = getEntryNumberForGivenProductCode(code);
                                   if (entryNumber != null) {
                                       return updateCartEntry(entryNumber.intValue(), 0, false);
                                   }
                                   return null;
                               } catch (CommerceCartModificationException e) {
                                   LOG.error("Could not remove product with code {} from cart.", code);
                                   return null;
                               }
                           })
                           .filter(Objects::nonNull)
                           .toList();
    }

    private Integer getEntryNumberForGivenProductCode(String code) {
        final ProductModel product = getProductService().getProductForCode(code);
        final CartModel cartModel = getCartService().getSessionCart();
        if (cartModel != null && CollectionUtils.isNotEmpty(cartModel.getEntries())) {
            return cartModel.getEntries().stream()
                            .filter(entry -> entry.getProduct().getCode().equals(product.getCode()))
                            .findFirst()
                            .map(AbstractOrderEntryModel::getEntryNumber)
                            .orElse(null);
        }
        return null;
    }

    private boolean isMoqUpdateNotApplicable(final Long qty, final Long minQty, final Long step) {
        if (qty != null && minQty != null && step != null && step != 0) {
            long sum = (qty - minQty) % step;
            if (qty == minQty.longValue() && sum == 0 || qty - (qty % step) == qty) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private long calculateNewMoqQuantity(final Long qty, final Long minQty, final Long step) {
        return qty > minQty ? qty - (qty % step) : minQty;
    }

    private boolean isRequestedQuantityAvailable(AbstractOrderEntryModel entry, int stockAvailable) {
        return stockAvailable >= entry.getQuantity();
    }

    private int getAvailableStock(List<ProductAvailabilityData> availabilities, AbstractOrderEntryModel entry) {
        return availabilities.stream()
                             .filter(availability -> entry.getProduct().getCode().equals(availability.getProductCode()))
                             .map(ProductAvailabilityData::getStockLevelTotal)
                             .findFirst().orElse(0);
    }

    @Override
    public CartModificationData updateCartEntry(final long entryNumber, final long quantity) throws CommerceCartModificationException {
        return updateCartEntry(entryNumber, quantity, true);
    }

    @Override
    public CartModificationData updateCartEntry(final long entryNumber, final long quantity, final boolean recalculate)
                                                                                                                        throws CommerceCartModificationException {
        final CartModel cartModel = getCartService().getSessionCart();
        final CommerceCartParameter parameter = new CommerceCartParameter();
        parameter.setEnableHooks(true);
        parameter.setCart(cartModel);
        parameter.setEntryNumber(entryNumber);
        parameter.setQuantity(quantity);
        parameter.setRecalculate(recalculate);
        final CommerceCartModification modification = getCommerceCartService().updateQuantityForCartEntry(parameter);

        return getCartModificationConverter().convert(modification);
    }

    @Override
    protected DistCartService getCartService() {
        return (DistCartService) super.getCartService();
    }
}
