/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistCatalogOrderProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class DistCatalogOrderEmailContext extends AbstractDistEmailContext {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);

        final DistCatalogOrderProcessModel process = (DistCatalogOrderProcessModel) businessProcessModel;
        put(EMAIL, getCatalogOrderEmail());
        put(DISPLAY_NAME, process.getEmailDisplayName());
        put(FROM_EMAIL, process.getEMail());

        put(process, DistCatalogOrderProcessModel.CATALOG);
        put(process, DistCatalogOrderProcessModel.COMPANYNAME);
        put(process, DistCatalogOrderProcessModel.COMPANYNAME2);
        put(process, DistCatalogOrderProcessModel.SALUTATION);
        put(process, DistCatalogOrderProcessModel.FIRSTNAME);
        put(process, DistCatalogOrderProcessModel.LASTNAME);
        put(process, DistCatalogOrderProcessModel.DEPARTMENT);
        put(process, DistCatalogOrderProcessModel.STREET);
        put(process, DistCatalogOrderProcessModel.NUMBER);
        put(process, DistCatalogOrderProcessModel.POBOX);
        put(process, DistCatalogOrderProcessModel.ZIP);
        put(process, DistCatalogOrderProcessModel.PLACE);
        put(process, DistCatalogOrderProcessModel.DIRECTPHONE);
        put(process, DistCatalogOrderProcessModel.MOBILE);
        put(process, DistCatalogOrderProcessModel.FAX);
        put(process, DistCatalogOrderProcessModel.EMAIL);
        put(process, DistCatalogOrderProcessModel.COMMENT);

    }

    private String getCatalogOrderEmail() {
        return getEmail(DistConstants.PropKey.Email.INFO_EMAIL_PREFIX, DistConstants.PropKey.Email.INFO_EMAIL_DEFAULT);
    }

    public Object put(final DistCatalogOrderProcessModel process, final String attribute) {
        final Object value = process.getProperty(attribute);
        return super.put(attribute, value != null ? value : "");
    }

    @Override
    protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof DistCatalogOrderProcessModel) {
            return ((DistCatalogOrderProcessModel) businessProcessModel).getSite().getDefaultLanguage();
        }
        return null;
    }

    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof DistCatalogOrderProcessModel) {
            return ((DistCatalogOrderProcessModel) businessProcessModel).getSite();
        }
        return null;
    }

    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
        return null;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

}
