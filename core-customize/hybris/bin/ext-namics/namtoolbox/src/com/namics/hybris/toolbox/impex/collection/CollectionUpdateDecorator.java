/*
 * Copyright 2000-2010 Namics AG. All rights reserved.
 */

package com.namics.hybris.toolbox.impex.collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.core.PK;
import de.hybris.platform.impex.jalo.header.AbstractColumnDescriptor;
import de.hybris.platform.impex.jalo.header.AbstractDescriptor.ColumnParams;
import de.hybris.platform.impex.jalo.header.AbstractImpExCSVCellDecorator;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.StandardColumnDescriptor;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.jalo.JaloInvalidParameterException;
import de.hybris.platform.jalo.JaloItemNotFoundException;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearch;
import de.hybris.platform.jalo.security.JaloSecurityException;
import de.hybris.platform.jalo.type.TypeManager;

/**
 * CollectionUpdateTranslater.
 * 
 * @author rhusi, namics ag
 * @since MGB MEL 1.0
 * 
 */
public class CollectionUpdateDecorator extends AbstractImpExCSVCellDecorator {
    private static final Logger log = Logger.getLogger(CollectionUpdateDecorator.class.getName());
    private static final TypeManager typeManager = TypeManager.getInstance();
    private static final FlexibleSearch flexibleSearch = FlexibleSearch.getInstance();
    private AbstractColumnDescriptor abstractColumnDescriptor;
    private Set<StandardColumnDescriptor> uniqueColumns;
    private String importType;
    private String referenceAttribute;

    @SuppressWarnings("unchecked")
    @Override
    public String decorate(final int paramInt, final Map<Integer, String> paramMap) {
        abstractColumnDescriptor = getColumnDescriptor();
        uniqueColumns = abstractColumnDescriptor.getHeader().calculateUniqueAttributeColumns();
        importType = abstractColumnDescriptor.getHeader().getTypeCode();
        referenceAttribute = abstractColumnDescriptor.getDescriptorData().getModifier("referenceAttribute");
        final String currentColumnName = abstractColumnDescriptor.getHeader().getColumns().get(paramInt - 1).getQualifier();
        final String importValue = paramMap.get(Integer.valueOf(paramInt));
        try {
            final Item item = getItem(paramMap);
            if (item != null) {
                final Object attribute = item.getAttribute(currentColumnName);
                if (attribute instanceof List) {
                    final List<Item> attributeList = (List<Item>) attribute;
                    for (Item itemInList : attributeList) {
                        if (itemInList.getAttribute(referenceAttribute).equals(importValue)) {
                            return null;
                        }
                    }
                } else {
                    log.error("The CollectionUpdateDecorator can only be used for attributes of type java.util.List");
                    throw new IllegalArgumentException("The CollectionUpdateDecorator can only be used for attributes of type java.util.List");
                }
            }
        } catch (JaloInvalidParameterException e) {
            log.error(e);
            throw new JaloSystemException(e);
        } catch (JaloSecurityException e) {
            log.error(e);
            throw new JaloSystemException(e);
        }
        return importValue;
    }

