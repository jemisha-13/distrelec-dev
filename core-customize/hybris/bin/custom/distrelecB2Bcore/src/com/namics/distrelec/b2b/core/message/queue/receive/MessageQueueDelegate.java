package com.namics.distrelec.b2b.core.message.queue.receive;

import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;

/**
 * {@code MessageQueueDelegate}
 * <p>
 * A delegate service to process JMS messages.
 * </p>
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @since Distrelec 6.1
 */
public interface MessageQueueDelegate {

    /**
     * Process the received JMS message.
     * 
     * @param message
     *            the JMS message to process.
     */
    void handleILMessage(final InternalLinkMessage message);
}
