package com.namics.distrelec.b2b.core.event;
import org.apache.commons.collections.CollectionUtils;


import com.namics.distrelec.b2b.core.model.process.ObsolescenceNotificationEmailModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.CustomerModel;

public class DistObsolescenceEventListener extends AbstractDistEventListener<DistObsolescenceEvent, ObsolescenceNotificationEmailModel>  {

    private static final String DISTRELEC_OBSOLESCENCE_NOTIFICATION_EMAIL_PROCESS = "obsolescencePreferenceEmailProcess";

    
	@Override
	public ObsolescenceNotificationEmailModel createTarget() {
        final ObsolescenceNotificationEmailModel process = getBusinessProcessService()
                .createProcess(DISTRELEC_OBSOLESCENCE_NOTIFICATION_EMAIL_PROCESS + System.currentTimeMillis(), DISTRELEC_OBSOLESCENCE_NOTIFICATION_EMAIL_PROCESS);
        return process;
	}
	
    @Override
    protected boolean validate(final DistObsolescenceEvent event) {
        return event != null && CollectionUtils.isNotEmpty(event.getPhasedOutOrderEntries());
    }
	
    @Override
    public void populate(final DistObsolescenceEvent event, final ObsolescenceNotificationEmailModel target) {

            target.setNotifiedCustomer(event.getNotifiedCustomer());
            target.setCustomer(event.getNotifiedCustomer());
            target.setLanguage(event.getNotifiedCustomer().getSessionLanguage());
            target.setOrderEntries(event.getPhasedOutOrderEntries());
            CustomerModel customer=event.getNotifiedCustomer();
            target.setSite(((B2BCustomerModel) customer).getCustomersBaseSite());
            getModelServiceViaLookup().save(target);
   
    }



}
