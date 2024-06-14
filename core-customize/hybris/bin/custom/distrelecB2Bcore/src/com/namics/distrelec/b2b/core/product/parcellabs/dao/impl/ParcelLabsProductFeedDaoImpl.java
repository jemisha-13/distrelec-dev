package com.namics.distrelec.b2b.core.product.parcellabs.dao.impl;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchExecutionService;
import com.namics.distrelec.b2b.core.product.parcellabs.dao.ParcelLabsProductFeedDao;

import de.hybris.platform.servicelayer.internal.dao.AbstractItemDao;

public class ParcelLabsProductFeedDaoImpl extends AbstractItemDao implements ParcelLabsProductFeedDao {

    private static final String FETCH_PRODUCTS_FOR_SPECIFIC_SALES_ORG = "SELECT " +
                                                                        "   {p.code}, " +
                                                                        "   CASE WHEN {p.nameSeo[en]} IS NULL THEN '' ELSE '/' + {p.nameSeo[en]} END " +
                                                                        "   + CASE WHEN {m.nameSeo[en]} IS NULL THEN '' ELSE '-' + {m.nameSeo[en]} END " +
                                                                        "   + CASE WHEN {p.typeNameSeo[en]} IS NULL THEN '' ELSE '-' + {p.typeNameSeo[en]} END " +
                                                                        "   + '/p/' + {p.code} AS ProductURL, " +
                                                                        "({{ " +
                                                                        "   SELECT {media.internalURL} " +
                                                                        "   FROM " +
                                                                        "   { " +
                                                                        "       Media! AS media " +
                                                                        "       JOIN MediaFormat! AS mf " +
                                                                        "           ON {mf.pk} = {media.mediaFormat} " +
                                                                        "           AND {mf.qualifier} = 'portrait_small' " +
                                                                        "   } " +
                                                                        "   WHERE {media.mediaContainer} = CASE WHEN {p.primaryImage} IS NOT NULL THEN {p.primaryImage} ELSE {p.illustrativeImage} END " +
                                                                        "}}) AS MediaURL " +
                                                                        "FROM { " +
                                                                        "   Product AS p JOIN DistSalesOrgProduct AS dsop ON {dsop.product} = {p.pk} " +
                                                                        "   JOIN DistSalesOrg AS dso ON {dso.pk} = {dsop.salesOrg} " +
                                                                        "   JOIN DistManufacturer AS m ON {m.pk} = {p.manufacturer} " +
                                                                        "} " +
                                                                        "WHERE {dso.code} = ?salesOrg";

    private static final String FETCH_PRODUCTS_FOR_SPECIFIC_SALES_ORG_SINCE_LAST_EXPORT = FETCH_PRODUCTS_FOR_SPECIFIC_SALES_ORG + " AND "
                                                                                            + "( "
                                                                                            + "{p.modifiedtime} > ?previousDate OR "
                                                                                            + "{p.lastModifiedErp} > ?previousDate"
                                                                                            + " )";

    @Autowired
    private DistFlexibleSearchExecutionService flexibleSearchExecutionService;

    @Override
    public ResultSet findProductsForSalesOrg(String salesOrg) {
        return flexibleSearchExecutionService.execute(FETCH_PRODUCTS_FOR_SPECIFIC_SALES_ORG, Collections.singletonMap("salesOrg", salesOrg));
    }

    @Override
    public ResultSet findChangedProductsForSalesOrg(String salesOrg, Date previousExportDate) {
        if (previousExportDate != null) {
            Map<String, Object> params = new HashMap<>();
            params.put("previousDate", previousExportDate);
            params.put("salesOrg", salesOrg);

            return flexibleSearchExecutionService.execute(FETCH_PRODUCTS_FOR_SPECIFIC_SALES_ORG_SINCE_LAST_EXPORT, params);
        }

        return findProductsForSalesOrg(salesOrg);
    }
}
