package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;

/**
 * Delegate events depending on attribute.
 * 
 * @author rhaemmerli, Namics AG
 * @since Namics Extensions 1.0
 */
public class AttributeAwareDelegateHandler implements ElementHandler {

    private static final Logger LOG = LogManager.getLogger(AttributeAwareDelegateHandler.class);

    private final String attribute;
    private final Map<String, ElementHandler> delegates;
    private final ElementHandler defaultHandler;

    public AttributeAwareDelegateHandler(final String attribute, final Map<String, ElementHandler> delegates, final ElementHandler defaultHandler) {
        this.attribute = attribute;
        this.delegates = delegates;
        this.defaultHandler = defaultHandler;
    }

    public AttributeAwareDelegateHandler(final String attribute, final Map<String, ElementHandler> delegates) {
        this(attribute, delegates, null);
    }

    @Override
    public void onStart(final ElementPath elementPath) {
        final ElementHandler delegate = getDelegate(elementPath);
        if (null != delegate) {
            delegate.onStart(elementPath);
        }
    }

    @Override
    public void onEnd(final ElementPath elementPath) {
        final ElementHandler delegate = getDelegate(elementPath);
        if (null != delegate) {
            delegate.onEnd(elementPath);
        }
    }

    private ElementHandler getDelegate(final ElementPath elementPath) {
        final Element row = elementPath.getCurrent();

        final String stringValue = row.attributeValue(attribute);

        if (delegates.containsKey(stringValue)) {
			return delegates.get(stringValue);
        }else
		{
			LOG.warn("getDelegate: no delegate handler found for: attribute [" + attribute + ", value [" + stringValue + "]]");
		}
        return null;

    }

}