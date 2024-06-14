/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.storefront.controllers.pages;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.ProductListBulkTools;
import com.namics.distrelec.b2b.core.enums.ProductListOrder;
import com.namics.distrelec.b2b.core.service.order.exceptions.ProductNotBuyableException;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.export.DistExportFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.wishlist.DistShoppingListFacade;
import com.namics.distrelec.b2b.facades.wishlist.data.MiniWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistData;
import com.namics.distrelec.b2b.facades.wishlist.data.NamicsWishlistEntryData;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.ShoppingListBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;

/**
 * Controller for shopping list page.
 */
@Controller
@RequestMapping(value = ControllerConstants.Views.Pages.Shopping.ShoppingListPage)
public class ShoppingListPageController extends AbstractPageController {
    private static final Logger LOG = LogManager.getLogger(ShoppingListPageController.class);

    private static final String SHOPPING_LIST_CMS_PAGE = "shopping";

    private static final String LIST_PATH_VARIABLE_PATTERN = "/{listId:.*}";

    private static final String CALCULATE_LIST_PATH_VARIABLE_PATTERN = LIST_PATH_VARIABLE_PATTERN + "/calculate";

    private static final String SORT_PATH_VARIABLE_PATTERN = "/{sort:.*}";

    private static final String REMOVE_PATH = "/remove";

    private static final String ADD_PATH = "/add";

    private static final String DOWNLOAD_PATH = "/download";

    private static final String EXPORT_FORMAT_PATH_VARIABLE_PATTERN = "/{exportFormat:.*}";

    private static final String EXPORT_DATA_ID_PATH_VARIABLE_PATTERN = "/{exportDataId:.*}";

    private static final String PRODUCT_CODE = "productCode";

    private static final String PRODUCT_CODES = "productCodes[]";

    private static final String PRODUCTS_JSON = "productsJson";

    private static final String LIST_PATH_VARIABLE = "listId";

    private static final String NAME_VARIABLE = "listName";

    private static final String SORT_PATH_VARIABLE = "sort";

    private static final String LIST_IDS_VARIABLE = "listIds[]";

    private static final String ACTION_STATUS = "actionStatus";

    private static final String DEFAULT_LIST_KEY = "shoppingList.default";

    protected static final String DEFAULT_LIST = "Shopping List";

    @Autowired
    private I18NService i18NService;

    @Autowired
    private DistExportFacade distExportFacade;

    @Autowired
    private DistShoppingListFacade shoppingListFacade;

    @Autowired
    private DistrelecProductFacade productFacade;

    @Autowired
    private ShoppingListBreadcrumbBuilder shoppingListBreadcrumbBuilder;

