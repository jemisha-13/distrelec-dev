/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.product.impl;

import static com.namics.distrelec.b2b.core.enums.DistProductExclusionReasonEnum.PRODUCT_AVAILABLE_TO_B2B_ONLY;
import static com.namics.distrelec.b2b.core.enums.DistProductExclusionReasonEnum.PUNCHOUT_PRODUCT_EXCLUDED;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.enums.DistProductExclusionReasonEnum;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.customer.strategies.CheckoutCustomerStrategy;
import com.namics.distrelec.b2b.core.service.environment.RuntimeEnvironmentService;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.product.data.PIMAlternateResult;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult;
import com.namics.distrelec.b2b.core.service.product.data.PunchoutFilterResult.PunchoutReason;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.site.DistrelecBaseStoreService;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.service.stock.DistStockNotificationService;
import com.namics.distrelec.b2b.core.service.wishlist.DistWishlistService;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.distrelec.b2b.core.util.LocalDateUtil;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.converters.ProductAvailabilityConverter;
import com.namics.distrelec.b2b.facades.product.converters.populator.DistEnergyEfficencyPopulator;
import com.namics.distrelec.b2b.facades.product.converters.populator.DistProductBasicPopulator;
import com.namics.distrelec.b2b.facades.product.data.EnergyEfficencyData;
import com.namics.distrelec.b2b.facades.product.data.PickupStockLevelData;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;
import com.namics.distrelec.b2b.facades.product.data.ProductEnhancementData;
import com.namics.hybris.toolbox.DateTimeUtils;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.references.ProductReferenceService;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.converters.populator.ProductPricePopulator;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.commercefacades.product.impl.DefaultProductFacade;
import de.hybris.platform.commerceservices.enums.SiteChannel;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Default implementation of {@link DistrelecProductFacade}
 *
 * @author pnueesch, Namics AG
 */
public class DefaultDistrelecProductFacade extends DefaultProductFacade implements DistrelecProductFacade {

    private static final Long FALLBACK_MINIMUM_QTY = 1L;

    private static final long TEN_DAYS = 10L;

    private static final Logger LOG = Logger.getLogger(DefaultDistrelecProductFacade.class);

    @Autowired
    protected ErpStatusUtil erpStatusUtil;

    @Autowired
    @Qualifier("erp.availabilityService")
    private AvailabilityService availabilityService;

    @Autowired
    private ProductAvailabilityConverter productAvailabilityConverter;

    @Autowired
    private CartService cartService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private DistrelecCMSSiteService cmsSiteService;

    @Autowired
    private CheckoutCustomerStrategy checkoutCustomerStrategy;

    @Autowired
    private ProductReferenceService productReferenceService;

    private DistEnergyEfficencyPopulator energyEfficencyPopulator;

    private Converter<ProductModel, ProductData> productReferenceTargetConverter;

    private Converter<ProductModel, ProductData> occProductReferenceTargetConverter;

    @Autowired
    private RuntimeEnvironmentService runtimeEnvironmentService;

    @Autowired
    @Qualifier("productPricePopulator")
    private ProductPricePopulator<ProductModel, ProductData> productPricePopulator;

    @Autowired
    @Qualifier("productBasicPopulator")
    private DistProductBasicPopulator basicProductPopulator;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistStockNotificationService distStockNotificationService;

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    @Autowired
    private DistWishlistService distWishlistService;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private DistCartService distCartService;

    @Autowired
    private DistrelecBaseStoreService baseStoreService;

    @Override
    public List<ProductAvailabilityData> getAvailability(final List<String> productCodes) {
        return getAvailability(productCodes, Boolean.FALSE);
    }

    @Override
    public List<ProductAvailabilityData> getAvailability(final List<String> productCodes, final Boolean detailInfo) {
        if (CollectionUtils.isNotEmpty(productCodes)) {
            final List<ProductAvailabilityExtModel> availability = getAvailabilityService().getAvailability(productCodes, detailInfo);
            return Converters.convertAll(availability, getProductAvailabilityConverter());
        }
        return Collections.<ProductAvailabilityData> emptyList();
    }

