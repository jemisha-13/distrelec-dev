package com.namics.distrelec.b2b.facades.search.converter.populator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class SearchResultChannelAvailabilityPopulator extends AbstractSearchResultPopulator {

    private static final Logger log = LoggerFactory.getLogger(SearchResultChannelAvailabilityPopulator.class);

    @Autowired
    private DistrelecProductFacade productFacade;

    @Override
    public void populate(SearchResultValueData source, ProductData target) throws ConversionException {
        String productCode = (String) source.getValues().get("ProductNumber");

        boolean availableToB2B = !productFacade.isProductExcludedForSiteChannel(productCode, SiteChannel.B2B);
        target.setAvailableToB2B(availableToB2B);

        boolean availableToB2C = !productFacade.isProductExcludedForSiteChannel(productCode, SiteChannel.B2C);
        target.setAvailableToB2C(availableToB2C);

    }
}
