package com.namics.hybris.ffsearch.export.query;

import java.util.Set;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.jalo.ProductCountry;
import com.namics.distrelec.b2b.core.model.DistErpPriceConditionTypeModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import com.namics.distrelec.b2b.core.model.DistReplacementReasonModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;
import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import com.namics.hybris.ffsearch.export.DistFactFinderDetailQueryCreator;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportPromotionColumnAttributes;
import com.namics.hybris.ffsearch.export.columns.ProductStatus;
import com.namics.hybris.ffsearch.util.DistFactFinderUtils;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.europe1.model.TaxRowModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import static com.namics.distrelec.b2b.core.inout.export.impl.DistDefaultCsvTransformationService.ENCODE_FF_SUFFIX;
import static com.namics.distrelec.b2b.core.util.DistSqlUtils.ORACLE_DATE_TIME_PATTERN;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.GROSS;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.MIN;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.NET;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.*;

public class DefaultDistFactFinderDetailQueryCreator implements DistFactFinderDetailQueryCreator {

    private static final String INSTOCK_JOIN_PK = "productpk";
    private static final String PRICES_JOIN_VALUE = "prices";

    private static final String CONCAT_SELECT_VALUE = "\", ";

    private final Logger LOG = LoggerFactory.getLogger(DefaultDistFactFinderDetailQueryCreator.class);

    private ConfigurationService configurationService;

    @Autowired
    private DistSqlUtils distSqlUtils;

