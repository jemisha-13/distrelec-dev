/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.session.impl;

import com.namics.distrelec.b2b.core.jalo.session.DistJaloSession;
import com.namics.distrelec.b2b.core.service.session.DistSession;

import de.hybris.platform.servicelayer.session.impl.DefaultSession;

/**
 * {@code DefaultDistSession}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.7
 */
public class DefaultDistSession extends DefaultSession implements DistSession {

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.session.DistSession#removeAttribute(java.lang.String, boolean)
     */
    @Override
    public void removeAttribute(final String name, final boolean force) {
        ((DistJaloSession) getJaloSession()).removeAttribute(name, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.session.DistSession#removeBackupCart()
     */
    @Override
    public void removeBackupCart() {
        ((DistJaloSession) getJaloSession()).removeBackupCart();
    }
}
