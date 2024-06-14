/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.tracking;

import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Facade for handling and propagate tracking events to FactFinder.
 *
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 *
 */
public interface DistTrackingFactFinderFacade {

    /**
     * Track when a user has clicked on a search result (=product detail page) from a product list.
     */
    void trackProductDetailClick(final HttpServletRequest request, final ProductData productData);

    /**
     * Track when a user has added a product to his cart.
     */
    void trackAddToCartClick(HttpServletRequest request, OrderEntryData cartEntryData, Long addedQuantity);

    /**
     * Track when a user has clicked on a recommended product from a carousel (delivered by FactFinder).
     */
    void trackRecommendedProductClick(final HttpServletRequest request, final ProductData productData);

    /**
     * Track when a user has sent an order and has arrived on the order overview page.
     */
    void trackOrderConfirmationPage(final HttpServletRequest request, final OrderData orderData);

    /**
     * Track when a user has sent a search result feedback.
     */
    void trackSearchFeedback(final HttpServletRequest request, final ProductData productData);

    /**
     * Track when a user adds shoping list to cart.
     */
    void trackAddBulkToCartClick(HttpServletRequest request, List<OrderEntryData> cartEntries);

    /**
     * Track when a user log in
     */
    void trackLogin(final HttpServletRequest request, final CustomerData customerData);
}
