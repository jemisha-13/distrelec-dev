package com.distrelec.webpimmedias.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.namics.distrelec.b2b.core.media.RedirectOnLocalStrategy;

public class ProdImageLocalDevEnvironmentFilter extends DelegatingFilterProxy {

    @Autowired
    private RedirectOnLocalStrategy redirectOnLocalStrategy;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        boolean isRedirected = redirectOnLocalStrategy.redirectIfLocal((HttpServletRequest) request, (HttpServletResponse) response);

        if (!isRedirected) {
            filterChain.doFilter(request, response);
        }
    }
}
