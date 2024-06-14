/*
 * Copyright 2000-2013 Distrelec. All rights reserved.
 */

package com.namics.distrelec.b2b.facades.importtool.impl;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.singletonMap;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import de.hybris.platform.commercefacades.product.data.PriceData;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.namics.distrelec.b2b.core.constants.DistConfigConstants;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.DistCommercePriceService;
import com.namics.distrelec.b2b.core.service.product.DistSalesOrgProductService;
import com.namics.distrelec.b2b.core.util.ErpStatusUtil;
import com.namics.distrelec.b2b.facades.importtool.DistImportToolFacade;
import com.namics.distrelec.b2b.facades.importtool.data.ImportToolMatchingData;
import com.namics.distrelec.b2b.facades.importtool.exception.ImportToolException;
import com.namics.distrelec.b2b.facades.importtool.exception.ImportToolException.ErrorSource;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.product.converters.populator.ProductVolumePricesPopulator;
import com.namics.distrelec.b2b.facades.product.data.ProductAvailabilityData;

import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.europe1.jalo.PriceRow;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Default implementation of {@link DistImportToolFacade}.
 *
 * @author daezamofin, Distrelec
 * @since Distrelec 1.0
 */
public class DefaultDistImportToolFacade implements DistImportToolFacade {

    private static final Logger LOG = LogManager.getLogger(DefaultDistImportToolFacade.class);

    private static final List<ProductOption> ONLINE_PRICE_PRODUCT_OPTIONS = Arrays.asList(ProductOption.MIN_BASIC, ProductOption.DIST_MANUFACTURER);

    private static final List<ProductOption> PRICE_PRODUCT_OPTIONS = Arrays.asList(ProductOption.MIN_BASIC, ProductOption.VOLUME_PRICES,
                                                                                   ProductOption.DIST_MANUFACTURER);

    // https://wiki.namics.com/display/distrelint/C300-BOMDataImport : Copy&Paste of comma-separated-values / tab-separated-values
    private static String[] csvSeparators = { ";", "\t", "," };

    // https://wiki.namics.com/display/distrelint/C300-BOMDataImport :
    // Limit number of rows : max 100 rows (server side validation)
    private static int rowsLimit = 300;

    // The first 4 columns of the first sheet of the file have to be the following:
    // Distrelec Article Number | Manufacturer | Manufacturer Article Number | Quantity |
    private static int defaultExcelSheet;

    private static int defaultDataArticleNumberPosition = 1;

    private static int defaultDataQuantityPosition;

    private static int defaultDataReferencePosition = 2;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private DistCommercePriceService distCommercePriceService;

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    private PriceDataFactory priceDataFactory;

    @Autowired
    private DistSalesOrgProductService salesOrgProductService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private ErpStatusUtil erpStatusUtil;

    @Override
    public Map<String, Object> searchProductsFromData(final String data) throws ImportToolException {
        return searchProductsFromData(data, defaultDataArticleNumberPosition, defaultDataQuantityPosition, defaultDataReferencePosition);
    }

