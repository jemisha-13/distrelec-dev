/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.enums.CmsWarningType;
import com.namics.distrelec.b2b.core.model.cms2.components.DistWarningComponentModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * For "promotion" and "information" is no icon designated and "displayIcon" has to be false.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistWarningComponentValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        final DistWarningComponentModel component = (DistWarningComponentModel) model;
        if (component.getWarningType().equals(CmsWarningType.INFORMATION) || component.getWarningType().equals(CmsWarningType.PROMOTION)) {
            if (component.getDisplayIcon().equals(Boolean.TRUE)) {
                throw new InterceptorException(l10nService.getLocalizedString("validations.distwarning.noicon", new Object[] { component.getWarningType() }));
            }
        }
    }
}
