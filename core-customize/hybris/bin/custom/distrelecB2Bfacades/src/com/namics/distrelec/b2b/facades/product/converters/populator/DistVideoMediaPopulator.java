/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.product.data.DistVideoData;

import de.hybris.platform.commercefacades.product.converters.populator.AbstractProductPopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

public class DistVideoMediaPopulator<SOURCE extends ProductModel, TARGET extends ProductData>
                                    extends AbstractProductPopulator<SOURCE, TARGET> {

    private static final Logger LOG = LoggerFactory.getLogger(DistVideoMediaPopulator.class);

    @Autowired
    @Qualifier("distVideoMediaConverter")
    private Converter<DistVideoMediaModel, DistVideoData> distVideoMediaConverter;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private CommonI18NService commonI18NService;

    public static final String ENGLISH_ISO_CODE = "en";

    @Override
    public void populate(final SOURCE source, final TARGET target) {
        if (source.getVideoMedias() != null && LOG.isDebugEnabled()) {
            LOG.debug("Size of Medias::" + String.valueOf(CollectionUtils.size(source.getVideoMedias())) + "for Product"
                      + source.getPk());
        }
        if (CollectionUtils.isNotEmpty(source.getVideoMedias())) {
            final List<DistVideoData> videoList = new ArrayList<>();
            for (final DistVideoMediaModel videoMedia : source.getVideoMedias()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Media PK::" + videoMedia.getPk());
                }
                if (DistUtils.checkLanguage(videoMedia.getLanguages())) {
                    videoList.add(distVideoMediaConverter.convert(videoMedia));
                }
            }
            target.setVideos(videoList);
        }
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

}
