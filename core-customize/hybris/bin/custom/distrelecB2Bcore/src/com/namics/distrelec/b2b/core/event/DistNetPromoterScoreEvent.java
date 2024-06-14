/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.event;

import java.util.Date;

import com.namics.distrelec.b2b.core.enums.NPSReason;
import com.namics.distrelec.b2b.core.enums.NPSSubReason;
import com.namics.distrelec.b2b.core.enums.NPSType;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

/**
 * {@code DistNetPromoterScoreEvent}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public class DistNetPromoterScoreEvent extends AbstractDistrelecCustomerEvent<BaseSiteModel> {

    private String code;
    private String erpCustomerID;
    private String erpContactID;
    private NPSReason reason;
    private NPSSubReason subreason;
    private NPSType type;
    private String email;
    private String salesOrg;
    private String firstname;
    private String lastname;
    private String companyName;
    private String orderNumber;
    private String domain;
    private Date creationDate;
    private Date deliveryDate;
    private int value;
    private String text;

    private String toEmail;

    // Getters & Setters

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
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

    public NPSType getType() {
        return type;
    }

    public void setType(final NPSType type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(final String toEmail) {
        this.toEmail = toEmail;
    }

    public NPSReason getReason() {
        return reason;
    }

    public void setReason(final NPSReason reason) {
        this.reason = reason;
    }

    public NPSSubReason getSubreason() {
        return subreason;
    }

    public void setSubreason(final NPSSubReason subreason) {
        this.subreason = subreason;
    }
}
