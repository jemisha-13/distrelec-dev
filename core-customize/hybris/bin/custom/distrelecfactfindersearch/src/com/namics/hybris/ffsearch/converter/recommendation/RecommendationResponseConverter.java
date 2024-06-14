/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.recommendation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.hybris.ffsearch.populator.suggestion.DistFactFinderEnergyEfficiencyPopulator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.namics.distrelec.b2b.core.suggestion.SuggestionEnergyEfficencyData;
import com.namics.hybris.ffsearch.data.recommendation.RecommendationResponse;

import de.factfinder.webservice.ws71.FFrecommender.Record;
import de.factfinder.webservice.ws71.FFrecommender.String2StringMap;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Response converter turning {@link RecommendationResponse} into a list of {@link SearchResultValueData}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class RecommendationResponseConverter implements Converter<RecommendationResponse, List<SearchResultValueData>> {

    private static final Logger LOG = LoggerFactory.getLogger(RecommendationResponseConverter.class);

    private static final String ENERGY_EFFICIENCY_KEY = "energyEfficiency";
    private static final String TYPENAME_KEY = "TypeName";
    private static final String MANUFACTURER_KEY = "Manufacturer";

    @Autowired
    private ObjectMapper jsonObjectMapper;

    @Autowired
    private DistFactFinderEnergyEfficiencyPopulator distFactFinderEnergyEfficiencyPopulator;

    @Override
    public List<SearchResultValueData> convert(final RecommendationResponse source, final List<SearchResultValueData> target) throws ConversionException {
        if (source != null && source.getRecommendations() != null && source.getRecommendations().getResultRecords() != null //
                && CollectionUtils.isNotEmpty(source.getRecommendations().getResultRecords().getRecord())) {
            for (final Record record : source.getRecommendations().getResultRecords().getRecord()) {
                final SearchResultValueData resultValues = convert(record);
                target.add(resultValues);
            }
        }
        return target;
    }

    private SearchResultValueData convert(final Record source) {
        final SearchResultValueData result = new SearchResultValueData();
        final Map<String, Object> values = Maps.newHashMap();
        for (final String2StringMap.Entry property : source.getRecord().getEntry()) {
            values.put(property.getKey(), property.getValue());
        }

        String manufacturer = (values.get(MANUFACTURER_KEY) instanceof String) ? (String) values.get(MANUFACTURER_KEY) : null;
        String typeName = (values.get(TYPENAME_KEY) instanceof String) ? (String) values.get(TYPENAME_KEY) : null;
        String energyEfficiencyJson = (values.get(ENERGY_EFFICIENCY_KEY) instanceof String) ? (String) values.get(ENERGY_EFFICIENCY_KEY) : null;

        SuggestionEnergyEfficencyData energyData = new SuggestionEnergyEfficencyData();
        getDistFactFinderEnergyEfficiencyPopulator().populate(energyEfficiencyJson, energyData);
        if (StringUtils.isNotBlank(energyData.getEfficency()) || StringUtils.isNotBlank(energyData.getPower())) {
            energyData.setManufacturer(manufacturer);
            energyData.setType(typeName);
            try {
                values.put("energyEfficiencyData", toJson(energyData));
            } catch (IOException e) {
                LOG.warn("Exception occurred during conversion of energy efficiency data", e);
            }
        } else {
            values.put("energyEfficiencyData", "");
        }

        result.setValues(values);

        return result;
    }

    protected String toJson(final Object obj) throws IOException {
        return jsonObjectMapper.writeValueAsString(obj);
    }

    @Override
    public List<SearchResultValueData> convert(final RecommendationResponse source) throws ConversionException {
        return convert(source, createTarget());
    }

    protected List<SearchResultValueData> createTarget() {
        return Lists.newArrayList();
    }

    public DistFactFinderEnergyEfficiencyPopulator getDistFactFinderEnergyEfficiencyPopulator() {
        return distFactFinderEnergyEfficiencyPopulator;
    }

    public void setDistFactFinderEnergyEfficiencyPopulator(final DistFactFinderEnergyEfficiencyPopulator distFactFinderEnergyEfficiencyPopulator) {
        this.distFactFinderEnergyEfficiencyPopulator = distFactFinderEnergyEfficiencyPopulator;
    }
}
