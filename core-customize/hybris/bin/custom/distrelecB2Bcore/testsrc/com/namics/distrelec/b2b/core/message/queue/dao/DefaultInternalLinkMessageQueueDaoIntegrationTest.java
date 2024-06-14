/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.message.queue.dao;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Ordering;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.category.CategoryService;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.exceptions.FlexibleSearchException;
import de.hybris.platform.site.BaseSiteService;

@IntegrationTest
public class DefaultInternalLinkMessageQueueDaoIntegrationTest extends ServicelayerTransactionalTest {

    private static final Logger LOG = LogManager.getLogger(DefaultInternalLinkMessageQueueDaoIntegrationTest.class);

    @Resource
    InternalLinkMessageDao internalLinkMessageQueueDao;

    @Resource
    DistManufacturerService distManufacturerService;

    @Resource
    BaseSiteService baseSiteService;

    @Resource
    CategoryService categoryService;

    private static final String CMS_SITE = "distrelec_CH";
    private static final String MANUFACTURER_MAIN = "manufacturer1";
    private static final String MANUFACTURER_RELATED = "manufacturer2";
    private static final String MANUFACTURER_UNRELATED = "manufacturer3";

    private CMSSiteModel siteModel;
    private DistManufacturerModel mainManufacturer;
    private final List<CategoryModel> testCategories = new ArrayList<>();

    @Before
    public void setUp() throws ImpExException {
        assertNotNull(getDistManufacturerService());
        assertNotNull(getBaseSiteService());
        importCsv("/distrelecB2Bcore/test/meshLinking/testDistManufacturers.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/meshLinking/testCategories.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/meshLinking/testProducts.impex", "utf-8");
        importCsv("/distrelecB2Bcore/test/meshLinking/testOrders.impex", "utf-8");
        
        mainManufacturer = getDistManufacturerService().getManufacturerByCode(MANUFACTURER_MAIN);
        assertNotNull(mainManufacturer);
        
        siteModel = (CMSSiteModel) getBaseSiteService().getBaseSiteForUID(CMS_SITE);
        assertNotNull(mainManufacturer);
        
        testCategories.add(getCategoryService().getCategoryForCode("C1"));
        testCategories.add(getCategoryService().getCategoryForCode("C2"));
        assertEquals(2, testCategories.size());
    }

