/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimAttributeDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimProductReferenceDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimXmlHashDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.ClassificationClassWrapper;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import org.apache.commons.lang.mutable.MutableInt;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * context to hold runtime configuration of running import
 * 
 * @author rhaemmerli, Namics AG
 * @since Namics Extensions 1.0
 */
public class ImportContext {

    // Initial settings
    private String filename;
    private boolean ignoreRootAttributes;
    private CatalogVersionModel productCatalogVersion;
    private ClassificationSystemVersionModel classificationSystemVersion;
    private ClassificationClassWrapper rootClassificationClassWrapper;
    private String categoryCodePrefix;
    private String categoryCodeSuffix;
    private String classificationClassCodePrefix;
    private List<String> rootCategoryParentIds;
    private int itemsImported;
    private long startTime;
    private boolean cancelled;
    private Date stiboExportDate;
    private CountryModel country;
    private Date globalHashTimestamp;
    // DISTRELEC-11130
    private int sortingIndexIncrement = 10;
    private AtomicInteger categorySortingNumber;

    // Set during import
    private ClassificationClassWrapper currentClassificationClassWrapper;

    /**
     * Contains Hash values from already imported objects. Key of outer Map is a type code. Key of inner Map is the identifier of a model.
     */
    private final Map<String, Map<String, PimXmlHashDto>> hashValues;
    private final Map<String, List<String>> codes;

    private final Map<String, PimAttributeDto> attributeDtos;
    private final List<PimProductReferenceDto> productReferenceDtos;
    private final Map<String, String> productHashesToSaveLater;
    private final Map<String, String> classificationMediaContainerReferences;
    private final  Map<String, Set<Locale>> mediaLocaleMap;

    private Set<String> blacklistedProductFeatures;
    private Set<String> whitelistedCategories;
    private Set<String> whitelistedRootProductFeatures;
    private boolean useDynamicRootProductFeatures;
    private Set<String> nonLocalizedProductFeatures;
    private boolean importProductsOfCurrentProductLine = true;
    private Map<String, Integer> categoryLevels;
    private final Map<String, CategoryModel> categoryCache;

    private final Map<String, MutableInt> counters;

    private Timer loggingTimer;
    private Timer cancelTimer;

    public ImportContext() {
        hashValues = new HashMap<>();
        attributeDtos = new HashMap<>();
        productReferenceDtos = new ArrayList<>();
        productHashesToSaveLater = new HashMap<>();
        blacklistedProductFeatures = new HashSet<>();
        whitelistedCategories = new HashSet<>();
        whitelistedRootProductFeatures = new HashSet<>();
        nonLocalizedProductFeatures = new HashSet<>();
        categoryLevels = new HashMap<>();
        categoryCache = new HashMap<>();
        counters = new TreeMap<>();
        classificationMediaContainerReferences = new HashMap<>();
        codes = new HashMap<>();
        categorySortingNumber = new AtomicInteger(100);
        mediaLocaleMap = new HashMap<>();
    }

    public void incrementCounter(final String typeCode) {
        MutableInt counter = counters.get(typeCode);

        if (counter == null) {
            counter = new MutableInt();
            counters.put(typeCode, counter);
        }

        counter.increment();
    }

    public void incrementSkippedCounter(final String typeCode) {
        final String skippedTypeCode = typeCode + ":Skipped";
        MutableInt counter = counters.get(skippedTypeCode);

        if (counter == null) {
            counter = new MutableInt();
            counters.put(skippedTypeCode, counter);
        }

        counter.increment();
    }

    public PimXmlHashDto getHashValue(final String typeCode, final String id) {
        if (hashValues == null) {
            return null;
        }

        final Map<String, PimXmlHashDto> typeHashValues = hashValues.get(typeCode);
        if (typeHashValues == null) {
            return null;
        }

        return typeHashValues.get(id);
    }

    /**
     * <p>
     * DISTRELEC-11130.
     * </p>
     * <p>
     * Add 10 to the current sorting index and return the old value.
     * </p>
     * 
     * @return the current sorting index.
     */
    public int getSortingIndex() {
        return categorySortingNumber.getAndAdd(sortingIndexIncrement);
    }

    // BEGIN GENERATED CODE

    public String getFilename() {
        return filename;
    }

    public void setFilename(final String filename) {
        this.filename = filename;
    }

    public boolean isIgnoreRootAttributes() {
        return ignoreRootAttributes;
    }

    public void setIgnoreRootAttributes(final boolean ignoreRootAttributes) {
        this.ignoreRootAttributes = ignoreRootAttributes;
    }

    public ClassificationSystemVersionModel getClassificationSystemVersion() {
        return classificationSystemVersion;
    }

    public void setClassificationSystemVersion(final ClassificationSystemVersionModel classificationSystemVersion) {
        this.classificationSystemVersion = classificationSystemVersion;
    }

    public ClassificationClassWrapper getCurrentClassificationClassWrapper() {
        return currentClassificationClassWrapper;
    }

    public void setCurrentClassificationClass(final ClassificationClassModel currentClassificationClass) {
        this.currentClassificationClassWrapper = new ClassificationClassWrapper(currentClassificationClass);
    }

    public Map<String, PimAttributeDto> getAttributeDtos() {
        return attributeDtos;
    }

    public CatalogVersionModel getProductCatalogVersion() {
        return productCatalogVersion;
    }

    public void setProductCatalogVersion(final CatalogVersionModel productCatalogVersion) {
        this.productCatalogVersion = productCatalogVersion;
    }

