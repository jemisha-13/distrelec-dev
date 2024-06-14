/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.query;

import static com.namics.distrelec.b2b.core.inout.export.impl.DistDefaultCsvTransformationService.ENCODE_FF_SUFFIX;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.GROSS;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.ENERGY_EFFICIENCY_LABEL_IMAGE_URL;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.NORMALIZED_ALTERNATE_ALIAS_MPN;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.ALTERNATE_ALIAS_MPN;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.MIN;
import static com.namics.hybris.ffsearch.constants.DistrelecfactfindersearchConstants.NET;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.ACCESSORY;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.ADDITIONAL_IMAGE_URLS;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.ALTERNATIVES_MPN;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.AVAILABLEINPICKUP;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.BUYABLE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY1;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY1_CODE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY2;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY2_CODE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY3;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY3_CODE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY4;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY4_CODE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY5;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY5_CODE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORYPATH;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY_CODE_PATH;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.CATEGORY_EXTENSIONS;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.DESCRIPTION;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.EAN;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.ENERGY_EFFICIENCY;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.IMAGE_URL;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.INSTOCK;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.ITEM_CATEGORY_GROUP;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.ITEMS_MIN;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.ITEMS_STEP;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.MANUFACTURER;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.MANUFACTURER_IMAGE_URL;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.MANUFACTURER_URL;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.NORMALIZED_ALTERNATIVES_MPN;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PICKUPSTOCK;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PIM_WEB_USE_D;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRICE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRODUCT_FAMILY_CODE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRODUCT_FAMILY_NAME;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRODUCT_HIERARCHY;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRODUCT_LINE_NAME;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRODUCT_NUMBER;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRODUCT_NUMBER_ELFA;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRODUCT_NUMBER_MOVEX;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PRODUCT_URL;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.PROMOTIONLABELS;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.REPLACEMENT;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.SALESUNIT;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.SINGLE_MIN_PRICE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.SINGLE_UNIT_PRICE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.SYNONYM_FULL_MATCH;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.TITLE;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.TITLE_SHORT;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.TOTALINSTOCK;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.TYPENAME;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.TYPE_NAME_NORMALIZED;
import static com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns.WEB_USE;
import static com.namics.hybris.ffsearch.export.query.DistFactFinderProductExportParameterProvider.PROMOLABEL_ATTRNAME;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.enums.DistErpSystem;
import com.namics.distrelec.b2b.core.inout.export.DistFlexibleSearchQueryCreator;
import com.namics.distrelec.b2b.core.model.DistCOPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistErpPriceConditionTypeModel;
import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.model.DistPimCategoryTypeModel;
import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import com.namics.distrelec.b2b.core.model.DistReplacementReasonModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.model.DistSalesUnitModel;
import com.namics.distrelec.b2b.core.model.ProductCountryModel;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportColumns;
import com.namics.hybris.ffsearch.export.columns.DistFactFinderExportPromotionColumnAttributes;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.link.LinkModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaFormatModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.price.TaxModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.product.UnitModel;
import de.hybris.platform.europe1.model.PriceRowModel;
import de.hybris.platform.europe1.model.TaxRowModel;
import org.springframework.beans.factory.annotation.Autowired;

//@formatter:off
/**
 * Builder which creates the flexible search query for exporting the product data to FactFinder.
 *
 * KEEP IN MIND:
 * When changing the logic of the Query - please always also update the respective FlexiSerach above the method createQuery().
 * and if you change the webUse section also the query for the similar product conversion in DefaultDistWebuseAttributesDao
 *
 * Improvement in DistFactFinderProductExportQueryCreator2:
 * - Concatenation of Webuse attribute is done during PIM import and stored in the database.
 *
 * @author bhauser, Namics AG
 * @author ceberle, Namics AG
 * @author ascherrer, Namics AG
 * @since Namics Extensions 1.0
 */
@SuppressWarnings({ "PMD" })
@Deprecated
public class DistFactFinderProductExportQueryCreator3 implements DistFlexibleSearchQueryCreator {

    private static final Logger LOG = LogManager.getLogger(DistFactFinderProductExportQueryCreator3.class);

    public static final int QUERY_LENGTH = 41000;

    private static final String CATEGORIES_JOIN_PK = "productpk";
    private static final String INSTOCK_JOIN_PK = "productpk";
    private static final String PRICES_JOIN_VALUE = "prices";
    private static final String PRICE_JOIN_PK = "productpk";
    private static final char CATEGORY_SEPARATOR = '/';

    private static final String CONCAT_SELECT_VALUE = "\", ";

    private static final String ORACLE_DATE_TIME_PATTERN = "YYYY-MM-DD HH24:MI:SS";

    @Autowired
    private DistSqlUtils distSqlUtils;
    
