package com.namics.distrelec.b2b.storefront.controllers.pages.checkout;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_BACKORDER;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.BackOrders;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.DigitalDatalayer;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Product;
import com.namics.distrelec.b2b.facades.backorder.BackOrderFacade;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import com.namics.distrelec.b2b.storefront.annotations.RequireHardLogin;
import com.namics.distrelec.b2b.storefront.breadcrumb.impl.SimpleBreadcrumbBuilder;
import com.namics.distrelec.b2b.storefront.controllers.ControllerConstants;
import com.namics.distrelec.b2b.storefront.controllers.pages.CartPageController;
import com.namics.distrelec.b2b.storefront.seo.DistLink;

import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.servicelayer.config.ConfigurationService;

@Controller
@RequestMapping("/checkout/backorderDetails")
@RequireHardLogin
public class DistCheckoutBackOrderDetailsController extends AbstractDistCheckoutController {
    protected static final Logger LOG = LogManager.getLogger(DistCheckoutBackOrderDetailsController.class);

    private static final String DEFAULT_STOCK = "0";

    private static final int ZERO = 0;

    private static final int ONE = 1;

    private static final String CAROUSEL_DATA_ATTRIBUTE_NAME = "productFFCarouselData";

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private SimpleBreadcrumbBuilder simpleBreadcrumbBuilder;

    @Autowired
    private BackOrderFacade defaultBackOrderFacade;

    @Autowired
    private AvailabilityService availabilityService;

    @Override
    protected boolean recalculateCartBeforePage() {
        return true;
    }

    @GetMapping
    public String checkoutBackOrderDetails(final Model model, final HttpServletRequest request,
                                           RedirectAttributes redirectModel) throws CMSItemNotFoundException {
        if (!hasReachedMovAmount()) {
            redirectModel.addFlashAttribute(WebConstants.MOV_DISPLAY_MESSAGE_ON_PAGE_LOAD, true);
            return addFasterizeCacheControlParameter(REDIRECT_PREFIX + CartPageController.PAGE_CART);
        }

        // Add Global Attributes
        long time, boTime, avTime, cartTime, globalDataTime;
        boTime = avTime = cartTime = globalDataTime = time = System.currentTimeMillis();
        addGlobalModelAttributes(model, request);
        final CartData cartData = getDistCheckoutFacade().getCheckoutCart();
        cartTime = System.currentTimeMillis() - cartTime;

        final List<OrderEntryData> entries = cartData == null ? new ArrayList<>() : cartData.getEntries();
        final List<OrderEntryData> backOrderItems = defaultBackOrderFacade.getBackOrderNotProfitableItems(entries);
        model.addAttribute("backOrderNotProfitableList", backOrderItems);
        List<String> codes = (List<String>) model.getAttribute("purchaseBlockedProductCodes");
        if (CollectionUtils.isNotEmpty(codes) && CollectionUtils.isNotEmpty(entries)) {
            List<OrderEntryData> blockedProducts = defaultBackOrderFacade.getPurchaseBlockedProducts(entries, codes);
            backOrderItems.addAll(blockedProducts);
        }
        model.addAttribute("backOrderNotProfitableList", backOrderItems);
        if (LOG.isDebugEnabled()) {
            boTime = System.currentTimeMillis() - boTime;
            final String methodName = this.getClass().getSimpleName() + ".makeObject()";
            LOG.debug("Time to get non profitable backorder items [{}]: {}ms To get cart time:{}ms", methodName, boTime, cartTime);
        }
        if (LOG.isDebugEnabled()) {
            avTime = System.currentTimeMillis() - avTime;
            final String methodName = this.getClass().getSimpleName() + ".makeObject()";
            LOG.debug("Time to get availability map [{}]: {}ms", methodName, avTime);
        }
        prepareDataForPage(model);
        // Add header Links
        final List<DistLink> headerLinkLang = new ArrayList<>();
        setupAlternateHreflangLinks(request, headerLinkLang, null, null);
        if (LOG.isDebugEnabled()) {
            time = System.currentTimeMillis() - time;
            final String methodName = this.getClass().getSimpleName() + ".makeObject()";
            LOG.debug("Time to set alternative links [{}]: {}ms", methodName, time);
        }
        model.addAttribute("headerLinksLangTags", headerLinkLang);
        model.addAttribute("footerLinksLangTags", headerLinkLang);
        model.addAttribute("cartData", cartData);
        globalDataTime = System.currentTimeMillis() - globalDataTime;
        final String methodName = this.getClass().getSimpleName() + ".makeObject()";
        addGlobalModelAttributes(model, request);
        if (isDatalayerEnabled()) {
            final DigitalDatalayer digitalDatalayer = getDigitalDatalayerFromModel(model);
            List<Product> digitalDataProducts = backOrderItems.stream()
                                                              .map(OrderEntryData::getProduct)
                                                              .map(product -> populateProductDTMObjects(product))
                                                              .collect(Collectors.toList());
            for (Product product : digitalDataProducts) {
                for (OrderEntryData backOrderEntryData : backOrderItems) {
                    if (backOrderEntryData.getProduct().getCode().equals(product.getProductInfo().getProductID())) {
                        BackOrders backOrders = new BackOrders();
                        backOrders.setAlternatives(String.valueOf(backOrderEntryData.getAlternateQuantity()));
                        backOrders.setQuantity(String.valueOf(backOrderEntryData.getBackOrderedQuantity() == 0 ? backOrderEntryData.getQuantity()
                                                                                                               : backOrderEntryData.getQuantity()
                                                                                                                 - backOrderEntryData.getBackOrderedQuantity()));
                        if (backOrderEntryData.getBackOrderedQuantity() == 0) {
                            backOrders.setReason("A");
                        } else {
                            backOrders.setReason("B");
                        }
                        product.getProductInfo().setBackOrders(backOrders);
                    }
                }
            }
            digitalDatalayer.setProduct(digitalDataProducts);
            LOG.debug("Time to set global data [{}]: {}ms", methodName, globalDataTime);
            LOG.debug("cart->backorder-> availability->alternative->globalDataTime");
            model.addAttribute(DigitalDatalayerConstants.AdobeDTM.DIGITAL_DATALAYER, digitalDatalayer);
        }

        return getViewForPage(model);
    }

