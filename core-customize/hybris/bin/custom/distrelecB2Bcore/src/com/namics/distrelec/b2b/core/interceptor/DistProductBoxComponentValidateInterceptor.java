/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import org.apache.commons.collections.CollectionUtils;

import com.namics.distrelec.b2b.core.model.cms2.components.DistProductBoxComponentModel;

import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

public class DistProductBoxComponentValidateInterceptor implements ValidateInterceptor {

    private static final Integer HORIZONTAL_MIN_PRODUCTS = Integer.valueOf(1);
    private static final Integer VERTICAL_MIN_PRODUCTS = Integer.valueOf(1);
    private static final Integer MAX_PRODUCTS = Integer.valueOf(5);
    private static final Integer CHECKOUT_HORIZONTAL_MIN_PRODUCTS = Integer.valueOf(4);

    @Override
    public void onValidate(final Object model, final InterceptorContext ctx) throws InterceptorException {
        final DistProductBoxComponentModel component = (DistProductBoxComponentModel) model;

        if (!component.isHeroProduct()) {
            if (CollectionUtils.isEmpty(component.getItems())) {
                throw new InterceptorException("The list of items must be non empty");
            }
            if (component.isCheckout()) {
                if (!component.isHorizontal()) {
                    throw new InterceptorException("For checkout components, the orientation must be horizontal");
                }
                if (component.getItems().size() < CHECKOUT_HORIZONTAL_MIN_PRODUCTS) {
                    throw new InterceptorException("For checkout horizontal orientation, the minimum of items is " + CHECKOUT_HORIZONTAL_MIN_PRODUCTS);
                }
            }

            if (component.isHorizontal() && component.getItems().size() < HORIZONTAL_MIN_PRODUCTS) {
                throw new InterceptorException("For horizontal orientation, the minimum of items is " + HORIZONTAL_MIN_PRODUCTS);
            } else if (!component.isHorizontal() && component.getItems().size() < VERTICAL_MIN_PRODUCTS) {
                throw new InterceptorException("For vertical orientation, the minimum of items is " + VERTICAL_MIN_PRODUCTS);
            }
        }

        if (CollectionUtils.isNotEmpty(component.getItems()) && component.getItems().size() > MAX_PRODUCTS) {
            throw new InterceptorException("The maximum number of items is " + MAX_PRODUCTS);
        }
    }
}
