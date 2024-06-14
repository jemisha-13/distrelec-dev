package com.namics.hybris.ffsearch.export.query;

import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchQueryCreator;
import com.namics.distrelec.b2b.core.model.DistPromotionLabelModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;
import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportPromotionColumnAttributes;

import de.hybris.platform.core.model.product.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;

public class DistFactFinderErpExportQueryCreator implements DistFlexibleSearchQueryCreator {

    @Autowired
    private DistSqlUtils distSqlUtils;

    /*
     * (non-Javadoc)
     *
     * @see com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchQueryCreator#createQuery()
     *
     * YOU FIND THE FF ERP EXPORT QUERY HERE: /distrelecfactfindersearch/resources/distrelecfactfindersearch/sql/ff_erp_export.sql
     * You can find the previous incarnation at the directory above but called previous_ff_erp_export.sql
     */

    @Override
    public String createQuery() {
        final StringBuilder query = new StringBuilder();

        // old header
        // "SELECT prizeRows.productCode, prizeRows.Price, singlePrize.singleMinPrice, CASE WHEN salesUnit.amount IS NOT NULL THEN
        // TO_NVARCHAR((singlePrize.singleMinPrice / salesUnit.amount)) ELSE '' END AS \"singleUnitPrice\", singlePrize.specialPrice,
        // standardPrize.standardPrice, standardPrize.standardPriceStart, standardPrize.standardPriceEnd, discountPrize.discountPrice,
        // discountPrize.discountPriceStart, discountPrize.discountPriceEnd, salesStatus.buyable, salesStatus.ItemsMin,
        // salesStatus.ItemsStep, salesStatus.Bestseller, stockInfo.InStock, stockInfo.totalInStock, stockInfo.availableInPickup,
        // stockInfo.pickupStock, stockInfo.availableFast, stockInfo.availableSlow FROM ");

        query.append("SELECT prizeRows.productCode, prizeRows.Price, CASE WHEN salesUnit.amount IS NOT NULL THEN ").append(toNvarchar("(singlePrize.singleMinPrice / salesUnit.amount)")).append(" ELSE '' END AS \"singleUnitPrice\", standardPrize.standardNetPrice, standardPrize.standardPriceStart, standardPrize.standardPriceEnd, discountPrize.discountNetPrice, discountPrize.discountPriceStart, discountPrize.discountPriceEnd, salesStatus.buyable, salesStatus.ItemsMin, salesStatus.ItemsStep, salesStatus.Bestseller, stockInfo.InStock, stockInfo.totalInStock, stockInfo.availableInPickup, stockInfo.pickupStock, stockInfo.availableFast, stockInfo.availableSlow FROM ");

        // prices query:
        query.append("({{ ");
        query.append("SELECT prices.productCode AS productCode, ");
        query.append("CASE WHEN (count(*) > 0) THEN '|' + ").append(stringAgg("prices.Price", null, "prices.minqtd")).append(" ELSE '' END AS Price ");
        query.append("FROM ");
        query.append("({{ ");
        query.append("SELECT scalePrice.productpk AS productpk, ");
        query.append("scalePrice.price AS price, ");
        query.append("scalePrice.minqtd AS minqtd, ");
        query.append("scalePrice.productCode AS productCode, ");
        query.append("scalePrice.priceType AS priceType ");
        query.append("FROM ");
        query.append("({{ ");
        query.append("SELECT {pr.product} AS productpk, ");
        query.append("( ").append(concat("{cur.isocode}", ";", "Gross", ";", "{pr.minqtd}", "=", "(1 + ({tax.value}/100)) * {pr.price} / {pr.unitFactor}", "|", "{cur.isocode}", ";", "Net", ";", "{pr.minqtd}", "=", "{pr.price} / {pr.unitFactor}", "|")).append(" ) AS price, ");
        query.append("{pr.minqtd} AS minqtd, ");
        query.append("{p.code} AS productCode, ");
        query.append("DENSE_RANK() OVER (PARTITION BY {p.pk} ORDER BY {pct.priority} ASC) AS rankValue, ");
        query.append("{pct.code} AS priceType ");
        query.append("FROM ");
        query.append("{ ");
        query.append("Product AS p ");
        query.append("JOIN DistPriceRow AS pr ON {p.pk} = {pr.product} ");
        query.append("JOIN DistErpPriceConditionType AS pct ON {pr.erpPriceConditionType} = {pct.pk} ");
        query.append("JOIN DistSalesOrgProduct AS dsop ON {p.pk} = {dsop.product} ");
        query.append("JOIN DistSalesOrg AS dso ON {dsop.salesOrg} = {dso.pk} AND {dso.pk} = (?DistSalesOrg) ");
        query.append("JOIN Currency AS cur ON {pr.currency} = {cur.pk} ");
        query.append("JOIN CMSSite AS cms ON {pr.ug} = {cms.userPriceGroup} AND {cms.pk} = (?CMSSite) ");
        query.append("JOIN TaxRow AS tr ON {cms.userTaxGroup} = {tr.ug} ");
        query.append("JOIN Tax AS tax ON {tr.tax} = {tax.pk} AND {tr.pg} = {dsop.productTaxGroup} ");
        query.append("} ");
        query.append(where("({pr.startTime} IS NULL OR {pr.startTime} <= (?Date)) ")
                .concat("AND ({pr.endTime} IS NULL OR {pr.endTime} >= (?Date)) ")
                .concat("AND {pr.matchValue} =  ({{ ")
                .concat("SELECT max({matchvaluepr.matchValue}) ")
                .concat("FROM { DistPriceRow AS matchvaluepr } ")
                .concat(where("{matchvaluepr.product} = {pr.product} "
                    .concat("AND {matchvaluepr.net} = {pr.net} ")
                    .concat("AND {matchvaluepr.ug} = {pr.ug} ")
                    .concat("AND {matchvaluepr.currency} = {pr.currency} ")
                    .concat("AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date)) ")
                    .concat("AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date)) ")))
                    .concat(("}}) ")));
        query.append("}}) scalePrice ");
        query.append(where("scalePrice.rankValue = 1 "));
        query.append("}} ");
        query.append("UNION ");
        query.append("{{ ");
        query.append("SELECT minPrice.productpk AS productpk, ");
        query.append("minPrice.price AS price, ");
        query.append("minPrice.minqtd AS minqtd, ");
        query.append("minPrice.productCode AS productCode, ");
        query.append("minPrice.priceType as priceType ");
        query.append("FROM ");
        query.append("({{ ");
        query.append("SELECT {pr.product} AS productpk, ");
        query.append("( ").append(concat("{cur.isocode}", ";", "Gross", ";Min=", "(1 + ({tax.value}/100)) * {pr.price} / {pr.unitFactor}", "|", "{cur.isocode}", ";", "Net", ";Min=", "{pr.price} / {pr.unitFactor}", "|")).append(" ) AS price, ");
        query.append("{pr.minqtd} AS minqtd, ");
        query.append("{p.code} AS productCode, ");
        query.append("DENSE_RANK() OVER (PARTITION BY {p.pk} ORDER BY {pr.startTime} DESC) AS rankValue, ");
        query.append("{pct.code} AS priceType ");
        query.append("FROM ");
        query.append("{ ");
        query.append("Product AS p ");
        query.append("JOIN DistPriceRow AS pr ON {p.pk} = {pr.product} ");
        query.append("JOIN DistErpPriceConditionType AS pct ON {pr.erpPriceConditionType} = {pct.pk} ");
        query.append("JOIN DistSalesOrgProduct AS dsop ON {p.pk} = {dsop.product} ");
        query.append("JOIN DistSalesOrg AS dso ON {dsop.salesOrg} = {dso.pk} AND {dso.pk} = (?DistSalesOrg) ");
        query.append("JOIN Currency AS cur ON {pr.currency} = {cur.pk} ");
        query.append("JOIN CMSSite AS cms ON {pr.ug} = {cms.userPriceGroup} AND {cms.pk} = (?CMSSite) ");
        query.append("JOIN TaxRow AS tr ON {cms.userTaxGroup} = {tr.ug} AND {tr.pg} = {dsop.productTaxGroup} ");
        query.append("JOIN Tax AS tax ON {tr.tax} = {tax.pk} ");
        query.append("} ");
        query.append(where("({pr.startTime} IS NULL OR {pr.startTime} <= (?Date)) "
                .concat("AND ({pr.endTime} IS NULL OR {pr.endTime} >= (?Date)) ")
                .concat("AND  {pr.minqtd} = ({{ ")
                .concat("SELECT min({minpr.minqtd}) ")
                .concat("FROM { DistPriceRow AS minpr } ")
                .concat(where("{minpr.product} = {pr.product} "
                    .concat("AND {minpr.net} = {pr.net} ")
                    .concat("AND {minpr.ug} =  {pr.ug} ")
                    .concat("AND {minpr.currency} = {pr.currency} ")
                    .concat("AND ({minpr.startTime} IS NULL OR {minpr.startTime} <= (?Date)) ")
                    .concat("AND ({minpr.endTime} IS NULL OR {minpr.endTime} >= (?Date)) ")
                    .concat("AND {minpr.matchValue} =  ({{ ")
                    .concat("SELECT max({matchvaluepr.matchValue}) ")
                    .concat("FROM { DistPriceRow AS matchvaluepr } ")
                    .concat(where("{matchvaluepr.product} = {minpr.product} "
                        .concat("AND {matchvaluepr.net} = {minpr.net} ")
                        .concat("AND {matchvaluepr.ug} = {minpr.ug} ")
                        .concat("AND {matchvaluepr.currency} = {minpr.currency} ")
                        .concat("AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date)) ")
                        .concat("AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date)) ")))
                    .concat("}}) ")))
                .concat("}}) ")
                .concat("AND {pr.matchValue} =  ({{ ")
                .concat("SELECT max({matchvaluepr.matchValue}) ")
                .concat("FROM { DistPriceRow AS matchvaluepr } ")
                .concat(where("{matchvaluepr.product} = {pr.product} "
                    .concat("AND {matchvaluepr.net} = {pr.net} ")
                    .concat("AND {matchvaluepr.ug} = {pr.ug} ")
                    .concat("AND {matchvaluepr.currency} = {pr.currency} ")
                    .concat("AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date)) ")
                    .concat("AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date)) "))
                .concat("}}) "))));
        query.append("}}) minPrice ");
        query.append(where("minPrice.rankValue = 1 "));
        query.append("}}) prices ");
        query.append("GROUP BY prices.productCode ");
        query.append("}}) prizeRows ");

        query.append("LEFT JOIN ({{ ");
        query.append("SELECT minPrice.productCode, sum(minPrice.singleMinPrice) AS singleMinPrice, sum(minPrice.specialPrice) AS specialPrice ");
        query.append("FROM ");
        query.append("({{ ");
        query.append("SELECT innerSelect.productCode AS productCode, innerSelect.singleMinPrice AS singleMinPrice, innerSelect.specialPrice AS specialPrice ");
        query.append("FROM ");
        query.append("({{ ");
        query.append("SELECT {p.pk} AS productpk, ");
        query.append("{p.code} AS productCode, ");
        query.append("({pr.price} / {pr.unitFactor}) AS singleMinPrice, ");
        query.append("(CASE WHEN {pct.code} = 'ZN00' THEN 1 ELSE 0 END) AS specialPrice, ");
        query.append("DENSE_RANK() OVER (PARTITION BY {p.pk} ORDER BY {pct.priority}) AS rankValue ");
        query.append("FROM ");
        query.append("{ ");
        query.append("Product AS p ");
        query.append("JOIN DistPriceRow AS pr ON {p.pk} = {pr.product} ");
        query.append("JOIN DistErpPriceConditionType AS pct ON {pr.erpPriceConditionType} = {pct.pk} ");
        query.append("JOIN DistSalesOrgProduct AS dsop ON {p.pk} = {dsop.product} ");
        query.append("JOIN DistSalesOrg AS dso ON {dsop.salesOrg} = {dso.pk} AND {dso.pk} = (?DistSalesOrg) ");
        query.append("JOIN Currency AS cur ON {pr.currency} = {cur.pk} ");
        query.append("JOIN CMSSite AS cms ON {pr.ug} = {cms.userPriceGroup} AND {cms.pk} = (?CMSSite) ");
        query.append("JOIN TaxRow AS tr ON {cms.userTaxGroup} = {tr.ug} AND {tr.pg} = {dsop.productTaxGroup} ");
        query.append("JOIN Tax AS tax ON {tr.tax} = {tax.pk} ");
        query.append("} ");
        query.append(where("({pr.startTime} IS NULL OR {pr.startTime} <= (?Date)) "
                .concat("AND ({pr.endTime} IS NULL OR {pr.endTime} >= (?Date)) ")
                .concat("AND {pr.minqtd} =  ({{ ")
                .concat("SELECT min({minpr.minqtd}) ")
                .concat("FROM { DistPriceRow AS minpr } ")
                .concat(where("{minpr.product} = {pr.product} "
                    .concat("AND {minpr.net} = {pr.net} ")
                    .concat("AND {minpr.ug} = {pr.ug} ")
                    .concat("AND {minpr.currency} = {pr.currency} ")
                    .concat("AND ({minpr.startTime} IS NULL OR {minpr.startTime} <= (?Date)) ")
                    .concat("AND ({minpr.endTime} IS NULL OR {minpr.endTime} >= (?Date)) ")
                    .concat("AND {minpr.matchValue} =  ({{ ")
                    .concat("SELECT max({matchvaluepr.matchValue}) ")
                    .concat("FROM { DistPriceRow AS matchvaluepr } ")
                    .concat(where("{matchvaluepr.product} = {minpr.product} "
                        .concat("AND {matchvaluepr.net} = {minpr.net} ")
                        .concat("AND {matchvaluepr.ug} = {minpr.ug} ")
                        .concat("AND {matchvaluepr.currency} = {minpr.currency} ")
                        .concat("AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date)) ")
                        .concat("AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date)) "))
                    .concat("}}) "))))
                .concat("}}) ")
                .concat("AND {pr.matchValue} =  ({{ ")
                .concat("SELECT max({matchvaluepr.matchValue}) ")
                .concat("FROM { DistPriceRow AS matchvaluepr } ")
                .concat(where("{matchvaluepr.product} = {pr.product} "
                    .concat(" AND {matchvaluepr.net} = {pr.net} ")
                    .concat(" AND {matchvaluepr.ug} = {pr.ug} ")
                    .concat(" AND {matchvaluepr.currency} = {pr.currency} ")
                    .concat(" AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date)) ")
                    .concat(" AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date)) ")))
                .concat(" }}) "))
            .concat(" }}) innerSelect "));
        query.append(where("innerSelect.rankValue = 1 "));
        query.append("}} ");
        query.append("UNION ");
        query.append("{{ ");
        query.append("SELECT {p.code} AS productCode, 0 AS singleMinPrice, 0 AS specialPrice ");
        query.append("FROM {Product AS p} ");
        query.append("}}) minPrice ");
        query.append("GROUP BY ");
        query.append("minPrice.productCode ");
        query.append("}}) singlePrize ");
        query.append("ON prizeRows.productCode = singlePrize.productCode ");

        // buyable (saleStatus) query:
        query.append("INNER JOIN ({{ ");
        query.append("SELECT {p.code} AS productCode, {dsu.amount} AS amount ");
        query.append("FROM {Product as p LEFT JOIN DistSalesUnit AS dsu ON {dsu.pk} = {p.salesUnit}} ");
        query.append("}}) salesUnit ");
        query.append("on prizeRows.productCode = salesUnit.productCode ");

        // promo labels
        // query.append("INNER JOIN ({{ ");
        // query.append("SELECT {p.code} AS productCode, ");
        // appendPromotionLabelsSelect(query);
        // query.append("AS PromotionLabels ");
        // query.append("FROM {Product AS p ");
        // query.append("JOIN DistSalesOrgProduct AS dsop ON {p.pk} = {dsop.product} ");
        // query.append("LEFT JOIN ProductCountry AS pc ON {pc.product} = {p.pk} AND {pc.country} = (?Country)} ");
        // query.append("}}) promoLabels ");
        // query.append("on singlePrize.productCode = promoLabels.productCode ");

        // stock query:
        query.append("INNER JOIN ({{ ");
        query.append(
                "SELECT s.productCode AS productCode, (CASE WHEN sum(s.totalInStock) <= 0 THEN sum(s.pickupStock) WHEN sum(s.totalInStock) > 0 THEN sum(s.totalInStock) ELSE 0 END) AS totalInStock, (CASE WHEN sum(s.inStock) >= 100 THEN 2 WHEN sum(s.inStock) = 75 THEN 1 WHEN sum(s.inStock) = 50 THEN 1 WHEN sum(s.inStock) = 25 THEN 1 WHEN sum(s.inStock) < 50 THEN 0 ELSE 0 END) AS InStock, sum(s.availableInPickup) AS availableInPickup, sum(s.pickupStock) AS pickupStock, sum(s.stockFast) AS availableFast, sum(s.stockSlow) AS availableSlow ");
        query.append("FROM ( ");
        query.append("{{ ");
        query.append(
                "SELECT {p.code} AS productCode, sum({sl.available}) AS totalInStock, (CASE WHEN sum({sl.available}) > 0 THEN 100 ELSE 0 END) AS InStock, 0 AS availableInPickup, 0 AS pickupStock, sum({sl.available}) AS stockFast, 0 AS stockSlow ");
        query.append("FROM {Product AS p JOIN StockLevel AS sl ON {p.code} = {sl.productCode}} ");
        query.append("WHERE ");
        query.append("( ");
        query.append("{sl.warehouse} IN ");
        query.append("({{ ");
        query.append("SELECT {wh.pk} ");
        query.append("FROM {Warehouse AS wh ");
        query.append("JOIN CMSSite2WarehouseExclusiveFast AS rel ON {rel.target} = {wh.PK} ");
        query.append("JOIN CMSSite AS cms ON {cms.pk} = {rel.source} AND {cms.pk} = (?CMSSite)} ");
        query.append("}}) OR ");
        query.append("{sl.warehouse} IN ");
        query.append("({{ ");
        query.append("SELECT {wh.pk} ");
        query.append("FROM {Warehouse AS wh ");
        query.append("JOIN CMSSite2WarehouseFast AS relFast ON {relFast.target} = {wh.PK} ");
        query.append("JOIN CMSSite AS cms ON {cms.pk} = {relFast.source} AND {cms.pk} = (?CMSSite)} ");
        query.append("}}) OR ");
        query.append("{sl.warehouse} IN ");
        query.append("({{ ");
        query.append("SELECT {wh.pk} ");
        query.append("FROM {Warehouse AS wh ");
        query.append("JOIN CMSSite2WarehouseExternalFast AS relExtFast ON {relExtFast.target} = {wh.PK} ");
        query.append("JOIN CMSSite AS cms ON {cms.pk} = {relExtFast.source} AND {cms.pk} = (?CMSSite)} ");
        query.append("}}) ");
        query.append(") GROUP BY {p.code} ");
        query.append("}} ");
        query.append("UNION ALL {{ ");
        query.append(
                "SELECT {p.code} AS productCode, sum({sl.available}) AS totalInStock, (CASE WHEN sum({sl.available}) > 0 THEN 50 ELSE 0 END) AS InStock, 0 AS availableInPickup, 0 AS pickupStock, 0 AS stockFast, sum({sl.available}) AS stockSlow ");
        query.append("FROM {Product AS p JOIN StockLevel AS sl ON {p.code} = {sl.productCode}} ");
        query.append("WHERE ");
        query.append("( ");
        query.append("{sl.warehouse} IN ");
        query.append("({{ ");
        query.append("SELECT {wh.pk} ");
        query.append("FROM {Warehouse AS wh ");
        query.append("JOIN CMSSite2WarehouseSlow AS relSlow ON {relSlow.target} = {wh.PK} ");
        query.append("JOIN CMSSite AS cms ON {cms.pk} = {relSlow.source} AND {cms.pk} = (?CMSSite)} ");
        query.append("}}) OR ");
        query.append("{sl.warehouse} IN ");
        query.append("({{ ");
        query.append("SELECT {wh.pk} ");
        query.append("FROM {Warehouse AS wh ");
        query.append("JOIN CMSSite2WarehouseExternalSlow AS relExtSlow ON {relExtSlow.target} = {wh.PK} ");
        query.append("JOIN CMSSite AS cms ON {cms.pk} = {relExtSlow.source} AND {cms.pk} = (?CMSSite)} ");
        query.append("}}) OR ");
        query.append("{sl.warehouse} IN ");
        query.append("({{ ");
        query.append("SELECT {wh.pk} ");
        query.append("FROM {Warehouse AS wh ");
        query.append("JOIN CMSSite2WarehouseExclusiveSlow AS rel ON {rel.target} = {wh.PK} ");
        query.append("JOIN CMSSite AS cms ON {cms.pk} = {rel.source} AND {cms.pk} = (?CMSSite)} ");
        query.append("}}) ");
        query.append(") GROUP BY {p.code} ");
        query.append("}} ");
        query.append("UNION ALL ");
        query.append("{{ ");
        query.append(
                "SELECT {p.code} AS productCode, 0 AS totalInStock, (CASE WHEN sum({sl.available}) > 0 THEN 25 ELSE 0 END) AS InStock, (CASE WHEN sum({sl.available}) > 0 THEN 1 ELSE 0 END) AS availableInPickup, (CASE WHEN sum({sl.available}) > 0 THEN sum({sl.available}) ELSE 0 END) AS pickupStock, 0 AS stockFast, 0 AS stockSlow ");
        query.append("FROM {Product AS p JOIN StockLevel AS sl ON {p.code} = {sl.productCode}} ");
        query.append("WHERE ");
        query.append("( ");
        query.append("{sl.warehouse} IN ");
        query.append("({{ ");
        query.append("SELECT {wh.pk} ");
        query.append("FROM {Warehouse AS wh ");
        query.append("JOIN Site2WarehousePickup AS relPickup ON {relPickup.target} = {wh.PK} ");
        query.append("JOIN CMSSite AS cms ON {cms.pk} = {relPickup.source} AND {cms.pk} = (?CMSSite)} ");
        query.append("}}) ");
        query.append(") GROUP BY {p.code} ");
        query.append("}} ");
        query.append("UNION ALL ");
        query.append("{{ ");
        query.append(
                "SELECT {p.code} AS productCode, 0 AS totalInStock, 0 AS InStock, 0 AS availableInPickup, 0 AS pickupStock, 0 AS stockFast, 0 AS stockSlow ");
        query.append("FROM {product AS p} ");
        query.append("}}) s ");
        query.append("GROUP BY s.productCode ");
        query.append("}}) stockInfo ");
        query.append("ON prizeRows.productCode = stockInfo.productCode ");

        // buyable (saleStatus) query:
        query.append("INNER JOIN ({{ ");
        query.append(
                "SELECT {p.code} AS productCode, {dss.buyableInShop} AS buyable, {dsop.orderQuantityMinimum} AS ItemsMin, {dsop.orderQuantityStep}  AS ItemsStep, ");
        appendBestsellerSelect(query);
        query.append("FROM {Product AS p ");
        query.append("JOIN DistSalesOrgProduct AS dsop ON {p.pk} = {dsop.product} ");
        query.append("JOIN DistSalesStatus AS dss ON {dsop.salesStatus} = {dss.pk}} ");
        query.append("WHERE ");
        query.append("{dsop.salesOrg} = (?DistSalesOrg) ");
        query.append("}}) salesStatus ");
        query.append("on prizeRows.productCode = salesStatus.productCode ");

        appendStandardPriceSelect(query);
        appendDiscountPriceSelect(query);

        appendVisibilityCondition(query);

        // System.out.println("QUERY: \n" + query.toString());

        return query.toString();
    }

