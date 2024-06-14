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

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Send {@link CategoryModel} to CUAN_IMPORT_SRV
 */
public class CategorySyncService extends AbstractImportHeaderSyncService<CategoryModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(CategorySyncService.class);

	protected boolean enableParentCategory;

	@Override
	protected Map<String, Object> convertModelToMap(CategoryModel category, Map<String, Object> parameters) throws IOException
	{
		Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="Id" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Object ID" sap:updatable="false"/>
		map.put("Id", category.getCode());
		//<Property Name="HierarchyId" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Object ID" sap:updatable="false"/>
		map.put("HierarchyId", this.convertCategoryModelToHierarchyId(category));

		//<Property Name="ParentCategoryId" Type="Edm.String" MaxLength="50" sap:unicode="false" sap:label="Object ID" sap:updatable="false"/>
		this.getParentCategory(category) //
				.filter(c -> this.enableParentCategory) //
				.ifPresent(c -> map.put("ParentCategoryId", c.getCode()));

		//<Property Name="Language" Type="Edm.String" MaxLength="2" sap:unicode="false" sap:label="Language" sap:updatable="false"/>
		map.put("Language", this.getLanguage(parameters));

		//<Property Name="Name" Type="Edm.String" MaxLength="40" sap:unicode="false" sap:label="Product Categry Name" sap:updatable="false"/>
		Optional.ofNullable(category.getName(this.getLocale(parameters))) //
				.ifPresent(n -> map.put("Name", n));

		//<Property Name="Description" Type="Edm.String" MaxLength="512" sap:unicode="false" sap:label="Prod. Cat. Desc." sap:updatable="false"/>
		Optional.ofNullable(category.getDescription(this.getLocale(parameters))) //
				.ifPresent(d -> map.put("Description", d.replaceAll("\\<.*?\\>", "")));

		return map;
	}

	protected Optional<CategoryModel> getParentCategory(CategoryModel category)
	{
		final List<CategoryModel> supercategories = category.getSupercategories().stream() //
				.filter(supCat -> category.getCatalogVersion().equals(supCat.getCatalogVersion())) //
				.collect(Collectors.toList());

		// Categories in yCOM can have many parent categories.
		// Categories in yMKT can have only one parent category.
		final CatalogVersionModel cv = category.getCatalogVersion();
		final Comparator<CatalogVersionModel> cvc = (cv1, cv2) -> cv.equals(cv1) && cv.equals(cv2) ? 0 : cv.equals(cv1) ? -1 : 1;
		final Comparator<CategoryModel> c3 = Comparator.comparing(CategoryModel::getCatalogVersion, cvc);
		final Comparator<String> cSize = Comparator.comparing(String::length).thenComparing(Comparator.naturalOrder());
		final Comparator<CategoryModel> c4 = c3.thenComparing(Comparator.comparing(CategoryModel::getCode, cSize));
		final Optional<CategoryModel> min = supercategories.stream().min(c4);

		LOG.debug("Selected parent category '{}' from supercategories='{}' for category '{}'.", min, supercategories, category);

		return min;
	}

	@Override
	protected String getImportHeaderNavigationProperty()
	{
		return "ProductCategories";
	}

	@Required
	public void setEnableParentCategory(boolean enableParentCategory)
	{
		this.enableParentCategory = enableParentCategory;
	}
}
