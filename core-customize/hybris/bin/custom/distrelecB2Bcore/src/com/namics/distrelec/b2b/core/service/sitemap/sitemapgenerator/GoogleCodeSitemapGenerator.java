package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Builds a code sitemap for Google Code Search. To configure options, use {@link #builder(URL, File)}
 * 
 * @author Dan Fabulich
 * @see <a href="http://www.google.com/support/webmasters/bin/answer.py?answer=75224">Creating Code Search Sitemaps</a>
 */
public class GoogleCodeSitemapGenerator extends SitemapGenerator<GoogleCodeSitemapUrl, GoogleCodeSitemapGenerator> {

    GoogleCodeSitemapGenerator(final AbstractSitemapGeneratorOptions<?> options) {
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
    public GoogleCodeSitemapGenerator(final String baseUrl, final File baseDir) throws MalformedURLException {
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
    public GoogleCodeSitemapGenerator(final URL baseUrl, final File baseDir) {
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
    public static SitemapGeneratorBuilder<GoogleCodeSitemapGenerator> builder(final URL baseUrl, final File baseDir) {
        return new SitemapGeneratorBuilder<GoogleCodeSitemapGenerator>(baseUrl, baseDir, GoogleCodeSitemapGenerator.class);
    }

    /**
     * Configures a builder so you can specify sitemap generator options
     * 
     * @param baseUrl
     *            All URLs in the generated sitemap(s) should appear under this base URL
     * @param baseDir
     *            Sitemap files will be generated in this directory as either "sitemap.xml" or "sitemap1.xml" "sitemap2.xml" and so on.
     * @return a builder; call .build() on it to make a sitemap generator
     * @throws MalformedURLException
     */
    public static SitemapGeneratorBuilder<GoogleCodeSitemapGenerator> builder(final String baseUrl,final File baseDir) throws MalformedURLException {
        return new SitemapGeneratorBuilder<GoogleCodeSitemapGenerator>(baseUrl, baseDir, GoogleCodeSitemapGenerator.class);
    }

    private static class Renderer extends AbstractSitemapUrlRenderer<GoogleCodeSitemapUrl> {

        @Override
        public Class<GoogleCodeSitemapUrl> getUrlClass() {
            return GoogleCodeSitemapUrl.class;
        }

        @Override
        public void render(final GoogleCodeSitemapUrl url, final OutputStreamWriter out, final W3CDateFormat dateFormat) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append("    <codesearch:codesearch>\n");
            renderTag(sb, "codesearch", "filetype", url.getFileType());
            renderTag(sb, "codesearch", "license", url.getLicense());
            renderTag(sb, "codesearch", "filename", url.getFileName());
            renderTag(sb, "codesearch", "packageurl", url.getPackageUrl());
            renderTag(sb, "codesearch", "packagemap", url.getPackageMap());
            sb.append("    </codesearch:codesearch>\n");
            super.render(url, out, dateFormat, sb.toString());
        }

        @Override
        public String getXmlNamespaces() {
            return "xmlns:codesearch=\"http://www.google.com/codesearch/schemas/sitemap/1.0\"";
        }

    }

}
