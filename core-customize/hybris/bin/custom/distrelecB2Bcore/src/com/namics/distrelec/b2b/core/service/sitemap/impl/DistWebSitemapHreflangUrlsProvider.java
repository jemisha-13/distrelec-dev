package com.namics.distrelec.b2b.core.service.sitemap.impl;

import static com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants.DEFAULT_HREF_LANG;
import static com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants.DEFAULT_LINK_REL;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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

public abstract class DistWebSitemapHreflangUrlsProvider<T extends ItemModel> implements WebSitemapUrlsProvider {

    protected static final Logger LOG = LoggerFactory.getLogger(DistWebSitemapHreflangUrlsProvider.class);

    // DISTRELEC-8787 skip TR, DISTRELEC-20157 skip international site
    private static final Set<String> IGNORABLE_CMS_SITES = Set.of("distrelec_TR", "distrelec");

    @Autowired
    private ConfigurationService configurationService;

    private DistUrlResolver<T> distUrlResolver;

    private FlexibleSearchQuery flexibleSearchQuery;

    private CMSSiteModel cmsSiteModel;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private ModelService modelService;

    private final Map<CMSSiteModel, Set<LanguageModel>> alternativeLanguages = new HashMap<>();

    private final Map<CMSSiteModel, String> cmsSiteUrlPrefix = new HashMap<>();

    private Collection<String> blackList;

    private BaseSiteModel baseSiteModel;

    protected int excluded = 0;

    protected int processed = 0;

    protected int ignored = 0;

    public DistWebSitemapHreflangUrlsProvider() {
        super();
    }

    protected Map<T, List<CMSSiteModel>> getEntitiesAndSites() {
        long queryTime = System.currentTimeMillis();
        List<T> results=getFlexibleSearchService().<T> search(getFlexibleSearchQuery()).getResult();
        LOG.info("Querying took: {}ms", System.currentTimeMillis() - queryTime);

        long collectingTime = System.currentTimeMillis();
        Map<T, List<CMSSiteModel>> map = new HashMap<>();
        for (T entity :results) {
            List<CMSSiteModel> cmsSites = getCmsSites(entity).stream()
                                                             .filter(cmsSite -> !IGNORABLE_CMS_SITES.contains(cmsSite.getUid()))
                                                             .collect(Collectors.toList());
            map.put(entity, cmsSites);
        }
        LOG.info("Collecting entities and cms sites took: {}ms", System.currentTimeMillis() - collectingTime);

        return map;
    }

    protected String getLocalizedLanguageIsocode(CMSSiteModel cmsSite, LanguageModel lang) {
        return cmsSite.getUid().equalsIgnoreCase("distrelec_EX") ? lang.getIsocode() : lang.getIsocode() + "-" + cmsSite.getCountry().getIsocode();
    }

    protected abstract List<CMSSiteModel> getCmsSites(T entity);

    @Override
    public Collection<WebSitemapUrl> getWebSitemapUrlsForWebsite() {
        // not supported
        return null;
    }

    @Override
    public Map<LanguageModel, Collection<WebSitemapUrl>> getLanguageWebSitemapUrlsForWebsite() {
        Map<LanguageModel, Collection<WebSitemapUrl>> sitemapUrlsMap = prepareUrlsMap();

        initQuery();

        final Map<T, List<CMSSiteModel>> entitiesWithSites = getEntitiesAndSites();

        if (MapUtils.isNotEmpty(entitiesWithSites)) {
            LOG.info("Retrieved {} entities.", entitiesWithSites.size());
            // Processing entities
            long processingTime = System.currentTimeMillis();
            
            for (T entity : entitiesWithSites.keySet()) {
                processed++;
                final String defaultURL = resolveURL(entity, getCmsSiteModel(), getCmsSiteModel().getDefaultLanguage().getIsocode());
                final String entityCode = getEntityCode(entity);

                if (isBlackListed(entityCode) || exclude(defaultURL, getCmsSiteModel())) {
                    // Skip the blacklisted entities and the entities with canonical URL that should be excluded
                    ignored++;
                    continue;
                }

                final Date lastModDate = getLastModifiedDate(entity);

                final Map<String, String> urlsMap = new HashMap<>();
                urlsMap.put(DEFAULT_HREF_LANG, defaultURL);

                for (final CMSSiteModel cmsSite : entitiesWithSites.get(entity)) {
                    for (final LanguageModel lang : getAlternativeLanguages(cmsSite)) {
                        final String url = resolveURL(entity, cmsSite, lang.getIsocode());
                        if (!exclude(url, cmsSite) && !url.startsWith("/")) {
                            urlsMap.put(getLocalizedLanguageIsocode(cmsSite, lang), url);
                        } else {
                            excluded++;
                        }
                    }
                }

                // Create a list for web sitemap links
                for (LanguageModel language : getAlternativeLanguages()) {
                    final List<WebSitemapLink> links = getWebSitemapLinks(urlsMap);
                    String loc = resolveURL(entity, getCmsSiteModel(), language.getIsocode());
                    try {
                        sitemapUrlsMap.get(language).add(new WebSitemapUrl.Options(loc)
                                                                                       .code(entityCode) //
                                                                                       .entity(getEntityName()) //
                                                                                       .links(links) //
                                                                                       .lastMod(lastModDate).build());
                    } catch (MalformedURLException e) {
                        LOG.warn("Unable to build web sitemap URL from " + loc + " ==> " + e.getMessage(), e);
                    }
                }

                if (processed % 20000 == 0) {
                    LOG.info(
                             "\n{}: Intermediate statistics. \n\tNumber of processed entities: {}\n\tignored entities: {}\n\tnumber of generated URLs: {}\n\tnumber of excluded URLs: {}\n\tnumber of remaining entities: {}\n\tprocessing time: {}ms",
                             getClass().getSimpleName(), processed, ignored, sitemapUrlsMap.get(getCmsSiteModel().getDefaultLanguage()).size(), excluded,
                             entitiesWithSites.size() - processed, System.currentTimeMillis() - processingTime);
                    processingTime = System.currentTimeMillis();
                }
            }
        }

        LOG.info(
                 "{}: End of URLs export. \n\tNumber of processed entities: {}\n\tignored entities: {}\n\tnumber of generated URLs: {}\n\tnumber of excluded alternative URLs: {}",
                 getClass().getSimpleName(), processed, ignored, sitemapUrlsMap.get(getCmsSiteModel().getDefaultLanguage()).size(), excluded);

        return sitemapUrlsMap;
    }

