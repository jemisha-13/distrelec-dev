/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

/**
 * write release note to html
 * 
 * @author rhaemmerli, Namics AG
 * 
 */

public class ReleaseNoteHtmlOutput {

    private final ReleaseInfo releaseInfo;

    private final ReleaseData releaseData;

    private final STGroup group;

    private final IOUtil ioUtil;

    public ReleaseNoteHtmlOutput(final ReleaseInfo releaseInfo, final ReleaseData releaseData) throws URISyntaxException {

        this.releaseInfo = releaseInfo;
        this.releaseData = releaseData;

        group = new STGroupFile("st_templates/release_notes.stg", '$', '$');

        ioUtil = new IOUtil(releaseInfo);

    }

    public void print(final String outputFilePath) throws IOException {

        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFilePath, false));

        printHeader(bufferedWriter);

        printTitle(bufferedWriter, "Release Notes: " + releaseInfo.getVersion() + " (" + Calendar.getInstance().getTime() + ") ");

        printToc(bufferedWriter);

        printStaticInclude(bufferedWriter, "description", "description.txt");

        printInclude(bufferedWriter, "delivery", releaseData.getSvnInfoPath());

        printMapConfig(bufferedWriter);

        printMapLimitations(bufferedWriter);

        printStaticInclude(bufferedWriter, "installation", "installation.txt");

        printStaticInclude(bufferedWriter, "post installation", "post_installation.txt");

        printFooter(bufferedWriter);

        bufferedWriter.close();

    }

    public void printInternal(final String outputFilePathInternal) throws IOException {
        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFilePathInternal, false));

        printHeader(bufferedWriter);

        printTitle(bufferedWriter, "Release Notes *Internal*: " + releaseInfo.getVersion() + " (" + Calendar.getInstance().getTime() + ") ");

        printStaticInclude(bufferedWriter, "description", "description.txt");

        printInclude(bufferedWriter, "delivery", releaseData.getSvnInfoPath());

        printInclude(bufferedWriter, "open branches", releaseData.getSvnBranchesPath());

        printSvnConfig(bufferedWriter);

        printFooter(bufferedWriter);

        bufferedWriter.close();

    }

    private void printFooter(final BufferedWriter bufferedWriter) throws IOException {
        final ST template = group.getInstanceOf("footer");
        template.add("releaseInfo", releaseInfo);

        final String output = template.render();

        bufferedWriter.append(output);

    }

    private void printInclude(final BufferedWriter bufferedWriter, final String title, final String includeFile) throws IOException {
        printTitle2(bufferedWriter, title);

        String output = ioUtil.readFile(includeFile);

        output = output.replaceAll("(\r\n|\n)", "<br />");

        bufferedWriter.append(output);

    }

    private void printStaticInclude(final BufferedWriter bufferedWriter, final String title, final String includeFile) throws IOException {
        printTitle2(bufferedWriter, title);

        String output = ioUtil.readSourceFile(includeFile);

        output = output.replaceAll("(\r\n|\n)", "<br />");

        bufferedWriter.append(output);

    }

    private void printHeader(final BufferedWriter bufferedWriter) throws IOException {
        final ST template = group.getInstanceOf("header");
        template.add("releaseInfo", releaseInfo);

        final String output = template.render();

        bufferedWriter.append(output);

    }

    private void printMapConfig(final BufferedWriter bufferedWriter) throws IOException {

        printTitle2(bufferedWriter, "configuration management");

        final Map<String, List<JiraData>> jiras = releaseData.getJiras();

        final ST template = group.getInstanceOf("main");
        template.add("keys", jiras.keySet());
        template.add("map", jiras);

        final String output = template.render();

        bufferedWriter.append(output);

    }

    private void printSvnConfig(final BufferedWriter bufferedWriter) throws IOException {

        printTitle2(bufferedWriter, "source management");

        final ST template = group.getInstanceOf("svn_items");
        template.add("svn", releaseData.getSvnNoJiraInRelease());
        template.add("title", "svn with jira not in this release");
        final String output = template.render();
        bufferedWriter.append(output);

        final ST template2 = group.getInstanceOf("svn_items");
        template2.add("svn", releaseData.getSvnNoJira());
        template2.add("title", "svn without jira");
        final String output2 = template2.render();
        bufferedWriter.append(output2);

    }

    private void printMapLimitations(final BufferedWriter bufferedWriter) throws IOException {

        printTitle2(bufferedWriter, "limitations");

        final ST template = group.getInstanceOf("limitations");
        template.add("jiraLimitationsMap", releaseData.getJiraLimitations().values());

        final String output = template.render();

        bufferedWriter.append(output);

    }

    private void printToc(final BufferedWriter bufferedWriter) throws IOException {
        final ST template = group.getInstanceOf("toc");
        bufferedWriter.append(template.render());
    }

    private void printTitle(final BufferedWriter bufferedWriter, final String title) throws IOException {
        final ST template = group.getInstanceOf("title");
        template.add("title", title);
        bufferedWriter.append(template.render());
    }

    private void printTitle2(final BufferedWriter bufferedWriter, final String title) throws IOException {
        final ST template = group.getInstanceOf("title2");
        template.add("title", title);
        bufferedWriter.append(template.render());
    }

}
