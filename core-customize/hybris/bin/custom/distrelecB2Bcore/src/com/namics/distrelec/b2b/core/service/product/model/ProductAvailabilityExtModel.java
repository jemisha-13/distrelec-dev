/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.product.model;

import java.util.Date;
import java.util.List;

/**
 * Contains all information about the availability including delivery time.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 */
public class ProductAvailabilityExtModel {

    private String productCode;
    private Integer requestedQuantity;
    private Boolean detailInfo;
    private Integer stockLevelTotal;
    private Integer backorderQuantity;
    private Date backorderDeliveryDate;
    private String deliveryTimeBackorder;
    private Integer leadTimeErp;

    private List<PickupStockLevelExtModel> stockLevelPickup;
    private List<StockLevelData> stockLevels;

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    public Integer getRequestedQuantity() {
        return requestedQuantity;
    }

    public void setRequestedQuantity(final Integer requestedQuantity) {
        this.requestedQuantity = requestedQuantity;
    }

    public Boolean getDetailInfo() {
        return detailInfo;
    }

    public void setDetailInfo(final Boolean detailInfo) {
        this.detailInfo = detailInfo;
    }

    public Integer getStockLevelTotal() {
        return stockLevelTotal;
    }

    public void setStockLevelTotal(final Integer stockLevelTotal) {
        this.stockLevelTotal = stockLevelTotal;
    }

    public Integer getBackorderQuantity() {
        return backorderQuantity;
    }

    public void setBackorderQuantity(final Integer backorderQuantity) {
        this.backorderQuantity = backorderQuantity;
    }

    public Date getBackorderDeliveryDate() {
        return backorderDeliveryDate;
    }

    public void setBackorderDeliveryDate(final Date backorderDeliveryDate) {
        this.backorderDeliveryDate = backorderDeliveryDate;
    }

    public List<PickupStockLevelExtModel> getStockLevelPickup() {
        return stockLevelPickup;
    }

    public void setStockLevelPickup(final List<PickupStockLevelExtModel> stockLevelPickup) {
        this.stockLevelPickup = stockLevelPickup;
    }

    public String getDeliveryTimeBackorder() {
        return deliveryTimeBackorder;
    }

    public void setDeliveryTimeBackorder(final String deliveryTimeBackorder) {
        this.deliveryTimeBackorder = deliveryTimeBackorder;
    }

    public Integer getLeadTimeErp() {
        return leadTimeErp;
    }

    public void setLeadTimeErp(final Integer leadTimeErp) {
        this.leadTimeErp = leadTimeErp;
    }

    public List<StockLevelData> getStockLevels() {
        return stockLevels;
    }

    public void setStockLevels(final List<StockLevelData> stockLevels) {
        this.stockLevels = stockLevels;
    }
}