    public List<PimProductReferenceDto> getProductReferenceDtos() {
        return productReferenceDtos;
    }

    public Map<String, MutableInt> getCounters() {
        return counters;
    }

    public Timer getLoggingTimer() {
        return loggingTimer;
    }

    public void setLoggingTimer(final Timer loggingTimer) {
        this.loggingTimer = loggingTimer;
    }

    public Set<String> getBlacklistedProductFeatures() {
        return blacklistedProductFeatures;
    }

    public void setBlacklistedProductFeatures(final Set<String> blacklistedProductFeatures) {
        this.blacklistedProductFeatures = blacklistedProductFeatures;
    }

    public Set<String> getWhitelistedCategories() {
        return whitelistedCategories;
    }

    public void setWhitelistedCategories(final Set<String> whitelistedCategories) {
        this.whitelistedCategories = whitelistedCategories;
    }

    public Set<String> getWhitelistedRootProductFeatures() {
        return whitelistedRootProductFeatures;
    }

    public void setWhitelistedRootProductFeatures(final Set<String> whitelistedRootProductFeatures) {
        this.whitelistedRootProductFeatures = whitelistedRootProductFeatures;
    }

    public boolean isImportProductsOfCurrentProductLine() {
        return importProductsOfCurrentProductLine;
    }

    public void setImportProductsOfCurrentProductLine(final boolean importProductsOfCurrentProductLine) {
        this.importProductsOfCurrentProductLine = importProductsOfCurrentProductLine;
    }

    public Map<String, Integer> getCategoryLevels() {
        return categoryLevels;
    }

    public void setCategoryLevels(final Map<String, Integer> categoryLevels) {
        this.categoryLevels = categoryLevels;
    }

    public Map<String, CategoryModel> getCategoryCache() {
        return categoryCache;
    }

    public ClassificationClassWrapper getRootClassificationClassWrapper() {
        return rootClassificationClassWrapper;
    }

    public void setRootClassificationClass(final ClassificationClassModel rootClassificationClass) {
        this.rootClassificationClassWrapper = new ClassificationClassWrapper(rootClassificationClass);
    }

    public String getCategoryCodePrefix() {
        return categoryCodePrefix;
    }

    public void setCategoryCodePrefix(final String categoryCodePrefix) {
        this.categoryCodePrefix = categoryCodePrefix;
    }

    public String getClassificationClassCodePrefix() {
        return classificationClassCodePrefix;
    }

    public void setClassificationClassCodePrefix(final String classificationClassCodePrefix) {
        this.classificationClassCodePrefix = classificationClassCodePrefix;
    }

    public List<String> getRootCategoryParentIds() {
        return rootCategoryParentIds;
    }

    public void setRootCategoryParentIds(final List<String> rootCategoryParentIds) {
        this.rootCategoryParentIds = rootCategoryParentIds;
    }

    public Map<String, Map<String, PimXmlHashDto>> getHashValues() {
        return hashValues;
    }

    public int getItemsImported() {
        return itemsImported;
    }

    public void setItemsImported(final int itemsImported) {
        this.itemsImported = itemsImported;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public Map<String, String> getClassificationMediaContainerReferences() {
        return classificationMediaContainerReferences;
    }

    public Map<String, List<String>> getCodes() {
        return codes;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Timer getCancelTimer() {
        return cancelTimer;
    }

    public void setCancelTimer(final Timer cancelTimer) {
        this.cancelTimer = cancelTimer;
    }

    public Map<String, String> getProductHashesToSaveLater() {
        return productHashesToSaveLater;
    }

    public Set<String> getNonLocalizedProductFeatures() {
        return nonLocalizedProductFeatures;
    }

    public void setNonLocalizedProductFeatures(final Set<String> nonLocalizedProductFeatures) {
        this.nonLocalizedProductFeatures = nonLocalizedProductFeatures;
    }

    public Date getStiboExportDate() {
        return stiboExportDate;
    }

    public void setStiboExportDate(Date stiboExportDate) {
        this.stiboExportDate = stiboExportDate;
    }

    public boolean isUseDynamicRootProductFeatures() {
        return useDynamicRootProductFeatures;
    }

    public void setUseDynamicRootProductFeatures(final boolean useDynamicRootProductFeatures) {
        this.useDynamicRootProductFeatures = useDynamicRootProductFeatures;
    }

    public CountryModel getCountry() {
        return country;
    }

    public void setCountry(final CountryModel country) {
        this.country = country;
    }

    public Date getGlobalHashTimestamp() {
        return globalHashTimestamp;
    }

    public void setGlobalHashTimestamp(final Date globalHashTimestamp) {
        this.globalHashTimestamp = globalHashTimestamp;
    }

    public String getCategoryCodeSuffix() {
        return categoryCodeSuffix;
    }

    public void setCategoryCodeSuffix(String categoryCodeSuffix) {
        this.categoryCodeSuffix = categoryCodeSuffix;
    }

    public int getSortingIndexIncrement() {
        return sortingIndexIncrement;
    }

    public void setSortingIndexIncrement(final int sortingIndexIncrement) {
        this.sortingIndexIncrement = sortingIndexIncrement;
    }

    public void addLocaleForMedia(String code, Locale locale) {
        getLocalesForMedia(code).add(locale);
    }

    public Set<Locale> getLocalesForMedia(String code) {
        return mediaLocaleMap.computeIfAbsent(code, k -> new HashSet<>());
    }

    // END GENERATED CODE
}
