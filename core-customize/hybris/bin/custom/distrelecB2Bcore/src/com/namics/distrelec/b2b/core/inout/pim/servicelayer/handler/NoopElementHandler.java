package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;

/**
 * No-operation element handler is good for the performance because it detaches the row and frees the memory.
 * 
 * @author rhaemmerli
 */
public class NoopElementHandler implements ElementHandler {

    @Override
    public void onStart(final ElementPath elementPath) {
        // do nothing
    }

    @Override
    public void onEnd(final ElementPath elementPath) {
        elementPath.getCurrent().detach();
    }

}
