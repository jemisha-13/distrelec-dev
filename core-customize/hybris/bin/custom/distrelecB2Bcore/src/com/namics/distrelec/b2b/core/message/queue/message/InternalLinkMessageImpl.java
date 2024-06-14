/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.message;

import com.namics.distrelec.b2b.core.message.queue.model.RowType;

/**
 * {@code InternalLinkMessageImpl}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.5
 */
public class InternalLinkMessageImpl implements InternalLinkMessage {

    private String code;
    private String site;
    private RowType type;
    private String language;
    private boolean force;

    /**
     * Create a new instance of {@code InternalLinkMessageImpl}
     */
    public InternalLinkMessageImpl() {
        super();
    }

    /**
     * Create a new instance of {@code InternalLinkMessageImpl}
     * 
     * @param code
     * @param site
     * @param type
     * @param language
     * @param force
     */
    public InternalLinkMessageImpl(final String code, final String site, final RowType type, final String language, final boolean force) {
        this.code = code;
        this.site = site;
        this.type = type;
        this.language = language;
        this.force = force;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#getCode()
     */
    @Override
    public String getCode() {
        return code;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#setCode(java.lang.String)
     */
    @Override
    public void setCode(final String code) {
        this.code = code;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#getSite()
     */
    @Override
    public String getSite() {
        return site;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#setSite(java.lang.String)
     */
    @Override
    public void setSite(final String site) {
        this.site = site;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#getType()
     */
    @Override
    public RowType getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#setType(com.namics.distrelec.b2b.core.message.queue.model.
     * RowType)
     */
    @Override
    public void setType(final RowType type) {
        this.type = type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#getLanguage()
     */
    @Override
    public String getLanguage() {
        return language;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage#setLanguage(java.lang.String)
     */
    @Override
    public void setLanguage(final String language) {
        this.language = language;
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
}
