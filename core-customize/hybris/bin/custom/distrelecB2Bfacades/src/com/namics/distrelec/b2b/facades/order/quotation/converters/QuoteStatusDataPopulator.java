package com.namics.distrelec.b2b.facades.order.quotation.converters;

import com.namics.distrelec.b2b.core.model.DistQuotationStatusModel;
import com.namics.distrelec.b2b.facades.order.quotation.data.QuoteStatusData;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class QuoteStatusDataPopulator implements Populator<DistQuotationStatusModel, QuoteStatusData> {
    @Override
    public void populate(DistQuotationStatusModel distQuotationStatusModel, QuoteStatusData quoteStatusData) throws ConversionException {
        quoteStatusData.setCode(distQuotationStatusModel.getCode());
        quoteStatusData.setName(distQuotationStatusModel.getName());
    }
}
