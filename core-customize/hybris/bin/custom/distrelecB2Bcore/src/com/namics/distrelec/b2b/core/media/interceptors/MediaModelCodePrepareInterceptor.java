package com.namics.distrelec.b2b.core.media.interceptors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.PrepareInterceptor;

public class MediaModelCodePrepareInterceptor implements PrepareInterceptor<MediaModel> {

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void onPrepare(MediaModel mediaModel, InterceptorContext interceptorContext) throws InterceptorException {
        String code = mediaModel.getCode();
        mediaModel.setCode(StringUtils.trim(code));
    
    }

    /**
     * @return the configurationService
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
