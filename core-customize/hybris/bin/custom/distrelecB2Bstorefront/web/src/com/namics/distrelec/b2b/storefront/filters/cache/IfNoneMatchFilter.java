package com.namics.distrelec.b2b.storefront.filters.cache;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Removes gzip suffix added by apache deflate, to be able to match etags.
 */
public class IfNoneMatchFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            IfNoneMatchRequestWrapper wrapper = new IfNoneMatchRequestWrapper((HttpServletRequest) servletRequest);
            filterChain.doFilter(wrapper, servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
        // do nothing
    }
}