    @PostMapping(value = "/create")
    public String createShoppingList(@RequestParam(value = NAME_VARIABLE) final String name) {
        final NamicsWishlistData newList = getDistShoppingListFacade().createEmptyList(name);
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + ControllerConstants.Views.Pages.Shopping.ShoppingListPage + "/" + newList.getUniqueId());
    }

    @PostMapping(value = "/new" + ADD_PATH)
    public String addToNewShoppingList(@RequestParam(value = NAME_VARIABLE) final String name, @RequestParam(value = PRODUCTS_JSON) final String productsJson,
                                       final Model model) {
        final NamicsWishlistData wishlist = getDistShoppingListFacade().createEmptyList(name);
        addProductsToLists(productsJson, Collections.<String> singleton(wishlist.getUniqueId()));
        model.addAttribute(ACTION_STATUS, Boolean.TRUE);
        model.addAttribute("newListId", wishlist.getUniqueId());
        return ControllerConstants.Views.Fragments.Wishlist.ActionStatus;
    }

    @PostMapping(value = ADD_PATH)
    public String addToShoppingList(final Model model, @RequestParam(value = PRODUCTS_JSON) final String productsJson,
                                    @RequestParam(value = LIST_IDS_VARIABLE) final String[] lists) {
        model.addAttribute("shoppingListCount", addProductsToLists(productsJson, Arrays.asList(lists)));
        model.addAttribute(ACTION_STATUS, Boolean.TRUE);
        return ControllerConstants.Views.Fragments.Wishlist.ActionStatus;
    }

    @GetMapping(value = "/popup")
    public String getShoppingLists(final Model model) {
        final List<MiniWishlistData> wishlists = getDistShoppingListFacade().getMiniWishlists();
        model.addAttribute("shoppingLists", wishlists);
        return ControllerConstants.Views.Fragments.Wishlist.ShoppingPopup;
    }

    @GetMapping(value = LIST_PATH_VARIABLE_PATTERN)
    public String getShoppingListById(final Model model, @PathVariable(value = LIST_PATH_VARIABLE) final String listId,
                                      final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        populatePage(model, listId, null);
        return getViewForPage(model);
    }

    @RequestMapping(value = CALCULATE_LIST_PATH_VARIABLE_PATTERN, method = { RequestMethod.GET,
                                                                             RequestMethod.POST }, produces = MediaType.APPLICATION_JSON_VALUE)
    public String calculateShoppingList(final Model model, @PathVariable(value = LIST_PATH_VARIABLE) final String listId) {
        if (StringUtils.isNotEmpty(listId)) {
            try {
                final NamicsWishlistData wishlist = getDistShoppingListFacade().calculateShoppingList(listId);
                if (wishlist != null) {
                    model.addAttribute("shoppingList", wishlist);
                } else {
                    model.addAttribute("errorMsg", "Shopping list not found");
                }
            } catch (final Exception exp) {
                LOG.error("Exception occured in calculating shopping list with ID {} \n{}", listId, exp.getMessage(), exp);
                model.addAttribute("errorMsg", exp.getMessage());
            }
        }
        return ControllerConstants.Views.Fragments.Wishlist.CalculateShoppingListJson;
    }

    @GetMapping(value = LIST_PATH_VARIABLE_PATTERN + SORT_PATH_VARIABLE_PATTERN)
    public String getShoppingListByIdSorted(final Model model, @PathVariable(value = LIST_PATH_VARIABLE) final String listId,
                                            @PathVariable(value = SORT_PATH_VARIABLE) final String order,
                                            final HttpServletRequest request) throws CMSItemNotFoundException {
        addGlobalModelAttributes(model, request);
        populatePage(model, listId, getProductListOrder(order));
        return getViewForPage(model);
    }

    @GetMapping
    public String getShoppingList() {
        final List<MiniWishlistData> lists = getDistShoppingListFacade().getMiniWishlists();
        final String listId = lists.iterator().next().getUniqueId();
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + ControllerConstants.Views.Pages.Shopping.ShoppingListPage + "/" + listId);
    }

    @PostMapping(value = "/update")
    public String updateShoppingList(final Model model, @RequestParam(value = LIST_PATH_VARIABLE) final String list,
                                     @RequestParam(value = NAME_VARIABLE) final String name) {
        getDistShoppingListFacade().updateWishlistNameAndDescription(list, name, StringUtils.EMPTY);
        model.addAttribute(ACTION_STATUS, Boolean.TRUE);
        return ControllerConstants.Views.Fragments.Wishlist.ActionStatus;
    }

    @PostMapping(value = "/update/entry")
    public String updateProductCount(final Model model, @RequestParam(value = LIST_PATH_VARIABLE) final String list,
                                     @RequestParam(value = PRODUCT_CODE) final String productCode, @RequestParam(value = "desired") final Integer desired) {
        getDistShoppingListFacade().updateProductCount(list, normalizeProductCode(productCode), desired);
        model.addAttribute(ACTION_STATUS, Boolean.TRUE);
        return ControllerConstants.Views.Fragments.Wishlist.ActionStatus;
    }

    @PostMapping(value = "/delete")
    public String deleteShoppingList(@RequestParam(value = LIST_PATH_VARIABLE) final String list) {
        getDistShoppingListFacade().deleteList(list);
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + ControllerConstants.Views.Pages.Shopping.ShoppingListPage);
    }

    @PostMapping(value = REMOVE_PATH)
    public String removeFromShoppingList(@RequestParam(value = LIST_IDS_VARIABLE) final String[] listIds,
                                         @RequestParam(value = PRODUCT_CODES) final String[] productCodes, final Model model) {
        for (final String list : listIds) {
            for (final String productCode : productCodes) {
                getDistShoppingListFacade().removeFromWishlist(list, normalizeProductCode(productCode));
            }
        }
        model.addAttribute(ACTION_STATUS, Boolean.TRUE);
        return ControllerConstants.Views.Fragments.Wishlist.ActionStatus;
    }

    @ExceptionHandler(UnknownIdentifierException.class)
    public String handleUnknownIdentifierException(final UnknownIdentifierException uie) {
        // catch exception in case user made this request with wrong credentials
        LOG.error("A Product or a Wishlist could not be found. User probably made this request with wrong credentials.", uie);
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + ROOT + "notFound");
    }

    @GetMapping(value = DOWNLOAD_PATH + EXPORT_FORMAT_PATH_VARIABLE_PATTERN + EXPORT_DATA_ID_PATH_VARIABLE_PATTERN)
    public void downloadShoppingList(@PathVariable("exportFormat") final String exportFormat, @PathVariable("exportDataId") final String exportDataId,
                                     final HttpServletResponse response) {
        final NamicsWishlistData shoppingList = getDistShoppingListFacade().getWishlist(exportDataId);
        final String fileNamePrefix = getConfigurationService().getConfiguration().getString("distrelec.exportShoppingListFileNamePrefix")
                                      + shoppingList.getName();
        downloadExportFile(shoppingList.getEntries(), exportFormat, fileNamePrefix, response);
    }

    protected Map<String, Integer> addProductsToLists(final String productsJson, final Collection<String> lists) {
        final Map<String, Integer> listCounts = new HashMap<>();
        try {
            final Map<String, Map<String, Object>> products = new ObjectMapper().readValue(productsJson, Map.class);
            for (final Entry<String, Map<String, Object>> product : products.entrySet()) {
                final String productCode = normalizeProductCode(product.getKey());
                final Map<String, Object> informations = product.getValue();
                final Integer qty = ((Integer) informations.get("qty"));
                final String ref = (String) informations.get("ref");
                for (final String listId : lists) {
                    final NamicsWishlistData wishlist = getDistShoppingListFacade().addToWishlistWithCustomerReference(listId, productCode, ref);
                    getDistShoppingListFacade().updateProductCount(listId, productCode, qty);
                    int size = 0;
                    if (CollectionUtils.isNotEmpty(wishlist.getEntries())) {
                        size = wishlist.getEntries().size();
                    }
                    listCounts.put(wishlist.getUniqueId(), size);
                }
            }
        } catch (final IOException e) {
            LOG.error("Could not parse sent JSON: {}", productsJson, e);
        }
        return listCounts;
    }

    protected ProductListOrder getProductListOrder(final String code) {
        ProductListOrder order = ProductListOrder.DATEADDEDDESC;
        try {
            order = ProductListOrder.valueOf(StringUtils.upperCase(code));
        } catch (final IllegalArgumentException e) {
            LOG.error("specified ProductListOrder is unknown.", e);
        }
        return order;
    }

    protected void populatePage(final Model model, final String listId, final ProductListOrder order) throws CMSItemNotFoundException {
        storeCmsPageInModel(model, getShoppingListPage());

        NamicsWishlistData wishlist;
        if (StringUtils.isNotEmpty(listId)) {
            wishlist = order == null ? getDistShoppingListFacade().getWishlist(listId) : getDistShoppingListFacade().getWishlist(listId, order);

            List<NamicsWishlistEntryData> punchedOutProducts = wishlist.getEntries()
                                                                       .stream()
                                                                       .filter(entry -> productFacade.isProductPunchedOut(entry.getProduct().getCode())
                                                                              || !productFacade.isProductBuyable(entry.getProduct().getCode()))
                                                                       .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(punchedOutProducts)) {
                punchedOutProducts.forEach(entry -> getDistShoppingListFacade().removeFromWishlist(listId, normalizeProductCode(entry.getProduct().getCode())));
                wishlist.getEntries().removeAll(punchedOutProducts);
                wishlist.setCalculated(Boolean.FALSE);
                GlobalMessages.addErrorMessage(model, "shoppinglist.product.error.punchout",
                                               new String[] { getJoinedPunchedOutProductCodes(punchedOutProducts) }, DistConstants.Punctuation.PIPE);
            }

            if (!wishlist.isCalculated()) {
                try {
                    wishlist = getDistShoppingListFacade().calculateShoppingList(listId);
                } catch (final ProductNotBuyableException e) {
                    LOG.error("Exception occurred in checking shopping list with ID {} \n{}", listId, e.getMessage(), e);
                } catch (final CalculationException e) {
                    LOG.error("Exception occurred in calculating shopping list with ID {} \n{}", listId, e.getMessage(), e);
                }
            }
        } else {
            wishlist = order == null ? shoppingListFacade.getWishlist() : shoppingListFacade.getWishlist(order);
        }
        populateBreadcrumb(model, wishlist);
        populateList(model, wishlist);
        model.addAttribute("currentOrder", order);
        model.addAttribute("productListOrders", ProductListOrder.values());
        model.addAttribute("bulkTools", ProductListBulkTools.values());
        getDistWebtrekkFacade().addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
    }

    private String getJoinedPunchedOutProductCodes(List<NamicsWishlistEntryData> punchedOutProducts) {
        return punchedOutProducts
                                 .stream()
                                 .filter(Objects::nonNull)
                                 .filter(entry -> entry.getProduct() != null)
                                 .map(entry -> entry.getProduct().getCode())
                                 .collect(Collectors.joining(", "));
    }

    protected void populateBreadcrumb(final Model model, final NamicsWishlistData wishlist) {
        String listName = wishlist.getName();
        final Locale currLocale = i18NService.getCurrentLocale();
        final String defaultShoppingListName = getMessageSource().getMessage(DEFAULT_LIST_KEY, null, DEFAULT_LIST, currLocale);
        if (listName.equalsIgnoreCase(DEFAULT_LIST)) {
            listName = defaultShoppingListName;
        }
        model.addAttribute(WebConstants.BREADCRUMBS_KEY, shoppingListBreadcrumbBuilder.getBreadcrumbs(listName));
    }

    protected void populateList(final Model model, final NamicsWishlistData wishlist) {
        model.addAttribute("currentList", wishlist);
        model.addAttribute("shoppingLists", getDistShoppingListFacade().getMiniWishlists());
    }

    protected void downloadExportFile(final List<NamicsWishlistEntryData> wishlistEntries, final String exportFormat, final String exportFileNamePrefix,
                                      final HttpServletResponse response) {
        final File downloadFile = distExportFacade.exportProducts(wishlistEntries, exportFormat, exportFileNamePrefix);
        try {
            setUpDownloadFile(response, downloadFile, exportFormat);
        } catch (final IOException e) {
            LOG.error("Could not set up file {} for download", downloadFile.getPath(), e);
        }
    }

    protected AbstractPageModel getShoppingListPage() throws CMSItemNotFoundException {
        final ContentPageModel contentPageModel = getContentPageForLabelOrId(SHOPPING_LIST_CMS_PAGE);
        contentPageModel.setWtAreaCode("so");
        return contentPageModel;
    }
}
