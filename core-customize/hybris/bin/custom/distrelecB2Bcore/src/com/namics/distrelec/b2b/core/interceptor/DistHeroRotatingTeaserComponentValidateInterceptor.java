/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.enums.CmsComponentWidth;
import com.namics.distrelec.b2b.core.interceptor.exceptions.DistValidationInterceptorException;
import com.namics.distrelec.b2b.core.model.cms2.components.DistHeroRotatingTeaserComponentModel;

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
public class DistHeroRotatingTeaserComponentValidateInterceptor implements ValidateInterceptor {

    protected static final int MIN_ITEMS_FULLWIDTH = 7;
    protected static final int MIN_ITEMS_TWOTHIRD = 5;

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        final DistHeroRotatingTeaserComponentModel component = (DistHeroRotatingTeaserComponentModel) model;
        if (component.getComponentWidth().equals(CmsComponentWidth.FULLWIDTH)) {
            if (component.getHeroRotatingTeaserItems() == null || component.getHeroRotatingTeaserItems().size() < MIN_ITEMS_FULLWIDTH) {
                throw new DistValidationInterceptorException(l10nService.getLocalizedString("validations.distherorotatingteasercomponent.notenoughitems",
                        new Object[] { Integer.valueOf(MIN_ITEMS_FULLWIDTH) }));
            }
        }
        if (component.getComponentWidth().equals(CmsComponentWidth.TWOTHIRD) || component.getComponentWidth().equals(CmsComponentWidth.ONETHIRD)) {
            if (component.getHeroRotatingTeaserItems() == null || component.getHeroRotatingTeaserItems().size() < MIN_ITEMS_TWOTHIRD) {
                throw new DistValidationInterceptorException(l10nService.getLocalizedString("validations.distherorotatingteasercomponent.notenoughitems",
                        new Object[] { Integer.valueOf(MIN_ITEMS_TWOTHIRD) }));
            }
        }
    }
}
