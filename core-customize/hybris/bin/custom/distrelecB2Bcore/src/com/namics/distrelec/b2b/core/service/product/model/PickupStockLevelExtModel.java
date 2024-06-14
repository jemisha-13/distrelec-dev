/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.model;

import de.hybris.platform.ordersplitting.model.WarehouseModel;

public class PickupStockLevelExtModel {

    private WarehouseModel warehouse;

    private Integer stockLevel;

    private boolean isWaldom;

    private String replenishmentDeliveryTime;

    private String replenishmentDeliveryTime2;

    private String mview;

    public WarehouseModel getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(final WarehouseModel warehouse) {
        this.warehouse = warehouse;
    }

    public Integer getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(final Integer stockLevel) {
        this.stockLevel = stockLevel;
    }

    public boolean isWaldom() {
        return isWaldom;
    }

    public void setWaldom(boolean waldom) {
        isWaldom = waldom;
    }

    public String getReplenishmentDeliveryTime() {
        return replenishmentDeliveryTime;
    }

    public void setReplenishmentDeliveryTime(String replenishmentDeliveryTime) {
        this.replenishmentDeliveryTime = replenishmentDeliveryTime;
    }

    public String getReplenishmentDeliveryTime2() {
        return replenishmentDeliveryTime2;
    }

    public void setReplenishmentDeliveryTime2(String replenishmentDeliveryTime2) {
        this.replenishmentDeliveryTime2 = replenishmentDeliveryTime2;
    }

    public String getMview() {
        return mview;
    }

    public void setMview(String mview) {
        this.mview = mview;
    }
}
