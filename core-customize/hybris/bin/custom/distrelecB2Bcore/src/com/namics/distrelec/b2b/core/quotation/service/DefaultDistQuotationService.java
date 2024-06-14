package com.namics.distrelec.b2b.core.quotation.service;

public interface DefaultDistQuotationService {

    boolean incrementQuotationCounter(final String uid);

    boolean isCustomerOverQuotationLimit(final String customerNumber);
}
