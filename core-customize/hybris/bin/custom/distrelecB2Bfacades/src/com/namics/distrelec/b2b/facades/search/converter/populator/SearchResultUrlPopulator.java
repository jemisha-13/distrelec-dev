/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRODUCT_URL;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriComponentsBuilder;

import com.namics.hybris.ffsearch.data.search.DistSearchType;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.commerceservices.url.UrlResolver;

/**
 * Populator for the URL on {@link ProductData}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class SearchResultUrlPopulator extends AbstractSearchResultPopulator {

    private UrlResolver<ProductData> productDataUrlResolver;

    @Override
    public void populate(final SearchResultValueData source, final ProductData target) {
        populate(source, target, null);
    }

    public void populate(final SearchResultValueData source, final ProductData target, final DistSearchType searchType) {
        final String url = this.<String> getValue(source, PRODUCT_URL.getValue());
        if (StringUtils.isNotEmpty(url)) {
            final StringBuilder productUrl = new StringBuilder(url);

            final UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(productUrl.toString());

            target.setUrl(uriComponentsBuilder.build().toUriString());
        } else {
            target.setUrl(getProductDataUrlResolver().resolve(target));
        }
    }

    // BEGIN GENERATED CODE

    protected UrlResolver<ProductData> getProductDataUrlResolver() {
        return productDataUrlResolver;
    }

    @Required
    public void setProductDataUrlResolver(final UrlResolver<ProductData> productDataUrlResolver) {
        this.productDataUrlResolver = productDataUrlResolver;
    }

}
