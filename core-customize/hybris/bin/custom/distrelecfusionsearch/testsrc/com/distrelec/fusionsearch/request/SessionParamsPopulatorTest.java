package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.SESSION_PARAM;
import static com.namics.hybris.ffsearch.converter.search.SearchServiceConverter.FF_TRACKING_COOKIE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.web.context.request.ServletRequestAttributes;

import de.hybris.bootstrap.annotations.UnitTest;

@UnitTest
public class SessionParamsPopulatorTest extends AbstractParamsPopulatorTest {

    @InjectMocks
    @Spy
    SessionParamsPopulator sessionParamsPopulator;

    @Mock
    HttpServletRequest request;

    @Mock
    ServletRequestAttributes requestAttrs;

    @Before
    public void setUp() {
        doReturn(requestAttrs).when(sessionParamsPopulator).currentRequestAttributes();
        when(requestAttrs.getRequest()).thenReturn(request);
    }

    @Test
    public void testPopulateSessionAsFFTrackingCookieIsDefined() {
        String ffId = "ffId";

        Cookie cookie = mock(Cookie.class);
        when(cookie.getName()).thenReturn(FF_TRACKING_COOKIE);
        when(cookie.getValue()).thenReturn(ffId);
        when(request.getCookies()).thenReturn(new Cookie[] { cookie });

        sessionParamsPopulator.populate(searchRequestTuple, params);

        Collection<String> values = params.get(SESSION_PARAM);

        assertEquals(1, values.size());
        assertEquals(ffId, values.iterator().next());
    }

    @Test
    public void testNotPopulateSessionAsFFTrackingCookieIsNotDefined() {
        // when(request.getCookies()).thenReturn(new Cookie[]{});
        sessionParamsPopulator.populate(searchRequestTuple, params);
        assertFalse(params.containsKey(SESSION_PARAM));
    }
}
