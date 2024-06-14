/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.impl;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.sitemap.EntityNames;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.WebSitemapLink;
import com.namics.distrelec.b2b.core.service.sitemap.sitemapgenerator.WebSitemapUrl;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ErpSalesStatus.ATTRIBUTE_SITEMAP_END_OF_LIFE;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Punctuation.COMMA;
import static com.namics.distrelec.b2b.core.service.sitemap.SitemapConstants.DEFAULT_HREF_LANG;

/**
 * {@code DistProductWebSitemapUrlsProvider}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistProductWebSitemapUrlsProvider extends DistWebSitemapUrlsProvider<ProductModel> {

    private static final String REGEXP_SUFFIX = "/(([a-z]{2}|-)/)?p/[0-9]+";
    private static final String SITEMAP_QUERY_IMPROVED ="sitemap.export.improved.query";

    private static final int PK_POS = 0;
    private static final int CODE_POS = 1;
    private static final int LAST_MOD_DATE_POS = 2;
    private static final int CANONICAL_URL_POS = 3;


    @Autowired
    private DistSqlUtils distSqlUtils;

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#addWebSitemapUrlsForWebsite(java.util.Collection)
     */
    @Override
    public boolean addWebSitemapUrlsForWebsite(final Collection<WebSitemapUrl> dest) {
        if (dest == null) {
            return true;
        }
        boolean result = true;


        if(getConfigurationService().getConfiguration().getBoolean(SITEMAP_QUERY_IMPROVED,false)) {
         // Retrieve the entities from the database.
            final List<ProductModel> rows = getListEntitiesNew();
            result = addWebsitemapNew(rows, dest);
        }else {
         // Retrieve the entities from the database.
            final List<List<Object>> rows = getListEntities();
            result =  addWebsitemapOld(rows, dest);
        }


        LOG.info(
                "\n{}: End of URLs export. \n\tNumber of processed entities: {}\n\tignored entities: {}\n\tnumber of generated URLs: {}\n\tnumber of excluded URLs: {}",
                new String[] { getClass().getSimpleName(), String.valueOf(processed), String.valueOf(ignored), String.valueOf(dest.size()),
                        String.valueOf(excluded) });

        return result;
    }


    private boolean addWebsitemapOld(final List<List<Object>> rows,final Collection<WebSitemapUrl> dest) {

        if (CollectionUtils.isNotEmpty(rows)) {
            // Create temporarily map for the different URLs
            final Map<String, String> urlsMap = new HashMap<String, String>();
            final int exclusionIncrement = getAlternativeLanguages().size() + 1;

            for (final List<Object> row : rows) {
                lastPk = ((Long) row.get(PK_POS)).longValue();
                processed++;

                final String entityCode = (String) row.get(CODE_POS);
                final String canonicalURL = getCmsSiteUrlPrefix() + (String) row.get(CANONICAL_URL_POS);

                if (isBlackListed(entityCode) || exclude(canonicalURL)) {
                    // Skip the blacklisted entities and the entities with canonical URL that should be excluded
                    excluded += exclusionIncrement;
                    ignored++;
                    continue;
                }

                urlsMap.clear();
                urlsMap.put(DEFAULT_HREF_LANG, canonicalURL);
                String lang_url = null;
                for (int i = (CANONICAL_URL_POS + 1); i < row.size(); i++) {
                    try {
                    lang_url = (String) row.get(i);
                    final String url = getCmsSiteUrlPrefix() + UrlResolverUtils.normalize(lang_url);
                    if (!exclude(url)) {
                        urlsMap.put(lang_url.substring(1, 3), url);
                    } else {
                        excluded++;
                        LOG.warn("{}: Excluding URL '{}'", new String[]{getClass().getSimpleName(), url});
                    }
                    }catch(Exception ex) {
                        LOG.error("Error while building sitemap :", ex);
                    }
                }

                final Date lastModDate = (Date) row.get(LAST_MOD_DATE_POS);

                // Create a list for web sitemap links
                final List<WebSitemapLink> links = new ArrayList<WebSitemapLink>();
                setWebSitemapLinks(urlsMap, links);

                try {
                    dest.add(new WebSitemapUrl.Options(urlsMap.get(DEFAULT_HREF_LANG)) //
                            .code(entityCode) //
                            .entity(getEntityName()) //
                            .links(links) //
                            .lastMod(lastModDate).build());
                } catch (final MalformedURLException e) {
                    LOG.warn("Unable to build web sitemap URL from " + urlsMap.get(DEFAULT_HREF_LANG) + " ==> " + e.getMessage(), e);
                }


                // Check whether we reached the max entities per query
                if (processed % getMaxEntitiesPerQuery() == 0) {
                    return false;
                }
            }
        }

        return true;
    }
    private boolean addWebsitemapNew(final List<ProductModel> rows,final Collection<WebSitemapUrl> dest) {

        if (CollectionUtils.isNotEmpty(rows)) {
            // Create temporarily map for the different URLs
            final Map<String, String> urlsMap = new HashMap<String, String>();
            final int exclusionIncrement = getAlternativeLanguages().size() + 1;

            for (final ProductModel product : rows) {
                lastPk =  product.getPk().getLong();
                processed++;

                final String entityCode =  product.getCode();
                final String canonicalURL = getCmsSiteUrlPrefix() +UrlResolverUtils.normalize(getDistUrlResolver().resolve(product, getBaseSiteModel(),Locale.ENGLISH.getLanguage(),true));

                if (isBlackListed(entityCode) || exclude(canonicalURL)) {
                    // Skip the blacklisted entities and the entities with canonical URL that should be excluded
                    excluded += exclusionIncrement;
                    ignored++;
                    continue;
                }

                urlsMap.clear();
                urlsMap.put(DEFAULT_HREF_LANG, canonicalURL);
                String lang_url = null;
                final Set<LanguageModel> languages = getAlternativeLanguages();

                for (final LanguageModel lang : languages) {
                    lang_url = UrlResolverUtils.normalize(getDistUrlResolver().resolve(product,getBaseSiteModel(),lang.getIsocode()));
                    final String url = getCmsSiteUrlPrefix() + UrlResolverUtils.normalize(lang_url);
                    if (!exclude(url)) {
                        urlsMap.put(lang.getIsocode(), url);
                    } else {
                        excluded++;
                        LOG.warn("{}: Excluding URL '{}'", new String[] { getClass().getSimpleName(), url });
                    }
                }

                final Date lastModDate = product.getLastModifiedErp();

                // Create a list for web sitemap links
                final List<WebSitemapLink> links = new ArrayList<WebSitemapLink>();
                setWebSitemapLinks(urlsMap, links);

                try {
                    dest.add(new WebSitemapUrl.Options(urlsMap.get(DEFAULT_HREF_LANG)) //
                            .code(entityCode) //
                            .entity(getEntityName()) //
                            .links(links) //
                            .lastMod(lastModDate).build());
                } catch (final MalformedURLException e) {
                    LOG.warn("Unable to build web sitemap URL from " + urlsMap.get(DEFAULT_HREF_LANG) + " ==> " + e.getMessage(), e);
                }


                // Check whether we reached the max entities per query
                if (processed % getMaxEntitiesPerQuery() == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#getEntityCode(de.hybris.platform.core.model.ItemModel)
     */
    @Override
    protected String getEntityCode(final ProductModel entity) {
        return entity.getCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#getEntities()
     */
    @Override
    protected List<ProductModel> getEntities() {
        throw new UnsupportedOperationException("Not supported operation for type: " + ProductModel.class.getName());
    }

    /**
     * Execute the flexible search query and return data as list of object lists. The form of a row of data is: {pk}, {code},
     * {lastModifiedErp}, {canonicalURL}, {language dependent URLs}. where:
     * <ul>
     * <li>Canonical URL: /{product-name}-{manufacturer-name}-{product-type}/p/{product-code}.</li>
     * <li>Language dependent URL: /language/{product-name}-{manufacturer-name}-{product-type}/p/{product-code}.</li>
     * </ul>
     *
     * @return a list of object lists
     */
    protected List<List<Object>> getListEntities() {

        initQuery();

        return getFlexibleSearchService().<List<Object>>search(getFlexibleSearchQuery()).getResult();
    }
    protected List<ProductModel> getListEntitiesNew() {
           
         initQueryNew();

        return getFlexibleSearchService().<ProductModel> search(getFlexibleSearchQuery()).getResult();
    }
    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#getLastModifiedDate(de.hybris.platform.core.model.
     * ItemModel )
     */
    @Override
    protected Date getLastModifiedDate(final ProductModel entity) {
        return entity.getLastModifiedErp();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.WebSitemapUrlsProvider#getEntityName()
     */
    @Override
    public String getEntityName() {
        return EntityNames.PRODUCT.name();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#initQuery()
     */
    @Override
    protected void initQueryNew() {
        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT {p." + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + " AS p } ");
        queryBuilder.append("WHERE {p."+ ProductModel.PIMID + "} IS NOT NULL ");
        queryBuilder.append("AND {p." + ProductModel.CODE + "} IS NOT NULL ");
        queryBuilder.append("AND ({p." + ProductModel.EXCLUDE + "} IS NULL OR {p." + ProductModel.EXCLUDE + "} != 1) ");
        queryBuilder.append("AND EXISTS ( {{ SELECT 1 FROM {" + DistSalesOrgProductModel._TYPECODE + " AS so} WHERE {so:product} = {p:pk} AND {so:salesOrg} = ?salesOrg ");
        queryBuilder.append("AND EXISTS ( {{ SELECT 1 from {" + DistSalesStatusModel._TYPECODE + " AS st} WHERE {so:salesStatus} = {st:pk} AND {st:code} NOT IN (?eolStatusCodes) AND {st:buyableInShop} = 1 }} ) }} ) ");
        queryBuilder.append("AND EXISTS ( {{ SELECT 1 from {" + CatalogVersionModel._TYPECODE + " AS cv} WHERE {p:catalogVersion} = {cv:pk} AND {cv:version} = 'Online' ");
        queryBuilder.append("AND EXISTS ( {{ SELECT 1 from {" + CatalogModel._TYPECODE + " AS c} WHERE {cv:catalog} = {c:pk} AND {c:id} = 'distrelecProductCatalog' }} ) }} ) ");

        // Append the query suffix
        queryBuilder.append(getQuerySuffix());
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryBuilder.toString());
        searchQuery.addQueryParameter("salesOrg", getCmsSiteModel().getSalesOrg());
        searchQuery.addQueryParameter("eolStatusCodes", getEolStatusCodes());
        setNewResultClassList(searchQuery, 1);
        setFlexibleSearchQuery(searchQuery);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#initQuery()
     */
    @Override
    protected void initQuery() {
        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT {p." + ProductModel.PK + "}");
        queryBuilder.append(", {p." + ProductModel.CODE + "}");
        queryBuilder.append(", {" + ProductModel.LASTMODIFIEDERP + "}");

        addLangSelect(getCmsSiteModel().getDefaultLanguage().getIsocode(), queryBuilder, true);

        final Set<LanguageModel> languages = getAlternativeLanguages();
        for (final LanguageModel lang : languages) {
            addLangSelect(lang.getIsocode(), queryBuilder, false);
        }
        queryBuilder.append("FROM {" + ProductModel._TYPECODE + " AS p ");
        queryBuilder.append("JOIN " + DistSalesOrgProductModel._TYPECODE + " AS so ON {so:product} = {p:pk} ");
        queryBuilder.append("JOIN " + DistSalesStatusModel._TYPECODE + " AS st ON {so:salesStatus} = {st:pk} ");
        queryBuilder.append("JOIN " + CatalogVersionModel._TYPECODE + " AS cv ON {p:catalogVersion} = {cv:pk} ");
        queryBuilder.append("JOIN " + CatalogModel._TYPECODE + " AS c ON {cv:catalog} = {c:pk} ");
        queryBuilder.append("LEFT JOIN " + DistManufacturerModel._TYPECODE + " AS m ON {p.manufacturer}={m.pk}} ");

        //blocked = until Erp Sales Status is there.
        queryBuilder.append("WHERE {so:salesOrg} = ?salesOrg AND {st:code} NOT IN (?eolStatusCodes) AND {st:buyableInShop} = 1 AND {c:id} = 'distrelecProductCatalog' AND {cv:version} = 'Online'");

        // Append the query suffix
        queryBuilder.append(getQuerySuffix());
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(queryBuilder.toString());
        searchQuery.addQueryParameter("salesOrg", getCmsSiteModel().getSalesOrg());
        searchQuery.addQueryParameter("eolStatusCodes", getEolStatusCodes());
        setResultClassList(searchQuery, languages.size() + 1);
        setFlexibleSearchQuery(searchQuery);
    }

    private List<String> getEolStatusCodes() {
        final String eolCodes = getConfigurationService().getConfiguration().getString(ATTRIBUTE_SITEMAP_END_OF_LIFE);
        return StringUtils.isEmpty(eolCodes) ? new ArrayList<>() : Stream.of(eolCodes.split(COMMA)).collect(Collectors.toList());
    }

    private void setResultClassList(final FlexibleSearchQuery searchQuery, final int lang_count) {
        final List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        resultClassList.add(Long.class);
        resultClassList.add(String.class);
        resultClassList.add(Date.class);
        for (int i = 0; i < lang_count; i++) {
            resultClassList.add(String.class);
        }

        searchQuery.setResultClassList(resultClassList);
    }

    private void setNewResultClassList(final FlexibleSearchQuery searchQuery, final int lang_count) {
        final List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        resultClassList.add(ProductModel.class);


        searchQuery.setResultClassList(resultClassList);
    }

    /**
     * Concatenate the product relative URL to the selection part.
     *
     * @param lang         the language used for the URL.
     * @param queryBuilder the query builder
     * @param canonical    when {@code true} it does not concatenate the language to the URL.
     */
    private void addLangSelect(final String lang, final StringBuilder queryBuilder, final boolean canonical) {
        /* /{language}/{product-name}-{manufacturer-name}-{product-type}/p/{product-code} */

        final String nameSeo = "{p.nameSeo[" + lang + "]}";
        queryBuilder.append(", ");
        queryBuilder.append(distSqlUtils.concat(canonical ? "'/'" : ("'/" + lang + "/'"),
                "CASE WHEN ".concat(nameSeo).concat(" IS NULL THEN '' ELSE ").concat(nameSeo).concat(" END"),
                "CASE WHEN {m.nameSeo} IS NULL THEN '' ELSE '-' || {m.nameSeo} END",
                "CASE WHEN {p.typeNameSeo} IS NULL THEN '' ELSE '-' || {p.typeNameSeo} END",
                "'/p/'", "{p.code}"));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#getQuerySuffix()
     */
    @Override
    protected String getQuerySuffix() {
        return " AND {p." + ProductModel.PK + "} > " + getLastPk() + " ORDER BY {p." + ProductModel.PK + "}";
    }

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#initExcludePattern()
     */
    @Override
    protected void initExcludePattern() {
        EXCLUDE_PATTERN_MATCHER = Pattern.compile(getCmsSiteUrlPrefix() + REGEXP_SUFFIX).matcher("");
    }

    @Override
    public int getMaxEntitiesPerQuery() {
        return MAX_ENTITIES_PER_QUERY / 2;
    }



}
