package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.COUNTRY_PARAM;

import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.annotation.Autowired;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

class CountryParamsPopulator implements Populator<SearchRequestTuple, MultiValuedMap<String, String>> {

    @Autowired
    private CMSSiteService cmsSiteService;

    @Override
    public void populate(SearchRequestTuple searchRequestTuple, MultiValuedMap<String, String> params) throws ConversionException {
        CMSSiteModel cmsSite = cmsSiteService.getCurrentSite();
        CountryModel country = cmsSite.getCountry();
        params.put(COUNTRY_PARAM, country.getIsocode());
    }

    void setCmsSiteService(CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

}
