package com.distrelec.solrfacetsearch.provider.category.impl;

import java.util.Set;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistCategoryShopVisibilityValueResolver extends AbstractDistValueResolver<CategoryModel> {

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext batchContext, IndexedProperty indexedProperty, CategoryModel category,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        boolean isVisibleInShop = isCategoryVisible(batchContext, category);
        addFieldValue(document, batchContext, indexedProperty, isVisibleInShop, resolverContext.getFieldQualifier());
    }

    /**
     * DISTRELEC-32227
     * All the visible categories for CMSSite are preloaded in the beginning - DistSolrIndexerJob.
     */
    private boolean isCategoryVisible(IndexerBatchContext batchContext, CategoryModel category) {
        return getCategoriesMarkedAsVisible(batchContext).contains(category.getCode());
    }

    private Set<String> getCategoriesMarkedAsVisible(IndexerBatchContext batchContext) {
        return batchContext.getFacetSearchConfig().getIndexConfig().getVisibleCategoryCodes();
    }

}
