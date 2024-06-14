/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;

import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * test to validate some things while improving the factfinder export.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.20
 * 
 */
@Ignore
public class FlexibleSearchOptimizationTest extends ServicelayerTransactionalTest {

    @Resource
    private FlexibleSearchService flexibleSearchService;

    // @Resource(name="distff.productExportParameterProviderV2")
    // private DistFlexibleSearchParameterProvider<DistFactFinderExportChannelModel> parameterProvider;
    //
    // @Resource(name="distff.distFactFinderProductExportQueryCreatorV2")
    // private DistFactFinderProductExportQueryCreatorV2 distFlexibleSearchQueryCreator;

    // @Before
    // public void prepare() throws Exception {
    // importCsv("/distrelecfactfindersearch/test/PromotionLabels.impex", "utf-8");
    // }

    @Test
    public void testParametersInSelect() {

        // init
        final String queryString = "Select ({uid} || '-' || ?parameter || '-' || {uid}), ?parameter FROM {Employee} where {uid} = 'admin'";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
        query.addQueryParameter("parameter", "test");

        final List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        resultClassList.add(String.class);
        resultClassList.add(String.class);
        query.setResultClassList(resultClassList);

        // execute
        final SearchResult<List<Object>> searchResult = flexibleSearchService.search(query);

        // evaluateno
        Assert.assertEquals(1, searchResult.getCount());

        final List<Object> row = searchResult.getResult().get(0);
        Assert.assertEquals("admin-test-admin", (String) row.get(0));
        Assert.assertEquals("test", (String) row.get(1));
    }

    @Test
    public void testParametersInSelectWithMoreThan7Attributes() {

        // init
        final String queryString = "Select ({uid} || '-' || ?parameter1), ?parameter2, ?parameter3, ?parameter4, ?parameter5, ?parameter6, ?parameter7, ?parameter8 FROM {Employee} where {uid} = 'admin'";

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
        int i = 1;
        while (i <= 8) {
            query.addQueryParameter("parameter" + i, "test " + i++);
        }

        final List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        resultClassList.add(String.class);
        resultClassList.add(String.class);
        query.setResultClassList(resultClassList);

        // execute
        final SearchResult<List<Object>> searchResult = flexibleSearchService.search(query);

        // evaluateno
        Assert.assertEquals(1, searchResult.getCount());

        final List<Object> row = searchResult.getResult().get(0);
        Assert.assertEquals("admin-test 1", (String) row.get(0));
        final int j = 2;
        while (j <= 8) {
            Assert.assertEquals("test " + j, row.get(j - 1));
        }
    }

    // @Test
    // public void testGetPromotionLabelParameters() {
    // //execute
    // final Map<String, Object> parameters = parameterProvider.getParameters(null);
    //
    // //evaluate
    // Assert.assertEquals("Bestseller (meistverkauftes Produkt)", parameters.get("PromotionLabel_bestseller_label"));
    // Assert.assertEquals(Integer.valueOf(6), parameters.get("PromotionLabel_bestseller_rank"));
    // Assert.assertEquals("Hit", parameters.get("PromotionLabel_hit_label"));
    // Assert.assertEquals(Integer.valueOf(3), parameters.get("PromotionLabel_hit_rank"));
    // Assert.assertEquals("Hot offer", parameters.get("PromotionLabel_hotOffer_label"));
    // Assert.assertEquals(Integer.valueOf(1), parameters.get("PromotionLabel_hotOffer_rank"));
    // Assert.assertEquals("Neu", parameters.get("PromotionLabel_new_label"));
    // Assert.assertEquals(Integer.valueOf(7), parameters.get("PromotionLabel_new_rank"));
    // Assert.assertEquals("Top Price", parameters.get("PromotionLabel_noMover_label"));
    // Assert.assertEquals(Integer.valueOf(8), parameters.get("PromotionLabel_noMover_rank"));
    // Assert.assertEquals("Angebot", parameters.get("PromotionLabel_offer_label"));
    // Assert.assertEquals(Integer.valueOf(5), parameters.get("PromotionLabel_offer_rank"));
    // Assert.assertEquals("Top", parameters.get("PromotionLabel_top_label"));
    // Assert.assertEquals(Integer.valueOf(2), parameters.get("PromotionLabel_top_rank"));
    // Assert.assertEquals("gebraucht", parameters.get("PromotionLabel_used_label"));
    // Assert.assertEquals(Integer.valueOf(4), parameters.get("PromotionLabel_used_rank"));
    // }

    // @Test
    // public void testGetPromotionLabelQuery() {
    // //init
    // final StringBuilder query = new StringBuilder();
    // //execute
    // final StringBuilder builder = distFlexibleSearchQueryCreator.appendPromotionLabelsSelect(query);
    //
    // //evaluate
    // Assert.assertEquals("", builder.toString());
    // }
}
