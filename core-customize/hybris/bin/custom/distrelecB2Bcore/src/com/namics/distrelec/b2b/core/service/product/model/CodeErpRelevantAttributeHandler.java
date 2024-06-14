/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.model;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.AbstractDynamicLocalizedAttributeHandler;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.I18NService;

/**
 * Dynamic attribute handler to return the ERP relevant article number.
 * 
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class CodeErpRelevantAttributeHandler extends AbstractDynamicLocalizedAttributeHandler<String, ProductModel> {

    @Autowired
    private I18NService i18NService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Override
    public String get(final ProductModel model) {
        return get(model, getI18NService().getCurrentLocale());
    }

    @Override
    public String get(final ProductModel model, final Locale loc) {
        return model.getCode();
    }

    public I18NService getI18NService() {
        return i18NService;
    }

    public void setI18NService(I18NService i18nService) {
        i18NService = i18nService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

}
