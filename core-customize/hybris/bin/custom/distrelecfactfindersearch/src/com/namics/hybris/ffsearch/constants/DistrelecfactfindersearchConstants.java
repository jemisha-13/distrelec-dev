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
package com.namics.hybris.ffsearch.constants;

import java.util.regex.Pattern;

/**
 * Global class for all Distrelecfactfindersearch constants. You can add global constants for your extension into this class.
 */
public final class DistrelecfactfindersearchConstants extends GeneratedDistrelecfactfindersearchConstants {

    public static final String EXTENSIONNAME = "distrelecfactfindersearch";

    public static final String PRICE_LIST = "priceList";

    public static final String CATEGORY_CODE_PREFIX = "category";
    public static final String CATEGORY_CODE_SUFFIX = "Code";
    public static final Pattern CATEGORY_CODE_PATTERN = Pattern.compile("^" + CATEGORY_CODE_PREFIX + "(\\d+)" + CATEGORY_CODE_SUFFIX + "$");

    public static final String CATEGORY_CODE_ROOT_PATH_PREFIX = "categoryCodePathROOT";
    public static final String SUB_CATEGORY_CODE_ROOT_PATH_PREFIX = CATEGORY_CODE_ROOT_PATH_PREFIX + "/";
    public static final Pattern CATEGORY_CODE_ROOT_PATH_PATTERN = Pattern.compile("^" + CATEGORY_CODE_ROOT_PATH_PREFIX + "(.*)$");

    public static final String PRODUCT_FAMILY_CODE = "productFamilyCode";

    public static final String NET = "Net";

    public static final String GROSS = "Gross";

    public static final String MIN = "Min";

    public static final String SEARCH_QUERY_PARAMETER_NAME = "q";
    public static final String TRACK_QUERY_PARAMETER_NAME = "trackQuery";
    public static final String PRODUCT_INFORMATION_QUERY_PARAMETER_NAME = "p";
    public static final String PAGE_PARAMETER_NAME = "page";
    public static final String ORIG_POS_PARAMETER_NAME = "origPos";
    public static final String ORIG_PAGE_SIZE_PARAMETER_NAME = "origPageSize";
    public static final String PAGE_SIZE_PARAMETER_NAME = "pageSize";
    public static final String SIMI_PARAMETER_NAME = "simi";
    public static final String MANUFACTURER_CODE_PARAMETER_NAME = "manufacturerCode";
    public static final String SORT_PARAMETER_NAME = "sort";
    public static final String OUTLET_SEARCH_PARAMETER_NAME = "outletSearch";
    public static final String NEW_SEARCH_PARAMETER_NAME = "newSearch";
    public static final String POS_PARAMETER_NAME = "pos";
    public static final String PRODUCT_PRICE = "prodprice";
    
    public static final String UNDEFINED = "undefined";

    public static final String FILTER = "filter_";
    public static final String SUBSTRING_FILTER = "substringFilter";

    public static final String FILTER_INSTOCK = "InStock";
    public static final String FILTER_PICKUP = "availableInPickup";
    public static final String FILTER_PRODUCT_STATUS = "productStatus";
    public static final String FILTER_INSTOCK_FAST = "search.nav.facet.instock.fast";
    public static final String FILTER_FACET_PRODUCT_STATUS = "search.nav.facet.productStatus";
    public static final String FILTER_AVAILABLE_IN_PICKUP = "search.nav.facet.available.in.pickup";
    public static final String FILTER_INSTOCK_SLOW = "search.nav.facet.instock.slow";
    public static final String FILTER_BOOLEAN_TRUE = "search.nav.facet.boolean.true";
    public static final String FILTER_BOOLEAN_FALSE = "search.nav.facet.boolean.false";
    public static final String FILTER_FAMILY_CATEGORY = "product.family.linkText";

    public static final String POSITION = "position";
    
    public static final String PRODUCT_FAMILY= "isProductFamily";

    public static final String QUERY = "query";
    public static final String LOG = "log";
    public static final String LOG_INTERNAL = "internal";
    public static final String LOG_INTERNAL_PARAM = LOG + "=" + LOG_INTERNAL;
    public static final String LOG_NAVIGATION = "navigation";

    public static final String MANUFACTURER_CODE = "manufacturerCode";

    public static final String ORIG_POSITION = "__ORIG_POSITION__";

    public static final String ORIG_PAGE_SIZE = "origPageSize";

    public static final String SIMILARITY = "similarity";

    public static final String FILTER_AVAILABLEINPICKUP = "availableInPickup";

    public static final String ADVISOR_CAMPAIGN_PARAMETER_PREFIX = "advisor_";
    public static final String ADVISOR_CAMPAIGN_PARAMETER_ANSWER_PATH_NAME = FILTER + ADVISOR_CAMPAIGN_PARAMETER_PREFIX + "answerPath";
    public static final String ADVISOR_CAMPAIGN_PARAMETER_CAMPAIGN_ID_NAME = FILTER + ADVISOR_CAMPAIGN_PARAMETER_PREFIX + "campaignId";
    public static final String CATEGORY_CODE_PATH_ROOT = FILTER + CATEGORY_CODE_ROOT_PATH_PREFIX;
    public static final String FILTER_CATEGORY_CODE_PATH_ROOT = "&" + CATEGORY_CODE_PATH_ROOT;
    public static final String TRACKING_PARAMETER = "track";
    public static final String PAGE_TYPE_PARAMETER = "pageType";
    public static final String CAMPAIGN_PARAMETER = "campaign";
    public static final String MANUFACTURER_CODE_PATH_ROOT = FILTER + MANUFACTURER_CODE_PARAMETER_NAME;

    private DistrelecfactfindersearchConstants() {
        // empty to avoid instantiating this constant class
    }

}
