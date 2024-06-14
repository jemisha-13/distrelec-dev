package com.namics.hybris.toolbox.impex;

import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.impex.jalo.ImpExMedia;
import de.hybris.platform.impex.jalo.exp.Export;
import de.hybris.platform.impex.jalo.exp.ExportConfiguration;
import de.hybris.platform.impex.jalo.exp.Exporter;
import de.hybris.platform.impex.jalo.exp.ImpExExportMedia;
import de.hybris.platform.impex.jalo.util.ImpExUtils;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.enumeration.EnumerationValue;
import de.hybris.platform.util.CSVConstants;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

import com.namics.hybris.toolbox.DateTimeUtils;
import com.namics.hybris.toolbox.FileUtils;
import com.namics.hybris.toolbox.impex.media.DirectoryMediaDataTranslator;

/**
 * <p>
 * Performs an export via the hybris impex interface.
 * </p>
 * <p>
 * Receives some file names and uses these files as impex header. The export on these files is done in the given order. The result is
 * written with a timestamp in a certain directory.
 * </p>
 * 
 * @author jweiss, namics ag
 * @since AXPO 1.0
 * 
 */

public class ImpexExportToDirectoryTask {
    /** Used logger instance. */
    private static final Logger LOG = Logger.getLogger(ImpexExportToDirectoryTask.class.getName());

    public static final String IMPEX_LINE_DELIMITER = "\n";

    /**
     * The list of files with impex header to export
     */
    protected List<String> impexFilelist;

    /**
     * The directory, where the files are exported.
     */
    protected String exportDirectory;

    /**
     * The encoding of all files in the list.
     */
    protected String encoding = "UTF-8";

    /**
     * Is every exported line printed out into the log file?
     */
    protected boolean outputExportDetail = false;

    /**
     * After a job completion, the name of the directory, where the files were exported is written to this attribute.
     * 
     * <code>exportDirectory + timestamp</code>
     * 
     * <pre>
     * C:\\data\export\20090306092742447\\
     * </pre>
     */
    protected String lastExportDirectory = null;

    /**
     * Main class to be able to run it directly as a java program.
     * 
     * @param args
     *            the arguments from commandline
     */
    public static void main(final String[] args) throws Exception {
        new ImpexExportToDirectoryTask().run();
    }

