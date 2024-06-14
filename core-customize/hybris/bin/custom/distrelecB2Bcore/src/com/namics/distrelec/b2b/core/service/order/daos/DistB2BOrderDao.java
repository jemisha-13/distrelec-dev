/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order.daos;

import java.util.List;

import de.hybris.platform.b2b.dao.B2BOrderDao;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.store.BaseStoreModel;

public interface DistB2BOrderDao extends B2BOrderDao {

    /**
     * Find the hybris order by its erp order code
     * 
     * @param erpCode
     * @return the hybris order
     */
    public <T extends OrderModel> T findOrderByErpCode(String erpCode);

	OrderModel findOrderByCodeAndStoreAndSubUser(String code, BaseStoreModel store, List<UserModel> b2bUsersList);

	List<OrderModel> findAllOrdersForGivenUserEmail(String email);
}
