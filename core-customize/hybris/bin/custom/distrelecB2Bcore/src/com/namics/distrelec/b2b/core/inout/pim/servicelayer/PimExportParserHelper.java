/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimXmlHashDto;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistAudioMediaModel;
import com.namics.distrelec.b2b.core.model.DistDownloadMediaModel;
import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;

import de.hybris.platform.catalog.model.classification.ClassificationAttributeUnitModel;
import de.hybris.platform.catalog.model.classification.ClassificationSystemVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.util.Config;

/**
 * Class with helper methods used in {@link ServiceLayerPimExportParser}.
 * 
 * @author ascherrer, Namics AG
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class PimExportParserHelper {

    private static final Logger LOG = LogManager.getLogger(PimExportParserHelper.class);
    public static final String FAMILY_TYPECODE = "Familie";

    private Resource blacklistedProductFeaturesTextFile;
    private Resource whitelistedRootProductFeaturesTextFile;
    private Resource whitelistedCategoriesTextFile;
    private Resource nonLocalizedProductFeaturesTextFile;

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    public Map<String, PimXmlHashDto> getProductHashes() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(ProductModel.CODE).append("}, ");
        query.append("{").append(ProductModel.PIMXMLHASHMASTER).append("}, ");
        query.append("{").append(ProductModel.PIMHASHTIMESTAMP).append("} ");
        query.append("FROM {").append(ProductModel._TYPECODE).append("} ");
        query.append("WHERE {").append(ProductModel.PIMXMLHASHMASTER).append("} IS NOT NULL");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());

        flexibleSearchQuery.setResultClassList(getResultClassList());

        return getHashes(flexibleSearchQuery);
    }

    public Map<String, PimXmlHashDto> getUnitHashes(final ClassificationSystemVersionModel classificationSystemVersion) {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(ClassificationAttributeUnitModel.CODE).append("}, ");
        query.append("{").append(ClassificationAttributeUnitModel.PIMXMLHASHMASTER).append("}, ");
        query.append("{").append(ClassificationAttributeUnitModel.PIMHASHTIMESTAMP).append("} ");
        query.append("FROM {").append(ClassificationAttributeUnitModel._TYPECODE).append("} ");
        query.append("WHERE {").append(ClassificationAttributeUnitModel.SYSTEMVERSION).append("} = ?").append(ClassificationAttributeUnitModel.SYSTEMVERSION);
        query.append(" AND {").append(ClassificationAttributeUnitModel.PIMXMLHASHMASTER).append("} IS NOT NULL");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());
        flexibleSearchQuery.addQueryParameter(ClassificationAttributeUnitModel.SYSTEMVERSION, classificationSystemVersion);

        flexibleSearchQuery.setResultClassList(getResultClassList());

        return getHashes(flexibleSearchQuery);
    }

    public Map<String, PimXmlHashDto> getMediaContainerHashes() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(MediaContainerModel.QUALIFIER).append("}, ");
        query.append("{").append(MediaContainerModel.PIMXMLHASHMASTER).append("}, ");
        query.append("{").append(MediaContainerModel.PIMHASHTIMESTAMP).append("} ");
        query.append("FROM {").append(MediaContainerModel._TYPECODE).append("} ");
        query.append("WHERE {").append(MediaContainerModel.PIMXMLHASHMASTER).append("} IS NOT NULL");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());

        flexibleSearchQuery.setResultClassList(getResultClassList());

        return getHashes(flexibleSearchQuery);
    }

    public Map<String, PimXmlHashDto> getAudioMediaHashes() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistAudioMediaModel.CODE).append("}, ");
        query.append("{").append(DistAudioMediaModel.PIMXMLHASHMASTER).append("}, ");
        query.append("{").append(MediaContainerModel.PIMHASHTIMESTAMP).append("} ");
        query.append("FROM {").append(DistAudioMediaModel._TYPECODE).append("} ");
        query.append("WHERE {").append(DistAudioMediaModel.PIMXMLHASHMASTER).append("} IS NOT NULL");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());

        flexibleSearchQuery.setResultClassList(getResultClassList());

        return getHashes(flexibleSearchQuery);
    }

    public Map<String, PimXmlHashDto> getDownloadMediaHashes() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistDownloadMediaModel.CODE).append("}, ");
        query.append("{").append(DistDownloadMediaModel.PIMXMLHASHMASTER).append("}, ");
        query.append("{").append(DistDownloadMediaModel.PIMHASHTIMESTAMP).append("} ");
        query.append("FROM {").append(DistDownloadMediaModel._TYPECODE).append("} ");
        query.append("WHERE {").append(DistDownloadMediaModel.PIMXMLHASHMASTER).append("} IS NOT NULL");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());

        flexibleSearchQuery.setResultClassList(getResultClassList());

        return getHashes(flexibleSearchQuery);
    }

    public Map<String, PimXmlHashDto> getVideoMediaHashes() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistVideoMediaModel.CODE).append("}, ");
        query.append("{").append(DistVideoMediaModel.PIMXMLHASHMASTER).append("}, ");
        query.append("{").append(DistVideoMediaModel.PIMHASHTIMESTAMP).append("} ");
        query.append("FROM {").append(DistVideoMediaModel._TYPECODE).append("} ");
        query.append("WHERE {").append(DistVideoMediaModel.PIMXMLHASHMASTER).append("} IS NOT NULL");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());

        flexibleSearchQuery.setResultClassList(getResultClassList());

        return getHashes(flexibleSearchQuery);
    }

    public Map<String, PimXmlHashDto> getManufacturerHashes() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistManufacturerModel.CODE).append("}, ");
        query.append("{").append(DistManufacturerModel.PIMXMLHASHMASTER).append("}, ");
        query.append("{").append(DistManufacturerModel.PIMHASHTIMESTAMP).append("} ");
        query.append("FROM {").append(DistManufacturerModel._TYPECODE).append("} ");
        query.append("WHERE {").append(DistManufacturerModel.PIMXMLHASHMASTER).append("} IS NOT NULL");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());

        flexibleSearchQuery.setResultClassList(getResultClassList());

        return getHashes(flexibleSearchQuery);
    }

    public Map<String, PimXmlHashDto> getImage360Hashes() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistImage360Model.CODE).append("}, ");
        query.append("{").append(DistImage360Model.PIMXMLHASHMASTER).append("}, ");
        query.append("{").append(DistImage360Model.PIMHASHTIMESTAMP).append("} ");
        query.append("FROM {").append(DistImage360Model._TYPECODE).append("} ");
        query.append("WHERE {").append(DistImage360Model.PIMXMLHASHMASTER).append("} IS NOT NULL");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());

        flexibleSearchQuery.setResultClassList(getResultClassList());

        return getHashes(flexibleSearchQuery);
    }

    public Map<String, PimXmlHashDto> getProductFamilyHashes() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {c.").append(CategoryModel.CODE).append("}, ");
        query.append("{c.").append(CategoryModel.PIMXMLHASHMASTER).append("}, ");
        query.append("{c.").append(CategoryModel.PIMHASHTIMESTAMP).append("} ");
        query.append("FROM {").append(CategoryModel._TYPECODE).append(" as c ");
        query.append("JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS t ON ");
        query.append("{c.").append(CategoryModel.PIMCATEGORYTYPE).append("} = {t.").append(DistPimCategoryTypeModel.PK).append("}} ");
        query.append("WHERE {t.").append(DistPimCategoryTypeModel.CODE).append("} = ?").append(DistPimCategoryTypeModel.CODE);
        query.append(" AND {c.").append(CategoryModel.PIMXMLHASHMASTER).append("} IS NOT NULL");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());
        flexibleSearchQuery.addQueryParameter(DistPimCategoryTypeModel.CODE, FAMILY_TYPECODE);

        flexibleSearchQuery.setResultClassList(getResultClassList());

        return getHashes(flexibleSearchQuery);
    }

    public List<String> getManufacturerCodes() {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT {").append(DistManufacturerModel.CODE).append("} ");
        query.append("FROM {").append(DistManufacturerModel._TYPECODE).append("}");

        final FlexibleSearchQuery flexibleSearchQuery = new FlexibleSearchQuery(query.toString());

        final List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        resultClassList.add(String.class);
        flexibleSearchQuery.setResultClassList(resultClassList);

        final SearchResult<String> result = flexibleSearchService.search(flexibleSearchQuery);
        final List<String> codes = new ArrayList<String>();
        codes.addAll(result.getResult());
        return codes;
    }

    public Map<String, Integer> getCategoryLevels() {
        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(CategoryModel.CODE).append("}, ");
        queryString.append("{").append(CategoryModel.LEVEL).append("} ");
        queryString.append("FROM {").append(CategoryModel._TYPECODE).append("}");
        queryString.append("WHERE {").append(CategoryModel.LEVEL).append("} IS NOT NULL");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());

        final List<Class<?>> resultClassList = new ArrayList<Class<?>>();
        resultClassList.add(String.class);
        resultClassList.add(Integer.class);
        query.setResultClassList(resultClassList);

        final Map<String, Integer> categoryLevels = new HashMap<String, Integer>();
        final SearchResult<List<?>> result = flexibleSearchService.search(query);
        for (final List<?> row : result.getResult()) {
            categoryLevels.put((String) row.get(0), (Integer) row.get(1));
        }

        return categoryLevels;
    }

    public Set<String> getBlacklistedProductFeatures() {
        final Set<String> blacklistedProductFeatures = readStringLines(blacklistedProductFeaturesTextFile);
        if (!blacklistedProductFeatures.isEmpty()) {
            LOG.info(blacklistedProductFeatures.size() + " blacklisted ProductFeatures will be skipped during import.");
        }
        return blacklistedProductFeatures;
    }

    public Set<String> getWhitelistedRootProductFeatures() {
        final Set<String> whitelistedRootProductFeatures = readStringLines(whitelistedRootProductFeaturesTextFile);
        if (!whitelistedRootProductFeatures.isEmpty()) {
            LOG.info(whitelistedRootProductFeatures.size() + " whitelisted ProductFeatures allowed to be assigned to root-class during import.");
        }
        return whitelistedRootProductFeatures;
    }

    public Set<String> getNonLocalizedProductFeatures() {
        final Set<String> nonLocalizedProductFeatures = readStringLines(nonLocalizedProductFeaturesTextFile);
        if (!nonLocalizedProductFeatures.isEmpty()) {
            LOG.info(nonLocalizedProductFeatures.size() + " non-localized ProductFeatures allowed to be language independant during import.");
        }
        return nonLocalizedProductFeatures;
    }

    public Set<String> getWhitelistedCategories() {
        if (!Config.getBoolean(DistConstants.PropKey.Import.IMPORT_PRODUCTS_OF_WHITELISTED_CATEGORIES_ONLY, false)) {
            return new HashSet<String>();
        }

        final Set<String> whitelistedCategories = readStringLines(whitelistedCategoriesTextFile);
        if (!whitelistedCategories.isEmpty()) {
            LOG.info("Only products of " + whitelistedCategories.size() + " whitelisted categories will be honored during import.");
        }
        return whitelistedCategories;
    }

    private Set<String> readStringLines(final Resource resourceFile) {
        final Set<String> entries = new HashSet<String>();

        InputStream inputStream = null;
        Scanner scanner = null;
        try {
            if (resourceFile.exists()) {
                inputStream = resourceFile.getInputStream();
                scanner = new Scanner(inputStream);
                while (scanner.hasNext()) {
                    final String line = scanner.nextLine().trim();
                    if (line.length() > 0 && line.charAt(0) != '#') {
                        entries.add(line);
                    }
                }
            } else {
                LOG.info("File [{}] cannot be found.", resourceFile.getFilename());
            }
        } catch (IOException e) {
            LOG.info("File [{}] cannot be read.", resourceFile.getFilename());
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(scanner);
        }
        return entries;
    }

    private Map<String, PimXmlHashDto> getHashes(final FlexibleSearchQuery flexibleSearchQuery) {
        final Map<String, PimXmlHashDto> hashes = new HashMap<>();
        final SearchResult<List<Object>> result = flexibleSearchService.search(flexibleSearchQuery);
        for (final List<Object> row : result.getResult()) {
            PimXmlHashDto pimXmlHashDto = new PimXmlHashDto();
            pimXmlHashDto.setPimXmlHashMaster((String) row.get(1));
            pimXmlHashDto.setPimHashTimestamp((Date) row.get(2));
            hashes.put((String) row.get(0), pimXmlHashDto);
        }
        return hashes;
    }

    private List<Class<?>> getResultClassList() {
        final List<Class<?>> resultClassList = new ArrayList<>();
        resultClassList.add(String.class);
        resultClassList.add(String.class);
        resultClassList.add(Date.class);
        return resultClassList;
    }

    @Required
    public void setBlacklistedProductFeaturesTextFile(final Resource blacklistedProductFeaturesTextFile) {
        this.blacklistedProductFeaturesTextFile = blacklistedProductFeaturesTextFile;
    }

    @Required
    public void setWhitelistedRootProductFeaturesTextFile(final Resource whitelistedRootProductFeaturesTextFile) {
        this.whitelistedRootProductFeaturesTextFile = whitelistedRootProductFeaturesTextFile;
    }

    @Required
    public void setWhitelistedCategoriesTextFile(final Resource whitelistedCategoriesTextFile) {
        this.whitelistedCategoriesTextFile = whitelistedCategoriesTextFile;
    }

    @Required
    public void setNonLocalizedProductFeaturesTextFile(final Resource nonLocalizedProductFeaturesTextFile) {
        this.nonLocalizedProductFeaturesTextFile = nonLocalizedProductFeaturesTextFile;
    }
}
