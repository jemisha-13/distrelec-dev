/*
 * Copyright 2000-2009 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.FileUtils;

import de.hybris.platform.util.CSVCellDecorator;

/**
 * Reads the input from a file and returns it as a string.
 * 
 * @author rhusi, namics ag
 * @since MGB PIM 1.0
 * 
 */
public class FileToStreamDecorator implements CSVCellDecorator {

    private static final Logger LOG = Logger.getLogger(FileToStreamDecorator.class.getName());

    @Override
    public String decorate(final int pos, final Map<Integer, String> srcLine) {
        try {
            final Resource res = FileUtils.createResourceFromFilepath(srcLine.get(Integer.valueOf(pos)), true);
            return FileUtils.readFile(res.getInputStream());
        } catch (final FileNotFoundException e) {
            LOG.error(e.getMessage());
        } catch (final IOException e) {
            LOG.error(e.getMessage());
        }
        return null;
    }
}
