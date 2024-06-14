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
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.common.odata.ODataService;


/**
 * Base class for sending ECP ItemModel to yMKT.<br>
 * Provide a collection of methods used by multiple OData services and ItemModels.
 * 
 * @param <M>
 *           Subclass of ItemModel to send.
 * @see #sendModels(List, Map)
 */
public abstract class AbstractSyncService<M extends ItemModel>
{
	protected static final String APPLICATION_JSON = "application/json";

	protected static final Function<String, Locale> LOCALE_NEW = Locale::new;
	protected static final ConcurrentHashMap<String, Locale> LOCALES = new ConcurrentHashMap<>();

	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSyncService.class);

	protected ModelService modelService;
	protected ODataService oDataService;

	protected byte[] compressGZIP(final byte[] payload) throws IOException
	{
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
		{
			try (GZIPOutputStream gzos = new GZIPOutputStream(baos))
			{
				gzos.write(payload);
			}
			return baos.toByteArray();
		}
	}

	protected String convertCategoryModelToHierarchyId(final CategoryModel category)
	{
		final CatalogVersionModel catalogVersion = category.getCatalogVersion();
		return catalogVersion.getCatalog().getId() + ':' + catalogVersion.getVersion();
	}

	// this.modelService.isNew(model) == true, not persisted.
	protected Map<String, Object> convertModelDeletedToMap(M model, Map<String, Object> parameters) throws IOException
	{
		throw new IllegalStateException("Not implemented !");
	}

	// this.modelService.isNew(model) == false, was persisted.
	/**
	 * @param model
	 * @param parameters
	 * @return {@link Map} of &lt;{@link String}, {@link Object}&gt; containing the OData values representing the model.
	 * @throws IOException
	 */
	protected abstract Map<String, Object> convertModelToMap(M model, Map<String, Object> parameters) throws IOException;

	protected String getLanguage(Map<String, Object> parameters)
	{
		return Optional.ofNullable(parameters.get("language")).map(String.class::cast).orElse("en");
	}

	protected Locale getLocale(Map<String, Object> parameters)
	{
		final String language = this.getLanguage(parameters);
		return LOCALES.computeIfAbsent(language, LOCALE_NEW);
	}

	protected int getReadTimeout()
	{
		return 300000; // 5 Minutes
	}

	protected <I extends ItemModel> void optionalPut(final Map<String, Object> map, final String key, final I model,
			final Function<I, ?> extraction)
	{
		Optional.of(model).map(extraction).ifPresent(o -> map.put(key, o));
	}

	protected <I extends ItemModel, T> void optionalPut(final Map<String, Object> map, final String key, final I model,
			final Function<I, T> extraction, final Function<T, ?> extraction2)
	{
		Optional.of(model).map(extraction).map(extraction2).ifPresent(o -> map.put(key, o));
	}

	protected <I extends ItemModel, J, T> void optionalPut(final Map<String, Object> map, final String key, final I model,
			final Function<I, J> extraction, final Function<J, T> extraction2, final Function<T, ?> extraction3)
	{
		Optional.of(model).map(extraction).map(extraction2).map(extraction3).ifPresent(o -> map.put(key, o));
	}

	/**
	 * @param models
	 * @param parameters
	 * @return true if success, false otherwise.
	 */
	public abstract boolean sendModels(List<M> models, Map<String, Object> parameters);

	@Required
	public void setModelService(ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Required
	public void setODataService(ODataService oDataService)
	{
		this.oDataService = oDataService;
	}
}
