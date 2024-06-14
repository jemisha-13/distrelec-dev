package com.distrelec.solrfacetsearch.provider.product.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistCountryValueResolver extends AbstractDistValueResolver<ItemModel> {

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext batchContext,
                                  IndexedProperty indexedProperty, ItemModel product,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        List<String> countries = batchContext.getFacetSearchConfig().getIndexConfig().getAllCountries()
                                             .stream()
                                             .map(CountryModel::getIsocode)
                                             .collect(Collectors.toList());
        addFieldValue(document, batchContext, indexedProperty, countries, resolverContext.getFieldQualifier());
    }
}
