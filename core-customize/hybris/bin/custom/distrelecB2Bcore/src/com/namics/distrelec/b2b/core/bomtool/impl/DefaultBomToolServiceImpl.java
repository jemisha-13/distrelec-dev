package com.namics.distrelec.b2b.core.bomtool.impl;

import com.namics.distrelec.b2b.core.bomtool.*;
import com.namics.distrelec.b2b.core.bomtool.data.BomToolImportData;
import com.namics.distrelec.b2b.core.bomtool.data.BomToolImportEntryData;
import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.event.BomNotificationEvent;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.bomtool.BomImportEntryModel;
import com.namics.distrelec.b2b.core.model.bomtool.BomImportModel;
import com.namics.distrelec.b2b.core.service.bomtool.dao.DistBomImportDao;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.product.model.StockLevelData;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.CronJob.DistUpdateTimeStamp.BOM_UNUSED_FILE_102_WEEKS_TIMESTAMP;
import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.CronJob.DistUpdateTimeStamp.BOM_UNUSED_FILE_104_WEEKS_TIMESTAMP;

public class DefaultBomToolServiceImpl implements BomToolService {

    @Autowired
    private DistBomImportDao distBomImportDao;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private DistSalesOrgProductService distSalesOrgProductService;

    @Autowired
    private BomToolProductAvailabilityStrategy newSalesStatusBomToolAvailabilityStrategy;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private AvailabilityService availabilityService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private BomToolNamingStrategy bomToolNamingStrategy;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Autowired
    private DistProductService productService;

    @Override
    public BomImportModel createBomFile(BomToolImportData data) {
        if (getCountOfFilesUploadedForCurrentCustomer() >= getBomFileCountLimit()) {
            throw new BomToolFileLimitExceededException("Customer " + getCurrentCustomer().getUid() + " has reached limit of number of BOM files allowed");
        }

        BomImportModel bomImport = modelService.create(BomImportModel.class);
        bomImport.setLastUsed(new Date());
        bomImport.setLastAddedToCart(new Date());
        bomImport.setName(bomToolNamingStrategy.getAvailableName(data.getFileName()));
        bomImport.setCustomer(getCurrentCustomer());
        bomImport.setEntries(convertEntries(data.getEntry()));
        modelService.save(bomImport);
        return bomImport;
    }

