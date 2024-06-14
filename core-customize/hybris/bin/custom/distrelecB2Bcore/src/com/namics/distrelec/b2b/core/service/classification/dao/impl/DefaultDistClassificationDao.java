/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.classification.dao.impl;

import com.distrelec.b2b.core.search.data.Unit;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.classification.dao.DistClassificationDao;
import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import de.hybris.platform.catalog.constants.CatalogConstants;
import de.hybris.platform.catalog.constants.GeneratedCatalogConstants.TC;
import de.hybris.platform.catalog.jalo.classification.ClassificationAttributeUnit;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeValueModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.classification.daos.impl.DefaultClassificationDao;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * {@code DefaultDistClassificationDao}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.10
 */
public class DefaultDistClassificationDao extends DefaultClassificationDao implements DistClassificationDao {

    private static final String FIND_CLASS_ATTR_QUERY = "SELECT {pk} FROM {" + ClassificationAttributeModel._TYPECODE + "} WHERE trim({"
            + ClassificationAttributeModel.CODE + "})=?" + ClassificationAttributeModel.CODE;

    private static final String GET_CLASS_ATTR_VALUE_QUERY = "SELECT {cav.pk} FROM {" + ClassificationAttributeValueModel._TYPECODE +
        " AS cav} WHERE trim({cav." + ClassificationAttributeValueModel.CODE + "}) = ?" + ClassificationAttributeValueModel.CODE +
        " AND EXISTS ({{SELECT 1 FROM {" + TC.ATTRIBUTEVALUEASSIGNMENT + " AS ava} WHERE {ava.attributeAssignment} IS NULL" +
        " AND {ava.attribute} = ?" + ClassificationAttributeModel._TYPECODE + " AND {ava.value} = {cav.pk} }})";

    private final Logger LOG = LoggerFactory.getLogger(DefaultDistClassificationDao.class);

    final private String CLASSIFICATION_UNITS_WITH_CONVERSION_GROUP = "SELECT {" + Item.PK + "} FROM {" + CatalogConstants.TC.CLASSIFICATIONATTRIBUTEUNIT + "} " + "WHERE {"
                + ClassificationAttributeUnit.SYSTEMVERSION + "}= ?code AND " +
            "{unittype} in ({{SELECT {unittype} FROM {classificationattributeunit} GROUP BY {unittype} HAVING COUNT(*) > 1)}}" + 
            " ORDER BY %s DESC";
    
    private static final String BASE_UNIT_QUERY = "SELECT {PK} FROM {" + ClassificationAttributeUnitModel._TYPECODE + "} WHERE " +
            "{" + ClassificationAttributeUnitModel.UNITTYPE + "}=?unitType AND {" + ClassificationAttributeUnitModel.CONVERSIONFACTOR + "}=?conversionFactor";
    
    private ConfigurationService configurationService;
    
    private ClassificationSystemService classificationSystemService;
  
    private DistSqlUtils distSqlUtils;
    
