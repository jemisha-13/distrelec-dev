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
import de.hybris.platform.acceleratorcms.model.components.NavigationBarComponentModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller for NavigationBarComponent
 */
@Controller("NavigationBarComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.NavigationBarComponent)
public class NavigationBarComponentController extends AbstractDistCMSComponentController<NavigationBarComponentModel> {
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final NavigationBarComponentModel component) {
        if (component.getDropDownLayout() != null) {
            model.addAttribute("dropDownLayout", component.getDropDownLayout().getCode().toLowerCase());
        }
    }
}
