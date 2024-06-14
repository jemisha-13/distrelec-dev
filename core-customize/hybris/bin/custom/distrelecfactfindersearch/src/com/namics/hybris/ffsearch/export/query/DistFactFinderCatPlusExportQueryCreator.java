/*
 * Copyright 2000-2014 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.query;

import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.SALESUNIT;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchQueryCreator;
import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;
import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderCatPlusExportColumns;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;

import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Query Creator for Catalog+ products export.
 * 
 * @author ceberle, Namics AG
 * @since Namics Distrelec 1.1
 */
public class DistFactFinderCatPlusExportQueryCreator implements DistFlexibleSearchQueryCreator {

    private static final String CONCAT_SELECT_VALUE = "\", ";

    // @formatter:off
    /*
        SELECT
            {p.code} AS "ProductNumber",
            {p.catPlusSupplierAID} AS "TypeName",
            {p.name[de]} AS "Title",
            CASE
                WHEN {dsu.name:o} IS NOT NULL THEN {dsu.name:o}
                WHEN {dsu.nameErp:o} IS NOT NULL THEN {dsu.nameErp:o}
                WHEN {u.name:o} IS NOT NULL THEN '1 ' || {u.name:o}
                ELSE '-'
            END AS "SalesUnit",
            {p.manufacturerAID} AS "ManufacturerAid",
            {p.manufacturerName} AS "Manufacturer",
            '/p/serviceplus/' || {p.code} AS "ProductURL",
            ({{
                SELECT {media.URL}
                FROM { Media! AS media
                    JOIN MediaFormat! AS mf ON {mf.pk} = {media.mediaFormat} AND {mf.qualifier} = 'portrait_small'
                }
                WHERE {media.mediaContainer} = {p.primaryImage}
            }}) AS "ImageURL",
            ({{
                SELECT CASE WHEN (count(*) > 0) THEN '|' || STRING_AGG(prices.price, '|') WITHIN GROUP (ORDER BY prices.price) || '|' ELSE '' END
                FROM ({{
                    SELECT distinct
                        ({cur.isocode} || ';' || 'Net'   || ';' || {pr.minqtd} || '=' || {pr.price} / {pr.unitFactor}) AS price,
                        {pr.product} AS product
                    FROM { DistPriceRow AS pr
                        JOIN Currency AS cur ON {pr.currency} = {cur.pk}
                    }
                    WHERE ({pr.startTime} IS NULL OR {pr.startTime} <= NOW())
                        AND ({pr.endTime} IS NULL OR {pr.endTime} >= NOW())
                }}
                UNION
                {{
                    SELECT distinct
                        ({cur.isocode} || ';' || 'Net'   || ';Min=' || {pr.price} / {pr.unitFactor}) AS price,
                        {pr.product} AS product
                    FROM { DistPriceRow AS pr
                        JOIN Currency AS cur ON {pr.currency} = {cur.pk}
                    }
                    WHERE ({pr.startTime} IS NULL OR {pr.startTime} <= NOW())
                        AND ({pr.endTime} IS NULL OR {pr.endTime} >= NOW())
                        AND  {pr.minqtd} =  ({{
                            SELECT min({minpr.minqtd})
                            FROM { DistPriceRow AS minpr }
                            WHERE {minpr.product} = {pr.product}
                            AND {minpr.net} = {pr.net}
                            AND {minpr.ug} = {pr.ug}
                            AND {minpr.currency} = {pr.currency}
                        }})
                }}) prices
                WHERE prices.product = {p.pk}
            }})  AS "Price"
        FROM {Product AS p
            JOIN CatalogVersion AS cv ON {p.catalogVersion} = {cv.pk}
            JOIN Catalog AS c ON {cv.catalog} = {c.pk}
            LEFT JOIN DistSalesUnit AS dsu ON {dsu.pk} = {p.salesUnit}
            LEFT JOIN Unit AS u ON {u.pk} = {p.unit}
        }
        WHERE {c.id} = 'distrelecCatalogPlusProductCatalog'
            AND {cv.version} = 'Online'
    */
    // @formatter:on

    @Autowired
    private DistSqlUtils distSqlUtils;

