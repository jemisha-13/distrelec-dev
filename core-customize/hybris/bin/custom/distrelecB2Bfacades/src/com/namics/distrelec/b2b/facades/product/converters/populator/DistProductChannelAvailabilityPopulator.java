package com.namics.distrelec.b2b.facades.product.converters.populator;

import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import org.springframework.beans.factory.annotation.Autowired;

public class DistProductChannelAvailabilityPopulator implements Populator<ProductModel, ProductData> {

    @Autowired
    private DistrelecProductFacade productFacade;

    @Override public void populate(final ProductModel source, final ProductData target) throws ConversionException {
        boolean availableToB2B = !productFacade.isProductExcludedForSiteChannel(source.getCode(), SiteChannel.B2B);
        target.setAvailableToB2B(availableToB2B);

        boolean availableToB2C = !productFacade.isProductExcludedForSiteChannel(source.getCode(), SiteChannel.B2C);
        target.setAvailableToB2C(availableToB2C);
    }
}
