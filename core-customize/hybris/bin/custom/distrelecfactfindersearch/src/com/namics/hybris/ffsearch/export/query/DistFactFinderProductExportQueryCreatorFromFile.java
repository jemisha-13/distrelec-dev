/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.query;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.CharStreams;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchQueryCreator;

/**
 * This class is used for testing only. The query to be executed will be loaded from file .........
 * 
 * @author ascherrer, Namics AG
 * @since v3.2.16
 */
@SuppressWarnings({ "PMD" })
public class DistFactFinderProductExportQueryCreatorFromFile implements DistFlexibleSearchQueryCreator {

    private static final Logger LOG = LoggerFactory.getLogger(DistFactFinderProductExportQueryCreatorFromFile.class);
    private static final String FLEXIBLESEARCH_FILE = "/distrelecfactfindersearch/test/query-creator/queryCreatorFromFile.fs";

    @Override
    public String createQuery() {
        String flexiSearchQuery = "";
        InputStream fis = getClass().getResourceAsStream(FLEXIBLESEARCH_FILE);
        try {
            flexiSearchQuery = CharStreams.toString(new InputStreamReader(fis, "UTF-8"));
        } catch (IOException ioe) {
            LOG.error("Unable to read file [" + FLEXIBLESEARCH_FILE + "].");
        }
        LOG.info("Executing Query\n{}", flexiSearchQuery);
        return flexiSearchQuery;
    }
}
