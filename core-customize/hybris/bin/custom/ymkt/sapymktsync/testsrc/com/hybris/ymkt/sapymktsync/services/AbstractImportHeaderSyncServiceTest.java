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

import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class AbstractImportHeaderSyncServiceTest
{

	AbstractImportHeaderSyncService<ItemModel> abstractSyncService = new AbstractImportHeaderSyncService<ItemModel>()
	{

		@Override
		protected Map<String, Object> convertModelToMap(ItemModel model, Map<String, Object> parameters)
		{
			return null;
		}

		@Override
		protected String getImportHeaderNavigationProperty()
		{
			return null;
		}

	};

	@Before
	public void setUp() throws Exception
	{
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testGenerateImportHeaderId()
	{
		String id1 = abstractSyncService.generateImportHeaderId();
		String id2 = abstractSyncService.generateImportHeaderId();

		// Always uppercase
		Assert.assertEquals(id1.toUpperCase(Locale.ENGLISH), id1);
		Assert.assertEquals(id2.toUpperCase(Locale.ENGLISH), id2);

		// Always 32 char
		Assert.assertEquals(32, id1.length());
		Assert.assertEquals(32, id2.length());

		// Never the same value
		Assert.assertNotEquals(id1, id2);

		// Sample values
		System.out.println(id1);
		System.out.println(id2);
	}

}
