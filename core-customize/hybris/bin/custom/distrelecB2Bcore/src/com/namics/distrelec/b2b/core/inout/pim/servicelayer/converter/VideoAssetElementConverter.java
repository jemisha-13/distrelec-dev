/*
 * Copyright 2000-2013 Namics AG. All rights reserved.
 */

package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;

import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.model.DistVideoMediaModel;

/**
 * Converts a video "Asset" XML element into a hybris {@link DistVideoMediaModel}.
 * 
 * @author csieber, Namics AG
 * @author <a href="nabil.benothman@distrelec.com">Nabil Benothman</a>, Distrelec
 * @since Distrelec 1.0
 */
public class VideoAssetElementConverter extends AbstractMediaAssetElementConverter<DistVideoMediaModel> {

    private static final String XP_VIDEO_ID = "ID";
    private static final String XP_STREAM_URL = "AssetPushLocation[@ConfigurationID='streams']";

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.AbstractMediaAssetElementConverter#convertSpecialAttibutes(org.dom4j.
     * Element, de.hybris.platform.core.model.media.MediaModel, com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext,
     * java.lang.String)
     */
    @Override
    public void convertSpecialAttibutes(final Element source, final DistVideoMediaModel target, final ImportContext importContext, final String hash) {
        final String brightCoveId = source.attribute(XP_VIDEO_ID).getStringValue();
        // final String brightCoveId = source.valueOf(XP_BRIGHTCOVE_ID);
        if (StringUtils.isEmpty(brightCoveId)) {
            getModelService().remove(target);
            return;
        }

        target.setBrightcoveVideoId(brightCoveId);
        target.setLanguages(getLanguages(source));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter.AbstractMediaAssetElementConverter#setMediaURLs(org.dom4j.Element,
     * de.hybris.platform.core.model.media.MediaModel)
     */
    @Override
    public void setMediaURLs(final Element source, final DistVideoMediaModel target) {
        // Push Location
        final String pushLocation = source.valueOf(XP_STREAM_URL);
        target.setURL(StringUtils.isNotEmpty(pushLocation) ? (ImageAssetElementConverter.URL_PREFIX + pushLocation) : null);
        target.setYoutubeUrl(source.valueOf(XP_EXTERNAL_URL));
    }
}
