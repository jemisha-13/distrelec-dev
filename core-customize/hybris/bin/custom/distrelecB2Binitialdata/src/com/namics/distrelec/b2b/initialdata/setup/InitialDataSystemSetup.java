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
package com.namics.distrelec.b2b.initialdata.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.namics.distrelec.b2b.initialdata.constants.Namb2bacceleratorInitialDataConstants;

import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.initialization.SystemSetupParameterMethod;
import de.hybris.platform.servicelayer.cronjob.PerformResult;

/**
 * This class provides hooks into the system's initialization and update processes.
 *
 * @see "https://wiki.hybris.com/display/release4/Hooks+for+Initialization+and+Update+Process"
 */
@SystemSetup(extension = Namb2bacceleratorInitialDataConstants.EXTENSIONNAME)
public class InitialDataSystemSetup extends AbstractSystemSetup {

    public static final String IMPORT_COMMON_DATA = "importCommonData";
    public static final String IMPORT_PRODUCT_CATALOG = "importProductCatalog";
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

    public static final String IMPORT_SYNC_CATALOGS = "syncContentCatalogs";

    public static final String DISTRELEC = "distrelec";
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

    /**
     * Generates the Dropdown and Multi-select boxes for the project data import
     */
    @Override
    @SystemSetupParameterMethod
    public List<SystemSetupParameter> getInitializationOptions() {
        final List<SystemSetupParameter> params = new ArrayList<SystemSetupParameter>();
        params.add(createBooleanSystemSetupParameter(IMPORT_COMMON_DATA, "Import Common Data (users, newsletter topics, payment modes, cockpit users)", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_PRODUCT_CATALOG, "Import Product Catalog", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_AT, "Import Site Distrelec AT", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_CH, "Import Site Distrelec CH", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_DE, "Import Site Distrelec DE", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_DK, "Import Site Distrelec DK", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_EE, "Import Site Distrelec EE", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_EX, "Import Site Distrelec EX (export shop)", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_FI, "Import Site Distrelec FI", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_IT, "Import Site Distrelec IT", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_LT, "Import Site Distrelec LT", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_LV, "Import Site Distrelec LV", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_NL, "Import Site Distrelec NL", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_NO, "Import Site Distrelec NO", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_PL, "Import Site Distrelec PL", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_SE, "Import Site Distrelec SE", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_CZ, "Import Site Distrelec CZ", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_HU, "Import Site Distrelec HU", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_RO, "Import Site Distrelec RO", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_SK, "Import Site Distrelec SK", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_BE, "Import Site Distrelec BE", true));
        params.add(createBooleanSystemSetupParameter(IMPORT_SITE_DISTRELEC_TR, "Import Site Distrelec TR", true));

        params.add(createBooleanSystemSetupParameter(IMPORT_SYNC_CATALOGS, "Sync Content Catalogs", true));
        return params;
    }

    /**
     * Implement this method to create initial objects. This method will be called by system creator during initialization and system
     * update. Be sure that this method can be called repeatedly.
     *
     * @param context
     *            the context provides the selected parameters and values
     */
    @SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
    public void createEssentialData(final SystemSetupContext context) {
        // Do not import essential data via initialdata extension --> place file in core extension
    }

