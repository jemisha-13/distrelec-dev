/*
 * Copyright 2000-2021 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.manufacturer;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;

import java.util.List;

/**
 * This interface provides methods to handling the manufacturers.
 *
 * @author pbueschi, Namics AG
 */
public interface DistManufacturerService {

    /**
     * Gets list of all manufacturers ordered by name.
     *
     * @return List<DistManufacturerModel>
     */
    List<DistManufacturerModel> getManufacturerList();

    /**
     * Gets alphabetically sorted map with character as key and list of manufacturers as value.
     *
     * @return Map<String, List < DistManufacturerModel>>
     */
    List<List<String>> getMiniManufacturerList();

    /**
     * Gets alphabetically sorted map with character as key and list of manufacturers as value.
     *
     * @return Map<String, List <DistManufacturerModel>>
     */
    List<List<String>> getMiniManufacturerListForOCC();

    /**
     * Gets manufacturer by its code.
     *
     * @param manufacturerCode
     * @return DistManufacturerModel
     */
    DistManufacturerModel getManufacturerByCode(final String manufacturerCode);

    /**
     * Gets manufacturer by its code.
     *
     * @param list of manufacturerCode
     * @return List<DistManufacturerModel>
     */
    List<DistManufacturerModel> getManufacturersByCodes(final List<String> manufacturerCodes);

    /**
     * Gets manufacturer by its urlId.
     *
     * @param manufacturerUrlId
     * @return DistManufacturerModel
     */
    DistManufacturerModel getManufacturerByUrlId(final String manufacturerUrlId);

    /**
     * Gets the country specific manufacturer information. The current country is identified by the current CMSSite.
     *
     * @param manufacturer
     * @return DistManufacturerCountryModel
     */
    DistManufacturerCountryModel getCountrySpecificManufacturerInformation(DistManufacturerModel manufacturer);

    /**
     * Search manufacturers by code or name
     *
     * @param term
     * @param page
     * @param pageSize
     * @return
     */
    List<DistManufacturerModel> searchByCodeOrName(String term, int page, int pageSize);

    /**
     * Gets the country specific manufacturer information.
     *
     * @param manufacturer
     * @param country
     * @return DistManufacturerCountryModel
     */
    DistManufacturerCountryModel getCountrySpecificManufacturerInformation(DistManufacturerModel manufacturer, CountryModel country);

    /**
     * Update Manufacturer country specific Flag information.
     */
    boolean updateManufacturerIndexList(final CountryModel country, final DistSalesOrgModel distSalesOrgModel);

    /**
     * To get available CMSSites for Manufacturer.
     */
    List<CMSSiteModel> getAvailableCMSSitesForManufacturer(DistManufacturerModel manufacture);

    DistManufacturerPunchOutFilterModel getManufacturerByCustomerPunchout(String manufacturerCode);
}
