package com.namics.hybris.ffsearch.export.query;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.model.DistCOPunchOutFilterModel;
import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.hybris.ffsearch.export.DistFactFinderPkQueryCreator;
import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.product.ProductModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDistFactFinderPkQueryCreator implements DistFactFinderPkQueryCreator {

    private final Logger LOG = LoggerFactory.getLogger(DefaultDistFactFinderPkQueryCreator.class);

    @Override
    public String createPkQuery(final CMSSiteModel cmsSiteModel) {
        CountryModel countryModel = cmsSiteModel.getCountry();
        DistSalesOrgModel salesOrgModel = cmsSiteModel.getSalesOrg();

        StringBuilder query = new StringBuilder();

        // select
        query.append("SELECT ");
        query.append(" {p.").append(ProductModel.PK).append("} ").append(" AS \"").append(ProductModel.PK).append("\" ");

        // from
        query.append(" FROM ");
        query.append(" { ");
        query.append(ProductModel._TYPECODE).append(" AS p");
        query.append(" } ");

        // where
        query.append(" WHERE ");

        query.append("{p.").append(ProductModel.PIMID).append("} IS NOT NULL");
        query.append(" AND {p.").append(ProductModel.CODE).append("} IS NOT NULL");
        query.append(" AND ({p.").append(ProductModel.EXCLUDE).append("} IS NULL OR {p.").append(ProductModel.EXCLUDE).append("} != 1)");
        appendVisibleInShopCondition(query, countryModel, salesOrgModel);
        appendApprovedCondition(query);
        appendExistsPriceCondition(query);
        appendCatalogVersionCondition(query);

        String flexSearch = query.toString();
        if (LOG.isDebugEnabled()) {
            LOG.debug(flexSearch);
        }

        return flexSearch;
    }

    protected StringBuilder appendVisibleInShopCondition(final StringBuilder query, final CountryModel countryModel,
            final DistSalesOrgModel salesOrgModel) {
        query.append(" AND EXISTS ({{");
        query.append("   SELECT 1");
        query.append("   FROM {").append(DistSalesOrgProductModel._TYPECODE).append(" AS dsop}");
        query.append("   WHERE {dsop.").append(DistSalesOrgProductModel.PRODUCT).append("}={p.").append(ProductModel.PK).append("}");
        query.append("     AND {dsop.").append(DistSalesOrgProductModel.SALESORG).append("} IN (").append("'").append(salesOrgModel.getPk()).append("')");
        query.append("     AND EXISTS ({{");
        query.append("       SELECT 1");
        query.append("       FROM {").append(DistSalesStatusModel._TYPECODE).append(" AS dss}");
        query.append("       WHERE {dsop.").append(DistSalesOrgProductModel.SALESSTATUS).append("}={dss.").append(DistSalesStatusModel.PK).append("}");
        query.append("         AND {dss.").append(DistSalesStatusModel.VISIBLEINSHOP).append("} = 1");
        query.append("     }})");
        query.append("     AND NOT EXISTS ({{");
        query.append("       SELECT 1");
        query.append("       FROM {").append(DistCOPunchOutFilterModel._TYPECODE).append(" AS pof} ");
        query.append("       WHERE {pof.").append(DistCOPunchOutFilterModel.PRODUCT).append("} = {p.").append(ProductModel.PK).append("}");
        query.append("         AND {pof.").append(DistCOPunchOutFilterModel.VALIDFROMDATE).append("} <= ?").append(DATE).append(" ");
        query.append("         AND {pof.").append(DistCOPunchOutFilterModel.VALIDUNTILDATE).append("} >= ?").append(DATE).append(" ");
        query.append("         AND {pof.").append(DistCOPunchOutFilterModel.SALESORG).append("} = {dsop.").append(DistSalesOrgProductModel.SALESORG).append("}");
        query.append("         AND {pof.").append(DistCOPunchOutFilterModel.COUNTRY).append("} = '").append(countryModel.getPk()).append("' ");
        query.append("     }})");
        query.append("     AND NOT EXISTS ({{");
        query.append("       SELECT 1");
        query.append("       FROM {").append(DistCOPunchOutFilterModel._TYPECODE).append(" AS pof} ");
        query.append("       WHERE {pof.").append(DistCOPunchOutFilterModel.PRODUCTHIERARCHY).append("} = {p.").append(ProductModel.PRODUCTHIERARCHY).append("}");
        query.append("         AND {pof.").append(DistCOPunchOutFilterModel.VALIDFROMDATE).append("} <= ?").append(DATE).append(" ");
        query.append("         AND {pof.").append(DistCOPunchOutFilterModel.VALIDUNTILDATE).append("} >= ?").append(DATE).append(" ");
        query.append("         AND {pof.").append(DistCOPunchOutFilterModel.SALESORG).append("} = {dsop.").append(DistSalesOrgProductModel.SALESORG).append("}");
        query.append("         AND {pof.").append(DistCOPunchOutFilterModel.COUNTRY).append("} = '").append(countryModel.getPk()).append("' ");
        query.append("      }})");
        query.append(" }})");
        return query;
    }

    protected StringBuilder appendApprovedCondition(final StringBuilder query) {
        query.append(" AND EXISTS ({{");
        query.append("   SELECT 1");
        query.append("   FROM {").append(ArticleApprovalStatus._TYPECODE).append(" AS aas}");
        query.append("   WHERE {aas.code} = '").append(ArticleApprovalStatus.APPROVED.getCode()).append("' ");
        query.append("     AND {p.").append(ProductModel.APPROVALSTATUS).append("} = {aas.pk} ");
        query.append(" }})");
        return query;
    }

    protected StringBuilder appendExistsPriceCondition(final StringBuilder query) {
        query.append(" AND EXISTS ({{");
        query.append("   SELECT 1");
        query.append("   FROM {").append(DistPriceRowModel._TYPECODE).append(" AS pr");
        query.append("      JOIN ").append(CMSSiteModel._TYPECODE).append(" AS cms ");
        query.append("         ON {pr.").append(DistPriceRowModel.UG).append("} = {cms.").append(CMSSiteModel.USERPRICEGROUP).append("}");
        query.append("   }");
        query.append("   WHERE {pr.").append(DistPriceRowModel.PRODUCT).append("}={p.").append(ProductModel.PK).append("}");
        query.append("      AND ({pr.").append(DistPriceRowModel.STARTTIME).append("} IS NULL OR {pr.").append(DistPriceRowModel.STARTTIME).append("} <= (?").append(DATE).append(")) ");
        query.append("      AND ({pr.").append(DistPriceRowModel.ENDTIME).append("} IS NULL OR {pr.").append(DistPriceRowModel.ENDTIME).append("} >= (?").append(DATE).append(")) ");
        query.append("      AND {cms.").append(CMSSiteModel.PK).append("} = (?").append(CMSSITE).append(")");
        query.append(" }})");
        return query;
    }

    protected StringBuilder appendCatalogVersionCondition(final StringBuilder query) {
        query.append(" AND EXISTS ({{ ");
        query.append("   SELECT 1 ");
        query.append("   FROM {").append(CatalogVersionModel._TYPECODE).append(" AS cv}");
        query.append("   WHERE {p.").append(ProductModel.CATALOGVERSION).append("} = {cv.").append(CatalogVersionModel.PK).append("}");
        query.append("     AND {cv.").append(CatalogVersionModel.VERSION).append("} = '").append(DistConstants.CatalogVersion.ONLINE).append("' ");
        query.append("     AND EXISTS ({{");
        query.append("       SELECT 1");
        query.append("       FROM {").append(CatalogModel._TYPECODE).append(" AS c}");
        query.append("       WHERE {cv.").append(CatalogVersionModel.CATALOG).append("} = {c.").append(CatalogModel.PK).append("}");
        query.append("         AND {c.").append(CatalogModel.ID).append("} = '").append(DistConstants.Catalog.DISTRELEC_PRODUCT_CATALOG_ID).append("' ");
        query.append("     }}) ");
        query.append(" }}) ");
        return query;
    }
}
