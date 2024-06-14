/*
 * Copyright 2000-2016 Distrelec AG. All rights reserved.
 */

package com.namics.hybris.ffsearch.export;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.utils.IOUtils;

import com.namics.distrelec.b2b.core.inout.export.DistCsvTransformationService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeature;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductFeatures;
import com.namics.distrelec.b2b.core.service.product.model.cassandra.CProductInformation;
import com.namics.hybris.exports.model.export.DistProductInfoExportCronJobModel;

import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code ExportProductInfoJob}
 * 
 * 
 * @author <a href="wilhelm-patrick.spalinger@distrelec.com">Wilhelm Spalinger</a>, Distrelec
 * @since Distrelec 4.13
 */
public class ExportProductInfoJob extends AbstractJobPerformable<DistProductInfoExportCronJobModel> {

    private static final String ALL_VISIBLE_PRODUCTS = "SELECT DISTINCT {p.code} AS P_CODE, {ct.name[lang_param]} AS C_ORIGIN, {rohs.nameErp[lang_param]} AS P_ROHS, {p.paperCatalogPageNumber[lang_param]} AS P_PCPN FROM {Product AS p JOIN DistSalesOrgProduct AS dsop ON {dsop.product}={p.pk} JOIN DistSalesStatus AS dss ON {dss.pk}={dsop.salesStatus} LEFT JOIN Country AS ct ON {p.countryOfOrigin}={ct.pk} LEFT JOIN DistRestrictionOfHazardousSubstances as rohs ON {p.rohs} = {rohs.pk}} WHERE {dss.visibleInShop}=1 AND {dsop.salesOrg}=(?DistSalesOrg)";

    private static final Logger LOG = LoggerFactory.getLogger(ExportProductInfoJob.class);

    private DistFactFinderExportHelper exportHelper;
    private DistCsvTransformationService distCsvTransformationService;
    private CommonI18NService commonI18NService;

    private static final String[] HEADER = { "productCode", "articleInformation", "environmentalInformation", "countryOfOrigin", "articleNotes",
            "familyInformation" };

    /**
     * Create a new instance of {@code TestBeanJob}
     */
    public ExportProductInfoJob() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable#perform(de.hybris.platform.cronjob.model.CronJobModel)
     */
    @Override
    public PerformResult perform(DistProductInfoExportCronJobModel cronJob) {

        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(ALL_VISIBLE_PRODUCTS.replace("lang_param", cronJob.getLanguage().getIsocode()));
        searchQuery.setResultClassList(Arrays.asList(String.class, String.class, String.class, String.class));
        searchQuery.addQueryParameter(DistSalesOrgModel._TYPECODE, cronJob.getCmsSite().getSalesOrg());

        final List<List<String>> rows = flexibleSearchService.<List<String>> search(searchQuery).getResult();
        final List<String[]> resultList = new ArrayList<String[]>();
        for (final List<String> row : rows) {
            final CProductInformation productInfo = new CProductInformation();
            productInfo.setProductCode(row.get(0));
            productInfo.setCountryOfOrigin(row.get(1) != null ? row.get(1) : "");
            productInfo.setEnvironmentalInformation(row.get(2) != null ? row.get(2) : "");
            productInfo.setArticleInformation(row.get(3) != null ? row.get(3) : "");
            resultList.add(productInfo.toCsvRow());
        }

        final InputStream exportData = getDistCsvTransformationService().transform(HEADER, resultList);

        try {
            getExportHelper().saveExportData(exportData, cronJob);
            getExportHelper().saveExternal(cronJob, false, false);
        } catch (final Exception e) {
            LOG.error("Unable to save data", e);
            return new PerformResult(CronJobResult.FAILURE, CronJobStatus.FINISHED);
        } finally {
            IOUtils.closeQuietly(exportData);
        }

        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void assignFeatures(final CProductFeatures cfeatures, final CProductInformation productInfo) {
        if (cfeatures != null) {
            for (final CProductFeature cfeature : cfeatures.getFeatures()) {
                if (cfeature.getQualifier().equalsIgnoreCase("class-root.5322")) {
                    if (productInfo.getFamilyInformation().length() > 0) {
                        productInfo.getFamilyInformation().append(" | ");
                    }
                    productInfo.getFamilyInformation().append(cfeature.getValue());
                } else if (cfeature.getQualifier().equalsIgnoreCase("class-root.5334")) {
                    if (productInfo.getArticleNotes().length() > 0) {
                        productInfo.getArticleNotes().append(" | ");
                    }
                    productInfo.getArticleNotes().append(cfeature.getValue());
                }
            }
        }
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

    public CommonI18NService getCommonI18NService() {
        return commonI18NService;
    }

    public void setCommonI18NService(CommonI18NService commonI18NService) {
        this.commonI18NService = commonI18NService;
    }
}
