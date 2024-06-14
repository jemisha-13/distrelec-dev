package com.namics.distrelec.b2b.facades.compare.converter;

import java.util.Locale;

import de.hybris.platform.commercefacades.storesession.converters.populator.LanguagePopulator;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.c2l.LanguageModel;

public class DistLanguagePopulator extends LanguagePopulator<LanguageModel, LanguageData> {

    @Override
    public void populate(final LanguageModel source, final LanguageData target) {
        super.populate(source, target);
        // target.setIsocodepim(source.getIsocodePim());
        target.setNameEN(source.getName(Locale.ENGLISH));
        target.setRank(source.getRank());
    }
}
