/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.util.Config;

/**
 * {@code CatplusAccessDeniedErrorPageController}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
@Controller("CatplusAccessDeniedErrorPageController")
@RequestMapping(value = "/catplusAccessDenied")
public class CatplusAccessDeniedErrorPageController extends AbstractPageController {
    public static final String CATPLUS_ACCESS_DENIED_CMS_PAGE = "catplusAccessDenied";
    public static final String SHOULD_DISPLAY_STACKTRACES_IN_FRONTEND = "namacceleratorpluscore.displaystacktracesinfrontend";

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String get(final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        // display the notFound page with error from controller
        storeCmsPageInModel(model, getContentPageForLabelOrId(CATPLUS_ACCESS_DENIED_CMS_PAGE));

        final String uuid = (String) request.getAttribute("uuid");
        final String message = getMessageSource().getMessage("system.error.page.catplus.accessDenied", null, getI18nService().getCurrentLocale());
        model.addAttribute("message", message);
        model.addAttribute("uuid", uuid);
        model.addAttribute("displayStacktrace", Boolean.valueOf(Config.getBoolean(SHOULD_DISPLAY_STACKTRACES_IN_FRONTEND, false)));
        model.addAttribute("exception", request.getAttribute("exception"));
        setWtErrors(Integer.toString(HttpServletResponse.SC_FORBIDDEN));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        return ControllerConstants.Views.Pages.Error.CatplusAccessDeniedErrorPage;
    }
}
