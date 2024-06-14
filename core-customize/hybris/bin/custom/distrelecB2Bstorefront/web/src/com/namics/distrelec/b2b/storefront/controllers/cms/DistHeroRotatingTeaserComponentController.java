/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.cms2.components.DistHeroRotatingTeaserComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.items.AbstractDistHeroRotatingTeaserModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserItemModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistHeroRotatingTeaserWithTextModel;
import com.namics.distrelec.b2b.facades.cms.data.DistCarouselItemData;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.facades.webtrekk.data.TeaserGenericItemDataLayer;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.tags.Functions;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Controller for CMS DistHeroRotatingTeaserComponent.
 */
@Controller("DistHeroRotatingTeaserComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistHeroRotatingTeaserComponent)
public class DistHeroRotatingTeaserComponentController extends AbstractDistCMSComponentController<DistHeroRotatingTeaserComponentModel> {

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Autowired
    @Qualifier("categoryConverter")
    private Converter<CategoryModel, CategoryData> categoryConverter;

    @Autowired
    @Qualifier("distManufacturerConverter")
    private Converter<DistManufacturerModel, DistManufacturerData> distManufacturerConverter;

    @Autowired
    @Qualifier("distCarouselProductDataConverter")
    private Converter<ProductData, DistCarouselItemData> distCarouselProductDataConverter;

    @Autowired
    @Qualifier("distCarouselCategoryDataConverter")
    private Converter<CategoryData, DistCarouselItemData> distCarouselCategoryDataConverter;

    @Autowired
    @Qualifier("distCarouselManufacturerDataConverter")
    private Converter<DistManufacturerData, DistCarouselItemData> distCarouselManufacturerDataConverter;

    @Autowired
    private Converter<MediaModel, ImageData> imageConverter;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistHeroRotatingTeaserComponentModel component) {
        final List<DistCarouselItemData> teaserItemData = new ArrayList<>();
        for (final DistHeroRotatingTeaserItemModel heroRotatingTeaserItem : component.getHeroRotatingTeaserItems()) {
            if (heroRotatingTeaserItem.getProduct() != null) {
                teaserItemData.add(createDistHeroRotatingItemFromProduct(heroRotatingTeaserItem.getProduct()));
                continue;
            }
            if (heroRotatingTeaserItem.getCategory() != null) {
                teaserItemData.add(createDistHeroRotatingItemFromCategory(heroRotatingTeaserItem.getCategory()));
                continue;
            }
            if (heroRotatingTeaserItem.getManufacturer() != null) {
                teaserItemData.add(createDistHeroRotatingItemFromManufacturer(heroRotatingTeaserItem.getManufacturer()));
                continue;
            }
            if (heroRotatingTeaserItem.getContentTeaser() != null) {
                teaserItemData.add(createDistHeroRotatingItemFromHeroTeaser(heroRotatingTeaserItem.getContentTeaser(), request));
                continue;
            }
        }

        model.addAttribute("teaserItemData", teaserItemData);
        final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        model.addAttribute("teaserGenericItemDataLayer", gson.toJson(getTeaserGenericItemDataLayer(teaserItemData)));

        if (component.getAutoplayTimeout() == null || component.getAutoplayTimeout().equals(Long.valueOf(0))) {
            model.addAttribute("autoplay", Boolean.FALSE);
        } else {
            model.addAttribute("autoplay", Boolean.TRUE);
        }

        distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
    }

    private List<TeaserGenericItemDataLayer> getTeaserGenericItemDataLayer(final List<DistCarouselItemData> teaserItemData) {
        if (CollectionUtils.isNotEmpty(teaserItemData)) {
            final List<TeaserGenericItemDataLayer> teaserGenericItemDataLayers = new ArrayList<>();
            final int placement = 0;
            for (final DistCarouselItemData teaserData : teaserItemData) {
                final TeaserGenericItemDataLayer teaserGenericItemDataLayer = new TeaserGenericItemDataLayer();
                teaserGenericItemDataLayer.setHref(teaserData.getUrl());
                if (teaserData.getPicture() != null) {
                    teaserGenericItemDataLayer.setImgSrc(teaserData.getPicture().getUrl());
                }
                teaserGenericItemDataLayer.setPlacement(placement + 1);
                teaserGenericItemDataLayers.add(teaserGenericItemDataLayer);
            }
            return teaserGenericItemDataLayers;
        }
        return null;
    }

    protected DistCarouselItemData createDistHeroRotatingItemFromProduct(final ProductModel productModel) {
        final List<ProductOption> productOptions = Arrays.asList(ProductOption.PRICE, ProductOption.DIST_MANUFACTURER, ProductOption.CATEGORIES,
                                                                 ProductOption.PROMOTION_LABELS);
        final ProductData product = productFacade.getProductForCodeAndOptions(productModel.getCode(), productOptions);
        return distCarouselProductDataConverter.convert(product);
    }

    protected DistCarouselItemData createDistHeroRotatingItemFromCategory(final CategoryModel categoryModel) {
        final CategoryData category = categoryConverter.convert(categoryModel);
        final CategoryData distCategory = (CategoryData) category;

        return distCarouselCategoryDataConverter.convert(distCategory);
    }

    protected DistCarouselItemData createDistHeroRotatingItemFromManufacturer(final DistManufacturerModel manufacturerModel) {
        final DistManufacturerData manufacturer = distManufacturerConverter.convert(manufacturerModel);
        return distCarouselManufacturerDataConverter.convert(manufacturer);
    }

    protected DistCarouselItemData createDistHeroRotatingItemFromHeroTeaser(final AbstractDistHeroRotatingTeaserModel teaserModel,
                                                                            final HttpServletRequest request) {
        return convert(teaserModel, request);
    }

    protected ImageData getImageDataFromListByType(final String imageType, final List<Map<String, ImageData>> images) {
        if (CollectionUtils.isNotEmpty(images)) {
            return getImageDataFromMapByType(imageType, images.get(0));
        }
        return null;
    }

    protected ImageData getImageDataFromMapByType(final String imageType, final Map<String, ImageData> image) {
        if (MapUtils.isNotEmpty(image)) {
            return image.get(imageType);
        }
        return null;
    }

    private DistCarouselItemData convert(final AbstractDistHeroRotatingTeaserModel teaserModel, final HttpServletRequest request) {
        final DistCarouselItemData target = new DistCarouselItemData();

        // Full screen teaser?
        if (teaserModel instanceof DistHeroRotatingTeaserWithTextModel) {
            target.setTitle(((DistHeroRotatingTeaserWithTextModel) teaserModel).getTitle());
            target.setName(((DistHeroRotatingTeaserWithTextModel) teaserModel).getText());
            target.setPromotionText(((DistHeroRotatingTeaserWithTextModel) teaserModel).getSubText());
        } else { // Without text, the image is bigger
            target.setFullsize(true);
        }

        if (teaserModel.getLink() != null) {
            target.setUrl(Functions.getUrlForCMSLinkComponent(teaserModel.getLink(), request));
            target.setLinkTarget(teaserModel.getLink().getTarget());
        }
        if (teaserModel.getPicture() != null) {
            target.setPicture(imageConverter.convert(teaserModel.getPicture()));
        }
        if (teaserModel.getThumbnail() != null) {
            target.setThumbnail(imageConverter.convert(teaserModel.getThumbnail()));
        }
        return target;
    }
}
