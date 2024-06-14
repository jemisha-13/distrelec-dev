/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistMaterialTypeModel;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.exception.NotFoundException;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Set showUsedLabel to true if material type was changed to ZOCC.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class ProductShowUsedLabelValidateInterceptor implements ValidateInterceptor {

    @Autowired
    private DistrelecCodelistService distCodelistService;

    private static final String MATERIAL_TYPE_USED = "ZOCC";

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        final ProductModel product = (ProductModel) model;
        final DistMaterialTypeModel materialTypeUsed = getMaterialTypeUsed();

        if (materialTypeUsed != null && materialTypeUsed.equals(product.getMaterialType())) {
            product.setShowUsedLabel(true);
        } else {
            product.setShowUsedLabel(false);
        }
    }

    private DistMaterialTypeModel getMaterialTypeUsed() {
        try {
            return distCodelistService.getDistrelecMaterialType(MATERIAL_TYPE_USED);
        } catch (final NotFoundException nfe) { // NOPMD
            // ignore
        }
        return null;
    }
}
