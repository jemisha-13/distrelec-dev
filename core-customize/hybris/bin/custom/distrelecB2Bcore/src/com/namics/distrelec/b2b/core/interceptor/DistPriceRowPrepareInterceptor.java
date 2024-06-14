/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import com.namics.distrelec.b2b.core.service.netmargin.NetMarginCalculator;
import de.hybris.platform.europe1.enums.UserPriceGroup;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Proves that the given price row fulfills all the necessary values.
 * 
 * @author DAEHUSIR, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DistPriceRowPrepareInterceptor implements PrepareInterceptor<DistPriceRowModel> {

    @Autowired
    private NetMarginCalculator netMarginCalculator;

    @Override
    public void onPrepare(DistPriceRowModel priceRow, InterceptorContext interceptorContext) throws InterceptorException {
        if(hasNetMarginData(priceRow)) {
            priceRow.setNetMarginRank(netMarginCalculator.calculateNetMarginRank(
                    priceRow.getNetMargin() / priceRow.getUnitFactor(),
                    priceRow.getNetMarginPercentage()
            ));
        }
    }

    private boolean hasNetMarginData(DistPriceRowModel priceRow) {
        return priceRow.getNetMargin() != null && priceRow.getNetMarginPercentage() != null;
    }
}
