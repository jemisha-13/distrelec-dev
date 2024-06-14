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

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.client.batch.BatchChangeSet;
import org.apache.olingo.odata2.api.client.batch.BatchChangeSetPart;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.product.ProductURLService;


/**
 * Send {@link ProductModel} to API_MKT_PRODUCT_SRV
 */
public class Product1708SyncService extends Abstract1708SyncService<ProductModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(Product1708SyncService.class);

	protected CommonI18NService commonI18NService;
	protected ProductURLService productURLService;

	protected Map<String, Object> convertCategoryModelToMap(final CategoryModel category)
	{
		final Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="ProductCategoryHierarchyID" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Product Category Hierarchy ID" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
		map.put("ProductCategoryHierarchyID", this.convertCategoryModelToHierarchyId(category));

		//<Property Name="ProductCategoryID" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Product Category ID" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
		map.put("ProductCategoryID", category.getCode());

		return map;
	}

	protected BatchChangeSet convertModelToChangeSet(Map<String, Object> parameters, ProductModel product)
			throws IOException, EdmException
	{
		final Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", APPLICATION_JSON);
		headers.put("Accept", APPLICATION_JSON);
		headers.put("Sap-Cuan-SequenceId", "SAP_HYBRIS_PRODUCT");
		headers.put("Sap-Cuan-RequestTimestamp", DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSSSSS").withZone(ZoneId.of("UTC")).format(Instant.now()));

		final BatchChangeSet changeSet = BatchChangeSet.newBuilder().build();
		final boolean isDeleted = this.modelService.isNew(product);
		final Map<String, Object> productData = isDeleted ? //
				this.convertModelDeletedToMap(product, parameters) : //
				this.convertModelToMap(product, parameters);

		final String uri = this.oDataService.createResourcePath("ProductOriginDataSet", productData);

		final EdmEntityType entityTypeProduct = this.oDataService.getEntitySet("ProductOriginDataSet").getEntityType();

		changeSet.add(BatchChangeSetPart.method("MERGE") //
				.uri(uri) //
				.body(this.oDataService.convertMapToJSONString(entityTypeProduct, productData)) //
				.headers(headers) //
				.build());

		final EdmNavigationProperty property = (EdmNavigationProperty) entityTypeProduct.getProperty("ProductCategoryAssignments");
		final EdmEntityType entityTypeCategory = property.getRelationship().getEnd2().getEntityType();

		for (Map<String, Object> categoryData : product.getSupercategories().stream() //
				.map(this::convertCategoryModelToMap).collect(Collectors.toList()))
		{
			changeSet.add(BatchChangeSetPart.method("POST") //
					.uri(uri + "/ProductCategoryAssignments") //
					.body(this.oDataService.convertMapToJSONString(entityTypeCategory, categoryData)) //
					.headers(headers) //
					.build());
		}

		final EdmNavigationProperty property2 = (EdmNavigationProperty) entityTypeProduct.getProperty("ProductNames");
		final EdmEntityType entityTypeName = property2.getRelationship().getEnd2().getEntityType();

		for (LanguageModel lang : this.commonI18NService.getAllLanguages())
		{
			if (!Boolean.TRUE.equals(lang.getActive()))
			{
				continue;
			}
			final Locale loc = this.commonI18NService.getLocaleForLanguage(lang);
			final Map<String, Object> locMap = new LinkedHashMap<>();

			//<Property Name="Name" Type="Edm.String" Nullable="false" MaxLength="120" sap:unicode="false" sap:label="Product Name" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
			this.optionalPut(locMap, "Name", product, p -> p.getName(loc));

			//<Property Name="ProductDescription" Type="Edm.String" Nullable="false" MaxLength="512" sap:unicode="false" sap:label="Product Description" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
			this.optionalPut(locMap, "ProductDescription", product, //
					p -> p.getDescription(loc), //
					d -> d.replaceAll("\\<.*?\\>", ""));
			if (!locMap.isEmpty())
			{
				//<Property Name="Language" Type="Edm.String" Nullable="false" MaxLength="2" sap:unicode="false" sap:label="Language" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
				locMap.put("Language", lang.getIsocode());

				//<NavigationProperty Name="ProductNames" Relationship="API_MKT_PRODUCT_SRV.ProductOriginDataProductOriginDataName" FromRole="FromRole_ProductOriginDataProductOriginDataName" ToRole="ToRole_ProductOriginDataProductOriginDataName"/>
				changeSet.add(BatchChangeSetPart.method("POST") //
						.uri(uri + "/ProductNames") //
						.body(this.oDataService.convertMapToJSONString(entityTypeName, locMap)) //
						.headers(headers) //
						.build());
			}

		}

		return changeSet;
	}

	@Override
	protected Map<String, Object> convertModelToMap(final ProductModel product, final Map<String, Object> parameters)
			throws IOException
	{
		final Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="ProductOrigin" Type="Edm.String" Nullable="false" MaxLength="30" sap:unicode="false" sap:label="Origin of Product" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
		map.put("ProductOrigin", "SAP_HYBRIS_PRODUCT");
		//<Property Name="ProductID" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Product ID" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
		map.put("ProductID", product.getCode());

		//<Property Name="ProductImageURL" Type="Edm.String" Nullable="false" MaxLength="1333" sap:unicode="false" sap:label="Product Image URL" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
		this.productURLService.getProductImageURL(product).ifPresent(img -> map.put("ProductImageURL", img));

		//<Property Name="WebsiteURL" Type="Edm.String" Nullable="false" MaxLength="1333" sap:unicode="false" sap:label="Website URL" sap:creatable="false" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
		map.put("WebsiteURL", this.productURLService.getProductURL(product));
		
		//<Property Name="ProductValidEndDate" sap:label="Valid To" sap:unicode="false" Type="Edm.DateTime" Precision="0"/>
		map.put("ProductValidEndDate", product.getOfflineDate());

		return map;
	}

	@Required
	public void setCommonI18NService(CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Required
	public void setProductURLService(ProductURLService productURLService)
	{
		this.productURLService = productURLService;
	}
}
