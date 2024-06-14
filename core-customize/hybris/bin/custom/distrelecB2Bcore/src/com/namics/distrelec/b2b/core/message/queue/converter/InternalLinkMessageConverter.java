package com.namics.distrelec.b2b.core.message.queue.converter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage;

/**
 * {@code InternalLinkMessageConverter}
 * <p>
 * Converter for {@link com.namics.distrelec.b2b.core.message.queue.message.InternalLinkMessage} messages.
 * </p>
 *
 * @author <a href="abhinay.jadhav@distrelec.com">Abhinay Jadhav</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.1
 */
public class InternalLinkMessageConverter implements MessageConverter {

    private static final Logger LOG = LogManager.getLogger(InternalLinkMessageConverter.class.getName());
    private static final String DUPLICATE_DETECTION_ID = org.apache.activemq.artemis.api.core.Message.HDR_DUPLICATE_DETECTION_ID.toString();

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.jms.support.converter.MessageConverter#fromMessage(javax.jms.Message)
     */
    @Override
    public Object fromMessage(final Message message) throws JMSException, MessageConversionException {
        if (message == null) {
            throw new IllegalArgumentException("Passed message is null");
        }
        if (!(message instanceof ObjectMessage)) {
            throw new IllegalArgumentException("Passed message is of wrong type (" + message.getClass() + ")");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("fromMessage: " + message);
        }
        /*
         * final TextMessage textMessage = (TextMessage) message; final InternalLinkMessage internalLinkMessage = new InternalLinkMessage();
         * internalLinkMessage.setText(textMessage.getText());
         */
        return ((ObjectMessage) message).getObject();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.jms.support.converter.MessageConverter#toMessage(java.lang.Object, javax.jms.Session)
     */
    @Override
    public Message toMessage(final Object object, final Session session) throws JMSException, MessageConversionException {
        if (object == null) {
            throw new IllegalArgumentException("Passed object is null");
        }
        if (!(object instanceof InternalLinkMessage)) {
            throw new IllegalArgumentException("Passed object is of wrong type (" + object.getClass() + ")");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("toMessage:  " + object);
        }
        
        final InternalLinkMessage iLinkMessage = (InternalLinkMessage) object;
        final Message message = session.createObjectMessage(iLinkMessage);
        message.setStringProperty(DUPLICATE_DETECTION_ID, buildILinkMessageID(iLinkMessage));
        return message;
    }

    /**
     * Build a unique ID for the message composed from the {@code code}, {@code site} and the {@code type}
     * 
     * @param iLinkMessage
     *            the target message
     * @return the unique ID of the message
     */
    private String buildILinkMessageID(final InternalLinkMessage iLinkMessage) {
        return new StringBuilder(iLinkMessage.getCode()).append('_') //
                .append(iLinkMessage.getSite()).append('_') //
                .append(iLinkMessage.getType().toString()).append('_') //
                .append(iLinkMessage.getLanguage()).append('_') //
                .append(iLinkMessage.isForce()) //
                .toString();
    }
}
