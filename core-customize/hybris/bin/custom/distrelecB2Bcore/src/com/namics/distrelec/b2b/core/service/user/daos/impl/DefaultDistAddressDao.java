/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user.daos.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.namics.distrelec.b2b.core.service.user.daos.DistAddressDao;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.daos.impl.DefaultAddressDao;

public class DefaultDistAddressDao extends DefaultAddressDao implements DistAddressDao {

    @Override
    public AddressModel findAddressesForOwner(final String erpAddressId, final Boolean duplicate) {

        final Map<String, Object> parameters = new HashMap<String, Object>();
        if (duplicate != null) {
            parameters.put(AddressModel.DUPLICATE, duplicate);
        }
        parameters.put(AddressModel.ERPADDRESSID, erpAddressId);

        final List<AddressModel> results = find(parameters);
        return results == null || results.isEmpty() ? null : results.get(0);
    }

}
