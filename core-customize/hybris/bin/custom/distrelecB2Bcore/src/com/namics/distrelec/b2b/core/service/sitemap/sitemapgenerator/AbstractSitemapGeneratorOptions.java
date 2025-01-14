package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.io.File;
import java.net.URL;

// that weird thing with generics is so sub-classed objects will return themselves
// It makes sense, I swear! http://madbean.com/2004/mb2004-3/
abstract class AbstractSitemapGeneratorOptions<THIS extends AbstractSitemapGeneratorOptions<THIS>> {
    File baseDir;
    String baseUrl;
    String fileNamePrefix = "sitemap";
    boolean allowMultipleSitemaps = true;
    W3CDateFormat dateFormat;
    int maxUrls = SitemapGenerator.MAX_URLS_PER_SITEMAP;
    boolean autoValidate = false;
    boolean gzip = false;
    String language;

    public AbstractSitemapGeneratorOptions(final URL baseUrl, final File baseDir) {
        if (baseDir == null) {
            throw new NullPointerException("baseDir may not be null");
        }
        if (baseUrl == null) {
            throw new NullPointerException("baseUrl may not be null");
        }
        this.baseDir = baseDir;
        this.baseUrl = baseUrl.toString();
    }

    /** The prefix of the name of the sitemaps we'll create; by default this is "sitemap" */
    public THIS fileNamePrefix(final String fileNamePrefix) {
        if (fileNamePrefix == null) {
            throw new NullPointerException("fileNamePrefix may not be null");
        }
        this.fileNamePrefix = fileNamePrefix;
        return getThis();
    }

    /**
     * When more than the maximum number of URLs are passed in, should we split into multiple sitemaps automatically, or just throw an exception?
     */
    public THIS allowMultipleSitemaps(final boolean allowMultipleSitemaps) {
        this.allowMultipleSitemaps = allowMultipleSitemaps;
        return getThis();
    }

    /** The date formatter, typically configured with a {@link W3CDateFormat.Pattern} and/or a time zone */
    public THIS dateFormat(final W3CDateFormat dateFormat) {
        this.dateFormat = dateFormat;
        return getThis();
    }

    /**
     * The maximum number of URLs to allow per sitemap; the default is the maximum allowed (50,000), but you can decrease it if you wish (to make your
     * auto-generated sitemaps smaller)
     */
    public THIS maxUrls(final int maxUrls) {
        if (maxUrls > SitemapGenerator.MAX_URLS_PER_SITEMAP) {
            throw new RuntimeException("You can only have " + SitemapGenerator.MAX_URLS_PER_SITEMAP
                    + " URLs per sitemap; to use more, allowMultipleSitemaps and generate a sitemap index. You asked for " + maxUrls);
        }
        this.maxUrls = maxUrls;
        return getThis();
    }

    /**
     * Validate the sitemaps automatically after writing them; this takes time (and may fail for Google-specific sitemaps)
     */
    public THIS autoValidate(final boolean autoValidate) {
        this.autoValidate = autoValidate;
        return getThis();
    }

    /** Gzip the sitemaps after they are written to disk */
    public THIS gzip(final boolean gzip) {
        this.gzip = gzip;
        return getThis();
    }

    public THIS language(final String language) {
        this.language = language;
        return getThis();
    }

    @SuppressWarnings("unchecked")
    THIS getThis() {
        return (THIS) this;
    }
}
