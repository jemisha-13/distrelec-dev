/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl.cache;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * SapReadAllOpenOrdersRequestCacheKey
 * 
 * @author Francesco Bersani, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class SapReadAllOpenOrdersRequestCacheKey {

    private final String salesOrganization;
    private final String erpCustomerId;

    public SapReadAllOpenOrdersRequestCacheKey(final String salesOrganization, final String erpCustomerId) {
        this.salesOrganization = salesOrganization;
        this.erpCustomerId = erpCustomerId;
    }

    @Override
    public int hashCode() {

        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(salesOrganization);
        builder.append(erpCustomerId);
        return builder.toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SapReadAllOpenOrdersRequestCacheKey other = (SapReadAllOpenOrdersRequestCacheKey) obj;

        // SalesOrganization
        if (salesOrganization == null) {
            if (other.getSalesOrganization() != null) {
                return false;
            }
        } else if (!salesOrganization.equals(other.getSalesOrganization())) {
            return false;
        }

        // CustomerId
        if (erpCustomerId == null) {
            if (other.getErpCustomerId() != null) {
                return false;
            }
        } else if (!erpCustomerId.equals(other.getErpCustomerId())) {
            return false;
        }

        return true;
    }

    public String getSalesOrganization() {
        return salesOrganization;
    }

    public String getErpCustomerId() {
        return erpCustomerId;
    }

}
