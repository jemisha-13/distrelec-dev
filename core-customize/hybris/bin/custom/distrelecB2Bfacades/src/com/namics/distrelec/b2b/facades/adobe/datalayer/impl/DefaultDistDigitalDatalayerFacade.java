/*
 * Copyright 2000-2017 Distrelec Group AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.adobe.datalayer.impl;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.rma.CreateRMAOrderEntryDataForm;
import com.namics.distrelec.b2b.core.rma.CreateRMARequestForm;
import com.namics.distrelec.b2b.core.rma.GuestRMACreateRequestForm;
import com.namics.distrelec.b2b.core.service.DistCmsPageService;
import com.namics.distrelec.b2b.core.service.rma.data.CreateRMAResponseData;
import com.namics.distrelec.b2b.core.service.url.DistUrlResolver;
import com.namics.distrelec.b2b.facades.adobe.datalayer.DistDigitalDatalayerFacade;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.*;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.Error;
import com.namics.distrelec.b2b.facades.adobe.datalayer.data.OrderData;
import com.namics.distrelec.b2b.facades.constants.DigitalDatalayerConstants;
import com.namics.distrelec.b2b.facades.order.OrderHistoryFacade;
import com.namics.distrelec.b2b.facades.order.cart.DistB2BCartFacade;
import com.namics.distrelec.b2b.facades.order.data.DistErpVoucherInfoData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.storesession.DistrelecStoreSessionFacade;
import com.namics.hybris.ffsearch.data.facet.FactFinderProductSearchPageData;
import com.namics.hybris.ffsearch.data.facet.FilterBadgeData;
import com.namics.hybris.ffsearch.data.paging.FactFinderSortData;
import com.sap.security.core.server.csi.util.URLEncoder;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.containers.AbstractCMSComponentContainerModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cms2lib.model.components.AbstractBannerComponentModel;
import de.hybris.platform.commercefacades.order.data.*;
import de.hybris.platform.commercefacades.product.ProductFacade;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.search.data.SearchStateData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.site.BaseSiteService;

/**
 * {@code DefaultDistDigitalDatalayerFacade}
 *
 * @since Distrelec 6.3
 */
public class DefaultDistDigitalDatalayerFacade implements DistDigitalDatalayerFacade {

    private static final Logger LOG = LogManager.getLogger(DefaultDistDigitalDatalayerFacade.class);

    private static final String SYSTEM_ENV = "Desktop";

    private static final String PAGE_15_16 = "15/16:";

    private static final String PAGE_16_17 = "16/17:";

    public static final String CUSTOM = "custom";

    public static final String LIST = "list";

    public static final String UTF8_ENCODE = "UTF-8";

    public static final int CATEGORY_SIZE_0 = 0;

    public static final int CATEGORY_SIZE_1 = 1;

    public static final int CATEGORY_SIZE_2 = 2;

    public static final int CATEGORY_SIZE_3 = 3;

    public static final int CATEGORY_SIZE_4 = 4;

    private static final String CMS_PAGE_KEY = "cmsPage";

    public static final String TITLE_COMMON = "| Distrelec Switzerland";

