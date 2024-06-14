package com.namics.distrelec.b2b.core.product.parcellabs.job;

import java.sql.ResultSet;

import com.namics.distrelec.b2b.core.model.jobs.ParcelLabsProductFeedCronjobModel;

public class ParcelLabsFullProductFeedJob extends ParcelLabsProductFeedJob {

    @Override
    protected ResultSet getResults(String salesOrg, ParcelLabsProductFeedCronjobModel cronJob) {
        return parcelLabsProductFeedDao.findProductsForSalesOrg(salesOrg);
    }
    
}
