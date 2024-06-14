/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

/**
 * Interface for missing after save interceptor. It extends from ValidateInterceptor to reuse default interceptor registration.
 * 
 * @author pnueesch, Namics AG
 * @since Distrelec 1.0
 * 
 */
public interface AfterSaveInterceptor extends ValidateInterceptor {

    void afterSave(Object model) throws InterceptorException;

}
