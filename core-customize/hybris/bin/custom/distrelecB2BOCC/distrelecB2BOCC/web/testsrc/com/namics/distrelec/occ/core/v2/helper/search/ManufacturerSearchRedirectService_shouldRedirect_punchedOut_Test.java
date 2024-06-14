package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class ManufacturerSearchRedirectService_shouldRedirect_punchedOut_Test
        extends AbstractManufacturerSearchRedirectServiceTest {

    final String manufacturerCode = "manufacturerCode";

    @Mock
    SearchRedirectRule redirectRule;

    @Mock
    SearchQueryData searchQuery;

    @Before
    public void setUp() {
        doReturn(manufacturerCode).when(searchQuery).getCode();
        doReturn(true).when(distManufacturerFacade).isManufacturerExcluded(manufacturerCode);
        doReturn(true).when(productFacade).enablePunchoutFilterLogic();
        doReturn(redirectRule).when(searchRedirectRuleFactory).createStatusRule(SearchRedirectStatus.PUNCHED_OUT);
    }

    @Test
    public void testShouldRedirectPunchedOut() {
        SearchRedirectRule redirectRule = service.shouldRedirect(searchQuery);

        assertEquals(this.redirectRule, redirectRule);
    }
}