    @Override
    public Pair<Boolean, Date> getPickupAvailabilityDate(Map<String, Integer> productQuantityMap, String warehouseCode) {
        LocalDate today = LocalDate.now();
        LocalDate latestDate = getLatestDate(today);

        final List<ProductAvailabilityData> availabilities = getAvailability(productQuantityMap, Boolean.TRUE);
        Pair<Boolean, LocalDate> pickupDate = getLatestPickupDate(warehouseCode, today, latestDate, availabilities);

        return Pair.of(pickupDate.getLeft(), LocalDateUtil.skipWeekendToNextWorkingDateAndConvertToDate(pickupDate.getRight()));
    }

    private Pair<Boolean, LocalDate> getLatestPickupDate(String warehouseCode, LocalDate earliestDate, LocalDate latestDate,
                                                         List<ProductAvailabilityData> availabilities) {
        boolean isAvailableForImmediatePickup = availabilities.stream()
                                                              .filter(availability -> availability.getStockLevelPickup() != null)
                                                              .allMatch(availability -> getPickUpQuantityAvailable(availability,
                                                                                                                   warehouseCode) >= availability.getRequestedQuantity());

        if (isAvailableForImmediatePickup) {
            return Pair.of(Boolean.TRUE, earliestDate);
        }

        return Pair.of(Boolean.FALSE, getBackorderOrFallbackDate(latestDate, availabilities));
    }

    private LocalDate getBackorderOrFallbackDate(LocalDate latestDate, List<ProductAvailabilityData> availabilities) {
        return availabilities.stream()
                             .filter(availability -> availability.getBackorderQuantity() != null
                                     && availability.getBackorderQuantity() >= calculateRemainingQty(availability))
                             .filter(availability -> availability.getBackorderDeliveryDate() != null)
                             .map(availability -> LocalDateUtil.convertDateToLocalDate(availability.getBackorderDeliveryDate()))
                             .max(LocalDate::compareTo).orElse(latestDate);
    }

    private int calculateRemainingQty(ProductAvailabilityData availability) {
        if (availability.getStockLevelTotal() != null) {
            return availability.getRequestedQuantity() - availability.getStockLevelTotal();
        }
        return availability.getRequestedQuantity();
    }

    private Integer getPickUpQuantityAvailable(ProductAvailabilityData availability, String warehouseCode) {
        return availability.getStockLevelPickup().stream()
                           .filter(pickupStockLevelData -> StringUtils.equals(warehouseCode,
                                                                              pickupStockLevelData.getWarehouseCode()))
                           .map(PickupStockLevelData::getStockLevel)
                           .findFirst()
                           .orElse(0);
    }

    private LocalDate getLatestDate(LocalDate today) {
        return today.plusDays(TEN_DAYS);
    }

    @Override
    public List<ProductAvailabilityData> getAvailabilityForEntries(final List<AbstractOrderEntryModel> entries) {
        return getAvailabilityForEntries(entries, Boolean.FALSE);
    }

    @Override
    public List<ProductAvailabilityData> getAvailabilityForEntries(final List<AbstractOrderEntryModel> entries, final Boolean detailInfo) {
        if (CollectionUtils.isEmpty(entries)) {
            return Collections.<ProductAvailabilityData> emptyList();
        }

        final List<ProductAvailabilityExtModel> availability = getAvailabilityService().getAvailabilityForEntries(entries, detailInfo);
        return Converters.convertAll(availability, getProductAvailabilityConverter());
    }

    @Override
    public List<ProductAvailabilityData> getAvailabilityAllCountries(final List<String> productCodes) {
        return getAvailabilityAllCountries(productCodes, Boolean.FALSE);
    }

    @Override
    public List<ProductAvailabilityData> getAvailabilityAllCountries(final List<String> productCodes, final Boolean detailInfo) {
        if (productCodes != null) {
            final List<ProductAvailabilityData> results = new ArrayList<>();
            for (final CMSSiteModel cmsSite : getCmsSiteService().getSites()) {
                final List<ProductAvailabilityExtModel> availability = getAvailabilityService().getAvailability(productCodes, detailInfo);
                final List<ProductAvailabilityData> partOfResults = Converters.convertAll(availability, getProductAvailabilityConverter());
                for (final ProductAvailabilityData data : partOfResults) {
                    data.setCountry(cmsSite.getCountry().getIsocode());
                }
                results.addAll(partOfResults);
            }
            return results;
        }
        return Collections.<ProductAvailabilityData> emptyList();
    }

