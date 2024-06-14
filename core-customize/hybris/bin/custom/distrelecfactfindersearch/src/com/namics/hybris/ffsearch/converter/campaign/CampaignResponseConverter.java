/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.campaign;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.namics.hybris.ffsearch.data.campaign.CampaignResponse;

import de.factfinder.webservice.ws71.FFcampaign.ArrayOfRecord;
import de.factfinder.webservice.ws71.FFcampaign.Campaign;
import de.factfinder.webservice.ws71.FFcampaign.Record;
import de.factfinder.webservice.ws71.FFcampaign.String2StringMap;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Response converter turning {@link CampaignResponse} into a list of {@link SearchResultValueData}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 */
public class CampaignResponseConverter implements Converter<CampaignResponse, List<SearchResultValueData>> {

    @Override
    public List<SearchResultValueData> convert(final CampaignResponse source, final List<SearchResultValueData> target) throws ConversionException {
        for (final Campaign campaign : source.getCampaigns().getCampaign()) {
            final ArrayOfRecord pushedProducts = campaign.getPushedProductsRecords();
            if (pushedProducts == null) {
                continue;
            }
            for (final Record record : pushedProducts.getRecord()) {
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
        result.setValues(values);
        return result;
    }

    @Override
    public List<SearchResultValueData> convert(final CampaignResponse source) throws ConversionException {
        return convert(source, createTarget());
    }

    protected List<SearchResultValueData> createTarget() {
        return Lists.newArrayList();
    }

}
