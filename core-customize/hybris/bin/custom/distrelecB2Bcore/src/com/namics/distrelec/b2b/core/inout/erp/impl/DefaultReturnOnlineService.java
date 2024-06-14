package com.namics.distrelec.b2b.core.inout.erp.impl;

import javax.xml.ws.WebServiceException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.distrelec.webservice.if19.v1.P2FaultMessage;
import com.distrelec.webservice.if19.v1.RMACreateRequest;
import com.distrelec.webservice.if19.v1.RMACreateResponse;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderRequest;
import com.distrelec.webservice.if19.v1.RMAGetListForOrderResponse;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsRequest;
import com.distrelec.webservice.if19.v1.RMAGetOrderItemsResponse;
import com.distrelec.webservice.if19.v1.SIHybrisIF19Out;
import com.namics.distrelec.b2b.core.inout.erp.ReturnOnlineService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;

/**
 * @author dattembhurs
 *
 */
public class DefaultReturnOnlineService implements ReturnOnlineService {

    private static final Logger LOG = LogManager.getLogger(SapOrderService.class);

    // injected by spring
    private SIHybrisIF19Out hybrisIF19Out;

    @Override
    public RMACreateResponse rmaCreate(final RMACreateRequest createRMARequest) {
        return executeSOAPRequestForCreateRMA(createRMARequest);
    }

    @Override
    public RMAGetOrderItemsResponse rmaGetOrderItems(final RMAGetOrderItemsRequest rmaOrderItemRequest) {
        return executeSOAPRequestForOrderRMAItems(rmaOrderItemRequest);
    }

    @Override
    public RMAGetListForOrderResponse rmaListForOrder(final RMAGetListForOrderRequest rmaOrderItemRequest) {
        return executeSOAPRequestForRMAListForOrders(rmaOrderItemRequest);
    }

    public RMACreateResponse executeSOAPRequestForCreateRMA(RMACreateRequest rmaCreateRequest) {
        RMACreateResponse rmaCreateResponse = null;
        try {
            rmaCreateResponse = getHybrisIF19Out().if19RMACreate(rmaCreateRequest);
        } catch (P2FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if19RMACreateRequest", faultMessage);
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if19RMACreateRequest", wsEx);
        }
        return rmaCreateResponse;
    }

    public RMAGetOrderItemsResponse executeSOAPRequestForOrderRMAItems(RMAGetOrderItemsRequest rmaGetOrderItemsRequest) {
        RMAGetOrderItemsResponse rmaGetOrderItemsResponse = null;
        try {
            rmaGetOrderItemsResponse = getHybrisIF19Out().if19RMAGetOrderItems(rmaGetOrderItemsRequest);
        } catch (P2FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if19RMAGetOrderItems", faultMessage);
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if19RMAGetOrderItems", wsEx);
        }
        return rmaGetOrderItemsResponse;
    }

    public RMAGetListForOrderResponse executeSOAPRequestForRMAListForOrders(RMAGetListForOrderRequest rmaGetListForOrderRequest) {
        RMAGetListForOrderResponse rmaGetListForOrderResponse = null;
        try {
            rmaGetListForOrderResponse = getHybrisIF19Out().if19RMAGetListForOrder(rmaGetListForOrderRequest);
        } catch (P2FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if19RMAGetListForOrder", faultMessage);
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if19RMAGetListForOrder", wsEx);
        }
        return rmaGetListForOrderResponse;
    }

    public SIHybrisIF19Out getHybrisIF19Out() {
        return hybrisIF19Out;
    }

    public void setHybrisIF19Out(SIHybrisIF19Out hybrisIF19Out) {
        this.hybrisIF19Out = hybrisIF19Out;
    }

}
