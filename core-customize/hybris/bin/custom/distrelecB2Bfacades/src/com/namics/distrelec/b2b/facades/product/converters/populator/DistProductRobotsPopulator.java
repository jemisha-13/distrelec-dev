package com.namics.distrelec.b2b.facades.product.converters.populator;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.seo.data.MetaData;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.enums.CmsRobotTag;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ProductPageModel;
import de.hybris.platform.cmsfacades.data.AbstractPageData;
import de.hybris.platform.cmsfacades.pages.PageFacade;
import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistProductRobotsPopulator extends AbstractProductPopulator<ProductModel, ProductData> {

    private static final Logger LOG = LoggerFactory.getLogger(DistProductRobotsPopulator.class);

    /**
     * Robots tag will be cached as it is not volatile and should not be changed without an application restart or a deployment.
     */
    private String productPageRobotsTag;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    private ProductService productService;

    @Autowired
    private PageFacade pageFacade;

    @Override
    public void populate(ProductModel product, ProductData target) throws ConversionException {
        String productCode = product.getCode();
        final DistSalesStatusModel salesStatus = productFacade.getProductSalesStatusModel(productCode);

        String robotTag;
        if (salesStatus == null) {
            robotTag = CmsRobotTag.NOINDEX_NOFOLLOW.name();
        } else if (salesStatus.isEndOfLifeInShop() && isNoCrawlCategory(product)) {
            robotTag = CmsRobotTag.NOINDEX_FOLLOW.name();
        } else {
            robotTag = getProductPageRobotTag();
        }

        setRobotsTag(robotTag, target);
    }

    private boolean isNoCrawlCategory(ProductModel product) {
        final List<String> productCategoryInfo = new ArrayList<>();
        if (product.getPrimarySuperCategory() != null) {
            productCategoryInfo.add(product.getPrimarySuperCategory().getCode());
        }
        for (final CategoryModel category : product.getSupercategories()) {
            fillCategories(category, productCategoryInfo);
        }
        Configuration configuration = configurationService.getConfiguration();
        final String noCrawlCategoryCodesConfig = configuration.getString("specialwebshop.category.nocrawl");

        if (isBlank(noCrawlCategoryCodesConfig)) {
            return false;
        }

        Set<String> noCrawlCategories = new HashSet<>(Arrays.asList(noCrawlCategoryCodesConfig.split(",")));

        return productCategoryInfo.stream()
                                  .anyMatch(noCrawlCategories::contains);
    }

    private void fillCategories(final CategoryModel category, final List<String> productCategoryInfo) {
        for (final CategoryModel superCategory : category.getSupercategories()) {
            if ((superCategory != null && !(superCategory instanceof ClassificationClassModel)) && //
                    superCategory.getLevel().equals(Integer.valueOf(category.getLevel().intValue() - 1)) && //
                    superCategory.getLevel().intValue() > 0) {
                productCategoryInfo.add(superCategory.getCode());
                fillCategories(superCategory, productCategoryInfo);
            }
        }
    }

    /**
     * Reuses the same logic as used by PageController.
     */
    private String getProductPageRobotTag() {
        if (productPageRobotsTag == null) {
            synchronized (this) {
                if (productPageRobotsTag == null) {
                    try {
                        AbstractPageData pageData = pageFacade.getPageData(ProductPageModel._TYPECODE, null, null);
                        String robotTag = pageData.getRobotTag();
                        productPageRobotsTag = robotTag;
                    } catch (CMSItemNotFoundException e) {
                        LOG.warn("Unable to resolve page", e);
                        return null;
                    }
                }
            }
        }
        return productPageRobotsTag;
    }

    private void setRobotsTag(String robotTag, ProductData target) {
        MetaData metaData = target.getMetaData();
        if (metaData == null) {
            metaData = new MetaData();
            target.setMetaData(metaData);
        }
        final MetaData meta = metaData;

        meta.setRobots(robotTag);
    }
}
