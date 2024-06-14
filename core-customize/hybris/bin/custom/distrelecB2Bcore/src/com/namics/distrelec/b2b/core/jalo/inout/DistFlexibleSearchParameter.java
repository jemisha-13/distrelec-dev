package com.namics.distrelec.b2b.core.jalo.inout;

import org.apache.log4j.Logger;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;

/**
 * Jalo type for DistFlexibleSearchParameter.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public class DistFlexibleSearchParameter extends GeneratedDistFlexibleSearchParameter {
    @SuppressWarnings("unused")
    private final static Logger LOG = Logger.getLogger(DistFlexibleSearchParameter.class.getName());

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
