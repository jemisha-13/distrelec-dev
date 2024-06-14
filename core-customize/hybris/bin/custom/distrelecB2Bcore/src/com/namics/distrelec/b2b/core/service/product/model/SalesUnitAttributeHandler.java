/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.model;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.AbstractDynamicLocalizedAttributeHandler;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

/**
 * Dynamic attribute handler for sales unit.
 * 
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class SalesUnitAttributeHandler extends AbstractDynamicLocalizedAttributeHandler<String, ProductModel> {

    @Autowired
    private I18NService i18NService;

    @Override
    public String get(final ProductModel model) {
        return get(model, getI18NService().getCurrentLocale());
    }

    @Override
    public String get(final ProductModel model, final Locale loc) {
        if (model.getSalesUnit() != null) {
            return model.getSalesUnit().getRelevantName(loc);
        } else if (model.getUnit() != null && null !=model.getUnit().getName(loc)) {
            return model.getUnit().getName(loc);
        }
        return "-";
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(I18NService i18nService) {
        i18NService = i18nService;
    }

}
