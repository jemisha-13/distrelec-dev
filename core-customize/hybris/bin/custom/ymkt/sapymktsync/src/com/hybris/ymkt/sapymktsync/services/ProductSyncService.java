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
import de.hybris.platform.core.model.product.ProductModel;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.product.ProductURLService;


/**
 * Send {@link ProductModel} to CUAN_IMPORT_SRV
 */
public class ProductSyncService extends AbstractImportHeaderSyncService<ProductModel>
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ProductSyncService.class);
	protected ProductURLService productURLService;
	

	protected Map<String, Object> convertCategoryModelToMap(final CategoryModel category)
	{
		final Map<String, Object> map = new LinkedHashMap<>();

		// Only the link between product & category is sent.

		//<Property Name="Id" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Object ID" sap:updatable="false"/>
		map.put("Id", category.getCode());

		//<Property Name="HierarchyId" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Object ID" sap:updatable="false"/>
		map.put("HierarchyId", this.convertCategoryModelToHierarchyId(category));

		// Remaining properties are not sent, they are covered by CategorySyncService.

		//<Property Name="ParentCategoryId" Type="Edm.String" MaxLength="50" sap:unicode="false" sap:label="Object ID" sap:updatable="false"/>
		//<Property Name="Language" Type="Edm.String" MaxLength="2" sap:unicode="false" sap:label="Language" sap:updatable="false"/>
		//<Property Name="Name" Type="Edm.String" MaxLength="40" sap:unicode="false" sap:label="Product Categry Name" sap:updatable="false"/>
		//<Property Name="Description" Type="Edm.String" MaxLength="512" sap:unicode="false" sap:label="Prod. Cat. Desc." sap:updatable="false"/>

		return map;
	}
	@Override
	protected Map<String, Object> convertModelToMap(final ProductModel product, final Map<String, Object> parameters)
			throws IOException
	{
		final Map<String, Object> map = new LinkedHashMap<>();
		final Locale loc = this.getLocale(parameters);

		//<Property Name="Id" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Product ID" sap:updatable="false"/>
		map.put("Id", product.getCode());
		//<Property Name="IdOrigin" Type="Edm.String" Nullable="false" MaxLength="30" sap:unicode="false" sap:label="Origin of Product" sap:updatable="false"/>
		map.put("IdOrigin", "SAP_HYBRIS_PRODUCT");

		//<Property Name="Language" Type="Edm.String" MaxLength="2" sap:unicode="false" sap:label="Language" sap:updatable="false"/>
		map.put("Language", this.getLanguage(parameters));

		//<Property Name="Name" Type="Edm.String" MaxLength="120" sap:unicode="false" sap:label="Product Name" sap:updatable="false"/>
		this.optionalPut(map, "Name", product, p -> p.getName(loc));

		//<Property Name="Description" Type="Edm.String" MaxLength="512" sap:unicode="false" sap:label="Product Desc." sap:updatable="false"/>
		this.optionalPut(map, "Description", product, //
				p -> p.getDescription(loc), //
				d -> d.replaceAll("\\<.*?\\>", ""));

		//<Property Name="ImageUrl" Type="Edm.String" MaxLength="1333" sap:unicode="false" sap:label="Short URI" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
		this.productURLService.getProductImageURL(product).ifPresent(img -> map.put("ImageUrl", img));

		//<Property Name="NavigationURL" Type="Edm.String" MaxLength="1333" sap:unicode="false" sap:label="Short URI" sap:updatable="false" sap:sortable="false" sap:filterable="false"/>
		map.put("NavigationURL", this.productURLService.getProductURL(product));

		final Collection<CategoryModel> supercategories = product.getSupercategories();
		if (supercategories.isEmpty())
		{
			// No mapping
		}
		else if (supercategories.size() == 1)
		{
			// Shortcut mapping
			final CategoryModel category = supercategories.iterator().next();
			//<Property Name="CategoryId" Type="Edm.String" MaxLength="50" sap:unicode="false" sap:label="Object ID" sap:updatable="false"/>
			map.put("CategoryId", category.getCode());
			//<Property Name="HierarchyId" Type="Edm.String" MaxLength="50" sap:unicode="false" sap:label="Object ID" sap:updatable="false"/>
			map.put("HierarchyId", this.convertCategoryModelToHierarchyId(category));
		}
		else
		{
			final List<Map<String, Object>> categories = supercategories.stream() //
					.map(this::convertCategoryModelToMap) //
					.collect(Collectors.toList());
			//<NavigationProperty Name="ProductCategories" Relationship="CUAN_IMPORT_SRV.ProductProductCategories" FromRole="FromRole_ProductProductCategories" ToRole="ToRole_ProductProductCategories" sap:label="Product Categories"/>
			map.put("ProductCategories", categories);
		}

		return map;
	}

	@Override
	protected String getImportHeaderNavigationProperty()
	{
		return "Products";
	}

	@Required
	public void setProductURLService(ProductURLService productURLService)
	{
		this.productURLService = productURLService;
	}
}
