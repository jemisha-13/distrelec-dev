/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;
import org.springframework.util.Assert;

/**
 * class to generate the release notes
 * 
 * @author rhaemmerli, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ReleaseNotesGenerator {

    private static final Logger LOG = Logger.getLogger(ReleaseNotesGenerator.class);

    private final String version;

    public ReleaseNotesGenerator(final String version) {
        LOG.info("generate release notes for version: " + version);
        this.version = version;
    }

    public static void main(final String[] args) throws IOException, DocumentException, URISyntaxException {

        Assert.notEmpty(args);
        Assert.hasText(args[0]);

        final String version = args[0];

        new ReleaseNotesGenerator(version).run();
    }

    private final SvnUtil svnUtil = new SvnUtil();
    private final JiraUtil jiraUtil = new JiraUtil();

    /*
     * private final List<SvnData> svnNoJira = new ArrayList<SvnData>();
     * 
     * private final List<SvnData> svnNoJiraInRelease = new ArrayList<SvnData>();
     * 
     * private final List<JiraData> jiraNoCommit = new ArrayList<JiraData>();
     * 
     * private final List<JiraData> jiraHasCommit = new ArrayList<JiraData>();
     */

    private final List<SvnData> svnNoJira = new ArrayList<SvnData>();

    private final List<SvnData> svnNoJiraInRelease = new ArrayList<SvnData>();

    public void run() throws IOException, DocumentException, URISyntaxException {

        LOG.info("start:run()");

        final ReleaseInfo releaseInfo = new ReleaseInfo(version);
        final ReleaseData releaseData = new ReleaseData();

        final IOUtil ioUtil = new IOUtil(releaseInfo);

        // create temporary files
        final String jiraLimitationsFilePath = ioUtil.getPath("jira_limitations.xml", IOUtil.FileType.TEMP);
        final String jiraReleaseFilePath = ioUtil.getPath("jira_release.xml", IOUtil.FileType.TEMP);
        final String svnXmlFilePath = ioUtil.getPath("svn_release.xml", IOUtil.FileType.TEMP);

        releaseData.setSvnInfoPath(ioUtil.getPath("svn_info.txt", IOUtil.FileType.TEMP));
        releaseData.setSvnBranchesPath(ioUtil.getPath("svn_branches.txt", IOUtil.FileType.TEMP));

        // generate jira info
        jiraUtil.downloadJira(releaseInfo, jiraReleaseFilePath, releaseInfo.getJiraRelease());
        jiraUtil.downloadJira(releaseInfo, jiraLimitationsFilePath, releaseInfo.getJiraLimitations());
        final Map<String, JiraData> jiraReleaseMap = jiraUtil.parseJiraData(jiraReleaseFilePath);

        // generate svn info
        svnUtil.generateSvnLog(releaseInfo, svnXmlFilePath);
        svnUtil.getInfo(releaseInfo, releaseData.getSvnInfoPath());
        svnUtil.getBranchesInfo(releaseInfo, releaseData.getSvnBranchesPath());
        final Stack<SvnData> svnReleaseStack = svnUtil.parseSvnData(svnXmlFilePath);

        // merge jira and svn
        releaseData.setJiraLimitations(jiraUtil.parseJiraData(jiraLimitationsFilePath));
        releaseData.setJiras(mergeData(jiraReleaseMap, svnReleaseStack));
        releaseData.setSvnNoJira(svnNoJira);
        releaseData.setSvnNoJiraInRelease(svnNoJiraInRelease);

        // generate output
        final ReleaseNoteHtmlOutput releseNoteHtmlOutput = new ReleaseNoteHtmlOutput(releaseInfo, releaseData);

        final String outputFilePath = ioUtil.getPath("release_" + releaseInfo.getVersion() + ".html", IOUtil.FileType.FINAL);
        final String outputFilePathInternal = ioUtil.getPath("release_" + releaseInfo.getVersion() + "_internal.html", IOUtil.FileType.FINAL);

        releseNoteHtmlOutput.print(outputFilePath);
        releseNoteHtmlOutput.printInternal(outputFilePathInternal);

        LOG.info("end:run()");

        System.out.println(outputFilePath);
        System.out.println(outputFilePathInternal);

    }

    private Map<String, List<JiraData>> mergeData(final Map<String, JiraData> jiraReleaseMap, final Stack<SvnData> svnReleaseStack) {

        final Map<String, List<JiraData>> jiras = new HashMap<String, List<JiraData>>();

        while (!svnReleaseStack.empty()) {
            final SvnData svnData = svnReleaseStack.pop();

            if (StringUtils.isEmpty(svnData.getJiraId())) {
                svnNoJira.add(svnData);
            } else {

                if (jiraReleaseMap.containsKey(svnData.getJiraId())) {
                    jiraReleaseMap.get(svnData.getJiraId()).addSvnEntry(svnData);
                } else {
                    svnNoJiraInRelease.add(svnData);
                }
            }
        }

        for (final JiraData data : jiraReleaseMap.values()) {
            addJira(jiras, data);
        }

        return jiras;
    }

    private void addJira(final Map<String, List<JiraData>> jiras, final JiraData data) {
        if (!jiras.containsKey(data.getType())) {
            jiras.put(data.getType(), new ArrayList<JiraData>());
        }
        jiras.get(data.getType()).add(data);
    }

}
