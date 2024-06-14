/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.nps.data;

import com.namics.distrelec.b2b.core.enums.NPSReason;
import com.namics.distrelec.b2b.core.enums.NPSSubReason;
import com.namics.distrelec.b2b.core.enums.NPSType;

import java.util.Date;

/**
 * {@code DistNetPromoterScoreData}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistNetPromoterScoreData {

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
    private Integer value;
    private String text;

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

    public Integer getValue() {
        return value;
    }

    public void setValue(final Integer value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
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

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(final Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getSalesOrg() {
        return salesOrg;
    }

    public void setSalesOrg(final String salesOrg) {
        this.salesOrg = salesOrg;
    }

    public String getErpContactID() {
        return erpContactID;
    }

    public void setErpContactID(final String erpContactID) {
        this.erpContactID = erpContactID;
    }

    public NPSReason getReason() {
        return reason;
    }

    public void setReason(NPSReason reason) {
        this.reason = reason;
    }

    public NPSSubReason getSubreason() {
        return subreason;
    }

    public void setSubreason(final NPSSubReason subreason) {
        this.subreason = subreason;
    }
}
