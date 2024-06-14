/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.namics.distrelec.b2b.core.eprocurement.service.cxml.CxmlException;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

/**
 * Controller for e-procurement pages.
 */
@Controller
public class CxmlAuthenticationController extends AbstractPageController {

    @RequestMapping(value = "/cxmlEntry")
    public void cxmlEntry(final HttpServletRequest request) throws CxmlException {
        // do CXML login and set all necessary jaloSession attributes
        getDistCxmlService().doCxmlLogin(request);
    }

    @RequestMapping(value = "/cxmlSuccess", method = RequestMethod.GET)
    public String cxmlEntrySuccess() throws CMSItemNotFoundException, CxmlException {
        // redirect to dedicated page if additional function is called otherwise to home pag
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + getDistCxmlService().getCxmlRedirectUrl());
    }
}
