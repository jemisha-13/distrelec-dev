package com.namics.distrelec.b2b.core.media.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants.PropKey.Environment;
import com.namics.distrelec.b2b.core.media.RedirectOnLocalStrategy;

import de.hybris.platform.servicelayer.config.ConfigurationService;

class RedirectOnLocalToStageStrategy implements RedirectOnLocalStrategy {

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public boolean redirectIfLocal(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
        if (isCurrentLocalEnvironmentDev()) {
            StringBuilder redirectUrlBuilder = new StringBuilder();
            redirectUrlBuilder.append(Environment.TEST_MEDIA_DOMAIN_HTTPS);
            redirectUrlBuilder.append(httpRequest.getRequestURI());
            String queryString = httpRequest.getQueryString();
            if (queryString != null) {
                redirectUrlBuilder.append("?").append(queryString);
            }

            httpResponse.sendRedirect(redirectUrlBuilder.toString());
            return true;
        } else {
            return false;
        }
    }

    private boolean isCurrentLocalEnvironmentDev() {
        Configuration config = configurationService.getConfiguration();
        String currentEnv = config.getString(Environment.ENVIRONMENT_KEY);
        return Environment.LOCAL_ENV_DEVELOPMENT.equals(currentEnv);
    }
}
