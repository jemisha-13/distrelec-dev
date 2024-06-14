/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.facades.productfinder.DistProductFinderFacade;
import com.namics.distrelec.b2b.facades.productfinder.data.DistProductFinderData;
import com.namics.distrelec.b2b.storefront.controllers.pages.CategoryPageController;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller with product finder operations.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@Controller
public class ProductFinderController {

    protected static final Logger LOG = Logger.getLogger(CategoryPageController.class);

    @Autowired
    private DistProductFinderFacade distProductFinderFacade;

    @RequestMapping(value = "/productFinder", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody DistProductFinderData refineProductFinderData(@RequestBody
    final DistProductFinderData productFinderData) {
        distProductFinderFacade.updateProductFinderData(productFinderData);
        return productFinderData;
    }

}
