package com.namics.distrelec.b2b.facades.backorder.impl;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_BACKORDER;
import static java.util.Objects.nonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;

import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.distrelec.b2b.facades.backorder.BackOrderFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import com.namics.distrelec.b2b.facades.search.ProductSearchFacade;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.data.search.SearchQueryData;

import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.exceptions.CalculationException;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.model.ModelService;

public class DefaultBackOrderFacadeImpl implements BackOrderFacade {

    public static final String DESC = "DESC";

    public static final List<ProductOption> CAT_PLUS_SUPPLIER_AID_PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE,
                                                                                                  ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                                                                                                  ProductOption.GALLERY,
                                                                                                  ProductOption.VOLUME_PRICES, ProductOption.PROMOTION_LABELS,
                                                                                                  ProductOption.COUNTRY_OF_ORIGIN,
                                                                                                  ProductOption.DIST_MANUFACTURER,
                                                                                                  ProductOption.PRODUCT_INFORMATION);

    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.ONLINE_PRICE /* ProductOption.PRICE */,
                                                                               ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                                                                               ProductOption.GALLERY, ProductOption.CATEGORIES, ProductOption.REVIEW,
                                                                               ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION,
                                                                               ProductOption.VARIANT_FULL, ProductOption.STOCK, ProductOption.VOLUME_PRICES,
                                                                               ProductOption.PROMOTION_LABELS,
                                                                               ProductOption.COUNTRY_OF_ORIGIN, ProductOption.DIST_MANUFACTURER,
                                                                               ProductOption.VIDEOS,
                                                                               /* ProductOption.PRODUCT_INFORMATION, */ProductOption.IMAGE360,
                                                                               ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION);

    private static final Logger LOG = LogManager.getLogger(DefaultBackOrderFacadeImpl.class);

    private static final String FF_PAGE_SIZE = "backorder.search.alternate.amount";

    private static final int CODE_LENGTH = 8;

    private static final int CODE_LENGTH_START = 0;

    private static final int CODE_LENGTH_3 = 3;

    private static final int CODE_LENGTH_5 = 5;

    @Autowired
    protected ErpStatusUtil erpStatusUtil;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade cartFacade;

    @Autowired
    @Qualifier("productModelUrlResolver")
    private DistUrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    @Qualifier("catalogPlusProductModelUrlResolver")
    private DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver;

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    private ProductService productService;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade b2bCartFacade;

    @Autowired
    private ProductSearchFacade<ProductData> productSearchFacade;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private I18NService i18nService;

    @Autowired
    private AvailabilityService availabilityService;

    @Override
    public void updateBackOrderItems(final List<OrderEntryData> backOrderItems) {
        boolean cartRecalculateRequired = false;
        for (final OrderEntryData backorderData : backOrderItems) {
            // Case 1: Remove whole line if Back order quantity equals to order quantity i.e. not even one quantity can be fulfilled hence we
            // remove the complete order Entry
            if (isNotProfitableAndBackorderQuantityIsEqual(backorderData)) {
                try {
                    b2bCartFacade.removeFromCart(backorderData.getProduct().getCode());
                    cartRecalculateRequired = true;
                } catch (final CommerceCartModificationException e) {
                    LOG.error("Could not remove Item" + backorderData.getEntryNumber());
                }
                // Case 2: We reduce the cart quantity by N if back order not profitable for specific quantity
            } else if (isNotProfitableAndBackorderQuantityMoreThanZero(backorderData) && isBackorderQuantityMoreThanQuantity(backorderData)) {
                try {
                    b2bCartFacade.updateCartEntry(backorderData.getEntryNumber(), backorderData.getBackOrderedQuantity(), false);
                    cartRecalculateRequired = true;
                    resetBackOrderFlag(backorderData.getEntryNumber());
                } catch (final CommerceCartModificationException e) {
                    LOG.error("Could not remove Item" + backorderData.getEntryNumber());
                }
            }
        }

        CartModel currentCart = b2bCartFacade.getSessionCartModel();
        currentCart.setSkipMovCheck(true);
        modelService.save(currentCart);

        if (cartRecalculateRequired) {
            try {
                b2bCartFacade.recalculateCart();
            } catch (final CalculationException e) {
                LOG.error("Could not recalculate Cart");
            }
        }
    }

    private void resetBackOrderFlag(final long entryNumber) {
        final AbstractOrderEntryModel entryModel = getCartFacade().getOrderEntry(entryNumber);
        entryModel.setIsBackOrderProfitable(Boolean.TRUE);
        getModelService().save(entryModel);
        getModelService().refresh(entryModel);
    }

    private boolean isBackorderQuantityMoreThanQuantity(final OrderEntryData backorderData) {
        return backorderData.getBackOrderedQuantity() < backorderData.getQuantity();
    }

    private boolean isNotProfitableAndBackorderQuantityMoreThanZero(final OrderEntryData backorderData) {
        return !backorderData.isBackOrderProfitable() && backorderData.getBackOrderedQuantity() > 0;
    }

    private boolean isNotProfitableAndBackorderQuantityIsEqual(final OrderEntryData backorderData) {
        return !backorderData.isBackOrderProfitable() && (backorderData.getBackOrderedQuantity() == 0);
    }

    @Override
    public List<ProductData> getBackOrderAlternativeProducts(final String productCode) {
        return productFacade.getMultipleProductAlternatives(productCode);
    }

    @Override
    public List<ProductData> getProductFamilyProducts(final String productCode) {
        final List<ProductData> productFamilyProductList = new ArrayList<>();
        final ProductModel sourceProductModel = getProductService().getProductForCode(productCode);
        final List<ProductData> productFamilyData = getProductFamilyData(sourceProductModel.getPimFamilyCategoryCode(),
                                                                         sourceProductModel.getPrimarySuperCategory().getCode());
        for (final ProductData productFamilyProduct : productFamilyData) {
            if (!productCode.equals(productFamilyProduct.getCode())) {
                final ProductData productDataBasicPopulated = productFamilyProduct;
                final ProductData sourceProduct = getProductFacade().getProductForCodeAndOptions(productDataBasicPopulated.getCode(),
                                                                                                 PRODUCT_OPTIONS);
                if (sourceProduct != null) {
                    productDataBasicPopulated.setOrderQuantityMinimum(sourceProduct.getOrderQuantityMinimum());
                    productDataBasicPopulated.setOrderQuantityStep(sourceProduct.getOrderQuantityStep());
                    productDataBasicPopulated.setPrice(sourceProduct.getPrice());
                    productDataBasicPopulated.setListPrice(sourceProduct.getListPrice());
                    productDataBasicPopulated.setVolumePrices(sourceProduct.getVolumePrices());
                    productDataBasicPopulated.setVolumePricesFlag(sourceProduct.getVolumePricesFlag());
                    productDataBasicPopulated.setVolumePricesMap(sourceProduct.getVolumePricesMap());
                    productDataBasicPopulated.setImages(sourceProduct.getImages());
                    productDataBasicPopulated.setProductImages(sourceProduct.getProductImages());
                    productFamilyProductList.add(productDataBasicPopulated);
                }
            }
        }
        return productFamilyProductList;
    }

    @Override
    public ProductData populateProductDetailForDisplay(final String productCode) {
        final ProductData productData;
        final ProductModel productModel = getProductService().getProductForCode(productCode);
        if (productModel.getCatPlusSupplierAID() != null) {
            productData = getProductFacade().getProductForCodeAndOptions(productModel, CAT_PLUS_SUPPLIER_AID_PRODUCT_OPTIONS);
        } else {
            productData = getProductFacade().getProductForCodeAndOptions(productModel, PRODUCT_OPTIONS);
        }
        formatResponseData(productData);
        return productData;
    }

    @Override
    public List<OrderEntryData> getPurchaseBlockedProducts(List<OrderEntryData> entries, List<String> codes) {
        return entries.stream()
                      .filter(entry -> nonNull(entry.getProduct()))
                      .filter(entry -> codes.contains(entry.getProduct().getCode()))
                      .collect(Collectors.toList());
    }

    @Override
    public void removeBlockedProductsFromCart(List<OrderEntryData> blockedProducts) {
        boolean cartRecalculateRequired = false;
        for (OrderEntryData blockedProduct : blockedProducts) {
            try {
                b2bCartFacade.removeFromCart(blockedProduct.getProduct().getCode());
                cartRecalculateRequired = true;
            } catch (final CommerceCartModificationException e) {
                LOG.error("Could not remove Item " + blockedProduct.getEntryNumber());
            }
        }

        CartModel currentCart = b2bCartFacade.getSessionCartModel();
        currentCart.setSkipMovCheck(true);
        modelService.save(currentCart);

        if (cartRecalculateRequired) {
            try {
                b2bCartFacade.recalculateCart();
            } catch (final CalculationException e) {
                LOG.error("Could not recalculate Cart");
            }
        }
    }

    private void formatResponseData(final ProductData productData) {
        if (productData.getSvhcReviewDate() != null) {
            final String format = getMessageSource().getMessage("text.store.dateformat", null, "MM/dd/yyyy", getI18nService().getCurrentLocale());
            final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            try {
                productData.setFormattedSvhcReviewDate(dateFormat.format(productData.getSvhcReviewDate()));
                productData.setSvhcReviewDate(dateFormat.parse(productData.getSvhcReviewDate().toString()));
            } catch (final ParseException e) {
                LOG.info("Cannot Parse  getSvhcReviewDate for " + productData.getCode());
            }
        }
        if (productData.getCode() != null && productData.getCode().length() == (CODE_LENGTH)) {
            productData.setCode(productData.getCode().substring(CODE_LENGTH_START, CODE_LENGTH_3) + "-"
                                + productData.getCode().substring(CODE_LENGTH_3, CODE_LENGTH_5) + "-"
                                + productData.getCode().substring(CODE_LENGTH_5, CODE_LENGTH));
        }
    }

    private List<ProductData> getProductFamilyData(final String pimFamilyCategoryCode, final String categoryCode) {
        final String searchQuery = "*&productFamilyCode=" + pimFamilyCategoryCode;
        final Map<String, List<String>> otherSearchParams = new HashMap<>();
        final SearchStateData searchState = createSearchStateData(searchQuery);
        final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData = getProductSearchFacade().search(false, searchState,
                                                                                                                             createPagingData(),
                                                                                                                             DistSearchType.CATEGORY,
                                                                                                                             categoryCode, true, null,
                                                                                                                             otherSearchParams);
        final List<ProductData> results = searchPageData.getResults();
        return CollectionUtils.isEmpty(results) ? new ArrayList<>() : results;

    }

    protected SearchStateData createSearchStateData(final String searchQuery) {
        final SearchStateData searchState = new SearchStateData();
        // maybe one could do something more generic with {@link ServletRequestParameterPropertyValues} ?
        final SearchQueryData queryData = new SearchQueryData();
        queryData.setValue(searchQuery);
        searchState.setQuery(queryData);
        return searchState;
    }

    private PageableData createPagingData() {
        final PageableData pageableData = new PageableData();
        pageableData.setCurrentPage(1);

        final String pageSize = configurationService.getConfiguration().getString(FF_PAGE_SIZE);
        pageableData.setPageSize(StringUtils.isNotBlank(pageSize) ? Integer.parseInt(pageSize) : 100);
        pageableData.setSort(DESC);
        return pageableData;
    }

    @Override
    public List<OrderEntryData> getBackOrderNotProfitableItems(List<OrderEntryData> cartEntries) {
        final List<OrderEntryData> completeRemoveBackOrderItems = new ArrayList<>();
        final List<OrderEntryData> partialRemoveBackOrderItems = new ArrayList<>();
        for (OrderEntryData orderEntry : cartEntries) {
            if (!orderEntry.isBackOrderProfitable() && (orderEntry.getBackOrderedQuantity() == 0L)) {
                List<ProductData> alternateProduct = getAlternateProductList(orderEntry.getProduct().getCode(), orderEntry.getQuantity().intValue());
                orderEntry.setAlternateAvailable(alternateProduct.size() > 0);
                orderEntry.setAlternateQuantity(alternateProduct.size());
                completeRemoveBackOrderItems.add(orderEntry);
            }
        }
        completeRemoveBackOrderItems.sort(Comparator.comparing(OrderEntryData::getQuantity).reversed());
        final List<OrderEntryData> backOrderItems = new ArrayList<>(completeRemoveBackOrderItems);
        for (OrderEntryData orderEntry : cartEntries) {
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
        partialRemoveBackOrderItems.sort(
                                         (h1, h2) -> Math.toIntExact((h2.getQuantity() - h2.getBackOrderedQuantity())
                                                                     - (h1.getQuantity() - h1.getBackOrderedQuantity())));
        backOrderItems.addAll(partialRemoveBackOrderItems);
        return backOrderItems;
    }

    @Override
    public List<ProductData> getAlternateProductList(final String productCode, final Integer productQuantityRequested) {
        final List<ProductData> alternativeProducts = getBackOrderAlternativeProducts(productCode);
        final List<ProductData> productFamilyProducts = getProductFamilyProducts(productCode);

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

    @Override
    public List<CartModificationData> updateBackOrderItemsForCurrentCart() {
        CartModel currentCart = b2bCartFacade.getSessionCartModel();
        List<CartModificationData> cartModificationData = currentCart.getEntries()
                                                                     .stream()
                                                                     .filter(entry -> BooleanUtils.isFalse(entry.getIsBackOrderProfitable())
                                                                             && nonNull(entry.getBackOrderedQuantity()))
                                                                     .map(entry -> {
                                                                         try {
                                                                             return updateEntryIfBackorder(entry);
                                                                         } catch (CommerceCartModificationException e) {
                                                                             LOG.error("Could not update Item" + entry.getEntryNumber());
                                                                             return null;
                                                                         }
                                                                     })
                                                                     .filter(Objects::nonNull)
                                                                     .collect(Collectors.toList());

        currentCart.setSkipMovCheck(true);
        modelService.save(currentCart);
        return cartModificationData;
    }

    private CartModificationData updateEntryIfBackorder(AbstractOrderEntryModel entry) throws CommerceCartModificationException {
        if (isNotAvailableForBackorder(entry)) {
            return b2bCartFacade.updateCartEntry(entry.getEntryNumber(), 0, Boolean.FALSE);
        } else if (isOrderedQuantityNotAvailable(entry)) {
            return b2bCartFacade.updateCartEntry(entry.getEntryNumber(), entry.getBackOrderedQuantity(), Boolean.FALSE);
        }
        return null;
    }

    @Override
    public List<CartModificationData> updateBlockedProductsForCurrentCart() {
        CartModel currentCart = b2bCartFacade.getSessionCartModel();
        List<String> purchaseBlockedProductCodes = availabilityService.getPurchaseBlockedProductCodes();
        List<AbstractOrderEntryModel> blockedProducts = currentCart.getEntries().stream()
                                                                   .filter(entry -> nonNull(entry.getProduct()))
                                                                   .filter(entry -> purchaseBlockedProductCodes.contains(entry.getProduct().getCode()))
                                                                   .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(blockedProducts)) {
            return blockedProducts
                                  .stream()
                                  .map(entry -> {
                                      try {
                                          return b2bCartFacade.updateCartEntry(entry.getEntryNumber(), 0);
                                      } catch (CommerceCartModificationException e) {
                                          LOG.error("Could not update Item" + entry.getEntryNumber());
                                          return null;
                                      }
                                  })
                                  .filter(Objects::nonNull)
                                  .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private boolean isOrderedQuantityNotAvailable(AbstractOrderEntryModel entry) {
        return entry.getBackOrderedQuantity() > 0 && entry.getQuantity() > entry.getBackOrderedQuantity();
    }

    private boolean isNotAvailableForBackorder(AbstractOrderEntryModel entry) {
        return entry.getBackOrderedQuantity() == 0;
    }

    private List<ProductAvailabilityData> getAvailabilityData(final Collection<ProductData> productFamilyProducts, final Integer requestedQuantity) {
        final Map<String, Integer> requestedQuantities = new LinkedHashMap<>();
        for (ProductData productData : productFamilyProducts) {
            requestedQuantities.put(productData.getCode(), requestedQuantity);
        }
        return getProductFacade().getAvailability(requestedQuantities, Boolean.FALSE);
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

    public DistB2BCartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(final DistB2BCartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    private DistUrlResolver<ProductModel> getProductUrlResolver(final ProductModel productModel) {
        return productModel.getCatPlusSupplierAID() == null ? getProductModelUrlResolver() : getCatalogPlusProductModelUrlResolver();
    }

    public DistUrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    public void setProductModelUrlResolver(final DistUrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    public DistUrlResolver<ProductModel> getCatalogPlusProductModelUrlResolver() {
        return catalogPlusProductModelUrlResolver;
    }

    public void setCatalogPlusProductModelUrlResolver(final DistUrlResolver<ProductModel> catalogPlusProductModelUrlResolver) {
        this.catalogPlusProductModelUrlResolver = catalogPlusProductModelUrlResolver;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public ProductSearchFacade<ProductData> getProductSearchFacade() {
        return productSearchFacade;
    }

    public void setProductSearchFacade(final ProductSearchFacade<ProductData> productSearchFacade) {
        this.productSearchFacade = productSearchFacade;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    protected ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(final MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public I18NService getI18nService() {
        return i18nService;
    }

    public void setI18nService(final I18NService i18nService) {
        this.i18nService = i18nService;
    }

}
