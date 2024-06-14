/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service.impl;

import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.suggest.AutocompleteSuggestion;
import com.namics.hybris.ffsearch.data.suggest.SuggestRequest;
import com.namics.hybris.ffsearch.data.suggest.SuggestResponse;
import com.namics.hybris.ffsearch.service.FactFinderAutocompleteService;
import com.namics.hybris.ffsearch.service.FactFinderChannelService;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;
import de.factfinder.webservice.ws71.FFsearch.Params;
import de.factfinder.webservice.ws71.FFsearch.SearchControlParams;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * The FactFinder autocomplete service implementation of {@link FactFinderAutocompleteService}.
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class FactFinderAutocompleteServiceImpl implements FactFinderAutocompleteService<AutocompleteSuggestion> {

    private Converter<SuggestRequest, SuggestResponse> suggestRequestConverter;
    private Converter<SuggestResponse, AutocompleteSuggestion> suggestResponseConverter;
    private FactFinderChannelService channelService;

    @Override
    public AutocompleteSuggestion getAutocompleteSuggestions(final String input, final ArrayOfFilter arrayOfFilter) {
        // Build the request
        final SuggestRequest request = createSuggestRequest();
        request.getSearchParams().setQuery(input);
        if (arrayOfFilter != null) {
            request.getSearchParams().setFilters(arrayOfFilter);
        }

        // request.getSearchParams().setFilters(value);
        request.getSearchParams().setChannel(getChannelService().getCurrentFactFinderChannel());
        // Execute the suggest request
        final SuggestResponse response = getSuggestRequestConverter().convert(request);
        // Convert the response
        return getSuggestResponseConverter().convert(response);
    }

    protected SuggestRequest createSuggestRequest() {
        final SuggestRequest request = new SuggestRequest();
        request.setControlParams(new SearchControlParams());
        request.setSearchParams(new Params());
        return request;
    }

    public Converter<SuggestRequest, SuggestResponse> getSuggestRequestConverter() {
        return suggestRequestConverter;
    }

    @Required
    public void setSuggestRequestConverter(final Converter<SuggestRequest, SuggestResponse> suggestRequestConverter) {
        this.suggestRequestConverter = suggestRequestConverter;
    }

    public Converter<SuggestResponse, AutocompleteSuggestion> getSuggestResponseConverter() {
        return suggestResponseConverter;
    }

    @Required
    public void setSuggestResponseConverter(final Converter<SuggestResponse, AutocompleteSuggestion> suggestResponseConverter) {
        this.suggestResponseConverter = suggestResponseConverter;
    }

    public FactFinderChannelService getChannelService() {
        return channelService;
    }

    @Required
    public void setChannelService(final FactFinderChannelService channelService) {
        this.channelService = channelService;
    }

}
