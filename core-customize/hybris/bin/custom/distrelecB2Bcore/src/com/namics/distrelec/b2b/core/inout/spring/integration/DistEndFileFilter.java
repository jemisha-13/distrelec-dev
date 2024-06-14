/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.spring.integration;

import java.io.File;

import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.integration.file.filters.AbstractFileListFilter;

/**
 * Search for an end file named like the given XML file.
 *
 * @param <F>
 * 
 * @author daehusir, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class DistEndFileFilter<F> extends AbstractFileListFilter<F> {

    private static final Logger LOG = LogManager.getLogger(DistEndFileFilter.class);

    @Override
    public boolean accept(final F file) {
        final File currentFile = (File) file;
        final String fileNameWithoutExtension = currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.'));
        final File[] endFiles = currentFile.getParentFile().listFiles((dir, name) -> name.endsWith(".end"));

        if (ArrayUtils.isEmpty(endFiles)) {
            return false;
        }

        for (final File endFile : endFiles) {
            final String fileName = endFile.getName();
            if (endFile.getName().startsWith(fileNameWithoutExtension)) {
                if (!endFile.delete()) {
                    LOG.error("Can not delete end file with name " + fileName + "! Import for " + currentFile.getName() + " has not been started!");
                } else {
                    return true;
                }
            }
        }

        return false;
    }
}
