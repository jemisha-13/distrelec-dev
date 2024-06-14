package com.namics.distrelec.b2b.facades.search.converter;

import static com.namics.distrelec.b2b.core.constants.DistConstants.FactFinder.CATEGORY;
import static com.namics.distrelec.b2b.core.constants.DistConstants.MediaFormat.PORTRAIT_MEDIUM;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.EFFICENCY_FEATURE;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.ENERGY_CLASSES_LUMINAR_BUILT_IN_LED;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.ENERGY_CLASSES_LUMINAR_FITTING;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.ENERGY_CLASSES_LUMINAR_INCLUDED_BULB;
import static com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimConstants.POWER_FEATURE;
import static java.util.Collections.singletonList;
import static org.apache.commons.collections4.MapUtils.isNotEmpty;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.service.classification.dao.DistClassificationDao;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;
import com.namics.distrelec.b2b.core.service.wishlist.DistWishlistService;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.b2b.facades.search.converter.populator.SearchResultUrlPopulator;
import com.namics.hybris.ffsearch.data.search.DistSearchType;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.catalog.model.classification.ClassificationAttributeModel;
import de.hybris.platform.classification.features.Feature;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.classification.features.FeatureValue;
import de.hybris.platform.classification.features.LocalizedFeature;
import de.hybris.platform.classification.features.UnlocalizedFeature;
import de.hybris.platform.commercefacades.product.data.CategoryData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.search.resultdata.SearchResultValueData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.site.BaseSiteService;

public class SearchResultProductConverter extends AbstractPopulatingConverter<SearchResultValueData, ProductData> {

    private static final Logger LOG = LogManager.getLogger(SearchResultProductConverter.class);

    private static final int[] CATEGORY_LEVELS = { 1, 2, 3, 4, 5 };

    private String searchType;

    public static final String TRUE_STRING = "1";

    private static final String CATALOG_PLUS_RECORD = "catalogPlusRecord";

    private static final String UNESCAPED_CHAR_MATCH_STRING = "%(?![0-9a-fA-F]{2})";

    private static final String UNESCAPED_CHAR_REPLACE = "%25";

    private static final String UNESCAPED_CHAR_PLUS = "%2B";

    private static final String PLUS_REGEX = "\\+";

    private static final String PIPE_SEPERATOR = "|";

    private static final String BLANK_STRING = "";

    private static final char CATEGORY_PATH_SEPERATOR = '/';

    private static final char EQUAL = '=';

    private static final char PATH_SEPERATOR = '~';

    private static final int MINIMUM_STRING_LENGTH = 0;

    public static final String SUB_CATEGORY_CODE_ROOT_PATH_PREFIX = "|categoryCodePathROOT/";

    private CommonI18NService commonI18NService;

    private DistSalesOrgService distSalesOrgService;

    private Populator<FeatureList, ProductData> productFeatureListPopulator;

    private SearchResultUrlPopulator searchResultUrlPopulator;

    @Autowired
    private DistClassificationDao classificationDao;

    @Autowired
    @Qualifier("productFacade")
    private DistrelecProductFacade productFacade;

    @Autowired
    private I18NService i18NService;

    @Autowired
    private DistWishlistService distWishlistService;

    @Autowired
    private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

    @Autowired
    private BaseSiteService baseSiteService;

    private static final List<String> EEL_CODES = Arrays.asList(EFFICENCY_FEATURE, POWER_FEATURE, ENERGY_CLASSES_LUMINAR_FITTING, //
                                                                ENERGY_CLASSES_LUMINAR_INCLUDED_BULB, ENERGY_CLASSES_LUMINAR_BUILT_IN_LED);

    private Map<String, Map<String, String>> energyEfficiencyLabels;

    @Override
    public void populate(final SearchResultValueData source, final ProductData target) {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (isServicePlusProduct(source)) {
            populateServicePlusProduct(source, target);
        } else {
            populateProduct(source, target);
        }
        super.populate(source, target);
    }

    protected boolean isServicePlusProduct(final SearchResultValueData source) {
        return BooleanUtils.toBoolean(this.<String> getValue(source, CATALOG_PLUS_RECORD));
    }

