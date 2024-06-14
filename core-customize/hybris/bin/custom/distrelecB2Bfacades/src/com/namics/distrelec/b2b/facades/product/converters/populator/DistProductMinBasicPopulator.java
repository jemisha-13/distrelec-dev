/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.converters.populator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants;

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commercefacades.product.converters.populator.ProductBasicPopulator;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * {@code DistProductExtendedBasicPopulator}
 * <p>
 * Custom implementation of ProductBasicPopulator. Some additional basic attributes are set here.
 * </p>
 *
 * @param <SOURCE>
 * @param <TARGET>
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.6
 */
public class DistProductMinBasicPopulator<SOURCE extends ProductModel, TARGET extends ProductData> extends ProductBasicPopulator<SOURCE, TARGET> {

    @Autowired
    @Qualifier("mediaContainerToImageMapConverter")
    private Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter;

    @Autowired
    @Qualifier("defaultDistCategoryModelUrlResolver")
    private DistUrlResolver<CategoryModel> categoryUrlResolver;

    @Override
    public void populate(final SOURCE productModel, final TARGET productData) {
        productData.setCode((String) getProductAttribute(productModel, ProductModel.CODE));
        productData.setCodeErpRelevant((String) getProductAttribute(productModel, ProductModel.CODEERPRELEVANT));
        productData.setTypeName((String) getProductAttribute(productModel, ProductModel.TYPENAME));
        productData.setPromotionText((String) getProductAttribute(productModel, ProductModel.PROMOTIONTEXT));

        addPrimaryImageAsFirst(productModel, productData);

        productData.setProductFamilyUrl(getProductFamilyUrl(productModel.getPrimarySuperCategory(), productModel.getPimFamilyCategoryCode()));

        // DISTRELEC-8887 : Display "old" article number on product detail page
        productData.setMovexArticleNumber(productModel.getCodeMovex() == null ? "" : productModel.getCodeMovex());
        productData.setNavisionArticleNumber(productModel.getCodeNavision() == null ? "" : productModel.getCodeNavision());
        productData.setElfaArticleNumber(productModel.getCodeElfa() == null ? "" : productModel.getCodeElfa());

        super.populate(productModel, productData);
    }

    protected void addPrimaryImageAsFirst(final ProductModel productModel, final ProductData b2bProductData) {
        if (productModel.getPrimaryImage() != null || productModel.getIllustrativeImage() != null) {
            final List<Map<String, ImageData>> images = new ArrayList<>();

            if (productModel.getPrimaryImage() != null) {
                images.add(mediaContainerToImageMapConverter.convert(productModel.getPrimaryImage()));
            } else if (productModel.getIllustrativeImage() != null) {
                images.add(mediaContainerToImageMapConverter.convert(productModel.getIllustrativeImage()));
                b2bProductData.setIllustrativeImage(true);
            }

            b2bProductData.setProductImages(images);
        }
    }

    protected String getProductFamilyUrl(final CategoryModel pimFamilyCategory, final String pimFamilyCategoryCode) {
        return pimFamilyCategory == null ? null : //
                                         new StringBuffer(getCategoryUrlResolver().resolve(pimFamilyCategory)) //
                                                                                                              .append("?q=*&filter_") //
                                                                                                              .append(DistrelecfactfindersearchConstants.PRODUCT_FAMILY_CODE) //
                                                                                                              .append("=") //
                                                                                                              .append(pimFamilyCategoryCode) //
                                                                                                              .toString();
    }

    public Converter<MediaContainerModel, Map<String, ImageData>> getMediaContainerToImageMapConverter() {
        return mediaContainerToImageMapConverter;
    }

    public void setMediaContainerToImageMapConverter(final Converter<MediaContainerModel, Map<String, ImageData>> mediaContainerToImageMapConverter) {
        this.mediaContainerToImageMapConverter = mediaContainerToImageMapConverter;
    }

    public DistUrlResolver<CategoryModel> getCategoryUrlResolver() {
        return categoryUrlResolver;
    }

    public void setCategoryUrlResolver(final DistUrlResolver<CategoryModel> categoryUrlResolver) {
        this.categoryUrlResolver = categoryUrlResolver;
    }
}
