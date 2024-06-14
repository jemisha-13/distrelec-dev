/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.cleanup;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import org.apache.http.client.utils.DateUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.DistPriceRowModel;
import com.namics.distrelec.b2b.core.model.jobs.DuplicatePriceCleanUpCronJobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * {@code DuplicatePriceCleanUpJob}
 * 
 *
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 4.13
 */
public class DuplicatePriceCleanUpJob extends AbstractJobPerformable<DuplicatePriceCleanUpCronJobModel> {

    private static final Logger LOG = Logger.getLogger(DuplicatePriceCleanUpJob.class);

    @Autowired
    private DistSqlUtils distSqlUtils;

    protected String getEndTimeStr() {
        return distSqlUtils.toTimestamp("9999-12-31 23:59:59", "YYYY-MM-DD HH24:MI:SS");
    }

   protected String getCQuery() {
        return "SELECT {" + DistPriceRowModel.PRODUCT + "}, {" + DistPriceRowModel.MINQTD + "}, {"
               + DistPriceRowModel.ERPPRICECONDITIONTYPE + "}, {" + DistPriceRowModel.UG + "},{" + DistPriceRowModel.UNIT + "},{" + DistPriceRowModel.CURRENCY + "}, COUNT({" + DistPriceRowModel.PK + "}) AS NR FROM {"
               + DistPriceRowModel._TYPECODE + "} WHERE  {" + DistPriceRowModel.STARTTIME + "} < " + distSqlUtils.now() +  "GROUP BY {" + DistPriceRowModel.PRODUCT + "}, {" + DistPriceRowModel.MINQTD + "}, {" + DistPriceRowModel.UNIT + "},{" + DistPriceRowModel.CURRENCY + "}, {" + DistPriceRowModel.ERPPRICECONDITIONTYPE
               + "}, {" + DistPriceRowModel.UG + "} HAVING COUNT({" + DistPriceRowModel.PK + "}) > 1";
   }

    protected String getPrQuery() {
        return "SELECT {" + DistPriceRowModel.PK + "} FROM {" + DistPriceRowModel._TYPECODE + "} WHERE {"
                + DistPriceRowModel.PRODUCT + "}=?product AND {" + DistPriceRowModel.UG + "}=?ug AND {" + DistPriceRowModel.ERPPRICECONDITIONTYPE + "}=?erpPCT "
                +"AND {" + DistPriceRowModel.CURRENCY + "}=?currency "
                +"AND {" + DistPriceRowModel.UNIT + "}=?unit "
                + "AND {" + DistPriceRowModel.MINQTD + "}=?scale AND {"
                + DistPriceRowModel.STARTTIME + "} < " + distSqlUtils.now() + " ORDER BY {" + DistPriceRowModel.STARTTIME + "} DESC";
    }


    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(final DuplicatePriceCleanUpCronJobModel cronJob) {
        final long startTime = System.nanoTime();
        LOG.info("Starting duplicate price cleanup Job.");
        try {
            performJob(cronJob);
        } catch (final InterruptedException e) {
            LOG.error("Could not sleep after removing item.", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.ABORTED);
        }
        final long endTime = System.nanoTime();
        LOG.info("Finished duplicate price cleanup Job in " + (int) ((endTime - startTime) / 1e9) + " seconds.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    protected void performJob(final DuplicatePriceCleanUpCronJobModel cronJob) throws InterruptedException {
        if (cronJob == null) {
            throw new InterruptedException("Null value of cronJob");
        }

        final int maxItemsToDelete = cronJob.getMaxItemsToDelete() != null && cronJob.getMaxItemsToDelete().intValue() > 0 ? cronJob.getMaxItemsToDelete()
                .intValue() : Integer.MAX_VALUE;

        final FlexibleSearchQuery c_fsq = new FlexibleSearchQuery(getCQuery());
        c_fsq.setResultClassList(Arrays.asList(String.class, Integer.class, String.class, String.class, String.class, String.class, Integer.class));

        final FlexibleSearchQuery pr_fsq = new FlexibleSearchQuery(getPrQuery());

        boolean error = false;
        final Date endDate = DateUtils.parseDate("9999/12/31T23:59:59",  new String[] { "yyyy/MM/dd'T'HH:mm:ss" });
        // Statistics
        int preserved = 0;
        int deleted = 0;
        int failed = 0;

        try {
            final SearchResult<List<Object>> searchResult = flexibleSearchService.<List<Object>> search(c_fsq);
            final List<List<Object>> rows = searchResult.getResult();
            for (final List<Object> row : rows) {
                if (deleted >= maxItemsToDelete) {
                    break;
                }

                try {
                    // Retrieve data from the current row
                    final String product = (String) row.get(0);
                    final Integer scale = (Integer) row.get(1);
                    final String erpPriceConditionType = (String) row.get(2);
                    final String ug = (String) row.get(3);
                    final String unit = (String) row.get(4);
                    final String currency = (String) row.get(5);

                    // Update the query parameters
                    pr_fsq.addQueryParameter("product", product);
                    pr_fsq.addQueryParameter("ug", ug);
                    pr_fsq.addQueryParameter("erpPCT", erpPriceConditionType);
                    pr_fsq.addQueryParameter("scale", scale);
                    pr_fsq.addQueryParameter("unit", unit);
                    pr_fsq.addQueryParameter("currency", currency);

                    // Run the query
                    final List<DistPriceRowModel> prices = flexibleSearchService.<DistPriceRowModel> search(pr_fsq).getResult();
                    if (prices == null || prices.size() <= 1) {
                        continue;
                    }
                    //filter prices with year ending 9999
                    final List<DistPriceRowModel> endofYearprices =prices.stream().filter(price -> price.getEndTime().equals(endDate)).sorted(Comparator.comparing(DistPriceRowModel::getStartTime).reversed()).collect(Collectors.toList());
                    //filter expired prices 
                    final List<DistPriceRowModel> expiredPrices =prices.stream().filter(price -> price.getEndTime().before(new Date())).collect(Collectors.toList());
                    
                    // If no price found or the number of prices is less than 2, we just ignore.
                    if(null!=endofYearprices && endofYearprices.size()>0)
                    {   
                        boolean first = true;
    
                        for (final DistPriceRowModel price : endofYearprices) {
                            // We keep only the first price since it has the latest start date, so we ignore just it.
                            if (first) {
                                first = false;
                                preserved++;
                            } else {
                                modelService.remove(price);
                                deleted++;
                            }
                        }
                    }
                    //remove expired prices 
                    if(null!=expiredPrices && expiredPrices.size()>0) {
                        for (final DistPriceRowModel expiredPrice : expiredPrices) {
                            // We keep only the first price since it has the latest start date, so we ignore just it.
                            
                                modelService.remove(expiredPrice);
                                deleted++;
                            
                        }
                    }
                } catch (final Exception e) {
                    LOG.error("Error occurs while processing prices : " + row + "\n    --> " + e.getMessage());
                    failed++;
                }
            }
        } catch (final Exception ex) {
            LOG.error("ERROR --> " + ex.getMessage(), ex);
            error = true;
        }

        if (error) {
            LOG.info("The program ended because of an error");
        }

        final StringBuilder message = new StringBuilder("\nEnd of program with following statistics:") //
                .append("\n * Preserved: " + preserved) //
                .append("\n * Deleted: " + deleted) //
                .append("\n * Failed: " + failed);
        LOG.info(message.toString());
    }
}
