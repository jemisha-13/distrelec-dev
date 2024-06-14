package com.namics.distrelec.b2b.core.service.process.strategies.impl;

import com.namics.distrelec.b2b.core.model.process.RequestInvoicePaymentModeEmailProcessModel;
import de.hybris.platform.acceleratorservices.process.strategies.impl.AbstractProcessContextStrategy;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

public class DistRequestInvoicePaymentMethodProcessContextStrategy extends AbstractProcessContextStrategy {

    @Override
    protected CustomerModel getCustomer(BusinessProcessModel businessProcess) {
        RequestInvoicePaymentModeEmailProcessModel requestProcess = (RequestInvoicePaymentModeEmailProcessModel) businessProcess;
        return requestProcess.getCustomer();
    }

    @Override
    public BaseSiteModel getCmsSite(BusinessProcessModel businessProcess) {
        RequestInvoicePaymentModeEmailProcessModel requestProcess = (RequestInvoicePaymentModeEmailProcessModel) businessProcess;
        return requestProcess.getSite();
    }

    @Override
    protected void setCurrency(final BusinessProcessModel businessProcess) {
        // override because currency is not used
    }
}
