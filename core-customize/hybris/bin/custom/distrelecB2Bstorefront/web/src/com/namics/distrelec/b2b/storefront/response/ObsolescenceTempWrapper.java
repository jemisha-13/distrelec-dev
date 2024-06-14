/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2019 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.namics.distrelec.b2b.storefront.response;

import com.namics.distrelec.b2b.facades.user.data.ObsolescenceTempData;

import java.io.Serializable;
import java.util.List;


/**
 *
 */
public class ObsolescenceTempWrapper implements Serializable
{
	private List<ObsolescenceTempData> obsoleCategories;

	/**
	 * @return the persons
	 */
	public List<ObsolescenceTempData> getObsoleCategories()
	{
		return obsoleCategories;
	}

	/**
	 * @param persons
	 *           the persons to set
	 */
	public void setObsoleCategories(final List<ObsolescenceTempData> obsoleCategories)
	{
		this.obsoleCategories = obsoleCategories;
	}
}
