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
import de.hybris.deltadetection.StreamConfiguration;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.edm.EdmEntitySet;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.ep.feed.ODataFeed;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.hybris.ymkt.common.http.HttpURLConnectionRequest;
import com.hybris.ymkt.common.http.HttpURLConnectionResponse;
import com.hybris.ymkt.common.odata.ODataService;
import com.hybris.ymkt.sapymktsync.model.SyncCronJobModel;
import com.hybris.ymkt.sapymktsync.services.TestTool;



@ManualTest
public class ProductSyncJobTest extends AbstractSyncJobTest<ProductSyncJob>
{
	protected static final String APPLICATION_JSON = "application/json";

	private static final Logger LOG = LoggerFactory.getLogger(ProductSyncJobTest.class);

	protected static final EntityProviderReadProperties NO_READ_PROPERTIES = EntityProviderReadProperties.init().build();

	@Resource(name = "ymktCategorySyncJob")
	CategorySyncJob categorySyncJob;

	@Resource(name = "ODataService_CUAN_PRODUCT_SRV")
	ODataService cuanProduct;

	@Resource(name = "ymktProductSyncJob")
	ProductSyncJob productSyncJob;



	@Override
	protected ProductSyncJob getSyncJob()
	{
		return this.productSyncJob;
	}

	protected Map<String, Object> readProduct(String productId) throws Exception
	{
		final URL url = this.cuanProduct.createURL("Products", //
				"$filter", this.cuanProduct.filter("Products").on("ProductID").eq(productId).toExpression(), //
				"$expand", "ProductCategoryHierarchies");
		final HttpURLConnectionRequest request = new HttpURLConnectionRequest("GET", url);
		request.getRequestProperties().put("Accept", APPLICATION_JSON);
		request.setReadTimeout(30 * 1000);

		final HttpURLConnectionResponse response = this.cuanProduct.executeWithRetry(request);
		final EdmEntitySet entitySet = this.cuanProduct.getEntitySet("Products");

		final InputStream content = new ByteArrayInputStream(response.getPayload());
		final ODataFeed feed = EntityProvider.readFeed(APPLICATION_JSON, entitySet, content, NO_READ_PROPERTIES);

		final List<ODataEntry> entries = feed.getEntries();
		Assert.assertEquals(1, entries.size());

		final String json = new String(response.getPayload(), StandardCharsets.UTF_8);
		final JsonObject jo = new Gson().fromJson(json, JsonObject.class);
		final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
		LOG.debug(gson.toJson(jo));

		return entries.get(0).getProperties();
	}

	protected void sendCategory(SyncCronJobModel cronJob)
	{
		// Setup category
		cronJob.setStartTime(new Date());
		Assert.assertEquals(CronJobResult.SUCCESS, this.categorySyncJob.perform(cronJob).getResult());
		final StreamConfiguration config = this.categorySyncJob.getConfiguration(cronJob);
		final Class<CategoryModel> modelClass = this.categorySyncJob.getModelClass();
		final List<ItemChangeDTO> collectChanges = this.syncChangeDetectionService.collectChanges(modelClass, config);
		Assert.assertEquals(Collections.emptyList(), collectChanges);
	}

	@Before
	public void setUp() throws Exception
	{
		super.setUp();
		TestTool.fixLogger(ProductSyncJob.class);
	}

	@After
	public void tearDown() throws Exception
	{
		super.tearDown();
	}

