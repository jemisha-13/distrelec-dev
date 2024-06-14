/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.paging;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;

import com.namics.hybris.ffsearch.data.paging.FactFinderPaginationData;
import com.namics.hybris.ffsearch.data.paging.ProductsPerPageOption;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.factfinder.webservice.ws71.FFsearch.ResultsPerPageOptionWithSearchParams;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.converters.Populator;

/**
 * {@code ProductsPerPageOptionPopulator}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class ProductsPerPageOptionPopulator<ITEM> implements Populator<SearchResponse, SearchPageData<ITEM>> {

    public static final int[] DEFAULT_VALUES = {10, 25, 50, 100};

    @Override
    public void populate(final SearchResponse source, final SearchPageData<ITEM> target) {
        if (source.getSearchResult() != null && source.getSearchResult().getResultsPerPageList() != null) {
            List<ResultsPerPageOptionWithSearchParams> resultsPerPageOptionWithSearchParams = source.getSearchResult().getResultsPerPageList().getResultsPerPageOptionWithSearchParams();

            if (CollectionUtils.isNotEmpty(resultsPerPageOptionWithSearchParams)){
                if (CollectionUtils.size(resultsPerPageOptionWithSearchParams) == DEFAULT_VALUES.length) {
                    List<ProductsPerPageOption> collect = source.getSearchResult().getResultsPerPageList().getResultsPerPageOptionWithSearchParams()
                            .stream()
                            .map(this::convertItem)
                            .sorted(Comparator.comparingInt(ProductsPerPageOption::getValue))
                            .collect(Collectors.toList());

                    ((FactFinderPaginationData) target.getPagination()).setProductsPerPageOptions(collect);
                    return;
                }
            }
        }
        // Use default options if nothing is setup in FACT-Finder
        setDefaultOptions((FactFinderPaginationData) target.getPagination());
    }

    /**
     * Converts an item of type {@code ResultsPerPageOptionWithSearchParams} to a {@code ProductsPerPageOption}
     * 
     * @param item
     *            an object of type {@code ResultsPerPageOptionWithSearchParams} to be converted.
     * @return a new instance of {@code ProductsPerPageOption}
     */
    private ProductsPerPageOption convertItem(final ResultsPerPageOptionWithSearchParams item) {
        return createOption(item.getValue() == null ? 0 : item.getValue(), BooleanUtils.isTrue(item.isDefault()),
                BooleanUtils.isTrue(item.isSelected()));
    }

    /**
     * Sets the default options to the pagination data
     * 
     * @param pagination
     *            the pagination data
     */
    private void setDefaultOptions(final FactFinderPaginationData pagination) {
        for (final int value : DEFAULT_VALUES) {
            pagination.getProductsPerPageOptions().add(createOption(value, value == DEFAULT_VALUES[0], value == pagination.getPageSize()));
        }
    }

    /**
     * Creates a new instance of {@code ProductsPerPageOption} and sets its values
     * 
     * @param value
     *            the value of the option
     * @param _default
     * @param selected
     * @return a new instance of {@code ProductsPerPageOption}
     */
    private ProductsPerPageOption createOption(final int value, final boolean _default, final boolean selected) {
        return new ProductsPerPageOption(value, _default, selected);
    }
}
