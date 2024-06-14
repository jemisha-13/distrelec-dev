package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Encapsulates a single URL to be inserted into a Web sitemap (as opposed to a Geo sitemap, a Mobile sitemap, a Video sitemap, etc which are Google
 * specific). Specifying a lastMod, changeFreq, or priority is optional; you specify those by using an Options object.
 * 
 * @see Options
 * @author Dan Fabulich
 */
public class WebSitemapUrl implements ISitemapUrl {

    private String code;
    private final String entity;
    private final String language;
    private final String defaultLanguage;
    private final URL url;
    private final Date lastMod;
    private final ChangeFreq changeFreq;
    private final Double priority;
    private List<WebSitemapLink> links;

    /** Encapsulates a single simple URL */
    public WebSitemapUrl(String url) throws MalformedURLException {
        this(new URL(url));
    }

    /** Encapsulates a single simple URL */
    public WebSitemapUrl(URL url) {
        this.url = url;
        this.entity = null;
        this.lastMod = null;
        this.changeFreq = null;
        this.priority = null;
        this.defaultLanguage = null;
        this.language = null;
        this.links = null;
    }

    /** Creates an URL with configured options */
    public WebSitemapUrl(Options options) {
        this((AbstractSitemapUrlOptions<?, ?>) options);
    }

    WebSitemapUrl(AbstractSitemapUrlOptions<?, ?> options) {
        this.entity = options.entity;
        this.url = options.url;
        this.lastMod = options.lastMod;
        this.changeFreq = options.changeFreq;
        this.priority = options.priority;
        this.defaultLanguage = options.defaultLanguage;
        this.language = options.language;
        this.links = options.links;
    }

    public String getEntity() {
        return entity;
    }

    @Override
    public Date getLastMod() {
        return lastMod;
    }

    public ChangeFreq getChangeFreq() {
        return changeFreq;
    }

    public Double getPriority() {
        return priority;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    public List<WebSitemapLink> getLinks() {
        return links;
    }

    public String getLanguage() {
        return language;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public String getCode() {
        return code;
    }

    /** Options to configure web sitemap URLs */
    public static class Options extends AbstractSitemapUrlOptions<WebSitemapUrl, Options> {

        private String code;

        public Options() {
            super(WebSitemapUrl.class);
        }

        /** Configure this URL */
        public Options(String url) throws MalformedURLException {
            this(new URL(url));
        }

        /** Configure this URL */
        public Options(URL url) {
            super(url, WebSitemapUrl.class);
        }

        public Options code(final String code) {
            this.code = code;
            return getThis();
        }
    }
}
