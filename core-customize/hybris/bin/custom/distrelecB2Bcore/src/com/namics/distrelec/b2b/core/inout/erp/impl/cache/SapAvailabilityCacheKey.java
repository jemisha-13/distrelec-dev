/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl.cache;

import org.apache.commons.lang.StringUtils;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

/**
 * {@code SapAvailabilityCacheKey}
 * <p>
 * Key for the cache for the SAP Availability Service.
 * </p>
 * 
 * @author ksperner, Namics AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class SapAvailabilityCacheKey {

    private final String productCode;
    private final String salesOrgCode;

    /**
     * Create a new instance of {@code SapAvailabilityCacheKey}
     * 
     * @param productCode
     */
    public SapAvailabilityCacheKey(final String productCode, final DistSalesOrgModel salesOrg) {
        this.productCode = productCode;
        this.salesOrgCode = salesOrg.getCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
        result = prime * result + ((salesOrgCode == null) ? 0 : salesOrgCode.hashCode());

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final SapAvailabilityCacheKey other = (SapAvailabilityCacheKey) obj;

        return StringUtils.equals(this.productCode, other.productCode)
                && StringUtils.equals(this.salesOrgCode, other.salesOrgCode);
    }
}
