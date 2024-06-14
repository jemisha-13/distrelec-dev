package com.namics.distrelec.b2b.core.event;

import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;
import com.namics.distrelec.b2b.core.message.queue.receive.MessageQueueDelegate;
import de.hybris.platform.servicelayer.event.impl.AbstractEventListener;
import org.springframework.beans.factory.annotation.Autowired;

public class DistInternalLinkEventListener extends AbstractEventListener<DistInternalLinkEvent> {

    @Autowired
    private MessageQueueDelegate messageQueueDelegate;

    @Override
    protected void onEvent(final DistInternalLinkEvent event) {
        final InternalLinkMessage iLMessage = InternalLinkMessage.createInternalLinkMessage(event.getCode(), event.getSite(), event.getType(),
          event.getLanguage(), event.isForce());
        getMessageQueueDelegate().handleILMessage(iLMessage);
    }

    public MessageQueueDelegate getMessageQueueDelegate() {
        return messageQueueDelegate;
    }

    public void setMessageQueueDelegate(final MessageQueueDelegate messageQueueDelegate) {
        this.messageQueueDelegate = messageQueueDelegate;
    }
}
