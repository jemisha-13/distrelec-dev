/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetItemModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Check if manual or search products are defined, not manual and search products are defined and at least 5 products are manual added.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistCarpetItemValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {

        final DistCarpetItemModel component = (DistCarpetItemModel) model;
        checkContent(component);
    }

    private void checkContent(final DistCarpetItemModel component) throws InterceptorException {
        if (component.getProduct() == null && component.getContentTeaser() == null) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.item.nooptionselected"));
        }

        if (component.getProduct() != null && component.getContentTeaser() != null) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.item.multipleoptionsselected"));
        }
    }

}
