/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Login Controller. Handles login and register for the account flow.
 */
@Controller
@RequestMapping(value = "/login")
public class LoginPageController extends AbstractLoginPageController {

    @Autowired
    private HttpSessionRequestCache httpSessionRequestCache;

    @Autowired
    private CacheManager manufacturerMenuCacheManager;

    @RequestMapping(value = "/unapproved", method = RequestMethod.GET)
    public String processUnapprovedUserRequest(final Model model, final HttpServletRequest request, final HttpServletResponse response,
                                               final HttpSession session, RedirectAttributes redirectAttributes) throws CMSItemNotFoundException {

        final String error = (String) request.getSession().getAttribute("error");
        final String qd =  String.valueOf(request.getSession().getAttribute("qd"));

        redirectAttributes.addFlashAttribute("notYetApprovedLoginError", error);
        redirectAttributes.addFlashAttribute("notYetApprovedEmail", qd);
        return "redirect:/login";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String doLogin(@RequestHeader(value = "referer", required = false) final String referer,
                          @RequestParam(value = "error", defaultValue = "false") final boolean loginError,
                          @RequestParam(value = "qd", required = false, defaultValue = "") final String email,
                          final Model model, final HttpServletRequest request, final HttpServletResponse response,
                          final HttpSession session)  throws CMSItemNotFoundException {

        boolean error = loginError;
        if(model.containsAttribute("notYetApprovedLoginError")){
            final Map<String, Object> modelMap =  model.asMap();
            final String errorAsString = (String) modelMap.get("notYetApprovedLoginError");
            error = BooleanUtils.toBoolean(errorAsString);
        }

        addGlobalModelAttributes(model, request);
        if (!error) {
            storeReferer(referer, request, response);
        }

        /*
         * String uname = (null != qd && !qd.isEmpty()) ? DistCryptography.decryptString(decodeToUTF8(qd.trim()),
         * DistCryptography.WEBTREKK_KEY, String.valueOf(decodeToUTF8(qd.trim()).length())) : "";
         */
        return getLogin(error, session, model, decodeBase64(email));
    }

    @Override
    protected String getLoginView() {
        return ControllerConstants.Views.Pages.Account.AccountLoginPage;
    }

    @Override
    protected String getSuccessRedirect() {
        if (getUserService().isMemberOfGroup(getUserService().getCurrentUser(), getUserService().getUserGroupForUID(DistConstants.User.B2BEESHOPGROUP_UID))) {
            return addFasterizeCacheControlParameter("/");
        }
        getSessionService().setAttribute(WebConstants.LOGIN_SUCCESS, Boolean.TRUE);
        return addFasterizeCacheControlParameter("/welcome");
    }

    @Override
    protected AbstractPageModel getLoginCmsPage() throws CMSItemNotFoundException {
        return getContentPageForLabelOrId("login");
    }

    protected void storeReferer(final String referer, final HttpServletRequest request, final HttpServletResponse response) {
        // Use getSessionService().setAttribute(WebConstants.DO_NOT_STORE_REFERRER, Boolean.TRUE) to avoid saving the referrer and using the
        // default redirect behavior
        if (StringUtils.isNotBlank(referer) && !BooleanUtils.toBoolean((Boolean) getSessionService().getAttribute(WebConstants.DO_NOT_STORE_REFERRER))) {
            getHttpSessionRequestCache().saveRequest(request, response);
        }

        // Remove the flag to use the "normal" referrer behavior
        getSessionService().removeAttribute(WebConstants.DO_NOT_STORE_REFERRER);
    }

    public HttpSessionRequestCache getHttpSessionRequestCache() {
        return httpSessionRequestCache;
    }

    public void setHttpSessionRequestCache(final HttpSessionRequestCache httpSessionRequestCache) {
        this.httpSessionRequestCache = httpSessionRequestCache;
    }
}
