package com.namics.distrelec.b2b.core.jalo;

import org.apache.log4j.Logger;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;

public class DistEmailMessage extends GeneratedDistEmailMessage {
    @SuppressWarnings("unused")
    private final static Logger LOG = Logger.getLogger(DistEmailMessage.class.getName());

    @Override
    protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException {
        // business code placed here will be executed before the item is created
        // then create the item
        final Item item = super.createItem(ctx, type, allAttributes);
        // business code placed here will be executed after the item was created
        // and return the item
        return item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.acceleratorservices.jalo.email.GeneratedEmailMessage#setBody(de.hybris.platform.jalo.SessionContext,
     * java.lang.String)
     */
    @Override
    public void setBody(final SessionContext ctx, final String value) {
        setProperty(ctx, BODY, value);
    }
}
