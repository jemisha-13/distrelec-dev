/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.cms.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Collection;
import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.namics.distrelec.b2b.core.model.DistContentPageMappingModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import com.namics.distrelec.b2b.core.model.pages.DistManufacturerPageModel;
import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.core.service.cms.dao.DistCmsPageDao;
import com.namics.distrelec.b2b.core.service.data.DistCmsDataFactory;
import com.namics.distrelec.b2b.core.service.data.DistRestrictionData;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSPageService;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;

public class DefaultDistCmsPageService extends DefaultCMSPageService implements DistCmsPageService {

    private static final String CATALOG_PLUS_PRODUCT_PAGE = "catalogPlusProductDetailsPage";

    private DistCmsDataFactory distCmsDataFactory;

    @Override
    public DistManufacturerPageModel getPageForManufacturer(final DistManufacturerModel manufacturer) throws CMSItemNotFoundException {
        final DistManufacturerPageModel page = (DistManufacturerPageModel) getSinglePage("DistManufacturerPage");
        if (page != null) {
            LOG.debug("Only one ManufacturerStoreDetailPage for Manufacturer [" + manufacturer.getCode() + "] found. Considering this as default.");
            return page;
        }
        final ComposedTypeModel type = getTypeService().getComposedTypeForCode("DistManufacturerPage");
        final Collection<CatalogVersionModel> versions = getCatalogVersionService().getSessionCatalogVersions();
        final DistRestrictionData data = distCmsDataFactory.createRestrictionData(manufacturer);
        final Collection pages = getCmsPageDao().findAllPagesByTypeAndCatalogVersions(type, versions);
        final Collection result = getCmsRestrictionService().evaluatePages(pages, data);
        if (result.isEmpty()) {
            throw new CMSItemNotFoundException("No page for manufacturer [" + manufacturer.getCode() + "] found.");
        }
        if (result.size() > 1) {
            LOG.warn("More than one page found for manufacturer [" + manufacturer.getCode() + "]. Returning default.");
        }
        return (DistManufacturerPageModel) result.iterator().next();
    }

    @Override
    public DistContentPageMappingModel getContentPageMappingForShortURL(final String shortURL) {
        DistContentPageMappingModel mappingModel = null;
        final CMSSiteModel cmsSite = getCurrentSite();
        if (StringUtils.isNotBlank(shortURL)) {
            mappingModel = getDistCmsPageDao().findActiveContentPageMapping(shortURL, cmsSite);
        }
        return mappingModel;
    }

    @Override
    public Collection<DistContentPageMappingModel> getAllActiveContentPageMappings(String baseSiteId) {
        CMSSiteModel cmsSite = (CMSSiteModel) this.getBaseSiteService().getBaseSiteForUID(baseSiteId);
        return getDistCmsPageDao().findAllActiveContentPageMappings(cmsSite);
    }

    @Override
    public ProductPageModel getPageForProduct(final ProductModel product) throws CMSItemNotFoundException {
        if (StringUtils.isNotEmpty(product.getCatPlusSupplierAID())) {
            return (ProductPageModel) getPageForId(CATALOG_PLUS_PRODUCT_PAGE);
        }
        return super.getPageForProduct(product);
    }

    @Override
    public ProductPageModel getPageForProductCode(String productCode) throws CMSItemNotFoundException {
        ProductModel product = null;
        try {
            product = getProductService().getProductForCode(productCode);
        } catch (Exception var4) {
            // ignore exception as product is not required
        }
        return super.getPageForProduct(product);
    }

    @Override
    public Optional<ProductFamilyPageModel> findProductFamilyPage(String code, Collection<CatalogVersionModel> catalogVersions) {
        Optional<ProductFamilyPageModel> page;
        if (isNotBlank(code)) {
            page = getDistCmsPageDao().findProductFamilySpecificPage(code, catalogVersions);
        } else {
            page = Optional.empty();
        }
        return page.isPresent() ? page : getDistCmsPageDao().findDefaultProductFamilyPage(catalogVersions);
    }

    @Override
    public ContentPageModel getPageForLabelOrId(String labelOrId, PagePreviewCriteriaData pagePreviewCriteria) throws CMSItemNotFoundException {
        if (labelOrId != null) {
            return super.getPageForLabelOrId(labelOrId, pagePreviewCriteria);
        }
        return null;
    }

    public DistCmsDataFactory getDistCmsDataFactory() {
        return distCmsDataFactory;
    }

    @Required
    public void setDistCmsDataFactory(final DistCmsDataFactory distCmsDataFactory) {
        this.distCmsDataFactory = distCmsDataFactory;
    }

    private DistCmsPageDao getDistCmsPageDao() {
        return (DistCmsPageDao) getCmsPageDao();
    }
}