    @Override
    public List<ProductAvailabilityData> getAvailability(final Map<String, Integer> productCodesQuantity, final Boolean detailInfo) {
        return getAvailability(productCodesQuantity, detailInfo, true);
    }

    @Override
    public List<ProductAvailabilityData> getAvailability(final Map<String, Integer> productCodesQuantity, final Boolean detailInfo,
                                                         final boolean useCache) {
        if (productCodesQuantity != null) {
            final List<String> productCodes = new ArrayList<>(productCodesQuantity.keySet());
            final List<ProductAvailabilityExtModel> productsAvailability = getAvailabilityService().getAvailability(productCodes, detailInfo, useCache);

            // Fill the requestedQuantity. The requested quantity (from product
            // detail or cart) is needed to calculate the availability
            // icons
            for (final ProductAvailabilityExtModel availability : productsAvailability) {
                // Set the requestedQuantity
                availability.setRequestedQuantity(productCodesQuantity.get(availability.getProductCode()));
            }

            return Converters.convertAll(productsAvailability, getProductAvailabilityConverter());
        }

        return Collections.<ProductAvailabilityData> emptyList();
    }

    @Override
    public List<ProductData> getSimilarProducts(final ProductModel productModel, final int offset, final int size) {
        final List<ProductModel> similarProducts = getProductService().getSimilarProducts(productModel, offset, size);
        return convertProducts(similarProducts);
    }

    @Override
    public List<ProductData> getSimilarProducts(final String productCode, final int offset, final int size) {
        return getSimilarProducts(getProductService().getProductForCode(productCode), offset, size);
    }

    @Override
    public List<ProductData> getProductsReferences(final String productCode, final List<ProductReferenceTypeEnum> referenceTypes, final int offset,
                                                   final int size) {
        final ProductModel product = getProductService().getProductForCode(productCode);
        final List<ProductData> productReferences = getProductsReferences(Arrays.asList(product), referenceTypes, offset, size);
        return productReferences.stream()
                                .filter(ProductData::isBuyable)
                                .collect(Collectors.toList());
    }

    @Override
    public List<ProductReferenceData> getProductReferencesForCode(final String code, final List referenceTypes, final List list, final Integer limit) {
        final List<ProductReferenceData> productReferences = super.getProductReferencesForCode(code, referenceTypes, list, limit);
        return productReferences.stream().filter(ref -> isProductBuyable(ref.getTarget().getCode())).collect(Collectors.toList());
    }

    @Override
    public List<ProductData> getProductsReferences(final List<ProductModel> sources, final List<ProductReferenceTypeEnum> referenceTypes, final int offset,
                                                   final int size) {
        final List<ProductModel> productAccessories = getProductService().getProductsReferences(sources, referenceTypes, offset, size);
        return convertProducts(productAccessories);
    }

    @Override
    public boolean isProductBuyable(final String productCode) {
        return getProductService().isProductBuyable(getProductService().getProductForCode(productCode));
    }

    @Override
    public boolean isProductBuyable(String productCode, boolean isPunchedOut) {
        return getProductService().isProductBuyable(getProductService().getProductForCode(productCode), isPunchedOut);
    }

    @Override
    public boolean isProductPunchedOut(final String productCode) {
        return getProductService().isProductPunchedOut(getProductService().getProductForCode(productCode));
    }

    @Override
    public boolean treatAsEOL(final String productCode) {
        return !getProductService().isProductListedForCurrentSalesOrg(getProductService().getProductForCode(productCode));
    }

    @Override
    public String getProductSalesStatus(final String productCode) {
        return getProductService().getProductSalesStatus(getProductService().getProductForCode(productCode));
    }

