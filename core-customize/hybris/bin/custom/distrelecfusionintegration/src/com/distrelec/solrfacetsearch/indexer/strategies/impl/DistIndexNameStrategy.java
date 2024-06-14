package com.distrelec.solrfacetsearch.indexer.strategies.impl;

import org.apache.commons.lang3.StringUtils;

import com.distrelec.solrfacetsearch.indexer.strategies.IndexNameStrategy;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

import static com.namics.distrelec.b2b.core.constants.DistConstants.Fusion.FUSION_PROFILE_SUFFIX;

public class DistIndexNameStrategy implements IndexNameStrategy {

    private static final String FUSION_INDEX_PROFILE_PREFIX = "webshop_";

    private ConfigurationService configurationService;

    public DistIndexNameStrategy(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public String createIndexName(FacetSearchConfig facetSearchConfig, IndexedType indexedType) {
        String suffix = getProfileSuffix();
        return FUSION_INDEX_PROFILE_PREFIX + indexedType.getIndexName().toLowerCase() + suffix;
    }

    private String getProfileSuffix() {
        String suffix = configurationService.getConfiguration().getString(FUSION_PROFILE_SUFFIX);
        return StringUtils.isNotBlank(suffix) ? "_" + suffix : "";
    }
}
