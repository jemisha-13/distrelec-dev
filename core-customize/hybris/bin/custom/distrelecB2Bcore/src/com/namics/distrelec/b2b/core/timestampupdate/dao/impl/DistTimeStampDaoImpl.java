package com.namics.distrelec.b2b.core.timestampupdate.dao.impl;

import com.google.common.collect.Lists;
import com.namics.distrelec.b2b.core.inout.erp.AvailabilityService;
import com.namics.distrelec.b2b.core.model.DistSalesOrgModel;
import com.namics.distrelec.b2b.core.model.DistSalesOrgProductModel;
import com.namics.distrelec.b2b.core.model.DistSalesStatusModel;
import com.namics.distrelec.b2b.core.service.product.DistProductService;
import com.namics.distrelec.b2b.core.service.product.model.ProductAvailabilityExtModel;
import com.namics.distrelec.b2b.core.service.site.DistrelecCMSSiteService;
import com.namics.distrelec.b2b.core.timestampupdate.dao.DistTimeStampDao;
import com.namics.distrelec.b2b.core.util.DistSqlUtils;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.namics.distrelec.b2b.core.constants.DistConfigConstants.CronJob.DistUpdateTimeStamp.ATTRIBUTE_UPDATE_FIRST_APPEARANCE_DATE_PARTITION_SIZE;

public class DistTimeStampDaoImpl implements DistTimeStampDao {

    private static final Logger LOG = LoggerFactory.getLogger(DistTimeStampDaoImpl.class);
    private static final int DEFAULT_BATCH_SIZE = 100;

    @Autowired
    private FlexibleSearchService flexibleSearchService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private DistProductService productService;
    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private DistrelecCMSSiteService distrelecCMSSiteService;
    @Autowired
    private ConfigurationService configurationService;
    @Autowired
    private DistSqlUtils distSqlUtils;

    /**
     * TimeStamp update Incremental Query
     */

    private static final String PRODUCT_UPDATE_FIRST_APPEARANCE_DATE_QUERY = "SELECT distinct {p." + ProductModel.PK + "} FROM { "
            + DistSalesStatusModel._TYPECODE + " as dss JOIN " + DistSalesOrgProductModel._TYPECODE + " as " + " dsop ON {dsop."
            + DistSalesOrgProductModel.SALESSTATUS + " } ={dss." + DistSalesStatusModel.PK + " } JOIN  " + ProductModel._TYPECODE + " as p on {p."
            + ProductModel.PK + " }={dsop." + DistSalesOrgProductModel.PRODUCT + " }} " + "where {p." + ProductModel.PIMID + " } is not null and {dss."
            + DistSalesStatusModel.VISIBLEINSHOP + "}=1 and {p." + ProductModel.NAME + "[en]} is not null and {p." + ProductModel.FIRSTAPPEARANCEDATE
            + "} is null and {dsop."+ DistSalesOrgProductModel.SALESSTATUS + "} is not null";

    private static final String PRODUCTS_MISSING_NEW_LABEL = "select {dsop.pk} from {Product as p join DistSalesOrgProduct as dsop on {dsop.product} = {p.pk} join DistSalesStatus as dss on {dsop.salesStatus} = {dss.pk} } where {dsop.showNewLabelFromDate} is null and {p.firstAppearanceDate} is not null and {dss.visibleInShop} = 1";

    /**
     * Product PIMID Query
     */
    private static final String PRODUCT_BY_PIMID_QUERY = "SELECT distinct{" + ProductModel.PK + "} FROM {" + ProductModel._TYPECODE + "} WHERE {"
            + ProductModel.PIMID + "} = (?" + ProductModel.PIMID + ")";

    /**
     * Product PIMID Query
     */
    protected String getPimExportTimestampQuery() {
        return "SELECT distinct {p." + ProductModel.PIMID + "}, " + distSqlUtils.toNvarchar("{p." + ProductModel.FIRSTAPPEARANCEDATE
                + "}", "YYYY-MM-DD HH24:MI:SS") + " FROM { " + DistSalesStatusModel._TYPECODE + " as dss JOIN " + DistSalesOrgProductModel._TYPECODE + " as "
                + " dsop ON {dsop." + DistSalesOrgProductModel.SALESSTATUS + " } ={dss." + DistSalesStatusModel.PK + " } JOIN  " + ProductModel._TYPECODE
                + " as p on {p." + ProductModel.PK + " }={dsop." + DistSalesOrgProductModel.PRODUCT + " }} " + "where {p." + ProductModel.PIMID
                + " } is not null and {dss." + DistSalesStatusModel.VISIBLEINSHOP + "}=1 and {p." + ProductModel.NAME + "[en]} is not null and {p."
                + ProductModel.FIRSTAPPEARANCEDATE + "} is not null";
    }