    @Override
    public Map<String, Object> searchProductsFromData(final String data, final int articleNumberPosition, final int quantityPosition,
                                                      final int referencePosition) throws ImportToolException {
        final List<String[]> importRows = new ArrayList<>();
        final String[] lines = data.split("\r\n");

        // https://wiki.namics.com/display/distrelint/C300-BOMDataImport :
        // Limit number of rows : max 100 rows (server side validation)
        if (lines.length > rowsLimit) {
            throw new ImportToolException("Too many lines! " + rowsLimit + " are allowed, but got" + lines.length, ErrorSource.TOO_MANY_LINES);
        }

        String speratorPattern = null;
        for (int lineCounter = 0; lineCounter < lines.length; lineCounter++) {
            final String line = lines[lineCounter];
            if (line.length() == 0) {
                continue;
            }

            // https://wiki.namics.com/display/distrelint/C300-BOMDataImport :
            // Quantity, Article Number, Item Reference
            // Example text with <code>csvSeparators</code>:
            // 100, 102010, Item Reference
            // or
            // https://jira.namics.com/browse/DISTRELEC-4949
            // 50 102011 Item Reference
            if (line.contains(",")) {
                speratorPattern = "\\s*,\\s*";
            } else if (line.contains(" ") || line.contains("\t") || line.contains("\b")) {
                speratorPattern = "\\s+";
            } else {
                throw new ImportToolException("Separator not found!", lineCounter, ErrorSource.COPY_PAST_SEPARATOR_NOT_FOUND);
            }

            final String[] dataArray = line.split(speratorPattern);
            if (dataArray.length < 2) {
                throw new ImportToolException("Separator not found!", lineCounter, ErrorSource.COPY_PAST_SEPARATOR_NOT_FOUND);
            } else if (dataArray.length > 3) {
                final int length = dataArray.length;
                final StringBuffer buffer = new StringBuffer(dataArray[defaultDataReferencePosition]);
                for (int i = 2; i < length; i++) {
                    buffer.append(dataArray[i]);
                }

                dataArray[defaultDataReferencePosition] = buffer.toString();
            }

            if (dataArray.length > 2 && dataArray[defaultDataReferencePosition] != null && dataArray[defaultDataReferencePosition].length() > 35) {
                throw new ImportToolException("Customer reference is longer than 35 chars!", lineCounter, ErrorSource.CUSTOMER_REFERENCE_FIELD);
            }

            importRows.add(dataArray);
        }

        if (importRows.isEmpty()) {
            throw new ImportToolException("No data found!", ErrorSource.NO_DATA);
        }
        return searchProducts(importRows, defaultDataArticleNumberPosition, defaultDataQuantityPosition, defaultDataReferencePosition);

    }

    @Override
    public Map<String, Object> searchProductsFromFile(final String orderFileName) throws ImportToolException {
        return searchProductsFromFile(orderFileName, defaultDataArticleNumberPosition, defaultDataQuantityPosition, defaultDataReferencePosition, false);
    }

    @Override
    public Map<String, Object> searchProductsFromFile(final String orderFileName, final int articleNumberPosition, final int quantityPosition,
                                                      final int referencePosition, final boolean ignoreFirstRow) throws ImportToolException {

        // Is it an .csv or an xls?
        String extension = null;

        final int position = orderFileName.lastIndexOf('.');
        if (position > 0) {
            extension = orderFileName.substring(position + 1);
        }
        // https://wiki.namics.com/display/distrelint/C300-BOMDataImport : only xls and csv allowed
        final List<String[]> importRows;
        if ("csv".equals(extension) || "txt".equals(extension)) {
            importRows = createArrayFromCsv(orderFileName, referencePosition);
        } else if ("xls".equals(extension)) {
            importRows = createArrayFromXLS(orderFileName, defaultExcelSheet, referencePosition);
        } else if ("xlsx".equals(extension)) {
            importRows = createArrayFromXLSX(orderFileName, defaultExcelSheet, referencePosition);
        } else {
            importRows = EMPTY_LIST;
        }

        if (importRows != null && !importRows.isEmpty()) {
            if (ignoreFirstRow) {
                importRows.remove(0);
            }
            if (!importRows.isEmpty()) {
                return searchProducts(importRows, articleNumberPosition, quantityPosition, referencePosition);
            }
        }
        throw new ImportToolException("No data found!", ErrorSource.NO_DATA);
    }

    @Override
    public List<String[]> getLinesFromFile(final String orderFileName) throws ImportToolException {
        List<String[]> importRows = new ArrayList<>();
        // Is it an .csv or an xls?
        String extension = null;

        final int position = orderFileName.lastIndexOf('.');
        if (position > 0) {
            extension = orderFileName.substring(position + 1);
        }
        // https://wiki.namics.com/display/distrelint/C300-BOMDataImport : only xls and csv allowed
        if ("csv".equals(extension) || "txt".equals(extension)) {
            importRows = createArrayFromCsv(orderFileName, defaultDataReferencePosition);
        } else if ("xls".equals(extension)) {
            importRows = createArrayFromXLS(orderFileName, defaultExcelSheet, defaultDataReferencePosition);
        } else if ("xlsx".equals(extension)) {
            importRows = createArrayFromXLSX(orderFileName, defaultExcelSheet, defaultDataReferencePosition);
        }

        return importRows;
    }

