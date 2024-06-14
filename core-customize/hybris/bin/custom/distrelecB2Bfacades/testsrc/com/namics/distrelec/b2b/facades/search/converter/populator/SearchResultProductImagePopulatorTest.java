/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.search.converter.populator;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

/**
 * Tests the {@link SearchResultProductImagePopulator} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class SearchResultProductImagePopulatorTest {

    private final SearchResultProductImagePopulator searchResultProductImagePopulator = new SearchResultProductImagePopulator();

    private ProductData b2bProductData;

    @Before
    public void setUp() {
        b2bProductData = new ProductData();
    }

    @Test
    public void testPopulate() {
        // Init
        final SearchResultValueData searchResultValueData = new SearchResultValueData();
        final Map<String, Object> values = new HashMap<>();

        final String portraitSmall = "http://test.distrelec.ch/Web/WebShopImages/portrait_small/_t/if/ps_273198f.jpg";
        values.put(DistFactFinderExportColumns.IMAGE_URL.getValue(), portraitSmall);

        final String portraitSmallWebp = "http://test.distrelec.ch/Web/WebShopImages/portrait_small/_t/if/ps_273198f.webp";
        values.put(DistFactFinderExportColumns.IMAGE_WEBP_URL.getValue(), portraitSmallWebp);

        final String landscapeSmall = "http://test.distrelec.ch/Web/WebShopImages/landscape_small/_t/if/ls_273198f.jpg";
        final String landscapeSmallWebp = "http://test.distrelec.ch/Web/WebShopImages/landscape_small/_t/if/ls_273198f.webp";

        final String landscapeMedium = "http://test.distrelec.ch/Web/WebShopImages/landscape_medium/_t/if/lm_273198f.jpg";
        final String landscapeMediumWebp = "http://test.distrelec.ch/Web/WebShopImages/landscape_medium/_t/if/lm_273198f.webp";

        final String additionalImageUrls = "{\"landscape_medium\":\"" + landscapeMedium
                                           + "\",\"landscape_small\":\"" + landscapeSmall
                                           + "\",\"landscape_medium_webp\":\"" + landscapeMediumWebp
                                           + "\",\"landscape_small_webp\":\"" + landscapeSmallWebp
                                           + "\"}";

        values.put(DistFactFinderExportColumns.ADDITIONAL_IMAGE_URLS.getValue(), additionalImageUrls);

        searchResultValueData.setValues(values);

        // Action
        searchResultProductImagePopulator.populate(searchResultValueData, b2bProductData);

        // Evaluation
        assertEquals(1, b2bProductData.getProductImages().size());
        assertEquals(6, b2bProductData.getProductImages().get(0).size());
        assertEquals(portraitSmall, b2bProductData.getProductImages().get(0).get(MediaFormat.PORTRAIT_SMALL).getUrl());
        assertEquals(MediaFormat.PORTRAIT_SMALL, b2bProductData.getProductImages().get(0).get(MediaFormat.PORTRAIT_SMALL).getFormat());
        assertEquals(portraitSmallWebp, b2bProductData.getProductImages().get(0).get(MediaFormat.PORTRAIT_SMALL_WEBP).getUrl());
        assertEquals(landscapeSmall, b2bProductData.getProductImages().get(0).get(MediaFormat.LANDSCAPE_SMALL).getUrl());
        assertEquals(landscapeSmallWebp, b2bProductData.getProductImages().get(0).get(MediaFormat.LANDSCAPE_SMALL_WEBP).getUrl());
        assertEquals(landscapeMedium, b2bProductData.getProductImages().get(0).get(MediaFormat.LANDSCAPE_MEDIUM).getUrl());
        assertEquals(landscapeMediumWebp, b2bProductData.getProductImages().get(0).get(MediaFormat.LANDSCAPE_MEDIUM_WEBP).getUrl());
    }

}
