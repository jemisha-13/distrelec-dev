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

import de.hybris.deltadetection.ChangeDetectionService;
import de.hybris.deltadetection.ChangesCollector;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.deltadetection.StreamConfiguration;
import de.hybris.deltadetection.impl.InMemoryChangesCollector;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;


/**
 * Consumption wrapper for {@link ChangeDetectionService} for needs of <b>sapymktsync</b>.
 */
public class SyncChangeDetectionService
{
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(SyncChangeDetectionService.class);

	protected ChangeDetectionService changeDetectionService;
	protected TypeService typeService;

	/**
	 * @param modelClass
	 * @param configuration
	 * @return {@link List} of {@link ItemChangeDTO}
	 * @see ChangeDetectionService#collectChangesForType(ComposedTypeModel, StreamConfiguration, ChangesCollector)
	 */
	public List<ItemChangeDTO> collectChanges(Class<? extends ItemModel> modelClass, StreamConfiguration configuration)
	{
		final InMemoryChangesCollector collector = new InMemoryChangesCollector();
		final ComposedTypeModel composedType = this.typeService.getComposedTypeForClass(modelClass);
		this.changeDetectionService.collectChangesForType(composedType, configuration, collector);
		return collector.getChanges();
	}

	/**
	 * @param changes
	 * @see ChangeDetectionService#consumeChanges(List)
	 */
	public void consumeChanges(List<ItemChangeDTO> changes)
	{
		if (!changes.isEmpty())
		{
			this.changeDetectionService.consumeChanges(changes);
		}
	}

	@Required
	public void setChangeDetectionService(final ChangeDetectionService changeDetectionService)
	{
		this.changeDetectionService = changeDetectionService;
	}

	@Required
	public void setTypeService(final TypeService typeService)
	{
		this.typeService = typeService;
	}

	/**
	 * Re-create an identical {@link ItemChangeDTO} with the info field updated.
	 * 
	 * @param change
	 *           {@link ItemChangeDTO} source.
	 * @param info
	 *           new info value to update.
	 * @return new instance of {@link ItemChangeDTO} with the info field updated.
	 */
	public ItemChangeDTO updateInfo(ItemChangeDTO change, String info)
	{
		final ItemChangeDTO newChange = new ItemChangeDTO(change.getItemPK(), //
				change.getVersion(), //
				change.getChangeType(), //
				info, //
				change.getItemComposedType(), //
				change.getStreamId());
		return newChange.withVersionValue(change.getVersionValue());
	}
}
