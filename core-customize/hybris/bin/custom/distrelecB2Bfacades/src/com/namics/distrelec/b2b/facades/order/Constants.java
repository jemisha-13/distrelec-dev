/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order;

import com.namics.distrelec.b2b.core.jalo.session.DistJaloSession;
import de.hybris.platform.order.impl.DefaultCartService;

/**
 * {@code Constants}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public final class Constants {

    public static final String ORDER_STATUS = "ORDER_STATUS";
    public static final String CURRENCY_CODE = "CURRENCY_CODE";
    public static final String ORDER_DATE = "ORDER_DATE";
    public static final String ORDER_TOTAL = "ORDER_TOTAL";

    public static final String ORDER_STATUS_UNKNOWN_VALUE = "UNKNOWN";

    public static final String DIST_BACKUP_CART = DistJaloSession.DIST_BACKUP_CART;

    public static final String SESSION_CART_PARAMERER_NAME = DefaultCartService.SESSION_CART_PARAMETER_NAME;

}