    protected static final List<ProductOption> PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC, ProductOption.PRICE, ProductOption.DIST_MANUFACTURER,
                                                                               ProductOption.VOLUME_PRICES, ProductOption.PROMOTION_LABELS,
                                                                               ProductOption.CLASSIFICATION);

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private BaseSiteService baseSiteService;

    @Autowired
    @Qualifier("b2bCartFacade")
    private DistB2BCartFacade cartFacade;

    @Autowired
    private DistrelecStoreSessionFacade storeSessionFacade;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private ProductService productService;

    @Autowired
    @Qualifier("productModelUrlResolver")
    private DistUrlResolver<ProductModel> productModelUrlResolver;

    @Autowired
    private SessionService sessionService;

    @Autowired
    @Qualifier("addressConverter")
    private Converter<AddressModel, AddressData> addressConverter;

    @Autowired
    @Qualifier("productFacade")
    private ProductFacade productFacade;

    @Autowired
    private DistCmsPageService pageService;

    @Autowired
    private OrderHistoryFacade orderHistoryFacade;

    @Autowired
    private DistrelecProductFacade distProductFacade;

    @Override
    public void populateDTMSearchData(final String searchQuery, final FactFinderProductSearchPageData<SearchStateData, ProductData> searchPageData,
                                      final DigitalDatalayer digitalDatalayer) {
        try {
            final long resultCount = null != searchPageData.getPagination() ? searchPageData.getPagination().getTotalNumberOfResults() : 0;
            Page pageData = digitalDatalayer.getPage();
            if (null == pageData) {
                pageData = new Page();
            }
            PageInfo pageInfo = pageData.getPageInfo();
            if (null == pageInfo) {
                pageInfo = new PageInfo();
            }

            pageInfo.setSearchTerm(searchQuery);
            pageInfo.setResults(Long.valueOf(resultCount).doubleValue());
            if (null != searchPageData.getCategories() && null != searchPageData.getCategories().getValues()) {
                final List<SearchCategory> searchCategoryList = searchPageData.getCategories().getValues().stream()
                                                                              .filter(fffv -> "categoryCodePathROOT".equalsIgnoreCase(fffv.getCode()))
                                                                              .map(fffv -> {
                                                                                  final SearchCategory searchCategory = new SearchCategory();
                                                                                  searchCategory.setCategory(fffv.getName().toLowerCase());
                                                                                  searchCategory.setResults(Double.valueOf(fffv.getCount()));
                                                                                  return searchCategory;
                                                                              }).collect(Collectors.toList());

                // pageInfo.setSearchSubcategory(searchSubCategoryList);
                pageInfo.setSearchCategory(searchCategoryList);
            }
            final SiteChannel currentChannel = getSessionService().getAttribute(DistConstants.Session.CHANNEL);
            pageInfo.setChannel(null != currentChannel ? currentChannel.getCode() : "");
            if (null != searchPageData.getFilters()) {
                final List<FilterBadgeData<SearchStateData>> filters = searchPageData.getFilters();
                final Map<String, String> list = filters.stream().collect(Collectors.groupingBy(FilterBadgeData<SearchStateData>::getFacetCode,
                                                                                                Collectors.mapping(FilterBadgeData<SearchStateData>::getFacetValueCode,
                                                                                                                   Collectors.joining("|"))));
                final String map = (null != list && list.size() > 0) ? list.toString() : "";
                pageInfo.setFiltertype(map.toString().replace("{", "").replace("}", ""));
            }
            final List<FactFinderSortData> sorting = searchPageData.getSorting();
            final FactFinderSortData orderBy = sorting.stream().filter(SortData::isSelected).findFirst().orElse(null);
            pageInfo.setOrderby((null != orderBy && null != orderBy.getName() && !orderBy.getName().isEmpty()) ? orderBy.getName().toLowerCase() + "-"
                                                                                                                 + ((null != orderBy.getSortType()
                                                                                                                         && null != orderBy.getSortType()
                                                                                                                                           .getValue()) ? orderBy.getSortType()
                                                                                                                                                                 .getValue()
                                                                                                                                                                 .toLowerCase()
                                                                                                                                                        : "")
                                                                                                               : "Best Match");
            pageInfo.setProductperpage(null != searchPageData.getPagination() ? Double.valueOf(searchPageData.getPagination().getPageSize()) : 0D);

            pageData.setPageInfo(pageInfo);
            digitalDatalayer.setPage(pageData);

        } catch (final Exception ex) {
            LOG.error("Exception while populating DTM product attributes in search page", ex);
        }
    }

    private List<String> createFilterList(final List<FilterBadgeData<SearchStateData>> filters) {
        final List<String> filterListForDatalayer = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(filters)) {
            for (final FilterBadgeData<SearchStateData> currentFilter : filters) {
                filterListForDatalayer.add(currentFilter.getFacetName() + DistConstants.Punctuation.EQUALS + currentFilter.getFacetValueCode());
            }
        }
        return filterListForDatalayer;
    }

    @Override
    public Product populateProductDTMObjects(final ProductData productData) {
        final Product product = new Product();
        try {
            product.setProductInfo(populateProductInfoFromData(productData));
            final Category categoryData = new Category();
            final ProductModel productModel = productService.getProductForCode(productData.getCode());
            populateCategoryData(productModel, categoryData);
            product.setCategory(categoryData);
        } catch (final Exception ex) {
            LOG.error("Error while populating products datalayer in DTM: ", ex);
        }

        return product;
    }

    private ProductInfo populateProductInfoFromData(final ProductData productData) {
        final ProductInfo productInfo = new ProductInfo();
        try {
            productInfo.setProductID(productData.getCode());
            productInfo.setStatus(null != productData.getSalesStatus() ? productData.getSalesStatus() : "");
            productInfo.setProductName(null != productData.getNameEN() ? productData.getNameEN().toLowerCase() : "");
            productInfo.setManufacturer((null != productData.getDistManufacturer() && null != productData.getDistManufacturer().getName())
                                                                                                                                           ? productData.getDistManufacturer()
                                                                                                                                                        .getName()
                                                                                                                                                        .toLowerCase()
                                                                                                                                           : "");
            productInfo.setDescription(null != productData.getDescriptionEN() ? productData.getDescriptionEN().toLowerCase() : "");
            productInfo.setProductSupplierArticleNumber(productData.getCode().toLowerCase());
            if (null != productData.getProductInformation()) {
                final String pgNumber = null != productData.getProductInformation().getPaperCatalogPageNumber()
                                                                                                                ? (PAGE_15_16
                                                                                                                   + productData.getProductInformation()
                                                                                                                                .getPaperCatalogPageNumber()
                                                                                                                   + "|")
                                                                                                                : ""
                                                                                                                  + null != productData.getProductInformation()
                                                                                                                                       .getPaperCatalogPageNumber_16_17()
                                                                                                                                                                          ? (PAGE_16_17
                                                                                                                                                                             + productData.getProductInformation()
                                                                                                                                                                                          .getPaperCatalogPageNumber_16_17())
                                                                                                                                                                          : "";
                productInfo.setPaperCataloguePage(pgNumber);
            }
            productInfo.setRohs(null != productData.getRohsEN() ? productData.getRohsEN().toLowerCase() : "");
            productInfo.setPreviousArticleNumber((null != productData.getElfaArticleNumber() ? productData.getElfaArticleNumber() : "") + "|"
                                                 + (null != productData.getMovexArticleNumber() ? productData.getMovexArticleNumber() : ""));
            productInfo.setManufacturer(null != productData.getDistManufacturer() ? productData.getDistManufacturer().getName() : "");

            if (null != productData.getActivePromotionLabels()) {
                final StringBuilder promoLabels = new StringBuilder();
                productData.getActivePromotionLabels().forEach(lable -> promoLabels.append(lable.getCode()));
                productInfo.setProductLabel(promoLabels.toString().toLowerCase());
            }

            if (null != productData.getFutureStocks()) {
                productData.getFutureStocks().forEach(stock -> productInfo.setAvailabilityTime(Arrays.asList(stock.getDate().toString())));
            }
            final List<String> dVolPrices = new ArrayList<>();
            final List<String> priceThreshold = new ArrayList<>();
            if (productData.getVolumePricesMap() != null) {
                productData.getVolumePricesMap().forEach((key, value) -> {
                    int saving = 0;
                    if (value != null && value.get(LIST) != null && value.get(LIST).getValue() != null) {
                        try {
                            final BigDecimal basePriceValue = null != value.get(LIST) ? value.get(LIST).getValue() : BigDecimal.valueOf(0);
                            final BigDecimal volumePrice = (null != value.get(CUSTOM) && value.get(CUSTOM).getValue() != null) ? value.get(CUSTOM).getValue()
                                                                                                                               : BigDecimal.valueOf(0);
                            final BigDecimal diff = (null != basePriceValue && null != volumePrice) ? basePriceValue.subtract(volumePrice)
                                                                                                    : BigDecimal.valueOf(0);
                            if (diff.compareTo(BigDecimal.ZERO) > 0 && volumePrice.compareTo(BigDecimal.ZERO) > 0) {
                                saving = (int) Math.round(100 * diff.doubleValue() / basePriceValue.doubleValue());
                            }
                        } catch (final Exception ex) {
                            LOG.error("Datalayer Discount Calculation Failed!", ex);
                        }
                        dVolPrices.add((null != value.get(LIST) ? value.get(LIST).getValue().toString() : 0) + ":" + saving);
                    } else {
                        dVolPrices.add((value != null && null != value.get(CUSTOM) ? value.get(CUSTOM).getValue().toString() : 0) + ":" + saving);
                    }
                    priceThreshold.add(key.toString());

                });
            }

            if (null != productData.getStock()) {
                productInfo.setAvailability(null != productData.getStock().getStockLevelStatus() ? productData.getStock().getStockLevelStatus().getCode() : "");
                productInfo
                           .setStockQuantity(null != productData.getStock().getStockLevel()
                                                                                            ? Arrays.asList(productData.getStock().getStockLevel()
                                                                                                                       .doubleValue())
                                                                                            : Collections.emptyList());
            }
            PricePerUnit ppUnit = productInfo.getPricePerUnit();
            if (null == ppUnit) {
                ppUnit = new PricePerUnit();
            }
            if (null != productData.getPrice() && null != productData.getPrice().getPricePerX()) {
                ppUnit.setPrice(productData.getPrice().getPricePerX().doubleValue());
                ppUnit.setUnit(productData.getPrice().getPricePerXUOM());
                ppUnit.setUnitDesc(productData.getPrice().getPricePerXUOMDesc());
                ppUnit.setPriceUomBaseQty(
                                          null != productData.getPrice().getPricePerXBaseQty() ? productData.getPrice().getPricePerXBaseQty().doubleValue()
                                                                                               : 0);
                ppUnit.setPriceUomQty(null != productData.getPrice().getPricePerXUOMQty() ? productData.getPrice().getPricePerXUOMQty().doubleValue() : 0);
                productInfo.setPricePerUnit(ppUnit);
            }
            productInfo.setUnitPrice(dVolPrices);
            productInfo.setUnitPriceThreshold(priceThreshold);
            productInfo.setQuantity(null != productData.getOrderQuantityMinimum() ? productData.getOrderQuantityMinimum().doubleValue() : 1D);
            productInfo.setCurrency(null != productData.getPrice() ? productData.getPrice().getCurrencyIso() : "");

            final List<Alternative> alternatives = new ArrayList<>();
            final List<ProductData> alternativeProducts = distProductFacade.getMultipleProductAlternatives(productData.getCode());
            final List<String> productCodes = new ArrayList<>();
            for (final ProductData altproductData : alternativeProducts) {
                productCodes.add(altproductData.getCode());
            }
            final List<com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData> altAvailabilityData = distProductFacade.getAvailability(productCodes);
            for (final com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData altAvailableProduct : altAvailabilityData) {
                final Alternative alternative = new Alternative();
                alternative.setProductID(altAvailableProduct.getProductCode());
                alternative.setStockAmount(altAvailableProduct.getStockLevelTotal().doubleValue());
                alternatives.add(alternative);
            }
            productInfo.setAlternatives(alternatives);

            URI prodUrl = null;
            try {
                prodUrl = null != productData.getUrl() ? new URI(productData.getUrl()) : null;
            } catch (final Exception ex) {
                LOG.error("Exception while populating URI ", ex);
                prodUrl = null != productData.getUrl() ? new URI(URLEncoder.encode(productData.getUrl(), UTF8_ENCODE)) : null;
            }

            productInfo.setProductURL(prodUrl);
        } catch (final Exception ex) {
            LOG.error("Exception while populating productInfo", ex);
        }

        return productInfo;
    }

    public void setVoucherInfo(final CartData cartData, final DigitalDatalayer digitalDatalayer) {
        try {
            final DistErpVoucherInfoData voucherData = (null != cartData && null != cartData.getErpVoucherInfoData()) ? cartData.getErpVoucherInfoData()
                                                                                                                      : new DistErpVoucherInfoData();
            Cart cart = digitalDatalayer.getCart();
            if (null == cart) {
                cart = new Cart();
            }
            Price price = cart.getPrice();
            if (null == price) {
                price = new Price();
            }

            if (null != voucherData && null != voucherData.getFixedValue()) {
                price.setVoucherCode(null != voucherData.getCode() ? voucherData.getCode().toLowerCase() : "");
                price.setVoucherDiscount(null != voucherData.getFixedValue().getValue() ? voucherData.getFixedValue().getValue().doubleValue() : 0D);
            } else {
                price.setVoucherCode("");
                price.setVoucherDiscount(0D);
            }
            price.setShippingCost(
                                  (null != cartData.getDeliveryCost() && null != cartData.getDeliveryCost().getValue())
                                                                                                                        ? cartData.getDeliveryCost().getValue()
                                                                                                                                  .doubleValue()
                                                                                                                        : 0D);
            cart.setPrice(price);
            digitalDatalayer.setCart(cart);
        } catch (final Exception ex) {
            LOG.error("Exception while populating discount data:", ex);
        }
    }

    @Override
    public void storePaymentErrorData(final DigitalDatalayer digitalDatalayer, final String errorCode, final String errorPage) {
        try {
            PageInfo pageInfo;
            if (null == digitalDatalayer.getPage().getPageInfo()) {
                pageInfo = new PageInfo();
            } else {
                pageInfo = digitalDatalayer.getPage().getPageInfo();
            }

            final Error errorData = new Error();

            errorData.setErrorCode(errorCode);
            errorData.setErrorPageType(errorPage);

            pageInfo.setError(errorData);
            digitalDatalayer.getPage().setPageInfo(pageInfo);
        } catch (final Exception exception) {
            LOG.error("Exception while populating Payment Error data:", exception);
        }

    }

    @Override
    public void storeCheckoutStep(final DigitalDatalayer digitalDatalayer, final String page) {
        try {
            Checkout checkout;
            if (null == digitalDatalayer.getPage().getCheckout()) {
                checkout = new Checkout();
            } else {
                checkout = digitalDatalayer.getPage().getCheckout();
            }

            // Check the Checkout page
            if (page.equals(DistConstants.CheckoutPage.PAGE_CHECKOUT_DELIVERY)) {
                checkout.setCheckoutStep(1);
            } else if (page.equals(DistConstants.CheckoutPage.PAGE_CHECKOUT_REVIEW_AND_PAY)) {
                checkout.setCheckoutStep(2);
            }

            digitalDatalayer.getPage().setCheckout(checkout);

        } catch (final Exception exception) {
            LOG.error("Exception while populating Checkout Page data:", exception);
        }
    }

    @Override
    public void getCartForDTM(final DigitalDatalayer digitalDatalayer, final AbstractOrderModel cartData, final String customerId) {
        if (null == cartData) {
            return;
        }

        try {
            final Price price = getPriceData(cartData);
            final Cart cart = getCartData(digitalDatalayer, cartData, price);
            digitalDatalayer.setCart(cart);
        } catch (final Exception ex) {
            LOG.error("Error while populating DTM cart elements", ex);
        }
    }

    @Override
    public Transaction getTransactionData(final DigitalDatalayer digitalDatalayer, final AbstractOrderModel cartData, final String customerId) {
        final Price price = getPriceData(cartData);

        Transaction transaction = digitalDatalayer.getTransaction();
        if (null == transaction) {
            transaction = new Transaction();
        }
        transaction.setTransactionID(cartData.getCode());
        transaction.setErpOrderNumber(null != cartData.getErpOrderCode() ? cartData.getErpOrderCode() : "");
        if (cartData.isOpenOrder() && null != cartData.getErpOpenOrderCode()) {
            transaction.setErpOrderNumber(cartData.getErpOpenOrderCode());
        }
        transaction.setProfile(new Profile());
        transaction.getProfile().setProfileInfo(new ProfileInfo());
        transaction.getProfile().getProfileInfo().setCustomerID(customerId);
        transaction.getProfile().getProfileInfo().setVisitorID(customerId);

        if (null != cartData.getDeliveryAddress()) {
            final AddressData deliveryAddress = addressConverter.convert(cartData.getDeliveryAddress());
            final Address address = new Address();
            address.setCountry(null != deliveryAddress.getCountry() ? deliveryAddress.getCountry().getIsocode() : "");
            address.setCity(null != deliveryAddress.getTown() ? deliveryAddress.getTown() : "");
            address.setPostalCode(null != deliveryAddress.getPostalCode() ? deliveryAddress.getPostalCode().toLowerCase() : "");
            address.setLine1(null != deliveryAddress.getLine1() ? deliveryAddress.getLine1().toLowerCase() : "");
            address.setLine2(null != deliveryAddress.getLine2() ? deliveryAddress.getLine2().toLowerCase() : "");
            transaction.getProfile().setAddress(address);
        }
        transaction.setTotal(price);
        transaction.setItem(getCartItemsData(cartData));
        return transaction;
    }

    private Cart getCartData(final DigitalDatalayer digitalDatalayer, final AbstractOrderModel cartData, final Price price) {
        Cart cart = digitalDatalayer.getCart();
        if (null == cart) {
            cart = new Cart();
        }
        cart.setCartID(cartData.getCode());
        cart.setItem(getCartItemsData(cartData));
        cart.setPrice(price);
        final boolean fastCheckout = null != ((Boolean) getSessionService().getAttribute(DistConstants.Checkout.FAST_CHECKOUT))
                                                                                                                                ? ((Boolean) getSessionService().getAttribute(DistConstants.Checkout.FAST_CHECKOUT))
                                                                                                                                : false;
        cart.setFastcheckout(fastCheckout);
        return cart;
    }

    private Price getPriceData(final AbstractOrderModel cartData) {
        final Price price = new Price();
        price.setBasePrice(null != cartData.getNetSubTotal() ? cartData.getNetSubTotal().doubleValue() : 0D);
        price.setCartTotal(null != cartData.getTotalPrice() ? cartData.getTotalPrice().doubleValue() : 0D);
        price.setCreationDate(cartData.getCreationtime());
        price.setTaxRate(cartData.getTotalTax() == null ? 0.00D : cartData.getTotalTax().doubleValue());
        final CurrencyData currencyData = getStoreSessionFacade().getCurrentCurrency();
        price.setCartCurrency(
                              null != cartData.getCurrency() ? cartData.getCurrency().getIsocode()
                                                             : (null != currencyData ? currencyData.getIsocode().toLowerCase() : ""));
        if (null != cartData.getErpVoucherInfo() && null != cartData.getErpVoucherInfo().getFixedValue()) {
            price.setVoucherCode(cartData.getErpVoucherInfo().getCode());
            price.setVoucherDiscount(cartData.getErpVoucherInfo().getFixedValue().doubleValue());
        } else {
            price.setVoucherCode("");
            price.setVoucherDiscount(0D);
        }
        price.setShipmentType((null != cartData.getDeliveryMode() && null != cartData.getDeliveryMode().getName(Locale.ENGLISH))
                                                                                                                                 ? cartData.getDeliveryMode()
                                                                                                                                           .getName(Locale.ENGLISH)
                                                                                                                                           .toLowerCase()
                                                                                                                                 : "");
        price.setPriceWithTax(null != cartData.getTotalTax() ? cartData.getNetSubTotal().doubleValue() + cartData.getTotalTax().doubleValue() : 0D);
        price.setTotalDistinctProducts(null != cartData.getEntries() ? cartData.getEntries().size() : 0D);
        price.setShippingCost(cartData.getDeliveryCost());
        price.setChosenDeliveryDate(cartData.getRequestedDeliveryDate());
        price.setPaymentMethod(null != cartData.getPaymentMode() ? cartData.getPaymentMode().getCode().toLowerCase() : "");
        price.setShippingMethod(null != cartData.getDeliveryMode() ? cartData.getDeliveryMode().getCode().toLowerCase() : "");
        price.setTransactionDate(cartData.getCreationtime());
        price.setTotalQuantity(
                               cartData.getEntries().stream().reduce(0.0, (acc, entry) -> acc + entry.getQuantity().doubleValue(),
                                                                     (acc1, acc2) -> acc1 + acc2));
        return price;
    }

    private List<Item> getCartItemsData(final AbstractOrderModel cartData) {
        return cartData.getEntries().stream().filter(data -> data.getProduct() != null).map(data -> {
            final ProductModel productModel = data.getProduct();
            final Category categoryData = new Category();
            populateCategoryData(productModel, categoryData);
            final Item item = new Item();
            item.setCategory(categoryData);
            item.setPrice(new Price());
            item.getPrice().setBasePrice(null != data.getBasePrice() ? data.getBasePrice().doubleValue() : 0D);
            final ProductData productData = productFacade.getProductForCodeAndOptions(productModel.getCode(), PRODUCT_OPTIONS);
            item.setProductInfo(populateProductInfoFromData(productData));
            item.setQuantity(null != data.getQuantity() ? data.getQuantity().doubleValue() : 0D);
            return item;
        }).collect(Collectors.toList());
    }

    private void populateCategoryData(final ProductModel productModel, final Category categoryData) {
        final List<String> productCategoryInfo = new ArrayList<>();
        final StringBuilder path = new StringBuilder();
        if (productModel != null && CollectionUtils.isNotEmpty(productModel.getSupercategories())) {
            for (final CategoryModel category : productModel.getSupercategories()) {
                categoryData.setCategoryPath(getProductCategoryPath(category, productCategoryInfo, path));
            }
            categoryData.setPrimaryCategory(
                                            null != productModel.getPrimarySuperCategory() ? productModel.getPrimarySuperCategory().getName(Locale.ENGLISH)
                                                                                                         .toLowerCase()
                                                                                           : "");
            categoryData.setProductFamily(
                                          null != productModel.getProductFamilyName(Locale.ENGLISH) ? productModel.getProductFamilyName(Locale.ENGLISH)
                                                                                                                  .toLowerCase()
                                                                                                    : "");
            // categoryData.setCategoryHL1((CollectionUtils.isNotEmpty(productCategoryInfo)
            // && productCategoryInfo.size() >= 1) ?
            // productCategoryInfo.get(0) : "");
            categoryData.setCategoryHL1((CollectionUtils.isNotEmpty(productCategoryInfo) && productCategoryInfo.size() >= CATEGORY_SIZE_1)
                                                                                                                                           ? productCategoryInfo.get(productCategoryInfo.size()
                                                                                                                                                                     - CATEGORY_SIZE_1)
                                                                                                                                                                .toLowerCase()
                                                                                                                                           : "");
            categoryData.setCategoryHL2((CollectionUtils.isNotEmpty(productCategoryInfo) && productCategoryInfo.size() >= CATEGORY_SIZE_2)
                                                                                                                                           ? productCategoryInfo.get(productCategoryInfo.size()
                                                                                                                                                                     - CATEGORY_SIZE_2)
                                                                                                                                                                .toLowerCase()
                                                                                                                                           : "");
            categoryData.setCategoryHL3((CollectionUtils.isNotEmpty(productCategoryInfo) && productCategoryInfo.size() >= CATEGORY_SIZE_3)
                                                                                                                                           ? productCategoryInfo.get(productCategoryInfo.size()
                                                                                                                                                                     - CATEGORY_SIZE_3)
                                                                                                                                                                .toLowerCase()
                                                                                                                                           : "");

        }
    }

    @Override
    public void storeDigitalDatalayerElements(final DigitalDatalayer digitalDatalayer, final AbstractPageModel cmsPage, final String teaserTrackingId) {
        if (null == cmsPage) {
            return;
        }
        try {
            if (null == digitalDatalayer.getPage()) {
                digitalDatalayer.setPage(new Page());
            }

            if (null == digitalDatalayer.getPage().getPageInfo()) {
                digitalDatalayer.getPage().setPageInfo(new PageInfo());
            }
            final SiteChannel currentChannel = getSessionService().getAttribute(DistConstants.Session.CHANNEL);
            digitalDatalayer.getPage().getPageInfo().setChannel(null != currentChannel ? currentChannel.getCode().toLowerCase() : "");
            digitalDatalayer.getPage().getPageInfo().setPageID(null != cmsPage.getUid() ? cmsPage.getUid().toLowerCase() : "");
            // pageInfo.setVersion(null != model.asMap().get("currentVersion") ?
            // model.asMap().get("currentVersion").toString() : "");
            digitalDatalayer.getPage().getPageInfo().setPublisher(null != cmsPage.getLockedBy() ? cmsPage.getLockedBy().getUid() : "");
            digitalDatalayer.getPage().getPageInfo().setSysEnv(SYSTEM_ENV);
            digitalDatalayer.getPage().getPageInfo().setIssueDate(null != cmsPage.getCreationtime() ? cmsPage.getCreationtime() : null);
            if (null == digitalDatalayer.getPage().getPageCategory()) {
                digitalDatalayer.getPage().setPageCategory(new PageCategory());
            }

            digitalDatalayer.getPage().getPageCategory()
                            .setPageTemplate(null != cmsPage.getMasterTemplate() ? cmsPage.getMasterTemplate().getUid().toLowerCase() : "");
            digitalDatalayer.getPage().getPageCategory()
                            .setPageType((null != digitalDatalayer.getPage().getPageCategory().getPageType()
                                    && !digitalDatalayer.getPage().getPageCategory().getPageType().isEmpty())
                                                                                                              ? digitalDatalayer.getPage().getPageCategory()
                                                                                                                                .getPageType()
                                                                                                              : cmsPage.getType(Locale.ENGLISH));

            if (digitalDatalayer.getComponent() == null) {
                digitalDatalayer.setComponent(new ArrayList<>());
            }

            final Component component = new Component();
            final ComponentInfo componentInfo = new ComponentInfo();
            componentInfo.setComponentID(null != cmsPage.getUid() ? cmsPage.getUid().toLowerCase() : "");
            componentInfo.setComponentType(null != cmsPage.getType(Locale.ENGLISH) ? cmsPage.getType(Locale.ENGLISH).toLowerCase() : "");
            componentInfo.setComponentName(null != cmsPage.getName() ? cmsPage.getName().toLowerCase() : "");
            componentInfo.setTrackingCode(null != teaserTrackingId ? teaserTrackingId : "");
            componentInfo.setPlacement(null != cmsPage.getWtAreaCode() ? cmsPage.getWtAreaCode() : "");
            component.setComponentInfo(componentInfo);
            digitalDatalayer.getComponent().add(component);
        } catch (final Exception ex) {
            LOG.error("Error while populating DTM-CMS  elements", ex);
        }
    }

    @Override
    public List<Product> getProductsDTMDataLayer(final List<ProductData> results) {
        if (CollectionUtils.isNotEmpty(results)) {
            try {
                return results.stream().map(productData -> populateProductDTMObjects(productData)).collect(Collectors.toList());
            } catch (final Exception ex) {
                LOG.error("Error while populating DTM-CMS  elements", ex);
            }
        }
        return Collections.<Product> emptyList();
    }

    /**
     *
     * @param digitalDatalayer
     * @param createRMAResponseData
     *
     */
    @Override
    public void populateGuestCreateReturnResponse(final DigitalDatalayer digitalDatalayer, final GuestRMACreateRequestForm createRMARequestForm) {
        try {
            RmaData rmaData = digitalDatalayer.getRmaData();
            if (null == rmaData) {
                rmaData = new RmaData();
            }
            Rma rma = rmaData.getRma();
            if (null == rma) {
                rma = new Rma();
            }
            rma.setOrderId(createRMARequestForm.getOrderNumber());

            List<ReturnItem> returnItems = rma.getReturnItems();
            if (null == returnItems) {
                returnItems = new ArrayList<>();
            }

            final ReturnItem returnItem = new ReturnItem();
            returnItem.setItemNumber(createRMARequestForm.getArticleNumber());
            returnItem.setReturnQty(Double.valueOf(createRMARequestForm.getQuantity()));
            returnItem.setComment(createRMARequestForm.getCustomerText());
            returnItem.setAdditionalProperty("customerName", createRMARequestForm.getCustomerName());
            returnItems.add(returnItem);

            rma.setReturnItems(returnItems);
            rmaData.setRma(rma);
            digitalDatalayer.setRmaData(rmaData);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param digitalDatalayer
     * @param createRMAResponseData
     *
     */
    @Override
    public void populateReturnResponse(final DigitalDatalayer digitalDatalayer, final CreateRMARequestForm createRMARequestForm,
                                       final CreateRMAResponseData createRMAResponseData) {
        try {
            RmaData rmaData = digitalDatalayer.getRmaData();
            if (null == rmaData) {
                rmaData = new RmaData();
            }
            Rma rma = rmaData.getRma();
            if (null == rma) {
                rma = new Rma();
            }
            rma.setOrderId(createRMARequestForm.getOrderId());

            rma.setRmaHeaderStatus(createRMAResponseData.getRmaHeaderStatus());
            rma.setRmaId(createRMAResponseData.getRmaNumber());
            List<ReturnItem> returnItems = rma.getReturnItems();
            if (null == returnItems) {
                returnItems = new ArrayList<>();
            }
            final List<CreateRMAOrderEntryDataForm> orderEntries = createRMARequestForm.getOrderItems();
            if (null != orderEntries && orderEntries.size() > 0) {
                for (final CreateRMAOrderEntryDataForm orderEntry : orderEntries) {
                    final ReturnItem returnItem = new ReturnItem();
                    returnItem.setItemNumber(orderEntry.getArticleNumber());
                    returnItem.setItemStatus(orderEntry.getCustomerText());
                    returnItem.setRefundType(orderEntry.getRefundType());
                    returnItem.setReturnQty(orderEntry.getQuantity() != null ? Double.valueOf(orderEntry.getQuantity()) : null);
                    returnItem.setReturnReason(orderEntry.getReturnReasonID());
                    returnItems.add(returnItem);
                }
            }
            rma.setReturnItems(returnItems);
            rmaData.setRma(rma);
            digitalDatalayer.setRmaData(rmaData);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param digitalDatalayer
     * @param primaryCategory
     * @param pageType
     *
     */
    @Override
    public void populatePrimaryPageCategoryAndPageType(final DigitalDatalayer digitalDatalayer, final String primaryCategory, final String pageType) {
        try {
            if (null == digitalDatalayer.getPage()) {
                digitalDatalayer.setPage(new Page());
            }
            if (null == digitalDatalayer.getPage().getPageCategory()) {
                digitalDatalayer.getPage().setPageCategory(new PageCategory());
            }
            if (null == digitalDatalayer.getPage().getPageInfo()) {
                digitalDatalayer.getPage().setPageInfo(new PageInfo());
            }
            digitalDatalayer.getPage().getPageCategory().setPrimaryCategory(primaryCategory.toLowerCase());
            digitalDatalayer.getPage().getPageCategory().setPageType(pageType.toLowerCase());
            digitalDatalayer.getPage().getPageInfo().setPageName(primaryCategory.toLowerCase());
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param digitalDatalayer
     * @param primaryCategory
     * @param subCategoryL1
     * @param pageType
     *
     */
    @Override
    public void populatePageCategoryAndPageType(final DigitalDatalayer digitalDatalayer, final String primaryCategory, final String subCategoryL1,
                                                final String pageType) {
        try {
            if (null == digitalDatalayer.getPage()) {
                digitalDatalayer.setPage(new Page());
            }
            if (null == digitalDatalayer.getPage().getPageCategory()) {
                digitalDatalayer.getPage().setPageCategory(new PageCategory());
            }
            if (null == digitalDatalayer.getPage().getPageInfo()) {
                digitalDatalayer.getPage().setPageInfo(new PageInfo());
            }
            digitalDatalayer.getPage().getPageCategory().setPrimaryCategory(primaryCategory.toLowerCase());
            digitalDatalayer.getPage().getPageCategory().setSubCategoryL1(subCategoryL1.toLowerCase());
            digitalDatalayer.getPage().getPageCategory().setPageType(pageType.toLowerCase());
            digitalDatalayer.getPage().getPageInfo().setPageName(primaryCategory.toLowerCase() + "|" + subCategoryL1.toLowerCase());
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param digitalDatalayer
     * @param orderListData
     *
     */
    @Override
    public void populateReturnData(final DigitalDatalayer digitalDatalayer, final SearchPageData<OrderHistoryData> orderListData) {
        try {

            final List<OrderHistoryData> orders = orderListData.getResults();
            OrderData dRmaData = digitalDatalayer.getOrderData();
            if (null == dRmaData) {
                dRmaData = new OrderData();
            }
            final List<Order> dOrders = new ArrayList<>();

            dRmaData.setOrders(dOrders);

            for (final OrderHistoryData entry : orders) {
                final OrderHistoryData b2bEntry = (OrderHistoryData) entry;

                final Order order = new Order();
                order.setOrderID(entry.getCode());
                order.setRmaRaised(entry.isRmaStatus());
                order.setOrderDate(entry.getPlaced());
                order.setInvoiceNumbers(b2bEntry.getInvoiceIds());

                final Price price = new Price();
                price.setCartTotal(entry.getTotal().getValue().doubleValue());
                price.setCartCurrency(entry.getTotal().getCurrencyIso());
                order.setPrice(price);

                dOrders.add(order);
            }

            digitalDatalayer.setOrderData(dRmaData);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param digitalDatalayer
     * @param orderData
     *
     */
    @Override
    public void populateOrderReturnData(final DigitalDatalayer digitalDatalayer, final de.hybris.platform.commercefacades.order.data.OrderData orderData) {
        try {
            OrderData dRmaData = digitalDatalayer.getOrderData();
            if (null == dRmaData) {
                dRmaData = new OrderData();
            }

            final List<OrderEntryData> oEntries = orderData.getEntries();
            final Order order = new Order();
            order.setOrderID(orderData.getCode());
            order.setOrderDate(orderData.getOrderDate());

            final OrderHistoryData orderHistoryData = orderHistoryFacade.getOrderHistoryById(orderData.getCode());
            if (orderHistoryData != null) {
                order.setInvoiceNumbers(orderHistoryData.getInvoiceIds());
            }

            final Price price = new Price();

            price.setCartTotal(orderData.getTotalPrice().getValue().doubleValue());
            price.setCartCurrency(orderData.getTotalPrice().getCurrencyIso());

            order.setPrice(price);

            order.setProducts(orderData.getEntries().stream().map(OrderEntryData::getProduct)
                                       .map(this::populateProductDTMObjects).collect(Collectors.toList()));

            final List<Item> items = new ArrayList<>();

            for (final OrderEntryData entry : oEntries) {
                final Item item = new Item();
                final DistRMAEntryData rmaEntry = entry.getRmaData();
                if (null != rmaEntry) {
                    item.setAdditionalProperty("isReturnNotAllowed", rmaEntry.isNotAllowed());
                    item.setAdditionalProperty("returnNotAllowedDesc", rmaEntry.getNotAllowedDesc());
                    item.setAdditionalProperty("remainingReturnQty", rmaEntry.getRemainingReturnQty());
                    final List<RMAData> rmaDatas = rmaEntry.getRmas();
                    final List<Rma> rmas = new ArrayList<>();
                    if (null != rmaDatas) {
                        for (final RMAData rmaData : rmaDatas) {
                            final Rma rma = new Rma();
                            rma.setRmaHeaderStatus(rmaData.getRmaItemStatus() != null ? rmaData.getRmaItemStatus().name() : "");
                            rma.setRmaId(rmaData.getRmaNumber());
                            rmas.add(rma);

                        }
                    }
                    item.setRma(rmas);
                    items.add(item);
                }
            }
            order.setItem(items);
            dRmaData.setOrder(order);
            digitalDatalayer.setOrderData(dRmaData);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param digitalDatalayer
     * @param pageType
     *
     */
    @Override
    public void populatePageType(final DigitalDatalayer digitalDatalayer, final String pageType) {
        try {
            if (null == digitalDatalayer.getPage()) {
                digitalDatalayer.setPage(new Page());
            }
            if (null == digitalDatalayer.getPage().getPageCategory()) {
                digitalDatalayer.getPage().setPageCategory(new PageCategory());
            }
            if (null == digitalDatalayer.getPage().getPageInfo()) {
                digitalDatalayer.getPage().setPageInfo(new PageInfo());
            }
            digitalDatalayer.getPage().getPageCategory().setPageType(pageType.toLowerCase());

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param digitalDatalayer
     * @param primaryCategory
     * @param stepName
     * @param pageType
     *
     */
    @Override
    public void populatePrimaryPageCategoryStepAndPageType(final DigitalDatalayer digitalDatalayer, final String primaryCategory, final String stepName,
                                                           final String pageType) {
        try {
            if (null == digitalDatalayer.getPage()) {
                digitalDatalayer.setPage(new Page());
            }
            if (null == digitalDatalayer.getPage().getPageCategory()) {
                digitalDatalayer.getPage().setPageCategory(new PageCategory());
            }
            if (null == digitalDatalayer.getPage().getPageInfo()) {
                digitalDatalayer.getPage().setPageInfo(new PageInfo());
            }
            digitalDatalayer.getPage().getPageCategory().setPrimaryCategory(null != primaryCategory ? primaryCategory.toLowerCase() : "");
            digitalDatalayer.getPage().getPageCategory().setPageType(pageType.toLowerCase());
            digitalDatalayer.getPage().getPageInfo()
                            .setPageName((null != primaryCategory ? primaryCategory.toLowerCase() : "") + "|"
                                         + (null != stepName ? stepName.toLowerCase() : ""));
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     *
     * @param digitalDatalayer
     * @param registrationInfo
     *
     */
    @Override
    public void populateRegistrationType(final DigitalDatalayer digitalDatalayer, final RegistrationInfo registrationInfo) {
        try {
            final List<User> users = digitalDatalayer.getUser();
            final User user;
            if (null != users && users.size() > 0) {
                user = users.get(0);
            } else {
                user = new User();
            }
            List<Profile> userProfiles = user.getProfile();
            final Profile userProfile;
            if (null != user.getProfile() && user.getProfile().size() > 0) {
                userProfile = userProfiles.get(0);
            } else {
                userProfiles = new ArrayList<>();
                userProfile = new Profile();
            }

            final ProfileInfo profileInfo;
            if (null != userProfile.getProfileInfo()) {
                profileInfo = userProfile.getProfileInfo();
            } else {
                profileInfo = new ProfileInfo();
            }
            profileInfo.setRegistrationInfo(registrationInfo);
            userProfile.setProfileInfo(profileInfo);
            userProfiles.add(userProfile);
            user.setProfile(userProfiles);
            user.getRegistration().setStage("complete");

            final Page page = null != digitalDatalayer.getPage() ? digitalDatalayer.getPage() : new Page();
            final PageInfo pageInfo = (null != page && null != page.getPageInfo()) ? page.getPageInfo() : new PageInfo();
            pageInfo.setChannel(registrationInfo.getRegType());
            page.setPageInfo(pageInfo);
            digitalDatalayer.setPage(page);
            if (users != null && users.size() > 0) {
                users.set(0, user);
            } else {
                users.add(user);
            }
            digitalDatalayer.setUser(users);
            digitalDatalayer.setEventName(DigitalDatalayer.EventName.REGISTER);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void addProductFamilyNameToSubcategory(PageCategory pageCategory, List<String> breadcrumbs, String productFamilyName) {
        if (isProductFamilyPage(pageCategory.getPageType())) {
            setProductFamilyNameOnFinalSubcategory(pageCategory, breadcrumbs.size(), productFamilyName);
        }
    }

    private boolean isProductFamilyPage(String pageType) {
        return DigitalDatalayerConstants.PageType.PRODUCTFAMILYPAGE.equals(pageType);
    }

    private void setProductFamilyNameOnFinalSubcategory(PageCategory pageCategory, int breadcrumbSize, String productFamilyName) {
        switch (breadcrumbSize) {
            case 1:
                pageCategory.setSubCategoryL1(productFamilyName);
                break;
            case 2:
                pageCategory.setSubCategoryL2(productFamilyName);
                break;
            case 3:
                pageCategory.setSubCategoryL3(productFamilyName);
                break;
            case 4:
                pageCategory.setSubCategoryL4(productFamilyName);
                break;
            case 5:
                pageCategory.setSubCategoryL5(productFamilyName);
                break;
        }
    }

    @Override
    public void populateBannersFromPosition(final DigitalDatalayer digitalDatalayer, final Model model, final String position, final String prefix) {
        final AbstractPageModel pageModel = (AbstractPageModel) model.asMap().get(CMS_PAGE_KEY);

        if (pageModel != null) {
            try {
                final ContentSlotData contentSlotData = pageService.getContentSlotForPage(pageModel, position);
                final ContentSlotModel contentSlotModel = contentSlotData.getContentSlot();

                final Stream<String> topLevelBanners = contentSlotModel.getCmsComponents().stream().filter(AbstractBannerComponentModel.class::isInstance)
                                                                       .map(banner -> (prefix != null ? prefix : "") + banner.getName());

                final Stream<String> containedBanners = contentSlotModel.getCmsComponents().stream()
                                                                        .filter(AbstractCMSComponentContainerModel.class::isInstance)
                                                                        .map(container -> (AbstractCMSComponentContainerModel) container)
                                                                        .flatMap(container -> container.getCurrentCMSComponents().stream())
                                                                        .filter(AbstractBannerComponentModel.class::isInstance)
                                                                        .map(banner -> (prefix != null ? prefix : "") + banner.getName());

                final List<String> banners = Stream.concat(topLevelBanners, containedBanners).collect(Collectors.toList());

                if (CollectionUtils.isNotEmpty(digitalDatalayer.getPage().getPageInfo().getBanners())) {
                    digitalDatalayer.getPage().getPageInfo().getBanners().addAll(banners);
                } else {
                    digitalDatalayer.getPage().getPageInfo().setBanners(banners);
                }
            } catch (final CMSItemNotFoundException e) {
                LOG.error("populateBannersFromPosition - Content slot {} not found on page {}", position, pageModel.getUid());
            }
        } else {
            LOG.error("populateBannersFromPosition - Page not found in model");
        }
    }

    @Override
    public void populateFFTrackingAttribute(final DigitalDatalayer digitalDatalayer, final Boolean pdp) {
        if (null == digitalDatalayer.getPage()) {
            digitalDatalayer.setPage(new Page());
        }
        if (null == digitalDatalayer.getPage().getPageCategory()) {
            digitalDatalayer.getPage().setPageCategory(new PageCategory());
        }
        if (null == digitalDatalayer.getPage().getPageInfo()) {
            digitalDatalayer.getPage().setPageInfo(new PageInfo());
        }

        final PageInfo pageInfo = digitalDatalayer.getPage().getPageInfo();
        if (null == pageInfo.getSearch()) {
            digitalDatalayer.getPage().getPageInfo().setSearch(new Search());
        }
        final Search search = digitalDatalayer.getPage().getPageInfo().getSearch();
        search.setPdp(pdp);
        search.setAddtocart(Boolean.TRUE);
    }

    /**
     * @param category
     * @param productCategoryInfo
     * @param path
     * @return String
     */
    private String getProductCategoryPath(final CategoryModel category, final List<String> productCategoryInfo, final StringBuilder path) {
        for (final CategoryModel superCategory : category.getSupercategories()) {
            if ((superCategory != null && !(superCategory instanceof ClassificationClassModel)) && //
                    superCategory.getLevel().equals(Integer.valueOf(category.getLevel().intValue() - 1)) && //
                    superCategory.getLevel().intValue() > 0) {
                productCategoryInfo.add(superCategory.getName(Locale.ENGLISH).toLowerCase());
                getProductCategoryPath(superCategory, productCategoryInfo, path);
            }
        }
        return path.append("/").append(category.getName(Locale.ENGLISH).toLowerCase()).toString();
    }

    @Override
    public List<RemoveCart> populateCartRemoval(final CartModificationData cartData) {
        final RemoveCart removeCart = new RemoveCart();
        final List<RemoveCart> removeCartList = new ArrayList();
        removeCart.setProductID(cartData.getEntry().getProduct().getCode());
        final Double quantity = (double) Math.abs(cartData.getQuantityAdded());
        removeCart.setQuantity(quantity);
        removeCartList.add(removeCart);
        return removeCartList;
    }

    // Getters & Setters

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public BaseSiteService getBaseSiteService() {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService) {
        this.baseSiteService = baseSiteService;
    }

    public DistB2BCartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(final DistB2BCartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    public DistrelecStoreSessionFacade getStoreSessionFacade() {
        return storeSessionFacade;
    }

    public void setStoreSessionFacade(final DistrelecStoreSessionFacade storeSessionFacade) {
        this.storeSessionFacade = storeSessionFacade;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public DistUrlResolver<ProductModel> getProductModelUrlResolver() {
        return productModelUrlResolver;
    }

    public void setProductModelUrlResolver(final DistUrlResolver<ProductModel> productModelUrlResolver) {
        this.productModelUrlResolver = productModelUrlResolver;
    }

    public SessionService getSessionService() {
        return sessionService;
    }

    public void setSessionService(final SessionService sessionService) {
        this.sessionService = sessionService;
    }
}
