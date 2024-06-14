package com.namics.distrelec.b2b.core.service.pricerow.dao.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import com.namics.distrelec.b2b.core.service.pricerow.dao.DistPriceRowDao;

import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import static java.util.Collections.emptyList;

public class DefaultDistPriceRowDao implements DistPriceRowDao {

    private static final Logger LOG = Logger.getLogger(DefaultDistPriceRowDao.class);

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<DistPriceRowModel> findOrphanedPriceRowsWithLimit(int limit) {
        Set<String> eolOrBlockedStatuses = new HashSet<>(Arrays.asList("60", "61", "62", "90", "91", "99"));

        String query =
          "SELECT DISTINCT {dpr.pk} " +
          "FROM { DistPriceRow AS dpr " +
          "JOIN Product AS p ON {p.pk} = {dpr.product} " +
          "JOIN DistSalesOrgProduct AS dsop ON {dsop.product} = {p.pk} " +
          "JOIN DistSalesStatus AS dss ON {dss.pk} = {dsop.salesStatus} } " +
          "WHERE {dpr.lastModifiedErp} IS NULL " +
          "AND {dss.code} NOT IN (?eolOrBlockedStatuses)";

        FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query);
        fQuery.addQueryParameter("eolOrBlockedStatuses", eolOrBlockedStatuses);
        fQuery.setCount(limit);
        fQuery.setNeedTotal(true);

        try {
            return flexibleSearchService.<DistPriceRowModel> search(fQuery).getResult();
        } catch (Exception e) {
            LOG.error("Error finding orphaned Price Rows", e);
            return emptyList();
        }
    }

}