    protected Map<LanguageModel, Collection<WebSitemapUrl>> prepareUrlsMap() {
        Map<LanguageModel, Collection<WebSitemapUrl>> map = new HashMap<>();
        for (LanguageModel language : getAlternativeLanguages()) {
            map.put(language, new ArrayList<>());
        }
        return map;
    }

    /**
     * Return a list of {@code WebSitemapLink} built from the {@code map<hreflang,URL>}
     *
     * @param urls
     *            the {@code map<hreflang,URL>} map.
     * @return a list of {@code WebSitemapLink}
     */
    protected List<WebSitemapLink> getWebSitemapLinks(final Map<String, String> urls) {
        final List<WebSitemapLink> links = new ArrayList<>();
        for (final String key : urls.keySet()) {
            final WebSitemapLink link = new WebSitemapLink();
            link.setHref(urls.get(key));
            link.setRel(DEFAULT_LINK_REL);
            link.setHreflang(key);
            link.setLanguage(key);
            links.add(link);
        }
        return links;
    }

    /*
     * (non-Javadoc)
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
     * @param entity
     *            the source entity
     * @return the unique identifier of the specified entity
     */
    protected abstract String getEntityCode(final T entity);

    /**
     * Initialize the {@code flexibleSearchQuery}
     */
    protected abstract void initQuery();

    @Override
    public void init() {
        // TODO remove
    }

    protected Matcher provideExcludePattern(CMSSiteModel cmsSiteModel) {
        return null;
    }

    /**
     * Tells whether we should or not exclude the specified URL from the sitemap.
     *
     * @param url
     *            the URL to check.
     * @return {@code true} if we should exclude the specified URL from the sitemap, else {@code false}.
     */
    protected boolean exclude(final String url, CMSSiteModel cmsSiteModel) {
        return provideExcludePattern(cmsSiteModel).reset(url).matches();
    }

    /**
     * Retrieve the last ERP modified date of the specified entity.
     *
     * @param entity
     *            the source entity
     * @return the last modified ERP date.
     */
    protected Date getLastModifiedDate(final T entity) {
        return null;
    }

    /**
     * Resolve the URL of the {@code entity} for the specified language
     *
     * @param entity
     *            the {@code entity} to resolve
     * @param language
     *            the used language
     * @return the {@code URL} of the entity.
     */
    protected String resolveURL(final T entity, CMSSiteModel cmsSiteModel, final String language) {
        return getCmsSiteUrlPrefix(cmsSiteModel) + getDistUrlResolver().resolve(entity, cmsSiteModel, language);
    }

    /**
     * Retrieve the list of supported languages by the CMS site.
     *
     * @return a set of alternative languages supported by the CMS site
     */
    @Override
    public Set<LanguageModel> getAlternativeLanguages() {
        return getAlternativeLanguages(getCmsSiteModel());
    }

    protected Set<LanguageModel> getAlternativeLanguages(CMSSiteModel cmsSiteModel) {
        if (!alternativeLanguages.containsKey(cmsSiteModel)) {
            if (CollectionUtils.isNotEmpty(cmsSiteModel.getStores())) {
                Set<LanguageModel> languageSet = cmsSiteModel.getStores().get(0).getLanguages().stream()
                                                             .filter(languageModel -> !languageModel.getIsocode().contains("_"))
                                                             .collect(Collectors.toSet());
                alternativeLanguages.put(cmsSiteModel, languageSet);
            }

            if (CollectionUtils.isEmpty(alternativeLanguages.get(cmsSiteModel))) {
                alternativeLanguages.get(cmsSiteModel).add(cmsSiteModel.getDefaultLanguage());
            }
        }

        return alternativeLanguages.get(cmsSiteModel);
    }

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

    public String getCmsSiteUrlPrefix(CMSSiteModel cmsSiteModel) {
        if (!cmsSiteUrlPrefix.containsKey(cmsSiteModel)) {
            cmsSiteUrlPrefix.put(cmsSiteModel, getConfigurationService().getConfiguration()
                                                                        .getString("website." + cmsSiteModel.getUid()
                                                                                   + (cmsSiteModel.isHttpsOnly() ? ".https" : ".http")));
        }
        return cmsSiteUrlPrefix.get(cmsSiteModel);

    }

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
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

}