    @Override
    public DistSalesStatusModel getProductSalesStatusModel(final String productCode) {
        if (!cmsSiteService.isViewedInSharedInternationalSite()) {
            return getProductService().getProductSalesStatusModel(getProductService().getProductForCode(productCode));
        } else {
            final DistSalesStatusModel dummyStatusModel = getModelService().create(DistSalesStatusModel.class);
            dummyStatusModel.setBuyableInShop(true);
            dummyStatusModel.setEndOfLifeInShop(false);
            dummyStatusModel.setNewInShop(false);
            dummyStatusModel.setVisibleInShop(true);
            return dummyStatusModel;
        }
    }

    @Override
    public Map<String, String> getSalesStatusForEntries(final List<AbstractOrderEntryModel> entries) {
        return getProductService().getSalesStatusForEntries(entries);
    }

    @Override
    public Collection<PunchoutFilterResult> getPunchOutFilters(final String productCode) {
        return getProductService().getPunchOutFilters(getProductService().getProductForCode(productCode));
    }

    @Override
    public Collection<PunchoutFilterResult> getPunchOutFilters(final String productCode, final SiteChannel channel) {
        return getProductService().getPunchOutFilters(getProductService().getProductForCode(productCode), channel);
    }

    @Override
    public boolean isEndOfLife(final String productCode) {
        return getProductService().isEndOfLife(getProductService().getProductForCode(productCode));
    }

    @Override
    public ProductData getProductForTypeOrCodeAndOptions(final String typeName, final Collection<ProductOption> options)
                                                                                                                         throws UnknownIdentifierException,
                                                                                                                         IllegalArgumentException {
        final ProductModel productModel = getProductService().getProductForTypeOrCode(typeName);
        if (productModel != null) {
            return getProductForCodeAndOptions(productModel.getCode(), options);
        } else {
            throw new UnknownIdentifierException("No product found for typeName/code " + typeName);
        }
    }

    @Override
    public boolean isProductReplaced(final String ProductCode) {
        final ProductModel productModel = getProductService().getProductForCode(ProductCode);

        final Date replacementFromDate = productModel.getReplacementFromDate();
        final Date replacementUntilDate = productModel.getReplacementUntilDate();

        if (replacementFromDate == null || replacementUntilDate == null) {
            return false;
        }

        final Date today = DateTimeUtils.getTodaysMidnightPlus1MinuteAsDate();
        return today.after(replacementFromDate) && today.before(replacementUntilDate);
    }

    @Override
    public ProductData getProductForOptions(final String productCode, final Collection<? extends ProductOption> options) {
        return getProductForCodeAndOptions(productCode, options);
    }

    @Override
    public List<ProductData> getProductListForCodesAndOptions(final List<String> codes, final Collection<ProductOption> options) {
        final List<ProductModel> productModels = getProductService().getProductListForCodes(codes);
        final List<ProductData> productData = getProductConverter().convertAll(productModels);

        if (options != null) {
            for (int i = 0; i < productModels.size(); i++) {
                getProductConfiguredPopulator().populate(productModels.get(i), productData.get(i), options);
            }
        }

        return productData;
    }

    @Override
    public boolean isDangerousProduct(final String productCode) {
        final ProductModel productModel = getProductService().getProductForCode(productCode);
        return productModel.getTransportGroup() != null && productModel.getTransportGroup().isDangerous();
    }

    @Override
    public EnergyEfficencyData getEnergyEfficencyData(final String productCode) {
        final ProductModel product = getProductService().getProductForCode(productCode);
        if (product != null) {
            final EnergyEfficencyData data = new EnergyEfficencyData();
            energyEfficencyPopulator.populate(product, data);
            if (StringUtils.isNotBlank(data.getEfficency()) || StringUtils.isNotBlank(data.getPower())) {
                return data;
            }
        }
        return null;
    }

    @Override
    public ProductData getProductDataBasicPopulated(final ProductModel product) {
        final ProductData productData = new ProductData();
        basicProductPopulator.populate(product, productData);
        productPricePopulator.populate(product, productData);
        if (product.getManufacturer() != null) {
            productData.setManufacturer(product.getManufacturer().getName());
        }
        return productData;
    }

