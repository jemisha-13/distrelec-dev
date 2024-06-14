/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.warehouse.converters;

import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.ordersplitting.model.WarehouseModel;

public class WarehouseConverter extends AbstractPopulatingConverter<WarehouseModel, WarehouseData> {

    @Override
    public void populate(final WarehouseModel source, final WarehouseData target) {
        target.setCode(source.getCode());
        target.setName(source.getPickupName());
        target.setStreetName(source.getPickupStreetName());
        target.setStreetNumber(source.getPickupStreetNumber());
        target.setPostalCode(source.getPickupPostalCode());
        target.setTown(source.getPickupTown());
        target.setPhone(source.getPickupPhone());
        target.setOpeningsHourMoFr(source.getPickupOpeningHoursMoFr());
        target.setOpeningsHourSa(source.getPickupOpeningHoursSa());
        super.populate(source, target);
    }
}
