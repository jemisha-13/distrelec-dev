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

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.common.user.UserContextService;

import static com.hybris.ymkt.sapymktsync.util.UserUtil.getUserOriginalUid;


/**
 * Send {@link AbstractOrderModel} to CUAN_BUSINESS_DOCUMENT_IMP_SRV
 * 
 * @param <M>
 *           Subclass of AbstractOrderModel
 */
public abstract class AbstractBusinessDocSyncService<M extends AbstractOrderModel> extends AbstractImportHeaderSyncService<M>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(AbstractBusinessDocSyncService.class);

	protected Map<String, Object> convertOrderEntryModelToMap(AbstractOrderEntryModel entry)
	{
		Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="Id" Type="Edm.String" Nullable="false" MaxLength="32" sap:label="ID" sap:updatable="false"/>

		//<Property Name="ObjectType" Type="Edm.String" Nullable="false" MaxLength="30" sap:label="Origin of Product" sap:updatable="false"/>
		map.put("ObjectType", "SAP_HYBRIS_PRODUCT");

		//<Property Name="ObjectId" Type="Edm.String" Nullable="false" MaxLength="50" sap:label="Product ID" sap:updatable="false"/>
		map.put("ObjectId", entry.getProduct().getCode());

		//<Property Name="ProductName" Type="Edm.String" MaxLength="40" sap:label="Product" sap:updatable="false"/>
		//<Property Name="ProductDesc" Type="Edm.String" MaxLength="512" sap:label="Product Desc." sap:updatable="false"/>

		//<Property Name="Amount" Type="Edm.Decimal" Precision="31" Scale="2" sap:label="Amount" sap:updatable="false"/>
		map.put("Amount", entry.getTotalPrice());

		//<Property Name="Quantity" Type="Edm.Decimal" Precision="22" Scale="5" sap:unit="UnitOfMeasure" sap:label="Quantity" sap:updatable="false"/>
		map.put("Quantity", entry.getQuantity());

		//<Property Name="UnitOfMeasure" Type="Edm.String" MaxLength="3" sap:label="Unit of Measure" sap:updatable="false"/>
		map.put("UnitOfMeasure", entry.getUnit().getCode());

		//<Property Name="StatusCode" Type="Edm.String" MaxLength="2" sap:label="Status Code" sap:updatable="false"/>
		//<Property Name="Reason" Type="Edm.String" MaxLength="20" sap:label="Reason" sap:updatable="false"/>

		return map;
	}

	@Override
	protected Map<String, Object> convertModelToMap(M model, Map<String, Object> parameters)
	{
		final Map<String, Object> map = new LinkedHashMap<>();
		final Optional<String> ymktID = Optional.ofNullable(model.getYmktTrackingId()).filter(s -> !s.isEmpty());

		//<Property Name="Id" Type="Edm.String" Nullable="false" MaxLength="32" sap:label="Key" sap:updatable="false"/>

		//<Property Name="ContactIdOrigin" Type="Edm.String" MaxLength="20" sap:label="Contact ID Origin" sap:updatable="false"/>
		map.put("ContactIdOrigin", ymktID.isPresent() ? null : UserContextService.getOriginIdSapHybrisConsumer());

		//<Property Name="ContactId" Type="Edm.String" MaxLength="255" sap:label="Contact ID" sap:updatable="false"/>
		map.put("ContactId", ymktID.orElse(getUserOriginalUid(model.getUser())));

		//<Property Name="ExternalId" Type="Edm.String" MaxLength="50" sap:label="External Id" sap:updatable="false"/>
		map.put("ExternalId", model.getCode());

		//<Property Name="ExternalTimeStamp" Type="Edm.DateTime" Precision="7" sap:label="External Time Stamp" sap:updatable="false"/>
		map.put("ExternalTimeStamp", model.getModifiedtime());

		//<Property Name="Currency" Type="Edm.String" MaxLength="5" sap:label="Currency" sap:updatable="false" sap:semantics="currency-code"/>
		map.put("Currency", model.getCurrency().getIsocode());

		//<Property Name="ActionCode" Type="Edm.String" MaxLength="2" sap:label="Action Code" sap:updatable="false"/>
		map.put("ActionCode", "04");

		//<Property Name="Amount" Type="Edm.Decimal" Precision="31" Scale="2" sap:unit="Currency" sap:label="Expected Revenue" sap:updatable="false"/>
		map.put("Amount", model.getTotalPrice());

		//<Property Name="StatusCode" Type="Edm.String" MaxLength="2" sap:label="Interaction Status" sap:updatable="false"/>
		map.put("StatusCode", OrderStatus.CANCELLED.equals(model.getStatus()) ? "04" : "03");

		//<NavigationProperty Name="ProductItems" Relationship="CUAN_BUSINESS_DOCUMENT_IMP_SRV.BusinessDocumentProductItem" FromRole="FromRole_BusinessDocumentProductItem" ToRole="ToRole_BusinessDocumentProductItem"/>
		map.put("ProductItems", model.getEntries().stream().map(this::convertOrderEntryModelToMap).collect(Collectors.toList()));

		return map;
	}

	@Override
	protected String getImportHeaderNavigationProperty()
	{
		return "BusinessDocuments";
	}
}
