package com.distrelec.fusionsearch.response.result;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.facades.product.data.DistPromotionLabelData;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class PromotionLabelResultPopulator implements Populator<Map<String, Object>, SearchResultValueData> {

    static final String ACTIVE_PROMOTION_LABELS_PROPERTY = "activePromotionLabels";

    @Override
    public void populate(Map<String, Object> doc, SearchResultValueData searchResult) throws ConversionException {
        String activePromotionLabels = (String) doc.get(ACTIVE_PROMOTION_LABELS_PROPERTY);

        if (activePromotionLabels != null) {
            String[] activePromLabels = activePromotionLabels.split("/");

            List<DistPromotionLabelData> convertedPromoLabels = Arrays.stream(activePromLabels)
                                                                      .map(this::convertLabelsListToData)
                                                                      .collect(Collectors.toList());

            String promotionLabels = convertToJson(convertedPromoLabels);
            searchResult.getValues().put(DistFactFinderExportColumns.PROMOTIONLABELS.getValue(), promotionLabels);
        }
    }

    private DistPromotionLabelData convertLabelsListToData(String activePromLabel) {
        String[] promLabelFields = activePromLabel.split("\\|");
        String promLabelCode = promLabelFields[0];
        String promLabelName = promLabelFields[1];

        DistPromotionLabelData promLabelData = new DistPromotionLabelData();
        promLabelData.setCode(promLabelCode);
        promLabelData.setLabel(promLabelName);
        promLabelData.setActive(true);
        return promLabelData;
    }

    private String convertToJson(List<DistPromotionLabelData> promoLabelData) {
        try {
            return new ObjectMapper().writeValueAsString(promoLabelData);
        } catch (JsonProcessingException e) {
            throw new ConversionException("Unable to convert to json", e);
        }
    }
}
