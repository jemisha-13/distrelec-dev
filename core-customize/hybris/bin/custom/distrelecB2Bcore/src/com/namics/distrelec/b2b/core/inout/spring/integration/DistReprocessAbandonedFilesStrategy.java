package com.namics.distrelec.b2b.core.inout.spring.integration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;

/**
 * Import files are moved to a processing folder once they are started to be imported. If hybris is restarted
 * during the import, they will remain there. This strategy returns those files to an import folder, so they
 * can be reprocessed again.
 */
public class DistReprocessAbandonedFilesStrategy {

    private final Logger LOG = LoggerFactory.getLogger(DistReprocessAbandonedFilesStrategy.class);

    private String baseDirectory;

    private String processingDirectory;

    private AcceptOnceFileListFilter acceptOnceFileListFilter;

    @PostConstruct
    public void reprocessFiles() {
        File processingFolder = new File(getProcessingDirectory());

        if (processingFolder != null && processingFolder.listFiles() != null) {
            List<File> files = new ArrayList<>();
            for (File file : processingFolder.listFiles()) {
                if (file.isFile()) {
                    files.add(file);
                }
            }

            // filter abandoned files
            List<File> filteredFiles = getAcceptOnceFileListFilter().filterFiles(files.toArray());
            for (File file : filteredFiles) {
                // move abandoned file
                File destination = new File(getBaseDirectory() + File.separator + file.getName());
                LOG.info(String.format("moving %s to %s", file.getAbsolutePath(), destination.getAbsolutePath()));
                getAcceptOnceFileListFilter().remove(file);
                file.renameTo(destination);
            }
        }
    }

    public String getBaseDirectory() {
        return baseDirectory;
    }

    @Required
    public void setBaseDirectory(final String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public String getProcessingDirectory() {
        return processingDirectory;
    }

    @Required
    public void setProcessingDirectory(final String processingDirectory) {
        this.processingDirectory = processingDirectory;
    }

    public AcceptOnceFileListFilter getAcceptOnceFileListFilter() {
        return acceptOnceFileListFilter;
    }

    @Required
    public void setAcceptOnceFileListFilter(final AcceptOnceFileListFilter acceptOnceFileListFilter) {
        this.acceptOnceFileListFilter = acceptOnceFileListFilter;
    }
}
