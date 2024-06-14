package com.namics.hybris.i18n.jalo;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;

/**
 * MessageResourceTableEntry.
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.2
 * 
 */
public class MessageResourceTableEntry extends GeneratedMessageResourceTableEntry {

    @Override
    protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException {
        // business code placed here will be executed before the item is created
        // then create the item
        final Item item = super.createItem(ctx, type, allAttributes);
        // business code placed here will be executed after the item was created
        // and return the item
        return item;
    }

}
