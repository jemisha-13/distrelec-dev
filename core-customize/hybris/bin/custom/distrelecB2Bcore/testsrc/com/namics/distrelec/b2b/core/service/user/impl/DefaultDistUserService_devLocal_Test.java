package com.namics.distrelec.b2b.core.service.user.impl;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import de.hybris.bootstrap.annotations.UnitTest;
import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.Collection;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@UnitTest
@RunWith(Parameterized.class)
public class DefaultDistUserService_devLocal_Test extends AbstractDefaultDistUserServiceTest {

    @Mock
    Configuration configuration;

    @Parameter(0)
    public String ipAddress;

    @Parameter(1)
    public boolean isDevLocal;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"127.0.0.1", true},
                {"80.12.13.1", false}});
    }

    @Before
    public void setUp() {
        doReturn(configuration).when(configurationService).getConfiguration();
        doReturn(ipAddress).when(configuration).getString(DistConstants.PropKey.Environment.YMS_HOSTNAME, "");
    }

    @Test
    public void testIsDevLocal() {
        boolean isDevLocal = distUserService.isDevLocal();

        assertEquals(this.isDevLocal, isDevLocal);
    }
}
