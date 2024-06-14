/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util.releasenotes;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.springframework.util.Assert;

/**
 * 
 * 
 * @author rhaemmerli, Namics AG
 * 
 */
public class ReleaseInfo {

    private static final String BASE_DIR = "release-notes";
    private static final String FILE_RELEASE_NOTES = BASE_DIR + "/release_notes.properties";

    public static final String CFG_JIRAURL = "jira_url";
    public static final String CFG_JIRAUSER = "jira_user";
    public static final String CFG_JIRAPASS = "jira_pass";

    public static final String PROP_JIRA_RELEASE = "jiraRelease";

    public static final String PROP_JIRA_LIMITATIONS = "jiraLimitations";

    public static final String PROP_SVN_MIN = "svnRevisionFrom";

    public static final String PROP_SVN_MAX = "svnRevisionTo";

    private final String version;

    private final String svnRevisionFrom;

    private final String svnRevisionTo;

    private final String jiraRelease;

    private final String jiraLimitations;

    private final Properties configuration;

    public ReleaseInfo(final String version) throws IOException {
        final ClassLoader loader = this.getClass().getClassLoader();

        Assert.hasText(version);
        this.version = version;

        this.configuration = getConfuguration();

        // load version property

        final Properties versionProperty = new Properties();
        final InputStream versionStream = loader.getResourceAsStream(BASE_DIR + "/" + version + "/src/release.properties");

        versionProperty.load(versionStream);

        this.jiraRelease = versionProperty.getProperty(PROP_JIRA_RELEASE);
        this.jiraLimitations = versionProperty.getProperty(PROP_JIRA_LIMITATIONS);
        this.svnRevisionFrom = versionProperty.getProperty(PROP_SVN_MIN);
        this.svnRevisionTo = versionProperty.getProperty(PROP_SVN_MAX);

        Assert.hasText(this.jiraRelease);
        Assert.hasText(this.jiraLimitations);
        Assert.hasText(this.svnRevisionFrom);
        Assert.hasText(this.svnRevisionTo);

    }

    private Properties getConfuguration() throws IOException {
        final ClassLoader loader = this.getClass().getClassLoader();

        // load main property
        final Properties mainProperty = new Properties();
        final InputStream mainStream = loader.getResourceAsStream(FILE_RELEASE_NOTES);
        mainProperty.load(mainStream);
        return mainProperty;
    }

    public String getVersion() {
        return version;
    }

    public String getSvnRevisionFrom() {
        return svnRevisionFrom;
    }

    public String getSvnRevisionTo() {
        return svnRevisionTo;
    }

    public String getJiraRelease() {
        return jiraRelease;
    }

    public String getJiraLimitations() {
        return jiraLimitations;
    }

    public Properties getConfiguration() {
        return configuration;
    }

}
