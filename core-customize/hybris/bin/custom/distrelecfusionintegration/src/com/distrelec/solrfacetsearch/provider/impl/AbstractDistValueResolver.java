package com.distrelec.solrfacetsearch.provider.impl;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.solrfacetsearch.provider.impl.ModelAttributesValueResolver;

public class AbstractDistValueResolver<T extends ItemModel> extends ModelAttributesValueResolver<T> {

    protected Optional<MediaModel> getMediaForFormat(MediaContainerModel mediaContainer, String mediaFormat) {
        if (StringUtils.isBlank(mediaFormat)) {
            return Optional.empty();
        }
        return mediaContainer.getMedias().stream()
                             .filter(media -> this.matchesFormat(media, mediaFormat))
                             .findAny();
    }

    protected boolean matchesFormat(MediaModel media, String mediaFormat) {
        return media.getMediaFormat() != null && mediaFormat.equals(media.getMediaFormat().getQualifier());
    }

}
