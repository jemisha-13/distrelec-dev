package com.namics.distrelec.b2b.occ.core.cms.populators;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.occ.core.basesite.data.DistSiteTrackingSettingsData;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class DefaultDistrelecCmsSiteModelToDataPopulator_getTrackingSettingsData_Test
        extends AbstracDistrelecCmsSiteModelToDataPopulatorTest {

    final String gtmAuth = "GTM_AUTH";
    final String gtmCookiesWin = "GTM_COOKIES_WIN";
    final String gtmPreview = "GTM_PREVIEW";
    final String gtmTagId = "GTM-TagId-123";

    @Mock
    protected Configuration config;


    @Before
    public void setUp() {
        doReturn(config).when(configurationService).getConfiguration();
        doReturn(gtmAuth).when(config).getString(DistConfigConstants.Tracking.GTM_AUTH);
        doReturn(gtmCookiesWin).when(config).getString(DistConfigConstants.Tracking.GTM_COOKIES_WIN);
        doReturn(gtmPreview).when(config).getString(DistConfigConstants.Tracking.GTM_PREVIEW);
        doReturn(gtmTagId).when(config).getString(DistConfigConstants.Tracking.GTM_TAG_ID);
    }

    @Test
    public void testGetTrackingSettingsData() {
        DistSiteTrackingSettingsData trackingSettingsData = populator.getTrackingSettingsData();

        assertEquals(this.gtmAuth, trackingSettingsData.getGtmAuth());
        assertEquals(this.gtmCookiesWin, trackingSettingsData.getGtmCookiesWin());
        assertEquals(this.gtmPreview, trackingSettingsData.getGtmPreview());
        assertEquals(this.gtmTagId, trackingSettingsData.getGtmTagId());
    }
}
