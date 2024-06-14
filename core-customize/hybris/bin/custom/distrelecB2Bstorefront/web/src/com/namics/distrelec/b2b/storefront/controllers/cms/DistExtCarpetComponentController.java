/*
 * Copyright 2000-2014 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.cms2.components.DistExtCarpetComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.items.AbstractDistCarpetContentTeaserModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserWithTextModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistExtCarpetItemModel;
import com.namics.distrelec.b2b.facades.cms.data.DistCarpetContentTeaserData;
import com.namics.distrelec.b2b.facades.cms.data.DistExtCarpetItemData;
import com.namics.distrelec.b2b.facades.product.converters.populator.ProductVolumePricesPopulator;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.facades.webtrekk.data.TeaserProductsDataLayer;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.tags.Functions;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.L10NService;

/**
 * {@code DistExtCarpetComponentController}
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 3.0
 */

@Controller("DistExtCarpetComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistExtCarpetComponent)
public class DistExtCarpetComponentController extends AbstractDistCMSComponentController<DistExtCarpetComponentModel> {

    private static final Logger LOG = Logger.getLogger(DistExtCarpetComponentController.class);

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Autowired
    private Converter<MediaModel, ImageData> imageConverter;

    protected static final int SEARCH_RESULT_LIMIT = 100; // Limit to 100 matching results

    protected static final int SEARCH_RESULT_MIN = 4; // Min 4 matching results

    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.DIST_MANUFACTURER,
                                                                               ProductOption.VOLUME_PRICES, ProductOption.PROMOTION_LABELS,
                                                                               ProductOption.CLASSIFICATION);

    @Autowired
    @Qualifier("productFacade")
    private ProductFacade productFacade;

    @Autowired
    @Qualifier("productSearchFacade")
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistExtCarpetComponentModel component) {
        final List<ProductData> products = new ArrayList<>();
        List<DistExtCarpetItemData> column1Items = new ArrayList<>();
        List<DistExtCarpetItemData> column2Items = new ArrayList<>();
        List<DistExtCarpetItemData> column3Items = new ArrayList<>();
        List<DistExtCarpetItemData> column4Items = new ArrayList<>();

        // Search query case?
        if (component.getSearchQuery() != null) {
            products.addAll(collectSearchProducts(request, model, component));
            model.addAttribute("search", Boolean.TRUE);
        }

        // Manual case
        // Check if all columns have the same length
        if (checkColumnsLength(component)) {
            column1Items = collectColumnItems(component.getCarpetColumn1Items(), request);
            column2Items = collectColumnItems(component.getCarpetColumn2Items(), request);
            column3Items = collectColumnItems(component.getCarpetColumn3Items(), request);
            column4Items = collectColumnItems(component.getCarpetColumn4Items(), request);
            model.addAttribute("search", Boolean.FALSE);
            final Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            if ((String) model.asMap().get("teaserProductsDataLayer") != null) {
                final List<TeaserProductsDataLayer> teaserProductsDataLayers = (List<TeaserProductsDataLayer>) model.asMap().get("teaserProductsDataLayer");
                teaserProductsDataLayers.addAll(getTeaserProductsDataLayer(column1Items, column2Items, column3Items, column4Items));
                model.addAttribute("teaserProductsDataLayer", gson.toJson(teaserProductsDataLayers));
            } else {
                model.addAttribute("teaserProductsDataLayer", gson.toJson(getTeaserProductsDataLayer(column1Items, column2Items, column3Items, column4Items)));
            }
        }

        model.addAttribute("carpetData", products);

        model.addAttribute("column1Items", column1Items);
        model.addAttribute("column2Items", column2Items);
        model.addAttribute("column3Items", column3Items);
        model.addAttribute("column4Items", column4Items);

    }

    private List<TeaserProductsDataLayer> getTeaserProductsDataLayer(final List<DistExtCarpetItemData> column1Items,
                                                                     final List<DistExtCarpetItemData> column2Items,
                                                                     final List<DistExtCarpetItemData> column3Items,
                                                                     final List<DistExtCarpetItemData> column4Items) {
        final List<TeaserProductsDataLayer> teaserProductsDataLayers = new ArrayList<>();
        int placement = 1;
        placement = addTeaserProductsDataLayer(column1Items, teaserProductsDataLayers, placement);
        placement = addTeaserProductsDataLayer(column2Items, teaserProductsDataLayers, placement);
        placement = addTeaserProductsDataLayer(column3Items, teaserProductsDataLayers, placement);
        placement = addTeaserProductsDataLayer(column4Items, teaserProductsDataLayers, placement);
        return teaserProductsDataLayers;
    }

    private int addTeaserProductsDataLayer(final List<DistExtCarpetItemData> columnItems, final List<TeaserProductsDataLayer> teaserProductsDataLayers,
                                           int placement) {
        for (final DistExtCarpetItemData columnItem : columnItems) {
            if (columnItem.getProduct() != null) {
                final TeaserProductsDataLayer teaserProductsDataLayer = new TeaserProductsDataLayer();
                teaserProductsDataLayer.setProductID(columnItem.getProduct().getCode());
                teaserProductsDataLayer.setInstrument(DistConstants.Webtrekk.TEASER_TRACKING_ONS);
                teaserProductsDataLayer.setPlacement(placement);
                teaserProductsDataLayers.add(teaserProductsDataLayer);
                placement = placement + 1;
            }
        }
        return placement;
    }

    protected boolean checkColumnsLength(final DistExtCarpetComponentModel component) {
        if (component.getCarpetColumn1Items().size() + component.getCarpetColumn2Items().size() + component.getCarpetColumn3Items().size()
            + component.getCarpetColumn4Items().size() != 0) {

            // Ensure that all columns have at least 1 element.
            if (component.getCarpetColumn1Items().isEmpty() || component.getCarpetColumn2Items().isEmpty() || component.getCarpetColumn3Items().isEmpty()
                    || component.getCarpetColumn4Items().isEmpty()) {
                return false;
            }

            final int sizeColumn1 = component.getCarpetColumn1Items().size();

            // Compare the sizes of different columns
            if (sizeColumn1 != component.getCarpetColumn2Items().size() || sizeColumn1 != component.getCarpetColumn3Items().size()
                    || sizeColumn1 != component.getCarpetColumn4Items().size()) {
                LOG.error(l10nService.getLocalizedString("validations.distcarpet.colomnlengthnotequal"));
                return false;
            }

            return true;
        }
        LOG.error(l10nService.getLocalizedString("validations.distcarpet.noitem"));
        return false;
    }

    protected List<DistExtCarpetItemData> collectColumnItems(final List<DistExtCarpetItemModel> items, final HttpServletRequest request) {
        final List<DistExtCarpetItemData> datas = new ArrayList<>();

        for (final DistExtCarpetItemModel carpetModel : items) {
            datas.add(convert(carpetModel, request));
        }

        return datas;
    }

    protected List<ProductData> collectSearchProducts(final HttpServletRequest request, final Model model, final DistExtCarpetComponentModel component) {
        List<ProductData> products = new ArrayList<>();
        final String searchQuery = component.getSearchQuery();
        final Integer maxSearchResults = component.getMaxSearchResults();

        if (StringUtils.isNotBlank(searchQuery)) {
            final SearchStateData searchState = new SearchStateData();
            final SearchQueryData searchQueryData = new SearchQueryData();
            searchQueryData.setValue(searchQuery);
            searchState.setQuery(searchQueryData);

            final PageableData pageableData = new PageableData();
            if (maxSearchResults == null || maxSearchResults.equals(Integer.valueOf(0))) {
                pageableData.setPageSize(SEARCH_RESULT_LIMIT);
            } else {
                pageableData.setPageSize(maxSearchResults.intValue());
            }

            products = productSearchFacade.search(searchState, pageableData).getResults();
        }

        // Test if we have enough products
        if (CollectionUtils.isNotEmpty(products) && products.size() >= SEARCH_RESULT_MIN) {
            distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_FAF_ONSITE);
            return products;
        }

        return Collections.EMPTY_LIST;
    }

    protected DistExtCarpetItemData convert(final DistExtCarpetItemModel itemModel, final HttpServletRequest request) {
        final DistExtCarpetItemData itemData = new DistExtCarpetItemData();

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
            teaserModel.setYouTubeID(teaserModel.getYouTubeID());
            itemData.setContentTeaser(contentTeaserData);

            // Is teaser
            itemData.setIsTeaser(Boolean.TRUE);
        }

        // Product
        if (itemModel.getProduct() != null) {
            final ProductData product = productFacade.getProductForCodeAndOptions(itemModel.getProduct().getCode(), PRODUCT_OPTIONS);
            itemData.setProduct(product);
            // Is product
            itemData.setIsTeaser(Boolean.FALSE);

            // Original Price text
            // Creating a price object.
            PriceData price = product.getPrice();
            itemData.setPrice(price);
            itemData.setShowOriginalPrice(itemModel.getShowOriginalPrice());

            if (BooleanUtils.isTrue(itemData.getShowOriginalPrice()) && //
                    product.getVolumePricesMap().containsKey(price.getMinQuantity())) { // check if the volume price map
                                                                                        // contains
                                                                                        // the correct scale

                // product.getVolumePrices().get(Long.valueOf(price.getMinQuantity())).containsKey("list") && // check if the list price for
                // the given scale exists
                // product.getVolumePrices().get(Long.valueOf(price.getMinQuantity())).containsKey("custom") // check if the list price for
                // the given scale exists
                if (product.getVolumePricesMap().get(price.getMinQuantity()).containsKey("list")) {
                    itemData.setOriginalPrice(product.getVolumePricesMap().get(price.getMinQuantity()).get("list"));
                }
                if (product.getVolumePricesMap().get(price.getMinQuantity()).containsKey("custom")) {
                    itemData.setPrice(product.getVolumePricesMap().get(price.getMinQuantity()).get("custom"));
                } else if (itemData.getOriginalPrice() != null) {
                    // Calculate the saving here
                    ProductVolumePricesPopulator.calculateSaving(product, itemData.getOriginalPrice(), price);
                }
            }

            itemData.setYouTubeID(itemModel.getYouTubeID());
        }

        return itemData;
    }

}
