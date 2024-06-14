package com.namics.distrelec.b2b.storefront.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class GenericAttributeInterceptor extends HandlerInterceptorAdapter {

    private static final String MARKETING_PREFERENCE_POPUP = "marketingPopup";

    private static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) {

        if (modelAndView != null) {
            modelAndView.getModelMap().addAttribute(MARKETING_PREFERENCE_POPUP, Boolean.FALSE);
        }
    }

    protected boolean isLoggedIn() {
        if (SecurityContextHolder.getContext().getAuthentication() == null || !SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            return false;
        } else {
            for (final GrantedAuthority authority : SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
                if (ROLE_ANONYMOUS.equals(authority.getAuthority())) {
                    return false;
                }
            }
        }

        return true;
    }
}