    private Set<BomImportEntryModel> convertEntries(List<BomToolImportEntryData> entries) {
        return entries.stream().map(dataEntry -> {
            BomImportEntryModel newEntry = modelService.create(BomImportEntryModel.class);
            newEntry.setCode(dataEntry.getCode());
            newEntry.setCustomerReference(dataEntry.getCustomerReference());
            try {
                newEntry.setProduct(distProductService.getProductForCode(dataEntry.getProductCode()));
            } catch (UnknownIdentifierException e) {
                // Do nothing
            }
            newEntry.setQuantity(dataEntry.getQuantity());
            return newEntry;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void updateBomFile(BomToolImportData data) {
        BomImportModel bomImport = findBomImportByNameForCurrentCustomer(data.getFileName());

        bomImport.setLastUsed(new Date());
        bomImport.setName(data.getFileName());
        bomImport.setCustomer(getCurrentCustomer());
        bomImport.setEntries(convertEntries(data.getEntry()));
        modelService.save(bomImport);
    }

    @Override
    public void updateCartTimestamp(BomToolImportData data) {
        BomImportModel bomImport = findBomImportByNameForCurrentCustomer(data.getFileName());
        bomImport.setLastAddedToCart(new Date());
        bomImport.setName(data.getFileName());
        bomImport.setCustomer(getCurrentCustomer());
        bomImport.setEntries(convertEntries(data.getEntry()));
        modelService.save(bomImport);
    }


    @Override
    public String renameBomFile(String currentName, String newName) {
        String availableName = bomToolNamingStrategy.getAvailableName(newName);
        BomImportModel bomImport = findBomImportByNameForCurrentCustomer(currentName);
        bomImport.setName(availableName);
        modelService.save(bomImport);
        return availableName;
    }

    @Override
    public BomImportModel cloneBomFile(String originalName) {
        if (getCountOfFilesUploadedForCurrentCustomer() >= getBomFileCountLimit()) {
            throw new BomToolFileLimitExceededException("Customer " + getCurrentCustomer().getUid() + " has reached limit of number of BOM files allowed");
        }

        BomImportModel bomImport = findBomImportByNameForCurrentCustomer(originalName);
        BomImportModel duplicateBomImport = modelService.clone(bomImport);
        duplicateBomImport.setCreationtime(new Date());
        duplicateBomImport.setName(bomToolNamingStrategy.getAvailableName(originalName + "_copy"));
        modelService.save(duplicateBomImport);
        return duplicateBomImport;
    }

    private int getBomFileCountLimit() {
        return configurationService.getConfiguration().getInt("bom.file.allowedlimit", 100);
    }

    @Override
    public void removeBomImportEntry(String productCode, String fileName) {
        Optional<BomImportModel> bomImport = distBomImportDao.findBomImportByCustomerAndName(getCurrentCustomer(), fileName);

        if (bomImport.isEmpty()) {
            throw new BomToolServiceException("Bom file " + fileName + " not found for customer " + getCurrentCustomer().getUid());
        }

        Optional<BomImportEntryModel> bomImportEntry = bomImport.get().getEntries().stream()
                .filter(entry -> entry.getProduct().getCode().equals(productCode))
                .findFirst();

        if (bomImportEntry.isEmpty()) {
            throw new BomToolServiceException("Product code " + productCode + " not found in BOM file " + fileName + " for customer " + getCurrentCustomer().getUid());
        }

        Set<BomImportEntryModel> entries = new LinkedHashSet<>(bomImport.get().getEntries());
        entries.remove(bomImportEntry.get());
        bomImport.get().setEntries(entries);
        modelService.save(bomImport.get());
        modelService.refresh(bomImport.get());
    }

    @Override
    public BomImportModel findBomImportByNameForCurrentCustomer(String name) {
        Optional<BomImportModel> bomImport = distBomImportDao.findBomImportByCustomerAndName(getCurrentCustomer(), name);
        if (bomImport.isEmpty()) {
            throw new BomToolServiceException("Bom file " + name + " not found for customer " + getCurrentCustomer().getUid());
        }
        return bomImport.get();
    }

    @Override
    public long getCountOfFilesUploadedForCurrentCustomer() {
        return distBomImportDao.getCountOfBomFilesByCustomer(getCurrentCustomer());
    }

    @Override
    public List<BomImportModel> findBomImportsForCurrentCustomer() {
        return distBomImportDao.findBomImportsByCustomer(getCurrentCustomer());
    }

    @Override
    public BomToolSearchResult searchBomProducts(List<BomToolSearchRow> searchRows) {
        BomToolSearchResult result = new BomToolSearchResult();
        List<BomToolSearchResultRow> matchingProductsRows = new ArrayList<>();

        int position = 0;
        for (BomToolSearchRow searchRow : searchRows) {
            position++;
            processSearchRow(searchRow, result, matchingProductsRows, position);
        }

        adjustProductQuantities(matchingProductsRows, result);
        mergeDuplicateProducts(result);

        return result;
    }

    private void processSearchRow(BomToolSearchRow searchRow, BomToolSearchResult result,
                                  List<BomToolSearchResultRow> matchingProductsRows, int position) {
        long quantity;
        try {
            quantity = Long.parseLong(searchRow.getQuantity());
        } catch (NumberFormatException e) {
            result.getNotMatchingProductCodes().add(new BomToolSearchResultRow(searchRow.getProductCodeOrMpn(),
                                                                               null,
                                                                               null,
                                                                               0,
                                                                               searchRow.getCustomerReference(),
                                                                               position,
                                                                               searchRow.getQuantity()));
            return;
        }

        if (handleDuplicateMPNProduct(searchRow, result, quantity, position)) {
            return;
        }

        ProductModel product = findProductByCodeOrMpn(searchRow);
        if (product == null || !handleSalesOrgProduct(product, searchRow, result, quantity, position)) {
            return;
        }

        processProductAvailability(product, searchRow, result, matchingProductsRows, quantity, position);
    }

    private boolean handleDuplicateMPNProduct(BomToolSearchRow searchRow, BomToolSearchResult result,
                                              long quantity, int position) {
        if (distProductService.isDuplicateMPNProduct(searchRow.getProductCodeOrMpn())) {
            result.getDuplicateMpnProducts().add(new BomToolSearchResultRow(searchRow.getProductCodeOrMpn(),
                                                                            null,
                                                                            null,
                                                                            quantity,
                                                                            searchRow.getCustomerReference(),
                                                                            position,
                                                                            String.valueOf(quantity)));
            return true;
        }
        return false;
    }

    private boolean handleSalesOrgProduct(ProductModel product, BomToolSearchRow searchRow,
                                          BomToolSearchResult result, long quantity, int position) {
        DistSalesOrgProductModel salesOrgProduct = distSalesOrgProductService.getSalesOrgProduct(product, distSalesOrgService.getCurrentSalesOrg());

        if (salesOrgProduct == null) {
            result.getNotMatchingProductCodes()
                  .add(new BomToolSearchResultRow(searchRow.getProductCodeOrMpn(), null, null, quantity, searchRow.getCustomerReference(), position,
                                                  String.valueOf(quantity)));
            return false;
        }
        return true;
    }

    private void processProductAvailability(ProductModel product, BomToolSearchRow searchRow,
                                            BomToolSearchResult result, List<BomToolSearchResultRow> matchingProductsRows,
                                            long quantity, int position) {
        if (!newSalesStatusBomToolAvailabilityStrategy.isAvailableForSale(product)) {
            DistSalesStatusModel salesStatus = distSalesOrgProductService.getSalesOrgProduct(product, distSalesOrgService.getCurrentSalesOrg())
                                                                         .getSalesStatus();
            if (hasAlternativeProduct(product)) {
                result.getUnavailableProducts().add(new BomToolSearchResultRow(searchRow.getProductCodeOrMpn(),
                                                                               product,
                                                                               salesStatus,
                                                                               quantity,
                                                                               searchRow.getCustomerReference(),
                                                                               position,
                                                                               String.valueOf(quantity)));
            } else {
                result.getNotMatchingProductCodes()
                      .add(new BomToolSearchResultRow(searchRow.getProductCodeOrMpn(),
                                                      null,
                                                      salesStatus,
                                                      quantity,
                                                      searchRow.getCustomerReference(),
                                                      position,
                                                      String.valueOf(quantity)));
            }
            return;
        }

        if (productService.isProductPunchedOut(product)) {
            result.getPunchedOutProducts().add(new BomToolSearchResultRow(searchRow.getProductCodeOrMpn(),
                                                                          product,
                                                                          distSalesOrgProductService.getSalesOrgProduct(
                                                                            product,
                                                                            distSalesOrgService.getCurrentSalesOrg()).getSalesStatus(),
                                                                          quantity,
                                                                          searchRow.getCustomerReference(),
                                                                          position,
                                                                          String.valueOf(quantity)));
            return;
        }

        matchingProductsRows.add(new BomToolSearchResultRow(searchRow.getProductCodeOrMpn(),
                                                            product,
                                                            distSalesOrgProductService.getSalesOrgProduct(product, distSalesOrgService.getCurrentSalesOrg())
                                                                                      .getSalesStatus(),
                                                            quantity,
                                                            searchRow.getCustomerReference(),
                                                            position,
                                                            String.valueOf(quantity)));
    }

    private void mergeDuplicateProducts(BomToolSearchResult result) {
        result.setMatchingProducts(mergeDuplicateProducts(result.getMatchingProducts()));
        result.setUnavailableProducts(mergeDuplicateProducts(result.getUnavailableProducts()));
    }

    private List<BomToolSearchResultRow> mergeDuplicateProducts(List<BomToolSearchResultRow> originalRows) {
        List<BomToolSearchResultRow> mergedRows = new ArrayList<>();
        for (BomToolSearchResultRow originalRow : originalRows) {
            Optional<BomToolSearchResultRow> existingMergedRowOption
                    = mergedRows.stream()
                                .filter(mergedRow -> Objects.nonNull(mergedRow.getProduct())
                                        && Objects.nonNull(mergedRow.getCustomerReference()))
                                .filter(mergedRow -> mergedRow.getProduct().equals(originalRow.getProduct())
                                        && mergedRow.getCustomerReference().equals(originalRow.getCustomerReference()))
                                .findFirst();

            if (existingMergedRowOption.isPresent()) {
                existingMergedRowOption.get().setQuantity(existingMergedRowOption.get().getQuantity() + originalRow.getQuantity());
            } else {
                mergedRows.add(originalRow);
            }
        }
        return mergedRows;
    }

    private void adjustProductQuantities(List<BomToolSearchResultRow> matchingProductsRows, BomToolSearchResult result) {
        List<ProductAvailabilityExtModel> matchingProductAvailabilities = getProductAvailabilities(matchingProductsRows);

        for (BomToolSearchResultRow matchingProductRow : matchingProductsRows) {
            Optional<ProductAvailabilityExtModel> productAvailabilityExtOption = matchingProductAvailabilities.stream()
                    .filter(productAvailabilityExt -> productAvailabilityExt.getProductCode().equals(matchingProductRow.getProduct().getCode()))
                    .findFirst();

            if (productAvailabilityExtOption.isEmpty()) {
                result.getUnavailableProducts().add(matchingProductRow);
                continue;
            }

            ProductAvailabilityExtModel availability = productAvailabilityExtOption.get();

            long minQuantity = matchingProductRow.getProduct().getMinOrderQuantity() != null ? matchingProductRow.getProduct().getMinOrderQuantity() : 1L;
            if (availability.getStockLevelTotal() < minQuantity && !newSalesStatusBomToolAvailabilityStrategy.isAvailableForSaleAfterStockIsDepleted(matchingProductRow.getProduct())) {
                if (!hasAlternativeProduct(matchingProductRow.getProduct())) {
                    result.getNotMatchingProductCodes().add(new BomToolSearchResultRow(matchingProductRow.getSearchTerm(),
                            matchingProductRow.getProduct(),
                            matchingProductRow.getSalesStatus(),
                            matchingProductRow.getQuantity(),
                            matchingProductRow.getCustomerReference(),
                            matchingProductRow.getPosition(),
                            String.valueOf(matchingProductRow.getQuantity())));
                }
                result.getUnavailableProducts().add(matchingProductRow);
                continue;
            }

            boolean waldomDepleted = waldomStockDepleted(availability, matchingProductRow);

            if ((availability.getStockLevelTotal() < matchingProductRow.getQuantity() && !newSalesStatusBomToolAvailabilityStrategy.isAvailableForSaleAfterStockIsDepleted(matchingProductRow.getProduct())) || waldomDepleted) {
                BomToolSearchResultRow quantityAdjustedRow = new BomToolSearchResultRow(matchingProductRow.getSearchTerm(),
                        matchingProductRow.getProduct(),
                        matchingProductRow.getSalesStatus(),
                        availability.getStockLevelTotal(),
                        matchingProductRow.getCustomerReference(),
                        matchingProductRow.getPosition(),
                        String.valueOf(availability.getStockLevelTotal()));
                result.getQuantityAdjustedProducts().add(quantityAdjustedRow);
                result.getMatchingProducts().add(quantityAdjustedRow);
                continue;
            }

            result.getMatchingProducts().add(matchingProductRow);
        }
    }

    private boolean hasAlternativeProduct(ProductModel product) {
        List<ProductReferenceTypeEnum> referenceTypes = Arrays.asList(ProductReferenceTypeEnum.DIS_ALTERNATIVE_BETTERVALUE,
                                                                      ProductReferenceTypeEnum.DIS_ALTERNATIVE_DE,
                                                                      ProductReferenceTypeEnum.DIS_ALTERNATIVE_SIMILAR,
                                                                      ProductReferenceTypeEnum.DIS_ALTERNATIVE_UPGRADE);
        return product.getProductReferences().stream().anyMatch(reference -> referenceTypes.contains(reference.getReferenceType()));
    }

    private List<ProductAvailabilityExtModel> getProductAvailabilities(List<BomToolSearchResultRow> matchingProducts) {
        List<String> matchingProductCodes = matchingProducts.stream()
                .map(BomToolSearchResultRow::getProduct)
                .map(ProductModel::getCode)
                .collect(Collectors.toList());

        return availabilityService.getAvailability(matchingProductCodes, false, true);
    }

    private ProductModel findProductByCodeOrMpn(BomToolSearchRow searchRow) {
        try {
            String productCode = searchRow.getProductCodeOrMpn().replace(DistConstants.Punctuation.DASH, StringUtils.EMPTY);
            String mpn = searchRow.getProductCodeOrMpn();
            return distProductService.findProductByCodeOrMPN(productCode, mpn);
        } catch (UnknownIdentifierException e) {
            return null;
        }
    }

    private boolean waldomStockDepleted(final ProductAvailabilityExtModel availability, BomToolSearchResultRow matchingProductRow) {
        boolean waldom = availability.getStockLevels().stream()
                .anyMatch(StockLevelData::isWaldom);

        return waldom && availability.getStockLevelTotal() > 0 && availability.getStockLevelTotal() < matchingProductRow.getQuantity();
    }

    @Override
    public void notifyUsersAboutBomFiles() {
        Configuration configuration = configurationService.getConfiguration();
        final int usedTimestamp = configuration.getInt(BOM_UNUSED_FILE_102_WEEKS_TIMESTAMP);
        final int deleteTimestamp = configuration.getInt(BOM_UNUSED_FILE_104_WEEKS_TIMESTAMP);
        List<BomImportModel> bomImportFilesList = distBomImportDao.getUnusedBomFiles(usedTimestamp, deleteTimestamp);
        Map<CustomerModel, List<String>> fileMap = new HashMap<>();

        for (BomImportModel entry : bomImportFilesList) {

            if (!fileMap.containsKey(entry.getCustomer())) {
                fileMap.put(entry.getCustomer(), new ArrayList<>());
            }
            List<String> values = fileMap.get(entry.getCustomer());
            values.add(entry.getName());
            fileMap.put(entry.getCustomer(), values);
        }

        for (Entry<CustomerModel, List<String>> entry : fileMap.entrySet()) {
            eventService.publishEvent(new BomNotificationEvent(entry.getKey(), entry.getValue()));
        }

    }

    @Override
    public void deleteUnusedBomFiles() {
        Configuration configuration = configurationService.getConfiguration();
        final int deleteTimestamp = configuration.getInt(BOM_UNUSED_FILE_104_WEEKS_TIMESTAMP);
        List<BomImportModel> bomImportFilesList = distBomImportDao.deleteUnusedBomFiles(deleteTimestamp);
        modelService.removeAll(bomImportFilesList);
    }

    private CustomerModel getCurrentCustomer() {
        return (CustomerModel) userService.getCurrentUser();
    }
}