    private void appendStandardPriceSelect(final StringBuilder query) {
        query.append("LEFT JOIN ({{ ");
        query.append("SELECT stdPrice.productCode, stdPrice.standardNetPrice, stdPrice.standardPriceStart, stdPrice.standardPriceEnd,  stdPrice.rankValue ");
        query.append("FROM ");
        query.append("({{ ");
        query.append(
                "SELECT {prod.code} AS productCode, {dpr.price} as standardNetPrice, {dpr.starttime} as standardPriceStart, {dpr.endtime} as standardPriceEnd, ");
        query.append("DENSE_RANK() OVER (PARTITION BY {prod.pk} ORDER BY {dprct.priority} ASC) AS rankValue ");
        query.append("FROM {DistPriceRow as dpr ");
        query.append("JOIN DistErpPriceConditionType as dprct on {dprct.pk} = {dpr.erpPriceConditionType} ");
        query.append("JOIN Product as prod on {prod.pk} = {dpr.product} ");
        query.append("JOIN CMSSite as site on {site.userPriceGroup} = {dpr.ug}} ");
        query.append("WHERE ");
        query.append("({dprct.code} = 'ZR00' OR {dprct.code} = 'PR00')  and {site.pk} = (?CMSSite) and {dpr.starttime} <= (?Date) and {dpr.endtime} >= (?Date) ");
        // add query where pricerow.scale = 1 if needed
        query.append("and {dpr.minqtd} = 1 ");
       // query.append("ORDER BY {dpr.starttime} ASC ");
        query.append("}}) stdPrice ");
        query.append("WHERE stdPrice.rankValue = 1 ");
        query.append("}}) standardPrize ");
        query.append("on prizeRows.productCode = standardPrize.productCode ");
    }

