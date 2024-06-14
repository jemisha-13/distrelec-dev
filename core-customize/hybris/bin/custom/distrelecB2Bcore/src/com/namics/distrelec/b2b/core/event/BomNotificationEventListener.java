package com.namics.distrelec.b2b.core.event;
import org.apache.commons.collections.CollectionUtils;

import com.namics.distrelec.b2b.core.model.process.BomFileNotificationEmailModel;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.core.model.user.CustomerModel;

public class BomNotificationEventListener extends AbstractDistEventListener<BomNotificationEvent, BomFileNotificationEmailModel>  {

    private static final String DISTRELEC_BOM_FILE_NOTIFICATION_EMAIL_PROCESS = "bomFileNotificationEmailProcess";


	@Override
	public BomFileNotificationEmailModel createTarget() {
        final BomFileNotificationEmailModel process = getBusinessProcessService()
                .createProcess(DISTRELEC_BOM_FILE_NOTIFICATION_EMAIL_PROCESS + System.currentTimeMillis(), DISTRELEC_BOM_FILE_NOTIFICATION_EMAIL_PROCESS);
        return process;
	}

    @Override
    protected boolean validate(final BomNotificationEvent event) {
        return event != null && CollectionUtils.isNotEmpty(event.getUnusedFileNames());
    }

    @Override
    public void populate(final BomNotificationEvent event, final BomFileNotificationEmailModel target) {

            target.setNotifiedCustomer(event.getNotifiedCustomer());
            target.setCustomer(event.getNotifiedCustomer());
            target.setLanguage(event.getNotifiedCustomer().getSessionLanguage());
            target.setUnusedfilenames(event.getUnusedFileNames());
            CustomerModel customer=event.getNotifiedCustomer();
            target.setSite(((B2BCustomerModel) customer).getCustomersBaseSite());
            getModelServiceViaLookup().save(target);

    }



}
