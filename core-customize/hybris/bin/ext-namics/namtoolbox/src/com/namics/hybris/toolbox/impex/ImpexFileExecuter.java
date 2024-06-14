/*
 * Copyright 2000-2010 namics ag. All rights reserved.
 */

package com.namics.hybris.toolbox.impex;

import de.hybris.platform.impex.jalo.ImpExManager;
import de.hybris.platform.jalo.AbstractSystemCreator;
import de.hybris.platform.util.JspContext;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Performs an import via the hybris impex interface. Receives some file names and runs the import on these files in the given order.
 * 
 * @author jweiss, namics ag
 * @since MGB MEL 1.0
 */
public class ImpexFileExecuter {

    private static final Logger LOG = Logger.getLogger(ImpexFileExecuter.class.getName());
    private List<String> impexFilelist = new ArrayList<String>();
    private String encoding = "utf-8";

    /**
     * Perform the import of the impex files.
     * 
     * @param jspc
     *            the jsp context; you can use it to write progress information to the jsp page during creation
     * @throws Exception
     *             is a general exception
     */
    public void performImport(final JspContext jspc) throws Exception {
        performImport(jspc, true);
    }

    /**
     * Perform the import of the impex files.
     * 
     * @param jspc
     *            the jsp context; you can use it to write progress information to the jsp page during creation
     * @param printUl
     *            set to true if the li-Tag should be encapsulate with ul-Tags
     * @throws Exception
     *             is a general exception
     */
    public void performImport(final JspContext jspc, final boolean printUl) throws Exception {
        LOG.info("Start importing " + impexFilelist.size() + " file(s) ...");
        if (printUl) {
            AbstractSystemCreator.log("<ul>", jspc);
        }
        if (getImpexFilelist().size() > 0) {
            for (final String filepath : getImpexFilelist()) {
                LOG.info("Import: " + filepath);
                AbstractSystemCreator.log("<li>Impex file:" + filepath + "</li>", jspc);
                final InputStream is = ImpexFileExecuter.class.getResourceAsStream(filepath.trim());
                if (is != null) {
                    try {
                        ImpExManager.getInstance().importData(is, encoding, true);
                    } finally {
                        is.close();
                    }
                } else {
                    LOG.error("File '" + filepath.trim() + "' not found. Could not import file!");
                }
            }
        } else {
            AbstractSystemCreator.log("<li>No files to import</li>", jspc);
        }
        if (printUl) {
            AbstractSystemCreator.log("</ul>", jspc);
        }
        LOG.info("Importing of " + impexFilelist.size() + " file(s) finished.");
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
}
