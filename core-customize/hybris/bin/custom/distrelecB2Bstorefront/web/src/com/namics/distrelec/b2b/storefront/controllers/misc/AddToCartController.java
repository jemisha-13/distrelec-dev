/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.controllers.misc;

import static com.namics.distrelec.b2b.storefront.controllers.pages.CartPageController.CART_DATA;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.DistUtils;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Page;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.PageInfo;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Search;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.importtool.data.ImportToolMatchingData;
import com.namics.distrelec.b2b.facades.importtool.data.ProductJsonData;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import com.namics.distrelec.b2b.storefront.controllers.AbstractController;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.util.GlobalMessages;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Controller for Add to Cart functionality which is not specific to a certain page.
 */
@Controller
public class AddToCartController extends AbstractController {

    protected static final Logger LOG = LogManager.getLogger(AddToCartController.class);

    protected static final String REFERER = "Referer";

    private static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.MIN_BASIC
    // ProductOption.BASIC , ProductOption.PROMOTION_LABELS
    );

    public static final String CART_ADD_REQUEST_MAPPING = "/cart/add";

    public static final String CART_ADD_BULK_REQUEST_MAPPING = CART_ADD_REQUEST_MAPPING + "/bulk";

    public static final String CART_ADD_EXT_REQUEST_MAPPING = "/cart/ext-add";

    public static final String CART_ENTRY = "cartEntry";

    public static final String ADDED_QUANTITY_ATTRIBUTE_NAME = "addedQuantity";

    private static final String URL_PARAM_SEPARATION = ",";

    public static final String QUOTATION_ADD_REQUEST_MAPPING = "/cart/addquotation";

    private static final int CHARACTER_LIMIT = Integer.parseInt(DistConstants.Product.ATTRIBUTE_MAX_NAME_LENGTH);

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade b2bCartFacade;

    @Autowired
    @Qualifier("distrelecProductFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    @Qualifier("productService")
    private ProductService productService;

    @Autowired
    @Qualifier("priceDataFactory")
    private PriceDataFactory priceDataFactory;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private CommerceCommonI18NService commerceCommonI18NService;

    @RequestMapping(value = QUOTATION_ADD_REQUEST_MAPPING, method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public String addQuotationInToCart(@RequestParam(value = "quotationId") final String quotationId,
                                       @RequestParam(value = "productsJson", required = false) final String productsJson, final Model model) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            final Map<MultiKey, Long> productQuantites = new HashMap<>();
            if (StringUtils.isNotBlank(productsJson)) {
                try {
                    mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                    mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
                    final List<ProductJsonData> list = mapper.readValue(productsJson,
                                                                        mapper.getTypeFactory().constructCollectionType(List.class, ProductJsonData.class));
                    for (final ProductJsonData data : list) {
                        if (data.getQuantity() > 0 && StringUtils.isNotBlank(data.getProductCode()) && StringUtils.isNotBlank(data.getItemNumber())) {
                            productQuantites.put(new MultiKey(data.getProductCode(), data.getItemNumber()), data.getQuantity());
                        }
                    }
                } catch (final Exception exp) {
                    LOG.error(exp);
                }
            }
            final Map<String, Object> result = getCartFacade().addQuotationInToCart(quotationId, productQuantites, Boolean.FALSE);
            if (BooleanUtils.isNotTrue((Boolean) result.get("status"))) {
                model.addAttribute("status", "error");
                model.addAttribute("errorMsg", result.get("message"));
            }
        } catch (final Exception ex) {
            LOG.warn("Couldn't add quotationId: " + quotationId + " . into cart", ex);
            model.addAttribute("errorMsg", "basket.error.occurred");
            model.addAttribute("status", "error");
        }
        model.addAttribute(CART_DATA, getCartFacade().getSessionCart());
        return ControllerConstants.Views.Fragments.Cart.CartJson;
    }

    @PostMapping(value = CART_ADD_REQUEST_MAPPING, produces = "application/json")
    public String addToCart(@RequestParam("productCodePost") final String code, final Model model,
                            @RequestParam(value = "qty", required = false, defaultValue = "0") final long qty,
                            @RequestParam(value = "pdpSearchDatalayer", required = false) final boolean pdpSearchDatalayer,
                            @RequestParam(value = "q", required = false) final String searchQuery,
                            @RequestParam(value = "trackQuery", required = false) final String trackQuery,
                            @RequestParam(value = POS_PARAMETER_NAME, required = false) final String pos,
                            @RequestParam(value = ORIG_POS_PARAMETER_NAME, required = false) final String origPos,
                            @RequestParam(value = ORIG_PAGE_SIZE_PARAMETER_NAME, required = false) final String origPageSize,
                            @RequestParam(value = "prodprice", required = false) final String prodprice,
                            @RequestParam(value = "pageType", required = false) final String pageType,
                            final HttpServletRequest request) {
        try {
            final String productCode = normalizeProductCode(code);
            final ProductData productData = getProductFacade().getProductForCodeAndOptions(productCode, PRODUCT_OPTIONS);
            productData.setName(DistUtils.truncateProductName(CHARACTER_LIMIT, productData.getName()));
            final long quantity = (qty > 0) ? qty : (productData.getOrderQuantityMinimum() != null ? productData.getOrderQuantityMinimum() : 1L);

            final List<String> phaseOutProducts = new ArrayList<>();
            final String errorCode = checkIfProductIsBuyable(productData, quantity, false, phaseOutProducts);
            model.addAttribute("product", productData);
            if (StringUtils.isNotEmpty(errorCode)) {
                model.addAttribute("errorMsg", errorCode);
                LOG.warn("cannot add Product {} because of: {}", code, errorCode);
                model.addAttribute("product", productData);
                LOG.warn("cannot add Product {} because of: {}", code, errorCode);
                model.addAttribute(ADDED_QUANTITY_ATTRIBUTE_NAME, quantity);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("addToCart has error:" + errorCode);
                }
                return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
            }
            final String trackingQ = StringUtils.isNotBlank(trackQuery) ? trackQuery : searchQuery;
            final CartModificationData cartModification = getCartFacade().addToCart(productCode, quantity, trackingQ, false);
            model.addAttribute(ADDED_QUANTITY_ATTRIBUTE_NAME, Long.valueOf(cartModification.getQuantityAdded()));
            model.addAttribute(CART_ENTRY, cartModification.getEntry());

            if (cartModification.getQuantityAdded() == 0L) {
                model.addAttribute("errorMsg", "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
            } else if (cartModification.getQuantityAdded() < quantity) {
                model.addAttribute("errorMsg", "basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
            }

            if (cartModification.getQuantityAdded() != cartModification.getQuantity()) {
                model.addAttribute("updatedEntry", cartModification.getEntry().getEntryNumber());
            }
            if (CollectionUtils.isNotEmpty(phaseOutProducts)) {
                model.addAttribute("errorMsg", "basket.information.quantity.reducedNumberOfItemsAdded.lowStock");
            }
            populateSearchEvent(request, model, pdpSearchDatalayer);

        } catch (final UnknownIdentifierException e) {
            model.addAttribute("errorMsg", "product.notFound");
            model.addAttribute(ADDED_QUANTITY_ATTRIBUTE_NAME, Long.valueOf(0L));
            LOG.warn("Couldn't add product of code " + code + " to cart " + getCartFacade().getSessionCart().getCode() + ".", e);
        } catch (final CommerceCartModificationException ex) {
            model.addAttribute("errorMsg", "basket.error.occurred");
            model.addAttribute(ADDED_QUANTITY_ATTRIBUTE_NAME, Long.valueOf(0L));
            LOG.warn("Couldn't add product of code " + code + " to cart " + getCartFacade().getSessionCart().getCode() + ".", ex);
            removeProductFromCart(code);
        } catch (final Exception e) {
            model.addAttribute("errorMsg", "basket.error.unknown");
            model.addAttribute(ADDED_QUANTITY_ATTRIBUTE_NAME, Long.valueOf(0L));
            LOG.warn("Couldn't add product of code " + code + " to cart " + getCartFacade().getSessionCart().getCode() + ".", e);
            removeProductFromCart(code);
        } finally {
            // Put in the cart again after it has been modified
            model.addAttribute("cartData", getCartFacade().getSessionCart());
        }

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    private DigitalDatalayer populateSearchEvent(final HttpServletRequest request, final Model model, final boolean pdpSearchDatalayer) {

        DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
        Page page = digitalDatalayer.getPage();
        if (null == page) {
            page = new Page();
        }
        PageInfo pageInfo = page.getPageInfo();
        if (null == pageInfo) {
            pageInfo = new PageInfo();
        }
        Search searchData = pageInfo.getSearch();
        if (null == searchData) {
            searchData = new Search();
        }
        String referUrl = request.getHeader(REFERER);
        if (pdpSearchDatalayer || (StringUtils.isNotEmpty(referUrl) && referUrl.contains("/search"))) {
            searchData.setAddtocart(true);
        }
        pageInfo.setSearch(searchData);
        page.setPageInfo(pageInfo);
        digitalDatalayer.setPage(page);
        return digitalDatalayer;
    }

    @RequestMapping(value = CART_ADD_EXT_REQUEST_MAPPING, method = { RequestMethod.GET, RequestMethod.POST })
    public String addToCartExt(@RequestParam("productCode") final String code, final Model model,
                               @RequestParam(value = "qty", required = false, defaultValue = "1") final String qty) {
        final List<String> codeList = Arrays.asList(code.split(URL_PARAM_SEPARATION));
        final List<String> qtyList = new ArrayList<>(codeList.size());
        final String[] qtyArray = qty.split(URL_PARAM_SEPARATION);
        qtyList.addAll(Arrays.asList(qtyArray));
        final int differences = codeList.size() - qtyList.size();
        for (int i = 0; i < differences; i++) {
            qtyList.add("1");
        }

        int index = 0;
        for (final String productCode : codeList) {
            final String productQuanity = qtyList.get(index);
            long quantity = Long.parseLong(productQuanity);
            index++;

            try {
                final ProductData productData = getProductFacade().getProductForTypeOrCodeAndOptions(productCode, PRODUCT_OPTIONS);
                if (quantity == 0) {
                    quantity = (productData.getOrderQuantityMinimum() != null) ? productData.getOrderQuantityMinimum().longValue() : 1L;
                }

                final String errorCode = checkIfProductIsBuyable(productData, quantity, false, null);
                if (StringUtils.isNotEmpty(errorCode)) {
                    model.addAttribute("errorMsg", errorCode);
                    return ControllerConstants.Views.Fragments.Cart.AddToCartExt;
                }

                final CartModificationData cartModification = getCartFacade().addToCartExt(productCode, quantity);
                if (cartModification.getQuantityAdded() == 0L) {
                    model.addAttribute("errorMsg", "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
                } else if (cartModification.getQuantityAdded() < quantity) {
                    model.addAttribute("errorMsg", "basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
                }
            } catch (final UnknownIdentifierException e) {
                model.addAttribute("errorMsg", "product.notFound");
                LOG.warn("Couldn't add product of code " + productCode + " to cart " + getCartFacade().getSessionCart().getCode() + ".");
            } catch (final CommerceCartModificationException ex) {
                model.addAttribute("errorMsg", "basket.error.occurred");
                LOG.warn("Couldn't add product of code " + productCode + " to cart " + getCartFacade().getSessionCart().getCode() + ".", ex);
                removeProductFromCart(productCode);
            } catch (final Exception e) {
                model.addAttribute("errorMsg", "basket.error.unknown");
                LOG.warn("Couldn't add product of code " + productCode + " to cart " + getCartFacade().getSessionCart().getCode() + ".", e);
                removeProductFromCart(productCode);
            }

        }

        return ControllerConstants.Views.Fragments.Cart.AddToCartExt;
    }

    @RequestMapping(value = CART_ADD_BULK_REQUEST_MAPPING, method = RequestMethod.POST)
    public String addToCartBulk(final Model model, @RequestParam(value = "productsJson") final String productsJson) {

        final Map<String, List<ProductData>> errorProducts = new HashMap<>();
        int updatedEntriesCount = 0;
        try {
            model.addAttribute("cartData", getCartFacade().getSessionCart());
            final ImportToolMatchingData[] importProducts = new Gson().fromJson(productsJson, ImportToolMatchingData[].class);
            final List<String> phaseOutProducts = new ArrayList<>();
            for (final ImportToolMatchingData importToolMatchingData : importProducts) {
                final String productCode = normalizeProductCode(importToolMatchingData.getProductCode());
                ProductModel productModel = getProductService().getProductForCode(productCode);
                final ProductData productData = productFacade.getProductDataBasicPopulated(productModel);
                final long qty = importToolMatchingData.getQuantity();
                final String errorCode = checkIfProductIsBuyable(productData, qty, true, phaseOutProducts);
                if (StringUtils.isNotEmpty(errorCode) && phaseOutProducts.isEmpty()) {
                    addToMap(errorProducts, productCode, errorCode);
                    LOG.warn("Couldn't add product of code [" + productCode + "] to cart due to error code [" + errorCode + "].");
                } else {
                    // User reference
                    final String ref = importToolMatchingData.getReference();

                    try {
                        phaseOutProducts.clear();
                        final CartModificationData cartModification = getCartFacade().addToCartWithoutCalcCart(productCode, qty, ref);

                        if (cartModification.getQuantityAdded() == 0L) {
                            LOG.warn("No items added to basket. Status code: " + cartModification.getStatusCode());
                            addToMap(errorProducts, productCode, "basket.information.quantity.noItemsAdded." + cartModification.getStatusCode());
                        } else if (cartModification.getQuantityAdded() < qty) {
                            LOG.warn("Reduced number of items added to basket. Status code: " + cartModification.getStatusCode());
                            addToMap(errorProducts, productCode, "basket.information.quantity.reducedNumberOfItemsAdded." + cartModification.getStatusCode());
                        }
                        // Increment the number of the updated entries.
                        updatedEntriesCount++;
                    } catch (final UnknownIdentifierException e) {
                        model.addAttribute("errorMsg", "product.notFound");
                        model.addAttribute(ADDED_QUANTITY_ATTRIBUTE_NAME, Long.valueOf(0L));
                        LOG.warn("Couldn't add product of code " + productCode + " to cart " + getCartFacade().getSessionCart().getCode() + ".", e);
                    } catch (final CommerceCartModificationException ex) {
                        addToMap(errorProducts, productCode, "basket.error.occurred");
                        LOG.warn("Couldn't add product of code " + productCode + " to cart " + getCartFacade().getSessionCart().getCode() + ".", ex);
                        removeProductFromCart(productCode);
                    } catch (final Exception e) {
                        addToMap(errorProducts, productCode, "basket.error.unknown");
                        LOG.warn("Couldn't add product of code " + productCode + " to cart " + getCartFacade().getSessionCart().getCode() + ".", e);
                        removeProductFromCart(productCode);
                    }
                }
            }

            getCartFacade().recalculateCart();

        } catch (final CalculationException e) {
            LOG.warn("Couldn't recalculate cart.", e);
            model.addAttribute(GlobalMessages.ERROR_MESSAGES_HOLDER, "basket.error.occurred");
        }

        model.addAttribute("addedProducts", Integer.valueOf(updatedEntriesCount));
        model.addAttribute("cartData", getCartFacade().getSessionCart());
        model.addAttribute("errorProducts", errorProducts);

        return ControllerConstants.Views.Fragments.Cart.AddToCartPopup;
    }

    protected void removeProductFromCart(final String code) {
        if (StringUtils.isBlank(code)) {
            return;
        }

        try {
            getCartFacade().removeFromCart(code);
        } catch (final CommerceCartModificationException e) {
            LOG.error("Could not remove product " + code + " from cart " + getCartFacade().getSessionCart().getCode() + ".", e);
        }
    }

    protected String checkIfProductIsBuyable(final ProductData productData, final long qty, final boolean bulk, final List<String> phaseOutProducts) {
        String errorCode = null;
        if (!validSAPCatalogQty(productData.getCode(), qty)) {
            return "sap.catalog.order.articles.error";
        }
        if (!getProductFacade().isProductBuyable(productData.getCode())) {
            final Collection<PunchoutFilterResult> punchoutFilterResult = getProductFacade().getPunchOutFilters(productData.getCode());
            errorCode = CollectionUtils.isNotEmpty(punchoutFilterResult) ? "cart.product.error.punchout" : "basket.error.occurred";
        }
        if (getProductFacade().isEndOfLife(productData.getCode())) {
            errorCode = "cart.product.error.endOfLife";
        }
        final String productsalesorgstatus = getProductFacade().getProductSalesStatus(productData.getCode());
        productData.setSalesStatus(productsalesorgstatus);
        final List<String> notforsale = erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_GENERIC);

        if (notforsale.contains(productsalesorgstatus)) {
            final String errorMsg = isStockAvailableForProduct(productData.getCode(), qty, phaseOutProducts);
            if (errorMsg != null) {
                errorCode = errorMsg;
            }
        }
        if (StringUtils.isBlank(errorCode)) {
            String errorMsg = checkIfPhasedOutAndBuyable(productData, productsalesorgstatus, qty, phaseOutProducts);
            if (errorMsg != null) {
                errorCode = errorMsg;
            }
        }
        if (StringUtils.isBlank(errorCode)) {
            errorCode = checkQuantity(productData, qty, bulk);
        }
        return errorCode;
    }

    private String checkIfPhasedOutAndBuyable(final ProductData productData, String productsalesorgstatus, final long qty,
                                              final List<String> phaseOutProducts) {
        final List<String> notforsale = erpStatusUtil.getErpSalesStatusFromConfiguration("distrelec.noproduct.forsale.salestatus.notbuyable");
        if (notforsale.contains(productsalesorgstatus)) {
            List<ProductAvailabilityData> availabilityData = getProductFacade().getAvailability(Arrays.asList(productData.getCode()), true);
            final Integer stockLevelTotal = CollectionUtils.isNotEmpty(availabilityData) ? availabilityData.get(0).getStockLevelTotal() : Integer.valueOf(0);
            if (stockLevelTotal.intValue() <= 0) {
                return "cart.nonstock.phaseout.product";
            } else if (stockLevelTotal.intValue() < qty) {
                phaseOutProducts.add(productData.getCode());
            }

        }
        return null;
    }

    public String checkQuantity(final ProductData product, final long qty, final boolean bulk) {
        final long minQty = (product.getOrderQuantityMinimum() != null) ? product.getOrderQuantityMinimum() : 1;
        final long step = (product.getOrderQuantityStep() != null) ? product.getOrderQuantityStep() : 1;
        LOG.debug("Product: {} has MOQ: {} and step:{}", product.getCode(), product.getOrderQuantityMinimum(), product.getOrderQuantityStep());

        final String quantityOrStepsMessage = messageSource.getMessage("validation.error.order.bulk.quantityOrStepsError",
                                                                       new Object[] { String.valueOf(step) }, commerceCommonI18NService.getCurrentLocale());
        if (qty < minQty) {
            return bulk ? quantityOrStepsMessage : "validation.error.min.order.quantity";
        }
        if ((qty - minQty) % step != 0) {
            return bulk ? quantityOrStepsMessage : "validation.error.steps.order.quantity";
        }
        return null;
    }

    /**
     * Add product to map
     * 
     * @param map
     *            the target map
     * @param productCode
     *            the product code
     * @param message
     * @see #addToMap(Map, ProductData, String)
     */
    protected void addToMap(final Map<String, List<ProductData>> map, final String productCode, final String message) {
        addToMap(map, getProductFacade().getProductForCodeAndOptions(productCode, PRODUCT_OPTIONS), message);
    }

    /**
     * Add product to map
     * 
     * @param map
     *            the target map
     * @param product
     *            the product data
     * @param message
     */
    protected void addToMap(final Map<String, List<ProductData>> map, final ProductData product, final String message) {
        product.setSalesStatus(getProductFacade().getProductSalesStatus(product.getCode()));
        map.computeIfAbsent(message, key -> new ArrayList<>()).add(product);
    }

    private String isStockAvailableForProduct(final String productCode, final long qty, final List<String> phaseOutProducts) {
        final List<ProductAvailabilityData> availabilityData = getProductFacade().getAvailability(Arrays.asList(productCode));
        final Integer stockLevelTotal = CollectionUtils.isNotEmpty(availabilityData) ? availabilityData.get(0).getStockLevelTotal() : Integer.valueOf(0);
        if (stockLevelTotal.intValue() <= 0) {
            return "cart.nonstock.phaseout.product";
        } else if (stockLevelTotal.intValue() < qty) {
            phaseOutProducts.add(productCode);
        }

        return null;
    }

    boolean validSAPCatalogQty(final String productCode, final long quantity) {
        final List<String> sapCatalogProducts = Arrays
                                                      .asList(getConfigurationService().getConfiguration().getString("sap.catalog.order.articles", "")
                                                                                       .split(","));
        if (sapCatalogProducts.contains(productCode)) {
            if (quantity == 1) {
                long cartQty = getCartFacade().checkCartLevel(productCode);
                cartQty = cartQty + quantity;
                if (cartQty == 1) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    protected DigitalDatalayer getDigitalDatalayerFromModel(final Model model) {
        if (!model.containsAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER)) {
            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, new DigitalDatalayer());
        }

        return (DigitalDatalayer) model.asMap().get(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER);
    }

    protected String normalizeProductCode(final String code) {
        return code == null ? null : code.replaceAll("-", "");
    }

    public DistB2BCartFacade getCartFacade() {
        return this.b2bCartFacade;
    }

    public void setCartFacade(final DistB2BCartFacade cartFacade) {
        this.b2bCartFacade = cartFacade;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public DistProductService getProductService() {
        return (DistProductService) productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public CommerceCommonI18NService getCommerceCommonI18NService() {
        return commerceCommonI18NService;
    }

    public void setCommerceCommonI18NService(CommerceCommonI18NService commerceCommonI18NService) {
        this.commerceCommonI18NService = commerceCommonI18NService;
    }
}
