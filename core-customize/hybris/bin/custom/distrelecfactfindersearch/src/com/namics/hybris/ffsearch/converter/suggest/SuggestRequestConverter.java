/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import javax.xml.ws.soap.SOAPFaultException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.namics.hybris.ffsearch.data.suggest.SuggestRequest;
import com.namics.hybris.ffsearch.data.suggest.SuggestResponse;
import com.namics.hybris.ffsearch.service.CookieSearchPortTypeWrapper;
import com.namics.hybris.ffsearch.service.impl.FactFinderAuthentication;

import de.factfinder.webservice.ws71.FFsearch.SuggestResult;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;

/**
 * Population Converter which uses the FactFinder instance to convert a {@link SuggestRequest} to a {@link SuggestResponse}. This is also
 * known as "executing a search request and fetching the result".
 * 
 * @author bhauser, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class SuggestRequestConverter extends AbstractPopulatingConverter<SuggestRequest, SuggestResponse> {

    private static final Logger LOG = LoggerFactory.getLogger(SuggestRequestConverter.class);

    private CookieSearchPortTypeWrapper searchWebserviceClientWrapper;
    private FactFinderAuthentication authentication;

    @Override
    public void populate(final SuggestRequest source, final SuggestResponse target) {
        target.setSuggestRequest(source);
        try {
            final SuggestResult searchResult = searchWebserviceClientWrapper.getSuggestions(source.getSearchParams(), source.getControlParams(),
                    getAuthentication().getSearchAuth());
            target.setSuggestResults(searchResult.getSuggestions());
        } catch (final SOAPFaultException e) {
            LOG.error("Unable to search for suggestion in FactFinder.", e);
        } catch (final Exception e) {
            LOG.error("Unable to search for suggestion in FactFinder.", e);
        }
        super.populate(source, target);
    }

    @Override
    protected SuggestResponse createTarget() {
        return new SuggestResponse();
    }

    // BEGIN GENERATED CODE

    @Required
    public void setSearchWebserviceClientWrapper(CookieSearchPortTypeWrapper searchWebserviceClientWrapper) {
        this.searchWebserviceClientWrapper = searchWebserviceClientWrapper;
    }

    protected FactFinderAuthentication getAuthentication() {
        return authentication;
    }

    @Required
    public void setAuthentication(final FactFinderAuthentication authentication) {
        this.authentication = authentication;
    }

}
