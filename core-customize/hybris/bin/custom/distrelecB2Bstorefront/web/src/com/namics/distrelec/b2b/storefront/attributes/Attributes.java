/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.attributes;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.storefront.controllers.pages.AbstractSearchPageController.ShowMode;

/**
 * The {@link Attributes} class is a collection of global {@link Attribute}s.
 *
 * @author rmeier, Namics AG
 * @since Namics Extensions 1.0
 */
public final class Attributes {

    public static final OptionalAttribute<String> CURRENT_SITE_THEME = Attributes.optionalAttribute("currentSiteTheme", String.class);

    public static final OptionalAttribute<String> CONTINUE_URL = Attributes.optionalAttribute("session_continue_url", String.class);
    public static final OptionalAttribute<String> LAST_SEARCH_QUERY = Attributes.optionalAttribute("lastSearchQuery", String.class);
    public static final OptionalAttribute<String> LAST_SEARCH_TEXT = Attributes.optionalAttribute("lastSearchText", String.class);
    public static final OptionalAttribute<String> LAST_SEARCH_CATEGORY = Attributes.optionalAttribute("lastSearchCategory", String.class);
    public static final OptionalAttribute<Integer> LAST_SEARCH_PAGE_POSITION = Attributes.optionalAttribute("lastSearchPagePosition", Integer.class);
    public static final OptionalAttribute<String> LAST_SEARCH_SORT_CODE = Attributes.optionalAttribute("lastSearchSort", String.class);
    public static final OptionalAttribute<ShowMode> LAST_SEARCH_SHOW_MODE = Attributes.optionalAttribute("lastSearchShowMode", ShowMode.class);

    public static final OptionalAttribute<String> SEARCH_QUERY = Attributes.optionalAttribute("q", String.class);
    public static final OptionalAttribute<String> SEARCH_TEXT = Attributes.optionalAttribute("text", "");
    public static final OptionalAttribute<Integer> PAGE_POSITION = Attributes.optionalAttribute("page", Integer.valueOf(0));
    public static final OptionalAttribute<Integer> PAGE_SIZE = Attributes.optionalAttribute("pageSize", Integer.valueOf(12));
    public static final OptionalAttribute<String> SORT_CODE = Attributes.optionalAttribute("sort", String.class);

    public static final OptionalAttribute<String> SHOP_SETTINGS = Attributes.optionalAttribute(DistConstants.Cookie.SHOP_SETTINGS_NAME, String.class);
    public static final OptionalAttribute<String> COMPARE = Attributes.optionalAttribute(DistConstants.Cookie.COMPARE_NAME, String.class);
    public static final OptionalAttribute<String> LAST_VIEWED = Attributes.optionalAttribute(DistConstants.Cookie.LAST_VIEWED_NAME, String.class);
    public static final OptionalAttribute<String> FORCE_DESKTOP = Attributes.optionalAttribute(DistConstants.Cookie.FORCE_DESKTOP_NAME, String.class);
    public static final OptionalAttribute<String> SEO_FASTERIZE_VARIATION_KEY = Attributes.optionalAttribute("sessionSettings", String.class);
    public static final OptionalAttribute<String> FF_TRACKING_COOKIE = Attributes.optionalAttribute("f_fid", String.class);
    public static final OptionalAttribute<String> FF_ENSIGTHEN_STATS_COOKIE = Attributes.optionalAttribute("DISTRELEC_ENSIGHTEN_PRIVACY_Statistics", String.class);
    public static final OptionalAttribute<String> FF_ENSIGTHEN_PREFERENCES_COOKIE = Attributes.optionalAttribute("DISTRELEC_ENSIGHTEN_PRIVACY_Preferences", String.class);
    public static final OptionalAttribute<String> FF_ENSIGTHEN_MARKETING_COOKIE = Attributes.optionalAttribute("DISTRELEC_ENSIGHTEN_PRIVACY_Marketing", String.class);
    public static final OptionalAttribute<String> DYID_COOKIE = Attributes.optionalAttribute("_dyid", String.class);
    public static final OptionalAttribute<String> DYID_SERVER_COOKIE = Attributes.optionalAttribute("_dyid_server", String.class);

    static {
        SHOP_SETTINGS.setMaxAge(DistConstants.Cookie.SHOP_SETTINGS_MAX_AGE);
        COMPARE.setMaxAge(DistConstants.Cookie.COMPARE_MAX_AGE);
        LAST_VIEWED.setMaxAge(DistConstants.Cookie.LAST_VIEWED_MAX_AGE);
    }

    private Attributes() {

    }

    public static <M extends Object> RequiredAttribute<M> requiredAttribute(final String name, final Class<M> type) {
        return new RequiredAttribute<M>(name, type);
    }

    public static <M extends Object> OptionalAttribute<M> optionalAttribute(final String name, final Class<M> type) {
        return new OptionalAttribute<M>(name, type);
    }

    public static <M extends Object> OptionalAttribute<M> optionalAttribute(final String name, final M defaultValue) {
        return new OptionalAttribute<M>(name, defaultValue);
    }
}
