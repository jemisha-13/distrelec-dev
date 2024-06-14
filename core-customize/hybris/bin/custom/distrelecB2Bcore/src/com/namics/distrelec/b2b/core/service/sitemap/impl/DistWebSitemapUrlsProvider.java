/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.impl;

import static com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants.DEFAULT_HREF_LANG;
import static com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants.DEFAULT_LINK_REL;
import static com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants.SLASH_CHAR;

import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.namics.distrelec.b2b.core.service.sitemap.SitemapUrlData;
import com.namics.distrelec.b2b.core.service.sitemap.WebSitemapUrlsProvider;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.WebSitemapLink;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.WebSitemapUrl;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@code DistWebSitemapUrlsProvider}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public abstract class DistWebSitemapUrlsProvider<T extends ItemModel> implements WebSitemapUrlsProvider {

    /**
     * Static variables
     */
    protected static final Logger LOG = LoggerFactory.getLogger(DistWebSitemapUrlsProvider.class);
    protected final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    protected final static int MAX_ENTITIES_PER_QUERY = 50000;

    @Autowired
    protected ConfigurationService configurationService;

    /**
     * Instance variable
     */
    private DistUrlResolver<T> distUrlResolver;
    private FlexibleSearchQuery flexibleSearchQuery;
    private CMSSiteModel cmsSiteModel;
    @Autowired
    private FlexibleSearchService flexibleSearchService;
    @Autowired
    private ModelService modelService;
    private Set<LanguageModel> alternativeLanguages;
    private String cmsSiteUrlPrefix;
    private Collection<String> blackList;
    private BaseSiteModel baseSiteModel;
    protected Matcher EXCLUDE_PATTERN_MATCHER;
    protected long lastPk = 0L;
    protected int excluded = 0;
    protected int processed = 0;
    protected int ignored = 0;

    /**
     * Create a new instance of {@code DistWebSitemapUrlsProvider}
     */
    public DistWebSitemapUrlsProvider() {
        super();
    }

    /**
     * Create a new instance of {@code DistWebSitemapUrlsProvider}
     *
     * @param flexibleSearchQuery
     */
    public DistWebSitemapUrlsProvider(final FlexibleSearchQuery flexibleSearchQuery) {
        this.flexibleSearchQuery = flexibleSearchQuery;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.WebSitemapUrlsProvider#addWebSitemapUrlsForWebsite(java.util.Collection)
     */
    public boolean addWebSitemapUrlsForWebsite(final Collection<WebSitemapUrl> dest) {
        if (dest == null) {
            return true;
        }

        final LanguageModel defaultLanguage = getCmsSiteModel().getDefaultLanguage();
        final Set<LanguageModel> languages = getAlternativeLanguages();

        final List<T> entities = getEntities();

        if (CollectionUtils.isNotEmpty(entities)) {
            // Create temporarily map for the different URLs
            final Map<String, String> urlsMap = new HashMap<String, String>();
            // Processing entities

            for (final T entity : entities) {
                lastPk = entity.getPk().getLongValue();
                processed++;
                final String canonicalURL = getCanonicalURL(entity, defaultLanguage.getIsocode());
                final String entityCode = getEntityCode(entity);

                if (isBlackListed(entityCode) || exclude(canonicalURL)) {
                    // Skip the blacklisted entities and the entities with canonical URL that should be excluded
                    excluded += languages.size() + 1;
                    ignored++;
                    // Release the entity for GC.
                    detach(entity, entityCode);
                    continue;
                }

                final Date lastModDate = getLastModifiedDate(entity);
                urlsMap.clear();
                urlsMap.put(DEFAULT_HREF_LANG, canonicalURL);

                for (final LanguageModel lang : languages) {
                    final String url = resolveURL(entity, lang.getIsocode());
                    if (!exclude(url)) {
                        urlsMap.put(lang.getIsocode(), url);
                    } else {
                        excluded++;
                        LOG.warn("{}: Excluding URL '{}'", new String[]{getClass().getSimpleName(), url});
                    }
                }

                // Create a list for web sitemap links
                final List<WebSitemapLink> links = new ArrayList<WebSitemapLink>();
                setWebSitemapLinks(urlsMap, links);

                try {
                    dest.add(//
                            new WebSitemapUrl.Options(urlsMap.get(DEFAULT_HREF_LANG)) //
                                    .code(entityCode) //
                                    .entity(getEntityName()) //
                                    .links(links) //
                                    .lastMod(lastModDate).build());
                } catch (MalformedURLException e) {
                    LOG.warn("Unable to build web sitemap URL from " + urlsMap.get(DEFAULT_HREF_LANG) + " ==> " + e.getMessage(), e);
                }

                // Release the entity for GC.
                detach(entity, entityCode);

                // Check whether we reached the max entities per query
                if (processed % getMaxEntitiesPerQuery() == 0) {
                    return false;
                }
            }
        }

        LOG.info(
                "{}: End of URLs export. \n\tNumber of processed entities: {}\n\tignored entities: {}\n\tnumber of generated URLs: {}\n\tnumber of excluded URLs: {}",
                new String[]{getClass().getSimpleName(), String.valueOf(processed), String.valueOf(ignored), String.valueOf(dest.size()),
                        String.valueOf(excluded)});

        return true;
    }

    /**
     * Detach the entity from the model service context.
     *
     * @param entity
     * @param entityCode
     */
    protected void detach(final T entity, final String entityCode) {
        try {
            // Release the entity for GC.
            getModelService().detach(entity);
        } catch (final Exception exp) {
            LOG.warn("{}: Could not detach entity with code '{}'", new String[]{getClass().getSimpleName(), entityCode});
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.WebSitemapUrlsProvider#getWebSitemapUrlsForWebsite()
     */
    @Override
    public Collection<WebSitemapUrl> getWebSitemapUrlsForWebsite() {
        final Collection<WebSitemapUrl> sitemapUrls = new ConcurrentLinkedQueue<WebSitemapUrl>() {
            @Override
            public Iterator<WebSitemapUrl> iterator() {
                return new CustomIterator<WebSitemapUrl>(super.iterator());
            }
        };

        while (!addWebSitemapUrlsForWebsite(sitemapUrls)) {
            // Reset the query.
            initQuery();
            LOG.info(
                    "\n{}: Intermediate statistics. \n\tNumber of processed entities: {}\n\tignored entities: {}\n\tnumber of generated URLs: {}\n\tnumber of excluded URLs: {}",
                    new String[]{getClass().getSimpleName(), String.valueOf(processed), String.valueOf(ignored), String.valueOf(sitemapUrls.size()),
                            String.valueOf(excluded)});
        }

        return sitemapUrls;
    }

    @Override
    public Map<LanguageModel, Collection<WebSitemapUrl>> getLanguageWebSitemapUrlsForWebsite() {
        //just for now
        return null;
    }

    /**
     * Convert the specified entity into a {@code SitemapUrlData}
     *
     * @param entity          the source entity to convert
     * @param language        the destination language
     * @param defaultLanguage the default language
     * @return a instance of {@code SitemapUrlData}
     */
    protected SitemapUrlData convert(final T entity, final String language, final String defaultLanguage) {
        final SitemapUrlData sitemapUrlData = new SitemapUrlData();
        sitemapUrlData.setEntity(getEntityName());
        sitemapUrlData.setCode(getEntityCode(entity));
        sitemapUrlData.setDefaultLanguage(defaultLanguage);
        sitemapUrlData.setLanguage(language);
        sitemapUrlData.setUrl(resolveURL(entity, language));
        sitemapUrlData.setLastModified(getLastModifiedDateAsString(entity));

        return sitemapUrlData;
    }

    /**
     * Build a language independent URL for the specified entity from the default language URL.
     *
     * @param entity          the source entity
     * @param defaultLanguage the default language used for the CMS site
     * @return a language independent URL for the specified entity
     */
    protected String getCanonicalURL(final T entity, final String defaultLanguage) {
        String url = resolveURL(entity, defaultLanguage);
        if (StringUtils.isNotBlank(url) && url.contains(SLASH_CHAR + defaultLanguage + SLASH_CHAR)) {
            final int pos = url.indexOf(SLASH_CHAR + defaultLanguage + SLASH_CHAR);
            return new StringBuilder(url).delete(pos, pos + 3).toString();
        }
        return url;
    }

    /**
     * Return a list of {@code WebSitemapLink} built from the {@code map<hreflang,URL>}
     *
     * @param urls the {@code map<hreflang,URL>} map.
     * @return a list of {@code WebSitemapLink}
     * @see #setWebSitemapLinks(Map, List)
     */
    protected List<WebSitemapLink> getWebSitemapLinks(final Map<String, String> urls) {
        final List<WebSitemapLink> links = new ArrayList<WebSitemapLink>();
        setWebSitemapLinks(urls, links);
        return links;
    }

    /**
     * Create {@code WebSitemapLink}s from using the (key, value) pairs from the map which represent the language and URL and add them to
     * the destination list.
     *
     * @param urls  the source key/value pairs (language/URL)
     * @param links the destination list.
     */
    protected void setWebSitemapLinks(final Map<String, String> urls, final List<WebSitemapLink> links) {
        for (final String key : urls.keySet()) {
            final WebSitemapLink link = new WebSitemapLink();
            link.setHref(urls.get(key));
            link.setRel(DEFAULT_LINK_REL);
            link.setHreflang(key);
            link.setLanguage(key);
            links.add(link);
        }
    }

    /**
     * Fetch the list of entities from the database using the {@code FlexibleSearchQuery}
     *
     * @return list of entities from the database
     */
    protected List<T> getEntities() {
        if (this.flexibleSearchQuery == null) {
            initQuery();
        }

        return getFlexibleSearchService().<T>search(this.flexibleSearchQuery).getResult();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.WebSitemapUrlsProvider#setBlackList(java.util.Collection)
     */
    @Override
    public void setBlackList(final Collection<String> blackList) {
        this.blackList = blackList;
    }

    protected boolean isBlackListed(final String entityCode) {
        return this.blackList != null && this.blackList.contains(entityCode);
    }

    /**
     * Retrieve the entity unique identifier
     *
     * @param entity the source entity
     * @return the unique identifier of the specified entity
     */
    protected abstract String getEntityCode(final T entity);

    /**
     * Initialize the {@code flexibleSearchQuery}
     */
    protected abstract void initQuery();

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.WebSitemapUrlsProvider#init()
     */
    @Override
    public void init() {
        initQuery();
        initExcludePattern();
    }

    /**
     * Initialize the exclusion pattern matcher
     */
    protected void initExcludePattern() {
        // NOP
    }

    protected String getQuerySuffix() {
        return " AND {" + T.PK + "} > " + getLastPk() + " ORDER BY {" + T.PK + "}";
    }

    /**
     * Tells whether we should or not exclude the specified URL from the sitemap.
     *
     * @param url the URL to check.
     * @return {@code true} if we should exclude the specified URL from the sitemap, else {@code false}.
     */
    protected boolean exclude(final String url) {
        return EXCLUDE_PATTERN_MATCHER.reset(url).matches();
    }

    /**
     * Retrieve the last ERP modified date of the specified entity.
     *
     * @param entity the source entity
     * @return the last modified ERP date as string.
     */
    protected String getLastModifiedDateAsString(final T entity) {
        return DATE_FORMAT.format(getLastModifiedDate(entity));
    }

    /**
     * Retrieve the last ERP modified date of the specified entity.
     *
     * @param entity the source entity
     * @return the last modified ERP date.
     */
    protected Date getLastModifiedDate(final T entity) {
        return null;
    }

    /**
     * Resolve the URL of the {@code entity} for the specified language
     *
     * @param entity   the {@code entity} to resolve
     * @param language the used language
     * @return the {@code URL} of the entity.
     */
    protected String resolveURL(final T entity, final String language) {
        return getCmsSiteUrlPrefix() + getDistUrlResolver().resolve(entity, getCmsSiteModel(), language);
    }

    /**
     * Retrieve the list of supported languages by the CMS site.
     *
     * @return a set of alternative languages supported by the CMS site
     */
    @Override
    public Set<LanguageModel> getAlternativeLanguages() {
        if (this.alternativeLanguages == null) {
            this.alternativeLanguages = new HashSet<LanguageModel>();

            if (CollectionUtils.isNotEmpty(getCmsSiteModel().getStores())) {
                final Collection<LanguageModel> languages = getCmsSiteModel().getStores().get(0).getLanguages();
                if (CollectionUtils.isNotEmpty(languages)) {
                    this.alternativeLanguages.addAll(languages);
                }
            }

            if (CollectionUtils.isEmpty(this.alternativeLanguages)) {
                this.alternativeLanguages.add(getCmsSiteModel().getDefaultLanguage());
            }
        }

        return alternativeLanguages;
    }

    /**
     * {@code CustomIterator}
     * <p>
     * Custom implementation of the {@code Iterator} interface which calls the {@link #remove()} method of the source iterator each time the
     * {@link #next()} method is called. The goal of this implementation is to reduce the memory footprint caused by huge lists.
     * </p>
     *
     * @param <T>
     * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
     * @since Distrelec 3.4
     */
    private static class CustomIterator<T> implements Iterator<T> {

        private Iterator<T> src;

        /**
         * Create a new instance of {@code CustomIterator}
         *
         * @param src
         */
        public CustomIterator(final Iterator<T> src) {
            this.src = src;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            return src.hasNext();
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#next()
         */
        @Override
        public T next() {
            final T next = src.next();
            src.remove();
            return next;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            // NOP: the remove operation is called each time we call the next method
        }
    }

    // Getters and Setters

    public DistUrlResolver<T> getDistUrlResolver() {
        return distUrlResolver;
    }

    public void setDistUrlResolver(final DistUrlResolver<T> urlResolver) {
        this.distUrlResolver = urlResolver;
    }

    public FlexibleSearchQuery getFlexibleSearchQuery() {
        return flexibleSearchQuery;
    }

    public void setFlexibleSearchQuery(final FlexibleSearchQuery flexibleSearchQuery) {
        this.flexibleSearchQuery = flexibleSearchQuery;
    }

    public CMSSiteModel getCmsSiteModel() {
        return cmsSiteModel;
    }

    public void setCmsSiteModel(final CMSSiteModel cmsSiteModel) {
        this.cmsSiteModel = cmsSiteModel;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }


    // DISTRELEC-11427 : Please make all instances of google sitemaps to be fully encrypted, they'll need to be passed through https
    public String getCmsSiteUrlPrefix() {
        if (this.cmsSiteUrlPrefix == null) {
            this.cmsSiteUrlPrefix = getConfigurationService().getConfiguration()
                    .getString("website." + cmsSiteModel.getUid() + (cmsSiteModel.isHttpsOnly() ? ".https" : ".http"));
        }
        return cmsSiteUrlPrefix;
    }

    public void setCmsSiteUrlPrefix(final String cmsSiteUrlPrefix) {
        this.cmsSiteUrlPrefix = cmsSiteUrlPrefix;
    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public long getLastPk() {
        return lastPk;
    }

    public int getMaxEntitiesPerQuery() {
        return MAX_ENTITIES_PER_QUERY;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }


    public BaseSiteModel getBaseSiteModel() {
        return baseSiteModel;
    }

    public void setBaseSiteModel(BaseSiteModel baseSiteModel) {
        this.baseSiteModel = baseSiteModel;
    }

    protected void initQueryNew() {
        // YTODO Auto-generated method stub

    }
}
