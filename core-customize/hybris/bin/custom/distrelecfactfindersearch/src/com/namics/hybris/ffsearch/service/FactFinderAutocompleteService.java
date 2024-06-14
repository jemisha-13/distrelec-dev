/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.service;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfFilter;

/**
 * Autocomplete interface. Its purpose is to retrieve valid search terms that start with the user's given input, to enhance the search
 * experience and avoid searches for nonexistent terms. This interface/service should be called asynchronously, assisting the user while
 * typing.
 * 
 * @param <RESULT>
 *            The type of the result data structure containing the returned suggestions
 */
public interface FactFinderAutocompleteService<RESULT> {
    /**
     * Get the auto complete suggestions for the input provided.
     * 
     * @param input
     *            the user's input on which the autocomplete is based
     * @return a list of suggested search terms
     */
    RESULT getAutocompleteSuggestions(String input, ArrayOfFilter arrayOfFilter);
}