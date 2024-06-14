package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Builds a Google Mobile Sitemap, consisting of only mobile-friendly content. To configure options, use {@link #builder(URL, File)}
 * 
 * @author Dan Fabulich
 * @see <a href="http://www.google.com/support/webmasters/bin/answer.py?answer=34648">Creating Mobile Sitemaps</a>
 */
public class GoogleMobileSitemapGenerator extends SitemapGenerator<GoogleMobileSitemapUrl, GoogleMobileSitemapGenerator> {

    GoogleMobileSitemapGenerator(final AbstractSitemapGeneratorOptions<?> options) {
        super(options, new Renderer());
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     * 
     * @param baseUrl
     *            All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir
     *            Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @throws MalformedURLException
     */
    public GoogleMobileSitemapGenerator(final String baseUrl,final File baseDir) throws MalformedURLException {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
    }

    /**
     * Configures the generator with a base URL and directory to write the sitemap files.
     * 
     * @param baseUrl
     *            All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir
     *            Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     */
    public GoogleMobileSitemapGenerator(final URL baseUrl,final File baseDir) {
        this(new SitemapGeneratorOptions(baseUrl, baseDir));
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
    public static SitemapGeneratorBuilder<GoogleMobileSitemapGenerator> builder(final URL baseUrl,final File baseDir) {
        return new SitemapGeneratorBuilder<GoogleMobileSitemapGenerator>(baseUrl, baseDir, GoogleMobileSitemapGenerator.class);
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
    public static SitemapGeneratorBuilder<GoogleMobileSitemapGenerator> builder(final String baseUrl,final  File baseDir) throws MalformedURLException {
        return new SitemapGeneratorBuilder<GoogleMobileSitemapGenerator>(baseUrl, baseDir, GoogleMobileSitemapGenerator.class);
    }

    private static class Renderer extends AbstractSitemapUrlRenderer<GoogleMobileSitemapUrl> {

        @Override
        public Class<GoogleMobileSitemapUrl> getUrlClass() {
            return GoogleMobileSitemapUrl.class;
        }

        @Override
        public void render(final GoogleMobileSitemapUrl url, final OutputStreamWriter out, final W3CDateFormat dateFormat) throws IOException {
            String additionalData = "    <mobile:mobile/>\n";
            super.render(url, out, dateFormat, additionalData);

        }

        @Override
        public String getXmlNamespaces() {
            return "xmlns:mobile=\"http://www.google.com/schemas/sitemap-mobile/1.0\"";
        }

    }
}
