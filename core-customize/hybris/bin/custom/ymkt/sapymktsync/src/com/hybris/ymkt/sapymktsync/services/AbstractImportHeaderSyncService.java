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

import de.hybris.platform.core.model.ItemModel;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.olingo.odata2.api.edm.EdmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.common.http.HttpURLConnectionRequest;


/**
 * Send subclass of {@link ItemModel} to any yMKT service using ImportHeaders as entity set.
 * 
 * @param <M>
 *           Subclass of AbstractOrderModel
 */
public abstract class AbstractImportHeaderSyncService<M extends ItemModel> extends AbstractSyncService<M>
{
	protected static final String FORCE_SYNCHRONOUS_PROCESSING = "ForceSynchronousProcessing";

	protected static final SecureRandom GENERATOR = new SecureRandom();

	protected static final char[] HEX =
	{ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	protected static final String IMPORT_HEADERS = "ImportHeaders";
	protected static final String IMPORT_HEADER_ID = "ImportHeaderId";
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractImportHeaderSyncService.class);

	protected Map<String, Object> createImportHeader(Map<String, Object> parameters) throws EdmException, IOException
	{
		final Map<String, Object> map = new LinkedHashMap<>();

		map.put("Id", parameters.get(IMPORT_HEADER_ID));
		map.put("SourceSystemType", "COM");
		map.put("SourceSystemId", "SAP_MERCH_SHOP");

		if (Boolean.TRUE.equals(parameters.get(FORCE_SYNCHRONOUS_PROCESSING)) && //
				this.oDataService.getEntitySet(IMPORT_HEADERS) //
						.getEntityType().getPropertyNames().contains(FORCE_SYNCHRONOUS_PROCESSING))
		{
			map.put(FORCE_SYNCHRONOUS_PROCESSING, Boolean.TRUE);
		}

		return map;
	}

	protected String generateImportHeaderId()
	{
		// 128 bits upper case hexadecimal String
		final byte[] randomBytes = new byte[16];
		GENERATOR.nextBytes(randomBytes); // synchronized
		final char[] randomChars = new char[32];
		for (int i = 0; i < 16; i++)
		{
			final byte b = randomBytes[i];
			randomChars[i * 2 + 0] = HEX[b >>> 4 & 0x0F];
			randomChars[i * 2 + 1] = HEX[b & 0x0F];
		}
		return new String(randomChars);
	}

	/**
	 * @return ImportHeader's navigationProperty name of the model to send.
	 */
	protected abstract String getImportHeaderNavigationProperty();

	@Override
	public boolean sendModels(List<M> models, Map<String, Object> parameters)
	{
		try
		{
			final URL url = this.oDataService.createURL(IMPORT_HEADERS);
			final HttpURLConnectionRequest request = new HttpURLConnectionRequest("POST", url);
			request.getRequestProperties().put("Accept", APPLICATION_JSON);
			request.getRequestProperties().put("Content-Type", APPLICATION_JSON);
			request.setReadTimeout(this.getReadTimeout());

			parameters.putIfAbsent(IMPORT_HEADER_ID, this.generateImportHeaderId());

			request.getRequestProperties().put("SAP_ApplicationID", (String) parameters.get(IMPORT_HEADER_ID));

			final Map<String, Object> data = this.createImportHeader(parameters);

			final List<Map<String, Object>> importHeaderChildData = new ArrayList<>();
			for (M model : models)
			{
				importHeaderChildData.add(this.modelService.isNew(model) ? //
						this.convertModelDeletedToMap(model, parameters) : //
						this.convertModelToMap(model, parameters));
			}
			data.put(this.getImportHeaderNavigationProperty(), importHeaderChildData);

			final byte[] payload = this.oDataService.convertMapToJSONPayload(IMPORT_HEADERS, data);

			final byte[] payloadGZIP = this.compressGZIP(payload);
			request.getRequestProperties().put("Content-Encoding", "gzip");
			request.setPayload(payloadGZIP);
			LOG.debug("GZIP compression {} Bytes -> {} Bytes, {}% reduction.", payload.length, payloadGZIP.length,
					(float) 100.0 * (payload.length - payloadGZIP.length) / payload.length);

			this.oDataService.executeWithRetry(request);

			return true;
		}
		catch (IOException | EdmException e)
		{
			LOG.error("Error sending '{}' models to YMKT", models.size(), e);
			return false;
		}
	}



}