    @Override
    public boolean isProductExcluded(final String productCode) {
        // List All the punchout filters for the given product
        // check if the product exists
        try {
            if (getProductService().getProductForCode(productCode) != null) {
                // TO-DO remove collection variable
                final Collection<PunchoutFilterResult> collection = getPunchOutFilters(productCode);
                return collection.stream()
                                 .anyMatch(filterResult -> StringUtils.equals(filterResult.getPunchOutReason().getValue(), PunchoutReason.COUNTRY.getValue())
                                         || StringUtils.equals(filterResult.getPunchOutReason().getValue(), PunchoutReason.MANUFACTURER.getValue())
                                         || StringUtils.equals(filterResult.getPunchOutReason().getValue(), PunchoutReason.CUSTOMER.getValue())
                                         || StringUtils.equals(filterResult.getPunchOutReason().getValue(), PunchoutReason.CUSTOMER_TYPE.getValue()));
            }
        } catch (final UnknownIdentifierException exception) {
            LOG.error(exception.getMessage());
            return false;
        } catch (final Exception exception) {
            // Swallow this exception and print to log because the purpose of this call is not to fetch the product, rather it is only to
            // confirm its existence
            LOG.error(exception.getMessage());
        }
        return false;
    }

    @Override
    public boolean isProductExcludedForSiteChannel(final String productCode, final SiteChannel siteChannel) {
        // List All the punchout filters for the given product
        // check if the product exists
        try {
            if (getProductService().getProductForCode(productCode) != null) {
                // TO-DO remove collection variable
                final Collection<PunchoutFilterResult> collection = getPunchOutFilters(productCode, siteChannel);
                return collection.stream()
                                 .anyMatch(filterResult -> StringUtils.equals(filterResult.getPunchOutReason().getValue(), PunchoutReason.COUNTRY.getValue())
                                         || StringUtils.equals(filterResult.getPunchOutReason().getValue(), PunchoutReason.MANUFACTURER.getValue())
                                         || StringUtils.equals(filterResult.getPunchOutReason().getValue(), PunchoutReason.CUSTOMER.getValue())
                                         || StringUtils.equals(filterResult.getPunchOutReason().getValue(), PunchoutReason.CUSTOMER_TYPE.getValue()));
            }
        } catch (final Exception exception) {
            // Swallow this exception and print to log because the purpose of this call is not to fetch the product, rather it is only to
            // confirm its existence
            LOG.error(exception.getMessage());
        }
        return false;
    }

    @Override
    public boolean enablePunchoutFilterLogic() {
        return StringUtils.equalsIgnoreCase("ON", getConfigurationService().getConfiguration().getString("punchout.filter.switch"));
    }

    @Override
    public String getRelevantSalesUnit(final String productCode) {

        return getProductService().getProductForTypeOrCode(productCode).getRelevantSalesUnit();

    }

    @Override
    public List<CMSSiteModel> getAvailableCMSSitesForProduct(final ProductModel product) {
        return getProductService().getAvailableCMSSitesByProduct(product);
    }

    @Override
    public ProductData getProductForCodeAndOptions(final ProductModel product, final Collection<ProductOption> options)
                                                                                                                        throws UnknownIdentifierException,
                                                                                                                        IllegalArgumentException {
        if (product == null) {
            return null;
        }

        final ProductData productData = (ProductData) getProductConverter().convert(product);

        if (options != null && !options.isEmpty()) {
            getProductConfiguredPopulator().populate(product, productData, options);
        }

        return productData;
    }

    @Cacheable(cacheNames = "alternativesWithoutOffsetResponse", cacheManager = "alternativesWithoutOffsetCacheManager", key = "{#productCode, @i18NService.getCurrentLocale()}")
    @Override
    public List<ProductData> getMultipleProductAlternatives(final String productCode) {
        final ProductModel productModel = getProductService().getProductForCode(productCode);
        final List<ProductReferenceTypeEnum> referenceTypes = Arrays.asList(ProductReferenceTypeEnum.DIS_ALTERNATIVE_BETTERVALUE,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_DE,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_SIMILAR,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_UPGRADE);
        return getProductsReferencesForAlternative(Arrays.asList(productModel), referenceTypes);
    }

