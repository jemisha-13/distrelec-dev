/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export.webtrekk;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import org.apache.commons.compress.utils.IOUtils;

import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.hybris.ffsearch.export.DistFactFinderExportHelper;
import com.namics.hybris.ffsearch.model.export.DistWTcustomerExportCronJobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.springframework.beans.factory.annotation.Autowired;

//@formatter:off
/**
 * {@code distWTcustomerExportJob}
 * 
 * 
 * @author <a href="wilhelm-patrick.spalinger@distrelec.com">Wilhelm Spalinger</a>, Distrelec
 * @since Distrelec 4.13
 */
public class DistWTcustomerExportJob extends AbstractJobPerformable<DistWTcustomerExportCronJobModel> {
    
    
    private static final String[] HEADER = { "UID", "ERP_CONTACT_ID", "ERP_CUSTOMER_ID", "CUSTOMER_TYPE", "SALES_ORG_ID",
    "COUNTRY", "REGISTRATION_DATE", "ACTIVE", "FIRST_PURCHASE_DATE", "C_NEWSLETTER" };

    private DistFactFinderExportHelper exportHelper;
    private DistCsvTransformationService distCsvTransformationService;

    @Autowired
    private DistSqlUtils distSqlUtils;

    /**
     * Create a new instance of {@code distWTcustomerExportJob}
     */
    public DistWTcustomerExportJob() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(DistWTcustomerExportCronJobModel cronJob) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT  cu.UniqueID AS \"UID\" ,  cu.p_erpcontactid AS erp_contact_id, ");
        query.append("un.p_erpcustomerid AS erp_customer_id, ");
        query.append("ct.Code AS CUSTOMER_TYPE, ");
        query.append("so.p_code AS SALES_ORG_ID, ");
        query.append("co.isocode AS COUNTRY, ");
        query.append(distSqlUtils.toNvarchar("cu.createdTS", "DD-MM-YYYY HH24:MI:SS")).append(" AS REGISTRATION_DATE, ");
        query.append("cu.p_doubleoptinactivated AS ACTIVE, ");
        query.append(distSqlUtils.toNvarchar("mo.p_orderplaceddate", "DD-MM-YYYY HH24:MI:SS")).append(" AS FIRST_PURCHASE_DATE, ");
        query.append("cu.p_newsletter AS C_NEWSLETTER ");
        query.append("FROM users cu  ");
          query.append("JOIN enumerationvalues ct ON  cu.p_customertype = ct.PK ");
          query.append("JOIN usergroups un ON  cu.p_defaultb2bunit = un.PK   ");
          query.append("JOIN distcodelist so ON  so.PK = un.p_salesorg ");
          query.append("LEFT JOIN ADDRESSES ad ON un.P_BILLINGADDRESS=ad.PK ");
          query.append("LEFT JOIN COUNTRIES co on ad.COUNTRYPK=co.PK OR un.P_COUNTRY=co.PK ");
          query.append("LEFT JOIN (SELECT  p_hybriscontactid AS p_hybriscontactid , MIN(p_orderplaceddate) AS p_orderplaceddate FROM movexorder GROUP BY p_hybriscontactid) mo ON  mo.p_hybriscontactid = cu.P_CUSTOMERID ");
        query.append("WHERE ct.Code  != 'GUEST' ");
              query.append("AND cu.UNIQUEID NOT LIKE 'distrelec.testing+%' ");
        query.append("ORDER BY so.p_code ,  cu.p_customertype   ");
        
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(query.toString());
        searchQuery.setResultClassList(Arrays.asList(String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class));

        final List<List<String>> rows = flexibleSearchService.<List<String>> search(searchQuery).getResult();
        final List<String[]> resultList = new ArrayList<String[]>();
        for (final List<String> row : rows) {
            String[] csvLine = new String[10];
            csvLine[0] = row.get(0) != null ? row.get(0) : "";
            csvLine[1] = row.get(1) != null ? row.get(1) : "";
            csvLine[2] = row.get(2) != null ? row.get(2) : "";
            csvLine[3] = row.get(3) != null ? row.get(3) : "";
            csvLine[4] = row.get(4) != null ? row.get(4) : "";
            csvLine[5] = row.get(5) != null ? row.get(5) : "";
            csvLine[6] = row.get(6) != null ? row.get(6) : "";
            csvLine[7] = row.get(7) != null ? row.get(7) : "";
            csvLine[8] = row.get(8) != null ? row.get(8) : "";
            csvLine[9] = row.get(9) != null ? row.get(9) : "";
            resultList.add(csvLine);
        }

        final InputStream exportData = getDistCsvTransformationService().transform(HEADER, resultList);

        try {
            getExportHelper().saveExportData(exportData, cronJob);
            getExportHelper().saveExternal(cronJob, false, false);
        } catch (final Exception e) {
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        } finally {
            IOUtils.closeQuietly(exportData);
        }

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    public DistFactFinderExportHelper getExportHelper() {
        return exportHelper;
    }

    public void setExportHelper(DistFactFinderExportHelper exportHelper) {
        this.exportHelper = exportHelper;
    }

    public DistCsvTransformationService getDistCsvTransformationService() {
        return distCsvTransformationService;
    }

    public void setDistCsvTransformationService(DistCsvTransformationService distCsvTransformationService) {
        this.distCsvTransformationService = distCsvTransformationService;
    }

}
