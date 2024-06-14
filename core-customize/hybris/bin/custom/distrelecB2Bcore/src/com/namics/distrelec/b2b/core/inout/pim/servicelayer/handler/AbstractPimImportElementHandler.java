/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.handler;

import org.dom4j.ElementPath;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.PimImportCancelledException;

/**
 * Abstract element handler class with default implementations.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public abstract class AbstractPimImportElementHandler implements PimImportElementHandler {

    private ImportContext importContext;

    @Override
    public final void onStart(final ElementPath elementPath) {
        checkCancelled();
        onPimImportElementStart(elementPath);
    }

    private void checkCancelled() {
        if (importContext.isCancelled()) {
            throw new PimImportCancelledException("Import has been cancelled");
        }
    }

    /**
     * Hook method to handle XML element.
     * 
     * @param elementPath
     *            the XML element.
     */
    protected void onPimImportElementStart(final ElementPath elementPath) {
        // Do nothing
    }

    @Override
    public void onEnd(final ElementPath elementPath) {
        // Do nothing
    }

    @Override
    public void setImportContext(final ImportContext importContext) {
        this.importContext = importContext;
    }

    public ImportContext getImportContext() {
        return importContext;
    }

}