    @Override
    public String createQuery() {
        final StringBuilder query = new StringBuilder();

        // Select
        query.append("SELECT ");
        query.append("  {p.").append(ProductModel.CODE).append("} AS \"").append(DistFactFinderExportColumns.PRODUCT_NUMBER.getValue())
                .append(CONCAT_SELECT_VALUE);
        query.append("  {p.").append(ProductModel.CATPLUSSUPPLIERAID).append("} AS \"").append(DistFactFinderExportColumns.TYPENAME.getValue())
                .append(CONCAT_SELECT_VALUE);
        query.append("  {p.").append(ProductModel.NAME).append("[de]} AS \"").append(DistFactFinderExportColumns.TITLE.getValue()).append(CONCAT_SELECT_VALUE);
        appendSalesUnitSelect(query).append(" AS \"").append(SALESUNIT.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("  {p.").append(ProductModel.MANUFACTURERAID).append("} AS \"").append(DistFactFinderCatPlusExportColumns.MANUFACTURER_AID.getValue())
                .append(CONCAT_SELECT_VALUE);
        query.append("  {p.").append(ProductModel.MANUFACTURERNAME).append("} AS \"").append(DistFactFinderExportColumns.MANUFACTURER.getValue())
                .append(CONCAT_SELECT_VALUE);
        query.append("  '/p/serviceplus/' || {p.").append(ProductModel.CODE).append("} AS \"").append(DistFactFinderExportColumns.PRODUCT_URL.getValue())
                .append(CONCAT_SELECT_VALUE);
        appendImageUrlSelect(query).append(" AS \"").append(DistFactFinderExportColumns.IMAGE_URL.getValue()).append(CONCAT_SELECT_VALUE);
        appendPriceSelect(query).append(" AS \"").append(DistFactFinderExportColumns.PRICE.getValue()).append("\" ");

        // From
        query.append("FROM {").append(ProductModel._TYPECODE).append(" AS p ");
        query.append("  JOIN ").append(CatalogVersionModel._TYPECODE).append(" AS cv ON {p.").append(ProductModel.CATALOGVERSION).append("} = {cv.")
                .append(CatalogVersionModel.PK).append("} ");
        query.append("  JOIN ").append(CatalogModel._TYPECODE).append(" AS c ON {cv.").append(CatalogVersionModel.CATALOG).append("} = {c.")
                .append(CatalogModel.PK).append("} ");
        query.append("  LEFT JOIN ").append(DistSalesUnitModel._TYPECODE).append(" AS dsu ");
        query.append("      ON {dsu.").append(DistSalesUnitModel.PK).append("} = {p.").append(ProductModel.SALESUNIT).append("} ");
        query.append("  LEFT JOIN ").append(UnitModel._TYPECODE).append(" AS u ");
        query.append("      ON {u.").append(UnitModel.PK).append("} = {p.").append(ProductModel.UNIT).append("} ");
        query.append("} ");

        // Where
        query.append("WHERE {c.").append(CatalogModel.ID).append("} = '").append(DistConstants.Catalog.CATALOG_PLUS_ID).append("' ");
        query.append("  AND {cv.").append(CatalogVersionModel.VERSION).append("} = '").append(DistConstants.CatalogVersion.ONLINE).append("' ");

        return query.toString();
    }

    protected StringBuilder appendSalesUnitSelect(final StringBuilder query) {
        query.append(" CASE ");
        query.append("   WHEN {dsu.").append(DistSalesUnitModel.NAME).append(":o} IS NOT NULL THEN {dsu.").append(DistSalesUnitModel.NAME).append(":o} ");
        query.append("   WHEN {dsu.").append(DistSalesUnitModel.NAMEERP).append(":o} IS NOT NULL THEN {dsu.").append(DistSalesUnitModel.NAMEERP).append(":o} ");
        query.append("   WHEN {u.").append(UnitModel.NAME).append(":o} IS NOT NULL THEN '1 ' || {u.").append(UnitModel.NAME).append(":o} ");
        query.append("   ELSE '-' ");
        query.append(" END ");
        return query;
    }

    protected StringBuilder appendImageUrlSelect(final StringBuilder query) {
        query.append("({{ ");
        query.append("  SELECT {media.").append(MediaModel.URL).append("} ");
        query.append("  FROM { ").append(MediaModel._TYPECODE).append("! AS media ");
        query.append("      JOIN ").append(MediaFormatModel._TYPECODE).append("! AS mf ");
        query.append("          ON {mf.").append(MediaFormatModel.PK).append("} = {media.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("          AND {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.PORTRAIT_SMALL).append("' ");
        query.append("  } ");
        query.append("  WHERE {media.").append(MediaModel.MEDIACONTAINER).append("} = {p.").append(ProductModel.PRIMARYIMAGE).append("} ");
        query.append("}}) ");
        return query;
    }

    protected StringBuilder appendPriceSelect(final StringBuilder query) {
        query.append("({{ ");
        query.append("  SELECT CASE WHEN (count(*) > 0) THEN '|' || ").append(stringAgg("prices.price", "|", "prices.price")).append(" || '|' ELSE '' END ");
        query.append("  FROM ({{ ");
        query.append("      SELECT distinct ");
        query.append("          ({cur.").append(CurrencyModel.ISOCODE).append("} || ';' || 'Net'   || ';' || {pr.").append(DistPriceRowModel.MINQTD)
                .append("} ");
        query.append("          || '=' || {pr.").append(DistPriceRowModel.PRICE).append("} / {pr.").append(DistPriceRowModel.UNITFACTOR)
                .append("}) AS price, ");
        query.append("          {pr.").append(DistPriceRowModel.PRODUCT).append("} AS product ");
        query.append("      FROM { ").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("          JOIN ").append(CurrencyModel._TYPECODE).append(" AS cur ON {pr.").append(DistPriceRowModel.CURRENCY).append("} = {cur.")
                .append(CurrencyModel.PK).append("} ");
        query.append("      } ");
        query.append("      ").append(where(" ({pr.".concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME)
                .concat("} <= ").concat(now()).concat(") ")
                .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME)
                .concat("} >= ").concat(now()).concat(") ")));
        query.append("  }} ");
        query.append("  UNION ");
        query.append("  {{ ");
        query.append("      SELECT distinct ");
        query.append("          ({cur.").append(CurrencyModel.ISOCODE).append("} || ';' || 'Net'   || ';Min=' ");
        query.append("          || {pr.").append(DistPriceRowModel.PRICE).append("} / {pr.").append(DistPriceRowModel.UNITFACTOR).append("}) AS price, ");
        query.append("          {pr.").append(DistPriceRowModel.PRODUCT).append("} AS product ");
        query.append("      FROM { ").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("          JOIN ").append(CurrencyModel._TYPECODE).append(" AS cur ON {pr.").append(DistPriceRowModel.CURRENCY).append("} = {cur.")
                .append(CurrencyModel.PK).append("} ");
        query.append("      } ");
        query.append("      ").append(where(" ({pr.".concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME)
                .concat("} <= ").concat(now()).concat(") ")
                .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME)
                .concat("} >= ").concat(now()).concat(") ")));
        query.append("          AND  {pr.").append(DistPriceRowModel.MINQTD).append("} =  ({{ ");
        query.append("          SELECT min({minpr.").append(DistPriceRowModel.MINQTD).append("}) ");
        query.append("              FROM { ").append(DistPriceRowModel._TYPECODE).append(" AS minpr } ");
        query.append("          ").append(where(" {minpr.".concat(DistPriceRowModel.PRODUCT).concat("} = {pr.").concat(DistPriceRowModel.PRODUCT).concat("} ")
                .concat(" AND {minpr.").concat(DistPriceRowModel.NET).concat("} = {pr.").concat(DistPriceRowModel.NET).concat("} ")
                .concat(" AND {minpr.").concat(DistPriceRowModel.UG).concat("} = {pr.").concat(DistPriceRowModel.UG).concat("} ")
                .concat(" AND {minpr.").concat(DistPriceRowModel.CURRENCY).concat("} = {pr.").concat(DistPriceRowModel.CURRENCY).concat("} ")));
        query.append("      }}) ");
        query.append("  }}) prices ");
        query.append("  WHERE prices.product = {p.").append(ProductModel.PK).append("} ");
        query.append("}}) ");
        return query;
    }

    protected String now() {
        return distSqlUtils.now();
    }

    protected String stringAgg(String expression) {
        return distSqlUtils.stringAgg(expression);
    }

    protected String stringAgg(String expression, String connector, String orderBy) {
        return distSqlUtils.stringAgg(expression, connector, orderBy);
    }

    protected String where(String where) {
        return distSqlUtils.where(where);
    }
}
