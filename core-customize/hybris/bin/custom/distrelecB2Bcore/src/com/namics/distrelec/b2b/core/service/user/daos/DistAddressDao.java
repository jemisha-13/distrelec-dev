/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user.daos;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.daos.AddressDao;

public interface DistAddressDao extends AddressDao {

    /**
     * Finds all addresses for the given erpAddressId.
     * 
     * @param erpAddressId
     *            the erpAddressId of the address.
     * @param duplicate
     *            if address is a duplicate.
     * @return null if for the given <code>erpAddressId</code> no {@link AddressModel} was found.
     */
    AddressModel findAddressesForOwner(String erpAddressId, Boolean duplicate);

}
