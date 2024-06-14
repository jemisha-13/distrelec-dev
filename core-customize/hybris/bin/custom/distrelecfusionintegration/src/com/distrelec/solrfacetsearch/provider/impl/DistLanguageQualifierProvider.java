package com.distrelec.solrfacetsearch.provider.impl;

import static java.util.stream.Collectors.joining;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.solrfacetsearch.config.FacetSearchConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedType;
import de.hybris.platform.solrfacetsearch.provider.Qualifier;
import de.hybris.platform.solrfacetsearch.provider.impl.LanguageQualifierProvider;

public class DistLanguageQualifierProvider extends LanguageQualifierProvider {

    private static final Logger LOG = LogManager.getLogger(DistLanguageQualifierProvider.class);

    @Override
    public Collection<Qualifier> getAvailableQualifiers(FacetSearchConfig facetSearchConfig, IndexedType indexedType) {
        String languagesBeingFetched = getLanguages(facetSearchConfig)
                                                                      .stream()
                                                                      .map(LanguageModel::getIsocode)
                                                                      .collect(joining(","));
        LOG.debug("Languages being fetched for document are: {}", languagesBeingFetched);
        return super.getAvailableQualifiers(facetSearchConfig, indexedType);
    }

    private Collection<LanguageModel> getLanguages(FacetSearchConfig facetSearchConfig) {
        return facetSearchConfig.getIndexConfig().getLanguages();
    }
}
