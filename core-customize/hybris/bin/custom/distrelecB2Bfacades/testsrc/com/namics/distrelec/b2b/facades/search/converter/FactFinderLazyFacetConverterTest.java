/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter;

import com.namics.hybris.ffsearch.data.facet.FactFinderLazyFacetData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Tests the {@link FactFinderLazyFacetConverter} class.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class FactFinderLazyFacetConverterTest {

    private final FactFinderLazyFacetConverter factFinderLazyFacetConverter = new FactFinderLazyFacetConverter();

    @Test
    public void testConvert() {
        // Init
        final FactFinderLazyFacetData<SearchQueryData> lazyFacet = new FactFinderLazyFacetData<SearchQueryData>();
        lazyFacet.setName("Sperrspannung");

        final SearchStateData searchStateData = new SearchStateData();
        final SearchQueryData queryData = new SearchQueryData();
        queryData.setValue("*&filter_Buyable=1");
        searchStateData.setQuery(queryData);

        final String path = "/de/Distrelec-Group-e-Shop/Components/Aktive-Komponenten/c/cat-L2D_379524";
        final String parameters = "q=*&filter_Buyable=1";
        searchStateData.setUrl(path + "?" + parameters);

        final Converter<SearchQueryData, SearchStateData> mockedSearchStateConverter = Mockito.mock(Converter.class);
        Mockito.when(mockedSearchStateConverter.convert(Mockito.<SearchQueryData> anyObject())).thenReturn(searchStateData);

        factFinderLazyFacetConverter.setSearchStateConverter(mockedSearchStateConverter);

        // Action
        final FactFinderLazyFacetData<SearchStateData> lazyFacetResult = factFinderLazyFacetConverter.convert(lazyFacet);

        // Evaluation
        Assert.assertEquals(lazyFacet.getName(), lazyFacetResult.getName());
        Assert.assertEquals(path + FactFinderLazyFacetConverter.ADDITIONAL_FACET_PATH + "?" + parameters + "&"
                + FactFinderLazyFacetConverter.ADDITIONAL_FACET_PARAM_NAME + "=" + lazyFacet.getName(), lazyFacetResult.getQuery().getUrl());
    }

}
