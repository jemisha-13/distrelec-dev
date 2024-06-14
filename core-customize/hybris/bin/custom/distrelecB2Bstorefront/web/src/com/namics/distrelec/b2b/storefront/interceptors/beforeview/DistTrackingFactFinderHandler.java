/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.interceptors.beforeview;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.ModelAndView;

import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.reco.impl.DefaultDistRecommendationFacade;
import com.namics.distrelec.b2b.facades.tracking.DistTrackingFactFinderFacade;
import com.namics.distrelec.b2b.storefront.controllers.misc.AddToCartController;
import com.namics.distrelec.b2b.storefront.controllers.pages.ProductPageController;
import com.namics.distrelec.b2b.storefront.controllers.pages.checkout.DistCheckoutOrderConfirmationController;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;

import de.hybris.platform.acceleratorstorefrontcommons.interceptors.BeforeViewHandler;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;

/**
 * BeforeViewHandler which propagates tracking information to FactFinder for specific requests (e.g. detailpage requests, add-to-cart
 * requests, recommended-detailpage requests or orderconfirmation-page-requests).
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class DistTrackingFactFinderHandler implements BeforeViewHandler {

    private static final PathMatcher PATH_MATCHER = new AntPathMatcher();

    protected static final Logger LOG = LogManager.getLogger(DistTrackingFactFinderHandler.class);

    @Autowired
    private DistTrackingFactFinderFacade trackingFacade;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade b2bCartFacade;

    @Override
    public void beforeView(final HttpServletRequest request, final HttpServletResponse response, final ModelAndView modelAndView) {

        final String servletPath = request.getServletPath();
        String productFamily = request.getParameter(DistrelecfactfindersearchConstants.PRODUCT_FAMILY);
        Boolean isProductFamily = productFamily == null ? false : Boolean.valueOf(productFamily);
        if (PATH_MATCHER.match(ProductPageController.PRODUCT_PAGE_REQUEST_MAPPING + ProductPageController.PRODUCT_CODE_PATH_VARIABLE_PATTERN, servletPath))

        {
            final ProductData productData = (ProductData) modelAndView.getModel().get(ProductPageController.PRODUCT_ATTRIBUTE_NAME);
            if (StringUtils.isNotEmpty(request.getParameter(DefaultDistRecommendationFacade.MAIN_ID_PARAMETER_NAME))) {
                getTrackingFacade().trackRecommendedProductClick(request, productData);
            }
            if (StringUtils.isNotEmpty(request.getParameter(DistrelecfactfindersearchConstants.TRACKING_PARAMETER)) && !isProductFamily) {
                getTrackingFacade().trackProductDetailClick(request, productData);
            }
        } else if (PATH_MATCHER.match(AddToCartController.CART_ADD_REQUEST_MAPPING + "*", servletPath)) {
            CartData data = getCartFacade().getSessionCartWithEntryOrdering(true);
            if (StringUtils.isNotEmpty(request.getParameter(DistrelecfactfindersearchConstants.PAGE_TYPE_PARAMETER)) && !isProductFamily) {
                final ProductData productData = (ProductData) modelAndView.getModel().get(ProductPageController.PRODUCT_ATTRIBUTE_NAME);
                getTrackingFacade().trackProductDetailClick(request, productData);
            }

            if (data.getEntries() != null && !data.getEntries().isEmpty()) {
                if (PATH_MATCHER.match(AddToCartController.QUOTATION_ADD_REQUEST_MAPPING, servletPath))
                    getTrackingFacade().trackAddBulkToCartClick(request, data.getEntries());
                else if (!isProductFamily) {
                    final Long addedQuantity = (Long) modelAndView.getModel().get(AddToCartController.ADDED_QUANTITY_ATTRIBUTE_NAME);
                    OrderEntryData orderData = (OrderEntryData) data.getEntries().get(0);
                    getTrackingFacade().trackAddToCartClick(request, orderData,
                                                            addedQuantity);
                }
            }

        } else if (PATH_MATCHER.match(AddToCartController.CART_ADD_BULK_REQUEST_MAPPING + "*", servletPath)) {
            CartData data = getCartFacade().getSessionCartWithEntryOrdering(true);

            if (data.getEntries() != null)
                getTrackingFacade().trackAddBulkToCartClick(request, data.getEntries());

        } else if (PATH_MATCHER.match(DistCheckoutOrderConfirmationController.ORDER_CONFIRMATION_PATH_VARIABLE_PATTERN + "/"
                                      + DistCheckoutOrderConfirmationController.ORDER_CODE_PATH_VARIABLE_PATTERN, servletPath)) {
            getTrackingFacade().trackOrderConfirmationPage(request, getOrderData(modelAndView));

        }

    }

    protected OrderData getOrderData(final ModelAndView modelAndView) {
        return (OrderData) modelAndView.getModel().get(DistCheckoutOrderConfirmationController.ORDER_ATTRIBUTE_NAME);
    }

    protected OrderEntryData getCartEntryData(final ModelAndView modelAndView) {
        return (OrderEntryData) modelAndView.getModel().get(AddToCartController.CART_ENTRY);
    }

    public DistTrackingFactFinderFacade getTrackingFacade() {
        return trackingFacade;
    }

    public void setTrackingFacade(final DistTrackingFactFinderFacade trackingFacade) {
        this.trackingFacade = trackingFacade;
    }

    public DistB2BCartFacade getCartFacade() {
        return this.b2bCartFacade;
    }

}
