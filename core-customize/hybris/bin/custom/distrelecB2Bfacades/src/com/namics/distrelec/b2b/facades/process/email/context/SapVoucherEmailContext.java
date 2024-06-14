/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.SapVoucherEmailProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;

/**
 * {@code SapVoucherEmailContext}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.8
 */
public class SapVoucherEmailContext extends CustomerEmailContext {

    private String voucherCode;
    private String validFrom;
    private String validUntil;
    private String value;
    private String emailSubject;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof SapVoucherEmailProcessModel) {
            final SapVoucherEmailProcessModel sapVoucherEmailProcess = (SapVoucherEmailProcessModel) businessProcessModel;
            // Email Data
            final B2BCustomerModel customer = (B2BCustomerModel) sapVoucherEmailProcess.getCustomer();
            put(FROM_EMAIL, fromEmail(getBaseSite()));
            put(EMAIL, customer.getEmail());
            put(DISPLAY_NAME, customer.getName());

            setEmailSubject(sapVoucherEmailProcess.getEmailSubjectMsg());

            // Voucher data
            setVoucherCode(sapVoucherEmailProcess.getVoucherCode());
            setValue(sapVoucherEmailProcess.getVoucherValue());
            setValidFrom(sapVoucherEmailProcess.getValidFrom());
            setValidUntil(sapVoucherEmailProcess.getValidUntil());
        }
    }

    protected String fromEmail(final BaseSiteModel baseSite) {
        return getConfigurationService().getConfiguration().getString("info.email." + baseSite.getUid(),
                getConfigurationService().getConfiguration().getString("info.email.default"));
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(final String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(final String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(final String validUntil) {
        this.validUntil = validUntil;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(final String emailSubject) {
        this.emailSubject = emailSubject;
    }
}
