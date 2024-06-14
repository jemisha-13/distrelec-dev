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
package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import com.namics.distrelec.b2b.storefront.web.view.UiExperienceViewResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for evil clients that go for the favicon.ico directly in the root, redirect them to the real location
 */
@Controller
public class FavIconController extends AbstractController {

    @RequestMapping(value = "/favicon.ico", method = RequestMethod.GET)
    public String getFavIcon() {
        return UiExperienceViewResolver.REDIRECT_PERMANENT_URL_PREFIX + "/_ui/all/media/favicon.ico";
    }
}
