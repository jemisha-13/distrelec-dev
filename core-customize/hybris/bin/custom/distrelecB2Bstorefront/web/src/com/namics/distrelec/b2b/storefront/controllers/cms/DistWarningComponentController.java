/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.model.cms2.components.DistWarningComponentModel;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * {@code DistWarningComponentController}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
@Controller("DistWarningComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistWarningComponent)
public class DistWarningComponentController extends AbstractDistCMSComponentController<DistWarningComponentModel> {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.storefront.controllers.cms.AbstractCMSComponentController#fillModel(javax.servlet.http.HttpServletRequest,
     * org.springframework.ui.Model, de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)
     */
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistWarningComponentModel component) {
        model.addAttribute("warningComponent", component);
    }

}
