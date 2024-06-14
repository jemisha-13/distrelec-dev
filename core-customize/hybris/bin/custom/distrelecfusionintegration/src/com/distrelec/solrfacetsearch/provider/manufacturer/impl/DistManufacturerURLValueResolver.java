package com.distrelec.solrfacetsearch.provider.manufacturer.impl;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistManufacturerURLValueResolver extends AbstractDistValueResolver<DistManufacturerModel> {
    private UrlResolver<DistManufacturerModel> manufacturerUrlResolver;

    public DistManufacturerURLValueResolver(UrlResolver<DistManufacturerModel> manufacturerUrlResolver) {
        this.manufacturerUrlResolver = manufacturerUrlResolver;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty,
                                  DistManufacturerModel manufacturer, ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        String manufacturerUrl = manufacturerUrlResolver.resolve(manufacturer);
        if (isNotBlank(manufacturerUrl)) {
            addFieldValue(document, indexerBatchContext, indexedProperty, manufacturerUrl, resolverContext.getFieldQualifier());
        }
    }
}
