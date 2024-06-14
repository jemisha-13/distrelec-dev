/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.imp.DefaultImportProcessor;
import de.hybris.platform.impex.jalo.imp.ValueLine;
import de.hybris.platform.jalo.Item;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Most of the CSV-Files do not have a first empty column, which is necessary for impex. This <code>ImportProcessor</code> adds virtually a
 * hidden column at the first position.
 * </p>
 * 
 * @author jweiss, namics ag
 * @since Nettoshop 1.0
 * 
 * @deprecated Doesn't work correctly!!
 */
public class MoveColumnImportProcessor extends DefaultImportProcessor {

    @Override
    public Item processItemData(final ValueLine valueLine) throws ImpExException {
        final ValueLine newValueLine = new ValueLine(valueLine.getHeader(), valueLine.getHeader().getTypeCode() /*
                                                                                                                 * Use not own type code,
                                                                                                                 * take it from the header
                                                                                                                 */, createNewMap(valueLine.getSource(),
                valueLine.getHeader().getTypeCode()), valueLine.getLineNumber(), valueLine.getLocation());
        return super.processItemData(newValueLine);
    }

    private static Map<Integer, String> createNewMap(final Map<Integer, String> source, final String typeCode) {
        final Map<Integer, String> sourceCopy = new HashMap<Integer, String>();
        // System.out.println("Original-Map: " + source);
        sourceCopy.put(Integer.valueOf(0), typeCode);
        for (final Integer keyInteger : source.keySet()) {
            final String value = source.get(keyInteger);
            final Integer newKeyInteger = Integer.valueOf(Integer.valueOf(keyInteger) + 1);
            sourceCopy.put(newKeyInteger, value);
        }
        // System.out.println("New-Map: " + sourceCopy);
        return Collections.unmodifiableMap(sourceCopy);
    }
}
