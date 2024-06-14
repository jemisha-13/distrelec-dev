package com.distrelec.fusionsearch.response.result;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Map;

import org.junit.Test;
import org.mockito.InjectMocks;

import com.namics.distrelec.b2b.facades.search.converter.SearchResultProductConverter;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class FactFinderColumnSearchResultPopulatorTest extends AbstractSearchResultPopulatorTest {

    @InjectMocks
    FactFinderColumnSearchResultPopulator ffColumnSearchResultPopulator;

    @Test
    public void testConvertColumnNameToMatchDistFactFinderExportColumns() {
        String productNumber = "productNumber"; // must be not equal to column name
        String productCode = "12345678";

        Map<String, Object> doc = Map.of(productNumber, productCode);

        // prerequisites
        assertNotEquals(productNumber, DistFactFinderExportColumns.PRODUCT_NUMBER.getValue());
        assertEquals(productNumber.toLowerCase(), DistFactFinderExportColumns.PRODUCT_NUMBER.getValue().toLowerCase());

        ffColumnSearchResultPopulator.populate(doc, searchResult);

        Map<String, Object> values = searchResult.getValues();
        assertEquals(1, values.size());
        assertEquals(productCode, values.get(DistFactFinderExportColumns.PRODUCT_NUMBER.getValue()));
    }

    @Test
    public void testConvertBooleanColumnValue() {
        String columnName = "columnName";
        boolean columnValue = true;
        Map<String, Object> doc = Map.of(columnName, columnValue);

        ffColumnSearchResultPopulator.populate(doc, searchResult);

        Map<String, Object> values = searchResult.getValues();
        assertEquals(1, values.size());
        assertEquals(SearchResultProductConverter.TRUE_STRING, values.get(columnName));
    }
}
