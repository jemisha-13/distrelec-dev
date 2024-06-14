/*
 * Copyright 2000-2021 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.manufacturer.impl;

import com.namics.distrelec.b2b.core.model.DistManufacturerCountryModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.core.service.manufacturer.dao.DistManufacturerDao;
import com.namics.distrelec.b2b.core.util.DistDateTimeUtils;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.internal.service.AbstractBusinessService;
import de.hybris.platform.servicelayer.user.UserService;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * Default implementation of {@link DistManufacturerService}
 *
 * @author pbueschi, Namics AG
 */
public class DefaultDistManufacturerService extends AbstractBusinessService implements DistManufacturerService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistManufacturerService.class);

    @Autowired
    private DistManufacturerDao distManufacturerDao;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Autowired
    private UserService userService;

    @Override
    public List<DistManufacturerModel> getManufacturerList() {
        return distManufacturerDao.findManufacturers();
    }

    @Override
    public List<List<String>> getMiniManufacturerList() {
        final CMSSiteModel currentCmsSite = cmsSiteService.getCurrentSite();
        if (currentCmsSite != null && currentCmsSite.getCountry() != null) {
            if(!userService.isAnonymousUser(userService.getCurrentUser())){
                B2BCustomerModel currentCustomer = (B2BCustomerModel) userService.getCurrentUser();
                return distManufacturerDao.findMiniManufacturers(currentCmsSite.getCountry(), currentCustomer.getDefaultB2BUnit());
            }
            return distManufacturerDao.findMiniManufacturers(currentCmsSite.getCountry());
        }
        return ListUtils.EMPTY_LIST;
    }

    @Override
    public DistManufacturerModel getManufacturerByCode(final String manufacturerCode) {
        final DistManufacturerModel manufacturer = distManufacturerDao.findManufacturerByCode(manufacturerCode);
        if (manufacturer != null) {
            return manufacturer;
        }
        throw new UnknownIdentifierException("Manufacturer with code '" + manufacturerCode + "' not found!");
    }

    @Override
    public List<DistManufacturerModel> getManufacturersByCodes(final List<String> manufacturerCodes) {
        final List<DistManufacturerModel> manufacturer = distManufacturerDao.findManufacturersByCodes(manufacturerCodes);
        if (manufacturer != null) {
            return manufacturer;
        }
        throw new UnknownIdentifierException("Manufacturer with code '" + manufacturerCodes + "' not found!");
    }

    @Override
    public DistManufacturerModel getManufacturerByUrlId(final String manufacturerUrlId) {
        final DistManufacturerModel manufacturer = distManufacturerDao.findManufacturerByUrlId(manufacturerUrlId);
        if (manufacturer != null) {
            return manufacturer;
        }
        throw new UnknownIdentifierException("Manufacturer with urlId '" + manufacturerUrlId + "' not found!");
    }

    @Override
    public DistManufacturerCountryModel getCountrySpecificManufacturerInformation(final DistManufacturerModel manufacturer) {
        final CMSSiteModel currentCmsSite = cmsSiteService.getCurrentSite();
        if (currentCmsSite != null && currentCmsSite.getCountry() != null) {
            return getCountrySpecificManufacturerInformation(manufacturer, currentCmsSite.getCountry());
        } else {
            return null;
        }
    }

    @Override
    public DistManufacturerCountryModel getCountrySpecificManufacturerInformation(final DistManufacturerModel manufacturer, final CountryModel country) {
        return distManufacturerDao.findCountrySpecificManufacturerInformation(manufacturer, country);
    }

    @Override
    public List<DistManufacturerModel> searchByCodeOrName(String term, int page, int pageSize) {
        return distManufacturerDao.findWithCodeOrNameLike(term, page, pageSize);
    }

    @Override
    public boolean updateManufacturerIndexList(final CountryModel country, final DistSalesOrgModel distSalesOrgModel) {
        return removeAssignedManufacturersWithoutProducts(country, distSalesOrgModel) &
        assignNotAssignedManufacturersWithProducts(country, distSalesOrgModel);
    }

    @Override
    public List<List<String>> getMiniManufacturerListForOCC()
    {
        final CMSSiteModel currentCmsSite = cmsSiteService.getCurrentSite();
        if (currentCmsSite != null && currentCmsSite.getCountry() != null) {
            return distManufacturerDao.findMiniManufacturersForOCC(currentCmsSite.getCountry());
        }
        return ListUtils.EMPTY_LIST;
    }

    private boolean removeAssignedManufacturersWithoutProducts(CountryModel country, DistSalesOrgModel distSalesOrgModel) {
        boolean success = true;
        List<DistManufacturerModel> manufacturersForRemoval = distManufacturerDao.findManufacturersForRemovalFromCountry(distSalesOrgModel, country);
        LOG.info("Found {} manufacturers for removal from country {}", manufacturersForRemoval.size(), country.getIsocode());

        for (DistManufacturerModel manufacturer : manufacturersForRemoval) {
            try {
                DistManufacturerCountryModel manufacturerCountry = distManufacturerDao.findCountrySpecificManufacturerInformation(manufacturer, country);
                getModelService().remove(manufacturerCountry);
                LOG.info("Removed manufacturer {} from country {}", manufacturer.getCode(), country.getIsocode());
            } catch (Exception e) {
                LOG.error("Failed to remove manufacturer to country relation {} -> {}. Error: {}", manufacturer.getCode(), country.getIsocode(), e.getMessage());
                success = false;
            }
        }
        return success;
    }

    private boolean assignNotAssignedManufacturersWithProducts(CountryModel country, DistSalesOrgModel distSalesOrgModel) {
        boolean success = true;
        List<DistManufacturerModel> manufacturersForAssignment = distManufacturerDao.findManufacturersForAssignToCountry(distSalesOrgModel, country);
        LOG.info("Found {} manufacturers for assignment to country {}", manufacturersForAssignment.size(), country.getIsocode());

        for(DistManufacturerModel manufacturer : manufacturersForAssignment){
            try {
                DistManufacturerCountryModel manufacturerCountry = createManufacturerCountryEntry(manufacturer, country);
                getModelService().save(manufacturerCountry);
                LOG.info("Assigned manufacturer {} to country {}", manufacturer.getCode(), country.getIsocode());
            } catch (Exception e) {
                LOG.error("Failed to create manufacturer to country relation {} -> {}. Error: {}", manufacturer.getCode(), country.getIsocode(), e.getMessage());
                success = false;
            }
        }

        return success;
    }

    private DistManufacturerCountryModel createManufacturerCountryEntry(DistManufacturerModel manufacturer, final CountryModel country) {
        final DistManufacturerCountryModel manufacturerCountry = getModelService().create(DistManufacturerCountryModel.class);
        manufacturerCountry.setManufacturer(manufacturer);
        manufacturerCountry.setCountry(country);
        manufacturerCountry.setVisibleOnShop(true);
        return manufacturerCountry;
    }

    @Override
    public List<CMSSiteModel> getAvailableCMSSitesForManufacturer(DistManufacturerModel manufacture) {
        return distManufacturerDao.getAvailableCMSSitesForManufacturer(manufacture);
    }

    @Override
    public DistManufacturerPunchOutFilterModel getManufacturerByCustomerPunchout(String manufacturerCode) {
        if(userService.isAnonymousUser(userService.getCurrentUser())){
            return null;
        }

        B2BCustomerModel currentCustomer = (B2BCustomerModel) userService.getCurrentUser();
        if(currentCustomer == null || currentCustomer.getDefaultB2BUnit() == null){
            return null;
        }
        DistManufacturerModel manufacturerModel = distManufacturerDao.findManufacturerByCode(manufacturerCode);
        if(manufacturerModel == null){
            return null;
        }

        return distManufacturerDao.findManufacturerByCustomerPunchout(manufacturerModel, currentCustomer.getDefaultB2BUnit(), new Date());
    }

}
