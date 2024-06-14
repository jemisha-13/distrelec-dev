package com.namics.distrelec.b2b.storefront.security.filters;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;

@RunWith(MockitoJUnitRunner.class)
public class DistXFrameOptionsFilterTest {

    @InjectMocks
    DistXFrameOptionsFilter distXFrameOptionsFilter;

    @Mock
    CMSSiteService cmsSiteService;

    @Test
    public void testIsXFrameOptionsProtectionEnabled() {
        CMSSiteModel cmsSiteModel = mock(CMSSiteModel.class);

        when(cmsSiteService.getCurrentSite()).thenReturn(cmsSiteModel);
        when(cmsSiteModel.isXFrameOptionsProtected()).thenReturn(true);

        boolean isXFrameOptionsProtectionEnabled = distXFrameOptionsFilter.isXFrameOptionsProtectionEnabled();
        assertTrue(isXFrameOptionsProtectionEnabled);
    }
}
