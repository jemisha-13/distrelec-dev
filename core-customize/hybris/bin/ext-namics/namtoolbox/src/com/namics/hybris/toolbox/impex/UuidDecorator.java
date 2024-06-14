/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex;

import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;

import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

/**
 * <p>
 * Returns a UUDI as value.
 * </p>
 * 
 * 
 * @author jweiss, namics ag
 * @since Nettoshop 1.0
 * 
 */
public class UuidDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger LOG = Logger.getLogger(UuidDecorator.class.getName());

    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        final String resultValue = UUID.randomUUID().toString();
        LOG.debug(resultValue);
        return resultValue;
    }

}
