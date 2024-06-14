package com.namics.distrelec.b2b.facades.compliance.impl;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.COMMA_COUNTRIES;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.BATTERY_COMPLIANCE;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.CONFLICT_MINERAL;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.DISPOSAL_OF_PACKAGING_WASTE;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.ROHS;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.ROHS_RND;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.SVHC_NO;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.SVHC_NO_RND;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.SVHC_YES;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.SVHC_YES_RND;
import static com.namics.distrelec.b2b.facades.compliance.impl.DefaultDistComplianceFacade.ComplianceDocumentType.WEEE;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.pdf.data.DistPdfData;
import com.namics.distrelec.b2b.core.pdf.helper.DistPDFNumberFormatHelper;
import com.namics.distrelec.b2b.core.pdf.service.DistPDFService;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;
import com.namics.distrelec.b2b.facades.compliance.DistComplianceFacade;
import com.namics.distrelec.b2b.facades.helper.DistLogoUrlHelper;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.i18n.CommerceCommonI18NService;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * DefaultDistComplianceFacade
 */
public class DefaultDistComplianceFacade implements DistComplianceFacade {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistComplianceFacade.class);

    private static final String DATE_PATTERN = "dd/MM/yyyy";

    private static final String PRODUCT_ROHS_XSL_NAME = "product-rohs.xsl";

    private static final String PRODUCT_ROHS_VM_NAME = "product-rohs.vm";

    private static final String PRODUCT_RND_ROHS_XSL_NAME = "product-rohs-rnd.xsl";

    private static final String PRODUCT_RND_ROHS_VM_NAME = "product-rohs-rnd.vm";

    private static final String PRODUCT_RND_NO_SVHC_XSL_NAME = "product-no-svhc-rnd.xsl";

    private static final String PRODUCT_RND_NO_SVHC_VM_NAME = "product-no-svhc-rnd.vm";

    private static final String PRODUCT_RND_YES_SVHC_XSL_NAME = "product-yes-svhc-rnd.xsl";

    private static final String PRODUCT_RND_YES_SVHC_VM_NAME = "product-yes-svhc-rnd.vm";

    private static final String PRODUCT_NO_SVHC_XSL_NAME = "product-no-svhc.xsl";

    private static final String PRODUCT_NO_SVHC_VM_NAME = "product-no-svhc.vm";

    private static final String PRODUCT_YES_SVHC_XSL_NAME = "product-yes-svhc.xsl";

    private static final String PRODUCT_YES_SVHC_VM_NAME = "product-yes-svhc.vm";

    private static final String BATTERY_COMPLIANCE_XSL_NAME = "battery-compliance.xsl";

    private static final String BATTERY_COMPLIANCE_VM_NAME = "battery-compliance.vm";

    private static final String WEEE_XSL_NAME = "weee.xsl";

    private static final String WEEE_VM_NAME = "weee.vm";

    private static final String DISPOSAL_OF_PACKAGING_XSL_NAME = "disposal-of-packaging.xsl";

    private static final String DISPOSAL_OF_PACKAGING_VM_NAME = "disposal-of-packaging.vm";

    private static final String CONFLICT_MINERAL_XSL_NAME = "conflict-mineral.xsl";

    private static final String CONFLICT_MINERAL_VM_NAME = "conflict-mineral.vm";

    private static final String ROHS_CONFORMITY = "15";

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private CommerceCommonI18NService commerceCommonI18NService;

    @Autowired
    private DistPDFService distPDFService;

    @Autowired
    private UserService userService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private MediaService mediaService;

    @Autowired
    private CatalogVersionService catalogVersionService;

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    @Autowired
    private DistLogoUrlHelper distLogoUrlHelper;

    public InputStream getPDFStreamForROHS(ProductData productData) {
        return getPdfStream(productData, ROHS);
    }

    public InputStream getPDFStreamForROHS_RND(ProductData productData) {
        Map<String, Object> params = new HashMap<>();
        params.put("distrelecLogoUrl", getBlackRSLogoUrl());
        params.put("rndLogoUrl", addImageUrl("rnd_grey_logo"));

        return getPdfStream(productData, params, ROHS_RND);
    }

    private String addImageUrl(String mediaName) {
        return addImageUrl(mediaName, null);
    }

    private String addImageUrl(String mediaName, String languageCode) {
        CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(DistConstants.Catalog.DEFAULT_CATALOG_ID,
                                                                                     DistConstants.CatalogVersion.ONLINE);
        final MediaModel media = mediaService.getMedia(catalogVersion, getMediaName(mediaName, languageCode));
        final String url = media == null ? null : media.getURL();
        return url == null ? null : StringEscapeUtils.escapeXml(distLogoUrlHelper.getLocalhostAndPort() + url);
    }

    private String getMediaName(String mediaName, String languageCode) {
        if (isBlank(languageCode)) {
            return mediaName;
        }

        return mediaName + "_" + languageCode.toLowerCase();
    }

    private String getDefaultCurrencyIso() {
        final CurrencyModel currency = commerceCommonI18NService.getCurrentCurrency();
        return currency == null ? null : currency.getIsocode();
    }

    private DistPdfData getRohsPdfData(final CMSSiteModel site, final LanguageModel language, final Map<String, Object> params) {
        return getPdfData(PRODUCT_ROHS_VM_NAME, PRODUCT_ROHS_XSL_NAME, site, language, params);
    }

    private DistPdfData getRohsRndPdfData(final CMSSiteModel site, final LanguageModel language, final Map<String, Object> params) {
        return getPdfData(PRODUCT_RND_ROHS_VM_NAME, PRODUCT_RND_ROHS_XSL_NAME, site, language, params);
    }

    @Override
    public boolean isROHSAllowedForProduct(ProductData productData) {
        final String ignoredSites = configurationService.getConfiguration().getString("rohs.ignore.sites");
        List<String> codesList = Arrays.asList(ignoredSites.split(","));
        CMSSiteModel site = cmsSiteService.getCurrentSite();
        if (codesList.contains(site.getUid())) {
            return false;
        }
        return isROHSCompliant(productData);
    }

    @Override
    public boolean isROHSCompliant(ProductData productData) {
        final String rohsAllowedCodes = configurationService.getConfiguration().getString("rohs.allowed.code");
        List<String> rohsAllowedList = Arrays.asList(rohsAllowedCodes.split(","));
        return rohsAllowedList.contains(productData.getRohsCode());
    }

    @Override
    public boolean isROHSConform(ProductData productData) {
        return StringUtils.equals(productData.getRohsCode(), ROHS_CONFORMITY);
    }

    @Override
    public boolean isRNDProduct(ProductData productData) {
        if (productData.getDistManufacturer() != null) {
            final String rohsRndManufacturers = configurationService.getConfiguration().getString("rohs.rnd.manufacturers.code");
            List<String> rohsRndManufacturersList = Arrays.asList(rohsRndManufacturers.split(","));

            return rohsRndManufacturersList.contains(productData.getDistManufacturer().getCode());
        }
        return false;
    }

    @Override
    public InputStream getPDFStreamForSvhc(ProductData productData, boolean hasSvhc) {
        return getPdfStream(productData, hasSvhc ? SVHC_YES : SVHC_NO);
    }

    @Override
    public InputStream getPDFStreamForBatteryCompliance() {
        Map<String, Object> params = new HashMap<>();
        params.put("recycleBinUrl", addImageUrl("recycle_bin_svg"));

        return getPdfStream(params, BATTERY_COMPLIANCE);
    }

    @Override
    public InputStream getPDFStreamForWEEE() {
        Map<String, Object> params = new HashMap<>();
        params.put("recycleBinUrl", addImageUrl("recycle_bin"));

        return getPdfStream(params, WEEE);
    }

    @Override
    public InputStream getPDFStreamForDisposalOfPackagingWaste() {
        Map<String, Object> params = new HashMap<>();
        params.put("recycling_symbol", addImageUrl("recycling_symbol"));
        params.put("hdpe_recycling_symbol", addImageUrl("hdpe_recycling_symbol"));

        return getPdfStream(params, DISPOSAL_OF_PACKAGING_WASTE);
    }

    @Override
    public InputStream getPDFStreamForSvhc_RND(ProductData productData, boolean hasSvhc) {
        Map<String, Object> params = new HashMap<>();
        params.put("distrelecLogoUrl", getBlackRSLogoUrl());
        params.put("rndLogoUrl", addImageUrl("rnd_grey_logo"));

        return getPdfStream(productData, params, hasSvhc ? SVHC_YES_RND : SVHC_NO_RND);
    }

    @Override
    public InputStream getPDFStreamForConflictMineral() {
        return getPdfStream(CONFLICT_MINERAL);
    }

    private DistPdfData getSvhcRndPdfData(final CMSSiteModel site, final LanguageModel language, final Map<String, Object> params,
                                          ComplianceDocumentType complianceDocumentType) {
        if (SVHC_NO_RND.equals(complianceDocumentType)) {
            return getPdfData(PRODUCT_RND_NO_SVHC_VM_NAME, PRODUCT_RND_NO_SVHC_XSL_NAME, site, language, params);
        }

        return getPdfData(PRODUCT_RND_YES_SVHC_VM_NAME, PRODUCT_RND_YES_SVHC_XSL_NAME, site, language, params);
    }

    private DistPdfData getSvhcPdfData(final CMSSiteModel site, final LanguageModel language, final Map<String, Object> params,
                                       ComplianceDocumentType complianceDocumentType) {
        if (SVHC_NO.equals(complianceDocumentType)) {
            return getPdfData(PRODUCT_NO_SVHC_VM_NAME, PRODUCT_NO_SVHC_XSL_NAME, site, language, params);
        }

        return getPdfData(PRODUCT_YES_SVHC_VM_NAME, PRODUCT_YES_SVHC_XSL_NAME, site, language, params);
    }

    private DistPdfData getBatteryCompliancePdfData(final CMSSiteModel site, final LanguageModel language, final Map<String, Object> params) {
        return getPdfData(BATTERY_COMPLIANCE_VM_NAME, BATTERY_COMPLIANCE_XSL_NAME, site, language, params);
    }

    private DistPdfData getWEEEPdfData(final CMSSiteModel site, final LanguageModel language, final Map<String, Object> params) {
        return getPdfData(WEEE_VM_NAME, WEEE_XSL_NAME, site, language, params);
    }

    private DistPdfData getDisposalOfPackagingWastePdfData(final CMSSiteModel site, final LanguageModel language, final Map<String, Object> params) {
        return getPdfData(DISPOSAL_OF_PACKAGING_VM_NAME, DISPOSAL_OF_PACKAGING_XSL_NAME, site, language, params);
    }

    private DistPdfData getConflictMineralPdfData(final CMSSiteModel site, final LanguageModel language, final Map<String, Object> params) {
        return getPdfData(CONFLICT_MINERAL_VM_NAME, CONFLICT_MINERAL_XSL_NAME, site, language, params);
    }

    private DistPdfData getPdfData(String vmName, String xslName, CMSSiteModel site, LanguageModel language, Map<String, Object> params) {
        final DistPdfData data = new DistPdfData(vmName, xslName);
        data.setCmsSiteModel(site);
        data.setLanguage(language);
        data.setAdditionalParameters(params);
        data.setLogoUrl(distLogoUrlHelper.getLogoUrl(site));
        final UserModel currentUser = userService.getCurrentUser();
        if (currentUser instanceof CustomerModel) {
            data.setCustomer((CustomerModel) currentUser);
        }
        return data;
    }

    @Override
    public boolean productHasSvhc(ProductData productData) {
        Boolean hasSvhc = productData.getHasSvhc();
        if (hasSvhc == null) {
            throw new IllegalArgumentException("Product " + productData.getCode() + " doesn't have hasSVHC flag defined");
        }
        return hasSvhc;
    }

    @Override
    public boolean isProductBatteryCompliant(ProductData productData) {
        final String batteryComplianceCodes = configurationService.getConfiguration().getString("battery.compliances.code");
        List<String> batteryComplianceList = Arrays.asList(batteryComplianceCodes.split(","));
        return StringUtils.isNotBlank(productData.getBatteryComplianceCode()) && batteryComplianceList.contains(productData.getBatteryComplianceCode());
    }

    private InputStream getPdfStream(ProductData productData, ComplianceDocumentType documentType) {
        return getPdfStream(productData, null, documentType);
    }

    private InputStream getPdfStream(Map<String, Object> documentParameters, ComplianceDocumentType documentType) {
        return getPdfStream(null, documentParameters, documentType);
    }

    private InputStream getPdfStream(ComplianceDocumentType documentType) {
        return getPdfStream(null, null, documentType);
    }

    private InputStream getPdfStream(ProductData productData, Map<String, Object> documentParameters, ComplianceDocumentType documentType) {
        CMSSiteModel currentSite = cmsSiteService.getCurrentSite();
        LanguageModel currentLanguage = commerceCommonI18NService.getCurrentLanguage();
        if (currentSite == null || currentLanguage == null) {
            LOG.debug("One or more arguments for generating Compliance PDF are null. Returning null");
            return null;
        }

        Locale currentLocale = commerceCommonI18NService.getCurrentLocale();
        String currencyIso = getDefaultCurrencyIso();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_PATTERN);

        Map<String, Object> defaultParameters = new HashMap<>();
        if (productData != null) {
            defaultParameters.put("productData", productData);
        }
        defaultParameters.put("brandingUrl", addImageUrl("colour_branding"));
        defaultParameters.put("todaysDate", simpleDateFormat.format(new Date()));
        defaultParameters.put("formatHelper",
                              new DistPDFNumberFormatHelper(currentLocale, currencyIso,
                                                            configurationService.getConfiguration().getString(COMMA_COUNTRIES, "")));
        defaultParameters.put("footerAddress", distSalesOrgService.getCurrentSalesOrg().getFullAddress(currentLocale));
        defaultParameters.put("StringUtils", StringUtils.class);
        Map<String, Object> params = new HashMap<>(defaultParameters);
        if (documentParameters != null) {
            params.putAll(documentParameters);
        }

        return getPdfStreamForComplianceDocument(currentSite, currentLanguage, params, documentType);
    }

    public boolean isNotRndProductAndIsNotAllowedForSiteAndNotCompliant(ProductData productData, boolean isRNDProduct) {
        return !isRNDProduct && !isROHSAllowedForProduct(productData);
    }

    public boolean isRndProductAndIsNotROHSConform(ProductData productData, boolean isRNDProduct) {
        return isRNDProduct && !isROHSConform(productData);
    }

    private InputStream getPdfStreamForComplianceDocument(CMSSiteModel currentSite, LanguageModel currentLanguage, Map<String, Object> params,
                                                          ComplianceDocumentType documentType) {
        if (ROHS.equals(documentType)) {
            return distPDFService.generatePdfFromData(getRohsPdfData(currentSite, currentLanguage, params));
        } else if (ROHS_RND.equals(documentType)) {
            return distPDFService.generatePdfFromData(getRohsRndPdfData(currentSite, currentLanguage, params));
        } else if (SVHC_NO.equals(documentType)) {
            return distPDFService.generatePdfFromData(getSvhcPdfData(currentSite, currentLanguage, params, documentType));
        } else if (SVHC_YES.equals(documentType)) {
            return distPDFService.generatePdfFromData(getSvhcPdfData(currentSite, currentLanguage, params, documentType));
        } else if (SVHC_NO_RND.equals(documentType)) {
            return distPDFService.generatePdfFromData(getSvhcRndPdfData(currentSite, currentLanguage, params, documentType));
        } else if (SVHC_YES_RND.equals(documentType)) {
            return distPDFService.generatePdfFromData(getSvhcRndPdfData(currentSite, currentLanguage, params, documentType));
        } else if (BATTERY_COMPLIANCE.equals(documentType)) {
            return distPDFService.generatePdfFromData(getBatteryCompliancePdfData(currentSite, currentLanguage, params));
        } else if (WEEE.equals(documentType)) {
            return distPDFService.generatePdfFromData(getWEEEPdfData(currentSite, currentLanguage, params));
        } else if (DISPOSAL_OF_PACKAGING_WASTE.equals(documentType)) {
            return distPDFService.generatePdfFromData(getDisposalOfPackagingWastePdfData(currentSite, currentLanguage, params));
        } else if (CONFLICT_MINERAL.equals(documentType)) {
            return distPDFService.generatePdfFromData(getConflictMineralPdfData(currentSite, currentLanguage, params));
        }

        return null;
    }

    private String getBlackRSLogoUrl() {
        try {
            return addImageUrl("distrelec_rs_logo_black", commerceCommonI18NService.getCurrentLocale().getLanguage());
        } catch (UnknownIdentifierException e) {
            // This is temporary
            // New black RS logos are missing in the following languages: sv, pl, no, lv, lt, fl, et, da
            // Until we get new logos, we'll display the English RS Black & White logo
            // Once we get new logos, remove the try-catch block, remove the line inside catch block, and leave only the line in try block
            // https://jira.distrelec.com/browse/DISTRELEC-32115
            return addImageUrl("distrelec_rs_logo_black", "en");
        }
    }

    enum ComplianceDocumentType {
        ROHS,
        ROHS_RND,
        SVHC_NO,
        SVHC_YES,
        SVHC_NO_RND,
        SVHC_YES_RND,
        BATTERY_COMPLIANCE,
        WEEE,
        DISPOSAL_OF_PACKAGING_WASTE,
        CONFLICT_MINERAL,
    }
}
