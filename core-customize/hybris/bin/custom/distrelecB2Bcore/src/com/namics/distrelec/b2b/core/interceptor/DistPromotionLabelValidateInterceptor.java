/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;

import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

public class DistPromotionLabelValidateInterceptor implements ValidateInterceptor {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        if (model instanceof DistPromotionLabelModel) {
            final DistPromotionLabelModel promoLabelModel = (DistPromotionLabelModel) model;
            if (Boolean.TRUE.equals(promoLabelModel.getMaintainedInDistSalesOrgProduct())) {
                if (Boolean.TRUE.equals(promoLabelModel.getMaintainedInProductCountry())) {
                    throw new InterceptorException(l10nService.getLocalizedString("validation.distpromotionlabel.maintainer"));
                }
            }
        }
    }

}
