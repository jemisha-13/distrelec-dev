/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import com.namics.distrelec.b2b.facades.product.data.DistVideoData;

import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

public class DistVideoMediaConverter extends AbstractPopulatingConverter<DistVideoMediaModel, DistVideoData> {

    @Autowired
    private Converter<LanguageModel, LanguageData> languageConverter;

    @Override
    protected DistVideoData createTarget() {
        return new DistVideoData();
    }

    @Override
    public void populate(final DistVideoMediaModel source, final DistVideoData target) {
        target.setBrightcoveVideoId(source.getCode());
        target.setBrightcovePlayerId(source.getBrightcoveVideoId());
        target.setYoutubeUrl(source.getYoutubeUrl());

        if (source.getLanguages() != null) {
            List<LanguageData> languages = source.getLanguages()
                                                 .stream()
                                                 .map(language -> languageConverter.convert(language))
                                                 .collect(Collectors.toList());
            target.setLanguages(languages);
        }

        super.populate(source, target);
    }
}
