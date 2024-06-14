/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.eprocurement.ariba.controllers;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.url.DistSiteBaseUrlResolutionService;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.eprocurement.DistAribaFacade;
import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

@Controller
@RequestMapping("/ariba/login")
public class AribaAuthenticationController extends AbstractController {

    private static final Logger LOG = LoggerFactory.getLogger(AribaAuthenticationController.class);

    @Autowired
    private DistAribaFacade distAribaFacade;

    @Autowired
    private AuthenticationProvider aribaAuthenticationProvider;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonI18NService commonI18NService;

    @Autowired
    private DistSiteBaseUrlResolutionService distSiteBaseUrlResolutionService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private ConfigurationService configurationService;

    @ResponseBody
    @RequestMapping
    public void login(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String setupRequest = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        LOG.info("Punchout Setup Request: {}", setupRequest);
        final Map<String, String> credentials = distAribaFacade.getCredentialsFromDistAribaOutSetupRequest(setupRequest);

        final String payloadId = credentials.get(DistConstants.Ariba.URL_PARAM_KEY_PAYLOADID);
        sessionService.setAttribute(DistConstants.Ariba.URL_PARAM_KEY_PAYLOADID, payloadId);

        final String username = authenticateUser(credentials);
        final String token = distAribaFacade.getAribaToken(credentials.get(DistConstants.Ariba.URL_PARAM_KEY_USERNAME),
                credentials.get(DistConstants.Ariba.URL_PARAM_KEY_PASSWORD), setupRequest);

        response.setContentType("application/xml; charset=UTF-8");
        final Writer writer = response.getWriter();

        final B2BCustomerModel customer = userService.getUserForUID(username, B2BCustomerModel.class);

        BaseSiteModel baseSite = getBaseSiteForCustomer(customer.getDefaultB2BUnit(),
                (CMSSiteModel) customer.getCustomersBaseSite(),
                credentials.get(DistConstants.Ariba.SetupRequestParams.PRODUCT_CODE));

        distAribaFacade.parseAribaSetupResponse(HttpStatus.OK, getUrl(baseSite, getPath(token)), payloadId, writer);
        writer.flush();
        writer.close();
    }

    private String authenticateUser(Map<String, String> credentials) {
        Authentication authenticate;
        try {
            authenticate = aribaAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(
                    credentials.get(WebConstants.URL_PARAM_KEY_USERNAME), credentials.get(WebConstants.URL_PARAM_KEY_PASSWORD)));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (final AuthenticationException e) {
            LOG.debug("Error authenticating Ariba customer [{}] [{}] ", credentials.get(WebConstants.URL_PARAM_KEY_USERNAME), credentials.get(WebConstants.URL_PARAM_KEY_PASSWORD));
            throw e;
        }
        return (String) authenticate.getPrincipal();
    }

    private BaseSiteModel getBaseSiteForCustomer(B2BUnitModel unit, CMSSiteModel cmsSiteModel, String countryIsoCode) {
        BaseSiteModel baseSite = getBaseSiteForMultiCountryCustomer(countryIsoCode);
        if (baseSite != null) {
            return baseSite;
        }
        return getBaseSiteForCountry(getDefaultUserCountry(unit, cmsSiteModel));
    }

    private BaseSiteModel getBaseSiteForMultiCountryCustomer(String countryIsoCode) {
        if (StringUtils.isNotEmpty(countryIsoCode)) {
            try {
                CountryModel country = getCountry(countryIsoCode);
                if (null != country) {
                    sessionService.setAttribute(DistConstants.Ariba.Session.ARIBA_MULTI_COUNTRY_CUSTOMER, Boolean.TRUE);
                    return getBaseSiteForCountry(country);
                }
            } catch (Exception ex) {
                LOG.error("Can not find country for given IsoCode: {}", countryIsoCode, ex);
            }
        }
        return null;
    }

    private CountryModel getCountry(String countryIsoCode) {
        CountryModel country = commonI18NService.getCountry(countryIsoCode.toUpperCase());
        if (country != null) {
            return country;
        }
        return commonI18NService.getCountry(countryIsoCode.toLowerCase());
    }

    private BaseSiteModel getBaseSiteForCountry(CountryModel country) {
        return baseSiteService.getBaseSiteForUID("distrelec_" + country.getIsocode());
    }

    private CountryModel getDefaultUserCountry(B2BUnitModel unit, CMSSiteModel cmsSiteModel) {
        if (cmsSiteModel != null && cmsSiteModel.getCountry() != null) {
            return cmsSiteModel.getCountry();
        }
        return unit.getSalesOrg().getCountry();
    }

    private String getUrl(final BaseSiteModel baseSite, final String path) {
        if (isCurrentEnvironmentProd()) {
            return distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(baseSite, true, path);
        }
        return distSiteBaseUrlResolutionService.getStorefrontWebsiteUrlForSite(baseSite, true, path);
    }

    private String getPath(String token) {
        return ControllerConstants.Views.Pages.EProcurement.Ariba.AribaLoginPath + DistConstants.Punctuation.QUESTION_MARK
                + URLEncodedUtils.format(Collections.singletonList(new BasicNameValuePair(WebConstants.URL_PARAM_KEY_TOKEN, token)), StandardCharsets.UTF_8);
    }

    private boolean isCurrentEnvironmentProd() {
        final String env = getCurrentEnvironment();
        return env.startsWith(DistConstants.PropKey.Environment.LIVE_ENV_PREFIX);
    }

    private String getCurrentEnvironment() {
        return configurationService.getConfiguration().getString(DistConstants.PropKey.Environment.ENVIRONMENT_KEY);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public void handleAribaAuthenticationException(final HttpServletResponse response, final Exception exception) throws IOException {
        LOG.error("Error occured during Ariba authentication.", exception);
        if (exception instanceof IOException) {
            response.sendRedirect(ROOT);
        }

        final Writer writer = response.getWriter();
        final String payloadId = sessionService.getAttribute(DistConstants.Ariba.URL_PARAM_KEY_PAYLOADID);
        sessionService.removeAttribute(DistConstants.Ariba.URL_PARAM_KEY_PAYLOADID);

        if (exception instanceof AuthenticationException) {
            distAribaFacade.parseAribaSetupResponse(HttpStatus.UNAUTHORIZED, null, payloadId, writer);
        } else {
            distAribaFacade.parseAribaSetupResponse(HttpStatus.BAD_REQUEST, null, payloadId, writer);
        }

        writer.flush();
        writer.close();
    }
}
