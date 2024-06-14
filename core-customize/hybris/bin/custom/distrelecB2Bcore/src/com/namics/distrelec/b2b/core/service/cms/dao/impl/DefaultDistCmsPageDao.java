/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.cms.dao.impl;

import com.namics.distrelec.b2b.core.enums.UrlMatchExpression;
import com.namics.distrelec.b2b.core.model.DistContentPageMappingModel;
import com.namics.distrelec.b2b.core.model.cms.ProductFamilyPageModel;
import com.namics.distrelec.b2b.core.service.cms.dao.DistCmsPageDao;
import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.impl.DefaultCMSPageDao;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.fest.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.namics.distrelec.b2b.core.service.category.dao.impl.DefaultDistCategoryDao.FAMILY_TYPE_CODE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

/**
 * DefaultDistCmsPageDao.
 *
 * @author dsivakumaran, Namics AG
 * @since Distrelec 1.0
 */
public class DefaultDistCmsPageDao extends DefaultCMSPageDao implements DistCmsPageDao {

    private static final Logger LOG = Logger.getLogger(DefaultDistCmsPageDao.class);

    private static final String PRODUCT_FAMILY_PAGE_SPECIFIC_QUERY = "SELECT {p.pk} FROM " +
            "{ProductFamilyPage as p " +
            "JOIN ProductFamilyPage2Category AS r ON {p.pk} = {r.source} " +
            "JOIN Category AS c ON {r.target} = {c.pk} " +
            "JOIN DistPimCategoryType AS t ON {c.pimCategoryType} = {t.pk}} " +
            "WHERE {c.code} = ?code AND {t.code} = ?type AND {p.catalogVersion} IN  (?catalogVersions) " +
            "ORDER BY {p.creationtime}";

    private static final String PRODUCT_FAMILY_PAGE_DEFAULT_QUERY = "SELECT {p.pk} FROM " +
            "{ProductFamilyPage as p} " +
            "WHERE {p.defaultFamilyPage} = 1 AND {p.catalogVersion} IN (?catalogVersions) " +
            "ORDER BY {p.creationtime}";
    public static final String INTERNATIONAL_SITE = "distrelec";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private DistSqlUtils distSqlUtils;
    
    private String getContentPageMappingsQuery() {
        return "SELECT {cpm.pk} FROM " +
               "{DistContentPageMapping as cpm} " +
               "WHERE {cpm.active} = " + distSqlUtils.booleanToTinyint(TRUE) + " " +
               "AND ( {cpm.validForSite} IS NULL " +
               "OR {cpm.validForSite} = ?cmsSite )";
    }

    @Override
    public DistContentPageMappingModel findContentPageMapping(final String shortURL, final CMSSiteModel cmsSite, final Boolean active) {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {m.").append(DistContentPageMappingModel.PK).append("}");
        query.append(" FROM {").append(DistContentPageMappingModel._TYPECODE).append(" AS m}");
        query.append(" WHERE {m.").append(DistContentPageMappingModel.ACTIVE).append("}=(?").append(DistContentPageMappingModel.ACTIVE).append(")");

        if (cmsSite != null) {
            query.append("   AND {m.").append(DistContentPageMappingModel.VALIDFORSITE).append("}=(?").append(DistContentPageMappingModel.VALIDFORSITE).append(")");
        } else {
            query.append("   AND {m.").append(DistContentPageMappingModel.VALIDFORSITE).append("} is null");
        }

        query.append(" AND (");
        appendUrlExpression(query, UrlMatchExpression.EQUALS, "{m.".concat(DistContentPageMappingModel.SHORTURL).concat("}=(?").concat(DistContentPageMappingModel.SHORTURL).concat(")"));
        query.append(" OR ");
        appendUrlExpression(query, UrlMatchExpression.ENDSWITH, "(?".concat(DistContentPageMappingModel.SHORTURL).concat(") like ").concat(distSqlUtils.concat("'%'", "{m.".concat(DistContentPageMappingModel.SHORTURL).concat("}"))));
        query.append(")");

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put(DistContentPageMappingModel.ACTIVE, active);
        if (cmsSite != null) {
            params.put(DistContentPageMappingModel.VALIDFORSITE, cmsSite);
        }
        params.put(DistContentPageMappingModel.SHORTURL, shortURL);

        final SearchResult<DistContentPageMappingModel> result = getFlexibleSearchService().search(query.toString(), params);
        final List<DistContentPageMappingModel> mappings = result.getResult();
        if (CollectionUtils.isNotEmpty(mappings)) {
            if (CollectionUtils.size(mappings) > 1) {
                String errMsg = "Found multiple content page mappings for short URL " + shortURL;
                if (cmsSite != null) {
                    errMsg += " and site " + cmsSite.getUid();
                } else {
                    errMsg += " and undefined site";
                }
                errMsg += ". Returning first entry.";
                LOG.warn(errMsg);
            }
            return mappings.iterator().next();
        }
        return null;
    }