    protected List createArrayFromCsv(final String orderFileName, final int referencePosition) throws ImportToolException {
        LineNumberReader lineNumberReader = null;

        try {
            final List<String[]> importRows = new ArrayList<>();

            final FileReader fileReader = new FileReader(new File(orderFileName));
            final int rowCount = count(new File(orderFileName));
            if (rowCount > rowsLimit) {
                // https://wiki.namics.com/display/distrelint/C300-BOMDataImport :
                // Limit number of rows : max 100 rows (server side validation)
                throw new ImportToolException("Too many lines! " + rowsLimit + " are allowed, but got" + rowCount, ErrorSource.TOO_MANY_LINES);
            }

            // Read the header and determine the separator
            lineNumberReader = new LineNumberReader(fileReader);
            String csvSeparator = null;
            String line = lineNumberReader.readLine();
            if (line != null) {
                for (int i = 0; i < csvSeparators.length; i++) {
                    if (line.split(csvSeparators[i]).length > 1) {
                        csvSeparator = csvSeparators[i];
                        break;
                    }
                }
            }
            if (csvSeparator == null) {
                throw new ImportToolException("Separator not found!", ErrorSource.FIELD_SEPARATOR_NOT_FOUND);
            } else {
                // DISTRELEC-10762 we need to add also the first line to the list.
                importRows.add(line.split(csvSeparator));
            }

            // https://wiki.namics.com/display/distrelint/C300-BOMDataImport
            while ((line = lineNumberReader.readLine()) != null) {
                final String[] data = line.split(csvSeparator);

                if (data.length > 2 && data[referencePosition] != null && data[referencePosition].length() > 35) {
                    throw new ImportToolException("Customer reference is longer than 35 chars!", lineNumberReader.getLineNumber(),
                                                  ErrorSource.CUSTOMER_REFERENCE_FIELD);
                }

                importRows.add(data);
            }

            if (importRows.isEmpty()) {
                throw new ImportToolException("No data found!", ErrorSource.NO_DATA);
            }

            return importRows;
        } catch (final IOException e) {
            throw new ImportToolException(e, ErrorSource.IO_EXCEPTION);
        } finally {
            IOUtils.closeQuietly(lineNumberReader);
        }
    }

    protected List<String[]> createArrayFromXLSX(final String orderFileName, final int sheetNumber, final int referencePosition) throws ImportToolException {
        try {
            return createArrayFromWorkbook(new XSSFWorkbook(new FileInputStream(orderFileName)), sheetNumber, referencePosition);
        } catch (final IOException e) {
            throw new ImportToolException(e, ErrorSource.IO_EXCEPTION);
        }
    }

    protected List createArrayFromXLS(final String orderFileName, final int sheetNumber, final int referencePosition) throws ImportToolException {
        try {
            return createArrayFromWorkbook(new HSSFWorkbook(new FileInputStream(orderFileName)), sheetNumber, referencePosition);
        } catch (final IOException e) {
            throw new ImportToolException(e, ErrorSource.IO_EXCEPTION);
        }
    }

    protected List createArrayFromWorkbook(final Workbook workbook, final int sheetNumber, final int referencePosition) throws ImportToolException {
        final List<String[]> importRows = new ArrayList<>();

        // Get the workbook instance for XLS file

        // Get first sheet from the workbook
        final Sheet sheet = workbook.getSheetAt(sheetNumber);

        // Read the header
        if (!sheet.iterator().hasNext()) {
            throw new ImportToolException("File is empty!", ErrorSource.FILE_EMPTY);
        } else if (sheet.getLastRowNum() >= rowsLimit) {
            // https://wiki.namics.com/display/distrelint/C300-BOMDataImport :
            // Limit number of rows : max 100 rows (server side validation)
            throw new ImportToolException("Too many lines! " + rowsLimit + " are allowed, but got" + sheet.getLastRowNum(), ErrorSource.TOO_MANY_LINES);
        }

        final Iterator<Row> rowIterator = sheet.iterator();
        int rowCounter = 0;
        while (rowIterator.hasNext()) {
            final Row row = rowIterator.next();
            rowCounter++;
            final String[] data = new String[row.getLastCellNum()];
            final Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                final Cell cell = cellIterator.next();
                if (cell != null) {
                    if (cell.getCellType() == CellType.NUMERIC) {
                        data[cell.getColumnIndex()] = String.valueOf((int) cell.getNumericCellValue());
                    } else {
                        data[cell.getColumnIndex()] = cell.getStringCellValue();
                    }
                }
            }

            if (data.length > 2 && data[referencePosition] != null && data[referencePosition].length() > 35) {
                throw new ImportToolException("Customer reference is longer than 35 chars!", rowCounter, ErrorSource.CUSTOMER_REFERENCE_FIELD);
            }

            importRows.add(data);
        }

