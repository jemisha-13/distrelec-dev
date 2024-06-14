/**
 * @author datneerajs, Elfa Distrelec AB
 * @since Namics Extensions 1.0
 */

package com.namics.distrelec.b2b.storefront.interceptors;

import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.web.util.CookieGenerator;
import org.springframework.web.util.UrlPathHelper;

import com.namics.distrelec.b2b.storefront.attributes.Attributes;
import com.namics.distrelec.b2b.storefront.security.GUIDCookieStrategy;
import com.namics.distrelec.b2b.storefront.security.impl.DefaultRememberMeService;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.session.SessionService;

public abstract class AbstractSeoInterceptor extends AbstractOncePerRequestPreHandleInterceptor {

    protected static final String ROOT_PATH = "/";
    private Set<String> excludeUrls;
    private String loginUrl;
    private RedirectStrategy redirectStrategy;
    private CookieGenerator cookieGenerator;
    private UrlPathHelper urlPathHelper;
    private DefaultRememberMeService defaultRememberMeService;
    private GUIDCookieStrategy guidCookieStrategy;
    private SessionService sessionService;

    private static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    protected static final String PRIVATE_MAX_AGE = "private, max-age=0";

    protected static final String PUBLIC_MAX_AGE = "public, max-age=0";

    protected static final String CONTENT_TYPE = "Content-Type";

    protected static final String CACHE_CONTROL = "Cache-Control";

    protected static final String NOT_SPECIAL_PRICE = "notspecialprice";

    protected static final String CHANNEL = "channel";

    protected static final String LANGUAGE = "language";

    protected final String AJAX_RESPONSE_CONTENT_TYPE[] = { "text/plain", "application/x-www-form-urlencoded" };

    protected boolean isPostRequest(final HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    /**
     *
     * @return true when logged in
     */
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

    protected void addAdditionalCookies(final HttpServletRequest request, final HttpServletResponse response) {

        final StringBuilder sb = new StringBuilder();
        sb.append("logged-in:").append(Boolean.toString(isLoggedIn()));

        final Object language = getSessionService().getAttribute("language");
        if (language instanceof LanguageModel) {
            final LanguageModel lm = (LanguageModel) language;
            sb.append("|language:").append(lm.getIsocode());
        }

        final Object channel = getSessionService().getAttribute("channel");
        if (channel != null) {
            sb.append("|channel:").append(channel.toString());
        }

        Attributes.SEO_FASTERIZE_VARIATION_KEY.setValue(request, response, sb.toString());
    }

    protected boolean isCookiesVariationAvailable(final HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (final Cookie cookie : request.getCookies()) {
                if (cookie.getName().equalsIgnoreCase(Attributes.SEO_FASTERIZE_VARIATION_KEY.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isAjaxRequest(final HttpServletRequest request) {
        final String header = request.getHeader(CONTENT_TYPE);
        if (header != null) {
            for (final String response : AJAX_RESPONSE_CONTENT_TYPE) {
                if (header.contains(response)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected Set<String> getExcludeUrls() {
        return excludeUrls;
    }

    protected boolean customerDoesntHaveSpecialPrice() {
        final Object specialprice = getSessionService().getAllAttributes().get(NOT_SPECIAL_PRICE);
        if (specialprice != null) {
            return true;
        }
        return false;
    }

    protected String getChannel() {
        final Object channel = getSessionService().getAllAttributes().get(CHANNEL);
        if (channel != null) {
            return channel.toString();
        }
        return "";
    }

    protected String getLanguage() {
        final Object language = getSessionService().getAllAttributes().get(LANGUAGE);
        if (language instanceof LanguageModel) {
            final LanguageModel lm = (LanguageModel) language;
            return lm.getIsocode();
        }
        return "";
    }

    @Required
    public void setExcludeUrls(final Set<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    protected String getLoginUrl() {
        return loginUrl;
    }

    @Required
    public void setLoginUrl(final String loginUrl) {
        this.loginUrl = loginUrl;
    }

    protected RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    @Required
    public void setRedirectStrategy(final RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

    protected CookieGenerator getCookieGenerator() {
        return cookieGenerator;
    }

    @Required
    public void setCookieGenerator(final CookieGenerator cookieGenerator) {
        this.cookieGenerator = cookieGenerator;
    }

    protected UrlPathHelper getUrlPathHelper() {
        return urlPathHelper;
    }

    @Required
    public void setUrlPathHelper(final UrlPathHelper urlPathHelper) {
        this.urlPathHelper = urlPathHelper;
    }

    public DefaultRememberMeService getDefaultRememberMeService() {
        return defaultRememberMeService;
    }

    public void setDefaultRememberMeService(final DefaultRememberMeService defaultRememberMeService) {
        this.defaultRememberMeService = defaultRememberMeService;
    }

    public GUIDCookieStrategy getGuidCookieStrategy() {
        return guidCookieStrategy;
    }

    @Required
    public void setGuidCookieStrategy(final GUIDCookieStrategy guidCookieStrategy) {
        this.guidCookieStrategy = guidCookieStrategy;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    @Required
    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }

}
