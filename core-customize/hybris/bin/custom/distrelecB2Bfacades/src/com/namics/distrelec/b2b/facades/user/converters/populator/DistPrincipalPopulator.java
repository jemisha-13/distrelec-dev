package com.namics.distrelec.b2b.facades.user.converters.populator;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.user.DistUserService;

import de.hybris.platform.commercefacades.user.converters.populator.PrincipalPopulator;
import de.hybris.platform.commercefacades.user.data.PrincipalData;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.CustomerModel;

public class DistPrincipalPopulator extends PrincipalPopulator {

    @Autowired
    private DistUserService userService;

    @Override
    public void populate(final PrincipalModel source, final PrincipalData target) {
        super.populate(source, target);
        if (source instanceof CustomerModel) {
            final CustomerModel customer = (CustomerModel) source;
            if (customer.getCustomerType() != null) {
                target.setType(userService.getCustomerType(customer).getCode());
            }
        }
    }
}
