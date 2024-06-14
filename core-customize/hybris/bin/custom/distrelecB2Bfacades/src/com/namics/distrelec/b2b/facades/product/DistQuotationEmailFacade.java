package com.namics.distrelec.b2b.facades.product;

import com.namics.distrelec.b2b.core.event.DistQuoteEmailEvent;

public interface DistQuotationEmailFacade {

	/**
	 * Sends product price quotation email to distrelec customer service.
	 */
	void sendQuotationEmail(DistQuoteEmailEvent event);

}
