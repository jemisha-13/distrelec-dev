/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.product.converters;

import com.namics.distrelec.b2b.core.model.DistTransportGroupModel;
import com.namics.distrelec.b2b.facades.product.data.DistTransportGroupData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * {@code DistTransportGroupConverter}
 *
 *
 * @author Abhinay Jadhav, Distrelec
 * @since Distrelec 5.17
 */
public class DistTransportGroupConverter extends AbstractPopulatingConverter<DistTransportGroupModel, DistTransportGroupData> {

    @Override
    protected DistTransportGroupData createTarget() {
        return new DistTransportGroupData();
    }

    @Override
    public void populate(final DistTransportGroupModel source, final DistTransportGroupData target) {
        target.setCode(source.getCode());
        target.setName(source.getName());
        target.setNameErp(source.getNameErp());
        target.setRelevantName(source.getRelevantName());
        target.setDangerous(source.isDangerous());
        target.setBulky(source.isBulky());
    }
}
