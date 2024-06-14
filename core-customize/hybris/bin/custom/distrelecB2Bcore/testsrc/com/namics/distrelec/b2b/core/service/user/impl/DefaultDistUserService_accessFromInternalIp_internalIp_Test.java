package com.namics.distrelec.b2b.core.service.user.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.Collection;

import static de.hybris.platform.testframework.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

@UnitTest
@RunWith(Parameterized.class)
public class DefaultDistUserService_accessFromInternalIp_internalIp_Test extends AbstractDefaultDistUserServiceTest {

    @Mock
    HttpServletRequest request;

    @Parameter(0)
    public String internalIps;

    @Parameter(1)
    public String remoteAddr;

    @Parameter(2)
    public String xForwardedFor;

    @Parameter(3)
    public boolean isInternal;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"80.12.13.1,11.12.32.12", "1.2.3.4", "11.12.32.12", true},
                {"80.12.13.1,1.2.3.4", "1.2.3.4", "11.12.32.12", true},
                {"80.12.13.1,11.12.32.12", "1.2.3.4", "3.2.1.0", false},
                {"80.12.13.1,11.12.32.12", "1.2.3.4", "11.12.32.12, 3.2.1.0", true}});
    }

    @Before
    public void setUp() {
        doReturn(false).when(distUserService).isDevLocal();
        doReturn(remoteAddr).when(request).getRemoteAddr();
        doReturn(internalIps).when(distUserService).getInternalIps();

        for (String header : DefaultDistUserService.HEADERS_TO_TRY) {
            doReturn(xForwardedFor).when(request).getHeader(header);
        }
    }

    @Test
    public void testAssertInternalIp() {
        boolean isInternal = distUserService.accessFromInternalIp(request);

        assertEquals(this.isInternal, isInternal);
    }
}
