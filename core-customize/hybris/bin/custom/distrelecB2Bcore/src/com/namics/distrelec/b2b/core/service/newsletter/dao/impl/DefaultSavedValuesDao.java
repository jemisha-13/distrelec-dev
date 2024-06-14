/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.newsletter.dao.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.newsletter.dao.SavedValuesDao;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

/**
 * {@code DefaultSavedValuesDao}
 * 
 *
 * @author <a href="marco.dellanna@distrelec.com">Marco Dell'Anna</a>, Distrelec
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 6.7
 */
public class DefaultSavedValuesDao implements SavedValuesDao{

    private static final Logger LOG = LogManager.getLogger(DefaultSavedValuesDao.class);
    
    
    @Autowired
    private FlexibleSearchService flexibleSearchService;
    
    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.service.newsletter.dao.SavedValuesDao#findByLastFieldUpdate(int, java.lang.String,
     * java.lang.String)
     */
    @Override
    public <T extends ItemModel> List<T> findByLastFieldUpdate(final int days, final String typeCode, String fieldName) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery("SELECT DISTINCT {c.pk} FROM {" + typeCode
                + " AS c JOIN SavedValues AS sv ON {c.pk}={sv.modifieditem} JOIN SavedValueEntry AS se ON {sv.pk}={se.parent} "
                + "    } WHERE {c.creationtime} >= (?fromDate) OR ({se.modifiedAttribute}= ?fieldName AND  {se.creationtime} >= (?fromDate))");
        searchQuery.addQueryParameter("fromDate", new DateTime(new Date()).minusDays(days).toDate());
        searchQuery.addQueryParameter("fieldName", fieldName);
        return flexibleSearchService.<T> search(searchQuery).getResult();
    }

    // Getters & Setters

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }
}
