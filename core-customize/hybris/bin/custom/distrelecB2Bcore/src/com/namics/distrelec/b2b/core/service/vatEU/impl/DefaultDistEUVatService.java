/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.vatEU.impl;

import com.namics.distrelec.b2b.core.service.vatEU.DistEUVatService;
import java.util.ResourceBundle;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.SOAPFaultException;
 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import com.distrelec.webservice.checkVatService.v1.CheckVatPortType;
 
import de.hybris.platform.servicelayer.util.ServicesUtil;
/**
 * Default implementation of DistB2BCommerceUnitService.
 * 
 * @author daehusir, Distrelec
 * @since Distrelec 1.0
 * 
 */

@CacheConfig(cacheManager = "vatEUCacheManager")
public class DefaultDistEUVatService  implements DistEUVatService {
	 private static final Logger LOG = LogManager.getLogger(DefaultDistEUVatService.class);
	    private static final String NOT_NULL_VAT_MSG = "Vat number must not be null!";
	 
	    private CheckVatPortType webServiceClient;
	    
	    @Override
	    @Cacheable(cacheNames = "vatEUValidationResponse", key = "{#vatNumber, #countryCode}")
		public boolean validateVat(String vatNumber, final String countryCode) throws VatEUServiceException{
	        ServicesUtil.validateParameterNotNull(vatNumber, NOT_NULL_VAT_MSG);
	        vatNumber = vatNumber.replaceAll("\\s+","");
	        final long startTime = System.currentTimeMillis();
	 
	        Holder<String> country_ = new Holder<String>(countryCode);
	        Holder<String> vatNumber_ = new Holder<String>(vatNumber);
	        Holder<XMLGregorianCalendar> date_ = new Holder<XMLGregorianCalendar>();
	        Holder<Boolean> valid_ = new Holder<Boolean>();
	        Holder<String> name_ = new Holder<String>();
	        Holder<String> address_ = new Holder<String>();
	 
	        try {
	            webServiceClient.checkVat(country_, vatNumber_, date_, valid_, name_, address_);
	        }catch (SOAPFaultException ex) {
	            SOAPFault fault = ex.getFault();
	            String faultKey = fault.getFaultString();
	            throw new VatEUServiceException(faultKey, countryCode + "-" + vatNumber + ": " + ex.getMessage());
	        }
	        LOG.debug("Call to EU VAt Check took {} ms", (System.currentTimeMillis() - startTime));
	        Boolean vatEUValidationResponse = valid_.value;
	        return vatEUValidationResponse;
	    }
	    
	    public CheckVatPortType getWebServiceClient() {
	        return webServiceClient;
	    }
	 
	    public void setWebServiceClient(final CheckVatPortType webServiceClient) {
	        this.webServiceClient = webServiceClient;
	    }
}