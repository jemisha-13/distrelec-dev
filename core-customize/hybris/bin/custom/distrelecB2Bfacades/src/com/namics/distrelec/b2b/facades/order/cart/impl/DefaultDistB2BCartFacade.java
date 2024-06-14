/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.order.cart.impl;

import static com.namics.distrelec.b2b.core.service.order.util.AddToCartParamsBuilder.aAddToCartParamsBuilder;
import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNullStandardMessage;
import static java.util.Objects.nonNull;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.annotations.CxTransaction;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.order.DistErpVoucherInfoModel;
import com.namics.distrelec.b2b.core.service.order.exceptions.AddToCartDisabledException;
import com.namics.distrelec.b2b.core.service.order.exceptions.BlockedProductException;
import com.namics.distrelec.b2b.core.service.order.exceptions.InvalidQuantityForSapCatalogException;
import com.namics.distrelec.b2b.core.service.order.exceptions.PunchoutException;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.distrelec.b2b.facades.message.queue.data.AddToCartBulkRequestData;
import com.namics.distrelec.b2b.facades.message.queue.data.AddToCartBulkResponseData;
import com.namics.distrelec.b2b.facades.message.queue.data.BulkProductData;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistCartFacade;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationProductData;
import com.namics.distrelec.b2b.facades.product.DistrelecOutOfStockNotificationFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.b2bacceleratorfacades.order.impl.DefaultB2BCartFacade;
import de.hybris.platform.commercefacades.order.data.*;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.order.CommerceCartMergingException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartRestorationException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.CartException;
import de.hybris.platform.commercewebservicescommons.errors.exceptions.ProductLowStockException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.SystemException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * {@code DefaultDistB2BCartFacade}
 * <p>
 * This B2B cart facade is dedicated mainly for non anonymous customers. The facade {@link DefaultDistCartFacade} is mainly targeting the
 * anonymous customers. Note that in this implementation we delegate some operation to the anonymous facade.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @see {@link DefaultDistCartFacade}
 * @since Distrelec 6.0
 */
public class DefaultDistB2BCartFacade extends DefaultB2BCartFacade implements DistB2BCartFacade {
    public static final String ATTRIBUTE_NO_PRODUCT_FOR_SALE_OUT_OF_STOCK = "distrelec.noproduct.forsale.salesstatus.outofstock";