    @Override
    public DistContentPageMappingModel findActiveContentPageMapping(final String shortURL, final CMSSiteModel cmsSite) {
        DistContentPageMappingModel cmsSiteMapping = findContentPageMapping(shortURL, cmsSite, TRUE);
        if (cmsSiteMapping != null) {
            return cmsSiteMapping;
        } else {
            // try find mapping for undefined cms site
            return findContentPageMapping(shortURL, null, TRUE);
        }
    }

    @Override
    public Collection<DistContentPageMappingModel> findAllActiveContentPageMappings(CMSSiteModel cmsSite) {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(getContentPageMappingsQuery());
        flexibleSearchQuery.addQueryParameters(Map.of("cmsSite", cmsSite));
        flexibleSearchQuery.setResultClassList(singletonList(DistContentPageMappingModel.class));
        SearchResult<DistContentPageMappingModel> result = search(flexibleSearchQuery);
        return result.getResult();
    }

    @Override
    public <T extends AbstractPageModel> Collection<T> findPagesByLabelsAndPageStatuses(Collection<String> labels, Collection<CatalogVersionModel> catalogVersions, List<CmsPageStatus> pageStatuses) {
        Collection<T> allSitesPages = super.findPagesByLabelsAndPageStatuses(labels, catalogVersions, pageStatuses);
        return filterPagesAvailableToCurrentSite(allSitesPages);
    }

    @Override
    public Collection<AbstractPageModel> findAllPagesByTypeAndCatalogVersionsAndPageStatuses(ComposedTypeModel composedType, Collection<CatalogVersionModel> catalogVersions, List<CmsPageStatus> pageStatuses) {
        Collection<AbstractPageModel> allSitesPages = super.findAllPagesByTypeAndCatalogVersionsAndPageStatuses(composedType, catalogVersions, pageStatuses);
        return filterPagesAvailableToCurrentSite(allSitesPages);
    }

    @Override
    public Optional<ProductFamilyPageModel> findProductFamilySpecificPage(String productFamilyCode, Collection<CatalogVersionModel> catalogVersions) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("code", productFamilyCode);
        parameters.put("type", FAMILY_TYPE_CODE);
        parameters.put("catalogVersions", catalogVersions);
        return findProductFamilyPage(PRODUCT_FAMILY_PAGE_SPECIFIC_QUERY, parameters);
    }

    @Override
    public Optional<ProductFamilyPageModel> findDefaultProductFamilyPage(Collection<CatalogVersionModel> catalogVersions) {
        return findProductFamilyPage(PRODUCT_FAMILY_PAGE_DEFAULT_QUERY, singletonMap("catalogVersions", catalogVersions));
    }

    protected StringBuilder appendUrlExpression(final StringBuilder query, final UrlMatchExpression expr, final String condition) {
        query.append("(EXISTS({{select 1");
        query.append("   FROM {").append(UrlMatchExpression._TYPECODE).append(" AS expr}");
        query.append("   WHERE {expr.pk}={m.").append(DistContentPageMappingModel.URLMATCHEXPRESSION).append("}");
        query.append("     AND {expr.code}='").append(expr).append("'}})");
        query.append(" AND ").append(condition).append(")");
        return query;
    }

    private Optional<ProductFamilyPageModel> findProductFamilyPage(String query, Map<String, Object> parameters) {
        FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query);
        flexibleSearchQuery.addQueryParameters(parameters);
        flexibleSearchQuery.setResultClassList(singletonList(ProductFamilyPageModel.class));
        SearchResult<ProductFamilyPageModel> result = search(flexibleSearchQuery);
        return Optional.ofNullable(result)
                       .map(SearchResult::getResult)
                       .map(this::filterPagesAvailableToCurrentSite)
                       .map(Collection::stream)
                       .orElse(Stream.empty())
                       .filter(p -> CmsPageStatus.ACTIVE.equals(p.getPageStatus()))
                       .findFirst();
    }

    private <T extends AbstractPageModel> Collection<T> filterPagesAvailableToCurrentSite(Collection<T> allSitesPages) {
        CMSSiteModel currentSite = cmsSiteService.getCurrentSite();

        if (currentSite != null) {
            if(currentSite.getUid().equals(INTERNATIONAL_SITE)){
                return allSitesPages;
            }

            return allSitesPages.stream()
                    .filter(page -> Collections.isEmpty(page.getDisplayOnSites()) || page.getDisplayOnSites().contains(cmsSiteService.getCurrentSite()))
                    .collect(Collectors.toList());
        } else {
            return allSitesPages;
        }
    }
}
