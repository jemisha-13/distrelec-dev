package com.namics.distrelec.b2b.core.service.user.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@UnitTest
public class DefaultDistUserService_getInternalIps_Test extends AbstractDefaultDistUserServiceTest {

    final String internalIps = "internalIps";

    @Mock
    Configuration configuration;

    @Before
    public void setUp() {
        doReturn(configuration).when(configurationService).getConfiguration();
        doReturn(internalIps).when(configuration).getString(DistConstants.PropKey.Environment.INTERNAL_IPS);
    }

    @Test
    public void testGetInternalIps() {
        String internalIps = distUserService.getInternalIps();

        assertEquals(this.internalIps, internalIps);
    }
}
