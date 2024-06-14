/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.attributes;

import de.hybris.platform.servicelayer.session.SessionService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * According to the JSP EL, the {@link AttributeReader} resolves attributes from the request or session object and returns their values. The
 * resolve order is: request parameters, cookies, session attributes, session service attributes, request attributes.
 * 
 * If no attribute value can be found an {@link AttributeMissingException} is thrown. In case of an {@link OptionalAttribute} the default
 * value is returned instead.
 * 
 * Clients can also get the specific values independently or set the values by using the {@link Attribute} itself.
 * 
 * @author sbechtold, Namics Deutschland GmbH
 * @since Namics Extensions 1.0
 */
public class AttributeReader {
    static final String ATTRIBUTE_READER_INSTANCE = AttributeReader.class.getCanonicalName();

    public static AttributeReader getInstance(final HttpServletRequest request, final SessionService sessionService) {
        AttributeReader attributeReader = (AttributeReader) request.getAttribute(ATTRIBUTE_READER_INSTANCE);

        if (attributeReader == null) {
            attributeReader = new AttributeReader(request, sessionService);
            request.setAttribute(ATTRIBUTE_READER_INSTANCE, attributeReader);
        }

        return attributeReader;
    }

    final HttpServletRequest request;
    final HttpSession session;
    final SessionService sessionService;

    private AttributeReader(final HttpServletRequest request, final SessionService sessionService) {
        this.request = request;
        this.session = request.getSession();
        this.sessionService = sessionService;
    }

    public <T extends Object> T getValue(final Attribute<T> attribute) {
        final T requestParameterValue = attribute.getValueFromParameter(request);
        if (requestParameterValue != null) {
            return requestParameterValue;
        }

        final T cookieValue = attribute.getValueFromCookies(request);
        if (cookieValue != null) {
            return cookieValue;
        }

        if (session != null) {
            final T sessionAttributeValue = attribute.getValue(session);
            if (sessionAttributeValue != null) {
                return sessionAttributeValue;
            }
        }

        if (sessionService != null) {
            final T sessionServiceAttributeValue = attribute.getValue(sessionService);
            if (sessionServiceAttributeValue != null) {
                return sessionServiceAttributeValue;
            }
        }

        final T requestAttributeValue = attribute.getValue(request);
        if (requestAttributeValue != null) {
            return requestAttributeValue;
        }

        if (attribute instanceof OptionalAttribute<?>) {
            return ((OptionalAttribute<T>) attribute).getDefaultValue();
        }

        throw new AttributeMissingException();
    }

    public final static class AttributeMissingException extends RuntimeException {
        // Exception for missing required attributes
    }
}
