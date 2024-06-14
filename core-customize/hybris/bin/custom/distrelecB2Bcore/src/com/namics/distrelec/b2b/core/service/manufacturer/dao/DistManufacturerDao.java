/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.manufacturer.dao;

import java.util.Date;
import java.util.List;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;

import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;

/**
 * Interface for manufacturer DAO.
 * 
 * @author pbueschi, Namics AG
 */
public interface DistManufacturerDao {

    /**
     * Finds all manufacturers ordered by name.
     * 
     * @return List<DistManufacturerModel>
     */
    List<DistManufacturerModel> findManufacturers();

    /**
     * Finds all manufacturers ordered by name and return just the code, name and the urlId.
     * 
     * @return List<List<String>>
     */
    List<List<String>> findMiniManufacturers(final CountryModel country);

    /**
     * Finds all manufacturers ordered by name and return just the code, name and the urlId.
     *
     * @return List<List<String>>
     */
    List<List<String>> findMiniManufacturersForOCC(final CountryModel country);


    List<List<String>> findMiniManufacturers(final CountryModel country, final B2BUnitModel company);

    /**
     * Finds manufacturer by its code.
     * 
     * @param manufacturerCode
     * @return DistManufacturerModel
     */
    DistManufacturerModel findManufacturerByCode(final String manufacturerCode);

    /**
     * Finds manufacturer by its code.
     * 
     * @param manufacturerCodes
     * @return List<DistManufacturerModel>
     */
    List<DistManufacturerModel> findManufacturersByCodes(final List<String> manufacturerCodes);

    /**
     * Finds manufacturer by its urlId.
     * 
     * @param manufacturerUrlId
     * @return DistManufacturerModel
     */
    DistManufacturerModel findManufacturerByUrlId(final String manufacturerUrlId);

    /**
     * Finds country specific manufacturer information by a manufacturer and a country.
     * 
     * @param manufacturer
     * @param country
     * @return DistManufacturerCountryModel
     */
    DistManufacturerCountryModel findCountrySpecificManufacturerInformation(DistManufacturerModel manufacturer, CountryModel country);

    /**
     * 
     * @param manufacture
     * @return
     */
    List<CMSSiteModel> getAvailableCMSSitesForManufacturer(DistManufacturerModel manufacture);

    /**
     * Find manufacturers where code or name is like term
     * @param term
     * @param page
     * @param pageSize
     * @return
     */
    List<DistManufacturerModel> findWithCodeOrNameLike(String term, int page, int pageSize);

    /**
     * Find list of manufacturers that were assigned to country, but no longer have active products and should be removed
     * @param country
     * @return
     */
    List<DistManufacturerModel> findManufacturersForRemovalFromCountry(DistSalesOrgModel salesOrg, CountryModel country);

    /**
     * Find list of manufacturers that are not assigned to country, but have active products and should be assigned
     * @param country
     * @return
     */
    List<DistManufacturerModel> findManufacturersForAssignToCountry(DistSalesOrgModel salesOrg, CountryModel country);

    DistManufacturerPunchOutFilterModel findManufacturerByCustomerPunchout(DistManufacturerModel manufacturer, B2BUnitModel defaultB2BUnit, Date now);
}
