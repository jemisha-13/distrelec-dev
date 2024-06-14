/*
 * Copyright 2000-2017 Distrelec AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.message.queue;

import com.namics.distrelec.b2b.core.message.queue.model.RowType;

/**
 * {@code DistSimpleMessageFacade}
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public interface DistSimpleMessageFacade {

    /**
     * Send message to the JMS queue to inform about the missing mesh-linking data of the object mode, given by its code and
     * type.
     *
     * @param code
     *            the object mode code.
     * @param type
     *            the object model type.
     */
    public void sendMessage(final String code, final RowType type);
}
