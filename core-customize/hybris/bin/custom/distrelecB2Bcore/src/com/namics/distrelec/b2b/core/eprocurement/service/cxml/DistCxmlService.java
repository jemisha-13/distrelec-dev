/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.eprocurement.service.cxml;

import javax.servlet.http.HttpServletRequest;

import com.namics.distrelec.b2b.core.eprocurement.service.cxml.impl.CxmlOutboundSection;

import de.hybris.platform.order.exceptions.CalculationException;

/**
 * CXML eProcurement service
 * 
 * @author rhaemmerli, Namics AG
 * @since Distrelec 1.0
 */
public interface DistCxmlService {

    /**
     * Checks if current customer is member of CXMLCUSTOMERGROUP and valid ociSession
     * 
     * @return is CXML customer
     */
    boolean isCxmlCustomer();

    /**
     * Gets redirect URL if additional CXML functionalities are called.
     * 
     * @return redirect URL
     */
    String getCxmlRedirectUrl();

    /**
     * Do additional CXML login (spring security login already done) for set up all necessary attributes.
     * 
     * @param request
     * @throws CxmlException
     */
    void doCxmlLogin(final HttpServletRequest request) throws CxmlException;

    /**
     * Create the cxml document
     * 
     * @throws CxmlException
     * @throws CalculationException
     * @return a cxml document
     */
    String createCxmlOrderMessage() throws CxmlException, CalculationException;

    /**
     * Returns an {@link CxmlOutboundSection}
     * 
     * @return {@link CxmlOutboundSection}
     */
    CxmlOutboundSection getOutboundSection();

}
