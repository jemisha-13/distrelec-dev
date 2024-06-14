/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.spring;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;

/**
 * 
 * Util class to create Resources from paths and get content from resources.
 * 
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class ResourceUtils {

    /**
     * Calls an url and get it's content as an <code>InputStream</code>.
     * 
     * @param url
     *            The url to call the data from.
     * @return An input stream.
     */
    public static InputStream receiveContentFromUrl(final String urlString) throws IOException {
        URL url;
        InputStream resultStream = null;

        // Open the URL for reading
        url = new URL(urlString);
        resultStream = url.openStream();

        return resultStream;
    }

    public static final Pattern LOCAL_FILENAME_PATTERN = Pattern.compile("([A-Za-z]{1,1})([:]{1,1})([\\\\/]{1,1})(.*)");
    public static final Pattern UNC_FILENAME_PATTERN = Pattern.compile("[\\\\/]{2,2}(.*)");

    /**
     * Returns a Resource for the <code>filepath</code> attribute. If the <code>filepath</code> starts with "classpath:", the method makes a
     * instance of <code>ClassPathResource</code>, otherwise a <code>FileSystemResource</code>.
     * 
     * @param throwExceptionIfNotExists
     *            Throws an exception, if the file doesn't exist and this parameter is <code>true</code>. If set to <code>false</code>, the
     *            file is create.
     * @see ClassPathResource
     * @see FileSystemResource
     */
    public static Resource createResourceFromString(final String path, final boolean throwExceptionIfNotExists) throws IOException {
        if (path == null) {
            if (throwExceptionIfNotExists) {
                throw new FileNotFoundException("File path must not be null");
            } else {
                return null;
            }
        }

        Resource resource = null;

        final String lowerCasePath = path.toLowerCase();

        final Matcher localFilenameMatcher = LOCAL_FILENAME_PATTERN.matcher(lowerCasePath);
        final Matcher uncFilenameMatcher = UNC_FILENAME_PATTERN.matcher(lowerCasePath);

        if (localFilenameMatcher.matches() || uncFilenameMatcher.matches()) {
            resource = new FileSystemResource(path);
        } else {
            final ResourceEditor resEditor = new ResourceEditor();
            resEditor.setAsText(path);
            resource = (Resource) resEditor.getValue();

        }

        return resource;
    }

    /**
     * Converts the InputStream to String, using UTF-8 encoding.
     */
    public static String convertStreamToString(final InputStream inputstream) throws IOException {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine() method. We iterate until the BufferedReader return null
         * which means there's no more data to read. Each line will appended to a StringBuilder and returned as String.
         */
        if (inputstream != null) {
            final StringBuilder buffer = new StringBuilder();
            String line;

            try {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    buffer.append(line).append("\n");
                }
            } finally {
                inputstream.close();
            }

            try {
                return buffer.substring(0, buffer.length() - 1);
            } catch (IndexOutOfBoundsException e) {
                return "";
            }
        } else {
            return "";
        }
    }

}
