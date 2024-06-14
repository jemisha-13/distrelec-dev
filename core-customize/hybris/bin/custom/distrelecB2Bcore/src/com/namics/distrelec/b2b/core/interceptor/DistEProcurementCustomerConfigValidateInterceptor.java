/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.eprocurement.DistCustomerConfigModel;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * DistEProcurementRestrictCatalogs Validator.
 * 
 * @author pbueschi, Namics AG
 * 
 */
public class DistEProcurementCustomerConfigValidateInterceptor implements ValidateInterceptor {

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
        final DistCustomerConfigModel customerConfig = (DistCustomerConfigModel) model;
        if (CollectionUtils.isNotEmpty(customerConfig.getAllowedCategories())) {
            for (final CategoryModel category : customerConfig.getAllowedCategories()) {
                if (category.getLevel() == null || category.getLevel().intValue() != 1) {
                    throw new InterceptorException(l10nService.getLocalizedString("validations.distcustomerconfig.wrongcategory"));
                }
            }
        }
    }
}
