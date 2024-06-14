/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.namics.distrelec.b2b.core.util.DistUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.PimAssetImageType;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.dto.PimMediaDto;
import com.namics.distrelec.b2b.core.service.media.DistMediaFormatService;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.media.MediaService;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Converts an image "Asset" XML element into a hybris {@link MediaContainerModel} with assigned medias.
 * 
 * @author ceberle, Namics AG
 * @since Distrelec 1.0
 */
public class ImageAssetElementConverter implements PimImportElementConverter<MediaContainerModel> {

    private static final Logger LOG = LogManager.getLogger(ImageAssetElementConverter.class);

    public static final String URL_PREFIX = "/";

    protected static final String XP_ASSET_PUSH_LOCATION = "AssetPushLocation";
    private static final String ATTRIBUTE_CONFIGURATION_ID = "ConfigurationID";
    private static final String XP_NAME = "Name";
    private static final String XP_DESCRIPTION = "Values/Value[@AttributeID='bmecat_ds_description']";

    @Autowired
    private ModelService modelService;

    @Autowired
    private DistMediaFormatService distMediaFormatService;

    @Autowired
    private MediaService mediaService;

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter#getId(org.dom4j.Element)
     */
    @Override
    public String getId(final Element element) {
        return element.attributeValue(ATTRIBUTE_ID);
    }

    protected List<Element> getPushLocations(Element source) {
        return source.selectNodes(XP_ASSET_PUSH_LOCATION);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.PimImportElementConverter#convert(org.dom4j.Element,
     * java.lang.Object, com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext, java.lang.String)
     */
    @Override
    public void convert(final Element source, final MediaContainerModel target, final ImportContext importContext, final String hash) {

        final List<MediaModel> medias = new ArrayList<>();

        // map existing medias within container
        Map<String, MediaModel> mediaMap;
        if (target.getMedias() != null) {
            mediaMap = target.getMedias().stream()
                               .collect(Collectors.toMap(MediaModel::getCode, media -> media));
        } else {
            mediaMap = new HashMap<>();
        }

        final List<Element> assetPushLocationElements = getPushLocations(source);
        for (final Element assetPushLocationElement : assetPushLocationElements) {
            final String url = assetPushLocationElement.getTextTrim();
            final String configurationId = getAttributeConfigurationId(assetPushLocationElement);

            if (StringUtils.isBlank(url)) {
                LOG.debug("Could not determine URL of Asset with ID [{}] and configurationID [{}]", target.getQualifier(), configurationId);
                continue;
            }

            final PimAssetImageType imageType = PimAssetImageType.getByPushLocationConfigurationId(configurationId);
            if (imageType != null) {
                final PimMediaDto mediaDto = new PimMediaDto();
                mediaDto.setCode(target.getQualifier() + imageType.getSuffix());
                mediaDto.setAltText(source.elementTextTrim(XP_NAME));
                mediaDto.setDescription(source.valueOf(XP_DESCRIPTION));
                String urlPrefix = DistUtils.startsWithHttpHttps(url) ? "" : URL_PREFIX;
                mediaDto.setUrl(urlPrefix + url);
                mediaDto.setMediaFormatCode(imageType.getMediaFormat());

                final MediaModel media = getOrCreateMedia(importContext, target.getMedias(), mediaDto);

                if (media == null) {
                    continue;
                }

                media.setMediaContainer(target);

                if (media.getPk() != null) { // old medias should not be removed
                    mediaMap.remove(media.getCode());
                }
                getModelService().save(media);
                medias.add(media);
            }
        }

        cleanUpMedias(mediaMap);

        target.setMedias(medias);

        target.setPimXmlHashMaster(hash);
        target.setPimHashTimestamp(new Date());
    }

    protected String getAttributeConfigurationId(Element assetPushLocationElement) {
        return assetPushLocationElement.attributeValue(ATTRIBUTE_CONFIGURATION_ID);
    }

    protected void cleanUpMedias(Map<String, MediaModel> mediaMap) {
        // We delete the remaining medias because they are not referenced anymore.
        if (MapUtils.isNotEmpty(mediaMap)) {
            LOG.info("Removing non referenced medias, count: " + mediaMap.size());
            getModelService().removeAll(mediaMap.values());
        }
    }