    @SuppressWarnings("unchecked")
    private Item getItem(final Map<Integer, String> paramMap) {
        Item item = null;
        String whereClause = "";
        for (StandardColumnDescriptor standardColumnDescriptor : uniqueColumns) {
            final String value = paramMap.get(Integer.valueOf(standardColumnDescriptor.getValuePosition()));
            if (StringUtils.hasText(value)) {
                try {
                    if (standardColumnDescriptor.getQualifier().equalsIgnoreCase("catalogversion")
                            || standardColumnDescriptor.getQualifier().equalsIgnoreCase("systemversion")) {

                        final String[] catalog = value.split(":");
                        if (catalog.length == 2) {
                            final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog(catalog[0]).getCatalogVersion(catalog[1]);
                            if (StringUtils.hasText(whereClause)) {
                                if (standardColumnDescriptor.getQualifier().equalsIgnoreCase("systemversion")) {
                                    whereClause = whereClause + " AND {systemVersion}=" + catalogVersion.getPK();
                                } else {
                                    whereClause = whereClause + " AND {catalogVersion}=" + catalogVersion.getPK();
                                }
                            } else {
                                if (standardColumnDescriptor.getQualifier().equalsIgnoreCase("systemversion")) {
                                    whereClause = "{systemVersion}=" + catalogVersion.getPK();
                                } else {
                                    whereClause = "{catalogVersion}=" + catalogVersion.getPK();
                                }
                            }
                        } else {
                            throw new JaloSystemException(
                                    "Wrong format for attribute catalogVersion! Please verify that the catalogversion is declared as 'CatalogName:CatalogVersion'!");
                        }

                    } else {
                        // Check unique column is a composed type
                        typeManager.getType(standardColumnDescriptor.getQualifier());

                        PK uniqueAttributePK;
                        final List<Item> pkResult = flexibleSearch.search(
                                "SELECT {PK} FROM {" + standardColumnDescriptor.getQualifier() + "} WHERE {" + referenceAttribute + "}='"
                                        + paramMap.get(Integer.valueOf(standardColumnDescriptor.getValuePosition())) + "'",
                                typeManager.getType(standardColumnDescriptor.getQualifier()).getClass()).getResult();

                        if (CollectionUtils.isEmpty(pkResult)) {
                            final String errorMsg = "Reference item of type " + standardColumnDescriptor.getQualifier() + " with value "
                                    + paramMap.get(Integer.valueOf(standardColumnDescriptor.getValuePosition())) + " not found!";
                            log.error(errorMsg);
                            throw new JaloSystemException(errorMsg);
                        } else if (pkResult.size() != 1) {
                            final String errorMsg = "More than one item found for type " + standardColumnDescriptor.getQualifier() + " with value "
                                    + paramMap.get(Integer.valueOf(standardColumnDescriptor.getValuePosition())) + "! Only one item is expected!";
                            log.error(errorMsg);
                            throw new JaloSystemException(errorMsg);
                        } else {
                            uniqueAttributePK = pkResult.get(0).getPK();
                        }

                        if (StringUtils.hasText(whereClause)) {
                            whereClause = whereClause + " AND {" + standardColumnDescriptor.getQualifier() + "}=" + uniqueAttributePK;
                        } else {
                            whereClause = "{" + standardColumnDescriptor.getQualifier() + "}=" + uniqueAttributePK;
                        }
                    }
                } catch (JaloItemNotFoundException e) {
                    if (StringUtils.hasText(whereClause)) {
                        whereClause = whereClause + " AND {" + standardColumnDescriptor.getQualifier() + "}='" + value + "'";
                    } else {
                        whereClause = "{" + standardColumnDescriptor.getQualifier() + "}='" + value + "'";
                    }
                }
            } else if (standardColumnDescriptor.getQualifier().equalsIgnoreCase("catalogversion")) {

                try {
                    String catalog = null;
                    String version = null;
                    final List<ColumnParams>[] itemPathElements = StandardColumnDescriptor.extractItemPathElements(standardColumnDescriptor.getDefinitionSrc());
                    if (itemPathElements.length > 0) {
                        final List<ColumnParams> catalogVersionItemPatternList = itemPathElements[0].get(0).getItemPatternLists()[0];
                        for (ColumnParams catalogVersionColumnParam : catalogVersionItemPatternList) {
                            if (catalogVersionColumnParam.getQualifier().equalsIgnoreCase("catalog")) {
                                final List<ColumnParams> catalogColumnParam = catalogVersionColumnParam.getItemPatternLists()[0];
                                if ("id".equalsIgnoreCase(catalogColumnParam.get(0).getQualifier())) {
                                    catalog = catalogColumnParam.get(0).getModifier("default");
                                }
                            } else if (catalogVersionColumnParam.getQualifier().equalsIgnoreCase("version")) {
                                version = catalogVersionColumnParam.getModifier("default");
                            }
                        }
                    }

                    if (catalog != null && version != null) {
                        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog(catalog).getCatalogVersion(version);
                        if (StringUtils.hasText(whereClause)) {
                            whereClause = whereClause + " AND {catalogVersion}=" + catalogVersion.getPK();
                        } else {
                            whereClause = "{catalogVersion}=" + catalogVersion.getPK();
                        }
                    } else {
                        throw new JaloSystemException(
                                "Found attribute catalogVersion, but could not extract the values for catalog.id or catalog.version. Please verify that the catalogversion is declared as catalogVersion(catalog(id[default='CatalogName']),version[default='CatalogVersion'])");
                    }

                } catch (HeaderValidationException e) {
                    log.error(e);
                    throw new JaloSystemException(e);
                }
            } else if (standardColumnDescriptor.getQualifier().equalsIgnoreCase("systemversion")) {
                try {
                    String catalog = null;
                    String version = null;
                    final List<ColumnParams>[] itemPathElements = StandardColumnDescriptor.extractItemPathElements(standardColumnDescriptor.getDefinitionSrc());
                    if (itemPathElements.length > 0) {
                        final List<ColumnParams> catalogVersionItemPatternList = itemPathElements[0].get(0).getItemPatternLists()[0];
                        for (ColumnParams catalogVersionColumnParam : catalogVersionItemPatternList) {
                            if ("catalog".equalsIgnoreCase(catalogVersionColumnParam.getQualifier())) {
                                final List<ColumnParams> catalogColumnParam = catalogVersionColumnParam.getItemPatternLists()[0];
                                if ("id".equalsIgnoreCase(catalogColumnParam.get(0).getQualifier())) {
                                    catalog = catalogColumnParam.get(0).getModifier("default");
                                }
                            } else if ("version".equalsIgnoreCase(catalogVersionColumnParam.getQualifier())) {
                                version = catalogVersionColumnParam.getModifier("default");
                            }
                        }
                    }

                    if (catalog != null && version != null) {
                        final CatalogVersion catalogVersion = CatalogManager.getInstance().getCatalog(catalog).getCatalogVersion(version);
                        if (StringUtils.hasText(whereClause)) {
                            whereClause = whereClause + " AND {systemVersion}=" + catalogVersion.getPK();
                        } else {
                            whereClause = "{systemVersion}=" + catalogVersion.getPK();
                        }
                    } else {
                        throw new JaloSystemException(
                                "Found attribute catalogVersion, but could not extract the values for catalog.id or catalog.version. Please verify that the catalogversion is declared as catalogVersion(catalog(id[default='CatalogName']),version[default='CatalogVersion'])");
                    }

                } catch (HeaderValidationException e) {
                    log.error(e);
                    throw new JaloSystemException(e);
                }
            }
        }

        final List<Item> result = flexibleSearch.search("SELECT {PK} FROM {" + importType + "} WHERE " + whereClause,
                typeManager.getType(importType).getClass()).getResult();
        if (!CollectionUtils.isEmpty(result)) {
            if (result.size() > 1) {
                throw new JaloSystemException("More than one item found for type " + importType
                        + " for update with the given unique qualifier! Only one item is expected!");
            } else {
                item = result.get(0);
            }
        }

        return item;
    }

}
