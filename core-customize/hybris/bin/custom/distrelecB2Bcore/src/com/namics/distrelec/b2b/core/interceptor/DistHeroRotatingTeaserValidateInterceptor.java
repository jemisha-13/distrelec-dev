/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Check if minimal number of required items is set on the component.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistHeroRotatingTeaserValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        final DistHeroRotatingTeaserModel component = (DistHeroRotatingTeaserModel) model;
        checkPictures(component);
    }

    protected void checkPictures(final DistHeroRotatingTeaserModel component) throws InterceptorException {
        if (component.getPicture() == null) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distherorotatingteaser.missing.picture"));
        }

        if (component.getThumbnail() == null) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distherorotatingteaser.missing.thumbnail"));
        }
    }
}
