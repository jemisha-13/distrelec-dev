/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.AddressService;

public interface DistAddressService extends AddressService {

    /**
     * Get the B2BUnit address by erpAddressId.
     * 
     * @param owner
     * @param erpAddressId
     * @return the address
     */
    public AddressModel getAddressByErpAddressId(final ItemModel owner, final String erpAddressId);

    /**
     * Getaddress by erpAddressId.
     * 
     * @param erpAddressId
     * @param duplicate
     * @return the address
     */
    public AddressModel getAddressByErpAddressId(final String erpAddressId, final Boolean duplicate);

    boolean isAddressValid(AddressModel address);

}
