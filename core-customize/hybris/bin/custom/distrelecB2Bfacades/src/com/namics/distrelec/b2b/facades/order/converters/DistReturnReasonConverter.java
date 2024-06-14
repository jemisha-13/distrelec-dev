package com.namics.distrelec.b2b.facades.order.converters;

import com.distrelec.webservice.if19.v1.ReturnReasons;
import de.hybris.platform.commercefacades.order.data.DistReturnReasonData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

public class DistReturnReasonConverter extends AbstractPopulatingConverter<ReturnReasons, DistReturnReasonData> {

    @Override
    public void populate(final ReturnReasons source, final DistReturnReasonData target) {
        target.setReturnReasonId(source.getReturnReasonID());
        target.setReturnReasonDesc(source.getReturnReasonDescription());
    }
}
