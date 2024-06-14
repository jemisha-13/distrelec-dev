/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import com.distrelec.webservice.if12.v1.P3FaultMessage;
import com.distrelec.webservice.if12.v1.SIHybrisIF12Out;
import com.distrelec.webservice.if12.v1.SearchRequest;
import com.distrelec.webservice.if12.v1.SearchResponse;
import com.distrelec.webservice.sap.v1.InvoiceSearchRequest;
import com.distrelec.webservice.sap.v1.InvoiceSearchResponse;
import com.distrelec.webservice.sap.v1.P1FaultMessage;
import com.distrelec.webservice.sap.v1.SIHybrisV1Out;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.InvoiceService;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import javax.xml.soap.*;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

/**
 * {@code SapInvoiceService}
 * 
 *
 */
public class SapInvoiceService extends AbstractSapService implements InvoiceService {

    private static final Logger LOG = LogManager.getLogger(SapInvoiceService.class);

    private ConfigurationService configurationService;

    private SIHybrisIF12Out webServiceIF12Client;

    private SIHybrisV1Out webServiceClient;

    public SapInvoiceService() {
        super(DistConstants.CacheName.INVOICE);
    }

    @Override
    public InvoiceSearchResponse searchInvoices(InvoiceSearchRequest request) {
        InvoiceSearchResponse response = getInvoicesFromCache(request);
        if (response == null) {
            // retrieve from SAP
            response = fetchUncachedInvoices(request);

            if (response == null) {
                return new InvoiceSearchResponse();
            }
        }


        return null;
    }

    @Override
    public SearchResponse searchInvoices(SearchRequest request) {
        SearchResponse response = getInvoicesFromCache(request);
        if (response == null) {
            // retrieve from SAP
            response = fetchUncachedInvoices(request);

            if (response == null) {
                return new SearchResponse();
            }
        }

        // convert the response
        return response;
    }

    /*
     * check cache for every product price. if found: add price to list. else: add to uncached list
     */
    private SearchResponse getInvoicesFromCache(final SearchRequest request) {
        return getFromCache(request, SearchResponse.class);
    }

    private InvoiceSearchResponse getInvoicesFromCache(final InvoiceSearchRequest request) {
        return getFromCache(request, InvoiceSearchResponse.class);
    }

    /*
     * fetch availabilities for products customer prices from SAP PI, add them to the cache and to the availabilities list.
     */
    private InvoiceSearchResponse fetchUncachedInvoices(final InvoiceSearchRequest request) {
        // build soap request for uncached products

        // execute request
        final InvoiceSearchResponse invoiceSearchResponse = executeSOAPRequest(request);

        if (null != invoiceSearchResponse && getCache() != null) {
            // cache the article price
            putIntoCache(request, invoiceSearchResponse);
        }
        return invoiceSearchResponse;
    }


    private SearchResponse fetchUncachedInvoices(final SearchRequest request) {
        // build soap request for uncached products

        // execute request
        final SearchResponse invoiceSearchResponse = executeSOAPRequest(request);

        if (null != invoiceSearchResponse && getCache() != null) {
            // cache the article price
            putIntoCache(request, invoiceSearchResponse);
        }
        return invoiceSearchResponse;
    }

    private SearchResponse executeSOAPRequest(final SearchRequest request) {
        SearchResponse invoiceSearchResponse = null;
        try {
            invoiceSearchResponse = getWebServiceIF12Client().search(request);
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "search", wsEx);
        } catch (P3FaultMessage p3FaultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "executeSoapRequest", p3FaultMessage);
        }
        return invoiceSearchResponse;
    }

    private InvoiceSearchResponse executeSOAPRequest(final InvoiceSearchRequest request) {
        InvoiceSearchResponse invoiceSearchResponse = null;
        final long startTime = System.currentTimeMillis();
        try {
            invoiceSearchResponse = getWebServiceClient().if16SearchInvoices(request);
        } catch (WebServiceException wsEx) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if16SearchInvoice", wsEx);
        } catch (P1FaultMessage p1FaultMessageFaultMessage) {
            LOG.error(p1FaultMessageFaultMessage);
        }
        final long endTime = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Call to SAP PI IF-16 SearchInvoice took " + (endTime - startTime) + "ms");
        }
        return invoiceSearchResponse;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public SIHybrisV1Out getWebServiceClient() {
        return webServiceClient;
    }

    @Required
    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public SIHybrisIF12Out getWebServiceIF12Client() {
        return webServiceIF12Client;
    }

    public void setWebServiceIF12Client(SIHybrisIF12Out webServiceIF12Client) {
        this.webServiceIF12Client = webServiceIF12Client;
    }
}
