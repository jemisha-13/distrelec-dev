/*
 * Copyright 2000-2015 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.sitemap.impl;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.sitemap.EntityNames;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

/**
 * {@code DistManufacturerWebSitemapUrlsProvider}
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.4
 */
public class DistManufacturerWebSitemapUrlsProvider extends DistWebSitemapUrlsProvider<DistManufacturerModel> {

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#getEntityCode(de.hybris.platform.core.model.ItemModel)
     */
    @Override
    protected String getEntityCode(final DistManufacturerModel entity) {
        return entity.getCode();
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.sitemap.WebSitemapUrlsProvider#getEntityName()
     */
    @Override
    public String getEntityName() {
        return EntityNames.MANUFACTURER.name();
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#initQuery()
     */
    @Override
    protected void initQuery() {
        final CountryModel country = getCmsSiteModel().getCountry();
        
        /* setFlexibleSearchQuery(new FlexibleSearchQuery("SELECT {" + DistManufacturerModel.PK + "} FROM {" + DistManufacturerModel._TYPECODE + "} WHERE {"
                + DistManufacturerModel.CODE + "} IS NOT NULL")); */
        
        setFlexibleSearchQuery(new FlexibleSearchQuery("SELECT {manu." + DistManufacturerModel.PK + "} FROM {" + DistManufacturerModel._TYPECODE
                + " AS manu JOIN " + DistManufacturerCountryModel._TYPECODE + " AS manucountry ON {manu.pk}={manucountry."
                + DistManufacturerCountryModel.MANUFACTURER + "}} WHERE {manucountry." + DistManufacturerCountryModel.VISIBLEONSHOP + " }=1 AND {manucountry."
                + DistManufacturerCountryModel.COUNTRY + "} = " + country.getPk()));
    }

    /*
     * (non-Javadoc)
     * @see com.namics.distrelec.b2b.core.service.sitemap.impl.DistWebSitemapUrlsProvider#exclude(java.lang.String)
     */
    @Override
    protected boolean exclude(final String url) {
        return false;
    }
}
