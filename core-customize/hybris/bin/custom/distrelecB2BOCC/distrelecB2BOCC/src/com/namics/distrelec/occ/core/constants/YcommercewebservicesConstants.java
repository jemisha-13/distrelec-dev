/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.occ.core.constants;

@SuppressWarnings({ "deprecation", "squid:CallToDeprecatedMethod" })
public class YcommercewebservicesConstants extends GeneratedYcommercewebservicesConstants {
    public static final String MODULE_NAME = "distrelecB2BOCC";

    public static final String MODULE_WEBROOT = ("y" + "commercewebservices").equals(MODULE_NAME) ? "rest" : MODULE_NAME;

    public static final String CONTINUE_URL = "session_continue_url";

    public static final String CONTINUE_URL_PAGE = "session_continue_url_page";

    public static final String ENUM_VALUES_SEPARATOR = ",";

    public static final String HTTP_REQUEST_PARAM_LANGUAGE = "lang";

    public static final String LANGUAGE_ATTRIBUTE = "language";

    public static final String HTTP_REQUEST_PARAM_CURRENCY = "curr";

    public static final String ROOT_CONTEXT_PROPERTY = "commercewebservices.rootcontext";

    public static final String V2_ROOT_CONTEXT = "/" + MODULE_WEBROOT + "/v2";

    public static final String URL_SPECIAL_CHARACTERS_PROPERTY = "commercewebservices.url.special.characters";

    public static final String DEFAULT_URL_SPECIAL_CHARACTERS = "?,/";

    public static final String LOCATION = "Location";

    public static final String SLASH = "/";

    public interface SEOConstants
    {
        String META_TITLE = "metaTitle";

        String META_DESCRIPTION = "metaDescription";

        String META_IMAGE = "metaImage";

        String CANONICAL_URL = "canonicalUrl";

        String ALTERNATE_HREFLANG_URLS = "alternateHreflangUrls";

    }

    private YcommercewebservicesConstants() {
        // empty
    }
}
