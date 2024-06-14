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
package com.hybris.ymkt.sapymktsync.jobs;

import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.deltadetection.StreamConfiguration;
import de.hybris.deltadetection.enums.ChangeType;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.AbstractSyncService;


public abstract class AbstractChildSyncJob<T extends SyncCronJobModel, M extends ItemModel, C extends ItemModel, S extends AbstractSyncService<M>>
		extends AbstractSyncJob<T, M, S>
{
	private static final Logger LOG = LoggerFactory.getLogger(AbstractChildSyncJob.class);

	/**
	 * Suggested implementation :<br>
	 * <br>
	 * <code>return MyChildModel.class;</code>
	 * 
	 * @return The child model's {@link Class} to collect to track parent model to send.
	 */
	protected abstract Class<C> getChildClass();

	/**
	 * @param cronJob
	 * @return {@link StreamConfiguration}
	 */
	protected StreamConfiguration getChildConfiguration(final T cronJob)
	{
		final String suffix = this.getChildClass().getSimpleName().replace("Model$", "");
		final StreamConfiguration configuration = StreamConfiguration.buildFor(cronJob.getCode() + suffix);
		this.getChildStreamConfigurationItemSelector(cronJob).ifPresent(configuration::withItemSelector);
		return configuration.withParameters(this.getStreamConfigurationParameters(cronJob));
	}

	/**
	 * @param cronJob
	 * @return
	 * @see StreamConfiguration#withItemSelector(String)
	 */
	protected Optional<String> getChildStreamConfigurationItemSelector(final T cronJob)
	{
		return Optional.empty();
	}

	/**
	 * @return {@link Function} mapping a child model to the main model to be send.
	 */
	protected abstract Function<C, M> getParentFunction();

	@Override
	protected Map<Long, SyncJobBatchEntry<M>> collectChanges(final T cronJob)
	{
		final Map<Long, SyncJobBatchEntry<M>> changes = super.collectChanges(cronJob);

		final SyncJobBatchEntry<M> failedEntry = new SyncJobBatchEntry<>(INVALID_PK);

		final StreamConfiguration childConfig = this.getChildConfiguration(cronJob);
		for (final ItemChangeDTO change : this.syncChangeDetectionService.collectChanges(this.getChildClass(), childConfig))
		{
			if (ChangeType.DELETED == change.getChangeType())
			{
				try
				{
					final Long key = Long.parseLong(change.getInfo());
					changes.computeIfAbsent(key, SyncJobBatchEntry::new).addChildChange(change);
				}
				catch (NumberFormatException e)
				{
					failedEntry.addChildChange(change);
					LOG.error(
							"Invalid child info='{}', this shall be a numeric pk, failed because of {}. ItemChangeDTOs will be ignored and consumed.",
							change, e, null);
				}
			}
			else
			{
				try
				{
					final Long itemPK = change.getItemPK();
					final PK pk = PK.fromLong(itemPK);
					final C childModel = this.modelService.get(pk);
					final M model = this.getParentFunction().apply(childModel);
					final Long key = model.getPk().getLong();
					final ItemChangeDTO changeUpdated = this.syncChangeDetectionService.updateInfo(change, key.toString());
					changes.computeIfAbsent(key, SyncJobBatchEntry::new).addChildChange(changeUpdated);
				}
				catch (ModelLoadingException e)
				{
					failedEntry.addChildChange(change);
					LOG.error("Unable to read childModel from '{}' because of {}. ItemChangeDTOs will be ignored and consumed.",
							change, e, null);
				}
			}
		}

		if (!failedEntry.getAllChanges().isEmpty())
		{
			changes.put(INVALID_PK, failedEntry);
		}

		return changes;
	}
}
