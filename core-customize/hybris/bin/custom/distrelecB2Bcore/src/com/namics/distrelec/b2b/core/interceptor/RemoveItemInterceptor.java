package com.namics.distrelec.b2b.core.interceptor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.user.UserService;

public class RemoveItemInterceptor implements RemoveInterceptor {
    
    private static final Logger LOG = LogManager.getLogger(RemoveItemInterceptor.class);
    
    @Autowired
    private UserService userService;
    
    @Override
    public void onRemove(Object arg0, InterceptorContext arg1) throws InterceptorException {
        
        if (arg0 instanceof B2BCustomerModel) {
            B2BCustomerModel customer=(B2BCustomerModel)arg0;
            LOG.info("Removing B2BCustomer ::"+customer.getName() +" PK ::"+customer.getPk() + " ERP Contact ID::"+customer.getErpContactID()+" by user::"+userService.getCurrentUser().getPk());
        }else if (arg0 instanceof B2BUnitModel) {
            B2BUnitModel unit=(B2BUnitModel)arg0;
            LOG.info("Removing B2BUnit ::"+unit.getName() +" PK ::"+unit.getPk() + " ERP ID::"+unit.getErpCustomerID()+" by user::"+userService.getCurrentUser().getPk());
        }else if (arg0 instanceof OrderModel) {
            OrderModel order=(OrderModel)arg0;
            LOG.info("Removing Order ::"+ order.getErpOrderCode() +" PK ::"+order.getPk()+" by user::"+userService.getCurrentUser().getPk());
        }
        
    }

}
