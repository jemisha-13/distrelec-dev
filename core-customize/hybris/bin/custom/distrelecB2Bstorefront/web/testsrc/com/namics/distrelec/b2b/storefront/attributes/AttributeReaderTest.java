/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.hybris.platform.servicelayer.session.SessionService;

public class AttributeReaderTest {
    private static final String TEST_BOOLEAN_ID = "testBoolean";
    private static final String TEST_INTEGER_ID = "testInteger";
    private static final String TEST_DOUBLE_ID = "testDouble";
    private static final String TEST_STRING_ID = "testString";
    private static final String NON_EXISTING_ID = "nonexisting";

    private static final Boolean TEST_BOOLEAN = Boolean.TRUE;
    private static final Integer TEST_INTEGER = Integer.valueOf(1);
    private static final Double TEST_DOUBLE = Double.valueOf(0.5D);
    private static final String TEST_STRING = "String";

    private static final Cookie BOOLEAN_COOKIE = new Cookie(TEST_BOOLEAN_ID, TEST_BOOLEAN.toString());
    private static final Cookie INTEGER_COOKIE = new Cookie(TEST_INTEGER_ID, TEST_INTEGER.toString());
    private static final Cookie DOUBLE_COOKIE = new Cookie(TEST_DOUBLE_ID, TEST_DOUBLE.toString());
    private static final Cookie STRING_COOKIE = new Cookie(TEST_STRING_ID, TEST_STRING);

    private static final Cookie[] TEST_COOKIES = new Cookie[] { BOOLEAN_COOKIE, INTEGER_COOKIE, DOUBLE_COOKIE, STRING_COOKIE };

    private static final Attribute<Boolean> BOOLEAN_ATTRIBUTE = Attributes.requiredAttribute(TEST_BOOLEAN_ID, Boolean.class);
    private static final Attribute<Integer> INTEGER_ATTRIBUTE = Attributes.requiredAttribute(TEST_INTEGER_ID, Integer.class);
    private static final Attribute<Double> DOUBLE_ATTRIBUTE = Attributes.requiredAttribute(TEST_DOUBLE_ID, Double.class);
    private static final Attribute<String> STRING_ATTRIBUTE = Attributes.requiredAttribute(TEST_STRING_ID, String.class);
    private static final Attribute<Boolean> OPT1_NONEXISTING_ATTRIBUTE = Attributes.optionalAttribute(NON_EXISTING_ID, TEST_BOOLEAN);
    private static final Attribute<Boolean> OPT2_NONEXISTING_ATTRIBUTE = Attributes.optionalAttribute(NON_EXISTING_ID, Boolean.class);
    private static final Attribute<Boolean> REQ_NONEXISTENT_ATTRIBUTE = Attributes.requiredAttribute(NON_EXISTING_ID, Boolean.class);

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private SessionService sessionService;

    private AttributeReader attributeReader;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(request.getSession()).thenReturn(session);

