package com.namics.distrelec.b2b.facades.user.converters;

import com.namics.distrelec.b2b.core.model.DistDepartmentModel;
import com.namics.distrelec.b2b.facades.user.data.DistDepartmentData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import org.springframework.util.Assert;

public class DistDepartmentConverter extends AbstractPopulatingConverter<DistDepartmentModel, DistDepartmentData> {

    @Override
    protected DistDepartmentData createTarget() {
        return new DistDepartmentData();
    }

    @Override
    public void populate(final DistDepartmentModel source, final DistDepartmentData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");
        target.setCode(source.getCode());
        target.setName(source.getRelevantName());
        super.populate(source, target);
    }
}
