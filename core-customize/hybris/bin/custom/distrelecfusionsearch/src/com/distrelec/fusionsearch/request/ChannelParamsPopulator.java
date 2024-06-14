package com.distrelec.fusionsearch.request;

import static com.distrelec.fusionsearch.constants.DistrelecfusionsearchConstants.FusionSearchParameters.CHANNEL_PARAM;

import org.apache.commons.collections4.MultiValuedMap;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;

class ChannelParamsPopulator implements Populator<SearchRequestTuple, MultiValuedMap<String, String>> {

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    private DistrelecBaseStoreService distBaseStoreService;

    @Override
    public void populate(SearchRequestTuple searchRequestTuple, MultiValuedMap<String, String> params) throws ConversionException {
        BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
        SiteChannel siteChannel = distBaseStoreService.getCurrentChannel(currentBaseSite);
        params.put(CHANNEL_PARAM, siteChannel.getCode());
    }
}