    protected void populateProduct(final SearchResultValueData source, final ProductData target) {
        target.setCode(this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER.getValue()));
        target.setCodeErpRelevant(evaluateCodeErpRelevant(source));
        try {
            String data = this.getValue(source, DistFactFinderExportColumns.TITLE.getValue());
            data = data.replaceAll(UNESCAPED_CHAR_MATCH_STRING, UNESCAPED_CHAR_REPLACE);
            data = data.replaceAll(PLUS_REGEX, UNESCAPED_CHAR_PLUS);
            target.setName(URLDecoder.decode(data, StandardCharsets.UTF_8));
            target.setSalesStatus(getProductFacade().getProductSalesStatus(target.getCode()));
        } catch (final Exception e) {
            LOG.error("Error while decoding product title: " + this.<String> getValue(source, DistFactFinderExportColumns.TITLE.getValue()), e);
        }
        target.setManufacturer(this.getValue(source, DistFactFinderExportColumns.MANUFACTURER.getValue()));
        target.setDescription(this.getValue(source, DistFactFinderExportColumns.DESCRIPTION.getValue()));
        target.setTypeName(this.getValue(source, DistFactFinderExportColumns.TYPENAME.getValue()));
        target.setSalesUnit(this.getValue(source, DistFactFinderExportColumns.SALESUNIT.getValue()));
        target.setBuyable(getBuyable(source));
        target.setProductFamilyUrl(getProductFamilyUrl(source));

        populateEELs(target, this.getValue(source, DistFactFinderExportColumns.WEB_USE.getValue()));

