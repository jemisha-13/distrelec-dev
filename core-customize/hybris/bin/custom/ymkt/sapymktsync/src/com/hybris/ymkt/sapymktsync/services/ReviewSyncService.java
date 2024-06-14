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

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.customerreview.model.CustomerReviewModel;
import de.hybris.platform.servicelayer.user.UserConstants;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.product.ProductURLService;
import com.hybris.ymkt.common.user.UserContextService;

import static com.hybris.ymkt.sapymktsync.util.UserUtil.getUserOriginalUid;


/**
 * Send {@link CustomerReviewModel} to CUAN_IMPORT_SRV
 */
public class ReviewSyncService extends AbstractImportHeaderSyncService<CustomerReviewModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ReviewSyncService.class);
	protected ProductURLService productURLService;

	@Override
	protected Map<String, Object> convertModelToMap(CustomerReviewModel review, Map<String, Object> parameters) throws IOException
	{
		final Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="Key" Type="Edm.String" Nullable="false" MaxLength="32" sap:unicode="false" sap:label="Interaction Key" sap:updatable="false"/>

		//<Property Name="CommunicationMedium" Type="Edm.String" MaxLength="20" sap:unicode="false" sap:label="Communication Medium" sap:updatable="false"/>
		map.put("CommunicationMedium", "ONLINE_SHOP");

		final String userId = getUserOriginalUid(review.getUser());
		if (!UserConstants.ANONYMOUS_CUSTOMER_UID.equals(userId))
		{
			//<Property Name="ContactId" Type="Edm.String" MaxLength="255" sap:unicode="false" sap:label="Contact Id" sap:updatable="false"/>
			map.put("ContactId", userId);
			//<Property Name="ContactIdOrigin" Type="Edm.String" MaxLength="20" sap:unicode="false" sap:label="Contact Id Origin" sap:updatable="false"/>
			map.put("ContactIdOrigin", UserContextService.getOriginIdSapHybrisConsumer());
		}

		//<Property Name="ContentData" Type="Edm.String" sap:unicode="false" sap:label="Content Data" sap:updatable="false" sap:sortable="false"/>
		map.put("ContentData", review.getComment());

		//<Property Name="ContentTitle" Type="Edm.String" MaxLength="255" sap:unicode="false" sap:label="Content Title" sap:updatable="false" sap:sortable="false"/>
		map.put("ContentTitle", review.getHeadline());

		//<Property Name="InteractionType" Type="Edm.String" MaxLength="20" sap:unicode="false" sap:label="Interaction Type" sap:updatable="false"/>
		map.put("InteractionType", "PROD_REVIEW_CREATED");

		//<Property Name="IsAnonymous" Type="Edm.Boolean" sap:unicode="false" sap:label="Is Anonymous" sap:updatable="false"/>
		map.put("IsAnonymous", Boolean.valueOf("anonymous".equals(userId)));

		//<Property Name="SourceDataUrl" Type="Edm.String" sap:unicode="false" sap:label="Source Data URL" sap:updatable="false" sap:sortable="false"/>
		map.put("SourceDataUrl", this.productURLService.getProductURL(review.getProduct()).concat("#tabreview"));

		//<Property Name="SourceObjectId" Type="Edm.String" MaxLength="50" sap:unicode="false" sap:label="Source Object ID" sap:updatable="false"/>
		map.put("SourceObjectId", review.getProduct().getCode());

		//<Property Name="SourceObjectType" Type="Edm.String" MaxLength="30" sap:unicode="false" sap:label="Source Object Type" sap:updatable="false"/>

		//<Property Name="SourceSystemId" Type="Edm.String" MaxLength="255" sap:unicode="false" sap:label="Source System Id" sap:updatable="false"/>
		map.put("SourceSystemId", "SAP_MERCH_SHOP");

		//<Property Name="SourceSystemType" Type="Edm.String" MaxLength="20" sap:unicode="false" sap:label="Source System Type" sap:updatable="false"/>
		map.put("SourceSystemType", "COM");

		//<Property Name="Timestamp" Type="Edm.DateTime" Precision="7" sap:unicode="false" sap:label="Timestamp" sap:updatable="false"/>
		map.put("Timestamp", review.getModifiedtime());

		//<Property Name="Valuation" Type="Edm.Int16" sap:unicode="false" sap:label="Valuation" sap:updatable="false"/>
		map.put("Valuation", review.getRating());

		//<NavigationProperty Name="Products" Relationship="CUAN_IMPORT_SRV.InteractionInteractionProducts" FromRole="FromRole_InteractionInteractionProducts" ToRole="ToRole_InteractionInteractionProducts" sap:label="Products"/>
		map.put("Products", this.convertProductModelToMap(review.getProduct()));

		return map;
	}

	protected Map<String, Object> convertProductModelToMap(ProductModel product)
	{
		final Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="ItemId" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Product ID" sap:updatable="false"/>
		map.put("ItemId", product.getCode());

		//<Property Name="ItemType" Type="Edm.String" Nullable="false" MaxLength="30" sap:unicode="false" sap:label="Origin of Product" sap:updatable="false"/>
		map.put("ItemType", "SAP_HYBRIS_PRODUCT");

		//<Property Name="SourceSystemId" Type="Edm.String" MaxLength="255" sap:unicode="false" sap:label="Source Syst. ID" sap:updatable="false"/>
		map.put("SourceSystemId", "SAP_MERCH_SHOP");

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
