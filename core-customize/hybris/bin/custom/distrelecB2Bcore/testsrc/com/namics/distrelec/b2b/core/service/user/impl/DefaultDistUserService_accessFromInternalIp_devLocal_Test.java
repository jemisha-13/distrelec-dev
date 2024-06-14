package com.namics.distrelec.b2b.core.service.user.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;

@UnitTest
public class DefaultDistUserService_accessFromInternalIp_devLocal_Test extends AbstractDefaultDistUserServiceTest {

    @Mock
    HttpServletRequest request;

    @Before
    public void setUp() {
        doReturn(true).when(distUserService).isDevLocal();
    }

    @Test
    public void testAccessFromInternalIpDevLocal() {
        boolean isInternalIp = distUserService.accessFromInternalIp(request);

        assertTrue(isInternalIp);
    }
}
