/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product;

import com.namics.distrelec.b2b.core.event.DistCatalogPlusPriceRequestEvent;
import com.namics.distrelec.b2b.core.event.DistQuoteProductPriceEvent;
import com.namics.distrelec.b2b.core.service.search.pagedata.QuotationHistoryPageableData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuotationHistoryData;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;
import com.namics.distrelec.b2b.facades.product.data.quote.QuotationRequestData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

import java.util.List;

/**
 * Quote product price facade interface.
 *
 * @author pbueschi, Namics AG
 *
 */
public interface DistProductPriceQuotationFacade {

    /**
     * Sends product price quotation email to distrelec customer service.
     *
     * @param distQuoteProductPriceEvent
     */
    void sendProductPriceQuotation(final DistQuoteProductPriceEvent distQuoteProductPriceEvent);

    /**
     * Sends product price quotation email to distrelec customer service.
     *
     * @param distCatalogPlusPriceRequestEvent
     */
    void sendCatalogPlusProductPriceRequest(final DistCatalogPlusPriceRequestEvent distCatalogPlusPriceRequestEvent);

    /**
     * Retrieve a list of {@link QuotationRequestData} belonging to the current user.
     *
     * @param status
     *            the quotation status list
     * @return a list of {@code QuotationRequestData}
     */
    List<QuotationHistoryData> getQuotationHistory(final String status);

    /**
     * Returns the quotation history of the current user for given statuses.
     *
     * @param pageableData
     *            paging information
     * @param statuses
     *            array of quotation statuses to filter the results
     * @return The quotation history of the current user.
     */
    SearchPageData<QuotationHistoryData> getQuotationHistory(final QuotationHistoryPageableData pageableData, final String statuses);

    QuotationData getQuotationDetails(final String quotationId);

    /**
     *
     * @param cart
     * @return QuotationData
     */
    QuoteStatusData createCartQuotation();

    /**
     * Retrieve all quotation statuses from the database.
     *
     * @return a list of {@code QuoteStatusData}
     */
    List<QuoteStatusData> getQuoteStatuses();


    /**
     *
     * @param previousQuoteId
     * @return {@code QuoteStatusData}
     */
    QuoteStatusData resubmitQuotation(final String previousQuoteId);

    /**
     *
     * @return whether if the user is under the Submission limit for quotes.
     */
    boolean checkIfUserWithinQuoteSubmissionLimit();

    /**
     * Increment Quotes submitted counter
     */
    void incrementQuotesRequestedCounter();
}
