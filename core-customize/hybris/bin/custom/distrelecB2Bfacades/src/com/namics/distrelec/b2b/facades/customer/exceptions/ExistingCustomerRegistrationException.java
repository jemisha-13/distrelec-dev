/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.customer.exceptions;

import com.namics.distrelec.b2b.core.inout.erp.data.FindContactResponseData;

/**
 * This exception is needed to manage the errors in case contact or customer are not found or not unique.
 *
 * @author daebersanif, Distrelec AG
 * @since Distrelec 3.0
 *
 */
public class ExistingCustomerRegistrationException extends Exception {

    private FindContactResponseData responseFromERP;

    private Reason reason;

    public ExistingCustomerRegistrationException(final String message, final FindContactResponseData responseFromERP, final Reason reason) {
        super(message);
        setResponseFromERP(responseFromERP);
        setReason(reason);
    }

    public ExistingCustomerRegistrationException(final Throwable throwable, final FindContactResponseData responseFromERP, final Reason reason) {
        super(throwable);
        setResponseFromERP(responseFromERP);
        setReason(reason);
    }

    public ExistingCustomerRegistrationException(final String message, final Throwable throwable, final FindContactResponseData responseFromERP,
                                                 final Reason reason) {
        super(message, throwable);
        setResponseFromERP(responseFromERP);
        setReason(reason);
    }

    public ExistingCustomerRegistrationException(final String message, final Reason reason) {
        super(message);
        setReason(reason);
    }

    @Override
    public String getMessage() {
        final StringBuffer buffer = new StringBuffer(200);
        buffer.append(super.getMessage());
        buffer.append(", Reason: ");
        buffer.append(this.getReason());
        buffer.append(", ERP Response: ");
        buffer.append(this.getResponseFromERP());
        return buffer.toString();
    }

    public FindContactResponseData getResponseFromERP() {
        return responseFromERP;
    }

    private void setResponseFromERP(final FindContactResponseData responseFromERP) {
        this.responseFromERP = responseFromERP;
    }

    public Reason getReason() {
        return reason;
    }

    private void setReason(final Reason reason) {
        this.reason = reason;
    }

    /**
     * Reason for the exception
     *
     * @author daehusir, Distrelec
     * @since Distrelec 3.0
     *
     */
    public static enum Reason {
        // @formatter:off
        CUSTOMER_NOT_FOUND("Customer not found", "register.organizationalNumber.new.customerID"),
        CONTACT_NOT_UNIQUE("No unique contact was found in the ERP!", "registration.error.existing.contact.not.unique"),
        ADMIN_MANAGING_SUBUSERS("The admin is managing the subsuser", "registration.error.existing.admin.managing.subuser"),
        OFFLINE_CUSTOMER_REGISTRATION_DISABLED("Making an offline customer fully online capable is not allowed", "registration.error.existing.offline.customer.registration.disabled"),
        SELF_REGISTRATION_DISABLED("It is not allowed to register a new contact to existing customer", "registration.error.existing.self.registration.disabled");
        //@formatter:on

        private final String value;

        private final String messageKey;

        private Reason(final String value, final String messageKey) {
            this.value = value;
            this.messageKey = messageKey;
        }

        public String getValue() {
            return value;
        }

        public String getMessageKey() {
            return messageKey;
        }
    }

}
