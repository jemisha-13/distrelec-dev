/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.interceptor;

import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.impex.impl.ImpExMediaInitDefaultsInterceptor;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;

/**
 * {@code DistImpExMediaInitDefaultsInterceptor}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.9
 */
public class DistImpExMediaInitDefaultsInterceptor extends ImpExMediaInitDefaultsInterceptor {

    private ConfigurationService configurationService;

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.impex.impl.ImpExMediaInitDefaultsInterceptor#onInitDefaults(java.lang.Object,
     * de.hybris.platform.servicelayer.interceptor.InterceptorContext)
     */
    @Override
    public void onInitDefaults(final Object model, final InterceptorContext ctx) throws InterceptorException {
        super.onInitDefaults(model, ctx);
        if ((model instanceof ImpExMediaModel)) {
            ImpExMediaModel mediaModel = (ImpExMediaModel) model;
            mediaModel.setRemoveOnSuccess(getConfigurationService().getConfiguration().getBoolean("erp.data.import.impexmedia.removeonsuccess", true));
        }
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
