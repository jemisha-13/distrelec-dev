package com.namics.distrelec.b2b.core.jalo.process;

import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloBusinessException;
import de.hybris.platform.jalo.SessionContext;
import de.hybris.platform.jalo.type.ComposedType;
import org.apache.log4j.Logger;

public class DistStockNotificationProcess extends GeneratedDistStockNotificationProcess
{
	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger( DistStockNotificationProcess.class.getName() );
	
	@Override
	protected Item createItem(final SessionContext ctx, final ComposedType type, final ItemAttributeMap allAttributes) throws JaloBusinessException{
		return super.createItem(ctx, type, allAttributes);
	}
}
