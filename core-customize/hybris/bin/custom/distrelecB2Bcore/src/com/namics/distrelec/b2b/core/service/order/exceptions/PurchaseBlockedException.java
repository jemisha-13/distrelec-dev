package com.namics.distrelec.b2b.core.service.order.exceptions;

import java.util.List;

public class PurchaseBlockedException extends RuntimeException {

    private List<String> productCodes;

    private String orderNumber;

    private List<String> productNames;

    public PurchaseBlockedException(List<String> productCodes) {
        this.productCodes = productCodes;
    }

    public PurchaseBlockedException(List<String> productCodes, List<String> productNames, String orderNumber) {
        this.productCodes = productCodes;
        this.productNames = productNames;
        this.orderNumber = orderNumber;
    }

    public List<String> getProductCodes() {
        return productCodes;
    }

    public void setProductCodes(List<String> productCodes) {
        this.productCodes = productCodes;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }
}
