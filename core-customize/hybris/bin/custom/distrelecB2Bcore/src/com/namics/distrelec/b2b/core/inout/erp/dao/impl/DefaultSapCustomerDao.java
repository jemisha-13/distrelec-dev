/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.dao.impl;

import com.namics.distrelec.b2b.core.inout.erp.dao.SapCustomerDao;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.internal.dao.DefaultGenericDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultSapCustomerDao extends DefaultGenericDao<AddressModel> implements SapCustomerDao {

    public DefaultSapCustomerDao(final String typecode) {
        super(typecode);
    }

    @Override
    public AddressModel getCustomerAddressForSapId(final B2BUnitModel customer, final String sapAddressId) {

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(AddressModel.OWNER, customer);
        parameters.put(AddressModel.DUPLICATE, Boolean.FALSE);
        parameters.put(AddressModel.ERPADDRESSID, sapAddressId);

        final List<AddressModel> results = find(parameters);
        return results == null || results.isEmpty() ? null : results.get(0);
    }

    @Override
    public CartModel getlastModifiedcartForCustomer(final UserModel customer) {
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put(CartModel.USER, customer);

        final StringBuilder builder = new StringBuilder();

        builder.append("select {").append(CartModel.PK).append("}, {").append(CartModel.MODIFIEDTIME).append("} from {").append(CartModel._TYPECODE)
                .append("} where {").append(CartModel.USER).append("} = ?").append(CartModel.USER).append(" order by {").append(CartModel.MODIFIEDTIME)
                .append("} DESC");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(builder.toString());
        query.addQueryParameter(CartModel.USER, customer);

        final SearchResult<CartModel> result = getFlexibleSearchService().search(query);
        return result.getCount() > 0 ? result.getResult().get(0) : null;
    }

}
