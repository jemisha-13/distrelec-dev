/*
 * Copyright 2013-2018 Distrelec Groups AG. All rights reserved.
 */
package com.namics.distrelec.b2b.core.message.queue.handlers;

import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;

import de.hybris.platform.core.model.ItemModel;

/**
 * {@code ILMessageHandler}
 * <p>
 * {@link InternalLinkMessage} handler interface.
 * </p>
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 7.1
 */
public interface ILMessageHandler<T extends ItemModel> {

    /**
     * Handle the message.
     * 
     * @param message
     */
    public void handle(final InternalLinkMessage message);

}
