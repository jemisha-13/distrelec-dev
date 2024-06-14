package com.distrelec.solrfacetsearch.provider.category.impl;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

import java.util.List;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistCategorySuperCategoriesValueResolver extends AbstractDistValueResolver<CategoryModel> {

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext batchContext,
                                  IndexedProperty indexedProperty,
                                  CategoryModel category,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        List<String> superCategoryCodes = emptyIfNull(category.getSupercategories())
                                                                                    .stream()
                                                                                    .map(CategoryModel::getCode)
                                                                                    .toList();

        if (isNotEmpty(superCategoryCodes)) {
            addFieldValue(document, batchContext, indexedProperty, superCategoryCodes, resolverContext.getFieldQualifier());
        }
    }
}