    /*
    SELECT {p.code} AS "ProductNumber",
            {p.ean} AS "EAN",
            {p.codeElfa} AS "ProductNumberElfa",
            {p.codeMovex} AS "ProductNumberMovex",
            {p.name:o} AS "Title",
            TO_NVARCHAR({p.description:o}) AS "Description",
            '/' + ?LanguageIsocode + '/'
                + {p.nameSeo:o}
                + CASE WHEN {m.nameSeo:o} IS NULL THEN '' ELSE '-' + {m.nameSeo:o} END
                + CASE WHEN {p.typeNameSeo} IS NULL THEN '' ELSE '-' + {p.typeNameSeo} END
                + '/p/' + {p.code} AS "ProductURL",
            ({{
                SELECT {media.URL}
                FROM {
                    Media! AS media
                    JOIN MediaFormat! AS mf ON {mf.pk} = {media.mediaFormat} AND {mf.qualifier} = 'portrait_small'
                }
                WHERE {media.mediaContainer} = CASE WHEN {p.primaryImage} IS NOT NULL THEN {p.primaryImage} ELSE {p.illustrativeImage} END
            }}) AS "ImageURL",
            ({{
                SELECT CHAR(123)
                    + STRING_AGG('"' + {mf.qualifier} + '":"' + {media.URL} + '"', ',') WITHIN GROUP (ORDER BY {mf.qualifier})
                    +  CHAR(125)
                FROM {
                    Media! AS media
                    JOIN MediaFormat! AS mf ON {mf.pk} = {media.mediaFormat}
                        AND ({mf.qualifier} = 'landscape_small' OR {mf.qualifier} = 'landscape_medium')
                }
                WHERE {media.URL} is not null
                    AND {media.mediaContainer} = CASE WHEN {p.primaryImage} IS NOT NULL THEN {p.primaryImage} ELSE {p.illustrativeImage} END
            }}) AS "AdditionalImageURLs",
            {m.name:o}  AS "Manufacturer",
            '/' + ?LanguageIsocode + '/manufacturer/' + CASE WHEN {m.nameSeo} IS NOT NULL THEN {m.nameSeo} + '/' ELSE '' END + {m.code} AS "ManufacturerURL",
            ({{
                SELECT CASE WHEN {media.URL} is not null THEN {media.URL} ELSE '' END
                FROM {
                    Media! AS media
                    JOIN MediaFormat! as mf ON {mf.pk} = {media.mediaFormat} AND {mf.qualifier} = 'brand_logo'
                }
                WHERE {media.mediaContainer} = {m.primaryImage}
            }}) AS "ManufacturerImageURL",
            price.Price AS "Price",
            {p.pimWebUse:o} AS "WebUse",
            cats.cat1name_encodeff  AS "Category1_encodeff",
            cats.cat2name_encodeff  AS "Category2_encodeff",
            cats.cat3name_encodeff  AS "Category3_encodeff",
            cats.cat4name_encodeff  AS "Category4_encodeff",
            cats.cat5name_encodeff  AS "Category5_encodeff",
            CASE WHEN cats.cat1nameSeo IS NOT NULL THEN cats.cat1nameSeo ELSE '' END +
                CASE WHEN cats.cat2nameSeo IS NOT NULL THEN '/' + cats.cat2nameSeo ELSE '' END +
                CASE WHEN cats.cat3nameSeo IS NOT NULL THEN '/' + cats.cat3nameSeo ELSE '' END +
                CASE WHEN cats.cat4nameSeo IS NOT NULL THEN '/' + cats.cat4nameSeo ELSE '' END +
                CASE WHEN cats.cat5nameSeo IS NOT NULL THEN '/' + cats.cat5nameSeo ELSE '' END +
                CASE WHEN cats.cat6nameSeo IS NOT NULL THEN '/' + cats.cat6nameSeo ELSE '' END +
                CASE WHEN cats.cat7nameSeo IS NOT NULL THEN '/' + cats.cat7nameSeo ELSE '' END AS "CategoryPath",
            ( TO_CLOB('[')
                + CASE WHEN cats.cat1nameSeo IS NOT NULL THEN CHAR(123) + ' "url" : "/' + ?LanguageIsocode
                    + '/' + cats.cat1nameSeo
                    + '/c/' + cats.cat1code + '"' + CASE WHEN cats.cat1imageUrl IS NOT NULL THEN ', "imageUrl" : "' + cats.cat1imageUrl + '"' ELSE '' END + CHAR(125) + ', ' ELSE '' END
                + CASE WHEN cats.cat2nameSeo IS NOT NULL THEN CHAR(123) + ' "url" : "/' + ?LanguageIsocode
                    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo
                    + '/c/' + cats.cat2code + '"' + CASE WHEN cats.cat2imageUrl IS NOT NULL THEN ', "imageUrl" : "' + cats.cat2imageUrl + '"' ELSE '' END + CHAR(125) + ', ' ELSE '' END
                + CASE WHEN cats.cat3nameSeo IS NOT NULL THEN CHAR(123) + ' "url" : "/' + ?LanguageIsocode
                    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo
                    + '/c/' + cats.cat3code + '"' + CASE WHEN cats.cat3imageUrl IS NOT NULL THEN ', "imageUrl" : "' + cats.cat3imageUrl + '"' ELSE '' END + CHAR(125) + ', ' ELSE '' END
                + CASE WHEN cats.cat4nameSeo IS NOT NULL THEN CHAR(123) + ' "url" : "/' + ?LanguageIsocode
                    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo + '/' + cats.cat4nameSeo
                    + '/c/' + cats.cat4code + '"' + CASE WHEN cats.cat4imageUrl IS NOT NULL THEN ', "imageUrl" : "' + cats.cat4imageUrl + '"' ELSE '' END + CHAR(125) + ', ' ELSE '' END
                + CASE WHEN cats.cat5nameSeo IS NOT NULL THEN CHAR(123) + ' "url" : "/' + ?LanguageIsocode
                    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo + '/' + cats.cat4nameSeo + '/' + cats.cat5nameSeo
                    + '/c/' + cats.cat5code + '"' + CASE WHEN cats.cat5imageUrl IS NOT NULL THEN ', "imageUrl" : "' + cats.cat5imageUrl + '"' ELSE '' END + CHAR(125) + ', ' ELSE '' END
                + CASE WHEN cats.cat6nameSeo IS NOT NULL THEN CHAR(123) + ' "url" : "/' + ?LanguageIsocode
                    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo + '/' + cats.cat4nameSeo + '/' + cats.cat5nameSeo + '/' + cats.cat6nameSeo
                    + '/c/' + cats.cat6code + '"' + CASE WHEN cats.cat6imageUrl IS NOT NULL THEN ', "imageUrl" : "' + cats.cat6imageUrl + '"' ELSE '' END + CHAR(125) + ', ' ELSE '' END
                + CASE WHEN cats.cat7nameSeo IS NOT NULL THEN CHAR(123) + ' "url" : "/' + ?LanguageIsocode
                    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo + '/' + cats.cat4nameSeo + '/' + cats.cat5nameSeo + '/' + cats.cat6nameSeo + '/' + cats.cat7nameSeo
                    + '/c/' + cats.cat7code + '"' + CASE WHEN cats.cat7imageUrl IS NOT NULL THEN ', "imageUrl" : "' + cats.cat7imageUrl + '"' ELSE '' END + CHAR(125) + ', ' ELSE '' END
                + ']' ) AS "CategoryExtensions",
            {p.typeName} AS "TypeName",
            REPLACE_REGEXPR('[^a-zA-Z0-9]' IN {p.typeName} WITH '') AS "TypeNameNormalized",
            
            stock.InStock AS "InStock",
            stock.totalInStock AS "totalInStock",
            stock.availableInPickup AS "availableInPickup",
            stock.pickupStock AS "availableInPickup",
            
            {dsop.orderQuantityMinimum} AS "ItemsMin",
            {dsop.orderQuantityStep}  AS "ItemsStep",
            CASE
                WHEN {dsu.name:o} IS NOT NULL THEN {dsu.name:o}
                WHEN {dsu.nameErp:o} IS NOT NULL THEN {dsu.nameErp:o}
                WHEN {u.name:o} IS NOT NULL THEN '1 ' + {u.name:o}
                ELSE '-'
            END AS "SalesUnit",
            '[' + CHAR(123) + ?dplParam_hotOffer   + ',active:' + (CASE WHEN ( {pc.showHotOfferLabelFromDate} <= (?Date) )     AND ( (?Date) <= {pc.showHotOfferLabelUntilDate} ) THEN 'true' ELSE 'false' END) + CHAR(125) + ','
                + CHAR(123) + ?dplParam_noMover    + ',active:' + (CASE WHEN ( {pc.showNoMoverLabelFromDate} <= (?Date) )      AND ( (?Date) <= {pc.showNoMoverLabelUntilDate} ) THEN 'true' ELSE 'false' END) + CHAR(125) + ','
                + CHAR(123) + ?dplParam_top        + ',active:' + (CASE WHEN ( {pc.showTopLabelFromDate} <= (?Date) )          AND ( (?Date) <= {pc.showTopLabelUntilDate} ) THEN 'true' ELSE 'false' END) + CHAR(125) + ','
                + CHAR(123) + ?dplParam_hit        + ',active:' + (CASE WHEN ( {pc.showHitLabelFromDate} <= (?Date) )          AND ( (?Date) <= {pc.showHitLabelUntilDate} ) THEN 'true' ELSE 'false' END) + CHAR(125) + ','
                + CHAR(123) + ?dplParam_used       + ',active:' + (CASE WHEN ( {p.showUsedLabel} = 1 ) THEN 'true' ELSE 'false' END) + CHAR(125) + ','
                + CHAR(123) + ?dplParam_new        + ',active:' + (CASE WHEN ( {dsop.showNewLabelFromDate} <= (?Date) )        AND ( (?Date) <= {dsop.showNewLabelUntilDate} ) THEN 'true' ELSE 'false' END) + CHAR(125) + ','
                + CHAR(123) + ?dplParam_bestseller + ',active:' + (CASE WHEN ( {dsop.showBestsellerLabelFromDate} <= (?Date) ) AND ( (?Date) <= {dsop.showBestsellerLabelUntilDate} ) THEN 'true' ELSE 'false' END) + CHAR(125) + ','
                + CHAR(123) + ?dplParam_offer      + ',active:' + (CASE WHEN ( singleMinPrice.specialPrice = 1 ) THEN 'true' ELSE 'false' END) + CHAR(125)
                + ']' AS "PromotionLabels",
            ({{
                SELECT CHAR(123) + CASE WHEN {dsop.endOfLifeDate} IS NOT NULL THEN '"eolDate":"' + TO_NVARCHAR({dsop.endOfLifeDate}, 'YYYY-MM-DD HH24:MI:SS') + '",' ELSE '' END
                            + '"buyable":' + CASE WHEN {pRepl.pk} IS NOT NULL
                                AND {pOrig.replacementFromDate} <= ?Date
                                AND ({pOrig.replacementUntilDate} IS NULL OR {pOrig.replacementUntilDate} >= ?Date)
                                AND {dssRepl.buyableInShop} = 1 THEN 'true' ELSE 'false' END
                            + CASE WHEN {drr.name:o} IS NOT NULL THEN ',"reason":"' + {drr.name:o} + '"' WHEN {drr.nameErp:o} IS NOT NULL THEN ',"reason":"' + {drr.nameErp:o} + '"' ELSE '' END
                            + CHAR(125)
                FROM {Product AS pOrig
                    LEFT JOIN Product AS pRepl ON {pOrig.replacementProduct} = {pRepl.pk}
                    LEFT JOIN DistSalesOrgProduct AS dsopRepl ON {pRepl.pk} = {dsopRepl.product} AND {dsopRepl.salesOrg} = ?DistSalesOrg
                    LEFT JOIN DistSalesStatus AS dssRepl ON {dsopRepl.salesStatus} = {dssRepl.pk}
                    LEFT JOIN DistReplacementReason AS drr ON {pOrig.replacementReason} = {drr.pk}
                }
                WHERE {pOrig.pk} = {p.pk}
            }}) AS "Replacement",
            CASE WHEN {dss.pk} IS NOT NULL AND {dss.buyableInShop} = 1 THEN 1 ELSE 0 END AS "Buyable",
            {p.nameShort:o} AS "TitleShort",
            {p.productLineName:o} AS "ProductLineName",
            {p.seoAccessory:o} AS "Accessory",
            {p.productFamilyName:o} AS "ProductFamilyName",
            singleMinPrice.singleMinPrice AS "singleMinPrice",
            {famCat.code} AS "productFamilyCode",
            {p.productHierarchy} AS "productHierarchy"
    FROM
        {
            Product AS p
            JOIN DistSalesOrgProduct AS dsop ON {dsop.product} = {p.pk} AND {dsop.salesOrg} = (?DistSalesOrg)
            LEFT JOIN DistSalesStatus AS dss ON {dss.pk} = {dsop.salesStatus}
            LEFT JOIN DistSalesUnit AS dsu ON {dsu.pk} = {p.salesUnit}
            LEFT JOIN Unit AS u ON {u.pk} = {p.unit}
            LEFT JOIN DistManufacturer AS m ON {m.pk} = {p.manufacturer}
            LEFT JOIN ProductCountry AS pc ON {pc.product} = {p.pk} AND {pc.country} = (?Country)
            JOIN ArticleApprovalStatus AS aas ON {p.approvalStatus} = {aas.pk}
            LEFT JOIN Category AS famCat ON {p.pimFamilyCategory} = {famCat.pk}
        },
        ({{
            SELECT  {p.pk} productpk,
                (case when {c6.level} = 1 then {c6.name:o} when {c5.level} = 1 then {c5.name:o} when {c4.level} = 1 then {c4.name:o} when {c3.level} = 1 then {c3.name:o} when {c2.level} = 1 then {c2.name:o} when {c1.level} = 1 then {c1.name:o} when {c0.level} = 1 then {c0.name:o} else '' end) cat1name_encodeff,
                (case when {c5.level} = 2 then {c5.name:o} when {c4.level} = 2 then {c4.name:o} when {c3.level} = 2 then {c3.name:o} when {c2.level} = 2 then {c2.name:o} when {c1.level} = 2 then {c1.name:o} when {c0.level} = 2 then {c0.name:o} else '' end) cat2name_encodeff,
                (case when {c4.level} = 3 then {c4.name:o} when {c3.level} = 3 then {c3.name:o} when {c2.level} = 3 then {c2.name:o} when {c1.level} = 3 then {c1.name:o} when {c0.level} = 3 then {c0.name:o} else '' end) cat3name_encodeff,
                (case when {c3.level} = 4 then {c3.name:o} when {c2.level} = 4 then {c2.name:o} when {c1.level} = 4 then {c1.name:o} when {c0.level} = 4 then {c0.name:o} else '' end) cat4name_encodeff,
                (case when {c2.level} = 5 then {c2.name:o} when {c1.level} = 5 then {c1.name:o} when {c0.level} = 5 then {c0.name:o} else '' end) cat5name_encodeff,
                (case when {c1.level} = 6 then {c1.name:o} when {c0.level} = 6 then {c0.name:o} else '' end) cat6name_encodeff,
                (case when {c0.level} = 7 then {c0.name:o} else '' end) cat7name_encodeff,
                (case when {c6.level} = 1 then {c6.nameSeo:o} when {c5.level} = 1 then {c5.nameSeo:o} when {c4.level} = 1 then {c4.nameSeo:o} when {c3.level} = 1 then {c3.nameSeo:o} when {c2.level} = 1 then {c2.nameSeo:o} when {c1.level} = 1 then {c1.nameSeo:o} when {c0.level} = 1 then {c0.nameSeo:o} else '' end) cat1nameSeo,
                (case when {c5.level} = 2 then {c5.nameSeo:o} when {c4.level} = 2 then {c4.nameSeo:o} when {c3.level} = 2 then {c3.nameSeo:o} when {c2.level} = 2 then {c2.nameSeo:o} when {c1.level} = 2 then {c1.nameSeo:o} when {c0.level} = 2 then {c0.nameSeo:o} else '' end) cat2nameSeo,
                (case when {c4.level} = 3 then {c4.nameSeo:o} when {c3.level} = 3 then {c3.nameSeo:o} when {c2.level} = 3 then {c2.nameSeo:o} when {c1.level} = 3 then {c1.nameSeo:o} when {c0.level} = 3 then {c0.nameSeo:o} else '' end) cat3nameSeo,
                (case when {c3.level} = 4 then {c3.nameSeo:o} when {c2.level} = 4 then {c2.nameSeo:o} when {c1.level} = 4 then {c1.nameSeo:o} when {c0.level} = 4 then {c0.nameSeo:o} else '' end) cat4nameSeo,
                (case when {c2.level} = 5 then {c2.nameSeo:o} when {c1.level} = 5 then {c1.nameSeo:o} when {c0.level} = 5 then {c0.nameSeo:o} else '' end) cat5nameSeo,
                (case when {c1.level} = 6 then {c1.nameSeo:o} when {c0.level} = 6 then {c0.nameSeo:o} else '' end) cat6nameSeo,
                (case when {c0.level} = 7 then {c0.nameSeo:o} else '' end) cat7nameSeo,
                (case when {c6.level} = 1 then {c6.code} when {c5.level} = 1 then {c5.code} when {c4.level} = 1 then {c4.code} when {c3.level} = 1 then {c3.code} when {c2.level} = 1 then {c2.code} when {c1.level} = 1 then {c1.code} when {c0.level} = 1 then {c0.code} else '' end) cat1code,
                (case when {c5.level} = 2 then {c5.code} when {c4.level} = 2 then {c4.code} when {c3.level} = 2 then {c3.code} when {c2.level} = 2 then {c2.code} when {c1.level} = 2 then {c1.code} when {c0.level} = 2 then {c0.code} else '' end) cat2code,
                (case when {c4.level} = 3 then {c4.code} when {c3.level} = 3 then {c3.code} when {c2.level} = 3 then {c2.code} when {c1.level} = 3 then {c1.code} when {c0.level} = 3 then {c0.code} else '' end) cat3code,
                (case when {c3.level} = 4 then {c3.code} when {c2.level} = 4 then {c2.code} when {c1.level} = 4 then {c1.code} when {c0.level} = 4 then {c0.code} else '' end) cat4code,
                (case when {c2.level} = 5 then {c2.code} when {c1.level} = 5 then {c1.code} when {c0.level} = 5 then {c0.code} else '' end) cat5code,
                (case when {c1.level} = 6 then {c1.code} when {c0.level} = 6 then {c0.code} else '' end) cat6code,
                (case when {c0.level} = 7 then {c0.code} else '' end) cat7code,
                
                (case when {c6.level} = 1 then {dpct6.code} when {c5.level} = 1 then {dpct5.code} when {c4.level} = 1 then {dpct4.code} when {c3.level} = 1 then {dpct3.code} when {c2.level} = 1 then {dpct2.code} when {c1.level} = 1 then {dpct1.code} when {c0.level} = 1 then {dpct0.code} else '' end) cat1PimTypecode,
                (case when {c5.level} = 2 then {dpct5.code} when {c4.level} = 2 then {dpct4.code} when {c3.level} = 2 then {dpct3.code} when {c2.level} = 2 then {dpct2.code} when {c1.level} = 2 then {dpct1.code} when {c0.level} = 2 then {dpct0.code} else '' end) cat2PimTypecode,
                (case when {c4.level} = 3 then {dpct4.code} when {c3.level} = 3 then {dpct3.code} when {c2.level} = 3 then {dpct2.code} when {c1.level} = 3 then {dpct1.code} when {c0.level} = 3 then {dpct0.code} else '' end) cat3PimTypecode,
                (case when {c3.level} = 4 then {dpct3.code} when {c2.level} = 4 then {dpct2.code} when {c1.level} = 4 then {dpct1.code} when {c0.level} = 4 then {dpct0.code} else '' end) cat4PimTypecode,
                (case when {c2.level} = 5 then {dpct2.code} when {c1.level} = 5 then {dpct1.code} when {c0.level} = 5 then {dpct0.code} else '' end) cat5PimTypecode,
                (case when {c1.level} = 6 then {dpct1.code} when {c0.level} = 6 then {dpct0.code} else '' end) cat6PimTypecode,
                (case when {c0.level} = 7 then {dpct0.code} else '' end) cat7PimTypecode,
                
                (case when {c6.level} = 1 then {media6.URL} when {c5.level} = 1 then {media5.URL} when {c4.level} = 1 then {media4.URL} when {c3.level} = 1 then {media3.URL} when {c2.level} = 1 then {media2.URL} when {c1.level} = 1 then {media1.URL} when {c0.level} = 1 then {media0.URL} else '' end) cat1imageUrl,
                (case when {c5.level} = 2 then {media5.URL} when {c4.level} = 2 then {media4.URL} when {c3.level} = 2 then {media3.URL} when {c2.level} = 2 then {media2.URL} when {c1.level} = 2 then {media1.URL} when {c0.level} = 2 then {media0.URL} else '' end) cat2imageUrl,
                (case when {c4.level} = 3 then {media4.URL} when {c3.level} = 3 then {media3.URL} when {c2.level} = 3 then {media2.URL} when {c1.level} = 3 then {media1.URL} when {c0.level} = 3 then {media0.URL} else '' end) cat3imageUrl,
                (case when {c3.level} = 4 then {media3.URL} when {c2.level} = 4 then {media2.URL} when {c1.level} = 4 then {media1.URL} when {c0.level} = 4 then {media0.URL} else '' end) cat4imageUrl,
                (case when {c2.level} = 5 then {media2.URL} when {c1.level} = 5 then {media1.URL} when {c0.level} = 5 then {media0.URL} else '' end) cat5imageUrl,
                (case when {c1.level} = 6 then {media1.URL} when {c0.level} = 6 then {media0.URL} else '' end) cat6imageUrl,
                (case when {c0.level} = 7 then {media0.URL} else '' end) cat7imageUrl
            
            FROM  { Product AS p
                LEFT JOIN CategoryProductRelation AS cpr ON  {p.pk} = {cpr.target}
                LEFT JOIN Category! AS c0 ON  {c0.pk} = {cpr.source}
                    LEFT JOIN MediaContainer AS mc0 ON {mc0.pk} = {c0.primaryImage}
                    LEFT JOIN Media AS media0 ON {media0.mediaContainer} = {mc0.pk}
                    LEFT JOIN MediaFormat AS mf0 ON {mf0.pk} = {media0.mediaFormat}
                    LEFT JOIN DistPimCategoryType AS dpct0 ON {dpct0.pk}={c0.pimCategoryType}
                LEFT JOIN CategoryCategoryRelation AS ccr1 ON  {c0.pk} = {ccr1.target}
                LEFT JOIN Category! AS c1 ON  {c1.pk} = {ccr1.source}
                    LEFT JOIN MediaContainer AS mc1 ON {mc1.pk} = {c1.primaryImage}
                    LEFT JOIN Media AS media1 ON {media1.mediaContainer} = {mc1.pk}
                    LEFT JOIN MediaFormat AS mf1 ON {mf1.pk} = {media1.mediaFormat}
                    LEFT JOIN DistPimCategoryType AS dpct1 ON {dpct1.pk}={c1.pimCategoryType}
                LEFT JOIN CategoryCategoryRelation AS ccr2 ON  {c1.pk} = {ccr2.target}
                LEFT JOIN Category! AS c2 ON  {c2.pk} = {ccr2.source}
                    LEFT JOIN MediaContainer AS mc2 ON {mc2.pk} = {c2.primaryImage}
                    LEFT JOIN Media AS media2 ON {media2.mediaContainer} = {mc2.pk}
                    LEFT JOIN MediaFormat AS mf2 ON {mf2.pk} = {media2.mediaFormat}
                    LEFT JOIN DistPimCategoryType AS dpct2 ON {dpct2.pk}={c2.pimCategoryType}
                LEFT JOIN CategoryCategoryRelation AS ccr3 ON  {c2.pk} = {ccr3.target}
                LEFT JOIN Category! AS c3 ON  {c3.pk} = {ccr3.source}
                    LEFT JOIN MediaContainer AS mc3 ON {mc3.pk} = {c3.primaryImage}
                    LEFT JOIN Media AS media3 ON {media3.mediaContainer} = {mc3.pk}
                    LEFT JOIN MediaFormat AS mf3 ON {mf3.pk} = {media3.mediaFormat}
                    LEFT JOIN DistPimCategoryType AS dpct3 ON {dpct3.pk}={c3.pimCategoryType}
                LEFT JOIN CategoryCategoryRelation AS ccr4 ON  {c3.pk} = {ccr4.target}
                LEFT JOIN Category! AS c4 ON  {c4.pk} = {ccr4.source}
                    LEFT JOIN MediaContainer AS mc4 ON {mc4.pk} = {c4.primaryImage}
                    LEFT JOIN Media AS media4 ON {media4.mediaContainer} = {mc4.pk}
                    LEFT JOIN MediaFormat AS mf4 ON {mf4.pk} = {media4.mediaFormat}
                    LEFT JOIN DistPimCategoryType AS dpct4 ON {dpct4.pk}={c4.pimCategoryType}
                LEFT JOIN CategoryCategoryRelation AS ccr5 ON  {c4.pk} = {ccr5.target}
                LEFT JOIN Category! AS c5 ON  {c5.pk} = {ccr5.source}
                    LEFT JOIN MediaContainer AS mc5 ON {mc5.pk} = {c5.primaryImage}
                    LEFT JOIN Media AS media5 ON {media5.mediaContainer} = {mc5.pk}
                    LEFT JOIN MediaFormat AS mf5 ON {mf5.pk} = {media5.mediaFormat}
                    LEFT JOIN DistPimCategoryType AS dpct5 ON {dpct5.pk}={c5.pimCategoryType}
                LEFT JOIN CategoryCategoryRelation AS ccr6 ON  {c5.pk} = {ccr6.target}
                LEFT JOIN Category! AS c6 ON  {c6.pk} = {ccr6.source}
                    LEFT JOIN MediaContainer AS mc6 ON {mc6.pk} = {c6.primaryImage}
                    LEFT JOIN Media AS media6 ON {media6.mediaContainer} = {mc6.pk}
                    LEFT JOIN MediaFormat AS mf6 ON {mf6.pk} = {media6.mediaFormat}
                    LEFT JOIN DistPimCategoryType AS dpct6 ON {dpct6.pk}={c6.pimCategoryType}
                LEFT JOIN CategoryCategoryRelation AS ccr7 ON  {c6.pk} = {ccr7.target}
                LEFT JOIN Category! AS c7 ON  {c7.pk} = {ccr7.source}
                    LEFT JOIN MediaContainer AS mc7 ON {mc7.pk} = {c7.primaryImage}
                    LEFT JOIN Media AS media7 ON {media7.mediaContainer} = {mc7.pk}
                    LEFT JOIN MediaFormat AS mf7 ON {mf7.pk} = {media7.mediaFormat}
                    LEFT JOIN DistPimCategoryType AS dpct7 ON {dpct7.pk}={c7.pimCategoryType}
                }
                
            WHERE ({mf0.qualifier} is null OR {mf0.qualifier} = 'landscape_small')
                AND ({mf1.qualifier} is null OR {mf1.qualifier} = 'landscape_small')
                AND ({mf2.qualifier} is null OR {mf2.qualifier} = 'landscape_small')
                AND ({mf3.qualifier} is null OR {mf3.qualifier} = 'landscape_small')
                AND ({mf4.qualifier} is null OR {mf4.qualifier} = 'landscape_small')
                AND ({mf5.qualifier} is null OR {mf5.qualifier} = 'landscape_small')
                AND ({mf6.qualifier} is null OR {mf6.qualifier} = 'landscape_small')
                AND ({mf7.qualifier} IS NULL OR {mf7.qualifier} = 'landscape_small')
        }}) cats,
        ({{
            SELECT minPrice.productpk, sum(minPrice.singleMinPrice) AS singleMinPrice, sum(minPrice.specialPrice) AS specialPrice
            FROM
            ({{
                SELECT innerSelect.productpk AS productpk, innerSelect.singleMinPrice AS singleMinPrice, innerSelect.specialPrice AS specialPrice
                FROM
                ({{
                    SELECT {p.pk} AS productpk,
                        ({pr.price} / {pr.unitFactor}) AS singleMinPrice,
                        (CASE WHEN {pct.code} = 'ZN00' THEN 1 ELSE 0 END) AS specialPrice,
                        RANK() OVER (PARTITION BY {p.pk} ORDER BY {pr.startTime} DESC) AS rankValue
                    FROM
                    {
                        Product AS p
                        JOIN DistPriceRow AS pr ON {p.pk} = {pr.product}
                        JOIN DistErpPriceConditionType AS pct ON {pr.erpPriceConditionType} = {pct.pk}
                        JOIN DistSalesOrgProduct AS dsop ON {p.pk} = {dsop.product}
                        JOIN DistSalesOrg AS dso ON {dsop.salesOrg} = {dso.pk} AND {dso.pk} = (?DistSalesOrg)
                        JOIN Currency AS cur ON {pr.currency} = {cur.pk}
                        JOIN CMSSite AS cms ON {pr.ug} = {cms.userPriceGroup} AND {cms.pk} = (?CMSSite)
                        JOIN TaxRow AS tr ON {cms.userTaxGroup} = {tr.ug} AND {tr.pg} = {dsop.productTaxGroup}
                        JOIN Tax AS tax ON {tr.tax} = {tax.pk}
                    }
                    WHERE ({pr.startTime} IS NULL OR {pr.startTime} <= (?Date))
                        AND ({pr.endTime} IS NULL OR {pr.endTime} >= (?Date))
                        AND {pr.minqtd} =  ({{
                            SELECT min({minpr.minqtd})
                            FROM { DistPriceRow AS minpr }
                            WHERE {minpr.product} = {pr.product}
                                AND {minpr.net} = {pr.net}
                                AND {minpr.ug} = {pr.ug}
                                AND {minpr.currency} = {pr.currency}
                                AND ({minpr.startTime} IS NULL OR {minpr.startTime} <= (?Date))
                                AND ({minpr.endTime} IS NULL OR {minpr.endTime} >= (?Date))
                                AND {minpr.matchValue} =  ({{
                                    SELECT max({matchvaluepr.matchValue})
                                    FROM { DistPriceRow AS matchvaluepr }
                                    WHERE {matchvaluepr.product} = {minpr.product}
                                        AND {matchvaluepr.net} = {minpr.net}
                                        AND {matchvaluepr.ug} = {minpr.ug}
                                        AND {matchvaluepr.currency} = {minpr.currency}
                                        AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date))
                                        AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date))
                                    }})
                        }})
                        AND {pr.matchValue} =  ({{
                            SELECT max({matchvaluepr.matchValue})
                            FROM { DistPriceRow AS matchvaluepr }
                            WHERE {matchvaluepr.product} = {pr.product}
                                AND {matchvaluepr.net} = {pr.net}
                                AND {matchvaluepr.ug} = {pr.ug}
                                AND {matchvaluepr.currency} = {pr.currency}
                                AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date))
                                AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date))
                        }})
                }}) innerSelect
                WHERE rankValue = 1
            }}
            UNION ALL
            {{
                SELECT {p.pk} AS productpk, 0 AS singleMinPrice, 0 AS specialPrice
                FROM {Product AS p}
            }}) minPrice
            GROUP BY
                minPrice.productpk
        }}) singleMinPrice,
        
        ({{
            SELECT prices.productpk AS productpk,
                CASE WHEN (count(*) > 0) THEN 'or' + STRING_AGG(prices.Price) WITHIN GROUP (ORDER BY prices.minqtd ) ELSE '' END AS Price
            FROM
            ({{
                SELECT scalePrice.productpk AS productpk,
                    scalePrice.price AS price,
                    scalePrice.minqtd AS minqtd
                FROM
                ({{
                    SELECT {pr.product} AS productpk,
                        ({cur.isocode} + ';' + 'Gross' + ';' + {pr.minqtd} + '=' + (1 + ({tax.value}/100)) * {pr.price} / {pr.unitFactor} + 'or' + {cur.isocode} + ';' + 'Net' + ';' + {pr.minqtd} + '=' + {pr.price} / {pr.unitFactor} + 'or' ) AS price,
                        {pr.minqtd} AS minqtd,
                        RANK() OVER (PARTITION BY {p.pk} ORDER BY {pr.startTime} DESC) AS rankValue
                    FROM
                    {
                        Product AS p
                        JOIN DistPriceRow AS pr ON {p.pk} = {pr.product}
                        JOIN DistErpPriceConditionType AS pct ON {pr.erpPriceConditionType} = {pct.pk}
                        JOIN DistSalesOrgProduct AS dsop ON {p.pk} = {dsop.product}
                        JOIN DistSalesOrg AS dso ON {dsop.salesOrg} = {dso.pk} AND {dso.pk} = (?DistSalesOrg)
                        JOIN Currency AS cur ON {pr.currency} = {cur.pk}
                        JOIN CMSSite AS cms ON {pr.ug} = {cms.userPriceGroup} AND {cms.pk} = (?CMSSite)
                        JOIN TaxRow AS tr ON {cms.userTaxGroup} = {tr.ug}
                        JOIN Tax AS tax ON {tr.tax} = {tax.pk} AND {tr.pg} = {dsop.productTaxGroup}
                    }
                    WHERE ({pr.startTime} IS NULL OR {pr.startTime} <= (?Date))
                        AND ({pr.endTime} IS NULL OR {pr.endTime} >= (?Date))
                        AND {pr.matchValue} =  ({{
                            SELECT max({matchvaluepr.matchValue})
                            FROM { DistPriceRow AS matchvaluepr }
                            WHERE {matchvaluepr.product} = {pr.product}
                                AND {matchvaluepr.net} = {pr.net}
                                AND {matchvaluepr.ug} = {pr.ug}
                                AND {matchvaluepr.currency} = {pr.currency}
                                AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date))
                                AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date))
                        }})
                }}) scalePrice
                WHERE scalePrice.rankValue = 1
            }}
            UNION
            {{
                SELECT minPrice.productpk AS productpk,
                    minPrice.price AS price,
                    minPrice.minqtd AS minqtd
                FROM
                ({{
                    SELECT {pr.product} AS productpk,
                        ({cur.isocode} + ';' + 'Gross' + ';Min=' + (1 + ({tax.value}/100)) * {pr.price} / {pr.unitFactor} + 'or' + {cur.isocode} + ';' + 'Net' + ';Min=' + {pr.price} / {pr.unitFactor} + 'or' ) AS price,
                        {pr.minqtd} AS minqtd,
                        RANK() OVER (PARTITION BY {p.pk} ORDER BY {pr.startTime} DESC) AS rankValue
                    FROM
                    {
                        Product AS p
                        JOIN DistPriceRow AS pr ON {p.pk} = {pr.product}
                        JOIN DistErpPriceConditionType AS pct ON {pr.erpPriceConditionType} = {pct.pk}
                        JOIN DistSalesOrgProduct AS dsop ON {p.pk} = {dsop.product}
                        JOIN DistSalesOrg AS dso ON {dsop.salesOrg} = {dso.pk} AND {dso.pk} = (?DistSalesOrg)
                        JOIN Currency AS cur ON {pr.currency} = {cur.pk}
                        JOIN CMSSite AS cms ON {pr.ug} = {cms.userPriceGroup} AND {cms.pk} = (?CMSSite)
                        JOIN TaxRow AS tr ON {cms.userTaxGroup} = {tr.ug} AND {tr.pg} = {dsop.productTaxGroup}
                        JOIN Tax AS tax ON {tr.tax} = {tax.pk}
                    }
                    WHERE ({pr.startTime} IS NULL OR {pr.startTime} <= (?Date))
                        AND ({pr.endTime} IS NULL OR {pr.endTime} >= (?Date))
                        AND  {pr.minqtd} = ({{
                            SELECT min({minpr.minqtd})
                            FROM { DistPriceRow AS minpr }
                            WHERE {minpr.product} = {pr.product}
                                AND {minpr.net} = {pr.net}
                                AND {minpr.ug} =  {pr.ug}
                                AND {minpr.currency} = {pr.currency}
                                AND ({minpr.startTime} IS NULL OR {minpr.startTime} <= (?Date))
                                AND ({minpr.endTime} IS NULL OR {minpr.endTime} >= (?Date))
                                AND {minpr.matchValue} =  ({{
                                    SELECT max({matchvaluepr.matchValue})
                                    FROM { DistPriceRow AS matchvaluepr }
                                    WHERE {matchvaluepr.product} = {minpr.product}
                                        AND {matchvaluepr.net} = {minpr.net}
                                        AND {matchvaluepr.ug} = {minpr.ug}
                                        AND {matchvaluepr.currency} = {minpr.currency}
                                        AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date))
                                        AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date))
                                }})
                        }})
                        AND {pr.matchValue} =  ({{
                            SELECT max({matchvaluepr.matchValue})
                            FROM { DistPriceRow AS matchvaluepr }
                            WHERE {matchvaluepr.product} = {pr.product}
                                AND {matchvaluepr.net} = {pr.net}
                                AND {matchvaluepr.ug} = {pr.ug}
                                AND {matchvaluepr.currency} = {pr.currency}
                                AND ({matchvaluepr.startTime} IS NULL OR {matchvaluepr.startTime} <= (?Date))
                                AND ({matchvaluepr.endTime} IS NULL OR {matchvaluepr.endTime} >= (?Date))
                        }})
                }}) minPrice
                WHERE minPrice.rankValue = 1
            }}) prices
            GROUP BY prices.productpk
        }}) price,
            
        ({{
            SELECT s.productpk, (CASE WHEN sum(s.inStock) >= 128 THEN 2 WHEN sum(s.inStock) >= 96 THEN 1 WHEN sum(s.inStock) >= 64 THEN 0 WHEN sum(s.inStock) >= 40 THEN 2 WHEN sum(s.inStock) >= 32 THEN 1 WHEN sum(s.inStock) >= 24 THEN 2 WHEN sum(s.inStock) >= 16 THEN 0 WHEN sum(s.inStock) >= 8 THEN 2 WHEN sum(s.inStock) >= 6 THEN 1 WHEN sum(s.inStock) >= 5 THEN 0 WHEN sum(s.inStock) >= 2 THEN 1 ELSE 0 END) AS InStock
                FROM ({{
                    SELECT {p.pk} AS productpk, (CASE WHEN sum({sl.available}) > 0 THEN 128 ELSE 64 END) AS InStock
                        FROM {Product AS p JOIN StockLevel AS sl ON {p.code} = {sl.productCode}}
                    WHERE
                        (
                            {sl.warehouse} IN
                            ({{
                                SELECT {wh.pk}
                                    FROM {Warehouse AS wh
                                    JOIN CMSSite2WarehouseExclusiveFast AS rel ON {rel.target} = {wh.PK}
                                    JOIN CMSSite AS cms ON {cms.pk} = {rel.source} AND {cms.pk} = (?CMSSite)}
                            }})
                        ) GROUP BY {p.pk}
                }}
                UNION ALL {{
                    SELECT {p.pk} AS productpk, (CASE WHEN sum({sl.available}) > 0 THEN 32 ELSE 16 END) AS InStock
                        FROM {Product AS p JOIN StockLevel AS sl ON {p.code} = {sl.productCode}}
                    WHERE
                        (
                            {sl.warehouse} IN
                            ({{
                                SELECT {wh.pk}
                                    FROM {Warehouse AS wh
                                    JOIN CMSSite2WarehouseExclusiveSlow AS rel ON {rel.target} = {wh.PK}
                                    JOIN CMSSite AS cms ON {cms.pk} = {rel.source} AND {cms.pk} = (?CMSSite)}
                            }})
                        ) GROUP BY {p.pk}
                }}
                UNION ALL {{
                    SELECT {p.pk} AS productpk, (CASE WHEN sum({sl.available}) > 0 THEN 8 ELSE 4 END) AS InStock
                        FROM {Product AS p JOIN StockLevel AS sl ON {p.code} = {sl.productCode}}
                    WHERE
                        (
                            {sl.warehouse} IN
                            ({{
                                SELECT {wh.pk}
                                    FROM {Warehouse AS wh
                                    JOIN CMSSite2WarehouseFast AS relFast ON {relFast.target} = {wh.PK}
                                    JOIN CMSSite AS cms ON {cms.pk} = {relFast.source} AND {cms.pk} = (?CMSSite)}
                            }}) OR
                            {sl.warehouse} IN
                            ({{
                                SELECT {wh.pk}
                                    FROM {Warehouse AS wh
                                    JOIN CMSSite2WarehouseExternalFast AS relExtFast ON {relExtFast.target} = {wh.PK}
                                    JOIN CMSSite AS cms ON {cms.pk} = {relExtFast.source} AND {cms.pk} = (?CMSSite)}
                            }})
                        ) GROUP BY {p.pk}
                }}
                UNION ALL {{
                    SELECT {p.pk} AS productpk, (CASE WHEN sum({sl.available}) > 0 THEN 2 ELSE 1 END) AS InStock
                        FROM {Product AS p JOIN StockLevel AS sl ON {p.code} = {sl.productCode}}
                    WHERE
                        (
                            {sl.warehouse} IN
                            ({{
                                SELECT {wh.pk}
                                    FROM {Warehouse AS wh
                                    JOIN CMSSite2WarehouseSlow AS relSlow ON {relSlow.target} = {wh.PK}
                                    JOIN CMSSite AS cms ON {cms.pk} = {relSlow.source} AND {cms.pk} = (?CMSSite)}
                            }}) OR
                            {sl.warehouse} IN
                            ({{
                                SELECT {wh.pk}
                                    FROM {Warehouse AS wh
                                    JOIN CMSSite2WarehouseExternalSlow AS relExtSlow ON {relExtSlow.target} = {wh.PK}
                                    JOIN CMSSite AS cms ON {cms.pk} = {relExtSlow.source} AND {cms.pk} = (?CMSSite)}
                            }})
                        ) GROUP BY {p.pk}
                }}
                UNION ALL
                {{
                    SELECT {p.pk} AS productpk, 0 AS InStock
                        FROM {product AS p}
                }}) s
            GROUP BY s.productpk
        }}) stock

    WHERE {p.pk} = cats.productpk
        AND {p.pk} = singleMinPrice.productpk
        AND {p.pk} = price.productpk
        AND {p.pk} = stock.productpk
        AND EXISTS ({{
            SELECT {dss.code}
            FROM {DistSalesStatus AS dss}
            WHERE {dss.pk} = {dsop.salesStatus}
                AND {dss.visibleInShop} = 1
        }})
        AND {p.pimId} IS NOT NULL
        AND (({p.code} IS NOT NULL AND (?ErpSystem) = 'SAP') OR ({p.codeElfa} IS NOT NULL AND (?ErpSystem) = 'ELFA') OR ({p.codeMovex} IS NOT NULL AND (?ErpSystem) = 'MOVEX'))
        AND {aas.code} = 'approved'
        AND {p.pk} NOT IN (?ExcludedProducts)
        AND ?NumberOfDeliveryCountries <> ({{
            SELECT COUNT(DISTINCT {pof.country})
            FROM {DistCOPunchOutFilter AS pof}
            WHERE ({pof.product} = {p.pk} OR {pof.productHierarchy} = {p.productHierarchy})
                AND {pof.validFromDate} <= ?Date
                AND {pof.validUntilDate} >= ?Date
                AND {pof.salesOrg} = ?DistSalesOrg
                AND {pof.country} IN (?DeliveryCountries)
        }})
        AND {p.catalogVersion} = ({{
            SELECT {cv.pk}
            FROM {CatalogVersion AS cv
                JOIN Catalog AS c ON {cv.catalog} = {c.pk}
            }
            WHERE {c.id} = 'distrelecProductCatalog'
                AND {cv.version} = 'Online'
        }})
    */
    