    @Override
    public String createDetailQuery(final CMSSiteModel cmsSite) {
        final StringBuilder query = new StringBuilder();
        Set<String> languages = DistFactFinderUtils.getLanguagesForCMSSite(cmsSite);

        query.append("SELECT ");
        query.append(" {p.").append(ProductModel.CODE).append("} ").append(" AS \"").append(PRODUCT_NUMBER.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.EAN).append("} ").append(" AS \"").append(EAN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.CODEELFA).append("} ").append(" AS \"").append(PRODUCT_NUMBER_ELFA.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.CODEMOVEX).append("} ").append(" AS \"").append(PRODUCT_NUMBER_MOVEX.getValue()).append(CONCAT_SELECT_VALUE);
        for (String language : languages) {
            query.append("{p.").append(ProductModel.NAME).append(languageSelector(language)).append(":o} ").append(" AS \"").append(TITLE.getValue()).append(ENCODE_FF_SUFFIX).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            query.append(toNvarchar("{p.".concat(ProductModel.DESCRIPTION).concat(languageSelector(language)).concat(":o}"))).append(" AS \"").append(DESCRIPTION.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            appendProductURLSelect(query, language).append(" AS \"").append(PRODUCT_URL.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
        }
        appendProductImageURLSelect(query, DistConstants.MediaFormat.PORTRAIT_SMALL).append(" AS \"").append(IMAGE_URL.getValue()).append(CONCAT_SELECT_VALUE);
        appendAdditionalProductImageURLSelect(query).append(" AS \"").append(ADDITIONAL_IMAGE_URLS.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {m.").append(DistManufacturerModel.NAME).append(":o} ").append(" AS \"").append(MANUFACTURER.getValue()).append(CONCAT_SELECT_VALUE);
        for (String language : languages) {
            appendManufacturerURLSelect(query, language).append(" AS \"").append(MANUFACTURER_URL.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
        }
        appendManufacturerImageURLSelect(query).append(" AS \"").append(MANUFACTURER_IMAGE_URL.getValue()).append(CONCAT_SELECT_VALUE);
        appendVolumePriceSubselect(query).append(" AS \"").append(PRICE.getValue()).append(CONCAT_SELECT_VALUE);

        for (String language : languages) {
            query.append(" {p.").append(ProductModel.PIMWEBUSE).append(languageSelector(language)).append(":o} ").append(" AS \"").append(WEB_USE.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            query.append(" {cats.cat1name").append(languageSelector(language)).append(":o} AS \"").append(CATEGORY1.getValue()).append(ENCODE_FF_SUFFIX).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            query.append(" {cats.cat2name").append(languageSelector(language)).append(":o} AS \"").append(CATEGORY2.getValue()).append(ENCODE_FF_SUFFIX).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            query.append(" {cats.cat3name").append(languageSelector(language)).append(":o} AS \"").append(CATEGORY3.getValue()).append(ENCODE_FF_SUFFIX).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            query.append(" {cats.cat4name").append(languageSelector(language)).append(":o} AS \"").append(CATEGORY4.getValue()).append(ENCODE_FF_SUFFIX).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            query.append(" {cats.cat5name").append(languageSelector(language)).append(":o} AS \"").append(CATEGORY5.getValue()).append(ENCODE_FF_SUFFIX).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            query.append(" {cats.catPathSelectName").append(languageSelector(language)).append(":o} AS \"").append(CATEGORYPATH.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            query.append(" {cats.catPathExtensions").append(languageSelector(language)).append(":o} AS \"").append(CATEGORY_EXTENSIONS.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
        }

        query.append(" {p.").append(ProductModel.TYPENAME).append("} ").append("AS \"").append(TYPENAME.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("{p.".concat(ProductModel.TYPENAMENORMALIZED).concat("}")).append(" AS \"").append(TYPE_NAME_NORMALIZED.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("stock").append(".InStock AS \"").append(INSTOCK.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {dsop").append(".").append(DistSalesOrgProductModel.ORDERQUANTITYMINIMUM).append("}").append(" AS \"").append(ITEMS_MIN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {dsop").append(".").append(DistSalesOrgProductModel.ORDERQUANTITYSTEP).append("}").append(" AS \"").append(ITEMS_STEP.getValue()).append(CONCAT_SELECT_VALUE);
        for (String language : languages) {
            appendSalesUnitSelect(query, language).append(" AS \"").append(SALESUNIT.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
        }
        for (String language : languages) {
            appendPromotionLabelsSelect(query, language).append(" AS \"").append(PROMOTIONLABELS.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
        }
        appendReplacementSelect(query, languages);
        query.append(" CASE WHEN {dss.").append(DistSalesStatusModel.BUYABLEINSHOP).append("} = 1 THEN 1 ELSE 0 END AS \"").append(BUYABLE.getValue()).append(CONCAT_SELECT_VALUE);
        for (String language : languages) {
            query.append(" {p.").append(ProductModel.NAMESHORT).append(languageSelector(language)).append(":o} AS \"").append(TITLE_SHORT.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);

            query.append(" {p.").append(ProductModel.SEOACCESSORY).append(languageSelector(language)).append(":o} AS \"").append(ACCESSORY.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
            query.append(" {p.").append(ProductModel.PRODUCTFAMILYNAME).append(languageSelector(language)).append(":o} AS \"").append(PRODUCT_FAMILY_NAME.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
        }
        appendSingleMinPriceSubselect(query).append(" AS \"").append(SINGLE_MIN_PRICE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.PIMFAMILYCATEGORYCODE).append("} AS \"").append(PRODUCT_FAMILY_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.PRODUCTHIERARCHY).append("} AS \"").append(PRODUCT_HIERARCHY.getValue()).append(CONCAT_SELECT_VALUE);

        query.append("stock.totalInStock AS  \"").append(TOTALINSTOCK.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("stock.availableInPickup AS \"").append(AVAILABLEINPICKUP.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("stock.pickupStock AS \"").append(PICKUPSTOCK.getValue()).append(CONCAT_SELECT_VALUE);

        query.append("{cats.cat1code} AS \"").append(CATEGORY1_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("{cats.cat2code} AS \"").append(CATEGORY2_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("{cats.cat3code} AS \"").append(CATEGORY3_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("{cats.cat4code} AS \"").append(CATEGORY4_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("{cats.cat5code} AS \"").append(CATEGORY5_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("{cats.catPathSelectCode} AS \"").append(CATEGORY_CODE_PATH.getValue()).append(CONCAT_SELECT_VALUE);
        appendSingleUnitPrice(query).append(" AS \"").append(SINGLE_UNIT_PRICE.getValue()).append(CONCAT_SELECT_VALUE);

        for (String language : languages) {
            // DISTRELEC-10398 energy efficiency data
            query.append(" {p.").append(ProductModel.ENERGYEFFIENCYLABELS).append(languageSelector(language)).append(":o} AS \"").append(ENERGY_EFFICIENCY.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
        }

        for (String language : languages) {
            query.append(" {p.").append(ProductModel.PIMWEBUSEDFEATURES).append(languageSelector(language)).append(":o} ").append(" AS \"").append(PIM_WEB_USE_D.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
        }
        query.append(" {p.").append(ProductModel.ALTERNATIVESMPN).append(":o} ").append(" AS \"").append(ALTERNATIVES_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.NORMALIZEDALTERNATIVESMPN).append(":o} ").append(" AS \"").append(NORMALIZED_ALTERNATIVES_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.CURATEDPRODUCTSELECTION).append("} AS \"").append(DistFactFinderExportColumns.CURATED_PRODUCT_SELECTION.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.SAPPLANTPROFILES).append("} AS \"").append(DistFactFinderExportColumns.SAP_PLANT_PROFILES.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.SAPMPN).append("} AS \"").append(DistFactFinderExportColumns.SAP_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.ALTERNATIVEALIASMPN).append(":o} ").append(" AS \"").append(ALTERNATE_ALIAS_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.NORMALIZEDALTERNATIVEALIASMPN).append(":o} ").append(" AS \"").append(NORMALIZED_ALTERNATE_ALIAS_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {dsop.").append(DistSalesOrgProductModel.ITEMCATEGORYGROUP).append("}").append(" AS \"").append(ITEM_CATEGORY_GROUP.getValue()).append(CONCAT_SELECT_VALUE);
        appendNetMarginSubselect(query).append(" AS \"").append(NET_MARGIN_RANK.getValue()).append(CONCAT_SELECT_VALUE);
        appendStandardPriceSubselect(query).append(" AS \"").append(STANDARD_PRICE.getValue()).append(CONCAT_SELECT_VALUE);
        appendProductStatusSelect(query).append(" AS \"").append(PRODUCT_STATUS.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {dss.").append(DistSalesStatusModel.CODE).append("}").append(" AS \"").append(SALES_STATUS.getValue()).append(CONCAT_SELECT_VALUE);
        appendProductImageURLSelect(query, DistConstants.MediaFormat.PORTRAIT_SMALL_WEBP).append(" AS \"").append(IMAGE_WEBP_URL.getValue()).append(CONCAT_SELECT_VALUE);
        // Energy Efficiency Label Image URL
        appendEnergyEfficiencyLabelImageURLSelect(query).append(" AS \"").append(ENERGY_EFFICIENCY_LABEL_IMAGE_URL.getValue()).append("\" ");

        query.append(" FROM ");
        query.append(" { ");
        query.append(ProductModel._TYPECODE).append(" AS p");
        query.append("  JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsop");
        query.append("         ON {dsop.").append(DistSalesOrgProductModel.PRODUCT).append("} = {p.").append(ProductModel.PK).append("} ");
        query.append("  JOIN ").append(DistSalesStatusModel._TYPECODE).append(" AS dss");
        query.append("         ON {dss.").append(DistSalesStatusModel.PK).append("} = {dsop.").append(DistSalesOrgProductModel.SALESSTATUS).append("} ");
        query.append("  LEFT JOIN ").append(DistSalesUnitModel._TYPECODE).append(" AS dsu ");
        query.append("         ON {dsu.").append(DistSalesUnitModel.PK).append("} = {p.").append(ProductModel.SALESUNIT).append("} ");
        query.append("  LEFT JOIN ").append(UnitModel._TYPECODE).append(" AS u ");
        query.append("         ON {u.").append(UnitModel.PK).append("} = {p.").append(ProductModel.UNIT).append("} ");
        query.append("  LEFT JOIN ").append(DistManufacturerModel._TYPECODE).append(" AS m ");
        query.append("         ON {m.").append(DistManufacturerModel.PK).append("} = {p.").append(ProductModel.MANUFACTURER).append("} ");

        query.append("  LEFT JOIN ").append(ProductCountryModel._TYPECODE).append(" AS pc");
        query.append("         ON {pc.").append(ProductCountryModel.PRODUCT).append("} = {p.").append(ProductModel.PK).append("} ");
        query.append("         AND {pc.").append(ProductCountryModel.COUNTRY).append("} = (?").append(CountryModel._TYPECODE).append(") ");
        query.append("  JOIN ").append(ProductModel._CATEGORYPRODUCTRELATION).append(" AS cpr ON {p.").append(ProductModel.PK).append("} = {cpr.").append(LinkModel.TARGET).append("} ");
        query.append("  JOIN ").append(CategoryModel._TYPECODE).append("! AS cats ON {cats.").append(CategoryModel.PK).append("} = {cpr.").append(LinkModel.SOURCE).append("} ");
        query.append(" } ");


        query.append(",({{ ");
        appendInStockSubSelect(query);
        query.append("}}) stock");

        query.append(" WHERE ");
        query.append("{p.").append(ProductModel.PK).append("} in (?").append(PK_LIST).append(")");
        query.append(" AND {dsop.").append(DistSalesOrgProductModel.SALESORG).append("} = (?").append(DistSalesOrgModel._TYPECODE).append(") ");
        query.append("  AND {p.").append(ProductModel.PK).append("} = stock.").append(INSTOCK_JOIN_PK).append(" ");

        String flexSearch = query.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(flexSearch);
        }

        return flexSearch;
    }

    protected StringBuilder appendProductStatusSelect(final StringBuilder query) {
        query.append(distSqlUtils.concat("CASE WHEN stock.totalInStock >= 1 THEN '|".concat(ProductStatus.AVAILABLE_DELIVERY.getValue()).concat("' ELSE '' END"),
                "CASE WHEN stock.pickupStock >=1 THEN '|".concat(ProductStatus.AVAILABLE_PICKUP.getValue()).concat("' ELSE '' END"),
                "CASE WHEN ( (({pc.".concat(ProductCountry.SHOWTOPLABELFROMDATE).concat("} <= (?").concat(DATE).concat(") ) AND ( (?").concat(DATE).concat(") <= {pc.").concat(ProductCountry.SHOWTOPLABELUNTILDATE).concat("} )) OR (({dsop.").concat(DistSalesOrgProductModel.SHOWNEWLABELFROMDATE).concat("} <= (?").concat(DATE).concat(") ) AND ( (?").concat(DATE).concat(") <= {dsop.").concat(DistSalesOrgProductModel.SHOWNEWLABELUNTILDATE).concat("} ))) THEN '|").concat(ProductStatus.NEW.getValue()).concat("' ELSE '' END"),
                "CASE WHEN ({pc.".concat(ProductCountry.SHOWHOTOFFERLABELFROMDATE).concat("} <= (?").concat(DATE).concat(") ) AND ( (?").concat(DATE).concat(") <= {pc.").concat(ProductCountry.SHOWHOTOFFERLABELUNTILDATE).concat("} ) THEN '|").concat(ProductStatus.EXCLUSIVE.getValue()).concat("' ELSE '' END"),
                "CASE WHEN ( (({pc.".concat(ProductCountry.SHOWNOMOVERLABELFROMDATE).concat("} <= (?").concat(DATE).concat(") ) AND ( (?").concat(DATE).concat(") <= {pc.").concat(ProductCountry.SHOWNOMOVERLABELUNTILDATE).concat("} )) OR (").concat(existsSpecialPriceSubselect()).concat(")) THEN '|").concat(ProductStatus.OFFER.getValue()).concat("' ELSE '' END"),
                "CASE WHEN ({p.".concat(ProductModel.CALIBRATIONSERVICE).concat("} = 1 AND {p.").concat(ProductModel.CALIBRATED).concat("} = 1) THEN '|").concat(ProductStatus.CALIBRATION.getValue()).concat("' ELSE '' END"),
                "CASE WHEN {p.".concat(ProductModel.CURATEDPRODUCTSELECTION).concat("} LIKE '%CAD_image%' THEN '|").concat(ProductStatus.CAD_IMAGE.getValue()).concat("|' ELSE '|' END")));

        return query;
    }

    protected StringBuilder appendInStockSubSelect(final StringBuilder query) {
        query.append(" SELECT s.productpk AS productpk, (CASE WHEN sum(s.totalInStock + s.pickupStock) <= 0 THEN sum(s.totalInStock + s.pickupStock) WHEN sum(s.totalInStock + s.pickupStock) > 0 THEN sum(s.totalInStock + s.pickupStock) ELSE 0 END) AS totalInStock,  (CASE WHEN sum(s.InStock) >= 100 THEN 2 WHEN sum(s.inStock) = 75 THEN 1 WHEN sum(s.inStock) = 50 THEN 1 WHEN sum(s.inStock) = 25 THEN 1 WHEN sum(s.InStock) < 50 THEN 0 ELSE 0 END) AS InStock, sum(s.availableInPickup) AS availableInPickup, sum(s.pickupStock) AS pickupStock ");
        query.append("FROM ( ");
        query.append("{{ ");
        query.append("SELECT {p.pk} AS productpk, sum({sl.available}) AS totalInStock, (CASE WHEN sum({sl.available}) > 0 THEN 100 ELSE 0 END) AS InStock, 0 AS availableInPickup, 0 AS pickupStock ");
        query.append("FROM {Product AS p JOIN StockLevel AS sl ON {p.code} = {sl.productCode}} ");
        query.append("WHERE ");
        query.append(" ( ");
        query.append("NOT EXISTS ({{ ");
        query.append("  SELECT 1 ");
        query.append("  FROM {Site2WarehousePickup AS relPickup} ");
        query.append("  WHERE {relPickup.target} = {sl.warehouse} ");
        query.append("}}) )");
        query.append(" GROUP BY {p.pk} ");
        query.append("}} ");
        query.append("UNION ALL ");
        query.append("{{ ");
        query.append("SELECT {p.pk} AS productpk, 0 AS totalInStock, (CASE WHEN sum({sl.available}) > 0 THEN 25 ELSE 0 END) AS InStock, (CASE WHEN sum({sl.available}) > 0 THEN 1 ELSE 0 END) AS availableInPickup, (CASE WHEN sum({sl.available}) > 0 THEN sum({sl.available}) ELSE 0 END) AS pickupStock ");
        query.append("FROM {Product AS p JOIN StockLevel AS sl ON {p.code} = {sl.productCode}} ");
        query.append("WHERE ");
        query.append(" ( ");
        query.append("EXISTS ({{ ");
        query.append("  SELECT 1 ");
        query.append("  FROM {Site2WarehousePickup AS relPickup} ");
        query.append("  WHERE {relPickup.target} = {sl.warehouse} ");
        query.append("    AND {relPickup.source} = (?CMSSite)");
        query.append("}}) )");
        query.append(" GROUP BY {p.pk} ");
        query.append("}} ");
        query.append("UNION ALL ");
        query.append("{{ ");
        query.append("SELECT {p.pk} AS productpk, 0 AS totalInStock, 0 AS InStock, 0 AS availableInPickup, 0 AS pickupStock ");
        query.append("FROM {product AS p} ");
        query.append("}}) s ");
        query.append("GROUP BY s.productpk  ");
        return query;
    }

    protected StringBuilder appendVolumePriceSubselect(final StringBuilder query) {
        query.append("({{SELECT CASE WHEN (count(*) > 0) THEN ").append(distSqlUtils.concat("'|'", stringAgg(PRICES_JOIN_VALUE.concat(".price"), null, PRICES_JOIN_VALUE.concat(".").concat(DistPriceRowModel.MINQTD)))).append(" ELSE '' END ");
        query.append("  FROM ({{ ");
        query.append("      SELECT scalePrice.price AS price, scalePrice.").append(DistPriceRowModel.MINQTD).append(" AS ").append(DistPriceRowModel.MINQTD).append(" ");
        query.append("      FROM ({{ ");
        query.append("          SELECT ");
        query.append("              ").append(distSqlUtils.concat("{cur.".concat(CurrencyModel.ISOCODE).concat("}"), "';'", "'".concat(GROSS).concat("'"), "';'", "{pr.".concat(DistPriceRowModel.MINQTD).concat("}"), "'='", "(1 + ({tax.".concat(TaxModel.VALUE).concat("}/100)) * {pr.").concat(DistPriceRowModel.PRICE).concat("} / {pr.").concat(PriceRowModel.UNITFACTOR).concat("}"), "'|'", "{cur.".concat(CurrencyModel.ISOCODE).concat("}"), "';'", "'".concat(NET).concat("'"), "';'", "{pr.".concat(DistPriceRowModel.MINQTD).concat("}"), "'='", "{pr.".concat(DistPriceRowModel.PRICE).concat("} / {pr.").concat(PriceRowModel.UNITFACTOR).concat("}"), "'|'")).append(" AS price, ");
        query.append("              {pr.").append(DistPriceRowModel.MINQTD).append("} AS ").append(DistPriceRowModel.MINQTD).append(", ");
        query.append("              RANK() OVER (PARTITION BY {pr.").append(DistPriceRowModel.PRODUCT).append("} ORDER BY {pct.")
            .append(DistErpPriceConditionTypeModel.PRIORITY).append("}, {pr.").append(DistPriceRowModel.LASTMODIFIEDERP).append("} DESC, {pr.")
            .append(DistPriceRowModel.MATCHVALUE).append("} DESC) AS rankValue");
        query.append("          FROM {").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("                  JOIN ").append(DistErpPriceConditionTypeModel._TYPECODE).append(" AS pct ");
        query.append("                      ON {pr.").append(DistPriceRowModel.ERPPRICECONDITIONTYPE).append("} = {pct.").append(DistPriceRowModel.PK).append("} ");
        query.append("                  JOIN ").append(CurrencyModel._TYPECODE).append(" AS cur ");
        query.append("                      ON {pr.").append(DistPriceRowModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK).append("}");
        query.append("                  JOIN ").append(CMSSiteModel._TYPECODE).append(" AS cms ");
        query.append("                      ON {pr.").append(DistPriceRowModel.UG).append("} = {cms.").append(CMSSiteModel.USERPRICEGROUP).append("}");
        query.append("                  JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsop ");
        query.append("                      ON {pr.").append(DistPriceRowModel.PRODUCT).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCT).append("}");
        query.append("                      AND {cms.").append(CMSSiteModel.SALESORG).append("} = {dsop.").append(DistSalesOrgProductModel.SALESORG).append("}");
        query.append("                  JOIN ").append(TaxRowModel._TYPECODE).append(" AS tr ");
        query.append("                      ON {cms.").append(CMSSiteModel.USERTAXGROUP).append("} = {tr.").append(TaxRowModel.UG).append("}");
        query.append("                      AND {tr.").append(TaxRowModel.PG).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCTTAXGROUP).append("} ");
        query.append("                  JOIN ").append(TaxModel._TYPECODE).append(" AS tax ");
        query.append("                      ON {tr.").append(TaxRowModel.TAX).append("} = {tax.").append(TaxModel.PK).append("}");
        query.append("          } ");
        query.append("          ").append(where("{pr.".concat(DistPriceRowModel.PRODUCT).concat("} = {p.").concat(ProductModel.PK).concat("}")
            .concat(" AND {cms.").concat(CMSSiteModel.PK).concat("} = (?CMSSite)")
            .concat(" AND ({pr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?").concat(DATE).concat(")) ")
            .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?").concat(DATE).concat(")) ")));
        query.append("      }}) scalePrice ");
        query.append("      ").append(where("scalePrice.rankValue = 1"));
        query.append("  }} UNION {{ ");
        // We need to UNION the MIN value for a pricerow explicitly to show factfinder which value to use for sorting etc.
        query.append("      SELECT minPrice.price AS price, minPrice.").append(DistPriceRowModel.MINQTD).append(" AS ").append(DistPriceRowModel.MINQTD).append(" ");
        query.append("      FROM ({{ ");
        query.append("          SELECT ");
        query.append("              ").append(distSqlUtils.concat("{cur.".concat(CurrencyModel.ISOCODE).concat("}"), "';'", "'".concat(GROSS).concat("'"), "';".concat(MIN).concat("='"), "(1 + ({tax.".concat(TaxModel.VALUE).concat("}/100)) * {pr.").concat(DistPriceRowModel.PRICE).concat("} / {pr.").concat(PriceRowModel.UNITFACTOR).concat("}"), "'|'", "{cur.".concat(CurrencyModel.ISOCODE).concat("}"), "';'", "'".concat(NET).concat("'"), "';".concat(MIN).concat("='"), "{pr.".concat(DistPriceRowModel.PRICE).concat("} / {pr.").concat(PriceRowModel.UNITFACTOR).concat("}"), "'|'")).append(" AS price, ");
        query.append("              {pr.").append(DistPriceRowModel.MINQTD).append("} AS ").append(DistPriceRowModel.MINQTD).append(", ");
        query.append("              RANK() OVER (PARTITION BY {pr.").append(DistPriceRowModel.PRODUCT).append("} ORDER BY {pct.")
            .append(DistErpPriceConditionTypeModel.PRIORITY).append("}, {pr.").append(DistPriceRowModel.LASTMODIFIEDERP).append("} DESC, {pr.")
            .append(DistPriceRowModel.MATCHVALUE).append("} DESC, {pr.").append(DistPriceRowModel.MINQTD).append("}, {pr.").append(DistPriceRowModel.PK).append("}) AS rankValue");
        query.append("          FROM {").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("              JOIN ").append(DistErpPriceConditionTypeModel._TYPECODE).append(" AS pct ");
        query.append("                  ON {pr.").append(DistPriceRowModel.ERPPRICECONDITIONTYPE).append("} = {pct.").append(DistPriceRowModel.PK).append("} ");
        query.append("              JOIN ").append(CurrencyModel._TYPECODE).append(" AS cur ");
        query.append("                  ON {pr.").append(DistPriceRowModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK).append("}");
        query.append("              JOIN ").append(CMSSiteModel._TYPECODE).append(" AS cms ");
        query.append("                  ON {pr.").append(DistPriceRowModel.UG).append("} = {cms.").append(CMSSiteModel.USERPRICEGROUP).append("}");
        query.append("              JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsop ");
        query.append("                  ON {pr.").append(DistPriceRowModel.PRODUCT).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCT).append("}");
        query.append("                  AND {cms.").append(CMSSiteModel.SALESORG).append("} = {dsop.").append(DistSalesOrgProductModel.SALESORG).append("}");
        query.append("              JOIN ").append(TaxRowModel._TYPECODE).append(" AS tr ");
        query.append("                  ON {cms.").append(CMSSiteModel.USERTAXGROUP).append("} = {tr.").append(TaxRowModel.UG).append("}");
        query.append("                  AND {tr.").append(TaxRowModel.PG).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCTTAXGROUP).append("} ");
        query.append("              JOIN ").append(TaxModel._TYPECODE).append(" AS tax ");
        query.append("                  ON {tr.").append(TaxRowModel.TAX).append("} = {tax.").append(TaxModel.PK).append("}");
        query.append("          } ");
        query.append(where("{pr.".concat(DistPriceRowModel.PRODUCT).concat("} = {p.").concat(ProductModel.PK).concat("}")
            .concat(" AND {cms.").concat(CMSSiteModel.PK).concat("} = (?CMSSite)")
            .concat(" AND ({pr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?").concat(DATE).concat(")) ")
            .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?").concat(DATE).concat(")) ")));
        query.append("      }}) minPrice ");
        query.append(where("minPrice.rankValue = 1"));
        query.append("  }}) ").append(PRICES_JOIN_VALUE).append("}}) ");
        return query;
    }

    protected StringBuilder appendStandardPriceSubselect(final StringBuilder query) {
        query.append("({{SELECT minPrice.price AS price ");
        query.append("FROM ({{ ");
        query.append("  SELECT ");
        query.append("      ").append(distSqlUtils.concat("{cur.".concat(CurrencyModel.ISOCODE).concat("}"), "';'", "'".concat(GROSS).concat("'"), "';'", "{pr.".concat(DistPriceRowModel.MINQTD).concat("}"), "'='", "(1 + ({tax.".concat(TaxModel.VALUE).concat("}/100)) * {pr.").concat(DistPriceRowModel.PRICE).concat("} / {pr.").concat(PriceRowModel.UNITFACTOR).concat("}"), "'|'", "{cur.".concat(CurrencyModel.ISOCODE).concat("}"), "';'", "'".concat(NET).concat("'"), "';'", "{pr.".concat(DistPriceRowModel.MINQTD).concat("}"), "'='", "{pr.".concat(DistPriceRowModel.PRICE).concat("} / {pr.").concat(PriceRowModel.UNITFACTOR).concat("}"), "'|'" )).append(" AS price, ");
        query.append("      RANK() OVER (PARTITION BY {pr.").append(DistPriceRowModel.PRODUCT).append("} ORDER BY {pct.")
                .append(DistErpPriceConditionTypeModel.PRIORITY).append("}, {pr.").append(DistPriceRowModel.LASTMODIFIEDERP).append("} DESC, {pr.")
                .append(DistPriceRowModel.MATCHVALUE).append("} DESC, {pr.").append(DistPriceRowModel.MINQTD).append("}, {pr.").append(DistPriceRowModel.PK).append("}) AS rankValue");
        query.append("  FROM {").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("      JOIN ").append(DistErpPriceConditionTypeModel._TYPECODE).append(" AS pct ");
        query.append("          ON {pr.").append(DistPriceRowModel.ERPPRICECONDITIONTYPE).append("} = {pct.").append(DistPriceRowModel.PK).append("} ");
        query.append("      JOIN ").append(CurrencyModel._TYPECODE).append(" AS cur ");
        query.append("          ON {pr.").append(DistPriceRowModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK).append("}");
        query.append("      JOIN ").append(CMSSiteModel._TYPECODE).append(" AS cms ");
        query.append("          ON {pr.").append(DistPriceRowModel.UG).append("} = {cms.").append(CMSSiteModel.USERPRICEGROUP).append("}");
        query.append("      JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsop ");
        query.append("          ON {pr.").append(DistPriceRowModel.PRODUCT).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCT).append("}");
        query.append("          AND {cms.").append(CMSSiteModel.SALESORG).append("} = {dsop.").append(DistSalesOrgProductModel.SALESORG).append("}");
        query.append("      JOIN ").append(TaxRowModel._TYPECODE).append(" AS tr ");
        query.append("          ON {cms.").append(CMSSiteModel.USERTAXGROUP).append("} = {tr.").append(TaxRowModel.UG).append("}");
        query.append("          AND {tr.").append(TaxRowModel.PG).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCTTAXGROUP).append("} ");
        query.append("      JOIN ").append(TaxModel._TYPECODE).append(" AS tax ");
        query.append("          ON {tr.").append(TaxRowModel.TAX).append("} = {tax.").append(TaxModel.PK).append("}");
        query.append("          } ");
        query.append(where("{pr.".concat(DistPriceRowModel.PRODUCT).concat("} = {p.").concat(ProductModel.PK).concat("}")
                .concat(" AND {cms.").concat(CMSSiteModel.PK).concat("} = (?CMSSite)")
                .concat(" AND ({pr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
                .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) "))
                .concat(" AND {pct.").concat(DistErpPriceConditionTypeModel.CODE).concat("} != 'ZN00' "));
        query.append("}}) minPrice ");
        query.append(where("minPrice.rankValue = 1"));
        query.append("}})");
        return query;
    }

    protected StringBuilder appendSingleMinPriceSubselect(final StringBuilder query) {
        query.append("({{SELECT minPrice.singleMinPrice ");
        query.append("FROM ({{ ");
        query.append("     SELECT ").append(toNvarchar("{pr.".concat(DistPriceRowModel.PRICE).concat("} / {pr.").concat(DistPriceRowModel.UNITFACTOR).concat("}"))).append(" AS singleMinPrice, ");
        query.append("        RANK() OVER (PARTITION BY {pr.").append(DistPriceRowModel.PRODUCT).append("} ORDER BY {pct.")
            .append(DistErpPriceConditionTypeModel.PRIORITY).append("}, {pr.").append(DistPriceRowModel.LASTMODIFIEDERP).append("} DESC, {pr.")
            .append(DistPriceRowModel.MATCHVALUE).append("} DESC, {pr.").append(DistPriceRowModel.MINQTD).append("}, {pr.").append(DistPriceRowModel.PK).append("}) AS rankValue");
        query.append("        FROM {").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("            JOIN ").append(DistErpPriceConditionTypeModel._TYPECODE).append(" AS pct ");
        query.append("                ON {pr.").append(DistPriceRowModel.ERPPRICECONDITIONTYPE).append("} = {pct.").append(DistPriceRowModel.PK).append("} ");
        query.append("            JOIN ").append(CMSSiteModel._TYPECODE).append(" AS cms ");
        query.append("                ON {pr.").append(DistPriceRowModel.UG).append("} = {cms.").append(CMSSiteModel.USERPRICEGROUP).append("}");
        query.append("        } ");
        query.append(where("{pr.".concat(DistPriceRowModel.PRODUCT).concat("} = {p.").concat(ProductModel.PK).concat("}")
            .concat(" AND {cms.").concat(CMSSiteModel.PK).concat("} = (?CMSSite)")
            .concat(" AND ({pr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
            .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) ")));
        query.append("}}) minPrice ");
        query.append(where("minPrice.rankValue = 1"));
        query.append("}})");
        return query;
    }

    protected StringBuilder appendNetMarginSubselect(final StringBuilder query) {
        return query.append("({{\n")
                .append("    SELECT rankedPriceRows.netMarginRank FROM\n")
                .append("    ({{\n")
                .append("        SELECT {pr.").append(DistPriceRowModel.NETMARGINRANK).append("} AS netMarginRank,\n")
                .append("            RANK() OVER (PARTITION BY {pr.").append(DistPriceRowModel.PRODUCT).append("}")
                .append("               ORDER BY {pct.").append(DistErpPriceConditionTypeModel.PRIORITY).append("}, ")
                .append("                        {pr.").append(DistPriceRowModel.LASTMODIFIEDERP).append("} DESC, ")
                .append("                        {pr.").append(DistPriceRowModel.MATCHVALUE).append("} DESC, ")
                .append("                        {pr.").append(DistPriceRowModel.MINQTD).append("}, ")
                .append("                        {pr.").append(DistPriceRowModel.PK).append("}) AS rank \n")
                .append("        FROM {").append(DistPriceRowModel._TYPECODE).append(" AS pr\n")
                .append("            JOIN ").append(DistErpPriceConditionTypeModel._TYPECODE).append(" AS pct ON {pr.").append(DistPriceRowModel.ERPPRICECONDITIONTYPE).append("} = {pct.").append(DistErpPriceConditionTypeModel.PK).append("}\n")
                .append("            JOIN ").append(CMSSiteModel._TYPECODE).append(" AS site ON {pr.").append(DistPriceRowModel.UG).append("}={site.").append(CMSSiteModel.USERPRICEGROUP).append("}\n")
                .append("        }\n")
                .append("        WHERE  {site.").append(CMSSiteModel.PK).append("}=(?CMSSite)\n")
                .append("            AND {pr.").append(DistPriceRowModel.PRODUCT).append("}={p.").append(ProductModel.PK).append("}\n")
                .append("    }}) rankedPriceRows\n")
                .append("    WHERE rankedPriceRows.rank = 1\n")
                .append("}})");
    }

    protected String existsSpecialPriceSubselect() {
        final StringBuilder query = new StringBuilder();
        query.append("EXISTS({{SELECT 1 ");
        query.append("    FROM {").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("        JOIN ").append(DistErpPriceConditionTypeModel._TYPECODE).append(" AS pct ");
        query.append("            ON {pr.").append(DistPriceRowModel.ERPPRICECONDITIONTYPE).append("} = {pct.").append(DistPriceRowModel.PK).append("} ");
        query.append("        JOIN ").append(CMSSiteModel._TYPECODE).append(" AS cms ");
        query.append("            ON {pr.").append(DistPriceRowModel.UG).append("} = {cms.").append(CMSSiteModel.USERPRICEGROUP).append("}");
        query.append("    } ");
        query.append(where("{pr.".concat(DistPriceRowModel.PRODUCT).concat("} = {p.").concat(ProductModel.PK).concat("}")
            .concat(" AND {cms.").concat(CMSSiteModel.PK).concat("} = (?CMSSite)")
            .concat(" AND ({pr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
            .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) "))
            .concat(" AND {pct.").concat(DistErpPriceConditionTypeModel.CODE).concat("} = 'ZN00'"));
        query.append("}})");
        return query.toString();
    }

    protected StringBuilder appendProductURLSelect(final StringBuilder query, final String language) {
        query.append(distSqlUtils.concat("'/'", " ?".concat(LANGUAGE_ISOCODE).concat(language), "'/'",
            "{p.".concat(ProductModel.NAMESEO).concat(languageSelector(language)).concat(":o}"),
            "CASE WHEN {m.".concat(DistManufacturerModel.NAMESEO).concat(":o} IS NULL THEN '' ELSE ").concat(distSqlUtils.concat("'-'", "{m.".concat(DistManufacturerModel.NAMESEO)
                .concat(":o}"))).concat(" END "),
            "CASE WHEN {p.".concat(ProductModel.TYPENAMESEO).concat("} IS NULL THEN '' ELSE ").concat(distSqlUtils.concat("'-'", "{p.".concat(ProductModel.TYPENAMESEO)
                .concat("}"))).concat(" END "),
            "'/p/'", "{p.".concat(ProductModel.CODE).concat("} ")));
        return query;
    }

    protected StringBuilder appendProductImageURLSelect(final StringBuilder query, String mediaFormat) {
        query.append("({{ ");
        query.append("  SELECT {media.").append(MediaModel.INTERNALURL).append("} ");
        query.append("  FROM ");
        query.append("     { ").append(MediaModel._TYPECODE).append("! AS media ");
        query.append("     JOIN ").append(MediaFormatModel._TYPECODE).append("! AS mf ");
        query.append("           ON {mf.").append(MediaFormatModel.PK).append("} = {media.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("             AND {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(mediaFormat).append("' ");
        query.append("     } ");
        query.append(where("{media.".concat(MediaModel.MEDIACONTAINER).concat("} = ")
            .concat("      CASE WHEN {p.").concat(ProductModel.PRIMARYIMAGE).concat("} IS NOT NULL THEN {p.").concat(ProductModel.PRIMARYIMAGE).concat("} ELSE {p.").concat(ProductModel.ILLUSTRATIVEIMAGE).concat("} END ")));
        query.append("}}) ");
        return query;
    }

    protected StringBuilder appendAdditionalProductImageURLSelect(final StringBuilder query) {
        query.append("({{ ");
        query.append("  SELECT ").append(distSqlUtils.concat(chr("123"),
            stringAgg(distSqlUtils.concat("'\"'", "{mf.".concat(MediaFormatModel.QUALIFIER).concat("}"), "'\":\"'", "{media.".concat(MediaModel.INTERNALURL).concat("}"), "'\"'"), ",", "{mf.".concat(MediaFormatModel.QUALIFIER).concat("}")),
            chr("125")));
        query.append("  FROM { ");
        query.append("    ").append(MediaModel._TYPECODE).append("! AS media ");
        query.append("    JOIN ").append(MediaFormatModel._TYPECODE).append("! AS mf ");
        query.append("      ON {mf.").append(MediaFormatModel.PK).append("} = {media.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("        AND (");
        query.append("          {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.LANDSCAPE_SMALL).append("' ");
        query.append("          OR ");
        query.append("          {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.LANDSCAPE_MEDIUM).append("' ");
        query.append("          OR ");
        query.append("          {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.LANDSCAPE_SMALL_WEBP).append("' ");
        query.append("          OR ");
        query.append("          {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.LANDSCAPE_MEDIUM_WEBP).append("' ");
        query.append("        ) ");
        query.append("  } ");
        query.append(where(" {media.".concat(MediaModel.INTERNALURL).concat("} IS NOT NULL ")
            .concat(" AND {media.").concat(MediaModel.MEDIACONTAINER).concat("} = ")
            .concat("   CASE WHEN {p.").concat(ProductModel.PRIMARYIMAGE).concat("} IS NOT NULL THEN {p.").concat(ProductModel.PRIMARYIMAGE).concat("} ELSE {p.").concat(ProductModel.ILLUSTRATIVEIMAGE).concat("} END ")));
        query.append("}}) ");
        return query;
    }

    protected StringBuilder appendManufacturerURLSelect(final StringBuilder query, final String language) {
        query.append(distSqlUtils.concat("'/'", "?".concat(LANGUAGE_ISOCODE).concat(language),
            "'/manufacturer/'",
            " CASE WHEN {m.".concat(DistManufacturerModel.NAMESEO).concat("} IS NOT NULL ")
                    .concat(" THEN ").concat(distSqlUtils.concat("{m.".concat(DistManufacturerModel.NAMESEO).concat("}"), "'/'"))
                    .concat(" ELSE ''")
                    .concat(" END"),
            "{m.".concat(DistManufacturerModel.CODE).concat("}")));

        return query;
    }

    protected StringBuilder appendManufacturerImageURLSelect(final StringBuilder query) {
        query.append("({{ ");
        query.append("    SELECT CASE WHEN {media." + MediaModel.INTERNALURL + "} is not null THEN {media." + MediaModel.INTERNALURL + "} ELSE '' END ");
        query.append("    FROM { ");
        query.append("        " + MediaModel._TYPECODE + "! AS media ");
        query.append("        JOIN " + MediaFormatModel._TYPECODE + "! as mf ON ");
        query.append("            {mf." + MediaFormatModel.PK + "} = {media." + MediaModel.MEDIAFORMAT + "} ");
        query.append("            AND {mf." + MediaFormatModel.QUALIFIER + "} = '" + DistConstants.MediaFormat.BRAND_LOGO + "' ");
        query.append("    } ");
        query.append(where(" {media." + MediaModel.MEDIACONTAINER + "} = {m." + DistManufacturerModel.PRIMARYIMAGE + "} "));
        query.append("}}) ");
        return query;
    }

    protected StringBuilder appendEnergyEfficiencyLabelImageURLSelect(final StringBuilder query) {
        query.append("({{ ");
        query.append("  SELECT {media.").append(MediaModel.INTERNALURL).append("} ");
        query.append("  FROM ");
        query.append("     { ").append(MediaModel._TYPECODE).append("! AS media ");
        query.append("     JOIN ").append(MediaFormatModel._TYPECODE).append("! AS mf ");
        query.append("           ON {mf.").append(MediaFormatModel.PK).append("} = {media.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("             AND {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.PORTRAIT_MEDIUM).append("' ");
        query.append("     } ");
        query.append(where("{media.".concat(MediaModel.MEDIACONTAINER).concat("} = {p.").concat(ProductModel.ENERGYEFFICIENCYLABELIMAGE).concat("}")));
        query.append("}}) ");
        return query;
    }

    protected StringBuilder appendSalesUnitSelect(final StringBuilder query, final String language) {
        query.append(" CASE ");
        query.append("   WHEN {dsu.").append(DistSalesUnitModel.NAME).append(languageSelector(language)).append(":o} IS NOT NULL THEN {dsu.").append(DistSalesUnitModel.NAME).append(languageSelector(language)).append(":o} ");
        query.append("   WHEN {dsu.").append(DistSalesUnitModel.NAMEERP).append(languageSelector(language)).append(":o} IS NOT NULL THEN {dsu.").append(DistSalesUnitModel.NAMEERP).append(languageSelector(language)).append(":o} ");
        query.append("   WHEN {u.").append(UnitModel.NAME).append(languageSelector(language)).append(":o} IS NOT NULL THEN ".concat(distSqlUtils.concat("'1 '", "{u.".concat(UnitModel.NAME).concat(languageSelector(language)).concat(":o}"))));
        query.append("   ELSE '-' ");
        query.append(" END ");
        return query;
    }

    protected StringBuilder appendSingleUnitPrice(final StringBuilder query) {
        StringBuilder subQuery = new StringBuilder();
        subQuery.append("(");
        appendSingleMinPriceSubselect(subQuery);
        subQuery.append(" / {dsu.amount})");

        query.append(" CASE WHEN {dsu.amount} IS NOT NULL THEN ").append(toNvarchar(subQuery.toString())).append(" ELSE '' END ");
        return query;
    }

    /**
     * Currently, PromotionLabel code/name/value fields need to be selected manually, since their values or defined implicitly on the {@link DistSalesOrgProductModel} by datefrom/to fields.
     * If the modelling of the PromotionLabels changes to be more dynamic, the SQL can be changed as well.
     *
     * See: https://wiki.namics.com/display/distrelint/G121+Promotion+Labels
     * And: https://wiki.namics.com/display/distrelint/Promotion+Label
     *
     * Labels are exported in the following format e.g.:
     * |hit=hit:3:1|
     * [{code:hit,label:hit,rank:3,active=true}, {code:bla,label:top,rank:3,active=false}]
     * <DistPromotionLabel.code>=<DistPromotionLabel.name>:<DistPromotionLabel.rank>:<DistPromotionLabel is valid>
     *
     * CHAR(123) = {
     * CHAR(125) = }
     */
    protected StringBuilder appendPromotionLabelsSelect(final StringBuilder query, final String language) {
        query.append(distSqlUtils.concat("'['",
                getPromotionLabelSubselect(language, DistFactFinderExportPromotionColumnAttributes.HOTOFFER.getValue(), "pc." + ProductCountryModel.SHOWHOTOFFERLABELFROMDATE, "pc." + ProductCountryModel.SHOWHOTOFFERLABELUNTILDATE),
                "','",
                getPromotionLabelSubselect(language, DistFactFinderExportPromotionColumnAttributes.NO_MOVER.getValue(), "pc." + ProductCountryModel.SHOWNOMOVERLABELFROMDATE, "pc." + ProductCountryModel.SHOWNOMOVERLABELUNTILDATE),
                "','",
                getPromotionLabelSubselect(language, DistFactFinderExportPromotionColumnAttributes.TOP.getValue(), "pc." + ProductCountryModel.SHOWTOPLABELFROMDATE, "pc." + ProductCountryModel.SHOWTOPLABELUNTILDATE),
                "','",
                getPromotionLabelSubselect(language, DistFactFinderExportPromotionColumnAttributes.HIT.getValue(), "pc." + ProductCountryModel.SHOWHITLABELFROMDATE, "pc." + ProductCountryModel.SHOWHITLABELUNTILDATE),
                "','",
                chr("123"), "?".concat(PROMOLABEL_ATTRNAME).concat(language).concat(DistFactFinderExportPromotionColumnAttributes.USED.getValue()),
                "(CASE "
                        .concat("        WHEN  ( {p.").concat(ProductModel.SHOWUSEDLABEL).concat("} = 1 ) THEN 'true' ")
                        .concat("        ELSE 'false' ")
                        .concat("        END) "),
                chr("125"),
                "','",

                chr("123"), "?".concat(PROMOLABEL_ATTRNAME).concat(language).concat(DistFactFinderExportPromotionColumnAttributes.CALIBRATIONSERVICE.getValue()),
                "(CASE "
                        .concat("        WHEN  ( {p.").concat(ProductModel.CALIBRATED).concat("} = 1 ) THEN 'true' ")
                        .concat("        ELSE 'false' ")
                        .concat("        END) "),
                chr("125"),
                "','",

                getPromotionLabelSubselect(language, DistFactFinderExportPromotionColumnAttributes.NEW.getValue(), "dsop." + DistSalesOrgProductModel.SHOWNEWLABELFROMDATE, "dsop." + DistSalesOrgProductModel.SHOWNEWLABELUNTILDATE),
                "','",
                getPromotionLabelSubselect(language, DistFactFinderExportPromotionColumnAttributes.BESTSELLER.getValue(), "dsop." + DistSalesOrgProductModel.SHOWBESTSELLERLABELFROMDATE, "dsop." + DistSalesOrgProductModel.SHOWBESTSELLERLABELUNTILDATE),
                "','",
                chr("123"), "?".concat(PROMOLABEL_ATTRNAME).concat(language).concat(DistFactFinderExportPromotionColumnAttributes.OFFER.getValue()),
                "(CASE "
                        .concat("          WHEN " + existsSpecialPriceSubselect() + " THEN 'true' ")
                        .concat("          ELSE 'false' ")
                        .concat("          END) "),
                chr("125"),
                "']'"));

        return query;
    }

    protected String getPromotionLabelSubselect(final String language, final String code, final String fromField, final String untilField) {
        final StringBuilder query = new StringBuilder();
        query.append(distSqlUtils.concat(chr("123"), "?".concat(PROMOLABEL_ATTRNAME).concat(language).concat(code),
                "(CASE "
                        .concat("        WHEN  ( {").concat(fromField).concat("} <= (?").concat(DATE).concat("))")
                        .concat("         AND  ( (?").concat(DATE).concat(") <= {").concat(untilField).concat("} ) THEN 'true' ")
                        .concat("        ELSE 'false' ")
                        .concat("        END) "),
                chr("125")));
        return query.toString();
    }

    /**
     * Replacements are exported in the following format e.g.: <br />
     * {"eolDate":"2013-12-01 00:00:00","buyable":true,"reason":"Name of repl. reason"}
     *
     * CHAR(123) = {
     * CHAR(125) = }
     */
    protected StringBuilder appendReplacementSelect(final StringBuilder query, final Set<String> languages) {

        for (String language : languages) {
            query.append("({{ ");
            query.append("  SELECT ").append(distSqlUtils.concat(chr("123"),
                    "CASE WHEN {dsop.".concat(DistSalesOrgProductModel.ENDOFLIFEDATE).concat("} IS NOT NULL THEN ").concat(distSqlUtils.concat("'\"eolDate\":\"'",
                            toNvarchar("{dsop.".concat(DistSalesOrgProductModel.ENDOFLIFEDATE).concat("}"), ORACLE_DATE_TIME_PATTERN), "'\"'")).concat(" ELSE '' END "),
                    " '\"buyable\":'", "CASE WHEN {pRepl.".concat(ProductModel.PK).concat("} IS NOT NULL ")
                            .concat("      AND {pOrig.").concat(ProductModel.REPLACEMENTFROMDATE).concat("} <= ?").concat(DATE)
                            .concat("      AND ({pOrig.").concat(ProductModel.REPLACEMENTUNTILDATE).concat("} IS NULL OR {pOrig.").concat(ProductModel.REPLACEMENTUNTILDATE).concat("} >= ?").concat(DATE).concat(") ")
                            .concat("      AND {dssRepl.").concat(DistSalesStatusModel.BUYABLEINSHOP).concat("} = 1 THEN 'true' ELSE 'false' END "),
                    "CASE WHEN {drr.".concat(DistReplacementReasonModel.NAME).concat(languageSelector(language)).concat(":o} IS NOT NULL THEN ").concat(distSqlUtils.concat("',\"reason\":\"'", "{drr.".concat(DistReplacementReasonModel.NAME).concat(languageSelector(language)).concat(":o}"), "'\"'"))
                            .concat("      WHEN {drr.").concat(DistReplacementReasonModel.NAMEERP).concat(languageSelector(language)).concat(":o} IS NOT NULL THEN ").concat(distSqlUtils.concat("',\"reason\":\"'", "{drr.".concat(DistReplacementReasonModel.NAMEERP).concat(languageSelector(language)).concat(":o}"), "'\"'")).concat(" ELSE '' END "),
                    chr("125")));
            query.append("  FROM {").append(ProductModel._TYPECODE).append(" AS pOrig ");
            query.append("    LEFT JOIN ").append(ProductModel._TYPECODE).append(" AS pRepl ON {pOrig.").append(ProductModel.REPLACEMENTPRODUCT).append("} = {pRepl.").append(ProductModel.PK).append("} ");
            query.append("    LEFT JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsopRepl ON {pRepl.").append(ProductModel.PK).append("} = {dsopRepl.").append(DistSalesOrgProductModel.PRODUCT).append("} ");
            query.append("    LEFT JOIN ").append(DistSalesStatusModel._TYPECODE).append(" AS dssRepl ON {dsopRepl.").append(DistSalesOrgProductModel.SALESSTATUS).append("} = {dssRepl.").append(DistSalesStatusModel.PK).append("} ");
            query.append("    LEFT JOIN ").append(DistReplacementReasonModel._TYPECODE).append(" AS drr ON {pOrig.").append(ProductModel.REPLACEMENTREASON).append("} = {drr.").append(DistReplacementReasonModel.PK).append("} ");
            query.append("  } ");
            query.append("  WHERE {pOrig.").append(ProductModel.PK).append("} = {p.").append(ProductModel.PK).append("} ");
            query.append("   AND {dsopRepl.").append(DistSalesOrgModel.PK).append("} = ?").append(DistSalesOrgModel._TYPECODE);
            query.append("}}) ");
            query.append(" AS \"").append(REPLACEMENT.getValue()).append(languageSuffix(language)).append(CONCAT_SELECT_VALUE);
        }

        return query;
    }

    protected String languageSelector(final String languageIsoCode) {
        return "[" + languageIsoCode + "]";
    }

    protected String languageSuffix(final String languageIsoCode) {
        return "_l" + languageIsoCode;
    }

    protected String chr(String character) {
        return distSqlUtils.chr(character);
    }

    protected String stringAgg(String expression, String connector, String orderBy) {
        return distSqlUtils.stringAgg(expression, connector, orderBy);
    }

    protected String toNvarchar(String character) {
        return distSqlUtils.toNvarchar(character);
    }

    protected String toNvarchar(String character, String format) {
        return distSqlUtils.toNvarchar(character, format);
    }

    protected String where(String where) {
        return distSqlUtils.where(where);
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    @Required
    public void setConfigurationService(final ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
}
