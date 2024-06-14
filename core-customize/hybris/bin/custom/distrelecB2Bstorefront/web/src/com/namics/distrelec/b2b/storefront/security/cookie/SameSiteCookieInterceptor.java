package com.namics.distrelec.b2b.storefront.security.cookie;

import com.namics.distrelec.b2b.core.security.SameSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SameSiteCookieInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private SameSiteService sameSiteService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (!response.isCommitted() && sameSiteService.is3rdPartyCookieAccessRequired()) {
            sameSiteService.allow3rdPartyCookieAccess(request, response);
        }
    }

}
