package com.namics.distrelec.occ.core.v2.helper.search;

import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;

public class ManufacturerSeachRedirectService_shouldRedirect_allowed_Test
        extends AbstractManufacturerSearchRedirectServiceTest {

    final String manufacturerCode = "manufacturerCode";

    @Mock
    SearchQueryData searchQuery;

    @Before
    public void setUp() {
        doReturn(manufacturerCode).when(searchQuery).getCode();
        doReturn(false).when(distManufacturerFacade).isManufacturerExcluded(manufacturerCode);
    }

    @Test
    public void testShouldRedirectAllowed() {
        SearchRedirectRule redirectRule = service.shouldRedirect(searchQuery);

        assertNull(redirectRule);
    }
}
