package com.distrelec.fusionsearch.response.result;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.FACTFINDER_UNIT_PREFIX;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class FeaturesResultPopulator implements Populator<Map<String, Object>, SearchResultValueData> {

    static final String DISPLAY_FIELDS_PROPERTY = "displayFields";

    static final String ATTRIBUTE_NAME = "attributeName";

    static final String ATTRIBUTE_VALUE = "value";

    static final String ATTRIBUTE_UNIT = "unit";

    private final Gson GSON = new Gson();

    @Override
    public void populate(Map<String, Object> doc, SearchResultValueData searchResult) throws ConversionException {
        String displayFields = (String) doc.get(DISPLAY_FIELDS_PROPERTY);

        List<Map<String, Object>> features = GSON.fromJson(displayFields, List.class);

        if (features != null) {
            processFeatures(searchResult, features);
        }
    }

    private void processFeatures(SearchResultValueData searchResult, List<Map<String, Object>> features) {
        StringBuilder featuresStringBuilder = new StringBuilder();

        for (Map<String, Object> feature : features) {
            String attrName = (String) feature.get(ATTRIBUTE_NAME);
            Object attrValue = feature.get(ATTRIBUTE_VALUE);
            String attrUnit = (String) feature.get(ATTRIBUTE_UNIT);

            featuresStringBuilder.append("|").append(attrName);

            if (StringUtils.isNotEmpty(attrUnit)) {
                featuresStringBuilder.append(FACTFINDER_UNIT_PREFIX).append(attrUnit);
            }

            featuresStringBuilder.append("=").append(attrValue);
        }

        featuresStringBuilder.append("|");

        String featuresString = featuresStringBuilder.toString();
        searchResult.getValues().put(DistFactFinderExportColumns.TECHNICAL_ATTRIBUTES.getValue(), featuresString);
    }

}
