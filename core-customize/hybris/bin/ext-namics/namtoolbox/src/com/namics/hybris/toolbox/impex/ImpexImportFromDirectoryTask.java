package com.namics.hybris.toolbox.impex;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.Importer;
import de.hybris.platform.impex.jalo.imp.ImpExImportReader;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.util.CSVConstants;
import de.hybris.platform.util.CSVReader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.ExceptionUtils;
import com.namics.hybris.toolbox.FileUtils;
import com.namics.hybris.toolbox.impex.media.DirectoryMediaDataTranslator;

/**
 * <p>
 * Performs an import via the hybris impex interface.
 * </p>
 * <p>
 * The files look for impex files in a given directory and perform an import of all files matching a wildcard pattern.
 * </p>
 * 
 * @author jweiss, namics ag
 * @since AXPO 1.0
 * 
 */

public class ImpexImportFromDirectoryTask {
    /** Used logger instance. */
    private static final Logger LOG = Logger.getLogger(ImpexExportToDirectoryTask.class.getName());

    /**
     * The directory, where the files to import are.
     */
    protected String importDirectory;

    /**
     * Only files matching the wildcard pattern will be imported.
     */
    protected String wildcard = "*.impex";
    /**
     * The encoding of all files in the list.
     */
    protected String encoding = "UTF-8";

    /**
     * Is every imported line printed out into the log file?
     */
    protected boolean outputImportDetail = false;

    /**
     * Main class to be able to run it directly as a java program.
     * 
     * @param args
     *            the arguments from commandline
     */
    public static void main(final String[] args) throws Exception {
        new ImpexImportFromDirectoryTask().run();
    }

    public void run() throws Exception {
        JaloSession.getCurrentSession();

        ImpexImportFromDirectoryTask task = null;
        task = (ImpexImportFromDirectoryTask) Registry.getApplicationContext().getBean("importImpexDataFromDirectoryTask");
        task.performTask();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.axi.axicore.task.AbstractTask#performTask()
     */
    public void performTask() throws IOException, Exception {

        // We disable all restrictions, so we get
        // all data from the flexisearch
        JaloSession.getCurrentSession().setAttribute("disableRestrictions", Boolean.TRUE);

        File[] impexFilelist;
        String importDirectoryPath = null;
        try {
            final Resource inputDirectoryRes = FileUtils.createResourceFromFilepath(this.importDirectory, false);
            if (!inputDirectoryRes.getFile().isDirectory()) {
                throw new IOException("Directory path isn't a directory: " + this.importDirectory);
            }
            importDirectoryPath = inputDirectoryRes.getFile().getAbsolutePath();
            final FilenameFilter filenameFilter = new WildcardFileFilter(wildcard);
            impexFilelist = inputDirectoryRes.getFile().listFiles(filenameFilter);
            Arrays.sort(impexFilelist, new FileComparator());

        } catch (final IOException e) {
            throw new IOException("File error. " + "Probably, this path points to a non-existing file or it is " + "a classpath resource in a ziped file/jar: "
                    + this.importDirectory, e);
        }

        // Configure, where medias has to be exported.
        DirectoryMediaDataTranslator.defaultExportDirectory = importDirectoryPath;
        DirectoryMediaDataTranslator.defaultImportDirectory = importDirectoryPath;

        LOG.info("Start importing " + impexFilelist.length + " files ...");
        for (final File file : impexFilelist) {

            Importer importer = null;
            try {
                final CSVReader reader = new CSVReader(file, CSVConstants.DEFAULT_ENCODING);
                importer = new Importer(reader);

                Item item = null;
                do {
                    item = importer.importNext();

                    if (item != null && this.outputImportDetail) {
                        try {
                            LOG.debug(" - Processed items: " + importer.getProcessedItemsCountOverall() + "(Item: " + item + ")");
                        } catch (final Exception e) {
                            /*
                             * When an item is removed, it is returned, but the access to this item (like item.toString() ends in an
                             * exception. This is here the case cause we call the .toString-method (Item: item). --> We catch this
                             * exceptions and continue to run the import. (jweiss, 6.3.2009
                             */
                        }
                    }
                } while (item != null);
                LOG.info("successfully imported impex file.");
            } catch (final Exception e) {
                LOG.error("catched Exception: " + e.getMessage());
                final ImpExImportReader impExImportReader = importer.getReader();
                LOG.error("Current exception: " + ExceptionUtils.generateExceptionText(e));
                LOG.error("Current header:    " + impExImportReader.getCurrentHeader());
                LOG.error("Current ValueLine: " + impExImportReader.getCurrentValueLine());

                throw e;
            } finally {
                LOG.debug("Importing impex data from '" + file.getAbsolutePath() + "' finished.");
            }
        }
        LOG.info("Importing of " + impexFilelist.length + " files finished.");

    }

    public String getImportDirectory() {
        return importDirectory;
    }

    public void setImportDirectory(final String importDirectory) {
        this.importDirectory = importDirectory;
    }

    public String getWildcard() {
        return wildcard;
    }

    public void setWildcard(final String wildcard) {
        this.wildcard = wildcard;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public boolean isOutputImportDetail() {
        return outputImportDetail;
    }

    public void setOutputImportDetail(final boolean outputImportDetail) {
        this.outputImportDetail = outputImportDetail;
    }

}

/**
 * Compares to filenames.
 * 
 * $Revision$ $LastChangedBy$ $LastChangedDate$
 */
class FileComparator implements Comparator<File> {

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(final File file1, final File file2) {
        return file1.getName().compareTo(file2.getName());
    }

}
