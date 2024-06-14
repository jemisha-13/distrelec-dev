/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.model.DistPriceRowModel;

import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Proves that the given price row fulfills all the necessary values.
 * 
 * @author DAEHUSIR, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DistPriceRowValidateInterceptor implements ValidateInterceptor {

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (model instanceof DistPriceRowModel) {
            final DistPriceRowModel priceRow = (DistPriceRowModel) model;

            if (priceRow.getUg() == null) {
                throw new IllegalArgumentException("Empty user group is not allowed. Please select a user group for this price row.");
            }

            if (priceRow.getUnitFactor() == null || priceRow.getUnitFactor().intValue() == 0) {
                throw new IllegalArgumentException("Unit factor is null or 0. Should be 1 or bigger.");
            }

            if (priceRow.getMinqtd() == null || priceRow.getMinqtd().longValue() == 0) {
                throw new IllegalArgumentException("Minimum quantity is null or 0. Should be 1 or bigger.");
            }
            
            if (UserPriceGroup.valueOf("SalesOrg_UPG_7350_01").equals(priceRow.getUg()) && !priceRow.getCurrency().getIsocode().equalsIgnoreCase("eur")) {
                throw new IllegalArgumentException("For SalesOrg 7350 we only accept the currency EUR");
            }
        }
    }

}
