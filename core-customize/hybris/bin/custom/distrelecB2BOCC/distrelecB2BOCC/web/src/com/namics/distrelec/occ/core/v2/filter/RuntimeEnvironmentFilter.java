package com.namics.distrelec.occ.core.v2.filter;

import com.namics.distrelec.b2b.core.service.environment.RuntimeEnvironmentService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RuntimeEnvironmentFilter extends AbstractUrlMatchingFilter {

    private final RuntimeEnvironmentService runtimeEnvironmentService;

    public RuntimeEnvironmentFilter(RuntimeEnvironmentService runtimeEnvironmentService) {
        this.runtimeEnvironmentService = runtimeEnvironmentService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        runtimeEnvironmentService.setHeadless(true);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
