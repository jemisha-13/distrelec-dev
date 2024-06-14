/*
 * Copyright 2000-2013 Distrelec. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.bomtool.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.namics.distrelec.b2b.core.bomtool.*;
import com.namics.distrelec.b2b.core.bomtool.data.BomToolImportData;
import com.namics.distrelec.b2b.core.bomtool.data.BomToolImportEntryData;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.bomtool.BomImportEntryModel;
import com.namics.distrelec.b2b.core.model.bomtool.BomImportModel;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.facades.bomtool.BomToolSearchResultData;
import com.namics.distrelec.b2b.facades.bomtool.DistBomFileParser;
import com.namics.distrelec.b2b.facades.bomtool.DistBomImportDataParser;
import com.namics.distrelec.b2b.facades.bomtool.DistBomToolFacade;
import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException;
import com.namics.distrelec.b2b.facades.importtool.data.ImportToolMatchingData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;

import de.hybris.platform.catalog.enums.ProductReferenceTypeEnum;
import de.hybris.platform.catalog.model.CatalogUnawareMediaModel;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.product.data.ProductReferenceData;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Default implementation of {@link DistBomToolFacade}.
 *
 * @author daezamofin, Distrelec
 * @since Distrelec 1.0
 */
public class DefaultDistBomToolFacade implements DistBomToolFacade {

    private static final Logger LOG = LogManager.getLogger(DefaultDistBomToolFacade.class);

