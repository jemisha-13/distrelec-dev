package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

abstract class SitemapGenerator<U extends ISitemapUrl, THIS extends SitemapGenerator<U, THIS>> {
    /** 15000 URLs per sitemap maximum */
    public static final int MAX_URLS_PER_SITEMAP = 15000;

    private final String baseUrl;
    private final File baseDir;
    private final String fileNamePrefix;
    private final String fileNameSuffix;
    private final boolean allowMultipleSitemaps;
    private final Collection<U> urls = new ConcurrentLinkedQueue<U>();
    private final W3CDateFormat dateFormat;
    private final int maxUrls;
    private final boolean autoValidate;
    private final boolean gzip;
    private final ISitemapUrlRenderer<U> renderer;
    private int mapCount;
    private String language;

    private final List<File> writtenFiles = new ArrayList<File>();

    private boolean finished;

    private String fileNameEntityPart;

    private final List<File> outFiles = new ArrayList<File>();

    public SitemapGenerator(final AbstractSitemapGeneratorOptions<?> options, ISitemapUrlRenderer<U> renderer) {
        baseDir = options.baseDir;
        baseUrl = options.baseUrl;
        fileNamePrefix = options.fileNamePrefix;
        this.dateFormat = options.dateFormat != null ? options.dateFormat : new W3CDateFormat();
        allowMultipleSitemaps = options.allowMultipleSitemaps;
        maxUrls = options.maxUrls;
        autoValidate = options.autoValidate;
        gzip = options.gzip;
        this.renderer = renderer;
        fileNameSuffix = gzip ? ".xml.gz" : ".xml";
        this.language = options.language;
    }

    /**
     * Add one URL of the appropriate type to this sitemap. If we have reached the maximum number of URLs, we'll throw an exception if
     * {@link #allowMultipleSitemaps} is false, or else write out one sitemap immediately.
     * 
     * @param url
     *            the URL to add to this sitemap
     * @return this
     */
    public THIS addUrl(final U url) {
        if (finished) {
            throw new RuntimeException("Sitemap already printed; you must create a new generator to make more sitemaps");
        }
        UrlUtils.checkUrl(url.getUrl().toString(), baseUrl);

        if (urls.size() == maxUrls) {
            if (!allowMultipleSitemaps) {
                throw new RuntimeException(
                        "More than "
                                + maxUrls
                                + " urls, but allowMultipleSitemaps is false.  Enable allowMultipleSitemaps to split the sitemap into multiple files with a sitemap index.");
            }
            if (mapCount == 0) {
                mapCount++;
            }
            writeSiteMap();
            mapCount++;
            urls.clear();
        }
        urls.add(url);
        return getThis();
    }

    /**
     * Add multiple URLs of the appropriate type to this sitemap, one at a time. If we have reached the maximum number of URLs, we'll throw
     * an exception if {@link #allowMultipleSitemaps} is false, or write out one sitemap immediately.
     * 
     * @param urls
     *            the URLs to add to this sitemap
     * @return this
     */
    public THIS addUrls(final Iterable<? extends U> urls) {
        int count = 0;
        for (U url : urls) {
            addUrl(url);

            // last item
            if (count == getUrlsSize(urls) - 1) {
                writeSiteMap();
            }

            count++;
        }
        return getThis();
    }

    private int getUrlsSize(Iterable<? extends U> urlIterable) {
        if (urlIterable instanceof Collection) {
            return ((Collection<?>) urlIterable).size();
        }

        return 0;
    }

    /**
     * Add multiple URLs of the appropriate type to this sitemap, one at a time. If we have reached the maximum number of URLs, we'll throw
     * an exception if {@link #allowMultipleSitemaps} is false, or write out one sitemap immediately.
     * 
     * @param urls
     *            the URLs to add to this sitemap
     * @return this
     */
    public THIS addUrls(final U... urls) {
        for (U url : urls) {
            addUrl(url);
        }
        return getThis();
    }

    /**
     * Add multiple URLs of the appropriate type to this sitemap, one at a time. If we have reached the maximum number of URLs, we'll throw
     * an exception if {@link #allowMultipleSitemaps} is false, or write out one sitemap immediately.
     * 
     * @param urls
     *            the URLs to add to this sitemap
     * @return this
     * @throws MalformedURLException
     */
    public THIS addUrls(final String... urls) throws MalformedURLException {
        for (String url : urls) {
            addUrl(url);
        }
        return getThis();
    }

