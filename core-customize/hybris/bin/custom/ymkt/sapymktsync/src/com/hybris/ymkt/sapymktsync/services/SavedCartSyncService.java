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

import de.hybris.platform.core.model.order.CartModel;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * Send {@link CartModel} to CUAN_BUSINESS_DOCUMENT_IMP_SRV
 */
public class SavedCartSyncService extends AbstractBusinessDocSyncService<CartModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(SavedCartSyncService.class);


	@Override
	protected Map<String, Object> convertModelDeletedToMap(CartModel cart, Map<String, Object> parameters) throws IOException
	{
		final Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="Id" Type="Edm.String" Nullable="false" MaxLength="32" sap:label="Key" sap:updatable="false"/>
		map.put("ExternalId", cart.getCode());

		//<Property Name="ExternalTimeStamp" Type="Edm.DateTime" Precision="7" sap:label="External Time Stamp" sap:updatable="false"/>
		map.put("ExternalTimeStamp", new Date());

		//<Property Name="ExternalObjectType" Type="Edm.String" MaxLength="30" sap:label="Internal Object Type" sap:updatable="false"/>
		map.put("ExternalObjectType", "SAP_HYBRIS_CART");

		//<Property Name="ActionCode" Type="Edm.String" MaxLength="2" sap:label="Action Code" sap:updatable="false"/>
		map.put("ActionCode", "05");

		return map;
	}

	@Override
	protected Map<String, Object> convertModelToMap(CartModel cart, Map<String, Object> parameters)
	{
		final Map<String, Object> map = super.convertModelToMap(cart, parameters);

		//<Property Name="InternalObjectType" Type="Edm.String" MaxLength="20" sap:label="Business Document Type" sap:updatable="false"/>
		map.put("InternalObjectType", "SHOP_CART_SAVED");

		//<Property Name="ExternalObjectType" Type="Edm.String" MaxLength="30" sap:label="Internal Object Type" sap:updatable="false"/>
		map.put("ExternalObjectType", "SAP_HYBRIS_CART");

		//<Property Name="Content" Type="Edm.String" sap:label="Business Document Content" sap:updatable="false" sap:sortable="false"/>
		//<Property Name="ContentTitle" Type="Edm.String" MaxLength="255" sap:label="Content Title" sap:updatable="false"/>

		return map;
	}
}