    /**
     * Implement this method to create data that is used in your project. This method will be called during the system initialization.
     *
     * @param context
     *            the context provides the selected parameters and values
     */
    @SystemSetup(type = Type.PROJECT, process = Process.ALL)
    public void createProjectData(final SystemSetupContext context) {
        final boolean importCommonData = getBooleanSystemSetupParameter(context, IMPORT_COMMON_DATA);
        final boolean importProductCatalog = getBooleanSystemSetupParameter(context, IMPORT_PRODUCT_CATALOG);
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

        if (importCommonData) {
            importCommonData(context, "distrelecB2Binitialdata");
        }

        if (importProductCatalog) {
            importProductCatalog(context, "distrelecB2Binitialdata", DISTRELEC);
        }

        if (importSiteDistrelecAT) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_AT, Collections.singletonList(DISTRELEC_AT));
        }

        if (importSiteDistrelecCH) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_CH, Collections.singletonList(DISTRELEC_CH));
        }

        if (importSiteDistrelecDE) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_DE, Collections.singletonList(DISTRELEC_DE));
        }

        if (importSiteDistrelecDK) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_DK, Collections.singletonList(DISTRELEC_DK));
        }

        if (importSiteDistrelecEE) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_EE, Collections.singletonList(DISTRELEC_EE));
        }

        if (importSiteDistrelecEX) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_EX, Collections.singletonList(DISTRELEC_EX));
        }

        if (importSiteDistrelecFI) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_FI, Collections.singletonList(DISTRELEC_FI));
        }

        if (importSiteDistrelecIT) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_IT, Collections.singletonList(DISTRELEC_IT));
        }

        if (importSiteDistrelecLT) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_LT, Collections.singletonList(DISTRELEC_LT));
        }

        if (importSiteDistrelecLV) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_LV, Collections.singletonList(DISTRELEC_LV));
        }

        if (importSiteDistrelecNL) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_NL, Collections.singletonList(DISTRELEC_NL));
        }

        if (importSiteDistrelecNO) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_NO, Collections.singletonList(DISTRELEC_NO));
        }

        if (importSiteDistrelecPL) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_PL, Collections.singletonList(DISTRELEC_PL));
        }

        if (importSiteDistrelecSE) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_SE, Collections.singletonList(DISTRELEC_SE));
        }

        if (importSiteDistrelecCZ) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_CZ, Collections.singletonList(DISTRELEC_CZ));
        }
        if (importSiteDistrelecHU) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_HU, Collections.singletonList(DISTRELEC_HU));
        }
        if (importSiteDistrelecRO) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_RO, Collections.singletonList(DISTRELEC_RO));
        }
        if (importSiteDistrelecSK) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_SK, Collections.singletonList(DISTRELEC_SK));
        }
        if (importSiteDistrelecBE) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_BE, Collections.singletonList(DISTRELEC_BE));
        }
        if (importSiteDistrelecTR) {
            importStoreInitialData(context, "distrelecB2Binitialdata", DISTRELEC_TR, Collections.singletonList(DISTRELEC_TR));
        }

        executeCatalogSyncJob(context, DISTRELEC_AT);
        executeCatalogSyncJob(context, DISTRELEC_CH);
        executeCatalogSyncJob(context, DISTRELEC_DE);
        executeCatalogSyncJob(context, DISTRELEC_DK);
        executeCatalogSyncJob(context, DISTRELEC_EE);
        executeCatalogSyncJob(context, DISTRELEC_EX);
        executeCatalogSyncJob(context, DISTRELEC_FI);
        executeCatalogSyncJob(context, DISTRELEC_IT);
        executeCatalogSyncJob(context, DISTRELEC_LT);
        executeCatalogSyncJob(context, DISTRELEC_LV);
        executeCatalogSyncJob(context, DISTRELEC_NL);
        executeCatalogSyncJob(context, DISTRELEC_NO);
        executeCatalogSyncJob(context, DISTRELEC_PL);
        executeCatalogSyncJob(context, DISTRELEC_SE);
        executeCatalogSyncJob(context, DISTRELEC_CZ);
        executeCatalogSyncJob(context, DISTRELEC_HU);
        executeCatalogSyncJob(context, DISTRELEC_RO);
        executeCatalogSyncJob(context, DISTRELEC_SK);
        executeCatalogSyncJob(context, DISTRELEC_BE);
        executeCatalogSyncJob(context, DISTRELEC_TR);
    }

    /**
     * Imports Common Data
     */
    protected void importCommonData(final SystemSetupContext context, final String importDirectory) {
        logInfo(context, "Importing Common Data...");

        final String importRoot = "/" + importDirectory + "/import/project";

        importImpexFile(context, importRoot + "/common/user-groups.impex", false);
        importImpexFile(context, importRoot + "/common/promotions.impex", false);
        importImpexFile(context, importRoot + "/common/payment-modes.impex", true);
        importImpexFile(context, importRoot + "/common/newsletter.impex", true);

        final List<String> extensionNames = Registry.getCurrentTenant().getTenantSpecificExtensionNames();
        if (extensionNames.contains("cmscockpit")) {
            importImpexFile(context, importRoot + "/cockpits/cmscockpit/cmscockpit-users.impex", false);
        }

        if (extensionNames.contains("productcockpit")) {
            importImpexFile(context, importRoot + "/cockpits/productcockpit/productcockpit-users.impex", false);
        }

        if (extensionNames.contains("cscockpit")) {
            importImpexFile(context, importRoot + "/cockpits/cscockpit/cscockpit-users.impex", false);
        }

        if (extensionNames.contains("reportcockpit")) {
            importImpexFile(context, importRoot + "/cockpits/reportcockpit/reportcockpit-users.impex", false);
            importImpexFile(context, importRoot + "/cockpits/reportcockpit/reportcockpit-mcc-links.impex", false);
        }
    }

    /**
     * Use this method to import a standard setup store.
     *
     * @param context
     *            the context provides the selected parameters and values
     * @param storeName
     *            the name of the store
     * @param contentCatalogs
     *            the list of content catalogs
     */
    protected void importStoreInitialData(final SystemSetupContext context, final String importDirectory, final String storeName,
            final List<String> contentCatalogs) {
        logInfo(context, "Begin importing store [" + storeName + "]");

        for (final String contentCatalog : contentCatalogs) {
            importContentCatalog(context, importDirectory, contentCatalog);
        }

        logInfo(context, "Begin importing advanced personalization rules for [" + storeName + "]");
        final String importRoot = "/" + importDirectory + "/import/project";
        importImpexFile(context, importRoot + "/stores/" + storeName + "/btg.impex", false);
        logInfo(context, "Done importing advanced personalization rules for [" + storeName + "]");

        logInfo(context, "Begin importing POS [" + storeName + "]");
        importImpexFile(context, importRoot + "/stores/" + storeName + "/points-of-service-media.impex", false);
        importImpexFile(context, importRoot + "/stores/" + storeName + "/points-of-service.impex", false);
        logInfo(context, "Done importing POS [" + storeName + "]");

        logInfo(context, "Done importing store [" + storeName + "]");
    }

    protected void importProductCatalog(final SystemSetupContext context, final String importDirectory, final String catalogName) {
        logInfo(context, "Begin importing Product Catalog [" + catalogName + "]");

        // Load Units
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/classifications-units.impex",
                false);

        // Load Categories
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/categories.impex", false);
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/categories-classifications.impex",
                false);

        // Load Suppliers
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/suppliers.impex", false);
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/suppliers-media.impex", false);

        // Load medias for Categories as Suppliers loads some new Categories
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/categories-media.impex", false);

        // Load Products
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/products.impex", false);
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/products-media.impex", false);
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/products-classifications.impex",
                false);

        // Load Products Relations
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/products-relations.impex", false);

        // Load Products Fixes
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/products-fixup.impex", false);

        // Load Prices
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/products-prices.impex", false);

        // Load Promotions
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/promotions.impex", false);

        // Update SOLR Index
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/solr.impex", false);

        // Load Stock Levels
        importImpexFile(context, "/" + importDirectory + "/import/project/productCatalogs/" + catalogName + "ProductCatalog/products-stocklevels.impex", false);

        logInfo(context, "Done importing Product Catalog [" + catalogName + "]");
    }

    protected void importContentCatalog(final SystemSetupContext context, final String importDirectory, final String catalogName) {
        logInfo(context, "Begin importing Content Catalog [" + catalogName + "]");

        final String importRoot = "/" + importDirectory + "/import/project";

        importImpexFile(context, importRoot + "/contentCatalogs/" + catalogName + "ContentCatalog/cms-content.impex", false);
        importImpexFile(context, importRoot + "/contentCatalogs/" + catalogName + "ContentCatalog/cms-navigation.impex", false);
        importImpexFile(context, importRoot + "/contentCatalogs/" + catalogName + "ContentCatalog/email-content.impex", false);
        importImpexFile(context, importRoot + "/contentCatalogs/" + catalogName + "ContentCatalog/webtrekk.impex", false);

        logInfo(context, "Done importing Content Catalog [" + catalogName + "]");
    }

    @Override
    public PerformResult executeCatalogSyncJob(final SystemSetupContext context, final String catalogName) {
        final boolean syncCatalogs = getBooleanSystemSetupParameter(context, IMPORT_SYNC_CATALOGS);
        PerformResult syncCronJobResult = null;
        if (syncCatalogs) {
            logInfo(context, "Executing catalogs sync job  [" + catalogName + "ContentCatalog]");
            syncCronJobResult = super.executeCatalogSyncJob(context, catalogName + "ContentCatalog");
            logInfo(context, "Executed catalogs sync job  [" + catalogName + "ContentCatalog]");
        }
        return syncCronJobResult;
    }
}
