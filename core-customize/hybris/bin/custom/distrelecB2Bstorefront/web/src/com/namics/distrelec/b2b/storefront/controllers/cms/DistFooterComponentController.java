/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.model.cms2.components.DistFooterComponentModel;
import com.namics.distrelec.b2b.facades.cms.data.DistFooterComponentData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

import static com.namics.distrelec.b2b.storefront.controllers.ControllerConstants.Actions.Cms.DistFooterComponent;

/**
 * {@code DistFooterComponentController}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
@Controller("DistFooterComponentController")
@RequestMapping(value = DistFooterComponent)
public class DistFooterComponentController extends AbstractDistCMSComponentController<DistFooterComponentModel> {

    @Autowired
    private Converter<DistFooterComponentModel, DistFooterComponentData> distFooterComponentConverter;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.storefront.controllers.cms.AbstractCMSComponentController#fillModel(javax.servlet.http.HttpServletRequest,
     * org.springframework.ui.Model, de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)
     */
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistFooterComponentModel component) {
        // Adding the Footer Component Data to the request attributes
        model.addAttribute("footerComponentData", distFooterComponentConverter.convert(component));
    }

    public Converter<DistFooterComponentModel, DistFooterComponentData> getDistFooterComponentConverter() {
        return distFooterComponentConverter;
    }

    public void setDistFooterComponentConverter(final Converter<DistFooterComponentModel, DistFooterComponentData> distFooterComponentConverter) {
        this.distFooterComponentConverter = distFooterComponentConverter;
    }
}
