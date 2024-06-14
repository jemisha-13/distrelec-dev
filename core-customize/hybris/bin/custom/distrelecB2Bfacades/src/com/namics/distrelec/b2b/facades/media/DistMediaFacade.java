package com.namics.distrelec.b2b.facades.media;

import java.util.List;

import de.hybris.platform.cmsfacades.media.MediaFacade;
import de.hybris.platform.cmswebservices.data.MediaData;

public interface DistMediaFacade extends MediaFacade {

    /**
     * Search for video medias
     *
     * @param term
     * @return
     */
    List<MediaData> searchVideoMedia(String term, int page, int pageSize, String catalogVersionUuid);

    /**
     * Find video media by code
     *
     * @param code
     * @return
     */
    MediaData findVideoMedia(String code, String catalogVersionUuid);

}
