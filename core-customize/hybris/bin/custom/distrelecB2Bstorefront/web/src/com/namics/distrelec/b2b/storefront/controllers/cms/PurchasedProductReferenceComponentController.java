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

import com.namics.distrelec.b2b.facades.suggestion.SimpleSuggestionFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.acceleratorcms.model.components.PurchasedProductReferencesComponentModel;
import de.hybris.platform.commercefacades.product.data.ProductData;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller for CMS PurchasedProductReferencesComponent.
 */
@Controller("PurchasedProductReferencesComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.PurchasedProductReferencesComponent)
public class PurchasedProductReferenceComponentController extends AbstractDistCMSComponentController<PurchasedProductReferencesComponentModel> {
    @Autowired
    private SimpleSuggestionFacade b2bSimpleSuggestionFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final PurchasedProductReferencesComponentModel component) {
        final List<ProductData> products = b2bSimpleSuggestionFacade.getReferencesForPurchasedInCategory(component.getCategory().getCode(),
                component.getProductReferenceType(), component.isFilterPurchased(), component.getMaximumNumberProducts());

        model.addAttribute("title", component.getTitle());
        model.addAttribute("productReferences", products);
    }

    @Override
    protected String getView(final PurchasedProductReferencesComponentModel component) {
        // Uses same view as the ProductReferencesComponent
        return ControllerConstants.Views.Cms.ComponentPrefix + StringUtils.lowerCase(PurchasedProductReferencesComponentModel._TYPECODE);
    }
}