    protected void appendDiscountPriceSelect(final StringBuilder query) {
        query.append("LEFT JOIN ({{ ");
        query.append("SELECT discountPrices.productCode, discountPrices.discountNetPrice, discountPrices.discountPriceStart, discountPrices.discountPriceEnd, discountPrices.rankValue ");
        query.append("FROM ({{");
        query.append(
                "SELECT {prod.code} AS productCode, {dpr.price} as discountNetPrice, {dpr.starttime} as discountPriceStart, {dpr.endtime} as discountPriceEnd, ");
        query.append("DENSE_RANK() OVER (PARTITION BY {prod.pk} ORDER BY {dprct.priority}) AS rankValue ");
        query.append("FROM {DistPriceRow as dpr ");
        query.append("JOIN DistErpPriceConditionType as dprct on {dprct.pk} = {dpr.erpPriceConditionType} ");
        query.append("JOIN Product as prod on {prod.pk} = {dpr.product} ");
        query.append("JOIN CMSSite as site on {site.userPriceGroup} = {dpr.ug}} ");

        query.append("WHERE ");
        query.append("({dprct.code} = 'ZN00' OR {dprct.code} = 'ZR00') and {site.pk} = (?CMSSite) ");
        // add query where pricerow.scale = 1 if needed
        query.append("and {dpr.minqtd} = 1 ");
        query.append("}}) discountPrices ");
        query.append("WHERE discountPrices.rankValue = 1 ");
        query.append("}}) discountPrize ");
        query.append("on prizeRows.productCode = discountPrize.productCode ");
    }

