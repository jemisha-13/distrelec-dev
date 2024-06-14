/*
 * Copyright 2000-2017 Elfa Distrelec. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.security;

import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.util.UrlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author nshandilya, Elfa Distrelec
 * @since Distrelec v4.14.1
 */
public class DistRedirectStrategy extends DefaultRedirectStrategy {

    private boolean contextRelative;
    private String CUSTOM_REDIRECT_ATTRIBUTE="customredirect";
    private static final Logger logger = Logger.getLogger(DistRedirectStrategy.class);

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        String redirectUrl = calculateRedirectUrl(request.getContextPath(), url);
        redirectUrl = response.encodeRedirectURL(redirectUrl);
        redirectUrl = AbstractController.addFasterizeCacheControlParameter(redirectUrl);
        if (logger.isDebugEnabled()) {
            logger.debug("Redirecting to '" + redirectUrl + "'");
        }
        if(null!=request.getParameter(CUSTOM_REDIRECT_ATTRIBUTE) && !StringUtils.isBlank(request.getParameter(CUSTOM_REDIRECT_ATTRIBUTE).toString())){
        	response.sendRedirect(request.getParameter(CUSTOM_REDIRECT_ATTRIBUTE).toString());
        }else {
        	response.sendRedirect(redirectUrl);
        }
    }

    @Override
    protected String calculateRedirectUrl(String contextPath, String url) {
        if (!(UrlUtils.isAbsoluteUrl(url))) {
            if (this.contextRelative) {
                return url;
            }
            return contextPath + url;
        }

        if (!(this.contextRelative)) {
            return url;
        }

        url = url.substring(url.indexOf("://") + 3);
        url = url.substring(url.indexOf(contextPath) + contextPath.length());

        if ((url.length() > 1) && (url.charAt(0) == '/')) {
            url = url.substring(1);
        }

        return url;
    }
}
