/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistExtHeroRotatingTeaserComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistExtHeroRotatingTeaserItemModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistExtHeroRotatingTeaserModel;
import com.namics.distrelec.b2b.facades.cms.data.DistExtCarouselItemData;
import com.namics.distrelec.b2b.facades.cms.data.DistMinCarouselItemData;
import com.namics.distrelec.b2b.facades.product.data.DistVideoData;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.facades.webtrekk.data.TeaserGenericItemDataLayer;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.tags.Functions;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code DistExtHeroRotatingTeaserComponentController}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */
@Controller("DistExtHeroRotatingTeaserComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistExtHeroRotatingTeaserComponent)
public class DistExtHeroRotatingTeaserComponentController extends AbstractDistCMSComponentController<DistExtHeroRotatingTeaserComponentModel> {

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Autowired
    private Converter<MediaModel, ImageData> imageConverter;

    @Autowired
    private Converter<DistVideoMediaModel, DistVideoData> distVideoMediaConverter;

    private List<Integer> placementList;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistExtHeroRotatingTeaserComponentModel component) {
        final List<DistMinCarouselItemData> teaserItemData = new ArrayList<DistMinCarouselItemData>();
        for (final DistExtHeroRotatingTeaserItemModel heroRotatingTeaserItem : component.getHeroRotatingTeaserItems()) {
            if (heroRotatingTeaserItem.getContentTeaser() != null) {
                teaserItemData.add(convert(heroRotatingTeaserItem.getContentTeaser(), request));
            }
        }
        // if content specialist requests teaser images to be displayed in a random order. Valid only for requests coming in from disparate
        // clients. Does not work on "reload"
        if (component.getRandomOrder() != null && component.getRandomOrder().booleanValue()) {
            Collections.shuffle(teaserItemData);
        }
        model.addAttribute("teaserItemData", teaserItemData);
        final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        model.addAttribute("teaserGenericItemDataLayer", gson.toJson(getTeaserGenericItemDataLayer(teaserItemData)));
        model.addAttribute("autoplay", Boolean.valueOf(component.getAutoplayTimeout() != null && !component.getAutoplayTimeout().equals(Long.valueOf(0))));
        distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
    }

    private List<TeaserGenericItemDataLayer> getTeaserGenericItemDataLayer(final List<DistMinCarouselItemData> teaserItemData) {
        if (CollectionUtils.isNotEmpty(teaserItemData)) {
            final List<TeaserGenericItemDataLayer> teaserGenericItemDataLayers = new ArrayList<TeaserGenericItemDataLayer>();
            int placement = 0;
            for (final DistMinCarouselItemData teaserData : teaserItemData) {
                final TeaserGenericItemDataLayer teaserGenericItemDataLayer = new TeaserGenericItemDataLayer();
                teaserGenericItemDataLayer.setHref(teaserData.getUrl());
                if (teaserData.getPicture() != null) {
                    teaserGenericItemDataLayer.setImgSrc(teaserData.getPicture().getUrl());
                }
                placement = placement + 1;
                teaserGenericItemDataLayer.setPlacement(placement);
                teaserGenericItemDataLayers.add(teaserGenericItemDataLayer);
            }
            return teaserGenericItemDataLayers;
        }
        return null;
    }

    /**
     * Convert the specified {@code DistExtHeroRotatingTeaserModel} to a {@code DistMinCarouselItemData}
     *
     * @param teaserModel
     *            the source item to convert
     * @param request
     *            the HTTP request
     * @return a new instance of {@code DistMinCarouselItemData}
     */
    private DistMinCarouselItemData convert(final DistExtHeroRotatingTeaserModel teaserModel, final HttpServletRequest request) {
        final DistExtCarouselItemData target = new DistExtCarouselItemData();
        target.setFullsize(true);
        target.setName(teaserModel.getName());
        if (teaserModel.getLink() != null) {
            target.setUrl(Functions.getUrlForCMSLinkComponent(teaserModel.getLink(), request));
            target.setLinkTarget(teaserModel.getLink().getTarget());
        }
        if (teaserModel.getPicture() != null) {
            target.setPicture(imageConverter.convert(teaserModel.getPicture()));
        }

        if (teaserModel.getVideo() != null) {
            target.setVideoData(distVideoMediaConverter.convert(teaserModel.getVideo()));
            target.setVideo(true);
            target.setVideoAutoplay(BooleanUtils.isTrue(teaserModel.getVideoAutoplay()));
        }
        target.setYouTubeID(teaserModel.getYouTubeID());

        return target;
    }

}
