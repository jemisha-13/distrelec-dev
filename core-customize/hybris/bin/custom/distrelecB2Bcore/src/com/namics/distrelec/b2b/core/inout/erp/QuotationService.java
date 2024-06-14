/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp;

import com.distrelec.webservice.if18.v1.CreateQuotationRequest;
import com.distrelec.webservice.if18.v1.CreateQuotationResponse;
import com.distrelec.webservice.if18.v1.QuotationsResponse;
import com.distrelec.webservice.if18.v1.ReadQuotationsResponse;
import com.namics.distrelec.b2b.core.service.search.pagedata.QuotationHistoryPageableData;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.order.CartModel;

/**
 * {@code SapQuotationService}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.5
 */
public interface QuotationService {

    /**
     * Create a quotation request.
     * 
     * @param articleNumber
     *            the article number
     * @param quantity
     *            the desired quantity
     * @param quotationNote
     *            the customer note.
     * @return the quotation Id. {@code null}, if the operation fails.
     */
    String createQuotation(final String articleNumber, final int quantity, final String quotationNote);


    /**
     *
     * @param cart
     * @return
     */
    CreateQuotationResponse createCartQuotation(final CartModel cart,final B2BCustomerModel user );

    /**
     * Fetch all the quotation requests made by the customer
     * 
     * @param pageableData
     *            the filter data
     * @param statuse
     *            the status
     * @return a list of {@code QuotationsResponse}.
     */
    SearchPageData<QuotationsResponse> searchQuotations(final QuotationHistoryPageableData pageableData, final String statuse);

    /**
     * Fetch details of quotation requests made by the customer
     * 
     * @param quotationId
     * @param customerId
     * @param salesOrg
     * @return {@code ReadQuotationsResponse}.
     */
    ReadQuotationsResponse getQuotationDetails(final String quotationId, final String customerId, final String salesOrg);

    /**
     * Resubmit Quotation for Phase 3
     *
     * @param quotationRequest
     * @return {@code ReadQuotationsResponse}.
     */
    CreateQuotationResponse resubmitQuotation(final CreateQuotationRequest quotationRequest);

    SearchPageData<QuotationsResponse> searchQuotations(final QuotationHistoryPageableData pageableData, final String status, final boolean forceRefresh);
}
