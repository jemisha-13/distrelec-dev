package com.namics.distrelec.b2b.core.missingorders.service;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.List;

public class MissingOrdersMatchResult {

    private final List<OrderModel> ordersFoundInErp;

    private final List<OrderModel> ordersMissingInErp;

    public MissingOrdersMatchResult(List<OrderModel> ordersFoundInErp, List<OrderModel> ordersMissingInErp) {
        this.ordersFoundInErp = ordersFoundInErp;
        this.ordersMissingInErp = ordersMissingInErp;
    }

    public List<OrderModel> getOrdersFoundInErp() {
        return ordersFoundInErp;
    }

    public List<OrderModel> getOrdersMissingInErp() {
        return ordersMissingInErp;
    }
}
