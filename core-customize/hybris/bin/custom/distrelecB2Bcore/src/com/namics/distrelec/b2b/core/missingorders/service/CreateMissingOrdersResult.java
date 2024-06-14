package com.namics.distrelec.b2b.core.missingorders.service;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.List;

public class CreateMissingOrdersResult {

    private final List<OrderModel> successfullyCreatedOrders;

    private final List<OrderModel> failedOrders;

    public CreateMissingOrdersResult(List<OrderModel> successfullyCreatedOrders, List<OrderModel> failedOrders) {
        this.successfullyCreatedOrders = successfullyCreatedOrders;
        this.failedOrders = failedOrders;
    }

    public List<OrderModel> getSuccessfullyCreatedOrders() {
        return successfullyCreatedOrders;
    }

    public List<OrderModel> getFailedOrders() {
        return failedOrders;
    }
}
