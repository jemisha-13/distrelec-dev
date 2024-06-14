/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.model.cms2.components.DistrelecManufacturerLinecardComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistrelecManufacturerLinecardItemComponentModel;
import com.namics.distrelec.b2b.facades.cms.data.DistrelecManufacturerLinecardItemData;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@code DistrelecManufacturerLinecardComponentController}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.0
 */
@Controller("DistrelecManufacturerLinecardComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistrelecManufacturerLinecardComponent)
public class DistrelecManufacturerLinecardComponentController extends AbstractDistCMSComponentController<DistrelecManufacturerLinecardComponentModel> {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Autowired
    @Qualifier("imageConverter")
    private Converter<MediaModel, ImageData> imageConverter;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistrelecManufacturerLinecardComponentModel component) {
        model.addAttribute("linecardItems", getItems(component.getItems()));
    }

    /**
     * Convert the collection of {@code DistrelecManufacturerLinecardItemComponentModel} to a list of
     * {@code DistrelecManufacturerLinecardItemData}
     *
     * @param items
     *            the source collection to be converted
     * @return a list of {@code DistrelecManufacturerLinecardItemData}
     */
    protected List<DistrelecManufacturerLinecardItemData> getItems(final Collection<DistrelecManufacturerLinecardItemComponentModel> items) {
        final List<DistrelecManufacturerLinecardItemData> datas = new ArrayList<DistrelecManufacturerLinecardItemData>();

        if (items != null && !items.isEmpty()) {
            for (final DistrelecManufacturerLinecardItemComponentModel carpetModel : items) {
                datas.add(convert(carpetModel));
            }
        }

        return datas;
    }

    /**
     * Convert the source {@code DistrelecManufacturerLinecardItemComponentModel} to a target {@code DistrelecManufacturerLinecardItemData}
     *
     * @param itemModel
     *            the source item to be converted
     * @return an instance of {@code DistrelecManufacturerLinecardItemData}
     */
    protected DistrelecManufacturerLinecardItemData convert(final DistrelecManufacturerLinecardItemComponentModel itemModel) {
        final DistrelecManufacturerLinecardItemData itemData = new DistrelecManufacturerLinecardItemData();
        itemData.setTitle(itemModel.getTitle());
        itemData.setUrl(itemModel.getUrl());

        if (itemModel.getIcon() != null) {
            imageConverter.convert(itemModel.getIcon(), itemData.getIcon());
        }

        return itemData;
    }
}
