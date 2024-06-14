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
import de.hybris.deltadetection.enums.ChangeType;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class SyncJobStopWatch
{
	protected final Map<ChangeType, AtomicInteger> mapCollectChanges = new HashMap<>();
	protected final Map<ChangeType, AtomicInteger> mapConsumeChanges = new HashMap<>();

	protected PerformResult result;

	protected int sizeSendModels;
	protected int sizeValidateImportHeader;

	protected final long start;
	protected long startCollectChanges;
	protected long startConsumeChanges;
	protected long startSendModels;
	protected long startValidateImportHeader;

	protected long sumCollectChanges;
	protected long sumConsumeChanges;
	protected long sumSendModels;
	protected long sumValidateImportHeader;

	public SyncJobStopWatch(long startTime)
	{
		this.start = startTime;
	}

	public void endCollectChanges(Stream<ItemChangeDTO> changes)
	{
		changes.forEach(c -> this.mapCollectChanges.computeIfAbsent(c.getChangeType(), z -> new AtomicInteger()).incrementAndGet());
		this.sumCollectChanges += System.currentTimeMillis() - this.startCollectChanges;
		this.startCollectChanges = 0;
	}

	public void endConsumeChanges(Stream<ItemChangeDTO> changes)
	{
		changes.forEach(c -> this.mapConsumeChanges.computeIfAbsent(c.getChangeType(), z -> new AtomicInteger()).incrementAndGet());
		this.sumConsumeChanges += System.currentTimeMillis() - this.startConsumeChanges;
		this.startConsumeChanges = 0;
	}

	public void endSendModels(int size)
	{
		this.sizeSendModels += size;
		this.sumSendModels += System.currentTimeMillis() - this.startSendModels;
		this.startSendModels = 0;
	}

	public void endValidateImportHeader()
	{
		this.sizeValidateImportHeader++;
		this.sumValidateImportHeader += System.currentTimeMillis() - this.startValidateImportHeader;
		this.startValidateImportHeader = 0;
	}

	public PerformResult getResult()
	{
		return this.result;
	}

	public void setResult(PerformResult result)
	{
		this.result = result;
	}

	public void startCollectChanges()
	{
		assert this.startCollectChanges == 0;
		this.startCollectChanges = System.currentTimeMillis();
	}

	public void startConsumeChanges()
	{
		assert this.startConsumeChanges == 0;
		this.startConsumeChanges = System.currentTimeMillis();
	}

	public void startSendModels()
	{
		assert this.startSendModels == 0;
		this.startSendModels = System.currentTimeMillis();
	}

	public void startValidateImportHeader()
	{
		assert this.startValidateImportHeader == 0;
		this.startValidateImportHeader = System.currentTimeMillis();
	}

	@Override
	public String toString()
	{
		final long end = System.currentTimeMillis();
		final String collect = this.toString(mapCollectChanges);
		final String consume = this.toString(mapConsumeChanges);

		final StringBuilder sb = new StringBuilder(200);
		sb.append(this.result.getResult()).append('/').append(this.result.getStatus());
		sb.append("=").append(end - this.start).append("ms");
		sb.append(", CollectChanges(").append(collect).append(")=").append(this.sumCollectChanges).append("ms");
		sb.append(", ConsumeChanges(").append(consume).append(")=").append(this.sumConsumeChanges).append("ms");
		sb.append(", SendModels(").append(this.sizeSendModels).append(")=").append(this.sumSendModels).append("ms");
		sb.append(", ValidateImportHeader(").append(this.sizeValidateImportHeader).append(")=").append(this.sumValidateImportHeader)
				.append("ms");
		return sb.toString();
	}

	protected String toString(final Map<ChangeType, AtomicInteger> changes)
	{
		if (changes.isEmpty())
		{
			return "0";
		}
		return changes.entrySet().stream() //
				.map(e -> e.getKey() + "=" + e.getValue()) //
				.sorted(Comparator.reverseOrder()) // NEW, MODIFIED, DELETED
				.collect(Collectors.joining(","));
	}
}
