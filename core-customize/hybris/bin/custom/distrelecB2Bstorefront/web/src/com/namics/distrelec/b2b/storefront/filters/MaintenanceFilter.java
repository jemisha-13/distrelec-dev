package com.namics.distrelec.b2b.storefront.filters;

import com.namics.distrelec.b2b.core.service.user.DistUserService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CountryModel;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class MaintenanceFilter extends GenericFilterBean {

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private DistUserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (returnServiceUnavailable(httpRequest)) {
            httpResponse.setStatus(503);
            httpResponse.setContentType("text/html");

            try (InputStream inputStream = getMaintenanceInputStream(httpRequest)) {
                String text = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .map(line -> line.replace("<base href=\"error/\"", "<base href=\"/error/\""))
                        .collect(Collectors.joining("\n"));
                httpResponse.getOutputStream().write(text.getBytes(StandardCharsets.UTF_8));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    protected boolean returnServiceUnavailable(HttpServletRequest httpRequest) {
        return !httpRequest.getRequestURI().startsWith("/error/")
                && getCmsSiteService().getCurrentSite().isMaintenanceActive()
                && !getUserService().accessFromInternalIp(httpRequest);
    }

    protected InputStream getMaintenanceInputStream(HttpServletRequest httpRequest) {
        CMSSiteModel cmsSiteModel = getCmsSiteService().getCurrentSite();
        CountryModel countryModel = cmsSiteModel.getCountry();
        String isoCode = countryModel.getIsocode();
        String suffix = "EX".equalsIgnoreCase(isoCode) ? "biz" : isoCode.toLowerCase();
        String htmlPath = "/maintenance/maintenance_" + suffix + ".html";

        return httpRequest.getSession().getServletContext().getResourceAsStream(htmlPath);
    }

    protected CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    protected DistUserService getUserService() {
        return userService;
    }

    protected void setUserService(DistUserService userService) {
        this.userService = userService;
    }
}
