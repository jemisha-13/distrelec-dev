/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.order.invoice;

import com.namics.distrelec.b2b.core.service.search.pagedata.DistInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.core.service.search.pagedata.DistOnlineInvoiceHistoryPageableData;
import com.namics.distrelec.b2b.facades.order.invoice.data.DistB2BInvoiceHistoryData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;

public interface InvoiceHistoryFacade {

    /**
     * Returns the invoice history of the current user for given filters.
     * 
     * @param pageableData
     *            paging information
     */
    SearchPageData<DistB2BInvoiceHistoryData> getInvoiceHistory(final DistInvoiceHistoryPageableData pageableData);

    SearchPageData<DistB2BInvoiceHistoryData> getInvoiceSearchHistory(final DistOnlineInvoiceHistoryPageableData pageableData);
}