    /***************************************** START MANUFACTURER ****************************************************/

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 - Manufacturer point 1
     * </p>
     * tests that the query is executed without error
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_RelatedCategoriesForManufacturer() throws FlexibleSearchException {
        getInternalLinkMessageQueueDao().fetchRelatedCategoriesForManufacturer(mainManufacturer, siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 2
     * </p>
     * tests that the query is executed without error
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_RelatedManufacturers() throws FlexibleSearchException {
        final List<CategoryModel> categories = getInternalLinkMessageQueueDao().fetchRelatedCategoriesForManufacturer(mainManufacturer, siteModel);
        getInternalLinkMessageQueueDao().fetchRelatedManufacturers(categories, siteModel, Collections.singletonList(mainManufacturer));
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 3
     * </p>
     * tests that the query is executed without error
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_NewArrivals() throws FlexibleSearchException {
        getInternalLinkMessageQueueDao().fetchNewArrivalsOfManufacturer(mainManufacturer, siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11505 Manufacturer point 3
     * </p>
     * tests that the query is executed without error
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_NewArrivalsForCategories() throws FlexibleSearchException {
        getInternalLinkMessageQueueDao().fetchNewArrivalsOfCategories(testCategories, siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-12853 - Manufacturer point 4
     * </p>
     * tests that the query is executed without error
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     **/
    @Test
    public void testQuery_TopSellers() throws FlexibleSearchException {
        getInternalLinkMessageQueueDao().fetchTopSellersOfManufacturer(mainManufacturer, siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 1
     * </p>
     * tests that the query gets the correct result
     * 
     */
    @Test
    public void testResult_RelatedCategoriesForManufacturer() {
        final List<CategoryModel> categories = getInternalLinkMessageQueueDao().fetchRelatedCategoriesForManufacturer(mainManufacturer, siteModel);

        assertEquals(3, categories.size());
        assertThat(categories, containsInAnyOrder( //
                hasProperty(CategoryModel.CODE, is("C1_1_1")), //
                hasProperty(CategoryModel.CODE, is("C1_1_2")), //
                hasProperty(CategoryModel.CODE, is("C1_2_1")) //
        ));
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 2
     * </p>
     * tests that the query gets the correct result
     * 
     */
    @Test
    public void testResult_RelatedManufacturers() {

        final List<CategoryModel> categories = Arrays.asList(getCategoryService().getCategoryForCode("C1_1_1"),
                getCategoryService().getCategoryForCode("C1_1_2"), getCategoryService().getCategoryForCode("C1_2_1"));

        final List<DistManufacturerModel> result = getInternalLinkMessageQueueDao().fetchRelatedManufacturers(categories, siteModel,
                Collections.singletonList(mainManufacturer));
        assertEquals(1, result.size());
        assertThat(result, contains(hasProperty(DistManufacturerModel.CODE, is(MANUFACTURER_RELATED))));
        assertThat(result, not(contains(hasProperty(DistManufacturerModel.CODE, is(MANUFACTURER_UNRELATED)))));
        assertThat(result, not(contains(hasProperty(DistManufacturerModel.CODE, is(MANUFACTURER_MAIN)))));
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-12853 Manufacturer point 4
     * </p>
     * tests that the query gets the correct result
     * 
     */
    @Test
    public void testResult_TopSellersOfManufacturer() {
        final List<ProductModel> products = getInternalLinkMessageQueueDao().fetchTopSellersOfManufacturer(mainManufacturer, siteModel);

        assertEquals(5, products.size());
        assertThat(products.subList(0, 3), containsInAnyOrder( //
                hasProperty(ProductModel.CODE, is("P1_1_1___2")), // Sold Twice
                hasProperty(ProductModel.CODE, is("P1_1_2___2")), // Sold Twice
                hasProperty(ProductModel.CODE, is("P1_2_1___2")) // Sold Twice
        ));
        assertThat(products.get(3), anyOf(//
                hasProperty(ProductModel.CODE, is("P1_1_1___1")), // Sold Once
                hasProperty(ProductModel.CODE, is("P1_1_2___1")), // Sold Once
                hasProperty(ProductModel.CODE, is("P1_2_1___1")) // Sold Once
        ));
        
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_1___3"))))); // other manufacturer
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_1___4"))))); // Other SalesOrg
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_1___5"))))); // Inactive
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_1___6"))))); // Not Sold
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_1___7"))))); // Out Of Stock

        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_2___3"))))); // other manufacturer
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_2___4"))))); // Other SalesOrg
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_2___5"))))); // Inactive
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_2___6"))))); // Not Sold
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_1_2___7"))))); // Out Of Stock

        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_2_1___3"))))); // other manufacturer
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_2_1___4"))))); // Other SalesOrg
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_2_1___5"))))); // Inactive
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_2_1___6"))))); // Not Sold
        assertThat(products, not(contains(hasProperty(ProductModel.CODE, is("P1_2_1___7"))))); // Out Of Stock
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-12852 - Categories point 4
     * </p>
     * tests that the query gets the correct result
     */
    @Test
    public void testResult_TopSellersInCategories() {
        final CategoryModel leafCategory = getCategoryService().getCategoryForCode("C1_1_1");
        final List<RelatedItemData<ProductModel>> relatedItemDatas = getInternalLinkMessageQueueDao()
                .fetchTopSellersInCategories(Collections.singletonList(leafCategory), siteModel);
        LOG.info("testResult_TopSellersInCategories: {}", relatedItemDatas);
        
        assertEquals(3, relatedItemDatas.size());
        
        assertTrue(Ordering.natural().reverse().isOrdered(relatedItemDatas.stream().map(rid -> rid.getCount()).collect(Collectors.toList())));
        
        assertThat(relatedItemDatas.get(0).getItem(), hasProperty(ProductModel.CODE, is("P1_1_1___2"))); // Sold Twice

        assertThat(relatedItemDatas.subList(1, 3).stream().map(rid -> rid.getItem()).collect(Collectors.toList()), containsInAnyOrder( //
                hasProperty(ProductModel.CODE, is("P1_1_1___1")), // Sold Once
                hasProperty(ProductModel.CODE, is("P1_1_1___3")) // Sold Once
        ));

    }

    /******************************************* END MANUFACTURER ****************************************************/

    /******************************************* START CATEGORY ******************************************************/

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11505 - Categories point 1
     * </p>
     * tests that the query is executed without error with one Product Line Category
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_RelatedManufacturersForCategory_OneCategory() throws FlexibleSearchException {
        final CategoryModel productLineCategory = getCategoryService().getCategoryForCode("C1_1_1");
        getInternalLinkMessageQueueDao().fetchRelatedManufacturersForCategory(Collections.singleton(productLineCategory), siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11505 - Categories point 1
     * </p>
     * tests that the query is executed without error with multiple categories
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_RelatedManufacturersForCategory_MultipleCategories() throws FlexibleSearchException {
        final CategoryModel category1 = getCategoryService().getCategoryForCode("C1_1_1");
        final CategoryModel category2 = getCategoryService().getCategoryForCode("C1_1_2");
        final CategoryModel category3 = getCategoryService().getCategoryForCode("C1_1");
        getInternalLinkMessageQueueDao().fetchRelatedManufacturersForCategory(Arrays.asList(category1, category2, category3), siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11505 - Categories point 2
     * </p>
     * tests that the query is executed without error
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_RelatedCategoriesForCategory() throws FlexibleSearchException {
        final DistManufacturerModel manufacturer1 = getDistManufacturerService().getManufacturerByCode("manufacturer1");
        final DistManufacturerModel manufacturer2 = getDistManufacturerService().getManufacturerByCode("manufacturer2");
        final DistManufacturerModel manufacturer3 = getDistManufacturerService().getManufacturerByCode("manufacturer3");
        getInternalLinkMessageQueueDao().fetchRelatedCategoriesForCategory(Arrays.asList(manufacturer1, manufacturer2, manufacturer3), siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11505 - Categories point 3
     * </p>
     * tests that the query is executed without error with one Product Line Category
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_fetchRelatedProductsForCategory_OneCategory() throws FlexibleSearchException {
        final CategoryModel productLineCategory = getCategoryService().getCategoryForCode("C1_1_1");
        getInternalLinkMessageQueueDao().fetchRelatedProductsForCategory(Collections.singleton(productLineCategory), siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-11505 - Categories point 3
     * </p>
     * tests that the query is executed without error with multiple categories
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_fetchRelatedProductsForCategory_MultipleCategories() throws FlexibleSearchException {
        final CategoryModel category1 = getCategoryService().getCategoryForCode("C1_1_1");
        final CategoryModel category2 = getCategoryService().getCategoryForCode("C1_1_2");
        final CategoryModel category3 = getCategoryService().getCategoryForCode("C1_1");
        getInternalLinkMessageQueueDao().fetchRelatedProductsForCategory(Arrays.asList(category1, category2, category3), siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-12852 - Categories point 4
     * </p>
     * tests that the query is executed without error with one Product Line Category
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_fetchTopSellersInCategories_OneCategory() throws FlexibleSearchException {
        final CategoryModel productLineCategory = getCategoryService().getCategoryForCode("C1_1_1");
        getInternalLinkMessageQueueDao().fetchTopSellersInCategories(Collections.singleton(productLineCategory), siteModel);
    }

    /**
     * <p>
     * https://jira.distrelec.com/browse/DISTRELEC-12852 - Categories point 4
     * </p>
     * tests that the query is executed without error with multiple categories
     * 
     * @throws FlexibleSearchException
     *             if SQL is wrong or not compatible with used DBMS
     */
    @Test
    public void testQuery_fetchTopSellersInCategories_MultipleCategories() throws FlexibleSearchException {
        final CategoryModel category1 = getCategoryService().getCategoryForCode("C1_1_1");
        final CategoryModel category2 = getCategoryService().getCategoryForCode("C1_1_2");
        final CategoryModel category3 = getCategoryService().getCategoryForCode("C1_1");
        getInternalLinkMessageQueueDao().fetchTopSellersInCategories(Arrays.asList(category1, category2, category3), siteModel);
    }

    /******************************************* END CATEGORY ********************************************************/

    // Getters and Setters
    public InternalLinkMessageDao getInternalLinkMessageQueueDao() {
        return internalLinkMessageQueueDao;
    }

    public void setInternalLinkMessageQueueDao(final DefaultInternalLinkMessageQueueDao internalLinkMessageQueueDao) {
        this.internalLinkMessageQueueDao = internalLinkMessageQueueDao;
    }

    public DistManufacturerService getDistManufacturerService() {
        return distManufacturerService;
    }

    public void setDistManufacturerService(final DistManufacturerService distManufacturerService) {
        this.distManufacturerService = distManufacturerService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public CategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

}
