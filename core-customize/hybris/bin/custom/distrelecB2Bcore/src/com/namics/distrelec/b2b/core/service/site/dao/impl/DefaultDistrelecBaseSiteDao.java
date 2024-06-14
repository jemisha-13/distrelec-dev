/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.site.dao.impl;

import java.util.List;

import com.namics.distrelec.b2b.core.util.DistUtils;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.DistBrandModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.site.dao.DistrelecBaseSiteDao;

import de.hybris.platform.basecommerce.site.dao.impl.BaseSiteDaoImpl;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

/**
 * Default implementation of {@link DistrelecBaseSiteDao}.
 *
 * @author rmeier, Namics AG
 * @since Namics Extensions 1.0
 */
public class DefaultDistrelecBaseSiteDao extends BaseSiteDaoImpl implements DistrelecBaseSiteDao {

    private static final Logger LOG = Logger.getLogger(DefaultDistrelecBaseSiteDao.class);

    /**
     * Find if a base site with the passed siteUid exists, if not
     */
    @Override
    public BaseSiteModel findBaseSiteByUID(String siteUid) {
        BaseSiteModel baseSite = super.findBaseSiteByUID(siteUid);

        if (baseSite == null && DistUtils.containsMinus(siteUid)) {
            baseSite = super.findBaseSiteByUID(DistUtils.revertSiteUidMinusToUnderscore(siteUid));
        }

        return baseSite;
    }

