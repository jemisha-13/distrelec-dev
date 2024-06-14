package com.distrelec.smartedit.populator;

import de.hybris.platform.cmsfacades.cmsitems.populators.CmsItemDefaultAttributesPopulator;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Map;

public class DistCmsItemDefaultAttributesPopulator extends CmsItemDefaultAttributesPopulator {

    @Override
    public void populate(ItemModel source, Map<String, Object> targetMap) throws ConversionException {
        super.populate(source, targetMap);
        targetMap.put("typeCode", source.getItemtype());
    }
}
