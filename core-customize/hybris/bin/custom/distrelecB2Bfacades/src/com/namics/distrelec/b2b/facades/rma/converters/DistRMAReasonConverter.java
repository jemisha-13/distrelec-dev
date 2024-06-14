package com.namics.distrelec.b2b.facades.rma.converters;

import com.namics.distrelec.b2b.core.model.DistCodelistModel;
import com.namics.distrelec.b2b.facades.rma.data.DistRMAReasonData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

public class DistRMAReasonConverter extends AbstractPopulatingConverter<DistCodelistModel, DistRMAReasonData> {

    @Override
    protected DistRMAReasonData createTarget() {
        return new DistRMAReasonData();
    }

    @Override
    public void populate(final DistCodelistModel source, final DistRMAReasonData target) {

        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setRelevantName(source.getRelevantName());

        super.populate(source, target);
    }
}