        attributeReader = AttributeReader.getInstance(request, sessionService);
    }

    @Test
    public void factoryMethodCreationRegistersAttributeReaderInRequest() throws Exception {
        assertNotNull(attributeReader);
        verify(request).setAttribute(AttributeReader.ATTRIBUTE_READER_INSTANCE, attributeReader);
    }

    @Test
    public void factoryMethodCreationReturnsAttributeReaderFromRequest() throws Exception {
        when(request.getAttribute(AttributeReader.ATTRIBUTE_READER_INSTANCE)).thenReturn(attributeReader);
        assertEquals(attributeReader, AttributeReader.getInstance(request, sessionService));
    }

    @Test
    public void attributeReaderReturnsRequestParameterValues() throws Exception {
        addTestValuesToRequestParameters();
        verifyTestValuesAreRetrieved();
    }

    @Test
    public void attributeReaderReturnsCookieValues() throws Exception {
        addTestValuesToCookies();
        verifyTestValuesAreRetrieved();
    }

    @Test
    public void attributeReaderReturnsRequestAttributeValues() throws Exception {
        addTestValuesToRequestAttributes();
        verifyTestValuesAreRetrieved();
    }

    @Test
    public void attributeReaderReturnsSessionAttributeValues() throws Exception {
        addTestValuesToSessionAttributes();
        verifyTestValuesAreRetrieved();
    }

    @Test
    public void attributeReaderReturnsSessionServiceAttributeValues() throws Exception {
        addTestValuesToSessionServiceAttributes();
        verifyTestValuesAreRetrieved();
    }

    @Test
    public void attributeReaderReturnsDefaultValueIfOptionalAttributeWithDefaultValueWasNotFound() throws Exception {
        final Boolean value = attributeReader.getValue(OPT1_NONEXISTING_ATTRIBUTE);
        assertEquals(TEST_BOOLEAN, value);
    }

    @Test
    public void attributeReaderReturnsNullIfOptionalAttributeWithoutDefaultValueWasNotFound() throws Exception {
        final Boolean value = attributeReader.getValue(OPT2_NONEXISTING_ATTRIBUTE);
        assertNull("There should be no default value on this instance!", value);
    }

    @Test(expected = AttributeReader.AttributeMissingException.class)
    public void attributeReaderThrowsExceptionIfRequiredAttributeWasNotFound() throws Exception {
        attributeReader.getValue(REQ_NONEXISTENT_ATTRIBUTE);
        fail("AttributeReader must throw exception for non-existing required attributes!");
    }

    @Test
    public void attributeReaderReturnsRequestParametersInFavorToCookies() throws Exception {
        addTestValuesToCookies();

        final Boolean requestParameter = Boolean.FALSE;
        when(request.getParameter(TEST_BOOLEAN_ID)).thenReturn(requestParameter.toString());

        final Boolean booleanValue = attributeReader.getValue(BOOLEAN_ATTRIBUTE);
        assertEquals(requestParameter, booleanValue);
    }

    @Test
    public void attributeReaderReturnsCookiesInFavorToRequestAttributes() throws Exception {
        addTestValuesToCookies();

        final Boolean requestAttribute = Boolean.FALSE;
        when(request.getAttribute(TEST_BOOLEAN_ID)).thenReturn(requestAttribute.toString());

        final Boolean booleanValue = attributeReader.getValue(BOOLEAN_ATTRIBUTE);
        assertEquals(TEST_BOOLEAN, booleanValue);
    }

    @Test
    public void attributeReaderReturnsSessionAttributesInFavorToRequestAttributes() throws Exception {
        final Boolean requestBoolean = Boolean.TRUE;
        final Boolean sessionBoolean = Boolean.FALSE;

        when(request.getAttribute(TEST_BOOLEAN_ID)).thenReturn(requestBoolean);
        when(session.getAttribute(TEST_BOOLEAN_ID)).thenReturn(sessionBoolean);

        final Boolean booleanValue = attributeReader.getValue(BOOLEAN_ATTRIBUTE);
        assertEquals(sessionBoolean, booleanValue);
    }

    @Test
    public void attributeReaderReturnsSessionAttributesInFavorToSessionServiceAttributes() throws Exception {
        final Boolean sessionBoolean = Boolean.TRUE;
        final Boolean sessionServiceBoolean = Boolean.FALSE;

        when(session.getAttribute(TEST_BOOLEAN_ID)).thenReturn(sessionBoolean);
        when(sessionService.getAttribute(TEST_BOOLEAN_ID)).thenReturn(sessionServiceBoolean);

        final Boolean booleanValue = attributeReader.getValue(BOOLEAN_ATTRIBUTE);
        assertEquals(sessionBoolean, booleanValue);
    }

    @Test
    public void attributeReaderHandlesNullSession() throws Exception {
        when(request.getSession()).thenReturn(null);
        attributeReader = AttributeReader.getInstance(request, sessionService);

        final Boolean booleanValue = attributeReader.getValue(OPT2_NONEXISTING_ATTRIBUTE);
        assertNull(booleanValue);
    }

    @Test
    public void attributeReaderHandlesNullSessionService() throws Exception {
        attributeReader = AttributeReader.getInstance(request, null);

        final Boolean booleanValue = attributeReader.getValue(OPT2_NONEXISTING_ATTRIBUTE);
        assertNull(booleanValue);
    }

    private void addTestValuesToRequestParameters() {
        when(request.getParameter(TEST_BOOLEAN_ID)).thenReturn(TEST_BOOLEAN.toString());
        when(request.getParameter(TEST_INTEGER_ID)).thenReturn(TEST_INTEGER.toString());
        when(request.getParameter(TEST_DOUBLE_ID)).thenReturn(TEST_DOUBLE.toString());
        when(request.getParameter(TEST_STRING_ID)).thenReturn(TEST_STRING);
    }

    private void addTestValuesToCookies() {
        when(request.getCookies()).thenReturn(TEST_COOKIES);
    }

    private void addTestValuesToRequestAttributes() {
        when(request.getAttribute(TEST_BOOLEAN_ID)).thenReturn(TEST_BOOLEAN);
        when(request.getAttribute(TEST_INTEGER_ID)).thenReturn(TEST_INTEGER);
        when(request.getAttribute(TEST_DOUBLE_ID)).thenReturn(TEST_DOUBLE);
        when(request.getAttribute(TEST_STRING_ID)).thenReturn(TEST_STRING);
    }

    private void addTestValuesToSessionAttributes() {
        when(session.getAttribute(TEST_BOOLEAN_ID)).thenReturn(TEST_BOOLEAN);
        when(session.getAttribute(TEST_INTEGER_ID)).thenReturn(TEST_INTEGER);
        when(session.getAttribute(TEST_DOUBLE_ID)).thenReturn(TEST_DOUBLE);
        when(session.getAttribute(TEST_STRING_ID)).thenReturn(TEST_STRING);
    }

    private void addTestValuesToSessionServiceAttributes() {
        when(sessionService.getAttribute(TEST_BOOLEAN_ID)).thenReturn(TEST_BOOLEAN);
        when(sessionService.getAttribute(TEST_INTEGER_ID)).thenReturn(TEST_INTEGER);
        when(sessionService.getAttribute(TEST_DOUBLE_ID)).thenReturn(TEST_DOUBLE);
        when(sessionService.getAttribute(TEST_STRING_ID)).thenReturn(TEST_STRING);
    }

    private void verifyTestValuesAreRetrieved() {
        final Boolean booleanValue = attributeReader.getValue(BOOLEAN_ATTRIBUTE);
        final Integer integerValue = attributeReader.getValue(INTEGER_ATTRIBUTE);
        final Double doubleValue = attributeReader.getValue(DOUBLE_ATTRIBUTE);
        final String stringValue = attributeReader.getValue(STRING_ATTRIBUTE);

        assertEquals(TEST_BOOLEAN, booleanValue);
        assertEquals(TEST_INTEGER, integerValue);
        assertEquals(TEST_DOUBLE, doubleValue);
        assertEquals(TEST_STRING, stringValue);
    }
}
