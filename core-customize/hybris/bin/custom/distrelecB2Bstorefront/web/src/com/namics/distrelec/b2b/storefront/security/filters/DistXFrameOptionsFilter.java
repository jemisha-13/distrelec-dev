package com.namics.distrelec.b2b.storefront.security.filters;

import com.namics.distrelec.b2b.core.eprocurement.service.DistEProcurementService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DistXFrameOptionsFilter extends GenericFilterBean {

    static final String X_FRAME_OPTIONS_HEADER = "X-Frame-Options";
    static final String SAMEORIGIN = "SAMEORIGIN";

    @Autowired
    CMSSiteService cmsSiteService;

    @Autowired
    DistEProcurementService distEProcurementService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (isXFrameOptionsProtectionEnabled() && !distEProcurementService.isEProcurementCustomer()) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader(X_FRAME_OPTIONS_HEADER, SAMEORIGIN);
        }

        filterChain.doFilter(request, response);
    }

    protected boolean isXFrameOptionsProtectionEnabled() {
        CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();
        return cmsSite.isXFrameOptionsProtected();
    }
}
