/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.interceptors.DefaultAbstractOrderEntryPreparer;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

/**
 * @author datneerajs, Namics AG
 * @since Distrelec 1.1
 *
 */
public class DefaultDistAbstractOrderEntryPreparer extends DefaultAbstractOrderEntryPreparer {

    private static final String NOT_AVAILABLE = "n/a";

    @Override
    protected String createEntryInformation(final AbstractOrderEntryModel newEntry, final InterceptorContext ctx) throws InterceptorException {
        final ProductModel product = newEntry.getProduct();
        return product == null ? NOT_AVAILABLE : "product " + product.getCode();
    }
}
