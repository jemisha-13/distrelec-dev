package com.namics.distrelec.b2b.core.cms.services.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService;
import de.hybris.platform.core.servicelayer.data.SearchPageData;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

public class DefaultDistCMSComponentService extends DefaultCMSComponentService {

    private static final String INTERNATIONAL_CONTENT_CATALOG = "distrelec_IntContentCatalog";

    private static <T extends AbstractCMSComponentModel> T mergeFunction(T compAlreadyInMap, T newCompWithSameKey) {
        return isInInternationalCatalog(compAlreadyInMap) ? newCompWithSameKey : compAlreadyInMap;
    }

    private static boolean isInInternationalCatalog(AbstractCMSComponentModel comp) {
        return INTERNATIONAL_CONTENT_CATALOG.equals(comp.getCatalogVersion().getCatalog().getId());
    }

    /**
     * Sorts components to match order of requested cms components IDs.
     */
    @Override
    public <T extends AbstractCMSComponentModel> SearchPageData<T> getAbstractCMSComponents(Collection<String> ids, SearchPageData searchPageData) {
        SearchPageData<T> pageData = super.getAbstractCMSComponents(ids, searchPageData);

        Map<String, T> indexedResults = pageData.getResults().stream()
                                                .filter(comp -> isTrue(comp.getVisible()))
                                                .collect(toMap(AbstractCMSComponentModel::getUid,
                                                               comp -> comp,
                                                               DefaultDistCMSComponentService::mergeFunction));

        List<T> sortedResults = ids.stream()
                                   .map(id -> indexedResults.get(id))
                                   .filter(Objects::nonNull)
                                   .collect(Collectors.toList());

        pageData.setResults(sortedResults);

        return pageData;
    }
}
