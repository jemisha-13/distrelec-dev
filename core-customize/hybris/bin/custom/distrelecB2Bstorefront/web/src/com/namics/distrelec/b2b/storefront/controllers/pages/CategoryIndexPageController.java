/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.category.DistCategoryIndexFacade;
import com.namics.distrelec.b2b.facades.product.data.DistCategoryIndexData;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.navigation.CMSNavigationNodeModel;
import de.hybris.platform.cms2.servicelayer.services.CMSNavigationService;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.servicelayer.session.SessionService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * {@code CategoryIndexPageController}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.6
 */
@Controller
@RequestMapping(value = "/**/categories")
public class CategoryIndexPageController extends AbstractPageController {

    private static final String CATEGORY_INDEX_PAGE = "category/categoryIndexPage";
    private static final String CATEGORY_INDEX_PAGE_LABEL = "categoryIndexPage";
    private static final String FORWARD_SLASH = "/";
    private static final String CATEGORIES = "categories";


    @Autowired
    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    @Autowired
    private CMSNavigationService cmsNavigationService;
    
    @Autowired
    private DistCategoryIndexFacade distCategoryIndexFacade;

    @RequestMapping(method = RequestMethod.GET)
    public String get(final Model model, final HttpServletRequest request, final HttpServletResponse response)
            throws CMSItemNotFoundException, UnsupportedEncodingException {

        final String resolvedUrl = new StringBuilder(FORWARD_SLASH).append(getCurrentLanguage().getIsocode()).append(FORWARD_SLASH + CATEGORIES).toString();
        final String redirection = checkRequestUrl(request, response, resolvedUrl);
        if (StringUtils.isNotEmpty(redirection)) {
            return redirection;
        }

        addGlobalModelAttributes(model, request);
        storeCmsPageInModel(model, getContentPageForLabelOrId(CATEGORY_INDEX_PAGE_LABEL));
        model.addAttribute(CATEGORIES, distCategoryIndexFacade.getCategoryIndexData());

        return PAGE_ROOT + CATEGORY_INDEX_PAGE;
    }

   
    public DistUrlResolver<CategoryModel> getCategoryModelUrlResolver() {
        return categoryModelUrlResolver;
    }

    public void setCategoryModelUrlResolver(final DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    public CMSNavigationService getCmsNavigationService() {
        return cmsNavigationService;
    }

    public void setCmsNavigationService(final CMSNavigationService cmsNavigationService) {
        this.cmsNavigationService = cmsNavigationService;
    }
}
