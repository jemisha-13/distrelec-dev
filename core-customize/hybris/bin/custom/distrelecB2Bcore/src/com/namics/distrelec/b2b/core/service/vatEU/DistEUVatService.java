/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.vatEU;

import java.net.SocketTimeoutException;

import javax.xml.ws.WebServiceException;

import com.namics.distrelec.b2b.core.service.vatEU.impl.VatEUServiceException;

/**
 * DistB2BCommerceUnitService extends B2BCommerceUnitService
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public interface DistEUVatService {

	boolean validateVat(String vatNumber, final String countryCode) throws VatEUServiceException, SocketTimeoutException, WebServiceException;
}
