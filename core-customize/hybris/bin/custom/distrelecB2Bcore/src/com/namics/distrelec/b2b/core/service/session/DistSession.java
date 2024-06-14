/*
 * Copyright 2013-2016 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.service.session;

import de.hybris.platform.servicelayer.session.Session;

/**
 * {@code DistSession}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 5.7
 */
public interface DistSession extends Session {

    /**
     * Removes the given attribute from the current session. Do nothing if the attribute doesn't exists in the session. This method is
     * mainly designed for the session cart, i.e., if the provided name is not equals to "cart", the behavior will be exactly the same as
     * {@code removeAttribute(name)}. This can be also extended to other types of models to avoid their deletion from the database.
     * 
     * @param name
     *            the attribute name
     * @param force
     *            if {@code true} then it behaves exactly like {@code removeAttribute(name)}
     */
    void removeAttribute(final String name, final boolean force);

    /**
     * delete any backup cart from the database.
     */
    public void removeBackupCart();
}