    @Cacheable(cacheNames = "alternativesResponse", cacheManager = "alternativesCacheManager", key = "{#product, #offset, #size, @i18NService.getCurrentLocale()}")
    @Override
    public List<ProductData> getMultipleProductAlternatives(final String product, final int offset, final int size) {
        final ProductModel productModel = getProductService().getProductForCode(product);
        final List<ProductReferenceTypeEnum> referenceTypes = Arrays.asList(ProductReferenceTypeEnum.DIS_ALTERNATIVE_BETTERVALUE,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_DE,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_SIMILAR,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_UPGRADE);
        return getProductsReferencesForAlternative(Arrays.asList(productModel), referenceTypes, offset, size);

    }

    @Override
    public Long getMinimumOrderQty(String productCode) {
        final ProductModel product = getProductService().getProductForCode(productCode);
        final DistSalesOrgProductModel salesOrgProduct = distSalesOrgProductService.getCurrentSalesOrgProduct(product);
        if (salesOrgProduct != null) {
            return salesOrgProduct.getOrderQuantityMinimum() != null ? salesOrgProduct.getOrderQuantityMinimum() : FALLBACK_MINIMUM_QTY;
        }
        return FALLBACK_MINIMUM_QTY;
    }

    @Override
    public boolean isPhasedOutOrSuspended(String productCode) {
        final ProductModel product = getProductService().getProductForCode(productCode);
        return getProductService().isProductNotForSale(product) || getProductService().isSuspendedProduct(product);
    }

