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
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.olingo.odata2.api.client.batch.BatchChangeSet;
import org.apache.olingo.odata2.api.client.batch.BatchChangeSetPart;
import org.apache.olingo.odata2.api.edm.EdmEntityType;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.edm.EdmNavigationProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Send {@link CategoryModel} to API_MKT_PRODCAT_HIERARCHY_SRV
 */
public class Category1708SyncService extends Abstract1708SyncService<CategoryModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(Category1708SyncService.class);

	protected CommonI18NService commonI18NService;

	protected boolean enableParentCategory;

	@Override
	protected Map<String, Object> convertModelToMap(CategoryModel category, Map<String, Object> parameters) throws IOException
	{
		Map<String, Object> map = new LinkedHashMap<>();

		//<Property Name="ProductCategoryID" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Product Category ID"/>
		map.put("ProductCategoryID", category.getCode());
		//<Property Name="ProductHierarchyID" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Product Hierarchy ID"/>
		map.put("ProductHierarchyID", this.convertCategoryModelToHierarchyId(category));

		//<Property Name="ParentProductCategoryID" Type="Edm.String" Nullable="false" MaxLength="50" sap:unicode="false" sap:label="Parent Category ID"/>
		this.getParentCategory(category) //
				.filter(c -> this.enableParentCategory) //
				.ifPresent(c -> map.put("ParentProductCategoryID", c.getCode()));

		return map;
	}

	protected BatchChangeSet convertModelToChangeSet(Map<String, Object> parameters, CategoryModel category)
			throws IOException, EdmException
	{
		final Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", APPLICATION_JSON);
		headers.put("Accept", APPLICATION_JSON);
		headers.put("Sap-Cuan-SequenceId", "SAP_HYBRIS_PRODUCT_CATEGORY");
		headers.put("Sap-Cuan-RequestTimestamp",
				DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSSSSSS").withZone(ZoneId.of("UTC")).format(Instant.now()));

		final BatchChangeSet changeSet = BatchChangeSet.newBuilder().build();
		final boolean isDeleted = this.modelService.isNew(category);
		final Map<String, Object> productCategoryData = isDeleted ? //
				this.convertModelDeletedToMap(category, parameters) : //
				this.convertModelToMap(category, parameters);

		final String uri = this.oDataService.createResourcePath("ProductCategories", productCategoryData);

		final EdmEntityType entityTypeProductCategory = this.oDataService.getEntitySet("ProductCategories").getEntityType();

		changeSet.add(BatchChangeSetPart.method("MERGE") //
				.uri(uri) //
				.body(this.oDataService.convertMapToJSONString(entityTypeProductCategory, productCategoryData)) //
				.headers(headers) //
				.build());

		final EdmNavigationProperty property2 = (EdmNavigationProperty) entityTypeProductCategory
				.getProperty("ProductCategoryNames");
		final EdmEntityType entityTypeName = property2.getRelationship().getEnd2().getEntityType();

		for (LanguageModel lang : this.commonI18NService.getAllLanguages())
		{
			if (!Boolean.TRUE.equals(lang.getActive()))
			{
				continue;
			}
			final Locale loc = this.commonI18NService.getLocaleForLanguage(lang);
			final Map<String, Object> locMap = new LinkedHashMap<>();

			//<Property Name="Name" Type="Edm.String" MaxLength="120" sap:unicode="false" sap:label="Product Category Name"/>
			this.optionalPut(locMap, "Name", category, p -> p.getName(loc));

			//<Property Name="Description" Type="Edm.String" MaxLength="512" sap:unicode="false" sap:label="Product Category Description"/>
			this.optionalPut(locMap, "Description", category, //
					p -> p.getDescription(loc), //
					d -> d.replaceAll("\\<.*?\\>", ""));
			if (!locMap.isEmpty())
			{
				//<Property Name="Language" Type="Edm.String" Nullable="false" MaxLength="2" sap:unicode="false" sap:label="Language"/>
				locMap.put("Language", lang.getIsocode());

				//<NavigationProperty Name="ProductCategoryNames" Relationship="API_MKT_PRODCAT_HIERARCHY_SRV.ProductCategoryProductCategoryName" FromRole="FromRole_ProductCategoryProductCategoryName" ToRole="ToRole_ProductCategoryProductCategoryName"/>
				changeSet.add(BatchChangeSetPart.method("POST") //
						.uri(uri + "/ProductCategoryNames") //
						.body(this.oDataService.convertMapToJSONString(entityTypeName, locMap)) //
						.headers(headers) //
						.build());
			}

		}

		return changeSet;
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

	@Required
	public void setCommonI18NService(CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}

	@Required
	public void setEnableParentCategory(boolean enableParentCategory)
	{
		this.enableParentCategory = enableParentCategory;
	}
}
