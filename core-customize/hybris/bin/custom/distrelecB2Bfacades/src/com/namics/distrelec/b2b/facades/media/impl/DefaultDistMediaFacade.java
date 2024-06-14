package com.namics.distrelec.b2b.facades.media.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.media.DistMediaService;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import com.namics.distrelec.b2b.facades.media.DistMediaFacade;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cmsfacades.media.impl.DefaultMediaFacade;
import de.hybris.platform.cmswebservices.data.MediaData;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

public class DefaultDistMediaFacade extends DefaultMediaFacade implements DistMediaFacade {

    @Autowired
    private DistMediaService distMediaService;

    @Override
    public List<MediaData> searchVideoMedia(String term, int page, int pageSize, String catalogVersionUuid) {
        CatalogVersionModel catalogVersion = getCatalogVersion(catalogVersionUuid);
        return distMediaService.searchVideoMedia(term, page, pageSize, catalogVersion).stream()
                               .map(this::convertVideoModelToData).collect(Collectors.toList());
    }

    @Override
    public MediaData findVideoMedia(String code, String catalogVersionUuid) {
        CatalogVersionModel catalogVersion = getCatalogVersion(catalogVersionUuid);
        return convertVideoModelToData(distMediaService.findVideoMedia(code, catalogVersion));
    }

    private MediaData convertVideoModelToData(DistVideoMediaModel videoMediaModel) {
        if (videoMediaModel == null) {
            return null;
        }

        MediaData videoMediaData = new MediaData();
        videoMediaData.setCode(videoMediaModel.getCode());
        videoMediaData.setCatalogId(videoMediaModel.getCatalogVersion().getCatalog().getId());
        videoMediaData.setCatalogVersion(videoMediaModel.getCatalogVersion().getVersion());
        videoMediaData.setDescription(videoMediaModel.getDescription());
        videoMediaData.setUuid(videoMediaModel.getCode());
        videoMediaData.setYoutubeUrl(videoMediaModel.getYoutubeUrl());
        return videoMediaData;
    }

    private CatalogVersionModel getCatalogVersion(String catalogVersionUuid) {
        return getUniqueItemIdentifierService().getItemModel(catalogVersionUuid, CatalogVersionModel.class)
                                               .orElseThrow(() -> new UnknownIdentifierException(
                                                                                                 "Failed to find a CatalogVersionModel for the given uuid"
                                                                                                 + catalogVersionUuid));
    }
}
