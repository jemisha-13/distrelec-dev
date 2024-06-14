/*
 * Copyright 2000-2012 Namics AG. All rights reserved.
 */
package com.namics.distrelec.b2b.facades.storesession.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import com.namics.distrelec.b2b.core.service.i18n.NamicsCommonI18NService;
import com.namics.distrelec.b2b.core.service.user.DistUserService;
import com.namics.distrelec.b2b.facades.storesession.NamicsStoreSessionFacade;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.commercefacades.storesession.impl.DefaultStoreSessionFacade;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.RegionModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.europe1.constants.Europe1Constants;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

/**
 * DefaultNamicsStoreSessionFacade extends DefaultStoreSessionFacade.
 *
 * @author rhusi, Namics AG
 * @since Namics Extensions 1.0
 */
public class DefaultNamicsStoreSessionFacade extends DefaultStoreSessionFacade implements NamicsStoreSessionFacade {
    private static final Logger LOG = LogManager.getLogger(DefaultNamicsStoreSessionFacade.class);

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistUserService userService;

    @Autowired
    private NamicsCommonI18NService namicsCommonI18NService;

    @Autowired
    @Qualifier("countryConverter")
    private Converter<CountryModel, CountryData> countryConverter;

    @Autowired
    @Qualifier("regionConverter")
    private Converter<RegionModel, RegionData> regionConverter;

    @Autowired
    private CMSSiteService cmsSiteService;

    @Override
    public void initializeSession(final HttpServletRequest request) {
        super.initializeSession(Collections.list(request.getLocales()));
        initializeSessionCountry(request);
    }

    @Override
    public CountryData getCurrentCountry() {
        final CountryModel countryModel = getNamicsCommonI18NService().getCurrentCountry();
        if (countryModel != null) {
            return getCountryConverter().convert(countryModel);
        } else {
            return null;
        }
    }

    @Override
    public void setCurrentCountry(final String isocode) {
        final Collection<CountryModel> countries = getCommonI18NService().getAllCountries();
        Assert.notEmpty(countries, "No supported countries found for the current site.");
        CountryModel countryModel = null;
        for (final CountryModel country : countries) {
            if (StringUtils.equalsIgnoreCase(country.getIsocode(), isocode)) {
                countryModel = country;
                break;
            }
        }

        if (countryModel == null) {
            LOG.info("Country with isocode " + isocode + " is not supported by this site. Switzerland will be set as current country.");
            countryModel = getCommonI18NService().getCountry("CH");
        }
        getNamicsCommonI18NService().setCurrentCountry(countryModel);
    }

    @Override
    public RegionData getCurrentRegion() {
        final RegionModel regionModel = getNamicsCommonI18NService().getCurrentRegion();
        if (regionModel != null) {
            return getRegionConverter().convert(regionModel);
        } else {
            return null;
        }
    }

    @Override
    public void setCurrentRegion(final String isocode) {
        final CountryModel currentCountry = getNamicsCommonI18NService().getCurrentCountry();
        final Collection<RegionModel> regions = currentCountry.getRegions();
        Assert.notEmpty(regions);
        RegionModel regionModel = null;
        for (final RegionModel region : regions) {
            if (StringUtils.equals(region.getIsocode(), isocode)) {
                regionModel = region;
                break;
            }
        }
        Assert.notNull(regionModel, "Region to set is not supported for the current country (" + currentCountry.getIsocode() + ").");
        getNamicsCommonI18NService().setCurrentRegion(regionModel);
    }

    /**
     * initializeSessionCountry
     */
    private void initializeSessionCountry(final HttpServletRequest request) {
        final CountryData currentCountry = this.getCurrentCountry();

        if (currentCountry == null) {
            final CMSSiteModel cmsSite = getCmsSiteService().getCurrentSite();
            String currentCountryIsoCode = null;
            if (cmsSite != null) {
                if (!"7801".equals(cmsSite.getSalesOrg().getCode())) {
                    final String siteLocaleString = cmsSite.getLocale();
                    if (StringUtils.isNotEmpty(siteLocaleString)) {
                        final String[] locale = siteLocaleString.split("_");
                        if (locale.length == 2) {
                            currentCountryIsoCode = locale[1];
                            if (StringUtils.isNotEmpty(currentCountryIsoCode)) {
                                LOG.info("Got no country by GEO targeting. Set country by site locale (Country: " + currentCountryIsoCode + ")");
                            }
                        }
                    }
                } else {
                    currentCountryIsoCode = "EX";
                }
            }

            if (StringUtils.isEmpty(currentCountryIsoCode)) {
                currentCountryIsoCode = getCountryByRequestIfAvailable(request);
            }

            this.setCurrentCountry(currentCountryIsoCode);
        }
    }

