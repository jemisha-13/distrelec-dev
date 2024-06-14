/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl.cache;

import org.apache.commons.lang.ObjectUtils;

public class SapCustomerPriceCacheKey {

    private final String salesOrganization;
    private final String currency;
    private final String customerCode;
    private final String productCode;
    private final String quantity;

    /**
     * Create a new instance of {@code SapCustomerPriceCacheKey}
     * 
     * @param salesOrganization
     * @param currency
     * @param customerCode
     * @param productCode
     * @param quantity
     */
    public SapCustomerPriceCacheKey(final String salesOrganization, final String currency, final String customerCode, final String productCode,
            final String quantity) {
        this.salesOrganization = salesOrganization;
        this.currency = currency;
        this.customerCode = customerCode;
        this.productCode = productCode;
        this.quantity = quantity;

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + ((salesOrganization == null) ? 0 : salesOrganization.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((customerCode == null) ? 0 : customerCode.hashCode());
        result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
        result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final SapCustomerPriceCacheKey other = (SapCustomerPriceCacheKey) obj;

        if (ObjectUtils.notEqual(salesOrganization, other.salesOrganization)) {
            return false;
        }
        if (ObjectUtils.notEqual(currency, other.currency)) {
            return false;
        }
        if (ObjectUtils.notEqual(customerCode, other.customerCode)) {
            return false;
        }
        if (ObjectUtils.notEqual(productCode, other.productCode)) {
            return false;
        }
        if (ObjectUtils.notEqual(quantity, other.quantity)) {
            return false;
        }

        return true;
    }

}
