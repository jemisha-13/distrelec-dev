/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.process.email.context;

import com.namics.distrelec.b2b.core.model.process.DistNpsProcessModel;
import com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;

import de.hybris.platform.servicelayer.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
/**
 * {@code NetPromoterScoreEmailContext}
 *
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class NetPromoterScoreEmailContext extends AbstractDistEmailContext {

    private String npsCode;
    private String erpCustomerID;
    private String erpContactID;
    private String reason;
    private String subreason;
    private String type;
    private String customerEmail;
    private String salesOrg;
    private String firstname;
    private String lastname;
    private String companyName;
    private String orderNumber;
    private String domain;
    private String deliveryDate;
    private String value;
    private String text;
    private String emailSubject;
	
	
    @Autowired
    private UserService userService;

    @Override
    public void init(final BusinessProcessModel businessProcessModel, final EmailPageModel emailPageModel) {
        super.init(businessProcessModel, emailPageModel);
        if (businessProcessModel instanceof DistNpsProcessModel) {
            final DistNpsProcessModel npsProcessModel = (DistNpsProcessModel) businessProcessModel;

            // Email Data
            String fromEmail = getConfigurationService().getConfiguration().getString("nps.email.sender");
            put(FROM_EMAIL, fromEmail);
            final String fromDisplayName = (StringUtils.isNotBlank(npsProcessModel.getFirstname()) ? npsProcessModel.getFirstname() : " ")
                    + (StringUtils.isNotBlank(npsProcessModel.getLastname()) ? npsProcessModel.getLastname() : "");
            put(FROM_DISPLAY_NAME, fromDisplayName);
            put(EMAIL, npsProcessModel.getToEmail());
            put(DISPLAY_NAME, get(EMAIL));
			put(EMAIL_LANGUAGE, getEmailLanguage(npsProcessModel));
            setEmailSubject(npsProcessModel.getEmailSubjectMsg());

            // NPS data
            setNpsCode(npsProcessModel.getNpsCode());
            setErpContactID(npsProcessModel.getErpContactID());
            setErpCustomerID(npsProcessModel.getErpCustomerID());
            setReason(npsProcessModel.getReason() != null ? npsProcessModel.getReason().getCode() : "");
            setSubreason(npsProcessModel.getSubReason() != null ? npsProcessModel.getSubReason().getCode() : "");
            setType(npsProcessModel.getType() != null ? npsProcessModel.getType().getCode() : "");
            setCustomerEmail(npsProcessModel.getEmail());
            setSalesOrg(npsProcessModel.getSalesOrg());
            setFirstname(npsProcessModel.getFirstname());
            setLastname(npsProcessModel.getLastname());
            setCompanyName(npsProcessModel.getCompanyName());
            setOrderNumber(npsProcessModel.getOrderNumber());
            setDomain(npsProcessModel.getDomain());
            setDeliveryDate(npsProcessModel.getDeliveryDate() != null ? new SimpleDateFormat("dd.MM.yyyy").format(npsProcessModel.getDeliveryDate()) : "");
            setValue(npsProcessModel.getValue().toString());
            setText(npsProcessModel.getText());
        }
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
        if (businessProcessModel instanceof DistNpsProcessModel) {
            return ((DistNpsProcessModel) businessProcessModel).getSite();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext#getCustomer(de.hybris.platform.processengine.model
     * .BusinessProcessModel)
     */
    @Override
    protected CustomerModel getCustomer(final BusinessProcessModel businessProcessModel) {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.service.process.email.context.AbstractDistEmailContext#getEmailLanguage(de.hybris.platform.processengine
     * .model.BusinessProcessModel)
     */
    @Override
    protected LanguageModel getEmailLanguage(final BusinessProcessModel businessProcessModel) {
        return getCommonI18NService().getLanguage("en");
    }

    // Getters & Setters

    public String getNpsCode() {
        return npsCode;
    }

    public void setNpsCode(final String npsCode) {
        this.npsCode = npsCode;
    }

    public String getErpCustomerID() {
        return erpCustomerID;
    }

    public void setErpCustomerID(final String erpCustomerID) {
        this.erpCustomerID = erpCustomerID;
    }

    public String getErpContactID() {
        return erpContactID;
    }

    public void setErpContactID(final String erpContactID) {
        this.erpContactID = erpContactID;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getSalesOrg() {
        return salesOrg;
    }

    public void setSalesOrg(final String salesOrg) {
        this.salesOrg = salesOrg;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(final String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(final String domain) {
        this.domain = domain;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(final String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(final String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(final String reason) {
        this.reason = reason;
    }

    public String getSubreason() {
        return subreason;
    }

    public void setSubreason(final String subreason) {
        this.subreason = subreason;
    }
}
