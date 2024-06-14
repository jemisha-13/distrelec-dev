package com.namics.hybris.toolbox.impex.media;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.media.MediaDataHandler;
import de.hybris.platform.jalo.media.Media;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.namics.hybris.toolbox.FileUtils;

/**
 * Import and export of the files to a directory in the file system.
 * 
 * $Revision$ $LastChangedBy$ $LastChangedDate$
 */
public class DirectoryMediaDataHandler implements MediaDataHandler {

    /**
     * The directory, where the files are exported, like
     * 
     * <pre>
     * &quot;C:\\data\\export\\&quot;
     * </pre>
     */
    protected String exportDirectory;

    /**
     * The directory, where the files to import are, like
     * 
     * <pre>
     * &quot;C:\\data\\import\\&quot;
     * </pre>
     */
    protected String importDirectory;

    /**
     * Default constructor
     */
    public DirectoryMediaDataHandler() {
        // empty constructor
    }

    public DirectoryMediaDataHandler(final String outputDirectory, final String inputDirectory) {
        this.exportDirectory = outputDirectory;
        this.importDirectory = inputDirectory;
    }

    /**
     * 
     * @param resourcePath
     *            A short path compatible with the <code>ResourceEditor</code>-Syntax.
     * @return The absolute path in the file system.
     */
    protected String getAbsoluteDirectoryAndCreate(final String resourcePath) {

        String resultDirectoryPath;
        try {
            Resource directoryRes = null;
            directoryRes = FileUtils.createResourceFromFilepath(resourcePath, false);
            final File outputDirectoryFile = FileUtils.checkAndCreateDirectory(directoryRes.getFile().getAbsolutePath());
            resultDirectoryPath = outputDirectoryFile.getAbsolutePath();
        } catch (final IOException e) {
            throw new RuntimeException("File error. " + "Probably, this path points to a directory in the " + "classpath or in a ziped file/jar: "
                    + resourcePath, e);
        }

        return resultDirectoryPath;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.media.MediaDataHandler#cleanUp()
     */
    public void cleanUp() {
        // do nothing

    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.media.MediaDataHandler#exportData(de.hybris.platform.jalo.media.Media)
     */
    public String exportData(final Media media) throws ImpExException {
        try {
            if (checkMediaFile(media) && StringUtils.hasText(media.getRealFileName())) {
                final String realname = media.getRealFileName() == null ? "" : media.getRealFileName();
                // we want the realname and it has to be unique
                final String filename = realname + "__" + media.getFileName();
                writeMediaFile(media, filename);
                return filename;
            }
        } catch (final IOException e) {
            throw new ImpExException(e, "The media '" + media + "' couldn't be exported.", -1);
        }

        // if no data is in the media
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.impex.jalo.media.MediaDataHandler#importData(de.hybris.platform.jalo.media.Media, java.lang.String)
     */
    public void importData(final Media media, final String value) throws ImpExException {
        try {
            readFileToMedia(media, value);
        } catch (final Exception e) {
            throw new ImpExException(e, "The file '" + value + "' for the media '" + media + "' couldn't be imported.", -1);
        }
    }

    /**
     * Checks if a file is attached to the media.
     * 
     * @param media
     *            The hybris media to export.
     * @return if there is a valid file attached.
     */
    @SuppressWarnings("unchecked")
    protected boolean checkMediaFile(final Media media) {
        final Collection<File> files = media.getFiles();
        if (files.size() <= 0) {
            return false;
        } else {
            final File file = files.iterator().next();
            return file.exists();
        }
    }

    /**
     * Writes an Media in the destination directory.
     * 
     * @param media
     *            The hybris media to export
     * @param filename
     *            The new name of the file.
     * @throws IOException
     *             If the output file(s) couldn't be open or wasn't writable.
     */
    @SuppressWarnings("unchecked")
    protected void writeMediaFile(final Media media, final String filename) throws IOException {
        final Collection<File> files = media.getFiles();
        if (files.size() <= 0) {
            throw new IOException("In the Media '" + media + "' were no files.");
        } else {
            final File file = files.iterator().next();
            final String sourceFilepath = file.getAbsolutePath();
            final String destinationFilepath = FileUtils.concatDirectoryAndFilename(this.getExportDirectory(), filename);

            FileUtils.copyFile(sourceFilepath, destinationFilepath);
        }
    }

    /**
     * Reads a file from the source directory into a Media.
     * 
     * @param media
     *            The hybris media to import/attach the file
     * @param filename
     *            The name of the file.
     * @throws IOException
     *             If the file(s) couldn't be open or wasn't readable.
     */
    protected void readFileToMedia(final Media media, final String filename) throws IOException, Exception {
        if (!StringUtils.hasText(filename)) {
            return;
        }

        if ("null".equalsIgnoreCase(filename)) {
            media.removeData(false);
            return;
        }

        final String sourceFilepath = FileUtils.concatDirectoryAndFilename(this.getImportDirectory(), filename);
        final File file = new File(sourceFilepath);

        media.setFile(file);
        // System.out.println("Imported the media '"+media+"'.");
        // file.exists();
        // media.getCode();
        // media.getRealFileName();
        // media.getFiles();
        // ((File)media.getFiles().iterator().next()).exists();

        try {
            final String realFilename = filename.substring(0, filename.lastIndexOf("__"));
            media.setRealFileName(realFilename);
        } catch (final IndexOutOfBoundsException e) {
            // we ignore and let the real
            // file name as it was before.
        }

    }

    public String getExportDirectory() {
        return exportDirectory;
    }

    public void setExportDirectory(final String exportDirectory) {
        this.exportDirectory = exportDirectory;
    }

    public String getImportDirectory() {
        return importDirectory;
    }

    public void setImportDirectory(final String importDirectory) {
        this.importDirectory = importDirectory;
    }

}
