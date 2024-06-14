/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.salesorg;

import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;

/**
 * Service for sales organization.
 * 
 * @author daehusir, Distrelec
 * 
 */
public interface DistSalesOrgService {

    /**
     * Returns the current sales org depending on the current site in the session.
     * 
     * @return the current sales org
     */
    DistSalesOrgModel getCurrentSalesOrg();

    /**
     * Returns the sales org for the given code.
     * 
     * @param code
     * @return the sales org for the given code.
     * @throws ModelNotFoundException
     *             if the sales org for the given code can not be found
     * @throws AmbiguousIdentifierException
     *             if more than one sales org has been found for the given code
     */
    DistSalesOrgModel getSalesOrgForCode(String code) throws ModelNotFoundException, AmbiguousIdentifierException;

    /**
     * Returns the sales org for the given country and brand by there codes.
     * 
     * @param countryCode
     * @param brandCode
     * @return the sales org for the given code.
     * @throws ModelNotFoundException
     *             if the sales org for the given code can not be found
     * @throws AmbiguousIdentifierException
     *             if more than one sales org has been found for the given code
     */
    DistSalesOrgModel getSalesOrgForCountryCodeAndBrandCode(String countryCode, String brandCode) throws ModelNotFoundException;

    boolean isCurrentSalesOrgItaly();

    boolean isCurrentSalesOrgExport();

}
