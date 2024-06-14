package com.distrelec.fusionsearch.response.result;

import java.util.Map;

import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class FusionToFFColumnResultPopulator implements Populator<Map<String, Object>, SearchResultValueData> {

    static final String DIST_MANUFACTURER_PROPERTY = "distManufacturer";

    static final String PRODUCT_FAMILY_PROPERTY = "productFamily";

    static final String URL_PROPERTY = "url";

    private final Map<String, DistFactFinderExportColumns> FUSION_TO_FF_COLUMN_MAPPING = Map.of(DIST_MANUFACTURER_PROPERTY,
                                                                                                DistFactFinderExportColumns.MANUFACTURER,
                                                                                                PRODUCT_FAMILY_PROPERTY,
                                                                                                DistFactFinderExportColumns.PRODUCT_FAMILY_NAME, URL_PROPERTY,
                                                                                                DistFactFinderExportColumns.PRODUCT_URL);

    @Override
    public void populate(Map<String, Object> doc, SearchResultValueData searchResult) throws ConversionException {
        for (String fusionProp : FUSION_TO_FF_COLUMN_MAPPING.keySet()) {
            if (doc.containsKey(fusionProp)) {
                String prop = (String) doc.get(fusionProp);
                DistFactFinderExportColumns ffColumn = FUSION_TO_FF_COLUMN_MAPPING.get(fusionProp);
                searchResult.getValues().put(ffColumn.getValue(), prop);
            }
        }
    }
}