        target.setMovexArticleNumber(this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER_MOVEX.getValue()));
        target.setElfaArticleNumber(this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER_ELFA.getValue()));
        target.setNavisionArticleNumber(this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER_NAVISION.getValue()));
        target.setOrigPosition(this.getValue(source, DistFactFinderExportColumns.ORIG_POSITION.getValue()));
        target.setCampaign(this.getValue(source, DistFactFinderExportColumns.CAMPAIGN.getValue()));
        target.setInShoppingList(isNotEmpty(distWishlistService.productsInShoppingList(singletonList(target.getCode()))));
        final String categoryCodePath = this.getValue(source, DistFactFinderExportColumns.CATEGORY_CODE_PATH.getValue());
        target.setCategoryCodePath(categoryCodePath != null ? getCategoryCodeRootPath(categoryCodePath) : BLANK_STRING);
        final String orderQuantityMinimum = this.getValue(source, DistFactFinderExportColumns.ITEMS_MIN.getValue());
        if (isNotBlank(orderQuantityMinimum) && StringUtils.isNumeric(orderQuantityMinimum)) {
            target.setOrderQuantityMinimum(Long.valueOf(orderQuantityMinimum));
        } else {
            target.setOrderQuantityMinimum(1L);
        }
        final String orderQuantityStep = this.getValue(source, DistFactFinderExportColumns.ITEMS_STEP.getValue());
        if (isNotBlank(orderQuantityStep) && StringUtils.isNumeric(orderQuantityStep)) {
            target.setOrderQuantityStep(Long.valueOf(orderQuantityStep));
        } else {
            target.setOrderQuantityStep(1L);
        }
        getSearchResultUrlPopulator().populate(source, target, getSearchType());
        target.setItemCategoryGroup(getValue(source, DistFactFinderExportColumns.ITEM_CATEGORY_GROUP.getValue()));
        target.setEnergyEfficiencyLabelImage(getEnergyEfficiencyLabelImage(source));
        target.setCategories(getCategories(source));
        target.setIsBetterWorld(getProductFacade().isBetterWorldProduct(target.getCode()));  // temporary: https://jira.distrelec.com/browse/DISTRELEC-35022
    }

    private List<CategoryData> getCategories(SearchResultValueData source) {
        return IntStream.of(CATEGORY_LEVELS)
                        .filter(categoryLevel -> isNotBlank(this.getValue(source, getCategoryNameColumn(categoryLevel))))
                        .mapToObj(categoryLevel -> {
                            CategoryData categoryData = new CategoryData();
                            categoryData.setName(this.getValue(source, getCategoryNameColumn(categoryLevel)));
                            categoryData.setCode(this.getValue(source, getCategoryCodeColumn(categoryLevel)));
                            categoryData.setLevel(categoryLevel);
                            return categoryData;
                        }).collect(Collectors.toList());
    }

    private String getCategoryNameColumn(int categoryLevel) {
        return DistFactFinderExportColumns.valueOf(CATEGORY + categoryLevel).getValue();
    }

    private String getCategoryCodeColumn(int categoryLevel) {
        return DistFactFinderExportColumns.valueOf(CATEGORY + categoryLevel + "_CODE").getValue();
    }

    private Map<String, ImageData> getEnergyEfficiencyLabelImage(SearchResultValueData source) {
        Map<String, ImageData> images = new HashMap<>();
        String imageUrl = this.getValue(source, DistFactFinderExportColumns.ENERGY_EFFICIENCY_LABEL_IMAGE_URL.getValue());

        if (isNotBlank(imageUrl)) {
            BaseSiteModel site = baseSiteService.getCurrentBaseSite();
            String mediaDomainUrl = siteBaseUrlResolutionService.getMediaUrlForSite(site, true);
            String alt = "Energy Efficiency Label";
            ImageData imageData = new ImageData();
            imageData.setAltText(alt);
            imageData.setFormat(PORTRAIT_MEDIUM);
            imageData.setUrl(mediaDomainUrl + imageUrl);
            imageData.setName(alt);
            images.put(PORTRAIT_MEDIUM, imageData);
        }

        return images;
    }

    protected void populateServicePlusProduct(final SearchResultValueData source, final ProductData target) {
        target.setCode(this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER.getValue()));
        target.setCodeErpRelevant(this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER.getValue()));
        target.setName(this.getValue(source, DistFactFinderExportColumns.TITLE.getValue()));
        target.setManufacturer(this.getValue(source, DistFactFinderExportColumns.MANUFACTURER.getValue()));
        target.setTypeName(this.getValue(source, DistFactFinderExportColumns.TYPENAME.getValue()));
        target.setSalesUnit(this.getValue(source, DistFactFinderExportColumns.SALESUNIT.getValue()));
        target.setBuyable(true);
        target.setCatPlusItem(true);

        populateEELs(target, this.getValue(source, DistFactFinderExportColumns.WEB_USE.getValue()));

        target.setMovexArticleNumber(this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER_MOVEX.getValue()));
        target.setElfaArticleNumber(this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER_ELFA.getValue()));
        target.setNavisionArticleNumber(this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER_NAVISION.getValue()));

        getSearchResultUrlPopulator().populate(source, target, getSearchType());
    }

    protected String evaluateCodeErpRelevant(final SearchResultValueData source) {
        return this.getValue(source, DistFactFinderExportColumns.PRODUCT_NUMBER.getValue());
    }

    @Deprecated
    protected FeatureList getFeaturesList(final SearchResultValueData source) {
        final List<Feature> featuresList = new ArrayList<>();
        final Locale currentLocale = getCommonI18NService().getLocaleForLanguage(getCommonI18NService().getCurrentLanguage());

        if (source != null && source.getFeatureValues() != null && !source.getFeatureValues().isEmpty()) {
            // Pull the classification features
            for (final Map.Entry<ClassAttributeAssignmentModel, Object> featureEntry : source.getFeatureValues().entrySet()) {
                final ClassAttributeAssignmentModel classAttributeAssignment = featureEntry.getKey();
                final Object value = featureEntry.getValue();
                final FeatureValue featureValue = new FeatureValue(value, null, classAttributeAssignment.getUnit());
                final Feature feature;
                if (Boolean.TRUE.equals(classAttributeAssignment.getLocalized())) {
                    final Map<Locale, List<FeatureValue>> featureMap = new HashMap<>();
                    featureMap.put(currentLocale, singletonList(featureValue));
                    feature = new LocalizedFeature(classAttributeAssignment, featureMap, currentLocale);
                } else {
                    feature = new UnlocalizedFeature(classAttributeAssignment, singletonList(featureValue));
                }
                featuresList.add(feature);
            }
        }
        return new FeatureList(featuresList);
    }

    private <T> T getValue(final SearchResultValueData source, final String propertyName) {
        if (source.getValues() == null) {
            return null;
        }
        return (T) source.getValues().get(propertyName);
    }

    private boolean getBuyable(final SearchResultValueData source) {
        final String buyableString = getValue(source, DistFactFinderExportColumns.BUYABLE.getValue());
        return TRUE_STRING.equals(buyableString);
    }

    /**
     * TODO: export product family url directly to ff and just use it here
     */
    protected String getProductFamilyUrl(final SearchResultValueData source) {
        final String productFamilyCode = this.getValue(source, DistFactFinderExportColumns.PRODUCT_FAMILY_CODE.getValue());
        if (isNotBlank(productFamilyCode)) {
            final String language = i18NService.getCurrentLocale().getLanguage();
            final String productFamilyName = this.getValue(source, DistFactFinderExportColumns.PRODUCT_FAMILY_NAME.getValue());
            return "/" + language
                   + "/" + UrlResolverUtils.normalize(productFamilyName, true)
                   + "/" + DistConstants.UrlTags.PRODUCT_FAMILY
                   + "/" + productFamilyCode;
        }
        return null;
    }

    @Override
    protected ProductData createTarget() {
        return new ProductData();
    }

    /**
     * Populate the EEL attributes
     *
     * @param target
     *            the product data
     * @param webUseString
     *            the web use string received from FF.
     */
    protected void populateEELs(final ProductData target, final String webUseString) {
        if (webUseString != null && target != null) {
            final String language = i18NService.getCurrentLocale().getLanguage();

            // Energy class
            target.setEnergyEfficiency(getEELValue(getEnergyLabel(EFFICENCY_FEATURE, language), webUseString));
            if (target.getEnergyEfficiency() != null && target.getEnergyEfficiency().contains("=")) {
                target.setEnergyEfficiency(target.getEnergyEfficiency().replace("=", ""));
            }
            // Energy Power
            final String energyPower = getEELValue(getEnergyLabel(POWER_FEATURE, language), webUseString);
            final int index = StringUtils.indexOf(energyPower, "=");
            target.setEnergyPower(energyPower != null && index >= 0 ? energyPower.substring(index + 1) : energyPower);
            // Energy classes fitting
            target.setEnergyClassesFitting(getEELValue(getEnergyLabel(ENERGY_CLASSES_LUMINAR_FITTING, language), webUseString));
            // Energy classes LED
            target.setEnergyClassesBuiltInLed(getEELValue(getEnergyLabel(ENERGY_CLASSES_LUMINAR_BUILT_IN_LED, language), webUseString));
            // Energy classes BULB
            target.setEnergyClassesIncludedBulb(getEELValue(getEnergyLabel(ENERGY_CLASSES_LUMINAR_INCLUDED_BULB, language), webUseString));
        }
    }

    /**
     * Look for the EEL value in the webUse string for the specified name. If there are more than one value, then one string containing
     * these values separated by ";".
     *
     * @param locName
     *            the EEL localized name.
     * @param webUseString
     *            the webUse String received from FF
     * @return the value of the EEL.
     */
    private String getEELValue(final String locName, final String webUseString) {
        if (locName == null || webUseString == null) {
            return null;
        }
        int index = 0;
        final StringBuilder sb = new StringBuilder();
        while ((index = webUseString.indexOf(locName, index)) > -1) {
            // e.g.: Energy classes=A+ => the '+1' is to ignore the '=' sign
            final String value = webUseString.substring(index + locName.length(), webUseString.indexOf('|', index));
            if (sb.length() != 0) {
                sb.append(";");
            }
            sb.append(value);
            // Add the "length(locName) + length(value) + 2" to avoid infinite
            // loops, adding just "1" should be enough also.
            index += locName.length() + value.length() + 2;
        }

        return sb.length() != 0 ? sb.toString() : null;
    }

    /**
     * Fetch the name of the energy efficiency label, given by it's code, in the specified language.
     *
     * @param eelCode
     *            the Energy efficiency label code
     * @param language
     *            the target language
     * @return the name of the energy efficiency label, given by it's code, in the specified language.
     */
    private String getEnergyLabel(final String eelCode, final String language) {
        if (energyEfficiencyLabels == null) {
            // This code must be executed only once during the application life
            // cycle.
            energyEfficiencyLabels = new HashMap<>();
            for (final String code : EEL_CODES) {
                try {
                    final ClassificationAttributeModel classificationAttribute = classificationDao.findClassificationAttribute(code);
                    final Map<String, String> loclizedLabels = new HashMap<>();

                    for (final LanguageModel lang : commonI18NService.getAllLanguages()) {
                        try {
                            final String name = classificationAttribute.getName(new Locale(lang.getIsocode()));
                            if (isNotBlank(name)) {
                                loclizedLabels.put(lang.getIsocode(), name);
                            }
                        } catch (final Exception exp) {
                            // NOOP
                        }
                    }
                    energyEfficiencyLabels.put(code, loclizedLabels);
                } catch (final ModelNotFoundException ex) {
                    LOG.error("EEL code: " + code + " -> " + ex.getMessage(), ex);
                }
            }
        }

        return energyEfficiencyLabels != null && energyEfficiencyLabels.get(eelCode) != null ? energyEfficiencyLabels.get(eelCode).get(language) : null;
    }

    private String getCategoryCodeRootPath(final String categoryPath) {
        String categoryCodePath = "";
        final int lastIndex = categoryPath.lastIndexOf(PIPE_SEPERATOR);
        final int secondLastIndex = lastIndex > MINIMUM_STRING_LENGTH ? categoryPath.lastIndexOf(PIPE_SEPERATOR, lastIndex - 1) : -1;
        if (secondLastIndex >= MINIMUM_STRING_LENGTH) {
            categoryCodePath = categoryPath.substring(secondLastIndex, lastIndex);
            categoryCodePath = categoryCodePath.replace(SUB_CATEGORY_CODE_ROOT_PATH_PREFIX, BLANK_STRING);
            categoryCodePath = categoryCodePath.replace(EQUAL, CATEGORY_PATH_SEPERATOR);
        }
        categoryCodePath = categoryCodePath.replace(CATEGORY_PATH_SEPERATOR, PATH_SEPERATOR);
        // DISTRELEC-28427 - Only show leaf category
        final int pathSeparatorIndex = categoryCodePath.lastIndexOf(PATH_SEPERATOR);
        if (pathSeparatorIndex > -1) {
            categoryCodePath = categoryCodePath.substring(pathSeparatorIndex + 1);
        }
        return categoryCodePath;
    }

    // BEGIN GENERATED CODE

    protected CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    @Required
    public void setCommonI18NService(final CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }

    protected DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    @Required
    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

    protected Populator<FeatureList, ProductData> getProductFeatureListPopulator() {
        return productFeatureListPopulator;
    }

    @Required
    public void setProductFeatureListPopulator(final Populator<FeatureList, ProductData> productFeatureListPopulator) {
        this.productFeatureListPopulator = productFeatureListPopulator;
    }

    protected DistSearchType getSearchType() {
        if (StringUtils.isNotEmpty(searchType)) {
            return DistSearchType.valueOf(searchType);
        }
        return null;
    }

    public void setSearchType(final String searchType) {
        this.searchType = searchType;
    }

    protected SearchResultUrlPopulator getSearchResultUrlPopulator() {
        return searchResultUrlPopulator;
    }

    @Required
    public void setSearchResultUrlPopulator(final SearchResultUrlPopulator searchResultUrlPopulator) {
        this.searchResultUrlPopulator = searchResultUrlPopulator;
    }

    public DistrelecProductFacade getProductFacade() {
        return productFacade;
    }

    public void setProductFacade(final DistrelecProductFacade productFacade) {
        this.productFacade = productFacade;
    }

}
