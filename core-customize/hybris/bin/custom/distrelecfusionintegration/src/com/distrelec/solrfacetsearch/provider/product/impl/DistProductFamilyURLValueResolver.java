package com.distrelec.solrfacetsearch.provider.product.impl;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistProductFamilyURLValueResolver extends AbstractDistProductValueResolver {

    private static final Logger LOG = LogManager.getLogger(DistProductFamilyURLValueResolver.class);

    private DistCategoryService categoryService;

    private DistUrlResolver<CategoryModel> distProductFamilyUrlResolver;

    protected DistProductFamilyURLValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                                EnumerationService enumerationService, DistCategoryService categoryService,
                                                DistUrlResolver<CategoryModel> distProductFamilyUrlResolver) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
        this.categoryService = categoryService;
        this.distProductFamilyUrlResolver = distProductFamilyUrlResolver;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        long t1 = System.currentTimeMillis();

        String pimFamilyCategoryCode = product.getPimFamilyCategoryCode();
        if (isBlank(pimFamilyCategoryCode)) {
            return;
        }

        Optional<CategoryModel> productFamily = categoryService.findProductFamily(pimFamilyCategoryCode);
        if (productFamily.isPresent()) {
            String productFamilyUrl = distProductFamilyUrlResolver.resolve(productFamily.get());
            addFieldValue(document, indexerBatchContext, indexedProperty, productFamilyUrl, resolverContext.getFieldQualifier());
        }
        LOG.debug("Building productFamily-Url took: {} ms", System.currentTimeMillis() - t1);
    }
}
