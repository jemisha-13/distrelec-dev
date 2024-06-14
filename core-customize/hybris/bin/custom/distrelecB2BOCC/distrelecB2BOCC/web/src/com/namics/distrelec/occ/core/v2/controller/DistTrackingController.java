/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.v2.controller;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import com.namics.distrelec.occ.core.swagger.CommonQueryParams;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.customer.DistCustomerFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.tracking.DistTrackingFactFinderFacade;
import com.namics.distrelec.b2b.facades.user.DistUserFacade;

import de.hybris.platform.commercefacades.order.OrderFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.servicelayer.session.SessionService;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Session.IS_STOREFRONT_REQUEST;
import static com.namics.distrelec.b2b.facades.reco.impl.DefaultDistRecommendationFacade.MAIN_ID_PARAMETER_NAME;
import static com.namics.distrelec.b2b.facades.tracking.impl.DefaultDistTrackingFactFinderFacade.FF_QUERY_SESSION_PARAMETER;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.PRODUCT_FAMILY;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.TRACKING_PARAMETER;
import static com.namics.hybris.ffsearch.service.impl.FactFinderTrackingServiceImpl.LB_COOKIE_NAME;
import static de.hybris.platform.commercefacades.product.ProductOption.BASIC;
import static de.hybris.platform.commercefacades.product.ProductOption.PRICE;
import static java.lang.Boolean.FALSE;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang.BooleanUtils.toBoolean;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.math.NumberUtils.toLong;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.notFound;
import static org.springframework.http.ResponseEntity.ok;

@Controller("distTrackingController")
@RequestMapping(value = "/{baseSiteId}/fftrack")
@CommonQueryParams
public class DistTrackingController {

    private static final Logger LOG = Logger.getLogger(DistTrackingController.class);

    private static final String GUID_PATTERN = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";

    private static final String PRODUCT_PARAMETER = "product";

    private static final String ORDER_PARAMETER = "order";

    private static final String QUANTITY_PARAMETER = "quantity";

    private static final String TRACK_LOGIN = "login";

    private static final String TRACK_PRODUCT_CLICK = "click";

    private static final String TRACK_CART_ADD = "cart";

    private static final String TRACK_RECOMMENDED_PRODUCT_CLICK = "recommendationClick";

    private static final String TRACK_ORDER_CONFIRMATION = "checkout";

    private static final String CHECKOUT_PRODUCT_QUERY_TRACK_PARAM = "trackQuery";

    @Autowired
    private DistTrackingFactFinderFacade distTrackingFactFinderFacade;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private DistCustomerFacade b2bCustomerFacade;

    @Autowired
    private DistUserFacade distUserFacade;

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade b2bCartFacade;

    @Autowired
    private OrderFacade orderFacade;

    @GetMapping
    public ResponseEntity trackEvents(HttpServletRequest request, @RequestParam String sid, @RequestParam String event) {
        try {
            setOccTracking();
            ResponseEntity.HeadersBuilder<?> responseBuilder = trackEventInternal(request, event);
            enrichResponse(responseBuilder);
            return responseBuilder.build();
        } catch (Exception ex) {
            LOG.error("Error tracking " + event + " event!", ex);
            return badRequest().build();
        }
    }

    private void setOccTracking() {
        sessionService.setAttribute(IS_STOREFRONT_REQUEST, FALSE);
    }

    private ResponseEntity.HeadersBuilder<?> trackEventInternal(HttpServletRequest request, String event) {
        switch (event) {
            case TRACK_LOGIN:
                return trackLoginEvent(request);
            case TRACK_PRODUCT_CLICK:
                return trackProductClickEvent(request);
            case TRACK_RECOMMENDED_PRODUCT_CLICK:
                return trackRecommendedProductClickEvent(request);
            case TRACK_CART_ADD:
                return trackCartAddEvent(request);
            case TRACK_ORDER_CONFIRMATION:
                return trackOrderConfirmationEvent(request);
            default:
                return notFound();
        }
    }

    private void enrichResponse(ResponseEntity.HeadersBuilder<?> headersBuilder) {
        String factFinderAWSCookieValue = sessionService.getAttribute(LB_COOKIE_NAME);
        if (isNotBlank(factFinderAWSCookieValue)) {
            headersBuilder.header(SET_COOKIE, LB_COOKIE_NAME + "=" + factFinderAWSCookieValue);
        }
    }

