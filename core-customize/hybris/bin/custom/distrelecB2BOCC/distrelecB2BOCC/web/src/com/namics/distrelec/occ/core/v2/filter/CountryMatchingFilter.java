package com.namics.distrelec.occ.core.v2.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.namics.distrelec.b2b.facades.storesession.NamicsStoreSessionFacade;

public class CountryMatchingFilter extends AbstractUrlMatchingFilter {

    private static final Logger LOG = LoggerFactory.getLogger(CountryMatchingFilter.class);

    public static final String COUNTRY_PARAMETER = "country";

    private String regexp;

    private NamicsStoreSessionFacade storeSessionFacade;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (matchesUrl(request, regexp)) {
            String country = request.getParameter(COUNTRY_PARAMETER);
            if (StringUtils.isNotEmpty(country)) {
                storeSessionFacade.setCurrentCountry(country.toUpperCase());
            } else {
                LOG.debug("Country parameter is missing, default country will be used");
            }
        }

        filterChain.doFilter(request, response);
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public NamicsStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(NamicsStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

}
