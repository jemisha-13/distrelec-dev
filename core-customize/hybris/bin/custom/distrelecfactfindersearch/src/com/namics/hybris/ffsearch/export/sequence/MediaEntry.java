package com.namics.hybris.ffsearch.export.sequence;

import java.io.PipedInputStream;

import com.opencsv.CSVWriter;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

public class MediaEntry {

    private final String channelCode;
    private final PipedInputStream inputStream;
    private final ZipArchiveOutputStream zipArchiveOutputStream;
    private final CSVWriter writer;
    private final Thread exportMediaThread;

    public MediaEntry(final String channelCode, final PipedInputStream inputStream, final ZipArchiveOutputStream zipArchiveOutputStream,
               final CSVWriter writer, final Thread exportMediaThread) {
        this.channelCode = channelCode;
        this.inputStream = inputStream;
        this.zipArchiveOutputStream = zipArchiveOutputStream;
        this.exportMediaThread = exportMediaThread;
        this.writer = writer;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public PipedInputStream getInputStream() {
        return inputStream;
    }

    public ZipArchiveOutputStream getZipArchiveOutputStream() {
        return zipArchiveOutputStream;
    }

    public CSVWriter getWriter() {
        return writer;
    }

    public Thread getExportMediaThread() {
        return exportMediaThread;
    }
}