    /**
     * Create a new instance of {@code DefaultDistClassificationDao}
     */
    public DefaultDistClassificationDao() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.classification.dao.DistClassificationDao#findClassificationAttribute(java.lang.String)
     */
    @Override
    public ClassificationAttributeModel findClassificationAttribute(final String code) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(FIND_CLASS_ATTR_QUERY);
        searchQuery.addQueryParameter(ClassificationAttributeModel.CODE, code);
        return searchUnique(searchQuery);
    }

    @Override
    public ClassificationAttributeValueModel getClassificationAttributeValue(ClassificationAttributeModel classificationAttributeModel, String code) {
        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(GET_CLASS_ATTR_VALUE_QUERY);
        searchQuery.addQueryParameter(ClassificationAttributeModel._TYPECODE, classificationAttributeModel);
        searchQuery.addQueryParameter(ClassificationAttributeValueModel.CODE, code);

        SearchResult<ClassificationAttributeValueModel> searchRes = search(searchQuery);
        List<ClassificationAttributeValueModel> classAttrValues = searchRes.getResult();

        if (classAttrValues.size() == 1) {
            return classAttrValues.get(0);
        } else if (classAttrValues.isEmpty()) {
            return null;
        } else {
            String msg = String.format("More than one classification attribute value for attribute: %s and value: %s",
                    classificationAttributeModel.getCode(), code);
            LOG.error(msg);
            throw new AmbiguousIdentifierException(msg);
        }
    }

    @Override
    public List<ClassificationAttributeModel> findClassificationAttribute(String classAttrName, Locale locale) {
        String query = getClassAttrQuery(locale);

        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        searchQuery.addQueryParameter("classAttrName", classAttrName);

        SearchResult<ClassificationAttributeModel> searchRes = search(searchQuery);
        List<ClassificationAttributeModel> attrs = searchRes.getResult();
        return attrs;
    }

    @Override
    public List<ClassificationAttributeValueModel> findClassificationAttributeValue(
            List<ClassificationAttributeModel> classAttrs, String classAttrValueName, Locale locale) {
        String query = getClassAttrValueQuery(locale);

        FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query);
        searchQuery.addQueryParameter(ClassificationAttributeModel._TYPECODE, classAttrs);
        searchQuery.addQueryParameter("classAttrValueName", classAttrValueName);

        SearchResult<ClassificationAttributeValueModel> searchRes = search(searchQuery);
        List<ClassificationAttributeValueModel> attrValues = searchRes.getResult();
        return attrValues;
    }

    private String getClassAttrQuery(Locale locale) {
        return "SELECT {ca.pk} FROM {" + ClassificationAttributeModel._TYPECODE + " AS ca} WHERE {ca." +
            ClassificationAttributeModel.NAME + "[" + locale.getLanguage() + "]} = ?classAttrName";
    }

    private String getClassAttrValueQuery(Locale locale) {
        return "SELECT {cav.pk} FROM {" + ClassificationAttributeValueModel._TYPECODE + " AS cav} WHERE {cav." +
                ClassificationAttributeValueModel.NAME + "[" + locale.getLanguage() + "]} = ?classAttrValueName" +
                " AND EXISTS ({{SELECT 1 FROM {" + TC.ATTRIBUTEVALUEASSIGNMENT + " AS ava} WHERE {ava.attributeAssignment} IS NULL" +
                " AND {ava.value} = {cav.pk} AND {ava.attribute} in (?" + ClassificationAttributeModel._TYPECODE + ") }})";
    }

    @Override
   	public List<Unit> findAttributeUnitsWithUnitGroup()
   	{
        final Map<String, Object> params = new HashMap<>();

        params.put("code", getClassificationSystemVersion().getPk());
   
   		final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(String.format(CLASSIFICATION_UNITS_WITH_CONVERSION_GROUP,distSqlUtils.length("{symbol}") ));
   		flexibleSearchQuery.addQueryParameters(params);
   		flexibleSearchQuery.setCount(-1);
   		flexibleSearchQuery.setStart(0);
   		flexibleSearchQuery.setNeedTotal(true);
   		final SearchResult<ClassificationAttributeUnitModel> results = search(flexibleSearchQuery);
   		return results.getResult().stream().map(this::convertUnitModelToDTO).collect(Collectors.toList());
   	}
    
       
    @Override
    public ClassificationAttributeUnitModel findBaseUnitForUnit(ClassificationAttributeUnitModel unit)
    {
        FlexibleSearchQuery fsq = new FlexibleSearchQuery(BASE_UNIT_QUERY);
        fsq.addQueryParameter("unitType", unit.getUnitType());
        fsq.addQueryParameter("conversionFactor", 1);

        List<ClassificationAttributeUnitModel> result = getFlexibleSearchService().<ClassificationAttributeUnitModel>search(fsq).getResult();

        if (result.size() > 1)
        {
            LOG.warn("More than one baseUnit found for unit:{}, first will be taken", unit.getCode());
        }

        if (result.size() > 0)
        {
            return result.get(0);
        }
        else
        {
            LOG.warn("No base unit found for unit:{}", unit.getCode());
        }
        return null;
    }
       
    private ClassificationSystemVersionModel getClassificationSystemVersion()
   	{
   		final Configuration configuration = configurationService.getConfiguration();
   		return classificationSystemService.getSystemVersion(configuration.getString(DistConstants.PropKey.Import.CLASSIFICATION_SYSTEM_ID),
   				configuration.getString(DistConstants.PropKey.Import.CLASSIFICATION_SYSTEM_VERSION));
   	}

    private Unit convertUnitModelToDTO(final ClassificationAttributeUnitModel unitModel)
    {
        Unit unit = new Unit();
        unit.setSymbol(unitModel.getSymbol());
        unit.setConversionFactor(unitModel.getConversionFactor());
        unit.setUnitType(unitModel.getUnitType());
        unit.setCode(unitModel.getCode());
        return unit;
    }

    protected ConfigurationService getConfigurationService()
    {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService)
    {
        this.configurationService = configurationService;
    }

    protected ClassificationSystemService getClassificationSystemService()
    {
        return classificationSystemService;
    }

    public void setClassificationSystemService(final ClassificationSystemService classificationSystemService)
    {
        this.classificationSystemService = classificationSystemService;
    }

    @Required
    public void setDistSqlUtils(final DistSqlUtils distSqlUtils)
    {
        this.distSqlUtils = distSqlUtils;
    }
}
