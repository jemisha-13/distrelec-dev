/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release;

import com.namics.distrelec.patches.structure.CountryOrganisation;
import com.namics.distrelec.patches.structure.ShopOrganisation;
import de.hybris.platform.patches.AbstractPatch;
import de.hybris.platform.patches.Release;
import de.hybris.platform.patches.actions.PatchAction;
import de.hybris.platform.patches.actions.data.PatchActionData;
import de.hybris.platform.patches.actions.data.PatchActionDataOption;
import de.hybris.platform.patches.data.ImpexHeaderOption;
import de.hybris.platform.patches.data.ImpexImportUnitOption;
import de.hybris.platform.patches.internal.logger.PatchLogger;
import de.hybris.platform.patches.internal.logger.PatchLoggerFactory;
import de.hybris.platform.patches.organisation.ImportLanguage;
import de.hybris.platform.patches.organisation.StructureState;
import de.hybris.platform.patches.utils.StructureStateUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static de.hybris.platform.patches.internal.logger.PatchLogger.LoggingMode.HAC_CONSOLE;


/**
 * Project specific parent class for all patches. It works as facade giving nice API for Patches class and translate calls to not project specific
 * format.
 */
public abstract class AbstractDemoPatch extends AbstractPatch implements DemoPatch {
    private static final PatchLogger LOG = PatchLoggerFactory.getLogger(AbstractDemoPatch.class);
    private static final ImpexHeaderOption STAGED_CATALOG_VERSION = new ImpexHeaderOption();
    private static final ImpexHeaderOption ONLINE_CATALOG_VERSION = new ImpexHeaderOption();
    private static final ImpexHeaderOption[] CATALOG_VERSION_OPTIONS = {STAGED_CATALOG_VERSION, ONLINE_CATALOG_VERSION};

    private PatchAction groovyPatchAction;

    private Set<String> catalogsForSynchronization = new HashSet<>();

    static {
        STAGED_CATALOG_VERSION.setMacro("$catalogVersion=Staged");
        ONLINE_CATALOG_VERSION.setMacro("$catalogVersion=Online");
    }

    public AbstractDemoPatch(final String patchId, final String patchName, final Release release, final StructureState structureState) {
        super(patchId, patchName, release, structureState);
    }

    // ***************************
    // *** CREATE DATA METHODS ***
    // ***************************

    @Override
    public void createProjectData(final StructureState structureState) {
        final boolean update = structureState != this.structureState;
        createGlobalData(structureState, update);
        createShopsData(structureState, update);
        createCountriesData(structureState, update);
    }

    /**
     * This method is responsible for update global data (not organisation specific).
     *
     * @param structureState indicates which structureState should be used to import these global data (see update parameter)
     * @param update         if set to true:<br/> only language specific files will be imported (assumption is that not language specific files were
     *                       imported before since this is just update). It also means that data will uploaded only for languages defined for
     *                       organisations defined in this structureState that weren't introduced for other organisations before
     */
    protected void createGlobalData(final StructureState structureState, final boolean update) {
        Set<ImportLanguage> importLanguages;
        String message;
        if (update) {
            importLanguages = StructureStateUtils.getNewGlobalLanguages(CountryOrganisation.values(), structureState);
            message = "Update global data for {}";
        } else {
            importLanguages = StructureStateUtils.getAllGlobalLanguages(CountryOrganisation.values(), structureState);
            message = "Creating global data for {}";
        }
        final String languages = importLanguages.stream().map(ImportLanguage::getCode).collect(Collectors.joining(","));
        LOG.info(HAC_CONSOLE, message, languages);
        createGlobalData(importLanguages, update);
    }

    /**
     * This method will update shop data with specific structureState.
     *
     * @param structureState indicates which structureState should be used to import these global data (see update parameter)
     * @param update         if set to true:<br/> only language specific files will be imported (assumption is that not language specific files were
     *                       imported before since this is just update). It also means that data will uploaded only for languages defined for
     *                       organisations defined in this structureState that weren't introduced for other organisations before
     */
    protected void createShopsData(final StructureState structureState, final boolean update) {
        final Set<ShopOrganisation> newShops = new HashSet<>();
        final Set<ShopOrganisation> updateShops = new HashSet<>();
        for (final ShopOrganisation shop : ShopOrganisation.values()) {
            if (shop.getStructureState() == structureState) {
                newShops.add(shop);
            } else if (structureState.isAfter(shop.getStructureState())) {
                updateShops.add(shop);
            }
        }
        createShopsData(newShops, false);
        createShopsData(updateShops, update);
    }

