package com.namics.distrelec.b2b.core.media;

import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.MediaService;

import java.util.List;

public interface DistMediaService extends MediaService {

    List<DistVideoMediaModel> searchVideoMedia(String term, int page, int pageSize, CatalogVersionModel catalogVersion);

    DistVideoMediaModel findVideoMedia(String code, CatalogVersionModel catalogVersion);
    
    void updateImageDimensions(MediaModel mediaModel);

}
