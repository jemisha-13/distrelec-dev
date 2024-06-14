package com.distrelec.solrfacetsearch.provider.product.impl;

import static com.distrelec.solrfacetsearch.indexer.impl.DistProductDocumentContextProvider.STOCK_LEVELS;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.distrelec.solrfacetsearch.dao.DistProductSearchExportDAO;
import com.namics.distrelec.b2b.core.cms.daos.DistCMSSiteDao;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.enums.SolrPropertiesTypes;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistTotalInStockForCMSSiteValueResolver extends AbstractDistProductValueResolver {

    public DistTotalInStockForCMSSiteValueResolver(DistCMSSiteDao distCMSSiteDao, DistProductSearchExportDAO distProductSearchExportDAO,
                                                   EnumerationService enumerationService) {
        super(distCMSSiteDao, distProductSearchExportDAO, enumerationService);
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext batchContext,
                                  IndexedProperty indexedProperty, ProductModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        ImmutablePair<Long, Long> stocklevels = getDocumentContextAttribute(document, STOCK_LEVELS);

        if (stocklevels == null) {
            return;
        }
        document.addField(createNewIndexedProperty(indexedProperty, "availableForDelivery", SolrPropertiesTypes.BOOLEAN.toString()),
                          stocklevels.getLeft() > 0,
                          resolverContext.getFieldQualifier());

        document.addField(createNewIndexedProperty(indexedProperty, "availableForPickup", SolrPropertiesTypes.BOOLEAN.toString()),
                          stocklevels.getRight() > 0,
                          resolverContext.getFieldQualifier());
    }
}
