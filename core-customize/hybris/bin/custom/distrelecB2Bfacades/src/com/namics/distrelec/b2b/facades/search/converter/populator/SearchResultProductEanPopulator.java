package com.namics.distrelec.b2b.facades.search.converter.populator;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class SearchResultProductEanPopulator extends AbstractSearchResultPopulator {

    private static final String EAN_KEY = "EAN";

    @Override
    public void populate(SearchResultValueData searchResultValueData, ProductData b2BProductData) throws ConversionException {
        b2BProductData.setEan((String) searchResultValueData.getValues().get(EAN_KEY));
    }
}
