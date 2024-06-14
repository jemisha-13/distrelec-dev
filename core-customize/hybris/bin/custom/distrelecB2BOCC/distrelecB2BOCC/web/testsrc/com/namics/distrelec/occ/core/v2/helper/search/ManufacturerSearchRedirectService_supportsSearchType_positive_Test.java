package com.namics.distrelec.occ.core.v2.helper.search;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.namics.hybris.ffsearch.data.search.DistSearchType;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class ManufacturerSearchRedirectService_supportsSearchType_positive_Test
        extends AbstractManufacturerSearchRedirectServiceTest {

    @Test
    public void testSupportsSearchTypePositive() {
        boolean supported = service.supportsSearchType(DistSearchType.MANUFACTURER);

        assertTrue(supported);
    }
}
