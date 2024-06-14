package com.namics.distrelec.b2b.core.punchouts.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.namics.distrelec.b2b.core.model.category.ProductHierarchyEntryModel;
import com.namics.distrelec.b2b.core.model.jobs.SyncProductHierarchyCronJobModel;

import de.hybris.platform.catalog.model.classification.ClassificationClassModel;
import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.jalo.flexiblesearch.FlexibleSearchException;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.SearchResult;

/**
 * Job that reads the modified codelists from the SAP system and updates the codelists in hybris.
 * 
 * @author ksperner, Namics AG
 * @since Distrelec 1.0
 * 
 */
public class SyncProductHierarchyJob extends AbstractJobPerformable<SyncProductHierarchyCronJobModel> {

    private static final Logger LOG = Logger.getLogger(SyncProductHierarchyJob.class);
    private ModelService modelService;

    public ModelService getModelService() {
        return modelService;
    }

    @Override
    public void setModelService(ModelService modelService) {
        this.modelService = modelService;
    }

    @Override
    public PerformResult perform(final SyncProductHierarchyCronJobModel cronJob) {
        int counter = 0;
        int partCounter = 0;
        StringBuilder query = new StringBuilder();
        query.append("SELECT {p.pk} FROM {Product as p} WHERE {p." + ProductModel.PRODUCTHIERARCHY + "} is not null");
        final SearchResult<ProductModel> result = flexibleSearchService.search(query.toString());

        if (CollectionUtils.isNotEmpty(result.getResult())) {
            LOG.info("synchronizing product hierarchy for " + result.getCount() + " products");
            for (ProductModel product : result.getResult()) {
                partCounter++;
                if (partCounter == 1000) {
                    counter = counter + 1000;
                    partCounter = 0;
                    LOG.info(counter + " products already synchronized");
                }
                if (product.getSupercategories() != null) {
                    for (final CategoryModel category : product.getSupercategories()) {
                        doProcess(category, product);
                        for (CategoryModel superCategory : category.getAllSupercategories()) {
                            doProcess(superCategory, product);
                        }
                    }
                }
            }

        }
        // removing old product hierarchy entry
        query = new StringBuilder();
        query.append("SELECT {p.pk} FROM {Product as p} WHERE {p." + ProductModel.PRODUCTHIERARCHY + "} is null");
        final SearchResult<ProductModel> delResult = flexibleSearchService.search(query.toString());
        if (delResult != null && CollectionUtils.isNotEmpty(delResult.getResult())) {
            LOG.info("removing product hierarchy entries for " + delResult.getCount() + " products");
            for (ProductModel product : delResult.getResult()) {
                query = new StringBuilder();
                final Map<String, Object> params = new HashMap<String, Object>();

                query.append("SELECT {he.pk} FROM {ProductHierarchyEntry as he} WHERE {he." + ProductHierarchyEntryModel.PRODUCTCODE + "}=(?")
                        .append(ProductHierarchyEntryModel.PRODUCTCODE).append(")");
                params.put(ProductHierarchyEntryModel.PRODUCTCODE, product.getCode());
                final SearchResult<ProductHierarchyEntryModel> toRemoveresult = flexibleSearchService.search(query.toString(), params);
                if (toRemoveresult != null && toRemoveresult.getResult() != null && CollectionUtils.isNotEmpty(toRemoveresult.getResult())) {
                    for (final ProductHierarchyEntryModel entry : toRemoveresult.getResult()) {
                        getModelService().remove(entry);
                    }
                }
            }
        }

        query = new StringBuilder();
        query.append("SELECT {he.pk} FROM {ProductHierarchyEntry as he} WHERE {he." + ProductHierarchyEntryModel.CATEGORY + "} is null");
        final SearchResult<ProductHierarchyEntryModel> nullEntries = flexibleSearchService.search(query.toString());
        if (nullEntries != null && CollectionUtils.isNotEmpty(nullEntries.getResult())) {
            for (ProductHierarchyEntryModel nullEntry : nullEntries.getResult()) {
                getModelService().remove(nullEntry);
            }
        }
        LOG.info("synchronizing is done.");
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private boolean hierarchyCodeExistsAlready(CategoryModel category, ProductModel product) {
        final StringBuilder query = new StringBuilder();
        final Map<String, Object> params = new HashMap<String, Object>();

        query.append("SELECT {he.pk} FROM {ProductHierarchyEntry as he} WHERE {he." + ProductHierarchyEntryModel.CATEGORY + "}=(?")
                .append(ProductHierarchyEntryModel.CATEGORY).append(")");
        query.append(" AND {he." + ProductHierarchyEntryModel.CODE + "}=(?").append(ProductHierarchyEntryModel.CODE).append(")");
        params.put(ProductHierarchyEntryModel.CATEGORY, category);
        params.put(ProductHierarchyEntryModel.CODE, product.getProductHierarchy());
        final SearchResult<ProductHierarchyEntryModel> result = flexibleSearchService.search(query.toString(), params);
        if (result == null || result.getResult() == null || CollectionUtils.isEmpty(result.getResult())) {
            return false;
        }
        return true;
    }

    private void doProcess(CategoryModel category, ProductModel product) {
        if (!(category instanceof ClassificationClassModel) && !hierarchyCodeExistsAlready(category, product)) {
            deleteExistingEntries(category, product.getCode());
            getModelService().refresh(category);
            List<ProductHierarchyEntryModel> entries = new ArrayList<ProductHierarchyEntryModel>();
            entries.addAll(category.getHierarchyCodes());
            if (product.getProductHierarchy() != null && product.getProductHierarchy().length() > 0) {
                entries.add(createEntry(product));
            }
            category.setHierarchyCodes(entries);
            try {
                getModelService().save(category);
            } catch (ModelSavingException ex) {
                LOG.error("ModelSavingException: \n" + ex.getMessage());
                LOG.error("category: " + category.getCode() + " product: " + product.getCode());
            } catch (FlexibleSearchException ex) {
                LOG.error("FlexibleSearchException: \n" + ex.getMessage());
                LOG.error("category: " + category.getCode() + " product: " + product.getCode());
            }
        }
    }

    private ProductHierarchyEntryModel createEntry(ProductModel product) {
        ProductHierarchyEntryModel entry = getModelService().create(ProductHierarchyEntryModel.class);
        entry.setCode(product.getProductHierarchy());
        entry.setProductCode(product.getCode());
        getModelService().save(entry);
        getModelService().refresh(entry);
        return entry;
    }

    private void deleteExistingEntries(CategoryModel category, String productCode) {
        final StringBuilder query = new StringBuilder();
        final Map<String, Object> params = new HashMap<String, Object>();

        query.append("SELECT {he.pk} FROM {ProductHierarchyEntry as he} WHERE {he." + ProductHierarchyEntryModel.CATEGORY + "}=(?")
                .append(ProductHierarchyEntryModel.CATEGORY).append(")");
        query.append(" AND {he." + ProductHierarchyEntryModel.PRODUCTCODE + "}=(?").append(ProductHierarchyEntryModel.PRODUCTCODE).append(")");
        params.put(ProductHierarchyEntryModel.CATEGORY, category);
        params.put(ProductHierarchyEntryModel.PRODUCTCODE, productCode);
        final SearchResult<ProductHierarchyEntryModel> result = flexibleSearchService.search(query.toString(), params);
        if (result != null && result.getResult() != null && CollectionUtils.isNotEmpty(result.getResult())) {
            for (ProductHierarchyEntryModel entry : result.getResult()) {
                getModelService().remove(entry);
            }
        }
    }

}
