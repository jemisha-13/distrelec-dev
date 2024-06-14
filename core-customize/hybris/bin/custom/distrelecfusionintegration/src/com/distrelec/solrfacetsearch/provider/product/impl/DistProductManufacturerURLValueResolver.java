package com.distrelec.solrfacetsearch.provider.product.impl;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistProductManufacturerURLValueResolver extends AbstractDistProductValueResolver {

    private static final String MANUFACTURER_URL_FIELD = "manufacturerUrl";

    private UrlResolver<DistManufacturerModel> manufacturerUrlResolver;

    protected DistProductManufacturerURLValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                                      UrlResolver<DistManufacturerModel> manufacturerUrlResolver, EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
        this.manufacturerUrlResolver = manufacturerUrlResolver;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        if (product.getManufacturer() != null) {
            String manufacturerUrl = manufacturerUrlResolver.resolve(product.getManufacturer());
            addFieldValue(document,
                          indexerBatchContext,
                          createNewIndexedProperty(indexedProperty, MANUFACTURER_URL_FIELD),
                          manufacturerUrl,
                          resolverContext.getFieldQualifier());
        }
    }
}