	@Test
	public void test_Create_Update() throws Exception
	{
		SyncCronJobModel cronJob = this.createCronJob();
		this.sendCategory(cronJob);

		// Create
		LOG.info("Test Create.");
		this.perform(cronJob);
		Thread.sleep(1000);

		final Map<String, Object> p1 = this.readProduct(product.getCode());
		Assert.assertEquals(product.getName(Locale.ENGLISH), p1.get("ProductName"));
		Assert.assertEquals(product.getDescription(Locale.ENGLISH), p1.get("ProductDescription"));
		Assert.assertTrue(p1.get("ProductImageURL").toString().contains(product.getPicture().getURL()));
		final ODataFeed feed1 = (ODataFeed) p1.get("ProductCategoryHierarchies");
		final List<ODataEntry> entries1 = feed1.getEntries();
		Assert.assertEquals(1, entries1.size());
		Assert.assertEquals(category.getCode(), entries1.get(0).getProperties().get("ProductCategory"));
		Assert.assertEquals(category.getName(Locale.ENGLISH), entries1.get(0).getProperties().get("ProductCategoryName"));
		Assert.assertEquals("Default:Staged", entries1.get(0).getProperties().get("ProductCategoryHier"));

		// Update
		LOG.info("Test Update.");

		media.setURL("/medias/?context=UUUUUUUPPPPPPPDDDDDDAAAAAAATTTTTTTEEEEEEDDDDDDDD");
		modelService.save(media);

		product.setName("Test Updated en", Locale.ENGLISH);
		product.setDescription("Test Description Updated en", Locale.ENGLISH);
		product.setSupercategories(Collections.emptyList());
		modelService.save(product);
		this.perform(cronJob);
		Thread.sleep(1000);

		final Map<String, Object> p2 = this.readProduct(product.getCode());
		Assert.assertEquals(product.getName(Locale.ENGLISH), p2.get("ProductName"));
		Assert.assertEquals(product.getDescription(Locale.ENGLISH), p2.get("ProductDescription"));
		Assert.assertTrue(p2.get("ProductImageURL").toString().contains(product.getPicture().getURL()));

		// yMKT cannot remove categories via CUAN_IMPORT
		//		final ODataFeed feed2 = (ODataFeed) p2.get("ProductCategoryHierarchies");
		//		final List<ODataEntry> entries2 = feed2.getEntries();
		//		Assert.assertEquals(0, entries2.size());

	}

	@Test
	public void test_DeleteDoesNothingNoError() throws Exception
	{
		SyncCronJobModel cronJob = this.createCronJob();
		this.sendCategory(cronJob);
		this.perform(cronJob);

		// Delete
		LOG.info("Test Delete.");
		modelService.remove(product);
		this.perform(cronJob);
	}

	@Test
	public void test_NoDescriptionNoName() throws Exception
	{
		final SyncCronJobModel cronJob = this.createCronJob();
		this.sendCategory(cronJob);

		this.createGet(ProductModel.class, //
				() -> productService.getProductForCode(catalogVersion, "Test1"), //
				p -> p.setCode("Test1"), //
				p -> p.setCatalogVersion(catalogVersion));

		this.perform(cronJob);
		Thread.sleep(1000);
	}

	@Test
	public void test_ProductDescriptionThumbsUpWithInvalidUnicode() throws Exception
	{
		SyncCronJobModel cronJob = this.createCronJob();

		// Twitter : <City>Bentley, Alberta &#55356;&#56808;&#55356;&#56806;</City>

		String s55356 = "\uD914\uDF56";
		String s56806 = "\uD91A\uDC06";
		String s56808 = "\uD91A\uDC08";
		String desc = "Test Description ThumbsUp : \uD83D\uDC4D " + s55356 + s56808 + s55356 + s56806;

		this.createGet(ProductModel.class, //
				() -> productService.getProductForCode(catalogVersion, "TestThumbsUp"), //
				p -> p.setCode("TestThumbsUp"), //
				p -> p.setCatalogVersion(catalogVersion), //
				p -> p.setDescription(desc, Locale.ENGLISH));

		this.perform(cronJob);

		final Map<String, Object> productThumbsUp = this.readProduct("TestThumbsUp");

		Assert.assertEquals(desc, productThumbsUp.get("ProductDescription"));
	}

	@Test
	public void test_ProductId_Unicode() throws Exception
	{
		SyncCronJobModel cronJob = this.createCronJob();

		this.createGet(ProductModel.class, //
				() -> productService.getProductForCode(catalogVersion, "Test7"), // Digit Seven “7” (U+0037)
				p -> p.setCode("Test7"), //
				p -> p.setCatalogVersion(catalogVersion));

		this.createGet(ProductModel.class, //
				() -> productService.getProductForCode(catalogVersion, "Test７"), // Fullwidth Digit Seven “７” (U+FF17)
				p -> p.setCode("Test７"), //
				p -> p.setCatalogVersion(catalogVersion));

		this.perform(cronJob);

		final Map<String, Object> p7 = this.readProduct("Test7");
		final Map<String, Object> p7fullwidth = this.readProduct("Test７");

		Assert.assertNotEquals(p7.get("ProductUUID"), p7fullwidth.get("ProductUUID"));
	}
}
