/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

/**
 * Set UrlId for manufacturer based imported code.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistManufacturerUrlIdPrepareInterceptor implements PrepareInterceptor {

    @Override
    public void onPrepare(final Object model, final InterceptorContext ctx) throws InterceptorException {
        final DistManufacturerModel distManufacturer = (DistManufacturerModel) model;
        final String distManufacturerCode = distManufacturer.getCode();
        if (distManufacturerCode.startsWith("man_")) {
            distManufacturer.setUrlId(distManufacturerCode.replace("man_", ""));
        } else {
            distManufacturer.setUrlId(distManufacturerCode);
        }
    }
}