        return importRows;
    }

    /**
     *
     * @param importRows
     * @return
     * @see #searchProducts(List, int, int, int)
     */
    protected Map<String, Object> searchProducts(final List<String[]> importRows) {
        return searchProducts(importRows, defaultDataArticleNumberPosition, defaultDataQuantityPosition, defaultDataReferencePosition);
    }

    /**
     * Search the products from the list of rows submitted by the customer either using the text area or with file upload.
     *
     * @param importRows
     * @param articleNumberPosition
     * @param quantityPosition
     * @param referencePosition
     * @return a {@code Map}
     */
    protected Map<String, Object> searchProducts(final List<String[]> importRows, final int articleNumberPosition, final int quantityPosition,
                                                 final int referencePosition) {

        final Map<String, Object> result = new HashMap<>();
        final Map<MultiKey, ImportToolMatchingData> matchingProducts = new LinkedHashMap<>();
        final Map<MultiKey, ImportToolMatchingData> unavailableProducts = new LinkedHashMap<>();
        final List<String[]> notMatchingProducts = new ArrayList<>();

        final List<String> productCodes = importRows.stream().map(columns -> columns[articleNumberPosition].replace("-", ""))
                                                    .filter(StringUtils::isNumeric) //
                                                    .filter(productCode -> productCode.length() <= 24) //
                                                    .distinct()
                                                    .collect(Collectors.toList()); //
        final Map<String, ProductAvailabilityData> availabilityDataMap = productFacade.getAvailability(productCodes).stream()
                                                                                      .collect(Collectors.toMap(ProductAvailabilityData::getProductCode,
                                                                                                                e -> e));

        // Check if the customer is Online Price Customer
        final boolean onlinePrice = getDistCommercePriceService().isOnlinePricingCustomer();
        final Map<String, PriceInformation> onlinePriceInfos = onlinePrice
                                                                           ? getDistCommercePriceService()
                                                                                                          .getWebPriceForProducts(importRows.stream()
                                                                                                                                            .map(pInfo -> pInfo[articleNumberPosition])
                                                                                                                                            .map(this::productForCode)
                                                                                                                                            .filter(this::isBuyable) //
                                                                                                                                            .distinct()
                                                                                                                                            .collect(Collectors.toList()),
                                                                                                                                  onlinePrice, onlinePrice)
                                                                           : MapUtils.EMPTY_MAP;

        final List<ProductOption> options = onlinePrice ? ONLINE_PRICE_PRODUCT_OPTIONS : PRICE_PRODUCT_OPTIONS;

        final int[] pos = { -1 };
        // Search the product one by one
        importRows.forEach(pInfos -> {
            pos[0]++;
            // String[] product = {Distrelec Article Number | Manufacturer |
            // Manufacturer Article Number | Quantity | Item Reference}
            final String productCode = pInfos[articleNumberPosition].replace("-", "");
            long qty = StringUtils.isNumeric(pInfos[quantityPosition]) ? Long.valueOf(pInfos[quantityPosition]) : 1L;
            String reference = (pInfos.length > 2) ? pInfos[referencePosition] : null;

            try {
                final ProductModel product = getProductService().getProductForCode(productCode);
                final MultiKey pKey = new MultiKey(product.getCode(), reference);
                // Merge of the products if we have a product in the map
                ImportToolMatchingData productInMap = matchingProducts.get(pKey);
                if (productInMap == null) {
                    productInMap = unavailableProducts.get(pKey);
                }
                if (productInMap != null) {
                    qty += productInMap.getQuantity();
                    reference = productInMap.getReference();
                }

                final DistSalesOrgProductModel salesOrgProduct = salesOrgProductService.getCurrentSalesOrgProduct(product);
                final DistSalesStatusModel salesStatus = salesOrgProduct != null ? salesOrgProduct.getSalesStatus() : null;
                final ImportToolMatchingData matchingData = convert(product, salesStatus, qty, reference, options, onlinePriceInfos, pos[0]);
                if (salesOrgProduct != null && isAvailableForSale(salesStatus)) {
                    matchingProducts.put(pKey, matchingData);
                } else {
                    unavailableProducts.put(pKey, matchingData);
                }
            } catch (final UnknownIdentifierException e) {
                notMatchingProducts.add(pInfos);
            }
        });

        final AtomicInteger quantityAdjustedCounter = new AtomicInteger(0);

        final Map<MultiKey, ImportToolMatchingData> filteredMatchingProducts = adjustMatchedProductsQuantity(matchingProducts, unavailableProducts,
                                                                                                             availabilityDataMap, quantityAdjustedCounter);

        result.put("quantityAdjustedCount", quantityAdjustedCounter.intValue());
        result.put("matchingProducts", filteredMatchingProducts.values().stream().sorted().collect(Collectors.toList()));
        result.put("unavailableProducts", unavailableProducts.values().stream().sorted().collect(Collectors.toList()));
        result.put("notMatchingProducts", notMatchingProducts);
        return result;
    }

    /**
     *
     * @param matchingProducts
     * @param unavailableProducts
     * @param availabilityDataMap
     * @param quantityAdjustedCounter
     * @return
     */
    private Map<MultiKey, ImportToolMatchingData> adjustMatchedProductsQuantity(final Map<MultiKey, ImportToolMatchingData> matchingProducts,
                                                                                final Map<MultiKey, ImportToolMatchingData> unavailableProducts,
                                                                                final Map<String, ProductAvailabilityData> availabilityDataMap,
                                                                                final AtomicInteger quantityAdjustedCounter) {
        return matchingProducts.entrySet().stream()
                               .filter(entry -> {
                                   final ImportToolMatchingData importToolMatchingData = entry.getValue();
                                   final ProductAvailabilityData availabilityData = availabilityDataMap.get(importToolMatchingData.getProductCode());

                                   if (availabilityData != null && !isAvailableAfterStockIsDepleted(importToolMatchingData.getSalesStatus())) {
                                       final Integer availableQuantity = availabilityData.getStockLevelTotal();

                                       if (availableQuantity == 0) {
                                           unavailableProducts.put(entry.getKey(), entry.getValue());
                                           return false;
                                       }

                                       if (availableQuantity < importToolMatchingData.getQuantity()) {
                                           quantityAdjustedCounter.incrementAndGet();
                                           importToolMatchingData.setQuantity(availableQuantity);
                                       }
                                   }

                                   return true;
                               }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private boolean isAvailableForSale(final DistSalesStatusModel salesStatus) {
        if (salesStatus == null || StringUtils.isEmpty(salesStatus.getCode())) {
            return false;
        }
        return !getErpStatusUtil().getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_NO_PRODUCT_FOR_SALE_IMPORTTOOL)
                                  .contains(salesStatus.getCode());
    }

    private boolean isAvailableAfterStockIsDepleted(final String salesStatusCode) {
        if (StringUtils.isEmpty(salesStatusCode)) {
            return false;
        }
        return !getErpStatusUtil().getErpSalesStatusFromConfiguration(DistConfigConstants.ErpSalesStatus.ATTRIBUTE_IS_AVAILABLE_AFTER_STOCK_DEPLETION_IMPORTTOOL)
                                  .contains(salesStatusCode);
    }

    /**
     *
     * @param product
     * @param salesStatus
     * @param quantity
     * @param reference
     * @param options
     * @param onlinePriceInfos
     * @param position
     * @return
     */
    protected ImportToolMatchingData convert(final ProductModel product, final DistSalesStatusModel salesStatus, final long quantity, final String reference,
                                             final List<ProductOption> options, final Map<String, PriceInformation> onlinePriceInfos, final int position) {

        final ImportToolMatchingData importToolMatchingData = new ImportToolMatchingData();
        importToolMatchingData.setProductCode(product.getCode());
        importToolMatchingData.setQuantity(quantity);
        final ProductData productData = getProductFacade().getProductForCodeAndOptions(product.getCode(), options);
        productData.setSalesUnit(product.getRelevantSalesUnit());
        // Process the online prices
        if (onlinePriceInfos.containsKey(product.getCode())) {
            final PriceInformation priceInfo = onlinePriceInfos.get(product.getCode());
            final Long minQuantity = defaultIfNull(getMinQuantity(priceInfo), Long.valueOf(1));
            productData.setVolumePricesMap(
                                           singletonMap(minQuantity, singletonMap(ProductVolumePricesPopulator.CUSTOM,
                                                                                  createPriceData(PriceDataType.BUY, priceInfo, minQuantity))));
            productData.setSalesUnit(product.getRelevantSalesUnit());
        }

        importToolMatchingData.setProduct(productData);
        importToolMatchingData.setReference(reference);
        importToolMatchingData.setPosition(position);

        if (salesStatus != null) {
            importToolMatchingData.setSalesStatus(salesStatus.getCode());
        }

        return importToolMatchingData;
    }

    private int count(final File file) throws ImportToolException {
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new FileReader(file));
            lnr.skip(Long.MAX_VALUE);
            return lnr.getLineNumber();
        } catch (final IOException e) {
            throw new ImportToolException(e, ErrorSource.IO_EXCEPTION);
        } finally {
            IOUtils.closeQuietly(lnr);
        }
    }

    /**
     * Calculate the scale of the given price.
     *
     * @param priceInfo
     *            the price.
     * @return the scale of the price.
     */
    protected Long getMinQuantity(final PriceInformation priceInfo) {
        final Map qualifiers = priceInfo.getQualifiers();
        final Object minQtdObj = qualifiers.get(PriceRow.MINQTD);
        if (minQtdObj instanceof Long) {
            return (Long) minQtdObj;
        }
        return null;
    }

    /**
     * Find the product by its unique code.
     *
     * @param pcode
     *            the product code
     * @return the {@code ProductModel} having the specified code {@code pcode} if any, {@literal null} otherwise.
     */
    protected ProductModel productForCode(final String pcode) {
        try {
            return getProductService().getProductForCode(pcode.replace("-", ""));
        } catch (final Exception exp) {
            return null;
        }
    }

    /**
     * check whether the specified product is buyable or not.
     *
     * @param product
     *            the {@link ProductModel} to check
     * @return {@literal true} if the specified {@link ProductModel} is not {@literal null} and is buyable. {@literal false} otherwise.
     */
    protected boolean isBuyable(final ProductModel product) {
        return product != null && getProductFacade().isProductBuyable(product.getCode());
    }

    /**
     * Create a {@code PriceData} data object for the given price info.
     *
     * @param priceType
     *            the price type.
     * @param priceInfo
     *            the price info.
     * @param minQuantity
     *            the price scale.
     * @return a new instance of {@code PriceData}.
     * @see #createPriceData(PriceDataType, PriceInformation)
     * @since Distrelec 5.11
     */
    protected PriceData createPriceData(final PriceDataType priceType, final PriceInformation priceInfo, final Long minQuantity) {
        final PriceData price = createPriceData(priceType, priceInfo);
        price.setMinQuantity(minQuantity.longValue());
        price.setCurrencyIso(priceInfo.getPriceValue().getCurrencyIso());
        price.setFormattedValue(Double.toString(priceInfo.getPriceValue().getValue()));
        return price;
    }

    protected <T> T defaultIfNull(final T value, final T defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * Create a {@code PriceData} data object for the given price info.
     *
     * @param priceType
     *            the price type.
     * @param priceInfo
     *            the price info.
     * @return a new instance of {@code PriceData}.
     */
    protected PriceData createPriceData(final PriceDataType priceType, final PriceInformation priceInfo) {
        return getPriceDataFactory().create(priceType, BigDecimal.valueOf(priceInfo.getPriceValue().getValue()),
                                            priceInfo.getPriceValue().getCurrencyIso());
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(final ProductService productService) {
        this.productService = productService;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    public UserService getUserService() {
        return userService;
    }

    public void setUserService(final UserService userService) {
        this.userService = userService;
    }

    public DistCommercePriceService getDistCommercePriceService() {
        return distCommercePriceService;
    }

    public void setDistCommercePriceService(final DistCommercePriceService distCommercePriceService) {
        this.distCommercePriceService = distCommercePriceService;
    }

    public PriceDataFactory getPriceDataFactory() {
        return priceDataFactory;
    }

    public void setPriceDataFactory(final PriceDataFactory priceDataFactory) {
        this.priceDataFactory = priceDataFactory;
    }

    public ErpStatusUtil getErpStatusUtil() {
        return erpStatusUtil;
    }

    public void setErpStatusUtil(ErpStatusUtil erpStatusUtil) {
        this.erpStatusUtil = erpStatusUtil;
    }
}
