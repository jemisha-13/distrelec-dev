/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order;

import java.util.Map;

import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;

import de.hybris.platform.core.model.order.CartModel;

/**
 * This interface provides methods to handling the payment process.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public interface DistCommercePaymentService {

    String getPaymentUrlForCart(final CartModel cartModel, final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent);

    Map<String, String> getPaymentParameters(final CartModel cartModel, final DistSiteBaseUrlResolutionService.MobileUserAgent userAgent);

    String handlePaymentSuccessFailure(final Map<String, String> encryptedPaymentParamsMap);

    Map<String, String> handlePaymentNotify(final String requestBody, final String currencycode);
}
