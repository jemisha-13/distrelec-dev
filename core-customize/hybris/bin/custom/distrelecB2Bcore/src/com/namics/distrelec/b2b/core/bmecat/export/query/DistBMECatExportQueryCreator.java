
package com.namics.distrelec.b2b.core.bmecat.export.query;

/**
 * 
 * 
 * @author Abhinay Jadhav, Datwyler IT
 * @since 10-Dec-2017
 * 
 */
@SuppressWarnings({ "PMD" })
public class DistBMECatExportQueryCreator implements DistBMECatQueryCreator {

    private static final int QUERY_LENGTH = 25000;

    /**
     * Query to fetch products from specified channel
     * 
     */
    @Override
    public String createQuery(boolean isDefaultLanguageDifferent) {
        final StringBuilder query = new StringBuilder(QUERY_LENGTH);
        query.append("SELECT {p.code} AS \"Code\"");
        query.append("  ,{p.seoAccessory } AS \"Accessory\"");
        query.append("  ,{p.SvhcReviewDate} AS \"ReachDate\"");
        query.append("  ,{p.seoKeywordMain} AS \"Keywords\"");
        query.append("  ,({{ SELECT {ddGP.code} FROM {DistDangerousGoodsProfile AS ddGP} where {p.dangerousGoodsProfile}={ddGP.pk} }}) AS \"DangerousGroup\"");
        query.append("  ,({{ SELECT {dat.code}");
        query.append("       FROM {DistDownloadMedia! AS ddm");
        query.append("         JOIN DistAssetType! AS dat ON {ddm.assetType} ={dat.pk}}");
        query.append("       WHERE {ddm.pk}=ddmss.ddm_pk");
        query.append("  }}) AS \"AssetType\"");
        query.append("  ,({{ SELECT {ddt.code}");
        query.append("       FROM {DistDownloadMedia! AS ddm");
        query.append("         JOIN DistDocumentType! AS ddt ON {ddm.documentType} ={ddt.pk}}");
        query.append("       WHERE {ddm.pk}=ddmss.ddm_pk");
        query.append("  }}) AS \"DocumentType\"");
        query.append("  ,({{ SELECT {ddm.internalurl}");
        query.append("       FROM {DistDownloadMedia! AS ddm}");
        query.append("       WHERE {ddm.pk}=ddmss.ddm_pk");
        query.append("  }}) AS \"DocumentURL\"");
        query.append("  ,({{ SELECT {ddm.description}");
        query.append("       FROM {DistDownloadMedia! AS ddm}");
        query.append("       WHERE {ddm.pk}=ddmss.ddm_pk");
        query.append("  }}) AS \"DocumentDescription\"");
        query.append("  ,ddmss.lang_isocode AS \"Language\"");
        query.append(" FROM {");
        query.append("   Product! AS p");
        query.append(" },({{ ");
        appendDistDownloadMediaSubSelect(query, isDefaultLanguageDifferent);
        query.append("}}) ddmss");
        query.append(" WHERE EXISTS({{");
        query.append("    SELECT 1");
        query.append("    FROM {DistSalesOrgProduct! AS dsop");
        query.append("      JOIN DistSalesStatus! AS dss on {dss.pk}={dsop.salesstatus}}");
        query.append("    WHERE {p.pk}={dsop.product}");
        query.append("      AND {dss.buyableInShop}=1");
        query.append("      AND {dsop.salesOrg}=?").append(SALESORG_PARAM);
        query.append("    }})");
        query.append("   AND {p.pk}=ddmss.p_pk");

        final String flexiSearchQuery = query.toString();
        return flexiSearchQuery;
    }

    protected StringBuilder appendDistDownloadMediaSubSelect(final StringBuilder query, final boolean isDefaultLanguageDifferent) {
        query.append(" SELECT {p.pk} p_pk");
        extractDistDownloadMedia(query, "min({ddm.pk})", isDefaultLanguageDifferent).append(" ddm_pk");
        extractDistDownloadMedia(query, "distinct {lang.isocode}", isDefaultLanguageDifferent).append(" lang_isocode");
        query.append(" FROM {Product! AS p}");
        return query;
    }

    protected StringBuilder extractDistDownloadMedia(final StringBuilder query, final String selection, final boolean isDefaultLanguageDifferent) {
        query.append("  , CASE");
        appendDistDownloadMediaWhenExpression(query, selection, "{ddm.description}=?".concat(DATASHEET_PARAM).concat(" AND {lang.isocode}=?").concat(LANG_ISOCODE_PARAM));
        if (isDefaultLanguageDifferent) {
            appendDistDownloadMediaWhenExpression(query, selection, "{ddm.description}=?".concat(DATASHEET_PARAM).concat(" AND {lang.isocode}=?").concat(DEFAULT_LANG_ISOCODE_PARAM));
        }
        appendDistDownloadMediaWhenExpression(query, selection, "{lang.isocode}=?".concat(LANG_ISOCODE_PARAM));
        if (isDefaultLanguageDifferent) {
            appendDistDownloadMediaWhenExpression(query, selection, "{lang.isocode}=?".concat(DEFAULT_LANG_ISOCODE_PARAM));
        }
        query.append("    ELSE null END ");
        return query;
    }

    protected StringBuilder appendDistDownloadMediaWhenExpression(final StringBuilder query, final String selection, final String condition) {
        query.append(" WHEN EXISTS({{");
        query.append("   SELECT 1");
        query.append("   FROM {DistDownloadMedia2Product! AS ddm2p");
        query.append("     JOIN DistDownloadMedia! AS ddm on {ddm2p.source}={ddm.pk}");
        query.append("     JOIN DistDownloadMedia2Language! AS ddm2l ON {ddm.pk} ={ddm2l.source}");
        query.append("     JOIN Language! AS lang ON {ddm2l.target}={lang.pk}}");
        query.append("   WHERE {p.pk} = {ddm2p.target} AND ").append(condition).append("}})");
        query.append(" THEN ({{");
        query.append("   SELECT ").append(selection);
        query.append("   FROM {DistDownloadMedia2Product! AS ddm2p");
        query.append("     JOIN DistDownloadMedia! AS ddm on {ddm2p.source}={ddm.pk}");
        query.append("     JOIN DistDownloadMedia2Language! AS ddm2l ON {ddm.pk} ={ddm2l.source}");
        query.append("     JOIN Language! AS lang ON {ddm2l.target}={lang.pk}}");
        query.append("   WHERE {p.pk} = {ddm2p.target} AND ").append(condition).append("}})");
        return query;
    }
}
//@formatter:on
