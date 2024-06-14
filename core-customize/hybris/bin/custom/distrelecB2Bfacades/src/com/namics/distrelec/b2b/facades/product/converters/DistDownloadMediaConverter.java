/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import com.namics.distrelec.b2b.core.model.DistDownloadMediaModel;
import com.namics.distrelec.b2b.facades.product.data.DistDownloadData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class DistDownloadMediaConverter extends AbstractPopulatingConverter<DistDownloadMediaModel, DistDownloadData> {

    private static final String PATH_DELIMITER = "/";

    @Autowired
    private Converter<MediaModel, ImageData> imageConverter;

    @Autowired
    private Converter<LanguageModel, LanguageData> languageConverter;

    @Override
    public void populate(final DistDownloadMediaModel source, final DistDownloadData target) {
        target.setName(extractFileName(source.getURL()));
        target.setDownloadUrl(source.getURL());
        target.setMimeType(source.getAssetType().getRelevantName());

        if (source.getLanguages() != null) {
            final List<LanguageData> languages = source.getLanguages().stream().map(languageModel -> getLanguageConverter().convert(languageModel))
                    .collect(Collectors.toList());
            target.setLanguages(languages);
        }

        if (source.getAssetType() != null && source.getAssetType().getIcon() != null) {
            target.setIcon(getImageConverter().convert(source.getAssetType().getIcon()));
        }

        super.populate(source, target);
    }

    private String extractFileName(final String url) {
        if (url == null || !url.contains(PATH_DELIMITER) || url.endsWith(PATH_DELIMITER)) {
            return url;
        } else {
            return url.substring(url.lastIndexOf(PATH_DELIMITER) + 1);
        }
    }

    public Converter<MediaModel, ImageData> getImageConverter() {
        return imageConverter;
    }

    public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter) {
        this.imageConverter = imageConverter;
    }

    public Converter<LanguageModel, LanguageData> getLanguageConverter() {
        return languageConverter;
    }

    public void setLanguageConverter(final Converter<LanguageModel, LanguageData> languageConverter) {
        this.languageConverter = languageConverter;
    }
}
