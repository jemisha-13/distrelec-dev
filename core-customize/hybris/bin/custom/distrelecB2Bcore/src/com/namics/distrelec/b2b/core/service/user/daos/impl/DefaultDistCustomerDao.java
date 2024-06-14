/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user.daos.impl;

import java.util.Collections;
import java.util.List;

import com.namics.distrelec.b2b.core.service.user.daos.DistCustomerDao;

import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;

public class DefaultDistCustomerDao extends DefaultGenericDao<CustomerModel> implements DistCustomerDao {

    public DefaultDistCustomerDao() {
        super(CustomerModel._TYPECODE);
    }

    @Override
    public CustomerModel getCustomerByContactId(final String erpContactId) {
        final List resList = this.find(Collections.singletonMap(CustomerModel.ERPCONTACTID, erpContactId));
        if (resList.size() > 1) {
            throw new AmbiguousIdentifierException(
                    "Found " + resList.size() + " users with the unique " + CustomerModel.ERPCONTACTID + " '" + erpContactId + "'");
        }
        return resList.isEmpty() ? null : (CustomerModel) resList.get(0);
    }

}
