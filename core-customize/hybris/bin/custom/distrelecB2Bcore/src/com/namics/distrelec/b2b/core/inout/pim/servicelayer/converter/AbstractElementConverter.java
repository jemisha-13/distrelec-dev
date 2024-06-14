/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Charsets;
import com.namics.distrelec.b2b.core.util.DistUtils;

/**
 * Provide common conversion functionality.
 * 
 * @author ascherrer, Namics AG
 * @since Distrelec 3.3
 */
public abstract class AbstractElementConverter {

    private static final Logger LOG = LogManager.getLogger(AbstractElementConverter.class);

    public static final int MAX_COLUMN_LENGTH_DEFAULT = 255;
    public static final int MAX_COLUMN_LENGTH_PRODUCT_NAME = 230; // Smaller because product name will be enriched by 31 chars when stored
                                                                  // in CartEntry.info (DISTRELEC-7076)

    protected String truncate(final String name, final int maxLength, final String pimId, final String xPath) {
        if (name != null) {
            final int currentLengthUtf8 = name.getBytes(Charsets.UTF_8).length;
            if (currentLengthUtf8 > maxLength) {
                LOG.warn("XPath [{}] content of Stibo object ID [{}] is too long and was truncated from {} to {} bytes",
                        new Object[] { xPath, pimId, Integer.toString(currentLengthUtf8), Integer.toString(maxLength) });
                return DistUtils.truncateWithCharset(name, maxLength, Charsets.UTF_8);
            }
        }
        return name;
    }


    protected String truncate(final String name, final int maxLength) {
        return truncate(name,maxLength,null,null);
    }

}
