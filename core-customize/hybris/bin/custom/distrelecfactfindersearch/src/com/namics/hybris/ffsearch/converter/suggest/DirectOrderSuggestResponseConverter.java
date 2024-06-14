/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.suggest.AutocompleteSuggestion;
import com.namics.hybris.ffsearch.data.suggest.SuggestRequest;

import de.factfinder.webservice.ws71.FFsearch.ResultSuggestion;
import de.hybris.platform.servicelayer.config.ConfigurationService;

/**
 * Converts a suggest response for direct order.
 * 
 * @author dathusir, Distrelec
 * @since Distrelec 1.0
 * 
 */
public class DirectOrderSuggestResponseConverter extends SuggestResponseConverter {

    private static final Logger LOG = LoggerFactory.getLogger(DirectOrderSuggestResponseConverter.class);

    private ConfigurationService configurationService;

    @Override
    protected void convertSuggestion(final ResultSuggestion suggestion, final SuggestRequest request, final AutocompleteSuggestion target) {
        target.setTerm(getSuggestTerm(request));

        // Results for Blocks
        if (getSuggestType().equals(SuggestType.get(suggestion.getType()))) {
            target.getRes().getProds().getList().add(getProductSuggestionConverter().convert(suggestion));
        } else {
            LOG.debug("Ignore converting suggestion of type {}", suggestion.getType());
        }
    }

    /**
     * Configurable suggest type. Use the property distrelecfactfindersearch.suggest.type.directOrder to define which suggest type is used
     * for direct order.
     * 
     * @return the configured suggest type
     */
    public SuggestType getSuggestType() {
        return SuggestType
                .valueOf(getConfigurationService().getConfiguration().getString("distrelecfactfindersearch.suggest.type.directOrder", "DIRECT_ORDER"));
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
