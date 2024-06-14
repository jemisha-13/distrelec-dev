/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import com.namics.distrelec.b2b.core.util.releasenotes.IOUtil.FileType;

/**
 * 
 * @author rhaemmerli, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ReleaseNotesGeneratorTest {

    private static final Logger LOG = LoggerFactory.getLogger(ReleaseNotesGeneratorTest.class);

    // private List<JiraData> jiraNoCommit;

    private Map<String, List<JiraData>> jiras;

    private Map<String, JiraData> jiraLimitations;

    private List<SvnData> svnNoJira;

    private List<SvnData> svnNoJiraInRelease;

    /**
     * init test data
     */
    @Before
    public void setUp() {

        // jiras
        jiras = new HashMap<String, List<JiraData>>();

        final JiraData jiraData1 = new JiraData();
        jiraData1.setId("DISTRELEC-123");
        jiraData1.setTitle("Test title");
        jiraData1.setUrl("https://test.com/jira");
        jiraData1.setStatus("open");

        final SvnData svnData1 = new SvnData();
        svnData1.setId("1234");
        svnData1.setComitter("tester");
        svnData1.setJiraId("DISTRELEC-123");
        svnData1.setComment("blabalbalb");
        svnData1.addFile("M: file 1");

        jiraData1.addSvnEntry(svnData1);

        final SvnData svnData2 = new SvnData();
        svnData2.setId("1235");
        svnData2.setComitter("tester");
        svnData2.setJiraId("DISTRELEC-123");
        svnData2.setComment("blabalbalb test 2");
        svnData2.addFile("M: file 2");
        jiraData1.addSvnEntry(svnData2);

        final JiraData jiraData2 = new JiraData();
        jiraData2.setId("DISTRELEC-124");
        jiraData2.setTitle("Test title 2");
        jiraData2.setUrl("https://test.com/jira2");
        jiraData2.setStatus("closed");

        final JiraData jiraData3 = new JiraData();
        jiraData3.setId("DISTRELEC-127");
        jiraData3.setTitle("Test title 3");
        jiraData3.setUrl("https://test.com/jira2");
        jiraData3.setStatus("closed");
        jiraData3.setResolution("wonfix");
        jiraData3.setAssignee("assig 000");
        jiraData3.setReporter("rep 00009");


        final ArrayList<JiraData> arrayList = new ArrayList<JiraData>();
        arrayList.add(jiraData1);
        arrayList.add(jiraData2);
        arrayList.add(jiraData3);
        jiras.put("Work Packages", arrayList);

        final JiraData jiraData4 = new JiraData();
        jiraData4.setId("DISTRELEC-1244");
        jiraData4.setTitle("Test title 3");
        jiraData4.setUrl("https://test.com/jira2");
        jiraData4.setType("Bug");

        jiraData4.setStatus("closed");
        
        final JiraData jiraDataB2 = new JiraData();
        jiraDataB2.setId("DISTRELEC-12443");
        jiraDataB2.setTitle("Test title 33");
        jiraDataB2.setUrl("https://test.com/jira23");
        jiraDataB2.setType("Bug");

        jiraDataB2.setStatus("closed");
        jiraDataB2.setResolution("wonfix");
        jiraDataB2.setAssignee("assig 000");
        jiraDataB2.setReporter("rep 00009");
        
        

        final SvnData svnData3 = new SvnData();
        svnData3.setId("1236");
        svnData3.setComitter("tester");
        svnData3.setJiraId("DISTRELEC-124");
        svnData3.setComment("blabalbalb 1236");
        svnData3.addFile("M: file 2");
        jiraData4.addSvnEntry(svnData3);

        final ArrayList<JiraData> arrayList2 = new ArrayList<JiraData>();
        arrayList2.add(jiraData4);
        arrayList2.add(jiraDataB2);

        
        
        jiras.put("Bug", arrayList2);

        // jiraNoCommit
        // jiraNoCommit = new ArrayList<JiraData>();

        // jiraNoCommit.add(jiraData3);

        // jiras.add(jiraData1);
        // jiras.add(jiraData2);

        // jiraLimitations
        jiraLimitations = new HashMap<String, JiraData>();

        final JiraData jiraData23 = new JiraData();
        jiraData23.setId("DISTRELEC-124");
        jiraData23.setTitle("Test title 2");
        jiraData23.setUrl("https://test.com/jira2");

        jiraLimitations.put("1", jiraData23);

        // svnNoJira

        svnNoJira = new ArrayList<SvnData>();
        final SvnData svnData4 = new SvnData();
        svnData4.setId("1237");
        svnData4.setComitter("tester");
        svnData4.setComment("blabalbalb 1237");

        final SvnData svnData5 = new SvnData();
        svnData5.setId("1238");
        svnData5.setComitter("tester");
        svnData5.setComment("blabalbalb 1237");

        svnNoJira.add(svnData4);
        svnNoJira.add(svnData5);

        // svnNoJiraInRelease
        svnNoJiraInRelease = new ArrayList<SvnData>();
        final SvnData svnData6 = new SvnData();
        svnData6.setId("1239");
        svnData6.setComitter("tester");
        svnData6.setComment("blabalbalb 1239");
        svnData6.setJiraId("DISTRELEC-788");

        final SvnData svnData7 = new SvnData();
        svnData7.setId("1240");
        svnData7.setComitter("tester");
        svnData7.setComment("blabalbalb 1240");
        svnData7.setJiraId("DISTRELEC-789");

        svnNoJiraInRelease.add(svnData6);
        svnNoJiraInRelease.add(svnData7);

    }

    /**
     * test simple template
     */
    @Test
    public void testST() {
        final ST hello = new ST("Hello, <name>");
        hello.add("name", "World");

        final String ret = hello.render();

        Assert.assertEquals("Hello, World", ret);
    }

    /**
     * test simple template
     */
    @Test
    public void testSTtemplate() {
        try {

            final STGroup group = new STGroupFile("st_templates/release_notes.stg", '$', '$');
            final ST template = group.getInstanceOf("main");
            // template.add("jiras", jiraHasCommit);
            // template.add("jiraNoCommit", jiraNoCommit);

            template.add("keys", jiras.keySet());
            template.add("map", jiras);
            /*
             * template.add("svnNoJiraInRelease", svnNoJiraInRelease); template.add("svnNoJira", svnNoJira);
             */
            final String output = template.render();

            final File tempFile = new File(System.getProperty("java.io.tmpdir") + "/testSTtemplate.html");
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tempFile, false));

            System.out.println("write output to temp file: " + tempFile.getAbsolutePath());
            bufferedWriter.append(output);

            bufferedWriter.close();

            Assert.assertTrue(output.length() > 1);
        } catch (final Exception e) {
            LOG.error("Exception occurred for template", e);
            Assert.fail();
        }

    }

    // private void print(final List<JiraData> jiraHasCommit, final List<JiraData> jiraNoCommit, final List<SvnData> svnNoJiraInRelease,
    // final List<SvnData> svnNoJira) throws IOException
    @Test
    public void testPrint() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException, URISyntaxException {

        final ReleaseInfo releaseInfo = new ReleaseInfo("junit.1.2.3");
        final ReleaseData releaseData = new ReleaseData();
        final IOUtil ioUtil = new IOUtil(releaseInfo);

        releaseData.setJiraLimitations(jiraLimitations);
        releaseData.setJiras(jiras);
        releaseData.setSvnInfoPath(ioUtil.getPath("svn_info", FileType.TEMP));
        releaseData.setSvnBranchesPath(ioUtil.getPath("svn_branches", FileType.TEMP));
        releaseData.setSvnNoJira(svnNoJira);
        releaseData.setSvnNoJiraInRelease(svnNoJiraInRelease);

        final ReleaseNoteHtmlOutput htmlOutput = new ReleaseNoteHtmlOutput(releaseInfo, releaseData);

        final String outputFilePath = System.getProperty("java.io.tmpdir") + "/testReleseNotes.html";
        final String outputFilePathInternal = System.getProperty("java.io.tmpdir") + "/testReleseNotesInternal.html";

        htmlOutput.print(outputFilePath);
        htmlOutput.printInternal(outputFilePathInternal);

        System.out.println("write output to temp release notes: " + outputFilePath);
        System.out.println("write output to temp release notes internal: " + outputFilePathInternal);
    }
}
