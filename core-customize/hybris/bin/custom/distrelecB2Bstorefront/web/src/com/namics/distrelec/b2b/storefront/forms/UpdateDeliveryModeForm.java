package com.namics.distrelec.b2b.storefront.forms;

import javax.validation.constraints.NotBlank;

public class UpdateDeliveryModeForm {
    String deliveryModeCode;

    String warehouseCode;

    @NotBlank
    public String getDeliveryModeCode() {
        return deliveryModeCode;
    }

    public void setDeliveryModeCode(String deliveryModeCode) {
        this.deliveryModeCode = deliveryModeCode;
    }

    public String getWarehouseCode() {
        return warehouseCode;
    }

    public void setWarehouseCode(String warehouseCode) {
        this.warehouseCode = warehouseCode;
    }
}
