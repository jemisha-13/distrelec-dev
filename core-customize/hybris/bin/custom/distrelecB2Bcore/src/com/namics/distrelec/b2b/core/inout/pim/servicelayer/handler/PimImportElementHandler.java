/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.dom4j.ElementHandler;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;

/**
 * Extends the {@link ElementHandler} with the {@link ImportContext}.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public interface PimImportElementHandler extends ElementHandler {

    void setImportContext(ImportContext importContext);

}
