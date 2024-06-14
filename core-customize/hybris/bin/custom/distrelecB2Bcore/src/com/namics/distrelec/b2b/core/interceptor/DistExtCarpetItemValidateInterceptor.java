/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.items.DistExtCarpetItemModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * {@code DistExtCarpetItemValidateInterceptor}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistExtCarpetItemValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (model instanceof DistExtCarpetItemModel) {
            final DistExtCarpetItemModel component = (DistExtCarpetItemModel) model;
            // One option must be selected
            if (component.getProduct() == null && component.getContentTeaser() == null) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.item.nooptionselected"));
            }
            // Only one option must be selected
            if (component.getProduct() != null && component.getContentTeaser() != null) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distcarpet.item.multipleoptionsselected"));
            }
        }
    }
}
