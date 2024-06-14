/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.user.impl;

import com.namics.distrelec.b2b.core.service.user.DistAddressService;
import com.namics.distrelec.b2b.core.service.user.daos.DistAddressDao;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.user.daos.AddressDao;
import de.hybris.platform.servicelayer.user.impl.DefaultAddressService;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class DefaultDistAddressService extends DefaultAddressService implements DistAddressService {

    private AddressDao addressDao;

    @Override
    public AddressModel getAddressByErpAddressId(final ItemModel owner, final String erpAddressId) {
        ServicesUtil.validateParameterNotNull(owner, "");
        ServicesUtil.validateParameterNotNull(erpAddressId, "");
        List<AddressModel> addresses = filterAddressesByErpAddressId((List<AddressModel>) super.getAddressesForOwner(owner), erpAddressId);
        return CollectionUtils.isEmpty(addresses) ? null : addresses.get(0);
    }

    @Override
    public AddressModel getAddressByErpAddressId(final String erpAddressId, final Boolean duplicate) {
        ServicesUtil.validateParameterNotNull(erpAddressId, "erpAddressId must not be null");
        return getDistAddressDao().findAddressesForOwner(erpAddressId, duplicate);
    }

    @Override
    public boolean isAddressValid(AddressModel address) {
        return address != null && address.getCountry() != null
                && StringUtils.isNotBlank(address.getLine1())
                && StringUtils.isNotBlank(address.getPostalcode())
                && StringUtils.isNotBlank(address.getTown())
                && (StringUtils.isNotBlank(address.getPhone1()) || StringUtils.isNotBlank(address.getCellphone()));
    }

    private List<AddressModel> filterAddressesByErpAddressId(final List<AddressModel> addresses, final String erpAddressId) {

        // create the list for filter
        final List<AddressModel> filteredAddresses = new ArrayList<AddressModel>();
        filteredAddresses.addAll(addresses);

        // perform the filter based on erp address id
        CollectionUtils.filter(filteredAddresses, new Predicate() {

            @Override
            public boolean evaluate(final Object o) {
                final AddressModel address = (AddressModel) o;
                return StringUtils.isNotEmpty(address.getErpAddressID()) && address.getErpAddressID().equals(erpAddressId);
            }
        });

        return filteredAddresses;
    }

    public DistAddressDao getDistAddressDao() {
        return (DistAddressDao) addressDao;
    }

    @Override
    public void setAddressDao(final AddressDao addressDao) {
        super.setAddressDao(addressDao);
        this.addressDao = addressDao;
    }

}
