/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.event;

import de.hybris.platform.servicelayer.event.events.AbstractEvent;

/**
 * {@code DistCustomerErrorFeedbackEvent}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.13
 */
public class DistCustomerErrorFeedbackEvent extends AbstractEvent {

    private String customerName;
    private String customerEmailId;
    private String erpCustomerId;
    private String errorReason;
    private String errorDescription;
    private String productId;
    private String productName;
    private String languageIso;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(final String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmailId() {
        return customerEmailId;
    }

    public void setCustomerEmailId(final String customerEmailId) {
        this.customerEmailId = customerEmailId;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(final String productId) {
        this.productId = productId;
    }

    public String getErpCustomerId() {
        return erpCustomerId;
    }

    public void setErpCustomerId(final String erpCustomerId) {
        this.erpCustomerId = erpCustomerId;
    }

    public String getLanguageIso() {
        return languageIso;
    }

    public void setLanguageIso(final String languageIso) {
        this.languageIso = languageIso;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(final String productName) {
        this.productName = productName;
    }
}
