package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * Generates a regular old sitemap (USE THIS CLASS FIRST). To configure options, use {@link #builder(URL, File)}
 * 
 * @author Dan Fabulich
 */
public class WebSitemapGenerator extends SitemapGenerator<WebSitemapUrl, WebSitemapGenerator> {

    WebSitemapGenerator(final AbstractSitemapGeneratorOptions<?> options) {
        super(options, new Renderer());
    }

    /**
     * Configures a builder so you can specify sitemap generator options
     * 
     * @param baseUrl
     *            All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir
     *            Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<WebSitemapGenerator> builder(final URL baseUrl, final File baseDir) {
        return new SitemapGeneratorBuilder<WebSitemapGenerator>(baseUrl, baseDir, WebSitemapGenerator.class);
    }

    /**
     * Configures a builder so you can specify sitemap generator options
     * 
     * @param baseUrl
     *            All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir
     *            Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     */
    public static SitemapGeneratorBuilder<WebSitemapGenerator> builder(final String baseUrl, final File baseDir) throws MalformedURLException {
        return new SitemapGeneratorBuilder<WebSitemapGenerator>(baseUrl, baseDir, WebSitemapGenerator.class);
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     * 
     * @param baseUrl
     *            All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir
     *            Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */

    public WebSitemapGenerator(final String baseUrl, final File baseDir) throws MalformedURLException {
        this(new SitemapGeneratorOptions(new URL(baseUrl), baseDir));
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     * 
     * @param baseUrl
     *            All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir
     *            Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public WebSitemapGenerator(final URL baseUrl, final File baseDir) {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
    }

    private static class Renderer extends AbstractSitemapUrlRenderer<WebSitemapUrl> {

        @Override
        public Class<WebSitemapUrl> getUrlClass() {
            return WebSitemapUrl.class;
        }

        @Override
        public void render(final WebSitemapUrl url, final OutputStreamWriter out, final W3CDateFormat dateFormat) throws IOException {
            super.render(url, out, dateFormat, null);
        }

        @Override
        public String getXmlNamespaces() {
            return "xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"";
        }
    }

    public void addUrls(final Map<String, Collection<WebSitemapUrl>> urls) {
        for (final Map.Entry<String, Collection<WebSitemapUrl>> entry : urls.entrySet()) {
            clearUrls();
            setFileNameEntityPart(entry.getKey());
            addUrls(entry.getValue());
            setMapCount(0);
        }
        // setEntityCount(urls.size());
    }
}
