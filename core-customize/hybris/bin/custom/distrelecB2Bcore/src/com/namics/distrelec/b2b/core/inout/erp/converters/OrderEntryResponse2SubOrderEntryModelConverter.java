/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.converters;

import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.webservice.if11.v3.OrderEntryResponse;
import com.namics.distrelec.b2b.core.model.order.SubOrderEntryModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * This converter is responsible for converting OrdersSearchLine coming from response of searchOrder soap call to OrderHistoryData
 * 
 * @author daeneerajs, Elfa Distrelec
 * @since Distrelec 2.0
 */
public class OrderEntryResponse2SubOrderEntryModelConverter extends AbstractPopulatingConverter<OrderEntryResponse, SubOrderEntryModel> {

    @Autowired
    DistProductService distProductService;

    @Override
    protected SubOrderEntryModel createTarget() {
        return new SubOrderEntryModel();
    }

    @Override
    public void populate(final OrderEntryResponse source, final SubOrderEntryModel target) {
        ProductModel productForCode = null;
        try {
            productForCode = getDistProductService().getProductForCode(source.getMaterialNumber());
            if (productForCode != null && productForCode.getGalleryImages() != null && productForCode.getGalleryImages().size() > 0
                    && productForCode.getGalleryImages().get(0) != null)
                target.setImageUrl(productForCode.getGalleryImages().get(0).getName());

            target.setMaterialName(productForCode.getName());

        } catch (final Exception e) {
            // NOP
        }
        target.setMaterialNumber(source.getMaterialNumber());
        target.setOrderQuantity((int) source.getOrderQuantity());
        super.populate(source, target);
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    public void setDistProductService(final DistProductService distProductService) {
        this.distProductService = distProductService;
    }

}
