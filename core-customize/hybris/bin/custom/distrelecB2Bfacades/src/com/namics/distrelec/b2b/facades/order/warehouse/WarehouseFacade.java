/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.warehouse;

import java.util.List;

import com.namics.distrelec.b2b.facades.order.warehouse.data.WarehouseData;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.order.data.CartData;

public interface WarehouseFacade {

    List<WarehouseData> getCheckoutPickupWarehousesForSite(CMSSiteModel cmsSite);

    List<WarehouseData> getCheckoutPickupWarehousesForCurrSite();

    List<WarehouseData> getPickupWarehousesAndCalculatePickupDate(CartData cartData);

}
