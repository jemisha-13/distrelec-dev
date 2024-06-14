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
package com.namics.distrelec.b2b.facades.constants;

/**
 * Global class for all B2BAcceleratorFacades constants. You can add global constants for your extension into this class.
 */
@SuppressWarnings("PMD")
public class Namb2bacceleratorFacadesConstants extends GeneratedNamb2bacceleratorFacadesConstants {
    public static final String EXTENSIONNAME = "distrelecB2Bfacades";

    /**
     * Session attribute key for the SalesOrg.country
     */
    public static final String SALESORG_COUNTRY_SESSION_ATTR_KEY = "salesorgcountry".intern();

    /**
     * Session attribute key for the channel
     */
    public static final String CHANNEL_SESSION_ATTR_KEY = "channel".intern();

    /**
     * Session attribute key for the technical view flag
     */
    public static final String TECHNICAL_VIEW_SESSION_ATTR_KEY = "usetechnicalview".intern();

    /**
     * Session attribute key for the icon view flag
     */
    public static final String ICON_VIEW_SESSION_ATTR_KEY = "useiconview".intern();

    /**
     * Session attribute key for the list view flag
     */
    public static final String LIST_VIEW_SESSION_ATTR_KEY = "uselistview".intern();

    /**
     * Session attribute key for use detail view for PLP
     */
    public static final String PLP_USE_DETAIL_VIEW = "useDetailView".intern();

    /**
     * Session attribute key for the auto apply filter flag
     */
    public static final String AUTO_APPLY_FILTER_SESSION_ATTR_KEY = "autoapplyfilter".intern();

    public static final String PAGE_VIEW_PER_CATEGORY_MAP = "pageViewPerCategoryMap".intern();

    private Namb2bacceleratorFacadesConstants() {
        super();
        assert false;
    }
}
