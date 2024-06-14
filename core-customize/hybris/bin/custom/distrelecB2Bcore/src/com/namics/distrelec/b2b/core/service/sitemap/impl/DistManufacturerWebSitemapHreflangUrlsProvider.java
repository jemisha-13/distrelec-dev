package com.namics.distrelec.b2b.core.service.sitemap.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.core.service.sitemap.EntityNames;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

public class DistManufacturerWebSitemapHreflangUrlsProvider extends DistWebSitemapHreflangUrlsProvider<DistManufacturerModel> {

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Override
    protected List<CMSSiteModel> getCmsSites(DistManufacturerModel entity) {
        return distManufacturerService.getAvailableCMSSitesForManufacturer(entity);
    }

    @Override
    protected String getEntityCode(final DistManufacturerModel entity) {
        return entity.getCode();
    }

    @Override
    public String getEntityName() {
        return EntityNames.MANUFACTURER.name();
    }

    @Override
    protected void initQuery() {
        final CountryModel country = getCmsSiteModel().getCountry();

        setFlexibleSearchQuery(new FlexibleSearchQuery("SELECT {manu." + DistManufacturerModel.PK + "} FROM {" + DistManufacturerModel._TYPECODE
                                                       + " AS manu JOIN " + DistManufacturerCountryModel._TYPECODE
                                                       + " AS manucountry ON {manu.pk}={manucountry."
                                                       + DistManufacturerCountryModel.MANUFACTURER + "}} WHERE {manucountry."
                                                       + DistManufacturerCountryModel.VISIBLEONSHOP + " }=1 AND {manucountry."
                                                       + DistManufacturerCountryModel.COUNTRY + "} = " + country.getPk()));
    }

    @Override
    protected boolean exclude(final String url, CMSSiteModel cmsSiteModel) {
        return false;
    }
}
