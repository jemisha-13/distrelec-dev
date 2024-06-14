/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistManufacturerCarouselComponentModel;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for CMS DistProductCarouselComponent.
 */
@Controller("DistManufacturerCarouselComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistManufacturerCarouselComponent)
public class DistManufacturerCarouselComponentController extends AbstractDistCMSComponentController<DistManufacturerCarouselComponentModel> {
    private static final String AUTOPLAY = "autoplay";

    @Autowired
    @Qualifier("distManufacturerConverter")
    private Converter<DistManufacturerModel, DistManufacturerData> distManufacturerConverter;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistManufacturerCarouselComponentModel component) {
        final List<DistManufacturerData> manufacturers = new ArrayList<DistManufacturerData>();
        manufacturers.addAll(collectManufacturers(component));

        if (component.getAutoplayTimeout() == null) {
            model.addAttribute(AUTOPLAY, Boolean.FALSE);
        } else {
            model.addAttribute(AUTOPLAY, Boolean.TRUE);
        }
        model.addAttribute("manufacturerCarouselData", manufacturers);
    }

    protected List<DistManufacturerData> collectManufacturers(final DistManufacturerCarouselComponentModel component) {
        final List<DistManufacturerData> manufacturers = new ArrayList<DistManufacturerData>();
        for (final DistManufacturerModel distManufacturerModel : component.getManufacturers()) {
            manufacturers.add(distManufacturerConverter.convert(distManufacturerModel));
        }
        return manufacturers;
    }

    public Converter<DistManufacturerModel, DistManufacturerData> getDistManufacturerConverter() {
        return distManufacturerConverter;
    }

    public void setDistManufacturerConverter(final Converter<DistManufacturerModel, DistManufacturerData> distManufacturerConverter) {
        this.distManufacturerConverter = distManufacturerConverter;
    }
}
