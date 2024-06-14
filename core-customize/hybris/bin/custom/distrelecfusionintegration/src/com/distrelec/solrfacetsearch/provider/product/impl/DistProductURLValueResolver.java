package com.distrelec.solrfacetsearch.provider.product.impl;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistProductURLValueResolver extends AbstractDistProductValueResolver {

    private static final String PRODUCT_URL_FIELD = "productUrl";

    private UrlResolver<ProductModel> productUrlResolver;

    protected DistProductURLValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                          UrlResolver<ProductModel> productUrlResolver, EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
        this.productUrlResolver = productUrlResolver;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        String productUrl = productUrlResolver.resolve(product);
        addFieldValue(document,
                      indexerBatchContext,
                      createNewIndexedProperty(indexedProperty, PRODUCT_URL_FIELD),
                      productUrl,
                      resolverContext.getFieldQualifier());
    }
}