    protected void prepareDataForPage(final Model model) throws CMSItemNotFoundException {
        // * CMS Details
        final ContentPageModel contentPage = getContentPageForLabelOrId(PAGE_BACKORDER_DETAILS);
        storeCmsPageInModel(model, contentPage);
        setUpMetaDataForContentPage(model, contentPage);
        model.addAttribute(WebConstants.BREADCRUMBS_KEY,
                           getSimpleBreadcrumbBuilder().getBreadcrumbs(contentPage.getTitle(), contentPage.getTitle(Locale.ENGLISH)));
        model.addAttribute("pageType", PageType.Cart);

        // Webtrekk
        prepareWebtrekkParams(model, null);

        getDistWebtrekkFacade().addTeaserTrackingId(model, DistConstants.Webtrekk.TEASER_TRACKING_ONS);
    }

    protected CountryData getCurrentCountry() {
        return getStoreSessionFacade().getCurrentCountry();
    }

    private List<OrderEntryData> getBackOrderNotProfitableItems(final List<OrderEntryData> entries) {
        final List<OrderEntryData> completeRemoveBackOrderItems = new ArrayList<>();
        final List<OrderEntryData> partialRemoveBackOrderItems = new ArrayList<>();
        final List<OrderEntryData> backOrderItems = new ArrayList<>();
        for (OrderEntryData orderEntry : entries) {
            if (!orderEntry.isBackOrderProfitable() && orderEntry.getBackOrderedQuantity() == 0L) {
                List<ProductData> alternateProduct = getAlternateProductList(orderEntry.getProduct().getCode(), orderEntry.getQuantity().intValue());
                orderEntry.setAlternateAvailable(alternateProduct.size() > 0);
                orderEntry.setAlternateQuantity(alternateProduct.size());
                completeRemoveBackOrderItems.add(orderEntry);
            }
        }
        completeRemoveBackOrderItems.sort(Comparator.comparing(OrderEntryData::getQuantity).reversed());
        backOrderItems.addAll(completeRemoveBackOrderItems);
        for (OrderEntryData orderEntry : entries) {
            if (!orderEntry.isBackOrderProfitable() && (orderEntry.getBackOrderedQuantity() != 0L)
                    && orderEntry.getQuantity() > orderEntry.getBackOrderedQuantity()) {
                List<ProductData> alternateProduct = getAlternateProductList(orderEntry.getProduct().getCode(),
                                                                             (int) (orderEntry.getQuantity()
                                                                                    - orderEntry.getBackOrderedQuantity()));
                orderEntry.setAlternateAvailable(alternateProduct.size() > 0);
                orderEntry.setAlternateQuantity(alternateProduct.size());
                partialRemoveBackOrderItems.add(orderEntry);
            }
        }
        partialRemoveBackOrderItems.sort((h1, h2) -> Math.toIntExact((h2.getQuantity() - h2.getBackOrderedQuantity())
                                                                     - (h1.getQuantity() - h1.getBackOrderedQuantity())));
        backOrderItems.addAll(partialRemoveBackOrderItems);
        return backOrderItems;
    }

