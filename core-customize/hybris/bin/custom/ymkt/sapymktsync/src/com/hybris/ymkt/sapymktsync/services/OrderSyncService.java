/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.ymkt.sapymktsync.services;

import de.hybris.platform.core.model.order.OrderModel;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Send {@link OrderModel} to CUAN_BUSINESS_DOCUMENT_IMP_SRV
 */
public class OrderSyncService extends AbstractBusinessDocSyncService<OrderModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(OrderSyncService.class);

	@Override
	protected Map<String, Object> convertModelToMap(OrderModel order, Map<String, Object> parameters)
	{
		final Map<String, Object> map = super.convertModelToMap(order, parameters);

		//<Property Name="InternalObjectType" Type="Edm.String" MaxLength="20" sap:label="Business Document Type" sap:updatable="false"/>
		map.put("InternalObjectType", "SALES_ORDER");

		//<Property Name="ExternalObjectType" Type="Edm.String" MaxLength="30" sap:label="Internal Object Type" sap:updatable="false"/>
		map.put("ExternalObjectType", "SAP_HYBRIS_ORDER");

		return map;
	}
}
