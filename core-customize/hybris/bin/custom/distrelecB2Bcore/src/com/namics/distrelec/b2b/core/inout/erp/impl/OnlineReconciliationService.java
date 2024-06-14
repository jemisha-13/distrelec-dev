package com.namics.distrelec.b2b.core.inout.erp.impl;

import java.math.BigInteger;

import javax.xml.ws.WebServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.webservice.if02.v1.File;
import com.distrelec.webservice.if02.v1.P2FaultMessage;
import com.distrelec.webservice.if02.v1.PricesTotalsRequest;
import com.distrelec.webservice.if02.v1.PricesTotalsResponse;
import com.distrelec.webservice.if02.v1.SIIF02PricesTotalsOut;
import com.namics.distrelec.b2b.core.inout.erp.ReturnOnlineService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;

/**
 * @author dattembhurs
 *
 */
public class OnlineReconciliationService {

    private static final Logger LOG = LogManager.getLogger(SapOrderService.class);

    // injected by spring
    private SIIF02PricesTotalsOut hybrisIF02Out;


    public PricesTotalsResponse sendProcessedRecordCount(final String filename, final BigInteger processedRecords ) {
    	PricesTotalsRequest pricesTotalsRequest = new PricesTotalsRequest();
    	File processedfile = new File();
    	processedfile.setFileName(filename);
    	processedfile.setRecords(processedRecords);
    	pricesTotalsRequest.setFile(processedfile);
        return executeSOAPRequestForPriceTotals(pricesTotalsRequest);
    }
   
    public PricesTotalsResponse executeSOAPRequestForPriceTotals(PricesTotalsRequest pricesTotalsRequest) {
    	PricesTotalsResponse pricesTotalsResponse = null;
        try {
        	pricesTotalsResponse = getHybrisIF02Out().if02PricesTotals(pricesTotalsRequest);
        } catch (P2FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if02PriceTotals", faultMessage);
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if02PriceTotals", wsEx);
        }
        return pricesTotalsResponse;
    }

    public SIIF02PricesTotalsOut getHybrisIF02Out() {
        return hybrisIF02Out;
    }

    public void setHybrisIF02Out(SIIF02PricesTotalsOut hybrisIF02Out) {
        this.hybrisIF02Out = hybrisIF02Out;
    }


}
