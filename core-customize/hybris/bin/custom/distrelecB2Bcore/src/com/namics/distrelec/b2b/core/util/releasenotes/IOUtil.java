/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Utility class to handle IO operations
 * 
 * @author rhaemmerli, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class IOUtil {

    private static final Logger LOG = LoggerFactory.getLogger(IOUtil.class);

    private final File baseDir;
    private final File tempDir;
    private final File sourceDir;

    public static final String RELEASES_DIR = "/release-notes";

    public enum FileType {
        TEMP, SOURCE, FINAL
    }

    /**
     * default constructor
     * 
     * @param releaseInfo
     * @throws URISyntaxException
     */
    public IOUtil(final ReleaseInfo releaseInfo) throws URISyntaxException {
        final URL resource = this.getClass().getResource(RELEASES_DIR);
        final File releasesDir = new File(resource.toURI());
        Assert.isTrue(releasesDir.exists(), "release config directory not found");

        baseDir = new File(releasesDir, releaseInfo.getVersion());
        Assert.isTrue(baseDir.exists(), "release config directory not found");

        sourceDir = new File(baseDir, "/src");
        Assert.isTrue(sourceDir.exists());

        tempDir = new File(baseDir, "/tmp");

        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
    }

    /**
     * execute command and write output to file
     * 
     * @param command
     *            the command with arguments
     * @param outputFile
     *            the output file
     * @throws IOException
     */
    public static void exec(final String[] command, final String outputFile) throws IOException {

        final Process tr = Runtime.getRuntime().exec(command);
        // final Writer wr = new OutputStreamWriter(tr.getOutputStream());
        final InputStream inputStream = tr.getInputStream();
        final BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream));

        final InputStream errorStream = tr.getErrorStream();
        final BufferedReader errorReader = new BufferedReader(new InputStreamReader(errorStream));

        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));

        String currentLine;
        while ((currentLine = inputReader.readLine()) != null) {
            bufferedWriter.write(currentLine);
            bufferedWriter.newLine();
        }

        while ((currentLine = errorReader.readLine()) != null) {
            LOG.info("ERROR:" + currentLine);

        }

        // wr.flush();

        bufferedWriter.close();
    }

    /**
     * download load file an store it on the disk
     * 
     * @param address
     *            url of the file
     * @param localFileName
     *            file name
     */
    public static void download(final String address, final String localFileName) {
        OutputStream out = null;
        URLConnection conn = null;
        InputStream in = null;
        try {
            // Get the URL
            final URL url = new URL(address);
            // Open an output stream to the destination file on our local filesystem
            out = new BufferedOutputStream(new FileOutputStream(localFileName));
            conn = url.openConnection();
            in = conn.getInputStream();

            // Get the data
            final byte[] buffer = new byte[1024];
            int numRead;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
            }
            // Done! Just clean up and get out
        } catch (final Exception exception) {
            LOG.warn("Exception occurred during download", exception);

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (final IOException ioe) {
                // Shouldn't happen, maybe add some logging here if you are not
                // fooling around ;)
            }
        }
    }

    /**
     * get absolute path of file
     * 
     * @param fileName
     *            the name of the file
     * @param type
     *            THE TYPE OG THE FILE
     * @return the absolute path to the file
     */
    public String getPath(final String fileName, final FileType type) {
        if (FileType.TEMP.equals(type)) {
            return tempDir + File.separator + fileName;
        } else if (FileType.SOURCE.equals(type)) {
            return sourceDir + File.separator + fileName;
        } else if (FileType.FINAL.equals(type)) {
            return baseDir + File.separator + fileName;
        } else {
            throw new IllegalArgumentException("unknown filetype [" + type + "]");
        }

    }

    /**
     * read a file
     * 
     * @param includeFile
     *            absolut path
     * @return content of the file as string
     */
    public String readFile(final String includeFile) {

        try {
            return FileUtils.readFileToString(new File(includeFile));
        } catch (final IOException e) {
            // LOG
            return "file not found" + e.getMessage();
        }
    }

    /**
     * read a file locates in the "src" directory
     * 
     * @param includeFile
     *            file name
     * @return content of the file as string
     */
    public String readSourceFile(final String includeFile) {
        final String path = getPath(includeFile, FileType.SOURCE);

        try {
            return FileUtils.readFileToString(new File(path));
        } catch (final IOException e) {
            // LOG
            return "file not found" + e.getMessage();
        }
    }
}
