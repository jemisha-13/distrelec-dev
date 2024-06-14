package com.namics.hybris.webservice.soapui;

import java.io.File;
import java.io.FileFilter;

public class JarFilter implements FileFilter {

    private static final String JAR_EXTENSION = ".jar";

    @Override
    public boolean accept(File file) {
        return file.getName().toLowerCase().endsWith(JAR_EXTENSION);
    }
}
