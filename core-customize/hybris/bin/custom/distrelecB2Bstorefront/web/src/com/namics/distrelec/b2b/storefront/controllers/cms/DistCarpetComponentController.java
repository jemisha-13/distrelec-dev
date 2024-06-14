/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.cms;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.DistCarpetItemSize;
import com.namics.distrelec.b2b.core.model.cms2.components.DistCarpetComponentModel;
import com.namics.distrelec.b2b.core.model.cms2.items.AbstractDistCarpetContentTeaserModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetContentTeaserWithTextModel;
import com.namics.distrelec.b2b.core.model.cms2.items.DistCarpetItemModel;
import com.namics.distrelec.b2b.facades.cms.data.DistCarpetContentTeaserData;
import com.namics.distrelec.b2b.facades.cms.data.DistCarpetItemData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.distrelec.b2b.facades.webtrekk.DistWebtrekkFacade;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.tags.Functions;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.L10NService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Controller for DistProductCarpetComponent
 */
@Controller("DistCarpetComponentController")
@RequestMapping(value = ControllerConstants.Actions.Cms.DistCarpetComponent)
public class DistCarpetComponentController extends AbstractDistCMSComponentController<DistCarpetComponentModel> {

    private static final Logger LOG = Logger.getLogger(DistCarpetComponentController.class);

    @Autowired
    @Qualifier("l10nService")
    private L10NService l10nService;

    @Autowired
    private Converter<MediaModel, ImageData> imageConverter;

