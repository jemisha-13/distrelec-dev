/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import org.junit.Assert;
import org.junit.Test;

import com.namics.hybris.ffsearch.data.suggest.CategorySuggestion;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.factfinder.webservice.ws71.FFsearch.ResultSuggestion;
import de.factfinder.webservice.ws71.FFsearch.String2StringMap;
import de.factfinder.webservice.ws71.FFsearch.String2StringMap.Entry;

/**
 * Tests the {@link CategorySuggestionConverter} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class CategorySuggestionConverterTest {

    private final CategorySuggestionConverter categorySuggestionConverter = new CategorySuggestionConverter();

    @Test
    public void testConvert() {
        // Init
        final ResultSuggestion resultSuggestion = getSampleResultSuggestion();

        // Action
        final CategorySuggestion categorySuggestion = categorySuggestionConverter.convert(resultSuggestion);

        // Evaluation
        Assert.assertEquals("Attribute parentCategory not as expected", "LED Lampen/Zubehör", categorySuggestion.getL1());
    }

    @Test
    public void testConvertUrlEncoded() {
        // Init
        final ResultSuggestion resultSuggestion = getSampleResultSuggestion();

        // Action
        final CategorySuggestion categorySuggestion = categorySuggestionConverter.convert(resultSuggestion);

        // Evaluation
        Assert.assertEquals("Attribute parentCategory not as expected", "LED Lampen/Zubehör", categorySuggestion.getL1());
    }

    private ResultSuggestion getSampleResultSuggestion() {
        final ResultSuggestion resultSuggestion = new ResultSuggestion();
        resultSuggestion.setHitCount(Integer.valueOf(10));

        final String2StringMap map = new String2StringMap();

        Entry entry = new Entry();
        entry.setKey(DistFactFinderExportColumns.BUYABLE.getValue());
        entry.setValue("1");
        map.getEntry().add(entry);
        entry = new Entry();
        entry.setKey(DistFactFinderExportColumns.CATEGORY_EXTENSIONS.getValue());
        entry.setValue("[{ \"url\" : \"/de/Netzteile-%26-Licht/c/cat-L1D_379521\"}, { \"url\" : \"/de/Netzteile-%26-Licht/Lichtquellen/c/cat-L2D_379659\", \"imageUrl\" : \"/Web/WebShopImages/landscape_small/ho/ne/16333_iPhone.jpg\"}, { \"url\" : \"/de/Netzteile-%26-Licht/Lichtquellen/LED-Lampen%2FZubeh%3Fr/c/cat-L3D_525567\", \"imageUrl\" : \"/Web/WebShopImages/landscape_small/ho/ne/16333_iPhone.jpg\"}, { \"url\" : \"/de/Netzteile-%26-Licht/Lichtquellen/LED-Lampen%2FZubeh%C3%B6r/Zubeh%C3%B6r-f%C3%BCr-LED-Lampen/c/cat-442368\"}, { \"url\" : \"/de/Netzteile-%26-Licht/Lichtquellen/LED-Lampen/Zubeh%C3%B6r-f%C3%BCr-LED-Lampen/Lampenabdeckungen-f%C3%BCr-Master-LEDbulb-Designer/c/cat-442386\"}, ]");
        map.getEntry().add(entry);
        entry = new Entry();
        entry.setKey(CategorySuggestionConverter.SOURCE_FIELD);
        entry.setValue("Category4");
        map.getEntry().add(entry);

        resultSuggestion.setAttributes(map);
        return resultSuggestion;
    }

}
