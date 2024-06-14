/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.interceptor.exceptions.DistValidationInterceptorException;
import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserItemModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Check if exactly one attribute is set..
 * 
 * @author pnueesch, Namics AG
 */
public class DistHeroRotatingTeaserItemValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof DistHeroRotatingTeaserItemModel)) {
            return;
        }
        final DistHeroRotatingTeaserItemModel item = (DistHeroRotatingTeaserItemModel) model;
        int attributesSet = 0;
        if (item.getProduct() != null) {
            attributesSet++;
        }
        if (item.getContentTeaser() != null) {
            attributesSet++;
        }
        if (item.getCategory() != null) {
            attributesSet++;
        }
        if (item.getManufacturer() != null) {
            attributesSet++;
        }
        if (attributesSet == 0) {
            throw new DistValidationInterceptorException(l10nService.getLocalizedString("validations.distherorotatingteaseritem.minitem"));
        } else if (attributesSet > 1) {
            throw new DistValidationInterceptorException(l10nService.getLocalizedString("validations.distherorotatingteaseritem.maxitem"));
        }
    }
}
