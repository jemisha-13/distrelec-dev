/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;
import org.dom4j.io.SAXReader;

/**
 * util to export and parse jira issues
 * 
 * @author rhaemmerli, Namics AG
 * @since Namics Extensions 1.0
 */

public class JiraUtil {

    public static final String JIRA_BASE_URL = "http://jira.distrelec.com";

    private static final String JIRA_SEARCH_URL = JIRA_BASE_URL + "/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?";

    public void downloadJira(final ReleaseInfo releaseInfo, final String jiraReleaseFilePath, final String query) throws UnsupportedEncodingException {
        final String jira_user = releaseInfo.getConfiguration().getProperty(ReleaseInfo.CFG_JIRAUSER);
        final String jira_pass = releaseInfo.getConfiguration().getProperty(ReleaseInfo.CFG_JIRAPASS);
        final String authParam = "os_username=" + jira_user + "&os_password=" + jira_pass;

        final String optParam = "&tempMax=1000";
        final String qParam = "&jqlQuery=";

        final String url = JIRA_SEARCH_URL + authParam + optParam + qParam + URLEncoder.encode(query, "ISO-8859-1");

        // final String jiraReleaseFilePath = tempDir.getAbsolutePath() + File.separator + outFile;
        IOUtil.download(url, jiraReleaseFilePath);

    }

    public Map<String, JiraData> parseJiraData(final String jiraReleaseFilePath) throws IllegalArgumentException, FileNotFoundException, DocumentException {
        final Map<String, JiraData> jiraMap = new HashMap<String, JiraData>();

        final SAXReader reader = new SAXReader();

        reader.addHandler("/rss/channel/item", new JiraEventHandler(jiraMap));
        reader.read(jiraReleaseFilePath);
        return jiraMap;
    }

    class JiraEventHandler implements ElementHandler {

        Map<String, JiraData> jiraMap;

        public JiraEventHandler(final Map<String, JiraData> jiraMap) {
            this.jiraMap = jiraMap;
        }

        @Override
        public void onEnd(final ElementPath arg0) {
            final JiraData data = new JiraData();

            final Element current = arg0.getCurrent();

            data.setId(current.valueOf("key"));
            data.setTitle(current.valueOf("title"));
            data.setUrl(current.valueOf("link"));
            data.setStatus(current.valueOf("status"));
            data.setResolution(current.valueOf("resolution"));
            data.setAssignee(current.valueOf("assignee"));
            data.setReporter(current.valueOf("reporter"));
            data.setType(current.valueOf("type"));
            data.setFixVersion(current.valueOf("fixVersion"));

            jiraMap.put(data.getId(), data);
        }

        @Override
        public void onStart(final ElementPath arg0) {
            // do nothing
        }

    }

}
