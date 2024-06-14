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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.edm.EdmException;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderException;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.odata.ODataService;


/**
 * This service is used by Fiori application to see imported massages.<br>
 * It is used for validation process.
 */
public class ImportMessageService
{
	protected static final String APPLICATION_JSON = "application/json";

	protected static final String IMPORT_HEADERS = "ImportHeaders";

	private static final Logger LOG = LoggerFactory.getLogger(ImportMessageService.class);

	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();

	protected ODataService oDataService;

	/**
	 * @param importHeaderId
	 * @return {@link Optional} of {@link String} containing the complete error detail if the importHeaderId could be
	 *         read and has error. {@link Optional#empty()} otherwise.
	 */
	public Optional<String> readImportHeaderErrors(final String importHeaderId)
	{
		try
		{
			final URL url = this.oDataService.createURL(IMPORT_HEADERS, //
					"$filter", this.oDataService.filter(IMPORT_HEADERS).on("RefMessageId").eq(importHeaderId).toExpression(), //
					"$expand", "Messages");
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);
			request.getRequestProperties().put("Accept", APPLICATION_JSON);
			final HttpURLConnectionResponse response = this.oDataService.executeWithRetry(request);

			final EdmEntitySet entitySet = this.oDataService.getEntitySet(IMPORT_HEADERS);
			final byte[] payload = response.getPayload();
			final InputStream content = new ByteArrayInputStream(payload);
			final ODataFeed feed = EntityProvider.readFeed("application/json", entitySet, content, NO_READ_PROPERTIES);

			if (feed.getEntries().stream().map(ODataEntry::getProperties) //
					.map(m -> m.get("FailedRecordsCount")) //
					.allMatch(Integer.valueOf(0)::equals))
			{
				return Optional.empty();
			}

			final String json = new String(response.getPayload(), StandardCharsets.UTF_8);
			final JsonObject jo = new Gson().fromJson(json, JsonObject.class);
			final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
			return Optional.of(gson.toJson(jo));
		}
		catch (IOException | EdmException | EntityProviderException e)
		{
			LOG.error("Error reading ImportHeader '{}' ", importHeaderId, e);
			return Optional.empty();
		}
	}

	@Required
	public void setODataService(ODataService oDataService)
	{
		this.oDataService = oDataService;
	}
}
