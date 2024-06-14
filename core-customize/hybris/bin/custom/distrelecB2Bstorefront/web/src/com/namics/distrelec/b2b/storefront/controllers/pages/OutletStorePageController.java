/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.facades.search.converter.FactFinderLazyFacetConverter;
import com.namics.hybris.ffsearch.data.search.DistSearchType;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;

/**
 * Controller for Outlet store.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.22
 */
@Controller
public class OutletStorePageController extends AbstractPromotionStorePageController {

    private static final String SEARCH_CMS_PAGE_ID = "clearance";

    @RequestMapping(value = "/**/clearance", method = RequestMethod.GET)
    public String outletStoreDetails(@RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false)
    final String searchQuery, @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "1")
    final int page, @RequestParam(value = "pageSize", defaultValue = "0")
    final int pageSize, @RequestParam(value = "show", defaultValue = "Page")
    final ShowMode showMode, @RequestParam(value = "sort", required = false)
    final String sortCode, @RequestParam(value = "requestType", required = false)
    final String requestType, @RequestParam(value = "useTechnicalView", required = false, defaultValue = "true")
    final Boolean useTechnicalView, final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        return getStoreDetails(searchQuery, page, pageSize, showMode, sortCode, requestType, useTechnicalView, model, request, response);
    }
    
    @RequestMapping(value = "/**/clearance/cnt", method = RequestMethod.GET ,produces=MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String outletStoreDetailscount(@RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false)
    final String searchQuery, @RequestParam(value = PAGE_PARAMETER_NAME, defaultValue = "1")
    final int page, @RequestParam(value = "pageSize", defaultValue = "0")
    final int pageSize, @RequestParam(value = "show", defaultValue = "Page")
    final ShowMode showMode, @RequestParam(value = "sort", required = false)
    final String sortCode, @RequestParam(value = "requestType", required = false)
    final String requestType, @RequestParam(value = "useTechnicalView", required = false, defaultValue = "true")
    final Boolean useTechnicalView, final Model model, final HttpServletRequest request, final HttpServletResponse response) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        model.addAttribute("metaRobots", "index, follow");
        return getStoreDetailsCount(searchQuery, page, pageSize, showMode, sortCode, requestType, useTechnicalView, model, request, response);
    }
    

    @RequestMapping(value = "/**/clearance/showmore", method = RequestMethod.GET)
    public String showMoreProducts(@RequestParam(value = SEARCH_QUERY_PARAMETER_NAME, required = false)
    final String searchQuery, @RequestParam(value = PAGE_PARAMETER_NAME)
    final int page, @RequestParam(value = "pageSize", defaultValue = "0")
    final int pageSize, @RequestParam(value = "show", defaultValue = "Page")
    final ShowMode showMode, @RequestParam(value = "sort", required = false)
    final String sortCode, final Model model, final HttpServletRequest request, final HttpServletResponse response) {

        model.addAttribute("metaRobots", "index, follow");
        return getShowMoreProducts(searchQuery, page, pageSize, showMode, sortCode, model, request, response);
    }

    @RequestMapping(value = "/**/clearance" + FactFinderLazyFacetConverter.ADDITIONAL_FACET_PATH, method = RequestMethod.GET, produces = "application/json")
    public final String loadAdditionalFacet(@RequestParam("q")
    final String searchQuery, @RequestParam(value = FactFinderLazyFacetConverter.ADDITIONAL_FACET_PARAM_NAME, required = true)
    final String additionalFacet, final HttpServletRequest request, final Model model) {
        model.addAttribute("metaRobots", "index, follow");
        return getAdditionalFacet(searchQuery, additionalFacet, request, model);
    }

    @Override
    protected String getStoreCmsPageName() {
        return SEARCH_CMS_PAGE_ID;
    }

    @Override
    protected DistSearchType getSearchType() {
        return DistSearchType.OUTLET;
    }

}
