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

import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.ProductDataHelper;
import de.hybris.platform.acceleratorcms.model.components.ProductReferencesComponentModel;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Controller for CMS ProductReferencesComponent.
 */
@Controller("ProductReferencesComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.ProductReferencesComponent)
public class ProductReferencesComponentController extends AbstractDistCMSComponentController<ProductReferencesComponentModel> {
    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE);

    @Autowired
    @Qualifier("productFacade")
    private ProductFacade productFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final ProductReferencesComponentModel component) {
        final List<ProductReferenceData> productReferences = productFacade.getProductReferencesForCode(ProductDataHelper.getCurrentProduct(request),
                component.getProductReferenceTypes(), PRODUCT_OPTIONS, component.getMaximumNumberProducts());

        model.addAttribute("title", component.getTitle());
        model.addAttribute("productReferences", productReferences);
    }
}
