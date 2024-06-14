package com.distrelec.solrfacetsearch.config.factories.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexOperation;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.config.IndexedTypeFlexibleSearchQuery;
import de.hybris.platform.solrfacetsearch.config.factories.impl.DefaultFlexibleSearchQuerySpecFactory;
import de.hybris.platform.solrfacetsearch.solr.exceptions.SolrServiceException;

public class DistFlexibleSearchQuerySpecFactory extends DefaultFlexibleSearchQuerySpecFactory {

    protected static final String LAST_INDEX_TIME = "lastIndexTime";

    protected static final String VISIBLE = "visible";

    protected static final String CMS_SITE = "cmsSite";

    protected static final String COUNTRY = "country";

    protected static final String SALES_ORG = "salesOrg";

    protected static final String PRODUCT_INDEX_TYPE_IDENTIFIER = "distproductType";

    protected static final String PRODUCT_ATOMIC_INDEX_TYPE_IDENTIFIER = "distproductTypeAtomic";

    /***
     * Provides the attribute needed for the query to determine the items which need to be indexed
     */
    @Override
    protected void populateRuntimeParameters(IndexedTypeFlexibleSearchQuery queryData, IndexedType indexedType,
                                             FacetSearchConfig facetSearchConfig) throws SolrServiceException {
        super.populateRuntimeParameters(queryData, indexedType, facetSearchConfig);

        Map<String, Object> parameters = queryData.getParameters();

        parameters.put(VISIBLE, true);
        CMSSiteModel cmsSite = facetSearchConfig.getIndexConfig().getCmsSite();
        if (cmsSite != null) {
            parameters.put(CMS_SITE, cmsSite);
            parameters.put(COUNTRY, cmsSite.getCountry());
            parameters.put(SALES_ORG, cmsSite.getSalesOrg());

            addLastIndexTime(queryData, indexedType, parameters, cmsSite, facetSearchConfig);
        }
    }

    private void addLastIndexTime(final IndexedTypeFlexibleSearchQuery queryData, final IndexedType indexedType,
                                  final Map<String, Object> parameters, final CMSSiteModel cmsSite,
                                  final FacetSearchConfig facetSearchConfig) {
        if (cmsSite.getLastFusionIndexUpdates() != null) {
            String lastFusionIndexKey = getLastFusionIndexMapKey(queryData.getType(), indexedType);

            // on atomic product update consider full update of regular product
            if (queryData.getType() == IndexOperation.UPDATE && facetSearchConfig.getIndexConfig().isAtomicUpdate()) {
                Date lastAtomicUpdate = cmsSite.getLastFusionIndexUpdates().get(lastFusionIndexKey);

                String lastFullProductFusionIndexKey = getFullProductMapKey();
                Date lastFullProductIndex = cmsSite.getLastFusionIndexUpdates().get(lastFullProductFusionIndexKey);

                parameters.put(LAST_INDEX_TIME, getLastIndexDate(lastFullProductIndex, lastAtomicUpdate));
                return;
            } else if (queryData.getType() == IndexOperation.UPDATE || queryData.getType() == IndexOperation.PARTIAL_UPDATE) {
                Date lastUpdate = cmsSite.getLastFusionIndexUpdates().get(lastFusionIndexKey);

                String lastFullFusionIndexKey = getLastFusionIndexMapKey(IndexOperation.FULL, indexedType);
                Date lastFullIndex = cmsSite.getLastFusionIndexUpdates().get(lastFullFusionIndexKey);

                parameters.put(LAST_INDEX_TIME, getLastIndexDate(lastFullIndex, lastUpdate));
                return;
            } else {
                Date date = cmsSite.getLastFusionIndexUpdates().get(lastFusionIndexKey);
                parameters.put(LAST_INDEX_TIME, date == null ? getInitialDate() : date);
                return;
            }

        }
        parameters.put(LAST_INDEX_TIME, getInitialDate());
    }

    protected Date getLastIndexDate(Date lastFullIndex, Date lastUpdateIndex) {
        if (lastFullIndex == null && lastUpdateIndex == null) {
            return getInitialDate();
        }

        if (lastFullIndex != null && lastUpdateIndex != null) {
            return lastFullIndex.after(lastUpdateIndex) ? lastFullIndex : lastUpdateIndex;
        }

        return lastFullIndex != null ? lastFullIndex : lastUpdateIndex;
    }

    public String getLastFusionIndexMapKey(final IndexOperation indexOperation, final IndexedType indexType) {
        return indexOperation.name() + "_" + indexType.getIdentifier();
    }

    private String getFullProductMapKey() {
        return IndexOperation.FULL.name() + "_" + PRODUCT_INDEX_TYPE_IDENTIFIER;
    }

    private Date getInitialDate() {
        ZoneId defaultZoneId = ZoneId.systemDefault();
        LocalDate localDate = LocalDate.of(2000, 1, 1);

        return Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
    }
}
