/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.attributes;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.namics.distrelec.b2b.storefront.stubs.HttpServletResponseStub;

import de.hybris.platform.servicelayer.session.SessionService;

public class AttributeTest {
    private static final String TEST_BOOLEAN_ID = "testBoolean";
    private static final String TEST_INTEGER_ID = "testInteger";
    private static final String TEST_DOUBLE_ID = "testDouble";
    private static final String TEST_STRING_ID = "testString";
    private static final String[] TEST_IDS = new String[] { TEST_BOOLEAN_ID, TEST_INTEGER_ID, TEST_DOUBLE_ID, TEST_STRING_ID };

    private static final Boolean TEST_BOOLEAN = Boolean.TRUE;
    private static final Integer TEST_INTEGER = Integer.valueOf(1);
    private static final Double TEST_DOUBLE = Double.valueOf(0.5D);
    private static final String TEST_STRING = "String";
    private static final String[] TEST_VALUES = new String[] { TEST_BOOLEAN.toString(), TEST_INTEGER.toString(), TEST_DOUBLE.toString(), TEST_STRING };

    private static final Cookie BOOLEAN_COOKIE = new Cookie(TEST_BOOLEAN_ID, TEST_BOOLEAN.toString());
    private static final Cookie INTEGER_COOKIE = new Cookie(TEST_INTEGER_ID, TEST_INTEGER.toString());
    private static final Cookie DOUBLE_COOKIE = new Cookie(TEST_DOUBLE_ID, TEST_DOUBLE.toString());
    private static final Cookie STRING_COOKIE = new Cookie(TEST_STRING_ID, TEST_STRING);
    private static final Cookie[] TEST_COOKIES = new Cookie[] { BOOLEAN_COOKIE, INTEGER_COOKIE, DOUBLE_COOKIE, STRING_COOKIE };

    private static final Attribute<Boolean> BOOLEAN_ATTRIBUTE = Attributes.requiredAttribute(TEST_BOOLEAN_ID, Boolean.class);
    private static final Attribute<Integer> INTEGER_ATTRIBUTE = Attributes.requiredAttribute(TEST_INTEGER_ID, Integer.class);
    private static final Attribute<Double> DOUBLE_ATTRIBUTE = Attributes.requiredAttribute(TEST_DOUBLE_ID, Double.class);
    private static final Attribute<String> STRING_ATTRIBUTE = Attributes.requiredAttribute(TEST_STRING_ID, String.class);

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private SessionService sessionService;