    /**
     * This method updates BackOrder Not profitable details
     * 
     * @param model
     *            MVC Model
     * @param request
     *            HTTP request
     * @return String
     */
    @PostMapping(value = "/updateBackOrder")
    public String updateBackOrderDetails(final Model model, final HttpServletRequest request) {
        final CartData cartData = getDistCheckoutFacade().getCheckoutCart();
        final List<OrderEntryData> entries = cartData == null ? new ArrayList<>() : cartData.getEntries();
        // Add Global Attributes
        addGlobalModelAttributes(model, request);
        defaultBackOrderFacade.updateBackOrderItems(defaultBackOrderFacade.getBackOrderNotProfitableItems(entries));
        updateBlockedProducts(entries);
        return addFasterizeCacheControlParameter(REDIRECT_PREFIX + PAGE_CHECKOUT);
    }

    private void updateBlockedProducts(List<OrderEntryData> entries) {
        List<String> purchaseBlockedProductCodes = availabilityService.getPurchaseBlockedProductCodes();
        List<OrderEntryData> blockedProducts = defaultBackOrderFacade.getPurchaseBlockedProducts(entries, purchaseBlockedProductCodes);
        if (CollectionUtils.isNotEmpty(blockedProducts)) {
            defaultBackOrderFacade.removeBlockedProductsFromCart(blockedProducts);
        }
    }

    @RequestMapping(value = "/getAlternateProductsForBackOrder/{productCode}/{qty}")
    public String alsoBought(@PathVariable("productCode") final String productCode, @PathVariable("qty") final String productQuantity, final Model model) {
        int requestedQuantity = 1;
        if (StringUtils.isNotBlank(productQuantity)) {
            requestedQuantity = Integer.parseInt(productQuantity);
        }
        model.addAttribute(CAROUSEL_DATA_ATTRIBUTE_NAME, defaultBackOrderFacade.getAlternateProductList(productCode, requestedQuantity));
        model.addAttribute("currentCountry", getCurrentCountry());
        return ControllerConstants.Views.Fragments.Product.CarouselProducts;
    }

    @GetMapping(value = "/getProductDetails/{productCode}")
    @ResponseBody
    public String getProductDetails(@PathVariable("productCode") final String productCode) {
        ProductData productDetails = defaultBackOrderFacade.populateProductDetailForDisplay(productCode);
        return convertObjectToJson(productDetails);
    }

    /**
     * Convert the Java Object to JSON.
     *
     * @param obj
     *            Object to be converted to json
     * @return String
     */
    private String convertObjectToJson(final Object obj) {
        String jsonInString = "";
        try {
            final ObjectMapper mapper = new ObjectMapper();
            jsonInString = mapper.writeValueAsString(obj);

        } catch (final JsonGenerationException e) {
            LOG.warn("Exception occurred during JSON generation", e);
        } catch (final JsonMappingException e) {
            LOG.warn("Exception occurred during JSON mapping", e);
        } catch (final IOException e) {
            LOG.warn("Exception occurred during conversion of object to JSON", e);
        }

        return jsonInString;
    }

