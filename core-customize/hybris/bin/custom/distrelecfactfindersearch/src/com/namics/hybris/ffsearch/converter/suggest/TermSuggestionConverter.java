/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.converter.suggest;

import com.namics.hybris.ffsearch.data.suggest.TermSuggestion;

import de.factfinder.webservice.ws71.FFsearch.ResultSuggestion;

/**
 * {@code SearchTermConverter}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
public class TermSuggestionConverter extends AbstractSuggestionConverter<ResultSuggestion, TermSuggestion> {

    @Override
    protected TermSuggestion createTarget() {
        return new TermSuggestion();
    }

    @Override
    public TermSuggestion convert(final ResultSuggestion source, final TermSuggestion target) {
        target.setCount(source.getHitCount() != null ? source.getHitCount().intValue() : 0);
        target.setName(source.getName());
        target.setQuery(source.getSearchParams().getQuery());
        return target;
    }
}
