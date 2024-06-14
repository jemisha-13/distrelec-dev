package com.namics.distrelec.b2b.storefront.util;

import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.session.SessionService;
import nl.captcha.Captcha;
import org.apache.commons.lang.StringUtils;
import com.namics.hybris.toolbox.spring.SpringUtil;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility class with CAPTCHA and invisible ReCAPTCHA utility methods.
 *
 * @author Aditya Bhavaraju for Datwyler Holding AG
 */
@Component
public class CaptchaUtilImpl implements CaptchaUtil {

    private static final Logger LOG = LogManager.getLogger(CaptchaUtilImpl.class);
    public static final String SKIP_CAPTCHA_VALIDATION = "skip.captcha.validation";
    public static final String[] HEADERS_TO_TRY = { "X-Forwarded-For" };

    private static final String WHITE_LISTED_IPS_KEY = "recaptcha.whitelisted.ips";

    private static final String CAPTCHA_ANSWER_PARAM_KEY = "captchaAnswer";


    private static final String CFG_PRIVATE_KEY = "captcha.private.key";
    private static final String CFG_PUBLIC_KEY = "captcha.public.key";
    private static final String CFG_PRIVATE_KEY_TEST = "captcha.private.key.test";
    private static final String CFG_PUBLIC_KEY_TEST = "captcha.public.key.test";


    private static final String G_CAPTCHA_RESPONSE = "g-recaptcha-response";
    private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=";
    private static final String RESPONSE = "&response=";
    private static final String REMOTEIP = "&remoteip=";
    private static final String PARAM_SKIP_CAPTCHA = "skipCaptcha";

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public boolean validateCaptcha(final HttpServletRequest request) {
        final Captcha captcha = getCaptchaFromRequest(true);
        final String captchaAnswer = request.getParameter(CAPTCHA_ANSWER_PARAM_KEY);
        return captcha != null && StringUtils.isNotBlank(captchaAnswer) && captcha.isCorrect(captchaAnswer);
    }

    /**
     * Validate the ReCaptcha from the request.
     *
     * @param request
     *            the HTTP request
     * @return {@literal true} if the ReCaptcha in the request is valid, {@literal false} otherwise.
     * @see #validateReCaptcha(HttpServletRequest, String)
     */
    @Override
    public boolean validateReCaptcha(final HttpServletRequest request) {
        final String gRecaptchaResponse = request.getParameter(G_CAPTCHA_RESPONSE);
        return validateReCaptcha(request, gRecaptchaResponse);
    }

    /**
     * Implementation of the ReCaptcha validation.
     *
     * @param gCaptchaResponse  Captcha response
     * @return {@code true} if the validation was successful, {@code false} otherwise.
     */
    @Override
    public boolean validateReCaptcha(final HttpServletRequest request, final String gCaptchaResponse) {
        try {
            final URI httpConnection = URI.create(buildReCaptchaURL(request, gCaptchaResponse));
            final CaptchaResponse captchaResponse = restTemplate.getForObject(httpConnection, CaptchaResponse.class);
            return captchaResponse != null && captchaResponse.isSuccess();
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return false;
    }

    private Captcha getCaptchaFromRequest(final boolean remove) {
        final SessionService sessionService = Registry.getApplicationContext().getBean("sessionService", SessionService.class);
        try {
            return (Captcha) sessionService.getAttribute(Captcha.NAME);
        } finally {
            if (remove) {
                sessionService.removeAttribute(Captcha.NAME);
            }
        }
    }

    /**
     * Check whether the IP address and the current environment are white listed.
     *
     * @param request
     *            the source request
     * @param configurationService
     *            the configuration service. If {@literal null}, then
     *            {@code SpringUtil.getBean("configurationService", ConfigurationService.class)} is called.
     * @return {@literal true} if both the source IP address and the current environment are white-listed, {@literal false} otherwise.
     */
    protected boolean isWhitelisted(final HttpServletRequest request) {
        final ConfigurationService configService = Optional.ofNullable(configurationService)
                .orElseGet(() -> SpringUtil.getBean("configurationService", ConfigurationService.class));

        // startsWith return true for empty strings so extra check

        final String whiteListedIPs = configService.getConfiguration().getString(WHITE_LISTED_IPS_KEY, StringUtils.EMPTY);

        return StringUtils.contains(whiteListedIPs, request.getRemoteAddr())
                || getHeaderIpStream(request)
                .anyMatch(whiteListedIPs::contains);
    }


    /**
     * Build the ReCaptcha target URL bases on the ReCaptcha response submitted with the HTTP request.
     *
     * @param request
     *            the origin HTTP request
     * @return the ReCaptcha URL bases on the ReCaptcha response submitted with the HTTP request.
     */
    private String buildReCaptchaURL(final HttpServletRequest request, final String gCaptchaResponse) {
        return new StringBuilder(RECAPTCHA_URL)
                .append(getPrivateKey(request))
                .append(RESPONSE)
                .append(gCaptchaResponse)
                .append(REMOTEIP)
                .append(request.getRemoteAddr())
                .toString();
    }

    @Override
    public String getPublicKey(final HttpServletRequest request) {
        // if captcha is disabled return test key
        if(isSkipCaptcha(request))
        {
            return configurationService.getConfiguration().getString(CFG_PUBLIC_KEY_TEST);
        }

        return configurationService.getConfiguration().getString(CFG_PUBLIC_KEY);

    }

    @Override
    public String getPrivateKey(final HttpServletRequest request) {
        // if captcha is disabled return test key
        if(isSkipCaptcha(request))
        {
            return configurationService.getConfiguration().getString(CFG_PRIVATE_KEY_TEST);
        }

        return configurationService.getConfiguration().getString(CFG_PRIVATE_KEY);
    }


    public void setSkipCaptcha(HttpServletRequest request) {
        HttpSession session = request.getSession();

        // only whitelisted IP's are allowed to set skipCaptcha
        if(isWhitelisted(request)){
            session.setAttribute(PARAM_SKIP_CAPTCHA,Boolean.TRUE);
        } else {
            List<String> ips = new ArrayList<>();
            ips.add(request.getRemoteAddr());
            List<String> headerIpsList = getHeaderIpStream(request)
                    .collect(Collectors.toList());
            ips.addAll(headerIpsList);
            String errMsg = "ip not whitelisted to set skipCaptcha: " + String.join(", ", ips);
            LOG.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
    }

    public boolean isSkipCaptcha(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return Boolean.TRUE.equals(session.getAttribute(PARAM_SKIP_CAPTCHA));
    }

    public RestTemplate getRestTemplate() {
        return restTemplate;
    }

    protected static Stream<String> getHeaderIpStream(HttpServletRequest request) {
        return Stream.of(HEADERS_TO_TRY)
                .map(request::getHeader)
                .filter(Objects::nonNull)
                .map(headerIps -> headerIps.split(", *"))
                .flatMap(Stream::of);
    }

    protected void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
