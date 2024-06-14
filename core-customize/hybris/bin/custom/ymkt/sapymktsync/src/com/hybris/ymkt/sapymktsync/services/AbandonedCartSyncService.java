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

import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.user.UserConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.product.ProductURLService;
import com.hybris.ymkt.common.user.UserContextService;

import static com.hybris.ymkt.sapymktsync.util.UserUtil.getUserOriginalUid;


/**
 * Send {@link CartModel} to CUAN_IMPORT_SRV
 */
public class AbandonedCartSyncService extends AbstractImportHeaderSyncService<CartModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(AbandonedCartSyncService.class);

	protected ProductURLService productURLService;

	protected Map<String, Object> convertCartEntryModelToMap(CartEntryModel cartEntry, Map<String, Object> parameters)
	{
		final Map<String, Object> map = new HashMap<>();
		final Locale loc = this.getLocale(parameters);
		final ProductModel product = cartEntry.getProduct();

		//<Property Name="ItemId" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Product ID" sap:updatable="false"/>
		map.put("ItemId", product.getCode());

		//<Property Name="ItemType" Type="Edm.String" Nullable="false" MaxLength="30" sap:unicode="false" sap:label="Origin of Product" sap:updatable="false"/>
		map.put("ItemType", "SAP_HYBRIS_PRODUCT");

		//<Property Name="Amount" Type="Edm.Decimal" Precision="31" Scale="2" sap:unicode="false" sap:label="Amount" sap:updatable="false"/>
		map.put("Amount", cartEntry.getTotalPrice());

		//<Property Name="Description" sap:updatable="false" sap:label="Product Desc." sap:unicode="false" MaxLength="120" Type="Edm.String"/>
		this.optionalPut(map, "Description", product, //
				p -> p.getDescription(loc), //
				d -> d.replaceAll("\\<.*?\\>", ""));

		//<Property Name="ImageUrl" sap:updatable="false" sap:label="Short URI" sap:unicode="false" MaxLength="1333" Type="Edm.String" sap:filterable="false" sap:sortable="false"/>
		this.productURLService.getProductImageURL(product).ifPresent(img -> map.put("ImageUrl", img));

		//<Property Name="Key" sap:updatable="false" sap:label="GUID 16" sap:unicode="false" MaxLength="32" Type="Edm.String"/>

		//<Property Name="Name" sap:updatable="false" sap:label="Product" sap:unicode="false" MaxLength="40" Type="Edm.String"/>
		this.optionalPut(map, "Name", product, p -> p.getName(loc));

		//<Property Name="NavigationURL" sap:updatable="false" sap:label="Short URI" sap:unicode="false" MaxLength="1333" Type="Edm.String" sap:filterable="false" sap:sortable="false"/>
		map.put("NavigationURL", this.productURLService.getProductURL(product));

		//<Property Name="SourceSystemId" Type="Edm.String" MaxLength="255" sap:unicode="false" sap:label="Source Syst. ID" sap:updatable="false"/>
		map.put("SourceSystemId", "SAP_MERCH_SHOP");

		//<Property Name="Quantity" Type="Edm.Decimal" Precision="22" Scale="5" sap:unicode="false" sap:unit="UnitOfMeasure" sap:label="Quantity" sap:updatable="false"/>
		map.put("Quantity", cartEntry.getQuantity());

		//<Property Name="UnitOfMeasure" Type="Edm.String" MaxLength="3" sap:unicode="false" sap:label="Unit of Measure" sap:updatable="false" sap:sortable="false"/>
		map.put("UnitOfMeasure", cartEntry.getUnit().getCode());

		return map;
	}

	@Override
	protected Map<String, Object> convertModelToMap(CartModel cart, Map<String, Object> parameters)
	{
		final Map<String, Object> map = new HashMap<>();
		final Optional<String> ymktID = Optional.ofNullable(cart.getYmktTrackingId());
		final String originalUid = getUserOriginalUid(cart.getUser());
		final boolean isAnonymous = UserConstants.ANONYMOUS_CUSTOMER_UID.equals(originalUid) && !ymktID.isPresent();

		//<Property Name="Key" Type="Edm.String" Nullable="false" MaxLength="32" sap:unicode="false" sap:label="Interaction Key" sap:updatable="false"/>
		map.put("Key", cart.getCode());

		//<Property Name="CommunicationMedium" Type="Edm.String" MaxLength="20" sap:unicode="false" sap:label="Communication Medium" sap:updatable="false"/>
		map.put("CommunicationMedium", "ONLINE_SHOP");

		//<Property Name="ContactId" Type="Edm.String" MaxLength="255" sap:unicode="false" sap:label="Contact Id" sap:updatable="false"/>
		map.put("ContactId", isAnonymous ? null : ymktID.orElse(originalUid));

		//<Property Name="IsAnonymous" Type="Edm.Boolean" sap:unicode="false" sap:label="Is Anonymous" sap:updatable="false"/>
		map.put("IsAnonymous", Boolean.valueOf(isAnonymous));

		//<Property Name="ContactIdOrigin" Type="Edm.String" MaxLength="20" sap:unicode="false" sap:label="Contact Id Origin" sap:updatable="false"/>
		map.put("ContactIdOrigin", isAnonymous || ymktID.isPresent() ? null : UserContextService.getOriginIdSapHybrisConsumer());

		//<Property Name="ContentTitle" Type="Edm.String" MaxLength="255" sap:unicode="false" sap:label="Content Title" sap:updatable="false" sap:sortable="false"/>
		map.put("ContentTitle", cart.getCode());

		//<Property Name="InteractionType" Type="Edm.String" MaxLength="20" sap:unicode="false" sap:label="Interaction Type" sap:updatable="false"/>
		map.put("InteractionType", "SHOP_CART_ABANDONED");

		//<Property Name="SourceObjectId" Type="Edm.String" MaxLength="50" sap:unicode="false" sap:label="Source Object ID" sap:updatable="false"/>
		map.put("SourceObjectId", cart.getCode());

		//<Property Name="SourceSystemId" Type="Edm.String" MaxLength="255" sap:unicode="false" sap:label="Source System Id" sap:updatable="false"/>
		map.put("SourceSystemId", "SAP_MERCH_SHOP");

		//<Property Name="SourceSystemType" Type="Edm.String" MaxLength="20" sap:unicode="false" sap:label="Source System Type" sap:updatable="false"/>
		map.put("SourceSystemType", "COM");

		//<Property Name="Timestamp" Type="Edm.DateTime" Precision="7" sap:unicode="false" sap:label="Timestamp" sap:updatable="false"/>
		map.put("Timestamp", cart.getModifiedtime());

		//<Property Name="Amount" Type="Edm.Decimal" Precision="31" Scale="2" sap:unicode="false" sap:unit="Currency" sap:label="Amount" sap:updatable="false"/>
		map.put("Amount", cart.getTotalPrice());

		//<Property Name="Currency" Type="Edm.String" MaxLength="3" sap:unicode="false" sap:label="Currency Code" sap:updatable="false" sap:sortable="false" sap:semantics="currency-code"/>
		map.put("Currency", cart.getCurrency().getIsocode());

		//<NavigationProperty Name="Products" Relationship="CUAN_IMPORT_SRV.InteractionInteractionProducts" FromRole="FromRole_InteractionInteractionProducts" ToRole="ToRole_InteractionInteractionProducts" sap:label="Products"/>
		final List<Map<String, Object>> products = cart.getEntries().stream() //
				.map(CartEntryModel.class::cast) //
				.map(cartEntry -> this.convertCartEntryModelToMap(cartEntry, parameters)) //
				.collect(Collectors.toList());
		map.put("Products", products);
		return map;
	}

	@Override
	protected String getImportHeaderNavigationProperty()
	{
		return "Interactions";
	}

	@Required
	public void setProductURLService(ProductURLService productURLService)
	{
		this.productURLService = productURLService;
	}
}
