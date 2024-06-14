package com.namics.distrelec.b2b.core.inout.pim.servicelayer.converter;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.exception.ElementConverterException;
import com.namics.distrelec.b2b.core.model.DistAudioMediaModel;
import com.namics.distrelec.b2b.core.service.media.DistMediaFormatService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Converts a data sheet "Asset" XML element into a hybris {@link DistAudioMediaModel}.
 */
public class AudioAssetElementConverter extends AbstractMediaAssetElementConverter<DistAudioMediaModel> {

    private static final Logger LOG = LogManager.getLogger(AudioAssetElementConverter.class);

    static final String URL_PREFIX = "/";

    static final String XP_URL = "AssetPushLocation[@ConfigurationID='Audio_mp3']";

    @Autowired
    private DistMediaFormatService distMediaFormatService;

    @Override
    public void convertSpecialAttibutes(final Element source, final DistAudioMediaModel target, final ImportContext importContext, final String hash) {
        target.setMediaFormat(getDistMediaFormatService().getMediaFormatForQualifier(DistConstants.MediaFormat.AUDIO));
    }

    @Override
    public void setMediaURLs(final Element source, final DistAudioMediaModel target) {
        final String pushLocation = source.valueOf(XP_URL);
        if (StringUtils.isNotEmpty(pushLocation)) {
            target.setURL(URL_PREFIX + pushLocation);
        } else {
            LOG.error("Could not resolve push location for audio media [{}]", new Object[] { getId(source) });
            throw new ElementConverterException("Error converting audio media");
        }
    }

    public DistMediaFormatService getDistMediaFormatService() {
        return distMediaFormatService;
    }

    public void setDistMediaFormatService(DistMediaFormatService distMediaFormatService) {
        this.distMediaFormatService = distMediaFormatService;
    }
}
