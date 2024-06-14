/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;
import com.namics.distrelec.b2b.core.model.DistCOPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistProductPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.salesorg.DistSalesOrgService;

import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

/**
 * ProductCountryElementConverter.
 * 
 * @author csieber, Namics AG
 * @since Namics Extensions 1.0
 * 
 */
public class ProductCOPunchOutFilterElementConverter extends AbstractCountryElementConverter {
    private static final Logger LOG = LogManager.getLogger(ProductCOPunchOutFilterElementConverter.class);

    private static final String SUPPRESS_KEY = "Values/Value[@AttributeID='calc_suppress_lov']";

    private static final String BRAND = "distrelec";

    private static final DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final String MIN_DATE = "19700101000001";
    private static final String MAX_DATE = "99991231235959";

    @Autowired
    private DistSalesOrgService distSalesOrgService;

    public void convert(final Element source, final ProductModel parent) {
        try {
            final Map<String, DistProductPunchOutFilterModel> targetMap = new HashMap<String, DistProductPunchOutFilterModel>();
            final Map<String, DistProductPunchOutFilterModel> actualMap = new HashMap<String, DistProductPunchOutFilterModel>();

            final List<DistProductPunchOutFilterModel> targetList = new ArrayList<DistProductPunchOutFilterModel>();

            if (parent.getPunchOutFilters() != null) {
                for (final DistProductPunchOutFilterModel punchOutFilter : parent.getPunchOutFilters()) {
                    if (punchOutFilter instanceof DistCOPunchOutFilterModel) {
                        // Update all country punchout filters with the current date
                        final DistCOPunchOutFilterModel distCOPunchoutFilterModel = (DistCOPunchOutFilterModel) punchOutFilter;
                        final String countryIsoCode = StringUtils.upperCase(distCOPunchoutFilterModel.getCountry().getIsocode());
                        distCOPunchoutFilterModel.setLastModifiedErp(new Date());
                        actualMap.put(countryIsoCode, punchOutFilter);
                    } else {
                        // Punchout filter was no country punchout, so it will stay
                        targetList.add(punchOutFilter);
                    }
                }
            }

            final List<CountryModel> countries = getCountries(source);
            if (CollectionUtils.isNotEmpty(countries)) {
                for (final CountryModel country : countries) {
                    final DistSalesOrgModel currentSalesOrg = getDistSalesOrgService().getSalesOrgForCountryCodeAndBrandCode(country.getIsocode(), BRAND);
                    final String countryIsoCode = StringUtils.upperCase(country.getIsocode());
                    if (actualMap.containsKey(country.getIsocode())) {
                        targetMap.put(countryIsoCode, actualMap.remove(countryIsoCode));
                    } else if (currentSalesOrg != null && !(DistErpSystem.SAP.equals(currentSalesOrg.getErpSystem()))) {
                        // Just create country punchout filters for non SAP based sales organisations (DISTRELEC-4052)
                        try {
                            final DistCOPunchOutFilterModel model = createModel(country, parent);
                            targetMap.put(countryIsoCode, model);
                        } catch (final ModelNotFoundException e) {
                            LOG.warn("Could not find DistSalesOrgModel for country [{}] and brand [{}]", new Object[] { countryIsoCode, BRAND });
                        } catch (final ParseException e) {
                            LOG.error("Could not parse date", e);
                        }
                    } else {
                        LOG.debug("Skip punchout for product [{}] in country [{}]. Punchout filters for SalesOrg [{}] are imported from SAP!",
                                new Object[] { parent.getCode(), countryIsoCode, currentSalesOrg.getCode() });
                    }
                }
            }

            getModelService().removeAll(actualMap.values());

            targetList.addAll(targetMap.values());
            getModelService().saveAll(targetList);
        } catch (final ModelSavingException e) {
            LOG.error("Could not process Element {}", new Object[] { source.asXML() }, e);
        }
    }

    private DistCOPunchOutFilterModel createModel(final CountryModel country, final ProductModel product) throws ParseException, ModelNotFoundException {
        final DistCOPunchOutFilterModel model = new DistCOPunchOutFilterModel();
        final DistSalesOrgModel salesOrg = getDistSalesOrgService().getSalesOrgForCountryCodeAndBrandCode(country.getIsocode(), BRAND);
        model.setSalesOrg(salesOrg);
        model.setValidFromDate(df.parse(MIN_DATE));
        model.setValidUntilDate(df.parse(MAX_DATE));
        model.setLastModifiedErp(new Date());
        model.setCountry(country);
        model.setProduct(product);
        return model;
    }

    private List<CountryModel> getCountries(final Element source) {
        final List<CountryModel> countries = new ArrayList<CountryModel>();
        final String countryString = source.valueOf(SUPPRESS_KEY);
        if (StringUtils.isNotEmpty(countryString)) {
            final String[] countryArray = countryString.split(";");
            if (countryArray != null) {
                final Set<String> countrySet = new HashSet<String>(Arrays.asList(countryArray));
                for (final String countryCode : countrySet) {
                    if (StringUtils.isNotBlank(countryCode)) {
                        final String value = StringUtils.lowerCase(countryCode.toUpperCase(), Locale.ROOT);
                        final CountryModel country = getOrCreateCountryForIsoCode(value);
                        if (country != null) {
                            countries.add(country);
                        }
                    }
                }
            } else {
                LOG.debug("No countries set on xml element {}", new Object[] { source.asXML() });
                throw new ElementConverterException("Error converting ProductCOPunchOutFilter");
            }
        }
        return countries;
    }

    public DistSalesOrgService getDistSalesOrgService() {
        return distSalesOrgService;
    }

    public void setDistSalesOrgService(final DistSalesOrgService distSalesOrgService) {
        this.distSalesOrgService = distSalesOrgService;
    }

}
