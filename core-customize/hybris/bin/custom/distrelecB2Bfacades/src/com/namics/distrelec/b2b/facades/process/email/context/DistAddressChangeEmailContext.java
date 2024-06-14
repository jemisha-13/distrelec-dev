/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistAddressChangeProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.i18n.I18NService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

public class DistAddressChangeEmailContext extends AbstractDistEmailContext {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);

        final DistAddressChangeProcessModel process = (DistAddressChangeProcessModel) businessProcessModel;
        put(EMAIL, getAddressChangeEmail() + DISPLAY_NAME);
        put(DISPLAY_NAME, process.getEmailDisplayName());
        put(process, DistAddressChangeProcessModel.CUSTOMERNUMBER);
        put(process, DistAddressChangeProcessModel.COMMENT);

        put(process, DistAddressChangeProcessModel.OLDCOMPANYNAME);
        put(process, DistAddressChangeProcessModel.OLDFIRSTNAME);
        put(process, DistAddressChangeProcessModel.OLDLASTNAME);
        put(process, DistAddressChangeProcessModel.OLDDEPARTMENT);
        put(process, DistAddressChangeProcessModel.OLDSTREET);
        put(process, DistAddressChangeProcessModel.OLDNUMBER);
        put(process, DistAddressChangeProcessModel.OLDPOBOX);
        put(process, DistAddressChangeProcessModel.OLDZIP);
        put(process, DistAddressChangeProcessModel.OLDPLACE);
        put(process, DistAddressChangeProcessModel.OLDCOUNTRY);

        put(process, DistAddressChangeProcessModel.NEWCOMPANYNAME);
        put(process, DistAddressChangeProcessModel.NEWFIRSTNAME);
        put(process, DistAddressChangeProcessModel.NEWLASTNAME);
        put(process, DistAddressChangeProcessModel.NEWDEPARTMENT);
        put(process, DistAddressChangeProcessModel.NEWSTREET);
        put(process, DistAddressChangeProcessModel.NEWNUMBER);
        put(process, DistAddressChangeProcessModel.NEWPOBOX);
        put(process, DistAddressChangeProcessModel.NEWZIP);
        put(process, DistAddressChangeProcessModel.NEWPLACE);
        put(process, DistAddressChangeProcessModel.NEWCOUNTRY);
    }

    private String getAddressChangeEmail() {
        return getEmail(DistConstants.PropKey.Email.INFO_EMAIL_PREFIX, DistConstants.PropKey.Email.INFO_EMAIL_DEFAULT);
    }

    public Object put(final DistAddressChangeProcessModel process, final String attribute) {
        final Object value = process.getProperty(attribute);
        return super.put(attribute, value != null ? value : "");
    }

    @Override
    protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof DistAddressChangeProcessModel) {
            return ((DistAddressChangeProcessModel) businessProcessModel).getSite().getDefaultLanguage();
        }
        return null;
    }

    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof DistAddressChangeProcessModel) {
            return ((DistAddressChangeProcessModel) businessProcessModel).getSite();
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
