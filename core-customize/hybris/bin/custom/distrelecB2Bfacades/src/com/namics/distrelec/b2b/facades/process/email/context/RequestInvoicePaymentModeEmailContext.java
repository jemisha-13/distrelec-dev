package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.RequestInvoicePaymentModeEmailProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

public class RequestInvoicePaymentModeEmailContext extends CustomerEmailContext {

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);

        RequestInvoicePaymentModeEmailProcessModel requestModel = (RequestInvoicePaymentModeEmailProcessModel) businessProcessModel;
        BaseSiteModel originSite = requestModel.getSite();

        String fromEmail = getConfigurationService().getConfiguration().getString("request.invoice.payment.method.email.from."
                + originSite.getUid());
        put(FROM_EMAIL, fromEmail);

        String displayName = getConfigurationService().getConfiguration().getString("request.invoice.payment.method.email.displayname."
                + originSite.getUid());
        put(FROM_DISPLAY_NAME, displayName);

        String toEmail = getConfigurationService().getConfiguration().getString("request.invoice.payment.method.email.to."
            + originSite.getUid());
        put(EMAIL, toEmail);
    }

    @Override
    protected BaseSiteModel getSite(BusinessProcessModel businessProcessModel) {
        RequestInvoicePaymentModeEmailProcessModel requestModel = (RequestInvoicePaymentModeEmailProcessModel) businessProcessModel;
        return requestModel.getSite();
    }

    @Override
    protected CustomerModel getCustomer(BusinessProcessModel businessProcessModel) {
        RequestInvoicePaymentModeEmailProcessModel requestModel = (RequestInvoicePaymentModeEmailProcessModel) businessProcessModel;
        return requestModel.getCustomer();
    }

    @Override
    protected LanguageModel getEmailLanguage(BusinessProcessModel businessProcessModel) {
        BaseSiteModel site = getSite(businessProcessModel);
        return site.getDefaultLanguage();
    }
}
