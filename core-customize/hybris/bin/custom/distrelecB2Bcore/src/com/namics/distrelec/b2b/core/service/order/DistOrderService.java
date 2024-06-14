/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.order;

import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.store.BaseStoreModel;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface DistOrderService extends B2BOrderService {

    /**
     * Get the hybris order with specific ERP order code
     * 
     * @param erpCode
     * @return the hyrbis order
     */
    OrderModel getOrderForErpCode(String erpCode);
    
	OrderModel getOrderForCodeWithSubUsers(String orderCode, BaseStoreModel baseStoreModel, List<UserModel> b2busers);

	void setCustomerClientID(HttpServletRequest request, String cartCode);

    void updateProjectNumber(String orderCode, String workflowCode, String projectNumber);

	List<OrderModel> findAllOrdersForGivenUserEmail(String email);
}
