package com.namics.distrelec.b2b.facades.order;

import java.util.List;

public class DistCancelledOrderPrepayment {
    private String uid;

    private String orderNumber;

    private List<String> articleNumbers;

    private List<String> productNames;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<String> getArticleNumbers() {
        return articleNumbers;
    }

    public void setArticleNumbers(List<String> articleNumbers) {
        this.articleNumbers = articleNumbers;
    }

    public List<String> getProductNames() {
        return productNames;
    }

    public void setProductNames(List<String> productNames) {
        this.productNames = productNames;
    }
}
