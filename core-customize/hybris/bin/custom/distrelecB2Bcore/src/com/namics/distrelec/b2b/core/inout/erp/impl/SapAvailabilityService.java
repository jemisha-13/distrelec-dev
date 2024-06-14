/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.erp.impl;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Numbers.ZERO;
import static com.namics.distrelec.b2b.core.util.SupplierleadTimeUtils.getSupplierleadTime;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

import javax.xml.ws.WebServiceException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.distrelec.webservice.sap.v1.*;
import com.google.common.collect.Lists;
import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.inout.erp.impl.cache.SapAvailabilityCacheKey;
import com.namics.distrelec.b2b.core.inout.erp.util.SoapErrorLogHelper;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.order.DistCartService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.model.PickupStockLevelExtModel;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.product.model.StockLevelData;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.ordersplitting.model.StockLevelModel;
import de.hybris.platform.ordersplitting.model.WarehouseModel;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.stock.StockService;

/**
 * SAP implementation of the <code>AvailabilityService</code>.
 * <p>
 * The interface is specified in the <a href="https://wiki.namics.com/display/distrelint/IF-06+Availability">wiki</a>.
 *
 * @author ceberle, ksperner, Namics AG
 * @since Distrelec 1.0
 */
public class SapAvailabilityService extends AbstractSapService implements AvailabilityService {

    private static final Logger LOG = LogManager.getLogger(SapAvailabilityService.class);

    private static final String CDC_WAREHOUSE_CODE = "7371";

    private static final String IGNORE_PRODITEC_CDC_SALES_ORGS = "ignore.proditec.cdc.salesorgs";

    private static final String CROSSDOCK_PRODUCT = "BTR";

    private static final String EXTERNAL_WAREHOUSE = "EXTP";

    private static final List<String> SALES_TRIGGER_STATUSES = List.of("50", "51", "53");

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private StockService stockService;

    @Autowired
    private ProductService productService;

    private AvailabilityService movexAvailabilityService;

    private SIHybrisV1Out webServiceClient;

    // The SOAP Requests object factory
    @Autowired
    private ObjectFactory sapObjectFactory;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ErpStatusUtil erpStatusUtil;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private DistCartService distCartService;

    /**
     * Create a new instance of {@code SapAvailabilityService}
     */
    public SapAvailabilityService() {
        super(DistConstants.CacheName.AVAILABILITY);
    }

    @Override
    public List<ProductAvailabilityExtModel> getAvailability(final List<String> productCodes, final Boolean detailInfo, boolean useCache) {

        final List<ErpArticleAvailability> erpArticleAvailabilities = new ArrayList<>();
        final List<String> uncachedProductCodes = new ArrayList<>();
        final DistSalesOrgModel salesOrg = getDistSalesOrgService().getCurrentSalesOrg();

        if (useCache) {
            getAvailabilitiesFromCache(productCodes, erpArticleAvailabilities, uncachedProductCodes, salesOrg);

            if (!uncachedProductCodes.isEmpty()) {
                fetchUncachedAvailabilities(erpArticleAvailabilities, uncachedProductCodes, salesOrg);
            }
        } else {
            fetchUncachedAvailabilities(erpArticleAvailabilities, productCodes, salesOrg);
        }

        return convertResponse(detailInfo, erpArticleAvailabilities);
    }

    @Override
    public List<ProductAvailabilityExtModel> getAvailability(List<String> productCodes, Boolean detailInfo) {
        return getAvailability(productCodes, detailInfo, true);
    }

    @Override
    public List<ProductAvailabilityExtModel> getAvailabilityForEntries(List<AbstractOrderEntryModel> entries, Boolean detailInfo) {
        return getAvailabilityForEntries(entries, detailInfo, true);
    }

