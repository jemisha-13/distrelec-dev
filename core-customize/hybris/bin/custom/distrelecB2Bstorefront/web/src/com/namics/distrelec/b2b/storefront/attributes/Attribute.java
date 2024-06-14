/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.attributes;

import de.hybris.platform.servicelayer.session.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * The {@link Attribute} class represents an instance for retrieving and setting attribute values from different sources, e.g. the
 * {@link HttpServletRequest}, {@link HttpSession}, and {@link SessionService}. It is also possible to extract values from the request
 * parameters and cookies.
 *
 * Note: In order to be casted correctly, the type of the attribute must implement a static valueOf(String) method that returns an instance
 * of that type. If not a {@link ClassCastException} will be thrown when requesting the value of the request parameters or cookies.
 *
 * @author sbechtold, Namics Deutschland GmbH
 * @since Namics Extensions 1.0
 *
 * @param <T>
 *            the type of object the attribute is resolved and cast to
 */
public abstract class Attribute<T extends Object> {

    private static final String WARN_MESSAGE_COOKIES_ARE_IGNORED = "The servlet response is already committed. Changes to cookies are ignored! Affected cookie key: {}";

    private static final Logger LOG = LoggerFactory.getLogger(Attribute.class);

    public static final String PATH_DEFAULT_VALUE = "/";

    public static final int MAX_AGE_DEFAULT_VALUE = 3600 * 24 * 7;

    private final String name;
    private final Class<T> type;
    private int maxAge;
    private String path;

    Attribute(final String name, final Class<T> type) {
        this.name = name;
        this.type = type;
        this.maxAge = MAX_AGE_DEFAULT_VALUE;
        this.path = PATH_DEFAULT_VALUE;
    }

    public String getName() {
        return name;
    }

    public Class<T> getType() {
        return type;
    }

    public final T getValueFromParameter(final HttpServletRequest request) {
        final String value = request.getParameter(name);
        return castStringValue(value);
    }

    public final T getValueFromCookies(final HttpServletRequest request) {
        final String value = getCookieValue(request.getCookies());
        return castStringValue(value);
    }

    public final void setValue(final HttpServletRequest request, final HttpServletResponse response, final T value) {
        // Add cookies only if it doesn't exists on client side
        // if it exists and value is same as previous we don't rewrite cookies
        if (needToaddCookie(request, value.toString())) {
            setValue(response, value);
        }
    }

    private boolean needToaddCookie(final HttpServletRequest request, final String cookiesValue) {
        if (request.getCookies() != null) {
            for (final Cookie cookie : request.getCookies()) {
                if (cookie.getName().equalsIgnoreCase(name)) {
                    return !cookie.getValue().equalsIgnoreCase(cookiesValue);
                }
            }
        }
        return true;
    }

    private final void setValue(final HttpServletResponse response, final T value) {
        if (response.isCommitted()) {
            LOG.warn(WARN_MESSAGE_COOKIES_ARE_IGNORED, this.getName());
            return;
        }
        final Cookie cookie = new Cookie(name, value.toString());
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setSecure(true);
        response.addCookie(cookie);
    }

    public final void removeValue(final HttpServletResponse response) {
        if (response.isCommitted()) {
            LOG.warn(WARN_MESSAGE_COOKIES_ARE_IGNORED, this.getName());
            return;
        }
        final Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath(path);
        response.addCookie(cookie);
    }

    public final T getValue(final HttpServletRequest request) {
        final Object value = request.getAttribute(name);
        if (value == null) {
            return null;
        }

        return (T) value;
    }

    public final void setValue(final HttpServletRequest request, final T value) {
        request.setAttribute(name, value);
    }

    public final void removeValue(final HttpServletRequest request) {
        request.removeAttribute(name);
    }

    public final T getValue(final HttpSession session) {
        final Object value = session.getAttribute(name);
        if (value == null) {
            return null;
        }

        return (T) value;
    }

    public final void setValue(final HttpSession session, final T value) {
        session.setAttribute(name, value);
    }

    public final void removeValue(final HttpSession session) {
        session.removeAttribute(name);
    }

    public final T getValue(final SessionService sessionService) {
        return sessionService.getAttribute(name);
    }

    public final void setValue(final SessionService sessionService, final T value) {
        sessionService.setAttribute(name, value);
    }

    public final void removeValue(final SessionService sessionService) {
        sessionService.removeAttribute(name);
    }

    private T castStringValue(final String value) {
        if (value == null) {
            return null;
        }

        if (String.class.equals(type)) {
            return (T) value;
        }

        try {
            final Method method = type.getMethod("valueOf", String.class);
            return (T) method.invoke(type, value);
        } catch (final Exception e) {
            final String msg = "Value %s for attribte %s could not be casted (%s)! Please verify that the class provides a valueOf(String) method!";
            throw new ClassCastException(String.format(msg, value, this, e.getMessage()));
        }
    }

    private String getCookieValue(final Cookie[] cookies) {
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    public Attribute setMaxAge(final int maxAge) {
        this.maxAge = maxAge;
        return this;
    }

    public Attribute setPath(final String path) {
        this.path = path;
        return this;
    }

    @Override
    public String toString() {
        return "Attribute [name=" + name + ", type=" + type.getName() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Attribute other = (Attribute) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }
}
