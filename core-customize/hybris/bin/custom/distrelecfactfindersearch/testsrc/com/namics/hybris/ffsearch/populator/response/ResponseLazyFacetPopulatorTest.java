/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.populator.response;

import org.junit.Assert;
import org.junit.Test;

import com.namics.hybris.ffsearch.data.facet.FactFinderFacetSearchPageData;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import com.namics.hybris.ffsearch.data.search.SearchResponse;

import de.factfinder.webservice.ws71.FFsearch.ArrayOfGroup;
import de.factfinder.webservice.ws71.FFsearch.ArrayOfGroupElement;
import de.factfinder.webservice.ws71.FFsearch.Group;
import de.factfinder.webservice.ws71.FFsearch.GroupElement;
import de.factfinder.webservice.ws71.FFsearch.Result;
import de.hybris.bootstrap.annotations.UnitTest;

/**
 * Tests the {@link ResponseLazyFacetPopulator} class.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
@UnitTest
public class ResponseLazyFacetPopulatorTest {

    private final ResponseLazyFacetPopulator populator = new ResponseLazyFacetPopulator();

    @Test
    public void testPopulate() {
        // Init
        final String lazyGroupName = "TestGroupName";
        final SearchResponse searchResponse = getSearchResponse(lazyGroupName, null);

        final FactFinderFacetSearchPageData<SearchQueryData, ?> searchPageData = new FactFinderFacetSearchPageData();

        // Action
        populator.populate(searchResponse, searchPageData);

        // Evaluation
        Assert.assertEquals(1, searchPageData.getLazyFacets().size());
        Assert.assertEquals(lazyGroupName, searchPageData.getLazyFacets().get(0).getName());
    }

    @Test
    public void testPopulateWithoutLazyFacet() {
        // Init
        final SearchResponse searchResponse = getSearchResponse("TestGroupName", "TestElementName");

        final FactFinderFacetSearchPageData<SearchQueryData, ?> searchPageData = new FactFinderFacetSearchPageData();

        // Action
        populator.populate(searchResponse, searchPageData);

        // Evaluation
        Assert.assertEquals(0, searchPageData.getLazyFacets().size());
    }

    private SearchResponse getSearchResponse(final String lazyGroupName, final String groupElementName) {
        final SearchResponse searchResponse = new SearchResponse();

        final Result result = new Result();
        searchResponse.setSearchResult(result);

        final ArrayOfGroup arrayOfGroup = new ArrayOfGroup();
        result.setGroups(arrayOfGroup);

        final Group lazyGroup = new Group();
        lazyGroup.setName(lazyGroupName);
        arrayOfGroup.getGroup().add(lazyGroup);

        final ArrayOfGroupElement arrayOfGroupElement = new ArrayOfGroupElement();
        lazyGroup.setElements(arrayOfGroupElement);

        final GroupElement groupElement = new GroupElement();
        groupElement.setName(groupElementName);
        arrayOfGroupElement.getGroupElement().add(groupElement);

        return searchResponse;
    }

}
