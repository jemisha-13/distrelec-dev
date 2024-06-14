/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.namics.distrelec.patches.release;

import com.namics.distrelec.patches.structure.CountryOrganisation;
import com.namics.distrelec.patches.structure.ShopOrganisation;
import de.hybris.platform.patches.Patch;
import de.hybris.platform.patches.organisation.ImportLanguage;

import java.util.Collection;
import java.util.Set;


/**
 * Patches demo specific interface. Adds global, shop and country specific methods. Check {@link SimpleDemoPatch} for default implementation.
 */
public interface DemoPatch extends Patch {
    /**
     * Creates global data for given languages.
     *
     * @param languages           to be imported
     * @param updateLanguagesOnly if import data only for new languages
     */
    void createGlobalData(final Set<ImportLanguage> languages, boolean updateLanguagesOnly);

    /**
     * Creates shop specific data.
     *
     * @param unit                shop for which data should be imported
     * @param languages           to be imported
     * @param updateLanguagesOnly import data only for new languages
     */
    void createShopData(final ShopOrganisation unit, final Set<ImportLanguage> languages, final boolean updateLanguagesOnly);

    /**
     * Creates country specific data.
     *
     * @param country for which data should be imported
     */
    void createCountryData(final CountryOrganisation country);

    /**
     * Register catalog id for syncronisation after all patches have been executed
     *
     * @param catalogId
     */
    void registerCatalogForSynchronization(String catalogId);

    /**
     * Register catalog ids for syncronisation after all patches have been executed
     *
     * @param catalogIds
     */
    void registerCatalogsForSynchronization(Collection<String> catalogIds);

    /**
     * Get list of catalog ids registered for synchronization
     *
     * @return List of content catalog ids
     */
    Set<String> getCatalogsForSyncronization();
}
