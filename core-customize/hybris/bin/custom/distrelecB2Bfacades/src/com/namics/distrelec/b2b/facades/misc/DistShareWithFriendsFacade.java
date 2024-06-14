/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.misc;

import com.namics.distrelec.b2b.core.event.DistSendToFriendEvent;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Share with friends facade interface.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public interface DistShareWithFriendsFacade {

    /**
     * Share product with friend by starting "sendToFriendEmailProcess".
     * 
     * @param distSendToFriendEvent
     * @param productModel
     */
    void shareProductWithFriends(final DistSendToFriendEvent distSendToFriendEvent, ProductModel productModel, String productPageUrl);

    void shareCartWithFriends(final DistSendToFriendEvent distSendToFriendEvent, CartModel cartModel, File csvFile);

    void shareCartPdfWithFriends(DistSendToFriendEvent distSendToFriendEvent, CartModel cartModel, InputStream pdfStream);

    void shareSearchResultsWithFriends(final DistSendToFriendEvent distSendToFriendEvent, String searchPageUrl);

    void shareProductComparisonWithFriends(final DistSendToFriendEvent distSendToFriendEvent, List<ProductData> productList);
}
