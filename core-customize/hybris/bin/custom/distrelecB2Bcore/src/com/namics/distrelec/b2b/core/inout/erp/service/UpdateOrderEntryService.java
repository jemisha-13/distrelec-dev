package com.namics.distrelec.b2b.core.inout.erp.service;

import com.distrelec.webservice.if11.v3.OrderEntryResponse;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

import java.util.Map;

public interface UpdateOrderEntryService {

    void updateOrderEntry(final OrderEntryResponse orderEntryResponse, final AbstractOrderEntryModel abstractOrderEntryModel, final Map<String, OrderEntryResponse> additionalAvailabilityInfoMap);

    void handleQuotation(final OrderEntryResponse orderEntryResponse, final AbstractOrderEntryModel abstractOrderEntryModel);

}