    private List<ProductData> getAlternateProductList(final String productCode, final Integer productQuantityRequested) {
        return getCarouselData(productCode, productQuantityRequested);
    }

    private List<ProductData> getCarouselData(final String productCode, Integer productQuantityRequested) {
        final List<ProductData> alternativeProducts = defaultBackOrderFacade.getBackOrderAlternativeProducts(productCode);
        final List<ProductData> productFamilyProducts = defaultBackOrderFacade.getProductFamilyProducts(productCode);

        Set<ProductData> allAlternativeProducts = new LinkedHashSet<>(alternativeProducts);
        allAlternativeProducts.addAll(productFamilyProducts);

        final List<ProductAvailabilityData> availabilityData = getAvailabilityData(allAlternativeProducts, productQuantityRequested);
        final Map<String, Integer> availabilityMap = new HashMap<>();
        for (ProductAvailabilityData availableData : availabilityData) {
            availabilityMap.put(availableData.getProductCode(), availableData.getStockLevelTotal());
        }
        final List<ProductData> filteredProductList = filterNotInStockProducts(availabilityMap, allAlternativeProducts, productQuantityRequested);
        final List<ProductData> carouselData = new ArrayList<>(filteredProductList);
        return carouselData.stream()
                           .filter(Objects::nonNull)
                           .collect(Collectors.toList());
    }

    private List<ProductData> filterNotInStockProducts(final Map<String, Integer> availabilityMap, final Collection<ProductData> allAlternativeProducts,
                                                       final Integer productQuantityRequested) {
        final List<ProductData> filteredAvailableProductList = new ArrayList<>();
        final List<String> alternateProductIdList = new ArrayList<>();
        for (ProductData product : allAlternativeProducts) {
            final List<String> erpUnsellableStatusesList = erpStatusUtil.getErpSalesStatusFromConfiguration(ATTRIBUTE_NO_PRODUCT_FOR_SALE_BACKORDER);
            if (availabilityMap.get(product.getCode()) > productQuantityRequested
                    && (StringUtils.isNotEmpty(product.getSalesStatus()) && !erpUnsellableStatusesList.contains(product.getSalesStatus())
                            && !alternateProductIdList.contains(product.getCode()))) {
                filteredAvailableProductList.add(product);
                alternateProductIdList.add(product.getCode());
            }
        }
        return filteredAvailableProductList;
    }

    private boolean isFilteredAvailableAlternateProduct(final Map<String, Integer> availabilityMap, final ProductData alternativeProduct,
                                                        final Integer productQuantityRequested) {
        final boolean isAlternateProductQuantityMoreThanRequested = availabilityMap.get(alternativeProduct.getCode()) > productQuantityRequested;
        final String salesStatus = alternativeProduct.getSalesStatus();
        final List<String> erpUnsellableStatusesList = erpStatusUtil.getErpSalesStatusFromConfiguration(ATTRIBUTE_NO_PRODUCT_FOR_SALE_BACKORDER);
        final boolean isSalesStatusNotAvailable = StringUtils.isNotEmpty(salesStatus) && erpUnsellableStatusesList.contains(salesStatus);
        return isAlternateProductQuantityMoreThanRequested && !isSalesStatusNotAvailable;
    }

    private List<ProductAvailabilityData> getAvailabilityData(final Collection<ProductData> productFamilyProducts, final Integer requestedQuantity) {
        final Map<String, Integer> requestedQuantities = new LinkedHashMap<>();
        for (ProductData productData : productFamilyProducts) {
            requestedQuantities.put(productData.getCode(), requestedQuantity);
        }
        return getProductFacade().getAvailability(requestedQuantities, Boolean.FALSE);
    }

    public SimpleBreadcrumbBuilder getSimpleBreadcrumbBuilder() {
        return simpleBreadcrumbBuilder;
    }

    public void setSimpleBreadcrumbBuilder(SimpleBreadcrumbBuilder simpleBreadcrumbBuilder) {
        this.simpleBreadcrumbBuilder = simpleBreadcrumbBuilder;
    }

    @Override
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Override
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