    protected StringBuilder appendBestsellerSelect(final StringBuilder query) {
        query.append(" (CASE ");
        query.append("     WHEN  ( {dsop.").append(DistSalesOrgProductModel.SHOWBESTSELLERLABELFROMDATE).append("} <= (?Date) )");
        query.append("      AND  ( (?Date) <= {dsop.").append(DistSalesOrgProductModel.SHOWBESTSELLERLABELUNTILDATE).append("} ) THEN '1' ");
        query.append("     ELSE '0' ");
        query.append(" END) AS Bestseller ");
        return query;
    }

    protected StringBuilder appendVisibilityCondition(final StringBuilder query) {
        query.append(" AND EXISTS ");
        query.append(" ({{");
        query.append("   SELECT {dss.code} ");
        query.append("      FROM {Product AS p ");
        query.append("      JOIN DistSalesOrgProduct AS dsop ON {p.pk} = {dsop.product} ");
        query.append("      JOIN DistSalesStatus AS dss ON {dsop.salesStatus} = {dss.pk}} ");
        query.append("    WHERE {dss.pk} = {dsop.salesStatus} ");
        query.append("      AND {dss.visibleInShop} = 1 AND prizeRows.productCode = {p.code} ");
        query.append("  }}) ");
        return query;
    }

    /**
     * Currently, PromotionLabel code/name/value fields need to be selected manually, since their values or defined implicitly on the
     * {@link DistSalesOrgProductModel} by datefrom/to fields. If the modelling of the PromotionLabels changes to be more dynamic, the SQL
     * can be changed as well.
     *
     * See: https://wiki.namics.com/display/distrelint/G121+Promotion+Labels And: https://wiki.namics.com/display/distrelint/Promotion+Label
     *
     * Labels are exported in the following format e.g.: |hit=hit:3:1| [{code:hit,label:hit,rank:3,active=true},
     * {code:bla,label:top,rank:3,active=false}]
     * <DistPromotionLabel.code>=<DistPromotionLabel.name>:<DistPromotionLabel.rank>:<DistPromotionLabel is valid>
     *
     * CHAR(123) = { CHAR(125) = }
     */
    protected StringBuilder appendPromotionLabelsSelect(final StringBuilder query) {
        query.append("'[' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.HOTOFFER.getValue(),
                "pc." + ProductCountryModel.SHOWHOTOFFERLABELFROMDATE, "pc." + ProductCountryModel.SHOWHOTOFFERLABELUNTILDATE);
        query.append(" + ',' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.NO_MOVER.getValue(),
                "pc." + ProductCountryModel.SHOWNOMOVERLABELFROMDATE, "pc." + ProductCountryModel.SHOWNOMOVERLABELUNTILDATE);
        query.append(" + ',' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.TOP.getValue(), "pc." + ProductCountryModel.SHOWTOPLABELFROMDATE,
                "pc." + ProductCountryModel.SHOWTOPLABELUNTILDATE);
        query.append(" + ',' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.HIT.getValue(), "pc." + ProductCountryModel.SHOWHITLABELFROMDATE,
                "pc." + ProductCountryModel.SHOWHITLABELUNTILDATE);
        query.append(" + ',' + ");
        query.append(chr("123")).append(" + 'code:").append(DistFactFinderExportPromotionColumnAttributes.USED.getValue()).append("' ");
        query.append("    + ({{");
        query.append("          SELECT ',label:\"' + {dpl.").append(DistPromotionLabelModel.NAME).append(":o} ");
        query.append("              + '\",priority:'  + CAST({dpl.").append(DistPromotionLabelModel.PRIORITY).append("} AS varchar(10)) ");
        query.append("              + ',rank:'  + CAST({dpl.").append(DistPromotionLabelModel.RANK).append("} AS varchar(10))");
        query.append("            FROM {").append(DistPromotionLabelModel._TYPECODE).append(" AS dpl} ");
        query.append("           WHERE {dpl.").append(DistPromotionLabelModel.CODE).append("} = '")
                .append(DistFactFinderExportPromotionColumnAttributes.USED.getValue()).append("' ");
        query.append("        }})");
        query.append("    + ',active:' ");
        query.append("    + (CASE ");
        query.append("        WHEN  ( {p.").append(ProductModel.SHOWUSEDLABEL).append("} = 1 ) THEN 'true' ");
        query.append("        ELSE 'false' ");
        query.append("        END) ");
        query.append(" + ").append(chr("125"));
        query.append(" + ',' + ");

        query.append(chr("123")).append(" + 'code:").append(DistFactFinderExportPromotionColumnAttributes.CALIBRATIONSERVICE.getValue()).append("' ");
        query.append("    + ({{");
        query.append("          SELECT ',label:\"' + {dpl.").append(DistPromotionLabelModel.NAME).append(":o} ");
        query.append("              + '\",priority:'  + CAST({dpl.").append(DistPromotionLabelModel.PRIORITY).append("} AS varchar(10))");
        query.append("              + ',rank:'  + CAST({dpl.").append(DistPromotionLabelModel.RANK).append("} AS varchar(10)) ");
        query.append("            FROM {").append(DistPromotionLabelModel._TYPECODE).append(" AS dpl} ");
        query.append("           WHERE {dpl.").append(DistPromotionLabelModel.CODE).append("} = '")
                .append(DistFactFinderExportPromotionColumnAttributes.CALIBRATIONSERVICE.getValue()).append("' ");
        query.append("        }})");
        query.append("    + ',active:' ");
        query.append("    + (CASE ");
        query.append("        WHEN  ( {p.").append(ProductModel.CALIBRATIONSERVICE).append("} = 1 ) THEN 'true' ");
        query.append("        ELSE 'false' ");
        query.append("        END) ");
        query.append(" + ").append(chr("125"));
        query.append(" + ',' + ");

        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.NEW.getValue(), "dsop." + DistSalesOrgProductModel.SHOWNEWLABELFROMDATE,
                "dsop." + DistSalesOrgProductModel.SHOWNEWLABELUNTILDATE);
        query.append(" + ',' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.BESTSELLER.getValue(),
                "dsop." + DistSalesOrgProductModel.SHOWBESTSELLERLABELFROMDATE, "dsop." + DistSalesOrgProductModel.SHOWBESTSELLERLABELUNTILDATE);
        query.append(" + ',' + ");
        query.append(chr("123")).append(" + 'code:").append(DistFactFinderExportPromotionColumnAttributes.OFFER.getValue()).append("' ");
        query.append("      + ({{");
        query.append("             SELECT ',label:\"' + {dpl.").append(DistPromotionLabelModel.NAME).append(":o} ");
        query.append("              + '\",priority:'  + CAST({dpl.").append(DistPromotionLabelModel.PRIORITY).append("} AS varchar(10))");
        query.append("              + ',rank:'  + CAST({dpl.").append(DistPromotionLabelModel.RANK).append("} AS varchar(10)) ");
        query.append("              FROM {").append(DistPromotionLabelModel._TYPECODE).append(" AS dpl} ");
        query.append("             WHERE {dpl.").append(DistPromotionLabelModel.CODE).append("} = '")
                .append(DistFactFinderExportPromotionColumnAttributes.OFFER.getValue()).append("' ");
        query.append("          }})");
        query.append("      + ',active:' ");
        query.append("      + (CASE ");
        query.append("          WHEN ( singlePrize.specialPrice = 1 ) THEN 'true' ");