    protected static final int COLUMN_MIN_ITEMS = 2; // Need to have 2 or more items
    protected static final int SEARCH_RESULT_LIMIT = 100; // Limit to 100 matching results
    protected static final int SEARCH_RESULT_MIN = 5; // Min 5 matching results
    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.DIST_MANUFACTURER,
            ProductOption.PROMOTION_LABELS);

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("productSearchFacade")
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    @Qualifier("webtrekkFacade")
    private DistWebtrekkFacade distWebtrekkFacade;

    @Override
    protected void fillModel(final HttpServletRequest request, final Model model, final DistCarpetComponentModel component) {
        final List<ProductData> products = new ArrayList<ProductData>();
        List<DistCarpetItemData> column1Items = new ArrayList<DistCarpetItemData>();
        List<DistCarpetItemData> column2Items = new ArrayList<DistCarpetItemData>();
        List<DistCarpetItemData> column3Items = new ArrayList<DistCarpetItemData>();

        // Search query case?
        if (component.getSearchQuery() != null) {
            products.addAll(collectSearchProducts(request, model, component));

            model.addAttribute("search", Boolean.TRUE);
        }

        // Manual case
        // Check if all columns have the same length
        if (checkColumnsLength(component)) {
            column1Items = collectColumn1Items(component, request);
            column2Items = collectColumn2Items(component, request);
            column3Items = collectColumn3Items(component, request);

            model.addAttribute("search", Boolean.FALSE);
        }
        model.addAttribute("carpetData", products);

        model.addAttribute("column1Items", column1Items);
        model.addAttribute("column2Items", column2Items);
        model.addAttribute("column3Items", column3Items);

    }

    protected boolean checkColumnsLength(final DistCarpetComponentModel component) {
        if (component.getCarpetColumn1Items().size() + component.getCarpetColumn2Items().size() + component.getCarpetColumn3Items().size() != 0) {
            int sizeColumn1 = 0;
            int sizeColumn2 = 0;
            int sizeColumn3 = 0;

            // Column 1
            if (component.getCarpetColumn1Items().size() < COLUMN_MIN_ITEMS) {
                LOG.warn("Component " + component.getUid() + ": column 1 size is less than minimum required (" + COLUMN_MIN_ITEMS + ")");
                return false;
            }

            for (final DistCarpetItemModel item : component.getCarpetColumn1Items()) {
                if (item.getSize() == DistCarpetItemSize.SMALL) {
                    sizeColumn1 += 1;
                } else { // DistCarpetItemSize.LARGE
                    sizeColumn1 += 2;
                }
            }

            // Column 2
            if (component.getCarpetColumn2Items().size() < COLUMN_MIN_ITEMS) {
                LOG.warn("Component " + component.getUid() + ": column 2 size is less than minimum required (" + COLUMN_MIN_ITEMS + ")");
                return false;
            }

            for (final DistCarpetItemModel item : component.getCarpetColumn2Items()) {
                if (item.getSize() == DistCarpetItemSize.SMALL) {
                    sizeColumn2 += 1;
                } else { // DistCarpetItemSize.LARGE
                    sizeColumn2 += 2;
                }
            }
            // Compare col 1 vs col2
            if (sizeColumn1 != sizeColumn2) {
                LOG.error(l10nService.getLocalizedString("validations.distcarpet.colomnlengthnotequal"));
                return false;
            }

            // Column 3
            if (component.getCarpetColumn3Items().size() < COLUMN_MIN_ITEMS) {
                LOG.warn("Component " + component.getUid() + ": column 3 size is less than minimum required (" + COLUMN_MIN_ITEMS + ")");
                return false;
            }

            for (final DistCarpetItemModel item : component.getCarpetColumn3Items()) {
                if (item.getSize() == DistCarpetItemSize.SMALL) {
                    sizeColumn3 += 1;
                } else { // DistCarpetItemSize.LARGE
                    sizeColumn3 += 2;
                }
            }

            // Compare col1 vs vol3 & col2 vs col3
            if (sizeColumn1 != sizeColumn3 || sizeColumn2 != sizeColumn3) {
                LOG.error(l10nService.getLocalizedString("validations.distcarpet.columnslengthnotequal"));
                return false;
            }

            return true;
        }
        LOG.error(l10nService.getLocalizedString("validations.distcarpet.noitem"));
        return false;

    }

    protected List<DistCarpetItemData> collectColumn1Items(final DistCarpetComponentModel component, final HttpServletRequest request) {
        final List<DistCarpetItemData> datas = new ArrayList<DistCarpetItemData>();

        for (final DistCarpetItemModel carpetModel : component.getCarpetColumn1Items()) {
            datas.add(convert(carpetModel, request));
        }

        return datas;
    }

    protected List<DistCarpetItemData> collectColumn2Items(final DistCarpetComponentModel component, final HttpServletRequest request) {
        final List<DistCarpetItemData> datas = new ArrayList<DistCarpetItemData>();

        for (final DistCarpetItemModel carpetModel : component.getCarpetColumn2Items()) {
            datas.add(convert(carpetModel, request));
        }

        return datas;
    }

    protected List<DistCarpetItemData> collectColumn3Items(final DistCarpetComponentModel component, final HttpServletRequest request) {
        final List<DistCarpetItemData> datas = new ArrayList<DistCarpetItemData>();

        for (final DistCarpetItemModel carpetModel : component.getCarpetColumn3Items()) {
            datas.add(convert(carpetModel, request));
        }

        return datas;
    }

    protected List<ProductData> collectSearchProducts(final HttpServletRequest request, final Model model, final DistCarpetComponentModel component) {
        List<ProductData> products = new ArrayList<ProductData>();
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

        // Test if we have enougth products
        if (CollectionUtils.isNotEmpty(products) && products.size() >= SEARCH_RESULT_MIN) {
            distWebtrekkFacade.addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_FAF_ONSITE);
            return products;
        }

        return Collections.EMPTY_LIST;
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
            final ProductData product = productFacade.getProductForCodeAndOptions(itemModel.getProduct().getCode(), PRODUCT_OPTIONS);
            itemData.setProduct(product);

            // Is product
            itemData.setIsTeaser(Boolean.FALSE);
        }

        // Size
        itemData.setSize(itemModel.getSize());
        return itemData;
    }
}
