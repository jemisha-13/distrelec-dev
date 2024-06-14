package com.namics.distrelec.b2b.core.event;

import java.util.List;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.CustomerModel;

public class DistObsolescenceEvent extends AbstractCommerceUserEvent<BaseSiteModel>{
    private List<AbstractOrderEntryModel> orderEntries;
    private CustomerModel notifiedCustomer;
    
    public DistObsolescenceEvent(){
        super();
    }
    
    public DistObsolescenceEvent(CustomerModel customerModel, List<AbstractOrderEntryModel> orderEntries){
        this.notifiedCustomer=customerModel;
        this.orderEntries=orderEntries;
    }

	public CustomerModel getNotifiedCustomer() {
		return notifiedCustomer;
	}

	public void setNotifiedCustomer(CustomerModel notifiedCustomer) {
		this.notifiedCustomer = notifiedCustomer;
	}

	public List<AbstractOrderEntryModel> getPhasedOutOrderEntries() {
		return orderEntries;
	}

	public void setgetPhasedOutOrderEntries(List<AbstractOrderEntryModel> orderEntries) {
		this.orderEntries = orderEntries;
	}
    
    
}