        // query.append("WHEN EXISTS ({{ ");
        // query.append("SELECT {pct.code} FROM ");
        // query.append("FROM {Product AS prod ");
        // query.append("JOIN DistPriceRow AS pr ON {prod.pk} = {pr.product} ");
        // query.append("JOIN DistErpPriceConditionType AS pct ON {pr.erpPriceConditionType} = {pct.pk} } ");
        // query.append("WHERE {pct.code} = 'ZN00' AND {prod.pk} = {p.pk} }}) THEN 'true' ELSE 'false' ");

        query.append("          ELSE 'false' ");
        query.append("          END) ");
        query.append(" + ").append(chr("125"));
        query.append(" + ']' ");

        // System.out.println("PROMOLABEL QUERY: \n" + query.toString());

        return query;
    }

    protected StringBuilder appendPromotionLabelSelect(final StringBuilder query, final String code, final String fromField, final String untilField) {
        query.append(chr("123")).append(" + 'code:").append(code).append("' ");
        query.append("    + ({{");
        query.append("          SELECT ',label:\"' + {dpl.").append(DistPromotionLabelModel.NAME).append(":o} ");
        // DISTRELEC-6799 Adding the priority to the promotion labels
        query.append("              + '\",priority:'  + CAST({dpl.").append(DistPromotionLabelModel.PRIORITY).append("} AS varchar(10))");
        query.append("              + ',rank:'  + CAST({dpl.").append(DistPromotionLabelModel.RANK).append("} AS varchar(10)) ");
        query.append("            FROM {").append(DistPromotionLabelModel._TYPECODE).append(" AS dpl} ");
        query.append("           WHERE {dpl.").append(DistPromotionLabelModel.CODE).append("} = '").append(code).append("' ");
        query.append("        }})");
        query.append("    + ',active:' ");
        query.append("    + (CASE ");
        query.append("        WHEN  ( {").append(fromField).append("} <= (?").append(DistFactFinderProductExportParameterProvider.DATE).append(") )");
        query.append("         AND  ( (?").append(DistFactFinderProductExportParameterProvider.DATE).append(") <= {").append(untilField)
                .append("} ) THEN 'true' ");
        query.append("        ELSE 'false' ");
        query.append("        END) ");
        query.append(" + ").append(chr("125"));
        return query;
    }

    protected String chr(String character) {
        return distSqlUtils.chr(character);
    }

    protected String concat(String... elements) {
        return distSqlUtils.concat(elements);
    }

    protected String stringAgg(String expression, String connector, String orderBy) {
        return distSqlUtils.stringAgg(expression, connector, orderBy);
    }

    protected String toNvarchar(String character) {
        return distSqlUtils.toNvarchar(character);
    }

    protected String where(String where) {
        return distSqlUtils.where(where);
    }
}
