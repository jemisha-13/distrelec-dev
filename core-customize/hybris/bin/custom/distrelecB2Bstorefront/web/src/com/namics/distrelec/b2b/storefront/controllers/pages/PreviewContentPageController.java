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

import javax.servlet.http.HttpServletRequest;

import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;

/**
 * Simple CMS Content Page controller. Used only to preview CMS Pages. The DefaultPageController is used to serve generic content pages.
 */
@Controller
@RequestMapping(value = "/preview-content")
public class PreviewContentPageController extends AbstractPageController {

    @RequestMapping(method = RequestMethod.GET, params = { "uid" })
    public String get(@RequestParam(value = "uid")
    final String cmsPageUid, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);

        final PagePreviewCriteriaData pagePreviewCriteriaData = cmsPreviewService.getPagePreviewCriteria();

        final AbstractPageModel pageForRequest = getCmsPageService().getPageForId(cmsPageUid, pagePreviewCriteriaData);
        storeCmsPageInModel(model, pageForRequest);
        setUpMetaDataForContentPage(model, getContentPageForLabelOrId(cmsPageUid));
        return getViewForPage(pageForRequest);
    }

}
