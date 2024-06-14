package com.namics.distrelec.b2b.core.jalo.productfinder;

import org.apache.log4j.Logger;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;

/**
 * DistProductFinderFeatureValue.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class DistProductFinderFeatureValue extends GeneratedDistProductFinderFeatureValue {
    @SuppressWarnings("unused")
    private final static Logger LOG = Logger.getLogger(DistProductFinderFeatureValue.class.getName());

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
