package com.namics.distrelec.b2b.facades.customer;

import de.hybris.platform.commercefacades.user.data.RegisterData;
import de.hybris.platform.commerceservices.enums.CustomerType;

public interface DistCustomerRegistrationFacade {

    void register(RegisterData registerData, final CustomerType customerType) throws Exception;

    void activate(RegisterData registerData, final CustomerType customerType) throws Exception;

}
