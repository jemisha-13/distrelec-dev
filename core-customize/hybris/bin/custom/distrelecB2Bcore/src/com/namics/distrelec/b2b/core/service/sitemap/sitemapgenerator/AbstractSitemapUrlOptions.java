package com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/** Container for optional URL parameters */
// that weird thing with generics is so sub-classed objects will return themselves
// It makes sense, I swear! http://madbean.com/2004/mb2004-3/
abstract class AbstractSitemapUrlOptions<U extends WebSitemapUrl, THIS extends AbstractSitemapUrlOptions<U, THIS>> {
    Date lastMod;
    ChangeFreq changeFreq;
    Double priority;
    URL url;
    Class<U> clazz;
    String entity;
    String defaultLanguage;
    String language;
    List<WebSitemapLink> links;

    public AbstractSitemapUrlOptions(final Class<U> clazz) {
        this.clazz = clazz;
    }

    public AbstractSitemapUrlOptions(final String url, final Class<U> clazz) throws MalformedURLException {
        this(new URL(url), clazz);
    }

    public AbstractSitemapUrlOptions(final URL url, final Class<U> clazz) {
        if (url == null) {
            throw new NullPointerException("URL may not be null");
        }
        this.url = url;
        this.clazz = clazz;
    }

    public THIS url(final String url) throws MalformedURLException {
        this.url = new URL(url);

        return getThis();
    }

    public THIS entity(final String entity) {
        this.entity = entity;
        return getThis();
    }

    public THIS defaultLanguage(final String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
        return getThis();
    }

    public THIS language(final String language) {
        this.language = language;
        return getThis();
    }

    public THIS links(final List<WebSitemapLink> links) {
        this.links = links;
        return getThis();
    }

    /**
     * The date of last modification of the file. Note that this tag is separate from the If-Modified-Since (304) header the server can return, and
     * search engines may use the information from both sources differently.
     */
    public THIS lastMod(final Date lastMod) {
        this.lastMod = lastMod;
        return getThis();
    }

    /**
     * The date of last modification of the file. Note that this tag is separate from the If-Modified-Since (304) header the server can return, and
     * search engines may use the information from both sources differently.
     * 
     * @throws ParseException
     *             if the string isn't a valid W3C date time
     * @see W3CDateFormat
     */
    public THIS lastMod(final String lastMod) throws ParseException {
        this.lastMod = new W3CDateFormat().parse(lastMod);
        return getThis();
    }

    /**
     * How frequently the page is likely to change. This value provides general information to search engines and may not correlate exactly to how
     * often they crawl the page. The value {@link ChangeFreq#ALWAYS} should be used to describe documents that change each time they are accessed.
     * The value {@link ChangeFreq#NEVER} should be used to describe archived URLs.
     * <p>
     * Please note that the value of this tag is considered a <em>hint</em> and not a command. Even though search engine crawlers may consider this
     * information when making decisions, they may crawl pages marked {@link ChangeFreq#HOURLY} less frequently than that, and they may crawl pages
     * marked {@link ChangeFreq#YEARLY} more frequently than that. Crawlers may periodically crawl pages marked {@link ChangeFreq#NEVER} so that they
     * can handle unexpected changes to those pages.
     * </p>
     */
    public THIS changeFreq(final ChangeFreq changeFreq) {
        this.changeFreq = changeFreq;
        return getThis();
    }

    /**
     * <p>
     * The priority of this URL relative to other URLs on your site. Valid values range from 0.0 to 1.0. This value does not affect how your pages are
     * compared to pages on other sites�it only lets the search engines know which pages you deem most important for the crawlers.
     * </p>
     * <p>
     * The default priority of a page is 0.5.
     * </p>
     * <p>
     * Please note that the priority you assign to a page is not likely to influence the position of your URLs in a search engine's result pages.
     * Search engines may use this information when selecting between URLs on the same site, so you can use this tag to increase the likelihood that
     * your most important pages are present in a search index.
     * </p>
     * <p>
     * Also, please note that assigning a high priority to all of the URLs on your site is not likely to help you. Since the priority is relative, it
     * is only used to select between URLs on your site.
     * </p>
     */
    public THIS priority(final Double priority) {
        if (priority > 1.0) {
            throw new IllegalArgumentException("Priority may not be greater than 1.0: " + priority);
        }
        if (priority < 0) {
            throw new IllegalArgumentException("Priority may not be less than 0: " + priority);
        }
        this.priority = priority;
        return getThis();
    }

    @SuppressWarnings("unchecked")
    THIS getThis() {
        return (THIS) this;
    }

    /** Return an URL based on these settings */
    public U build() {
        try {
            return clazz.getConstructor(getClass()).newInstance(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
