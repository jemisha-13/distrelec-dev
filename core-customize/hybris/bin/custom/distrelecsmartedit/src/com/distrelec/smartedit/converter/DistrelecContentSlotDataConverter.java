package com.distrelec.smartedit.converter;

import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cmsfacades.data.PageContentSlotData;
import de.hybris.platform.cmsfacades.pagescontentslots.converter.ContentSlotDataConverter;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistrelecContentSlotDataConverter extends ContentSlotDataConverter {

    @Override
    public PageContentSlotData convert(ContentSlotData source, PageContentSlotData target) throws ConversionException {
        super.convert(source, target);

        target.setName(source.getName());

        return target;
    }
}
