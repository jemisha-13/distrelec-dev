/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import java.util.List;

import com.namics.distrelec.b2b.core.model.delivery.AbstractDistDeliveryModeModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;

/**
 * Service to provide shipping options.
 * 
 * @author pbueschi, Namics AG
 * @since Distrelec 1.0
 */
public interface ShippingOptionService {

    String getPickupDeliveryModeCode();

    AbstractDistDeliveryModeModel getAbstractDistDeliveryModeForDistShippingMethodCode(final String distShippingMethodCode);

    boolean updateDefaultShippingOption(final AbstractDistDeliveryModeModel shippingOption);

    List<AbstractDistDeliveryModeModel> getSupportedShippingOptionsForUser(B2BCustomerModel b2bCustomer, List<String> ignoredOptions);

    AbstractDistDeliveryModeModel getDefaultShippingOptionForUser(B2BCustomerModel b2bCustomer);

    void setErpDefaultShippingOptionForUser(B2BCustomerModel customer);
}
