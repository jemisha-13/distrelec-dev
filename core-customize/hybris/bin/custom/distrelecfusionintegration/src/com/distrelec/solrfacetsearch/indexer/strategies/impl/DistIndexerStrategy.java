package com.distrelec.solrfacetsearch.indexer.strategies.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.indexer.IndexerContext;
import de.hybris.platform.solrfacetsearch.indexer.exceptions.IndexerException;
import de.hybris.platform.solrfacetsearch.indexer.strategies.impl.DefaultIndexerStrategy;
import de.hybris.platform.solrfacetsearch.indexer.workers.IndexerWorker;
import de.hybris.platform.solrfacetsearch.indexer.workers.IndexerWorkerParameters;
import de.hybris.platform.solrfacetsearch.solr.Index;
import de.hybris.platform.solrfacetsearch.solr.SolrSearchProvider;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrIndexNotFoundException;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

public class DistIndexerStrategy extends DefaultIndexerStrategy {

    private static final Logger LOG = LogManager.getLogger(DistIndexerStrategy.class);

    // method is overridden only to add more log-output, business logic is the same as in the super-class
    @Override
    protected List<PK> resolvePks() throws IndexerException {
        long t1 = System.currentTimeMillis();
        List<PK> pks = super.resolvePks();

        if (getFacetSearchConfig().getIndexConfig().getCmsSite() != null) {
            String uid = getFacetSearchConfig().getIndexConfig().getCmsSite().getUid();
            LOG.info("{}: Query found {} {} to index  for CMSSite {}, query took: {} ms", getIndexOperation(), pks.size(),
                     this.getIndexedType().getCode(), uid, System.currentTimeMillis() - t1);
        } else {
            LOG.info("{}: Query found {} {} to index, query took: {} ms", getIndexOperation(), pks.size(),
                     this.getIndexedType().getCode(), System.currentTimeMillis() - t1);
        }

        return pks;
    }

    /**
     * Overridden to prevent the solrIndexService.getActiveIndex(this.facetSearchConfig.getName(), this.indexedType.getIdentifier()) from happening.
     * The concept of active indexes does not exist in fusion
     */
    @Override
    protected Index resolveIndex() throws IndexerException {
        if (this.getIndex() != null) {
            return this.getIndex();
        } else {
            try {
                SolrSearchProvider searchProvider = this.getSolrSearchProviderFactory().getSearchProvider(this.getFacetSearchConfig(), this.getIndexedType());
                return searchProvider.resolveIndex(this.getFacetSearchConfig(), this.getIndexedType(), this.getIndexedType().getIdentifier());
            } catch (SolrIndexNotFoundException e) {
                return null;
            } catch (SolrServiceException e) {
                throw new IndexerException(e);
            }
        }
    }

    // method is overridden only to add more log-output, business logic is the same as in the super-class
    @Override
    protected void runWorkers(IndexerContext indexerContext, ExecutorCompletionService<Integer> completionService,
                              List<DefaultIndexerStrategy.IndexerWorkerWrapper> workers, int retriesLeft) throws IndexerException {
        long t1 = System.currentTimeMillis();
        int currentRetriesLeft = retriesLeft;
        Map<Integer, DefaultIndexerStrategy.IndexerWorkerWrapper> failedWorkers = new HashMap();
        LOG.debug("Submitting indexer workers (retries left: {})", retriesLeft);

        for (final IndexerWorkerWrapper worker : workers) {
            completionService.submit(worker.getIndexerWorker(), worker.getWorkerNumber());
            failedWorkers.put(worker.getWorkerNumber(), worker);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Worker {} has been submitted (retries left: {}", worker.getWorkerNumber(), worker.getRetriesLeft());
            }
        }

        for (int i = 0; i < workers.size(); ++i) {
            LOG.info("Batch {}/{} is being processed, indexing {}", i + 1, workers.size(), indexerContext.getIndexedType().getCode());
            try {
                Future<Integer> future = completionService.take();
                Integer workerNumber = future.get();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Worker {} finished", workerNumber);
                }

                failedWorkers.remove(workerNumber);
            } catch (ExecutionException e) {
                if (currentRetriesLeft <= 0) {
                    throw new IndexerException("Indexer worker failed. Max number of retries in total has been reached", e);
                }

                --currentRetriesLeft;
            } catch (InterruptedException var12) {
                throw new IndexerException("Indexer worker was interrupted.", var12);
            }
        }

