package com.namics.distrelec.occ.core.interceptors;

import de.hybris.platform.webservicescommons.cache.CacheControl;
import de.hybris.platform.webservicescommons.cache.CacheControlDirective;
import de.hybris.platform.webservicescommons.interceptors.CacheControlHandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class DistCacheControlHandlerInterceptor extends CacheControlHandlerInterceptor {

    private static final String CMS_TICKET_ID = "cmsTicketId";

    static int STALE_WHILE_REVALIDATE_TTL = 600;

    @Override
    protected String createCacheControlHeaderField(CacheControl cacheAnnotation) {
        String baseCacheHeader = super.createCacheControlHeaderField(cacheAnnotation);

        StringBuilder cacheHeader = new StringBuilder(baseCacheHeader);
        if (!baseCacheHeader.isEmpty()) {
            cacheHeader.append(", ");
        }

        if (Arrays.asList(cacheAnnotation.directive()).contains(CacheControlDirective.PUBLIC)) { // append stale-while-revalidate just for public
            cacheHeader.append("stale-while-revalidate=").append(STALE_WHILE_REVALIDATE_TTL).append(", ");
            cacheHeader.append("immutable, ");
        }
        cacheHeader.append("stale-if-error=0");

        return cacheHeader.toString();
    }

    @Override
    protected boolean isEligibleForCacheControl(HttpServletRequest request, HttpServletResponse response, Object handler) {
        return super.isEligibleForCacheControl(request, response, handler) && !requestContainsCmsPreviewTicketId(request);
    }

    private boolean requestContainsCmsPreviewTicketId(HttpServletRequest request) {
        return request.getParameterMap().containsKey(CMS_TICKET_ID);
    }
}
