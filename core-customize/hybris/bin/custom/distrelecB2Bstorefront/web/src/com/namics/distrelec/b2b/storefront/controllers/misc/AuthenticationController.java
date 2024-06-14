package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.security.CaptchaAuthenticationFailureHandler;
import com.namics.distrelec.b2b.storefront.security.GUIDCookieStrategy;
import com.namics.distrelec.b2b.storefront.security.exceptions.AuthenticationCaptchaException;
import com.namics.distrelec.b2b.storefront.util.CaptchaUtil;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private static final Logger log = LogManager.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private GUIDCookieStrategy guidCookieStrategy;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private CaptchaAuthenticationFailureHandler captchaAuthenticationFailureHandler;

    @Autowired
    private DistrelecCMSSiteService cmsSiteService;

    @Autowired
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Autowired
    private CaptchaUtil captchaUtil;

    public static class AuthenticationRequestData {
        private String username;
        private String password;
        private String captchaResponse;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCaptchaResponse() {
            return captchaResponse;
        }

        public void setCaptchaResponse(String captchaResponse) {
            this.captchaResponse = captchaResponse;
        }
    }

    public static class AuthenticationResponseData {
        private final boolean success;
        private final boolean captchaRequired;
        private final String errorMsg;

        public AuthenticationResponseData(boolean success) {
            this(success, false, null);
        }

        public AuthenticationResponseData(boolean success, boolean captchaRequired, String errorMsg) {
            this.success = success;
            this.captchaRequired = captchaRequired;
            this.errorMsg = errorMsg;
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean isCaptchaRequired() {
            return captchaRequired;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public AuthenticationResponseData authenticate(@RequestBody final AuthenticationRequestData authenticationData, final HttpServletRequest request, final HttpServletResponse response) {
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(authenticationData.getUsername(), authenticationData.getPassword());

        boolean loginSuccess = false;
        try {
            validateCaptcha(request, authenticationData.captchaResponse);
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            guidCookieStrategy.setCookie(request, response);
            loginSuccess = true;
        } catch (AuthenticationException e) {
            captchaAuthenticationFailureHandler.onFailedLogin(request);
            log.info("Authentication failed for user {}: {}", authenticationData.getUsername(), e.getMessage());
        }

        if (loginSuccess) {
            return new AuthenticationResponseData(true);
        }

        String errorMsg = getErrorMessage();
        return new AuthenticationResponseData(false, isCaptchaRequired(request), errorMsg);
    }

    private String getErrorMessage() {
        ApplicationContext context = Registry.getApplicationContext();
        Locale currentLocale = i18NService.getCurrentLocale();

        if (Boolean.TRUE.equals(sessionService.getAttribute(WebConstants.LOGIN_WRONG_COUNTRY))) {
            final CountryModel redirectCountry = sessionService.getAttribute(WebConstants.REDIRECT_COUNTRY);
            final CMSSiteModel redirectSite = cmsSiteService.getSiteForCountry(redirectCountry);
            final String correctUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(redirectSite, false, null);
            final String countryName = redirectCountry.getName();
            return context.getMessage("login.error.account.wrong.country", new Object[]{countryName, correctUrl}, currentLocale);
        }

        if (Boolean.TRUE.equals(sessionService.getAttribute(WebConstants.WRONG_CAPTCHA))) {
            return context.getMessage("login.error.account.not.found.title", null, currentLocale);
        }

        if (Boolean.TRUE.equals(sessionService.getAttribute(WebConstants.DUPLICATE_EMAIL))) {
            return context.getMessage("login.error.duplicate.email.title", null, currentLocale);
        }

        return context.getMessage("login.error.account.not.found.title", null, currentLocale);
    }

    private void validateCaptcha(final HttpServletRequest request, final String captchaResponse){
        final Boolean showCaptcha = (Boolean) request.getSession().getAttribute(WebConstants.SHOW_CAPTCHA);
        if (Boolean.TRUE.equals(showCaptcha) && !captchaUtil.validateReCaptcha(request, captchaResponse)) {
            sessionService.setAttribute(WebConstants.WRONG_CAPTCHA, true);
            throw new AuthenticationCaptchaException("Invalid Captcha");
        }
    }

    private boolean isCaptchaRequired(final HttpServletRequest request){
        final Boolean captchaRequired = (Boolean) request.getSession().getAttribute(WebConstants.SHOW_CAPTCHA);
        return captchaRequired != null ? captchaRequired : false;
    }
}
