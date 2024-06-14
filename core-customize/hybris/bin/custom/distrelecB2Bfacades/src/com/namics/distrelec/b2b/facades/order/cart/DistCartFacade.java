/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.cart;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;

import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationProductData;

import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.SystemException;

public interface DistCartFacade extends CartFacade {

    CartData getSessionCartIfPresent();

    @Override
    CartModificationData addToCart(final String code, final long quantity, final String searchQuery) throws CommerceCartModificationException;

    CartModificationData addToCart(final String code, final long quantity, final String searchQuery,
                                   final boolean recalculate) throws CommerceCartModificationException;

    CartModificationData addToCartWithoutCalcCart(String code, long quantity, String ref) throws CommerceCartModificationException;

    @Override
    CartModificationData updateCartEntry(long entryNumber, String customerReference) throws CommerceCartModificationException;

    CartModificationData updateCartEntry(long entryNumber, long quantity, final boolean recalculate) throws CommerceCartModificationException;

    CartModificationData updateCartEntry(final String code, long quantity) throws CommerceCartModificationException;

    CartModificationData removeFromCart(final String code) throws CommerceCartModificationException;

    void removeQuotationFromCart(final String quotationId) throws CommerceCartModificationException;

    void recalculateCart() throws CalculationException;

    Collection<PunchoutFilterResult> removeProductsWithPunchout();

    void persistCart();

    void loadCart(String code);

    CartModificationData addToCartExt(String code, long quantity) throws CommerceCartModificationException;

    void emptySessionCart() throws SystemException;

    void restoreSessionCart() throws SystemException;

    boolean isProductInCart(final String code);

    List<String> productsInCart(final String... codes);

    boolean allowedToAccessCartWithCode(final String cartCode);

    long checkCartLevel(String productCode);

    AbstractOrderEntryModel getOrderEntry(long entryNumber);

    void replace(final String target, final String replacement);

    Map<String, Object> addQuotationInToCart(final String quotationId, final Map<MultiKey, Long> productQuantities,
                                             boolean recalculate) throws CalculationException,
                                                                  CommerceCartModificationException;

    void addQuotationInToCart(final String quotationId, List<QuotationProductData> quotationProductData,
                              boolean recalculate) throws CommerceCartModificationException;

    PriceData getFreeShippingValue(BigDecimal value, String isoCode);

    CartData emptyCart() throws CalculationException;

    CartData getSessionMiniCart();

    List<OrderEntryData> getEntriesFromCart();

    String formatErpID(final String loginID, final int maxLength);

    List<CartModificationData> removeEndOfLifeProductsFromCart();

    List<CartModificationData> updatePhasedOutProducts();

    List<CartModificationData> updateEntriesWithMOQ();

    OrderEntryData getCartEntryForNumber(int number);

    void checkIsProductBuyable(String productCode, long quantity);

    boolean isInvalidQuantityForSAPCatalogProduct(ProductModel product, long quantity);

    List<CartModificationData> removeBlockedProductsFromCart();
}
