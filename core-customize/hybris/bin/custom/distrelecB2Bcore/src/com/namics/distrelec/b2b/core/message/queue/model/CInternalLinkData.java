/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.message.queue.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;

/**
 * {@code CInternalLinkData}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay JAdhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
@JsonPropertyOrder({ "code", "site", "type", "timestamp", "datas" })
public class CInternalLinkData implements InternalLinkMessage {

    // Field code for the data is queried
    @JsonProperty("code")
    private String code;
    @JsonProperty("site")
    private String site;
    @JsonProperty("type")
    private RowType type;
    @JsonIgnore
    private String language;
    @JsonProperty("datas")
    private List<CRelatedData> datas;
    @JsonProperty("timestamp")
    private Date timestamp;
    @JsonIgnore
    private boolean force;

    /**
     * Create a new instance of {@code CInternalLinkData}
     */
    public CInternalLinkData() {
        // NOOP
    }

    /**
     * Create a new instance of {@code CInternalLinkData}
     * 
     * @param code
     * @param site
     * @param type
     */
    public CInternalLinkData(final String code, final String site, final RowType type) {
        this(code, site, type, null, null);
    }

    /**
     * Create a new instance of {@code CInternalLinkData}
     * 
     * @param code
     * @param site
     * @param type
     * @param force
     */
    public CInternalLinkData(final String code, final String site, final RowType type, final boolean force) {
        this(code, site, type, null, null);
        this.force = force;
    }

    /**
     * Create a new instance of {@code CInternalLinkData}
     * 
     * @param code
     * @param site
     * @param type
     * @param language
     * @param datas
     */
    public CInternalLinkData(final String code, final String site, final RowType type, final String language,
            final List<CRelatedData> datas) {
        this.code = code;
        this.site = site;
        this.type = type;
        this.language = language;
        this.datas = datas;
    }

    // Getters & Setters

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    @Override
    public RowType getType() {
        return type;
    }

    @Override
    public void setType(final RowType type) {
        this.type = type;
    }

    @Override
    public String getSite() {
        return site;
    }

    @Override
    public void setSite(final String site) {
        this.site = site;
    }

    @Override
    public String getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(final String language) {
        this.language = language;
    }

    public List<CRelatedData> getDatas() {
        return datas;
    }

    public void setDatas(final List<CRelatedData> datas) {
        this.datas = datas;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#isForce()
     */
    @Override
    public boolean isForce() {
        return force;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#setForce(boolean)
     */
    @Override
    public void setForce(final boolean force) {
        this.force = force;
    }

    @Override
    public String toString() {
        return String.format("CInternalLinkData [code=%s, site=%s, type=%s, language=%s, datas=%s, timestamp=%s, force=%s]", code, site, type, language, datas,
                timestamp, force);
    }

}