    public void run() throws Exception {
        JaloSession.getCurrentSession();

        ImpexExportToDirectoryTask task = null;
        task = (ImpexExportToDirectoryTask) Registry.getApplicationContext().getBean("exportImpexDataToDirectoryTask");
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

        String exportDirectoryPath = null;

        try {
            Resource outputDirectoryRes = null;
            final String dateString = DateTimeUtils.createDateString();
            outputDirectoryRes = FileUtils.createResourceFromFilepath(this.exportDirectory + "/" + dateString, false);
            final File outputDirectoryFile = FileUtils.checkAndCreateDirectory(outputDirectoryRes.getFile().getAbsolutePath());
            exportDirectoryPath = outputDirectoryFile.getAbsolutePath();

        } catch (final IOException e) {
            throw new IOException("File error. " + "Probably, this path points to a directory in the " + "classpath or in a ziped file/jar: "
                    + this.exportDirectory, e);
        }

        // Configure, where medias has to be exported.
        DirectoryMediaDataTranslator.defaultExportDirectory = exportDirectoryPath;
        DirectoryMediaDataTranslator.defaultImportDirectory = exportDirectoryPath;

        try {
            LOG.info("Start importing " + impexFilelist.size() + " files ...");
            int number = 0;
            for (final String filepath : impexFilelist) {
                final String numberString = DateTimeUtils.convertNumber(number++, 7);
                final String outputFilename = numberString + "." + FileUtils.getFileNameFromPath(filepath) + ".impex";
                final String outputFilepath = FileUtils.concatDirectoryAndFilename(exportDirectoryPath, outputFilename);

                Exporter exporter = null;
                try {
                    final StringBuilder beforeHeaderData = new StringBuilder();

                    final Resource headerFileRes = FileUtils.createResourceFromFilepath(filepath, true);
                    final String headerText = FileUtils.readFile(headerFileRes.getInputStream());
                    LOG.info("Export impex data of header file: " + headerFileRes.getDescription());
                    LOG.debug("Header is: " + headerText);

                    final ImpExMedia scriptmedia = ImpExManager.getInstance().createImpExMedia(outputFilename + ".header.impex", CSVConstants.DEFAULT_ENCODING,
                            headerText);
                    final EnumerationValue exportMode = ImpExManager.getExportReimportStrictMode();

                    final ExportConfiguration exportconfig = new ExportConfiguration(scriptmedia, exportMode);
                    exportconfig.setSingleFile(true);

                    exporter = new Exporter(exportconfig);
                    final Export export = exporter.export();

                    final ImpExExportMedia exporteddata = export.getExportedData();
                    final ImpExExportMedia exportedmedias = export.getExportedMedias();

                    /*
                     * If the header contains more than one line, each line must be commented out. Otherwise this can produce errors (like
                     * missing macros).
                     */

                    beforeHeaderData.append(exportconfig.getCommentCharacter());
                    beforeHeaderData.append(" Header file: ");
                    beforeHeaderData.append(filepath);
                    beforeHeaderData.append("(" + headerFileRes.getDescription() + ")");
                    beforeHeaderData.append(IMPEX_LINE_DELIMITER);

                    beforeHeaderData.append(exportconfig.getCommentCharacter());
                    beforeHeaderData.append(" Output file: ");
                    beforeHeaderData.append(outputFilepath);
                    beforeHeaderData.append(IMPEX_LINE_DELIMITER);

                    beforeHeaderData.append(exportconfig.getCommentCharacter());
                    beforeHeaderData.append(" Header: ");
                    beforeHeaderData.append(headerText);
                    beforeHeaderData.append(IMPEX_LINE_DELIMITER);
                    beforeHeaderData.append(exportconfig.getCommentCharacter() + IMPEX_LINE_DELIMITER);

                    beforeHeaderData.append(exportconfig.getCommentCharacter());
                    beforeHeaderData.append(" Download URL Data: ");
                    beforeHeaderData.append(exporteddata != null ? exporteddata.getDownloadURL() : "-");
                    beforeHeaderData.append(IMPEX_LINE_DELIMITER);

                    beforeHeaderData.append(exportconfig.getCommentCharacter());
                    beforeHeaderData.append(" Download URL Media: ");
                    beforeHeaderData.append(exportedmedias != null ? exportedmedias.getDownloadURL() : "-");
                    beforeHeaderData.append(IMPEX_LINE_DELIMITER);

                    final StringBuilder content = ImpExUtils.getContent(exporteddata);
                    FileUtils.writeFile(beforeHeaderData.toString() + content.toString(), outputFilepath);

                    LOG.info("successfully exported impex file '" + filepath + "'.");
                } catch (final Exception e) {
                    LOG.warn("An exception occured while exporting impex file '" + filepath + "'.", e);
                    throw e;
                } finally {
                    LOG.info("Importing impex data from '" + filepath + "' finished.");
                }
            }
        } finally {
            this.lastExportDirectory = exportDirectoryPath;
            LOG.info("Finished importing " + impexFilelist.size() + " files.");
        }

    }

    public List<String> getImpexFilelist() {
        return impexFilelist;
    }

    public void setImpexFilelist(final List<String> impexFilelist) {
        this.impexFilelist = impexFilelist;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public String getExportDirectory() {
        return exportDirectory;
    }

    public void setExportDirectory(final String exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

    public boolean isOutputExportDetail() {
        return outputExportDetail;
    }

    public void setOutputExportDetail(final boolean outputExportDetail) {
        this.outputExportDetail = outputExportDetail;
    }

    public String getLastExportDirectory() {
        return lastExportDirectory;
    }

}
