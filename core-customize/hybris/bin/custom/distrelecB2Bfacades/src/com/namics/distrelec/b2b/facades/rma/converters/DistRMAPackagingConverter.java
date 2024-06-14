package com.namics.distrelec.b2b.facades.rma.converters;

import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.facades.rma.data.DistRMAPackagingData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
public class DistRMAPackagingConverter extends AbstractPopulatingConverter<DistCodelistModel, DistRMAPackagingData> {

    @Override
    protected DistRMAPackagingData createTarget() {
        return new DistRMAPackagingData();
    }

    @Override
    public void populate(final DistCodelistModel source, final DistRMAPackagingData target) {

        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setRelevantName(source.getRelevantName());

        super.populate(source, target);
    }
}
