package com.namics.distrelec.b2b.core.version;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

@Service
public class GitVersionServiceImpl implements GitVersionService {

    private static final Logger LOG = LoggerFactory.getLogger(GitVersionServiceImpl.class);

    private static final String GIT_INFO_PROPERTIES = "/gitinfo/gitinfo.properties";

    private String cachedVersion;
    private String cachedKey;

    @Override
    public String getGitVersion() {
        if (StringUtils.isNotBlank(cachedVersion)) {
            return cachedVersion;
        }

        final Properties gitProperties = new Properties();
        try {
            gitProperties.load(getClass().getResourceAsStream(GIT_INFO_PROPERTIES));
            cachedVersion = gitProperties.getProperty("git.tag");
            return cachedVersion;
        } catch (final IOException | NullPointerException e) {
            LOG.error("Can not load git property file to read the current version", e);
        } catch (final Exception e) {
            LOG.error("Error occur while loading git info ", e);
        }

        cachedVersion = "n/a";
        return cachedVersion;
    }

    @Override
    public String getRevision() {
        if (StringUtils.isNotBlank(cachedKey)) {
            return cachedKey;
        }

        try {
            final Properties gitProperties = new Properties();
            gitProperties.load(getClass().getResourceAsStream(GIT_INFO_PROPERTIES));
            final String revision = gitProperties.getProperty("git.revision");
            if (StringUtils.isNotBlank(revision)) {
                cachedKey = String.valueOf(Math.abs(revision.hashCode()));
                return cachedKey;
            }
        } catch (final IOException | NullPointerException e) {
            LOG.error("Can not load git property file to read the current key", e);
        } catch (final Exception e) {
            LOG.error("Error occur while loading git info ", e);
        }

        cachedKey = String.valueOf((new Random()).nextInt()
                * 1000);
        return cachedKey;
    }
}
