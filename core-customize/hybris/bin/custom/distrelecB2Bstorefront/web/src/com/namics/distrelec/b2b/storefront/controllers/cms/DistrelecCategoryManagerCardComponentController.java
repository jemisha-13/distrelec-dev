/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.model.cms2.components.DistrelecCategoryManagerCardComponentModel;
import com.namics.distrelec.b2b.facades.cms.data.DistrelecCategoryManagerCardData;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * {@code DistrelecCategoryManagerCardComponentController}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.7
 */
@Controller("DistrelecCategoryManagerCardComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistrelecCategoryManagerCardComponent)
public class DistrelecCategoryManagerCardComponentController extends AbstractDistCMSComponentController<DistrelecCategoryManagerCardComponentModel> {

    @Autowired
    private Converter<MediaModel, ImageData> imageConverter;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistrelecCategoryManagerCardComponentModel component) {
        if (component == null) {
            return;
        }

        final DistrelecCategoryManagerCardData data = new DistrelecCategoryManagerCardData(component.getManagerName(), component.getJobTitle(),
                component.getOrganisation(), component.getQuote(), component.getTipp(), component.getCtaText(), component.getCtaLink(),
                component.isRightFloat());
        if (component.getImage() != null) {
            data.setImage(getImageConverter().convert(component.getImage()));
        }

        model.addAttribute("categoryManagerCard", data);
    }

    public Converter<MediaModel, ImageData> getImageConverter() {
        return imageConverter;
    }

    public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter) {
        this.imageConverter = imageConverter;
    }
}