    private static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC);

    @Autowired
    protected ErpStatusUtil erpStatusUtil;

    private Logger LOGGER = LoggerFactory.getLogger(DefaultDistB2BCartFacade.class);

    @Autowired
    private ModelService modelService;

    @Autowired
    @Qualifier("distrelecProductFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistrelecOutOfStockNotificationFacade outOfStockFacade;

    @Override
    public CartData getSessionCart() {
        return getCartFacade().getSessionCart();
    }

    @Override
    public CartData getSessionCartWithEntryOrdering(final boolean recentlyAddedFirst) {
        return getCartFacade().getSessionCartWithEntryOrdering(recentlyAddedFirst);
    }

    @Override
    public boolean hasSessionCart() {
        return getCartFacade().hasSessionCart();
    }

    @Override
    public boolean hasEntries() {
        return getCartFacade().hasEntries();
    }

    @Override
    public CartData getMiniCart() {
        return getCartFacade().getMiniCart();
    }

    @Override
    public CartModificationData addToCart(final String code, final long quantity) throws CommerceCartModificationException {
        return getCartFacade().addToCart(code, quantity);
    }

    @Override
    public CartModificationData addToCart(final String code, final long quantity, final String storeId) throws CommerceCartModificationException {
        return getCartFacade().addToCart(code, quantity, storeId);
    }

    @Override
    public CartModificationData addToCart(final AddToCartParams addToCartParams) throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().addToCart(addToCartParams);
    }

    @Override
    public List<CartModificationData> validateCartData() throws CommerceCartModificationException {
        return getCartFacade().validateCartData();
    }

    @Override
    public CartModificationData updateCartEntry(final long entryNumber, final long quantity) throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().updateCartEntry(entryNumber, quantity);
    }

    @Override
    public CartModificationData updateCartEntry(final long entryNumber, final String storeId) throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().updateCartEntry(entryNumber, storeId);
    }

    @Override
    public CartRestorationData restoreSavedCart(final String guid) throws CommerceCartRestorationException {
        return getCartFacade().restoreSavedCart(guid);
    }

    @Override
    public List<CountryData> getDeliveryCountries() {
        return getCartFacade().getDeliveryCountries();
    }

    @Override
    public CartData estimateExternalTaxes(final String deliveryZipCode, final String countryIsoCode) {
        return getCartFacade().estimateExternalTaxes(deliveryZipCode, countryIsoCode);
    }

    @Override
    public void removeStaleCarts() {
        getCartFacade().removeStaleCarts();
    }

    @Override
    public CartRestorationData restoreAnonymousCartAndTakeOwnership(final String guid) throws CommerceCartRestorationException {
        return getCartFacade().restoreAnonymousCartAndTakeOwnership(guid);
    }

    @Override
    public void removeSessionCart() {
        getCartFacade().removeSessionCart();
    }

    @Override
    public List<CartData> getCartsForCurrentUser() {
        return getCartFacade().getCartsForCurrentUser();
    }

    @Override
    public CartRestorationData restoreAnonymousCartAndMerge(final String fromAnonumousCartGuid, final String toUserCartGuid)
                                                                                                                             throws CommerceCartMergingException,
                                                                                                                             CommerceCartRestorationException {
        return getCartFacade().restoreAnonymousCartAndMerge(fromAnonumousCartGuid, toUserCartGuid);
    }

    @Override
    public CartRestorationData restoreCartAndMerge(final String fromUserCartGuid, final String toUserCartGuid)
                                                                                                               throws CommerceCartRestorationException,
                                                                                                               CommerceCartMergingException {
        return getCartFacade().restoreCartAndMerge(fromUserCartGuid, toUserCartGuid);
    }

    @Override
    public CartModificationData updateCartEntry(final OrderEntryData cartEntry) throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().updateCartEntry(cartEntry);
    }

    @Override
    public void updateCartMetadata(final CommerceCartMetadata metadata) {
        getCartFacade().updateCartMetadata(metadata);
    }

    @Override
    public CartModificationData removeEntryGroup(@Nonnull final Integer groupNumber) throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().removeEntryGroup(groupNumber);
    }

    @Override
    protected DistCartFacade getCartFacade() {
        return (DistCartFacade) super.getCartFacade();
    }

    @Override
    public CartData getSessionCartIfPresent() {
        return getCartFacade().getSessionCartIfPresent();
    }

    @Override
    public CartModificationData addToCartWithoutCalcCart(final String code, final long quantity, final String ref) throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().addToCartWithoutCalcCart(code, quantity, ref);
    }

    @Override
    public CartModificationData updateCartEntry(final String code, final long quantity) throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().updateCartEntry(code, quantity);
    }

    @Override
    public CartModificationData removeFromCart(final String code) throws CommerceCartModificationException {
        return getCartFacade().removeFromCart(code);
    }

    @Override
    @CxTransaction
    public void recalculateCart() throws CalculationException {
        getCartFacade().recalculateCart();
    }

    @Override
    public Collection<PunchoutFilterResult> removeProductsWithPunchout() {
        return getCartFacade().removeProductsWithPunchout();
    }

    @Override
    public void persistCart() {
        getCartFacade().persistCart();
    }

    @Override
    public void loadCart(final String code) {
        getCartFacade().loadCart(code);
    }

    @Override
    public CartModificationData addToCartExt(final String code, final long quantity) throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().addToCartExt(code, quantity);
    }

    @Override
    public void emptySessionCart() throws SystemException {
        enableCartMovCheck();
        getCartFacade().emptySessionCart();
    }

    @Override
    public void restoreSessionCart() throws SystemException {
        getCartFacade().restoreSessionCart();
    }

    @Override
    public boolean isProductInCart(final String code) {
        return getCartFacade().isProductInCart(code);
    }

    @Override
    public List<String> productsInCart(final String... codes) {
        return getCartFacade().productsInCart(codes);
    }

    @Override
    public long checkCartLevel(final String productCode) {
        return getCartFacade().checkCartLevel(productCode);
    }

    @Override
    public AbstractOrderEntryModel getOrderEntry(final long entryNumber) {
        return getCartFacade().getOrderEntry(entryNumber);
    }

    @Override
    public void replace(final String target, final String replacement) {
        enableCartMovCheck();
        getCartFacade().replace(target, replacement);
    }

    @Override
    public void removeQuotationFromCart(final String quotationId) throws CommerceCartModificationException {
        getCartFacade().removeQuotationFromCart(quotationId);
    }

    @Override
    public Map<String, Object> addQuotationInToCart(final String quotationId, final Map<MultiKey, Long> productQuantities, boolean recalculate)
                                                                                                                                                throws CalculationException,
                                                                                                                                                CommerceCartModificationException {
        return getCartFacade().addQuotationInToCart(quotationId, productQuantities, recalculate);
    }

    @Override
    public void addQuotationInToCart(final String quotationId, List<QuotationProductData> quotationProducts,
                                     boolean recalculate) throws CommerceCartModificationException {
        getCartFacade().addQuotationInToCart(quotationId, quotationProducts, recalculate);
    }

    @Override
    public PriceData getFreeShippingValue(final BigDecimal value, final String isoCode) {
        return getCartFacade().getFreeShippingValue(value, isoCode);
    }

    @Override
    public CartData getSessionMiniCart() {
        return getCartFacade().getSessionMiniCart();
    }

    @Override
    public List<OrderEntryData> getEntriesFromCart() {
        return getCartFacade().getEntriesFromCart();
    }

    @Override
    public String formatErpID(String loginID, int maxLength) {
        return getCartFacade().formatErpID(loginID, maxLength);
    }

    @Override
    public List<CartModificationData> removeEndOfLifeProductsFromCart() {
        return getCartFacade().removeEndOfLifeProductsFromCart();
    }

    @Override
    public List<CartModificationData> updatePhasedOutProducts() {
        return getCartFacade().updatePhasedOutProducts();
    }

    @Override
    public List<CartModificationData> updateEntriesWithMOQ() {
        return getCartFacade().updateEntriesWithMOQ();
    }

    @Override
    public OrderEntryData getCartEntryForNumber(int number) {
        return getCartFacade().getCartEntryForNumber(number);
    }

    @Override
    public void checkIsProductBuyable(String productCode, long quantity) {
        getCartFacade().checkIsProductBuyable(productCode, quantity);
    }

    @Override
    public boolean isInvalidQuantityForSAPCatalogProduct(ProductModel product, long quantity) {
        return getCartFacade().isInvalidQuantityForSAPCatalogProduct(product, quantity);
    }

    @Override
    public List<CartModificationData> removeBlockedProductsFromCart() {
        return getCartFacade().removeBlockedProductsFromCart();
    }

    @Override
    public CartModel getSessionCartModel() {
        return getCartService().getSessionCart();
    }

    @Override
    public CartModificationData updateCartEntry(final long entryNumber, final long quantity, final boolean recalculate)
                                                                                                                        throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().updateCartEntry(entryNumber, quantity, recalculate);
    }

    private void enableCartMovCheck() {
        final CartModel sessionCart = getSessionCartModel();
        sessionCart.setSkipMovCheck(false);
        modelService.save(sessionCart);
    }

    @Override
    public CartModificationData addToCart(final String code, final long quantity, final String searchQuery, final boolean recalculate)
                                                                                                                                       throws CommerceCartModificationException {
        enableCartMovCheck();
        return getCartFacade().addToCart(code, quantity, searchQuery, recalculate);
    }

    @Override
    public boolean allowedToAccessCartWithCode(final String cartCode) {
        return getCartFacade().allowedToAccessCartWithCode(cartCode);
    }

    @Override
    public void setVoucherCodeToRedeem(final String voucherCode) {
        validateParameterNotNullStandardMessage("voucherCode", voucherCode);

        final CartModel cartModel = getSessionCartModel();
        final DistErpVoucherInfoModel info = new DistErpVoucherInfoModel();
        info.setCode(voucherCode);
        info.setReturnERPCode("00");
        info.setCalculatedInERP(false);
        cartModel.setErpVoucherInfo(info);
        modelService.save(cartModel);
    }

    @Override
    public String getVoucherReturnCodeFromCurrentCart() {
        final DistErpVoucherInfoData erpVoucherInfoData = getCartFacade().getSessionCart().getErpVoucherInfoData();
        if (erpVoucherInfoData != null && StringUtils.equals("00", erpVoucherInfoData.getReturnERPCode())
                && Boolean.TRUE.equals(erpVoucherInfoData.getValid())) {
            return erpVoucherInfoData.getReturnERPCode();
        }
        return null;
    }

    @Override
    public boolean isSessionCartCalculated() {
        if (hasSessionCart()) {
            return BooleanUtils.isTrue(getSessionCartModel().getCalculated());
        }
        return Boolean.FALSE;
    }

    @Override
    public void resetVoucherOnCurrentCart() {
        if (hasSessionCart()) {
            final CartModel cartModel = getSessionCartModel();
            if (cartModel.getErpVoucherInfo() != null) {
                cartModel.setErpVoucherInfo(null);
                modelService.save(cartModel);
            }
        }
    }

    @Override
    public AddToCartBulkResponseData addToCartBulk(final AddToCartBulkRequestData addToCartBulkRequestData) {
        final List<String> phaseOutProducts = new ArrayList<>();
        final List<String> punchedOutProducts = new ArrayList<>();
        final List<String> errorProducts = new ArrayList<>();
        final List<String> notFoundProducts = new ArrayList<>();
        final List<String> blockedProducts = new ArrayList<>();

        List<CartModificationData> cartModificationData = addToCartBulkRequestData.getProducts()
                                                                                  .stream()
                                                                                  .map(product -> {
                                                                                      String normalizedProductCode = normalizeProductCode(product.getProductCode());
                                                                                      try {
                                                                                          checkIsProductBuyable(normalizedProductCode, product.getQuantity());
                                                                                          AddToCartParams addToCartParams = buildAddToCartParams(addToCartBulkRequestData,
                                                                                                                                                 product,
                                                                                                                                                 normalizedProductCode);
                                                                                          CartModificationData cartModification = getCartFacade().addToCart(addToCartParams);
                                                                                          if (cartModification.getQuantityAdded() == 0L) {
                                                                                              throw new ProductLowStockException("Add to cart failed, quantity equals 0",
                                                                                                                                 "basket.information.quantity.noItemsAdded."
                                                                                                                                                                          + cartModification.getStatusCode());
                                                                                          } else if (cartModification.getQuantityAdded() < product.getQuantity()) {
                                                                                              throw new ProductLowStockException("Add to cart failed, quantity equals 0",
                                                                                                                                 "basket.information.quantity.reducedNumberOfItemsAdded."
                                                                                                                                                                          + cartModification.getStatusCode());
                                                                                          }
                                                                                          return cartModification;
                                                                                      } catch (ProductLowStockException e) {
                                                                                          phaseOutProducts.add(normalizedProductCode);
                                                                                          return null;
                                                                                      } catch (PunchoutException e) {
                                                                                          punchedOutProducts.add(normalizedProductCode);
                                                                                          return null;
                                                                                      } catch (BlockedProductException e) {
                                                                                          blockedProducts.add(normalizedProductCode);
                                                                                          return null;
                                                                                      } catch (InvalidQuantityForSapCatalogException e) {
                                                                                          errorProducts.add(normalizedProductCode);
                                                                                          return null;
                                                                                      } catch (final UnknownIdentifierException e) {
                                                                                          LOGGER.warn("Couldn't add product of code {} to cart {}",
                                                                                                      normalizedProductCode,
                                                                                                      getCartService().getSessionCart().getCode(), e);
                                                                                          notFoundProducts.add(normalizedProductCode);
                                                                                          return null;
                                                                                      } catch (AddToCartDisabledException ex) {
                                                                                          throw new CartException("Add to cart is disabled",
                                                                                                                  "add.to.cart.disabled", ex);
                                                                                      } catch (CommerceCartModificationException ex) {
                                                                                          LOGGER.warn("Couldn't add product of code {} to cart {}",
                                                                                                      normalizedProductCode,
                                                                                                      getCartService().getSessionCart().getCode(), ex);
                                                                                          errorProducts.add(normalizedProductCode);
                                                                                          return null;
                                                                                      }
                                                                                  }).filter(Objects::nonNull)
                                                                                  .collect(Collectors.toList());
        AddToCartBulkResponseData response = new AddToCartBulkResponseData();
        response.setCartModifications(cartModificationData);
        response.setErrorProducts(getProductData(errorProducts));
        response.setPunchOutProducts(getProductData(punchedOutProducts));
        response.setPhaseOutProducts(getProductData(phaseOutProducts));
        response.setBlockedProducts(getProductData(blockedProducts));
        return response;
    }

    private AddToCartParams buildAddToCartParams(AddToCartBulkRequestData addToCartBulkRequestData, BulkProductData product, String normalizedProductCode) {
        return aAddToCartParamsBuilder().withProductCode(normalizedProductCode)
                                        .withQuantity(product.getQuantity())
                                        .withReference(product.getReference())
                                        .withAddedFrom(addToCartBulkRequestData.getAddedFrom())
                                        .build();
    }

    private List<ProductData> getProductData(List<String> errorProducts) {
        return errorProducts
                            .stream()
                            .map(product -> productFacade.getProductForCodeAndOptions(product, PRODUCT_OPTIONS))
                            .collect(Collectors.toList());
    }

    private String normalizeProductCode(final String code) {
        return StringUtils.isEmpty(code) ? null : code.replace(DistConstants.Punctuation.DASH, StringUtils.EMPTY);
    }

    @Override
    public CartData emptyCart() throws CalculationException {
        final CartModel cartModel = getSessionCartModel();
        if (cartModel != null && CollectionUtils.isNotEmpty(cartModel.getEntries())) {
            modelService.removeAll(cartModel.getEntries());
            cartModel.setErpVoucherInfo(null);
            modelService.save(cartModel);
            modelService.refresh(cartModel);
            recalculateCart();
        }
        return getCartFacade().getSessionCart();
    }

    @Override
    public boolean updateCustomerEmailForOOS(final String customerEmail, final List<String> articleNumber) {

        boolean wasSavingOosSuccessful = false;
        if (StringUtils.isNotEmpty(customerEmail) && CollectionUtils.isNotEmpty(articleNumber)) {
            final List<OrderEntryData> outOfStockCartItems = getOutOfStockItems(articleNumber);
            boolean hasDiscontinuedProduct = outOfStockCartItems.stream().map(OrderEntryData::getProduct)
                                                                .filter(Objects::nonNull)
                                                                .anyMatch(e -> erpStatusUtil
                                                                                            .getErpSalesStatusFromConfiguration(ATTRIBUTE_NO_PRODUCT_FOR_SALE_OUT_OF_STOCK)
                                                                                            .contains(e.getSalesStatus()));

            if (!hasDiscontinuedProduct && CollectionUtils.isNotEmpty(outOfStockCartItems)) {
                wasSavingOosSuccessful = outOfStockFacade.saveBackOrderOutOfStock(customerEmail,
                                                                                  outOfStockCartItems);
            }
        }
        return wasSavingOosSuccessful;
    }

    private List<OrderEntryData> getOutOfStockItems(final List<String> articleNumbers) {
        return getSessionCart().getEntries()
                               .stream()
                               .filter(entry -> nonNull(entry) && nonNull(entry.getProduct()))
                               .filter(entry -> articleNumbers.stream()
                                                              .anyMatch(articleNumber -> entry.getProduct().getCode().equalsIgnoreCase(articleNumber)))
                               .collect(Collectors.toList());
    }

}