    @Override
    public CMSSiteModel findBaseSiteByCountryAndBrand(final String country, final String brand) {
        // Search in country of CMSSite as well as in delivery countries.
        //
        // SELECT x.pk
        // FROM (
        // {{
        // SELECT {s.pk}
        // FROM {CMSSite AS s
        // JOIN DistSalesOrg AS so ON {s.salesOrg}={so.pk}
        // JOIN DistBrand AS br ON {so.brand}={br.pk}
        // JOIN Country AS c ON {s.country}={c.pk}}
        // WHERE {br.code} = ?brand AND {c.isocode} = ?country
        // }}
        // UNION
        // {{
        // SELECT {s.pk}
        // FROM {CMSSite AS s
        // JOIN DistSalesOrg AS so ON {s.salesOrg}={so.pk}
        // JOIN DistBrand AS br ON {so.brand}={br.pk}
        // JOIN StoresForCMSSite AS s2s ON {s.pk}={s2s.source}
        // JOIN BaseStore AS bs ON {s2s.target}={bs.pk}
        // JOIN BaseStore2CountryRel AS bs2c ON {bs.pk}={bs2c.target}
        // JOIN Country AS c ON {bs2c.source}={c.pk}}
        // WHERE {br.code} = ?brand AND {c.isocode} = ?country
        // }}
        // ) x

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT x.").append(CMSSiteModel.PK).append(" FROM ").append("(").append("{{").append("SELECT {s.").append(CMSSiteModel.PK)
                .append("} ").append("FROM {").append(CMSSiteModel._TYPECODE).append(" AS s ").append("JOIN ").append(DistSalesOrgModel._TYPECODE)
                .append(" AS so ON {s.").append(CMSSiteModel.SALESORG).append("}={so.").append(DistSalesOrgModel.PK).append("} ").append("JOIN ")
                .append(DistBrandModel._TYPECODE).append(" AS br ON {so.").append(DistSalesOrgModel.BRAND).append("}={br.").append(DistBrandModel.PK)
                .append("} ").append("JOIN ").append(CountryModel._TYPECODE).append(" AS c ON {s.").append(CMSSiteModel.COUNTRY).append("}={c.")
                .append(CountryModel.PK).append("}} ").append("WHERE {br.").append(DistBrandModel.CODE).append("} = ?").append(DistSalesOrgModel.BRAND)
                .append(" AND {c.").append(CountryModel.ISOCODE).append("} = ?").append(CMSSiteModel.COUNTRY).append("}} ").append("UNION ").append("{{")
                .append("SELECT {s.").append(CMSSiteModel.PK).append("} ").append("FROM {").append(CMSSiteModel._TYPECODE).append(" AS s ").append("JOIN ")
                .append(DistSalesOrgModel._TYPECODE).append(" AS so ON {s.").append(CMSSiteModel.SALESORG).append("}={so.").append(DistSalesOrgModel.PK)
                .append("} ").append("JOIN ").append(DistBrandModel._TYPECODE).append(" AS br ON {so.").append(DistSalesOrgModel.BRAND).append("}={br.")
                .append(DistBrandModel.PK).append("} ").append("JOIN ").append(BaseStoreModel._STORESFORCMSSITE).append(" AS s2s ON {s.")
                .append(CMSSiteModel.PK).append("}={s2s.").append(LinkModel.SOURCE).append("} ").append("JOIN ").append(BaseStoreModel._TYPECODE)
                .append(" AS bs ON {s2s.").append(LinkModel.TARGET).append("}={bs.").append(BaseStoreModel.PK).append("} ").append("JOIN ")
                .append(BaseStoreModel._BASESTORE2COUNTRYREL).append(" AS bs2c ON {bs.").append(BaseStoreModel.PK).append("}={bs2c.").append(LinkModel.TARGET)
                .append("} ").append("JOIN ").append(CountryModel._TYPECODE).append(" AS c ON {bs2c.").append(LinkModel.SOURCE).append("}={c.")
                .append(CountryModel.PK).append("}} ").append("WHERE {br.").append(DistBrandModel.CODE).append("} = ?brand AND {c.")
                .append(CountryModel.ISOCODE).append("} = ?country").append("}} ").append(") x");

        FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        query.addQueryParameter(CMSSiteModel.COUNTRY, country.toUpperCase());
        query.addQueryParameter(DistSalesOrgModel.BRAND, brand);

        final SearchResult<CMSSiteModel> searchResult = search(query);
        List<CMSSiteModel> result = searchResult.getResult();
        if (CollectionUtils.isNotEmpty(result)) {
            return result.stream()
                    .filter(s -> StringUtils.endsWith(s.getUid(), country.toUpperCase()))
                    .findFirst()
                    .orElse(result.get(0));
        } else {
            LOG.error("No BaseSite item was found with search country: " + country + " and brand: " + brand);
        }

        return null;
    }

    @Override
    public CMSSiteModel findBaseSiteByCountry(final CountryModel country) {
        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(CMSSiteModel.PK).append("} FROM {").append(CMSSiteModel._TYPECODE).append("} WHERE {")
                .append(CMSSiteModel.COUNTRY).append("}=(?").append(CMSSiteModel.COUNTRY).append(")");

        FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        query.addQueryParameter(CMSSiteModel.COUNTRY, country);

        final SearchResult<CMSSiteModel> result = search(query);
        final List<CMSSiteModel> cmsSites = result.getResult();
        if (CollectionUtils.isNotEmpty(cmsSites)) {
            if (CollectionUtils.size(cmsSites) > 1) {
                LOG.info("More than one CMSSite found for country " + country.getIsocode() + ". Returning first entry");
            }
            return cmsSites.get(0);
        }

        return null;
    }

    @Override
    public CMSSiteModel findBaseSiteByCountryAndSalesOrg(CountryModel country, DistSalesOrgModel salesOrg) {
        // Search in country of CMSSite as well as in delivery countries.
        //
        // SELECT x.pk
        // FROM (
        // {{
        // SELECT {s.pk}
        // FROM {CMSSite AS s
        // JOIN DistSalesOrg AS so ON {s.salesOrg}={so.pk}
        // JOIN Country AS c ON {s.country}={c.pk}}
        // WHERE {s.uid} != 'distrelec' AND {so.code} = ?salesOrg AND {c.isocode} = ?country
        // }}
        // UNION
        // {{
        // SELECT {s.pk}
        // FROM {CMSSite AS s
        // JOIN DistSalesOrg AS so ON {s.salesOrg}={so.pk}
        // JOIN StoresForCMSSite AS s2s ON {s.pk}={s2s.source}
        // JOIN BaseStore AS bs ON {s2s.target}={bs.pk}
        // JOIN BaseStore2CountryRel AS bs2c ON {bs.pk}={bs2c.target}
        // JOIN Country AS c ON {bs2c.source}={c.pk}}
        // WHERE {s.uid} != 'distrelec' AND {so.code} = ?salesOrg AND {c.isocode} = ?country
        // }}
        // ) x

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT x.").append(CMSSiteModel.PK).append(" FROM ({{ SELECT {s.").append(CMSSiteModel.PK).append("}  FROM {")
                .append(CMSSiteModel._TYPECODE).append(" AS s JOIN ").append(DistSalesOrgModel._TYPECODE).append(" AS so ON {s.").append(CMSSiteModel.SALESORG)
                .append("}={so.").append(DistSalesOrgModel.PK).append("} JOIN ").append(CountryModel._TYPECODE).append(" AS c ON {s.")
                .append(CMSSiteModel.COUNTRY).append("}={c.").append(CountryModel.PK).append("}} WHERE {s.").append(CMSSiteModel.UID)
                .append("}!='distrelec' AND {so.").append(DistSalesOrgModel.CODE).append("} = ?")
                .append(DistSalesOrgModel._TYPECODE).append(" AND {c.").append(CountryModel.ISOCODE).append("} = ?").append(CMSSiteModel.COUNTRY)
                .append("}} UNION {{ SELECT {s.").append(CMSSiteModel.PK).append("} FROM {").append(CMSSiteModel._TYPECODE).append(" AS s JOIN ")
                .append(DistSalesOrgModel._TYPECODE).append(" AS so ON {s.").append(CMSSiteModel.SALESORG).append("}={so.").append(DistSalesOrgModel.PK)
                .append("} JOIN ").append(BaseStoreModel._STORESFORCMSSITE).append(" AS s2s ON {s.").append(CMSSiteModel.PK).append("}={s2s.")
                .append(LinkModel.SOURCE).append("} JOIN ").append(BaseStoreModel._TYPECODE).append(" AS bs ON {s2s.").append(LinkModel.TARGET)
                .append("}={bs.").append(BaseStoreModel.PK).append("} JOIN ").append(BaseStoreModel._BASESTORE2COUNTRYREL).append(" AS bs2c ON {bs.")
                .append(BaseStoreModel.PK).append("}={bs2c.").append(LinkModel.TARGET).append("} JOIN ").append(CountryModel._TYPECODE)
                .append(" AS c ON {bs2c.").append(LinkModel.SOURCE).append("}={c.").append(CountryModel.PK).append("}} WHERE {s.")
                .append(CMSSiteModel.UID).append("}!='distrelec' AND {so.")
                .append(DistSalesOrgModel.CODE).append("} = ?").append(DistSalesOrgModel._TYPECODE).append(" AND {c.").append(CountryModel.ISOCODE)
                .append("} = ?").append(CountryModel._TYPECODE).append("}}) x");

        FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());
        query.addQueryParameter(CountryModel._TYPECODE, country.getIsocode().toUpperCase());
        query.addQueryParameter(DistSalesOrgModel._TYPECODE, salesOrg.getCode());

        final SearchResult<CMSSiteModel> result = search(query);
        final List<CMSSiteModel> cmsSites = result.getResult();
        if (CollectionUtils.isNotEmpty(cmsSites)) {
            if (CollectionUtils.size(cmsSites) > 1) {
                LOG.info("More than one CMSSite found for country " + country.getIsocode() + " and sales organistation " + salesOrg.getCode()
                        + ". Returning first entry");
            }
            return cmsSites.get(0);
        }

        return null;
    }
}