    @Override
    public List<ProductAvailabilityExtModel> getAvailabilityForEntries(final List<AbstractOrderEntryModel> entries, final Boolean detailInfo,
                                                                       final boolean useCache) {
        final List<ErpArticleAvailability> erpArticleAvailabilities = new ArrayList<>();
        final List<String> uncachedProductCodes = new ArrayList<>();
        final DistSalesOrgModel salesOrg = getDistSalesOrgService().getCurrentSalesOrg();

        if (useCache) {
            getAvailabilitiesForEntriesFromCache(entries, erpArticleAvailabilities, uncachedProductCodes, salesOrg);

            if (!uncachedProductCodes.isEmpty()) {
                fetchUncachedAvailabilities(erpArticleAvailabilities, uncachedProductCodes, salesOrg);
            }
        } else {
            List<String> productCodes = entries.stream()
                                               .map(AbstractOrderEntryModel::getProduct)
                                               .map(ProductModel::getCode)
                                               .collect(Collectors.toList());

            fetchUncachedAvailabilities(erpArticleAvailabilities, productCodes, salesOrg);
        }

        return convertResponse(detailInfo, erpArticleAvailabilities);
    }

    @Override
    public List<String> getPurchaseBlockedProductCodes() {
        List<String> productCodes = getProductCodes();
        if (CollectionUtils.isNotEmpty(productCodes)) {
            List<ProductAvailabilityExtModel> productAvailabilities = getAvailability(productCodes, false, false);
            return productAvailabilities.stream()
                                        .filter(product -> product.getStockLevelTotal() == 0)
                                        .map(ProductAvailabilityExtModel::getProductCode)
                                        .collect(toList());
        }
        return Collections.emptyList();
    }

    private List<String> getProductCodes() {
        if (distCartService.hasSessionCart()) {
            return distCartService.getSessionCart().getEntries().stream()
                                  .map(AbstractOrderEntryModel::getProduct)
                                  .filter(product -> SALES_TRIGGER_STATUSES.contains(distProductService.getProductSalesStatus(product)))
                                  .map(ProductModel::getCode)
                                  .collect(toList());
        }
        return Collections.emptyList();
    }

    /**
     * check cache for every product. if found: add availability to list. else: add product to uncached list
     */
    protected void getAvailabilitiesFromCache(final List<String> productCodes, final List<ErpArticleAvailability> erpArticleAvailabilities,
                                              final List<String> uncachedProductCodes, final DistSalesOrgModel salesOrg) {

        if (productCodes == null) {
            return;
        }

        productCodes.forEach(productCode -> {
            final ErpArticleAvailability availability = getFromCache(new SapAvailabilityCacheKey(productCode, salesOrg), ErpArticleAvailability.class);

            if (availability == null) {
                uncachedProductCodes.add(productCode);
            } else {
                erpArticleAvailabilities.add(availability);
            }
        });
    }

    /**
     * check cache for every product. if found: add availability to list. else: add product to uncached list
     */
    protected void getAvailabilitiesForEntriesFromCache(final List<AbstractOrderEntryModel> entries,
                                                        final List<ErpArticleAvailability> erpArticleAvailabilities,
                                                        final List<String> uncachedProductCodes, final DistSalesOrgModel salesOrg) {

        if (entries == null) {
            return;
        }

        entries.stream().filter(entry -> entry != null && entry.getProduct() != null).forEach(entry -> {
            final String productCode = entry.getProduct().getCode();
            final ErpArticleAvailability availability = getFromCache(new SapAvailabilityCacheKey(productCode, salesOrg), ErpArticleAvailability.class);
            if (availability == null) {
                uncachedProductCodes.add(productCode);
            } else {
                erpArticleAvailabilities.add(availability);
            }
        });
    }

