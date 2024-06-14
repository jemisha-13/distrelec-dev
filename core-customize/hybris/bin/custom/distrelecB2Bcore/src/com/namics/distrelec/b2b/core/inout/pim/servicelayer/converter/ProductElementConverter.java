/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.constants.DistConstants.Product;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ConverterLanguageUtil;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimProductReferenceType;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimProductReferenceDto;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementIdNotFoundException;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.util.ProductReferenceCreator;
import com.namics.distrelec.b2b.core.model.DistAudioMediaModel;
import com.namics.distrelec.b2b.core.model.DistDownloadMediaModel;
import com.namics.distrelec.b2b.core.model.DistImage360Model;
import com.namics.distrelec.b2b.core.model.DistReplacementReasonModel;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import com.namics.distrelec.b2b.core.service.category.DistCategoryService;
import com.namics.distrelec.b2b.core.service.codelist.DistrelecCodelistService;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.core.service.media.DistImage360Service;
import com.namics.distrelec.b2b.core.service.media.DistMediaContainerService;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.url.UrlResolverUtils;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.catalog.model.ProductReferenceModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Product.ReplacementReasonCode.CALIBRATED_INSTRUMENTS;
import static com.namics.distrelec.b2b.core.constants.DistConstants.Product.ReplacementReasonCode.NOT_CALIBRATED_INSTRUMENTS;
import static org.apache.commons.lang.StringUtils.isNotBlank;

