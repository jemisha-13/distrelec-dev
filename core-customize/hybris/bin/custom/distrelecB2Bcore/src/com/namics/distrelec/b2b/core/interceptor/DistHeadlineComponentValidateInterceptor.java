/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.cms2.components.DistHeadlineComponentModel;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Check if the component is already used on the current page.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistHeadlineComponentValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        final DistHeadlineComponentModel component = (DistHeadlineComponentModel) model;
        int headlineCounter = 0;
        if (CollectionUtils.isNotEmpty(component.getSlots())) {
            for (final ContentSlotModel contentSlot : component.getSlots()) {
                for (final AbstractCMSComponentModel cmsComponent : contentSlot.getCmsComponents()) {
                    if (cmsComponent instanceof DistHeadlineComponentModel && (cmsComponent != component)) {
                        headlineCounter++;
                    }
                }
            }
        }
        if (headlineCounter > 1) {
            throw new InterceptorException(l10nService.getLocalizedString("validations.distheadline.morethanone"));
        }
    }
}
