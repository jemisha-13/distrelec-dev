/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class DistProductsPerWebshopDao extends AbstractReportDao {

    private static final Logger LOG = LogManager.getLogger(DistProductsPerWebshopDao.class);

    @Autowired
    private DistSqlUtils distSqlUtils;

    @Override
    public String getQuery() {
        String QUERY = new StringBuilder() //
            .append("SELECT ") //
            .append("  {dso.code} as Sales_Org_Code, ") //
            .append("  {dso.nameErp[en]} AS Sales_Org_Name, ") //
            .append("  {c.name[en]} AS Top_Level_Category, ") //
            .append("  {dss.buyableInShop} AS Buyable, ") //
            .append("  count(*) AS Amount ") //
            .append("FROM {distsalesorgproduct AS dsp} , ") //
            .append("  {DistSalesStatus AS dss}, ") //
            .append("  {DistSalesOrg AS dso}, ") //
            .append("  {Product AS p}, ") //
            .append("  {Category as c} ") //
            .append("WHERE ") //
            .append("  {dsp.product} = {p.PK} AND ") //
            .append("  {dsp.salesstatus} = {dss.PK} AND ") //
            .append("  {dsp.salesorg} = {dso.PK} AND ") //
            .append("  {p.pimId} IS NOT NULL AND ") //
            .append("  {c.code} = ").append(substring(toNvarchar("{p.additionalPaths}") , "2", "CASE ".concat(locate(toNvarchar("{p.additionalPaths}"), "/", 1, 1)).concat(" WHEN 0 THEN length(").concat(toNvarchar("{p.additionalPaths}")).concat(") ELSE ".concat(locate(toNvarchar("{p.additionalPaths}"), "/", 1, 1)).concat(" END -2")))).append(") ") //
            .append("GROUP BY ") //
            .append("  {dso.code}, ")
            .append("  {dso.nameErp[en]}, ") //
            .append("  {dss.buyableInShop}, ") //
            .append("  {c.name[en]} ") //
            .append("ORDER BY ") //
            .append("  {dso.nameErp[en]}, ") //
            .append("  {c.name[en]}, ") //
            .append("  {dss.buyableInShop}").toString();

        return QUERY;
    }

    @Override
    protected int getResultCount() {
        return 5;
    }

    protected String locate(String haystack, String needle, int startPos, int occurrences) {
        return distSqlUtils.locate(haystack, needle, startPos, occurrences);
    }

    protected String substring(String expression, String startPos, String length) {
        return distSqlUtils.substring(expression, startPos, length);
    }

    protected String toNvarchar(String character) {
        return distSqlUtils.toNvarchar(character);
    }
}
