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
import de.hybris.platform.acceleratorstorefrontcommons.controllers.cms.AbstractCMSComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Abstract Controller for CMS Components.
 */
public abstract class AbstractDistCMSComponentController<T extends AbstractCMSComponentModel>
        extends AbstractCMSComponentController<T> {

    @Override
    protected String handleComponent(final HttpServletRequest request, final HttpServletResponse response,
            final Model model, final T component) {

        final Date now = new Date();

        if (!(component.getVisibleFromDate() != null && now.before(component.getVisibleFromDate()))
                && !(component.getVisibleToDate() != null && now.after(component.getVisibleToDate()))) {
            return super.handleComponent(request, response, model, component);
        } else {
            return getView(component);
        }
    }

    protected String getView(final T component) {
        // build a jsp response based on the component type
        return ControllerConstants.Views.Cms.ComponentPrefix + StringUtils.lowerCase(getTypeCode(component));
    }
}
