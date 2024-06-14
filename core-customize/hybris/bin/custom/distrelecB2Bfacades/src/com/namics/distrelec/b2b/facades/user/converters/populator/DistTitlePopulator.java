package com.namics.distrelec.b2b.facades.user.converters.populator;

import de.hybris.platform.commercefacades.user.converters.populator.TitlePopulator;
import de.hybris.platform.commercefacades.user.data.TitleData;
import de.hybris.platform.core.model.user.TitleModel;

public class DistTitlePopulator extends TitlePopulator {

    @Override
    public void populate(final TitleModel source, final TitleData target) {
        super.populate(source, target);
        target.setSapCode(source.getSapCode());
        target.setActive(source.isActive());
    }
}
