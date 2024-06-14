 /*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */
 package com.namics.distrelec.b2b.core.message.queue.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.message.queue.dao.InternalLinkMessageDao;
import com.namics.distrelec.b2b.core.message.queue.dao.RelatedItemData;
import com.namics.distrelec.b2b.core.message.queue.strategies.ProcessCategoryDirectlyOrRecursivelyStrategy;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.site.BaseSiteService;

/**
 * {@code DefaultInternalLinkMessageQueueService}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public class DefaultInternalLinkMessageQueueService implements InternalLinkMessageQueueService {

    // private static final Logger LOG = LogManager.getLogger(DefaultInternalLinkMessageQueueService.class);

    @Autowired
    @Qualifier("internalLinkMessageQueueDao")
    private InternalLinkMessageDao internalLinkMessageQueueDao;

    @Autowired
    private ProductService productService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private DistCategoryService categoryService;

    @Autowired
    private DistManufacturerService manufacturerService;

    @Autowired
    private ProcessCategoryDirectlyOrRecursivelyStrategy processCategoryDirectlyOrRecursivelyStrategy;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchRelatedProductsForProduct(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<ProductModel> fetchRelatedProductsForProduct(final String productCode, final String site) {
        return internalLinkMessageQueueDao.fetchRelatedProductsForProduct(getProductService().getProductForCode(productCode),
                (CMSSiteModel) getBaseSiteService().getBaseSiteForUID(site));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchRelatedCategoriesForProduct(java.lang.
     * String)
     */
    @Override
    public List<CategoryModel> fetchRelatedCategoriesForProduct(final String productCode, final String site) {
        return internalLinkMessageQueueDao.fetchRelatedCategoriesForProduct(getProductService().getProductForCode(productCode),
                (CMSSiteModel) getBaseSiteService().getBaseSiteForUID(site));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchRelatedManufacturersForProduct(java.lang.
     * String)
     */
    @Override
    public List<DistManufacturerModel> fetchRelatedManufacturersForProduct(final String productCode, final String site,
            final List<CategoryModel> preSelectedCategories) {
        return internalLinkMessageQueueDao.fetchRelatedManufacturersForProduct(getProductService().getProductForCode(productCode),
                (CMSSiteModel) getBaseSiteService().getBaseSiteForUID(site), preSelectedCategories);
    }

    /*
     * https://jira.distrelec.com/browse/DISTRELEC-11505 - Categories point 1 (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchRelatedManufacturersForCategory(java.lang.
     * String, java.lang.String)
     */
    @Override
    public List<RelatedItemData<DistManufacturerModel>> fetchRelatedManufacturersForCategory(final String categoryCode, final String site) {
        final CategoryModel category = getCategoryService().getCategoryForCode(categoryCode);
        return internalLinkMessageQueueDao.fetchRelatedManufacturersForCategory(
                getProcessCategoryDirectlyOrRecursivelyStrategy().getCategoriesToSearchInto(category),
                (CMSSiteModel) getBaseSiteService().getBaseSiteForUID(site));
    }

    /*
     * https://jira.distrelec.com/browse/DISTRELEC-11505 - Categories point 2 (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchRelatedCategoriesForCategory(List<
     * DistManufacturerModel>, java.lang.String)
     */
    @Override
    public List<RelatedItemData<CategoryModel>> fetchRelatedCategoriesForCategory(final List<DistManufacturerModel> manufacturers, final String site) {
        return internalLinkMessageQueueDao.fetchRelatedCategoriesForCategory(manufacturers, (CMSSiteModel) getBaseSiteService().getBaseSiteForUID(site));
    }

    /*
     * https://jira.distrelec.com/browse/DISTRELEC-11505 - Categories point 3 (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchRelatedProductsForCategory(java.lang.
     * String, java.lang.String)
     */
    @Override
    public List<RelatedItemData<ProductModel>> fetchRelatedProductsForCategory(final String categoryCode, final String site) {
        final CategoryModel category = getCategoryService().getCategoryForCode(categoryCode);
        return internalLinkMessageQueueDao.fetchRelatedProductsForCategory(
                getProcessCategoryDirectlyOrRecursivelyStrategy().getCategoriesToSearchInto(category),
                (CMSSiteModel) getBaseSiteService().getBaseSiteForUID(site));
    }

    /*
     * https://jira.distrelec.com/browse/DISTRELEC-12852 - Categories point 4 (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchTopSellersOfManufacturer(de.hybris.platform.
     * category.model.CategoryModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<RelatedItemData<ProductModel>> fetchTopSellersOfCategory(final CategoryModel categoryModel, final CMSSiteModel siteModel) {
        return internalLinkMessageQueueDao
                .fetchTopSellersInCategories(getProcessCategoryDirectlyOrRecursivelyStrategy().getCategoriesToSearchInto(categoryModel), siteModel);
    }

    /*
     * https://jira.distrelec.com/browse/DISTRELEC-11506 - Manufacturer point 1 (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchRelatedCategoriesForManufacturer(
     * com.namics.distrelec.b2b.core.model.DistManufacturerModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<CategoryModel> fetchRelatedCategoriesForManufacturer(final DistManufacturerModel manufacturer, final CMSSiteModel site) {
        return internalLinkMessageQueueDao.fetchRelatedCategoriesForManufacturer(manufacturer, site);
    }

    /*
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 2 (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchRelatedManufacturers(com.namics.distrelec.
     * b2b.core.model.DistManufacturerModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Deprecated
    @Override
    public List<DistManufacturerModel> fetchRelatedManufacturers(final DistManufacturerModel manufacturer, final CMSSiteModel site) {
        final List<CategoryModel> categories = fetchRelatedCategoriesForManufacturer(manufacturer, site);
        return fetchRelatedManufacturers(manufacturer, site, categories);
    }

    /*
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 2 (non-Javadoc)
     * 
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchRelatedManufacturers(java.util.List,
     * de.hybris.platform.cms2.model.site.CMSSiteModel, java.util.List)
     */
    @Override
    public List<DistManufacturerModel> fetchRelatedManufacturers(final DistManufacturerModel manufacturer, final CMSSiteModel site,
            final List<CategoryModel> categories) {
        return internalLinkMessageQueueDao.fetchRelatedManufacturers(categories, site,
                Collections.singletonList(manufacturer));
    }

    /*
     * https://jira.distrelec.com/browse/DISTRELEC-11506 Manufacturer point 3 (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchNewArrivalsOfManufacturer(com.namics.
     * distrelec.b2b.core.model.DistManufacturerModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<ProductModel> fetchNewArrivalsOfManufacturer(final DistManufacturerModel manufacturerModel, final CMSSiteModel siteModel) {
        return internalLinkMessageQueueDao.fetchNewArrivalsOfManufacturer(manufacturerModel, siteModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchNewArrivalsOfCategory(de.hybris.platform.
     * category.model.CategoryModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<ProductModel> fetchNewArrivalsOfCategory(final CategoryModel categoryModel, final CMSSiteModel siteModel) {
        return internalLinkMessageQueueDao
                .fetchNewArrivalsOfCategories(getProcessCategoryDirectlyOrRecursivelyStrategy().getCategoriesToSearchInto(categoryModel), siteModel);
    }
    
    /*
     * https://jira.distrelec.com/browse/DISTRELEC-12853 - Manufacturer point 4 (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.service.InternalLinkMessageQueueService#fetchTopSellersOfManufacturer(com.namics.
     * distrelec.b2b.core.model.DistManufacturerModel, de.hybris.platform.cms2.model.site.CMSSiteModel)
     */
    @Override
    public List<ProductModel> fetchTopSellersOfManufacturer(final DistManufacturerModel manufacturerModel, final CMSSiteModel siteModel) {
        return internalLinkMessageQueueDao.fetchTopSellersOfManufacturer(manufacturerModel, siteModel);
    }

    // Getters & Setters

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public InternalLinkMessageDao getInternalLinkMessageQueueDao() {
        return internalLinkMessageQueueDao;
    }

    public void setInternalLinkMessageQueueDao(final InternalLinkMessageDao internalLinkMessageQueueDao) {
        this.internalLinkMessageQueueDao = internalLinkMessageQueueDao;
    }

    public DistCategoryService getCategoryService() {
        return categoryService;
    }

    public void setCategoryService(final DistCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public DistManufacturerService getManufacturerService() {
        return manufacturerService;
    }

    public void setManufacturerService(final DistManufacturerService manufacturerService) {
        this.manufacturerService = manufacturerService;
    }

    public ProcessCategoryDirectlyOrRecursivelyStrategy getProcessCategoryDirectlyOrRecursivelyStrategy() {
        return processCategoryDirectlyOrRecursivelyStrategy;
    }

    public void setProcessCategoryDirectlyOrRecursivelyStrategy(
            final ProcessCategoryDirectlyOrRecursivelyStrategy processCategoryDirectlyOrRecursivelyStrategy) {
        this.processCategoryDirectlyOrRecursivelyStrategy = processCategoryDirectlyOrRecursivelyStrategy;
    }


}
