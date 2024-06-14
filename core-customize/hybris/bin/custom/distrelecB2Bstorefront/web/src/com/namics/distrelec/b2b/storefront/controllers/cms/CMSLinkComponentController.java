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
package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.service.url.ContentPageData;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.tags.Functions;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.contents.components.CMSLinkComponentModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller fo cms link component.
 */
@Controller("CMSLinkComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.CMSLinkComponent)
public class CMSLinkComponentController extends AbstractDistCMSComponentController<CMSLinkComponentModel> {
    @Autowired
    @Qualifier("productUrlConverter")
    private Converter<ProductModel, ProductData> productUrlConverter;

    @Autowired
    @Qualifier("categoryUrlConverter")
    private Converter<CategoryModel, CategoryData> categoryUrlConverter;

    @Autowired
    @Qualifier("contentPageUrlConverter")
    private Converter<ContentPageModel, ContentPageData> contentPageUrlConverter;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final CMSLinkComponentModel component) {
        model.addAttribute("localizedUrl", getUrl(component, request));
    }

    protected String getUrl(final CMSLinkComponentModel component, HttpServletRequest request) {
        // Call the function getUrlForCMSLinkComponent so that this code is only in one place
        return Functions.getUrlForCMSLinkComponent(component, request, productUrlConverter, categoryUrlConverter, contentPageUrlConverter);
    }
}
