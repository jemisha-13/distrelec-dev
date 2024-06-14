/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.process.DistrelecCustomerErrorFeedbackProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.process.StoreFrontProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.site.BaseSiteService;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

/**
 * {@code DistCustomerErrorFeedbackEmailContext}
 *
 *
 * @since Distrelec 5.13
 */
public class DistCustomerErrorFeedbackEmailContext extends AbstractDistEmailContext {

    @Autowired
    private BaseSiteService baseSiteService;

    private String productCode;
    private String productName;
    private String customerEmail;
    private String customerName;
    private String erpCustomerId;
    private String errorReason;
    private String errorDescription;
    private String language;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext#init(de.hybris.platform.processengine.model.
     * BusinessProcessModel, de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel)
     */
    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        // super.init(businessProcessModel, emailPageModel);

        put(THEME, null);
        put(FROM_EMAIL, emailPageModel.getFromEmail());
        put(FROM_DISPLAY_NAME, emailPageModel.getFromName());
        put(DISPLAY_NAME, emailPageModel.getFromName());
        put(EMAIL, getNewProductsNewsLetterEmail());
        put(EMAIL_LANGUAGE, getEmailLanguage(businessProcessModel));
        // DISTRELEC-2427: add string escape utils to email context
        put("StringEscapeUtils", StringEscapeUtils.class);

        if (businessProcessModel instanceof DistrelecCustomerErrorFeedbackProcessModel) {
            final DistrelecCustomerErrorFeedbackProcessModel customerErrorFeedbackProcess = (DistrelecCustomerErrorFeedbackProcessModel) businessProcessModel;
            setProductCode(customerErrorFeedbackProcess.getProductId());
            setProductName(customerErrorFeedbackProcess.getProductName());
            setCustomerEmail(customerErrorFeedbackProcess.getCustomerEmailId());
            setCustomerName(customerErrorFeedbackProcess.getCustomerName());
            setErpCustomerId(customerErrorFeedbackProcess.getErpCustomerId());
            setErrorReason(customerErrorFeedbackProcess.getErrorReason());
            setErrorDescription(customerErrorFeedbackProcess.getErrorDescription());
            if (customerErrorFeedbackProcess.getLanguage() != null) {
                setLanguage(customerErrorFeedbackProcess.getLanguage().getName(new Locale("en")));
            }
        }
    }

    /**
     * @return the destination email address.
     */
    private String getNewProductsNewsLetterEmail() {
        final String email = getConfigurationService().getConfiguration().getString(DistConstants.PropKey.Email.CUSTOMER_ERROR_FEEDBACK_EMAIL);
        return email;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getSite(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    protected BaseSiteModel getSite(final BusinessProcessModel businessProcessModel) {
        if (businessProcessModel instanceof StoreFrontProcessModel) {
            return ((StoreFrontProcessModel) businessProcessModel).getSite();
        }
        return getBaseSiteService().getBaseSiteForUID("distrelec_CH");
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getCustomer(de.hybris.platform.processengine.model.
     * BusinessProcessModel)
     */
    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
        return null;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    public String getErrorReason() {
        return errorReason;
    }

    public void setErrorReason(final String errorReason) {
        this.errorReason = errorReason;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(final String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getErpCustomerId() {
        return erpCustomerId;
    }

    public void setErpCustomerId(final String erpCustomerId) {
        this.erpCustomerId = erpCustomerId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }
}