    /**
     * fetch availabilities for products from SAP PI, add them to the cache and to the availabilities list.
     */
    protected void fetchUncachedAvailabilities(final List<ErpArticleAvailability> erpArticleAvailabilities, final List<String> uncachedProductCodes,
                                               final DistSalesOrgModel salesOrg) {

        // Build soap request for uncached products
        if (uncachedProductCodes.size() > 100) {
            final List<List<String>> lists = Lists.partition(uncachedProductCodes, 100);
            for (final List<String> subList : lists) {
                fetchUncachedAvailabilities(erpArticleAvailabilities, subList, salesOrg);
            }
            return;
        }

        // build soap request for uncached products
        final AvailabilityRequest availabilityRequest = buildSOAPRequest(uncachedProductCodes, salesOrg);
        // Execute request
        final AvailabilityResponse availabilityResponse = executeSOAPRequest(availabilityRequest);

        if (availabilityResponse != null && availabilityResponse.getErpArticleAvailability() != null) {
            // Write results to cache and availability list
            availabilityResponse.getErpArticleAvailability().forEach(erpArticleAvailability -> {
                if (getCache() != null) {
                    putIntoCache(new SapAvailabilityCacheKey(erpArticleAvailability.getArticleNumber(), salesOrg), erpArticleAvailability);
                }
                erpArticleAvailabilities.add(erpArticleAvailability);
            });
        }
    }

    protected AvailabilityRequest buildSOAPRequest(final List<String> productCodes, DistSalesOrgModel salesOrg) {
        final AvailabilityRequest availabilityRequest = sapObjectFactory.createAvailabilityRequest();
        availabilityRequest.setSalesOrganization(salesOrg.getCode());
        availabilityRequest.getArticleNumbers().addAll(productCodes);

        return availabilityRequest;
    }