    /**
     * 
     * @param importContext
     *            the import context
     * @param medias
     *            a collection of {@link MediaModel}s
     * @param mediaDto
     *            a media DTO
     * @return {@link MediaModel}
     */
    protected MediaModel getOrCreateMedia(final ImportContext importContext, final Collection<MediaModel> medias, final PimMediaDto mediaDto) {
        return medias == null ? createMedia(importContext, mediaDto) : medias.stream().filter(m -> isEqual(m, mediaDto)).findFirst(). //
                orElseGet(() -> {
                    try {
                        return findMediaByCode(mediaDto.getCode(), importContext.getProductCatalogVersion());
                    } catch (final UnknownIdentifierException uie) {
                        // No media found, then we create a new one.
                        return createMedia(importContext, mediaDto);
                    } catch (final AmbiguousIdentifierException exp) {
                        LOG.error("More than 1 media found with code [" + mediaDto.getCode() + "]", exp);
                        // More than one found, we return null.
                        return null;
                    }
                });
    }

    /**
     * Fetch the media from the database.
     * 
     * @param code
     *            the media's code
     * @param catalogVersion
     *            the media's catalog version.
     * @return the {@link MediaModel} belonging to the specified {@code catalogVersion} and having the {@code code}
     * @throws UnknownIdentifierException
     * @throws AmbiguousIdentifierException
     */
    protected MediaModel findMediaByCode(final String code, final CatalogVersionModel catalogVersion)
            throws UnknownIdentifierException, AmbiguousIdentifierException {
        return getMediaService().getMedia(catalogVersion, code);
    }

    /**
     * Compare the {@link MediaModel} with the specified media DTO.
     * 
     * @param media
     *            the target media
     * @param mediaDto
     *            the media DTO
     * @return {@code true} if the {@link MediaModel} equals to the specified media DTO, {@code false} otherwise.
     */
    private boolean isEqual(final MediaModel media, final PimMediaDto mediaDto) {
        return media != null //
                && equals(media.getCode(), mediaDto.getCode()) //
                && equals(media.getAltText(), mediaDto.getAltText()) //
                && equals(media.getDescription(), mediaDto.getDescription()) //
                && equals(media.getURL(), mediaDto.getUrl()) //
                && equals(media.getInternalURL(), mediaDto.getUrl()) //
                && isMediaFormatEqual(media, mediaDto.getMediaFormatCode());
    }

    /**
     * Compare the {@link MediaModel#getMediaFormat()} qualifier with the specified media format code.
     * 
     * @param media
     *            the target media
     * @param mediaFormatCode
     *            the media format code
     * @return {@code true} if the {@link MediaModel#getMediaFormat()} qualifier equals to the specified media format code, {@code false}
     *         otherwise.
     */
    private boolean isMediaFormatEqual(final MediaModel media, final String mediaFormatCode) {
        return (media.getMediaFormat() == null && mediaFormatCode == null) //
                || (media.getMediaFormat() != null && equals(media.getMediaFormat().getQualifier(), mediaFormatCode));
    }

    /**
     * Returns true if both objects are null or if object1.equals(object2).
     * 
     * @see java.util.Objects#equals(Object, Object)
     */
    private boolean equals(final Object object1, final Object object2) {
        // return (object1 == object2) || (object1 != null && object1.equals(object2));
        return object1 == null ? object2 == null : object1.equals(object2);
    }

    /**
     * Create a new instance of {@link MediaModel} from the specified DTO.
     * 
     * @param importContext
     *            the PIM import context
     * @param mediaDto
     *            the source DTO
     * @return a new instance of {@link MediaModel}
     */
    protected MediaModel createMedia(final ImportContext importContext, final PimMediaDto mediaDto) {
        final MediaModel media = modelService.create(MediaModel.class);
        populateMediaFromDTO(importContext, mediaDto, media);
        return media;
    }

    protected MediaModel populateMediaFromDTO(ImportContext importContext, PimMediaDto source, MediaModel target) {
        target.setCode(source.getCode());
        target.setURL(source.getUrl());
        target.setCatalogVersion(importContext.getProductCatalogVersion());
        target.setAltText(source.getAltText());
        target.setDescription(source.getDescription());
        target.setMediaFormat(distMediaFormatService.getMediaFormatForQualifier(source.getMediaFormatCode()));
        return target;
    }

    public ModelService getModelService() {
        return modelService;
    }

    public void setModelService(final ModelService modelService) {
        this.modelService = modelService;
    }

    public DistMediaFormatService getDistMediaFormatService() {
        return distMediaFormatService;
    }

    public void setDistMediaFormatService(final DistMediaFormatService distMediaFormatService) {
        this.distMediaFormatService = distMediaFormatService;
    }

    public MediaService getMediaService() {
        return mediaService;
    }

    public void setMediaService(final MediaService mediaService) {
        this.mediaService = mediaService;
    }
}
