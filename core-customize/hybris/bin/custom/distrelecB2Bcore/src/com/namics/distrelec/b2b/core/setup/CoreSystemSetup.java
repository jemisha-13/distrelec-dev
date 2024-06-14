/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 * 
 *  
 */
package com.namics.distrelec.b2b.core.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.namics.distrelec.b2b.core.constants.Namb2bacceleratorCoreConstants;
import com.namics.hybris.toolbox.impex.userrights.RowbasedUserRightsImport;
import com.namics.hybris.toolbox.spring.SpringUtil;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.validation.services.ValidationService;

/**
 * This class provides hooks into the system's initialization and update processes.
 * 
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = Namb2bacceleratorCoreConstants.EXTENSIONNAME)
public class CoreSystemSetup extends AbstractSystemSetup {

    public static final String IMPORT_SYNC_CATALOGS = "syncContentCatalogs";
    public static final String IMPORT_COCKPIT_COMPONENTS = "cockpitComponents";
    public static final String IMPORT_ACCESS_RIGHTS = "accessRights";

    public static final String IMPORT_BASE_DATA = "importBaseData";
    public static final String IMPORT_BASE_COUNTRIES = "importBaseCountries";
    public static final String IMPORT_BASE_REGIONS = "importBaseRegions";
    public static final String IMPORT_BASE_PRODUCT_CATALOG = "importBaseProductCatalog";
    public static final String IMPORT_BASE_ERP_CODELIST = "importBaseErpCodelist";
    public static final String IMPORT_BASE_DELIVERY_MODES = "importBaseDeliveryModes";
    public static final String IMPORT_BASE_PAYMENT_MODES = "importBasePaymentModes";
    public static final String IMPORT_BASE_MCC_SITE_LINKS = "importBaseMccSiteLinks";
    public static final String IMPORT_BASE_THEMES = "importBaseThemes";
    public static final String IMPORT_BASE_USER_GROUPS = "importBaseUserGroups";
    public static final String IMPORT_BASE_SEARCH_RESTRICTIONS = "importBaseSearchRestrictions";
    public static final String IMPORT_BASE_EMAIL_CONTENT = "importBaseEmailContent";
    public static final String IMPORT_BASE_SYNC_JOBS = "importBaseSyncJobs";
    public static final String IMPORT_BASE_SITE = "importBaseSite";
    public static final String IMPORT_BASE_COCKPIT_CONFIG = "importBaseCockpitConfig";

    public static final String IMPORT_PRODUCT_CATALOG = "importProductCatalog";
    public static final String IMPORT_CATALOG_PLUS_PRODUCT_CATALOG = "importCatalogPlusProductCatalog";

    public static final String IMPORT_SITE_DISTRELEC_AT = "importSiteDistrelec_AT";
    public static final String IMPORT_SITE_DISTRELEC_CH = "importSiteDistrelec_CH";
    public static final String IMPORT_SITE_DISTRELEC_DE = "importSiteDistrelec_DE";
    public static final String IMPORT_SITE_DISTRELEC_DK = "importSiteDistrelec_DK";
    public static final String IMPORT_SITE_DISTRELEC_EE = "importSiteDistrelec_EE";
    public static final String IMPORT_SITE_DISTRELEC_EX = "importSiteDistrelec_EX";
    public static final String IMPORT_SITE_DISTRELEC_FI = "importSiteDistrelec_FI";
    public static final String IMPORT_SITE_DISTRELEC_IT = "importSiteDistrelec_IT";
    public static final String IMPORT_SITE_DISTRELEC_LT = "importSiteDistrelec_LT";
    public static final String IMPORT_SITE_DISTRELEC_LV = "importSiteDistrelec_LV";
    public static final String IMPORT_SITE_DISTRELEC_NL = "importSiteDistrelec_NL";
    public static final String IMPORT_SITE_DISTRELEC_NO = "importSiteDistrelec_NO";
    public static final String IMPORT_SITE_DISTRELEC_PL = "importSiteDistrelec_PL";
    public static final String IMPORT_SITE_DISTRELEC_SE = "importSiteDistrelec_SE";
    public static final String IMPORT_SITE_DISTRELEC_CZ = "importSiteDistrelec_CZ";
    public static final String IMPORT_SITE_DISTRELEC_HU = "importSiteDistrelec_HU";
    public static final String IMPORT_SITE_DISTRELEC_RO = "importSiteDistrelec_RO";
    public static final String IMPORT_SITE_DISTRELEC_SK = "importSiteDistrelec_SK";
    public static final String IMPORT_SITE_DISTRELEC_BE = "importSiteDistrelec_BE";
    public static final String IMPORT_SITE_DISTRELEC_TR = "importSiteDistrelec_TR";

    public static final String DISTRELEC = "distrelec";
    public static final String DISTRELEC_CATALOG_PLUS = DISTRELEC + "CatalogPlus";
    public static final String DISTRELEC_AT = "distrelec_AT";
    public static final String DISTRELEC_CH = "distrelec_CH";
    public static final String DISTRELEC_DE = "distrelec_DE";
    public static final String DISTRELEC_DK = "distrelec_DK";
    public static final String DISTRELEC_EE = "distrelec_EE";
    public static final String DISTRELEC_EX = "distrelec_EX";
    public static final String DISTRELEC_FI = "distrelec_FI";
    public static final String DISTRELEC_IT = "distrelec_IT";
    public static final String DISTRELEC_LT = "distrelec_LT";
    public static final String DISTRELEC_LV = "distrelec_LV";
    public static final String DISTRELEC_NL = "distrelec_NL";
    public static final String DISTRELEC_NO = "distrelec_NO";
    public static final String DISTRELEC_PL = "distrelec_PL";
    public static final String DISTRELEC_SE = "distrelec_SE";
    public static final String DISTRELEC_CZ = "distrelec_CZ";
    public static final String DISTRELEC_HU = "distrelec_HU";
    public static final String DISTRELEC_RO = "distrelec_RO";
    public static final String DISTRELEC_SK = "distrelec_SK";
    public static final String DISTRELEC_BE = "distrelec_BE";
    public static final String DISTRELEC_TR = "distrelec_TR";

    private static final String BASE_DIR = "/distrelecB2Bcore/import";
    private static final String ESSENTIALDATA_DIR = BASE_DIR + "/essential";
    private static final String BASEDATA_DIR = BASE_DIR + "/base";
    private static final String PROJECTDATA_DIR = BASE_DIR + "/project";
    private static final String COMMON_DIR = "/common";
    private static final String CRONJOB_DIR = "/cronjob";

    /**
     * This method will be called by system creator during initialization and system update. Be sure that this method can be called
     * repeatedly.
     * 
     * @param context
     *            the context provides the selected parameters and values
     */
    @SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
    public void createEssentialData(final SystemSetupContext context) {
        // Impex files move to project data as per DISTRELEC-8108 optimise: deployment time
        importImpexFile(context, ESSENTIALDATA_DIR + COMMON_DIR + "/saved-queries.impex");
        importImpexFile(context, ESSENTIALDATA_DIR + COMMON_DIR + "/cronjobs.impex");
        importImpexFile(context, ESSENTIALDATA_DIR + COMMON_DIR + "/cronjobs-generic-sanity-check.impex");
        importImpexFile(context, ESSENTIALDATA_DIR + COMMON_DIR + "/first_appearance_composite_cronjob.impex");
        importImpexFile(context, ESSENTIALDATA_DIR + COMMON_DIR + "/search-restrictions.impex");
    }

    /**
     * Generates the Dropdown and Multi-select boxes for the project data import
     */
    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();

        params.add(createBooleanSystemSetupParameter(IMPORT_BASE_DATA,
                "IMPORT BASE DATA like base, countries, erpcodes, delivery modes, payment modes theme usergroups", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_BASE_REGIONS, "Import Regions", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_BASE_PRODUCT_CATALOG, "Import Base Product Catalog", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_BASE_MCC_SITE_LINKS, "Import Mcc Site Links", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_BASE_SEARCH_RESTRICTIONS, "Import Search Restrictions", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_BASE_EMAIL_CONTENT, "Import Base Email Content", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_BASE_SYNC_JOBS, "Import Sync Jobs", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_BASE_SITE, "Import Base Sites", false));

        params.add(createBooleanSystemSetupParameter(IMPORT_PRODUCT_CATALOG, "Import Product Catalog", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_CATALOG_PLUS_PRODUCT_CATALOG, "Import Catalog+ Product Catalog", false));

        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_AT, "Import Site Distrelec AT", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_CH, "Import Site Distrelec CH", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_DE, "Import Site Distrelec DE", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_DK, "Import Site Distrelec DK", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_EE, "Import Site Distrelec EE", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_EX, "Import Site Distrelec EX (export shop)", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_FI, "Import Site Distrelec FI", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_IT, "Import Site Distrelec IT", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_LT, "Import Site Distrelec LT", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_LV, "Import Site Distrelec LV", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_NL, "Import Site Distrelec NL", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_NO, "Import Site Distrelec NO", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_PL, "Import Site Distrelec PL", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_SE, "Import Site Distrelec SE", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_CZ, "Import Site Distrelec CZ", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_HU, "Import Site Distrelec HU", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_RO, "Import Site Distrelec RO", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_SK, "Import Site Distrelec SK", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_BE, "Import Site Distrelec BE", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_TR, "Import Site Distrelec TR", false));

        params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_CATALOGS, "Sync Content Catalogs", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_COCKPIT_COMPONENTS, "Import Cockpit Components", false));
        params.add(createBooleanSystemSetupParameter(IMPORT_ACCESS_RIGHTS, "Import Users & Groups", false));

        return params;
    }

    /**
     * This method will be called during the system initialization.
     * 
     * @param context
     *            the context provides the selected parameters and values
     */
    @SystemSetup(type = Type.PROJECT, process = Process.ALL)
    public void createProjectData(final SystemSetupContext context) {
        final boolean importBaseData = getBooleanSystemSetupParameter(context, IMPORT_BASE_DATA);
        final boolean importBaseRegions = getBooleanSystemSetupParameter(context, IMPORT_BASE_REGIONS);
        final boolean importBaseProductCatalog = getBooleanSystemSetupParameter(context, IMPORT_BASE_PRODUCT_CATALOG);
        final boolean importBaseMccSiteLinks = getBooleanSystemSetupParameter(context, IMPORT_BASE_MCC_SITE_LINKS);
        final boolean importBaseSearchRestriction = getBooleanSystemSetupParameter(context, IMPORT_BASE_SEARCH_RESTRICTIONS);
        final boolean importBaseEmailcontent = getBooleanSystemSetupParameter(context, IMPORT_BASE_EMAIL_CONTENT);
        final boolean importBaseSyncJob = getBooleanSystemSetupParameter(context, IMPORT_BASE_SYNC_JOBS);
        final boolean importBaseSite = getBooleanSystemSetupParameter(context, IMPORT_BASE_SITE);
        final boolean importBaseCockpitConfig = getBooleanSystemSetupParameter(context, IMPORT_BASE_COCKPIT_CONFIG);

        final boolean importProductCatalog = getBooleanSystemSetupParameter(context, IMPORT_PRODUCT_CATALOG);
        final boolean importCatalogPlusProductCatalog = getBooleanSystemSetupParameter(context, IMPORT_CATALOG_PLUS_PRODUCT_CATALOG);

        final boolean importSiteDistrelecAT = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_AT);
        final boolean importSiteDistrelecCH = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_CH);
        final boolean importSiteDistrelecDE = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_DE);
        final boolean importSiteDistrelecDK = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_DK);
        final boolean importSiteDistrelecEE = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_EE);
        final boolean importSiteDistrelecEX = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_EX);
        final boolean importSiteDistrelecFI = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_FI);
        final boolean importSiteDistrelecIT = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_IT);
        final boolean importSiteDistrelecLT = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_LT);
        final boolean importSiteDistrelecLV = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_LV);
        final boolean importSiteDistrelecNL = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_NL);
        final boolean importSiteDistrelecNO = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_NO);
        final boolean importSiteDistrelecPL = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_PL);
        final boolean importSiteDistrelecSE = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_SE);
        final boolean importSiteDistrelecCZ = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_CZ);
        final boolean importSiteDistrelecHU = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_HU);
        final boolean importSiteDistrelecRO = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_RO);
        final boolean importSiteDistrelecSK = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_SK);
        final boolean importSiteDistrelecBE = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_BE);
        final boolean importSiteDistrelecTR = getBooleanSystemSetupParameter(context, IMPORT_SITE_DISTRELEC_TR);

        if (importBaseData) {
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/base-data.impex");
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/countries.impex");
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/erp-codelists.impex");
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/delivery-modes.impex");
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/payment-modes.impex");
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/themes.impex");
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/user-groups.impex");
        }
        if (importBaseRegions) {
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/regions.impex");
        }
        if (importBaseProductCatalog) {
            importImpexFile(context, BASEDATA_DIR + "/productCatalogs/distrelecProductCatalog" + "/catalog.impex");
            importImpexFile(context, BASEDATA_DIR + "/productCatalogs/distrelecCatalogPlusProductCatalog" + "/catalog.impex");
        }
        if (importBaseMccSiteLinks) {
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/mcc-sites-links.impex");
        }
        if (importBaseSearchRestriction) {
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/search-restrictions.impex");
        }
        if (importBaseEmailcontent) {
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/email-content.impex");
        }
        if (importBaseSyncJob) {
            importImpexFile(context, BASEDATA_DIR + COMMON_DIR + "/sync-job.impex");
        }
        if (importBaseSite) {
            importImpexFile(context, BASEDATA_DIR + "/stores/distrelec_external/site.impex");
        }

        final boolean importAccessRights = getBooleanSystemSetupParameter(context, IMPORT_ACCESS_RIGHTS);

        // Import global webtrekk params
        importImpexFile(context, PROJECTDATA_DIR + COMMON_DIR + "/webtrekk.impex");

        // global product catalog for all sites
        if (importProductCatalog) {
            importProductCatalog(context, DISTRELEC);
        }
        if (importCatalogPlusProductCatalog) {
            importProductCatalog(context, DISTRELEC_CATALOG_PLUS);
        }

        if (importSiteDistrelecAT) {
            importSite(context, DISTRELEC_AT);
        }
        if (importSiteDistrelecCH) {
            importSite(context, DISTRELEC_CH);
        }
        if (importSiteDistrelecDE) {
            importSite(context, DISTRELEC_DE);
        }
        if (importSiteDistrelecDK) {
            importSite(context, DISTRELEC_DK);
        }
        if (importSiteDistrelecEE) {
            importSite(context, DISTRELEC_EE);
        }
        if (importSiteDistrelecEX) {
            importSite(context, DISTRELEC_EX);
        }
        if (importSiteDistrelecIT) {
            importSite(context, DISTRELEC_IT);
        }
        if (importSiteDistrelecFI) {
            importSite(context, DISTRELEC_FI);
        }
        if (importSiteDistrelecLT) {
            importSite(context, DISTRELEC_LT);
        }
        if (importSiteDistrelecLV) {
            importSite(context, DISTRELEC_LV);
        }
        if (importSiteDistrelecNL) {
            importSite(context, DISTRELEC_NL);
        }
        if (importSiteDistrelecNO) {
            importSite(context, DISTRELEC_NO);
        }
        if (importSiteDistrelecPL) {
            importSite(context, DISTRELEC_PL);
        }
        if (importSiteDistrelecSE) {
            importSite(context, DISTRELEC_SE);
        }
        if (importSiteDistrelecCZ) {
            importSite(context, DISTRELEC_CZ);
        }
        if (importSiteDistrelecHU) {
            importSite(context, DISTRELEC_HU);
        }
        if (importSiteDistrelecRO) {
            importSite(context, DISTRELEC_RO);
        }
        if (importSiteDistrelecSK) {
            importSite(context, DISTRELEC_SK);
        }
        if (importSiteDistrelecBE) {
            importSite(context, DISTRELEC_BE);
        }
        if (importSiteDistrelecTR) {
            importSite(context, DISTRELEC_TR);
        }

        if (importSiteDistrelecAT //
                || importSiteDistrelecCH //
                || importSiteDistrelecDE //
                || importSiteDistrelecDK //
                || importSiteDistrelecEE //
                || importSiteDistrelecEX //
                || importSiteDistrelecFI //
                || importSiteDistrelecIT //
                || importSiteDistrelecLT //
                || importSiteDistrelecLV //
                || importSiteDistrelecNL //
                || importSiteDistrelecNO //
                || importSiteDistrelecPL //
                || importSiteDistrelecSE //
                || importSiteDistrelecCZ //
                || importSiteDistrelecHU //
                || importSiteDistrelecRO //
                || importSiteDistrelecSK //
                || importSiteDistrelecBE //
                || importSiteDistrelecTR //
        ) {
            SpringUtil.getBean("validationService", ValidationService.class).reloadValidationEngine();
        }

        final List<String> extensionNames = Registry.getCurrentTenant().getTenantSpecificExtensionNames();

        if (importAccessRights) {
            importAccessRights(context);
            // Import catalog rights
            importImpexFile(context, PROJECTDATA_DIR + COMMON_DIR + "/userrights-on-catalogs.impex", false);
        }

        if (importAccessRights && extensionNames.contains("cmscockpit")) {
            importImpexFile(context, PROJECTDATA_DIR + "/cockpits/cmscockpit/cmscockpit-users.impex", false);
            importImpexFile(context, PROJECTDATA_DIR + "/cockpits/cmscockpit/cmscockpit-access-rights.impex", false);
        }

        if (importAccessRights && extensionNames.contains("btgcockpit")) {
            importImpexFile(context, PROJECTDATA_DIR + "/cockpits/cmscockpit/btgcockpit-users.impex", false);
            importImpexFile(context, PROJECTDATA_DIR + "/cockpits/cmscockpit/btgcockpit-access-rights.impex", false);
        }

        if (importAccessRights && extensionNames.contains("productcockpit")) {
            importImpexFile(context, PROJECTDATA_DIR + "/cockpits/productcockpit/productcockpit-users.impex", false);
            importImpexFile(context, PROJECTDATA_DIR + "/cockpits/productcockpit/productcockpit-access-rights.impex", false);
            importImpexFile(context, PROJECTDATA_DIR + "/cockpits/productcockpit/productcockpit-constraints.impex", false);
        }

        if (importAccessRights && extensionNames.contains("cscockpit")) {
            importImpexFile(context, PROJECTDATA_DIR + "/cockpits/cscockpit/cscockpit-users.impex", false);
            importImpexFile(context, PROJECTDATA_DIR + "/cockpits/cscockpit/cscockpit-access-rights.impex", false);
        }
    }

    /**
     * Run the import site project data such as store data, content catalog and synchronization of the content catalog.
     * 
     * @param context
     * @param site
     */
    protected void importSite(final SystemSetupContext context, final String site) {
        importStore(context, site);
        importStoreProject(context, site);
        importContentCatalog(context, site);
        executeCatalogSyncJob(context, site);
    }

    @Override
    public PerformResult executeCatalogSyncJob(final SystemSetupContext context, final String catalogName) {
        final boolean syncCatalogs = getBooleanSystemSetupParameter(context, IMPORT_SYNC_CATALOGS);
        PerformResult syncCronJobResult = null;
        if (syncCatalogs) {
            logInfo(context, "Begin preparing catalogs sync job  [" + catalogName + "]");
            try {
                getSetupSyncJobService().assignDependentSyncJobs(catalogName + "ProductCatalog", Collections.singleton(catalogName + "ContentCatalog"));
                logInfo(context, "Done preparing catalogs sync job  [" + catalogName + "]");
                logInfo(context, "Executing catalogs sync job  [" + catalogName + "]");
                syncCronJobResult = super.executeCatalogSyncJob(context, catalogName + "ContentCatalog");
                logInfo(context, "Executed catalogs sync job  [" + catalogName + "]");
            } catch (final Exception exp) {
                logError(context, "Error occurs while executing catalog sync job [" + catalogName + "] -> Try catalog sync from Backoffice", exp);
            }
        }
        return syncCronJobResult;
    }

    protected void importProductCatalog(final SystemSetupContext context, final String catalogName) {
        logInfo(context, "Begin importing product catalog [" + catalogName + "]");
        logInfo(context, "Done importing product catalog [" + catalogName + "]");
    }

    protected void importContentCatalog(final SystemSetupContext context, final String catalogName) {
        logInfo(context, "Begin importing content catalog [" + catalogName + "]");

        importImpexFile(context, PROJECTDATA_DIR + "/contentCatalogs/" + catalogName + "ContentCatalog/cms-content.impex", false);
        importImpexFile(context, PROJECTDATA_DIR + "/contentCatalogs/" + catalogName + "ContentCatalog/email-content.impex", false);
        importImpexFile(context, PROJECTDATA_DIR + "/contentCatalogs/" + catalogName + "ContentCatalog/pdf-content.impex", false);
        importImpexFile(context, PROJECTDATA_DIR + "/contentCatalogs/" + catalogName + "ContentCatalog/webtrekk.impex", false);

        createContentCatalogSyncJob(context, catalogName + "ContentCatalog");

        importImpexFile(context, PROJECTDATA_DIR + "/contentCatalogs/" + catalogName + "ContentCatalog/sync-job.impex", false);

        logInfo(context, "Done importing content catalog [" + catalogName + "]");
    }

    protected void importStore(final SystemSetupContext context, final String storeName) {
        logInfo(context, "Begin importing store [" + storeName + "]");

        importImpexFile(context, BASEDATA_DIR + "/stores/" + storeName + "/catalog.impex");
        importImpexFile(context, BASEDATA_DIR + "/stores/" + storeName + "/store.impex");
        importImpexFile(context, BASEDATA_DIR + "/stores/" + storeName + "/site.impex");

        logInfo(context, "Done importing store [" + storeName + "]");
    }

    protected void importStoreProject(final SystemSetupContext context, final String storeName) {
        logInfo(context, "Begin importing store [" + storeName + "]");

        importImpexFile(context, PROJECTDATA_DIR + "/stores/" + storeName + "/store.impex");
        importImpexFile(context, PROJECTDATA_DIR + "/stores/" + storeName + "/site.impex");

        logInfo(context, "Done importing store [" + storeName + "]");
    }

    private void importAccessRights(final SystemSetupContext context) {
        logInfo(context, "Begin importing Access Rights");
        importGroupSpecificObjectRights(context);
        importGroupSpecificCatalogRights(context);
        logInfo(context, "Done importing Access Rights");
    }

    private void importGroupSpecificObjectRights(final SystemSetupContext context) {
        try {
            final RowbasedUserRightsImport userrightImportTask = SpringUtil.getBean("rowbasedUserrightsImportTask", RowbasedUserRightsImport.class);
            userrightImportTask.performTask();
        } catch (final Exception ex) {
            logError(context, "Error occured during user right configurations", ex);
        }
    }

    private void importGroupSpecificCatalogRights(final SystemSetupContext context) {
        logInfo(context, "Begin importing GroupSpecificCatalogRights");
        importImpexFile(context, PROJECTDATA_DIR + COMMON_DIR + "/userrights-on-catalogs.impex");
        logInfo(context, "Done importing GroupSpecificCatalogRights");
    }
}