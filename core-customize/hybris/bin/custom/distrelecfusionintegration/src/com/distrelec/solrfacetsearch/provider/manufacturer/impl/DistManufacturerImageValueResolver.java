package com.distrelec.solrfacetsearch.provider.manufacturer.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.BRAND_LOGO;

import java.util.Optional;

import com.distrelec.solrfacetsearch.provider.impl.AbstractDistValueResolver;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;

import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.indexer.IndexerBatchContext;
import de.hybris.platform.solrfacetsearch.indexer.spi.InputDocument;

public class DistManufacturerImageValueResolver extends AbstractDistValueResolver<DistManufacturerModel> {

    @Override
    protected void addFieldValues(final InputDocument document, final IndexerBatchContext indexerBatchContext, final IndexedProperty indexedProperty,
                                  final DistManufacturerModel manufacturer,
                                  final ValueResolverContext<Object, Object> resolverContext) throws FieldValueProviderException {
        MediaContainerModel mediaContainer = manufacturer.getPrimaryImage();
        if (mediaContainer == null) {
            return;
        }

        Optional<MediaModel> media = getMediaForFormat(mediaContainer, BRAND_LOGO);
        if (media.isPresent()) {
            document.addField(indexedProperty, media.get().getDownloadURL(), resolverContext.getFieldQualifier());
        }
    }
}
