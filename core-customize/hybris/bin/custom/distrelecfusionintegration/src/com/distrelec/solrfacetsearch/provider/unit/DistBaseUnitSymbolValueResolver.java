package com.distrelec.solrfacetsearch.provider.unit;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;
import com.namics.distrelec.b2b.core.service.classification.dao.DistClassificationDao;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistBaseUnitSymbolValueResolver extends AbstractDistValueResolver<ClassificationAttributeUnitModel> {
    private DistClassificationDao distClassificationDao;

    public DistBaseUnitSymbolValueResolver(DistClassificationDao distClassificationDao) {
        this.distClassificationDao = distClassificationDao;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
                                  ClassificationAttributeUnitModel unit,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        ClassificationAttributeUnitModel baseUnitForUnit = distClassificationDao.findBaseUnitForUnit(unit);

        if (baseUnitForUnit != null) {
            addFieldValue(document, indexerBatchContext, indexedProperty, baseUnitForUnit.getSymbol(), resolverContext.getFieldQualifier());
        }
    }
}
