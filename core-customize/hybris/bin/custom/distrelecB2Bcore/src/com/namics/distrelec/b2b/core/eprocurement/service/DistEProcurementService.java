/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service;

public interface DistEProcurementService {

    /**
     * Checks if current customer is member of EPROCUREMENTGROUP
     * 
     * @return is E-Procurement customer
     */
    boolean isEProcurementCustomer();

}
