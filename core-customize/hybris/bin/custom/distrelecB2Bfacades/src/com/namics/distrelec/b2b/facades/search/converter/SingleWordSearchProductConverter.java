/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.search.converter;

import java.util.stream.Collectors;

import com.namics.hybris.ffsearch.data.facet.SingleWordSearchItem;

import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * {@code SingleWordSearchProductConverter}
 * <p>
 * Convert the {@link SingleWordSearchItem} holding the {@link SearchResultValueData} to new instance of {@link SingleWordSearchItem}
 * holding {@link ProductData}
 * </p>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.7
 */
public class SingleWordSearchProductConverter extends
                                              AbstractPopulatingConverter<SingleWordSearchItem<SearchResultValueData>, SingleWordSearchItem<ProductData>> {

    private Converter<SearchResultValueData, ProductData> searchResultProductConverter;

    /*
     * (non-Javadoc)
     * @see de.hybris.platform.converters.impl.AbstractPopulatingConverter#populate(java.lang.Object, java.lang.Object)
     */
    @Override
    public void populate(final SingleWordSearchItem<SearchResultValueData> source, final SingleWordSearchItem<ProductData> target) {
        if (source == null || target == null) {
            return;
        }

        target.setCount(source.getCount());
        target.setNumber(source.getNumber());
        target.setSingleTerm(source.getSingleTerm());
        if (source.getItems() != null) {
            target.setItems(source.getItems().stream().map(item -> getSearchResultProductConverter().convert(item)).collect(Collectors.toList()));
        }

        super.populate(source, target);
    }

    public Converter<SearchResultValueData, ProductData> getSearchResultProductConverter() {
        return searchResultProductConverter;
    }

    public void setSearchResultProductConverter(final Converter<SearchResultValueData, ProductData> searchResultProductConverter) {
        this.searchResultProductConverter = searchResultProductConverter;
    }
}
