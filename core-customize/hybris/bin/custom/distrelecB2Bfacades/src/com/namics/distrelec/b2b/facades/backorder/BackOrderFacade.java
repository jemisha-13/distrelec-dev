/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.backorder;

import java.util.List;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * Backorder Facade Interface
 *
 */
public interface BackOrderFacade {

    void updateBackOrderItems(final List<OrderEntryData> backOrderItems);

    List<ProductData> getBackOrderAlternativeProducts(String productCode);

    List<ProductData> getProductFamilyProducts(String productCode);

    ProductData populateProductDetailForDisplay(String productCode);

    List<OrderEntryData> getPurchaseBlockedProducts(List<OrderEntryData> entries, List<String> codes);

    void removeBlockedProductsFromCart(List<OrderEntryData> blockedProducts);

    List<OrderEntryData> getBackOrderNotProfitableItems(List<OrderEntryData> cartEntries);

    List<ProductData> getAlternateProductList(final String productCode, final Integer productQuantityRequested);

    List<CartModificationData> updateBackOrderItemsForCurrentCart();

    List<CartModificationData> updateBlockedProductsForCurrentCart();
}
