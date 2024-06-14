package com.namics.distrelec.b2b.core.product.parcellabs.dao;

import java.sql.ResultSet;
import java.util.Date;

public interface ParcelLabsProductFeedDao {
    /**
     * Used for full export (full == all products).
     */
    ResultSet findProductsForSalesOrg(String salesOrg);

    /**
     * Used for delta export (delta == only the products that were changed (last modified date) since the last delta export).
     */
    ResultSet findChangedProductsForSalesOrg(String salesOrg, Date previousExportDate);
}
