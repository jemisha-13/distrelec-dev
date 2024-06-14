/*
 * Copyright 2000-2018 Distrelec Group AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.mail.internal;

public class DistMOQOverStockReportDao extends AbstractReportDao {

    private static final String QUERY = new StringBuilder() //
            .append("SELECT dsoCode, dsoName, pCode, MOQ, stock FROM ({{ ") //
            .append("SELECT ") //
            .append("  {dso.code} AS dsoCode, ") //
            .append("  {dso.nameErp[en]} AS dsoName, ") //
            .append("  {p.code} AS pCode, ") //
            .append("  {dsop.orderQuantityMinimum} AS MOQ, ") //
            .append("  sum({sl.available}) AS stock ") //
            .append("FROM { ") //
            .append("  DistSalesOrgProduct AS dsop JOIN ") //
            .append("  DistSalesOrg AS dso ON {dsop.salesOrg}={dso.pk} JOIN ") //
            .append("  DistSalesStatus AS dss ON {dsop.salesStatus}={dss.pk} JOIN ") //
            .append("  Product AS p ON {dsop.product}={p.pk} JOIN ") //
            .append("  StockLevel AS sl ON {sl.productCode}={p.code} JOIN ") //
            .append("  Warehouse as w ON {sl.warehouse}={w.pk} ") //
            .append("} ") //
            .append("WHERE  ") //
            .append("  {dss.code} IN ('40', '41', '42', '43', '44', '45') AND  ") //
            .append("  ( ") //
            .append("    {dso.code} = '7310' AND   {w.code} IN ('7374', '7375', '7371', 'EF11', 'ES11') OR ") //
            .append("    {dso.code} = '7320' AND   {w.code} IN ('EF71', 'ES71', '7371') OR ") //
            .append("    {dso.code} = '7330' AND   {w.code} IN ('EF71', 'ES71', '7371', 'EF31', 'ES31') OR ") //
            .append("    {dso.code} = '7350' AND   {w.code} IN ('EF51', 'ES51', '7371') OR ") //
            .append("    {dso.code} = '7640' AND   {w.code} IN ('7641', 'EF71', 'ES71', '7371') OR ") //
            .append("    {dso.code} = '7650' AND   {w.code} IN ('EF71', 'ES71', '7371') OR ") //
            .append("    {dso.code} = '7660' AND   {w.code} IN ('EF71', 'ES71', '7371') OR ") //
            .append("    {dso.code} = '7670' AND   {w.code} IN ('EF71', 'ES71', '7371') OR ") //
            .append("    {dso.code} = '7680' AND   {w.code} IN ('EF71', 'ES71', '7371') OR ") //
            .append("    {dso.code} = '7790' AND   {w.code} IN ('EF71', 'ES71', '7371', '7791') OR ") //
            .append("    {dso.code} = '7800' AND   {w.code} IN ('EF71', 'ES71', '7371') OR ") //
            .append("    {dso.code} = '7801' AND   {w.code} IN ('EF71', 'ES71', '7371') OR ") //
            .append("    {dso.code} = '7810' AND   {w.code} IN ('EF71', 'ES71', '7371', '7811') OR ") //
            .append("    {dso.code} = '7820' AND   {w.code} IN ('EF71', 'ES71', '7371') ") //
            .append("   ) ") //
            .append("GROUP BY {dso.code}, {dso.nameErp[en]}, {p.code}, {dsop.orderQuantityMinimum} ") //
            .append("}}) INNERTABLE WHERE MOQ > stock ") //
            .append("AND stock > 0") //
            .toString();

    @Override
    protected String getQuery() {
        return QUERY;
    }

    @Override
    protected int getResultCount() {
        return 5;
    }

}
