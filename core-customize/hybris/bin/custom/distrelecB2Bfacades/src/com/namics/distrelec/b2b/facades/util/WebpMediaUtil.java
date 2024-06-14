package com.namics.distrelec.b2b.facades.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;

public class WebpMediaUtil {
    public static ImageData getImageDataFromListByType(final String imageType, String fallbackImageType, final List<Map<String, ImageData>> images) {
        if (CollectionUtils.isEmpty(images)) {
            return null;
        }
        return getImageDataFromMapByType(imageType, fallbackImageType, images.iterator().next());
    }

    public static ImageData getImageDataFromMapByType(final String imageType, String fallbackImageType, final Map<String, ImageData> image) {
        if (MapUtils.isEmpty(image)) {
            return null;
        }
        return image.getOrDefault(imageType, image.get(fallbackImageType));
    }

    public static MediaModel getMediaModelByType(final String imageType, String fallbackImageType, final MediaContainerModel mediaContainer) {
        if (mediaContainer == null) {
            return null;
        }
        MediaModel mediaModel = getImageByFormat(imageType, mediaContainer.getMedias());
        return mediaModel != null ? mediaModel : getImageByFormat(fallbackImageType, mediaContainer.getMedias());
    }

    public static MediaModel getImageByFormat(String imageFormat, Collection<MediaModel> medias) {
        if (imageFormat == null || medias == null) {
            return null;
        }
        for (final MediaModel media : medias) {
            if (media.getMediaFormat() != null && imageFormat.equals(media.getMediaFormat().getQualifier())) {
                return media;
            }
        }
        return null;
    }
}
