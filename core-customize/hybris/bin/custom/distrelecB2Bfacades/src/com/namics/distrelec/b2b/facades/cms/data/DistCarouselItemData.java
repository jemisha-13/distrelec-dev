/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.cms.data;

import com.namics.distrelec.b2b.facades.product.data.DistPromotionLabelData;
import de.hybris.platform.cms2.enums.LinkTargets;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.PriceData;

import java.util.Collection;

/**
 * DistHeroRotatingItem Data Object
 */
public class DistCarouselItemData {

    private ImageData picture;
    private ImageData thumbnail;
    private ImageData manufacturerImage;
    private String productCode;
    private String title;
    private String name;
    private String promotionText;
    private String url;
    private LinkTargets linkTarget;
    private PriceData price;
    private DistPromotionLabelData activePromotion;
    private String manufacturer;
    private Collection<CategoryData> categories;
    private String codeErpRelevant;
    private String typeName;
    private boolean fullsize;

    public ImageData getPicture() {
        return picture;
    }

    public void setPicture(final ImageData picture) {
        this.picture = picture;
    }

    public ImageData getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(final ImageData thumbnail) {
        this.thumbnail = thumbnail;
    }

    public ImageData getManufacturerImage() {
        return manufacturerImage;
    }

    public void setManufacturerImage(final ImageData manufacturerImage) {
        this.manufacturerImage = manufacturerImage;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(final String productCode) {
        this.productCode = productCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPromotionText() {
        return promotionText;
    }

    public void setPromotionText(final String promotionText) {
        this.promotionText = promotionText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public LinkTargets getLinkTarget() {
        return linkTarget;
    }

    public void setLinkTarget(final LinkTargets linkTarget) {
        this.linkTarget = linkTarget;
    }

    public PriceData getPrice() {
        return price;
    }

    public void setPrice(final PriceData price) {
        this.price = price;
    }

    public DistPromotionLabelData getActivePromotion() {
        return activePromotion;
    }

    public void setActivePromotion(final DistPromotionLabelData activePromotion) {
        this.activePromotion = activePromotion;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(final String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Collection<CategoryData> getCategories() {
        return categories;
    }

    public void setCategories(final Collection<CategoryData> categories) {
        this.categories = categories;
    }

    public String getCodeErpRelevant() {
        return codeErpRelevant;
    }

    public void setCodeErpRelevant(final String codeErpRelevant) {
        this.codeErpRelevant = codeErpRelevant;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(final String typeName) {
        this.typeName = typeName;
    }

    public boolean isFullsize() {
        return fullsize;
    }

    public void setFullsize(final boolean fullsize) {
        this.fullsize = fullsize;
    }

}
