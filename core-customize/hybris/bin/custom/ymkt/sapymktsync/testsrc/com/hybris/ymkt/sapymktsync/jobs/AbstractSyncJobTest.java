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

import de.hybris.bootstrap.annotations.ManualTest;
import de.hybris.deltadetection.ItemChangeDTO;
import de.hybris.platform.catalog.CatalogService;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.product.UnitService;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.cronjob.CronJobService;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.hybris.ymkt.common.http.HttpURLConnectionService;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.AbstractBusinessDocSyncService;
import com.hybris.ymkt.sapymktsync.services.AbstractImportHeaderSyncService;
import com.hybris.ymkt.sapymktsync.services.AbstractSyncService;
import com.hybris.ymkt.sapymktsync.services.SyncChangeDetectionService;
import com.hybris.ymkt.sapymktsync.services.TestTool;


@ManualTest
public abstract class AbstractSyncJobTest<J extends AbstractSyncJob<SyncCronJobModel, ?, ?>> extends ServicelayerBaseTest
{
	CatalogModel catalog;
	@Resource
	CatalogService catalogService;

	CatalogVersionModel catalogVersion;
	@Resource
	CatalogVersionService catalogVersionService;

	CategoryModel category;
	@Resource
	CategoryService categoryService;

	@Resource
	CommonI18NService commonI18NService;
	@Resource
	CronJobService cronJobService;
	CurrencyModel currencyCAD;

	CurrencyModel currencyUSD;
	CurrencyModel currencyX2X;
	CustomerModel customer;

	@Resource
	FlexibleSearchService flexibleSearchService;
	MediaModel media;
	@Resource
	MediaService mediaService;

	@Resource
	ModelService modelService;
	ProductModel product;

	@Resource
	ProductService productService;

	@Resource(name = "ymktSyncChangeDetectionService")
	SyncChangeDetectionService syncChangeDetectionService;
	UnitModel unitPAL;

	@Resource
	UnitService unitService;
	@Resource
	UserService userService;

	List<ItemChangeDTO> collectChanges(SyncCronJobModel cronJob)
	{
		final J syncJob = this.getSyncJob();
		return this.syncChangeDetectionService.collectChanges(syncJob.getModelClass(), syncJob.getConfiguration(cronJob));
	}

	@SuppressWarnings("unchecked")
	List<ItemChangeDTO> collectChildChanges(SyncCronJobModel cronJob)
	{
		final AbstractChildSyncJob<SyncCronJobModel, ?, ?, ?> childJob = (AbstractChildSyncJob<SyncCronJobModel, ?, ?, ?>) this
				.getSyncJob();
		return this.syncChangeDetectionService.collectChanges(childJob.getChildClass(), childJob.getChildConfiguration(cronJob));
	}

	SyncCronJobModel createCronJob()
	{
		ServicelayerJobModel servicelayerJob = modelService.create(ServicelayerJobModel.class);
		servicelayerJob.setSpringId("ymkt" + this.getSyncJob().getClass().getSimpleName());
		servicelayerJob.setCode(uniqueId());
		modelService.save(servicelayerJob);

		SyncCronJobModel cronJob = modelService.create(SyncCronJobModel.class);
		cronJob.setActive(Boolean.TRUE);
		cronJob.setCatalogVersion(catalogVersion);
		cronJob.setCode(uniqueId());
		cronJob.setJob(servicelayerJob);
		cronJob.setPerformValidation(true);
		cronJob.setStartTime(new Date());
		modelService.save(cronJob);
		return cronJob;
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Consumer<M> set)
	{
		return this.createGet(modelClass, (Supplier<M>) null, set);
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Consumer<M> set1, Consumer<M> set2)
	{
		return this.createGet(modelClass, (Supplier<M>) null, set1, set2);
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Consumer<M> set1, Consumer<M> set2, Consumer<M> set3)
	{
		return this.createGet(modelClass, (Supplier<M>) null, set1, set2, set3);
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Supplier<M> get, Consumer<M> set)
	{
		return this.createGet(modelClass, get, Collections.singletonList(set));
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Supplier<M> get, Consumer<M> set1, Consumer<M> set2)
	{
		return this.createGet(modelClass, get, Arrays.asList(set1, set2));
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Supplier<M> get, Consumer<M> set1, Consumer<M> set2, Consumer<M> set3)
	{
		return this.createGet(modelClass, get, Arrays.asList(set1, set2, set3));
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Supplier<M> get, Consumer<M> set1, Consumer<M> set2, Consumer<M> set3,
			Consumer<M> set4)
	{
		return this.createGet(modelClass, get, Arrays.asList(set1, set2, set3, set4));
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Supplier<M> get, Consumer<M> setter1, Consumer<M> setter2,
			Consumer<M> setter3, Consumer<M> setter4, Consumer<M> setter5)
	{
		return this.createGet(modelClass, get, Arrays.asList(setter1, setter2, setter3, setter4, setter5));
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Supplier<M> get, Consumer<M> setter1, Consumer<M> setter2,
			Consumer<M> setter3, Consumer<M> setter4, Consumer<M> setter5, Consumer<M> setter6)
	{
		return this.createGet(modelClass, get, Arrays.asList(setter1, setter2, setter3, setter4, setter5, setter6));
	}

