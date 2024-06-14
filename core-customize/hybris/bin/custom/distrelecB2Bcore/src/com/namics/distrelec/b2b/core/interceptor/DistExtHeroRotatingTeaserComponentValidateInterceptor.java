/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.enums.CmsComponentWidth;
import com.namics.distrelec.b2b.core.interceptor.exceptions.DistValidationInterceptorException;
import com.namics.distrelec.b2b.core.model.cms2.components.DistExtHeroRotatingTeaserComponentModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * {@code DistExtHeroRotatingTeaserComponentValidateInterceptor}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class DistExtHeroRotatingTeaserComponentValidateInterceptor implements ValidateInterceptor {

    /**
     * Default values used for {@code minItemsFullWidth} and {@code minItemsTwoThird}
     */
    protected static final int MIN_ITEMS_FULLWIDTH = 2;
    protected static final int MIN_ITEMS_TWOTHIRD = 2;

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof DistExtHeroRotatingTeaserComponentModel)) {
            return;
        }
        final DistExtHeroRotatingTeaserComponentModel component = (DistExtHeroRotatingTeaserComponentModel) model;
        final CmsComponentWidth componentWidth = component.getComponentWidth();
        if (componentWidth.equals(CmsComponentWidth.FULLWIDTH) || componentWidth.equals(CmsComponentWidth.THREEQUARTERS)) {
            if (component.getHeroRotatingTeaserItems() == null || component.getHeroRotatingTeaserItems().size() < MIN_ITEMS_FULLWIDTH) {
                throw new DistValidationInterceptorException(l10nService.getLocalizedString("validations.distherorotatingteasercomponent.notenoughitems",
                        new Object[] { Integer.valueOf(MIN_ITEMS_FULLWIDTH) }));
            }
        }
        if (componentWidth.equals(CmsComponentWidth.TWOTHIRD) || componentWidth.equals(CmsComponentWidth.ONETHIRD)) {
            if (component.getHeroRotatingTeaserItems() == null || component.getHeroRotatingTeaserItems().size() < MIN_ITEMS_TWOTHIRD) {
                throw new DistValidationInterceptorException(l10nService.getLocalizedString("validations.distherorotatingteasercomponent.notenoughitems",
                        new Object[] { Integer.valueOf(MIN_ITEMS_TWOTHIRD) }));
            }
        }
    }

}
