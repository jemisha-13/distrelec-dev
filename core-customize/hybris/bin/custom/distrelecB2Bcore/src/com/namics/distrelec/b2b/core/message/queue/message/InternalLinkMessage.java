/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.message.queue.message;

import java.io.Serializable;

import com.namics.distrelec.b2b.core.message.queue.model.RowType;

/**
 * {@code InternalLinkMessage}
 * 
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public interface InternalLinkMessage extends Serializable {

    public String getCode();

    public void setCode(final String code);

    public RowType getType();

    public void setType(final RowType type);

    public String getSite();

    public void setSite(final String site);

    public String getLanguage();

    public void setLanguage(final String language);

    public boolean isForce();

    public void setForce(final boolean force);

    /**
     * A factory method which creates a new instance of #InternalLinkMessage
     * 
     * @return new instance of #InternalLinkMessage
     */
    public static InternalLinkMessage createInternalLinkMessage() {
        return new InternalLinkMessageImpl();
    }

    /**
     * A factory method which creates a new instance of #InternalLinkMessage and initialize it with the specifed attributes.
     * 
     * @param code
     * @param site
     * @param type
     * @param language
     * @param force
     * @return new instance of #InternalLinkMessage
     */
    public static InternalLinkMessage createInternalLinkMessage(final String code, final String site, final RowType type, final String language,
            final boolean force) {
        return new InternalLinkMessageImpl(code, site, type, language, force);
    }
}
