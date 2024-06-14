package com.namics.distrelec.b2b.facades.order.converters.populator;

import de.hybris.platform.commercefacades.order.converters.populator.CartModificationPopulator;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModification;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DistMinCartModificationPopulator extends CartModificationPopulator {

    @Override
    public void populate(final CommerceCartModification source, final CartModificationData target) throws ConversionException {
        if (source.getEntry() != null) {
            target.setEntry(getOrderEntryConverter().convert(source.getEntry()));
            if (source.getEntry().getOrder() != null) {
                target.setCartCode(source.getEntry().getOrder().getCode());
            }
        }
        target.setStatusCode(source.getStatusCode());
        target.setQuantity(source.getQuantity());
        target.setQuantityAdded(source.getQuantityAdded());
        target.setDeliveryModeChanged(source.getDeliveryModeChanged());
    }
}