	<M extends ItemModel> M createGet(Class<M> modelClass, Supplier<M> get, List<Consumer<M>> setters)
	{
		M m = null;
		try
		{
			if (get != null)
			{
				m = get.get();
			}
		}
		catch (UnknownIdentifierException e)
		{
			// Remains null
		}

		if (m == null)
		{
			m = modelService.create(modelClass);
		}

		for (Consumer<M> set : setters)
		{
			try
			{
				set.accept(m);
				modelService.save(m);
			}
			catch (ModelSavingException e)
			{
				if (!modelService.isNew(m))
				{
					modelService.refresh(m);
				}
			}
		}
		modelService.saveAll();
		return m;
	}

	// Use createGet
	@Deprecated
	<M extends ItemModel> M createGetReset(Class<M> modelClass, Supplier<M> get, Collection<Consumer<M>> keyFieldSetters,
			Collection<Consumer<M>> fieldSetters)
	{
		M m = null;
		try
		{
			m = get.get();
		}
		catch (UnknownIdentifierException e)
		{
		}

		if (m == null)
		{
			m = modelService.create(modelClass);
			M m1 = m;
			keyFieldSetters.stream().forEach(s -> s.accept(m1));
		}

		M m2 = m;
		fieldSetters.stream().forEach(s -> s.accept(m2)); // reset fields

		modelService.saveAll();
		return m;
	}

	abstract J getSyncJob();

	void perform(SyncCronJobModel cronJob) throws Exception
	{
		cronJob.setStartTime(new Date());
		Assert.assertEquals(CronJobResult.SUCCESS, this.getSyncJob().perform(cronJob).getResult());
		Assert.assertEquals(Collections.emptyList(), this.collectChanges(cronJob));
	}

	@Before
	public void setUp() throws Exception
	{
		TestTool.fixLogger(this.getClass());
		TestTool.fixLogger(this.getClass().getSuperclass());
		TestTool.fixLogger(AbstractSyncService.class);
		TestTool.fixLogger(AbstractImportHeaderSyncService.class);
		TestTool.fixLogger(AbstractBusinessDocSyncService.class);
		TestTool.fixLogger(HttpURLConnectionService.class);
		TestTool.fixLogger(ODataService.class);

		TestTool.disableCertificates();

		userService.setCurrentUser(userService.getAdminUser());

		catalog = this.createGet(CatalogModel.class, //
				() -> catalogService.getCatalogForId("Default"), //
				c -> c.setId("Default"));

		catalogVersion = this.createGet(CatalogVersionModel.class,
				() -> catalogVersionService.getCatalogVersion("Default", "Staged"), //
				cv -> cv.setCatalog(catalog), //
				cv -> cv.setVersion("Staged"));

		category = this.createGet(CategoryModel.class, //
				() -> categoryService.getCategoryForCode(catalogVersion, "Test"), //
				c -> c.setCatalogVersion(catalogVersion), //
				c -> c.setCode("Test"), //
				c -> c.setName("Test en", Locale.ENGLISH));

		currencyCAD = this.createGet(CurrencyModel.class, //
				() -> commonI18NService.getCurrency("CAD"), //
				c -> c.setIsocode("CAD"), //
				c -> c.setSymbol("$"));

		currencyUSD = this.createGet(CurrencyModel.class, //
				() -> commonI18NService.getCurrency("USD"), //
				c -> c.setIsocode("USD"), //
				c -> c.setSymbol("$"));

		currencyX2X = this.createGet(CurrencyModel.class, //
				() -> commonI18NService.getCurrency("X2X"), //
				c -> c.setIsocode("X2X"), //
				c -> c.setSymbol("X"));

		media = this.createGet(MediaModel.class, //
				() -> mediaService.getMedia("/96Wx96H/1715477-Sony-DSLR-A900-13114.jpg"), //
				m -> m.setCode("/96Wx96H/1715477-Sony-DSLR-A900-13114.jpg"), //
				m -> m.setCatalogVersion(catalogVersion), //
				m -> m.setURL(
						"/medias/?context=bWFzdGVyfGltYWdlc3wyMjM0fGltYWdlL2pwZWd8aW1hZ2VzL2hlOS9oOGQvODc5NjIyNjc4MTIxNC5qcGd8YjY4YzhmZTI5OWU5MjhmNWNlMGU4M2M2MjFjYTE5Nzg3NThkMzcxNTU3MzIyNzQwNWZiODBkZjVmNGU5N2Y1ZQ"));

		product = this.createGet(ProductModel.class, //
				() -> productService.getProductForCode(catalogVersion, "Test"), //
				p -> p.setCode("Test"), //
				p -> p.setCatalogVersion(catalogVersion), //
				p -> p.setName("Test en", Locale.ENGLISH), //
				p -> p.setDescription("Test Description en", Locale.ENGLISH), //
				p -> p.setPicture(media), //
				p -> p.setSupercategories(Arrays.asList(category)));

		unitPAL = this.createGet(UnitModel.class, //
				() -> unitService.getUnitForCode("PAL"), //
				u -> u.setCode("PAL"), //
				u -> u.setUnitType("pallet"));

		customer = this.createGet(CustomerModel.class, //
				() -> (CustomerModel) userService.getUserForUID("apollo.creed@predator.com"), //
				c -> c.setUid("apollo.creed@predator.com"), //
				c -> c.setName("Apollo Creed"), //
				c -> // delete existing addresses
				Optional.ofNullable(c.getAddresses()).ifPresent(addr -> addr.forEach(modelService::remove)));

		modelService.saveAll();
	}

	@After
	public void tearDown() throws Exception
	{
		modelService.saveAll();
	}

	String uniqueId()
	{
		return UUID.randomUUID().toString();
	}
}