    /**
     * Add one URL of the appropriate type to this sitemap. If we have reached the maximum number of URLs, we'll throw an exception if
     * {@link #allowMultipleSitemaps} is false, or else write out one sitemap immediately.
     * 
     * @param url
     *            the URL to add to this sitemap
     * @return this
     * @throws MalformedURLException
     */
    public THIS addUrl(final String url) throws MalformedURLException {
        U sitemapUrl;
        try {
            sitemapUrl = renderer.getUrlClass().getConstructor(String.class).newInstance(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return addUrl(sitemapUrl);
    }

    /**
     * Add multiple URLs of the appropriate type to this sitemap, one at a time. If we have reached the maximum number of URLs, we'll throw
     * an exception if {@link #allowMultipleSitemaps} is false, or write out one sitemap immediately.
     *
     * @param urls
     *            the URLs to add to this sitemap
     * @return this
     */
    public THIS addUrls(final URL... urls) {
        for (URL url : urls) {
            addUrl(url);
        }
        return getThis();
    }

    /**
     * Add one URL of the appropriate type to this sitemap. If we have reached the maximum number of URLs, we'll throw an exception if
     * {@link #allowMultipleSitemaps} is false, or write out one sitemap immediately.
     * 
     * @param url
     *            the URL to add to this sitemap
     * @return this
     */
    public THIS addUrl(final URL url) {
        U sitemapUrl;
        try {
            sitemapUrl = renderer.getUrlClass().getConstructor(URL.class).newInstance(url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return addUrl(sitemapUrl);
    }

    @SuppressWarnings("unchecked")
    THIS getThis() {
        return (THIS) this;
    }

    /**
     * Write out remaining URLs; this method can only be called once.
     * 
     * @return a list of files we wrote out to disk
     */
    public List<File> write() {
        if (finished) {
            throw new RuntimeException("Sitemap already printed; you must create a new generator to make more sitemaps");
        }
        if (urls.isEmpty() && mapCount == 0) {
            throw new RuntimeException("No URLs added, sitemap would be empty; you must add some URLs with addUrls");
        }
        writeSiteMap();
        finished = true;
        return outFiles;
    }

    private void writeSiteMap() {
        if (urls.isEmpty()) {
            return;
        }
        String fileNamePrefix = this.fileNamePrefix + "_" + this.fileNameEntityPart;
        if (StringUtils.isNotBlank(language)) {
            fileNamePrefix +=  "_" + language;
        }
        if (mapCount > 0) {
            fileNamePrefix +=  "_" + mapCount;
        }
        final File outFile = new File(baseDir, fileNamePrefix + fileNameSuffix);
        outFiles.add(outFile);
        writtenFiles.add(outFile);
        try {
            final OutputStreamWriter out = gzip ? new SitemapOutputStreamWriter(new GZIPOutputStream(new FileOutputStream(outFile))) : new FileWriter(outFile);
            // Writing the sitemap to the file.
            writeSiteMap(out);
            if (autoValidate) {
                SitemapValidator.validateWebSitemap(outFile);
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem writing sitemap file " + outFile, e);
        } catch (SAXException e) {
            throw new RuntimeException("Sitemap file failed to validate (bug?)", e);
        }
    }

    private void writeSiteMap(final OutputStreamWriter out) throws IOException {
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\" ");

        if (renderer.getXmlNamespaces() != null) {
            out.write(renderer.getXmlNamespaces());
            out.write(" ");
        }
        out.write(">\n");
        for (U url : urls) {
            renderer.render(url, out, dateFormat);
        }
        out.write("</urlset>");
        out.close();
    }

    public Collection<U> getUrls() {
        return this.urls;
    }

    public void clearUrls() {
        this.urls.clear();
    }

    public String getFileNameEntityPart() {
        return fileNameEntityPart;
    }

    public void setFileNameEntityPart(final String fileNameEntityPart) {
        this.fileNameEntityPart = fileNameEntityPart;
    }

    public void setMapCount(final int mapCount) {
        this.mapCount = mapCount;
    }

    /**
     * {@code SitemapOutputStreamWriter}
     * <p>
     * Customized {@link java.io.OutputStreamWriter OutputStreamWriter} that writes only when buffer size exceeds 8K. This is way to reduce
     * the number of {@link #write(String)} method calls.<br/>
     * The call to the {@link #close()} method will force a write to the disk before closing the stream.
     * </p>
     *
     * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
     * @since Distrelec 3.4
     */
    private static class SitemapOutputStreamWriter extends OutputStreamWriter {

        private final static int MAX_LENGTH = 1024 * 8;
        private StringBuilder stringBuilder = new StringBuilder();

        /**
         * Create a new instance of {@code SitemapOutputStreamWriter}
         * 
         * @param out
         */
        public SitemapOutputStreamWriter(final OutputStream out) {
            super(out);
        }

        @Override
        public void write(final String str) throws IOException {
            if (str == null) {
                throw new NullPointerException("str: null value");
            }
            this.stringBuilder.append(str);
            tryWrite(false);
        }

        @Override
        public void close() throws IOException {
            tryWrite(true);
            super.close();
        }

        /**
         * Try to write to the stream the content of the internal buffer. The content of the internal buffer will be written to the stream
         * if:
         * <ul>
         * <li>The boolean flag {@code force} is {@code true}</li>
         * <li>The length of the current buffer exceeds the {@code MAX_LENGTH}</li>
         * </ul>
         * 
         * @param force
         * @throws IOException
         */
        private void tryWrite(final boolean force) throws IOException {
            if (force || this.stringBuilder.length() >= MAX_LENGTH) {
                super.write(stringBuilder.toString());
                stringBuilder.delete(0, stringBuilder.length());
            }
        }
    }

}
