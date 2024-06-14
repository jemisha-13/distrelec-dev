package com.distrelec.solrfacetsearch.provider.category.impl;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistCategoryURLValueResolver extends AbstractDistValueResolver<CategoryModel> {

    private DistUrlResolver<CategoryModel> categoryModelUrlResolver;

    public DistCategoryURLValueResolver(DistUrlResolver<CategoryModel> categoryModelUrlResolver) {
        this.categoryModelUrlResolver = categoryModelUrlResolver;
    }

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, CategoryModel category,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        String categoryURL = categoryModelUrlResolver.resolve(category);
        if (isNotBlank(categoryURL)) {
            addFieldValue(document, indexerBatchContext, indexedProperty, categoryURL, resolverContext.getFieldQualifier());
        }
    }
}
