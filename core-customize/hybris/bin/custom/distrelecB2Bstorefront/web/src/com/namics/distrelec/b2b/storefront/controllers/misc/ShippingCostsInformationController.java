/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.misc;

import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * {@code ShippingCostsInformationController}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.9
 */
@Controller
public class ShippingCostsInformationController extends AbstractController {
    protected static final Logger LOG = Logger.getLogger(ShippingCostsInformationController.class);

    private static final String SHIPPING_COSTS_COMPONENT_UID = "shipping-costs-information-component";

    @Autowired
    private CMSComponentService cmsComponentService;

    @RequestMapping(value = "/shipping-costs", method = { RequestMethod.GET, RequestMethod.POST }, produces = "text/html; charset=UTF-8")
    public @ResponseBody String get() {

        try {
            final CMSParagraphComponentModel component = getCmsComponentService()
                    .<CMSParagraphComponentModel> getSimpleCMSComponent(SHIPPING_COSTS_COMPONENT_UID);
            return component.getContent();
        } catch (final Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return StringUtils.EMPTY;
    }

    public CMSComponentService getCmsComponentService() {
        return cmsComponentService;
    }

    public void setCmsComponentService(final CMSComponentService cmsComponentService) {
        this.cmsComponentService = cmsComponentService;
    }
}
