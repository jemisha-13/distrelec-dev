/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.suggestion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.namics.distrelec.b2b.facades.product.data.DistPromotionLabelData;
import com.namics.distrelec.b2b.facades.search.data.DistFactFinderReplacementData;
import de.hybris.bootstrap.annotations.UnitTest;
import junit.framework.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Small unit tests for simulating JSON Data parsing from the search index.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
@UnitTest
public class SearchResultJsonParsingTest {

    private static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Test
    public void test() {
        final String json = "[{code:hotOffer,label:\"hot offer\",rank:1,active:true},{code:top,label:tipp,rank:2,active:true},{code:hit,label:hit,rank:3,active:true},{code:used,label:used,rank:4,active:false},{code:new,label:neu,rank:7,active:true},{code:bestseller,label:bestseller,rank:6,active:true}]";
        final Type collectionType = new TypeToken<Collection<DistPromotionLabelData>>() { // empty constructor
        }.getType();
        final List<DistPromotionLabelData> data = GSON.fromJson(json, collectionType);
        Assert.assertNotNull(data);
        Assert.assertEquals(data.size(), 6);
    }

    @Test
    public void testReplacementParsing() {
        final String json = "{\"eolDate\":\"2013-12-01 00:00:00\",\"buyable\":true,\"reason\":\"B Repl. Reason\"}";

        final DistFactFinderReplacementData data = GSON.fromJson(json, DistFactFinderReplacementData.class);
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getEolDate());
        Assert.assertEquals(data.isBuyable(), true);
        Assert.assertEquals(data.getReason(), "B Repl. Reason");
    }

    @Test
    public void testReplacementParsingWithoutReason() {
        final String json = "{\"eolDate\":\"2013-12-01 00:00:00\",\"buyable\":true}";

        final DistFactFinderReplacementData data = GSON.fromJson(json, DistFactFinderReplacementData.class);
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getEolDate());
        Assert.assertEquals(data.isBuyable(), true);
        Assert.assertNull(data.getReason());
    }

    @Test
    public void testReplacementParsingNotBuyable() {
        final String json = "{\"eolDate\":\"2013-12-01 00:00:00\",\"buyable\":false}";

        final DistFactFinderReplacementData data = GSON.fromJson(json, DistFactFinderReplacementData.class);
        Assert.assertNotNull(data);
        Assert.assertNotNull(data.getEolDate());
        Assert.assertEquals(false, data.isBuyable());
        Assert.assertNull(data.getReason());
    }

    @Test
    public void testReplacementParsingWithouthEOL() {
        final String jsonWithoutEolDate = "{\"buyable\":false}";
        final DistFactFinderReplacementData data = GSON.fromJson(jsonWithoutEolDate, DistFactFinderReplacementData.class);
        Assert.assertNotNull(data);
        Assert.assertNull(data.getEolDate());
        Assert.assertEquals(false, data.isBuyable());
        Assert.assertNull(data.getReason());
    }

    @Test
    public void testCategoryUrlParsing() {
        final String categoryCodeValues = "{\"Network / Telecommunication\" : \"cat-L2D_379678\", \"GN Netcom Accessories\" : \"cat-203655\", \"Accessories\" : \"cat-12060\", \"Distrelec Group e-Shop\" : \"cat-L0D_324785\", \"IT & Hobby\" : \"cat-L1D_379523\" }";
        final Map<String, String> categoryCodes = GSON.fromJson(categoryCodeValues, Map.class);
        Assert.assertNotNull(categoryCodes);
        Assert.assertNotNull(categoryCodes.get("Network / Telecommunication"));
        Assert.assertEquals(categoryCodes.get("Network / Telecommunication"), "cat-L2D_379678");
    }

    @Test
    public void testCategoryUrlParsingWithouthValues() {
        final String categoryCodeValues = "{}";
        final Map<String, String> categoryCodes = GSON.fromJson(categoryCodeValues, Map.class);
        Assert.assertNotNull(categoryCodes);
        Assert.assertTrue(categoryCodes.isEmpty());
    }
}
