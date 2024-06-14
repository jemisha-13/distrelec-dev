/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.mail.internal;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * {@code DistInternalMailContext}
 * 
 * @param <T>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.10
 */
public class DistInternalMailContext<T> implements Serializable {

    private String emailSubject;
    private List<T> data;
    private Map<String, Object> attributes = new Hashtable<>();

    /**
     * Create a new instance of {@code DistInternalMailContext}
     */
    public DistInternalMailContext() {
        super();
    }

    /**
     * Create new instance of {@code DistInternalMailContext}
     * 
     * @param data
     */
    public DistInternalMailContext(final List<T> data) {
        this.data = data;
    }

    public boolean isEmpty() {
        return data == null || data.isEmpty();
    }

    /**
     * Add the attribute with the specific name to the additional attributes.
     * 
     * @param name
     *            the attribute name
     * @param attr
     *            the attribute value
     * @return the old value associated with the specified name if any.
     */
    public Object setAttribute(final String name, final Object attr) {
        return this.attributes.put(name, attr);
    }

    /**
     * @return {@code true} if the context has at least one additional attribute, {@code false} otherwise.
     */
    public boolean hasAttributes() {
        return !this.attributes.isEmpty();
    }

    /**
     * Check whether an attribute value is associated with the specified name.
     * 
     * @param name
     *            the attribute name.
     * @return {@code true} if an attribute value is associated with the specified name, {@code false} otherwise.
     */
    public boolean hasAttribute(final String name) {
        return this.attributes.containsKey(name);
    }

    // Getters & Setters

    public List<T> getData() {
        return data;
    }

    public void setData(final List<T> data) {
        this.data = data;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(final String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(final Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