    @Override
    public void initializeSession(final List<Locale> preferredLocales) {
        super.initializeSession(preferredLocales);
        initializeSessionUserPriceGroup();
    }

    @Override
    protected void initializeSessionTaxGroup() {
        final UserModel currentUser = getUserService().getCurrentUser();
        if (currentUser.getEurope1PriceFactory_UTG() != null) {
            getSessionService().getCurrentSession().setAttribute(Europe1Constants.PARAMS.UTG, currentUser.getEurope1PriceFactory_UTG());
        } else {
            final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
            if (currentSite != null && currentSite.getSalesOrg() != null && currentSite.getUserTaxGroup() != null) {
                getSessionService().getCurrentSession().setAttribute(Europe1Constants.PARAMS.UTG, currentSite.getUserTaxGroup());
            }
        }
    }

    protected void initializeSessionUserPriceGroup() {
        final UserModel currentUser = getUserService().getCurrentUser();
        if (currentUser.getEurope1PriceFactory_UPG() != null) {
            getSessionService().setAttribute(Europe1Constants.PARAMS.UPG, currentUser.getEurope1PriceFactory_UPG());
        } else {
            final CMSSiteModel currentSite = getCmsSiteService().getCurrentSite();
            if (currentSite != null && currentSite.getSalesOrg() != null && currentSite.getUserPriceGroup() != null) {
                getSessionService().setAttribute(Europe1Constants.PARAMS.UPG, currentSite.getUserPriceGroup());
            }
        }
    }

    @Override
    public void setCurrentLanguage(final String isocode) {
        super.setCurrentLanguage(isocode);
        if (getCartService().hasSessionCart()) {
            final CartModel cart = getCartService().getSessionCart();
            cart.setLanguage(getCommonI18NService().getLanguage(isocode));
            getModelService().save(cart);
        }
    }

    private String getCountryByRequestIfAvailable(final HttpServletRequest request) {
        String currentCountryIsoCode;
        final Locale requestLocale = request.getLocale();
        if (requestLocale != null && StringUtils.isNotEmpty(requestLocale.getCountry())) {
            currentCountryIsoCode = requestLocale.getCountry();
            LOG.info("Got no country by GEO targeting and site locale. Set country by request locale (Country: " + currentCountryIsoCode + ")");
        } else {
            currentCountryIsoCode = Config.getString("default.country.isocode", "ch");
            LOG.info("Got no country by GEO targeting and site locale. Set country by config property 'default.country.isocode' (Country: "
                     + currentCountryIsoCode + ")");
        }
        return currentCountryIsoCode;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistUserService getUserService() {
        return userService;
    }

    public void setUserService(final DistUserService userService) {
        this.userService = userService;
    }

    public NamicsCommonI18NService getNamicsCommonI18NService() {
        return namicsCommonI18NService;
    }

    public void setNamicsCommonI18NService(final NamicsCommonI18NService namicsCommonI18NService) {
        this.namicsCommonI18NService = namicsCommonI18NService;
    }

    public Converter<CountryModel, CountryData> getCountryConverter() {
        return countryConverter;
    }

    public void setCountryConverter(final Converter<CountryModel, CountryData> countryConverter) {
        this.countryConverter = countryConverter;
    }

    public Converter<RegionModel, RegionData> getRegionConverter() {
        return regionConverter;
    }

    public void setRegionConverter(final Converter<RegionModel, RegionData> regionConverter) {
        this.regionConverter = regionConverter;
    }

    public CMSSiteService getCmsSiteService() {
        return cmsSiteService;
    }

    public void setCmsSiteService(final CMSSiteService cmsSiteService) {
        this.cmsSiteService = cmsSiteService;
    }

}