    /**
     * This method will update country data with specific structureState.
     *
     * @param structureState indicates which structureState should be used to import these global data (see update parameter)
     * @param update         if set to true:<br/> only language specific files will be imported (assumption is that not language specific files were
     *                       imported before since this is just update). It also means that data will uploaded only for languages defined for
     *                       organisations defined in this structureState that weren't introduced for other organisations before
     */
    private void createCountriesData(final StructureState structureState, final boolean update) {
        for (final CountryOrganisation country : CountryOrganisation.values()) {
            if (country.getStructureState() == structureState || (!update && !country.getStructureState().isAfter(structureState))) {
                LOG.info(HAC_CONSOLE, "Creating country specific data for {}", country.getName());
                createCountryData(country);
            }
        }
    }

    /**
     * This method will update set of given shop organisations with given structureState. Update indicate if only language specific files should be
     * imported.
     */
    protected void createShopsData(final Collection<ShopOrganisation> shops, final boolean update) {
        for (final ShopOrganisation shop : shops) {
            if (update) {
                LOG.info(HAC_CONSOLE, "Update shop specific data for {}", shop.getName());
            } else {
                LOG.info(HAC_CONSOLE, "Creating shop specific data for {}", shop.getName());
            }
            createShopData(shop, StructureStateUtils.getNewLanguages(shop, this.structureState), update);
        }
    }

    /**
     * @deprecated since 1811 This method will update set of given shop organisations with given structureState. Update indicate if only language
     * specific files should be imported.
     */
    @Deprecated(since = "1811",
            forRemoval = true)
    protected void createShopsData(final Collection<ShopOrganisation> shops, final StructureState structureState, final boolean update) {
        createShopsData(shops, update);
    }

    // ***************************
    // ** FACADE IMPORT METHODS **
    // ***************************

    protected void importShopSpecificData(final String fileName,
                                          final Set<ImportLanguage> languages,
                                          final ShopOrganisation shopOrganisation,
                                          final boolean runAgain) {
        importShopSpecificData(fileName, languages, shopOrganisation, runAgain, null);
    }

    protected void importShopSpecificData(final String fileName,
                                          final Set<ImportLanguage> languages,
                                          final ShopOrganisation shopOrganisation,
                                          final boolean runAgain,
                                          final ImpexImportUnitOption[] importOptions) {
        importData(fileName, shopOrganisation, languages, runAgain, importOptions, null);
    }

    protected void importShopCatalogVersionSpecificData(final String fileName,
                                                        final Set<ImportLanguage> languages,
                                                        final ShopOrganisation shopOrganisation,
                                                        final boolean runAgain) {
        importShopCatalogVersionSpecificData(fileName, languages, shopOrganisation, runAgain, null);
    }

    protected void importShopCatalogVersionSpecificData(final String fileName,
                                                        final Set<ImportLanguage> languages,
                                                        final ShopOrganisation shopOrganisation,
                                                        final boolean runAgain,
                                                        final ImpexImportUnitOption[] importOptions) {
        importData(fileName, shopOrganisation, languages, runAgain, importOptions, new ImpexHeaderOption[][]{CATALOG_VERSION_OPTIONS});
    }

    protected void importCountryData(final String fileName, final CountryOrganisation countryOrganisationn) {
        importCountryData(fileName, countryOrganisationn, null);
    }

    protected void importCountryData(final String fileName,
                                     final CountryOrganisation countryOrganisationn,
                                     final ImpexImportUnitOption[] importOptions) {
        importData(fileName, countryOrganisationn, countryOrganisationn.getLanguages(), false, importOptions, null);
    }

    protected void executeGroovyScript(String fileName) {
        PatchActionData data = new PatchActionData(fileName, this);
        data.setOption(PatchActionDataOption.Impex.FILE_NAME, fileName);
        groovyPatchAction.perform(data);
    }

    @Autowired
    public void setGroovyPatchAction(PatchAction groovyPatchAction) {
        this.groovyPatchAction = groovyPatchAction;
    }

    @Override
    @Autowired
    public final void setImportPatchAction(PatchAction importPatchAction) {
        super.setImportPatchAction(importPatchAction);
    }

    @Override
    @Autowired
    public final void setSqlCommandPatchAction(PatchAction sqlCommandPatchAction) {
        super.setSqlCommandPatchAction(sqlCommandPatchAction);
    }

    @Override
    public void registerCatalogForSynchronization(String catalogId) {
        catalogsForSynchronization.add(catalogId);
    }

    @Override
    public void registerCatalogsForSynchronization(Collection<String> catalogIds) {
        catalogsForSynchronization.addAll(catalogIds);
    }

    @Override
    public Set<String> getCatalogsForSyncronization() {
        return Collections.unmodifiableSet(catalogsForSynchronization);
    }
}
