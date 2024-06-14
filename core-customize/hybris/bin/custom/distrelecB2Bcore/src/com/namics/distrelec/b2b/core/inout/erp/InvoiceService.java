/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import com.distrelec.webservice.if12.v1.SearchRequest;
import com.distrelec.webservice.if12.v1.SearchResponse;
import com.distrelec.webservice.sap.v1.InvoiceSearchRequest;
import com.distrelec.webservice.sap.v1.InvoiceSearchResponse;

/**
 * @author francesco, Distrelec AG
 * @since Distrelec Extensions 1.0
 * 
 */
public interface InvoiceService {

    /**
     * This
     * 
     * @param request
     * @return
     */
    public InvoiceSearchResponse searchInvoices(InvoiceSearchRequest request);

    /**
     * This
     *
     * @param request
     * @return
     */
    public SearchResponse searchInvoices(SearchRequest request);
}
