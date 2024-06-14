package com.namics.distrelec.b2b.core.missingorders.dao;

import java.util.Date;
import java.util.List;

import de.hybris.platform.core.model.order.OrderModel;

public interface DistMissingOrdersDao{

	/**
	 * Find all missing orders created between fromDate and toDate
	 *
	 * @param fromDate Start date
	 * @param toDate End date
	 * @return
	 */
	List<OrderModel> findMissingOrders(Date fromDate, Date toDate);

	/**
	 * Find all missing orders created in numberOfDays before now
	 *
	 * @param numberOfDays Number of days to include
	 * @return
	 */
	List<OrderModel> findMissingOrders(int numberOfDays);
}
