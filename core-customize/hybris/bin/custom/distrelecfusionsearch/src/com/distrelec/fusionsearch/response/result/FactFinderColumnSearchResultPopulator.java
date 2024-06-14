package com.distrelec.fusionsearch.response.result;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.namics.distrelec.b2b.facades.search.converter.SearchResultProductConverter;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;

class FactFinderColumnSearchResultPopulator implements Populator<Map<String, Object>, SearchResultValueData> {

    static final String FALSE_STRING = "0";

    private final Map<String, String> lowerCaseFactFinderColumnMap;

    FactFinderColumnSearchResultPopulator() {
        DistFactFinderExportColumns[] columns = DistFactFinderExportColumns.values();
        lowerCaseFactFinderColumnMap = Stream.of(columns)
                                             .collect(Collectors.toMap(column -> column.getValue().toLowerCase(), DistFactFinderExportColumns::getValue));
    }

    @Override
    public void populate(Map<String, Object> doc, SearchResultValueData searchResult) {
        Map<String, Object> values = searchResult.getValues();

        for (Entry<String, Object> entry : doc.entrySet()) {
            String columnName = entry.getKey();
            Object columnValue = entry.getValue();

            String convertedColumnName = convertColumnName(columnName);
            Object convertedColumnValue = convertColumnValue(columnValue);

            values.put(convertedColumnName, convertedColumnValue);
        }
    }

    private String convertColumnName(String columnName) {
        String convertedColumnName = lowerCaseFactFinderColumnMap.get(columnName.toLowerCase());
        if (convertedColumnName != null) {
            return convertedColumnName;
        } else {
            return columnName;
        }
    }

    private Object convertColumnValue(Object columnValue) {
        if (columnValue instanceof Boolean) {
            if (Boolean.TRUE.equals(columnValue)) {
                return SearchResultProductConverter.TRUE_STRING;
            } else {
                return FALSE_STRING;
            }
        } else {
            return columnValue;
        }
    }
}