        if (!failedWorkers.isEmpty()) {
            List<DefaultIndexerStrategy.IndexerWorkerWrapper> rerunWorkers = new ArrayList();

            for (final IndexerWorkerWrapper indexerWorkerWrapper : failedWorkers.values()) {
                if (indexerWorkerWrapper.getRetriesLeft() <= 0) {
                    throw new IndexerException("Indexer worker " + indexerWorkerWrapper.getWorkerNumber()
                                               + " failed. Max number of retries per worker has been reached");
                }

                IndexerWorker indexerWorker = this.createIndexerWorker(indexerContext, (long) indexerWorkerWrapper.getWorkerNumber(),
                                                                       indexerWorkerWrapper.getWorkerPks());
                indexerWorkerWrapper.setIndexerWorker(indexerWorker);
                indexerWorkerWrapper.setRetriesLeft(indexerWorkerWrapper.getRetriesLeft() - 1);
                rerunWorkers.add(indexerWorkerWrapper);
            }

            this.runWorkers(indexerContext, completionService, rerunWorkers, currentRetriesLeft);
        }

        String code = indexerContext.getIndexedType().getCode();
        CMSSiteModel cmsSite = indexerContext.getFacetSearchConfig().getIndexConfig().getCmsSite();
        int size = indexerContext.getPks().size();

        String site = cmsSite != null ? cmsSite.getUid() : "Global";
        LOG.info("Creating and updating {} documents of type {} for {} took:{} s", size, code, site, (System.currentTimeMillis() - t1) / 1000);
    }

    /**
     * Is overridden to set entire facetSearchConfig onto workerParameters and then call indexerWorkder.initialize with that workerParameters.
     */
    @Override
    protected IndexerWorker createIndexerWorker(IndexerContext indexerContext, long workerNumber, List<PK> workerPks) throws IndexerException {
        Collection<String> indexedProperties = new ArrayList();

        for (final IndexedProperty indexedProperty : indexerContext.getIndexedProperties()) {
            indexedProperties.add(indexedProperty.getName());
        }

        IndexerWorkerParameters workerParameters = new IndexerWorkerParameters();
        workerParameters.setWorkerNumber(workerNumber);
        workerParameters.setIndexOperationId(indexerContext.getIndexOperationId());
        workerParameters.setIndexOperation(indexerContext.getIndexOperation());
        workerParameters.setExternalIndexOperation(indexerContext.isExternalIndexOperation());
        workerParameters.setFacetSearchConfig(indexerContext.getFacetSearchConfig().getName());
        workerParameters.setIndexedType(indexerContext.getIndexedType().getUniqueIndexedTypeCode());
        workerParameters.setIndexedProperties(indexedProperties);
        workerParameters.setPks(workerPks);
        workerParameters.setIndexerHints(indexerContext.getIndexerHints());
        workerParameters.setIndex(indexerContext.getIndex().getQualifier());
        String tenantId = this.resolveTenantId();
        UserModel sessionUser = this.resolveSessionUser();
        LanguageModel sessionLanguage = this.resolveSessionLanguage();
        CurrencyModel sessionCurrency = this.resolveSessionCurrency();
        boolean sessionUseReadOnlyDataSource = this.resolveSessionUseReadOnlyDataSource();
        workerParameters.setTenant(tenantId);
        workerParameters.setSessionUser(sessionUser.getUid());
        workerParameters.setSessionLanguage(sessionLanguage == null ? null : sessionLanguage.getIsocode());
        workerParameters.setSessionCurrency(sessionCurrency == null ? null : sessionCurrency.getIsocode());
        workerParameters.setSessionUseReadOnlyDataSource(sessionUseReadOnlyDataSource);
        workerParameters.setFacetSearchConfigData(indexerContext.getFacetSearchConfig());

        IndexerWorker indexerWorker = this.getIndexerWorkerFactory().createIndexerWorker(this.getFacetSearchConfig());

        indexerWorker.initialize(workerParameters);

        return indexerWorker;
    }
}