    /*
    ORDER BY {p.code}
    */
    
    @Override
    public String createQuery() {
        final StringBuilder query = new StringBuilder(QUERY_LENGTH);

        query.append("SELECT ");
        query.append(" {p.").append(ProductModel.CODE).append("} ").append(" AS \"").append(PRODUCT_NUMBER.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.EAN).append("} ").append(" AS \"").append(EAN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.CODEELFA).append("} ").append(" AS \"").append(PRODUCT_NUMBER_ELFA.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.CODEMOVEX).append("} ").append(" AS \"").append(PRODUCT_NUMBER_MOVEX.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.NAME).append(":o} ").append(" AS \"").append(TITLE.getValue()).append(ENCODE_FF_SUFFIX).append(CONCAT_SELECT_VALUE);
        query.append(toNvarchar("{p.".concat(ProductModel.DESCRIPTION).concat(":o}"))).append(" AS \"").append(DESCRIPTION.getValue()).append(CONCAT_SELECT_VALUE);
        appendProductURLSelect(query).append(" AS \"").append(PRODUCT_URL.getValue()).append(CONCAT_SELECT_VALUE);
        appendProductImageURLSelect(query).append(" AS \"").append(IMAGE_URL.getValue()).append(CONCAT_SELECT_VALUE);
        appendAdditionalProductImageURLSelect(query).append(" AS \"").append(ADDITIONAL_IMAGE_URLS.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {m.").append(DistManufacturerModel.NAME).append(":o} ").append(" AS \"").append(MANUFACTURER.getValue()).append(CONCAT_SELECT_VALUE);
        appendManufacturerURLSelect(query).append(" AS \"").append(MANUFACTURER_URL.getValue()).append(CONCAT_SELECT_VALUE);
        appendManufacturerImageURLSelect(query).append(" AS \"").append(MANUFACTURER_IMAGE_URL.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" price.price AS \"").append(PRICE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.PIMWEBUSE).append(":o} ").append(" AS \"").append(WEB_USE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" cats.cat1name").append(ENCODE_FF_SUFFIX).append(" AS \"").append(CATEGORY1.getValue()).append(ENCODE_FF_SUFFIX).append(CONCAT_SELECT_VALUE);
        query.append(" cats.cat2name").append(ENCODE_FF_SUFFIX).append(" AS \"").append(CATEGORY2.getValue()).append(ENCODE_FF_SUFFIX).append(CONCAT_SELECT_VALUE);
        query.append(" cats.cat3name").append(ENCODE_FF_SUFFIX).append(" AS \"").append(CATEGORY3.getValue()).append(ENCODE_FF_SUFFIX).append(CONCAT_SELECT_VALUE);
        query.append(" cats.cat4name").append(ENCODE_FF_SUFFIX).append(" AS \"").append(CATEGORY4.getValue()).append(ENCODE_FF_SUFFIX).append(CONCAT_SELECT_VALUE);
        query.append(" cats.cat5name").append(ENCODE_FF_SUFFIX).append(" AS \"").append(CATEGORY5.getValue()).append(ENCODE_FF_SUFFIX).append(CONCAT_SELECT_VALUE);
        appendCategoryPathSelectName(query).append(" AS \"").append(CATEGORYPATH.getValue()).append(CONCAT_SELECT_VALUE);
        appendCategoryExtensionsSelect(query).append(" AS \"").append(CATEGORY_EXTENSIONS.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.TYPENAME).append("} ").append("AS \"").append(TYPENAME.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(replaceRegexp("{p.".concat(ProductModel.TYPENAME).concat("}"), "[^a-zA-Z0-9]", "")).append(" AS \"").append(TYPE_NAME_NORMALIZED.getValue()).append(CONCAT_SELECT_VALUE);
        //query.append("(CASE WHEN {dss.").append(DistSalesStatusModel.PK).append("} IS NULL OR {dss.").append(DistSalesStatusModel.BUYABLEINSHOP).append("} = 0 THEN 0 ELSE stock.").append(INSTOCK.getValue()).append(" END) AS \"").append(INSTOCK.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("stock.InStock AS \"").append(INSTOCK.getValue()).append(CONCAT_SELECT_VALUE);
        
        query.append(" {dsop.").append(DistSalesOrgProductModel.ORDERQUANTITYMINIMUM).append("}").append(" AS \"").append(ITEMS_MIN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {dsop.").append(DistSalesOrgProductModel.ORDERQUANTITYSTEP).append("}").append(" AS \"").append(ITEMS_STEP.getValue()).append(CONCAT_SELECT_VALUE);
        appendSalesUnitSelect(query).append(" AS \"").append(SALESUNIT.getValue()).append(CONCAT_SELECT_VALUE);

        appendPromotionLabelsSelect(query).append(" AS \"").append(PROMOTIONLABELS.getValue()).append(CONCAT_SELECT_VALUE);
        appendReplacementSelect(query).append(" AS \"").append(REPLACEMENT.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" CASE WHEN {dss.").append(DistSalesStatusModel.PK).append("} IS NOT NULL AND {dss.").append(DistSalesStatusModel.BUYABLEINSHOP).append("} = 1 THEN 1 ELSE 0 END AS \"").append(BUYABLE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.NAMESHORT).append(":o} AS \"").append(TITLE_SHORT.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.SEOKEYWORDMAIN).append(":o} AS \"").append(SYNONYM_FULL_MATCH.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.PRODUCTLINENAME).append(":o} AS \"").append(PRODUCT_LINE_NAME.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.SEOACCESSORY).append(":o} AS \"").append(ACCESSORY.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.PRODUCTFAMILYNAME).append(":o} AS \"").append(PRODUCT_FAMILY_NAME.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" singleMinPrice.").append("singleMinPrice").append(" AS \"").append(SINGLE_MIN_PRICE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.PIMFAMILYCATEGORYCODE).append("} AS \"").append(PRODUCT_FAMILY_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.PRODUCTHIERARCHY).append("} AS \"").append(PRODUCT_HIERARCHY.getValue()).append(CONCAT_SELECT_VALUE);

        query.append("stock.totalInStock AS  \"").append(TOTALINSTOCK.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("stock.availableInPickup AS \"").append(AVAILABLEINPICKUP.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("stock.pickupStock AS \"").append(PICKUPSTOCK.getValue()).append(CONCAT_SELECT_VALUE);
        
        query.append("cats.cat1code AS \"").append(CATEGORY1_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("cats.cat2code AS \"").append(CATEGORY2_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("cats.cat3code AS \"").append(CATEGORY3_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("cats.cat4code AS \"").append(CATEGORY4_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        query.append("cats.cat5code AS \"").append(CATEGORY5_CODE.getValue()).append(CONCAT_SELECT_VALUE);
        appendCategoryPathSelectCode(query).append(" AS \"").append(CATEGORY_CODE_PATH.getValue()).append(CONCAT_SELECT_VALUE);
        
        appendSingleUnitPrice(query).append(" AS \"").append(SINGLE_UNIT_PRICE.getValue()).append(CONCAT_SELECT_VALUE);
        
        // DISTRELEC-10398 energy efficiency data
        query.append(" {p.").append(ProductModel.ENERGYEFFIENCYLABELS).append(":o} AS \"").append(ENERGY_EFFICIENCY.getValue()).append(CONCAT_SELECT_VALUE);

        query.append(" {p.").append(ProductModel.PIMWEBUSEDFEATURES).append(":o} ").append(" AS \"").append(PIM_WEB_USE_D.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.ALTERNATIVESMPN).append(":o} ").append(" AS \"").append(ALTERNATIVES_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.NORMALIZEDALTERNATIVESMPN).append(":o} ").append(" AS \"").append(NORMALIZED_ALTERNATIVES_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.CURATEDPRODUCTSELECTION).append("} AS \"").append(DistFactFinderExportColumns.CURATED_PRODUCT_SELECTION.getValue()).append(CONCAT_SELECT_VALUE); 
        query.append(" {p.").append(ProductModel.SAPPLANTPROFILES).append("} AS \"").append(DistFactFinderExportColumns.SAP_PLANT_PROFILES.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.SAPMPN).append("} AS \"").append(DistFactFinderExportColumns.SAP_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.ALTERNATIVEALIASMPN).append(":o} ").append(" AS \"").append(ALTERNATE_ALIAS_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {p.").append(ProductModel.NORMALIZEDALTERNATIVEALIASMPN).append(":o} ").append(" AS \"").append(NORMALIZED_ALTERNATE_ALIAS_MPN.getValue()).append(CONCAT_SELECT_VALUE);
        query.append(" {dsop.").append(DistSalesOrgProductModel.ITEMCATEGORYGROUP).append("}").append(" AS \"").append(ITEM_CATEGORY_GROUP.getValue()).append("\" ").append(CONCAT_SELECT_VALUE);
        // Energy Efficiency Label Image URL
        appendEnergyEfficiencyLabelImageURLSelect(query).append(" AS \"").append(ENERGY_EFFICIENCY_LABEL_IMAGE_URL.getValue()).append("\" ");


        query.append("FROM ");
        query.append(" { ");
        query.append(ProductModel._TYPECODE).append(" AS p");
        query.append("  JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsop ");
        query.append("         ON {dsop.").append(DistSalesOrgProductModel.PRODUCT).append("} = {p.").append(ProductModel.PK).append("} ");
        query.append("        AND {dsop.").append(DistSalesOrgProductModel.SALESORG).append("} = (?").append(DistSalesOrgModel._TYPECODE).append(") ");
        query.append("  LEFT JOIN ").append(DistSalesStatusModel._TYPECODE).append(" AS dss ");
        query.append("         ON {dss.").append(DistSalesStatusModel.PK).append("} = {dsop.").append(DistSalesOrgProductModel.SALESSTATUS).append("} ");
        query.append("  LEFT JOIN ").append(DistSalesUnitModel._TYPECODE).append(" AS dsu ");
        query.append("         ON {dsu.").append(DistSalesUnitModel.PK).append("} = {p.").append(ProductModel.SALESUNIT).append("} ");
        query.append("  LEFT JOIN ").append(UnitModel._TYPECODE).append(" AS u ");
        query.append("         ON {u.").append(UnitModel.PK).append("} = {p.").append(ProductModel.UNIT).append("} ");
        query.append("  LEFT JOIN ").append(DistManufacturerModel._TYPECODE).append(" AS m ");
        query.append("         ON {m.").append(DistManufacturerModel.PK).append("} = {p.").append(ProductModel.MANUFACTURER).append("} ");
        query.append("  LEFT JOIN ").append(ProductCountryModel._TYPECODE).append(" AS pc ");
        query.append("         ON {pc.").append(ProductCountryModel.PRODUCT).append("} = {p.").append(ProductModel.PK).append("} ");
        query.append("         AND {pc.").append(ProductCountryModel.COUNTRY).append("} = (?").append(CountryModel._TYPECODE).append(") ");
        query.append("  JOIN ").append(ArticleApprovalStatus._TYPECODE).append(" AS aas ");
        query.append("         ON {p.").append(ProductModel.APPROVALSTATUS).append("} = {aas.pk} ");
        query.append(" }, ");
        query.append(" ({{");
        appendCategoriesSubSelect(query);
        query.append(" }}) cats, ");
        query.append("({{ ");
        appendSingleMinPriceSubselect(query);
        query.append("}}) singleMinPrice, ");
        query.append("({{ ");
        appendPriceSubselect(query);
        query.append("}}) price, ");
        query.append("({{ ");
        appendInStockSubSelect(query);
        query.append("}}) stock ");

        StringBuilder whereBuilder = new StringBuilder();
        whereBuilder.append("{p.").append(ProductModel.PK).append("} = cats.").append(CATEGORIES_JOIN_PK).append(" ");
        whereBuilder.append("  AND ").append("{p.").append(ProductModel.PK).append("} = singleMinPrice.").append(PRICE_JOIN_PK).append(" ");
        whereBuilder.append("  AND ").append("{p.").append(ProductModel.PK).append("} = price.").append(PRICE_JOIN_PK).append(" ");
        whereBuilder.append("  AND ").append("{p.").append(ProductModel.PK).append("} = stock.").append(INSTOCK_JOIN_PK).append(" ");
        appendVisibilityCondition(whereBuilder);
        whereBuilder.append("  AND ").append("{p.").append(ProductModel.PIMID).append("} IS NOT NULL");
        whereBuilder.append("  AND ({p.").append(ProductModel.CODE).append("} IS NOT NULL AND (?ErpSystem) = '").append(DistErpSystem.SAP.getCode()).append("') ");
        whereBuilder.append("  AND {aas.code} = '").append(ArticleApprovalStatus.APPROVED.getCode()).append("' ");
        whereBuilder.append("  AND {p.").append(ProductModel.PK).append("} NOT IN (?").append(DistFactFinderProductExportParameterProvider.EXCLUDED_PRODUCTS).append(") ");
        appendPunchOutFilterCondition(whereBuilder);
        appendCatalogVersionCondition(whereBuilder);
        // whereBuilder.append("ORDER BY {p.").append(ProductModel.CODE).append("} ");
        query.append(where(whereBuilder.toString()));

        final String flexiSearchQuery = query.toString();
        LOG.debug("Executing Query [{}].", flexiSearchQuery);
        return flexiSearchQuery;
    }
    
    protected StringBuilder appendSalesRankValue(final StringBuilder query) {
        query.append(" ({{ ");
        //query.append("select CHAR(123) + STRING_AGG('\"' + {sre.value} + '\"', ',')  WITHIN GROUP (ORDER BY {sre.value}) + CHAR(125) ");
        //query.append("from {SalesRankEntry as sre} where {sre.product} = {p.pk} and {sre.country} = (?Country) ");
        query.append("select {sre.value} from {SalesRankEntry as sre} where {sre.product} = {p.pk} and {sre.country} = (?Country) ");
        query.append(" }}) ");
        return query;
    }

    protected StringBuilder appendCategoryPathSelectCode(final StringBuilder query) {
        query.append(" CASE WHEN cats.cat1code IS NOT NULL").append(getProductLineCategoryString("cat1")).append(" THEN cats.cat1code ELSE '' END + ");
        query.append(" CASE WHEN cats.cat2code IS NOT NULL").append(getProductLineCategoryString("cat2")).append(" THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat2code ELSE '' END + ");
        query.append(" CASE WHEN cats.cat3code IS NOT NULL").append(getProductLineCategoryString("cat3")).append(" THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat3code ELSE '' END + ");
        query.append(" CASE WHEN cats.cat4code IS NOT NULL").append(getProductLineCategoryString("cat4")).append(" THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat4code ELSE '' END + ");
        query.append(" CASE WHEN cats.cat5code IS NOT NULL").append(getProductLineCategoryString("cat5")).append(" THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat5code ELSE '' END + ");
        query.append(" CASE WHEN cats.cat6code IS NOT NULL").append(getProductLineCategoryString("cat6")).append(" THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat6code ELSE '' END + ");
        query.append(" CASE WHEN cats.cat7code IS NOT NULL").append(getProductLineCategoryString("cat7")).append(" THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat7code ELSE '' END");
        return query;
    }
    
    //  DISTRELEC-11300 :- exclude category depth level more 5 
    private StringBuilder getProductLineCategoryString(final String prefix){
        final StringBuilder query = new StringBuilder("");
        final String pimCategoryTypes = "'Familie','Serie','SerieSpezial'";
        query.append(" AND cats.").append(prefix).append("PimTypecode NOT IN (");
        query.append(pimCategoryTypes);
        query.append(")");
        return query;
    }
    
    protected StringBuilder appendCategoryPathSelectName(final StringBuilder query) {
        query.append(" CASE WHEN cats.cat1nameSeo IS NOT NULL THEN cats.cat1nameSeo ELSE '' END + ");
        query.append(" CASE WHEN cats.cat2nameSeo IS NOT NULL THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat2nameSeo ELSE '' END + ");
        query.append(" CASE WHEN cats.cat3nameSeo IS NOT NULL THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat3nameSeo ELSE '' END + ");
        query.append(" CASE WHEN cats.cat4nameSeo IS NOT NULL THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat4nameSeo ELSE '' END + ");
        query.append(" CASE WHEN cats.cat5nameSeo IS NOT NULL THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat5nameSeo ELSE '' END + ");
        query.append(" CASE WHEN cats.cat6nameSeo IS NOT NULL THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat6nameSeo ELSE '' END + ");
        query.append(" CASE WHEN cats.cat7nameSeo IS NOT NULL THEN '").append(CATEGORY_SEPARATOR).append("' + cats.cat7nameSeo ELSE '' END");
        return query;
    }

    protected StringBuilder appendCategoryExtensionsSelect(final StringBuilder query) {
        query.append("( TO_CLOB('[') ");  // to_clob is required to concatenate this huge string (see DISTRELEC-6551)
        query.append("  + CASE WHEN cats.cat1nameSeo IS NOT NULL THEN ").append(chr("123")).append(" + ' \"url\" : \"/' + ?").append(DistFactFinderProductExportParameterProvider.LANGUAGE_ISOCODE).append(" ");
        query.append("    + '/' + cats.cat1nameSeo ");
        query.append("    + '/c/' + cats.cat1code + '\"' + CASE WHEN cats.cat1imageUrl IS NOT NULL THEN ', \"imageUrl\" : \"' + cats.cat1imageUrl + '\"' ELSE '' END + ").append(distSqlUtils.chr("125")).append(" + ', ' ELSE '' END ");
        query.append("  + CASE WHEN cats.cat2nameSeo IS NOT NULL THEN ").append(chr("123")).append(" + ' \"url\" : \"/' + ?").append(DistFactFinderProductExportParameterProvider.LANGUAGE_ISOCODE).append(" ");
        query.append("    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo ");
        query.append("    + '/c/' + cats.cat2code + '\"' + CASE WHEN cats.cat2imageUrl IS NOT NULL THEN ', \"imageUrl\" : \"' + cats.cat2imageUrl + '\"' ELSE '' END + ").append(distSqlUtils.chr("125")).append(" + ', ' ELSE '' END ");
        query.append("  + CASE WHEN cats.cat3nameSeo IS NOT NULL THEN ").append(chr("123")).append(" + ' \"url\" : \"/' + ?").append(DistFactFinderProductExportParameterProvider.LANGUAGE_ISOCODE).append(" ");
        query.append("    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo ");
        query.append("    + '/c/' + cats.cat3code + '\"' + CASE WHEN cats.cat3imageUrl IS NOT NULL THEN ', \"imageUrl\" : \"' + cats.cat3imageUrl + '\"' ELSE '' END + ").append(distSqlUtils.chr("125")).append(" + ', ' ELSE '' END ");
        query.append("  + CASE WHEN cats.cat4nameSeo IS NOT NULL THEN ").append(chr("123")).append(" + ' \"url\" : \"/' + ?").append(DistFactFinderProductExportParameterProvider.LANGUAGE_ISOCODE).append(" ");
        query.append("    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo + '/' + cats.cat4nameSeo ");
        query.append("    + '/c/' + cats.cat4code + '\"' + CASE WHEN cats.cat4imageUrl IS NOT NULL THEN ', \"imageUrl\" : \"' + cats.cat4imageUrl + '\"' ELSE '' END + ").append(distSqlUtils.chr("125")).append(" + ', ' ELSE '' END ");
        query.append("  + CASE WHEN cats.cat5nameSeo IS NOT NULL THEN ").append(chr("123")).append(" + ' \"url\" : \"/' + ?").append(DistFactFinderProductExportParameterProvider.LANGUAGE_ISOCODE).append(" ");
        query.append("    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo + '/' + cats.cat4nameSeo + '/' + cats.cat5nameSeo ");
        query.append("    + '/c/' + cats.cat5code + '\"' + CASE WHEN cats.cat5imageUrl IS NOT NULL THEN ', \"imageUrl\" : \"' + cats.cat5imageUrl + '\"' ELSE '' END + ").append(distSqlUtils.chr("125")).append(" + ', ' ELSE '' END ");
        query.append("  + CASE WHEN cats.cat6nameSeo IS NOT NULL THEN ").append(chr("123")).append(" + ' \"url\" : \"/' + ?").append(DistFactFinderProductExportParameterProvider.LANGUAGE_ISOCODE).append(" ");
        query.append("    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo + '/' + cats.cat4nameSeo + '/' + cats.cat5nameSeo + '/' + cats.cat6nameSeo ");
        query.append("    + '/c/' + cats.cat6code + '\"' + CASE WHEN cats.cat6imageUrl IS NOT NULL THEN ', \"imageUrl\" : \"' + cats.cat6imageUrl + '\"' ELSE '' END + ").append(distSqlUtils.chr("125")).append(" + ', ' ELSE '' END ");
        query.append("  + CASE WHEN cats.cat7nameSeo IS NOT NULL THEN ").append(chr("123")).append(" + ' \"url\" : \"/' + ?").append(DistFactFinderProductExportParameterProvider.LANGUAGE_ISOCODE).append(" ");
        query.append("    + '/' + cats.cat1nameSeo + '/' + cats.cat2nameSeo + '/' + cats.cat3nameSeo + '/' + cats.cat4nameSeo + '/' + cats.cat5nameSeo + '/' + cats.cat6nameSeo + '/' + cats.cat7nameSeo ");
        query.append("    + '/c/' + cats.cat7code + '\"' + CASE WHEN cats.cat7imageUrl IS NOT NULL THEN ', \"imageUrl\" : \"' + cats.cat7imageUrl + '\"' ELSE '' END + ").append(chr("125")).append(" + ', ' ELSE '' END ");
        query.append("  + ']' ) ");
        return query;
    }

    protected StringBuilder appendCategoriesSubSelect(final StringBuilder query) {
        // select the correct category for the respective category level
        query.append("SELECT");
        query.append("  {p.").append(ProductModel.PK).append("} ").append(CATEGORIES_JOIN_PK).append(", ");

        // Category names
        query.append("  (case when {c6.").append(CategoryModel.LEVEL).append("} = 1 then {c6.").append(CategoryModel.NAME).append(":o} when {c5.").append(CategoryModel.LEVEL).append("} = 1 then {c5.").append(CategoryModel.NAME).append(":o} when {c4.").append(CategoryModel.LEVEL).append("} = 1 then {c4.").append(CategoryModel.NAME).append(":o} when {c3.").append(CategoryModel.LEVEL).append("} = 1 then {c3.").append(CategoryModel.NAME).append(":o} when {c2.").append(CategoryModel.LEVEL).append("} = 1 then {c2.").append(CategoryModel.NAME).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 1 then {c1.").append(CategoryModel.NAME).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 1 then {c0.").append(CategoryModel.NAME).append(":o} else '' end) cat1name").append(ENCODE_FF_SUFFIX).append(", ");
        query.append("  (case when {c5.").append(CategoryModel.LEVEL).append("} = 2 then {c5.").append(CategoryModel.NAME).append(":o} when {c4.").append(CategoryModel.LEVEL).append("} = 2 then {c4.").append(CategoryModel.NAME).append(":o} when {c3.").append(CategoryModel.LEVEL).append("} = 2 then {c3.").append(CategoryModel.NAME).append(":o} when {c2.").append(CategoryModel.LEVEL).append("} = 2 then {c2.").append(CategoryModel.NAME).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 2 then {c1.").append(CategoryModel.NAME).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 2 then {c0.").append(CategoryModel.NAME).append(":o} else '' end) cat2name").append(ENCODE_FF_SUFFIX).append(", ");
        query.append("  (case when {c4.").append(CategoryModel.LEVEL).append("} = 3 then {c4.").append(CategoryModel.NAME).append(":o} when {c3.").append(CategoryModel.LEVEL).append("} = 3 then {c3.").append(CategoryModel.NAME).append(":o} when {c2.").append(CategoryModel.LEVEL).append("} = 3 then {c2.").append(CategoryModel.NAME).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 3 then {c1.").append(CategoryModel.NAME).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 3 then {c0.").append(CategoryModel.NAME).append(":o} else '' end) cat3name").append(ENCODE_FF_SUFFIX).append(", ");
        query.append("  (case when {c3.").append(CategoryModel.LEVEL).append("} = 4 then {c3.").append(CategoryModel.NAME).append(":o} when {c2.").append(CategoryModel.LEVEL).append("} = 4 then {c2.").append(CategoryModel.NAME).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 4 then {c1.").append(CategoryModel.NAME).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 4 then {c0.").append(CategoryModel.NAME).append(":o} else '' end) cat4name").append(ENCODE_FF_SUFFIX).append(", ");
        query.append("  (case when {c2.").append(CategoryModel.LEVEL).append("} = 5 then {c2.").append(CategoryModel.NAME).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 5 then {c1.").append(CategoryModel.NAME).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 5 then {c0.").append(CategoryModel.NAME).append(":o} else '' end) cat5name").append(ENCODE_FF_SUFFIX).append(", ");
        query.append("  (case when {c1.").append(CategoryModel.LEVEL).append("} = 6 then {c1.").append(CategoryModel.NAME).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 6 then {c0.").append(CategoryModel.NAME).append(":o} else '' end) cat6name").append(ENCODE_FF_SUFFIX).append(", ");
        query.append("  (case when {c0.").append(CategoryModel.LEVEL).append("} = 7 then {c0.").append(CategoryModel.NAME).append(":o} else '' end) cat7name").append(ENCODE_FF_SUFFIX).append(", ");
        
        // Category seo names
        query.append("  (case when {c6.").append(CategoryModel.LEVEL).append("} = 1 then {c6.").append(CategoryModel.NAMESEO).append(":o} when {c5.").append(CategoryModel.LEVEL).append("} = 1 then {c5.").append(CategoryModel.NAMESEO).append(":o} when {c4.").append(CategoryModel.LEVEL).append("} = 1 then {c4.").append(CategoryModel.NAMESEO).append(":o} when {c3.").append(CategoryModel.LEVEL).append("} = 1 then {c3.").append(CategoryModel.NAMESEO).append(":o} when {c2.").append(CategoryModel.LEVEL).append("} = 1 then {c2.").append(CategoryModel.NAMESEO).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 1 then {c1.").append(CategoryModel.NAMESEO).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 1 then {c0.").append(CategoryModel.NAMESEO).append(":o} else '' end) cat1nameSeo, ");
        query.append("  (case when {c5.").append(CategoryModel.LEVEL).append("} = 2 then {c5.").append(CategoryModel.NAMESEO).append(":o} when {c4.").append(CategoryModel.LEVEL).append("} = 2 then {c4.").append(CategoryModel.NAMESEO).append(":o} when {c3.").append(CategoryModel.LEVEL).append("} = 2 then {c3.").append(CategoryModel.NAMESEO).append(":o} when {c2.").append(CategoryModel.LEVEL).append("} = 2 then {c2.").append(CategoryModel.NAMESEO).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 2 then {c1.").append(CategoryModel.NAMESEO).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 2 then {c0.").append(CategoryModel.NAMESEO).append(":o} else '' end) cat2nameSeo, ");
        query.append("  (case when {c4.").append(CategoryModel.LEVEL).append("} = 3 then {c4.").append(CategoryModel.NAMESEO).append(":o} when {c3.").append(CategoryModel.LEVEL).append("} = 3 then {c3.").append(CategoryModel.NAMESEO).append(":o} when {c2.").append(CategoryModel.LEVEL).append("} = 3 then {c2.").append(CategoryModel.NAMESEO).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 3 then {c1.").append(CategoryModel.NAMESEO).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 3 then {c0.").append(CategoryModel.NAMESEO).append(":o} else '' end) cat3nameSeo, ");
        query.append("  (case when {c3.").append(CategoryModel.LEVEL).append("} = 4 then {c3.").append(CategoryModel.NAMESEO).append(":o} when {c2.").append(CategoryModel.LEVEL).append("} = 4 then {c2.").append(CategoryModel.NAMESEO).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 4 then {c1.").append(CategoryModel.NAMESEO).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 4 then {c0.").append(CategoryModel.NAMESEO).append(":o} else '' end) cat4nameSeo, ");
        query.append("  (case when {c2.").append(CategoryModel.LEVEL).append("} = 5 then {c2.").append(CategoryModel.NAMESEO).append(":o} when {c1.").append(CategoryModel.LEVEL).append("} = 5 then {c1.").append(CategoryModel.NAMESEO).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 5 then {c0.").append(CategoryModel.NAMESEO).append(":o} else '' end) cat5nameSeo, ");
        query.append("  (case when {c1.").append(CategoryModel.LEVEL).append("} = 6 then {c1.").append(CategoryModel.NAMESEO).append(":o} when {c0.").append(CategoryModel.LEVEL).append("} = 6 then {c0.").append(CategoryModel.NAMESEO).append(":o} else '' end) cat6nameSeo, ");
        query.append("  (case when {c0.").append(CategoryModel.LEVEL).append("} = 7 then {c0.").append(CategoryModel.NAMESEO).append(":o} else '' end) cat7nameSeo, ");

        // Category codes
        query.append("  (case when {c6.").append(CategoryModel.LEVEL).append("} = 1 then {c6.").append(CategoryModel.CODE).append("} when {c5.").append(CategoryModel.LEVEL).append("} = 1 then {c5.").append(CategoryModel.CODE).append("} when {c4.").append(CategoryModel.LEVEL).append("} = 1 then {c4.").append(CategoryModel.CODE).append("} when {c3.").append(CategoryModel.LEVEL).append("} = 1 then {c3.").append(CategoryModel.CODE).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 1 then {c2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 1 then {c1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 1 then {c0.").append(CategoryModel.CODE).append("} else '' end) cat1code, ");
        query.append("  (case when {c5.").append(CategoryModel.LEVEL).append("} = 2 then {c5.").append(CategoryModel.CODE).append("} when {c4.").append(CategoryModel.LEVEL).append("} = 2 then {c4.").append(CategoryModel.CODE).append("} when {c3.").append(CategoryModel.LEVEL).append("} = 2 then {c3.").append(CategoryModel.CODE).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 2 then {c2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 2 then {c1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 2 then {c0.").append(CategoryModel.CODE).append("} else '' end) cat2code, ");
        query.append("  (case when {c4.").append(CategoryModel.LEVEL).append("} = 3 then {c4.").append(CategoryModel.CODE).append("} when {c3.").append(CategoryModel.LEVEL).append("} = 3 then {c3.").append(CategoryModel.CODE).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 3 then {c2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 3 then {c1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 3 then {c0.").append(CategoryModel.CODE).append("} else '' end) cat3code, ");
        query.append("  (case when {c3.").append(CategoryModel.LEVEL).append("} = 4 then {c3.").append(CategoryModel.CODE).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 4 then {c2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 4 then {c1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 4 then {c0.").append(CategoryModel.CODE).append("} else '' end) cat4code, ");
        query.append("  (case when {c2.").append(CategoryModel.LEVEL).append("} = 5 then {c2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 5 then {c1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 5 then {c0.").append(CategoryModel.CODE).append("} else '' end) cat5code, ");
        query.append("  (case when {c1.").append(CategoryModel.LEVEL).append("} = 6 then {c1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 6 then {c0.").append(CategoryModel.CODE).append("} else '' end) cat6code, ");
        query.append("  (case when {c0.").append(CategoryModel.LEVEL).append("} = 7 then {c0.").append(CategoryModel.CODE).append("} else '' end) cat7code, ");
        
        // PimCategorTypecode codes
        query.append("  (case when {c6.").append(CategoryModel.LEVEL).append("} = 1 then {dpct6.").append(CategoryModel.CODE).append("} when {c5.").append(CategoryModel.LEVEL).append("} = 1 then {dpct5.").append(CategoryModel.CODE).append("} when {c4.").append(CategoryModel.LEVEL).append("} = 1 then {dpct4.").append(CategoryModel.CODE).append("} when {c3.").append(CategoryModel.LEVEL).append("} = 1 then {dpct3.").append(CategoryModel.CODE).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 1 then {dpct2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 1 then {dpct1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 1 then {dpct0.").append(CategoryModel.CODE).append("} else '' end) cat1PimTypecode, ");
        query.append("  (case when {c5.").append(CategoryModel.LEVEL).append("} = 2 then {dpct5.").append(CategoryModel.CODE).append("} when {c4.").append(CategoryModel.LEVEL).append("} = 2 then {dpct4.").append(CategoryModel.CODE).append("} when {c3.").append(CategoryModel.LEVEL).append("} = 2 then {dpct3.").append(CategoryModel.CODE).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 2 then {dpct2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 2 then {dpct1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 2 then {dpct0.").append(CategoryModel.CODE).append("} else '' end) cat2PimTypecode, ");
        query.append("  (case when {c4.").append(CategoryModel.LEVEL).append("} = 3 then {dpct4.").append(CategoryModel.CODE).append("} when {c3.").append(CategoryModel.LEVEL).append("} = 3 then {dpct3.").append(CategoryModel.CODE).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 3 then {dpct2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 3 then {dpct1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 3 then {dpct0.").append(CategoryModel.CODE).append("} else '' end) cat3PimTypecode, ");
        query.append("  (case when {c3.").append(CategoryModel.LEVEL).append("} = 4 then {dpct3.").append(CategoryModel.CODE).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 4 then {dpct2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 4 then {dpct1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 4 then {dpct0.").append(CategoryModel.CODE).append("} else '' end) cat4PimTypecode, ");
        query.append("  (case when {c2.").append(CategoryModel.LEVEL).append("} = 5 then {dpct2.").append(CategoryModel.CODE).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 5 then {dpct1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 5 then {dpct0.").append(CategoryModel.CODE).append("} else '' end) cat5PimTypecode, ");
        query.append("  (case when {c1.").append(CategoryModel.LEVEL).append("} = 6 then {dpct1.").append(CategoryModel.CODE).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 6 then {dpct0.").append(CategoryModel.CODE).append("} else '' end) cat6PimTypecode, ");
        query.append("  (case when {c0.").append(CategoryModel.LEVEL).append("} = 7 then {dpct0.").append(CategoryModel.CODE).append("} else '' end) cat7PimTypecode, ");

        // Category images
        query.append("  (case when {c6.").append(CategoryModel.LEVEL).append("} = 1 then {media6.").append(MediaModel.INTERNALURL).append("} when {c5.").append(CategoryModel.LEVEL).append("} = 1 then {media5.").append(MediaModel.INTERNALURL).append("} when {c4.").append(CategoryModel.LEVEL).append("} = 1 then {media4.").append(MediaModel.INTERNALURL).append("} when {c3.").append(CategoryModel.LEVEL).append("} = 1 then {media3.").append(MediaModel.INTERNALURL).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 1 then {media2.").append(MediaModel.INTERNALURL).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 1 then {media1.").append(MediaModel.INTERNALURL).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 1 then {media0.").append(MediaModel.INTERNALURL).append("} else '' end) cat1imageUrl, ");
        query.append("  (case when {c5.").append(CategoryModel.LEVEL).append("} = 2 then {media5.").append(MediaModel.INTERNALURL).append("} when {c4.").append(CategoryModel.LEVEL).append("} = 2 then {media4.").append(MediaModel.INTERNALURL).append("} when {c3.").append(CategoryModel.LEVEL).append("} = 2 then {media3.").append(MediaModel.INTERNALURL).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 2 then {media2.").append(MediaModel.INTERNALURL).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 2 then {media1.").append(MediaModel.INTERNALURL).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 2 then {media0.").append(MediaModel.INTERNALURL).append("} else '' end) cat2imageUrl, ");
        query.append("  (case when {c4.").append(CategoryModel.LEVEL).append("} = 3 then {media4.").append(MediaModel.INTERNALURL).append("} when {c3.").append(CategoryModel.LEVEL).append("} = 3 then {media3.").append(MediaModel.INTERNALURL).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 3 then {media2.").append(MediaModel.INTERNALURL).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 3 then {media1.").append(MediaModel.INTERNALURL).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 3 then {media0.").append(MediaModel.INTERNALURL).append("} else '' end) cat3imageUrl, ");
        query.append("  (case when {c3.").append(CategoryModel.LEVEL).append("} = 4 then {media3.").append(MediaModel.INTERNALURL).append("} when {c2.").append(CategoryModel.LEVEL).append("} = 4 then {media2.").append(MediaModel.INTERNALURL).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 4 then {media1.").append(MediaModel.INTERNALURL).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 4 then {media0.").append(MediaModel.INTERNALURL).append("} else '' end) cat4imageUrl, ");
        query.append("  (case when {c2.").append(CategoryModel.LEVEL).append("} = 5 then {media2.").append(MediaModel.INTERNALURL).append("} when {c1.").append(CategoryModel.LEVEL).append("} = 5 then {media1.").append(MediaModel.INTERNALURL).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 5 then {media0.").append(MediaModel.INTERNALURL).append("} else '' end) cat5imageUrl, ");
        query.append("  (case when {c1.").append(CategoryModel.LEVEL).append("} = 6 then {media1.").append(MediaModel.INTERNALURL).append("} when {c0.").append(CategoryModel.LEVEL).append("} = 6 then {media0.").append(MediaModel.INTERNALURL).append("} else '' end) cat6imageUrl, ");
        query.append("  (case when {c0.").append(CategoryModel.LEVEL).append("} = 7 then {media0.").append(MediaModel.INTERNALURL).append("} else '' end) cat7imageUrl ");
        
     // build up the category path, starting from a product
        query.append("FROM  { " + ProductModel._TYPECODE + " AS p ");
        query.append("  LEFT JOIN ").append(ProductModel._CATEGORYPRODUCTRELATION).append(" AS cpr ON {p.").append(ProductModel.PK).append("} = {cpr.").append(LinkModel.TARGET).append("} ");
        query.append("  LEFT JOIN ").append(CategoryModel._TYPECODE).append("! AS c0 ON {c0.").append(CategoryModel.PK).append("} = {cpr.").append(LinkModel.SOURCE).append("} ");
        query.append("    LEFT JOIN ").append(MediaContainerModel._TYPECODE).append(" AS mc0 ON {mc0.").append(MediaContainerModel.PK).append("} = {c0.").append(CategoryModel.PRIMARYIMAGE).append("} ");
        query.append("    LEFT JOIN ").append(MediaModel._TYPECODE).append("! AS media0 ON {media0.").append(MediaModel.MEDIACONTAINER).append("} = {mc0.").append(MediaContainerModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(MediaFormatModel._TYPECODE).append(" AS mf0 ON {mf0.").append(MediaFormatModel.PK).append("} = {media0.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("    LEFT JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct0 ON {dpct0.").append(DistPimCategoryTypeModel.PK).append("} = {c0.").append(CategoryModel.PIMCATEGORYTYPE).append("} ");
        
        query.append("  LEFT JOIN ").append(CategoryModel._CATEGORYCATEGORYRELATION).append(" AS ccr1 ON {c0.").append(CategoryModel.PK).append("} = {ccr1.target} ");
        query.append("  LEFT JOIN ").append(CategoryModel._TYPECODE).append("! AS c1 ON {c1.").append(CategoryModel.PK).append("} = {ccr1.").append(LinkModel.SOURCE).append("} ");
        query.append("    LEFT JOIN ").append(MediaContainerModel._TYPECODE).append(" AS mc1 ON {mc1.").append(MediaContainerModel.PK).append("} = {c1.").append(CategoryModel.PRIMARYIMAGE).append("} ");
        query.append("    LEFT JOIN ").append(MediaModel._TYPECODE).append("! AS media1 ON {media1.").append(MediaModel.MEDIACONTAINER).append("} = {mc1.").append(MediaContainerModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(MediaFormatModel._TYPECODE).append(" AS mf1 ON {mf1.").append(MediaFormatModel.PK).append("} = {media1.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("    LEFT JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct1 ON {dpct1.").append(DistPimCategoryTypeModel.PK).append("} = {c1.").append(CategoryModel.PIMCATEGORYTYPE).append("} ");
        
        query.append("  LEFT JOIN ").append(CategoryModel._CATEGORYCATEGORYRELATION).append(" AS ccr2 ON {c1.").append(CategoryModel.PK).append("} = {ccr2.target} ");
        query.append("  LEFT JOIN ").append(CategoryModel._TYPECODE).append("! AS c2 ON {c2.").append(CategoryModel.PK).append("} = {ccr2.").append(LinkModel.SOURCE).append("} ");
        query.append("    LEFT JOIN ").append(MediaContainerModel._TYPECODE).append(" AS mc2 ON {mc2.").append(MediaContainerModel.PK).append("} = {c2.").append(CategoryModel.PRIMARYIMAGE).append("} ");
        query.append("    LEFT JOIN ").append(MediaModel._TYPECODE).append("! AS media2 ON {media2.").append(MediaModel.MEDIACONTAINER).append("} = {mc2.").append(MediaContainerModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(MediaFormatModel._TYPECODE).append(" AS mf2 ON {mf2.").append(MediaFormatModel.PK).append("} = {media2.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("    LEFT JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct2 ON {dpct2.").append(DistPimCategoryTypeModel.PK).append("} = {c2.").append(CategoryModel.PIMCATEGORYTYPE).append("} ");
        
        query.append("  LEFT JOIN ").append(CategoryModel._CATEGORYCATEGORYRELATION).append(" AS ccr3 ON {c2.").append(CategoryModel.PK).append("} = {ccr3.target} ");
        query.append("  LEFT JOIN ").append(CategoryModel._TYPECODE).append("! AS c3 ON {c3.").append(CategoryModel.PK).append("} = {ccr3.").append(LinkModel.SOURCE).append("} ");
        query.append("    LEFT JOIN ").append(MediaContainerModel._TYPECODE).append(" AS mc3 ON {mc3.").append(MediaContainerModel.PK).append("} = {c3.").append(CategoryModel.PRIMARYIMAGE).append("} ");
        query.append("    LEFT JOIN ").append(MediaModel._TYPECODE).append("! AS media3 ON {media3.").append(MediaModel.MEDIACONTAINER).append("} = {mc3.").append(MediaContainerModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(MediaFormatModel._TYPECODE).append(" AS mf3 ON {mf3.").append(MediaFormatModel.PK).append("} = {media3.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("    LEFT JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct3 ON {dpct3.").append(DistPimCategoryTypeModel.PK).append("} = {c3.").append(CategoryModel.PIMCATEGORYTYPE).append("} ");
        
        query.append("  LEFT JOIN ").append(CategoryModel._CATEGORYCATEGORYRELATION).append(" AS ccr4 ON {c3.").append(CategoryModel.PK).append("} = {ccr4.target} ");
        query.append("  LEFT JOIN ").append(CategoryModel._TYPECODE).append("! AS c4 ON {c4.").append(CategoryModel.PK).append("} = {ccr4.").append(LinkModel.SOURCE).append("} ");
        query.append("    LEFT JOIN ").append(MediaContainerModel._TYPECODE).append(" AS mc4 ON {mc4.").append(MediaContainerModel.PK).append("} = {c4.").append(CategoryModel.PRIMARYIMAGE).append("} ");
        query.append("    LEFT JOIN ").append(MediaModel._TYPECODE).append("! AS media4 ON {media4.").append(MediaModel.MEDIACONTAINER).append("} = {mc4.").append(MediaContainerModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(MediaFormatModel._TYPECODE).append(" AS mf4 ON {mf4.").append(MediaFormatModel.PK).append("} = {media4.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("    LEFT JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct4 ON {dpct4.").append(DistPimCategoryTypeModel.PK).append("} = {c4.").append(CategoryModel.PIMCATEGORYTYPE).append("} ");
        
        query.append("  LEFT JOIN ").append(CategoryModel._CATEGORYCATEGORYRELATION).append(" AS ccr5 ON {c4.").append(CategoryModel.PK).append("} = {ccr5.target} ");
        query.append("  LEFT JOIN ").append(CategoryModel._TYPECODE).append("! AS c5 ON {c5.").append(CategoryModel.PK).append("} = {ccr5.").append(LinkModel.SOURCE).append("} ");
        query.append("    LEFT JOIN ").append(MediaContainerModel._TYPECODE).append(" AS mc5 ON {mc5.").append(MediaContainerModel.PK).append("} = {c5.").append(CategoryModel.PRIMARYIMAGE).append("} ");
        query.append("    LEFT JOIN ").append(MediaModel._TYPECODE).append("! AS media5 ON {media5.").append(MediaModel.MEDIACONTAINER).append("} = {mc5.").append(MediaContainerModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(MediaFormatModel._TYPECODE).append(" AS mf5 ON {mf5.").append(MediaFormatModel.PK).append("} = {media5.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("    LEFT JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct5 ON {dpct5.").append(DistPimCategoryTypeModel.PK).append("} = {c5.").append(CategoryModel.PIMCATEGORYTYPE).append("} ");
        
        query.append("  LEFT JOIN ").append(CategoryModel._CATEGORYCATEGORYRELATION).append(" AS ccr6 ON {c5.").append(CategoryModel.PK).append("} = {ccr6.target} ");
        query.append("  LEFT JOIN ").append(CategoryModel._TYPECODE).append("! AS c6 ON {c6.").append(CategoryModel.PK).append("} = {ccr6.").append(LinkModel.SOURCE).append("} ");
        query.append("    LEFT JOIN ").append(MediaContainerModel._TYPECODE).append(" AS mc6 ON {mc6.").append(MediaContainerModel.PK).append("} = {c6.").append(CategoryModel.PRIMARYIMAGE).append("} ");
        query.append("    LEFT JOIN ").append(MediaModel._TYPECODE).append("! AS media6 ON {media6.").append(MediaModel.MEDIACONTAINER).append("} = {mc6.").append(MediaContainerModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(MediaFormatModel._TYPECODE).append(" AS mf6 ON {mf6.").append(MediaFormatModel.PK).append("} = {media6.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("    LEFT JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct6 ON {dpct6.").append(DistPimCategoryTypeModel.PK).append("} = {c6.").append(CategoryModel.PIMCATEGORYTYPE).append("} ");
        
        query.append("  LEFT JOIN ").append(CategoryModel._CATEGORYCATEGORYRELATION).append(" AS ccr7 ON {c6.").append(CategoryModel.PK).append("} = {ccr7.target} ");
        query.append("  LEFT JOIN ").append(CategoryModel._TYPECODE).append("! AS c7 ON {c7.").append(CategoryModel.PK).append("} = {ccr7.").append(LinkModel.SOURCE).append("} ");
        query.append("    LEFT JOIN ").append(MediaContainerModel._TYPECODE).append(" AS mc7 ON {mc7.").append(MediaContainerModel.PK).append("} = {c7.").append(CategoryModel.PRIMARYIMAGE).append("} ");
        query.append("    LEFT JOIN ").append(MediaModel._TYPECODE).append("! AS media7 ON {media7.").append(MediaModel.MEDIACONTAINER).append("} = {mc7.").append(MediaContainerModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(MediaFormatModel._TYPECODE).append(" AS mf7 ON {mf7.").append(MediaFormatModel.PK).append("} = {media7.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("    LEFT JOIN ").append(DistPimCategoryTypeModel._TYPECODE).append(" AS dpct7 ON {dpct7.").append(DistPimCategoryTypeModel.PK).append("} = {c7.").append(CategoryModel.PIMCATEGORYTYPE).append("} ");
        
        query.append("} ");
        query.append(where("({mf0.".concat(MediaFormatModel.QUALIFIER).concat("} IS NULL OR {mf0.").concat(MediaFormatModel.QUALIFIER).concat("} = '").concat(DistConstants.MediaFormat.LANDSCAPE_SMALL).concat("') ")
                .concat("  AND ({mf1.").concat(MediaFormatModel.QUALIFIER).concat("} IS NULL OR {mf1.").concat(MediaFormatModel.QUALIFIER).concat("} = '").concat(DistConstants.MediaFormat.LANDSCAPE_SMALL).concat("') ")
                .concat("  AND ({mf2.").concat(MediaFormatModel.QUALIFIER).concat("} IS NULL OR {mf2.").concat(MediaFormatModel.QUALIFIER).concat("} = '").concat(DistConstants.MediaFormat.LANDSCAPE_SMALL).concat("') ")
                .concat("  AND ({mf3.").concat(MediaFormatModel.QUALIFIER).concat("} IS NULL OR {mf3.").concat(MediaFormatModel.QUALIFIER).concat("} = '").concat(DistConstants.MediaFormat.LANDSCAPE_SMALL).concat("') ")
                .concat("  AND ({mf4.").concat(MediaFormatModel.QUALIFIER).concat("} IS NULL OR {mf4.").concat(MediaFormatModel.QUALIFIER).concat("} = '").concat(DistConstants.MediaFormat.LANDSCAPE_SMALL).concat("') ")
                .concat("  AND ({mf5.").concat(MediaFormatModel.QUALIFIER).concat("} IS NULL OR {mf5.").concat(MediaFormatModel.QUALIFIER).concat("} = '").concat(DistConstants.MediaFormat.LANDSCAPE_SMALL).concat("') ")
                .concat("  AND ({mf6.").concat(MediaFormatModel.QUALIFIER).concat("} IS NULL OR {mf6.").concat(MediaFormatModel.QUALIFIER).concat("} = '").concat(DistConstants.MediaFormat.LANDSCAPE_SMALL).concat("') ")
                .concat("  AND ({mf7.").concat(MediaFormatModel.QUALIFIER).concat("} IS NULL OR {mf7.").concat(MediaFormatModel.QUALIFIER).concat("} = '").concat(DistConstants.MediaFormat.LANDSCAPE_SMALL).concat("') ")));
        
        return query;
    }

    protected StringBuilder appendInStockSubSelect(final StringBuilder query) {
        query.append("SELECT s.productpk AS productpk, (CASE WHEN sum(s.totalInStock) <= 0 THEN sum(s.pickupStock) WHEN sum(s.totalInStock) > 0 THEN sum(s.totalInStock) ELSE 0 END) AS totalInStock, (CASE WHEN sum(s.InStock) >= 100 THEN 2 WHEN sum(s.inStock) = 75 THEN 1 WHEN sum(s.inStock) = 50 THEN 1 WHEN sum(s.inStock) = 25 THEN 1 WHEN sum(s.InStock) < 50 THEN 0 ELSE 0 END) AS InStock, sum(s.availableInPickup) AS availableInPickup, sum(s.pickupStock) AS pickupStock ");
        query.append("FROM ( ");
        query.append("{{ ");
        query.append("SELECT {p.pk} AS productpk, sum({sl.available}) AS totalInStock, (CASE WHEN sum({sl.available}) > 0 THEN 100 ELSE 0 END) AS InStock, 0 AS availableInPickup, 0 AS pickupStock ");
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
        query.append(") GROUP BY {p.pk} ");
        query.append("}} ");
        query.append("UNION ALL {{ ");
        query.append("SELECT {p.pk} AS productpk, sum({sl.available}) AS totalInStock, (CASE WHEN sum({sl.available}) > 0 THEN 50 ELSE 0 END) AS InStock, 0 AS availableInPickup, 0 AS pickupStock ");
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
        query.append(") GROUP BY {p.pk} ");
        query.append("}} ");
        query.append("UNION ALL ");
        query.append("{{ ");
        query.append("SELECT {p.pk} AS productpk, 0 AS totalInStock, (CASE WHEN sum({sl.available}) > 0 THEN 25 ELSE 0 END) AS InStock, (CASE WHEN sum({sl.available}) > 0 THEN 1 ELSE 0 END) AS availableInPickup, (CASE WHEN sum({sl.available}) > 0 THEN sum({sl.available}) ELSE 0 END) AS pickupStock ");
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
        query.append(") GROUP BY {p.pk} ");               
        query.append("}} ");
        query.append("UNION ALL ");
        query.append("{{ ");
        query.append("SELECT {p.pk} AS productpk, 0 AS totalInStock, 0 AS InStock, 0 AS availableInPickup, 0 AS pickupStock ");
        query.append("FROM {product AS p} ");
        query.append("}}) s ");
        query.append("GROUP BY s.productpk ");
        return query;
    }

    protected StringBuilder appendPriceSubselect(final StringBuilder query) {
        query.append("SELECT prices.").append(PRICE_JOIN_PK).append(" AS ").append(PRICE_JOIN_PK).append(", CASE WHEN (count(*) > 0) THEN 'or' + ").append(stringAgg(PRICES_JOIN_VALUE.concat(".price"), null, PRICES_JOIN_VALUE.concat(".").concat(DistPriceRowModel.MINQTD))).append(" ELSE '' END AS price ");
        query.append("  FROM ({{ ");
        query.append("      SELECT scalePrice.").append(PRICE_JOIN_PK).append(" AS ").append(PRICE_JOIN_PK).append(", scalePrice.price AS price, scalePrice.").append(DistPriceRowModel.MINQTD).append(" AS ").append(DistPriceRowModel.MINQTD).append(" ");
        query.append("      FROM ({{ ");
        query.append("          SELECT ");
        query.append("              {pr.").append(DistPriceRowModel.PRODUCT).append("} AS ").append(PRICE_JOIN_PK).append(", ");
        query.append("              ({cur.").append(CurrencyModel.ISOCODE).append("} + ';' + '").append(GROSS).append("' + ';' + {pr.").append(DistPriceRowModel.MINQTD).append("} + '=' + (1 + ({tax.").append(TaxModel.VALUE).append("}/100)) * {pr.").append(DistPriceRowModel.PRICE).append("} / {pr.").append(PriceRowModel.UNITFACTOR).append("} + 'or' + {cur.").append(CurrencyModel.ISOCODE).append("} + ';' + '").append(NET).append("' + ';' + {pr.").append(DistPriceRowModel.MINQTD).append("} + '=' + {pr.").append(DistPriceRowModel.PRICE).append("} / {pr.").append(PriceRowModel.UNITFACTOR).append("} + 'or' ) AS price, ");
        query.append("              {pr.").append(DistPriceRowModel.MINQTD).append("} AS ").append(DistPriceRowModel.MINQTD).append(", ");
        query.append("              RANK() OVER (PARTITION BY {p.").append(ProductModel.PK).append("} ORDER BY {pct.").append(DistErpPriceConditionTypeModel.PRIORITY).append("}, {pr.").append(DistPriceRowModel.STARTTIME).append("} DESC) AS rankValue ");
        query.append("          FROM {").append(ProductModel._TYPECODE).append(" AS p ");
        query.append("                  JOIN ").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("                      ON {p.").append(ProductModel.PK).append("} = {pr.").append(DistPriceRowModel.PRODUCT).append("} ");
        query.append("                  JOIN ").append(DistErpPriceConditionTypeModel._TYPECODE).append(" AS pct ");
        query.append("                      ON {pr.").append(DistPriceRowModel.ERPPRICECONDITIONTYPE).append("} = {pct.").append(DistPriceRowModel.PK).append("} ");
        query.append("                  JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsop ");
        query.append("                      ON {p.").append(ProductModel.PK).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCT).append("}");
        query.append("                  JOIN ").append(DistSalesOrgModel._TYPECODE).append(" AS dso ");
        query.append("                      ON {dsop.").append(DistSalesOrgProductModel.SALESORG).append("} = {dso.").append(DistSalesOrgModel.PK).append("} AND {dso.").append(DistSalesOrgModel.PK).append("} = (?").append(DistSalesOrgModel._TYPECODE).append(")");
        query.append("                  JOIN ").append(CurrencyModel._TYPECODE).append(" AS cur ");
        query.append("                      ON {pr.").append(DistPriceRowModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK).append("}");
        query.append("                  JOIN ").append(CMSSiteModel._TYPECODE).append(" AS cms ");
        query.append("                      ON {pr.").append(DistPriceRowModel.UG).append("} = {cms.").append(CMSSiteModel.USERPRICEGROUP).append("} AND {cms.").append(CMSSiteModel.PK).append("} = (?CMSSite)");
        query.append("                  JOIN ").append(TaxRowModel._TYPECODE).append(" AS tr ");
        query.append("                      ON {cms.").append(CMSSiteModel.USERTAXGROUP).append("} = {tr.").append(TaxRowModel.UG).append("}");
        query.append("                  JOIN ").append(TaxModel._TYPECODE).append(" AS tax ");
        query.append("                      ON {tr.").append(TaxRowModel.TAX).append("} = {tax.").append(TaxModel.PK).append("}");
        query.append("                      AND {tr.").append(TaxRowModel.PG).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCTTAXGROUP).append("} ");
        query.append("          } ");
        query.append("          ").append(where(" ({pr.".concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?").concat(DistFactFinderProductExportParameterProvider.DATE).concat(")) ")
                .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?").concat(DistFactFinderProductExportParameterProvider.DATE).concat(")) ")
                .concat(" AND {pr.matchValue} =  ({{ ")
                .concat(" SELECT max({matchvaluepr.matchValue}) ")
                .concat(" FROM { ").concat(DistPriceRowModel._TYPECODE).concat(" AS matchvaluepr } ")
                .concat(where("{matchvaluepr.".concat(DistPriceRowModel.PRODUCT).concat("} = {pr.").concat(DistPriceRowModel.PRODUCT).concat("} ")
                    .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.NET).concat("} = {pr.").concat(DistPriceRowModel.NET).concat("} ")
                    .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.UG).concat("} = {pr.").concat(DistPriceRowModel.UG).concat("} ")
                    .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.CURRENCY).concat("} = {pr.").concat(DistPriceRowModel.CURRENCY).concat("} ")
                    .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
                    .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) ")))
                    .concat(" }}) ")));
        query.append("      }}) scalePrice ");
        query.append("      ").append(where("scalePrice.rankValue = 1"));
        query.append("  }} UNION {{ ");
        // We need to UNION the MIN value for a pricerow explicitly to show factfinder which value to use for sorting etc.
        query.append("      SELECT minPrice.").append(PRICE_JOIN_PK).append(" AS ").append(PRICE_JOIN_PK).append(", minPrice.price AS price, minPrice.").append(DistPriceRowModel.MINQTD).append(" AS ").append(DistPriceRowModel.MINQTD).append(" ");
        query.append("      FROM ({{ ");
        query.append("          SELECT ");
        query.append("              {pr.").append(DistPriceRowModel.PRODUCT).append("} AS ").append(PRICE_JOIN_PK).append(", ");
        query.append("              ({cur.").append(CurrencyModel.ISOCODE).append("} + ';' + '").append(GROSS).append("' + ';").append(MIN).append("=' + (1 + ({tax.").append(TaxModel.VALUE).append("}/100)) * {pr.").append(DistPriceRowModel.PRICE).append("} / {pr.").append(PriceRowModel.UNITFACTOR).append("} + 'or' + {cur.").append(CurrencyModel.ISOCODE).append("} + ';' + '").append(NET).append("' + ';").append(MIN).append("=' + {pr.").append(DistPriceRowModel.PRICE).append("} / {pr.").append(PriceRowModel.UNITFACTOR).append("} + 'or' ) AS price, ");
        query.append("              {pr.").append(DistPriceRowModel.MINQTD).append("} AS ").append(DistPriceRowModel.MINQTD).append(", ");
        query.append("              RANK() OVER (PARTITION BY {p.").append(ProductModel.PK).append("} ORDER BY {pct.").append(DistErpPriceConditionTypeModel.PRIORITY).append("}, {pr.").append(DistPriceRowModel.STARTTIME).append("} DESC) AS rankValue ");
        query.append("          FROM {").append(ProductModel._TYPECODE).append(" AS p ");
        query.append("              JOIN ").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("                  ON {p.").append(ProductModel.PK).append("} = {pr.").append(DistPriceRowModel.PRODUCT).append("} ");
        query.append("              JOIN ").append(DistErpPriceConditionTypeModel._TYPECODE).append(" AS pct ");
        query.append("                  ON {pr.").append(DistPriceRowModel.ERPPRICECONDITIONTYPE).append("} = {pct.").append(DistPriceRowModel.PK).append("} ");
        query.append("              JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsop ");
        query.append("                  ON {p.").append(ProductModel.PK).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCT).append("}");
        query.append("              JOIN ").append(DistSalesOrgModel._TYPECODE).append(" AS dso ");
        query.append("                  ON {dsop.").append(DistSalesOrgProductModel.SALESORG).append("} = {dso.").append(DistSalesOrgModel.PK).append("} AND {dso.").append(DistSalesOrgModel.PK).append("} = (?").append(DistSalesOrgModel._TYPECODE).append(")");
        query.append("              JOIN ").append(CurrencyModel._TYPECODE).append(" AS cur ");
        query.append("                  ON {pr.").append(DistPriceRowModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK).append("}");
        query.append("              JOIN ").append(CMSSiteModel._TYPECODE).append(" AS cms ");
        query.append("                  ON {pr.").append(DistPriceRowModel.UG).append("} = {cms.").append(CMSSiteModel.USERPRICEGROUP).append("} AND {cms.").append(CMSSiteModel.PK).append("} = (?CMSSite)");
        query.append("              JOIN ").append(TaxRowModel._TYPECODE).append(" AS tr ");
        query.append("                  ON {cms.").append(CMSSiteModel.USERTAXGROUP).append("} = {tr.").append(TaxRowModel.UG).append("}");
        query.append("                  AND {tr.").append(TaxRowModel.PG).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCTTAXGROUP).append("} ");
        query.append("              JOIN ").append(TaxModel._TYPECODE).append(" AS tax ");
        query.append("                  ON {tr.").append(TaxRowModel.TAX).append("} = {tax.").append(TaxModel.PK).append("}");
        query.append("          } ");
        query.append(where(" ({pr.".concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?").concat(DistFactFinderProductExportParameterProvider.DATE).concat(")) ")
                .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?").concat(DistFactFinderProductExportParameterProvider.DATE).concat(")) ")
                .concat(" AND  {pr.").concat(DistPriceRowModel.MINQTD).concat("} = ({{ ")
                .concat(" SELECT min({minpr.").concat(DistPriceRowModel.MINQTD).concat("}) ")
                .concat(" FROM { ").concat(DistPriceRowModel._TYPECODE).concat(" AS minpr } ")
                .concat(where(" {minpr.".concat(DistPriceRowModel.PRODUCT).concat("} = {pr.").concat(DistPriceRowModel.PRODUCT).concat("} ")
                    .concat(" AND {minpr.").concat(DistPriceRowModel.NET).concat("} = {pr.").concat(DistPriceRowModel.NET).concat("} ")
                    .concat(" AND {minpr.").concat(DistPriceRowModel.UG).concat("} =  {pr.").concat(DistPriceRowModel.UG).concat("} ")
                    .concat(" AND {minpr.").concat(DistPriceRowModel.CURRENCY).concat("} = {pr.").concat(DistPriceRowModel.CURRENCY).concat("} ")
                    .concat(" AND ({minpr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {minpr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
                    .concat(" AND ({minpr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {minpr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) ")
                    .concat(" AND {minpr.matchValue} =  ({{ ")
                    .concat("     SELECT max({matchvaluepr.matchValue}) ")
                    .concat("     FROM { DistPriceRow AS matchvaluepr } ")
                    .concat(where(" {matchvaluepr.".concat(DistPriceRowModel.PRODUCT).concat("} = {minpr.").concat(DistPriceRowModel.PRODUCT).concat("} ")
                        .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.NET).concat("} = {minpr.").concat(DistPriceRowModel.NET).concat("} ")
                        .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.UG).concat("} = {minpr.").concat(DistPriceRowModel.UG).concat("} ")
                        .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.CURRENCY).concat("} = {minpr.").concat(DistPriceRowModel.CURRENCY).concat("} ")
                        .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
                        .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) ")))
                    .concat(" }}) ")))
                .concat(" }})")
                .concat(" AND {pr.matchValue} =  ({{ ")
                .concat("     SELECT max({matchvaluepr.matchValue}) ")
                .concat("     FROM { DistPriceRow AS matchvaluepr } ")
                .concat(where(" {matchvaluepr.".concat(DistPriceRowModel.PRODUCT).concat("} = {pr.").concat(DistPriceRowModel.PRODUCT).concat("} ")
                    .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.NET).concat("} = {pr.").concat(DistPriceRowModel.NET).concat("} ")
                    .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.UG).concat("} = {pr.").concat(DistPriceRowModel.UG).concat("} ")
                    .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.CURRENCY).concat("} = {pr.").concat(DistPriceRowModel.CURRENCY).concat("} ")
                    .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
                    .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) ")))
                .concat(" }}) ")));
        query.append("      }}) minPrice ");
        query.append(where("minPrice.rankValue = 1"));
        query.append("  }}) ").append(PRICES_JOIN_VALUE).append(" ");
        query.append("GROUP BY prices.productpk ");
        return query;
    }

    protected StringBuilder appendSingleMinPriceSubselect(final StringBuilder query) {
        query.append("SELECT minPrice.").append(PRICE_JOIN_PK).append(", sum(minPrice.singleMinPrice) AS singleMinPrice, sum(minPrice.specialPrice) AS specialPrice ");
        query.append("  FROM ({{ ");
        query.append("      SELECT innerSelect.").append(PRICE_JOIN_PK).append(" AS ").append(PRICE_JOIN_PK).append(", innerSelect.singleMinPrice AS singleMinPrice, innerSelect.specialPrice AS specialPrice ");
        query.append("      FROM ({{ ");
        query.append("          SELECT {p.").append(ProductModel.PK).append("} AS ").append(PRICE_JOIN_PK).append(", ");
        query.append("              ({pr.").append(DistPriceRowModel.PRICE).append("} / {pr.").append(DistPriceRowModel.UNITFACTOR).append("}) AS singleMinPrice, ");
        query.append("              (CASE WHEN {pct.").append(DistErpPriceConditionTypeModel.CODE).append("} = 'ZN00' THEN 1 ELSE 0 END) AS specialPrice, ");
        query.append("              RANK() OVER (PARTITION BY {p.").append(ProductModel.PK).append("} ORDER BY {pct.").append(DistErpPriceConditionTypeModel.PRIORITY).append("}, {pr.").append(DistPriceRowModel.STARTTIME).append("} DESC) AS rankValue ");
        query.append("              FROM {").append(ProductModel._TYPECODE).append(" AS p ");
        query.append("                  JOIN ").append(DistPriceRowModel._TYPECODE).append(" AS pr ");
        query.append("                      ON {p.").append(ProductModel.PK).append("} = {pr.").append(DistPriceRowModel.PRODUCT).append("} ");
        query.append("                  JOIN ").append(DistErpPriceConditionTypeModel._TYPECODE).append(" AS pct ");
        query.append("                      ON {pr.").append(DistPriceRowModel.ERPPRICECONDITIONTYPE).append("} = {pct.").append(DistPriceRowModel.PK).append("} ");
        query.append("                  JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsop ");
        query.append("                      ON {p.").append(ProductModel.PK).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCT).append("}");
        query.append("                  JOIN ").append(DistSalesOrgModel._TYPECODE).append(" AS dso ");
        query.append("                      ON {dsop.").append(DistSalesOrgProductModel.SALESORG).append("} = {dso.").append(DistSalesOrgModel.PK).append("} AND {dso.").append(DistSalesOrgModel.PK).append("} = (?").append(DistSalesOrgModel._TYPECODE).append(")");
        query.append("                  JOIN ").append(CurrencyModel._TYPECODE).append(" AS cur ");
        query.append("                      ON {pr.").append(DistPriceRowModel.CURRENCY).append("} = {cur.").append(CurrencyModel.PK).append("}");
        query.append("                  JOIN ").append(CMSSiteModel._TYPECODE).append(" AS cms ");
        query.append("                      ON {pr.").append(DistPriceRowModel.UG).append("} = {cms.").append(CMSSiteModel.USERPRICEGROUP).append("} AND {cms.").append(CMSSiteModel.PK).append("} = (?CMSSite)");
        query.append("                  JOIN ").append(TaxRowModel._TYPECODE).append(" AS tr ");
        query.append("                      ON {cms.").append(CMSSiteModel.USERTAXGROUP).append("} = {tr.").append(TaxRowModel.UG).append("}");
        query.append("                      AND {tr.").append(TaxRowModel.PG).append("} = {dsop.").append(DistSalesOrgProductModel.PRODUCTTAXGROUP).append("} ");
        query.append("                  JOIN ").append(TaxModel._TYPECODE).append(" AS tax ");
        query.append("                      ON {tr.").append(TaxRowModel.TAX).append("} = {tax.").append(TaxModel.PK).append("}");
        query.append("              } ");
        query.append(where(" ({pr.".concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
                .concat(" AND ({pr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {pr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) ")
                .concat(" AND {pr.").concat(DistPriceRowModel.MINQTD).concat("} =  ({{ ")
                .concat("     SELECT min({minpr.").concat(DistPriceRowModel.MINQTD).concat("}) ")
                .concat("     FROM { DistPriceRow AS minpr } ")
                .concat(where(" {minpr.".concat(DistPriceRowModel.PRODUCT).concat("} = {pr.").concat(DistPriceRowModel.PRODUCT).concat("} ")
                    .concat(" AND {minpr.").concat(DistPriceRowModel.NET).concat("} = {pr.").concat(DistPriceRowModel.NET).concat("} ")
                    .concat(" AND {minpr.").concat(DistPriceRowModel.UG).concat("} = {pr.").concat(DistPriceRowModel.UG).concat("} ")
                    .concat(" AND {minpr.").concat(DistPriceRowModel.CURRENCY).concat("} = {pr.").concat(DistPriceRowModel.CURRENCY).concat("} ")
                    .concat(" AND ({minpr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {minpr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
                    .concat(" AND ({minpr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {minpr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) ")
                    .concat(" AND {minpr.matchValue} =  ({{ ")
                    .concat("     SELECT max({matchvaluepr.matchValue}) ")
                    .concat("     FROM { DistPriceRow AS matchvaluepr } ")
                    .concat(where(" {matchvaluepr.".concat(DistPriceRowModel.PRODUCT).concat("} = {minpr.").concat(DistPriceRowModel.PRODUCT).concat("} ")
                        .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.NET).concat("} = {minpr.").concat(DistPriceRowModel.NET).concat("} ")
                        .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.UG).concat("} = {minpr.").concat(DistPriceRowModel.UG).concat("} ")
                        .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.CURRENCY).concat("} = {minpr.").concat(DistPriceRowModel.CURRENCY).concat("} ")
                        .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
                        .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) ")))))
                .concat("         }}) ")
                .concat(" }}) ")
                .concat(" AND {pr.matchValue} =  ({{ ")
                .concat("     SELECT max({matchvaluepr.matchValue}) ")
                .concat("     FROM { DistPriceRow AS matchvaluepr } ")
                .concat(where(" {matchvaluepr.".concat(DistPriceRowModel.PRODUCT).concat("} = {pr.").concat(DistPriceRowModel.PRODUCT).concat("} ")
                    .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.NET).concat("} = {pr.").concat(DistPriceRowModel.NET).concat("} ")
                    .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.UG).concat("} = {pr.").concat(DistPriceRowModel.UG).concat("} ")
                    .concat(" AND {matchvaluepr.").concat(DistPriceRowModel.CURRENCY).concat("} = {pr.").concat(DistPriceRowModel.CURRENCY).concat("} ")
                    .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.STARTTIME).concat("} <= (?Date)) ")
                    .concat(" AND ({matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} IS NULL OR {matchvaluepr.").concat(DistPriceRowModel.ENDTIME).concat("} >= (?Date)) ")))
                .concat(" }}) ")));
        query.append("      }}) innerSelect ");
        query.append(where("rankValue = 1"));
        query.append("  }} ");
        query.append("  UNION ALL ");
        query.append("  {{ ");
        query.append("      SELECT {p.").append(ProductModel.PK).append("} AS ").append(PRICE_JOIN_PK).append(", 0 AS singleMinPrice, 0 AS specialPrice ");
        query.append("      FROM {").append(ProductModel._TYPECODE).append(" AS p} ");
        query.append("  }}) minPrice ");
        query.append("GROUP BY minPrice.").append(PRICE_JOIN_PK).append(" ");
        return query;
    }

    protected StringBuilder appendProductURLSelect(final StringBuilder query) {
        query.append("'/' + ?").append(DistFactFinderProductExportParameterProvider.LANGUAGE_ISOCODE).append(" + '/' ");
        query.append("  + {p.").append(ProductModel.NAMESEO).append(":o} ");
        query.append("  + CASE WHEN {m.").append(DistManufacturerModel.NAMESEO).append(":o} IS NULL THEN '' ELSE '-' + {m.").append(DistManufacturerModel.NAMESEO)
                .append(":o} END ");
        query.append("  + CASE WHEN {p.").append(ProductModel.TYPENAMESEO).append("} IS NULL THEN '' ELSE '-' + {p.").append(ProductModel.TYPENAMESEO)
                .append("} END ");
        query.append("  + '/p/' + {p.").append(ProductModel.CODE).append("} ");
        return query;
    }

    protected StringBuilder appendProductImageURLSelect(final StringBuilder query) {
        query.append("({{ ");
        query.append("  SELECT {media.").append(MediaModel.INTERNALURL).append("} ");
        query.append("  FROM ");
        query.append("     { ").append(MediaModel._TYPECODE).append("! AS media ");
        query.append("     JOIN ").append(MediaFormatModel._TYPECODE).append("! AS mf ");
        query.append("           ON {mf.").append(MediaFormatModel.PK).append("} = {media.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("             AND {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.PORTRAIT_SMALL).append("' ");
        query.append("     } ");
        query.append(where("{media.".concat(MediaModel.MEDIACONTAINER).concat("} = ")
                .concat("      CASE WHEN {p.").concat(ProductModel.PRIMARYIMAGE).concat("} IS NOT NULL THEN {p.").concat(ProductModel.PRIMARYIMAGE).concat("} ELSE {p.").concat(ProductModel.ILLUSTRATIVEIMAGE).concat("} END ")));
        query.append("}}) ");
        return query;
    }

    protected StringBuilder appendAdditionalProductImageURLSelect(final StringBuilder query) {
        query.append("({{ ");
        query.append("  SELECT ").append(chr("123"));
        query.append("    + ").append(stringAgg("'\"' + {mf.".concat(MediaFormatModel.QUALIFIER).concat("} + '\":\"' + {media.").concat(MediaModel.INTERNALURL).concat("} + '\"'"), ",", "{mf.".concat(MediaFormatModel.QUALIFIER).concat("}")));
        query.append("    +  ").append(chr("125"));
        query.append("  FROM { ");
        query.append("    ").append(MediaModel._TYPECODE).append("! AS media ");
        query.append("    JOIN ").append(MediaFormatModel._TYPECODE).append("! AS mf ");
        query.append("      ON {mf.").append(MediaFormatModel.PK).append("} = {media.").append(MediaModel.MEDIAFORMAT).append("} ");
        query.append("        AND (");
        query.append("          {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.LANDSCAPE_SMALL).append("' ");
        query.append("          OR ");
        query.append("          {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.LANDSCAPE_MEDIUM).append("' ");
        query.append("        ) ");
        query.append("  } ");
        query.append(where(" {media.".concat(MediaModel.INTERNALURL).concat("} IS NOT NULL ")
                .concat(" AND {media.").concat(MediaModel.MEDIACONTAINER).concat("} = ")
                .concat("   CASE WHEN {p.").concat(ProductModel.PRIMARYIMAGE).concat("} IS NOT NULL THEN {p.").concat(ProductModel.PRIMARYIMAGE).concat("} ELSE {p.").concat(ProductModel.ILLUSTRATIVEIMAGE).concat("} END ")));
        query.append("}}) ");
        return query;
    }

    protected StringBuilder appendManufacturerURLSelect(final StringBuilder query) {
        query.append("'/' + ?").append(DistFactFinderProductExportParameterProvider.LANGUAGE_ISOCODE).append(" + ");
        query.append("'/manufacturer/' + ");
        query.append(" CASE WHEN {m.").append(DistManufacturerModel.NAMESEO).append("} IS NOT NULL ");
        query.append(" THEN {m.").append(DistManufacturerModel.NAMESEO).append("} + '/' ");
        query.append(" ELSE ''");
        query.append(" END");
        query.append(" + {m.").append(DistManufacturerModel.CODE).append("} ");

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
        query.append("             AND {mf.").append(MediaFormatModel.QUALIFIER).append("} = '").append(DistConstants.MediaFormat.PORTRAIT_SMALL).append("' ");
        query.append("     } ");
        query.append(where("{media.".concat(MediaModel.MEDIACONTAINER).concat("} = {p.").concat(ProductModel.ENERGYEFFICIENCYLABELIMAGE).concat("}")));
        query.append("}}) ");
        return query;
    }

    protected StringBuilder appendSalesUnitSelect(final StringBuilder query) {
        query.append(" CASE ");
        query.append("   WHEN {dsu.").append(DistSalesUnitModel.NAME).append(":o} IS NOT NULL THEN {dsu.").append(DistSalesUnitModel.NAME).append(":o} ");
        query.append("   WHEN {dsu.").append(DistSalesUnitModel.NAMEERP).append(":o} IS NOT NULL THEN {dsu.").append(DistSalesUnitModel.NAMEERP).append(":o} ");
        query.append("   WHEN {u.").append(UnitModel.NAME).append(":o} IS NOT NULL THEN '1 ' + {u.").append(UnitModel.NAME).append(":o} ");
        query.append("   ELSE '-' ");
        query.append(" END ");
        return query;
    }
    
    protected StringBuilder appendSingleUnitPrice(final StringBuilder query) {
        query.append(" CASE WHEN {dsu.amount} IS NOT NULL THEN ").append(toNvarchar("(singleMinPrice.singleMinPrice / {dsu.amount})")).append(" ELSE '' END ");
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
    protected StringBuilder appendPromotionLabelsSelect(final StringBuilder query) {
        query.append("'[' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.HOTOFFER.getValue(), "pc." + ProductCountryModel.SHOWHOTOFFERLABELFROMDATE, "pc." + ProductCountryModel.SHOWHOTOFFERLABELUNTILDATE);
        query.append(" + ',' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.NO_MOVER.getValue(), "pc." + ProductCountryModel.SHOWNOMOVERLABELFROMDATE, "pc." + ProductCountryModel.SHOWNOMOVERLABELUNTILDATE);
        query.append(" + ',' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.TOP.getValue(), "pc." + ProductCountryModel.SHOWTOPLABELFROMDATE, "pc." + ProductCountryModel.SHOWTOPLABELUNTILDATE);
        query.append(" + ',' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.HIT.getValue(), "pc." + ProductCountryModel.SHOWHITLABELFROMDATE, "pc." + ProductCountryModel.SHOWHITLABELUNTILDATE);
        query.append(" + ',' + ");
        query.append(chr("123")).append(" + ?").append(PROMOLABEL_ATTRNAME).append(DistFactFinderExportPromotionColumnAttributes.USED.getValue());;
        query.append("    + ',active:' ");
        query.append("    + (CASE ");
        query.append("        WHEN  ( {p.").append(ProductModel.SHOWUSEDLABEL).append("} = 1 ) THEN 'true' ");
        query.append("        ELSE 'false' ");
        query.append("        END) ");
        query.append(" + ").append(chr("125"));
        query.append(" + ',' + ");
        
        query.append(chr("123")).append(" + ?").append(PROMOLABEL_ATTRNAME).append(DistFactFinderExportPromotionColumnAttributes.CALIBRATIONSERVICE.getValue());
        query.append("    + ',active:' ");
        query.append("    + (CASE ");
        query.append("        WHEN  ( {p.").append(ProductModel.CALIBRATIONSERVICE).append("} = 1 AND {p.").append(ProductModel.CALIBRATED).append("} = 1) THEN 'true' ");
        query.append("        ELSE 'false' ");
        query.append("        END) ");
        query.append(" + ").append(chr("125"));
        query.append(" + ',' + ");
        
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.NEW.getValue(), "dsop." + DistSalesOrgProductModel.SHOWNEWLABELFROMDATE, "dsop." + DistSalesOrgProductModel.SHOWNEWLABELUNTILDATE);
        query.append(" + ',' + ");
        appendPromotionLabelSelect(query, DistFactFinderExportPromotionColumnAttributes.BESTSELLER.getValue(), "dsop." + DistSalesOrgProductModel.SHOWBESTSELLERLABELFROMDATE, "dsop." + DistSalesOrgProductModel.SHOWBESTSELLERLABELUNTILDATE);
        query.append(" + ',' + ");
        query.append(chr("123")).append(" + ?").append(PROMOLABEL_ATTRNAME).append(DistFactFinderExportPromotionColumnAttributes.OFFER.getValue());
        query.append("      + ',active:' ");
        query.append("      + (CASE ");
        query.append("          WHEN ( singleMinPrice.specialPrice = 1 ) THEN 'true' ");
        query.append("          ELSE 'false' ");
        query.append("          END) ");
        query.append(" + ").append(chr("125"));
        query.append(" + ']' ");
        
        return query;
    }

    protected StringBuilder appendPromotionLabelSelect(final StringBuilder query, final String code, final String fromField, final String untilField) {
        query.append(chr("123")).append(" + ?").append(PROMOLABEL_ATTRNAME).append(code);
        query.append("    + ',active:' ");
        query.append("    + (CASE ");
        query.append("        WHEN  ( {").append(fromField).append("} <= (?").append(DistFactFinderProductExportParameterProvider.DATE).append(") )");
        query.append("         AND  ( (?").append(DistFactFinderProductExportParameterProvider.DATE).append(") <= {").append(untilField).append("} ) THEN 'true' ");
        query.append("        ELSE 'false' ");
        query.append("        END) ");
        query.append(" + ").append(chr("125"));
        return query;
    }

    /**
     * Replacements are exported in the following format e.g.: <br />
     * {"eolDate":"2013-12-01 00:00:00","buyable":true,"reason":"Name of repl. reason"}
     *
     * CHAR(123) = {
     * CHAR(125) = }
     */
    protected StringBuilder appendReplacementSelect(final StringBuilder query) {
        query.append("({{ ");
        query.append("  SELECT ").append(chr("123"));
        query.append("    + CASE WHEN {dsop.").append(DistSalesOrgProductModel.ENDOFLIFEDATE).append("} IS NOT NULL THEN '\"eolDate\":\"' ");
        query.append("      + ").append(toNvarchar("{dsop.".concat(DistSalesOrgProductModel.ENDOFLIFEDATE).concat("}") , ORACLE_DATE_TIME_PATTERN)).append(" + '\",' ELSE '' END ");
        query.append("    + '\"buyable\":' + CASE WHEN {pRepl.").append(ProductModel.PK).append("} IS NOT NULL ");
        query.append("      AND {pOrig.").append(ProductModel.REPLACEMENTFROMDATE).append("} <= ?").append(DistFactFinderProductExportParameterProvider.DATE).append(" ");
        query.append("      AND ({pOrig.").append(ProductModel.REPLACEMENTUNTILDATE).append("} IS NULL OR {pOrig.").append(ProductModel.REPLACEMENTUNTILDATE).append("} >= ?").append(DistFactFinderProductExportParameterProvider.DATE).append(") ");
        query.append("      AND {dssRepl.").append(DistSalesStatusModel.BUYABLEINSHOP).append("} = 1 THEN 'true' ELSE 'false' END ");
        query.append("    + CASE WHEN {drr.").append(DistReplacementReasonModel.NAME).append(":o} IS NOT NULL THEN ',\"reason\":\"' + {drr.").append(DistReplacementReasonModel.NAME).append(":o} + '\"' ");
        query.append("      WHEN {drr.").append(DistReplacementReasonModel.NAMEERP).append(":o} IS NOT NULL THEN ',\"reason\":\"' + {drr.").append(DistReplacementReasonModel.NAMEERP).append(":o} + '\"' ELSE '' END ");
        query.append("    + ").append(chr("125"));
        query.append("  FROM {").append(ProductModel._TYPECODE).append(" AS pOrig ");
        query.append("    LEFT JOIN ").append(ProductModel._TYPECODE).append(" AS pRepl ON {pOrig.").append(ProductModel.REPLACEMENTPRODUCT).append("} = {pRepl.").append(ProductModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsopRepl ON {pRepl.").append(ProductModel.PK).append("} = {dsopRepl.").append(DistSalesOrgProductModel.PRODUCT).append("} ");
        query.append("        AND {dsopRepl.").append(DistSalesOrgProductModel.SALESORG).append("} = ?DistSalesOrg ");
        query.append("    LEFT JOIN ").append(DistSalesStatusModel._TYPECODE).append(" AS dssRepl ON {dsopRepl.").append(DistSalesOrgProductModel.SALESSTATUS).append("} = {dssRepl.").append(DistSalesStatusModel.PK).append("} ");
        query.append("    LEFT JOIN ").append(DistReplacementReasonModel._TYPECODE).append(" AS drr ON {pOrig.").append(ProductModel.REPLACEMENTREASON).append("} = {drr.").append(DistReplacementReasonModel.PK).append("} ");
        query.append("  } ");
        query.append("  WHERE {pOrig.").append(ProductModel.PK).append("} = {p.").append(ProductModel.PK).append("} ");
        query.append("}}) ");
        return query;
    }

    protected StringBuilder appendVisibilityCondition(final StringBuilder query) {
        query.append(" AND EXISTS ");
        query.append(" ({{");
        query.append("   SELECT {dss.").append(DistSalesStatusModel.CODE).append("} ");
        query.append("     FROM {").append(DistSalesStatusModel._TYPECODE).append(" AS dss} ");
        query.append("    WHERE {dss.").append(DistSalesStatusModel.PK).append("} = {dsop.").append(DistSalesOrgProductModel.SALESSTATUS).append("} ");
        query.append("      AND {dss.").append(DistSalesStatusModel.VISIBLEINSHOP).append("} = 1 ");
        query.append("  }})");
        return query;
    }

    protected StringBuilder appendPunchOutFilterCondition(final StringBuilder query) {
        query.append(" AND ?").append(DistFactFinderProductExportParameterProvider.NUMBER_OF_DELIVERY_COUNTRIES).append(" <> ({{ ");
        query.append("   SELECT COUNT(DISTINCT {pof.").append(DistCOPunchOutFilterModel.COUNTRY).append("}) ");
        query.append("   FROM {").append(DistCOPunchOutFilterModel._TYPECODE).append(" AS pof} ");
        query.append("   WHERE ({pof.").append(DistCOPunchOutFilterModel.PRODUCT).append("} = {p.").append(ProductModel.PK).append("} ");
        query.append("       OR {pof.").append(DistCOPunchOutFilterModel.PRODUCTHIERARCHY).append("} = {p.").append(ProductModel.PRODUCTHIERARCHY).append("}) ");
        query.append("     AND {pof.").append(DistCOPunchOutFilterModel.VALIDFROMDATE).append("} <= ?").append(DistFactFinderProductExportParameterProvider.DATE).append(" ");
        query.append("     AND {pof.").append(DistCOPunchOutFilterModel.VALIDUNTILDATE).append("} >= ?").append(DistFactFinderProductExportParameterProvider.DATE).append(" ");
        query.append("     AND {pof.").append(DistCOPunchOutFilterModel.SALESORG).append("} = ?").append(DistSalesOrgModel._TYPECODE).append(" ");
        query.append("     AND {pof.").append(DistCOPunchOutFilterModel.COUNTRY).append("} IN (?").append(DistFactFinderProductExportParameterProvider.DELIVERY_COUNTRIES).append(") ");
        query.append(" }}) ");
        return query;
    }

    protected StringBuilder appendCatalogVersionCondition(final StringBuilder query) {
        query.append(" AND {p.").append(ProductModel.CATALOGVERSION).append("} = ({{ ");
        query.append("   SELECT {cv.").append(CatalogVersionModel.PK).append("} ");
        query.append("   FROM {").append(CatalogVersionModel._TYPECODE).append(" AS cv ");
        query.append("     JOIN ").append(CatalogModel._TYPECODE).append(" AS c ");
        query.append("       ON {cv.").append(CatalogVersionModel.CATALOG).append("} = {c.").append(CatalogModel.PK).append("} ");
        query.append("   } ");
        query.append("   WHERE {c.").append(CatalogModel.ID).append("} = '").append(DistConstants.Catalog.DISTRELEC_PRODUCT_CATALOG_ID).append("' ");
        query.append("     AND {cv.").append(CatalogVersionModel.VERSION).append("} = '").append(DistConstants.CatalogVersion.ONLINE).append("' ");
        query.append(" }}) ");
        return query;
    }

    protected String chr(String character) {
        return distSqlUtils.chr(character);
    }

    protected String replaceRegexp(String expression, String pattern, String replacement) {
        return distSqlUtils.replaceRegexp(expression, pattern, replacement);
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
}
//@formatter:on