    private HttpServletResponseStub response;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(request.getSession()).thenReturn(session);
        response = new HttpServletResponseStub();
    }

    @Test
    public void testAttributesReturnCorrectName() throws Exception {
        assertEquals(TEST_BOOLEAN_ID, BOOLEAN_ATTRIBUTE.getName());
        assertEquals(TEST_INTEGER_ID, INTEGER_ATTRIBUTE.getName());
        assertEquals(TEST_DOUBLE_ID, DOUBLE_ATTRIBUTE.getName());
        assertEquals(TEST_STRING_ID, STRING_ATTRIBUTE.getName());
    }

    @Test
    public void testAttributesReturnCorrectType() throws Exception {
        assertEquals(TEST_BOOLEAN.getClass(), BOOLEAN_ATTRIBUTE.getType());
        assertEquals(TEST_INTEGER.getClass(), INTEGER_ATTRIBUTE.getType());
        assertEquals(TEST_DOUBLE.getClass(), DOUBLE_ATTRIBUTE.getType());
        assertEquals(TEST_STRING.getClass(), STRING_ATTRIBUTE.getType());
    }

    @Test
    public void testRequestParametersAreRetrieved() throws Exception {
        when(request.getParameter(TEST_BOOLEAN_ID)).thenReturn(TEST_BOOLEAN.toString());
        when(request.getParameter(TEST_INTEGER_ID)).thenReturn(TEST_INTEGER.toString());
        when(request.getParameter(TEST_DOUBLE_ID)).thenReturn(TEST_DOUBLE.toString());
        when(request.getParameter(TEST_STRING_ID)).thenReturn(TEST_STRING);

        assertEquals(TEST_BOOLEAN, BOOLEAN_ATTRIBUTE.getValueFromParameter(request));
        assertEquals(TEST_INTEGER, INTEGER_ATTRIBUTE.getValueFromParameter(request));
        assertEquals(TEST_DOUBLE, DOUBLE_ATTRIBUTE.getValueFromParameter(request));
        assertEquals(TEST_STRING, STRING_ATTRIBUTE.getValueFromParameter(request));
    }

    @Test
    public void testCookieAttributesAreRetrieved() throws Exception {
        when(request.getCookies()).thenReturn(TEST_COOKIES);

        assertEquals(TEST_BOOLEAN, BOOLEAN_ATTRIBUTE.getValueFromCookies(request));
        assertEquals(TEST_INTEGER, INTEGER_ATTRIBUTE.getValueFromCookies(request));
        assertEquals(TEST_DOUBLE, DOUBLE_ATTRIBUTE.getValueFromCookies(request));
        assertEquals(TEST_STRING, STRING_ATTRIBUTE.getValueFromCookies(request));
    }

    @Test
    public void testCookieAttributesAreSet() throws Exception {
        BOOLEAN_ATTRIBUTE.setValue(request, response, TEST_BOOLEAN);
        INTEGER_ATTRIBUTE.setValue(request, response, TEST_INTEGER);
        DOUBLE_ATTRIBUTE.setValue(request, response, TEST_DOUBLE);
        STRING_ATTRIBUTE.setValue(request, response, TEST_STRING);

        assertCookiesAreSet();
    }

    @Test
    public void givenTheResponseIsAlreadyCommitted_then_ignoreNewCookies() {
        response.setCommitted(true);
        BOOLEAN_ATTRIBUTE.setValue(request, response, TEST_BOOLEAN);
        assertThat(response.getCookies(), is(Collections.<Cookie> emptyList()));
    }

    @Test
    public void testCookieAttributesAreRemoved() throws Exception {
        BOOLEAN_ATTRIBUTE.removeValue(response);
        INTEGER_ATTRIBUTE.removeValue(response);
        DOUBLE_ATTRIBUTE.removeValue(response);
        STRING_ATTRIBUTE.removeValue(response);

        assertRemoveCookiesAreSet();
    }

    @Test
    public void givenTheResponseIsAlreadyCommitted_then_ignoreCookieRemovals() {
        response.setCommitted(true);
        BOOLEAN_ATTRIBUTE.removeValue(response);
        assertThat(response.getCookies(), is(Collections.<Cookie> emptyList()));
    }

    @Test
    public void testUnknownCookieAttributeRetrievesNull() throws Exception {
        when(request.getCookies()).thenReturn(new Cookie[0]);

        assertNull(BOOLEAN_ATTRIBUTE.getValueFromCookies(request));
    }

    @Test
    public void testWithCookiesSetAttributeRetrievesNull() throws Exception {
        when(request.getCookies()).thenReturn(null);
        assertNull(BOOLEAN_ATTRIBUTE.getValueFromCookies(request));
    }

    @Test
    public void testRequestAttributesAreRetrieved() throws Exception {
        when(request.getAttribute(TEST_BOOLEAN_ID)).thenReturn(TEST_BOOLEAN);
        when(request.getAttribute(TEST_INTEGER_ID)).thenReturn(TEST_INTEGER);
        when(request.getAttribute(TEST_DOUBLE_ID)).thenReturn(TEST_DOUBLE);
        when(request.getAttribute(TEST_STRING_ID)).thenReturn(TEST_STRING);

        assertEquals(TEST_BOOLEAN, BOOLEAN_ATTRIBUTE.getValue(request));
        assertEquals(TEST_INTEGER, INTEGER_ATTRIBUTE.getValue(request));
        assertEquals(TEST_DOUBLE, DOUBLE_ATTRIBUTE.getValue(request));
        assertEquals(TEST_STRING, STRING_ATTRIBUTE.getValue(request));
    }

    @Test
    public void testRequestAttributesAreSet() throws Exception {
        BOOLEAN_ATTRIBUTE.setValue(request, TEST_BOOLEAN);
        INTEGER_ATTRIBUTE.setValue(request, TEST_INTEGER);
        DOUBLE_ATTRIBUTE.setValue(request, TEST_DOUBLE);
        STRING_ATTRIBUTE.setValue(request, TEST_STRING);

        verify(request).setAttribute(TEST_BOOLEAN_ID, TEST_BOOLEAN);
        verify(request).setAttribute(TEST_INTEGER_ID, TEST_INTEGER);
        verify(request).setAttribute(TEST_DOUBLE_ID, TEST_DOUBLE);
        verify(request).setAttribute(TEST_STRING_ID, TEST_STRING);
    }

    @Test
    public void testRequestAttributesAreRemoved() throws Exception {
        BOOLEAN_ATTRIBUTE.removeValue(request);
        INTEGER_ATTRIBUTE.removeValue(request);
        DOUBLE_ATTRIBUTE.removeValue(request);
        STRING_ATTRIBUTE.removeValue(request);

        verify(request).removeAttribute(TEST_BOOLEAN_ID);
        verify(request).removeAttribute(TEST_INTEGER_ID);
        verify(request).removeAttribute(TEST_DOUBLE_ID);
        verify(request).removeAttribute(TEST_STRING_ID);
    }

    @Test
    public void testUnknownRequestAttributeRetrievesNull() throws Exception {
        assertNull(BOOLEAN_ATTRIBUTE.getValue(request));
    }

    @Test
    public void testSessionAttributesAreRetrieved() throws Exception {
        when(session.getAttribute(TEST_BOOLEAN_ID)).thenReturn(TEST_BOOLEAN);
        when(session.getAttribute(TEST_INTEGER_ID)).thenReturn(TEST_INTEGER);
        when(session.getAttribute(TEST_DOUBLE_ID)).thenReturn(TEST_DOUBLE);
        when(session.getAttribute(TEST_STRING_ID)).thenReturn(TEST_STRING);

        assertEquals(TEST_BOOLEAN, BOOLEAN_ATTRIBUTE.getValue(session));
        assertEquals(TEST_INTEGER, INTEGER_ATTRIBUTE.getValue(session));
        assertEquals(TEST_DOUBLE, DOUBLE_ATTRIBUTE.getValue(session));
        assertEquals(TEST_STRING, STRING_ATTRIBUTE.getValue(session));
    }

    @Test
    public void testSessionAttributesAreSet() throws Exception {
        BOOLEAN_ATTRIBUTE.setValue(session, TEST_BOOLEAN);
        INTEGER_ATTRIBUTE.setValue(session, TEST_INTEGER);
        DOUBLE_ATTRIBUTE.setValue(session, TEST_DOUBLE);
        STRING_ATTRIBUTE.setValue(session, TEST_STRING);

        verify(session).setAttribute(TEST_BOOLEAN_ID, TEST_BOOLEAN);
        verify(session).setAttribute(TEST_INTEGER_ID, TEST_INTEGER);
        verify(session).setAttribute(TEST_DOUBLE_ID, TEST_DOUBLE);
        verify(session).setAttribute(TEST_STRING_ID, TEST_STRING);
    }

    @Test
    public void testSessionAttributesAreRemoved() throws Exception {
        BOOLEAN_ATTRIBUTE.removeValue(session);
        INTEGER_ATTRIBUTE.removeValue(session);
        DOUBLE_ATTRIBUTE.removeValue(session);
        STRING_ATTRIBUTE.removeValue(session);

        verify(session).removeAttribute(TEST_BOOLEAN_ID);
        verify(session).removeAttribute(TEST_INTEGER_ID);
        verify(session).removeAttribute(TEST_DOUBLE_ID);
        verify(session).removeAttribute(TEST_STRING_ID);
    }

    @Test
    public void testUnknownSessionAttributeRetrievesNull() throws Exception {
        assertNull(BOOLEAN_ATTRIBUTE.getValue(session));
    }

    @Test
    public void testSessionServiceAttributesAreRetrieved() throws Exception {
        when(sessionService.getAttribute(TEST_BOOLEAN_ID)).thenReturn(TEST_BOOLEAN);
        when(sessionService.getAttribute(TEST_INTEGER_ID)).thenReturn(TEST_INTEGER);
        when(sessionService.getAttribute(TEST_DOUBLE_ID)).thenReturn(TEST_DOUBLE);
        when(sessionService.getAttribute(TEST_STRING_ID)).thenReturn(TEST_STRING);

        assertEquals(TEST_BOOLEAN, BOOLEAN_ATTRIBUTE.getValue(sessionService));
        assertEquals(TEST_INTEGER, INTEGER_ATTRIBUTE.getValue(sessionService));
        assertEquals(TEST_DOUBLE, DOUBLE_ATTRIBUTE.getValue(sessionService));
        assertEquals(TEST_STRING, STRING_ATTRIBUTE.getValue(sessionService));
    }

    @Test
    public void testSessionServiceAttributesAreSet() throws Exception {
        BOOLEAN_ATTRIBUTE.setValue(sessionService, TEST_BOOLEAN);
        INTEGER_ATTRIBUTE.setValue(sessionService, TEST_INTEGER);
        DOUBLE_ATTRIBUTE.setValue(sessionService, TEST_DOUBLE);
        STRING_ATTRIBUTE.setValue(sessionService, TEST_STRING);

        verify(sessionService).setAttribute(TEST_BOOLEAN_ID, TEST_BOOLEAN);
        verify(sessionService).setAttribute(TEST_INTEGER_ID, TEST_INTEGER);
        verify(sessionService).setAttribute(TEST_DOUBLE_ID, TEST_DOUBLE);
        verify(sessionService).setAttribute(TEST_STRING_ID, TEST_STRING);
    }

    @Test
    public void testSessionServiceAttributesAreRemoved() throws Exception {
        BOOLEAN_ATTRIBUTE.removeValue(sessionService);
        INTEGER_ATTRIBUTE.removeValue(sessionService);
        DOUBLE_ATTRIBUTE.removeValue(sessionService);
        STRING_ATTRIBUTE.removeValue(sessionService);

        verify(sessionService).removeAttribute(TEST_BOOLEAN_ID);
        verify(sessionService).removeAttribute(TEST_INTEGER_ID);
        verify(sessionService).removeAttribute(TEST_DOUBLE_ID);
        verify(sessionService).removeAttribute(TEST_STRING_ID);
    }

    private void assertCookiesAreSet() {
        final List<Cookie> cookies = response.getCookies();
        assertEquals(TEST_IDS.length, cookies.size());
        for (int i = 0; i < cookies.size(); i++) {
            final Cookie cookie = cookies.get(i);
            assertEquals(TEST_IDS[i], cookie.getName());
            assertEquals(TEST_VALUES[i], cookie.getValue());
        }
    }

    private void assertRemoveCookiesAreSet() {
        final List<Cookie> cookies = response.getCookies();
        assertEquals(TEST_IDS.length, cookies.size());
        for (int i = 0; i < cookies.size(); i++) {
            final Cookie cookie = cookies.get(i);
            assertEquals(TEST_IDS[i], cookie.getName());
            assertEquals(0, cookie.getMaxAge());
        }
    }
}
