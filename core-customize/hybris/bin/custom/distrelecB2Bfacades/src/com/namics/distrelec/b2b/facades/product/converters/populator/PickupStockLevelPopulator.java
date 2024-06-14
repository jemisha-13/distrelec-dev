/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.service.product.model.PickupStockLevelExtModel;
import com.namics.distrelec.b2b.facades.product.data.PickupStockLevelData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

/**
 * PickupStockLevelPopulator to populate a {@link PickupStockLevelExtModel} to a {@link PickupStockLevelData}.
 *
 * @author pnueesch, Namics AG
 *
 */
public class PickupStockLevelPopulator<SOURCE extends PickupStockLevelExtModel, TARGET extends PickupStockLevelData> implements Populator<SOURCE, TARGET> {

    @Override
    public void populate(final SOURCE pickupStockLevelModel, final TARGET pickupStockLevelData) throws ConversionException {
        if (pickupStockLevelModel.getWarehouse() != null) {
            final WarehouseModel warehouse = pickupStockLevelModel.getWarehouse();
            pickupStockLevelData.setWarehouseCode(warehouse.getCode());
            pickupStockLevelData.setWarehouseName(StringUtils.isNotBlank(warehouse.getPickupName()) ? warehouse.getPickupName() : warehouse.getName());
        }
        pickupStockLevelData.setStockLevel(pickupStockLevelModel.getStockLevel());
        pickupStockLevelData.setWaldom(pickupStockLevelModel.isWaldom());
        pickupStockLevelData.setReplenishmentDeliveryTime(pickupStockLevelModel.getReplenishmentDeliveryTime());
        pickupStockLevelData.setReplenishmentDeliveryTime2(pickupStockLevelModel.getReplenishmentDeliveryTime2());
        pickupStockLevelData.setMview(pickupStockLevelModel.getMview());
    }
}
