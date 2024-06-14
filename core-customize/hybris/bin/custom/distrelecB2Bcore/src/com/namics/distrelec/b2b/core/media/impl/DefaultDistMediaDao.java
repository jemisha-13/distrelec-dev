package com.namics.distrelec.b2b.core.media.impl;

import com.namics.distrelec.b2b.core.media.DistMediaDao;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.media.impl.DefaultMediaDao;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultDistMediaDao extends DefaultMediaDao implements DistMediaDao {

    private static final String DIST_VIDEO_WITH_CODE = "SELECT DISTINCT{" + DistVideoMediaModel._TYPECODE + "." + DistVideoMediaModel.PK + "} FROM  {"
            + DistVideoMediaModel._TYPECODE + "} WHERE {" + DistVideoMediaModel._TYPECODE + "." + DistVideoMediaModel.CODE + "} = ?code AND {"
            + DistVideoMediaModel._TYPECODE + "." + DistVideoMediaModel.CATALOGVERSION + "} = ?catalogVersion";

    private static final String DIST_VIDEO_WITH_CODE_LIKE = "SELECT DISTINCT{" + DistVideoMediaModel._TYPECODE + "." + DistVideoMediaModel.PK + "} FROM  {"
            + DistVideoMediaModel._TYPECODE + "} WHERE {" + DistVideoMediaModel._TYPECODE + "." + DistVideoMediaModel.CODE + "} LIKE ?term AND {"
            + DistVideoMediaModel._TYPECODE + "." + DistVideoMediaModel.CATALOGVERSION + "} = ?catalogVersion";

    @Autowired
    private FlexibleSearchService flexibleSearchService;

    @Override
    public List<DistVideoMediaModel> searchVideoMedia(String term, int page, int pageSize, CatalogVersionModel catalogVersion) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("term", "%" + term + "%");
        queryParams.put("catalogVersion", catalogVersion);
        FlexibleSearchQuery query = new FlexibleSearchQuery(DIST_VIDEO_WITH_CODE_LIKE, queryParams);
        query.setStart(page * pageSize);
        query.setCount(pageSize);

        SearchResult<DistVideoMediaModel> searchResult = flexibleSearchService.search(query);
        return searchResult.getResult();
    }

    @Override
    public DistVideoMediaModel findVideoMedia(String code, CatalogVersionModel catalogVersion) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("code", code);
        queryParams.put("catalogVersion", catalogVersion);
        FlexibleSearchQuery query = new FlexibleSearchQuery(DIST_VIDEO_WITH_CODE, queryParams);
        return flexibleSearchService.searchUnique(query);
    }

}
