/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.util;

import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.if12.v1.P3FaultMessage;
import com.distrelec.webservice.if15.v1.P2FaultMessage;
import com.distrelec.webservice.if19.v1.FaultMessage;
import org.apache.logging.log4j.Logger;

import javax.xml.ws.WebServiceException;

import static com.namics.distrelec.b2b.core.constants.DistConstants.ErrorSource.SAP_FAULT;
import static com.namics.distrelec.b2b.core.util.DistLogUtils.logError;

/**
 * Helper class for consistent logging of web service exception and SOAP fault messages from SAP PI calls.
 * 
 * @author ksperner, Namics AG
 * @since Namics Extensions 1.0
 */
public class SoapErrorLogHelper {

    /**
     * Helper method to log consistent error message for SOAP fault messages from SAP PI.
     * 
     * @param logger
     *            The logger to log the error to.
     * @param methodName
     *            The exact name of the function that caused the fault, e.g. "if15ReadOrder"
     * @param faultMessage
     *            The fault message from the SAP PI system.
     */
    public static void logSoapFault(final Logger logger, final String methodName, final P2FaultMessage faultMessage) {
        logError(logger, "{} SOAP error while calling method '{}' on SAP PI interface: faultstring = '{}', faultText = '{}'.", null,
                SAP_FAULT.getCode(methodName), methodName, faultMessage.getMessage(), faultMessage.getFaultInfo().getFaultText());
    }

    /**
     * Helper method to log consistent error message for SOAP fault messages from SAP PI.
     * 
     * @param logger
     *            The logger to log the error to.
     * @param methodName
     *            The exact name of the function that caused the fault, e.g. "if09ReadShippingMethods"
     * @param faultMessage
     *            The fault message from the SAP PI system.
     */
    public static void logSoapFault(final Logger logger, final String methodName, final com.distrelec.webservice.if11.v3.P1FaultMessage faultMessage) {
        logError(logger, "{} SOAP error while calling method '{}' on SAP PI interface: faultstring = '{}', faultText = '{}'.", null,
                SAP_FAULT.getCode(methodName), methodName, faultMessage.getMessage(), faultMessage.getFaultInfo().getFaultText());
    }
    
    /**
     * Helper method to log consistent error message for SOAP fault messages from SAP PI.
     * 
     * @param logger
     *            The logger to log the error to.
     * @param methodName
     *            The exact name of the function that caused the fault, e.g. "if09ReadShippingMethods"
     * @param faultMessage
     *            The fault message from the SAP PI system.
     */
    public static void logSoapFault(final Logger logger, final String methodName, final P1FaultMessage faultMessage) {
        logError(logger, "{} SOAP error while calling method '{}' on SAP PI interface: faultstring = '{}', faultText = '{}'.", null,
                SAP_FAULT.getCode(methodName), methodName, faultMessage.getMessage(), faultMessage.getFaultInfo().getFaultText());
    }

    /**
     * Helper method to log consistent error message for SOAP fault messages from SAP PI.
     * 
     * @param logger
     *            The logger to log the error to.
     * @param methodName
     *            The exact name of the function that caused the fault, e.g. "if09ReadShippingMethods"
     * @param faultMessage
     *            The fault message from the SAP PI system.
     */
    public static void logSoapFault(final Logger logger, final String methodName, final com.distrelec.webservice.if18.v1.P1FaultMessage faultMessage) {
        logError(logger, "{} SOAP error while calling method '{}' on SAP PI interface: faultstring = '{}', faultText = '{}'.", null,
                SAP_FAULT.getCode(methodName), methodName, faultMessage.getMessage(), faultMessage.getFaultInfo().getFaultText());
    }

    /**
     * Helper method to log consistent error message for SOAP fault messages from SAP PI This is for the P3FaultMessage.
     *
     * @param logger
     *            The logger to log the error to.
     * @param methodName
     *            The exact name of the function that caused the fault, e.g. "if12"
     * @param faultMessage
     *            The fault message from the SAP PI system.
     */
    public static void logSoapFault(final Logger logger, final String methodName, final P3FaultMessage faultMessage) {
        logError(logger, "{} SOAP error while calling method '{}' on SAP PI interface: faultstring = '{}', faultText = '{}'.", null,
                SAP_FAULT.getCode(methodName), methodName, faultMessage.getMessage(), faultMessage.getFaultInfo().getFaultText());
    }
    /**
     * Helper method to log consistent error message for SOAP fault messages from SAP PI This is for the P3FaultMessage.
     *
     * @param logger
     *            The logger to log the error to.
     * @param methodName
     *            The exact name of the function that caused the fault, e.g. "if12"
     * @param faultMessage
     *            The fault message from the SAP PI system.
     */
    public static void logSoapFault(final Logger logger, final String methodName, final FaultMessage faultMessage) {
        logger.error("SOAP error while calling method '" + methodName + "' on SAP PI interface: faultstring = '" + faultMessage.getFaultName()
                + "', faultText = '" + faultMessage.getFaultText() + "'.");
    }
    /**
     * Helper method to log consistent error message for SOAP fault messages from SAP PI This is for the P3FaultMessage.
     *
     * @param logger
     *            The logger to log the error to.
     * @param methodName
     *            The exact name of the function that caused the fault, e.g. "v1"
     * @param faultMessage
     *            The fault message from the SAP PI system.
     */
    public static void logSoapFault(final Logger logger, final String methodName, final com.distrelec.webservice.sap.v1.FaultMessage faultMessage) {
        logger.error("SOAP error while calling method '" + methodName + "' on SAP PI interface: faultstring = '" + faultMessage.getFaultName()
                + "', faultText = '" + faultMessage.getFaultText() + "'.");
    }
    /**
     * Helper method to log consistent error message for SOAP fault messages from SAP PI.
     * 
     * @param logger
     *            The logger to log the error to.
     * @param methodName
     *            The exact name of the function that caused the fault, e.g. "if19RMA"
     * @param faultMessage
     *            The fault message from the SAP PI system.
     */
    public static void logSoapFault(final Logger logger, final String methodName, final com.distrelec.webservice.if19.v1.P2FaultMessage faultMessage) {
        logger.error("SOAP error while calling method '" + methodName + "' on SAP PI interface: faultstring = '" + faultMessage.getMessage()
                + "', faultText = '" + faultMessage.getFaultInfo().getFaultText() + "'.");
    }


    /**
     * Helper method to log consistent error message for web service exception resulting from call to SAP PI.
     * 
     * @param logger
     *            The logger to log the error to.
     * @param methodName
     *            The exact name of the function that caused the exception, e.g. "if09ReadShippingMethods"
     * @param webServiceException
     *            The exception occurring during the call the SAP PI system.
     */
    public static void logWebServiceException(final Logger logger, final String methodName, final WebServiceException webServiceException) {
        logError(logger, "{} WebServiceException while calling method '{}' on SAP PI interface: message = '{}', cause = '{}'.", null,
                SAP_FAULT.getCode(methodName), methodName, webServiceException.getMessage(), webServiceException.getCause().getMessage());
    }

    public static void logSoapFault(final Logger logger, final String methodName, final com.distrelec.webservice.if02.v1.P2FaultMessage faultMessage) {
        logError(logger, "{} SOAP error while calling method '{}' on SAP PI interface: faultstring = '{}', faultText = '{}'.", null,
                SAP_FAULT.getCode(methodName), methodName, faultMessage.getMessage(), faultMessage.getFaultInfo().getFaultText());
    }
}
