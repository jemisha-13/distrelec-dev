/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.interceptor;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

/**
 * {@code DistNoOpCategoryPrepareInterceptor}
 * <p>
 * The category prepare interceptor defined in {@link de.hybris.platform.category.interceptors.CategoryPrepareInterceptor} is doing some
 * checks about the {@link CategoryModel#ALLOWEDPRINCIPALS} which slow down considerably the PIM import. The allowed principals are anyway
 * set during the PIM import which means that the default category interceptor is no longer needed. Therefore, we create this NOOP prepare
 * interceptor to replace the default one.
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.0
 */
public class DistNoOpCategoryPrepareInterceptor implements PrepareInterceptor<CategoryModel> {

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.interceptor.PrepareInterceptor#onPrepare(java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onPrepare(final CategoryModel model, final InterceptorContext paramInterceptorContext) throws InterceptorException {
        // NOP
    }
}