    @Override
    public List<String> getProductCodesForBlockedSalesStatus() {
        if (distCartService.hasSessionCart()) {
            return distCartService.getSessionCart().getEntries().stream()
                                  .map(AbstractOrderEntryModel::getProduct)
                                  .filter(this::isBlockedProduct)
                                  .map(ProductModel::getCode)
                                  .toList();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isBlockedProduct(ProductModel product) {
        List<String> blockedStatusCodes = erpStatusUtil.getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_SALES_STATUS_BLOCKED);
        if (CollectionUtils.isNotEmpty(blockedStatusCodes) && nonNull(product)) {
            return blockedStatusCodes.contains(distProductService.getProductSalesStatus(product));
        }
        return Boolean.FALSE;
    }

    @Override
    public boolean isBetterWorldProduct(String productCode) {
        return isTrue(distProductService.getProductForCode(productCode).getIsBetterWorld());
    }

    private List<ProductData> convertProducts(List<ProductModel> sources) {
        return Converters.convertAll(sources,
                                     runtimeEnvironmentService.isHeadless() ? occProductReferenceTargetConverter : productReferenceTargetConverter);
    }

    public List<ProductData> getProductsReferencesForAlternative(final List<ProductModel> sources, final List<ProductReferenceTypeEnum> referenceTypes) {
        final PIMAlternateResult pimAlternateResult = getProductService().getProductsReferencesForAlternative(sources, referenceTypes);
        return convertMultiplePimAlternatives(pimAlternateResult);
    }

    public List<ProductData> getProductsReferencesForAlternative(final List<ProductModel> sources, final List<ProductReferenceTypeEnum> referenceTypes,
                                                                 final int offset, final int size) {
        final PIMAlternateResult pimAlternateResult = getProductService().getProductsReferencesForAlternative(sources, referenceTypes, offset, size);
        return convertMultiplePimAlternatives(pimAlternateResult);
    }

    private List<ProductData> convertMultiplePimAlternatives(final PIMAlternateResult pimAlternateResult) {
        List<ProductData> alternateProducts = new ArrayList<>();
        if (pimAlternateResult != null) {
            final Map<String, String> codeCategoryMapping = pimAlternateResult.getCodeCategoryMapping();
            if (codeCategoryMapping != null) {
                alternateProducts = convertProducts(pimAlternateResult.getAlternativeProducts());
                for (final ProductData product : alternateProducts) {
                    product.setAlternateCategory(codeCategoryMapping.get(product.getCode()));
                }
            }
        }
        return alternateProducts;
    }

    @Override
    public ProductData getProductForCodeWithOptions(final String code, final Collection<ProductOption> options) {
        return getProductForCodeAndOptions(getProductService().getProductForCode(code), options);
    }

    @Override
    public List<ProductEnhancementData> getEnhancementsForProducts(List<String> productCodes, int limitSearchSizeTo) {
        if (productCodes.size() > limitSearchSizeTo) {
            productCodes.subList(limitSearchSizeTo, productCodes.size()).clear();
        }

        List<ProductEnhancementData> productEnhancements = new ArrayList<>();

        Map<String, List<String>> productsInShoppingList = distWishlistService.productsInShoppingList(productCodes);

        for (String productCode : productCodes) {
            ProductEnhancementData productEnhancement = new ProductEnhancementData();
            productEnhancement.setProductCode(productCode);
            productEnhancement.setInShoppingList(productsInShoppingList.containsKey(productCode));

            // exclusion reason
            if (enablePunchoutFilterLogic() && isProductExcluded(productCode)) {
                BaseStoreModel baseStore = baseStoreService.getCurrentBaseStore();
                SiteChannel siteChannel = baseStore.getChannel();
                DistProductExclusionReasonEnum exclusionReason;
                if (SiteChannel.B2B.equals(siteChannel)) {
                    exclusionReason = PUNCHOUT_PRODUCT_EXCLUDED;
                } else {
                    // we do not restrict products to particular b2c customers
                    exclusionReason = PRODUCT_AVAILABLE_TO_B2B_ONLY;
                }
                productEnhancement.setExclusionReason(exclusionReason.getCode());
            }

            productEnhancements.add(productEnhancement);
        }

        return productEnhancements;
    }

    @Override
    protected DistProductService getProductService() {
        return (DistProductService) super.getProductService();
    }

    // Getters & Setters

    public AvailabilityService getAvailabilityService() {
        return availabilityService;
    }

    public void setAvailabilityService(final AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    public ProductAvailabilityConverter getProductAvailabilityConverter() {
        return productAvailabilityConverter;
    }

    public void setProductAvailabilityConverter(final ProductAvailabilityConverter productAvailabilityConverter) {
        this.productAvailabilityConverter = productAvailabilityConverter;
    }

    public ProductReferenceService getProductReferenceService() {
        return productReferenceService;
    }

    public void setProductReferenceService(final ProductReferenceService productReferenceService) {
        this.productReferenceService = productReferenceService;
    }

    public CartService getCartService() {
        return cartService;
    }

    public void setCartService(final CartService cartService) {
        this.cartService = cartService;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public CheckoutCustomerStrategy getCheckoutCustomerStrategy() {
        return checkoutCustomerStrategy;
    }

    public void setCheckoutCustomerStrategy(final CheckoutCustomerStrategy checkoutCustomerStrategy) {
        this.checkoutCustomerStrategy = checkoutCustomerStrategy;
    }

    public DistrelecCMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final DistrelecCMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    @Override
    public Collection<PunchoutFilterResult> getPunchOutFiltersForCustomer() {
        return getProductService().getPunchOutFilters(null);
    }

    public DistEnergyEfficencyPopulator getEnergyEfficencyPopulator() {
        return energyEfficencyPopulator;
    }

    public void setEnergyEfficencyPopulator(final DistEnergyEfficencyPopulator energyEfficencyPopulator) {
        this.energyEfficencyPopulator = energyEfficencyPopulator;
    }

    public DistProductBasicPopulator getBasicProductPopulator() {
        return basicProductPopulator;
    }

    public void setBasicProductPopulator(final DistProductBasicPopulator basicProductPopulator) {
        this.basicProductPopulator = basicProductPopulator;
    }

    public ProductPricePopulator<ProductModel, ProductData> getProductPricePopulator() {
        return productPricePopulator;
    }

    public void setProductPricePopulator(final ProductPricePopulator<ProductModel, ProductData> productPricePopulator) {
        this.productPricePopulator = productPricePopulator;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public DistStockNotificationService getDistStockNotificationService() {
        return distStockNotificationService;
    }

    public void setDistStockNotificationService(final DistStockNotificationService distStockNotificationService) {
        this.distStockNotificationService = distStockNotificationService;
    }

    public void setProductReferenceTargetConverter(Converter<ProductModel, ProductData> productReferenceTargetConverter) {
        this.productReferenceTargetConverter = productReferenceTargetConverter;
    }

    public void setOccProductReferenceTargetConverter(Converter<ProductModel, ProductData> occProductReferenceTargetConverter) {
        this.occProductReferenceTargetConverter = occProductReferenceTargetConverter;
    }
}