    protected AvailabilityResponse executeSOAPRequest(final AvailabilityRequest availabilityRequest) {
        AvailabilityResponse availabilityResponse = null;
        final long startTime = System.currentTimeMillis();
        try {
            availabilityResponse = webServiceClient.if06Availability(availabilityRequest);
        } catch (P1FaultMessage faultMessage) {
            SoapErrorLogHelper.logSoapFault(LOG, "if06Availability", faultMessage);
        } catch (WebServiceException webServiceException) {
            SoapErrorLogHelper.logWebServiceException(LOG, "if06Availability", webServiceException);
        }
        final long endTime = System.currentTimeMillis();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Call to SAP PI IF-06 Availability took " + (endTime - startTime) + "ms");
        }
        return availabilityResponse;
    }

    /**
     * Convert the SAP availability response to a list of {@code ProductAvailabilityExtModel}
     *
     * @param detailInfo
     * @param erpArticleAvailabilities
     *            the SAP response to convert.
     * @return a list of {@code ProductAvailabilityExtModel}
     */
    protected List<ProductAvailabilityExtModel> convertResponse(final Boolean detailInfo, final List<ErpArticleAvailability> erpArticleAvailabilities) {

        // Convert list of availabilities from SAP format to Hybris format
        final List<ProductAvailabilityExtModel> availabilities = new ArrayList<>();

        final String salesOrg = getCmsSiteService().getCurrentSite().getSalesOrg().getCode();
        final String[] salesOrgs = getConfigurationService().getConfiguration().getString(IGNORE_PRODITEC_CDC_SALES_ORGS, EMPTY).split(",");
        for (final ErpArticleAvailability erpArticleAvailability : erpArticleAvailabilities) {
            // Convert the SAP availability to ProductAvailabilityExtModel
            final ProductAvailabilityExtModel availability = convertErpAvailabilityToExtModel(erpArticleAvailability.getArticleNumber(), detailInfo,
                                                                                              erpArticleAvailability, ArrayUtils.contains(salesOrgs, salesOrg));
            if (null != availability) {
                availabilities.add(availability);
            }
        }
        return availabilities;
    }

    /**
     * Convert the {@code ErpArticleAvailability} to a {@code ProductAvailabilityExtModel}
     *
     * @param productCode
     *            the article number
     * @param detailInfo
     * @param erpArticleAvailability
     *            the {@code ErpArticleAvailability} to convert
     * @return an instance of {@code ProductAvailabilityExtModel}, {@code null} if the article was not found.
     */
    protected ProductAvailabilityExtModel convertErpAvailabilityToExtModel(final String productCode, final Boolean detailInfo,
                                                                           final ErpArticleAvailability erpArticleAvailability,
                                                                           final boolean ignoreProditecCDC) {

        // Ignore if not found
        if (!erpArticleAvailability.isArticleFound()) {
            return null;
        }

        // Ignore article if it is found, but not found in any warehouse
        boolean articleFoundInAtLeastOneWarehouse = false;
        for (final StockLevels stockLevels : erpArticleAvailability.getStockLevels()) {
            if (stockLevels.isArticleFoundInWarehouse()) {
                articleFoundInAtLeastOneWarehouse = true;
                break;
            }
        }
        if (!articleFoundInAtLeastOneWarehouse) {
            return null;
        }

        final ProductModel product;
        try {
            product = getProductService().getProductForCode(productCode);
        } catch (UnknownIdentifierException e) {
            LOG.info("Product with code {} not exists!", productCode);
            return null;
        }

        final DistSalesStatusModel productSalesStatusModel = getProductService().getProductSalesStatusModel(product);
        if (!productSalesStatusModel.isVisibleInShop()) {
            LOG.info("Product with code '{}' have SaleStatus '{}' and is not visible in webshop!", productCode,
                     productSalesStatusModel.getCode());
            return null;
        }

        // Build availability model
        final ProductAvailabilityExtModel availability = getAvailabilitySkeleton(erpArticleAvailability.getArticleNumber(), detailInfo);
        // Fill availability model, do not use the external warehouses as this information gets added later based on the hybris stock level
        // information. Reason: SAP can not deliver the stock for the external warehouses
        calculateStockLevels(availability, erpArticleAvailability, product, ignoreProditecCDC);

        // BackorderQuantity gets taken from the hybris stock levels
        CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
        Set<WarehouseModel> deliveryWarehouses = currentSite.getDeliveryWarehouses();
        Set<WarehouseModel> pickupWarehouses = currentSite.getPickupWarehouses();
        // hack end
        final Set<WarehouseModel> backorderWarehouses = new HashSet<>(deliveryWarehouses);

        final Collection<StockLevelModel> stockLevels = getStockService().getStockLevels(product, deliveryWarehouses);
        if (isListedInWarehouse(deliveryWarehouses, stockLevels, product, ignoreProditecCDC)) {
            backorderWarehouses.addAll(deliveryWarehouses);
        }

        sumBackorderStockLevelsInWarehouses(availability,
                                            getStockService().getStockLevels(getProductService().getProductForCode(erpArticleAvailability.getArticleNumber()),
                                                                             backorderWarehouses),
                                            backorderWarehouses, deliveryWarehouses);

        // pickupStockLevel
        if (Boolean.TRUE.equals(availability.getDetailInfo())) {
            availability.setStockLevelPickup(getPickupStockLevels(pickupWarehouses, erpArticleAvailability));
        }

        // Sort the stock level data by delivery time label.
        availability.getStockLevels().sort((o1, o2) -> {
            if (o1.getFast() && !o2.getFast()) {
                return -1;
            } else if (!o1.getFast() && o2.getFast()) {
                return 1;
            }
            return o1.getDeliveryTime().compareTo(o2.getDeliveryTime());
        });
        return availability;
    }

    protected ProductAvailabilityExtModel getAvailabilitySkeleton(final String articelNumber, final Boolean detailInfo) {
        final ProductAvailabilityExtModel availability = new ProductAvailabilityExtModel();
        availability.setProductCode(articelNumber);
        availability.setDetailInfo(detailInfo);
        availability.setStockLevelTotal(ZERO);
        availability.setBackorderQuantity(ZERO);
        availability.setLeadTimeErp(ZERO);
        availability.setStockLevels(new ArrayList<>());
        return availability;
    }

    /**
     * Returns the availability in the pickup warehouses
     *
     * @param pickupWarehouses
     *            the list of pickup warehouses
     * @param erpArticleAvailability
     * @return a list of {@code PickupStockLevelExtModel}
     */
    protected List<PickupStockLevelExtModel> getPickupStockLevels(final Set<WarehouseModel> pickupWarehouses,
                                                                  final ErpArticleAvailability erpArticleAvailability) {
        if (CollectionUtils.isNotEmpty(pickupWarehouses) && CollectionUtils.isNotEmpty(erpArticleAvailability.getStockLevels())) {
            final List<PickupStockLevelExtModel> pickupStockLevels = new ArrayList<>();
            for (final StockLevels stockLevel : erpArticleAvailability.getStockLevels()) {
                if (stockLevel.isArticleFoundInWarehouse() && validWarehouse(pickupWarehouses, stockLevel.getWarehouseId(), null, false)) {
                    final PickupStockLevelExtModel pickupStockLevel = new PickupStockLevelExtModel();
                    pickupStockLevel.setWarehouse(getWarehouse(pickupWarehouses, stockLevel.getWarehouseId()));
                    pickupStockLevel.setStockLevel(toInteger(stockLevel.getAvailable()));

                    if (equalsIgnoreCase(CROSSDOCK_PRODUCT, stockLevel.getMview()) &&
                            equalsIgnoreCase(EXTERNAL_WAREHOUSE, stockLevel.getWarehouseId())) {
                        pickupStockLevel.setWaldom(true);
                        pickupStockLevel.setReplenishmentDeliveryTime(stockLevel.getReplenishmentDeliverytime());
                        pickupStockLevel.setReplenishmentDeliveryTime2(stockLevel.getReplenishmentDeliverytime2());
                        pickupStockLevel.setMview(stockLevel.getMview()); // it should always be "BTR"
                    }

                    pickupStockLevels.add(pickupStockLevel);
                }
            }
            return pickupStockLevels;
        }

        return Collections.emptyList();
    }

    protected void calculateStockLevels(final ProductAvailabilityExtModel availability, final ErpArticleAvailability erpArticleAvailability,
                                        final ProductModel product, final boolean ignoreProditecCDC) {

        final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();

        List<StockLevels> erpStockLevels = erpArticleAvailability.getStockLevels().stream()
                                                                 .filter(stockLevel -> !(ignoreProditecCDC
                                                                         && isProditecArticleInCDECWarehouse(stockLevel, product)))
                                                                 .filter(StockLevels::isArticleFoundInWarehouse)
                                                                 .collect(Collectors.toList());

        List<StockLevels> erpStockLevelsNonWaldom = erpStockLevels.stream()
                                                                  .filter(stockLevels -> !isWaldom(stockLevels)) // If we want to merge totals for Phase 2,
                                                                                                                 // remove this filter DISTRELEC-26027
                                                                                                                 // DISTRELEC-24148
                                                                  .collect(Collectors.toList());
        List<StockLevels> erpStockLevelsWaldom = erpStockLevels.stream()
                                                               .filter(this::isWaldom)
                                                               .collect(Collectors.toList());

        int totalStock = 0;

        if (CollectionUtils.isNotEmpty(erpStockLevelsWaldom)) {
            totalStock = erpStockLevelsWaldom.get(0).getAvailable().intValue();
        } else {
            totalStock = erpStockLevelsNonWaldom.stream()
                                                .map(StockLevels::getAvailable)
                                                .mapToInt(BigInteger::intValue)
                                                .sum();
        }

        availability.setStockLevelTotal(totalStock);

        calculateMainWarehouseStock(availability, erpStockLevelsNonWaldom);
        calculateWaldomStock(availability, erpStockLevelsWaldom);
        availability.setDeliveryTimeBackorder(currentSite.getBackorderDeliveryTime());
    }

    private boolean isWaldom(StockLevels stockLevel) {
        return CROSSDOCK_PRODUCT.equalsIgnoreCase(stockLevel.getMview()) &&
                EXTERNAL_WAREHOUSE.equalsIgnoreCase(stockLevel.getWarehouseId());
    }

    private boolean isProditecArticleInCDECWarehouse(final StockLevels stockLevel, final ProductModel product) {
        return CDC_WAREHOUSE_CODE.equals(stockLevel.getWarehouseId()) && product.isProditecArticle();
    }

    private void calculateMainWarehouseStock(final ProductAvailabilityExtModel availability, final List<StockLevels> erpStockLevels) {
        for (StockLevels stock : erpStockLevels) {
            calculateStock(availability, stock.getAvailable().intValue(), false, false, stock);
        }
    }

    private void calculateWaldomStock(final ProductAvailabilityExtModel availability, final List<StockLevels> erpStockLevels) {
        StockLevels waldomStock;
        Optional<StockLevels> waldomStockOptional = erpStockLevels.stream()
                                                                  .filter(this::isWaldom)
                                                                  .findFirst();

        if (waldomStockOptional.isPresent()) {
            waldomStock = waldomStockOptional.get();
        } else {
            return;
        }

        BigInteger available = waldomStock.getAvailable() != null ? waldomStock.getAvailable() : BigInteger.ZERO;
        calculateStock(availability, available.intValue(), true, true, waldomStock);
    }

    private void calculateStock(ProductAvailabilityExtModel availability, Integer available, boolean external, boolean waldom, StockLevels stock) {
        StockLevelData stockLevelData = new StockLevelData();
        stockLevelData.setWarehouseId(stock.getWarehouseId());
        stockLevelData.setLeadTime(null);
        stockLevelData.setAvailable(available);
        stockLevelData.setDeliveryTime(EMPTY);
        stockLevelData.setExternal(external);
        stockLevelData.setFast(false);
        stockLevelData.setWaldom(waldom);
        stockLevelData.setReplenishmentDeliveryTime(stock.getReplenishmentDeliverytime() != null ? stock.getReplenishmentDeliverytime() : EMPTY);
        stockLevelData.setReplenishmentDeliveryTime2(stock.getReplenishmentDeliverytime2() != null ? stock.getReplenishmentDeliverytime2() : EMPTY);
        stockLevelData.setMview(stock.getMview() != null ? stock.getMview() : EMPTY);
        stockLevelData.setAvailablePO(isNotBlank(stock.getAvailablePO()) ? stock.getAvailablePO() : EMPTY);
        stockLevelData.setVendorNumber(isNotBlank(stock.getVendorNumber()) ? stock.getVendorNumber() : EMPTY);

        availability.getStockLevels().add(stockLevelData);
    }

    private boolean isSupplierleadTimeCalculationNeededForProduct(final ProductModel product) {
        if (product == null) {
            return false;
        }

        final String salesStatus = getProductService().getProductSalesStatus(product);
        return !getErpStatusUtil().getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_GENERIC)
                                  .contains(salesStatus);
    }

    protected void sumBackorderStockLevelsInWarehouses(final ProductAvailabilityExtModel availability, final Collection<StockLevelModel> stockLevels,
                                                       final Set<WarehouseModel> backorderWarehouses, final Set<WarehouseModel> fastWarehouses) {
        if (CollectionUtils.isEmpty(backorderWarehouses) || CollectionUtils.isEmpty(stockLevels)) {
            return;
        }

        for (final StockLevelModel stockLevel : stockLevels) {
            if (backorderWarehouses.contains(stockLevel.getWarehouse())) {
                if (availability.getBackorderDeliveryDate() == null) {
                    availability
                                .setBackorderQuantity(stockLevel.getNextDeliveryAvailable() != null ? stockLevel.getNextDeliveryAvailable()
                                                                                                    : Integer.valueOf(0));
                    availability.setBackorderDeliveryDate(stockLevel.getNextDeliveryTime());
                } else if (stockLevel.getNextDeliveryTime() != null && availability.getBackorderDeliveryDate().after(stockLevel.getNextDeliveryTime())) {
                    availability
                                .setBackorderQuantity(stockLevel.getNextDeliveryAvailable() != null ? stockLevel.getNextDeliveryAvailable()
                                                                                                    : Integer.valueOf(0));
                    availability.setBackorderDeliveryDate(stockLevel.getNextDeliveryTime());
                }
                // DISTRELEC-9514: the SLT is calculated only using fast warehouses
                if (stockLevel.getLeadTimeErp() != null && fastWarehouses.contains(stockLevel.getWarehouse())
                        && isSupplierleadTimeCalculationNeededForProduct(getProductService().getProductForCode(stockLevel.getProductCode()))) { // DISTRELEC-9400
                    final int supplierleadTimeWeekenumber = getSupplierleadTime(stockLevel.getLeadTimeErp());
                    if (availability.getLeadTimeErp() == 0 || availability.getLeadTimeErp() > supplierleadTimeWeekenumber)
                        availability.setLeadTimeErp(supplierleadTimeWeekenumber);
                }
            }
        }
    }

    protected boolean validWarehouse(final Set<WarehouseModel> warehouses, final String warehouseId, final ProductModel product,
                                     final boolean ignoreProditecCDC) {
        for (final WarehouseModel warehouse : warehouses) {
            if (warehouse.getCode().equals(warehouseId)) {
                // DISTRELEC-9168 Hide CDC stock for Proditec articles CH
                return !ignoreProditecCDC || !CDC_WAREHOUSE_CODE.equals(warehouseId) || (product != null && !product.isProditecArticle());
            }
        }
        return false;
    }

    protected WarehouseModel getWarehouse(final Set<WarehouseModel> warehouses, final String warehouseId) {
        for (final WarehouseModel warehouse : warehouses) {
            if (warehouse.getCode().equals(warehouseId)) {
                return warehouse;
            }
        }
        return null;
    }

    protected Integer toInteger(final BigInteger bigInteger) {
        return Integer.valueOf(bigInteger.min(BigInteger.valueOf(Integer.MAX_VALUE)).intValue());
    }

    protected boolean isListedInWarehouse(final Collection<WarehouseModel> warehouses, final Collection<StockLevelModel> stockLevels,
                                          final ProductModel product, final boolean ignoreProditecCDC) {
        for (final StockLevelModel stockLevel : stockLevels) {
            if (warehouses.contains(stockLevel.getWarehouse())) {
                // DISTRELEC-9168 Hide CDC stock for Proditec articles CH
                return !ignoreProditecCDC || !CDC_WAREHOUSE_CODE.equals(stockLevel.getWarehouse().getCode()) || !product.isProditecArticle();
            }
        }

        return false;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

    public StockService getStockService() {
        return stockService;
    }

    public void setStockService(StockService stockService) {
        this.stockService = stockService;
    }

    public DistProductService getProductService() {
        return (DistProductService) productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public AvailabilityService getMovexAvailabilityService() {
        if (movexAvailabilityService == null) {
            movexAvailabilityService = SpringUtil.getBean("movex.availabilityService", AvailabilityService.class);
        }

        return movexAvailabilityService;
    }

    public void setMovexAvailabilityService(final AvailabilityService movexAvailabilityService) {
        this.movexAvailabilityService = movexAvailabilityService;
    }

    public SIHybrisV1Out getWebServiceClient() {
        return webServiceClient;
    }

    @Required
    public void setWebServiceClient(final SIHybrisV1Out webServiceClient) {
        this.webServiceClient = webServiceClient;
    }

    public ObjectFactory getSapObjectFactory() {
        return sapObjectFactory;
    }

    public void setSapObjectFactory(final ObjectFactory sapObjectFactory) {
        this.sapObjectFactory = sapObjectFactory;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public ErpStatusUtil getErpStatusUtil() {
        return erpStatusUtil;
    }

    public void setErpStatusUtil(ErpStatusUtil erpStatusUtil) {
        this.erpStatusUtil = erpStatusUtil;
    }
}
