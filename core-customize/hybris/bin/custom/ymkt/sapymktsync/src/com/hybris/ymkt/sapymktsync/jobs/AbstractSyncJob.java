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
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelLoadingException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.AbstractSyncService;
import com.hybris.ymkt.sapymktsync.services.ImportMessageService;
import com.hybris.ymkt.sapymktsync.services.SyncChangeDetectionService;


/**
 * Base job class for collecting changes from extension <b>deltadetection</b> and sending the models to Hybris
 * Marketing.
 * 
 * @param <T>
 *           SyncCronJobModel.
 * @param <M>
 *           ItemModel. Main model sent to Hybris Marketing.
 * @param <S>
 *           AbstractSyncService. Service responsible for sending the models to Hybris Marketing.
 */
public abstract class AbstractSyncJob<T extends SyncCronJobModel, M extends ItemModel, S extends AbstractSyncService<M>>
		extends AbstractJobPerformable<T>
{
	protected class BatchBuilder implements Iterable<List<SyncJobBatchEntry<M>>>, Iterator<List<SyncJobBatchEntry<M>>>
	{
		protected final int batchSize;
		protected final Queue<SyncJobBatchEntry<M>> entries;
		protected final List<SyncJobBatchEntry<M>> entriesNext = new ArrayList<>();
		protected final List<SyncJobBatchEntry<M>> entriesNoModel = new ArrayList<>();
		protected boolean success = true;

		protected BatchBuilder(final Collection<SyncJobBatchEntry<M>> entries, final int batchSize)
		{
			this.entries = new ArrayDeque<>(entries);
			this.batchSize = batchSize;
		}

		protected List<SyncJobBatchEntry<M>> getEntriesNoModel()
		{
			final List<SyncJobBatchEntry<M>> noModel = new ArrayList<>(this.entriesNoModel);
			this.entriesNoModel.clear();
			return noModel;
		}

		@Override
		public boolean hasNext()
		{
			this.prepareNextBatch();
			return !this.entriesNext.isEmpty();
		}

		@Override
		public Iterator<List<SyncJobBatchEntry<M>>> iterator()
		{
			return this;
		}

		@Override
		public List<SyncJobBatchEntry<M>> next()
		{
			if (!this.hasNext())
			{
				throw new NoSuchElementException();
			}
			final List<SyncJobBatchEntry<M>> next = new ArrayList<>(this.entriesNext);
			this.entriesNext.clear();
			return next;
		}

		protected boolean populateModel(final SyncJobBatchEntry<M> entry)
		{
			final Optional<Function<String, M>> itemModelDeserializer = AbstractSyncJob.this.getItemModelDeserializer();
			boolean deleted = entry.getChange().map(ItemChangeDTO::getChangeType).filter(ChangeType.DELETED::equals).isPresent();

			if (deleted && !itemModelDeserializer.isPresent())
			{
				// Deleted ItemChangeDTO are ignored when deletion are not sent. (restoreFunction missing)
				return false;
			}

			if (deleted && itemModelDeserializer.isPresent() && entry.getInfo().isPresent())
			{
				final M model = itemModelDeserializer.get().apply(entry.getInfo().get());
				AbstractSyncJob.this.modelService.detach(model);
				entry.setModel(model);
				return true;
			}

			final ModelLoadingException modelLoadingException;
			try
			{
				final Long itemPK = entry.getItemPK();
				final PK pk = PK.fromLong(itemPK);
				final M model = AbstractSyncJob.this.modelService.get(pk);
				entry.setModel(model);

				return true;
			}
			catch (ModelLoadingException e)
			{
				modelLoadingException = e;
			}

			// The root model does not exists in the database nor can be re-created from info field.
			LOG.warn("Unable to read nor create ItemModel from '{}' because of {}. ItemChangeDTOs will be ignored and consumed.",
					entry.getAllChanges(), modelLoadingException, modelLoadingException);
			success = false;
			return false;
		}

		protected void prepareNextBatch()
		{
			if (!this.entriesNext.isEmpty())
			{
				return;
			}

			while (!entries.isEmpty() && this.entriesNext.size() < this.batchSize)
			{
				final SyncJobBatchEntry<M> entry = entries.remove();
				if (this.populateModel(entry))
				{
					this.entriesNext.add(entry);
				}
				else
				{
					this.entriesNoModel.add(entry);
				}
			}
		}
	}

	protected static final Long INVALID_PK = Long.valueOf(-1L);

	protected static final Optional<String> ITEM_SELECTOR_CATALOG_VERSION = Optional
			.of("{catalogVersion}=?cronJob.catalogVersion");

	private static final Logger LOG = LoggerFactory.getLogger(AbstractSyncJob.class);

	protected static final PerformResult RESULT_FAILURE_ABORTED = new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
	protected static final PerformResult RESULT_FAILURE_FINISHED = new PerformResult(CronJobResult.FAILURE,
			CronJobStatus.FINISHED);
	protected static final PerformResult RESULT_SUCCESS_ABORTED = new PerformResult(CronJobResult.SUCCESS, CronJobStatus.ABORTED);
	protected static final PerformResult RESULT_SUCCESS_FINISHED = new PerformResult(CronJobResult.SUCCESS,
			CronJobStatus.FINISHED);

	protected ImportMessageService importMessageService;
	protected SyncChangeDetectionService syncChangeDetectionService;
	protected S syncService;

	protected Map<Long, SyncJobBatchEntry<M>> collectChanges(final T cronJob)
	{
		final StreamConfiguration modelConfig = this.getConfiguration(cronJob);
		final List<ItemChangeDTO> changes = this.syncChangeDetectionService.collectChanges(this.getModelClass(), modelConfig);

		if (!this.getItemModelSerializer().isPresent())
		{
			return changes.stream().collect(Collectors.toMap(ItemChangeDTO::getItemPK, SyncJobBatchEntry::new));
		}

		final Function<M, String> itemModelSerializer = this.getItemModelSerializer().get();
		final Map<Long, SyncJobBatchEntry<M>> entries = new HashMap<>();

		for (final ItemChangeDTO change : changes)
		{
			if (ChangeType.DELETED == change.getChangeType())
			{
				entries.put(change.getItemPK(), new SyncJobBatchEntry<M>(change));
			}
			else
			{
				try
				{
					final Long itemPK = change.getItemPK();
					final PK pk = PK.fromLong(itemPK);
					final M model = this.modelService.get(pk);
					final String info = itemModelSerializer.apply(model);
					final ItemChangeDTO changeUpdate = this.syncChangeDetectionService.updateInfo(change, info);
					entries.put(itemPK, new SyncJobBatchEntry<M>(changeUpdate));
				}
				catch (ModelLoadingException e)
				{
					// The root model does not exists in the database.
					LOG.warn("Unable to update ItemChangeDTO.info on '{}' because of {}", change, e, null);
					entries.put(change.getItemPK(), new SyncJobBatchEntry<M>(change));
				}
			}
		}
		return entries;
	}

	protected Stream<ItemChangeDTO> consumeChanges(final Collection<SyncJobBatchEntry<M>> entries)
	{
		final List<ItemChangeDTO> changes = entries.stream() //
				.map(SyncJobBatchEntry::getAllChanges) //
				.flatMap(List::stream) //
				.collect(Collectors.toList());
		this.syncChangeDetectionService.consumeChanges(changes);
		return changes.stream();
	}

	protected int getBatchSize(final T cronJob)
	{
		final int batchSize = cronJob.getBatchSize();
		if (batchSize <= 0 || batchSize > 5000)
		{
			LOG.warn("Invalid batchSize='{}', Defaulting to 500", batchSize);
			return 500;
		}
		return batchSize;
	}

	protected StreamConfiguration getConfiguration(final T cronJob)
	{
		StreamConfiguration configuration = StreamConfiguration.buildFor(cronJob.getCode());
		this.getStreamConfigurationItemSelector(cronJob).ifPresent(configuration::withItemSelector);
		return configuration.withParameters(this.getStreamConfigurationParameters(cronJob));
	}

	protected int getErrorCountStop(final T cronJob)
	{
		final int errorStopCount = cronJob.getErrorStopCount();
		if (errorStopCount <= 0)
		{
			LOG.warn("Invalid errorStopCount='{}', Defaulting to 3", errorStopCount);
			return 3;
		}
		return errorStopCount;
	}

	/**
	 * Returns a function that serializes an ItemModel instance (e.g. CustomerModel) into a String. This is used in the
	 * deletion scenario to persist an ItemModel after it's been deleted. The String representation of the ItemModel is
	 * stored in the 'info' field of ItemVersionMarkerModel.
	 *
	 * @return
	 * @see SyncChangeDetectionService#updateInfo(List, Function)
	 */
	protected Optional<Function<M, String>> getItemModelSerializer()
	{
		return Optional.empty();
	}

	/**
	 * Returns a function that deserializes the String representation of an ItemModel into an ItemModel instance.
	 * 
	 * @return
	 * @see SyncChangeDetectionService#readModelFromMerge(Map, Class, Optional)
	 */
	protected Optional<Function<String, M>> getItemModelDeserializer()
	{
		return Optional.empty();
	}

	/**
	 * Suggested implementation :<br>
	 * <code><pre>
	 * &#64;Override
	 * protected Class&lt;MyModel&gt; getModelClass() {
	 *   return MyModel.class;
	 * }</pre></code>
	 * 
	 * @return The model's {@link Class} to collect and send.
	 */
	protected abstract Class<M> getModelClass();

	/**
	 * @param cronJob
	 * @return {@link Map}&lt;{@link String}, {@link Object}&gt; for {@link AbstractSyncService#sendModels(List, Map)}
	 * @see AbstractSyncService#sendModels(List, Map)
	 */
	protected Map<String, Object> getSendParameters(final T cronJob)
	{
		final Map<String, Object> parameters = new HashMap<>();

		final String language = Optional.ofNullable(cronJob.getSessionLanguage()).map(LanguageModel::getIsocode).orElse("en");
		LOG.debug("Using language={}", language);
		parameters.put("language", language);

		final Boolean forceSynchronousProcessing = cronJob.isPerformValidation();
		LOG.debug("Using ForceSynchronousProcessing={}", forceSynchronousProcessing);
		parameters.put("ForceSynchronousProcessing", forceSynchronousProcessing);

		return parameters;
	}

	/**
	 * @param cronJob
	 * @return
	 * @see StreamConfiguration#withItemSelector(String)
	 */
	protected Optional<String> getStreamConfigurationItemSelector(T cronJob)
	{
		return Optional.empty();
	}

	/**
	 * @param cronJob
	 * @return {@link Map}&lt;{@link String}, {@link Object}&gt; for {@link StreamConfiguration#withParameters(Map)}
	 * @see StreamConfiguration#withParameters(Map)
	 */
	protected Map<String, Object> getStreamConfigurationParameters(T cronJob)
	{
		final Map<String, Object> map = new HashMap<>();
		map.put("cronJob", cronJob);
		LOG.debug("Using CatalogVersionModel={}", cronJob.getCatalogVersion());
		return map;
	}

	@Override
	public boolean isAbortable()
	{ // See usage of this.clearAbortRequestedIfNeeded(cronJob)
		return true;
	}

	@Override
	public PerformResult perform(final T cronJob)
	{
	    LOG.info("start perform {}",cronJob.getJob());
	    final SyncJobStopWatch watch = new SyncJobStopWatch(cronJob.getStartTime().getTime());
        final int errorCountStop = this.getErrorCountStop(cronJob);

        // Track the success of the cronJob's execution.
        boolean success = true;

	    try
	    {

    		watch.startCollectChanges();
    		final Map<Long, SyncJobBatchEntry<M>> changes = this.collectChanges(cronJob);
    		watch.endCollectChanges(changes.values().stream().map(SyncJobBatchEntry::getAllChanges).flatMap(List::stream));

    		LOG.info("getJob {}",cronJob.getJob());

    		final SyncJobBatchEntry<M> entryFailed = changes.remove(INVALID_PK);
    		if (entryFailed != null)
    		{
    			success = false;
    			watch.startConsumeChanges();
    			watch.endConsumeChanges(this.consumeChanges(Collections.singletonList(entryFailed)));
    		}


    		LOG.info("perform:changes: {}",changes.size());
    		int batchSize =  this.getBatchSize(cronJob);
    		final BatchBuilder batch = new BatchBuilder(changes.values(),batchSize);

    		float batchCount = (batch.entries.size()/ batchSize);
    		LOG.info("perform:batches: {}",batchCount);

    		int errorCount = 0;

    		for (final Collection<SyncJobBatchEntry<M>> entries : batch)
    		{
    		    int i = 0;
    		    LOG.debug("perform:batch start: {}",i);
    			success &= batch.success;

    			// Consume ItemChangeDTO for which nothing can be sent.
    			// I.e. Deleted model with no InfoFunction nor InfoSetter
    			watch.startConsumeChanges();
    			watch.endConsumeChanges(this.consumeChanges(batch.getEntriesNoModel()));

    			if (errorCount >= errorCountStop || this.clearAbortRequestedIfNeeded(cronJob))
    			{
    				watch.setResult(success ? RESULT_SUCCESS_ABORTED : RESULT_FAILURE_ABORTED);
    				LOG.info("{}", watch);
    				return watch.getResult();
    			}

    			final List<M> models = entries.stream().map(SyncJobBatchEntry::getModel).collect(Collectors.toList());

    			final Map<String, Object> parameters = this.getSendParameters(cronJob);

                LOG.debug("perform:batch send: "+i+"/"+batchCount);
    			watch.startSendModels();
    			boolean sending = this.syncService.sendModels(models, parameters);
    			LOG.debug("perform:batch send done: {}",i);
    			watch.endSendModels(models.size());

    			if (sending && cronJob.isPerformValidation())
    			{
    				watch.startValidateImportHeader();
    				sending = Optional.ofNullable(parameters.get("ImportHeaderId")) //
    						.map(String.class::cast) //
    						.map(this::validateImportHeader) //
    						.orElse(sending);
    				watch.endValidateImportHeader();
    			}

    			if (sending)
    			{
    				watch.startConsumeChanges();
    				watch.endConsumeChanges(this.consumeChanges(entries));
    			}
    			else
    			{
    				success = false;
    				errorCount++;
    			}
    			LOG.debug("batch done: {}",i);
    			i++;
    		}

    		success &= batch.success;

    		// Consume ItemChangeDTO for which nothing can be sent.
    		// I.e. Deleted model with no InfoFunction nor InfoSetter
    		watch.startConsumeChanges();
    		watch.endConsumeChanges(this.consumeChanges(batch.getEntriesNoModel()));

	    } catch ( Exception e )
	    {
	        LOG.error("error:", e);
	        success = false;
	    }

		watch.setResult(success ? RESULT_SUCCESS_FINISHED : RESULT_FAILURE_FINISHED);
		LOG.info("{}", watch);
		return watch.getResult();
	}


	@Required
	public void setImportMessageService(ImportMessageService importMessageService)
	{
		this.importMessageService = importMessageService;
	}

	@Required
	public void setSyncChangeDetectionService(SyncChangeDetectionService syncChangeDetectionService)
	{
		this.syncChangeDetectionService = syncChangeDetectionService;
	}

	@Required
	public void setSyncService(S syncService)
	{
		this.syncService = syncService;
	}

	protected boolean validateImportHeader(String importHeaderId)
	{
		final Optional<String> errorMessage = this.importMessageService.readImportHeaderErrors(importHeaderId);
		if (errorMessage.isPresent())
		{
			LOG.error("Error reported by yMKT : {}", errorMessage.get());
			return false;
		}
		return true;
	}
}
