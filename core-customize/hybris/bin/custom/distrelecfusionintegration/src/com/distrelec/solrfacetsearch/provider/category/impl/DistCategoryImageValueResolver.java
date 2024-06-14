package com.distrelec.solrfacetsearch.provider.category.impl;

import java.util.Optional;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;
import com.namics.distrelec.b2b.core.constants.DistConstants;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistCategoryImageValueResolver extends AbstractDistValueResolver<CategoryModel> {

    @Override
    protected void addFieldValues(InputDocument document, IndexerBatchContext indexerBatchContext, IndexedProperty indexedProperty, CategoryModel category,
                                  ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        MediaContainerModel mediaContainer = category.getPrimaryImage();
        if (mediaContainer == null) {
            return;
        }
        Optional<MediaModel> media = getMediaForFormat(mediaContainer, DistConstants.MediaFormat.PORTRAIT_SMALL);

        if (media.isPresent()) {
            addFieldValue(document, indexerBatchContext, indexedProperty, media.get().getInternalURL(), resolverContext.getFieldQualifier());
        }
    }
}
