/*
 * Copyright 2000-2015 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.converters;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.distrelec.webservice.if11.v3.OrderEntryResponse;
import com.namics.distrelec.b2b.core.inout.erp.service.UpdateOrderEntryService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;

import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;

/**
 * This converter is responsible for converting OrdersSearchLine coming from response of searchOrder soap call to OrderHistoryData
 * 
 * @author daeneerajs, Elfa Distrelec
 * @since Distrelec 2.0
 */
public class OrderEntryResponse2AbstractOrderEntryModelConverter extends AbstractPopulatingConverter<OrderEntryResponse, AbstractOrderEntryModel> {

    private static final Map<String, OrderEntryResponse> EMPTY_MAP = Collections.<String, OrderEntryResponse> emptyMap();

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private UpdateOrderEntryService updateOrderEntryService;

    @Override
    protected AbstractOrderEntryModel createTarget() {
        return new CartEntryModel();
    }

    @Override
    public void populate(final OrderEntryResponse source, final AbstractOrderEntryModel target) {
        final ProductModel productForCode = getDistProductService().getProductForCode(source.getMaterialNumber());
        target.setProduct(productForCode);
        target.setUnit(productForCode.getUnit());
        updateOrderEntryService.updateOrderEntry(source, target, EMPTY_MAP);
        super.populate(source, target);
    }

    public DistProductService getDistProductService() {
        return distProductService;
    }

    public void setDistProductService(final DistProductService distProductService) {
        this.distProductService = distProductService;
    }

}
