/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.impex;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderDescriptor;
import de.hybris.platform.jalo.JaloSystemException;

public abstract class AbstractDistCellDecorator extends AbstractImpExCSVCellDecorator {

    protected AbstractColumnDescriptor getColumnDescriptorByName(final HeaderDescriptor header, final Map<Integer, String> line, final String columnName)
            throws JaloSystemException {
        final Collection<AbstractColumnDescriptor> columns = getColumnDescriptor().getHeader().getColumnsByQualifier(columnName);
        if (CollectionUtils.isNotEmpty(columns)) {
            return columns.iterator().next();
        } else {
            throw new JaloSystemException("can not find " + columnName + " in hedaer definition");
        }
    }

    protected String getColumnValueByName(final HeaderDescriptor header, final Map<Integer, String> line, final String columnName) throws JaloSystemException {
        final AbstractColumnDescriptor column = getColumnDescriptorByName(header, line, columnName);
        return line.get(Integer.valueOf(column.getValuePosition()));
    }
}
