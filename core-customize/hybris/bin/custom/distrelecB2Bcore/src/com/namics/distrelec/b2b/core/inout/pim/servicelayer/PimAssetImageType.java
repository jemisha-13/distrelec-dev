package com.namics.distrelec.b2b.core.inout.pim.servicelayer;

import com.namics.distrelec.b2b.core.constants.DistConstants;

/**
 * Representation of image types.
 * 
 * @author ascherrer, Namics AG
 * @since 1.0
 * 
 */
public enum PimAssetImageType {

    LANDSCAPE_SMALL(DistConstants.MediaFormat.LANDSCAPE_SMALL, "landscape_small", "_ls"), //
    LANDSCAPE_SMALL_WEBP(DistConstants.MediaFormat.LANDSCAPE_SMALL_WEBP, "landscape_small_webp", "_lsw"), //
    LANDSCAPE_MEDIUM(DistConstants.MediaFormat.LANDSCAPE_MEDIUM, "landscape_medium", "_lm"), //
    LANDSCAPE_MEDIUM_WEBP(DistConstants.MediaFormat.LANDSCAPE_MEDIUM_WEBP, "landscape_medium_webp", "_lmw"), //
    LANDSCAPE_LARGE(DistConstants.MediaFormat.LANDSCAPE_LARGE, "landscape_large", "_ll"), //
    LANDSCAPE_LARGE_WEBP(DistConstants.MediaFormat.LANDSCAPE_LARGE_WEBP, "landscape_large_webp", "_llw"), //
    PORTRAIT_SMALL(DistConstants.MediaFormat.PORTRAIT_SMALL, "portrait_small", "_ps"), //
    PORTRAIT_SMALL_WEBP(DistConstants.MediaFormat.PORTRAIT_SMALL_WEBP, "portrait_small_webp", "_psw"), //
    PORTRAIT_MEDIUM(DistConstants.MediaFormat.PORTRAIT_MEDIUM, "portrait_medium", "_pm"), //
    PORTRAIT_MEDIUM_WEBP(DistConstants.MediaFormat.PORTRAIT_MEDIUM_WEBP, "portrait_medium_webp", "_pmw"), //
    BRAND_LOGO(DistConstants.MediaFormat.BRAND_LOGO, "manufacturer_logo", "_bl"), //
    BRAND_LOGO_WEBP(DistConstants.MediaFormat.BRAND_LOGO_WEBP, "manufacturer_logo_webp", "_blw"), //
    IMAGE360_SMALL(DistConstants.MediaFormat.IMAGE360_SMALL, "360_small", "_3s"), //
    IMAGE360_SMALL_WEBP(DistConstants.MediaFormat.IMAGE360_SMALL_WEBP, "360_small_webp", "_3sw"), //
    IMAGE360_MEDIUM(DistConstants.MediaFormat.IMAGE360_MEDIUM, "360_medium", "_3m"), //
    IMAGE360_MEDIUM_WEBP(DistConstants.MediaFormat.IMAGE360_MEDIUM_WEBP, "360_medium_webp", "_3mw"), //
    IMAGE360_LARGE(DistConstants.MediaFormat.IMAGE360_LARGE, "360_large", "_3l"), //
    IMAGE360_LARGE_WEBP(DistConstants.MediaFormat.IMAGE360_LARGE_WEBP, "360_large_webp", "_3lw"), //
    FAMILY_DATASHEET(DistConstants.MediaFormat.PDF, "downloads", "_ds"), //
    FAMILY_VIDEO(DistConstants.MediaFormat.VIDEO, "video", "_v");

    private final String mediaFormat;
    private final String pushLocationConfigurationId;
    private final String suffix;

    PimAssetImageType(final String mediaFormat, final String pushLocationConfigurationId, final String suffix) {
        this.mediaFormat = mediaFormat;
        this.pushLocationConfigurationId = pushLocationConfigurationId;
        this.suffix = suffix;
    }

    public static PimAssetImageType getByPushLocationConfigurationId(final String id) {
        for (final PimAssetImageType value : values()) {
            if (value.getPushLocationConfigurationId().equals(id)) {
                return value;
            }
        }
        return null;
    }

    // BEGIN GENERATED CODE

    public String getMediaFormat() {
        return mediaFormat;
    }

    public String getPushLocationConfigurationId() {
        return pushLocationConfigurationId;
    }

    public String getSuffix() {
        return suffix;
    }

    // END GENERATED CODE
}
