/*
 * Copyright 2013-2017 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * {@code CIValue}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonPropertyOrder({ "uid", "name", "url", "order" })
public class CIValue implements Serializable {

    @JsonProperty("uid")
    private String uid;
    @JsonProperty("name")
    private String name;
    @JsonProperty("url")
    private String url;
    @JsonProperty("order")
    private Integer order;
    @JsonProperty("count")
    private Long count;

    /**
     * Create a new instance of {@code CIValue}
     */
    public CIValue() {
        super();
    }

    /**
     * Create a new instance of {@code CIValue}
     * 
     * @param name
     * @param url
     */
    public CIValue(final String uid, final String name, final String url) {
        this(uid, name, url, null, null);
    }

    /**
     * Create a new instance of {@code CIValue}
     * 
     * @param name
     * @param url
     * @param order
     */
    public CIValue(final String uid, final String name, final String url, final Integer order) {
        this(uid, name, url, order, null);
    }

    /**
     * Create a new instance of {@code CIValue}
     * 
     * @param name
     * @param url
     * @param order
     * @param count
     */
    public CIValue(final String uid, final String name, final String url, final Integer order, final Long count) {
        this.uid = uid;
        this.name = name;
        this.url = url;
        this.order = order;
        this.count = count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof CIValue)) {
            return false;
        }
        final CIValue other = (CIValue) obj;

        if (this.uid != null && other.uid != null && this.uid.equals(other.uid)) {
            return true;
        }
        if (!Objects.equals(this.uid, other.uid)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hashcode = 89;
        hashcode = 47 * hashcode + Objects.hashCode(this.uid);
        hashcode = 47 * hashcode + Objects.hashCode(this.name);
        hashcode = 47 * hashcode + Objects.hashCode(this.url);

        return hashcode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return String.format("CIValue [getName()=%s, getUrl()=%s, getOrder()=%s]", getName(), getUrl(), getOrder());
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(final Integer order) {
        this.order = order;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}
