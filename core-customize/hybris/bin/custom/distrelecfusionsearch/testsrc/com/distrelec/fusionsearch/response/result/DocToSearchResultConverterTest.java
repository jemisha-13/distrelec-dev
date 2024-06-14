package com.distrelec.fusionsearch.response.result;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;

@UnitTest
public class DocToSearchResultConverterTest {

    DocToSearchResultConverter docToSearchResultConverter;

    @Before
    public void setUp() {
        docToSearchResultConverter = new DocToSearchResultConverter();
    }

    @Test
    public void testCreatesValuesMap() {
        SearchResultValueData searchResult = docToSearchResultConverter.createTarget();
        assertNotNull(searchResult.getValues());
    }
}
