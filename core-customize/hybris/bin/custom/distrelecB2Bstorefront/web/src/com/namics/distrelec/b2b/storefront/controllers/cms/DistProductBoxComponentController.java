/*
 * Copyright 2000-2016 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.namics.distrelec.b2b.core.model.cms2.components.DistProductBoxComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.items.AbstractDistCarpetContentTeaserModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserWithTextModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetItemModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.cms.data.DistCarpetContentTeaserData;
import com.namics.distrelec.b2b.facades.cms.data.DistCarpetItemData;
import com.namics.distrelec.b2b.facades.cms.data.DistProductBoxComponentData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.tags.Functions;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.i18n.L10NService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * {@code DistProductBoxComponentController}
 *
 * @author datneerajs, Distrelec Group AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec Group AG
 * @since Distrelec 5.6
 */
@Controller("DistProductBoxComponentController")
@Scope("tenant")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistProductBoxComponent)
public class DistProductBoxComponentController extends AbstractDistCMSComponentController<DistProductBoxComponentModel> {

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Autowired
    private UserService userService;

    @Autowired
    private Converter<MediaModel, ImageData> imageConverter;

    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.DIST_MANUFACTURER,
                                                                               ProductOption.PROMOTION_LABELS);

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private DistProductService productService;

    /*
     * (non-Javadoc)
     * @see
     * com.namics.distrelec.b2b.storefront.controllers.cms.AbstractCMSComponentController#fillModel(javax.servlet.http.HttpServletRequest,
     * org.springframework.ui.Model, de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)
     */
    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistProductBoxComponentModel component) {

        final UserModel currentUser = getUserService().getCurrentUser();
        if (component.isCheckout() && currentUser instanceof B2BCustomerModel) {
            if (!((B2BCustomerModel) currentUser).isShowProductBox()) {
                // Don't show the component for the logged in customers who don't want to see this component.
                return;
            }
        }

        DistProductBoxComponentData data;

        if (component.isHeroProduct()) {
            final List<ProductModel> heroProducts = getProductService().getHeroProducts();
            if (CollectionUtils.isNotEmpty(heroProducts)) {
                data = createProductBoxData(component);
                data.setItems(collectItems(heroProducts));
            } else {
                // If no hero product defined then we don't show anything
                return;
            }
        } else {
            data = createProductBoxData(component);
            data.setItems(collectItems(component, request));
        }
        model.addAttribute("productBoxData", data);
    }

    protected DistProductBoxComponentData createProductBoxData(final DistProductBoxComponentModel component) {
        final DistProductBoxComponentData data = new DistProductBoxComponentData();
        data.setHorizontal(component.isHorizontal());
        data.setRotating(component.isRotating());
        data.setShowLogo(component.isShowLogo());
        data.setCheckout(component.isCheckout());
        data.setHeroProduct(component.isHeroProduct());
        data.setTitle(component.getTitle(getI18nService().getCurrentLocale()));
        return data;
    }

    /**
     * Convert the carpet items to carpet item data
     * 
     * @param component
     * @param request
     * @return a list of {@code DistCarpetItemData}
     */
    protected List<DistCarpetItemData> collectItems(final DistProductBoxComponentModel component, final HttpServletRequest request) {
        final List<DistCarpetItemData> datas = new ArrayList<>();

        for (final DistCarpetItemModel carpetModel : component.getItems()) {
            datas.add(convert(carpetModel, request));
        }

        return datas;
    }

    /**
     * Convert the products to carpet item data
     * 
     * @param products
     * @return a list of {@code DistCarpetItemData}
     */
    protected List<DistCarpetItemData> collectItems(final List<ProductModel> products) {
        final List<DistCarpetItemData> datas = new ArrayList<>();

        for (final ProductModel product : products) {
            if (product != null) {
                datas.add(convert(product));
            }
        }

        return datas;
    }

    /**
     * Convert a product to a carpet item data
     * 
     * @param product
     * @return a new instance of {@code DistCarpetItemData}
     */
    protected DistCarpetItemData convert(final ProductModel product) {
        final DistCarpetItemData itemData = new DistCarpetItemData();
        itemData.setProduct(productFacade.getProductForOptions(product, PRODUCT_OPTIONS));
        if (productFacade.getRelevantSalesUnit(product.getCode()) != null) {
            itemData.getProduct().setSalesUnit(productFacade.getRelevantSalesUnit(product.getCode()));
        }
        return itemData;
    }

    protected DistCarpetItemData convert(final DistCarpetItemModel itemModel, final HttpServletRequest request) {
        final DistCarpetItemData itemData = new DistCarpetItemData();

        // Teaser
        if (itemModel.getContentTeaser() != null) {
            final AbstractDistCarpetContentTeaserModel teaserModel = itemModel.getContentTeaser();
            final DistCarpetContentTeaserData contentTeaserData = new DistCarpetContentTeaserData();

            // Full screen teaser?
            if (teaserModel instanceof DistCarpetContentTeaserWithTextModel) {
                final DistCarpetContentTeaserWithTextModel teaserWithTextModel = (DistCarpetContentTeaserWithTextModel) teaserModel;
                contentTeaserData.setTitle(teaserWithTextModel.getTitle());
                contentTeaserData.setText(teaserWithTextModel.getText());
                contentTeaserData.setSubText(teaserWithTextModel.getSubText());
            } else {
                contentTeaserData.setFullsize(true);
            }

            if (teaserModel.getLink() != null) {
                contentTeaserData.setLink(Functions.getUrlForCMSLinkComponent(teaserModel.getLink(), request));
                contentTeaserData.setLinkTarget(teaserModel.getLink().getTarget());
            }

            if (teaserModel.getImageSmall() != null) {
                contentTeaserData.setImageSmall(imageConverter.convert(teaserModel.getImageSmall()));
            }
            if (teaserModel.getImageLarge() != null) {
                contentTeaserData.setImageLarge(imageConverter.convert(teaserModel.getImageLarge()));
            }

            itemData.setContentTeaser(contentTeaserData);

            // Is teaser
            itemData.setIsTeaser(Boolean.TRUE);
        }

        // Product
        if (itemModel.getProduct() != null) {
            final ProductData product = productFacade.getProductForOptions(itemModel.getProduct(), PRODUCT_OPTIONS);
            if (productFacade.getRelevantSalesUnit(product.getCode()) != null) {
                product.setSalesUnit(productFacade.getRelevantSalesUnit(product.getCode()));
            }
            itemData.setProduct(product);
            // Is product
            itemData.setIsTeaser(Boolean.FALSE);
        }

        // Size
        itemData.setSize(itemModel.getSize());
        return itemData;
    }

    public L10NService getL10nService() {
        return l10nService;
    }

    public void setL10nService(final L10NService l10nService) {
        this.l10nService = l10nService;
    }

    public Converter<MediaModel, ImageData> getImageConverter() {
        return imageConverter;
    }

    public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter) {
        this.imageConverter = imageConverter;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public DistWebtrekkFacade getDistWebtrekkFacade() {
        return distWebtrekkFacade;
    }

    public void setDistWebtrekkFacade(final DistWebtrekkFacade distWebtrekkFacade) {
        this.distWebtrekkFacade = distWebtrekkFacade;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public DistProductService getProductService() {
        return productService;
    }

    public void setProductService(DistProductService productService) {
        this.productService = productService;
    }
}
