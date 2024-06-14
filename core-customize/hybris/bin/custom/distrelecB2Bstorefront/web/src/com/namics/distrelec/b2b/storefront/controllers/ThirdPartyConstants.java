/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.controllers;

public interface ThirdPartyConstants {

    interface PageType {
        String OTHER = "other";
        String HOME = "home";
        String SEARCHRESULTS = "searchresults";
        String CATEGORY = "category";
        String PRODUCT = "product";
        String CART = "cart";
        String PURCHASE = "purchase";
    }

    interface Google {
        String API_KEY_ID = "googleApiKey";
        String API_VERSION = "googleApiVersion";
        String ANALYTICS_TRACKING_ID = "google.analytics.tracking.id";
        String ADWORDS_CONVERSION_ID = "google.adwords.conversion.id";
        String ADWORDS_CONVERSION_TRACKING_ID = "google.adwords.conversion.tracking.id";
        String ADWORDS_CONVERSION_LABEL = "google.adwords.conversion.label";
    }

    interface IntelliAd {
        String ID = "intelliad.id";
        String CART_TRACKING = "includeIntelliAdCartTracking";
        String REGISTRATION_TRACKING = "includeIntelliAdRegistrationTracking";
    }

    interface Affilinet {
        String CONVERSION_TRACKING_SITE_ID = "affilinet.conversion.tracking.site.id";
    }

    interface Webtrekk {
        String WT_OCI = "webtrekk.oci.trackId";
        String WT_ARIBA = "webtrekk.ariba.trackId";
        String WT_JS_MEDIA = "webtrekk.javascript.media";
        String WT_TOTAL_SEARCH_RESULTS = "wtTotalSearchResults";
        String WT_TRACKID = "wtTrackId";
        String WT_TRACK_DOMAIN = "wtTrackDomain";
        String WT_DOMAIN = "wtDomain";
        String WT_JS_FILE_URL = "webtrekkJsFileUrl";
        String WT_CONTENT_GROUP = "wtContentGroup";
        String WT_CHANNEL = "wtChannel";
        String WT_LANGUAGE = "wtLanguage";
        String WT_PAGE_ID = "wtPageId";
        String WT_PAGE_AREA_CODE = "wtPageAreaCode";
        String WT_CAT_AREA_CODE = "wtCatAreaCode";
        String WT_BASIC_PARAMS = "wtBasicParams";
        String WT_ADVANCED_PARAMS = "wtAdvancedParams";
        String WT_CUSTOMER_ID = "wtCustomerId";
        String WT_CONTENT_ID = "wtContentId";
        String WT_ERRORS = "wtErrors";
        String WT_ACTIVATED_EVENTS = "wtActivatedEvents";

        String WT_PRODUCT = "wtProduct";
        String WT_PRODUCT_CATEGORY = "wtProductCategory";
        String WT_PRODUCT_COST = "wtProductCost";
        String WT_CURRENCY = "wtCurrency";
        String WT_PRODUCT_STATUS = "wtProductStatus";
        String WT_PRODUCT_QUANTITY = "wtQuantity";
        String WT_ORDER_VALUE = "wtOrderValue";
        String WT_ORDER_ID = "wtOrderId";
        String WT_DISCOUNT_VALUE = "wtDiscountValue";
        String WT_VOUCHER_VALUE = "wtVoucherValue";
        String WT_SHIPPING_COSTS = "wtShippingCosts";
        String WT_PAYMENT_FEE = "wtPaymentFee";
        String WT_VAT = "wtVat";
        String WT_TOTAL_VALUE = "wtTotalValue";
        String WT_ORDER_CONFIRMATION_CONTENT_ID = "wtOrderConfirmationContentId";

        String WT_PARAM_ONSITESEARCHRESULTS = "10";
        String WT_PARAM_ERRORS = "9";
        String WT_PARAM_CONTENTGROUP = "8";
        String WT_PARAM_CHANNEL = "7";
        String WT_PARAM_AREA = "6";
        String WT_PARAM_LANGUAGE = "5";
        String WT_PARAM_PAGEID = "4";
        String WT_PARAM_DISCOUNTVALUE = "10";
        String WT_PARAM_VOUCHERVALUE = "9";
        String WT_PARAM_SHIPPINGCOSTS = "8";
        String WT_PARAM_PAYMENTFEE = "7";
        String WT_PARAM_VAT = "6";
        String WT_PARAM_TOTALVALUE = "5";

    }

    interface Jirafe {
        String API_URL = "jirafe.api.url";
        String API_TOKEN = "jirafe.api.token";
        String APPLICATION_ID = "jirafe.app.id";
        String VERSION = "jirafe.version";
        String DATA_URL = "jirafe.data.url";
        String SITE_ID = "jirafe.site.id";
    }

    interface SeoRobots // NOSONAR
    {
        String META_ROBOTS = "metaRobots";
        String INDEX_FOLLOW = "index,follow";
        String INDEX_NOFOLLOW = "index,nofollow";
        String NOINDEX_FOLLOW = "noindex,follow";
        String NOINDEX_NOFOLLOW = "noindex,nofollow";
    }

    
}
