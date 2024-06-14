package com.namics.distrelec.b2b.core.inout.erp.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.distrelec.webservice.if12.v1.GetPaymentTermsRequest;
import com.distrelec.webservice.if12.v1.GetPaymentTermsResponse;
import com.distrelec.webservice.if12.v1.P3FaultMessage;
import com.distrelec.webservice.if12.v1.SIHybrisIF12Out;

public class TestClass {

	private static final Logger LOG = LoggerFactory.getLogger(TestClass.class);
	
	private SIHybrisIF12Out webServiceClient;
	
	
	public SIHybrisIF12Out getWebServiceClient() {
		return webServiceClient;
	}

	@Required
	public void setWebServiceClient(SIHybrisIF12Out webServiceClient) {
		this.webServiceClient = webServiceClient;
	}


	public void trigger()
	{
		
		GetPaymentTermsRequest prq=new GetPaymentTermsRequest();
		prq.setSalesOrganization("7310");
		
		GetPaymentTermsResponse prp = null;
		try {
			prp = webServiceClient.getPaymentTerms(prq);
		} catch (P3FaultMessage e) {
			// YTODO Auto-generated catch block
			LOG.warn("Exception occurred for payment terms request", e);
		}
		LOG.info(prp.getPaymentTerm().toString());
		
	}
}
