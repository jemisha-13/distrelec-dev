/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.importtool;

import com.namics.distrelec.b2b.facades.importtool.exception.ImportToolException;

import java.util.List;
import java.util.Map;

/**
 * DistImportToolFacade
 * 
 * @author daezamofinl, Distrelec
 * @since Distrelec 1.0
 */
public interface DistImportToolFacade {

    Map<String, Object> searchProductsFromFile(final String orderFileName) throws ImportToolException;

    Map<String, Object> searchProductsFromFile(final String orderFileName, final int articleNumberPosition, final int quantityPosition,
            final int referencePosition, final boolean ignoreFirstRow) throws ImportToolException;

    Map<String, Object> searchProductsFromData(final String data) throws ImportToolException;

    Map<String, Object> searchProductsFromData(final String data, final int articleNumberPosition, final int quantityPosition, final int referencePosition)
            throws ImportToolException;

    List<String[]> getLinesFromFile(final String orderFileName) throws ImportToolException;

}
