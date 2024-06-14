package com.namics.distrelec.b2b.core.event;

import java.util.List;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.event.AbstractCommerceUserEvent;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.user.CustomerModel;

public class BomNotificationEvent extends AbstractCommerceUserEvent<BaseSiteModel>{
    private List<String> unusedfilenames;
    private CustomerModel notifiedCustomer;

    public BomNotificationEvent(){
        super();
    }

    public BomNotificationEvent(CustomerModel customerModel, List<String> filenames){
        this.notifiedCustomer=customerModel;
        this.unusedfilenames=filenames;
    }

	public CustomerModel getNotifiedCustomer() {
		return notifiedCustomer;
	}

	public void setNotifiedCustomer(CustomerModel notifiedCustomer) {
		this.notifiedCustomer = notifiedCustomer;
	}

	public List<String> getUnusedFileNames() {
		return unusedfilenames;
	}

	public void setUnusedFileNames(List<String> filenames) {
		this.unusedfilenames = filenames;
	}


}
