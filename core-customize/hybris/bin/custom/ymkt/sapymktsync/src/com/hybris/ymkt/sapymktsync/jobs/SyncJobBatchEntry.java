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
import de.hybris.platform.core.model.ItemModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SyncJobBatchEntry<M extends ItemModel>
{
	protected final List<ItemChangeDTO> allChanges = new ArrayList<>();
	protected final Optional<ItemChangeDTO> change;
	protected M model;
	protected final Long pk;

	public SyncJobBatchEntry(ItemChangeDTO change)
	{
		this.allChanges.add(change);
		this.change = Optional.of(change);
		this.pk = change.getItemPK();
	}

	public SyncJobBatchEntry(Long pk)
	{
		this.change = Optional.empty();
		this.pk = pk;
	}

	public void addChildChange(ItemChangeDTO childChange)
	{
		this.allChanges.add(childChange);
	}

	public List<ItemChangeDTO> getAllChanges()
	{
		return this.allChanges;
	}

	public Optional<ItemChangeDTO> getChange()
	{
		return this.change;
	}

	public Optional<String> getInfo()
	{
		return this.change.map(ItemChangeDTO::getInfo);
	}

	public Long getItemPK()
	{
		return this.pk;
	}

	public M getModel()
	{
		return model;
	}

	public void setModel(M model)
	{
		this.model = model;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("SyncJobBatchEntry [allChanges=");
		builder.append(allChanges);
		builder.append(", pk=");
		builder.append(pk);
		builder.append(", model=");
		builder.append(model);
		builder.append("]");
		return builder.toString();
	}
}
