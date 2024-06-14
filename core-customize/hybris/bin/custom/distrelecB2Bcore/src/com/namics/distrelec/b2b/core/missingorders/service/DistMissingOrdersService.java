package com.namics.distrelec.b2b.core.missingorders.service;

import java.util.Date;
import java.util.List;

import de.hybris.platform.core.model.order.OrderModel;



public interface DistMissingOrdersService{

	MissingOrdersMatchResult matchMissingOrders(Integer numberOfDays, boolean fetchOrdersByDays, Date orderFromDate, Date orderToDate);

	CreateMissingOrdersResult createSapOrders(List<OrderModel> orders);

	boolean sendReportEmail(List<OrderModel> matchedOrders, List<OrderModel> createdOrders, List<OrderModel> failedOrders);
	
}
