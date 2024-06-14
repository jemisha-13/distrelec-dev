/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.daos.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.core.model.user.UserModel;
import com.namics.distrelec.b2b.core.service.order.daos.DistB2BOrderDao;

import de.hybris.platform.b2b.dao.impl.DefaultB2BOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;

public class DefaultDistOrderDao extends DefaultB2BOrderDao implements DistB2BOrderDao {

	private static final String FIND_ORDERS_BY_CODE_STORE_SUBUSER_QUERY = "SELECT {" + OrderModel.PK + "} FROM {" + OrderModel._TYPECODE + "} WHERE {" + OrderModel.CODE + "} = ?code AND {"
			+ OrderModel.VERSIONID + "} IS NULL AND {" + OrderModel.STORE + "} = ?store" + " AND {"
			+ OrderModel.USER + "} IN (?b2bUsersList)";

	private static final String FIND_ORDERS_BY_USER = "SELECT {order.PK} FROM {Order AS order JOIN B2BCustomer AS " +
			"customer ON {order.user} = {customer.pk}} WHERE {customer.email} = ?email";
	
	@Override
	public OrderModel findOrderByCodeAndStoreAndSubUser(final String code, final BaseStoreModel store,
			final List<UserModel> b2bUsersList)
	{
		final Map<String, Object> queryParams = new HashMap<String, Object>();
		queryParams.put("code", code);
		queryParams.put("store", store);
		queryParams.put("b2bUsersList", b2bUsersList);
		final OrderModel result = getFlexibleSearchService()
				.searchUnique(new FlexibleSearchQuery(FIND_ORDERS_BY_CODE_STORE_SUBUSER_QUERY, queryParams));
		return result;
	}

	@Override
	public List<OrderModel> findAllOrdersForGivenUserEmail(String email) {
		final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_ORDERS_BY_USER);
		searchQuery.addQueryParameter("email", email);
		return getFlexibleSearchService().<OrderModel>search(searchQuery).getResult();
	}

	@Override
    public <T extends OrderModel> T findOrderByErpCode(String erpCode) {
        final Map<String, String> attr = new HashMap<String, String>();
        attr.put("erpOrderCode", erpCode);
        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT {o:pk} from {Order as o} WHERE {o:erpOrderCode} = ?erpOrderCode AND {o:versionID} IS NULL");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(sql.toString());
        query.getQueryParameters().putAll(attr);
        SearchResult result = getFlexibleSearchService().search(query);
        List orders = result.getResult();
        return (T) ((orders.isEmpty()) ? null : (OrderModel) orders.get(0));
    }

}
