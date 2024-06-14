package com.namics.distrelec.b2b.core.media;

import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.servicelayer.media.impl.MediaDao;

import java.util.List;

public interface DistMediaDao extends MediaDao {

    List<DistVideoMediaModel> searchVideoMedia(String term, int page, int pageSize, CatalogVersionModel catalogVersion);

    DistVideoMediaModel findVideoMedia(String code, CatalogVersionModel catalogVersion);

}
