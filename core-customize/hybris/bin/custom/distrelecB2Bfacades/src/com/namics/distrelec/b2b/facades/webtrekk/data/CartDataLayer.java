/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.webtrekk.data;

import java.util.List;

public class CartDataLayer {

    private List<ProductData> products;
    private String orderValue;
    private String orderId;
    private String discountValue;
    private String voucherValue;
    private String shippingCosts;
    private String paymentFee;
    private String VAT;
    private String GTV;
    private int totalProductDistinct;
    private int totalProducts;

    public List<ProductData> getProducts() {
        return products;
    }

    public void setProducts(final List<ProductData> products) {
        this.products = products;
    }

    public String getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(final String orderValue) {
        this.orderValue = orderValue;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(final String discountValue) {
        this.discountValue = discountValue;
    }

    public String getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(final String voucherValue) {
        this.voucherValue = voucherValue;
    }

    public String getShippingCosts() {
        return shippingCosts;
    }

    public void setShippingCosts(final String shippingCosts) {
        this.shippingCosts = shippingCosts;
    }

    public String getPaymentFee() {
        return paymentFee;
    }

    public void setPaymentFee(final String paymentFee) {
        this.paymentFee = paymentFee;
    }

    public String getVAT() {
        return VAT;
    }

    public void setVAT(final String vAT) {
        VAT = vAT;
    }

    public String getGTV() {
        return GTV;
    }

    public void setGTV(final String gTV) {
        GTV = gTV;
    }

    public int getTotalProductDistinct() {
        return totalProductDistinct;
    }

    public void setTotalProductDistinct(final int totalProductDistinct) {
        this.totalProductDistinct = totalProductDistinct;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(final int totalProducts) {
        this.totalProducts = totalProducts;
    }
}
