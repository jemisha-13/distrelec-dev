package com.distrelec.fusionsearch.response.result;

import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_MEDIUM;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.LANDSCAPE_SMALL;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_SMALL;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.UNDERSCORE;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class AdditionalImageUrlResultPopulator implements Populator<Map<String, Object>, SearchResultValueData> {

    private final Map<String, String> imageUrlAttrMappings = Map.of(imageUrlAttr(LANDSCAPE_MEDIUM), LANDSCAPE_MEDIUM, imageUrlAttr(LANDSCAPE_SMALL),
                                                                    LANDSCAPE_SMALL, imageUrlAttr(PORTRAIT_SMALL), PORTRAIT_SMALL);

    private final Gson GSON = new Gson();

    @Override
    public void populate(Map<String, Object> doc, SearchResultValueData searchResult) throws ConversionException {
        Map<String, String> additionalImageUrls = new HashMap<>();

        for (Entry<String, Object> docEntry : doc.entrySet()) {
            String attrName = docEntry.getKey();
            String attrNameLowercase = attrName.toLowerCase();
            if (imageUrlAttrMappings.containsKey(attrNameLowercase)) {
                String mappedAttr = imageUrlAttrMappings.get(attrNameLowercase);
                String imageUrl = (String) docEntry.getValue();
                additionalImageUrls.put(mappedAttr, imageUrl);
            }
        }

        String additionalImageUrlJson = GSON.toJson(additionalImageUrls);
        searchResult.getValues().put(DistFactFinderExportColumns.ADDITIONAL_IMAGE_URLS.getValue(), additionalImageUrlJson);
    }

    private static String imageUrlAttr(String format) {
        String attr = DistFactFinderExportColumns.IMAGE_URL.getValue() + UNDERSCORE + format;
        String attrLowercase = attr.toLowerCase();
        return attrLowercase;
    }
}