    private static final List<ProductOption> ONLINE_PRICE_PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC,
                                                                                          ProductOption.ONLINE_PRICE, ProductOption.SUMMARY,
                                                                                          ProductOption.DESCRIPTION, ProductOption.GALLERY,
                                                                                          ProductOption.CATEGORIES, ProductOption.REVIEW,
                                                                                          ProductOption.PROMOTIONS, ProductOption.CLASSIFICATION,
                                                                                          ProductOption.VARIANT_FULL, ProductOption.STOCK,
                                                                                          ProductOption.VOLUME_PRICES,
                                                                                          ProductOption.PROMOTION_LABELS, ProductOption.COUNTRY_OF_ORIGIN,
                                                                                          ProductOption.DIST_MANUFACTURER,
                                                                                          ProductOption.VIDEOS, ProductOption.IMAGE360,
                                                                                          ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION,
                                                                                          ProductOption.TECH_ATTRIBUTE, ProductOption.REFERENCES,
                                                                                          ProductOption.BREADCRUMBS);

    private static final List<ProductOption> PRICE_PRODUCT_OPTIONS = Arrays.asList(ProductOption.BASIC,
                                                                                   ProductOption.ONLINE_PRICE, ProductOption.SUMMARY, ProductOption.DESCRIPTION,
                                                                                   ProductOption.GALLERY,
                                                                                   ProductOption.CATEGORIES, ProductOption.REVIEW, ProductOption.PROMOTIONS,
                                                                                   ProductOption.CLASSIFICATION,
                                                                                   ProductOption.VARIANT_FULL, ProductOption.STOCK, ProductOption.VOLUME_PRICES,
                                                                                   ProductOption.PROMOTION_LABELS, ProductOption.COUNTRY_OF_ORIGIN,
                                                                                   ProductOption.DIST_MANUFACTURER,
                                                                                   ProductOption.VIDEOS, ProductOption.IMAGE360,
                                                                                   ProductOption.CLASSIFICATION_AND_PRODUCT_INFORMATION,
                                                                                   ProductOption.TECH_ATTRIBUTE, ProductOption.REFERENCES,
                                                                                   ProductOption.BREADCRUMBS);

    private static final String COLUMN_SEPERATOR = ",";

    private static final String LINE_SEPERATOR = "\r\n";

    private static final String MEDIA_FOLDER = "bom-tool";

    private static int DEFAULT_DATA_ARTICLE_NUMBER_POSITION = 1;

    private static int DEFAULT_DATA_QUANTITY_POSITION = 0;

    private static Integer DEFAULT_DATA_REFERENCE_POSITION = 2;

    List<ProductReferenceTypeEnum> alternativeReferenceType = Arrays.asList(
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_BETTERVALUE,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_DE,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_SIMILAR,
                                                                            ProductReferenceTypeEnum.DIS_ALTERNATIVE_UPGRADE);

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private DistCommercePriceService distCommercePriceService;

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    private DistBomFileParser distBomFileParser;

    @Autowired
    private BomToolService bomToolService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private UserService userService;

    @Autowired
    private DistBomImportDataParser importDataParser;

    @Override
    public BomToolSearchResultData searchProductsFromData(final String data) throws BomToolFacadeException {
        return searchProductsFromData(data, DEFAULT_DATA_ARTICLE_NUMBER_POSITION, DEFAULT_DATA_QUANTITY_POSITION,
                                      DEFAULT_DATA_REFERENCE_POSITION);
    }

    @Override
    public BomToolSearchResultData searchProductsFromData(final String data, final int bomToolRows,
                                                          final int quantityPosition, final int referencePosition) throws BomToolFacadeException {
        final List<String[]> importRows = importDataParser.parseFromString(data, bomToolRows, quantityPosition, referencePosition);
        return searchProducts(importRows, DEFAULT_DATA_ARTICLE_NUMBER_POSITION, DEFAULT_DATA_QUANTITY_POSITION, DEFAULT_DATA_REFERENCE_POSITION);

    }

    @Override
    public BomToolSearchResultData searchProductsFromFile(final MediaModel file, final int articleNumberPosition,
                                                          final int quantityPosition, final Integer referencePosition, final boolean ignoreFirstRow)
                                                                                                                                                     throws BomToolFacadeException {
        final List<String[]> importRows = importDataParser.parseFromFile(file, articleNumberPosition,
                                                                         quantityPosition, referencePosition, ignoreFirstRow);
        return searchProducts(importRows, articleNumberPosition, quantityPosition, referencePosition);
    }

    @Override
    public List<String[]> getLinesFromFile(final MediaModel file) throws BomToolFacadeException {
        return importDataParser.getLinesFromFile(file);
    }

    @Override
    public void deleteBomImportEntry(final String productCode, final String fileName) {
        bomToolService.removeBomImportEntry(productCode, fileName);
    }

    /**
     * Search the products from the list of rows submitted by the customer
     * either using the text area or with file upload.
     *
     * @param importRows
     * @param articleNumberPosition
     * @param quantityPosition
     * @param referencePosition
     * @return a {@code Map}
     */
    protected BomToolSearchResultData searchProducts(final List<String[]> importRows, final int articleNumberPosition, final int quantityPosition,
                                                     final Integer referencePosition) {
        final List<BomToolSearchRow> searchRows = mapImportRows(importRows, articleNumberPosition, quantityPosition, referencePosition);
        final BomToolSearchResult searchResult = bomToolService.searchBomProducts(searchRows);
        return convertSearchResult(searchResult);
    }

    private List<BomToolSearchRow> mapImportRows(final List<String[]> importRows, final int articleNumberPosition,
                                                 final int quantityPosition, final Integer referencePosition) {
        return importRows.stream().map(importRow -> {
            String productCodeOrMpn = null;
            if (articleNumberPosition < importRow.length) {
                productCodeOrMpn = importRow[articleNumberPosition];
            }
            final String quantity = importRow[quantityPosition];
            String customerReference = null;
            if (referencePosition != null && referencePosition != Integer.MIN_VALUE) {
                if (importRow.length > 2) {
                    customerReference = importRow[referencePosition];
                }
            }
            return new BomToolSearchRow(productCodeOrMpn, quantity, customerReference);
        }).collect(Collectors.toList());
    }

    private BomToolSearchResultData convertSearchResult(final BomToolSearchResult searchResult) {
        final BomToolSearchResultData searchResultData = new BomToolSearchResultData();
        searchResultData
                        .setNotMatchingProductCodes(convertNotMatchingProducts(searchResult.getNotMatchingProductCodes()));
        searchResultData
                        .setDuplicateMpnProducts(convertDuplicateMpnProductsList(searchResult.getDuplicateMpnProducts()));
        searchResultData.setUnavailableProducts(convertUnavailableProducts(searchResult.getUnavailableProducts()));
        searchResultData.setMatchingProducts(convertMatchingProducts(searchResult.getMatchingProducts()));
        searchResultData.setQuantityAdjustedProducts(
                                                     convertQuantityAdjustedProducts(searchResult.getQuantityAdjustedProducts()));
        searchResultData.setQuantityAdjustedProductsCount(searchResult.getQuantityAdjustedProducts().size());
        searchResultData.setPunchedOutProducts(convertPunchedOutProducts(searchResult.getPunchedOutProducts()));
        return searchResultData;
    }

    private List<ImportToolMatchingData> convertNotMatchingProducts(
                                                                    final List<BomToolSearchResultRow> notMatchingProductCodes) {
        return notMatchingProductCodes.stream().map(resultRow -> {
            final ImportToolMatchingData noMatchData = new ImportToolMatchingData();
            noMatchData.setSearchTerm(resultRow.getSearchTerm());
            noMatchData.setProductCode(resultRow.getSearchTerm());
            noMatchData.setQuantity(resultRow.getQuantity());
            noMatchData.setReference(resultRow.getCustomerReference());
            noMatchData.setPosition(resultRow.getPosition());
            noMatchData.setQuantityRaw(resultRow.getQuantityRaw());
            if (resultRow.getSalesStatus() != null) {
                noMatchData.setSalesStatus(resultRow.getSalesStatus().getCode());
            }
            return noMatchData;
        }).collect(Collectors.toList());
    }

    private List<ImportToolMatchingData> convertMatchingProducts(final List<BomToolSearchResultRow> matchingProducts) {
        return matchingProducts.stream()
                               .map(resultRow -> convert(resultRow.getSearchTerm(), resultRow.getProduct(), resultRow.getSalesStatus(),
                                                         resultRow.getQuantity(), resultRow.getCustomerReference(), getProductOptions(),
                                                         resultRow.getPosition()))
                               .collect(Collectors.toList());
    }

    private List<ImportToolMatchingData> convertQuantityAdjustedProducts(
                                                                         final List<BomToolSearchResultRow> matchingProducts) {
        return matchingProducts.stream()
                               .map(resultRow -> convert(resultRow.getSearchTerm(), resultRow.getProduct(), resultRow.getSalesStatus(),
                                                         resultRow.getQuantity(), resultRow.getCustomerReference(), getProductOptions(),
                                                         resultRow.getPosition()))
                               .collect(Collectors.toList());
    }

    private List<ImportToolMatchingData> convertUnavailableProducts(
                                                                    final List<BomToolSearchResultRow> unavailableProducts) {
        return unavailableProducts.stream()
                                  .map(resultRow -> convert(resultRow.getSearchTerm(), resultRow.getProduct(), resultRow.getSalesStatus(),
                                                            resultRow.getQuantity(), resultRow.getCustomerReference(), getProductOptions(),
                                                            resultRow.getPosition()))
                                  .collect(Collectors.toList());
    }

    private List<ImportToolMatchingData> convertDuplicateMpnProductsList(
                                                                         final List<BomToolSearchResultRow> duplicateMpnProducts) {
        return duplicateMpnProducts.stream().map(resultRow -> {
            final List<ProductModel> mpnAlternativeProducts = distProductService
                                                                                .findProductByMPN(resultRow.getSearchTerm());
            return convert(resultRow.getSearchTerm(), mpnAlternativeProducts, getProductOptions(), resultRow.getQuantity(),
                           resultRow.getCustomerReference(), resultRow.getPosition(), resultRow.getSearchTerm());
        }).collect(Collectors.toList());
    }

    private List<ImportToolMatchingData> convertPunchedOutProducts(
                                                                   final List<BomToolSearchResultRow> punchedOutProducts) {
        return punchedOutProducts.stream()
                                 .map(resultRow -> convert(resultRow.getSearchTerm(), resultRow.getProduct(), resultRow.getSalesStatus(),
                                                           resultRow.getQuantity(), resultRow.getCustomerReference(), getProductOptions(),
                                                           resultRow.getPosition()))
                                 .collect(Collectors.toList());
    }

    private List<ProductOption> getProductOptions() {
        return distCommercePriceService.isOnlinePricingCustomer() ? ONLINE_PRICE_PRODUCT_OPTIONS
                                                                  : PRICE_PRODUCT_OPTIONS;
    }

    @Override
    public void createBomFile(final BomToolImportData data) {
        bomToolService.createBomFile(data);
    }

    @Override
    public void editBomFile(final BomToolImportData importData) {
        bomToolService.updateBomFile(importData);
    }

    @Override
    public void saveCartTimestamp(final BomToolImportData importData) {
        bomToolService.updateCartTimestamp(importData);
    }

    @Override
    public String renameBomImportFile(final String currentName, final String newName) {
        return bomToolService.renameBomFile(currentName, newName);
    }

    @Override
    public BomToolSearchResultData loadBomFile(final String fileName) throws BomToolFacadeException {
        final BomImportModel bomImport = bomToolService.findBomImportByNameForCurrentCustomer(fileName);
        return searchProductsFromData(getBomEntryString(bomImport.getEntries()), DEFAULT_DATA_ARTICLE_NUMBER_POSITION,
                                      DEFAULT_DATA_QUANTITY_POSITION, DEFAULT_DATA_REFERENCE_POSITION);
    }

    private String getBomEntryString(final Set<BomImportEntryModel> bomEntries) {
        final StringBuilder dataString = new StringBuilder();
        for (final BomImportEntryModel bomEntry : bomEntries) {
            dataString.append(bomEntry.getQuantity())
                      .append(COLUMN_SEPERATOR)
                      .append(bomEntry.getCode())
                      .append(COLUMN_SEPERATOR)
                      .append(bomEntry.getCustomerReference())
                      .append(LINE_SEPERATOR);
        }
        return dataString.toString();
    }

    public static boolean isNumeric(final String str) {
        try {
            Long.parseLong(str);
            return true;
        } catch (final NumberFormatException e) {
            return false;
        }
    }

    protected ImportToolMatchingData convert(final String searchTerm, final ProductModel product,
                                             final DistSalesStatusModel salesStatus, final long quantity, final String reference,
                                             final List<ProductOption> options, final int position) {

        final ImportToolMatchingData importToolMatchingData = new ImportToolMatchingData();
        importToolMatchingData.setSearchTerm(searchTerm);
        importToolMatchingData.setProductCode(product.getCode());
        importToolMatchingData.setQuantity(quantity);
        final ProductData productData = productFacade.getProductForCodeAndOptions(product.getCode(),
                                                                                                options);
        productData.setSalesUnit(product.getRelevantSalesUnit());

        importToolMatchingData.setProduct(productData);
        importToolMatchingData.setReference(reference);
        importToolMatchingData.setPosition(position);

        filterAlternatives(importToolMatchingData);

        if (salesStatus != null) {
            importToolMatchingData.setSalesStatus(salesStatus.getCode());
        }

        return importToolMatchingData;
    }

    private void filterAlternatives(final ImportToolMatchingData unavailableProduct) {
        final List<ProductReferenceData> productReferences = unavailableProduct.getProduct().getProductReferences();
        if (productReferences != null) {
            final Iterator<ProductReferenceData> iter = productReferences.listIterator();
            while (iter.hasNext()) {
                if (!alternativeReferenceType.contains(iter.next().getReferenceType())) {
                    iter.remove();
                }
            }
        }
    }

    protected ImportToolMatchingData convert(final String searchTerm, final List<ProductModel> duplicateMpnProducts,
                                             final List<ProductOption> options, final long quantity, final String reference, final int position,
                                             final String mpn) {
        final ImportToolMatchingData importToolMatchingData = new ImportToolMatchingData();
        importToolMatchingData.setSearchTerm(searchTerm);
        importToolMatchingData.setDuplicateMpnProducts(duplicateMpnProducts
                                                                           .stream()
                                                                           .map(duplicateMpnProduct -> productFacade.getProductForCodeAndOptions(duplicateMpnProduct.getCode(),
                                                                                                                                                 options))
                                                                           .map(ProductData.class::cast)
                                                                           .filter(p -> p.getSalesStatus() != null)
                                                                           .collect(Collectors.toList()));
        importToolMatchingData.setQuantity(quantity);
        importToolMatchingData.setMpn(mpn);
        importToolMatchingData.setPosition(position);
        importToolMatchingData.setReference(reference);
        return importToolMatchingData;
    }

    @Override
    public String copyBomFile(final String filename) {
        final BomImportModel duplicateBomImport = bomToolService.cloneBomFile(filename);
        return duplicateBomImport.getName();
    }

    @Override
    public long getCountOfBomFilesUploadedForCurrentCustomer() {
        return bomToolService.getCountOfFilesUploadedForCurrentCustomer();
    }

    @Override
    public List<BomToolImportData> getSavedBomToolEntries() {
        return bomToolService.findBomImportsForCurrentCustomer().stream()
                             .map(bomImportModel -> mapBomImportModelToData(bomImportModel)).collect(Collectors.toList());
    }

    private BomToolImportData mapBomImportModelToData(final BomImportModel bomImportModel) {
        final BomToolImportData bomImportData = new BomToolImportData();
        bomImportData.setFileName(bomImportModel.getName());
        bomImportData.setEntry(bomImportModel.getEntries().stream()
                                             .map(bomImportEntryModel -> mapBomImportEntryModelToData(bomImportEntryModel))
                                             .collect(Collectors.toList()));
        return bomImportData;
    }

    private BomToolImportEntryData mapBomImportEntryModelToData(final BomImportEntryModel bomImportEntryModel) {
        final BomToolImportEntryData bomImportEntryData = new BomToolImportEntryData();
        bomImportEntryData.setCode(bomImportEntryModel.getCode());
        bomImportEntryData.setCustomerReference(bomImportEntryModel.getCustomerReference());
        if (bomImportEntryModel.getProduct() != null) {
            bomImportEntryData.setProductCode(bomImportEntryModel.getProduct().getCode());
        }
        bomImportEntryData.setQuantity(bomImportEntryModel.getQuantity());
        return bomImportEntryData;
    }

    @Override
    public boolean deleteBomFile(final String filename) {
        try {
            final BomImportModel bomImport = bomToolService.findBomImportByNameForCurrentCustomer(filename);
            modelService.remove(bomImport);
            return true;
        } catch (final BomToolServiceException e) {
            return false;
        }
    }

    @Override
    public MediaModel saveFileAsMedia(final String fileName, final CommonsMultipartFile uploadFile) throws IOException {
        final MediaFolderModel folder = mediaService.getFolder(MEDIA_FOLDER);
        final CatalogUnawareMediaModel media = modelService.create(CatalogUnawareMediaModel.class);
        media.setCode(fileName);
        media.setFolder(folder);
        media.setMime(uploadFile.getContentType());
        modelService.save(media);
        mediaService.setStreamForMedia(media, uploadFile.getFileItem().getInputStream());
        modelService.save(media);
        return media;
    }

    @Override
    public void removeFileIfAlreadyExists(final String fileName) {
        try {
            MediaModel file = mediaService.getMedia(fileName);
            if (file != null) {
                modelService.remove(file);
            }
        } catch (UnknownIdentifierException | AmbiguousIdentifierException e) {
            return;
        } catch (ModelRemovalException e) {
            LOG.error("An error occurred during BOM Tool Import file removal", e);
        }
    }

    public String generateBomToolFileName(final String originalFileName) {
        final String fileNamePrefix = userService.getCurrentUser().getUid() + "+";
        return fileNamePrefix + originalFileName;
    }

}
