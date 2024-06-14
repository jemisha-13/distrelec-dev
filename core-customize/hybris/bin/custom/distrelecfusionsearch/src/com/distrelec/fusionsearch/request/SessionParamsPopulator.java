package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.SESSION_PARAM;
import static com.namics.hybris.ffsearch.converter.search.SearchServiceConverter.FF_TRACKING_COOKIE;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * Reuses the same session ID as used for Fact-Finder. Check SearchServiceConverter to get more details.
 */
class SessionParamsPopulator implements Populator<SearchRequestTuple, MultiValuedMap<String, String>> {

    @Override
    public void populate(SearchRequestTuple searchRequestTuple, MultiValuedMap<String, String> params) throws ConversionException {
        ServletRequestAttributes requestAttrs = currentRequestAttributes();
        String sessionId = getJSessionId(requestAttrs);
        if (sessionId != null) {
            params.put(SESSION_PARAM, sessionId);
        }
    }

    private String getJSessionId(ServletRequestAttributes requestAttrs) {
        HttpServletRequest request = requestAttrs.getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName()
                          .equals(FF_TRACKING_COOKIE)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    ServletRequestAttributes currentRequestAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    }
}
