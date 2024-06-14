/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * util to parse svn data
 * 
 * @author rhaemmerli, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class SvnUtil {

    private static final Logger LOG = LoggerFactory.getLogger(SvnUtil.class);

    private static final String SVN_COMMAND = "svn";

    private static final String SVN_URL = "http://svn.distrelec.com/repositories/Distrelec-Relaunch-2012-intern/";

    public void generateSvnLog(final ReleaseInfo releaseInfo, final String svnReleaseFilePath) throws IOException {

        IOUtil.exec(new String[] { SVN_COMMAND, "log", "--xml", "-v", "-r" + releaseInfo.getSvnRevisionFrom() + ":" + releaseInfo.getSvnRevisionTo(),
                SVN_URL + "tags/" + releaseInfo.getVersion() }, svnReleaseFilePath);

    }

    public Stack<SvnData> parseSvnData(final String svnReleaseFilePath) {

        final Stack<SvnData> svnMap = new Stack<SvnData>();

        final SAXReader reader = new SAXReader();
        reader.addHandler("/log/logentry", new SvnEventHandler(svnMap));
        try {
            reader.read(svnReleaseFilePath);
        } catch (DocumentException e) {
            // YTODO Auto-generated catch block
            LOG.warn("Exception occurred during parsing of SVN data", e);
        }

        return svnMap;
    }

    private static final Pattern JIRA_PATTERN = Pattern.compile(".*(DIST\\-|DISTRELEC\\-)(\\d*).*");

    private String extractJiraId(final String title) {
        final Matcher macher = JIRA_PATTERN.matcher(title);
        if (macher.find()) {
            return "DISTRELEC-" + macher.group(2);
        }
        return null;
    }

    class SvnEventHandler implements ElementHandler {

        private final Stack<SvnData> svnMap;

        public SvnEventHandler(final Stack<SvnData> svnMap) {
            this.svnMap = svnMap;
        }

        @Override
        public void onEnd(final ElementPath arg0) {
            final SvnData data = new SvnData();

            final Element current = arg0.getCurrent();

            data.setId(current.valueOf("@revision"));

            data.setComment(current.valueOf("msg"));
            data.setComitter(current.valueOf("author"));

            data.setJiraId(extractJiraId(current.valueOf("msg")));

            final List<Element> paths = current.selectNodes("paths/path");

            for (final Element path : paths) {
                data.addFile(path.valueOf("@action") + ": " + path.getText());
            }

            // for(elements)

            svnMap.add(data);
        }

        @Override
        public void onStart(final ElementPath arg0) {
            // do nothing
        }

    }

    public void getInfo(final ReleaseInfo releaseInfo, final String svnInfoFilePath) throws IOException {
        IOUtil.exec(new String[] { SVN_COMMAND, "info", SVN_URL + "tags/" + releaseInfo.getVersion() }, svnInfoFilePath);
    }

    public void getBranchesInfo(final ReleaseInfo releaseInfo, final String svnInfoFilePath) throws IOException {
        IOUtil.exec(new String[] { SVN_COMMAND, "ls", "-v", SVN_URL + "branches" }, svnInfoFilePath);
    }
}