/**
 * Converts an XML element to a {@link ProductModel}.
 *
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ProductElementConverter extends AbstractElementConverter implements PimImportElementConverter<ProductModel> {

    private static final Logger LOG = LogManager.getLogger(ProductElementConverter.class);

    private final SimpleDateFormat DATE_FORMAT_PIM_SHORT = new SimpleDateFormat(PIM_DATE_FORMAT_SHORT, Locale.ENGLISH);
    private final SimpleDateFormat DATE_FORMAT_PIM_LONG = new SimpleDateFormat(PIM_DATE_LONG_FORMAT, Locale.ENGLISH);

    private static final String NOT_ALPHANUMERIC = "mpnalias.pimimport.regex.notalphanumeric";
    private static final String EMPTY_STRING = StringUtils.EMPTY;

    private static final String ATTRIBUTE_PRODUCT_ID = "ProductID";
    private static final String ATTRIBUTE_TYPE = "Type";
    private static final String METADATA = "MetaData";

    private static final String XP_COMPETITOR_ARTICLENUMBER = "Values/MultiValue[@AttributeID='competitor_articlenumber']/Value";
    private static final String CURATED_PRODUCT_SELECTION = "Values/MultiValue[@AttributeID='curated_product_selection']/Value";
    private static final String XP_ERP_TYP = "Values/Value[@AttributeID='ERP_Typ']";
    private static final String XP_PRODUCT_NAME = "Name";

    private static final String XP_PRODUCT_CROSS_REFERENCE = "ProductCrossReference";
    private static final String XP_PRODUCT_FAMILY_NAME = "Values/ValueGroup[@AttributeID='5547']/Value";
    private static final String XP_PRODUCT_LINE_NAME = "Values/ValueGroup[@AttributeID='titleproductline']/Value";
    private static final String XP_PRODUCT_ID = "@ProductID";
    private static final String XP_PRODUCT_META_DESC = "Values/ValueGroup[@AttributeID='DIS_ProductMetaDescription']/Value";
    private static final String XP_PRODUCT_META_TITLE = "Values/ValueGroup[@AttributeID='DIS_ProductMetaTitle']/Value";
    private static final String XP_MANUFACTURERID = "ProductCrossReference[@Type='Manufacturer']/" + XP_PRODUCT_ID;
    private static final String XP_IMAGE_360 = "ProductCrossReference[@Type='robot360collection']";
    private static final String XP_CLASSIFICATION_REFERENCE = "ClassificationReference[@Type='e-ShopDistrelec']";
    // private static final String XP_CLASSIFICATION_REFERENCE_SEC = "ClassificationReference[@Type='e-ShopDistrelec_sec']";

    private static final String XP_SVHC = "Values/MultiValue[@AttributeID='casno_reach']/ValueGroup/Value";
    private static final String XP_SVHC_USED = "Values/Value[@AttributeID='DIS_REACH_ART_TXT']";
    private static final String XP_SVHC_REVIEWDATE = "Values/Value[@AttributeID='reachreviewdate_law']";
    private static final String XP_NAME = "Values/ValueGroup[@AttributeID='shortdescription_shop_displayed']/Value']";
    private static final String XP_NAME_SHORT = "Values/ValueGroup[@AttributeID='Bezeichnung_text']/Value";
    private static final String XP_DESCRIPTION = "Values/ValueGroup[@AttributeID='longdescription']/Value";
    private static final String XP_PROMOTION_ARTICLE = "Values/ValueGroup[@AttributeID='promotionart_txt']/Value";
    private static final String XP_SEO_ACCESSORY = "Values/ValueGroup[@AttributeID='calc_accessory_yn']/Value";
    private static final String XP_SEO_CAMPAIGN_TAG = "Values/MultiValue[@AttributeID='seo_campaign_tag']/Value']";
    private static final String XP_SEO_KEYWORD_ADDITIONAL = "ValueGroup/MultiValue[@AttributeID='seo_keywords']/ValueGroup/Value";
    private static final String XP_SEO_KEYWORD_MAIN = "Values/ValueGroup[@AttributeID='seo_keywordsA']/Value";
    private static final String XP_SEO_MISSPELLINGS = "Values/MultiValue[@AttributeID='seo_misspellings']/ValueGroup/Value";
    private static final String XP_ERP_EAN_CODE = "Values/Value[@AttributeID='ERP_EANCode']";
    private static final String XP_ERP_ENUMBER_CODE = "Values/Value[@AttributeID='ERP_enumber']";
    private static final String XP_PAPER_CATALOG_PAGE_NUMBER = "Values/ValueGroup[@AttributeID='page_number_cat']/Value";
    private static final String XP_PAPER_CATALOG_PAGE_NUMBER_16_17 = "Values/ValueGroup[@AttributeID='page_number_cat_16_17']/Value";
    private static final String XP_SCIP = "Values/Value[@AttributeID='DIS_DistrelecSCIPNo_TXT']";
    private static final String XP_BETTER_WORLD = "Values/Value[@AttributeID='DIS_Better_World']";

    private static final String XP_AUDIO = "AssetCrossReference[@Type='Audio_mp3']";
    private static final String XP_DATASHEET = "AssetCrossReference[@Type='Datasheet']";
    private static final String XP_IMAGES = "AssetCrossReference[@Type='Primary Image']";
    private static final String XP_VIDEO = "AssetCrossReference[@Type='Video']";
    private static final String XP_PRODUCT_IMAGES = "AssetCrossReference[@Type='Product Image' or @Type='Promotion Image' or @Type='Information Image']";
    private static final String XP_ENERGY_CLASS = "AssetCrossReference[@Type='DIS_Energy_Class']";
    private static final String XP_ASSET_ID = "@AssetID";
    private static final String XP_IMAGES_INHERITED_FROM = "@InheritedFrom";

    private static final String ALTERNATIVE_UPGRADE = "alternative_upgrade";
    private static final String XP_ALTERNATIVE_UPGRADE = "Values/Value[@AttributeID='" + ALTERNATIVE_UPGRADE + "']";
    private static final String ALTERNATIVE_ALIAS_MPN = "alternative_aliasMPN";
    private static final String XP_ALTERNATIVE_ALIAS_MPN = "Values/MultiValue[@AttributeID='" + ALTERNATIVE_ALIAS_MPN + "']/Value";
    private static final String ALTERNATIVE_NS_MPN = "alternative_NSmpn";
    private static final String XP_ALTERNATIVE_NS_MPN = "Values/MultiValue[@AttributeID='" + ALTERNATIVE_NS_MPN + "']/Value";
    private static final String XP_CALIBRATED_PRODUCT = "MetaData/Value[@AttributeID='DIS_CalibrationType']";
    private static final String REEOO_AVAILABLE = "DIS_ReevooAvailable";
    private static final String XP_REEOO_AVAILABLE = "Values/Value[@AttributeID='" + REEOO_AVAILABLE + "']";
    private static final String SIGNAL_WORD = "DIS_SignalWord_TXT";
    private static final String XP_SIGNAL_WORD = "Values/Value[@AttributeID='" + SIGNAL_WORD + "']";
    private static final String MULTI_LINE_DELIMITER = "<br />";
    private static final String MULTI_VALUE_DELIMITER = "|";
    private static final String EMPTY_STR = StringUtils.EMPTY;
    private static final String PIM_BOOLEAN_TRUE = "Y";

    private static final String PRODUCT_ROHS_EXEMPTIONS = "Values/MultiValue[@AttributeID='DIS_RoHS_Exem_TXT']/Value";
    private static final String WEEE_CATEGORY = "Values/ValueGroup[@AttributeID='DIS_ERP_Label_TXT']/Value']";

    public static final List<String> MULTI_LINE_ATTRIBUTE_CODES = Arrays.asList(
            Product.ATTRIBUTE_CODE_FAMILY_DESCRIPTION,
            Product.ATTRIBUTE_CODE_FAMILY_DESCRIPTION_BULLET,
            Product.ATTRIBUTE_CODE_SERIES_DESCRIPTION,
            Product.ATTRIBUTE_CODE_SERIES_DESCRIPTION_BULLET,
            Product.ATTRIBUTE_CODE_ARTICLE_DESCRIPTION,
            Product.ATTRIBUTE_CODE_ARTICLE_DESCRIPTION_BULLET);

    public static final Pattern MULTI_LINE_VALUE_SPLIT_PATTERN = Pattern.compile("#NEWLINE#|<br\\/>");
    private static final Map<String, String> SPECIAL_CHAR_MAP;

    static {
        SPECIAL_CHAR_MAP = new HashMap<>();
        SPECIAL_CHAR_MAP.put("\u00BE", "3/4");
        SPECIAL_CHAR_MAP.put("\u00BD", "1/2");
        SPECIAL_CHAR_MAP.put("\u00BC", "1/4");
        SPECIAL_CHAR_MAP.put("⅓", "1/3");
        SPECIAL_CHAR_MAP.put("¾", "3/4");
        SPECIAL_CHAR_MAP.put("½", "1/2");
        SPECIAL_CHAR_MAP.put("¼", "1/4");
        SPECIAL_CHAR_MAP.put("\u0085", "...");
    }

    @Autowired
    private DistCategoryService distCategoryService;

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistProductService distProductService;

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Autowired
    private ProductCountryElementConverter productCountryElementConverter;

    @Autowired
    private ProductCOPunchOutFilterElementConverter productCOPunchOutFilterElementConverter;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private DistMediaContainerService distMediaContainerService;

    @Autowired
    private ProductReferenceCreator productReferenceCreator;

    @Autowired
    private DistImage360Service image360Service;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DistrelecCodelistService distrelecCodelistService;

    @Autowired
    private ConverterLanguageUtil converterLanguageUtil;

    @Override
    public String getId(final Element element) {
        final String name = element.valueOf(XP_PRODUCT_NAME);
        String productCode = null;
        if (StringUtils.isNotEmpty(name)) {
            productCode = name;
        }

        if (StringUtils.isBlank(productCode)) {
            throw new ElementIdNotFoundException("No product code found for product with PIM-ID [" + element.attributeValue(ATTRIBUTE_ID) + "]");
        }

        return productCode;
    }

    @Override
    public void convert(final Element source, final ProductModel target, final ImportContext importContext, final String hash) {
        convertMaster(source, target, importContext, hash);

        convertLocalized(source, target, importContext, hash);
    }

    /**
     * Convert product master data
     *
     * @param source
     * @param target
     * @param importContext
     * @param hash
     */
    private void convertMaster(final Element source, final ProductModel target, final ImportContext importContext, final String hash) {
        target.setPimId(source.attributeValue(ATTRIBUTE_ID));

        final String manufacturerCode = source.valueOf(XP_MANUFACTURERID);
        if (isNotBlank(manufacturerCode)) {
            try {
                target.setManufacturer(distManufacturerService.getManufacturerByCode(manufacturerCode));
            } catch (final UnknownIdentifierException e) {
                LOG.error("Could not find manufacturer for code [{}] to set manufacturer on product [{}]", manufacturerCode, target.getCode());
            }
        } else {
            target.setManufacturer(null);
        }

        setCategories(source, target, importContext);
        // Once the super categories are set, one calculate the additional code paths
        calculateAdditionalCodePaths(target);
        target.setCodeCompetitorMultivalue(getMultiValueList(source, XP_COMPETITOR_ARTICLENUMBER, MULTI_VALUE_DELIMITER));
        target.setCuratedProductSelection(getMultiValueList(source, CURATED_PRODUCT_SELECTION, MULTI_VALUE_DELIMITER));
        target.setSvhcReviewDate(getDate(source.valueOf(XP_SVHC_REVIEWDATE)));
        final String typeName = source.valueOf(XP_ERP_TYP);
        target.setTypeName(typeName);
        target.setTypeNameSeo(UrlResolverUtils.normalize(typeName, true));
        target.setEan(source.valueOf(XP_ERP_EAN_CODE));
        target.setEnumber(source.valueOf(XP_ERP_ENUMBER_CODE));
        target.setReevooAvailable(isReevooAvailable(source.valueOf(XP_REEOO_AVAILABLE)));
        target.setSignalWord(source.valueOf(XP_SIGNAL_WORD));
        final String alternativeAliasMPN = getAlternativeAliasMPN(source.selectNodes(XP_ALTERNATIVE_ALIAS_MPN));
        target.setAlternativeAliasMPN(alternativeAliasMPN);
        target.setIsBetterWorld(StringUtils.equals("Y", source.valueOf(XP_BETTER_WORLD)));

        final String normalisedAlternativeAliasMPN = getNormalisedMPNValue(alternativeAliasMPN);
        target.setNormalizedAlternativeAliasMPN(normalisedAlternativeAliasMPN);

        // Converting Medias
        convertPrimaryImages(source, target, importContext);
        convertProductImages(source, target, importContext);
        convertEnergyEfficiencyImages(source, target, importContext);
        convertAudios(source, target, importContext);
        convertDatasheets(source, target, importContext);
        convertVideo(source, target, importContext);
        convertProductReferences(source, target, importContext, hash);
        convertImage360(source, target, importContext);

        // Convert MPN alternatives
        convertAlternativesMPN(source, target);

        // RoHS exemptions
        target.setRohsExemptions(getMultiValueListForRohsExemptions(source, PRODUCT_ROHS_EXEMPTIONS));

        productCountryElementConverter.convert(source, target);
        productCOPunchOutFilterElementConverter.convert(source, target);
    }

    private boolean isReevooAvailable(String isReevooAvailable) {
        if (isReevooAvailable != null && PIM_BOOLEAN_TRUE.equalsIgnoreCase(isReevooAvailable)) {
            return true;
        }
        return false;
    }

    private String getNormalisedMPNValue(final String mpnValue) {
        if (StringUtils.isNotEmpty(mpnValue)) {
            final String nonAlphaNumbericRegex = configurationService.getConfiguration().getString(NOT_ALPHANUMERIC);
            if (StringUtils.isNotEmpty(nonAlphaNumbericRegex)) {
                return mpnValue.replaceAll(nonAlphaNumbericRegex, EMPTY_STRING);
            }
        }
        return EMPTY_STRING;
    }

    private String getAlternativeAliasMPN(final List<Node> alternativeAliasMPNFromPIM) {
        if (CollectionUtils.isNotEmpty(alternativeAliasMPNFromPIM)) {
            return getMPNValues(alternativeAliasMPNFromPIM);
        }
        return EMPTY_STRING;
    }

    private String getMPNValues(final List<Node> npmNodes) {
        return npmNodes.stream().map(Node::getText).filter(Objects::nonNull).collect(Collectors.joining(MULTI_VALUE_DELIMITER));
    }

    /**
     * Convert the MPN alternatives
     *
     * @param source the XML source element
     * @param target the target product
     */
    private void convertAlternativesMPN(final Element source, final ProductModel target) {
        final Map<Boolean, StringBuilder> mpnMap = new HashMap<>();
        mpnMap.put(Boolean.TRUE, new StringBuilder());
        mpnMap.put(Boolean.FALSE, new StringBuilder());

        processNodes(source.selectNodes(XP_ALTERNATIVE_NS_MPN), mpnMap);
        // Set the values of on the target product.
        target.setNormalizedAlternativesMPN(getNormalisedMPNValue(mpnMap.get(Boolean.TRUE).toString()));

        target.setAlternativesMPN(StringUtils.stripToNull(mpnMap.get(Boolean.FALSE).toString()));
    }

    /**
     * Concatenate the value of each node to the String builder using the delimiter '|'
     *
     * @param nodes  the source list of nodes
     * @param mpnMap the target string builders
     */
    private void processNodes(final List<Node> nodes, final Map<Boolean, StringBuilder> mpnMap) {
        if (CollectionUtils.isEmpty(nodes) || mpnMap == null) {
            return;
        }

        // Normalized version
        final StringBuilder normalizedMPN = mpnMap.computeIfAbsent(Boolean.TRUE, (key) -> new StringBuilder());
        normalizedMPN.append(normalizedMPN.length() > 0 ? MULTI_VALUE_DELIMITER : EMPTY_STR) // append the '|' if it is non empty
                .append(nodes.stream().filter(Objects::nonNull).map(node -> node.getText().replaceAll(NOT_ALPHANUMERIC, StringUtils.EMPTY))
                        .collect(Collectors.joining(MULTI_VALUE_DELIMITER)));
        // Non normalized version
        final StringBuilder sbn = mpnMap.computeIfAbsent(Boolean.FALSE, (key) -> new StringBuilder());
        sbn.append(sbn.length() > 0 ? MULTI_VALUE_DELIMITER : EMPTY_STR) // append the '|' if it is non empty
                .append(nodes.stream().map(Node::getText).collect(Collectors.joining(MULTI_VALUE_DELIMITER)));
    }

    /**
     * Convert the localized attributes of the product
     *
     * @param source
     * @param target
     * @param importContext
     * @param hash
     */
    private void convertLocalized(final Element source, final ProductModel target, final ImportContext importContext, final String hash) {
        final String productCode = target.getCode();

        Map<Locale, String> nameValues = converterLanguageUtil.getLocalizedValues(source, XP_NAME);

        nameValues.forEach((locale, value) -> {
            final String name = truncate(value, MAX_COLUMN_LENGTH_PRODUCT_NAME, productCode, XP_NAME);
            target.setName(replacingForFF(name), locale);
            target.setNameSeo(UrlResolverUtils.normalize(name, true), locale);
        });

        Map<Locale, String> seoProductTitleValues = converterLanguageUtil.getLocalizedValues(source, XP_PRODUCT_META_TITLE);
        seoProductTitleValues.forEach((locale, value) -> {
            target.setSeoMetaTitle(truncate(value, MAX_COLUMN_LENGTH_DEFAULT, productCode, XP_PRODUCT_META_TITLE), locale);
        });

        Map<Locale, String> seoProductDescValues = converterLanguageUtil.getLocalizedValues(source, XP_PRODUCT_META_DESC);
        seoProductDescValues.forEach((locale, value) -> {
            target.setSeoMetaDescription(truncate(value, MAX_COLUMN_LENGTH_DEFAULT, productCode, XP_PRODUCT_META_DESC), locale);
        });

        // Used for FF export.
        Map<Locale, String> shortNameValues = converterLanguageUtil.getLocalizedValues(source, XP_NAME_SHORT);
        shortNameValues.forEach((locale, value) -> {
            target.setNameShort(replacingForFF(truncate(value, MAX_COLUMN_LENGTH_DEFAULT, productCode, XP_NAME_SHORT)), locale);
        });

        Map<Locale, String> descriptionValues = converterLanguageUtil.getLocalizedValues(source, XP_DESCRIPTION);
        descriptionValues.forEach((locale, value) -> {
            final String description = StringUtils.replace(value, "#NEWLINE#", "<br />");
            target.setDescription(truncate(description, 1000, productCode, XP_DESCRIPTION), locale);
        });

        Map<Locale, String> seoKeywordAdditionalMultivalue = converterLanguageUtil.getLocalizedValues(source, XP_SEO_KEYWORD_ADDITIONAL);
        seoKeywordAdditionalMultivalue.forEach((locale, value) -> {
            target.setSeoKeywordAdditionalMultivalue(truncate(value, 500, productCode, XP_SEO_KEYWORD_ADDITIONAL), locale);
        });

        Map<Locale, String> seoKeywordMainValues = converterLanguageUtil.getLocalizedValues(source, XP_SEO_KEYWORD_MAIN);
        seoKeywordMainValues.forEach((locale, value) -> {
            target.setSeoKeywordMain(value, locale);
        });

        Map<Locale, String> seoMisspellingMultivalue = getLocalizedMultiValues(source, XP_SEO_MISSPELLINGS);
        seoMisspellingMultivalue.forEach((locale, value) -> {
            target.setSeoMisspellingMultivalue(truncate(value, MAX_COLUMN_LENGTH_DEFAULT, productCode, XP_SEO_MISSPELLINGS), locale);
        });

        Map<Locale, String> productFamilyName = converterLanguageUtil.getLocalizedValues(source, XP_PRODUCT_FAMILY_NAME);
        productFamilyName.forEach((locale, value) -> {
            target.setProductFamilyName(truncate(value, MAX_COLUMN_LENGTH_DEFAULT, productCode, XP_PRODUCT_FAMILY_NAME), locale);
        });

        Map<Locale, String> productLineName = converterLanguageUtil.getLocalizedValues(source, XP_PRODUCT_LINE_NAME);
        productLineName.forEach((locale, value) -> {
            target.setProductLineName(source.valueOf(XP_PRODUCT_LINE_NAME), locale);
        });

        Map<Locale, String> promotionText = converterLanguageUtil.getLocalizedValues(source, XP_PROMOTION_ARTICLE);
        promotionText.forEach((locale, value) -> {
            target.setPromotionText(value, locale);
        });

        Map<Locale, String> seoAccessory = converterLanguageUtil.getLocalizedValues(source, XP_SEO_ACCESSORY);
        seoAccessory.forEach((locale, value) -> {
            target.setSeoAccessory(value, locale);
        });

        Map<Locale, String> svhc = getLocalizedMultiValues(source, XP_SVHC);
        svhc.forEach((locale, value) -> {
            target.setSvhc(value, locale);
        });

        Map<Locale, String> paperCatalogPageNumber = getLocalizedMultiValues(source, XP_PAPER_CATALOG_PAGE_NUMBER);
        paperCatalogPageNumber.forEach((locale, value) -> {
            target.setPaperCatalogPageNumber(value, locale);
        });

        Map<Locale, String> paperCatalogPageNumber_16_17 = getLocalizedMultiValues(source, XP_PAPER_CATALOG_PAGE_NUMBER_16_17);
        paperCatalogPageNumber_16_17.forEach((locale, value) -> {
            target.setPaperCatalogPageNumber_16_17(value, locale);
        });

        Map<Locale, String> weee_categories = converterLanguageUtil.getLocalizedValues(source, WEEE_CATEGORY);
        weee_categories.forEach((locale, value) -> {
            target.setWeeeCategory(value, locale);
        });

        Node svhcUsedNode = source.selectSingleNode(XP_SVHC_USED);
        String svhcUsed = svhcUsedNode != null ? svhcUsedNode.getText() : null;
        if (StringUtils.isNotEmpty(svhcUsed)) {
            if (svhcUsed.equals("Y")) {
                target.setHasSvhc(true);
            }
            if (svhcUsed.equals("N")) {
                target.setHasSvhc(false);
            }
        }

        Node scipNode = source.selectSingleNode(XP_SCIP);
        String scip = scipNode != null ? scipNode.getText() : null;
        if (StringUtils.isNotEmpty(scip)) {
            target.setScip(scip);
        }

        // Calculate the additional paths
        //TODO: check if needed
        //calculateAdditionalPaths(target, importContext.getLocale());

        if (!importContext.getProductHashesToSaveLater().containsKey(target.getCode())) {
            target.setPimXmlHashMaster(hash);
            target.setPimHashTimestamp(new Date());
        }
    }

    private Map<Locale, String> getLocalizedMultiValues(final Element source, final String xpathString) {
        Map<Locale, String> ret = new HashMap<>();

        final XPath xpath = source.createXPath(xpathString);

        final List<Element> values = xpath.selectNodes(source);
        if (values != null) {
            for (final Element element : values) {
                Locale locale = converterLanguageUtil.getLocaleForElement(element);
                if (ret.containsKey(locale)) {
                    ret.put(locale, ret.get(locale).concat(MULTI_VALUE_DELIMITER).concat(element.getTextTrim()));
                } else {
                    ret.put(locale, element.getTextTrim());
                }
            }
        }

        return ret;
    }

    /**
     * Sets the super categories of the specified target product
     *
     * @param source
     * @param target
     * @param importContext
     */
    private void setCategories(final Element source, final ProductModel target, final ImportContext importContext) {

        // Set the primary category first
        setPrimaryCategory(source, target, importContext);
        // Then set the super categories. The primary category is also part of super categories
        // final List<CategoryModel> parents = getSuperCategories(source, target, importContext, XP_CLASSIFICATION_REFERENCE_SEC, false);
        final List<CategoryModel> parents = getSuperCategories(source, target, importContext, XP_CLASSIFICATION_REFERENCE, false);

        // if (target.getPrimarySuperCategory() != null) {
        // parents.add(target.getPrimarySuperCategory());
        // }

        target.setSupercategories(parents);

        // Setting the PIM Family category code
        target.setPimFamilyCategoryCode(source.attributeValue("ParentID"));
    }

    /**
     * Set the primary category to the product
     *
     * @param source        the source XML element
     * @param target
     * @param importContext
     */
    private void setPrimaryCategory(final Element source, final ProductModel target, final ImportContext importContext) {

        final List<CategoryModel> categories = getSuperCategories(source, target, importContext, XP_CLASSIFICATION_REFERENCE, true);

        if (CollectionUtils.isNotEmpty(categories)) {
            target.setPrimarySuperCategory(categories.get(0));
        } else {
            LOG.error("Could not determine primary category of product with code [{}]. Parent id is empty.", target.getCode());
        }
    }

    /**
     * Parse the XML element and find the super categories matching the {@code xPath}
     *
     * @param source        the source XML element
     * @param target        the target product
     * @param importContext the PIM import context
     * @param xPath         the XPath of the sub-elements
     * @param peekFirst     a boolean flag telling whether we should stop after finding the first element or not.
     * @return a list of {@link CategoryModel}
     */
    private List<CategoryModel> getSuperCategories(final Element source, final ProductModel target, final ImportContext importContext, final String xPath,
            final boolean peekFirst) {

        final List<CategoryModel> parents = new ArrayList<>();
        final List<Node> classificationReference = source.selectNodes(xPath);
        if (CollectionUtils.isEmpty(classificationReference)) {
            return parents;
        }

        for (final Node categoryReference : classificationReference) {
            final List<Node> idNodes = categoryReference.selectNodes("@ClassificationID");
            if (CollectionUtils.isEmpty(idNodes)) {
                continue;
            }

            final String parentCategoryCode =
                    importContext.getCategoryCodePrefix() + idNodes.get(0).getStringValue().replace(importContext.getCategoryCodeSuffix(), EMPTY_STR);
            try {
                // Lookup for the parent category
                final CategoryModel parentCategory = distCategoryService.getCategoryForCode(parentCategoryCode);
                parents.add(getVisibleCategory(parentCategory));
            } catch (final UnknownIdentifierException e) {
                LOG.error("Could not find category for code [" + (importContext.getCategoryCodePrefix() + parentCategoryCode) + "]", e);
            }

            if (peekFirst && !parents.isEmpty()) {
                break;
            }
        }

        if (CollectionUtils.isEmpty(parents)) {
            LOG.error("Could not determine parent categories of product with code [{}]. Parent id is empty.", target.getCode());
        }

        return parents;
    }

    /**
     * Calculate the additional paths based on super category codes.
     *
     * @param target the target product
     */
    private void calculateAdditionalCodePaths(final ProductModel target) {
        final Collection<CategoryModel> superCats = CollectionUtils.select(target.getSupercategories(), object -> object.getClass() == CategoryModel.class);

        if (CollectionUtils.isEmpty(superCats)) {
            return;
        }

        final StringBuilder sb = new StringBuilder();

        for (final CategoryModel category : superCats) {
            buildPath(category, sb.append('|'), null);
        }

        if (sb.length() > 1) {
            target.setAdditionalPaths(sb.append('|').toString());
        }
    }

    /**
     * Calculate the additional paths based on super category localized names.
     *
     * @param target
     * @param locale
     */
    protected void calculateAdditionalPaths(final ProductModel target, final Locale locale) {
        final Collection<CategoryModel> superCats = CollectionUtils.select(target.getSupercategories(), object -> object.getClass() == CategoryModel.class);

        if (CollectionUtils.isEmpty(superCats)) {
            return;
        }

        final StringBuilder sb = new StringBuilder();

        for (final CategoryModel category : superCats) {
            buildPath(category, sb.append('|'), locale);
        }

        if (sb.length() > 1) {
            target.setAdditionalNamePaths(sb.append('|').toString(), locale);
        }
    }

    /**
     * Build the category path for the specified category. If the locale is not null then the localized name is used, otherwise the category
     * code is used.
     *
     * @param category the target category
     * @param sb
     * @param locale   the target locale (must be null in case category code should be used)
     */
    private void buildPath(final CategoryModel category, final StringBuilder sb, final Locale locale) {
        if (category == null || sb == null || category.getLevel() == null || category.getLevel() < 1) {
            return;
        }

        final CategoryModel superCat = (CategoryModel) CollectionUtils.find(category.getSupercategories(), object -> object.getClass() == CategoryModel.class);
        // Append first the parent code
        buildPath(superCat, sb, locale);

        sb.append(category.getLevel() > 1 ? "/" : StringUtils.EMPTY).append(locale == null ? category.getCode() : category.getNameSeo(locale));
    }

    /**
     * Look for the closest visible category from the category tree.
     *
     * @param category
     * @return the closest visible category from the category tree
     */
    private CategoryModel getVisibleCategory(final CategoryModel category) {
        if (category.getPimCategoryType() != null && BooleanUtils.isTrue(category.getPimCategoryType().getVisible())) {
            return category;
        }

        final List<CategoryModel> superCategories = distCategoryService.getProductSuperCategories(category);
        if (CollectionUtils.isEmpty(superCategories)) {
            return category;
        }

        return getVisibleCategory(superCategories.get(0));
    }

    /**
     * @param source
     * @param target
     * @param importContext
     */
    private void convertPrimaryImages(final Element source, final ProductModel target, final ImportContext importContext) {
        MediaContainerModel mediaContainerPrimary = null;
        MediaContainerModel mediaContainerIllustrative = null;

        final List<Node> imageNodes = source.selectNodes(XP_IMAGES);
        if (CollectionUtils.isNotEmpty(imageNodes)) {
            final Iterator<Node> iterator = imageNodes.iterator();
            while (iterator.hasNext()) {
                final Node imageNode = iterator.next();
                final List<Node> assetIdNodes = imageNode.selectNodes(XP_ASSET_ID);
                if (CollectionUtils.isNotEmpty(assetIdNodes)) {
                    final String mediaContainerQualifier = assetIdNodes.get(0).getStringValue();
                    final List<Node> inheritedFromNodes = imageNode.selectNodes(XP_IMAGES_INHERITED_FROM);
                    final boolean isPrimary = CollectionUtils.isEmpty(inheritedFromNodes);
                    if (isPrimary && mediaContainerPrimary == null) {
                        mediaContainerPrimary = getMediaContainerForQualifier(importContext.getProductCatalogVersion(), mediaContainerQualifier);
                    } else if (!isPrimary && mediaContainerIllustrative == null) {
                        mediaContainerIllustrative = getMediaContainerForQualifier(importContext.getProductCatalogVersion(), mediaContainerQualifier);
                    } else {
                        break;
                    }
                }
            }
        }

        target.setPrimaryImage(mediaContainerPrimary);
        target.setIllustrativeImage(mediaContainerIllustrative);
    }

    /**
     * @param source
     * @param target
     * @param importContext
     */
    private void convertProductImages(final Element source, final ProductModel target, final ImportContext importContext) {
        final List<MediaContainerModel> productImages = new ArrayList<>();

        final List<Node> imageNodes = source.selectNodes(XP_PRODUCT_IMAGES);
        if (CollectionUtils.isNotEmpty(imageNodes)) {
            final Iterator<Node> iterator = imageNodes.iterator();
            while (iterator.hasNext()) {
                final Node imageNode = iterator.next();
                final List<Node> assetIdNodes = imageNode.selectNodes(XP_ASSET_ID);
                if (CollectionUtils.isNotEmpty(assetIdNodes)) {
                    final String mediaContainerQualifier = assetIdNodes.get(0).getStringValue();
                    final List<Node> inheritedFromNodes = imageNode.selectNodes(XP_IMAGES_INHERITED_FROM);
                    final boolean isNotInherited = CollectionUtils.isEmpty(inheritedFromNodes);
                    if (isNotInherited) {
                        final MediaContainerModel productImage = getMediaContainerForQualifier(importContext.getProductCatalogVersion(),
                                mediaContainerQualifier);
                        if (productImage != null) {
                            productImages.add(productImage);
                        }
                    }
                }
            }
        }

        target.setGalleryImages(productImages);
    }

    /**
     * Converts the EEL (Energy Efficiency Label) images and places them in a separate container.
     *
     * @param source The element representing DIS_Energy_Class
     * @param target The product
     * @param importContext Impex import context
     */
    private void convertEnergyEfficiencyImages(Element source, ProductModel target, ImportContext importContext) {
        Node eelNode = source.selectSingleNode(XP_ENERGY_CLASS);
        if (eelNode != null) {
            Node assetIdNode = eelNode.selectSingleNode(XP_ASSET_ID);
            if (assetIdNode != null && isNotBlank(assetIdNode.getStringValue())) {
                MediaContainerModel eelImage = getMediaContainerForQualifier(importContext.getProductCatalogVersion(),
                                                                             assetIdNode.getStringValue());
                if (eelImage != null) {
                    target.setEnergyEfficiencyLabelImage(eelImage);
                }
            }
        }
    }

    /**
     * @param source
     * @param target
     * @param importContext
     */
    private void convertAudios(final Element source, final ProductModel target, final ImportContext importContext) {
        final List<DistAudioMediaModel> audios = new ArrayList<>();

        final List<Node> audioNodes = source.selectNodes(XP_AUDIO);
        if (CollectionUtils.isEmpty(audioNodes)) {
            target.setAudioMedias(null);
            return;
        }

        final Iterator<Node> iterator = audioNodes.iterator();
        while (iterator.hasNext()) {
            final Node audioNode = iterator.next();
            final List<Node> assetIdNodes = audioNode.selectNodes(XP_ASSET_ID);
            final DistAudioMediaModel audio;
            if (CollectionUtils.isEmpty(assetIdNodes)
                    || (audio = getAudioMediaForQualifier(importContext.getProductCatalogVersion(), assetIdNodes.get(0).getStringValue())) == null) {
                continue;
            }

            audios.add(audio);
            final List<ProductModel> products = audio.getProducts() == null ? new ArrayList<>() : new ArrayList<>(audio.getProducts());
            if (!products.contains(target)) {
                products.add(target);
                audio.setProducts(products);
                modelService.save(audio);
            }
        }

        target.setAudioMedias(audios);
    }

    /**
     * Look for the audio media from the datastore.
     *
     * @param productCatalogVersion
     * @param audioMediaQualifier
     * @return {@link DistAudioMediaModel}
     */
    private DistAudioMediaModel getAudioMediaForQualifier(final CatalogVersionModel productCatalogVersion, final String audioMediaQualifier) {
        if (isNotBlank(audioMediaQualifier)) {
            try {
                final MediaModel mediaModel = mediaService.getMedia(productCatalogVersion, audioMediaQualifier);
                if (mediaModel instanceof DistAudioMediaModel) {
                    return (DistAudioMediaModel) mediaModel;
                } else {
                    LOG.error("Could not convert Media to AudioMedia for qualifier [{}]", audioMediaQualifier);
                }
            } catch (final UnknownIdentifierException e) {
                LOG.debug("Could not find AudioMedia for qualifier [{}] and catalog version [{}]: {}", audioMediaQualifier, productCatalogVersion,
                          e.getMessage());
            }
        }

        return null;
    }

    /**
     * @param source
     * @param target
     * @param importContext
     */
    private void convertDatasheets(final Element source, final ProductModel target, final ImportContext importContext) {
        final Set<DistDownloadMediaModel> datasheets = new HashSet<>();

        final List<Node> imageNodes = source.selectNodes(XP_DATASHEET);
        if (CollectionUtils.isEmpty(imageNodes)) {
            target.setDownloadMedias(null);
            return;
        }

        final Iterator<Node> iterator = imageNodes.iterator();
        while (iterator.hasNext()) {
            final Node imageNode = iterator.next();
            final List<Node> assetIdNodes = imageNode.selectNodes(XP_ASSET_ID);
            final DistDownloadMediaModel datasheet;
            if (CollectionUtils.isEmpty(assetIdNodes)
                    || (datasheet = getDownloadMediaForQualifier(importContext.getProductCatalogVersion(), assetIdNodes.get(0).getStringValue())) == null) {
                continue;
            }

            datasheets.add(datasheet);
            final Set<ProductModel> products = datasheet.getProducts() == null ? new HashSet<>() : new HashSet<>(datasheet.getProducts());
            if (!products.contains(target)) {
                products.add(target);
                datasheet.setProducts(products);
                modelService.save(datasheet);
            }
        }

        target.setDownloadMedias(datasheets);
    }

    /**
     * Look for the download media from the datastore.
     *
     * @param productCatalogVersion
     * @param downloadMediaQualifier
     * @return {@link DistDownloadMediaModel}
     */
    private DistDownloadMediaModel getDownloadMediaForQualifier(final CatalogVersionModel productCatalogVersion, final String downloadMediaQualifier) {
        if (isNotBlank(downloadMediaQualifier)) {
            try {
                final MediaModel mediaModel = mediaService.getMedia(productCatalogVersion, downloadMediaQualifier);
                if (mediaModel instanceof DistDownloadMediaModel) {
                    return (DistDownloadMediaModel) mediaModel;
                } else {
                    LOG.error("Could not convert Media to DownloadMedia for qualifier [{}]", downloadMediaQualifier);
                }
            } catch (final UnknownIdentifierException e) {
                LOG.debug("Could not find DownloadMedia for qualifier [{}] and catalog version [{}]: {}", downloadMediaQualifier, productCatalogVersion,
                        e.getMessage());
            }
        }

        return null;
    }

    /**
     * @param source
     * @param target
     * @param importContext
     */
    private void convertVideo(final Element source, final ProductModel target, final ImportContext importContext) {
        final Set<DistVideoMediaModel> videos = new LinkedHashSet<>();

        final List<Node> videoNodes = source.selectNodes(XP_VIDEO);
        if (CollectionUtils.isNotEmpty(videoNodes)) {
            final Iterator<Node> iterator = videoNodes.iterator();
            while (iterator.hasNext()) {
                final Node videoNode = iterator.next();
                final List<Node> assetIdNodes = videoNode.selectNodes(XP_ASSET_ID);
                if (CollectionUtils.isNotEmpty(assetIdNodes)) {
                    final String videoQualifier = assetIdNodes.get(0).getStringValue();
                    final DistVideoMediaModel video = getVideoMediaForQualifier(importContext.getProductCatalogVersion(), videoQualifier);
                    if (video != null) {
                        videos.add(video);
                    }
                }
            }
        }

        target.setVideoMedias(videos);
    }

    /**
     * @param source
     * @param target
     * @param importContext
     */
    private void convertImage360(final Element source, final ProductModel target, final ImportContext importContext) {
        final Set<DistImage360Model> images360 = new LinkedHashSet<>();
        final List<Node> image360Nodes = source.selectNodes(XP_IMAGE_360);
        if (image360Nodes != null) {
            for (final Node image360Node : image360Nodes) {
                final String image360Code = image360Node.valueOf(XP_PRODUCT_ID);
                try {
                    final DistImage360Model image360Model = image360Service.getImage360(importContext.getProductCatalogVersion(), image360Code);
                    images360.add(image360Model);
                } catch (final UnknownIdentifierException e) {
                    LOG.debug("Could not find Image360 for code [{}]", image360Code);
                }
            }
        }
        target.setImages360(images360);
    }

    /**
     * Look for the video media from the database.
     *
     * @param productCatalogVersion
     * @param videoQualifier
     * @return {@link DistVideoMediaModel}
     */
    private DistVideoMediaModel getVideoMediaForQualifier(final CatalogVersionModel productCatalogVersion, final String videoQualifier) {
        if (isNotBlank(videoQualifier)) {
            try {
                final MediaModel mediaModel = mediaService.getMedia(productCatalogVersion, videoQualifier);
                if (mediaModel instanceof DistVideoMediaModel) {
                    return (DistVideoMediaModel) mediaModel;
                } else {
                    LOG.error("Could not convert Media to VideoMedia for qualifier [{}]", productCatalogVersion);
                }
            } catch (final UnknownIdentifierException e) {
                LOG.debug("Could not find VideoMedia for qualifier [{}] and catalog version [{}]: {}", videoQualifier, productCatalogVersion, e.getMessage());
            }
        }

        return null;
    }

    /**
     * Look for the media container from the database.
     *
     * @param productCatalogVersion
     * @param mediaContainerQualifier
     * @return {@link MediaContainerModel}
     */
    private MediaContainerModel getMediaContainerForQualifier(final CatalogVersionModel productCatalogVersion, final String mediaContainerQualifier) {
        if (isNotBlank(mediaContainerQualifier)) {
            try {
                return distMediaContainerService.getMediaContainerForQualifier(productCatalogVersion, mediaContainerQualifier);
            } catch (final UnknownIdentifierException e) {
                LOG.error("Could not find MediaContainer for qualifier [{}] and catalog version [{}]: {}", mediaContainerQualifier, productCatalogVersion,
                        e.getMessage());
            }
        }

        return null;
    }

    /**
     * @param source
     * @param target
     * @param importContext
     * @param hash
     */
    private void convertProductReferences(final Element source, final ProductModel target, final ImportContext importContext, final String hash) {
        final Collection<ProductReferenceModel> productReferencesOld = target.getProductReferences();
        final List<ProductReferenceModel> productReferencesNew = new ArrayList<>();

        final List<Element> referenceElements = source.selectNodes(XP_PRODUCT_CROSS_REFERENCE);
        for (final Element element : referenceElements) {
            DistReplacementReasonModel replaceReason = null;
            final String stiboCode = element.attributeValue(ATTRIBUTE_TYPE);
            final PimProductReferenceType type = PimProductReferenceType.getByStiboCode(stiboCode);
            if (type == null) {
                continue;
            }
            if (type.equals(PimProductReferenceType.ALTERNATIVECALIBRATED)) {
                target.setCalibrationService(Boolean.TRUE);

                final String display = element.valueOf(XP_CALIBRATED_PRODUCT);
                String replacementReasonCode = null;
                if ("NonCal".equalsIgnoreCase(display)) {
                    replacementReasonCode = NOT_CALIBRATED_INSTRUMENTS;
                    target.setCalibrated(true);
                } else if ("Cal".equalsIgnoreCase(display)) {
                    replacementReasonCode = CALIBRATED_INSTRUMENTS;
                    target.setCalibrated(false);
                } else {
                    LOG.warn("Unable to resolve replacement reason for product {} : {}", target.getCode(), display);
                }

                if (replacementReasonCode != null) {
                    replaceReason = distrelecCodelistService.getDistrelecReplacementReason(replacementReasonCode);
                }
            }
            final String targetPimId = element.attributeValue(ATTRIBUTE_PRODUCT_ID);

            final PimProductReferenceDto productReferenceDto = new PimProductReferenceDto();
            productReferenceDto.setSourceCode(target.getCode());
            productReferenceDto.setTargetPimId(targetPimId);
            productReferenceDto.setProductReferenceType(type.getProductReferenceType());
            productReferenceDto.setReplacementReason(replaceReason);

            copyOrCreateProductReference(target, productReferencesOld, productReferencesNew, productReferenceDto, importContext, hash);
        }

        target.setProductReferences(productReferencesNew);
    }

    /**
     * @param sourceProduct
     * @param productReferencesOld
     * @param productReferencesNew
     * @param productReferenceDto
     * @param importContext
     * @param hash
     */
    private void copyOrCreateProductReference(final ProductModel sourceProduct, final Collection<ProductReferenceModel> productReferencesOld,
            final List<ProductReferenceModel> productReferencesNew, final PimProductReferenceDto productReferenceDto, final ImportContext importContext,
            final String hash) {

        final ProductReferenceModel productReference = getOrCreateProductReference(sourceProduct, productReferencesOld, productReferenceDto);
        if (productReference == null) {
            // Create new ProductReference later when all products are imported
            importContext.getProductReferenceDtos().add(productReferenceDto);
            // Remember Hash of product to save later when ProductReference was created
            importContext.getProductHashesToSaveLater().put(sourceProduct.getCode(), hash);
        } else {
            productReferencesNew.add(productReference);
        }
    }

    /**
     * @param sourceProduct
     * @param productReferences
     * @param productReferenceDto
     * @return {@link ProductReferenceModel}
     */
    private ProductReferenceModel getOrCreateProductReference(final ProductModel sourceProduct, final Collection<ProductReferenceModel> productReferences,
            final PimProductReferenceDto productReferenceDto) {

        if (productReferences != null) {
            final ProductReferenceModel productReference = productReferences.stream()
                    .filter(pRef -> StringUtils.equals(pRef.getTarget().getPimId(), productReferenceDto.getTargetPimId()) && productReferenceDto
                            .getProductReferenceType().equals(pRef.getReferenceType())).findFirst().orElse(null);
            if (productReference != null) {
                if (!Objects.equals(productReference.getReplacementReason(), productReferenceDto.getReplacementReason())) {
                    // update replacement reason if they are not equals
                    productReference.setReplacementReason(productReferenceDto.getReplacementReason());
                    modelService.save(productReference);
                }
                return productReference;
            }
        }

        try {
            // Try to get target product
            final ProductModel targetProduct = distProductService.getProductForPimId(productReferenceDto.getTargetPimId());
            return productReferenceCreator.create(productReferenceDto, sourceProduct, targetProduct);
        } catch (final UnknownIdentifierException e) {
            // May be target product will be updated with PIM-ID later during PIM import
            LOG.debug("Product with PIM-ID [{}] not found. ProductReference to this product will be created later.", productReferenceDto.getTargetPimId());
        } catch (final AmbiguousIdentifierException e) {
            // May be target product will be updated with PIM-ID later during PIM import
            LOG.error("Product with PIM-ID [{}] could not be updated", productReferenceDto.getTargetPimId(), e);
        }

        return null;
    }

    private String replacingForFF(String value) {
        if (isNotBlank(value)) {
            for (Map.Entry<String, String> entry : SPECIAL_CHAR_MAP.entrySet()) {
                if (value.contains(entry.getKey())) {
                    value = value.replace(entry.getKey(), entry.getValue());
                }
            }
        }
        return value;
    }

    private String getMultiValueList(final Element source, final String xpathString, final String delimiter) {

        final XPath xpath = source.createXPath(xpathString);
        final List<String> resultList = new ArrayList<>();
        final List<Element> valuesXX = xpath.selectNodes(source);
        if (valuesXX != null) {
            for (final Element element : valuesXX) {
                resultList.add(element.getTextTrim());
            }
        }

        return StringUtils.join(resultList, delimiter);
    }

    private List<String> getMultiValueListForRohsExemptions(final Element source, final String xpathString) {

        final XPath xpath = source.createXPath(xpathString);
        final List<String> resultList = new ArrayList<>();
        final List<Element> valuesXX = xpath.selectNodes(source);
        if (valuesXX != null) {
            for (final Element element : valuesXX) {
                if (isNotBlank(element.getTextTrim())) {
                    // https://jira.distrelec.com/browse/DISTRELEC-34395
                    // In case of getTextTrim returning a blank value, RoHS exemptions on Product model will also be blank/null
                    // In case of having a list of exemptions as the value of getTextTrim, those exemptions will be stored on the Product model
                    // In case of "no exemptions" as a value of getTextTrim, the actual text "no exemptions" will be stored on the Product model
                    resultList.add(element.getTextTrim());
                }
            }
        }

        return resultList;
    }

    private Date getDate(final String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        }

        try {
            return DATE_FORMAT_PIM_LONG.parse(dateString);
        } catch (final ParseException e) {
            // Try short format...
            try {
                return DATE_FORMAT_PIM_SHORT.parse(dateString);
            } catch (final ParseException pe) {
                LOG.error("Date parse error. Value [{}] is neither a valid date of format [{}] nor a valid date of format [{}]", dateString,
                        DATE_FORMAT_PIM_SHORT.toPattern(), DATE_FORMAT_PIM_LONG.toPattern());
                return null;
            }
        }
    }
}