    private boolean isProductFamily(HttpServletRequest request) {
        String productFamily = request.getParameter(PRODUCT_FAMILY);
        return toBoolean(productFamily);
    }

    private ResponseEntity.BodyBuilder trackLoginEvent(HttpServletRequest request) {
        try {
            validateForLoginEvent();
            CustomerData currentCustomer = b2bCustomerFacade.getCurrentCustomer();
            distTrackingFactFinderFacade.trackLogin(request, currentCustomer);
            return ok();
        } catch (Exception ex) {
            LOG.error("Error tracking login event!", ex);
            return badRequest();
        }
    }

    private void validateForLoginEvent() {
        if (distUserFacade.isAnonymousUser()) {
            throw new IllegalArgumentException("Anonymous user can't trigger login track event!");
        }
    }

    private ResponseEntity.BodyBuilder trackProductClickEvent(HttpServletRequest request) {
        String track = request.getParameter(TRACKING_PARAMETER);
        if (toBoolean(track) && !isProductFamily(request)) {
            String productCode = request.getParameter(PRODUCT_PARAMETER);
            ProductData product = productFacade.getProductForCodeAndOptions(productCode, List.of(BASIC, PRICE));
            distTrackingFactFinderFacade.trackProductDetailClick(request, product);
        }
        return ok();
    }

    private ResponseEntity.BodyBuilder trackRecommendedProductClickEvent(HttpServletRequest request) {
        String mainId = request.getParameter(MAIN_ID_PARAMETER_NAME);
        if (isNotBlank(mainId)) {
            String productCode = request.getParameter(PRODUCT_PARAMETER);
            ProductData product = productFacade.getProductForCodeAndOptions(productCode, List.of(BASIC, PRICE));
            distTrackingFactFinderFacade.trackRecommendedProductClick(request, product);
        }
        return ok();
    }

    private ResponseEntity.BodyBuilder trackCartAddEvent(HttpServletRequest request) {
        CartData data = b2bCartFacade.getSessionCartWithEntryOrdering(true);
        if (data.getEntries() != null && !data.getEntries().isEmpty()) {
            Long addedQuantity = toLong(request.getParameter(QUANTITY_PARAMETER));
            OrderEntryData orderData = data.getEntries().get(0);
            distTrackingFactFinderFacade.trackAddToCartClick(request, orderData, addedQuantity);
        }
        return ok();
    }

    private ResponseEntity.BodyBuilder trackOrderConfirmationEvent(HttpServletRequest request) {
        String orderCode = request.getParameter(ORDER_PARAMETER);
        if (isBlank(orderCode)) {
            return ok();
        }
        setSessionForCheckoutTracking(orderCode, request);
        OrderData orderData = orderFacade.getOrderDetailsForCode(orderCode);
        distTrackingFactFinderFacade.trackOrderConfirmationPage(request, orderData);
        return ok();
    }

    private void setSessionForCheckoutTracking(String orderCode, HttpServletRequest request) {
        if (isGuestCheckout(orderCode)) {
            sessionService.setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
        }
        sessionService.setAttribute(FF_QUERY_SESSION_PARAMETER, getProductTrackingQueries(request));
    }

    /**
     * In case of guest checkout, GUID is sent instead of order code
     */
    private boolean isGuestCheckout(String orderCode) {
        Pattern guidRegex = Pattern.compile(GUID_PATTERN);
        return guidRegex.matcher(orderCode).matches();
    }

    private Map<String, String> getProductTrackingQueries(HttpServletRequest request) {
        return request.getParameterMap().entrySet()
                      .stream()
                      .filter(entry -> entry.getKey().startsWith(CHECKOUT_PRODUCT_QUERY_TRACK_PARAM))
                      .collect(toMap(e -> getProductCode(e.getKey()),
                                     e -> getProductQuery(e.getValue()),
                                     (a, b) -> b));
    }

    /**
     * @param key is in format trackQuery[CODE]
     */
    private String getProductCode(String key) {
        return key.replace(CHECKOUT_PRODUCT_QUERY_TRACK_PARAM, EMPTY)
                  .replace("[", EMPTY)
                  .replace("]", EMPTY);
    }

    private String getProductQuery(String[] value) {
        return Stream.of(value).findFirst().orElse(null);
    }

}
