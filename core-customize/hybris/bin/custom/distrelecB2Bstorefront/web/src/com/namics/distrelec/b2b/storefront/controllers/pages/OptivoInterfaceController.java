/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.security.exceptions.CatalogPlusItemAccessException;

import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * {@code OptivoInterfaceController}
 * 
 * 
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
@Controller
@RequestMapping(value = OptivoInterfaceController.OPTIVO_INTERFACE_PAGE_REQUEST_MAPPING)
public class OptivoInterfaceController extends ProductPageController {

    private static final Logger LOG = LogManager.getLogger(OptivoInterfaceController.class);
    public static final String OPTIVO_INTERFACE_PAGE_REQUEST_MAPPING = "/optivo";
    public static final String COMPUTING_AND_HOBBY_CATEGORY_CODE = "cat-L1D_379523";

    @Autowired
    private CommonI18NService commonI18NService;

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST }, produces = { "application/xml" })
    public String get(@RequestParam(value = "id", required = false)
    final String productCode, @RequestParam(value = "language", required = false, defaultValue = "en")
    final String lang, final Model model, final HttpServletRequest request) throws CMSItemNotFoundException {

        try {
            if (!getProductFacade().isProductBuyable(productCode)) {
                throw new UnknownIdentifierException(PUNCHOUT_ERROR_MESSAGE);
            }
            setRequestLocale(lang);
            final ProductModel productModel = getProductService().getProductForCode(productCode);

            if (productModel.getCatPlusSupplierAID() != null) {
                // CatalogPlus items are not available for guests and B2C customers
                if (getUserService().isAnonymousUser(getUserService().getCurrentUser())
                        || ((B2BCustomerModel) getUserService().getCurrentUser()).getCustomerType() == CustomerType.B2C) {
                    throw new CatalogPlusItemAccessException("Service+ products are available only for authenticated B2B customers");
                }
            }

            checkProductChannel(productModel);

            populateProductDetailForDisplay(productModel, model, request);
        } catch (final UnknownIdentifierException uie) {
            LOG.error("No product found for code {}",productCode);
        } catch (final Exception exp) {
            LOG.error("No product found for code {} \n{}" ,productCode,exp.getMessage(), exp);
        }

        return ControllerConstants.Views.Fragments.Product.OptivoProduct;
    }

    /**
     * Check if the product is member of Computing & Hobby category. If it is the case, then switch the channel to B2C.
     * 
     * @param productModel
     */
    private void checkProductChannel(final ProductModel productModel) {
        if (isComputerAndHobbyItem(productModel.getSupercategories()) && !"B2C".equals(getStoreSessionFacade().getCurrentChannel().getType())) {
            getStoreSessionFacade().setCurrentChannel("B2C");
        }
    }

    /**
     * Check whether one
     * 
     * @param categories
     * @return {@code true} if at least one
     */
    private boolean isComputerAndHobbyItem(final Collection<CategoryModel> categories) {
        if (categories != null) {
            for (final CategoryModel category : categories) {
                if (COMPUTING_AND_HOBBY_CATEGORY_CODE.equals(category.getCode()) || isComputerAndHobbyItem(category.getSupercategories())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 
     * @param productModel
     * @param model
     * @param request
     * @throws CMSItemNotFoundException
     */
    protected void populateProductDetailForDisplay(final ProductModel productModel, final Model model, final HttpServletRequest request)
            throws CMSItemNotFoundException {

        final ProductData productData;

        if (productModel.getCatPlusSupplierAID() != null) {
            productData = getProductFacade().getProductForCodeAndOptions(productModel.getCode(),
                    Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY,
                            ProductOption.VOLUME_PRICES, ProductOption.ACCESSORIES, ProductOption.SIMILAR_PRODUCTS, ProductOption.PROMOTION_LABELS,
                            ProductOption.COUNTRY_OF_ORIGIN, ProductOption.DIST_MANUFACTURER, ProductOption.DOWNLOADS, ProductOption.PRODUCT_INFORMATION));
        } else {
            productData = getProductFacade().getProductForCodeAndOptions(productModel.getCode(),
                    Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION, ProductOption.GALLERY,
                            ProductOption.CATEGORIES, ProductOption.REVIEW, ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION, ProductOption.VARIANT_FULL,
                            ProductOption.STOCK, ProductOption.VOLUME_PRICES, ProductOption.ACCESSORIES, ProductOption.SIMILAR_PRODUCTS,
                            ProductOption.PROMOTION_LABELS, ProductOption.COUNTRY_OF_ORIGIN, ProductOption.DIST_MANUFACTURER, ProductOption.DOWNLOADS,
                            ProductOption.VIDEOS, ProductOption.PRODUCT_INFORMATION));
        }

        final String baseURL = getBaseURL(request);
        model.addAttribute("baseURL", baseURL);
        model.addAttribute("linkUrl", getProductUrlResolver(productModel).resolve(productModel));

        populateProductData(productData, model, request, null);
        populateProductTitle(productData, model);
        model.addAttribute("productData", productData);

        boolean foundImage = false;
        if (productModel.getPrimaryImage() != null && !CollectionUtils.isEmpty(productModel.getPrimaryImage().getMedias())) {
            final Iterator<MediaModel> iter = productModel.getPrimaryImage().getMedias().iterator();

            while (iter.hasNext()) {
                final MediaModel media = iter.next();
                if ("landscape_medium".equalsIgnoreCase(media.getMediaFormat().getQualifier())) {
                    model.addAttribute("imageURL", baseURL + media.getURL());
                    foundImage = true;
                }
            }
        }

        // if there is no primary image, look for an other one
        if (!foundImage) {
            if (productModel.getIllustrativeImage() != null && !CollectionUtils.isEmpty(productModel.getIllustrativeImage().getMedias())) {
                final Iterator<MediaModel> iter = productModel.getIllustrativeImage().getMedias().iterator();

                while (iter.hasNext()) {
                    final MediaModel media = iter.next();
                    if ("landscape_medium".equalsIgnoreCase(media.getMediaFormat().getQualifier())) {
                        model.addAttribute("imageURL", baseURL + media.getURL());
                        foundImage = true;
                    }
                }
            }
        }

    }

    /**
     * Extract the URL (protocol://hostname:port) used to make the request
     * 
     * @param request
     *            the request object
     * @return the URL
     */
    private String getBaseURL(final HttpServletRequest request) {
        final String requestURL = request.getRequestURL().toString();
        return requestURL.substring(0, requestURL.indexOf(OPTIVO_INTERFACE_PAGE_REQUEST_MAPPING));
    }

    /**
     * Set the locale
     * 
     * @param lang
     *            the request locale.
     */
    private void setRequestLocale(final String lang) {
        if (lang != null) {
            try {
                getCommonI18NService().setCurrentLanguage(getCommonI18NService().getLanguage(lang.trim()));
            } catch (final Exception exp) {
                LOG.error("Unknown language iso-code : {}", lang);
            }
        }
    }

    @Override
    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Override
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
