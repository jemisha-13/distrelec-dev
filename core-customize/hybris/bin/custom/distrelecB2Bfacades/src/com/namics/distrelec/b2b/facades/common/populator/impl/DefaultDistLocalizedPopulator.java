package com.namics.distrelec.b2b.facades.common.populator.impl;

import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;

import java.util.Locale;
import java.util.Objects;

public class DefaultDistLocalizedPopulator extends DefaultLocalizedPopulator {

    @Override
    public String getLanguage(final Locale locale) {
        if (Objects.isNull(locale)) {
            throw new IllegalArgumentException("Locale cannot be null");
        }
        return locale.toLanguageTag().replace('-', '_');
    }
}