    @Override
    public boolean updateProductFirstAppearanceDate() {
        int count = 0;
        boolean success = true;
        try {
            updateFirstAppearanceField();

            final FlexibleSearchQuery query = new FlexibleSearchQuery(PRODUCTS_MISSING_NEW_LABEL);
            final SearchResult<DistSalesOrgProductModel> missing_new_labels = getFlexibleSearchService().search(query);

            for (DistSalesOrgProductModel distproduct : missing_new_labels.getResult()) {
                DistSalesOrgModel salesOrg = distproduct.getSalesOrg();

                if (CollectionUtils.isNotEmpty(distproduct.getSalesOrg().getCmsSites())) {
                    distrelecCMSSiteService.setCurrentSite(distproduct.getSalesOrg().getCmsSites().iterator().next());

                    List<ProductAvailabilityExtModel> availabilityList = availabilityService.getAvailability(Arrays.asList(distproduct.getProduct().getCode()),
                                                                                                             false);
                    
                    if (CollectionUtils.isNotEmpty(availabilityList)) {
                        ProductAvailabilityExtModel availability = availabilityList.get(0);

                        if (availability.getStockLevelTotal() > 0) {
                            LocalDate start = LocalDate.now();
                            LocalDate end = start.plusMonths(12);
                            distproduct.setShowNewLabelFromDate(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            distproduct.setShowNewLabelUntilDate(Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            modelService.save(distproduct);
                            count++;
                        }
                    }
                } else {
                    success = false;
                    LOG.warn("No site is connected to sales org {}", distproduct.getSalesOrg().getCode());
                }
            }
        } catch (Exception e) {
            LOG.info("updateProductFirstAppearanceDate: Exception in Save Model for missing new label : " + e.toString());
            success = false;
        }
        LOG.info("updateProductFirstAppearanceDate: saveUpdatedEntries - Number Of records updated : " + count);
        return success;
    }

    private void updateFirstAppearanceField() {
        final FlexibleSearchQuery query = new FlexibleSearchQuery(PRODUCT_UPDATE_FIRST_APPEARANCE_DATE_QUERY);
        final SearchResult<ProductModel> result = getFlexibleSearchService().search(query);

        if(CollectionUtils.isNotEmpty(result.getResult())) {
            final LocalDate start = LocalDate.now();

            final int partitionSize = getConfigurationService().getConfiguration().getInt(ATTRIBUTE_UPDATE_FIRST_APPEARANCE_DATE_PARTITION_SIZE, DEFAULT_BATCH_SIZE);
            final List<List<ProductModel>> partitionedResults = ListUtils.partition(result.getResult(), partitionSize);

            int processed = 0;
            for(final List<ProductModel> partition: partitionedResults){
                for(final ProductModel product: partition){
                    product.setFirstAppearanceDate(Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    processed++;
                }
                getModelService().saveAll(partition);
                LOG.info("PRODUCT_UPDATE_FIRST_APPEARANCE_DATE_QUERY processed :" + processed +"/"+result.getCount());
            }
        }
    }

    @Override
    public ProductModel getProductInfoPimId(final String pimId) {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(PRODUCT_BY_PIMID_QUERY);
        int resultCount = 0;
        searchQuery.addQueryParameter("PIMID", pimId);
        final SearchResult<? extends ProductModel> result = getFlexibleSearchService().search(searchQuery);
        if ((null) != result) {
            resultCount = result.getTotalCount();
        }
        return resultCount == 0 ? null : result.getResult().get(0);
    }

    @Override
    public List<List<String>> searchProductsForExport() {
        final FlexibleSearchQuery searchQuery = new FlexibleSearchQuery(getPimExportTimestampQuery());
        searchQuery.setResultClassList(Lists.newArrayList(String.class, String.class));
        return flexibleSearchService.<List<String>>search(searchQuery).getResult();
        // final List<ProductModel> result = flexibleSearchService.<ProductModel> search(searchQuery).getResult();
        // return CollectionUtils.isNotEmpty(result)==false?null:result;
    }

    // Getter and Setter

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    public FlexibleSearchService getFlexibleSearchService() {
        return flexibleSearchService;
    }

    public void setFlexibleSearchService(FlexibleSearchService flexibleSearchService) {
        this.flexibleSearchService = flexibleSearchService;
    }

    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

}
