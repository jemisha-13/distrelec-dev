package com.namics.distrelec.b2b.storefront.controllers.pages;


import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.seo.DistLink;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the display for all exceptions not catched by other page controllers
 *
 * @author rhusi, Distrelec
 * @since Distrelec 2.0
 *
 */
@Controller
public class ErrorPageController extends AbstractPageController {

    private static final Logger LOG = LogManager.getLogger(ErrorPageController.class);

    private static final String BAD_REQUEST_CMS_PAGE = "badRequest";
    private static final String NOT_FOUND_CMS_PAGE = "notFound";
    private static final String UNKNOWN_ERROR_CMS_PAGE = "unknownError";
    private static final String FORBIDDEN_CMS_PAGE = "forbidden";

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @RequestMapping(value = "/badRequest", method = { RequestMethod.GET, RequestMethod.POST })
    public String badRequest(final Model model, final HttpServletRequest request, final HttpServletResponse response)
      throws CMSItemNotFoundException {
        // display the badRequest page with error from controller

        addGlobalModelAttributes(model, request);
        storeCmsPageInModel(model, getContentPageForLabelOrId(BAD_REQUEST_CMS_PAGE));
        populateModel(model, request, response, "system.error.page.bad.request", HttpServletResponse.SC_BAD_REQUEST);

        setupLinks(model, request);

        return ControllerConstants.Views.Pages.Error.ERROR_BAD_REQUEST_400;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @RequestMapping(value = "/notFound", method = { RequestMethod.GET, RequestMethod.POST })
    public String notFound(final Model model, final HttpServletRequest request, final HttpServletResponse response)
            throws CMSItemNotFoundException {
        // display the notFound page with error from controller

        addGlobalModelAttributes(model, request);
        storeCmsPageInModel(model, getContentPageForLabelOrId(NOT_FOUND_CMS_PAGE));
        populateModel(model, request, response, "system.error.page.not.found", HttpServletResponse.SC_NOT_FOUND);

        setupLinks(model, request);

        return ControllerConstants.Views.Pages.Error.ERROR_NOT_FOUND_404;
    }

    /**
     * This is only for a testing - to check what would happen if exception is thrown.
     */
    @RequestMapping(value = "/unknownError2", method = { RequestMethod.GET, RequestMethod.POST })
    public String unknownError2(final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException {
        if (!isProd()) {
            throw new NullPointerException();
        } else {
            return unknownError(model, request, response);
        }
    }

    @RequestMapping(value = "/unknownError", method = { RequestMethod.GET, RequestMethod.POST })
    public String unknownError(final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException {
        // display the unknownError page with error from controller
        try {
            addGlobalModelAttributes(model, request);
            storeCmsPageInModel(model, getContentPageForLabelOrId(UNKNOWN_ERROR_CMS_PAGE));
            populateModel(model, request, response, "system.error.unknown", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ControllerConstants.Views.Pages.Error.UnknownErrorPage;
        } catch (Exception e) {
            LOG.error("Unable to render unknown error page", e);
            // redirect to homepage
            return REDIRECT_PREFIX + "/";
        }
    }

    protected void populateModel(final Model model, final HttpServletRequest request, final HttpServletResponse response, final String messageKey,
            final int httpServletResponse) {
        model.addAttribute("message", getMessageSource().getMessage(messageKey, null, getI18nService().getCurrentLocale()));
        model.addAttribute("uuid", request.getAttribute("uuid"));
        model.addAttribute("displayStacktrace", getConfigurationService().getConfiguration().getBoolean(SHOULD_DISPLAY_STACKTRACES_IN_FRONTEND, Boolean.FALSE));
        model.addAttribute("exception", request.getAttribute("exception"));
        setWtErrors(Integer.toString(httpServletResponse));
        response.setStatus(httpServletResponse);
    }
    
    @RequestMapping(value = "/forbidden", method = { RequestMethod.GET, RequestMethod.POST })
    public String forbiddenErrpr(final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        storeCmsPageInModel(model, getContentPageForLabelOrId(FORBIDDEN_CMS_PAGE));
        populateModel(model, request, response, "system.error.page.myaccount.accessDenied", HttpServletResponse.SC_FORBIDDEN);

        setupLinks(model, request);

        return ControllerConstants.Views.Pages.Error.ERROR_NOT_FOUND_403;
    }

    /**
     * Setup the footer and and header links
     * 
     * @param model
     * @param request
     */
    private void setupLinks(final Model model, final HttpServletRequest request) {
        final List<DistLink> headerLinkLang = new ArrayList<DistLink>();
        setupAlternateHreflangLinks(request, headerLinkLang, null, null);

        // Remove the '/notFound' from the URL to redirect to the Home Page.
        headerLinkLang.stream().forEach(dLink -> {
            final String href = dLink.getHref();
            dLink.setHref(href.substring(0, href.lastIndexOf('/')));
        });

        model.addAttribute("headerLinksLangTags", headerLinkLang);
        model.addAttribute("footerLinksLangTags", headerLinkLang);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.storefront.controllers.pages.AbstractPageController#getURLRequest(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected URL getURLRequest(final HttpServletRequest request) {
        try {
            // We need to remove the '/notFound' and/or '/unknownError'
            final String requestURL = request.getRequestURL().toString();
            return new URL(requestURL.substring(0, requestURL.lastIndexOf('/')));
        } catch (final MalformedURLException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Problems while setting the <link> content. The url is not correct.", e);
            }
            return null;
        }
    }
}
