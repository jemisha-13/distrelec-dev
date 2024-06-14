package com.distrelec.smartedit.cmsitems.search;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.cmsitems.service.CMSItemSearchService;
import de.hybris.platform.cms2.cmsitems.service.impl.DefaultFlexibleCMSItemSearchService;
import de.hybris.platform.cms2.data.CMSItemSearchData;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.namedquery.Sort;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.util.ServicesUtil;
import de.hybris.platform.util.FlexibleSearchUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DistrelecFlexibleCMSItemSearchService extends DefaultFlexibleCMSItemSearchService implements CMSItemSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(DistrelecFlexibleCMSItemSearchService.class);

    private String SEARCH_ABSTRACT_PAGE_QUERY = "SELECT q.p_pk FROM\n" +
            "({{\n" +
            "    SELECT {ap.pk} p_pk," +
            "           {ct.code} p_typecode," +
            "           {ap.name} p_name," +
            "           {ap.uid} p_uid," +
            "           '' p_label," +
            "           {ap.itemtype} p_itemtype," +
            "           {ap.modifiedtime} p_modifiedtime," +
            "           {ap.pageStatus} p_pageStatus" +
            "        FROM {AbstractPage AS ap \n" +
            "        JOIN CatalogVersion AS cv ON {ap.catalogVersion}={cv.pk}" +
            "        JOIN ComposedType AS ct ON {ap.itemtype}={ct.pk}} \n" +
            "        WHERE {ap.catalogVersion}=?catalogVersion AND {ct.code} != 'ContentPage' AND {ap.masterTemplate} IS NOT NULL\n" +
            "}}\n" +
            "UNION\n" +
            "{{\n" +
            "    SELECT {cp.pk} p_pk," +
            "           {ct.code} p_typecode," +
            "           {cp.name} p_name," +
            "           {cp.uid} p_uid," +
            "           {cp.label} p_label," +
            "           {cp.itemtype} p_itemtype," +
            "           {cp.modifiedtime} p_modifiedtime," +
            "           {cp.pageStatus} p_pageStatus" +
            "        FROM {ContentPage AS cp\n" +
            "        JOIN CatalogVersion AS cv ON {cp.catalogVersion}={cv.pk}" +
            "        JOIN ComposedType AS ct ON {cp.itemtype}={ct.pk}} \n" +
            "        WHERE {cp.catalogVersion}=?catalogVersion AND {cp.masterTemplate} IS NOT NULL\n" +
            "}}) q";

    private String SEARCH_ABSTRACT_PAGE_FILTERED_QUERY = "SELECT q.p_pk FROM\n" +
            "({{\n" +
            "    SELECT {ap.pk} p_pk," +
            "           {ct.code} p_typecode," +
            "           {ap.name} p_name," +
            "           {ap.uid} p_uid," +
            "           '' p_label," +
            "           {ap.itemtype} p_itemtype," +
            "           {ap.modifiedtime} p_modifiedtime," +
            "           {ap.pageStatus} p_pageStatus" +
            "        FROM {AbstractPage AS ap \n" +
            "        JOIN CatalogVersion AS cv ON {ap.catalogVersion}={cv.pk}" +
            "        JOIN ComposedType AS ct ON {ap.itemtype}={ct.pk}} \n" +
            "        WHERE ({ap.catalogVersion}=?catalogVersion AND {ct.code} != 'ContentPage' AND {ap.masterTemplate} IS NOT NULL) AND (LOWER({ap.uid}) LIKE LOWER(?mask) OR LOWER({ap.name}) LIKE LOWER(?mask))\n" +
            "}}\n" +
            "UNION\n" +
            "{{\n" +
            "    SELECT {cp.pk} p_pk," +
            "           {ct.code} p_typecode," +
            "           {cp.name} p_name," +
            "           {cp.uid} p_uid," +
            "           {cp.label} p_label," +
            "           {cp.itemtype} p_itemtype," +
            "           {cp.modifiedtime} p_modifiedtime," +
            "           {cp.pageStatus} p_pageStatus" +
            "        FROM {ContentPage AS cp\n" +
            "        JOIN CatalogVersion AS cv ON {cp.catalogVersion}={cv.pk}" +
            "        JOIN ComposedType AS ct ON {cp.itemtype}={ct.pk}} \n" +
            "        WHERE ({cp.catalogVersion}=?catalogVersion AND {cp.masterTemplate} IS NOT NULL) AND (LOWER({cp.uid}) LIKE LOWER(?mask) OR LOWER({cp.name}) LIKE LOWER(?mask) OR LOWER({cp.label}) LIKE LOWER(?mask))\n" +
            "}}) q";

    @Override
    public SearchResult<CMSItemModel> findCMSItems(CMSItemSearchData cmsItemSearchData, PageableData pageableData) {
        ServicesUtil.validateParameterNotNull(pageableData, "PageableData object cannot be null.");
        ServicesUtil.validateParameterNotNull(cmsItemSearchData, "CMSItemSearchData object cannot be null.");
        boolean hasMask = StringUtils.isNotBlank(cmsItemSearchData.getMask());
        boolean hasItemSearchParams = MapUtils.isNotEmpty(cmsItemSearchData.getItemSearchParams());
        boolean hasSort = StringUtils.isNotBlank(pageableData.getSort());
        List<ComposedTypeModel> composedTypes = this.getValidComposedTypes(cmsItemSearchData);
        String typeCode = this.getFirstCommonAncestorTypeCode(composedTypes);
        CatalogVersionModel catalogVersionModel = this.getCatalogVersionService().getCatalogVersion(cmsItemSearchData.getCatalogId(), cmsItemSearchData.getCatalogVersion());
        StringBuilder queryBuilder = null;
        if (typeCode.equals(AbstractPageModel._TYPECODE)) {
            queryBuilder = new StringBuilder(SEARCH_ABSTRACT_PAGE_QUERY);
        } else {
            queryBuilder = new StringBuilder(String.format("SELECT {c.pk} FROM { %s AS c JOIN ComposedType AS type ON {c.itemtype}={type.PK} } WHERE {c.catalogVersion}=?catalogVersion", typeCode));
        }
        Map<String, Object> queryParameters = new HashMap();
        queryParameters.put("catalogVersion", catalogVersionModel);
        this.appendTypeExclusions(typeCode, queryBuilder, queryParameters);
        if (hasMask) {
            if (typeCode.equals(AbstractPageModel._TYPECODE)) {
                queryBuilder = new StringBuilder(SEARCH_ABSTRACT_PAGE_FILTERED_QUERY);
                this.appendTypeExclusions(typeCode, queryBuilder, queryParameters);
            } else {
                queryBuilder.append(" AND (LOWER({c.uid}) LIKE LOWER(?mask) OR LOWER({c.name}) LIKE LOWER(?mask))");
            }
            queryParameters.put("mask", "%" + cmsItemSearchData.getMask() + "%");
        }

        if (hasItemSearchParams) {
            this.appendSearchParams(cmsItemSearchData.getItemSearchParams(), queryBuilder, queryParameters, typeCode);
        }

        if (hasSort) {
            if (typeCode.equals(AbstractPageModel._TYPECODE)) {
                appendPageSort(pageableData.getSort(), queryBuilder);
            } else {
                this.appendSort(pageableData.getSort(), queryBuilder, typeCode);
            }
        }

        FlexibleSearchQuery query = new FlexibleSearchQuery(queryBuilder.toString());
        query.addQueryParameters(queryParameters);
        query.setStart(pageableData.getCurrentPage() * pageableData.getPageSize());
        query.setCount(pageableData.getPageSize());
        query.setNeedTotal(true);
        return this.getFlexibleSearchService().search(query);
    }

    @Override
    protected void appendSearchParams(Map<String, String> itemSearchParams, StringBuilder queryBuilder, Map<String, Object> queryParameters, String typeCode) {
        try {
            for (Map.Entry<String, String> itemSearchParamEntry : itemSearchParams.entrySet()) {
                String field = itemSearchParamEntry.getKey();
                String value = itemSearchParamEntry.getValue();
                AttributeDescriptorModel attributeDescriptorModel = this.getTypeService().getAttributeDescriptor(typeCode, field);
                if ("null".equalsIgnoreCase(value.trim())) {
                    if (typeCode.equals(AbstractPageModel._TYPECODE)) {
                        queryBuilder.append(String.format(" AND q.p_%s IS NULL", field));
                    } else {
                        queryBuilder.append(String.format(" AND {c.%s} IS NULL", field));
                    }
                } else {
                    queryParameters.put(field, this.getFlexibleSearchAttributeValueConverter().convert(attributeDescriptorModel, value));
                    if (typeCode.equals(AbstractPageModel._TYPECODE)) {
                        queryBuilder.append(String.format(" AND q.p_%s=?%s", field, field));
                    } else {
                        queryBuilder.append(String.format(" AND {c.%s}=?%s", field, field));
                    }
                }

            }
        } catch (UnknownIdentifierException var5) {
            LOG.info(String.format("Unknown attribute in additionalParams for type: [%s]", typeCode), var5);
        }
    }

    protected void appendPageSort(String sortNameAndDirection, StringBuilder queryBuilder) {
        List<Sort> sortList = this.getSearchHelper().convertSort(sortNameAndDirection, SortDirection.ASC);
        queryBuilder.append(" ORDER BY ");
        String commaSeparatedSorts = (String) sortList.stream().map((sort) -> {
            return "q.p_" + sort.getParameter() + " " + sort.getDirection().name();
        }).collect(Collectors.joining(", "));
        queryBuilder.append(commaSeparatedSorts);
    }

    protected void appendTypeExclusions(String typeCode, StringBuilder queryBuilder, Map<String, Object> queryParameters) {
        if (this.getCmsItemSearchTypeBlacklistMap().containsKey(typeCode)) {
            if (typeCode.equals(AbstractPageModel._TYPECODE)) {
                queryBuilder.append(" WHERE ");
                queryBuilder.append(FlexibleSearchUtils.buildOracleCompatibleCollectionStatement("q.p_typecode NOT IN (?excludedTypes)", "excludedTypes", "OR", (Collection) this.getCmsItemSearchTypeBlacklistMap().get(typeCode), queryParameters));
            } else {
                queryBuilder.append(" AND ");
                queryBuilder.append(FlexibleSearchUtils.buildOracleCompatibleCollectionStatement("{type.code} NOT IN (?excludedTypes)", "excludedTypes", "OR", (Collection) this.getCmsItemSearchTypeBlacklistMap().get(typeCode), queryParameters));
            }
        }
    }
}
