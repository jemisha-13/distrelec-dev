package com.namics.distrelec.b2b.backoffice.cockpitng.dataaccess.facades.search;

import com.hybris.backoffice.cockpitng.dataaccess.facades.search.DefaultPlatformFieldSearchFacadeStrategy;
import com.hybris.cockpitng.search.data.SearchQueryData;
import de.hybris.platform.genericsearch.GenericSearchQuery;

public class DefaultDistPlatformFieldSearchFacadeStrategy extends DefaultPlatformFieldSearchFacadeStrategy {

    @Override
    public GenericSearchQuery buildQuery(SearchQueryData searchQueryData) {
        return super.buildQuery(searchQueryData);
    }

    @Override
    public SearchQueryData adjustSearchQuery(SearchQueryData searchQueryData) {
        return super.adjustSearchQuery(searchQueryData);
    }
}
