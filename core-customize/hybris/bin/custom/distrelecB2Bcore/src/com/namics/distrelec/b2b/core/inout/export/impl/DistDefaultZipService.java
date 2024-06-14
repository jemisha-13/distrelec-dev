/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.export.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import com.namics.distrelec.b2b.core.inout.export.DistZipService;
import com.namics.distrelec.b2b.core.inout.export.job.exception.DistFlexibleSearchExportJobException;

/**
 * Default implementation of {@link DistZipService}.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public class DistDefaultZipService implements DistZipService {

    @Override
    public byte[] zip(final InputStream data, final String zipArchiveEntryName) {
        ByteArrayOutputStream zippedData = null;
        ZipArchiveOutputStream exportZipDataStream = null;
        try {
            zippedData = new ByteArrayOutputStream();
            exportZipDataStream = new ZipArchiveOutputStream(zippedData);
            final ZipArchiveEntry entry = new ZipArchiveEntry(zipArchiveEntryName);
            exportZipDataStream.putArchiveEntry(entry);
            IOUtils.copy(data, exportZipDataStream);
            exportZipDataStream.closeArchiveEntry();
            exportZipDataStream.close();
        } catch (final IOException e) {
            throw new DistFlexibleSearchExportJobException("Could not zip data (archive entry name [" + zipArchiveEntryName + "])", e);
        } finally {
            IOUtils.closeQuietly(exportZipDataStream);
            IOUtils.closeQuietly(zippedData);
        }

        return zippedData.toByteArray();
    }

}
