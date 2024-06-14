package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimAssetImageType;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimMediaDto;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.media.impl.MediaDao;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductFamilyVideoAssetConverter extends ImageAssetElementConverter {

    private static final String XP_PUSH_DOWNLOADED = "Values/Value[@AttributeID='35_assetexternallyurl_txt']";

    @Autowired
    private MediaDao mediaDao;

    @Override
    protected List<Element> getPushLocations(Element source) {
        return source.selectNodes(XP_PUSH_DOWNLOADED);
    }

    @Override
    protected void cleanUpMedias(Map<String, MediaModel> mediaMap) {}

    @Override
    protected MediaModel getOrCreateMedia(ImportContext importContext, Collection<MediaModel> medias, PimMediaDto mediaDto) {
        Optional<MediaModel> existing = Optional.ofNullable(medias)
                                                .map(Collection::stream)
                                                .orElse(Stream.empty())
                                                .filter(m -> m.getCode().equals(mediaDto.getCode()))
                                                .map(m -> populateMediaFromDTO(importContext, mediaDto, m))
                                                .findFirst();
        return existing.orElseGet(() -> existing.orElse(mediaDao.findMediaByCode(importContext.getProductCatalogVersion(), mediaDto.getCode())
                .stream()
                .findAny()
                .orElse(createMedia(importContext, mediaDto))));
    }

    @Override
    protected String getAttributeConfigurationId(Element assetPushLocationElement) {
        return PimAssetImageType.FAMILY_VIDEO.getPushLocationConfigurationId();
    }

    @Override
    protected MediaModel createMedia(final ImportContext importContext, final PimMediaDto mediaDto) {
        final DistVideoMediaModel media = getModelService().create(DistVideoMediaModel.class);
        populateMediaFromDTO(importContext, mediaDto, media);
        return media;
    }

    @Override
    protected MediaModel populateMediaFromDTO(ImportContext importContext, PimMediaDto source, MediaModel target) {
        DistVideoMediaModel videoMedia = (DistVideoMediaModel) target;
        videoMedia.setCode(source.getCode());
        videoMedia.setYoutubeUrl(source.getUrl());
        videoMedia.setCatalogVersion(importContext.getProductCatalogVersion());
        videoMedia.setAltText(source.getAltText());
        videoMedia.setDescription(source.getDescription());
        return target;
    }
}
