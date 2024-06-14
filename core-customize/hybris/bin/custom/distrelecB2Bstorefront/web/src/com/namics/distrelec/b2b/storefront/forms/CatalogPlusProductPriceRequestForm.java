/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.forms;

import java.util.ArrayList;
import java.util.Collection;

/**
 * {@code CatalogPlusProductPriceRequestForm}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class CatalogPlusProductPriceRequestForm extends QuoteProductPriceForm {

    private Collection<String> supplierAIDs;

    /**
     * Default constructor
     */
    public CatalogPlusProductPriceRequestForm() {
        super();
        // NOP
    }

    /**
     * Parameterized constructor
     * 
     * @param modalQuotationQuantity
     * @param modalQuotationMessage
     */
    public CatalogPlusProductPriceRequestForm(final Long modalQuotationQuantity, final String modalQuotationMessage) {
        setModalQuotationQuantity(modalQuotationQuantity);
        setModalQuotationMessage(modalQuotationMessage);
    }

    /* Getters & Setters */

    public Collection<String> getSupplierAIDs() {
        if (this.supplierAIDs == null) {
            this.supplierAIDs = new ArrayList<String>();
        }

        return supplierAIDs;
    }

    public void setSupplierAIDs(final Collection<String> supplierAIDs) {
        this.supplierAIDs = supplierAIDs;
    }

}
