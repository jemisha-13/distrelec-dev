package com.namics.distrelec.b2b.facades.user.converters;

import com.namics.distrelec.b2b.core.model.DistFunctionModel;
import com.namics.distrelec.b2b.facades.user.data.DistFunctionData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.springframework.util.Assert;

public class DistFunctionConverter extends AbstractPopulatingConverter<DistFunctionModel, DistFunctionData> {

    @Override
    protected DistFunctionData createTarget() {
        return new DistFunctionData();
    }

    @Override
    public void populate(final DistFunctionModel source, final DistFunctionData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");
        target.setCode(source.getCode());
        target.setName(source.getRelevantName());
        super.populate(source, target);
    }
}
