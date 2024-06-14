package com.namics.distrelec.b2b.core.service.manufacturer.model;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

public class ManufacturerSafeUrlNameAttributeHandler extends AbstractDynamicAttributeHandler<String, DistManufacturerModel> {

    @Override
    public String get(DistManufacturerModel product) {
        String rawName = product.getCode();
        return DistUtils.urlSafe(rawName);
    }
}
