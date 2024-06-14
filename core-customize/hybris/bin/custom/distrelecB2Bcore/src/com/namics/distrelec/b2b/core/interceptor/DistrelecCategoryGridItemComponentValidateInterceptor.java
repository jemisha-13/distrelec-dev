/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.components.DistrelecCategoryGridItemComponentModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * {@code DistrelecCategoryGridItemComponentValidateInterceptor}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class DistrelecCategoryGridItemComponentValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.interceptor.ValidateInterceptor#onValidate(java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (!(model instanceof DistrelecCategoryGridItemComponentModel)) {
            return;
        }

        final DistrelecCategoryGridItemComponentModel component = (DistrelecCategoryGridItemComponentModel) model;

        if (component.getCategory() == null && (component.getTitle() == null || component.getIcon() == null || component.getUrl() == null)) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distcategoryitemgrid.empty.error"));
        }
    }
}
