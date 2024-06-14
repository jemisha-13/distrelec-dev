package com.namics.distrelec.b2b.core.cms.daos.impl;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.data.PageableData;
import de.hybris.platform.cms2.servicelayer.daos.impl.DefaultCMSMediaDao;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import org.apache.commons.lang3.StringUtils;

public class DefaultDistCMSMediaDao extends DefaultCMSMediaDao {

    @Override
    public SearchResult<MediaModel> findMediasForCatalogVersion(String mask, String mimeType, CatalogVersionModel catalogVersion, PageableData pageableData) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT {").append("m.pk").append("} ");
        query.append("FROM {").append("Media AS m");
        query.append(" JOIN CatalogVersion AS cv ON {").append("m.catalogVersion").append("} = {").append("cv.pk").append("}");
        query.append(" JOIN Catalog AS c ON {").append("cv.catalog").append("} = {").append("c.pk").append("}} ");
        query.append("WHERE ({").append("m.catalogVersion").append("} = ?").append("catalogVersion");
        query.append(" OR ({").append("c.id").append("} = ").append("'Default'");
        query.append(" AND {").append("cv.version").append("} = ").append("'Staged'").append("))");
        if (StringUtils.isNotEmpty(mask)) {
            query.append(" AND (LOWER({").append("m.code").append("}) LIKE LOWER(?").append("code").append(")");
            query.append(" OR LOWER({").append("m.description").append("}) LIKE LOWER(?").append("description").append("))");
        }

        if (StringUtils.isNotEmpty(mimeType)) {
            query.append(" AND LOWER({").append("m.mime").append("}) LIKE LOWER(?").append("mime").append(")");
        }

        query.append(" ORDER BY {").append("m.code").append("} ASC");
        FlexibleSearchQuery fQuery = new FlexibleSearchQuery(query.toString());
        fQuery.addQueryParameter("code", "%" + mask + "%");
        fQuery.addQueryParameter("description", "%" + mask + "%");
        fQuery.addQueryParameter("mime", "%" + mimeType + "%");
        fQuery.addQueryParameter("catalogVersion", catalogVersion);
        fQuery.setStart(pageableData.getCurrentPage() * pageableData.getPageSize());
        fQuery.setCount(pageableData.getPageSize());
        fQuery.setNeedTotal(true);
        return this.search(fQuery);
    }
}
