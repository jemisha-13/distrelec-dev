
package com.namics.distrelec.b2b.core.service.process.strategies.impl;

import java.util.Optional;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.process.PaymentNotifyProcessModel;
import com.namics.distrelec.b2b.core.service.order.daos.DistB2BOrderDao;


import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * Strategy to impersonate site and initialize session context from an instance of OrderProcessModel.
 */
public class DistOrderProcessContextStrategy extends AbstractDistOrderProcessContextStrategy
{
    @Autowired
    private DistB2BOrderDao b2bOrderDao;

	private String defaultCatalog;

	@Override
	protected Optional<AbstractOrderModel> getOrderModel(final BusinessProcessModel businessProcessModel)
	{
	    Optional<AbstractOrderModel> optionOrderModel = null;
	    if(businessProcessModel instanceof OrderProcessModel) {
	        optionOrderModel= Optional.of(businessProcessModel)
				.filter(businessProcess -> businessProcess instanceof OrderProcessModel)
				.map(businessProcess -> ((OrderProcessModel) businessProcess).getOrder());
	    }else if(businessProcessModel instanceof PaymentNotifyProcessModel) {
	        
	        PaymentNotifyProcessModel notifyProcess=(PaymentNotifyProcessModel)businessProcessModel; 
	        String cartCode = notifyProcess.getCartCode();
	        optionOrderModel = Optional.of(getB2bOrderDao().findOrderByCode(cartCode));
		}
	    
	    return optionOrderModel;
	}

	@Override
	public CatalogVersionModel getContentCatalogVersion(final BusinessProcessModel businessProcessModel) {
		if (businessProcessModel instanceof PaymentNotifyProcessModel) {
			return getCatalogVersionService().getCatalogVersion(getDefaultCatalog(), "Online");
		}

		return super.getContentCatalogVersion(businessProcessModel);
	}

    public DistB2BOrderDao getB2bOrderDao() {
        return b2bOrderDao;
    }


    public void setB2bOrderDao(DistB2BOrderDao b2bOrderDao) {
        this.b2bOrderDao = b2bOrderDao;
    }

		public String getDefaultCatalog() {
			return defaultCatalog;
		}

		public void setDefaultCatalog(String defaultCatalog) {
			this.defaultCatalog = defaultCatalog;
		}
}
