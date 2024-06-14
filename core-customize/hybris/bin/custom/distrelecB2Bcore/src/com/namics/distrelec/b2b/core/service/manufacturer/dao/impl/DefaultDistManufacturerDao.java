/*
 * Copyright 2000-2021 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.service.manufacturer.dao.impl;

import com.google.common.collect.Lists;
import com.namics.distrelec.b2b.core.jalo.DistManufacturerCountry;
import com.namics.distrelec.b2b.core.model.*;
import com.namics.distrelec.b2b.core.service.manufacturer.dao.DistManufacturerDao;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.hybris.platform.servicelayer.util.ServicesUtil.validateParameterNotNull;

/**
 * Default implementation of {@link DistManufacturerDao}
 *
 * @author pbueschi, Namics AG
 */
public class DefaultDistManufacturerDao implements DistManufacturerDao {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultDistManufacturerDao.class);

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Autowired
    private ModelService modelService;

    private static final String MANUFACTURERE_COUNTRY_QUERY = "SELECT {" + DistManufacturerCountryModel.PK + "} FROM {" + DistManufacturerCountryModel._TYPECODE
            + "}  WHERE {" + DistManufacturerCountryModel.MANUFACTURER + "}=(?" + DistManufacturerCountryModel.MANUFACTURER + " ) and {"
            + DistManufacturerCountryModel.COUNTRY + "}=(?" + DistManufacturerCountryModel.COUNTRY + ")";

    private static final String MANUFACTURERE_INDEX_QUERY = "SELECT DISTINCT {m." + DistManufacturerModel.CODE + "}, {m." + DistManufacturerModel.NAME + "}, "
            + "{m." + DistManufacturerModel.URLID + "}, {m." + DistManufacturerModel.NAMESEO + "} FROM {" + DistManufacturerModel._TYPECODE + " AS m JOIN "
            + DistManufacturerCountryModel._TYPECODE + " AS mc ON {m." + DistManufacturerModel.PK + "}={mc." + DistManufacturerCountryModel.MANUFACTURER + "}} "
            + "WHERE {mc." + DistManufacturerCountryModel.VISIBLEONSHOP + " } = 1 AND {mc." + DistManufacturerCountryModel.COUNTRY + "} = (?"
            + DistManufacturerCountryModel.COUNTRY + ")";

    private static final String PUNCHOUTS_VALIDITY_CLOSE = " {" + DistPunchOutFilterModel.VALIDFROMDATE + "} <= (?" + DistPunchOutFilterModel.VALIDFROMDATE
            + ") AND {" + DistPunchOutFilterModel.VALIDUNTILDATE + "} >= (?" + DistPunchOutFilterModel.VALIDUNTILDATE + ")";

    private static final String MANUFACTURER_PUNCHOUT_SUB_QUERY = " AND NOT EXISTS ({{ SELECT {" + DistManufacturerCountryModel.MANUFACTURER + "} FROM {"
            + DistManufacturerPunchOutFilterModel._TYPECODE + " AS filter} WHERE {m." + DistManufacturerModel.PK + "}={filter." + DistManufacturerPunchOutFilterModel.MANUFACTURER + "}"
            + " AND {filter." + DistManufacturerPunchOutFilterModel.ERPCUSTOMERID + "}=(?erpCustomerId) AND {" + DistManufacturerPunchOutFilterModel.SALESORG + "}=(?salesOrg) AND " + PUNCHOUTS_VALIDITY_CLOSE + "}})";

    private static final String MANUFACTURER_WITH_CODE_OR_NAME_LIKE = "SELECT DISTINCT{" + DistManufacturerModel._TYPECODE + "." + DistManufacturerModel.PK + "} FROM  {"
            + DistManufacturerModel._TYPECODE + "} WHERE {" + DistManufacturerModel._TYPECODE + "." + DistManufacturerModel.CODE + "} LIKE ?term " +
            "OR {" + DistManufacturerModel._TYPECODE + "." + DistManufacturerModel.NAME + "} LIKE ?term";

    /**
     * Query to fetch a list of cmsSites given by Manufacturer code.
     */
    private static final String CMSSITES_BY_MANUFACTURER_QUERY = "SELECT DISTINCT({" + CMSSiteModel.PK + "}) FROM {" + CMSSiteModel._TYPECODE + "} WHERE {"
            + CMSSiteModel.COUNTRY + "} in ({{SELECT {" + DistManufacturerCountryModel.COUNTRY + "} from  {" + DistManufacturerCountryModel._TYPECODE
            + "} WHERE {" + DistManufacturerCountryModel.VISIBLEONSHOP + "} = 1 and {" + DistManufacturerCountryModel.MANUFACTURER + "} = (?"
            + DistManufacturerCountryModel.MANUFACTURER + ")}})";

    private static final String MANUFACTURER_PUNCHOUTS_QUERY = "SELECT {" + DistManufacturerPunchOutFilterModel.PK + "} FROM {"
            + DistManufacturerPunchOutFilterModel._TYPECODE + "} WHERE {" + DistManufacturerPunchOutFilterModel.MANUFACTURER + "} = (?"
            + DistManufacturerPunchOutFilterModel.MANUFACTURER + ") AND {" + DistManufacturerPunchOutFilterModel.ERPCUSTOMERID + "} = (?"
            + DistManufacturerPunchOutFilterModel.ERPCUSTOMERID + ") AND " + PUNCHOUTS_VALIDITY_CLOSE;

    @Override
    public List<DistManufacturerModel> findManufacturers() {
        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(DistManufacturerModel.PK).append("} FROM {").append(DistManufacturerModel._TYPECODE).append("} ORDER BY LOWER({")
                .append(DistManufacturerModel.NAME).append("})");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString.toString());

        return flexibleSearchService.<DistManufacturerModel>search(query).getResult();
    }

    @Override
    public List<List<String>> findMiniManufacturers(final CountryModel country) {
        return findManufacturerMenu(country, null);
    }

    @Override
    public List<List<String>> findMiniManufacturersForOCC(CountryModel country) {
        return findManufacturerMenu(country, null);
    }

    @Override
    public List<List<String>> findMiniManufacturers(CountryModel country, B2BUnitModel company) {
        return findManufacturerMenu(country, company);
    }

    private List<List<String>> findManufacturerMenu(CountryModel country, B2BUnitModel company) {
        StringBuilder queryStringBuilder = new StringBuilder(MANUFACTURERE_INDEX_QUERY);
        if (company != null) {
            queryStringBuilder.append(MANUFACTURER_PUNCHOUT_SUB_QUERY);
        }
        queryStringBuilder.append(" ORDER BY {m." + DistManufacturerModel.NAME + "}");

        final FlexibleSearchQuery query = new FlexibleSearchQuery(queryStringBuilder.toString());
        query.addQueryParameter(DistManufacturerCountryModel.COUNTRY, country);

        if (company != null) {
            Date currentDate = new Date();
            query.addQueryParameter(DistManufacturerPunchOutFilterModel.ERPCUSTOMERID, company.getErpCustomerID());
            query.addQueryParameter(DistManufacturerPunchOutFilterModel.SALESORG, company.getSalesOrg());
            query.addQueryParameter(DistPunchOutFilterModel.VALIDFROMDATE, currentDate);
            query.addQueryParameter(DistPunchOutFilterModel.VALIDUNTILDATE, currentDate);
        }

        query.setResultClassList(Lists.newArrayList(String.class, String.class, String.class, String.class));
        return flexibleSearchService.<List<String>>search(query).getResult();
    }

    @Override
    public DistManufacturerModel findManufacturerByCode(final String manufacturerCode) {
        validateParameterNotNull(manufacturerCode, "Manufacturer code must not be null!");

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(DistManufacturerModel.PK).append("} FROM {").append(DistManufacturerModel._TYPECODE).append("} ")
                .append("WHERE {").append(DistManufacturerModel.CODE).append("}=(?").append(DistManufacturerModel.CODE).append(")");

        final Map<String, Object> params = new HashMap<>();
        params.put(DistManufacturerModel.CODE, manufacturerCode);

        final SearchResult<DistManufacturerModel> searchResult = flexibleSearchService.search(queryString.toString(), params);
        final List<DistManufacturerModel> result = searchResult.getResult();
        if (CollectionUtils.isNotEmpty(result)) {
            if (result.size() > 1) {
                LOG.debug("Found multiple DistManufacturer for code: " + manufacturerCode);
            }
            return result.iterator().next();
        }

        return null;
    }

    @Override
    public List<DistManufacturerModel> findManufacturersByCodes(final List<String> manufacturerCodes) {
        validateParameterNotNull(manufacturerCodes, "Manufacturer code must not be null!");

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(DistManufacturerModel.PK).append("} FROM {").append(DistManufacturerModel._TYPECODE).append("} ")
                .append("WHERE {").append(DistManufacturerModel.CODE).append("} IN (?").append(DistManufacturerModel.CODE).append(")");

        final Map<String, Object> params = new HashMap<>();
        params.put(DistManufacturerModel.CODE, manufacturerCodes);

        final SearchResult<DistManufacturerModel> searchResult = flexibleSearchService.search(queryString.toString(), params);
        final List<DistManufacturerModel> result = searchResult.getResult();
        if (CollectionUtils.isNotEmpty(result)) {
            if (result.size() > 1) {
                LOG.debug("Found multiple DistManufacturer for code: " + manufacturerCodes);
            }
            return result;
        }

        return null;
    }

    @Override
    public DistManufacturerModel findManufacturerByUrlId(final String manufacturerUrlId) {
        validateParameterNotNull(manufacturerUrlId, "Manufacturer urlId must not be null!");

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(DistManufacturerModel.PK).append("} FROM {").append(DistManufacturerModel._TYPECODE).append("} ")
                .append("WHERE {").append(DistManufacturerModel.URLID).append("}=(?").append(DistManufacturerModel.URLID).append(")");

        final Map<String, Object> params = new HashMap<>();
        params.put(DistManufacturerModel.URLID, manufacturerUrlId);

        final SearchResult<DistManufacturerModel> searchResult = flexibleSearchService.search(queryString.toString(), params);
        final List<DistManufacturerModel> result = searchResult.getResult();
        if (CollectionUtils.isNotEmpty(result)) {
            if (result.size() > 1) {
                LOG.debug("Found multiple DistManufacturer for urlId: " + manufacturerUrlId);
            }
            return result.iterator().next();
        }
        return null;
    }

    @Override
    public DistManufacturerCountryModel findCountrySpecificManufacturerInformation(final DistManufacturerModel manufacturer, final CountryModel country) {
        validateParameterNotNull(manufacturer, "Manufacturer must not be null!");
        validateParameterNotNull(country, "Country must not be null!");

        final StringBuilder queryString = new StringBuilder();
        queryString.append("SELECT {").append(DistManufacturerCountryModel.PK).append("} FROM {").append(DistManufacturerCountryModel._TYPECODE).append("} ")
                .append("WHERE {").append(DistManufacturerCountryModel.MANUFACTURER).append("} = ?").append(DistManufacturerCountryModel.MANUFACTURER)
                .append(" AND ").append("{").append(DistManufacturerCountryModel.COUNTRY).append("} = ?").append(DistManufacturerCountryModel.COUNTRY);

        final Map<String, Object> params = new HashMap<>();
        params.put(DistManufacturerCountryModel.MANUFACTURER, manufacturer);
        params.put(DistManufacturerCountryModel.COUNTRY, country);

        final SearchResult<DistManufacturerCountryModel> searchResult = flexibleSearchService.search(queryString.toString(), params);
        final List<DistManufacturerCountryModel> result = searchResult.getResult();
        if (CollectionUtils.isNotEmpty(result)) {
            if (result.size() > 1) {
                LOG.debug("Found multiple DistManufacturerCountry for manufacturer with code '" + manufacturer.getCode() + "' and country '"
                        + country.getIsocode() + "'");
            }
            return result.get(0);
        }

        return null;
    }

    @Override
    public List<CMSSiteModel> getAvailableCMSSitesForManufacturer(DistManufacturerModel manufacturer) {
        final Map<String, Object> params = new HashMap<>();
        params.put(DistManufacturerCountry.MANUFACTURER, manufacturer);

        final SearchResult<CMSSiteModel> result = getFlexibleSearchService().search(CMSSITES_BY_MANUFACTURER_QUERY, params);

        return CollectionUtils.isNotEmpty(result.getResult()) ? result.getResult() : ListUtils.EMPTY_LIST;
    }

    @SuppressWarnings("unused")
    private DistManufacturerCountryModel getManufactureInfo(DistManufacturerModel distManufacturer, final CountryModel country) {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(MANUFACTURERE_COUNTRY_QUERY);
        query.addQueryParameter(DistManufacturerCountryModel.MANUFACTURER, distManufacturer);
        query.addQueryParameter(DistManufacturerCountryModel.COUNTRY, country);

        final SearchResult<DistManufacturerCountryModel> result = flexibleSearchService.search(query);
        return result.getCount() > 0 ? result.getResult().get(0) : null;
    }

    @Override
    public List<DistManufacturerModel> findWithCodeOrNameLike(String term, int page, int pageSize) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("term", "%" + term + "%");
        FlexibleSearchQuery query = new FlexibleSearchQuery(MANUFACTURER_WITH_CODE_OR_NAME_LIKE, queryParams);
        query.setStart(page * pageSize);
        query.setCount(pageSize);

        SearchResult<DistManufacturerModel> searchResult = flexibleSearchService.search(query);
        return searchResult.getResult();
    }

    /**
     * Query:
     *
     * SELECT {manu.pk} FROM
     *      {DistManufacturer AS manu
     *       JOIN DistManufacturerCountry AS dmc ON {dmc.manufacturer}={manu.pk}
     *       JOIN Country AS c ON {dmc.country}={c.pk}}
     *      WHERE {c.pk}=?country
     *      AND
     *      NOT EXISTS ({{
     *          SELECT {p.code} FROM {Product AS p
     *                 JOIN DistManufacturer AS m ON {p.manufacturer}={m.pk}
     *                 JOIN DistSalesOrgProduct AS dsop ON {dsop.product}={p.pk}
     *                 JOIN DistSalesStatus AS dss ON {dsop.salesStatus}={dss.pk}
     *                 JOIN DistSalesOrg AS dso ON {dsop.salesOrg}={dso.pk}}
     *                     WHERE {dso.pk}=?salesOrg AND
     *                       {m.pk}={manu.pk} AND
     *                       (
     *                           ({dss.visibleInShop} = 1 AND
     *                            {dss.endOfLifeInShop} = 0 AND
     *                            {dss.buyableInShop} = 1) AND
     *                            NOT EXISTS (
     *                                 {{SELECT {dcpf.pk} FROM
     *                                     {DistCOPunchOutFilter AS dcpf}
     *                                     WHERE {dcpf.product}={p.pk}
     *                                     AND {dcpf.salesOrg}=?salesOrg
     *                                 }})
     *                         )
     *      }})
     *
     * @param country
     * @return
     */
    @Override
    public List<DistManufacturerModel> findManufacturersForRemovalFromCountry(DistSalesOrgModel salesOrg, CountryModel country) {
        String query = "SELECT {manu.pk} FROM\n" +
                "     {DistManufacturer AS manu\n" +
                "      JOIN DistManufacturerCountry AS dmc ON {dmc.manufacturer}={manu.pk}\n" +
                "      JOIN Country AS c ON {dmc.country}={c.pk}}\n" +
                "     WHERE {c.pk}=?country\n" +
                "     AND\n" +
                "     NOT EXISTS ({{\n" +
                "         SELECT {p.code} FROM {Product AS p\n" +
                "                JOIN DistManufacturer AS m ON {p.manufacturer}={m.pk}\n" +
                "                JOIN DistSalesOrgProduct AS dsop ON {dsop.product}={p.pk}\n" +
                "                JOIN DistSalesStatus AS dss ON {dsop.salesStatus}={dss.pk}\n" +
                "                JOIN DistSalesOrg AS dso ON {dsop.salesOrg}={dso.pk}}\n" +
                "                    WHERE {dso.pk}=?salesOrg AND\n" +
                "                      {m.pk}={manu.pk} AND\n" +
                "                      (\n" +
                "                          ({dss.visibleInShop} = 1 AND\n" +
                "                           {dss.endOfLifeInShop} = 0 AND\n" +
                "                           {dss.buyableInShop} = 1) AND\n" +
                "                           NOT EXISTS (\n" +
                "                                {{SELECT {dcpf.pk} FROM\n" +
                "                                    {DistCOPunchOutFilter AS dcpf}\n" +
                "                                    WHERE {dcpf.product}={p.pk}\n" +
                "                                    AND {dcpf.salesOrg}=?salesOrg\n" +
                "                                }})\n" +
                "                        )\n" +
                "     }})";

        Map<String, Object> params = new HashMap<>();
        params.put("salesOrg", salesOrg);
        params.put("country", country);

        return flexibleSearchService.<DistManufacturerModel>search(query, params).getResult();
    }

    /**
     * Query:
     *
     * SELECT {manu.pk} FROM
     *     {DistManufacturer AS manu}
     *     WHERE NOT EXISTS ({{
     *         SELECT * FROM {DistManufacturerCountry AS dmc
     *             JOIN Country AS c ON {dmc.country}={c.pk}}
     *             WHERE {dmc.country}=?country
     *             AND {dmc.manufacturer}={manu.pk}
     *     }})
     *     AND EXISTS ({{
     *         SELECT {p.code} FROM {Product AS p
     *             JOIN DistManufacturer AS m ON {p.manufacturer}={m.pk}
     *             JOIN DistSalesOrgProduct AS dsop ON {dsop.product}={p.pk}
     *             JOIN DistSalesStatus AS dss ON {dsop.salesStatus}={dss.pk}
     *             JOIN DistSalesOrg AS dso ON {dsop.salesOrg}={dso.pk}}
     *
     *             WHERE {dso.pk}=?salesOrg AND
     *                   {m.pk}={manu.pk} AND
     *                   (
     *                       ({dss.visibleInShop} = 1 AND
     *                        {dss.endOfLifeInShop} = 0 AND
     *                        {dss.buyableInShop} = 1) AND
     *                        NOT EXISTS (
     *                             {{SELECT {dcpf.pk} FROM
     *                                 {DistCOPunchOutFilter AS dcpf}
     *                                 WHERE {dcpf.product}={p.pk}
     *                                 AND {dcpf.salesOrg}=?salesOrg
     *                             }})
     *                     )
     *     }})
     *
     * @param salesOrg
     * @param country
     * @return
     */
    @Override
    public List<DistManufacturerModel> findManufacturersForAssignToCountry(DistSalesOrgModel salesOrg, CountryModel country) {
        String query = "SELECT {manu.pk} FROM \n" +
                "    {DistManufacturer AS manu}\n" +
                "    WHERE NOT EXISTS ({{\n" +
                "        SELECT * FROM {DistManufacturerCountry AS dmc\n" +
                "            JOIN Country AS c ON {dmc.country}={c.pk}}\n" +
                "            WHERE {dmc.country}=?country\n" +
                "            AND {dmc.manufacturer}={manu.pk}\n" +
                "    }})\n" +
                "    AND EXISTS ({{\n" +
                "        SELECT {p.code} FROM {Product AS p\n" +
                "            JOIN DistManufacturer AS m ON {p.manufacturer}={m.pk}\n" +
                "            JOIN DistSalesOrgProduct AS dsop ON {dsop.product}={p.pk}\n" +
                "            JOIN DistSalesStatus AS dss ON {dsop.salesStatus}={dss.pk}\n" +
                "            JOIN DistSalesOrg AS dso ON {dsop.salesOrg}={dso.pk}}\n" +
                "\n" +
                "            WHERE {dso.pk}=?salesOrg AND \n" +
                "                  {m.pk}={manu.pk} AND\n" +
                "                  (\n" +
                "                      ({dss.visibleInShop} = 1 AND \n" +
                "                       {dss.endOfLifeInShop} = 0 AND \n" +
                "                       {dss.buyableInShop} = 1) AND\n" +
                "                       NOT EXISTS (\n" +
                "                            {{SELECT {dcpf.pk} FROM \n" +
                "                                {DistCOPunchOutFilter AS dcpf}                \n" +
                "                                WHERE {dcpf.product}={p.pk}\n" +
                "                                AND {dcpf.salesOrg}=?salesOrg\n" +
                "                            }})\n" +
                "                    )\n" +
                "    }})";

        Map<String, Object> params = new HashMap<>();
        params.put("salesOrg", salesOrg);
        params.put("country", country);

        return flexibleSearchService.<DistManufacturerModel>search(query, params).getResult();
    }

    @Override
    public DistManufacturerPunchOutFilterModel findManufacturerByCustomerPunchout(DistManufacturerModel manufacturerModel, B2BUnitModel defaultB2BUnit, Date now){
        FlexibleSearchQuery query = new FlexibleSearchQuery(MANUFACTURER_PUNCHOUTS_QUERY);

        query.addQueryParameter(DistManufacturerPunchOutFilterModel.MANUFACTURER, manufacturerModel);
        query.addQueryParameter(DistManufacturerPunchOutFilterModel.ERPCUSTOMERID, defaultB2BUnit.getErpCustomerID());
        query.addQueryParameter(DistPunchOutFilterModel.VALIDFROMDATE, now);
        query.addQueryParameter(DistPunchOutFilterModel.VALIDUNTILDATE, now);

        final SearchResult<DistManufacturerPunchOutFilterModel> searchResult = flexibleSearchService.search(query);

        if (CollectionUtils.isNotEmpty(searchResult.getResult())) {
            List<DistManufacturerPunchOutFilterModel> results = searchResult.getResult();
            return (results.get(0));
        }
        return null;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public void setFlexibleSearchService(final FlexibleSearchService flexibleSearch) {
        this.flexibleSearchService = flexibleSearch;
    }

    protected FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }


}
