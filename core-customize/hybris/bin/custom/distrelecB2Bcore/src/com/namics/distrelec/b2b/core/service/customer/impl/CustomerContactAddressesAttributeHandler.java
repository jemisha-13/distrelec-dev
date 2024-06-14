package com.namics.distrelec.b2b.core.service.customer.impl;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomerContactAddressesAttributeHandler extends AbstractDynamicAttributeHandler<Collection<AddressModel>, CustomerModel> {

    @Override
    public Collection<AddressModel> get(CustomerModel customer) {
        return customer.getAddresses().stream()
                .filter(address -> BooleanUtils.isTrue(address.getContactAddress()))
                .collect(Collectors.toList());
    }

}
