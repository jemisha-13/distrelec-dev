/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.feature.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.feature.dao.DistWebuseAttributesDao;

import de.hybris.platform.catalog.enums.ClassificationAttributeVisibilityEnum;
import de.hybris.platform.catalog.model.ProductFeatureModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Default implementatino of {@link DistWebuseAttributesDao}.<br />
 * <br />
 * ATTENTION: if you change something in the logic of the FlexibleSearch-Query, also check the FactfinderExport in
 * DistFactFinderProductExportQueryCreator.
 * 
 * @author rlehmann, Namics AG
 * @since Distrelec 2.0.20 (DISTRELEC-4581)
 * 
 */
public class DefaultDistWebuseAttributesDao implements DistWebuseAttributesDao {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistWebuseAttributesDao.class);

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private DistSqlUtils distSqlUtils;

    private final static String VALID_VISIBILITIES_ATTR = "validVisibilities";
    private final static String LANGUAGE_ATTR = "lang";
    private final static String PRODUCT_ATTR = "product";
    private static final String UNIT_DELIMITER = " ";

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getWebuseAttributes(ProductModel product, LanguageModel language, List<String> validVisibilities) {

        // Map needs to be sorted
        Map<String, String> attributes = new TreeMap<String, String>();

        // create query: productfeature name will be retrieved in the session language!
        FlexibleSearchQuery query = new FlexibleSearchQuery(createQuery());
        query.addQueryParameter(VALID_VISIBILITIES_ATTR, validVisibilities);
        query.addQueryParameter(LANGUAGE_ATTR, language);
        query.addQueryParameter(PRODUCT_ATTR, product);

        // set class list, as we don't use models here
        final List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        resultClassList.add(String.class);
        resultClassList.add(String.class);
        resultClassList.add(String.class);
        query.setResultClassList(resultClassList);

        query.setNeedTotal(true);

        // execute search
        final SearchResult<List<Object>> searchResult = getFlexibleSearchService().search(query);

        if (searchResult.getTotalCount() > 0) {
            for (final List<Object> row : searchResult.getResult()) {
                final String name = (String) row.get(0);
                final String value = (String) row.get(1);
                final String unit = (String) row.get(2);

                if (StringUtils.isNotEmpty(name)) {
                    if (StringUtils.isNotEmpty(unit)) {
                        attributes.put(name, value + UNIT_DELIMITER + unit);
                    } else {
                        attributes.put(name, value);
                    }
                }
            }
        }

        return attributes;
    }

    /**
     * ATTRENTION: if you change something in this query, also check the Factfinder export in DistFactFinderProductExportQueryCreator!
     * 
     * @return the search query
     */
    public String createQuery() {
        final StringBuilder query = new StringBuilder();

        query.append("SELECT {ca.").append(ClassificationAttributeModel.NAME).append("}, ");
        query.append(distSqlUtils.toNvarchar("{pf.stringvalue}")).append(", ");
        query.append("{cu.").append(ClassificationAttributeUnitModel.SYMBOL).append("} ");
        query.append("FROM {").append(ProductFeatureModel._TYPECODE).append(" AS pf ");
        query.append("JOIN ").append(ClassAttributeAssignmentModel._TYPECODE).append(" AS caa ON  {caa.").append(ClassAttributeAssignmentModel.PK).append("} ");
        query.append("= {pf. ").append(ProductFeatureModel.CLASSIFICATIONATTRIBUTEASSIGNMENT).append("} ");
        query.append("JOIN ").append(ClassificationAttributeModel._TYPECODE).append(" AS ca ON  {ca.").append(ClassificationAttributeModel.PK).append("} ");
        query.append("= {caa. ").append(ClassAttributeAssignmentModel.CLASSIFICATIONATTRIBUTE).append("} ");
        query.append("JOIN ").append(ClassificationAttributeVisibilityEnum._TYPECODE).append(" AS v ON  {v.pk} ");
        query.append("= {caa. ").append(ClassAttributeAssignmentModel.VISIBILITY).append("} ");
        query.append("LEFT JOIN  ").append(ClassificationAttributeUnitModel._TYPECODE).append(" AS cu ON  {cu.").append(ClassificationAttributeUnitModel.PK).append("} ");
        query.append("= {pf.").append(ProductFeatureModel.UNIT).append("}} ");
        query.append("WHERE {v.code").append("} IN (?").append(VALID_VISIBILITIES_ATTR).append(")");
        query.append(" AND {pf.").append(ProductFeatureModel.LANGUAGE).append("} = ?").append(LANGUAGE_ATTR);
        query.append(" AND {pf.").append(ProductFeatureModel.PRODUCT).append("} = ?").append(PRODUCT_ATTR);
        query.append(" ORDER BY {caa.").append(ClassAttributeAssignmentModel.POSITION).append("}");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Query: {}", query.toString());
        }

        return query.toString();
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

}
