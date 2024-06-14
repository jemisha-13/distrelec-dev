package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;

import java.util.List;

public class OrderCancellationEvent extends AbstractCommerceUserEvent<BaseSiteModel> {

    private String orderNumber;

    private List<String> articleNumbers;

    private List<String> productNames;

    public OrderCancellationEvent() {
        super();
    }

    public OrderCancellationEvent(String orderNumber, List<String> articleNumbers, List<String> productNames) {
        this.orderNumber = orderNumber;
        this.articleNumbers = articleNumbers;
        this.productNames = productNames;
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
